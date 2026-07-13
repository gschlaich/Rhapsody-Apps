package de.schlaich.gunnar.aiTools.mcp.tools;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.telelogic.rhapsody.core.IRPAttribute;
import com.telelogic.rhapsody.core.IRPModelElement;

import de.schlaich.gunnar.aiTools.mcp.RhapsodyClient;

/**
 * MCP Tool to set the initial value (default value) of an IRPAttribute
 * identified by its GUID.
 */
public class SetAttributeInitialValueTool extends Tool
{
	private final RhapsodyClient rh;

	public SetAttributeInitialValueTool(RhapsodyClient rh, Consumer<String> aTraceAction)
	{
		super("rhapsody-set-attribute-initial-value",
				"Sets the initial value (default value) of an IRPAttribute identified by the given GUID (UUID). "
						+ "Returns whether the value was set successfully or an error message if it failed.",
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
										put("description", "The GUID (UUID) of the IRPAttribute.");
									}
								});
								put("value", new LinkedHashMap<String, Object>()
								{
									{
										put("type", "string");
										put("description", "The new initial value to set for the attribute.");
									}
								});
							}
						});
						put("required", Arrays.asList("id", "value"));
					}
				}, aTraceAction);
		this.rh = rh;
	}

	@Override
	public Object call(Map<String, Object> args)
	{
		String id = String.valueOf(args.get("id"));
		String newValue = String.valueOf(args.get("value"));

		trace("set-attribute-initial-value called for ID: " + id + ", value: " + newValue);

		java.util.Optional<IRPModelElement> opt = rh.byGUID(id);
		if (!opt.isPresent())
		{
			trace("  Element not found: " + id);
			return Collections.singletonMap("error", "Element not found for GUID: " + id);
		}

		IRPModelElement element = opt.get();
		trace("  Found element: " + element.getFullPathName() + " (" + element.getMetaClass() + ")");

		// Check if the element is an IRPAttribute
		if (!(element instanceof IRPAttribute))
		{
			String errorMsg = "Element is not an attribute. Found: " + element.getMetaClass();
			trace("  " + errorMsg);
			return Collections.singletonMap("error", errorMsg);
		}

		IRPAttribute attribute = (IRPAttribute) element;
		
		try
		{
			// Set the initial value (default value) of the attribute
			attribute.setDefaultValue(newValue);

			// Verify the value was set correctly
			String actualValue = attribute.getDefaultValue();
			boolean success = newValue.equals(actualValue);

			trace("  Initial value set to '" + newValue + "', verified: " + success);

			Map<String, Object> result = new LinkedHashMap<>();
			result.put("status", success ? "success" : "failed");
			result.put("element", element.getFullPathName());
			result.put("attributeName", attribute.getName());
			result.put("requestedValue", newValue);
			result.put("actualValue", actualValue);
			return Collections.singletonMap("content", result);
		}
		catch (Exception e)
		{
			String errorMsg = "Failed to set initial value: " + e.getMessage();
			trace("  " + errorMsg);
			return Collections.singletonMap("error", errorMsg);
		}
	}
}
