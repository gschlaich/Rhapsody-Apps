package de.schlaich.gunnar.aiTools.mcp;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ToolRegistry
{
	private final Map<String, McpProtocol.McpTool> tools = new LinkedHashMap<>();

	public ToolRegistry register(McpProtocol.McpTool tool)
	{
		tools.put(tool.name(), tool);
		return this;
	}

	public List<McpProtocol.ToolDesc> list()
	{
		return tools.values().stream().map(new Function<McpProtocol.McpTool, McpProtocol.ToolDesc>()
		{
			@Override
			public McpProtocol.ToolDesc apply(McpProtocol.McpTool t)
			{
				return new McpProtocol.ToolDesc(t.name(), t.description(), t.inputSchema());
			}
		}).collect(Collectors.toList());
	}

	public Object call(String name, Map<String, Object> args) throws Exception
	{
		McpProtocol.McpTool t = tools.get(name);
		if (t == null) throw new IllegalArgumentException("Unknown tool: " + name);
		return t.call(args);
	}
}