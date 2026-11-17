package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.telelogic.rhapsody.core.IRPConnector;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPState;

public class JsonConnector extends JsonStateVertex
{

	/*
	 * IRPTransition createDefaultTransition(IRPState from) Creates a default
	 * transition leading to this connector, within the state specified.
	 * java.lang.String getConnectorType() Returns the type of the connector:
	 * Condition, Diagram, EnterExit, Fork, History, Join, Junction, Termination,
	 * InPin, OutPin, or InOutPin. IRPCollection getDerivedInEdges() Returns a
	 * collection of the transitions coming into the connector. IRPTransition
	 * getDerivedOutEdge() Returns the transition exiting the connector. IRPSwimlane
	 * getItsSwimlane() For connectors in a swimlane, returns the swimlane that
	 * contains the connector. IRPState getOfState() For history connectors, returns
	 * the state that the history connector belongs to.
	 * 
	 * 
	 */

	enum ConnectorType
	{
		Condition, Diagram, EnterExit, Fork, History, Join, Junction, Termination, InPin, OutPin, InOutPin, unknown
	}

	@JsonProperty("connectorType")
	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	protected ConnectorType connectorType = ConnectorType.unknown;
	
	@JsonProperty("derivedInEdges")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	protected List<JsonModelElementBase> derivedInEdges;
	
	@JsonProperty("derivedOutEdge")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	protected JsonModelElementBase derivedOutEdge;
	
	@JsonProperty("OfState")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	protected JsonModelElementBase ofState;

	public JsonConnector()
	{
		// TODO Auto-generated constructor stub
	}

	public JsonConnector(IRPModelElement aModelElement, int level)
	{
		super(aModelElement, level);
		if (aModelElement == null)
		{
			return;
		}

		if (!(aModelElement instanceof IRPConnector))
		{
			return;
		}

		IRPConnector theConnector = (IRPConnector) aModelElement;

		connectorType = ConnectorType.valueOf(theConnector.getConnectorType());

		derivedInEdges = convertToJsonModelElementBaseList(theConnector.getDerivedInEdges());

		if (theConnector.getDerivedOutEdge() != null)
		{
			derivedOutEdge = getJsonModelElementBase(theConnector.getDerivedOutEdge());
		}

		if (theConnector.getOfState() != null)
		{
			ofState = getJsonModelElementBase(theConnector.getOfState());
		}

	}

	@Override
	public IRPModelElement createModelElement(IRPModelElement parent)
	{
		if (parent == null)
		{
			return null;
		}

		IRPProject project = parent.getProject();

		trace("Parent of connector: " + this.parentState.fullName + " GUID: " + this.parentState.guid);

		IRPModelElement parentModel = this.parentState.getReference(project);
		if (parentModel == null)
		{
			return null;
		}
		if (parentModel instanceof IRPState == false)
		{
			return null;
		}

		IRPState parentState = (IRPState) parentModel;

		return parentState.addConnector(this.connectorType.name());

	}

	@Override
	public void setAttributes(IRPModelElement aModelElement, IRPProject aProject, ImportMode aImportMode)
	{
		super.setAttributes(aModelElement, aProject, aImportMode);

		if (aModelElement instanceof IRPConnector == false)
		{
			return;
		}

		IRPConnector theConnector = (IRPConnector) aModelElement;

		if (ofState != null)
		{
			IRPModelElement ofStateModel = ofState.getReference(aProject);
			if (ofStateModel != null && ofStateModel instanceof IRPState)
			{
				theConnector.setOfState((IRPState) ofStateModel);
			}
		}

	}

}
