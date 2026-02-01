package de.schlaich.gunnar.aiTools.mcp.tools;

import java.util.Map;
import java.util.function.Consumer;

import de.schlaich.gunnar.aiTools.mcp.McpProtocol;
import de.schlaich.gunnar.aiTools.mcp.McpProtocol.McpTool;

public abstract class Tool implements McpProtocol.McpTool
{
	private final String name, description;
	private final Map<String, Object> schema;
	private Consumer<String> myTraceAction = null;

	protected Tool(String name, String description, Map<String, Object> schema, Consumer<String> aTraceAction)
	{
		this.name = name;
		this.description = description;
		this.schema = schema;
		myTraceAction = aTraceAction;
	}
	
	public String name()
	{
		return name;
	}

	public String description()
	{
		return description;
	}

	public Map<String, Object> inputSchema()
	{
		return schema;
	}
	
	protected String getDerivedClassName()
	{
		return this.getClass().getSimpleName(); // Returns just the class name
	}

	protected String getFullDerivedClassName()
	{
		return this.getClass().getName(); // Returns fully qualified class name with package
	}
	
	protected void trace(String aMessage)
	{
		if (myTraceAction == null)
		{
			// no traceaction set...
			return;
		}

		aMessage = getDerivedClassName() + ": " + aMessage;

		myTraceAction.accept(aMessage);
	}
	
}