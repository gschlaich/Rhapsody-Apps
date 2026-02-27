package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPPackage;
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
	
	
	@Override
	public void setAttributes(IRPModelElement aModelElement, IRPProject aProject, ImportMode aImportMode)
	{
		super.setAttributes(aModelElement, aProject, aImportMode);

		if (aModelElement instanceof IRPClass == false)
		{
			return;
		}

		IRPClass theClass = (IRPClass) aModelElement;

		theClass.setIsAbstract(isAbstract ? 1 : 0);
		theClass.setIsActive(isActive ? 1 : 0);
		theClass.setIsBehaviorOverriden(isBehaviorOverriden ? 1 : 0);
		// theClass.setIsComposite(isComposite ? 1 : 0);
		theClass.setIsFinal(isFinal ? 1 : 0);
	}
	
	@Override
	public IRPModelElement createModelElement(IRPModelElement parentElement)
    {
      
        if (parentElement instanceof IRPClass)
        {
        	IRPClass parentClass = (IRPClass) parentElement;
        	return parentClass.addClass(name);
        }
        else if(parentElement instanceof IRPPackage)
        {
			IRPPackage parentPackage = (IRPPackage) parentElement;
			return parentPackage.addClass(name);
		}
		
        return null;
    }
        	
}
