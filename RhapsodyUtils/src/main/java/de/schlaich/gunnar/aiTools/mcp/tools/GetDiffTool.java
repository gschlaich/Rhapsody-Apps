package de.schlaich.gunnar.aiTools.mcp.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.telelogic.rhapsody.core.IRPModelElement;

import de.schlaich.gunnar.aiTools.mcp.RhapsodyClient;
import de.schlaich.gunnar.rhapsody.utilities.SVNTools;

public class GetDiffTool extends Tool
{
	private final RhapsodyClient rh;

	public GetDiffTool(RhapsodyClient rh, Consumer<String> aTraceAction)
	{
		super("rhapsody-get-diff",
				"Returns the list of changed IRPModelElements between two SVN revisions for the given model element (GUID). "
						+ "Uses diffmerge() internally. Returns name, metaclass and UUID for each changed element.",
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
										put("description", "The GUID (UUID) of the IRPModelElement to compare.");
									}
								});
								put("revisionA", new LinkedHashMap<String, Object>()
								{
									{
										put("type", "integer");
										put("description", "The base revision number (older revision).");
									}
								});
								put("revisionB", new LinkedHashMap<String, Object>()
								{
									{
										put("type", "integer");
										put("description", "The source revision number (newer revision).");
									}
								});
							}
						});
						put("required", Arrays.asList("id", "revisionA", "revisionB"));
					}
				}, aTraceAction);
		this.rh = rh;
	}

	@Override
	public Object call(Map<String, Object> args)
	{
		String id = String.valueOf(args.get("id"));
		int revisionA = Integer.parseInt(String.valueOf(args.get("revisionA")));
		int revisionB = Integer.parseInt(String.valueOf(args.get("revisionB")));

		trace("get-diff called for ID: " + id + ", revisionA: " + revisionA + ", revisionB: " + revisionB);

		java.util.Optional<IRPModelElement> opt = rh.byGUID(id);
		if (!opt.isPresent())
		{
			trace("  Element not found: " + id);
			return Collections.singletonMap("error", "Element not found for GUID: " + id);
		}

		IRPModelElement element = opt.get();
		trace("  Found element: " + element.getFullPathName() + " (" + element.getMetaClass() + ")");

		SVNTools svnTools = new SVNTools(rh.getApp(), null);
		List<IRPModelElement> changedElements = svnTools.diffmerge(element, revisionA, revisionB, true, false);

		if (changedElements == null)
		{
			trace("  diffmerge returned null.");
			return Collections.singletonMap("error", "Could not compute diff for element: " + element.getFullPathName());
		}

		trace("  Found " + changedElements.size() + " changed elements.");

		List<Map<String, Object>> rows = new ArrayList<>();
		for (IRPModelElement changed : changedElements)
		{
			Map<String, Object> entry = new LinkedHashMap<>();
			entry.put("name", changed.getName());
			entry.put("metaclass", changed.getMetaClass());
			entry.put("uuid", changed.getGUID());
			rows.add(entry);
		}

		Map<String, Object> result = new LinkedHashMap<>();
		result.put("element", element.getFullPathName());
		result.put("revisionA", revisionA);
		result.put("revisionB", revisionB);
		result.put("changedElements", rows);
		return Collections.singletonMap("content", result);
	}
}
