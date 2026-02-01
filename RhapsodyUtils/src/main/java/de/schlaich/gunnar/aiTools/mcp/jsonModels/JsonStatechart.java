package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPState;
import com.telelogic.rhapsody.core.IRPStatechart;

import de.schlaich.gunnar.aiTools.mcp.jsonModels.JsonModelElementBase.ImportMode;

public class JsonStatechart extends JsonModelElement
{

	/*
	 int 	getIsMainBehavior()
          Checks whether the statechart is the main behavior for the class.
 	int 	getIsOverridden()
          Checks whether the inheritance relationship between this statechart and the statechart of the base class was overridden.
 	IRPClassifier 	getItsClass()
          Returns the class that the statechart is associated with.
	 * 
	 */
	@JsonProperty("isMainBehavior")
	protected boolean isMainBehavior = false;
	@JsonProperty("isOverridden")
	protected boolean isOverridden = false;
	@JsonProperty("itsClass")
	protected JsonModelElementBase itsClass = null;
	
	
	public JsonStatechart(IRPModelElement aModelElement, int level)
	{
		super(aModelElement, level);
		if (aModelElement == null)
		{
			return;
		}
		
		if (aModelElement instanceof IRPStatechart)
		{
			com.telelogic.rhapsody.core.IRPStatechart theStatechart = (IRPStatechart) aModelElement;
			isMainBehavior = theStatechart.getIsMainBehavior() == 1;
			isOverridden = theStatechart.getIsOverridden() == 1;
			if (theStatechart.getItsClass() != null)
			{
				itsClass = new JsonModelElementBase(theStatechart.getItsClass());
			}
		}
	}
	
 
	

	public JsonStatechart()
	{
		
	}
	
	public IRPModelElement toModelElement(JsonModelElementBase parent, IRPProject project, ImportMode mode)
    {
        
		IRPModelElement elem = super.toModelElement(parent, project, mode);

		if (elem == null)
		{
			return null;
		}

		if (elem instanceof IRPStatechart == false)
		{
			return null;
		}
		
		if (mode == ImportMode.reference)
		{
			return elem;
		}
		
		IRPStatechart theStatechart = (IRPStatechart) elem;
        
        if(isMainBehavior)
        {
        	theStatechart.setAsMainBehavior(); 
        }
        
		if (isOverridden)
		{
			 theStatechart.overrideInheritance();
		}
        return theStatechart;
    }
	
//	@Override
//	protected void addNestedElements(IRPProject aProject, ImportMode aImportMode)
//	{
//		
//		IRPModelElement modelElement = aProject.findElementByGUID(this.getGuid());
//		
//		if (modelElement == null)
//		{
//			return;
//		}
//		
//		if (modelElement instanceof IRPStatechart == false)
//		{
//			return;
//		}
//		
//		IRPStatechart theStatechart = (IRPStatechart) modelElement;
//		
//		
//		for (JsonModelElementBase jsonNestedElement : this.nestedElements)
//		{
//			
//			if(jsonNestedElement.getMetaclass() == MetaClass.State)
//			{
//				if(jsonNestedElement.getName().equals("ROOT"))
//				{
//					continue;
//				}
//			}
//			
//			if (jsonNestedElement.getMetaclass() == MetaClass.DefaultTransition)
//			{
//				
//				if(jsonNestedElement instanceof JsonDefaultTransition)
//				{
//					theStatechart.set
//				}
//				// transitions are added to the statechart via the source and target states
//				continue;
//			}
//			
//			jsonNestedElement.toModelElement(this, aProject, aImportMode);
//        
//		}
//	}

	

}
