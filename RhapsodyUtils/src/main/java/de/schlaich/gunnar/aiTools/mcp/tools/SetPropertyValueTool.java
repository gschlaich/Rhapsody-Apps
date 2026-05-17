package de.schlaich.gunnar.aiTools.mcp.tools;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.telelogic.rhapsody.core.IRPModelElement;

import de.schlaich.gunnar.aiTools.mcp.RhapsodyClient;

public class SetPropertyValueTool extends Tool
{
	private final RhapsodyClient rh;

	public SetPropertyValueTool(RhapsodyClient rh, Consumer<String> aTraceAction)
	{
		super("rhapsody-set-property-value",
				"Sets the value of a named property of an IRPModelElement identified by the given GUID (UUID). "
						+ "Returns whether the property was set successfully.",
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
										put("description", "The name of the property to set.");
									}
								});
								put("value", new LinkedHashMap<String, Object>()
								{
									{
										put("type", "string");
										put("description", "The value to set for the property.");
									}
								});
							}
						});
						put("required", Arrays.asList("id", "property", "value"));
					}
				}, aTraceAction);
		this.rh = rh;
	}

	@Override
	public Object call(Map<String, Object> args)
	{
		String id = String.valueOf(args.get("id"));
		String propertyName = String.valueOf(args.get("property"));
		String propertyValue = String.valueOf(args.get("value"));

		trace("set-property-value called for ID: " + id + ", property: " + propertyName + ", value: " + propertyValue);

		java.util.Optional<IRPModelElement> opt = rh.byGUID(id);
		if (!opt.isPresent())
		{
			trace("  Element not found: " + id);
			return Collections.singletonMap("error", "Element not found for GUID: " + id);
		}

		IRPModelElement element = opt.get();
		trace("  Found element: " + element.getFullPathName() + " (" + element.getMetaClass() + ")");

		element.setPropertyValue(propertyName, propertyValue);

		String newValue = element.getPropertyValue(propertyName);
		boolean success = propertyValue.equals(newValue);

		trace("  Property '" + propertyName + "' set to '" + propertyValue + "', verified: " + success);

		Map<String, Object> result = new LinkedHashMap<>();
		result.put("status", success ? "success" : "failed");
		result.put("element", element.getFullPathName());
		result.put("property", propertyName);
		result.put("requestedValue", propertyValue);
		result.put("actualValue", newValue);
		return Collections.singletonMap("content", result);
	}
}
