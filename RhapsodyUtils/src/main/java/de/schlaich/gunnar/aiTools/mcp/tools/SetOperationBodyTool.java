package de.schlaich.gunnar.aiTools.mcp.tools;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPOperation;

import de.schlaich.gunnar.aiTools.mcp.RhapsodyClient;

public class SetOperationBodyTool extends Tool
{
	private final RhapsodyClient rh;

	public SetOperationBodyTool(RhapsodyClient rh, Consumer<String> aTraceAction)
	{
		super("rhapsody-set-operation-body",
				"Sets the body (implementation) of an IRPOperation identified by the given GUID (UUID). "
						+ "Returns an error if the element is not an IRPOperation.",
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
										put("description", "The GUID (UUID) of the IRPOperation whose body should be set.");
									}
								});
								put("body", new LinkedHashMap<String, Object>()
								{
									{
										put("type", "string");
										put("description", "The body (implementation code) to set on the operation.");
									}
								});
							}
						});
						put("required", Arrays.asList("id", "body"));
					}
				}, aTraceAction);
		this.rh = rh;
	}

	@Override
	public Object call(Map<String, Object> args)
	{
		String id = String.valueOf(args.get("id"));
		String body = String.valueOf(args.get("body"));

		trace("set-operation-body called for ID: " + id);

		java.util.Optional<IRPModelElement> opt = rh.byGUID(id);
		if (!opt.isPresent())
		{
			trace("  Element not found: " + id);
			return Collections.singletonMap("error", "Element not found for GUID: " + id);
		}

		IRPModelElement element = opt.get();
		trace("  Found element: " + element.getFullPathName() + " (" + element.getMetaClass() + ")");

		if (!(element instanceof IRPOperation))
		{
			trace("  Element is not an IRPOperation: " + element.getMetaClass());
			return Collections.singletonMap("error",
					"Element is not an IRPOperation. Found type: " + element.getMetaClass()
							+ " for element: " + element.getFullPathName());
		}

		IRPOperation operation = (IRPOperation) element;
		operation.setBody(body);

		trace("  Operation body set successfully.");

		Map<String, Object> result = new LinkedHashMap<>();
		result.put("status", "success");
		result.put("element", operation.getFullPathName());
		result.put("body", body);
		return Collections.singletonMap("content", result);
	}
}
