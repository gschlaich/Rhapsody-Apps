package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPModule;
import com.telelogic.rhapsody.core.IRPProject;

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

}
