package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;

public class JsonClassifier extends JsonUnit
{

	public JsonClassifier(IRPModelElement aModelElement, int level)
	{
		super(aModelElement, level);
		
		trace("JsonClassifier created from model element: " + aModelElement.getName());
		
	}

	public JsonClassifier()
	{
		// TODO Auto-generated constructor stub
	}
	
	public IRPModelElement toModelElement(JsonModelElementBase parent, IRPProject project, ImportMode importMode)
	{
		IRPModelElement modelElement = super.toModelElement(parent, project, importMode);

		return modelElement;
	}

}
