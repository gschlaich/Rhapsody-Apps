package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.telelogic.rhapsody.core.IRPAssociationClass;
import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPInstance;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPRelation;
import com.telelogic.rhapsody.core.IRPStereotype;

public class JsonRelation extends JsonUnit
{

	public enum LinkType {
		Association, Aggregation, Composition, none
	}

	/*
	 * 
	 * IRPAssociationClass getAssociationClass() method getAssociationClass
	 * IRPRelation getInverse() get property inverse int getIsNavigable() get
	 * property isNavigable int getIsSymmetric() get property isSymmetric
	 * java.lang.String getMultiplicity() get property multiplicity
	 * 
	 * IRPClass getObjectAsObjectType() get property ObjectAsObjectType
	 * IRPClassifier getOfClass() get property ofClass IRPClassifier getOtherClass()
	 * Gets the class that this class is related to via this relation.
	 * java.lang.String getQualifier() get property qualifier IRPCollection
	 * getQualifiers() method getQualifiers IRPClassifier getQualifierType() For
	 * associations that use qualifiers, returns the type of the qualifier.
	 * java.lang.String getRelationLabel() get property relationLabel
	 * java.lang.String getRelationLinkName() get property relationLinkName
	 * java.lang.String getRelationRoleName() get property relationRoleName
	 * java.lang.String getRelationType() get property relationType java.lang.String
	 * getVisibility() get property visibility int isTypelessObject() method
	 * isTypelessObject
	 * 
	 */
	@JsonProperty("inverse")
	protected JsonModelElementBase inverse = null;
	@JsonProperty("associationClass")
	protected JsonModelElementBase associationClass = null;
	@JsonProperty("isSymmetric")
	protected boolean isSymmetric = false;
	@JsonProperty("isNavigable")
	protected boolean isNavigable = false;
	@JsonProperty("multiplicity")
	protected String multiplicity = "";
	@JsonProperty("objectAsObjectType")
	protected JsonModelElementBase objectAsObjectType = null;
	@JsonProperty("ofClass")
	protected JsonModelElementBase ofClass = null;
	@JsonProperty("otherClass")
	protected JsonModelElementBase otherClass = null;
	@JsonProperty("qualifierType")
	protected JsonModelElementBase qualifierType = null;
	@JsonProperty("relationLabel")
	protected String relationLabel = "";
	@JsonProperty("relationLinkName")
	protected String relationLinkName = "";
	@JsonProperty("relationType")
	protected String relationType = "";
	@JsonProperty("relationRoleName")
	protected String relationRoleName = "";
	@JsonProperty("visibility")
	protected String visibility = "";
	@JsonProperty("qualifier")
	protected String qualifier = "";
	@JsonProperty("isTypelessObject")
	protected boolean isTypelessObject = false;
	@JsonProperty("qualifiers")
	List<JsonModelElementBase> qualifiers = null;

	public JsonRelation(IRPModelElement aModelElement, int level)
	{
		super(aModelElement, level);

		if (aModelElement == null)
		{
			return;
		}

		if (aModelElement instanceof IRPRelation == false)
		{
			return;
		}

		IRPRelation theRelation = (IRPRelation) aModelElement;

		if (theRelation.getInverse() != null)
		{
			inverse = new JsonModelElementBase(theRelation.getInverse());
		}

//		if (theRelation.getAssociationClass() != null)
//		{
//			associationClass = new JsonModelElementBase(theRelation.getAssociationClass());
//		}

		isSymmetric = theRelation.getIsSymmetric() == 1;

		isNavigable = theRelation.getIsNavigable() == 1;

		multiplicity = theRelation.getMultiplicity();

		if (theRelation.getObjectAsObjectType() != null)
		{
			objectAsObjectType = new JsonModelElementBase(theRelation.getObjectAsObjectType());
		}

		if (theRelation.getOfClass() != null)
		{
			ofClass = new JsonModelElementBase(theRelation.getOfClass());
		}

		if (theRelation.getOtherClass() != null)
		{
			otherClass = new JsonModelElementBase(theRelation.getOtherClass());
		}

		if (theRelation.getQualifierType() != null)
		{
			qualifierType = new JsonModelElementBase(theRelation.getQualifierType());
		}

		relationLabel = theRelation.getRelationLabel();
		relationLinkName = theRelation.getRelationLinkName();
		relationType = theRelation.getRelationType();
		relationRoleName = theRelation.getRelationRoleName();
		visibility = theRelation.getVisibility();
		qualifier = theRelation.getQualifier();
		isTypelessObject = theRelation.isTypelessObject() == 1;
		qualifiers = convertToJsonModelElementBaseList(theRelation.getQualifiers());

	}

