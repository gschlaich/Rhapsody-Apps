package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
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

	public IRPModelElement toModelElement(JsonModelElementBase parent, IRPProject project, ImportMode importMode)
	{

		IRPRelation theRelation = null;

		if (importMode == ImportMode.reference)
		{
			IRPModelElement model = super.toModelElement(parent, project, importMode);

			return model;
		}

		if (parent == null || project == null)
		{
			return null;
		}

		IRPModelElement parentElement = project.findElementByGUID(parent.getGuid());

		if (parentElement == null)
		{
			return null;
		}

		if (!(parentElement instanceof IRPClassifier))
		{
			return null;
		}

		IRPClassifier parentClassifier = (IRPClassifier) parentElement;

		List<IRPRelation> existingRelations = parentClassifier.getRelations().toList();

		for (IRPRelation rel : existingRelations)
		{
			if (rel.getGUID().equals(this.getGuid()))
			{
				theRelation = rel;
			}
		}

		if (theRelation == null)
		{

			IRPModelElement otherElement = otherClass.toModelElement(project, ImportMode.update);

			if (otherElement == null)
			{
				return null;
			}

			if (!(otherElement instanceof IRPClassifier))
			{
				return null;
			}

			IRPClassifier otherClassifier = (IRPClassifier) otherElement;

			LinkType linkType = LinkType.Association;

			if (this instanceof JsonInstance)
			{
				linkType = LinkType.Composition;
			}

			theRelation = parentClassifier.addUnidirectionalRelationTo(otherClassifier, relationRoleName,
					linkType.toString(), multiplicity, relationLinkName);

		}

		if (qualifierType != null)
		{
			IRPModelElement qualifierTypeME = qualifierType.toModelElement(project, ImportMode.update);
			if (qualifierTypeME != null && qualifierTypeME instanceof IRPClassifier)
			{
				theRelation.setQualifierType((IRPClassifier) qualifierTypeME);
			}
		}

		if (theRelation instanceof IRPInstance == false)
		{

			if (isSet(relationType))
			{
				theRelation.setRelationType(relationType);
			}
		}

		// theRelation.setVisibility(visibility);
		// is property
		if (isSet(visibility))
		{
			theRelation.setPropertyValue("CPP_CG.Relation.DataMemberVisibility", visibility);
		}

		if (isSet(qualifier))
		{
			theRelation.setQualifier(qualifier);
		}

		// theRelation.setTypelessObject(isTypelessObject ? 1 : 0);

		if (qualifiers != null)
		{
			for (JsonModelElementBase jsonQualifier : qualifiers)
			{
				IRPModelElement qualifierME = jsonQualifier.toModelElement(project, ImportMode.update);
				if (qualifierME != null)
				{
					theRelation.addQualifier(qualifierME);
				}
			}
		}

		setProperties(theRelation, importMode);

		return theRelation;

	}

}
