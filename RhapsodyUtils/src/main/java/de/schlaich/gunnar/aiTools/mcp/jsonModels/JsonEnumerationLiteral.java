package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.telelogic.rhapsody.core.IRPEnumerationLiteral;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPType;

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
	
	
	@Override
	public IRPModelElement createModelElement(IRPModelElement aParentElement)
	{
		IRPModelElement modelElement = null;

		if (aParentElement instanceof IRPType)
		{
			IRPType parentType = (IRPType) aParentElement;

			modelElement = parentType.addEnumerationLiteral(name);
		}

		return modelElement;

	}
	
	@Override
	public void setAttributes(IRPModelElement aModelElement, IRPProject aProject, ImportMode aImportMode)
	{
		
		super.setAttributes(aModelElement, aProject, aImportMode);
		
		if (aModelElement instanceof IRPEnumerationLiteral == false)
		{
			return;
		}

		IRPEnumerationLiteral theLiteral = (IRPEnumerationLiteral) aModelElement;

		if (isSet(value))
		{
			theLiteral.setValue(value);
		}

	}

}
