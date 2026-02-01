package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.telelogic.rhapsody.core.IRPEnumerationLiteral;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;

import de.schlaich.gunnar.aiTools.mcp.jsonModels.JsonModelElementBase.ImportMode;

public class JsonEnumerationLiteral extends JsonModelElement
{

	@JsonProperty("Value")
	protected String value = null;
	
	
	public JsonEnumerationLiteral(IRPModelElement aModelElement, int level)
	{
		super(aModelElement, level);
		
		
		if (aModelElement == null)
		{
			return;
		}
		if (aModelElement instanceof IRPEnumerationLiteral)
		{
			IRPEnumerationLiteral theLiteral = (IRPEnumerationLiteral) aModelElement;

			value = theLiteral.getValue();
		}
		
	}

	public JsonEnumerationLiteral()
	{
		
	}
	
	
	public IRPModelElement toModelElement(JsonModelElementBase parent, IRPProject project, ImportMode importMode)
	{
		IRPModelElement model = super.toModelElement(parent, project, importMode);
		
		if (model == null)
		{
			return null;
		}
		
		if (model instanceof IRPEnumerationLiteral == false)
		{
			return null;
		}
		
		IRPEnumerationLiteral theEnumerationLiteral = (IRPEnumerationLiteral) model;
		
		if (importMode == ImportMode.reference)
		{
			return theEnumerationLiteral;
		}
		
		if (isSet(value))
		{
			theEnumerationLiteral.setValue(value);
		}

		return theEnumerationLiteral;
	}

}
