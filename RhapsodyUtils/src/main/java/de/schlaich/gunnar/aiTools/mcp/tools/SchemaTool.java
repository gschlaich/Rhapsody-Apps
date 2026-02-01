package de.schlaich.gunnar.aiTools.mcp.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.telelogic.rhapsody.core.IRPModelElement;

import de.schlaich.gunnar.aiTools.mcp.RhapsodyClient;
import de.schlaich.gunnar.aiTools.mcp.jsonModels.JsonAvailableMetaClass;

public class SchemaTool extends Tool
{

	private final RhapsodyClient rh;
	
	public SchemaTool(RhapsodyClient rh, Consumer<String> aTraceAction)
	{
		super("rhapsody-Schema", "Provide schemas of the Json model data", new LinkedHashMap<String, Object>()
		{
			{
				put("$schema", "http://json-schema.org/draft-07/schema#");
				put("type", "object");
				put("additionalProperties", Boolean.FALSE);
				put("properties", new LinkedHashMap<String, Object>()
				{
					{					
						put("metaclass", new LinkedHashMap<String, Object>()
						{
							{
								put("type", "string");
								put("description", "MetaClass of the schema (e.g. Class, Operation, Package)");
							}
						});
					}
				});
				put("required", Arrays.asList("type"));
			}
		}, aTraceAction);
		this.rh = rh;
	}

	@Override
	public Object call(Map<String, Object> args)
	{
		String type = (String) args.get("metaclass");
		
		String result = "";
		JsonAvailableMetaClass availableMetaClasses = new JsonAvailableMetaClass();
		
		try
		{
			result = availableMetaClasses.toJsonString();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		trace(result);
		return result;
		
//		
//
//		List<Map<String,Object>> items = new ArrayList<Map<String,Object>>();
//	   
//	    Map<String,Object> m = rh.serializeToJsonSchemaObject(type);
//	    items.add(m);
//
//	    Map<String,Object> out = new LinkedHashMap<String,Object>();
//	    out.put("content", items);
//	    out.put("count", items.size());
//	    return out;

	}

}
