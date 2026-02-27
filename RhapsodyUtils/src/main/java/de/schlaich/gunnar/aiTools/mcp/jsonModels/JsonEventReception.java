package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPEvent;
import com.telelogic.rhapsody.core.IRPEventReception;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPUseCase;

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
	
	@Override
	public IRPModelElement createModelElement(IRPModelElement parent)
	{
		if (parent instanceof IRPClass)
		{
			IRPClass parentClass = (IRPClass) parent;
			return parentClass.addEventReception(name);
		}
		else if(parent instanceof IRPUseCase)
		{
			IRPUseCase parentUseCase = (IRPUseCase) parent;
			
			if(event == null)
            {
                trace("Event is null for event reception " + name);
                return null;
            }
			
			IRPEvent eventModel = (IRPEvent)this.event.getReference(parent.getProject());
			
			return parentUseCase.addEventReceptionWithEvent(creationName, eventModel);
		}
		
		trace("Parent element is not an IRPClass or IRPUseCase. Its " + parent.getMetaClass());
		return null;
	}
	
	
	@Override
	public void setAttributes(IRPModelElement aModelElement, IRPProject aProject, ImportMode aImportMode)
	{
		if (aModelElement instanceof IRPEventReception == false)
		{
			return;
		}

		super.setAttributes(aModelElement, aProject, aImportMode);

		IRPEventReception theEventReception = (IRPEventReception) aModelElement;

		if (event != null)
		{
			IRPEvent irpEvent = (IRPEvent) event.getReference(aProject);
			theEventReception.setEvent(irpEvent);
		}

	}

}
