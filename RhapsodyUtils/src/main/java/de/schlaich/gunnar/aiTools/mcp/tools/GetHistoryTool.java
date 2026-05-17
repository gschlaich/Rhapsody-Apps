package de.schlaich.gunnar.aiTools.mcp.tools;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.telelogic.rhapsody.core.IRPModelElement;

import de.schlaich.gunnar.aiTools.mcp.RhapsodyClient;
import de.schlaich.gunnar.rhapsody.utilities.SVNTools;

public class GetHistoryTool extends Tool
{
	private final RhapsodyClient rh;

	public GetHistoryTool(RhapsodyClient rh, Consumer<String> aTraceAction)
	{
		super("rhapsody-get-history",
				"Reads the SVN history of an IRPModelElement identified by the given GUID (UUID). "
						+ "Returns a JSON list of log entries with revision, author, date and message.",
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
										put("description", "The GUID (UUID) of the IRPModelElement whose history should be read.");
									}
								});
								put("limit", new LinkedHashMap<String, Object>()
								{
									{
										put("type", "integer");
										put("description", "Maximum number of log entries to return. Use 0 for no limit.");
									}
								});
							}
						});
						put("required", Arrays.asList("id", "limit"));
					}
				}, aTraceAction);
		this.rh = rh;
	}

	@Override
	public Object call(Map<String, Object> args)
	{
		String id = String.valueOf(args.get("id"));
		int limit = Integer.parseInt(String.valueOf(args.get("limit")));

		trace("get-history called for ID: " + id + ", limit: " + limit);

		java.util.Optional<IRPModelElement> opt = rh.byGUID(id);
		if (!opt.isPresent())
		{
			trace("  Element not found: " + id);
			return Collections.singletonMap("error", "Element not found for GUID: " + id);
		}

		IRPModelElement element = opt.get();
		trace("  Found element: " + element.getFullPathName() + " (" + element.getMetaClass() + ")");

		SVNTools svnTools = new SVNTools(rh.getApp(), null);
		List<Map<String, Object>> rows = svnTools.readHistoryAsMaps(element, limit);

		if (rows == null)
		{
			trace("  No history available (element may not be in a save unit).");
			return Collections.singletonMap("error", "No history available for element: " + element.getFullPathName());
		}

		trace("  Returning " + rows.size() + " history entries.");

		Map<String, Object> result = new LinkedHashMap<>();
		result.put("element", element.getFullPathName());
		result.put("history", rows);
		return Collections.singletonMap("content", result);
	}
}