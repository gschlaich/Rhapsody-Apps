package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.telelogic.rhapsody.core.IRPCollection;
import com.telelogic.rhapsody.core.IRPDiagram;
import com.telelogic.rhapsody.core.IRPGraphEdge;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPState;

import de.schlaich.gunnar.aiTools.mcp.jsonModels.JsonModelElementBase.ImportMode;

import com.telelogic.rhapsody.core.IRPGraphElement;
import com.telelogic.rhapsody.core.IRPGraphNode;
import com.telelogic.rhapsody.core.IRPGraphicalProperty;

public class JsonDiagram extends JsonUnit
{

	/*
	 
	  
	  IRPCollection 	getCustomViews()
          Gets the custom views that were applied to this diagram view.
 IRPDiagram 	getDiagramViewOf()
          For diagram views, gets the diagram on which the diagram view is based.
 IRPCollection 	getDiagramViews()
          Gets the diagram views that are based on this diagram.
 IRPCollection 	getElementsInDiagram()
          Returns a collection of all the model elements in the diagram.
 IRPCollection 	getGraphicalElements()
          Returns a collection of all the graphical elements in the diagram.
 java.lang.String 	getLastVisualizationModifiedTime()
          Returns the time at which the visual representation of the diagram was last changed.
	 
	 */
	
//	@JsonProperty("elementsInDiagram")
//	protected List<JsonModelElementBase> elementsInDiagram = new java.util.ArrayList<JsonModelElementBase>();
	@JsonProperty("graphicalNodes")
	protected List<JsonGraphNode> graphicalNodes = new java.util.ArrayList<JsonGraphNode>();
	@JsonProperty("graphicalEdges")
	protected List<JsonGraphEdge> graphicalEdges = new java.util.ArrayList<JsonGraphEdge>();
	
	@JsonProperty("lastVisualizationModifiedTime")
	protected String lastVisualizationModifiedTime = "";
	
	
	public JsonDiagram(IRPModelElement aModelElement, int level)
	{
		super(aModelElement, level);
		if (aModelElement == null)
		{
			return;
		}
		if (aModelElement instanceof IRPDiagram)
		{
			IRPDiagram theDiagram = (IRPDiagram) aModelElement;
			
			IRPProject project = theDiagram.getProject();
			
			// elementsInDiagram
//			for (Object theObj : theDiagram.getElementsInDiagram().toList())
//			{
//				if (theObj instanceof IRPModelElement)
//				{
//					IRPModelElement theME = (IRPModelElement) theObj;
//					elementsInDiagram.add(new JsonModelElementBase(theME,level));
//				}
//			}
	
			List<IRPGraphElement> graphElements = theDiagram.getGraphicalElements().toList();
			
			for (IRPGraphElement ge : graphElements)
			{
				if(ge instanceof IRPGraphNode)
				{
					IRPModelElement modelElement = ge.getModelObject();
					if(modelElement == null)
					{
						continue;
					}
					
					if(modelElement instanceof IRPState)
					{
						String modelElementGuid = modelElement.getGUID();
						IRPState theState = (IRPState)modelElement;
						boolean added = false;
						for(int i = 0; i < graphicalNodes.size(); i++)
						{
							
							JsonGraphNode jg = graphicalNodes.get(i);
							JsonModelElementBase jsonME = jg.modelObject;
							
							IRPModelElement modelElementOfJsonME = jsonME.getReference(project);
							if(modelElementOfJsonME instanceof IRPState)
							{
								IRPState stateOfJsonME = (IRPState)modelElementOfJsonME;
								IRPState parentStateOfJsonME = stateOfJsonME.getParent();
								if(parentStateOfJsonME != null)
								{
									String parentStateOfJsonMEGuid = parentStateOfJsonME.getGUID();
									if(parentStateOfJsonMEGuid.equals(modelElementGuid))
									{
										// if the state is already in the list, add the graph node after the state element
										graphicalNodes.add(i, new JsonGraphNode((IRPGraphNode) ge));
										added = true;
										break;
									}
								}
							}	
						}
						if(added == false)
						{
							graphicalNodes.add(new JsonGraphNode((IRPGraphNode) ge));
						}
					}
					else
					{
					
						graphicalNodes.add(new JsonGraphNode((IRPGraphNode) ge));
					}
				}
				else if (ge instanceof IRPGraphEdge)
				{
					graphicalEdges.add(new JsonGraphEdge((IRPGraphEdge) ge));
				}
			}
			
			// lastVisualizationModifiedTime
			lastVisualizationModifiedTime = theDiagram.getLastVisualizationModifiedTime();
		}
	}
	
	public void replaceEdgeLinkGUID(String aOldGuid, String aNewGuid)
	{
		for (JsonGraphEdge edge : graphicalEdges)
		{
			
			String sourceGuid = edge.getSourceGUID();
			if( sourceGuid != null && sourceGuid.equals(aOldGuid) )
			{
				edge.setSourceGUID(aNewGuid);
			}
			String targetGuid = edge.getTargetGUID();
			if( targetGuid != null && targetGuid.equals(aOldGuid) )
			{
				edge.setTargetGUID(aNewGuid);
			}
			
		}
	}

	public JsonDiagram()
	{
		
	}
	
	@Override
	public void setAttributes(IRPModelElement modelElement, IRPProject project, ImportMode importMode)
	{
		super.setAttributes(modelElement, project, importMode);
		if (modelElement instanceof IRPDiagram == false)
		{
			return;
		}

		IRPDiagram theDiagram = (IRPDiagram) modelElement;

		// elementsInDiagram
//		for (JsonModelElementBase jsonME : elementsInDiagram)
//		{
//			IRPModelElement me = jsonME.getReference(project);
//			if (me != null)
//			{
//				//TODO 
//			}
//		}
		
		for (JsonGraphNode node : graphicalNodes)
		{		
			node.createNodeElement(this, project);
		}
		
		for (JsonGraphEdge edge : graphicalEdges)
		{		
			edge.createEdgeElement(theDiagram, project);
		}
	
	}
	
	
	public static IRPGraphNode GetGraphNodeByGUID(IRPDiagram aDiagram, String guid)
	{
		if (aDiagram == null || guid == null)
		{
			return null;
		}
		
		List<IRPGraphElement> graphElements = aDiagram.getGraphicalElements().toList();
		
		for (IRPGraphElement ge : graphElements)
		{
			String geGuid = JsonGraphElement.GUID(ge);
			if (guid.equals(geGuid))
			{
				if(ge instanceof IRPGraphNode)
					
				return (IRPGraphNode)ge;
			}
		}
		
		return null;
		
	}
	
	
	
}
