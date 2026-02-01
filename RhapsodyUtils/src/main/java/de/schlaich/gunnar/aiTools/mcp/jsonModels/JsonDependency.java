package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.telelogic.rhapsody.core.IRPDependency;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;

public class JsonDependency extends JsonModelElement
{
	/*
	IRPModelElement 	getDependent()
    Returns the source element in the dependency relation, meaning the element that depends on the other element.
	IRPModelElement 	getDependsOn()
    Returns the target element in the dependency relation, meaning the element on which the first element depends.
      
	 */
	@JsonProperty("dependent")
	protected JsonModelElementBase dependent = null;
	@JsonProperty("dependsOn")
	protected JsonModelElementBase dependsOn = null;
	
	
	public JsonDependency(IRPModelElement aModelElement, int level)
	{
		super(aModelElement, level);
		
		if (aModelElement == null)
		{
			return;
		}
		if (aModelElement instanceof IRPDependency)
		{
			IRPDependency theDep = (IRPDependency) aModelElement;

			if (theDep.getDependent() != null)
			{
				dependent = new JsonModelElementBase(theDep.getDependent(),level);
			}
			if (theDep.getDependsOn() != null)
			{
				dependsOn = new JsonModelElementBase(theDep.getDependsOn(),level);
			}
		}
		
	}

	public JsonDependency()
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

		if (model instanceof IRPDependency == false)
		{
			return null;
		}
		
		if (importMode == ImportMode.reference)
		{
			return model;
		}
		
		IRPDependency theDep = (IRPDependency) model;
		
		if (dependent != null)
		{
			IRPModelElement depModel = dependent.toModelElement(project, ImportMode.reference);
			if (depModel != null)
			{
				theDep.setDependent(depModel);
			}
		}
		
		if (dependsOn != null)
		{
			IRPModelElement depOnModel = dependsOn.toModelElement(project, ImportMode.reference);
			if (depOnModel != null)
			{
				theDep.setDependsOn(depOnModel);
			}
		}

		return model;

	}

}
