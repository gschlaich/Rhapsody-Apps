package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.telelogic.rhapsody.core.IRPAssociationClass;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPRelation;

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
				end1 = createJsonModelElement(theAssociationClass.getEnd1(), 0);
			}
			if (theAssociationClass.getEnd2() != null)
			{
				end2 = createJsonModelElement(theAssociationClass.getEnd2(), 0);
			}
		}

	}

	public JsonAssociationClass()
	{
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public IRPModelElement createModelElement(IRPModelElement parent)
	{
		IRPProject project = parent.getProject();
		
		IRPRelation end1Relation = null;
		IRPRelation end2Relation = null;
		
		
		if(end1 != null)
		{
			IRPModelElement end1Model = end1.createModelElement(parent);
			if (end1Model == null)
			{
				trace("End1 of association class " + name + " could not be created.");
				return null;
			}
			end1.setAttributes(end1Model, project, ImportMode.create);
			
			if (end1Model instanceof IRPRelation == false)
			{
				trace("End1 of association class " + name
						+ " is not an IRPRelation. Creating association class without end1.");
				return null;
			}
			end1Relation = (IRPRelation) end1Model;
			
		}
		
		if (end2 != null)
		{
			IRPModelElement end2Model = end2.createModelElement(parent);
			if (end2Model == null)
			{
				trace("End2 of association class " + name + " could not be created.");
				return null;
			}
			end2.setAttributes(end2Model, project, ImportMode.create);
			
			if (end2Model instanceof IRPRelation == false)
			{
				trace("End2 of association class " + name
						+ " is not an IRPRelation. Creating association class without end2.");
				return null;
			}
			end2Relation = (IRPRelation) end2Model;
		}

		IRPAssociationClass ret = parent.addAssociation(end1Relation, end2Relation, name);
		return ret;
	}

	
	@Override
	public void setAttributes(IRPModelElement aModelElement, IRPProject aProject, ImportMode aImportMode)
    {
        super.setAttributes(aModelElement, aProject, aImportMode);

        if (aModelElement instanceof IRPAssociationClass == false)
        {
            return;
        }

        IRPAssociationClass theAssociationClass = (IRPAssociationClass) aModelElement;

        if (isClass)
        {
            theAssociationClass.setIsClass(1);
        }
        else
        {
            theAssociationClass.setIsClass(0);

        }
    }

}
