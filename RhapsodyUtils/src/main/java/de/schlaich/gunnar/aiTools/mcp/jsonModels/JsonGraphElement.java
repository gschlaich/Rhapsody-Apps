package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.telelogic.rhapsody.core.IRPDiagram;
import com.telelogic.rhapsody.core.IRPGraphElement;
import com.telelogic.rhapsody.core.IRPGraphicalProperty;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;

@JsonTypeInfo( use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "Type") 
@JsonSubTypes({
	@JsonSubTypes.Type(value = JsonGraphEdge.class, name = "GraphEdge"),
	@JsonSubTypes.Type(value = JsonGraphNode.class, name = "GraphNode")
	})

public class JsonGraphElement
{
	private static Consumer<String> myTraceAction = null;
	
	
//	  	IRPCollection 	getAllGraphicalProperties()
//		      method getAllGraphicalProperties
//		java.lang.String 	getAssociatedImage()
//		      get associatedImage
//		IRPDiagram 	getDiagram()
//		      method getDiagram
//		IRPGraphElement 	getGraphicalParent()
//		      get property graphicalParent
//		
//		java.lang.String 	getImageLayout()
//		      Returns the image layout specified for the image linked to the graphic element.
//		java.lang.String 	getInterfaceName()
//		      get property interfaceName
//		IRPCollection 	getLocalProperties()
//		      method getLocalProperties
//		IRPModelElement 	getModelObject()
//		      get property modelObject
//		java.lang.String 	getSelectedImage()
//		      Returns the full path of the image that was linked to the graphic element.
	
	//@JsonProperty("allGraphicalProperties")
	//protected List<JsonGraphicalProperty> allGraphicalProperties = null;
	@JsonProperty("associatedImage")
	protected String associatedImage = null;
	@JsonProperty("imageLayout")
	protected String imageLayout = null;
	@JsonProperty("interfaceName")
	protected String interfaceName = null;
	@JsonProperty("localProperties")
	protected Map<String, String> localProperties = null;
	@JsonProperty("modelObject")
	protected JsonModelElementBase modelObject = null;
	@JsonProperty("selectedImage")
	protected String selectedImage = null;
	
	@JsonProperty("graphicalPropertyMap")
	protected Map<String, String> myGraphicalPropertyMap = null;

	public JsonGraphElement(IRPGraphElement aGraphElement)
	{
		if (aGraphElement == null)
		{
			return;
		}
		// allGraphicalProperties
		//allGraphicalProperties = new ArrayList<JsonGraphicalProperty>();
		myGraphicalPropertyMap = new HashMap<String, String>();
		for (Object theObj : aGraphElement.getAllGraphicalProperties().toList())
		{
			if (theObj instanceof IRPGraphicalProperty)
			{
				IRPGraphicalProperty theProp = (IRPGraphicalProperty) theObj;
				JsonGraphicalProperty jsonProp = new JsonGraphicalProperty(theProp);
				if(jsonProp.hasValue())
                {
                    // only add properties that have a value
					//allGraphicalProperties.add(new JsonGraphicalProperty(theProp));
					myGraphicalPropertyMap.put(jsonProp.key, jsonProp.value);
                }
			}
		}
		associatedImage = aGraphElement.getAssociatedImage();

		imageLayout = aGraphElement.getImageLayout();
		interfaceName = aGraphElement.getInterfaceName();

		localProperties = new HashMap<String, String>();
		List<String> propList = aGraphElement.getLocalProperties().toList();
		for (String theProp : propList)
		{
			String[] keyValue = theProp.split(":");
			localProperties.put(keyValue[0], keyValue[1]);
		}
		

		if (aGraphElement.getModelObject() != null)
		{
			modelObject = new JsonModelElementBase(aGraphElement.getModelObject());
		}

		selectedImage = aGraphElement.getSelectedImage();
	}
	
	public JsonGraphElement()
	{
		// TODO Auto-generated constructor stub
	}
	
	
//	public IRPGraphElement toGraphElement(JsonModelElementBase aDiagram, IRPProject project)
//	{
//		if (aDiagram != null)
//		{
//			diagram = aDiagram;
//		}
//			
//		IRPModelElement elem = diagram.toModelElement(null, project);
//		if(elem instanceof IRPDiagram == false)
//		{
//			return null;
//		}
//		
//		IRPDiagram diagramModel = (IRPDiagram) elem;
//
//		// TODO Auto-generated method stub
//		return null;
//	}
	
	
	protected void trace(String aMessage)
	{
		Trace(aMessage);
	}
	
	protected static void Trace(String aMessage)
	{
		if (myTraceAction == null)
		{
			// no traceaction set...
			return;
		}

		aMessage = "JsonModel: " + aMessage;

		myTraceAction.accept(aMessage);
	}
	
	public static void SetTraceAction(Consumer<String> aTraceAction)
	{
		myTraceAction = aTraceAction;
	}
	

}
