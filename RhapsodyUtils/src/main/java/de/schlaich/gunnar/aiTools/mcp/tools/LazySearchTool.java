package de.schlaich.gunnar.aiTools.mcp;

import com.telelogic.rhapsody.core.*;

import java.util.*;

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

	public LazySearchTool(RhapsodyClient rh)
	{

		super("rhapsody.lazySearch", "On-demand search through the model with timeout and filters.",
				new LinkedHashMap<String, Object>()
				{
					{
						put("type", "object");
						put("properties", new LinkedHashMap<String, Object>()
						{
							{
								put("query", new LinkedHashMap<String, Object>()
								{
									{
										put("type", "string");
									}
								});
								put("top_k", new LinkedHashMap<String, Object>()
								{
									{
										put("type", "integer");
										put("default", 20);
									}
								});
								put("timeout_ms", new LinkedHashMap<String, Object>()
								{
									{
										put("type", "integer");
										put("default", 200);
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
								put("stereotypes", new LinkedHashMap<String, Object>()
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
								put("includeFeatures", new LinkedHashMap<String, Object>()
								{
									{
										put("type", "boolean");
										put("default", false);
									}
								});
							}
						});
						put("required", Arrays.asList("query"));
					}
				});
		this.rh = rh;
	}

	@Override
	public Object call(Map<String, Object> args)
	{
		String query = (String) args.get("query");
		int topK = ((Number) args.getOrDefault("top_k", 20)).intValue();
		long timeoutMs = ((Number) args.getOrDefault("timeout_ms", 200)).longValue();
		List<String> kinds = args.containsKey("kinds") ? (List<String>) args.get("kinds") : Collections.emptyList();
		List<String> stereotypes = args.containsKey("stereotypes") ? (List<String>) args.get("stereotypes")
				: Collections.emptyList();
		String under = (String) args.get("under");
		boolean includeFeatures = Boolean.TRUE.equals(args.get("includeFeatures"));

		long deadline = System.currentTimeMillis() + timeoutMs;
		Deque<IRPModelElement> q = new ArrayDeque<IRPModelElement>();
		if (under != null && !under.isEmpty())
		{
			// einfache Suche nach Startknoten anhand Prefix
			Optional<IRPModelElement> opt = rh.byGUID(under); // oder: Lookup by QualifiedName (eigenes Helper)
			if (opt.isPresent()) q.add(opt.get());
		}
		else
		{
			q.addAll(rh.topLevelElements());
		}

		List<Map<String, Object>> hits = new ArrayList<Map<String, Object>>();
		String qLower = query.toLowerCase();
		while (!q.isEmpty() && hits.size() < topK && System.currentTimeMillis() < deadline)
		{
			IRPModelElement el = q.poll();
			if (el == null) continue;
			String name = el.getName() != null ? el.getName() : "";
			String qname = el.getFullPathName() != null ? el.getFullPathName() : "";
			String kind = el.getMetaClass();

			List<IRPStereotype> stList = el.getStereotypes().toList();

			String stereoJoined = "";

			for (IRPStereotype s : stList)
			{
				if (stereoJoined.isEmpty())
				{
					stereoJoined = s.getName();
				}
				else
				{
					stereoJoined += "," + s.getName();
				}
			}

			boolean match = name.toLowerCase().contains(qLower) || qname.toLowerCase().contains(qLower)
					|| stereoJoined.toLowerCase().contains(qLower);
			if (!kinds.isEmpty() && !kinds.contains(kind)) match = false;
			if (!stereotypes.isEmpty())
			{
				boolean any = false;
				for (String s : stereotypes)
					if (stereoJoined.contains(s))
					{
						any = true;
						break;
					}
				if (!any) match = false;
			}
			if (match)
			{
				Map<String, Object> m = new LinkedHashMap<String, Object>();
				m.put("id", el.getGUID());
				m.put("kind", kind);
				m.put("name", name);
				m.put("qualifiedName", qname);
				m.put("stereotype", stereoJoined);
				hits.add(m);
			}

			if (el instanceof IRPPackage)
			{
				IRPCollection ch = ((IRPPackage) el).getNestedElements();
				for (int i = 1; i <= ch.getCount(); i++)
					q.add((IRPModelElement) ch.getItem(i));
			}
			else if (includeFeatures && el instanceof IRPClass)
			{
				IRPCollection feats = ((IRPClass) el).getNestedElements();
				for (int i = 1; i <= feats.getCount(); i++)
					q.add((IRPModelElement) feats.getItem(i));
			}
		}

		boolean partial = (hits.size() < topK && System.currentTimeMillis() >= deadline);
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		result.put("items", hits);
		result.put("partial", partial);
		return result;
	}

}

/*
 * { put("type","object"); put("properties", new LinkedHashMap<String,Object>()
 * { { put("query", new LinkedHashMap<String,Object>() { { put("type","string");
 * } }); put("top_k", new LinkedHashMap<String,Object>() { {
 * put("type","integer"); put("default",20); } }); put("timeout_ms", new
 * LinkedHashMap<String,Object>() { { put("type","integer"); put("default",250);
 * } }); put("kinds", new LinkedHashMap<String,Object>() { {
 * put("type","array"); put("items", new LinkedHashMap<String,Object>() { {
 * put("type","string"); } }); } }); put("stereotypes", new
 * LinkedHashMap<String,Object>() { { put("type","array"); put("items", new
 * LinkedHashMap<String,Object>() { { put("type","string"); } }); } });
 * put("under", new LinkedHashMap<String,Object>() { { put("type","string"); }
 * }); put("includeFeatures", new LinkedHashMap<String,Object>() { {
 * put("type","boolean"); put("default", false); } }); put("offset", new
 * LinkedHashMap<String,Object>() { { put("type","integer"); put("default",0); }
 * }); } }); put("required", Arrays.asList("query")); }
 * 
 * ); /* new LinkedHashMap<String,Object>() { { put("type","object");
 * put("properties", new LinkedHashMap<String,Object>() { { put("query", new
 * LinkedHashMap<String,Object>() { { put("type","string"); } }); put("top_k",
 * new LinkedHashMap<String,Object>() { { put("type","integer");
 * put("default",20); } }); put("timeout_ms", new LinkedHashMap<String,Object>()
 * { { put("type","integer"); put("default",250); } }); put("kinds", new
 * LinkedHashMap<String,Object>() { { put("type","array"); put("items", new
 * LinkedHashMap<String,Object>() { { put("type","string"); } }); } });
 * put("stereotypes", new LinkedHashMap<String,Object>() { {
 * put("type","array"); put("items", new LinkedHashMap<String,Object>() { {
 * put("type","string"); } }); } }); put("under", new
 * LinkedHashMap<String,Object>() { { put("type","string"); } });
 * put("includeFeatures", new LinkedHashMap<String,Object>() { {
 * put("type","boolean"); put("default", false); } }); put("offset", new
 * LinkedHashMap<String,Object>() { { put("type","integer"); put("default",0); }
 * }); } }); put("required", Arrays.asList("query")); } }
 * 
 * 
 * ); }
 * 
 */

/*
 * 
 * 
 * @Override public Object call(Map<String, Object> args) { final String query =
 * (String) args.get("query"); final int topK = ((Number)
 * args.getOrDefault("top_k", 20)).intValue(); final long timeout = ((Number)
 * args.getOrDefault("timeout_ms", 250)).longValue(); final boolean
 * includeFeatures = args.containsKey("includeFeatures") &&
 * Boolean.TRUE.equals(args.get("includeFeatures")); final int offset =
 * ((Number) args.getOrDefault("offset", 0)).intValue();
 * 
 * 
 * final Set<String> kinds = toLowerSet((List<?>) args.get("kinds")); final
 * Set<String> stereos = toLowerSet((List<?>) args.get("stereotypes")); final
 * String under = (String) args.get("under");
 * 
 * 
 * long deadline = System.currentTimeMillis() + Math.max(1, timeout);
 * Deque<IRPModelElement> q = new ArrayDeque<IRPModelElement>();
 * 
 * 
 * // Startknoten bestimmen if (under != null && !under.isEmpty()) {
 * IRPModelElement root = findByQualifiedPrefix(under); if (root != null)
 * q.add(root); } else { for (IRPModelElement e : rh.topLevelElements())
 * q.add(e); }
 * 
 * 
 * List<Map<String,Object>> items = new ArrayList<Map<String,Object>>(); int
 * visited = 0; int accepted = 0; // für offset boolean truncated = false;
 * 
 * 
 * while (!q.isEmpty()) { }
 * 
 * 
 * 
 */