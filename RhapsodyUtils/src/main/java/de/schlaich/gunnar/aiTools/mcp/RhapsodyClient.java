package de.schlaich.gunnar.aiTools.mcp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPCollection;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPSearchManager;
import com.telelogic.rhapsody.core.IRPSearchQuery;
import de.schlaich.gunnar.aiTools.mcp.jsonModels.JsonModelElementBase;
import de.schlaich.gunnar.aiTools.mcp.jsonModels.JsonModelElementBase.ImportMode;
import de.schlaich.gunnar.aiTools.mcp.jsonModels.JsonModelElementBase.MetaClass;
import de.schlaich.gunnar.aiTools.mcp.jsonModels.JsonModelFactory;

public class RhapsodyClient
{
	private final IRPApplication app;
	private final IRPProject project;
	// private MCP_JSONExporter myJsonExporter = null;
	// private RhapsodyJSONTools myRhapsodyJsonTools = null;
	private Consumer<String> myTraceAction = null;

	private JsonModelFactory myJsonModelFactory;

	// caching system

	private Map<String, List<IRPModelElement>> cacheFindByMetaClass = new java.util.HashMap<>();

	/**
	 * @param projectPath Voller Pfad zur .rpy / .rpyx Datei
	 * @param writable    true = beim close() wird gespeichert
	 */
	public RhapsodyClient(IRPApplication aApp, Consumer<String> aTraceAction)
	{

		this.app = aApp;
		this.project = app.activeProject();

		myJsonModelFactory = JsonModelFactory.Instance(aApp, aTraceAction);

		// myRhapsodyJsonTools = new
		// RhapsodyJSONTools(app.getApplicationConnectionString(), true);
		// myJsonExporter = new MCP_JSONExporter(myRhapsodyJsonTools);
		myTraceAction = aTraceAction;
		// myRhapsodyJsonTools.setTrace(myTraceAction);

	}

	private void trace(String aMessage)
	{
		if (myTraceAction == null)
		{
			// no traceaction set...
			return;
		}

		aMessage = "RhapsodyClent: " + aMessage;

		myTraceAction.accept(aMessage);
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

		trace("Top Level Elements: " + out.size());

		return out;
	}

	public List<IRPModelElement> findByName(String name, String metaclass, boolean isQuery)
	{
		List<IRPModelElement> out = new ArrayList<IRPModelElement>();
		List<IRPModelElement> metaElements = cacheFindByMetaClass.get(metaclass);

		if (metaElements == null)
		{

			metaElements = project.getNestedElementsByMetaClass(metaclass, 1).toList();
			trace("Caching " + metaElements.size() + " elements of type " + metaclass);
			cacheFindByMetaClass.put(metaclass, metaElements);
		}

		for (IRPModelElement el : metaElements)
		{

			if (isQuery)
			{
				if (el.getName() != null && el.getName().toLowerCase().contains(name.toLowerCase()))
				{
					out.add(el);
				}
			}
			else
			{
				if (el.getName() != null && el.getName().equals(name))
				{
					out.add(el);
				}
			}

		}

		return out;
	}

	public List<IRPModelElement> findByName(String name, String metaclass)
	{

		IRPSearchManager searchManager = app.getSearchManager();

		IRPSearchQuery query = searchManager.createSearchQuery();

		query.addFilterElementType(metaclass);

		query.addFilterSearchInField(IRPSearchQuery.SearchInField.NAME);
		query.setSearchText(name);
		query.addSearchScope(project);

		List<IRPModelElement> results = searchManager.search(query).toList();

		return results;
	}

