package de.schlaich.gunnar.aiTools.mcp;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.google.gson.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;



@SuppressWarnings("restriction")
public final class HttpJsonRpcBridge
{
	private final ToolRegistry registry;
	private final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
	private HttpServer server;
	private Consumer<String> myTraceAction = null;

	public HttpJsonRpcBridge(ToolRegistry registry, Consumer<String> aTraceAction )
	{
		myTraceAction = aTraceAction;
		this.registry = registry;
	}
	
	
	private void trace(String aMessage)
	{
		if (myTraceAction == null)
		{
			// no traceaction set...
			return;
		}

		aMessage = "MCPServer: " + aMessage;

		myTraceAction.accept(aMessage);
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
		
		JsonObject resp = new JsonObject();
		
		try
		{
		
			JsonElement jo = JsonParser.parseString(body);
		
			JsonObject req = jo.getAsJsonObject();
			JsonElement id = req.get("id");
			String method = req.get("method").getAsString();

			
			resp.addProperty("jsonrpc", "2.0");
			resp.add("id", id);
		
			Object result;
			if ("ping".equals(method))
			{
				trace("Ping received");
				result = java.util.Collections.singletonMap("ok", true);
			}
			else if ("initialize".equals(method))
			{
				trace("Initialize received");
				
				// Request enthält u.a. protocolVersion, clientInfo, capabilities (des Clients)
			    // -> Wir antworten mit serverInfo und unseren capabilities
			    JsonObject respCaps = new JsonObject();

			    // Tools-Capabilities (empfohlen)
			    JsonObject toolsCaps = new JsonObject();
			    // Ob wir Tool-Liste-Änderungen pushen könnten – hier false:
			    JsonObject listChanged = new JsonObject();
			    listChanged.addProperty("enabled", false);
			    toolsCaps.add("listChanged", listChanged);
			    respCaps.add("tools", toolsCaps);

			    // OPTIONAL: Logging-Capability anbieten (dann muss logging/setLevel akzeptiert werden)
			    //JsonObject loggingCaps = new JsonObject();
			    // keine speziellen Felder nötig – das Vorhandensein signalisiert Support
			    //respCaps.add("logging", loggingCaps);

			    JsonObject serverInfo = new JsonObject();
			    serverInfo.addProperty("name", "rhapsody-mcp-java8");
			    serverInfo.addProperty("version", "0.1.0");

			    JsonObject initResult = new JsonObject();
			    initResult.addProperty("protocolVersion", "2024-11-05"); // MCP-Protokoll-ID (Beispiel)
			    initResult.add("capabilities", respCaps);
			    initResult.add("serverInfo", serverInfo);

			    result = initResult;

				
			}
			else if ("tools/list".equals(method))
			{
				trace("Tools list requested");
				result = java.util.Collections.singletonMap("tools", registry.list());
			}
			else if ("tools/call".equals(method))
			{
				trace("Tool call requested");
				JsonObject p = req.getAsJsonObject("params");
				String name = p.get("name").getAsString();
				@SuppressWarnings("unchecked")
				Map<String, Object> args = gson.fromJson(p.get("arguments"), Map.class);
				result = registry.call(name, args);
			}
			
			else if ("notifications/initialized".equals(method))
			{
			    // MCP-Clients schicken das direkt nach dem Start.
			    // Kein Ergebnis nötig; einfach acknowledge.
				trace("Client initialized notification received");
			    result = Collections.singletonMap("ack", true);
			}
			else
			{
				trace("Unknown method requested: " + method);
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
			trace("Error processing request: " + e.getMessage());
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