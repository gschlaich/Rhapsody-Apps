package de.schlaich.gunnar.aiTools.mcp.tools;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.telelogic.rhapsody.core.IRPModelElement;

import de.schlaich.gunnar.aiTools.mcp.RhapsodyClient;
import de.schlaich.gunnar.rhapsody.utilities.RhapsodyHelper;

public class SetActiveComponentTool extends Tool
{
	private final RhapsodyClient rh;

	public SetActiveComponentTool(RhapsodyClient rh, Consumer<String> aTraceAction)
	{
		super("rhapsody-set-active-component",
				"Sets the active component of the project to the component that owns the model element identified by the given GUID (UUID). "
						+ "Traverses the ownership chain upwards until an IRPComponent is found.",
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
										put("description",
												"The GUID (UUID) of the IRPModelElement whose owning component should be set as active.");
									}
								});
							}
						});
						put("required", Arrays.asList("id"));
					}
				}, aTraceAction);
		this.rh = rh;
	}

	@Override
	public Object call(Map<String, Object> args)
	{
		String id = String.valueOf(args.get("id"));
		trace("set-active-component called for ID: " + id);

		java.util.Optional<IRPModelElement> opt = rh.byGUID(id);
		if (!opt.isPresent())
		{
			trace("  Element not found: " + id);
			return Collections.singletonMap("error", "Element not found for GUID: " + id);
		}

		IRPModelElement element = opt.get();
		trace("  Setting active component for: " + element.getFullPathName() + " (" + element.getMetaClass() + ")");

		boolean success = RhapsodyHelper.setActive(element, rh.getApp());

		if (!success)
		{
			trace("  No owning component found for element: " + element.getFullPathName());
			return Collections.singletonMap("error",
					"No owning component found for element: " + element.getFullPathName());
		}

		trace("  Active component set successfully.");

		Map<String, Object> result = new LinkedHashMap<>();
		result.put("status", "success");
		result.put("element", element.getFullPathName());
		return Collections.singletonMap("content", result);
	}
}