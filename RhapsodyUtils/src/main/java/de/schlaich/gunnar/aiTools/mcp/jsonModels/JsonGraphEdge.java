package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.telelogic.rhapsody.core.IRPDiagram;
import com.telelogic.rhapsody.core.IRPGraphEdge;
import com.telelogic.rhapsody.core.IRPGraphElement;
import com.telelogic.rhapsody.core.IRPGraphNode;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;



public class JsonGraphEdge extends JsonGraphElement
{

	// IRPGraphEdge 	getContainingArrow()
	//    method getContainingArrow
	//IRPGraphElement 	getSource()
	//    get property source
	//IRPGraphElement 	getTarget()
	//    get property target
	
	@JsonProperty("source")
	protected String sourceGUID = null;
	@JsonProperty("target")
	protected String targetGUID = null;
	
	
	
	public JsonGraphEdge(IRPGraphEdge aGraphEdge)
	{
		super(aGraphEdge);
		
		sourceGUID = GUID(aGraphEdge.getSource());
		targetGUID = GUID(aGraphEdge.getTarget());
	
	}
	
	public String getSourceGUID()
	{
		return sourceGUID;
	}
	
	public String getTargetGUID()
	{
		return targetGUID;
	}
	
	public void setSourceGUID(String sourceGUID)
	{
		this.sourceGUID = sourceGUID;
	}
	
	public void setTargetGUID(String targetGUID)
	{
		this.targetGUID = targetGUID;
	}

	public JsonGraphEdge()
	{
		// TODO Auto-generated constructor stub
	}
	
	public IRPGraphEdge createEdgeElement(IRPDiagram aDiagram, IRPProject aProject)
	{
		if (aDiagram == null || aProject == null)
		{
			return null;
		}
			
		IRPModelElement modelElement = modelObject.getReference(aProject);
		
		if (modelElement == null)
		{
			return null;
		}
		
		int xSourcePosition = 0;
		int ySourcePosition = 0;
		int xTargetPosition = 0;
		int yTargetPosition = 0;
		
		String sourcePosition = myGraphicalPropertyMap.get("SourcePosition");
		if (sourcePosition != null)
		{
			xSourcePosition = Integer.parseInt(sourcePosition.split(",")[0]);
			ySourcePosition = Integer.parseInt(sourcePosition.split(",")[1]);
		}
		
		String targetPosition = myGraphicalPropertyMap.get("TargetPosition");
		if (targetPosition != null)
		{
			xTargetPosition = Integer.parseInt(targetPosition.split(",")[0]);
			yTargetPosition = Integer.parseInt(targetPosition.split(",")[1]);
		}
		
		IRPGraphNode sourceNode = JsonDiagram.GetGraphNodeByGUID(aDiagram, sourceGUID);
		IRPGraphNode targetNode = JsonDiagram.GetGraphNodeByGUID(aDiagram, targetGUID);
		
		IRPGraphEdge edge = aDiagram.addNewEdgeForElement(modelElement, sourceNode, xSourcePosition, ySourcePosition, targetNode, xTargetPosition, yTargetPosition);
		if(edge == null)
		{
			return null;
		}
		
		addAttributes(edge);
		
		return edge;
		
	}

}
