package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.telelogic.rhapsody.core.IRPArgument;
import com.telelogic.rhapsody.core.IRPInterfaceItem;
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
	
	@Override
	protected IRPModelElement createModelElement(IRPModelElement aParentElement)
	{
		
		IRPModelElement modelElement = null;
		if (aParentElement instanceof IRPInterfaceItem)
		{
			IRPInterfaceItem parentInterfaceItem = (IRPInterfaceItem) aParentElement;

			modelElement = parentInterfaceItem.addArgument(name);
			
		}
		else
		{
			trace("Parent element is not an IRPInterfaceItem. Its " + aParentElement.getMetaClass());
			modelElement =super.createModelElement(aParentElement);
		}
		
		return modelElement;
	}
	
//	public IRPModelElement toModelElement( JsonModelElementBase parent, IRPProject project, ImportMode importMode)
//    {
//       
//		IRPModelElement model = null;
//		IRPModelElement parentModel = parent.getReference(project);
//		if(importMode == ImportMode.reference)
//		{
//			model = getReference(project);
//			return model;
//		}
//		else
//		{
//			
//			
//			IRPModelElement parentElement = parent.toModelElement(project, ImportMode.reference);
//
//			if (parentElement == null)
//			{
//				return null;
//			}
//
//			//model = parentElement.addArgument(name, type, argumentDirection);
//		}
//		if (model == null)
//		{
//			return null;
//		}
//		
//		if (model instanceof IRPArgument == false)
//		{
//			return null;
//		}
//		
//		IRPArgument theArgument = (IRPArgument) model;
//        
//		if (importMode == ImportMode.reference)
//		{
//			return theArgument;
//		}
//        
//		setAttributes(theArgument, project, importMode);
//        
//        return theArgument;
//    }
	
	@Override
	public void setAttributes(IRPModelElement modelElement, IRPProject project, ImportMode importMode)
	{
		super.setAttributes(modelElement, project, importMode);

		if (modelElement instanceof IRPArgument == false)
		{
			return;
		}

		IRPArgument theArgument = (IRPArgument) modelElement;

		if (argumentDirection != null)
		{
			theArgument.setArgumentDirection(argumentDirection);
		}

	}

}
