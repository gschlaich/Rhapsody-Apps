package de.schlaich.gunnar.aiTools.mcp.tools;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProfile;
import com.telelogic.rhapsody.core.IRPProject;

import de.schlaich.gunnar.aiTools.mcp.RhapsodyClient;
import de.schlaich.gunnar.rhapsody.ghs.MultiPlugin;
import de.schlaich.gunnar.rhapsody.vs.VSPlugin;

public class CompileElementTool extends Tool
{
	private final RhapsodyClient rh;

	public CompileElementTool(RhapsodyClient rh, Consumer<String> aTraceAction)
	{
		super("rhapsody-compile-element",
				"Compiles the model element identified by the given GUID (UUID). "
						+ "Detects whether the VSProfile or GHSMultiProfile is active and uses the corresponding plugin.",
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
										put("description", "The GUID (UUID) of the IRPModelElement to compile.");
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
		trace("compile-element called for ID: " + id);

		java.util.Optional<IRPModelElement> opt = rh.byGUID(id);
		if (!opt.isPresent())
		{
			trace("  Element not found: " + id);
			return Collections.singletonMap("error", "Element not found for GUID: " + id);
		}

		IRPModelElement element = opt.get();
		trace("  Compiling element: " + element.getFullPathName() + " (" + element.getMetaClass() + ")");

		IRPApplication app = rh.getApp();
		IRPProject project = app.activeProject();

		List<IRPProfile> profiles = project.getProfiles().toList();

		for (IRPProfile profile : profiles)
		{
			if (profile.getName().equals(VSPlugin.PROFILE_NAME))
			{
				trace("  VSProfile detected – using VSPlugin.compile()");
				VSPlugin vsPlugin = new VSPlugin();
				vsPlugin.RhpPluginInit(app);
				vsPlugin.compile(element);

				Map<String, Object> result = new LinkedHashMap<>();
				result.put("status", "success");
				result.put("ide", "VisualStudio");
				result.put("element", element.getFullPathName());
				return Collections.singletonMap("content", result);
			}

			if (profile.getName().equals(MultiPlugin.PROFILE_NAME))
			{
				trace("  GHSMultiProfile detected – using MultiPlugin.compile()");
				MultiPlugin multiPlugin = new MultiPlugin();
				multiPlugin.RhpPluginInit(app);
				multiPlugin.compile(element);

				Map<String, Object> result = new LinkedHashMap<>();
				result.put("status", "success");
				result.put("ide", "GHSMulti");
				result.put("element", element.getFullPathName());
				return Collections.singletonMap("content", result);
			}
		}

		trace("  No supported IDE profile found (VSProfile / GHSMultiProfile).");
		return Collections.singletonMap("error",
				"No supported IDE profile found. Neither '" + VSPlugin.PROFILE_NAME
						+ "' nor '" + MultiPlugin.PROFILE_NAME + "' is loaded in the project.");
	}
}
