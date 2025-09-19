package de.schlaich.gunnar.aiTools.mcp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPStereotype;

public class FetchTool extends Tool
{
	private final RhapsodyClient rh;

	public FetchTool(RhapsodyClient rh)
	{
		super("rhapsody.fetch", "Fetch element metadata for given IDs (GUIDs).", 
				new LinkedHashMap<String, Object>()
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
		});
		this.rh = rh;
	}

	@Override
	public Object call(Map<String, Object> args)
	{
		List<?> ids = (List<?>) args.get("ids");
		List<Map<String, Object>> out = new ArrayList<Map<String, Object>>();
		for (Object idObj : ids)
		{
			String id = String.valueOf(idObj);
			java.util.Optional<IRPModelElement> opt = rh.byGUID(id);
			if (opt.isPresent()) out.add(serialize(opt.get()));
		}
		return Collections.singletonMap("items", out);
	}

	private static Map<String, Object> serialize(IRPModelElement el)
	{
		Map<String, Object> m = new LinkedHashMap<String, Object>();
		m.put("id", el.getGUID());
		m.put("kind", el.getMetaClass());
		m.put("name", el.getName());
		m.put("qualifiedName", el.getFullPathName());
		
		String stereo = (el.getStereotypes()) != null
				? String.join(",", ((IRPStereotype) el).getStereotypes().toList())
				: "";
		m.put("stereotype", stereo);
		
		m.put("ownerPath", el.getOwner() != null ? el.getOwner().getFullPathName() : null);
		return m;
	}
}