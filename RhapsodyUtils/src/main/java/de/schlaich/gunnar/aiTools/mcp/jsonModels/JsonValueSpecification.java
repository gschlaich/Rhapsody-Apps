package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;

import de.schlaich.gunnar.aiTools.mcp.jsonModels.JsonModelElementBase.ImportMode;

public class JsonValueSpecification extends JsonModelElement
{

	public JsonValueSpecification(IRPModelElement aModelElement, int level)
	{
		super(aModelElement, level);
		// TODO Auto-generated constructor stub
	}

	public JsonValueSpecification()
	{
		// TODO Auto-generated constructor stub
	}
	
	public IRPModelElement toModelElement(JsonModelElementBase parent, IRPProject project, ImportMode importMode)
	{

		IRPModelElement model = super.toModelElement(parent, project, importMode);

		return model;
	}
}
