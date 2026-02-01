package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPType;

public class JsonType extends JsonClassifier
{

	/*
	 * java.lang.String getDeclaration() get property declaration IRPCollection
	 * getEnumerationLiterals() get property enumerationLiterals int
	 * getIsPredefined() get property isPredefined int getIsTypedef() get property
	 * isTypedef int getIsTypedefConstant() get property isTypedefConstant int
	 * getIsTypedefOrdered() get property isTypedefOrdered int
	 * getIsTypedefReference() get property isTypedefReference java.lang.String
	 * getKind() get property kind IRPClassifier getTypedefBaseType() get property
	 * typedefBaseType java.lang.String getTypedefMultiplicity() get property
	 * typedefMultiplicity int isArray() method isArray int isEnum() For types whose
	 * "kind" was set to Language, parses the declaration to see if the type is
	 * actually an enum. int isEqualTo() method isEqualTo int isImplicit() method
	 * isImplicit int isKindEnumeration() Checks whether the "kind" of the type is
	 * Enumeration. int isKindLanguage() Checks whether the "kind" of the type was
	 * set to Language. int isKindStruct() Checks whether the "kind" of the type is
	 * Structure. int isKindTypedef() Checks whether the "kind" of the type is
	 * Typedef. int isKindUnion() Checks whether the "kind" of the type is Union.
	 * int isPointer() method isPointer int isPointerToPointer() method
	 * isPointerToPointer int isReference() method isReference int
	 * isReferenceToPointer() method isReferenceToPointer int isStruct() For types
	 * whose "kind" was set to Language, parses the declaration to see if the type
	 * is actually a struct. int isTemplate() method isTemplate int isUnion() For
	 * types whose "kind" was set to Language, parses the declaration to see if the
	 * type is actually a union.
	 * 
	 */

	@JsonProperty("declaration")
	protected String declaration = null;
	@JsonProperty("kind")
	protected String kind = null;
	@JsonProperty("isPredefined")
	protected boolean isPredefined = false;
	@JsonProperty("isTypedef")
	protected boolean isTypedef = false;
	@JsonProperty("isTypedefConstant")
	protected boolean isTypedefConstant = false;
	@JsonProperty("isTypedefOrdered")
	protected boolean isTypedefOrdered = false;
	@JsonProperty("isTypedefReference")
	protected boolean isTypedefReference = false;
	@JsonProperty("typedefMultiplicity")
	protected String typedefMultiplicity = null;
	@JsonProperty("typedefBaseType")
	protected JsonModelElementBase typedefBaseType = null;
	//@JsonProperty("enumerationLiterals")
	//protected List<JsonEnumerationLiteral> enumerationLiterals = null;
	@JsonProperty("isArray")
	protected boolean isArray = false;
	@JsonProperty("isEnum")
	protected boolean isEnum = false;
	protected boolean isImplicit = false;
	@JsonProperty("isKindEnumeration")
	protected boolean isKindEnumeration = false;
	@JsonProperty("isKindLanguage")
	protected boolean isKindLanguage = false;
	@JsonProperty("isKindStruct")
	protected boolean isKindStruct = false;
	@JsonProperty("isKindTypedef")
	protected boolean isKindTypedef = false;
	@JsonProperty("isKindUnion")
	protected boolean isKindUnion = false;
	@JsonProperty("isPointer")
	protected boolean isPointer = false;
	@JsonProperty("isPointerToPointer")
	protected boolean isPointerToPointer = false;
	@JsonProperty("isReference")
	protected boolean isReference = false;
	@JsonProperty("isReferenceToPointer")
	protected boolean isReferenceToPointer = false;
	@JsonProperty("isStruct")
	protected boolean isStruct = false;
	@JsonProperty("isTemplate")
	protected boolean isTemplate = false;
	@JsonProperty("isUnion")
	protected boolean isUnion = false;

	public JsonType(IRPModelElement aModelElement, int level)
	{
		super(aModelElement, level);

		if (aModelElement == null)
		{
			return;
		}
		if (aModelElement instanceof IRPType)
		{
			IRPType theType = (IRPType) aModelElement;

			declaration = theType.getDeclaration();
			kind = theType.getKind();
			isPredefined = theType.getIsPredefined() == 1;
			isTypedef = theType.getIsTypedef() == 1;
			isTypedefConstant = theType.getIsTypedefConstant() == 1;
			isTypedefOrdered = theType.getIsTypedefOrdered() == 1;
			isTypedefReference = theType.getIsTypedefReference() == 1;
			typedefMultiplicity = theType.getTypedefMultiplicity();
			if (theType.getTypedefBaseType() != null)
			{
				typedefBaseType = new JsonModelElementBase(theType.getTypedefBaseType());
			}
			// nested?
			// enumerationLiterals = convertToJsonModelElementBaseList(theType.getEnumerationLiterals()); 
			isArray = theType.isArray() == 1;
			isEnum = theType.isEnum() == 1;
			isImplicit = theType.isImplicit() == 1;
			isKindEnumeration = theType.isKindEnumeration() == 1;
			isKindLanguage = theType.isKindLanguage() == 1;
			isKindStruct = theType.isKindStruct() == 1;
			isKindTypedef = theType.isKindTypedef() == 1;
			isKindUnion = theType.isKindUnion() == 1;
			isPointer = theType.isPointer() == 1;
			isPointerToPointer = theType.isPointerToPointer() == 1;
			isReference = theType.isReference() == 1;
			isReferenceToPointer = theType.isReferenceToPointer() == 1;
			isStruct = theType.isStruct() == 1;
			isTemplate = theType.isTemplate() == 1;
			isUnion = theType.isUnion() == 1;

		}
	}

	public JsonType()
	{

	}

	public IRPModelElement toModelElement(JsonModelElementBase parent, IRPProject project, ImportMode aImportMode)
	{
		IRPModelElement model = super.toModelElement(parent, project, aImportMode);

		if (model == null)
		{
			return null;
		}

		if (model instanceof IRPType == false)
		{
			return null;
		}

		if (aImportMode == ImportMode.reference)
		{
			return model;
		}

		IRPType theType = (IRPType) model;

		if (isSet(declaration))
		{
			theType.setDeclaration(declaration);
		}
		if (isSet(kind))
		{
			theType.setKind(kind);
		}
		// from prooerty
		// theType.setIsPredefined(isPredefined ? 1 : 0);
		// theType.setIsTypedef(isTypedef ? 1 : 0);

		if (theType.isKindTypedef() == 1)
		{
			theType.setIsTypedefConstant(isTypedefConstant ? 1 : 0);
			theType.setIsTypedefOrdered(isTypedefOrdered ? 1 : 0);
			theType.setIsTypedefReference(isTypedefReference ? 1 : 0);
			if (isSet(typedefMultiplicity))
			{
				theType.setTypedefMultiplicity(typedefMultiplicity);
			}
			if (typedefBaseType != null)
			{
				theType.setTypedefBaseType(
						(IRPClassifier) typedefBaseType.toModelElement(typedefBaseType, project, aImportMode));
			}

		}

		// nested?
//			if (enumerationLiterals != null)
//			{
//				theType.getEnumerationLiterals().clear();
//				for (JsonModelElementBase jsonElem : enumerationLiterals)
//				{
//					theType.getEnumerationLiterals().add(jsonElem.toModelElement(jsonElem, project));
//				}
//			}

		return model;
	}

}
