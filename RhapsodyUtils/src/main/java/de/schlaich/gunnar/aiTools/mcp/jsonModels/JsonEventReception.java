package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.telelogic.rhapsody.core.IRPEvent;
import com.telelogic.rhapsody.core.IRPEventReception;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;

public class JsonEventReception extends JsonInterfaceItem
{

	/*
	 *   IRPEvent 	getEvent()
          method getEvent
	 */
	
	@JsonProperty("event")
	protected JsonModelElementBase event = null;
	
	public JsonEventReception(IRPModelElement aModelElement, int level)
	{
		super(aModelElement, level);
		if (aModelElement == null)
		{
			return;
		}
		if (aModelElement instanceof IRPEventReception)
		{
			IRPEventReception theEventReception = (IRPEventReception) aModelElement;
			event = new JsonModelElementBase(theEventReception.getEvent());
		}
	}

	public JsonEventReception()
	{
		
	}
	
	public IRPModelElement toModelElement(JsonModelElementBase parent, IRPProject project, ImportMode importMode)
	{
		IRPModelElement model = super.toModelElement(parent, project, importMode);
		
		if (model == null)
		{
			return null;
		}
		
		if (model instanceof IRPEventReception == false)
		{
			return null;
		}
		
		IRPEventReception theEventReception = (IRPEventReception) model;
		
		if (importMode == ImportMode.reference)
		{
			return theEventReception;
		}
		
		if (event != null)
		{
			IRPEvent irpEvent = (IRPEvent) event.toModelElement(project, ImportMode.reference); 
			theEventReception.setEvent(irpEvent);
		}
		
		return theEventReception;
	}

}
