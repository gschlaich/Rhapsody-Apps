package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;

import de.schlaich.gunnar.aiTools.mcp.jsonModels.JsonModelElementBase.ImportMode;

public class JsonClassifier extends JsonUnit
{

	public JsonClassifier(IRPModelElement aModelElement, int level)
	{
		super(aModelElement, level);
		
		//trace("JsonClassifier created from model element: " + aModelElement.getName());
		
	}

	public JsonClassifier()
	{
		// TODO Auto-generated constructor stub
	}
	

	
	public void setAttributes(IRPModelElement aModelElement, IRPProject aProject, ImportMode aImportMode)
	{
		super.setAttributes(aModelElement, aProject, aImportMode);
	}

}
