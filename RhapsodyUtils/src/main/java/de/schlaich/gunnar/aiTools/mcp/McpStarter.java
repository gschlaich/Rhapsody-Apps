package de.schlaich.gunnar.aiTools.mcp;

import java.util.function.Consumer;

import com.telelogic.rhapsody.core.IRPApplication;

import de.schlaich.gunnar.aiTools.mcp.tools.AvailableMetaClassesTool;
import de.schlaich.gunnar.aiTools.mcp.tools.FetchTool;
import de.schlaich.gunnar.aiTools.mcp.tools.ImportTool;
import de.schlaich.gunnar.aiTools.mcp.tools.LazySearchTool;
import de.schlaich.gunnar.aiTools.mcp.tools.ReferencesTool;
import de.schlaich.gunnar.aiTools.mcp.tools.SchemaTool;
import de.schlaich.gunnar.aiTools.mcp.tools.SearchTool;

public class McpStarter
{

	static public McpStarter INSTANCE;

	private RhapsodyClient client;
	private ModelIndexer indexer;
	private ToolRegistry registry;
	private HttpJsonRpcBridge http;
	private Consumer<String> myTraceAction = null;

	public static McpStarter getInstance()
	{
		if (INSTANCE == null)
		{
			INSTANCE = new McpStarter();
		}
		return INSTANCE;
	}

	public void setTrace(Consumer<String> aTraceAction)
	{
		myTraceAction = aTraceAction;
	}

	public void start(IRPApplication aApp, Consumer<String> aTraceAction) throws Exception
	{

		myTraceAction = aTraceAction;
		// Rhapsody anbinden (in-process): holt aktive Instanz & öffnet Projekt
		client = new RhapsodyClient(aApp, aTraceAction);

		// Index bauen
		// indexer = new ModelIndexer(client);
		// indexer.build();

		// Tools registrieren
		registry = new ToolRegistry();
		registry.register(new FetchTool(client, aTraceAction));
		registry.register(new LazySearchTool(client, aTraceAction));
		registry.register(new SearchTool(client, aTraceAction));
		registry.register(new AvailableMetaClassesTool(client, aTraceAction));
		registry.register(new ReferencesTool(client, aTraceAction));
		registry.register(new SchemaTool(client, aTraceAction));
		registry.register(new ImportTool(client, aTraceAction));

		// HTTP-Bridge starten
		http = new HttpJsonRpcBridge(registry, myTraceAction);
		http.start(8765);
	}

	public void stop()
	{
		try
		{
			if (http != null) http.stop();
		}
		catch (Throwable ignored)
		{
		}

	}

}
