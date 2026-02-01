package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.telelogic.rhapsody.core.IRPCollection;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPProject;

public class JsonPackage extends JsonUnit
{

	@JsonProperty("nameSpace")
	protected String nameSpace = null;
	
	//@JsonProperty("GlobalFunctions")
	//protected List<JsonModelElementBase> globalFunctions = new ArrayList<>();
	
	//@JsonProperty("GlobalVariables")
	//protected List<JsonModelElementBase> globalVariables = new ArrayList<>();
	
	public JsonPackage(IRPModelElement aModel, int level)
	{
		super(aModel, level);
		
		if (aModel == null)
		{
			return;
		}
		
		if (aModel instanceof IRPPackage == false)
		{
			return;
		}
		
		IRPPackage thePackage = (IRPPackage)aModel;
		
		nameSpace = thePackage.getNamespace();
		
		IRPCollection functions = thePackage.getGlobalFunctions();
		
		//globalFunctions = convertToJsonModelElementList(functions);
		nestedElements.addAll(convertToJsonModelElementList(functions));
		
		IRPCollection variables = thePackage.getGlobalVariables();
		
		//globalVariables = convertToJsonModelElementList(variables);
		nestedElements.addAll(convertToJsonModelElementList(variables));
		
	}

	public JsonPackage()
	{
		// TODO Auto-generated constructor stub
	}
	
	public IRPModelElement toModelElement(JsonModelElementBase parent, IRPProject project, ImportMode importMode)
	{
		return super.toModelElement(parent, project, importMode);
		
	}
	
//	protected List<JsonModelElementBase> convertToJsonModelElementList(IRPCollection aCollection)
//	{
//		//only Stubs in package...
//		return convertToJsonModelElementBaseList(aCollection);
//	}

}
