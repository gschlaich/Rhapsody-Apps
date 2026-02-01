package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;

public class JsonDefaultTransition extends JsonTransition
{

	public JsonDefaultTransition(IRPModelElement aModelElement, int level)
	{
		super(aModelElement, level);
		// TODO Auto-generated constructor stub
	}

	public JsonDefaultTransition()
	{
		// TODO Auto-generated constructor stub
	}
	
	public IRPModelElement toModelElement(JsonModelElementBase parent, IRPProject project, ImportMode importMode)
	{
		IRPModelElement modelElement = super.toModelElement(parent, project, importMode);

		return modelElement;
	}

}
