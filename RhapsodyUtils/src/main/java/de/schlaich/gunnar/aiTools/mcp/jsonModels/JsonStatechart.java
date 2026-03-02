package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPCollection;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPState;
import com.telelogic.rhapsody.core.IRPStatechart;

import de.schlaich.gunnar.aiTools.mcp.jsonModels.JsonModelElementBase.ImportMode;

public class JsonStatechart extends JsonClass
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
	
	@JsonProperty("statechartDiagram")
	protected JsonModelElementBase statechartDiagram = null;
	
	
	
	public JsonStatechart(IRPModelElement aModelElement, int level)
	{
		super(aModelElement, level);
		if (aModelElement == null)
		{
			return;
		}
		
		if (aModelElement instanceof IRPStatechart)
		{
			IRPStatechart theStatechart = (IRPStatechart) aModelElement;
			isMainBehavior = theStatechart.getIsMainBehavior() == 1;
			isOverridden = theStatechart.getIsOverridden() == 1;
			statechartDiagram = new JsonModelElementBase(theStatechart.getStatechartDiagram(), 0);		
		}
	}
	
 
	

	public JsonStatechart()
	{
		
	}
	
	
	@Override
	protected void getNestedElements(IRPModelElement aModelElement)
	{
		
		JsonModelFactory factory = JsonModelFactory.Instance();
		
		if (factory == null)
		{
			trace("Could not get JsonModelFactory instance");
			return;
		}
		
		IRPApplication app = factory.getRhapsodyApplication();
		
		
		
		
		List<IRPModelElement> nestedElements = aModelElement.getNestedElements().toList();
		IRPCollection nestedElementsCollection = app.createNewCollection();
		for (IRPModelElement nestedElement : nestedElements)
		{
			if (nestedElement instanceof IRPState)
			{
				IRPState state = (IRPState) nestedElement;
				if(state.isRoot()==1)
				{
					nestedElementsCollection.addItem(nestedElement);
				}
			}
			else 
			{
				nestedElementsCollection.addItem(nestedElement);
			}
		}

		this.nestedElements = convertToJsonModelElementList(nestedElementsCollection);
		nestedElementsCollection.empty();
		
	}
	
	
	
	
	@Override
	public IRPModelElement createModelElement(IRPModelElement aParentElement)
	{
		if (aParentElement == null)
		{
			return null;
		}
		
		if (aParentElement instanceof IRPState)
		{
			IRPState parentState = (IRPState) aParentElement;
			return parentState.createNestedStatechart();
		}
		else if (aParentElement instanceof IRPClassifier)
		{
			IRPClassifier parentClassifier = (IRPClassifier) aParentElement;
			return parentClassifier.addStatechart();
		}
		
		trace("Could not create statechart for parent element " + aParentElement.getName());
		return null;
	}
	
	@Override
	public void setAttributes(IRPModelElement aModelElement, IRPProject aProject, ImportMode aImportMode)
	{
		super.setAttributes(aModelElement, aProject, aImportMode);
		
		if (aModelElement instanceof IRPStatechart == false)
		{
			return;
		}
		IRPStatechart theStatechart = (IRPStatechart) aModelElement;

		if(isMainBehavior)
        {
        	theStatechart.setAsMainBehavior(); 
        }
        
		if (isOverridden)
		{
			 theStatechart.overrideInheritance();
		}
	}
	

}
