package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.telelogic.rhapsody.core.IRPLiteralSpecification;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;

import de.schlaich.gunnar.aiTools.mcp.jsonModels.JsonModelElementBase.ImportMode;

public class JsonLiteralSpecification extends JsonValueSpecification
{

	@JsonProperty("value")
	protected String value = null;
	
	public JsonLiteralSpecification(IRPModelElement aModelElement, int level)
	{
		super(aModelElement, level);
		value = ((IRPLiteralSpecification)aModelElement).getValue();
	}

	public JsonLiteralSpecification()
	{
		
	}
	
	public IRPModelElement toModelElement(JsonModelElementBase parent, IRPProject project, ImportMode importMode)
	{
		IRPModelElement model = super.toModelElement(parent, project, importMode);
		
		if (model == null)
		{
			return null;
		}
		
		if (model instanceof IRPLiteralSpecification == false)
		{
			
			return null;
		}

		IRPLiteralSpecification literalSpec = (IRPLiteralSpecification) model;
		
		if (importMode == ImportMode.reference)
		{		
			return model;
		}

		literalSpec.setValue(value);

		return model;
	}
	
	

}
