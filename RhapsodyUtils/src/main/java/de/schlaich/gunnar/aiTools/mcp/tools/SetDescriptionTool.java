package de.schlaich.gunnar.aiTools.mcp.tools;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.telelogic.rhapsody.core.IRPModelElement;

import de.schlaich.gunnar.aiTools.mcp.RhapsodyClient;

public class SetDescriptionTool extends Tool
{
	private final RhapsodyClient rh;

	public SetDescriptionTool(RhapsodyClient rh, Consumer<String> aTraceAction)
	{
		super("rhapsody-set-description",
				"Sets the description of the IRPModelElement identified by the given GUID (UUID).",
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
										put("description", "The GUID (UUID) of the IRPModelElement whose description should be set.");
									}
								});
								put("description", new LinkedHashMap<String, Object>()
								{
									{
										put("type", "string");
										put("description", "The description text to set on the model element.");
									}
								});
							}
						});
						put("required", Arrays.asList("id", "description"));
					}
				}, aTraceAction);
		this.rh = rh;
	}

	@Override
	public Object call(Map<String, Object> args)
	{
		String id = String.valueOf(args.get("id"));
		String description = String.valueOf(args.get("description"));

		trace("set-description called for ID: " + id);

		java.util.Optional<IRPModelElement> opt = rh.byGUID(id);
		if (!opt.isPresent())
		{
			trace("  Element not found: " + id);
			return Collections.singletonMap("error", "Element not found for GUID: " + id);
		}

		IRPModelElement element = opt.get();
		trace("  Setting description for: " + element.getFullPathName() + " (" + element.getMetaClass() + ")");

		element.setDescription(description);

		trace("  Description set successfully.");

		Map<String, Object> result = new LinkedHashMap<>();
		result.put("status", "success");
		result.put("element", element.getFullPathName());
		result.put("description", description);
		return Collections.singletonMap("content", result);
	}
}
