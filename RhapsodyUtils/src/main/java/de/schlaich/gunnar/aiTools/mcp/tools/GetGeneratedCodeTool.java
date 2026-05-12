package de.schlaich.gunnar.aiTools.mcp.tools;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPModelElement;

import de.schlaich.gunnar.aiTools.mcp.RhapsodyClient;
import de.schlaich.gunnar.rhapsody.utilities.ASTHelper;

public class GetGeneratedCodeTool extends Tool
{
	private static final String[] EXTENSIONS = { ".cpp", ".h", ".c" };

	private final RhapsodyClient rh;

	public GetGeneratedCodeTool(RhapsodyClient rh, Consumer<String> aTraceAction)
	{
		super("rhapsody-get-generated-code",
				"Returns the generated source code of a model element identified by its GUID (UUID). "
						+ "Tries file extensions .cpp, .h and .c in that order.",
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
										put("description", "The GUID (UUID) of the IRPModelElement whose generated code should be returned.");
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
		trace("get-generated-code called for ID: " + id);

		java.util.Optional<IRPModelElement> opt = rh.byGUID(id);
		if (!opt.isPresent())
		{
			trace("  Element not found: " + id);
			return Collections.singletonMap("error", "Element not found for GUID: " + id);
		}

		IRPModelElement element = opt.get();
		trace("  Looking for generated code of: " + element.getFullPathName() + " (" + element.getMetaClass() + ")");

		IRPApplication app = rh.getApp();

		Map<String, Object> files = new LinkedHashMap<>();

		for (String ext : EXTENSIONS)
		{
			File file = ASTHelper.getSourcePath(element, app, ext);
			if (file != null && file.exists())
			{
				try
				{
					String content = new String(Files.readAllBytes(file.toPath()));
					files.put(file.getName(), content);
					trace("  Found file: " + file.getAbsolutePath());
				}
				catch (Exception e)
				{
					trace("  Error reading file " + file.getAbsolutePath() + ": " + e.getMessage());
					files.put(file.getName(), "Error reading file: " + e.getMessage());
				}
			}
		}

		if (files.isEmpty())
		{
			trace("  No generated files found for element: " + element.getFullPathName());
			return Collections.singletonMap("error",
					"No generated files found for element: " + element.getFullPathName()
							+ ". Make sure the active component is set correctly and code has been generated.");
		}

		Map<String, Object> result = new LinkedHashMap<>();
		result.put("element", element.getFullPathName());
		result.put("metaClass", element.getMetaClass());
		result.put("files", files);
		return Collections.singletonMap("content", result);
	}
}
