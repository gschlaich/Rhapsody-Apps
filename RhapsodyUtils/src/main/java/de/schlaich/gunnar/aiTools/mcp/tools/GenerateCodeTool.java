package de.schlaich.gunnar.aiTools.mcp.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPCollection;
import com.telelogic.rhapsody.core.IRPModelElement;

import de.schlaich.gunnar.aiTools.mcp.RhapsodyClient;

public class GenerateCodeTool extends Tool
{
	private final RhapsodyClient rh;

	public GenerateCodeTool(RhapsodyClient rh, Consumer<String> aTraceAction)
	{
		super("rhapsody-generate-code",
				"Generate code for one or more model elements identified by their GUIDs (UUIDs).",
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
										put("description", "One or more GUIDs (UUIDs) of the IRPModelElements to generate code for.");
										put("minItems", 1);
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
		Object rawIds = args.get("ids");
		List<String> ids = new ArrayList<>();
		if (rawIds instanceof List)
		{
			for (Object o : (List<?>) rawIds)
				ids.add(String.valueOf(o));
		}
		else
		{
			ids.add(String.valueOf(rawIds));
		}

		trace("generate-code called for " + ids.size() + " element(s)");

		try
		{
			IRPApplication app = rh.getApp();
			IRPCollection col = app.createNewCollection();
			List<String> resolved = new ArrayList<>();
			List<String> notFound = new ArrayList<>();

			for (String id : ids)
			{
				java.util.Optional<IRPModelElement> opt = rh.byGUID(id);
				if (!opt.isPresent())
				{
					trace("  Element not found: " + id);
					notFound.add(id);
				}
				else
				{
					IRPModelElement element = opt.get();
					trace("  Adding element: " + element.getFullPathName() + " (" + element.getMetaClass() + ")");
					col.addItem(element);
					resolved.add(element.getFullPathName());
				}
			}

			if (resolved.isEmpty())
			{
				return Collections.singletonMap("error", "None of the provided GUIDs could be resolved: " + notFound);
			}

			app.generateElements(col);
			trace("  Code generation completed.");

			Map<String, Object> result = new LinkedHashMap<>();
			result.put("status", "success");
			result.put("elements", resolved);
			if (!notFound.isEmpty())
				result.put("notFound", notFound);
			return Collections.singletonMap("content", result);
		}
		catch (Exception e)
		{
			trace("  Error during code generation: " + e.getMessage());
			return Collections.singletonMap("error", "Code generation failed: " + e.getMessage());
		}
	}
}