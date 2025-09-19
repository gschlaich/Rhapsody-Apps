package de.schlaich.gunnar.aiTools.mcp;



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;



/**
 * Minimaler JSON-RPC 2.0 Server über STDIN/STDOUT. Unterstützte Methoden: -
 * "ping" - "tools/list" - "tools/call" { name, arguments }
 */
public class JsonRpcServer
{
	private final ToolRegistry registry;
	private final Gson gson = Json.gson();

	public JsonRpcServer(ToolRegistry registry)
	{
		this.registry = registry;
	}

	public void serve() throws IOException
	{
		
		
		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(System.out, StandardCharsets.UTF_8));

		String line;
		while ((line = in.readLine()) != null)
		{
			if (line.trim().isEmpty()) continue;

			JsonObject req = JsonParser.parseString(line).getAsJsonObject();
			JsonElement id = req.get("id");
			String method = req.get("method").getAsString();

			try
			{
				Object result;
				switch (method)
				{
				case "ping":
					result = Collections.singletonMap("ok", Boolean.TRUE);
					break;
				case "tools/list":
					result = Collections.singletonMap("tools", registry.list());
					break;
				case "tools/call":
					JsonObject p = req.getAsJsonObject("params");
					String name = p.get("name").getAsString();
					@SuppressWarnings("unchecked")
					Map<String, Object> args = gson.fromJson(p.get("arguments"), Map.class);
					result = registry.call(name, args);
					break;
				default:
					throw new IllegalArgumentException("Unknown method: " + method);
				}

				JsonObject resp = new JsonObject();
				resp.addProperty("jsonrpc", "2.0");
				resp.add("id", id);
				resp.add("result", gson.toJsonTree(result));

				out.write(gson.toJson(resp));
				out.write("\n");
				out.flush();

			}
			catch (Exception ex)
			{
				JsonObject err = new JsonObject();
				err.addProperty("code", -32000);
				err.addProperty("message", ex.getMessage());

				JsonObject resp = new JsonObject();
				resp.addProperty("jsonrpc", "2.0");
				resp.add("id", id);
				resp.add("error", err);

				out.write(gson.toJson(resp));
				out.write("\n");
				out.flush();
			}
		}
	}
}