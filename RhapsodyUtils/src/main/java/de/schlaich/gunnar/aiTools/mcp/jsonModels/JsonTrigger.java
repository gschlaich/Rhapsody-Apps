package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPTransition;
import com.telelogic.rhapsody.core.IRPTrigger;

import de.schlaich.gunnar.aiTools.mcp.jsonModels.JsonModelElementBase.ImportMode;

public class JsonTrigger extends JsonModelElement
{

	@JsonProperty("body")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	protected String body = null;
	@JsonProperty("isOperation")
	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	protected boolean isOperation = false;
	@JsonProperty("isTimeout")
	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	protected boolean isTimeout = false;
	@JsonProperty("itsOperation")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	protected JsonModelElementBase itsOperation = null;
	
	
	public JsonTrigger(IRPModelElement aModelElement, int level)
	{
		super(aModelElement, level);
		
		if (aModelElement == null)
		{
			return;
		}
		
		if (aModelElement instanceof IRPTrigger)
		{
			IRPTrigger trigger = (IRPTrigger) aModelElement;
			this.body = trigger.getBody();
			
			if(trigger.isOperation()==1)
			{
				this.isOperation = true;
			}
			if(trigger.isTimeout()==1)
			{
				this.isTimeout = true;
			}
			
			if(trigger.getItsOperation() != null)
			{
				this.itsOperation = getJsonModelElementBase(trigger.getItsOperation());
			}	
		}
		
	}

	public JsonTrigger()
	{
		// TODO Auto-generated constructor stub
	}
	
	public JsonModelElementBase getItsOperation() 
	{
		return itsOperation;
	}
	
	
	@Override
	public IRPModelElement createModelElement(IRPModelElement parent)
	{
		
		if (parent == null)
		{
			return null;
		}
		if (parent instanceof IRPTransition)
		{
			IRPTransition transition = (IRPTransition) parent;
			return transition.setItsTrigger(body);
		}
		return null;
		
	}
	
	@Override
	public void setAttributes(IRPModelElement modelElement, IRPProject project, ImportMode importMode)
	{
		super.setAttributes(modelElement, project, importMode);

		if (modelElement instanceof IRPTrigger)
		{
			IRPTrigger trigger = (IRPTrigger) modelElement;
			trigger.setBody(body);
		}
	}

}
