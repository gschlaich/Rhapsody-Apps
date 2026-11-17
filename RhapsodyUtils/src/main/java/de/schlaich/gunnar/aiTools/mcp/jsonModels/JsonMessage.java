package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.telelogic.rhapsody.core.IRPCollection;
import com.telelogic.rhapsody.core.IRPMessage;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;

public class JsonMessage extends JsonModelElement
{
	
//				IRPExecutionOccurrence 	addSourceExecutionOccurrence()
//				      method addSourceExecutionOccurrence
	@JsonProperty("sourceExecutionOccurrence")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	protected JsonModelElementBase sourceExecutionOccurrence;
//				IRPExecutionOccurrence 	addTargetExecutionOccurrence()
//				      method addTargetExecutionOccurrence
	@JsonProperty("targetExecutionOccurrence")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	protected JsonModelElementBase targetExecutionOccurrence;
//				IRPCollection 	getActualParameterList()
//				      get property actualParameterList
	@JsonProperty("actualParameterList")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	protected List<String> actualParameterList;
//				IRPAssociationRole 	getCommunicationConnection()
//				      get property communicationConnection
	@JsonProperty("communicationConnection")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	protected JsonModelElementBase communicationConnection;
//				java.lang.String 	getCondition()
//				      get property condition
	@JsonProperty("condition")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	protected String condition;
//				java.lang.String 	getDurationConstraint()
//				      Gets the text of the Duration Constraint.
	@JsonProperty("durationConstraint")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	protected String durationConstraint;
//				java.lang.String 	getDurationObservation()
//				      Gets the text of the Duration Observation.
//				IRPSysMLPort 	getFlowPort()
//				      get property flowPort
	@JsonProperty("flowPort")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	protected JsonModelElementBase flowPort;
//				IRPInterfaceItem 	getFormalInterfaceItem()
//				      get property formalInterfaceItem
	@JsonProperty("formalInterfaceItem")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	protected JsonModelElementBase formalInterfaceItem;
//				IRPModelElement 	getFormalType()
//				      Returns the model element associated with an action block, condition mark, timeout, or canceled timeout, in a sequence diagram.
	@JsonProperty("formalType")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	protected JsonModelElementBase formalType;
//				java.lang.String 	getInvariant()
//				      Gets the text of the Invariant field for the state invariant.
	@JsonProperty("invariant")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	protected String invariant;
//				java.lang.String 	getMessageType()
//				      get property messageType
	@JsonProperty("messageType")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	protected String messageType;
//				IRPPort 	getPort()
//				      get property Port
	@JsonProperty("port")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	protected JsonModelElementBase port;
//				java.lang.String 	getReturnValue()
//				      get property returnValue
	@JsonProperty("returnValue")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	protected String returnValue;
//				java.lang.String 	getSequenceNumber()
//				      get property sequenceNumber
	@JsonProperty("sequenceNumber")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	protected String sequenceNumber;
//				java.lang.String 	getSignature()
//				      method getSignature
	
//				IRPClassifierRole 	getSource()
//				      get property source
	@JsonProperty("source")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	protected JsonModelElementBase source;
//				IRPExecutionOccurrence 	getSourceExecutionOccurrence()
//				      get property sourceExecutionOccurrence
	
//				IRPClassifierRole 	getTarget()
//				      get property target
	@JsonProperty("target")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	protected JsonModelElementBase target;
//				IRPExecutionOccurrence 	getTargetExecutionOccurrence()
//				      get property targetExecutionOccurrence
//				java.lang.String 	getTimeConstraint()
//				      Gets the text for the Time Constraint that was applied to this state variant.
	@JsonProperty("timeConstraint")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	protected String timeConstraint;
//				java.lang.String 	getTimeObservation()
//				      Gets the text of the Time Observation.
	@JsonProperty("timeObservation")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	protected String timeObservation;
//				java.lang.String 	getTimerValue()
//				      get property timerValue
	@JsonProperty("timerValue")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	protected String timerValue;
	
	
	
