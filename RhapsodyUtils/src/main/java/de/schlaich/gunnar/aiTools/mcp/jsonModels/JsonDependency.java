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
	
	@Override
	public IRPModelElement createModelElement(IRPModelElement parent)
	{
	    IRPProject project = parent.getProject();
		
		if(dependent == null)
		{
			trace("Dependent element is null for dependency " + name);
			return null;
		}
	    
	    IRPModelElement dependentModel = dependent.getReference(project);
		
		if (dependentModel == null)
		{
			trace("Could not find model for dependent element " + name);
			return null;
		}
		
		if (dependsOn == null)
		{
			trace("DependsOn element is null for dependency " + name);
			return null;
		}
		
		IRPModelElement dependsOnModel = dependsOn.getReference(project);
		
		if (dependsOnModel == null)
		{
			trace("Could not find model for dependsOn element " + name);
			return null;
		}

		return parent.addDependencyBetween(dependentModel, dependsOnModel);
	}
	
	@Override
	public void setAttributes(IRPModelElement modelElement, IRPProject project, ImportMode importMode)
    {
        super.setAttributes(modelElement, project, importMode);

        if (modelElement instanceof IRPDependency == false)
        {
            return;
        }
        
        IRPDependency theDep = (IRPDependency) modelElement;
        
        if (dependent != null)
        {
            IRPModelElement depModel = dependent.getReference(project);
            if (depModel != null)
            {
                theDep.setDependent(depModel);
            }
        }
        
        if (dependsOn != null)
        {
            IRPModelElement depOnModel = dependsOn.getReference(project);
            if (depOnModel != null)
            {
                theDep.setDependsOn(depOnModel);
            }
        }
    }

}
