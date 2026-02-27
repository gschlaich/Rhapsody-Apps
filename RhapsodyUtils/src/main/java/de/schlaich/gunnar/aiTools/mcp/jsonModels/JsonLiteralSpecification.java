package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.telelogic.rhapsody.core.IRPLiteralSpecification;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPVariable;

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
	
	
	@Override
	public IRPModelElement createModelElement(IRPModelElement aParentElement)
	{
		if(aParentElement instanceof IRPVariable)
		{
			IRPVariable parentVariable = (IRPVariable) aParentElement;
			return parentVariable.addStringDefaultValue(value);
		}
		
		return null;
	}
	
	
	@Override
	public void setAttributes(IRPModelElement aModelElement, IRPProject aProject, ImportMode aImportMode)
	{
		super.setAttributes(aModelElement, aProject, aImportMode);

		if (aModelElement instanceof IRPLiteralSpecification == false)
		{
			return;
		}

		IRPLiteralSpecification literalSpec = (IRPLiteralSpecification) aModelElement;

		literalSpec.setValue(value);

	}
	

}
