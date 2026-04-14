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

public class FetchTool extends Tool
{
	private final RhapsodyClient rh;
	
	

	public FetchTool(RhapsodyClient rh, Consumer<String> aTraceAction)
	{
		super("rhapsody-fetch", "Fetch element metadata for given IDs (GUIDs).", new LinkedHashMap<String, Object>()
		{
			{
				put("type", "object");
				put("properties", new LinkedHashMap<String, Object>()
				{
					{
						put("ids", new LinkedHashMap<String, Object>()
						{
							{
								put("type", "array");
								put("items", new LinkedHashMap<String, Object>()
								{
									{
										put("type", "string");
									}
								});
							}
						});
					}
				});
				put("required", Arrays.asList("ids"));
			}
		}, aTraceAction);
		this.rh = rh;
	
	}

	@Override
	public Object call(Map<String, Object> args)
	{
		trace("fetch called with args: " + args.toString());
		List<?> ids = (List<?>) args.get("ids");
		List<Map<String, Object>> out = new ArrayList<Map<String, Object>>();
		for (Object idObj : ids)
		{
			trace("Fetching element for ID: " + idObj);
			String id = String.valueOf(idObj);
			java.util.Optional<IRPModelElement> opt = rh.byGUID(id);
			if (opt.isPresent())
			{
				//trace("  Found: " + opt.get().getFullPathName() + " (" + opt.get().getMetaClass() + ")");
				//trace("  JSON: " + serializeToJsonObject(opt.get()));
				out.add(rh.serializeToJsonObject(opt.get(), false));
			}
			else
			{
				trace("  Not found!");
			}
		}
		
		//trace("returned:" + Collections.singletonMap("content", out).toString());
		return Collections.singletonMap("content", out);
	}


}