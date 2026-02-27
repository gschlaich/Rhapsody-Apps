package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPModule;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.RPPackage;

public class JsonModule extends JsonInstance
{

	public JsonModule(IRPModelElement aModelElement, int level)
	{
		super(aModelElement, level);
		
	}

	public JsonModule()
	{
		 
	}
	
	public IRPModelElement toModelElement(JsonModelElementBase parent, IRPProject project, ImportMode importMode)
	{
		IRPModelElement model = super.toModelElement(parent, project, importMode);
		
		if (model == null)
		{
			return null;
		}
		
		if (model instanceof IRPModule == false)
		{
			return null;
		}
		
		IRPModule module = (IRPModule)model;

		return module;
	}
	
	@Override
	public IRPModelElement createModelElement(IRPModelElement aParentElement)
	{
		if (aParentElement instanceof IRPPackage == false)
		{
			return null;
		}

		IRPPackage p = (IRPPackage) aParentElement;

		IRPModule module = p.addModule(this.name);

		return module;
	}
	
	@Override
	public void setAttributes(IRPModelElement aModelElement, IRPProject aProject, ImportMode aImportMode)
	{
		super.setAttributes(aModelElement, aProject, aImportMode);

		if (aModelElement instanceof IRPModule == false)
		{
			return;
		}

		IRPModule module = (IRPModule) aModelElement;

	}

}
