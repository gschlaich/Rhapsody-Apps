package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.telelogic.rhapsody.core.IRPAction;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPState;
import com.telelogic.rhapsody.core.IRPTransition;

public class JsonAction extends JsonModelElement
{
	/*
	java.lang.String 	getBody()
    Gets the code defined as the action for the transition.
	*/
	
	enum ActionType {
		entry, exit, transition, unknown
	}
	
	@JsonProperty("body")
	protected String body = null;
	
	@JsonProperty("actionType")
	protected ActionType actionType = ActionType.unknown;
	
	public JsonAction(IRPModelElement aModelElement, int level)
	{
		super(aModelElement, level);
		
		if (aModelElement == null)
		{
			return;
		}
		
		if (aModelElement instanceof IRPAction == false)
		{
			return;
		}
		
		IRPModelElement ownerElement = aModelElement.getOwner();
		if(ownerElement == null)
		{
			return;
		}
		
		if (ownerElement instanceof IRPState)
		{
			IRPState parentState = (IRPState) ownerElement;

			if (parentState.getEntryAction() != null && parentState.getEntryAction().equals(aModelElement))
			{
				actionType = ActionType.entry;
			}
			else if (parentState.getExitAction() != null && parentState.getExitAction().equals(aModelElement))
			{
				actionType = ActionType.exit;
			}
			
		}
		else if(ownerElement instanceof IRPTransition)
		{
			actionType = ActionType.transition;
		}

		IRPAction theAction = (IRPAction) aModelElement;

		body = theAction.getBody();
	
	}
	
	public JsonAction()
	{
		// TODO Auto-generated constructor stub
	}
	
//	public IRPModelElement toModelElement(JsonModelElementBase parent, IRPProject project, ImportMode aImportMode)
//	{
//		if (aImportMode == ImportMode.reference)
//		{
//			return super.toModelElement(parent, project, aImportMode);
//		}
//		
//		IRPAction theAction = null;
//		
//		IRPModelElement parentElement = parent.toModelElement(project, ImportMode.reference);
//		
//		if (parentElement == null)
//		{
//			return null;
//		}
//		
//		if (parentElement instanceof IRPState)
//		{
//			IRPState parentState = (IRPState) parentElement;
//			
//			theAction = parentState.setEntryAction(body);
//			
//		}
//		
//		
//		IRPModelElement model = super.toModelElement(parent, project, aImportMode);
//		
//		if (model == null)
//		{
//			return null;
//		}
//		
//		if (model instanceof IRPAction == false)
//		{
//			return null;
//		}
//		
//		IRPAction theAction = (IRPAction) model;
//		
//		if (aImportMode == ImportMode.reference)
//		{
//			return theAction;
//		}
//		
//		setAttributes(theAction, project, aImportMode);
//
//		return theAction;
//	}
	
	@Override
	public void setAttributes(IRPModelElement aModelElement, IRPProject aProject, ImportMode aImportMode)
	{

		if (aModelElement instanceof IRPAction == false)
		{
			return;
		}

		IRPAction theAction = (IRPAction) aModelElement;

		if (isSet(body))
		{
			theAction.setBody(body);
		}
		
		super.setAttributes(aModelElement, aProject, aImportMode);

	}
	
	@Override
	protected IRPModelElement createModelElement(IRPModelElement aParentElement)
    {
		IRPModelElement ret = null;
		
		if (aParentElement instanceof IRPState)
		{
			IRPState parentState = (IRPState) aParentElement;

			if (actionType == ActionType.entry)
			{
				parentState.setEntryAction(body);
				ret = parentState.getTheEntryAction();
			}
			else if (actionType == ActionType.exit)
			{
				parentState.setExitAction(body);
				ret =  parentState.getTheExitAction();
			}
			
		}
		else if (aParentElement instanceof IRPTransition)
		{
			IRPTransition parentTransition = (IRPTransition) aParentElement;

			if (actionType == ActionType.transition)
			{
				ret = parentTransition.setItsAction(body);
			}
			
		}
		
		if (ret == null)
		{
			trace("Action type ("+actionType.name()+")does not match the owner element type "+ aParentElement.getMetaClass());
		}
		return ret;
    }

}
