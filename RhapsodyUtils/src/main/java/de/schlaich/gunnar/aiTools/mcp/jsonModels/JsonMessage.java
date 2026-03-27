package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import java.util.List;

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
	protected JsonModelElementBase sourceExecutionOccurrence;
//				IRPExecutionOccurrence 	addTargetExecutionOccurrence()
//				      method addTargetExecutionOccurrence
	@JsonProperty("targetExecutionOccurrence")
	protected JsonModelElementBase targetExecutionOccurrence;
//				IRPCollection 	getActualParameterList()
//				      get property actualParameterList
	@JsonProperty("actualParameterList")
	protected List<String> actualParameterList;
//				IRPAssociationRole 	getCommunicationConnection()
//				      get property communicationConnection
	@JsonProperty("communicationConnection")
	protected JsonModelElementBase communicationConnection;
//				java.lang.String 	getCondition()
//				      get property condition
	@JsonProperty("condition")
	protected String condition;
//				java.lang.String 	getDurationConstraint()
//				      Gets the text of the Duration Constraint.
	@JsonProperty("durationConstraint")
	protected String durationConstraint;
//				java.lang.String 	getDurationObservation()
//				      Gets the text of the Duration Observation.
//				IRPSysMLPort 	getFlowPort()
//				      get property flowPort
	@JsonProperty("flowPort")
	protected JsonModelElementBase flowPort;
//				IRPInterfaceItem 	getFormalInterfaceItem()
//				      get property formalInterfaceItem
	@JsonProperty("formalInterfaceItem")
	protected JsonModelElementBase formalInterfaceItem;
//				IRPModelElement 	getFormalType()
//				      Returns the model element associated with an action block, condition mark, timeout, or canceled timeout, in a sequence diagram.
	@JsonProperty("formalType")
	protected JsonModelElementBase formalType;
//				java.lang.String 	getInvariant()
//				      Gets the text of the Invariant field for the state invariant.
	@JsonProperty("invariant")
	protected String invariant;
//				java.lang.String 	getMessageType()
//				      get property messageType
	@JsonProperty("messageType")
	protected String messageType;
//				IRPPort 	getPort()
//				      get property Port
	@JsonProperty("port")
	protected JsonModelElementBase port;
//				java.lang.String 	getReturnValue()
//				      get property returnValue
	@JsonProperty("returnValue")
	protected String returnValue;
//				java.lang.String 	getSequenceNumber()
//				      get property sequenceNumber
	@JsonProperty("sequenceNumber")
	protected String sequenceNumber;
//				java.lang.String 	getSignature()
//				      method getSignature
	
//				IRPClassifierRole 	getSource()
//				      get property source
	@JsonProperty("source")
	protected JsonModelElementBase source;
//				IRPExecutionOccurrence 	getSourceExecutionOccurrence()
//				      get property sourceExecutionOccurrence
	
//				IRPClassifierRole 	getTarget()
//				      get property target
	@JsonProperty("target")
	protected JsonModelElementBase target;
//				IRPExecutionOccurrence 	getTargetExecutionOccurrence()
//				      get property targetExecutionOccurrence
//				java.lang.String 	getTimeConstraint()
//				      Gets the text for the Time Constraint that was applied to this state variant.
	@JsonProperty("timeConstraint")
	protected String timeConstraint;
//				java.lang.String 	getTimeObservation()
//				      Gets the text of the Time Observation.
	@JsonProperty("timeObservation")
	protected String timeObservation;
//				java.lang.String 	getTimerValue()
//				      get property timerValue
	@JsonProperty("timerValue")
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
			sourceExecutionOccurrence = new JsonModelElementBase(theMessage.getSourceExecutionOccurrence());
		}
		if (theMessage.getTargetExecutionOccurrence() != null)
		{
			targetExecutionOccurrence = new JsonModelElementBase(theMessage.getTargetExecutionOccurrence());
		}
		
		List<String> actualParameterListTemp = theMessage.getActualParameterList().toList();
		actualParameterList = actualParameterListTemp;
		
		if (theMessage.getCommunicationConnection() != null)
		{
			communicationConnection = new JsonModelElementBase(theMessage.getCommunicationConnection());
		}
		condition = theMessage.getCondition();
		if(theMessage.getDurationConstraint() != null)
		{
			durationConstraint = theMessage.getDurationConstraint();
		}
		if (theMessage.getFlowPort() != null)
		{
			flowPort = new JsonModelElementBase(theMessage.getFlowPort());
		}
		if (theMessage.getFormalInterfaceItem() != null)
		{
			formalInterfaceItem = new JsonModelElementBase(theMessage.getFormalInterfaceItem());
		}
		if (theMessage.getFormalType() != null)
		{
			formalType = new JsonModelElementBase(theMessage.getFormalType());
		}
		//invariant = theMessage.getInvariant();
		messageType = theMessage.getMessageType();
		if (theMessage.getPort() != null)
		{
			port = new JsonModelElementBase(theMessage.getPort());
		}
		returnValue = theMessage.getReturnValue();
		sequenceNumber = theMessage.getSequenceNumber();
		if (theMessage.getSource() != null)
		{
			source = new JsonModelElementBase(theMessage.getSource());
		}
		if (theMessage.getTarget() != null)
		{
			target = new JsonModelElementBase(theMessage.getTarget());
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
