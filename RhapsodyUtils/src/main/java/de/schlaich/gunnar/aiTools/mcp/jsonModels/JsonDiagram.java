package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.telelogic.rhapsody.core.IRPCollection;
import com.telelogic.rhapsody.core.IRPDiagram;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;

import de.schlaich.gunnar.aiTools.mcp.jsonModels.JsonModelElementBase.ImportMode;

import com.telelogic.rhapsody.core.IRPGraphElement;
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
	
	@JsonProperty("elementsInDiagram")
	protected List<JsonModelElementBase> elementsInDiagram = new java.util.ArrayList<JsonModelElementBase>();
	@JsonProperty("graphicalElements")
	protected List<JsonGraphElement> graphicalElements = new java.util.ArrayList<JsonGraphElement>();
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
			// elementsInDiagram
			for (Object theObj : theDiagram.getElementsInDiagram().toList())
			{
				if (theObj instanceof IRPModelElement)
				{
					IRPModelElement theME = (IRPModelElement) theObj;
					elementsInDiagram.add(new JsonModelElementBase(theME,level));
				}
			}
			
				
			List<IRPGraphElement> graphElements = theDiagram.getGraphicalElements().toList();
			
			for (IRPGraphElement ge : graphElements)
			{
				JsonGraphElement jsonGE = new JsonGraphElement(ge);
				
				graphicalElements.add(jsonGE);
			}
			
			// lastVisualizationModifiedTime
			lastVisualizationModifiedTime = theDiagram.getLastVisualizationModifiedTime();
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
		for (JsonModelElementBase jsonME : elementsInDiagram)
		{
			IRPModelElement me = jsonME.getReference(project);
			if (me != null)
			{
				//todo 
			}
		}

		
	}
	
}
