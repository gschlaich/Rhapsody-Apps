package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.telelogic.rhapsody.core.IRPInstance;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPOperation;
import com.telelogic.rhapsody.core.IRPProject;

public class JsonInstance extends JsonRelation
{

	/*
	   IRPCollection 	getAllNestedElements()
          Returns a collection of all the model elements that are directly under the object.
	 java.lang.String 	getAttributeValue(java.lang.String attName)
	          method getAttributeValue
	 IRPCollection 	getInLinks()
	          method getInLinks
	 IRPOperation 	getInstantiatedBy()
	          get property instantiatedBy
	 IRPCollection 	getListOfInitializerArguments()
	          method getListOfInitializerArguments
	 IRPCollection 	getOutLinks()
	          method getOutLinks 
	 
	 */
	
	@JsonProperty("allNestedElements")
	protected List<JsonModelElementBase> allNestedElements;
	@JsonProperty("inLinks")
	protected List<JsonModelElementBase> inLinks;
	@JsonProperty("instantiatedBy")
	protected JsonModelElementBase instantiatedBy;
	@JsonProperty("listOfInitializerArguments")
	protected List<JsonModelElementBase> listOfInitializerArguments;
	@JsonProperty("outLinks")
	protected List<JsonModelElementBase> outLinks;
	
	
	
	
	public JsonInstance(IRPModelElement aModelElement, int level)
	{
		super(aModelElement, level);
		if (aModelElement == null)
		{
			return;
		}
		if (!(aModelElement instanceof IRPInstance))
		{
			return;
		}
		
		
		IRPInstance theInstance = (IRPInstance) aModelElement;
		
		allNestedElements = convertToJsonModelElementBaseList(theInstance.getAllNestedElements());
		inLinks = convertToJsonModelElementBaseList(theInstance.getInLinks());
		if (theInstance.getInstantiatedBy() != null)
		{
			instantiatedBy = new JsonModelElementBase(theInstance.getInstantiatedBy());
		}
		listOfInitializerArguments = convertToJsonModelElementBaseList(theInstance.getListOfInitializerArguments());
		outLinks = convertToJsonModelElementBaseList(theInstance.getOutLinks());
		
	}

	public JsonInstance()
	{
		
	}
	
	public IRPModelElement toModelElement(JsonModelElementBase parent, IRPProject project, ImportMode importMode)
	{
		IRPInstance theInstance = (IRPInstance) super.toModelElement(parent, project, importMode  );
		
		if (instantiatedBy != null)
		{
			theInstance.setInstantiatedBy((IRPOperation) instantiatedBy.toModelElement(project, ImportMode.reference));
		}
		
		return theInstance;

	}

}
