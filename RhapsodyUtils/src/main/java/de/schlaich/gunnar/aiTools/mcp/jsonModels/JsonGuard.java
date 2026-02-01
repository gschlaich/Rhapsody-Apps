package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.telelogic.rhapsody.core.IRPGuard;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;

public class JsonGuard extends JsonModelElement
{

	/*
	 java.lang.String 	getBody()
          get property body 
	 */
	
	@JsonProperty("body")
	protected String body = null;
	
	public JsonGuard(IRPModelElement aModelElement, int level)
	{
		super(aModelElement, level);
		
		if (aModelElement == null)
		{
			return;
		}
		
		if (aModelElement instanceof IRPGuard)
		{
			IRPGuard theGuard = (IRPGuard) aModelElement;

			body = theGuard.getBody();
		}
	}

	public JsonGuard()
	{

	}
	
	public IRPModelElement toModelElement(JsonModelElementBase parent, IRPProject project, ImportMode importMode)
	{
		IRPModelElement model = super.toModelElement(parent, project, importMode);
		
		if (model == null)
		{
			return null;
		}
		
		if (model instanceof IRPGuard == false)
		{
			return null;
		}
		
		IRPGuard theGuard = (IRPGuard) model;
		
		if (importMode == ImportMode.reference)
		{
			return theGuard;
		}

		theGuard.setBody(body);

		return theGuard;
	}

}
