package de.schlaich.gunnar.aiTools.mcp;

import java.util.Map;

public final class McpProtocol
{
	public static final class ToolDesc
	{
		private final String name;
		private final String description;
		private final Map<String, Object> schema;

		public ToolDesc(String name, String description, Map<String, Object> schema)
		{
			this.name = name;
			this.description = description;
			this.schema = schema;
		}

		public String getName()
		{
			return name;
		}

		public String getDescription()
		{
			return description;
		}

		public Map<String, Object> getSchema()
		{
			return schema;
		}
	}

	public interface McpTool
	{
		String name();

		String description();

		Map<String, Object> inputSchema();

		Object call(Map<String, Object> args) throws Exception;
	}
}
