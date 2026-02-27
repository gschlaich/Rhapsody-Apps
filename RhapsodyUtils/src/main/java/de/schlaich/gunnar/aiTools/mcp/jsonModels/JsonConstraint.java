package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.telelogic.rhapsody.core.IRPConstraint;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;

import de.schlaich.gunnar.aiTools.mcp.jsonModels.JsonModelElementBase.ImportMode;

public class JsonConstraint extends JsonAnnotation
{

	  //IRPCollection 	getConstraintsByMe()
      //	Returns all of the model elements affected by this constraint.
	
	@JsonProperty("constraintsByMe")
	List<JsonModelElementBase> constraintsByMe = null;
	
	public JsonConstraint(IRPModelElement aModelElement, int level)
	{
		super(aModelElement, level);
		if (aModelElement == null)
		{
			return;
		}
		if (aModelElement instanceof IRPConstraint)
		{
			IRPConstraint constraint = (IRPConstraint) aModelElement;

			constraintsByMe = convertToJsonModelElementBaseList(constraint.getConstraintsByMe());
		}
	}

	public JsonConstraint()
	{
		// TODO Auto-generated constructor stub
	}
	
	
	
	@Override
	public void setAttributes(IRPModelElement aModelElement, IRPProject aProject, ImportMode aImportMode)
	{
		super.setAttributes(aModelElement, aProject, aImportMode);

		if (aModelElement instanceof IRPConstraint == false)
		{
			return;
		}
		
		

	}
	
}
