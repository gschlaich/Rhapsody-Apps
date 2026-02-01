package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPGeneralization;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;

public class JsonGeneralization extends JsonModelElement
{

	/*
	 * IRPClassifier getBaseClass() get method baseClass IRPClassifier
	 * getDerivedClass() get method derivedClass java.lang.String
	 * getExtensionPoint() get property extensionPoint int getIsVirtual() get
	 * property is virtual java.lang.String getVisibility() get property visibility
	 */
	@JsonProperty("extensionPoint")
	protected String extensionPoint = null;
	@JsonProperty("isVirtual")
	protected boolean isVirtual = false;
	@JsonProperty("visibility")
	protected String visibility = null;
	@JsonProperty("baseClass")
	protected JsonModelElementBase baseClass = null;
	@JsonProperty("derivedClass")
	protected JsonModelElementBase derivedClass = null;

	public JsonGeneralization(IRPModelElement aModelElement, int level)
	{
		super(aModelElement, level);
		if (aModelElement == null)
		{
			return;
		}
		if (aModelElement instanceof IRPGeneralization)
		{
			IRPGeneralization theGen = (IRPGeneralization) aModelElement;

			extensionPoint = theGen.getExtensionPoint();
			isVirtual = theGen.getIsVirtual() == 1;
			visibility = theGen.getVisibility();

			if (theGen.getBaseClass() != null)
			{
				baseClass = new JsonModelElementBase(theGen.getBaseClass());
			}
			if (theGen.getDerivedClass() != null)
			{
				derivedClass = new JsonModelElementBase(theGen.getDerivedClass());
			}
		}

	}

	public JsonGeneralization()
	{

	}
	
	public IRPModelElement toModelElement(JsonModelElementBase parent, IRPProject project, ImportMode importMode)
	{
		IRPModelElement model = super.toModelElement(parent, project, importMode);
		
		if (model == null)
		{
			return null;
		}
		
		if (model instanceof IRPGeneralization == false)
		{
			return null;
		}
		
		IRPGeneralization theGen = (IRPGeneralization) model;
		
		if (importMode == ImportMode.reference)
		{
			return theGen;
		}

		if (isSet(extensionPoint))
		{
			theGen.setExtensionPoint(extensionPoint);
		}
		theGen.setIsVirtual(isVirtual ? 1 : 0);
		if (visibility != null)
		{
			theGen.setVisibility(visibility);
		}
		
		
		if (baseClass != null)
		{
			IRPModelElement base = baseClass.toModelElement(project, ImportMode.reference);
			if (base instanceof IRPClassifier)
			{
				theGen.setBaseClass((IRPClassifier) base);
			}
		}


		return theGen;
	}

}
