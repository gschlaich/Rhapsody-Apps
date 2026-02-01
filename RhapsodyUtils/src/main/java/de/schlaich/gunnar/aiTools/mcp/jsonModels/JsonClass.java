package de.schlaich.gunnar.aiTools.mcp.jsonModels;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPCollection;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;

public class JsonClass extends JsonClassifier
{

	@JsonProperty("isAbstract")
	protected boolean isAbstract = false;
	
	@JsonProperty("isActive")
	protected boolean isActive = false;
	
	@JsonProperty("isBehaviorOverriden")
	protected boolean isBehaviorOverriden = false;
	
	@JsonProperty("isComposite")
	protected boolean isComposite = false;
	
	@JsonProperty("isFinal")
	protected boolean isFinal = false;
	
	
	public JsonClass(IRPModelElement aModelElement, int level)
	{
		super(aModelElement, level);
		// TODO Auto-generated constructor stub
		IRPClass theClass = (IRPClass)aModelElement;
		
		if (theClass == null)
		{
			return;
		}
		
		isAbstract = theClass.getIsAbstract()==1;
		isActive = theClass.getIsActive()==1;
		isBehaviorOverriden = theClass.getIsBehaviorOverriden()==1;
		isComposite = theClass.getIsComposite()==1;
		isFinal = theClass.getIsFinal()==1;	
		
			
	}

	public JsonClass()
	{
		// TODO Auto-generated constructor stub
	}
	
	public IRPModelElement toModelElement(JsonModelElementBase parent, IRPProject project, ImportMode importMode)
	{
		
		IRPModelElement model  = super.toModelElement(parent, project, importMode);
		
		if (model == null)
		{
			return null;
		}
		
		if (model instanceof IRPClass == false)
		{
			return null;
		}
		
		IRPClass theClass = (IRPClass) model;
		
		if (importMode == ImportMode.reference)
		{
			return theClass;
		}

		theClass.setIsAbstract(isAbstract ? 1 : 0);
		theClass.setIsActive(isActive ? 1 : 0);
		theClass.setIsBehaviorOverriden(isBehaviorOverriden ? 1 : 0);
		//theClass.setIsComposite(isComposite ? 1 : 0);
		theClass.setIsFinal(isFinal ? 1 : 0);
		

		return theClass;
	}
	
	

}
