package de.schlaich.gunnar.aiTools.mcp;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPCollection;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPStereotype;

public class ModelIndexer
{
	private final RhapsodyClient rh;

	public static class Entry
	{
		private final String id;
		private final String kind;
		private final String name;
		private final String qname;
		private final String stereotype;

		public Entry(String id, String kind, String name, String qname, String stereotype)
		{
			this.id = id;
			this.kind = kind;
			this.name = name;
			this.qname = qname;
			this.stereotype = stereotype;
		}

		public String getId()
		{
			return id;
		}

		public String getKind()
		{
			return kind;
		}

		public String getName()
		{
			return name;
		}

		public String getQname()
		{
			return qname;
		}

		public String getStereotype()
		{
			return stereotype;
		}
	}

	private final Map<String, Entry> byId = new HashMap<>();
	private final List<Entry> all = new ArrayList<>();

	public ModelIndexer(RhapsodyClient rh)
	{
		this.rh = rh;
	}

	public void build()
	{
		for (IRPModelElement root : rh.topLevelElements())
		{
			traverse(root);
		}
	}

	private void traverse(IRPModelElement el)
	{
		String id = RhapsodyClient.id(el);
		String kind = RhapsodyClient.kind(el);
		String qn = RhapsodyClient.qname(el);
		String name = el.getName();
		
		
		String stereo = "";
		
		List<IRPStereotype> stereotypes = el.getStereotypes().toList();
		for (IRPStereotype s : stereotypes)
		{
			if (stereo.isEmpty())
			{
				stereo = s.getName();
			}
			else
			{
				stereo += "," + s.getName();
			}
		}
		
		
		
		Entry e = new Entry(id, kind, name, qn, stereo);
		byId.put(id, e);
		all.add(e);

		if (el instanceof IRPPackage)
		{
			IRPCollection els = ((IRPPackage) el).getNestedElements();
			for (int i = 1; i <= els.getCount(); i++)
				traverse((IRPModelElement) els.getItem(i));
		}
		if (el instanceof IRPClass)
		{
			IRPCollection feats = ((IRPClass) el).getNestedElements();
			for (int i = 1; i <= feats.getCount(); i++)
				traverse((IRPModelElement) feats.getItem(i));
		}
	}

	public List<Entry> search(String query, int topK)
	{
		String q = query.toLowerCase();
		return all.stream()
				.filter(e -> e.getName().toLowerCase().contains(q) || e.getQname().toLowerCase().contains(q)
						|| e.getStereotype().toLowerCase().contains(q) || e.getKind().toLowerCase().contains(q))
				.limit(topK).collect(Collectors.toList());
	}

	public Optional<Entry> get(String id)
	{
		return Optional.ofNullable(byId.get(id));
	}
}