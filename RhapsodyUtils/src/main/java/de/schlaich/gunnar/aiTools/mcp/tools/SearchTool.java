package de.schlaich.gunnar.aiTools.mcp.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.telelogic.rhapsody.core.IRPModelElement;

import de.schlaich.gunnar.aiTools.mcp.RhapsodyClient;

public class SearchTool extends Tool
{
	
	private RhapsodyClient rh = null;

	public SearchTool(RhapsodyClient rh, Consumer<String> aTraceAction)
	{
		super("rhapsody-search", "Search model elements by name and type (MetaClass; for example Class, Block, Package).",
				new LinkedHashMap<String, Object>()
				{
					{
						put("$schema", "http://json-schema.org/draft-07/schema#");
						put("type", "object");
						put("additionalProperties", Boolean.FALSE);
						put("properties", new LinkedHashMap<String, Object>()
						{
							{
								put("query", new LinkedHashMap<String, Object>()
								{
									{
										put("type", "string");
										put("description","text to search for in name");
									}
								});
								put("type", new LinkedHashMap<String, Object>()
								{
									{
										put("type", "string");
										put("description", "MetaClass of the elements to search for (e.g. Class, Operation, Package)");
						
									}
								});
							}
						});
						put("required", Arrays.asList("query", "type"));
					}
				}, aTraceAction);
		this.rh = rh;
	}

	@Override
	public Object call(Map<String, Object> args)
	{
		String q = (String) args.get("query");
		String type = (String) args.get("type");
		
		List<IRPModelElement> elems = rh.findByName(q, type);
		
		
		List<Map<String,Object>> items = new ArrayList<Map<String,Object>>();
	    for (IRPModelElement e : elems) 
	    {
	      Map<String,Object> m = rh.serializeToJsonBaseObject(e);
	      items.add(m);
	    }

	    Map<String,Object> out = new LinkedHashMap<String,Object>();
	    out.put("content", items);
	    out.put("count", items.size());
	    trace(out.toString());
	    return out;

	}
}