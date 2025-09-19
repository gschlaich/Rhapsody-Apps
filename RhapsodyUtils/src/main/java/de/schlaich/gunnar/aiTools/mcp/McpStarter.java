package de.schlaich.gunnar.aiTools.mcp;

import com.telelogic.rhapsody.core.IRPApplication;

public class McpStarter
{

	static public McpStarter INSTANCE;
	
	private RhapsodyClient client;
	private ModelIndexer indexer;
	private ToolRegistry registry;
	private HttpJsonRpcBridge http;

	
	public static McpStarter getInstance()
	{
		if (INSTANCE == null) 
		{
			INSTANCE = new McpStarter();
		}
		return INSTANCE;
	}
	
	
	public void start(IRPApplication aApp) throws Exception
	{
		// Rhapsody anbinden (in-process): holt aktive Instanz & öffnet Projekt
		client = new RhapsodyClient(aApp);

		// Index bauen
		indexer = new ModelIndexer(client);
		indexer.build();

		// Tools registrieren
		registry = new ToolRegistry().register(new SearchTool(indexer)).register(new FetchTool(client));

		// HTTP-Bridge starten
		http = new HttpJsonRpcBridge(registry);
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
