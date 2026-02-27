package de.schlaich.gunnar.aiTools.mcp.jsonModels;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPState;
import com.telelogic.rhapsody.core.IRPStateVertex;
import com.telelogic.rhapsody.core.IRPTransition;

import de.schlaich.gunnar.aiTools.mcp.jsonModels.JsonModelElement;
import de.schlaich.gunnar.aiTools.mcp.jsonModels.JsonModelElementBase;

public class JsonStateVertex extends JsonModelElement
{

	/*
	    IRPCollection 	getInTransitions()
          Returns all of the transitions that enter the element.
 		IRPCollection 	getOutTransitions()
          Returns all of the transitions that exit the element.
            IRPState 	getParent()
          Returns the element's parent.
 void 	setParent(IRPState parent)
          Sets the parent state of the element.
	 */
	
	@JsonProperty("inTransitions")
	protected List<JsonModelElementBase> inTransitions = null;
	@JsonProperty("outTransitions")
	protected List<JsonModelElementBase> outTransitions = null;
	@JsonProperty("parent")
	protected JsonModelElementBase parentState = null;
	
	
	
	
	public JsonStateVertex(IRPModelElement aModelElement, int level)
	{
		super(aModelElement, level);
		
		if (aModelElement == null)
		{
			return;
		}
		
		if (aModelElement instanceof IRPStateVertex)
		{
			IRPStateVertex theStateVertex = (IRPStateVertex) aModelElement;

			inTransitions = convertToJsonModelElementBaseList(theStateVertex.getInTransitions());
			
			outTransitions = convertToJsonModelElementBaseList(theStateVertex.getOutTransitions());
			
			if (theStateVertex.getParent() != null)
			{
				parentState = new JsonModelElementBase(theStateVertex.getParent());
			}
			
			//outTransitions = convertToJsonModelElementList(theStateVertex.getOutTransitions());
		}
		
		
	}
	
	/*
	
	@Override
	protected void getNestedElements(IRPModelElement aModelElement)
	{
		if (aModelElement instanceof IRPStateVertex == false)
		{
			return;
		}
		
		IRPStateVertex theStateVertex = (IRPStateVertex) aModelElement;
		
		List<IRPModelElement> nestedElements = theStateVertex.getNestedElements().toList();
		
		for (IRPModelElement nestedElement : nestedElements)
		{

			if (nestedElement instanceof IRPTransition == false)
			{
				JsonModelElementBase jsonNestedElement = JsonModelFactory.Instance().getJsonModelElement((IRPModelElement) nestedElement, 0);
				this.nestedElements.add(jsonNestedElement);
			}
	
		}
		
		
	}
	
	*/

	public JsonStateVertex()
	{
		
	}
	
	public IRPModelElement toModelElement(JsonModelElementBase parent, IRPProject project, ImportMode mode)
	{
		
		
		// will never be called
		
		IRPModelElement model = super.toModelElement(parent, project, mode);
		if (model == null)
		{
			return null;
		}
		if (model instanceof IRPStateVertex == false)
		{
			return null;
		}
		if (mode == ImportMode.reference)
		{
			return model;
		}
		
		IRPStateVertex theStateVertex = (IRPStateVertex) model;
		
		if (parentState != null)
		{
			if (parentState.name.equals("ROOT")==false)
			{
				
				IRPModelElement parentElement = parentState.toModelElement(parent, project, ImportMode.reference);
				if (parentElement != null && parentElement instanceof IRPState)
				{
					theStateVertex.setParent((IRPState) parentElement);
				}
			}
		}

		return theStateVertex;
	}

}
