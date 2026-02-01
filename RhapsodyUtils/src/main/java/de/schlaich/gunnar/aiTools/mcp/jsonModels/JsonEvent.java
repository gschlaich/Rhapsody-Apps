package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.telelogic.rhapsody.core.IRPEvent;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;

public class JsonEvent extends JsonInterfaceItem
{
	
//		IRPEvent 	getBaseEvent()
//		    get property baseEvent
//		IRPEvent 	getSuperEvent()
//		    get property baseEvent
	
	
	@JsonProperty("baseEvent")
	protected JsonModelElementBase baseEvent = null;
	@JsonProperty("superEvent")
	protected JsonModelElementBase superEvent = null;
	

	public JsonEvent(IRPModelElement aModelElement, int level)
	{
		super(aModelElement, level);
		if (aModelElement == null)
		{
			return;
		}
		if (aModelElement instanceof IRPEvent)
		{
			IRPEvent theEvent = (IRPEvent) aModelElement;

			if (theEvent.getBaseEvent() != null)
			{
				baseEvent = new JsonModelElementBase(theEvent.getBaseEvent());
			}
			if (theEvent.getSuperEvent() != null)
			{
				superEvent = new JsonModelElementBase(theEvent.getSuperEvent());
			}
		}
	}

	public JsonEvent()
	{
		
	}
	
	public IRPModelElement toModelElement(JsonModelElementBase parent, IRPProject project, ImportMode importMode)
	{
		
		IRPModelElement model = super.toModelElement(parent, project, importMode);
		
		if (model == null)
		{
			return null;
		}
		
		if (model instanceof IRPEvent == false)
		{
			return null;
		}
		
		IRPEvent theEvent = (IRPEvent) model;
		
		if (importMode == ImportMode.reference)
		{
			return theEvent;
		}

		if (baseEvent != null)
		{
			theEvent.setBaseEvent((IRPEvent) baseEvent.toModelElement(project, ImportMode.reference));
		}
		if (superEvent != null)
		{
			theEvent.setSuperEvent((IRPEvent) superEvent.toModelElement(project, ImportMode.reference));
		}

		return theEvent;
	}

}
