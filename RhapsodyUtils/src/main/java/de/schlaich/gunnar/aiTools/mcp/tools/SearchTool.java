package de.schlaich.gunnar.aiTools.mcp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SearchTool extends Tool
{
	private final ModelIndexer index;

	public SearchTool(ModelIndexer index)
	{
		super("rhapsody.search", "Search model elements by name, qualified name, kind or stereotype.",
				new LinkedHashMap<String, Object>()
				{
					{
						put("type", "object");
						put("properties", new LinkedHashMap<String, Object>()
						{
							{
								put("query", new LinkedHashMap<String, Object>()
								{
									{
										put("type", "string");
									}
								});
								put("top_k", new LinkedHashMap<String, Object>()
								{
									{
										put("type", "integer");
										put("default", 20);
									}
								});
							}
						});
						put("required", Arrays.asList("query"));
					}
				});
		this.index = index;
	}

	@Override
	public Object call(Map<String, Object> args)
	{
		String q = (String) args.get("query");
		Number k = (Number) (args.containsKey("top_k") ? args.get("top_k") : 20);
		
		List<ModelIndexer.Entry> hits = index.search(q, k.intValue());
		List<Map<String, Object>> out = new ArrayList<Map<String, Object>>();
		for (ModelIndexer.Entry e : hits)
		{
			Map<String, Object> row = new LinkedHashMap<String, Object>();
			row.put("id", e.getId());
			row.put("kind", e.getKind());
			row.put("name", e.getName());
			row.put("qualifiedName", e.getQname());
			row.put("stereotype", e.getStereotype());
			out.add(row);
		}
		return Collections.singletonMap("items", out);
	}
}