package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPStatechart;
import com.telelogic.rhapsody.core.IRPStatechartDiagram;

public class JsonStatechartDiagram extends JsonDiagram
{

	//IRPStatechart 	getStatechart()
    //Returns the IRPStatechart object underlying the statechart.
	
	@JsonProperty("statechart")
	protected JsonModelElementBase statechart = null;
	
	public JsonStatechartDiagram(IRPModelElement aModelElement, int level)
	{
		super(aModelElement, level);
		if (aModelElement == null)
		{
			return;
		}
		if (aModelElement instanceof IRPStatechartDiagram)
		{
			IRPStatechartDiagram diagram = (IRPStatechartDiagram) aModelElement;

			this.statechart = new JsonModelElementBase(diagram.getStatechart());
			
		}
		
		
	}

	public JsonStatechartDiagram()
	{
		
	}
	
	@Override
	public IRPModelElement createModelElement(IRPModelElement aParentElement)
	{
		IRPModelElement modelElement = null;

		if (aParentElement instanceof IRPStatechart)
		{
			IRPStatechart parentStatechart = (IRPStatechart) aParentElement;
			modelElement = parentStatechart.getStatechartDiagram();
		}
		else
		{
			trace("Parent element is not an IRPStatechart. Its " + aParentElement.getMetaClass());			
		}

		return modelElement;

	}
	

}
