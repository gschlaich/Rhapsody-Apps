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
	
	
	
	@Override
	public IRPModelElement createModelElement(IRPModelElement parent)
	{
		
		if (parent instanceof IRPClassifier)
		{
			
			IRPClassifier parentClassifier = (IRPClassifier) parent;
			IRPModelElement model = this.baseClass.getReference(parent.getProject());
			if(model instanceof IRPClassifier)
            {
				IRPClassifier baseClass = (IRPClassifier) model;
				parentClassifier.addGeneralization(baseClass);
				
				IRPGeneralization gen = parentClassifier.findGeneralization(this.baseClass.name);
				return gen;				
            }
			
			
		}

		return null;
	}
	
	@Override
	public void setAttributes(IRPModelElement aModelElement, IRPProject aProject, ImportMode aImportMode)
    {
        super.setAttributes(aModelElement, aProject, aImportMode);

        if (aModelElement instanceof IRPGeneralization == false)
        {
            return;
        }

        IRPGeneralization theGen = (IRPGeneralization) aModelElement;

        theGen.setExtensionPoint(extensionPoint);
        theGen.setIsVirtual(isVirtual ? 1 : 0);
        theGen.setVisibility(visibility);

        if (derivedClass != null)
        {
            IRPModelElement derived = derivedClass.getReference(aProject);
            if (derived instanceof IRPClassifier)
            {
                theGen.setDerivedClass((IRPClassifier) derived);
            }
        }
        if (baseClass != null)
		{
			IRPModelElement base = baseClass.getReference(aProject);
			if (base instanceof IRPClassifier)
			{
				theGen.setBaseClass((IRPClassifier) base);
			}
		}
    }

}
