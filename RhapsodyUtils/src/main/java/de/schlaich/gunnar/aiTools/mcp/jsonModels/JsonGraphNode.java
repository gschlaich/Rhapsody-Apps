package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import com.telelogic.rhapsody.core.IRPDiagram;
import com.telelogic.rhapsody.core.IRPGraphElement;
import com.telelogic.rhapsody.core.IRPGraphNode;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;

public class JsonGraphNode extends JsonGraphElement
{

	public JsonGraphNode(IRPGraphElement aGraphElement)
	{
		super(aGraphElement);
		// TODO Auto-generated constructor stub
	}

	public JsonGraphNode()
	{
		// TODO Auto-generated constructor stub
	}
	
	public IRPGraphNode createNodeElement(JsonDiagram aJsonDiagram, IRPProject aProject )
	{
		if (aJsonDiagram == null || aProject == null)
		{
			return null;
		}
		
		IRPDiagram diagram = (IRPDiagram) aJsonDiagram.getReference(aProject);
		
		if (diagram == null)
		{
			return null;
		}
			
		IRPModelElement modelElement = modelObject.getReference(aProject);
		
		if (modelElement == null)
		{
			return null;
		}
		
		//get cooridinates and size from graphical properties
		String xyPosition = myGraphicalPropertyMap.get("Position");
		
		if (xyPosition == null)
		{
			xyPosition = "0,0";
		}
		
		
		int xPosition = Integer.parseInt(xyPosition.split(",")[0]);
		int yPosition = Integer.parseInt(xyPosition.split(",")[1]);
		
		String heightStr = myGraphicalPropertyMap.get("Height");
		int height = 0;
		if (heightStr != null)
		{
			height = Integer.parseInt(heightStr);
		}
		
		String widthStr = myGraphicalPropertyMap.get("Width");
		int width = 0;
		if (widthStr != null)
		{
			width = Integer.parseInt(widthStr);
		}
		
		IRPGraphNode node = diagram.addNewNodeForElement(modelElement, xPosition, yPosition, width, height);
		
		if (node == null)
		{
			return null;
		}
		
		
		
		myGraphicalPropertyMap.replace("GUID", GUID(node));
		
		//addAttributes(node);
		return node;
			
	}

}
