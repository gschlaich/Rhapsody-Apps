package de.schlaich.gunnar.aiTools.mcp.tools;

import com.telelogic.rhapsody.core.*;

import de.schlaich.gunnar.aiTools.mcp.RhapsodyClient;

import java.util.*;
import java.util.function.Consumer;

/**
 * Lazy/On-Demand Suche ohne Vollindex.
 *
 * Argumente (alle optional außer query): - query : String (erforderlich) -
 * top_k : int (Default 20) - timeout_ms : long (Default 250) - kinds :
 * Array<String> (z. B. ["Class","Block"]) – Filter auf MetaClass - stereotypes
 * : Array<String> – Filter, wenn Element Stereotypnamen enthält - under :
 * String – Qualified-Name-Präfix als Suchwurzel (Subbaum) - includeFeatures :
 * boolean – in Class-Features absteigen (Default false) - offset : int – Anzahl
 * bereits akzeptierter Treffer (für Pagination)
 *
 * Rückgabe: { items: [ {id,kind,name,qualifiedName,stereotype} ... ],
 * truncated: boolean, // true, wenn per timeout/Limit abgebrochen visited: int,
 * // besuchte Knoten next_offset: int // offset + items.length (für naive
 * Pagination) }
 */
public class LazySearchTool extends Tool
{
	private RhapsodyClient rh = null;

	public LazySearchTool(RhapsodyClient rh, Consumer<String> aTraceAction)
	{

		super("rhapsody-lazySearch", "On-demand search through the model with timeout and filters.",
				new LinkedHashMap<String, Object>()
				{
					{
						put("$schema", "http://json-schema.org/draft-07/schema#");
						put("type", "object");
						put("additionalProperties", Boolean.FALSE);
						put("properties", new LinkedHashMap<String, Object>()
						{
							{
								put("query", new LinkedHashMap<String, Object>()
								{
									{
										put("type", "string");
										put("description", "text to search for in name, qualified name or stereotype");
									}
								});
								put("top_k", new LinkedHashMap<String, Object>()
								{
									{
										put("type", "integer");
										put("default", 20);

									}
								});

								put("kinds", new LinkedHashMap<String, Object>()
								{
									{
										put("type", "array");
										put("items", new LinkedHashMap<String, Object>()
										{
											{
												put("type", "string");
											}
										});
									}
								});

								put("under", new LinkedHashMap<String, Object>()
								{
									{
										put("type", "string");

									}
								});

							}
						});
						put("required", Arrays.asList("query"));

					}
				}, aTraceAction);
		this.rh = rh;
	}

	/**
	 *
	 */
	@Override
	public Object call(Map<String, Object> args)
	{
		String query = (String) args.get("query");

		if (query == null || query.isEmpty())
		{
			return errorResult();
		}

		int topK = ((Number) args.getOrDefault("top_k", 20)).intValue();

		List<String> kinds = args.containsKey("kinds") ? (List<String>) args.get("kinds") : Collections.emptyList();

		String under = (String) args.get("under");

		trace("call query='" + query + "', topK=" + topK + ", kinds=" + kinds + ", , under='" + under);

		
		List<Map<String, Object>> hits = new ArrayList<Map<String, Object>>();
		List<IRPModelElement> results = rh.searchElements(query, kinds, SearchFindAsEnum.RP_SEARCH_WILDCARD);
		
		for (IRPModelElement el : results)
		{
			Map<String, Object> m = rh.serializeToJsonObject(el, true);

			hits.add(m);
		}
		
		
		

//		if ((kinds.isEmpty() == false) && (under == null || under.isEmpty()))
//		{
//			// wenn Filter, aber kein Startknoten, dann auf Top-Level beschränken
//			List<IRPModelElement> found = new ArrayList<>();
//
//			for (String k : kinds)
//			{
//				List<IRPModelElement> elements = rh.findByName(query, k, true);
//				found.addAll(elements);
//			}
//
//			for (IRPModelElement el : found)
//			{
//				trace("found: " + el.getFullPathName() + " (" + el.getMetaClass() + ")");
//				Map<String, Object> m = rh.serialize(el);
//
//				hits.add(m);
//			}
//
//			Map<String, Object> result = new LinkedHashMap<String, Object>();
//			result.put("content", hits);
//
//			trace("result: " + result);
//
//			return result;
//
//		}

//		Deque<IRPModelElement> q = new ArrayDeque<IRPModelElement>();
//
//		if (under != null && !under.isEmpty())
//		{
//			// einfache Suche nach Startknoten anhand Prefix
//			Optional<IRPModelElement> opt = rh.byGUID(under);
//			if (opt.isPresent())
//			{
//				IRPModelElement start = opt.get();
//				q.add(start);
//			}
//			else
//			{
//				trace("call: under='" + under + "' not found!");
//				// Startknoten nicht gefunden, also keine Treffer
//				return errorResult();
//			}
//		}
//		else
//		{
//			q.addAll(rh.topLevelElements());
//		}
//
//		while (!q.isEmpty() && hits.size() < topK)
//		{
//			IRPModelElement el = q.poll();
//
//			if (el == null)
//			{
//				continue;
//			}
//
//			String name = el.getName() != null ? el.getName() : "";
//			String qname = el.getFullPathName() != null ? el.getFullPathName() : "";
//			String kind = el.getMetaClass();
//
//			boolean match = name.toLowerCase().contains(query.toLowerCase());
//			
//			if (kinds.isEmpty() == false)
//			{
//				match = match && kinds.contains(kind);
//			}
//			
//			
//
//			if (match)
//			{
//
//				Map<String, Object> m = rh.serialize(el);
//
//				hits.add(m);
//			}
//
//			if (el instanceof IRPPackage)
//			{
//				IRPPackage p = (IRPPackage) el;
//				List<IRPModelElement> children = el.getNestedElements().toList();
//
//				for (IRPModelElement child : children)
//				{
//					q.add(child);
//				}
//			}
//		}

		Map<String, Object> result = new LinkedHashMap<String, Object>();
		result.put("content", hits);

		trace("result: " + result);

		return result;
	}

	private Object errorResult()
	{
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		result.put("content", Collections.emptyList());
		return result;
	}

}