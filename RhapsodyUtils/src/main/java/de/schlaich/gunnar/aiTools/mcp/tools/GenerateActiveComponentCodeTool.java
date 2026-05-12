package de.schlaich.gunnar.aiTools.mcp.tools;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPComponent;
import com.telelogic.rhapsody.core.IRPProject;

import de.schlaich.gunnar.aiTools.mcp.RhapsodyClient;

public class GenerateActiveComponentCodeTool extends Tool
{
	private static final String ACTION_GENERATE          = "generate";
	private static final String ACTION_BUILD             = "build";
	private static final String ACTION_GENERATE_AND_BUILD = "generate and build";

	private final RhapsodyClient rh;

	public GenerateActiveComponentCodeTool(RhapsodyClient rh, Consumer<String> aTraceAction)
	{
		super("rhapsody-generate-active-component-code",
				"Generates code and/or builds the currently active component of the Rhapsody project.",
				new LinkedHashMap<String, Object>()
				{
					{
						put("type", "object");
						put("properties", new LinkedHashMap<String, Object>()
						{
							{
								put("action", new LinkedHashMap<String, Object>()
								{
									{
										put("type", "string");
										put("enum", Arrays.asList(
												ACTION_GENERATE,
												ACTION_BUILD,
												ACTION_GENERATE_AND_BUILD));
										put("description",
												"Action to perform on the active component: "
												+ "'generate' to generate code, "
												+ "'build' to build, "
												+ "'generate and build' to do both.");
										put("default", ACTION_GENERATE);
									}
								});
							}
						});
					}
				}, aTraceAction);
		this.rh = rh;
	}

	@Override
	public Object call(Map<String, Object> args)
	{
		String action = args.containsKey("action") ? String.valueOf(args.get("action")) : ACTION_GENERATE;
		trace("generate-active-component-code called with action: " + action);

		IRPApplication app = rh.getApp();
		IRPProject project = app.activeProject();
		IRPComponent activeComponent = project.getActiveComponent();

		if (activeComponent == null)
		{
			trace("  No active component found.");
			return Collections.singletonMap("error", "No active component set in the current project.");
		}

		trace("  Active component: " + activeComponent.getFullPathName());

		try
		{
			if (ACTION_GENERATE.equals(action) || ACTION_GENERATE_AND_BUILD.equals(action))
			{
				trace("  Generating code...");
				app.generate();
				trace("  Code generation completed.");
			}

			if (ACTION_BUILD.equals(action) || ACTION_GENERATE_AND_BUILD.equals(action))
			{
				trace("  Building...");
				app.build();
				trace("  Build completed.");
			}

			Map<String, Object> result = new LinkedHashMap<>();
			result.put("status", "success");
			result.put("action", action);
			result.put("component", activeComponent.getFullPathName());
			return Collections.singletonMap("content", result);
		}
		catch (Exception e)
		{
			trace("  Error during '" + action + "': " + e.getMessage());
			return Collections.singletonMap("error", "Action '" + action + "' failed: " + e.getMessage());
		}
	}
}