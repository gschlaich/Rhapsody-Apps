package de.schlaich.gunnar.aiTools.mcp;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPCollection;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;


public class RhapsodyClient 
{
	private final IRPApplication app;
	private final IRPProject project;
	

	/**
	 * @param projectPath Voller Pfad zur .rpy / .rpyx Datei
	 * @param writable    true = beim close() wird gespeichert
	 */
	public RhapsodyClient(IRPApplication aApp)
	{
		
		this.app = aApp;
		this.project = app.activeProject();
		
		
	}

	/** Zugriff auf das geöffnete Projekt */
	public IRPProject project()
	{
		return project;
	}

	/** Top‑Level‑Pakete (und andere Wurzelelemente) zurückgeben */
	public List<IRPModelElement> topLevelElements()
	{
		List<IRPModelElement> out = new ArrayList<IRPModelElement>();
		IRPCollection pkgs = project.getPackages();
		if (pkgs != null)
		{
			for (int i = 1; i <= pkgs.getCount(); i++)
			{
				Object it = pkgs.getItem(i);
				if (it instanceof IRPModelElement) out.add((IRPModelElement) it);
			}
		}
		return out;
	}

	/** Lookup eines beliebigen Elements per GUID */
	public Optional<IRPModelElement> byGUID(String guid)
	{
		IRPModelElement el = project.findElementByGUID(guid);
		return Optional.ofNullable(el);
	}

// ---------- Statische Helper (bequem für Tools/Indexer) ----------
	public static String qname(IRPModelElement e)
	{
		return e != null ? e.getFullPathName() : null;
	}

	public static String id(IRPModelElement e)
	{
		return e != null ? e.getGUID() : null;
	}

	public static String kind(IRPModelElement e)
	{
		return e != null ? e.getMetaClass() : null;
	}

	
	
}