	public JsonRelation()
	{
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public IRPModelElement createModelElement(IRPModelElement aParentElement)
	{
		
		IRPProject project = aParentElement.getProject();
		
		if(aParentElement instanceof IRPClassifier)
		{
			IRPClassifier parentClass = (IRPClassifier) aParentElement;

			IRPModelElement otherElement = otherClass.getReference(project);

			if (otherElement == null)
			{
				return null;
			}

			if ((otherElement instanceof IRPClassifier)==false)
			{
				return null;
			}

			IRPClassifier otherClassifier = (IRPClassifier) otherElement;
			
			if(ofClass == null)
			{
				trace("Relation " + name + " has no ofClass specified. Cannot create relation.");
				return null;
			}
			
			if(ofClass.getGuid().equals(parentClass.getGUID()) == false)
            {
                trace("ofClass of relation " + name + " does not match the parent class. Cannot create relation.");
                return null;
            }

			//linkType - used to determine the type of association to create. The strings that can be used for this parameter are Association, Aggregation and Composition (parameter is case-sensitive).
			//IRPClassifier.addUnidirectionalRelationTo(IRPClassifier otherClassifier, java.lang.String roleName, java.lang.String linkType, java.lang.String multiplicity, java.lang.String linkName)

			return parentClass.addUnidirectionalRelationTo(otherClassifier, relationRoleName, relationType,
					multiplicity, relationLinkName);
		}
		
		else
		{	
			
			if(ofClass == null)
			{
				trace("Relation " + name + " has no ofClass specified. Cannot create relation.");
				return null;
			}
			
			IRPModelElement ofClassElement = ofClass.getReference(project);
			if(ofClassElement == null)
			{
				trace("ofClass of relation " + name + " could not be resolved. Cannot create relation.");
				return null;
			}
			
			if(ofClassElement instanceof IRPClassifier == false)
			{
				trace("ofClass of relation " + name + " is not an IRPClassifier. Cannot create relation.");
				return null;
			}
			
			return createModelElement(ofClassElement);
   
		}
		
		

	}

	
	
	@Override
	public void setAttributes(IRPModelElement modelElement, IRPProject project, ImportMode importMode)
    {
        super.setAttributes(modelElement, project, importMode);

        if (modelElement instanceof IRPRelation == false)
        {
            return;
        }

        IRPRelation theRelation = (IRPRelation) modelElement;

        if (isSet(relationLabel))
        {
            theRelation.setRelationLabel(relationLabel);
        }

        if (isSet(relationLinkName))
        {
            theRelation.setRelationLinkName(relationLinkName);
        }

        if (isSet(relationRoleName))
        {
            theRelation.setRelationRoleName(relationRoleName);
        }
        
        if (isSet(visibility))
		{
			theRelation.setPropertyValue("CPP_CG.Relation.DataMemberVisibility", visibility);
		}

		if (isSet(qualifier))
		{
			theRelation.setQualifier(qualifier);
		}
		
		if(qualifierType!=null)
		{
			IRPModelElement qualifierTypeME = qualifierType.getReference(project);
            if (qualifierTypeME != null && qualifierTypeME instanceof IRPClassifier)
            {
                theRelation.setQualifierType((IRPClassifier) qualifierTypeME);
            }
		}
		
		if (qualifiers != null)
		{
			for (JsonModelElementBase jsonQualifier : qualifiers)
			{
				IRPModelElement qualifierME = jsonQualifier.getReference(project);
				if (qualifierME != null)
				{
					theRelation.addQualifier(qualifierME);
				}
			}
		}
    }
        
        

}
