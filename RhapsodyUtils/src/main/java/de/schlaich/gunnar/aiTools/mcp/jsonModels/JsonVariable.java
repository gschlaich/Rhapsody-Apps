package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPVariable;

public class JsonVariable extends JsonModelElement
{

	/*
	 * java.lang.String getDeclaration() Returns the type declaration if an
	 * on-the-fly type was used for the element rather than an existing type.
	 * java.lang.String getDefaultValue() Returns the default value that was set for
	 * the variable. IRPClassifier getType() Returns the type of the variable.
	 * IRPCollection getValueSpecifications() Returns a collection of the initial
	 * values that were declared for elements where the multiplicity is greater than
	 * one.
	 */

	@JsonProperty("declaration")
	protected String declaration = null;

	@JsonProperty("defaultValue")
	protected String defaultValue = null;

	@JsonProperty("type")
	protected JsonModelElementBase vType = null;

	@JsonProperty("valueSpecifications")
	protected List<JsonModelElementBase> valueSpecifications = null;

	public JsonVariable(IRPModelElement aModelElement, int level)
	{
		super(aModelElement, level);

		if (aModelElement == null)
		{
			return;
		}

		if (aModelElement instanceof IRPVariable)
		{
			IRPVariable theVar = (IRPVariable) aModelElement;

			declaration = theVar.getDeclaration();
			defaultValue = theVar.getDefaultValue();

			if (theVar.getType() != null)
			{
				vType = new JsonModelElementBase(theVar.getType());
			}

			valueSpecifications = convertToJsonModelElementBaseList(theVar.getValueSpecifications());
		}

	}

	public IRPModelElement toModelElement(JsonModelElementBase parent, IRPProject project, ImportMode aImportMode)
	{
		IRPModelElement model = super.toModelElement(parent, project, aImportMode);

		if (model == null)
		{
			return null;
		}

		if (aImportMode == ImportMode.reference)
		{
			return model;
		}

		if (model instanceof IRPVariable == false)
		{
			return null;
		}
		IRPVariable theVar = (IRPVariable) model;

		if (isSet(declaration))
		{
			theVar.setDeclaration(declaration);
		}
		else
		{
			if (vType != null)
			{

				theVar.setType((IRPClassifier) vType.getReference(project));
			}

		}

		if (isSet(defaultValue))
		{
			theVar.setDefaultValue(defaultValue);
		}

		return model;

	}
	
	public JsonModelElementBase getType()
	{
		return vType;
	}

	public JsonVariable()
	{
		// TODO Auto-generated constructor stub
	}

}
