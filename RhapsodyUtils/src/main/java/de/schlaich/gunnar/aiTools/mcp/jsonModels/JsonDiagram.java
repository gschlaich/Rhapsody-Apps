package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.telelogic.rhapsody.core.IRPCollection;
import com.telelogic.rhapsody.core.IRPDiagram;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;
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
	
	public IRPModelElement toModelElement(JsonModelElementBase parent, IRPProject project, ImportMode importMode)
	{
		IRPModelElement elem = super.toModelElement(parent, project, importMode);
		
		if (elem == null)
		{
			return null;
		}
		
		if (elem instanceof IRPDiagram == false)
		{
			return null;
		}
		
		IRPDiagram diagram = (IRPDiagram) elem;
		
		if (importMode == ImportMode.reference)
		{
			return diagram;
		}
		
		
		
//		if (importMode == ImportMode.create)
//		{
//			if (diagram.getGraphicalElements().toList().size() != 0)
//			{
//				// in create mode we do not want to create graphical elements
//				return null;
//			}
//
//		}
//		else
//		{
//			// remove existing graphical elements
//			IRPCollection existingGraphElements = diagram.getGraphicalElements();
//			diagram.removeGraphElements(existingGraphElements);
//			
//		}
//		
//		for (JsonGraphElement jsonGE : graphicalElements)
//		{
//			IRPGraphElement ge = (IRPGraphElement) jsonGE.toModelElement(null, project, ImportMode.reference);
//			if (ge != null)
//			{
//				diagram.grap
//			}
//		}

		return diagram;

	}

}