	public JsonMessage(IRPModelElement aModelElement, int level)
	{
		super(aModelElement, level);
		
		if(aModelElement == null)
		{
			return;
		}
		
		if(!(aModelElement instanceof IRPMessage))
		{
			return;
		}
		
		IRPMessage theMessage = (IRPMessage) aModelElement;
		
		if (theMessage.getSourceExecutionOccurrence() != null)
		{
			sourceExecutionOccurrence = getJsonModelElementBase(theMessage.getSourceExecutionOccurrence());
		}
		if (theMessage.getTargetExecutionOccurrence() != null)
		{
			targetExecutionOccurrence = getJsonModelElementBase(theMessage.getTargetExecutionOccurrence());
		}
		
		List<String> actualParameterListTemp = theMessage.getActualParameterList().toList();
		actualParameterList = actualParameterListTemp;
		
		if (theMessage.getCommunicationConnection() != null)
		{
			communicationConnection = getJsonModelElementBase(theMessage.getCommunicationConnection());
		}
		condition = theMessage.getCondition();
		if(theMessage.getDurationConstraint() != null)
		{
			durationConstraint = theMessage.getDurationConstraint();
		}
		if (theMessage.getFlowPort() != null)
		{
			flowPort = getJsonModelElementBase(theMessage.getFlowPort());
		}
		if (theMessage.getFormalInterfaceItem() != null)
		{
			formalInterfaceItem = getJsonModelElementBase(theMessage.getFormalInterfaceItem());
		}
		if (theMessage.getFormalType() != null)
		{
			formalType = getJsonModelElementBase(theMessage.getFormalType());
		}
		//invariant = theMessage.getInvariant();
		messageType = theMessage.getMessageType();
		if (theMessage.getPort() != null)
		{
			port = getJsonModelElementBase(theMessage.getPort());
		}
		returnValue = theMessage.getReturnValue();
		sequenceNumber = theMessage.getSequenceNumber();
		if (theMessage.getSource() != null)
		{
			source = getJsonModelElementBase(theMessage.getSource());
		}
		if (theMessage.getTarget() != null)
		{
			target = getJsonModelElementBase(theMessage.getTarget());
		}
		//timeConstraint = theMessage.getTimeConstraint();
		//timeObservation = theMessage.getTimeObservation();
		timerValue = theMessage.getTimerValue();
		
	}
	
	public JsonMessage()
	{
		
	}
	
	@Override
	public void setAttributes(IRPModelElement aModelElement, IRPProject aProject, ImportMode aImportMode) {
		super.setAttributes(aModelElement, aProject, aImportMode);
		
		if (!(aModelElement instanceof IRPMessage))
		{
			return;
		}
		
		IRPMessage theMessage = (IRPMessage) aModelElement;
		
		
		if (isSet(durationConstraint))
		{
			theMessage.setDurationConstraint(durationConstraint);
		}
		
		if (isSet(invariant))
		{
			theMessage.setInvariant(invariant);
		}

		if (isSet(returnValue))
		{
			theMessage.setReturnValue(returnValue);
		}

		if (isSet(timeConstraint))
		{
			theMessage.setTimeConstraint(timeConstraint);
		}
		
		if (isSet(timeObservation))
		{
			theMessage.setTimeObservation(timeObservation);
		}
		
		if (isSet(timerValue))
		{
			theMessage.setTimerValue(timerValue);
		}
		
		theMessage.setActualParameterList(null);
		
		
		
		IRPCollection actualParameterListCollection = theMessage.getActualParameterList();
		actualParameterListCollection.empty();
		int index = 0;
		
		for (String actualParameter : actualParameterList)
		{
			index++;
			actualParameterListCollection.setString(index, actualParameter);
		}
		
		theMessage.setActualParameterList(actualParameterListCollection);
	
		
	}
	
	@Override
	protected IRPModelElement createModelElement(IRPModelElement aParentElement)
	{
		return null;
	}


}
