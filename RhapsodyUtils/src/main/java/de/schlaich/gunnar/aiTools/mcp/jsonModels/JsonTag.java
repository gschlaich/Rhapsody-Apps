package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPTag;

public class JsonTag extends JsonVariable
{

//		IRPTag 	getBase()
//		      Returns the base tag on which the local copy of the tag is based.
//		IRPProfile 	getFromProfile()
//		      For tags whose source is a profile that was added to the project (as opposed to tags defined locally in the project), this method returns the profile in which the tag was defined.
//		java.lang.String 	getMultiplicity()
//		      Returns the multiplicity that was specified for the tag.
//		java.lang.String 	getTagMetaClass()
//		      Returns the name of the metaclass to which the tag is applicable.
//		java.lang.String 	getValue()

	@JsonProperty("base")
	protected JsonModelElementBase base = null;
	@JsonProperty("fromProfile")
	protected JsonModelElementBase fromProfile = null;
	@JsonProperty("multiplicity")
	protected String multiplicity = null;
	@JsonProperty("tagMetaClass")
	protected String tagMetaClass = null;
	@JsonProperty("value")
	protected String value = null;

	public JsonTag(IRPModelElement aModelElement, int level)
	{
		super(aModelElement, level);
		if (aModelElement == null)
		{
			return;
		}
		if (aModelElement instanceof IRPTag)
		{
			IRPTag tag = (IRPTag) aModelElement;

			this.base = new JsonModelElementBase(tag.getBase());
			this.fromProfile = new JsonModelElementBase(tag.getFromProfile());
			this.multiplicity = tag.getMultiplicity();
			this.tagMetaClass = tag.getTagMetaClass();
			this.value = tag.getValue();
		}
	}

	public JsonTag()
	{

	}

	public IRPModelElement toModelElement(JsonModelElementBase parent, IRPProject project, ImportMode mode)
	{
		IRPModelElement elem = super.toModelElement(parent, project, mode);
		if (elem == null)
		{
			return null;
		}

		if (elem instanceof IRPTag == false)
		{
			return null;
		}

		if (mode == ImportMode.reference)
		{
			return elem;
		}

		IRPTag tag = (IRPTag) elem;

		// tag.setBase( ... ); // Base is usually read-only

		// tag.setFromProfile( ... ); // FromProfile is usually read-only

		tag.setMultiplicity(this.multiplicity);
		tag.setTagMetaClass(tagMetaClass);

		tag.setValue(this.value);

		return elem;

	}

}
