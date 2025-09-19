package de.schlaich.gunnar.aiTools.mcp;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.google.gson.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;



@SuppressWarnings("restriction")
public final class HttpJsonRpcBridge
{
	private final ToolRegistry registry;
	private final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
	private HttpServer server;

	public HttpJsonRpcBridge(ToolRegistry registry)
	{
		this.registry = registry;
	}

	public void start(int port) throws IOException
	{
		server = HttpServer.create(new InetSocketAddress("127.0.0.1", port), 0);
		server.createContext("/rpc", this::handle);
		server.setExecutor(null);
		server.start();
	}

	public void stop()
	{
		if (server != null) server.stop(0);
	}

	private void handle(HttpExchange ex) throws IOException
	{
		if (!"POST".equalsIgnoreCase(ex.getRequestMethod()))
		{
			ex.sendResponseHeaders(405, -1);
			return;
		}

		String body;
		try (InputStream in = ex.getRequestBody())
		{
			
			String bb = "";
			
			while (in.available() > 0)
			{
				int b = in.read();
				if (b == -1) break;
				bb += (char) b;
			}
			
			body = new String(bb.getBytes(), StandardCharsets.UTF_8);
				
		}
		JsonObject req = JsonParser.parseString(body).getAsJsonObject();
		JsonElement id = req.get("id");
		String method = req.get("method").getAsString();

		JsonObject resp = new JsonObject();
		resp.addProperty("jsonrpc", "2.0");
		resp.add("id", id);
		try
		{
			Object result;
			if ("ping".equals(method))
			{
				result = java.util.Collections.singletonMap("ok", true);
			}
			else if ("tools/list".equals(method))
			{
				result = java.util.Collections.singletonMap("tools", registry.list());
			}
			else if ("tools/call".equals(method))
			{
				JsonObject p = req.getAsJsonObject("params");
				String name = p.get("name").getAsString();
				@SuppressWarnings("unchecked")
				Map<String, Object> args = gson.fromJson(p.get("arguments"), Map.class);
				result = registry.call(name, args);
			}
			else
			{
				throw new IllegalArgumentException("Unknown method: " + method);
			}
			resp.add("result", gson.toJsonTree(result));
			byte[] out = gson.toJson(resp).getBytes(StandardCharsets.UTF_8);
			ex.getResponseHeaders().add("Content-Type", "application/json");
			ex.sendResponseHeaders(200, out.length);
			try (OutputStream os = ex.getResponseBody())
			{
				os.write(out);
			}
		}
		catch (Exception e)
		{
			JsonObject err = new JsonObject();
			err.addProperty("code", -32000);
			err.addProperty("message", e.getMessage());
			resp.add("error", err);
			byte[] out = gson.toJson(resp).getBytes(StandardCharsets.UTF_8);
			ex.getResponseHeaders().add("Content-Type", "application/json");
			ex.sendResponseHeaders(200, out.length);
			try (OutputStream os = ex.getResponseBody())
			{
				os.write(out);
			}
		}
	}
}