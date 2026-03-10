package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.telelogic.rhapsody.core.IRPGuard;
import com.telelogic.rhapsody.core.IRPInterfaceItem;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPState;
import com.telelogic.rhapsody.core.IRPStateVertex;
import com.telelogic.rhapsody.core.IRPTransition;
import com.telelogic.rhapsody.core.IRPTrigger;

public class JsonTransition extends JsonModelElement
{

	/*
	 * IRPTransition getInheritsFrom() For transitions inherited from a base
	 * statechart, returns the base transition from which this transition is
	 * derived. int getIsOverridden() Checks whether the transition is a new
	 * transition added to the derived statechart, or a transition inherited from
	 * the base statechart. IRPAction getItsAction() Returns the action that was set
	 * for the transition. IRPGuard getItsGuard() Returns the guard that was set for
	 * the transition. java.lang.String getItsLabel() Returns the trigger, guard,
	 * and action for the transition, as a single string, as it appears in the label
	 * for the transition in the statechart, for example, IgnitionEvent[gear ==
	 * 0]/runStarter(). IRPStateVertex getItsSource() Returns the state that is the
	 * source of the transition. IRPStatechart getItsStatechart() Returns the
	 * statechart that the transition belongs to. IRPStateVertex getItsTarget()
	 * Returns the state that is the target of the transition. IRPTrigger
	 * getItsTrigger() Returns the trigger that was set for the transition. IRPState
	 * getOfState() For default transitions, returns the state where the transition
	 * originates. int isDefaultTransition() Checks whether this is the default
	 * transition of the statechart. int isStaticReaction() Checks whether the
	 * transition is an internal transition in a state. IRPCollection
	 * itsCompoundSource() method itsCompoundSource
	 */

	@JsonProperty("inheritsFrom")
	protected JsonModelElementBase inheritsFrom = null;
	@JsonProperty("isOverridden")
	protected boolean isOverridden = false;
	@JsonProperty("itsAction")
	protected JsonModelElementBase itsAction = null;
	@JsonProperty("itsGuard")
	protected String itsGuard = null;
	@JsonProperty("itsLabel")
	protected String itsLabel = null;
	@JsonProperty("itsTrigger")
	protected JsonModelElementBase itsTrigger = null;
	@JsonProperty("itsSource")
	protected JsonModelElementBase itsSource = null;
	@JsonProperty("itsTarget")
	protected JsonModelElementBase itsTarget = null;
	@JsonProperty("isDefaultTransition")
	protected boolean isDefaultTransition = false;
	@JsonProperty("isStaticReaction")
	protected boolean isStaticReaction = false;

	public JsonTransition(IRPModelElement aModelElement, int level)
	{
		super(aModelElement, level);

		if (aModelElement == null)
		{
			return;
		}

		if (!(aModelElement instanceof IRPTransition))
		{
			return;
		}

		IRPTransition theTransition = (IRPTransition) aModelElement;

		isOverridden = theTransition.getIsOverridden() == 1;
		itsLabel = theTransition.getItsLabel();
		if (theTransition.getInheritsFrom() != null)
		{
			inheritsFrom = new JsonModelElementBase(theTransition.getInheritsFrom());
		}
		if (theTransition.getItsAction() != null)
		{
			itsAction = new JsonModelElementBase(theTransition.getItsAction());
		}
		if (theTransition.getItsGuard() != null)
		{
			IRPGuard g = theTransition.getItsGuard();
			if (g != null)
			{
				itsGuard = g.getBody();
			}
		}
		if (theTransition.getItsTrigger() != null)
		{
			trace("------------------ Transition trigger: " + theTransition.getItsTrigger().getMetaClass() + " Class: " + theTransition.getItsTrigger().getClass());
			
			itsTrigger = new JsonModelElementBase(theTransition.getItsTrigger());
		}

		if (theTransition.getItsSource() != null)
		{
			itsSource = new JsonModelElementBase(theTransition.getItsSource());
		}

		if (theTransition.getItsTarget() != null)
		{
			itsTarget = new JsonModelElementBase(theTransition.getItsTarget());
		}

		isDefaultTransition = theTransition.isDefaultTransition() == 1;
		isStaticReaction = theTransition.isStaticReaction() == 1;

	}

	public JsonTransition()
	{
		// TODO Auto-generated constructor stub
	}

	public JsonModelElementBase getItsTarget()
	{
		return itsTarget;
	}