	@SuppressWarnings("unchecked")
	public List<IRPModelElement> findReferences(String aGuid)
	{
		Optional<IRPModelElement> optEl = byGUID(aGuid);

		if (optEl.isPresent() == false)
		{
			return new ArrayList<IRPModelElement>();
		}

		IRPModelElement el = optEl.get();

		if (el instanceof IRPClassifier == false)
		{
			return new ArrayList<IRPModelElement>();
		}

		IRPClassifier cls = (IRPClassifier) el;

		// List<IRPModelElement> relations = cls.getRelationsIncludingBases().toList();

		List<IRPModelElement> references = el.getReferences().toList();

		return references;
	}

	/** Lookup eines beliebigen Elements per GUID */
	public Optional<IRPModelElement> byGUID(String guid)
	{
		if (guid.startsWith("GUID") == false)
		{
			guid = "GUID " + guid;
		}

		IRPModelElement el = project.findElementByGUID(guid);
		return Optional.ofNullable(el);
	}

	/** Lookup eines beliebigen Elements per Qualified Name */
	public Optional<IRPModelElement> byQualifiedName(String qname, String metaclass)
	{
		IRPModelElement el = project.findNestedElementRecursive(qname, metaclass);
		return Optional.ofNullable(el);
	}

	public Optional<IRPModelElement> findfrom(String aGUID, String aName, String aMetaClass)
	{
		IRPModelElement parent = project.findElementByGUID(aGUID);
		if (parent == null)
		{
			return Optional.empty();
		}

		IRPModelElement el = parent.findNestedElementRecursive(aName, aMetaClass);
		return Optional.ofNullable(el);
	}

