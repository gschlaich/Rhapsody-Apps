package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.telelogic.rhapsody.core.IRPAssociationClass;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;

public class JsonAssociationClass extends JsonClass
{

	/*
	 * 
	 * IRPRelation getEnd1() Gets the relation represented by the first end of the
	 * association class. IRPRelation getEnd2() Gets the relation represented by the
	 * second end of the association class. int getIsClass() Checks whether the
	 * element is an association class or an association element.
	 * 
	 * 
	 */

	@JsonProperty("isClass")
	protected boolean isClass = false;
	@JsonProperty("end1")
	protected JsonModelElementBase end1 = null;
	@JsonProperty("end2")
	protected JsonModelElementBase end2 = null;

	public JsonAssociationClass(IRPModelElement aModelElement, int level)
	{
		super(aModelElement, level);
		if (aModelElement == null)
		{
			return;
		}
		if (aModelElement instanceof IRPAssociationClass)
		{
			IRPAssociationClass theAssociationClass = (IRPAssociationClass) aModelElement;
			isClass = theAssociationClass.getIsClass() == 1;
			if (theAssociationClass.getEnd1() != null)
			{
				end1 = new JsonModelElementBase(theAssociationClass.getEnd1(),level);
			}
			if (theAssociationClass.getEnd2() != null)
			{
				end2 = new JsonModelElementBase(theAssociationClass.getEnd2(),level);
			}
		}

	}

	public JsonAssociationClass()
	{
		// TODO Auto-generated constructor stub
	}

	public IRPModelElement toModelElement(JsonModelElementBase parent, IRPProject project, ImportMode importMode)
	{
		IRPModelElement model = super.toModelElement(parent, project, importMode);
		
		if (model == null)
		{
			return null;
		}
		
		if (model instanceof IRPAssociationClass == false)
		{
			return null;
		}
		
		IRPAssociationClass theAssociationClass = (IRPAssociationClass) model;
		
		if (importMode == ImportMode.reference)
		{
			return theAssociationClass;
		}
		
		if (isClass)
		{
			theAssociationClass.setIsClass(1);
		}
		else
		{
			theAssociationClass.setIsClass(0);

		}
		
		return theAssociationClass;
	}

}
