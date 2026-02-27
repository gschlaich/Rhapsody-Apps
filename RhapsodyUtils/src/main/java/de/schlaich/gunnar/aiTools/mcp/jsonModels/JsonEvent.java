package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.telelogic.rhapsody.core.IRPEvent;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPPackage;
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
	
	
	
	@Override
	public IRPModelElement createModelElement(IRPModelElement parent)
	{
		if (parent instanceof IRPPackage)
		{
			IRPPackage parentPackage = (IRPPackage) parent;
			IRPEvent event = parentPackage.addEvent(name);
			return event;
		}
		trace("Parent element is not an IRPPackage. Its " + parent.getMetaClass());
		return null;
	
	}
	
	@Override
	public void setAttributes(IRPModelElement aModelElement, IRPProject aProject, ImportMode aImportMode)
    {
        super.setAttributes(aModelElement, aProject, aImportMode);
        
        if (aModelElement instanceof IRPEvent == false)
        {
            return;
        }
        
        IRPEvent theEvent = (IRPEvent) aModelElement;
        
        if (baseEvent != null)
        {
        	theEvent.setBaseEvent((IRPEvent) baseEvent.getReference(aProject));
        }
        if (superEvent != null)
        {
            theEvent.setSuperEvent((IRPEvent) superEvent.getReference(aProject));
        }
    }

}