	public List<IRPModelElement> findAllofType(String aGUID, String aMetaClass)
	{

		IRPModelElement parent = project.findElementByGUID(aGUID);
		if (parent == null)
		{
			return new ArrayList<IRPModelElement>();
		}

		return parent.getNestedElementsByMetaClass(aMetaClass, 1).toList();

	}
	
	
	public JsonModelElementBase updateFromJson(String partialJson, JsonModelElementBase existing) 
		    throws JsonProcessingException
		{
		    ObjectMapper mapper = new ObjectMapper();
		    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		    ObjectReader updater = mapper.readerForUpdating(existing);
		    JsonModelElementBase updated = updater.readValue(partialJson);

		    return updated;
		}
	
	
	public JsonModelElementBase updateModelFromJson(IRPModelElement aTargetModel, String aJsonModelString)
	{
		
		JsonModelElementBase targetJson = toJsonObject(aTargetModel, false);

		JsonModelElementBase mergedJson = null;
		try
		{
			mergedJson = updateFromJson(aJsonModelString, targetJson);
		}
		catch (JsonProcessingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		return mergedJson;

	}
	
	
	

	public String importModelFromJson(String aJsonModelString, String aTargetGUID, JsonModelElementBase.ImportMode aImportMode,
			boolean aValidateOnly)
	{

		if (aTargetGUID == null || aTargetGUID.isEmpty())
		{
			
			return null;
		}
		
		
		Optional<IRPModelElement> targetModelOpt = byGUID(aTargetGUID);

		if (targetModelOpt.isPresent() == false)
		{
			return null;
		}
		
		IRPModelElement targetModel = targetModelOpt.get();
		
		JsonModelElementBase modifiedJson = null;
	
		modifiedJson = myJsonModelFactory.fromJson(aJsonModelString);
		
		
		JsonModelElementBase parentJson = new JsonModelElementBase(targetModel);
		
		
		
		if(aValidateOnly == false)
		{
			IRPModelElement modifiedModel =  modifiedJson.toModelElement(parentJson, project, aImportMode);
			if (modifiedModel == null)
			{
				return "Error: Could not import model from JSON.";
			}

			//String ret = toJsonString(modifiedModel, false);
            String ret = "OK: Model imported successfully.";
			
			return ret;
		}
		else
		{
//			String ret = "";
//			try
//			{
//				ret = modifiedJson.toJsonString();
//			}
//			catch (IOException e)
//			{
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				ret = "Error: Could not serialize modified JSON model.";
//			}
//			return ret;
			return "OK: JSON model validated successfully.";
		}
		
	}


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

	public String toJsonString(IRPModelElement aModelElement, boolean asStub)
	{
		JsonModelElementBase jme = toJsonObject(aModelElement, asStub);
		
		if (jme == null)
		{
			return null;
		}

		String json = null;

		try
		{
			json = jme.toJsonString();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (json == null || json.isEmpty())
		{
			return null;
		}

		return json;

	}

	private JsonModelElementBase toJsonObject(IRPModelElement aModelElement, boolean asStub)
	{
		if (aModelElement == null)
		{
			return null;
		}

		JsonModelElementBase jme = null;

		if (asStub == true)
		{
			jme = new JsonModelElementBase(aModelElement);
		}
		else
		{
			jme = myJsonModelFactory.getJsonModelElement(aModelElement, 0);
		}

		if (jme == null)
		{
			return null;
		}
		return jme;
	}
	
	

	public String toJsonBase64(IRPModelElement aModelElement, boolean asStub)
	{
		String json = toJsonString(aModelElement, asStub);

		String jsonBase64 = Base64.getEncoder().encodeToString(json.getBytes());
		return jsonBase64;
	}

	public Map<String, Object> serializeToJsonObject_(IRPModelElement el, boolean asStub)
	{
		Map<String, Object> m = new LinkedHashMap<String, Object>();
		m.put("type", "resource");

		// Create the resource submap
		Map<String, Object> resource = new LinkedHashMap<>();
		String jsonElement = toJsonBase64(el, asStub);

		String description = "Model information of " + el.getName() + " (" + el.getMetaClass() + ")"
				+ (asStub ? " stub" : "") + " in base64-encoded JSON format.";

		resource.put("uri", "data:application/json;base64," + jsonElement);
		resource.put("name", "data.json");
		resource.put("description", description);
		resource.put("mimeType", "application/json");

		// Add the resource submap to the main map
		m.put("resource", resource);

		return m;

	}

	public Map<String, Object> serializeToJsonObject(IRPModelElement el, boolean asStub)
	{
		Map<String, Object> m = new LinkedHashMap<String, Object>();

		String jsonElement = toJsonString(el, asStub);

		m.put("type", "text");
		m.put("text", jsonElement);

		return m;

	}

	public Map<String, Object> serializeToJsonSchemaObject(String aMetaClassName)
	{
		Map<String, Object> m = new LinkedHashMap<String, Object>();

		String schema = myJsonModelFactory.generateJsonSchemaForMetaClassName(aMetaClassName);

		m.put("type", "text");
		m.put("text", schema);

		return m;

	}

	public List<String> getAllMetaClassNames()
	{
		
		//List<String> metaClasses = myJsonModelFactory.getRegisteredMetaClasses();
		
		List<String> metaClassNames = myJsonModelFactory.getRegisteredMetaClassNames();
		
		return metaClassNames;

	}

	public Map<String, Object> serializeToJsonBaseObject(IRPModelElement el)
	{
		Map<String, Object> m = new LinkedHashMap<String, Object>();
		m.put("type", "text");
		m.put("text", "name: " + el.getName() + " GUID: " + el.getGUID() + " type: " + el.getMetaClass() + " qname: "
				+ el.getFullPathName());
		return m;
	}

	public List<IRPModelElement> searchElements(String aSearchString, List<String> aMetaClasses, char aSearchOption)
	{

		IRPSearchManager searchManager = app.getSearchManager();

		IRPSearchQuery query = searchManager.createSearchQuery();

		for (String aMetaClass : aMetaClasses)
		{
			query.addFilterElementType(aMetaClass);
		}

		query.addFilterSearchInField(IRPSearchQuery.SearchInField.NAME);
		query.setSearchText(aSearchString);
		query.addSearchScope(project);
		query.setSearchFindAsOption(aSearchOption);
		

		List<IRPModelElement> results = searchManager.search(query).toList();

		return results;
	}

}