package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.telelogic.rhapsody.core.IRPAttribute;
import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPProject;

public class JsonAttribute extends JsonVariable
{

	/*
	 *   int 	getIsConstant()
          Checks whether the attribute was defined as constant.
 int 	getIsOrdered()
          For attributes with multiplicity greater than one, checks whether the order of the items was specified as significant.
 int 	getIsReference()
          Checks whether the attribute was defined as a pointer.
 int 	getIsStatic()
          Checks whether the attribute was defined as static.
 java.lang.String 	getMultiplicity()
          Gets the multiplicity specified for the attribute.
 java.lang.String 	getVisibility()
          Gets the visibility specified for the attribute.
	 */
	@JsonProperty("isConstant")
	protected boolean isConstant = false;
	@JsonProperty("isOrdered")
	protected boolean isOrdered = false;
	@JsonProperty("isReference")
	protected boolean isReference = false;
	@JsonProperty("isStatic")
	protected boolean isStatic = false;
	@JsonProperty("multiplicity")
	protected String multiplicity = "";
	@JsonProperty("visibility")
	protected String visibility = "";
	
	public JsonAttribute(IRPModelElement aModelElement, int level)
	{
		super(aModelElement, level);
		if (aModelElement == null)
		{
			return;
		}
		if (aModelElement instanceof IRPAttribute)
		{
			IRPAttribute theAttribute = (IRPAttribute) aModelElement;
			isConstant = theAttribute.getIsConstant() == 1;
			isOrdered = theAttribute.getIsOrdered() == 1;
			isReference = theAttribute.getIsReference() == 1;
			isStatic = theAttribute.getIsStatic() == 1;
			multiplicity = theAttribute.getMultiplicity();
			visibility = theAttribute.getVisibility();
		}
	}

	public JsonAttribute()
	{
		
	}
	
	
	@Override
	protected IRPModelElement createModelElement(IRPModelElement aParentElement)
	{
		//package
		//classifier
		
		IRPModelElement modelElement = null;
		if (aParentElement instanceof IRPClassifier)
		{
			IRPClassifier parentClass = (IRPClassifier) aParentElement;
			modelElement = parentClass.addAttribute(name);
		}
		else if (aParentElement instanceof IRPPackage)
		{
			IRPPackage parentPackage = (IRPPackage) aParentElement;
			modelElement = parentPackage.addGlobalVariable(name);
		}
		else
		{
			trace("Parent element is not an IRPClassifier or IRPPackage. Its " + aParentElement.getMetaClass());
			modelElement = super.createModelElement(aParentElement);			
		}

		return modelElement;
	}
	
	@Override
	public void setAttributes(IRPModelElement aModelElement, IRPProject aProject, ImportMode aImportMode)
	{

		if (aModelElement instanceof IRPAttribute == false)
		{
			return;
		}

		IRPAttribute theAttribute = (IRPAttribute) aModelElement;

		theAttribute.setIsConstant(isConstant ? 1 : 0);
		theAttribute.setIsOrdered(isOrdered ? 1 : 0);
		theAttribute.setIsReference(isReference ? 1 : 0);
		theAttribute.setIsStatic(isStatic ? 1 : 0);
		theAttribute.setMultiplicity(multiplicity);
		theAttribute.setVisibility(visibility);

		super.setAttributes(aModelElement, aProject, aImportMode);

	}

}