	@Override
	public IRPModelElement createModelElement(IRPModelElement aParent)
	{
		if (aParent == null)
		{
			return null;
		}

		IRPProject project = aParent.getProject();

		IRPStateVertex targetVertex = null;
		IRPStateVertex sourceVertex = null;

		IRPModelElement targetElement = itsTarget.getReference(project);
		if (targetElement != null)
		{
			if (targetElement instanceof IRPStateVertex)
			{
				targetVertex = (IRPStateVertex) targetElement;
			}

		}

		IRPModelElement sourceElement = itsSource.getReference(project);
		if (sourceElement != null)
		{
			if (sourceElement instanceof IRPStateVertex)
			{
				sourceVertex = (IRPStateVertex) sourceElement;
			}
		}

		if (isDefaultTransition)
		{
			if (targetVertex == null)
			{
				trace("Default transition target is not set.");
				return null;
			}
			if (sourceVertex == null)
			{
				trace("Default transition source must not be set.");
				return null;
			}
			IRPState targetState = null;
			if (targetVertex instanceof IRPState)
			{
				targetState = (IRPState) targetVertex;
			}
			else
			{
				trace("Default transition target must be a state");
				return null;
			}
			IRPState sourceState = null;
			if (sourceVertex instanceof IRPState)
			{
				sourceState = (IRPState) sourceVertex;
			}
			else
			{
				trace("Parent of default transition must be a state");
				return null;
			}

			return targetState.createDefaultTransition(sourceState);

		}
		if (isStaticReaction)
		{
			if (sourceVertex == null)
			{
				trace("Static reaction source must not be set.");
				return null;
			}

			IRPState sourceState = null;
			if (sourceVertex instanceof IRPState)
			{
				sourceState = (IRPState) sourceVertex;
			}
			else
			{
				trace("Source of static reaction must be a state");
				return null;
			}

			IRPInterfaceItem triggerOperation = null;

			for (JsonModelElementBase nestedElement : this.nestedElements)
			{
				if (nestedElement instanceof JsonTrigger == false)
				{
					continue;
				}
				JsonTrigger jsonTrigger = (JsonTrigger) nestedElement;

				JsonModelElementBase triggerJsonOperation = jsonTrigger.getItsOperation();

				if (triggerJsonOperation == null)
				{
					trace("Trigger operation reference is not set.");
					return null;
				}
				IRPModelElement triggerOperationElement = triggerJsonOperation.getReference(project);
				if (triggerOperationElement == null)
				{
					trace("Trigger operation reference could not be resolved.");
					return null;
				}
				if (triggerOperationElement instanceof IRPInterfaceItem == false)
				{
					trace("Trigger operation reference must be an operation.");
					return null;
				}

				triggerOperation = (IRPInterfaceItem) triggerOperationElement;
				break;
			}

			return sourceState.addStaticReaction(triggerOperation);

		}

		if (sourceVertex == null)
		{
			trace("Transition source is not set");
			return null;
		}

		return sourceVertex.addTransition(targetVertex);

	}

	@Override
	public void setAttributes(IRPModelElement aModelElement, IRPProject aProject, ImportMode aImportMode)
	{
		super.setAttributes(aModelElement, aProject, aImportMode);

		if (aModelElement instanceof IRPTransition == false)
		{
			return;
		}

		IRPTransition theTransition = (IRPTransition) aModelElement;

		if (isSet(itsGuard))
		{
			theTransition.setItsGuard(itsGuard);
		}

	}

	/*
	 * 
	 * public IRPModelElement toModelElement(JsonModelElementBase parent, IRPProject
	 * project, ImportMode aImportMode) {
	 * 
	 * 
	 * 
	 * 
	 * if(itsSource == null) { trace("Transition source is not set"); return null; }
	 * 
	 * 
	 * 
	 * IRPModelElement sourceElement = this.itsSource.getReference(project);
	 * 
	 * if(sourceElement == null) { return null; }
	 * 
	 * if (!(sourceElement instanceof IRPStateVertex)) { return null; };
	 * 
	 * IRPStateVertex sourceVertex = (IRPStateVertex) sourceElement;
	 * 
	 * if (itsTarget == null) { trace("Transition target is not set"); return null;
	 * }
	 * 
	 * trace("Source vertex: "+sourceVertex.getName());
	 * trace("Source vertex Fullname: " +sourceVertex.getFullPathName());
	 * 
	 * IRPModelElement targetElement = itsTarget.getReference(project);
	 * 
	 * if (targetElement == null) { return null; }
	 * 
	 * if (!(targetElement instanceof IRPStateVertex)) { return null; }
	 * 
	 * IRPStateVertex targetVertex = (IRPStateVertex) targetElement;
	 * 
	 * trace("Target vertex: "+ targetVertex.getName());
	 * trace("Target vertex Fullname: " + targetVertex.getFullPathName());
	 * 
	 * IRPTransition theTransition = null;
	 * 
	 * if (this instanceof JsonDefaultTransition) { if(targetVertex instanceof
	 * IRPState == false) { return null; } if (sourceVertex instanceof IRPState ==
	 * false) { return null; }
	 * 
	 * 
	 * IRPState targetState = (IRPState) targetVertex; IRPState sourceState =
	 * (IRPState) sourceVertex;
	 * 
	 * //check if default transition already exists IRPTransition
	 * existingDefaultTransition = targetState.getDefaultTransition(); if
	 * (existingDefaultTransition != null) {
	 * trace("Default transition from state "+targetState.getName()
	 * +" already exists. "); trace("Name of existing default transition target: "
	 * +existingDefaultTransition.getName());
	 * 
	 * theTransition = existingDefaultTransition;
	 * 
	 * } else {
	 * 
	 * theTransition = targetState.createDefaultTransition(sourceState); }
	 * 
	 * } else { if(sourceVertex instanceof IRPState) { IRPState sourceState =
	 * (IRPState) sourceVertex; trace("SourceState " + sourceState.getName() +
	 * " Root? " + sourceState.isRoot()); }
	 * 
	 * if (targetVertex instanceof IRPState) { IRPState targetState = (IRPState)
	 * targetVertex; trace("TargetState "+ targetState.getName() + " Root? " +
	 * targetState.isRoot()); }
	 * 
	 * theTransition = sourceVertex.addTransition(targetVertex); }
	 * 
	 * if (theTransition == null) { return null; }
	 * 
	 * 
	 * // if (isOverridden) // { // theTransition.overrideInheritance(); // }
	 * 
	 * if(itsTrigger!=null) { itsTrigger.createModelElement(theTransition); }
	 * 
	 * if (isSet(itsGuard)) { theTransition.setItsGuard(itsGuard); }
	 * 
	 * return theTransition; }
	 */

}
