package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.telelogic.rhapsody.core.IRPGuard;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPTransition;

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
	
	
	
	@Override
	protected IRPModelElement createModelElement(IRPModelElement aParentElement)
	{
		IRPModelElement modelElement = null;

		if (aParentElement instanceof IRPTransition)
		{
			IRPTransition transition = (IRPTransition) aParentElement;

			modelElement = transition.setItsGuard(body);

		}
		else
		{
			trace("Parent element is not an IRPGuard. Its " + aParentElement.getMetaClass());
			modelElement = super.createModelElement(aParentElement);
		}

		return modelElement;
	}
	
	@Override
	public void setAttributes(IRPModelElement aModelElement, IRPProject aProject, ImportMode aImportMode)
	{

		super.setAttributes(aModelElement, aProject, aImportMode);
		
		if (aModelElement instanceof IRPGuard == false)
		{
			return;
		}

		IRPGuard theGuard = (IRPGuard) aModelElement;

		theGuard.setBody(body);

	}

}
