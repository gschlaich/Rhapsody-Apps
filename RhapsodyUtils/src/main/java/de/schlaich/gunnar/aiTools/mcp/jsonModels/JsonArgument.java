package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.telelogic.rhapsody.core.IRPArgument;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;

public class JsonArgument extends JsonVariable
{

/*
	java.lang.String 	getArgumentDirection()
    Returns the direction of the argument (In, Out, or InOut).
*/
	@JsonProperty("argumentDirection")
	protected String argumentDirection = null;
	
	public JsonArgument(IRPModelElement aModelElement, int level)
	{
		super(aModelElement, level);
		
		if (aModelElement == null)
		{
			return;
		}
		
		argumentDirection = ((IRPArgument)aModelElement).getArgumentDirection();
	}

	public JsonArgument()
	{
		
	}
	
	public IRPModelElement toModelElement( JsonModelElementBase parent, IRPProject project, ImportMode importMode)
    {
        IRPModelElement model  = super.toModelElement(parent, project, importMode);
		if (model == null)
		{
			return null;
		}
		
		if (model instanceof IRPArgument == false)
		{
			return null;
		}
		
		IRPArgument theArgument = (IRPArgument) model;
        
		if (importMode == ImportMode.reference)
		{
			return theArgument;
		}
        
        if (argumentDirection != null)
        {
            theArgument.setArgumentDirection(argumentDirection);
        }
        
        return theArgument;
    }

}
