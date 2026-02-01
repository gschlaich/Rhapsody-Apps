package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.telelogic.rhapsody.core.IRPAction;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;

public class JsonAction extends JsonModelElement
{
	/*
	java.lang.String 	getBody()
    Gets the code defined as the action for the transition.
	*/
	
	@JsonProperty("body")
	protected String body = null;
	
	public JsonAction(IRPModelElement aModelElement, int level)
	{
		super(aModelElement, level);
		
		if (aModelElement == null)
		{
			return;
		}
		
		if (aModelElement instanceof IRPAction)
		{
			IRPAction theAction = (IRPAction) aModelElement;

			body = theAction.getBody();
		}

	}
	
	public JsonAction()
	{
		// TODO Auto-generated constructor stub
	}
	
	public IRPModelElement toModelElement(JsonModelElementBase parent, IRPProject project, ImportMode aImportMode)
	{
		IRPModelElement model = super.toModelElement(parent, project, aImportMode);
		
		if (model == null)
		{
			return null;
		}
		
		if (model instanceof IRPAction == false)
		{
			return null;
		}
		
		IRPAction theAction = (IRPAction) model;
		
		if (aImportMode == ImportMode.reference)
		{
			return theAction;
		}
		
		if(isSet(body))
		{
			theAction.setBody(body);
		}

		return theAction;
	}

}
