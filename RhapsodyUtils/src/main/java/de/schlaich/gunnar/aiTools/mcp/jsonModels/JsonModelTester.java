package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;

import de.schlaich.gunnar.aiTools.mcp.jsonModels.JsonModelElementBase.ImportMode;
import de.schlaich.gunnar.aiTools.mcp.jsonModels.JsonModelElementBase.MetaClass;

public class JsonModelTester
{

	private Consumer<String> myTraceAction = null;

	private static JsonModelTester instance = null;

	private IRPApplication myApp = null;

	public JsonModelTester(IRPApplication aApp, Consumer<String> aTraceAction)
	{

		myApp = aApp;
		myTraceAction = aTraceAction;

		JsonModelFactory.Instance(aApp, aTraceAction);
	}

	private void trace(String aMessage)
	{
		if (myTraceAction == null)
		{
			// no traceaction set...
			return;
		}

		aMessage = this.getClass().getSimpleName() + ": " + aMessage;

		myTraceAction.accept(aMessage);
	}

	public static JsonModelTester Instance(IRPApplication aApp, Consumer<String> aTraceAction)
	{

		if (instance == null)
		{
			instance = new JsonModelTester(aApp, aTraceAction);
		}

		return instance;
	}

	public String getJsonSchema(IRPModelElement aModelElement)
	{
		JsonModelFactory jsonFactory = JsonModelFactory.Instance();

		MetaClass metaClass = MetaClass.valueOf(aModelElement.getMetaClass());

		return jsonFactory.generateJsonSchemaForMetaClass(metaClass);
	}

	public String getJson(IRPModelElement aModelElement)
	{

		// getAllJsonShemas();

		// initCache();

		String jsonString = "";

		JsonModelFactory jsonFactory = JsonModelFactory.Instance();

		if (aModelElement == null)
		{
			trace("getJson: ModelElement is null!");
			return "";
		}

		JsonModelElementBase jme = jsonFactory.getJsonModelElement(aModelElement, 0);
		try
		{
			jsonString = jme.toJsonString();
		}
		catch (IOException e)
		{
			trace("Exception : " + e.getMessage());
			return "";
		}

		//trace("JsonString: \n" + jsonString);

		trace("Successfully converted model element " + aModelElement.getFullPathName() + " to JSON string.");

		return jsonString;

	}

	public JsonModelElementBase getModelElementFromJson(String jsonString)
	{
		JsonModelFactory jsonFactory = JsonModelFactory.Instance();
		return jsonFactory.fromJson(jsonString);
	}
	
	
	public IRPModelElement getRhapsodyModelElementFromJson(String jsonString, IRPModelElement parentElement, IRPProject project)
	{
		
		JsonModelFactory jsonFactory = JsonModelFactory.Instance();
		JsonModelElementBase jme = jsonFactory.fromJson(jsonString);
		if (jme == null)
		{
			return null;
		}
		
		
		JsonModelElementBase parentJme = jsonFactory.getJsonModelElement(parentElement, 0);
		
		return jme.toModelElement(parentJme, project, ImportMode.create);
		
	}

	public String getAllJsonShemas()
	{
		JsonModelFactory jsonFactory = JsonModelFactory.Instance();
		return jsonFactory.generateJsonSchemaForRegisteredClasses();
	}
	
	public String listAllMetaClasses()
	{
		
		String result = "";
		JsonAvailableMetaClass availableMetaClasses = new JsonAvailableMetaClass();
		
		try
		{
			result = availableMetaClasses.toJsonString();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
//		JsonModelFactory jsonFactory = JsonModelFactory.Instance();
//		List<MetaClass> metaClasses = jsonFactory.getRegisteredMetaClasses();
//		StringBuilder sb = new StringBuilder();
//		sb.append("Registered MetaClasses:\n");
//		for (MetaClass mc : metaClasses)
//		{
//			sb.append(" - " + mc.toString() + "\n");
//		}
//		result = sb.toString();
		
		//trace(result);
		return result;
	}

}
