package de.schlaich.gunnar.aiTools.mcp;

import java.util.Map;

public abstract class Tool implements McpProtocol.McpTool
{
	private final String name, description;
	private final Map<String, Object> schema;

	protected Tool(String name, String description, Map<String, Object> schema)
	{
		this.name = name;
		this.description = description;
		this.schema = schema;
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
}