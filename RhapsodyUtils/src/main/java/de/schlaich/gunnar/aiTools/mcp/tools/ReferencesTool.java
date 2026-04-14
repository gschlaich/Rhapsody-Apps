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

public class ReferencesTool extends Tool
{

	RhapsodyClient rh = null;
	
	public ReferencesTool(RhapsodyClient rh, Consumer<String> aTraceAction)
	{
		super("rhapsody-references", "Search related model elements by Id (GUID) (References: Association, Generalization, Dependency).",
				new LinkedHashMap<String, Object>()
				{
					{
						put("$schema", "http://json-schema.org/draft-07/schema#");
						put("type", "object");
						put("additionalProperties", Boolean.FALSE);
						put("properties", new LinkedHashMap<String, Object>()
						{
							{
								put("Id", new LinkedHashMap<String, Object>()
								{
									{
										put("type", "string");
										put("description","GUID of the element to search references for");
									}
								});
							}
						});
						put("required", Arrays.asList("Id"));
					}
				}, aTraceAction);
		this.rh = rh;
		
	}

	@Override
	public Object call(Map<String, Object> args) throws Exception
	{
		trace("references called with args: " + args.toString());
		String guid = (String) args.get("Id");
		List<IRPModelElement> relations = rh.findReferences(guid);
		List<Map<String, Object>> out = new ArrayList<Map<String, Object>>();
		for (IRPModelElement el : relations)
		{
			trace("Element name: "+el.getName() + ", metaclass: " + el.getMetaClass());
			out.add(rh.serializeToJsonObject(el, false));
		}
		//trace("returned:" + Collections.singletonMap("content", out).toString());
		return Collections.singletonMap("content", out);
	}

}
