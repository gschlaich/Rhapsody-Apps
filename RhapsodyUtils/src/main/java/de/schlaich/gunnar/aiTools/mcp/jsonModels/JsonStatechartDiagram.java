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
	
	public IRPModelElement toModelElement(JsonModelElementBase parent, IRPProject project, ImportMode mode)
	{
		if(parent == null)
		{
			return super.toModelElement(parent, project, mode);
		}
		
		IRPModelElement parentElement = parent.toModelElement(project, ImportMode.reference);
		
		if(parentElement instanceof IRPStatechart == false)
		{
			return super.toModelElement(parent, project, mode);
		}
		
		IRPStatechart stateChart = (IRPStatechart)parentElement;
		
		IRPStatechartDiagram ret = stateChart.getStatechartDiagram();
		
		return ret;
		
	}

}
