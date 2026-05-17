package de.schlaich.gunnar.aiTools.mcp.tools;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.telelogic.rhapsody.core.IRPModelElement;

import de.schlaich.gunnar.aiTools.mcp.RhapsodyClient;

public class GetPropertyValueTool extends Tool
{
	private final RhapsodyClient rh;

	public GetPropertyValueTool(RhapsodyClient rh, Consumer<String> aTraceAction)
	{
		super("rhapsody-get-property-value",
				"Reads the value of a named property of an IRPModelElement identified by the given GUID (UUID). "
						+ "Returns the property value as a string.",
				new LinkedHashMap<String, Object>()
				{
					{
						put("type", "object");
						put("properties", new LinkedHashMap<String, Object>()
						{
							{
								put("id", new LinkedHashMap<String, Object>()
								{
									{
										put("type", "string");
										put("description", "The GUID (UUID) of the IRPModelElement.");
									}
								});
								put("property", new LinkedHashMap<String, Object>()
								{
									{
										put("type", "string");
										put("description", "The name of the property to read.");
									}
								});
							}
						});
						put("required", Arrays.asList("id", "property"));
					}
				}, aTraceAction);
		this.rh = rh;
	}

	@Override
	public Object call(Map<String, Object> args)
	{
		String id = String.valueOf(args.get("id"));
		String propertyName = String.valueOf(args.get("property"));

		trace("get-property-value called for ID: " + id + ", property: " + propertyName);

		java.util.Optional<IRPModelElement> opt = rh.byGUID(id);
		if (!opt.isPresent())
		{
			trace("  Element not found: " + id);
			return Collections.singletonMap("error", "Element not found for GUID: " + id);
		}

		IRPModelElement element = opt.get();
		trace("  Found element: " + element.getFullPathName() + " (" + element.getMetaClass() + ")");

		String value = element.getPropertyValue(propertyName);
		trace("  Property '" + propertyName + "' = " + value);

		Map<String, Object> result = new LinkedHashMap<>();
		result.put("element", element.getFullPathName());
		result.put("property", propertyName);
		result.put("value", value);
		return Collections.singletonMap("content", result);
	}
}
