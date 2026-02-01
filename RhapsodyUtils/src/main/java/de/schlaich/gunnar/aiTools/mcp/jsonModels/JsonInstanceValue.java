package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.telelogic.rhapsody.core.IRPInstanceValue;
import com.telelogic.rhapsody.core.IRPLiteralSpecification;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;

import de.schlaich.gunnar.aiTools.mcp.jsonModels.JsonModelElementBase.ImportMode;

public class JsonInstanceValue extends JsonValueSpecification
{

	@JsonProperty("value")
	protected JsonModelElementBase value = null;
	
	public JsonInstanceValue(IRPModelElement aModelElement, int level)
	{
		super(aModelElement, level);
		
		if (aModelElement == null)
		{
			return;
		}
		
		if (aModelElement instanceof JsonInstanceValue == false)
		{
			return;
		}
		
		IRPInstanceValue instanceValue = (IRPInstanceValue) aModelElement;
		
		value = new JsonModelElementBase(instanceValue.getValue(), 0);
		
	}

	public JsonInstanceValue()
	{
		
	}
	
	public IRPModelElement toModelElement(JsonModelElementBase parent, IRPProject project, ImportMode importMode)
	{
		IRPModelElement model = super.toModelElement(parent, project, importMode);
		
		if (model == null)
		{
			return null;
		}
		
		if (model instanceof JsonInstanceValue == false)
		{
			
			return null;
		}

		IRPInstanceValue instanceValue = (IRPInstanceValue) model;
		
		if (importMode == ImportMode.reference)
		{		
			return model;
		}
		
		if (value != null)
		{
			IRPModelElement e = value.toModelElement(parent, project, ImportMode.reference);
			instanceValue.setValue(e);
		}

		return model;
	}
		

}
