package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;

import de.schlaich.gunnar.aiTools.mcp.jsonModels.JsonModelElementBase.ImportMode;

public class JsonComment extends JsonAnnotation
{

	public JsonComment(IRPModelElement aModelElement, int level)
	{
		super(aModelElement, level);
		
	}

	public JsonComment()
	{
		
	}
	
	public IRPModelElement toModelElement(JsonModelElementBase parent, IRPProject project, ImportMode importMode)
	{
		IRPModelElement modelElement = super.toModelElement(parent, project, importMode);

		return modelElement;
	}

}
