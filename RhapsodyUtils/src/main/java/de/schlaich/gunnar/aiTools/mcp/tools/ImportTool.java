package de.schlaich.gunnar.aiTools.mcp.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.telelogic.rhapsody.core.IRPModelElement;

import de.schlaich.gunnar.aiTools.mcp.RhapsodyClient;
import de.schlaich.gunnar.aiTools.mcp.jsonModels.JsonModelElementBase;
import de.schlaich.gunnar.aiTools.mcp.jsonModels.JsonModelElementBase.ImportMode;

public class ImportTool extends Tool
{
	
	private static 
	
	RhapsodyClient rh = null;
	public ImportTool(RhapsodyClient rh, Consumer<String> aTraceAction)
	{
		super("rhapsody-import", "\"Import JSON model into Rhapsody model",
				new LinkedHashMap<String, Object>()
				{
					{
						put("$schema", "http://json-schema.org/draft-07/schema#");
						put("type", "object");
						put("additionalProperties", Boolean.FALSE);
						
						put("properties", new LinkedHashMap<String, Object>()
						{
							{
								put("modelJson", new LinkedHashMap<String, Object>()
								{
									{
										put("type", "string");
										put("description", "JSON representation of the model element");
									}
								});
								put("ParentGUID", new LinkedHashMap<String, Object>()
								{
									{
										put("type", "string");
										put("description", "GUID of the parent element to import into");
									}
								});
								
								
								put("ImportMode", new LinkedHashMap<String, Object>()
								{
									{
										/*
										
										create, update, reference, remove
										
										 */
										put("type", "string");
										put("enum", Arrays.asList("create","update", "remove"));
										put("description",
												"Import mode: create new element, update existing, or remove existing");
										put("default", "update");
									}
								});
								put("ValidateOnly", new LinkedHashMap<String, Object>()
                                {
                                    {
                                        put("type", "boolean");
                                        put("description", "If true, only validate without changes in the model");
                                        put("default", false);
                                    }
                                });
								
							}
						});
						put("required", Arrays.asList("modelJson", "TargetGUID", "ImportMode", "ValidateOnly") );
					}
				}, aTraceAction);
		this.rh = rh;
		
	}


	@Override
	public Object call(Map<String, Object> args) throws Exception
	{
		
		trace ("import called with args: " + args.toString());
		
		String modelJson = (String) args.get("modelJson");
		String targetGUID = (String) args.get("TargetGUID");
		
		if (targetGUID == null || targetGUID.isEmpty())
		{
			targetGUID = (String) args.get("rootGUID");
		}
		
		String importModeStr = (String) args.getOrDefault("ImportMode", "update");
		
		Boolean validateOnly = (Boolean) args.getOrDefault("ValidateOnly", Boolean.FALSE);
		
		if (modelJson == null || modelJson.isEmpty())
		{
			throw new IllegalArgumentException("modelJson is empty");
		}

		RhapsodyClient.ImportMode importMode = RhapsodyClient.ImportMode.valueOf(importModeStr);

		String ret = rh.importModelFromJson(modelJson, targetGUID, importMode, validateOnly != null && validateOnly);
		trace("Return from import: " + ret);
		
		List<Map<String,Object>> items = new ArrayList<Map<String,Object>>();
		
		Map<String,Object> m = new LinkedHashMap<String,Object>();
		m.put("type", "text");
		m.put("text",  "Import " + ret);
		items.add(m);
	    
	    Map<String,Object> out = new LinkedHashMap<String,Object>();
	    out.put("content", items);
		
		
		return out;
	}

}
