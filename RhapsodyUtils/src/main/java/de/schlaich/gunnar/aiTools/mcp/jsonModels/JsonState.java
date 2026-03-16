package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPSendAction;
import com.telelogic.rhapsody.core.IRPState;
import com.telelogic.rhapsody.core.IRPStatechart;
import com.telelogic.rhapsody.core.IRPSwimlane;
import com.telelogic.rhapsody.core.IRPTransition;

public class JsonState extends JsonStateVertex
{
	
	/*
		   IRPTransition 	getDefaultTransition()
	          Returns the default transition within the state.
		 java.lang.String 	getEntryAction()
		          Returns the entry action that was defined for the state.
		 java.lang.String 	getExitAction()
		          Returns the exit action that was defined for the state.
		 java.lang.String 	getFullNameInStatechart()
		          Returns the full name of the state within the statechart, including information about its hierarchical position within the statechart.
		 IRPState 	getInheritsFrom()
		          Returns the corresponding state from the statechart of the class that this class is derived from.
		 IRPCollection 	getInternalTransitions()
		          Returns a collection of the state's internal transitions.
		 int 	getIsOverridden()
		          Checks whether there is still an inheritance relationship between this state and the corresponding state from the statechart of the class that this class is derived from.
		 int 	getIsReferenceActivity()
		          Checks whether this element is a call behavior element.
		 IRPStatechart 	getItsStatechart()
		          Returns the statechart that this state belongs to.
		 IRPSwimlane 	getItsSwimlane()
		          Returns the swimlane that the action is located in.
		 IRPCollection 	getLogicalStates()
		          Returns a collection of all the substates of the current state and all the first-level substates of those states, meaning down to the second level.
		 IRPStatechart 	getNestedStatechart()
		          Returns the state's sub-statechart.
		 IRPModelElement 	getReferenceToActivity()
		          For call behavior elements, returns the activity that is referenced.
		 IRPSendAction 	getSendAction()
		          Returns the Send Action element associated with the state.
		 java.lang.String 	getStateType()
		          Returns the type of the state, for example, an And state or a Termination state.
		 IRPCollection 	getStaticReactions()
		          Returns a collection of the state's internal transitions.
		 IRPCollection 	getSubStates()
          Returns a collection of the substates contained in this state.
          
	 */
	
	
	@JsonProperty("entryAction")
	protected String entryAction = null;
	@JsonProperty("exitAction")
	protected String exitAction = null;
	@JsonProperty("internalTransitions")
	protected List<JsonModelElementBase> internalTransitions = null;
	@JsonProperty("isOverridden")
	protected Boolean isOverridden = false;
	@JsonProperty("itsSwimlane")
	protected JsonModelElementBase itsSwimlane = null;
	@JsonProperty("subStates")
	protected List<JsonModelElementBase> subStates = null;
	@JsonProperty("stateType")
	protected String stateType = null;
	@JsonProperty("nestedStatechart")
	protected JsonModelElementBase nestedStatechart = null;
	@JsonProperty("root")
	protected Boolean root = false;
	
//	@JsonProperty("referenceToActivity")
//	protected JsonModelElementBase referenceToActivity = null;
//	@JsonProperty("sendAction")
//	protected JsonModelElementBase sendAction = null;
//	
	
	public JsonState(IRPModelElement aModelElement, int level)
	{
        super(aModelElement, level);
        if (aModelElement == null)
        {
            return;
        }
        
        if (aModelElement instanceof IRPState)
        {
            IRPState theState = (IRPState) aModelElement;
            
			
            entryAction = theState.getEntryAction();
            exitAction = theState.getExitAction();
            internalTransitions = convertToJsonModelElementBaseList(theState.getInternalTransitions());
            isOverridden = theState.getIsOverridden() == 1;
			if (theState.getItsSwimlane() != null)
			{
				itsSwimlane = new JsonModelElementBase(theState.getItsSwimlane());
			}
			subStates = convertToJsonModelElementBaseList(theState.getSubStates());
			//subStates = convertToJsonModelElementList(theState.getSubStates());
			
			stateType = theState.getStateType();
			if (theState.getNestedStatechart() != null)
			{
				nestedStatechart = new JsonModelElementBase(theState.getNestedStatechart());
			}
			
			root = theState.isRoot()==1;
			
//			if (theState.getReferenceToActivity() != null)
//			{
//				referenceToActivity = new JsonModelElementBase(theState.getReferenceToActivity());
//			}
//			if (theState.getSendAction() != null)
//			{
//				sendAction = new JsonModelElementBase(theState.getSendAction());
//			}
            
        }
	}
	public JsonState()
	{

	}
	
	public boolean hasSubState(JsonState aState)
	{
		if (subStates == null)
		{
			return false;
		}

		for (JsonModelElementBase s : subStates)
		{
			if (s.name.equals(aState.name))
			{
				return true;
			}
		}

		return false;
	}
	
	
	@Override
	public IRPModelElement createModelElement(IRPModelElement aParentElement)
	{
		
		
		trace("Creating state: " + this.name + " in parent element: " + aParentElement.getFullPathName());
		
		IRPState theState = null;

		if (this.root == true)
		{
			if (aParentElement instanceof IRPStatechart == false)
			{
				return null;
			}

			IRPStatechart parentStatechart = (IRPStatechart) aParentElement;
			theState = parentStatechart.getRootState();
		}
		else
		{
			
			IRPProject project = aParentElement.getProject();
			
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
			
			List<IRPState> subStates = parentState.getSubStates().toList();
			
			
		
			for (IRPState subState : subStates)
			{
				if(subState.getName().equals(this.name))
				{
					trace("State " + this.name + " already exists in parent state " + parentState.getFullPathName() + ". Reusing existing state.");
					return subState;
				}
			}
			
				
			try
			{
				theState = parentState.addState(this.name);
			}
			catch (Exception e)
			{
				trace("Could not create nested state " + this.fullName + " in parent state " + parentState.getFullPathName() + ". Exception: " + e.getMessage());
				
				List<IRPState> sStates = parentState.getSubStates().toList();
				for (IRPState subState : sStates)
				{
					trace("Existing substate: " + subState.getName());
				}
				
				return null;
			}
			
		}

		return theState;

	}
	
	
	@Override
	public void setAttributes(IRPModelElement aModelElement, IRPProject aProject, ImportMode aImportMode)
    {
        super.setAttributes(aModelElement, aProject, aImportMode);
        
        if (aModelElement instanceof IRPState == false)
        {
            return;
        }
        IRPState theState = (IRPState) aModelElement;
        
        if (isSet(entryAction))
        {
            theState.setEntryAction(entryAction);
        }
        if (isSet(exitAction))
        {
            theState.setExitAction(exitAction);
        }
        if (internalTransitions != null)
        {
            //convertToModelElementList( internalTransitions, aProject, ImportMode.create);
        }
        if (isOverridden == false)
        {
            //theState.overrideInheritance();
            theState.unoverrideInheritance();
        }
        if (itsSwimlane != null)
        {
            //theState.setItsSwimlane((IRPSwimlane) itsSwimlane.toModelElement(aProject, ImportMode.create));
        }
        if (subStates != null)
        {
			//convertToModelElementList( subStates, aProject, ImportMode.create);	
        }
        if (isSet(stateType))
        {
            theState.setStateType(stateType);
        }
        if (nestedStatechart != null)
        {
            //nestedStatechart.toModelElement(this, aProject, ImportMode.create);
        }
        //        if (referenceToActivity != null)
        //        {
        //            theState.setReferenceToActivity((IRPModelElement) referenceToActivity.toModelElement(null, project));
        //        }
        //        if (sendAction != null)
        //        {
        //            //theState.setSendAction((IRPSendAction) sendAction.toModelElement(null, project));
        //        }
    }

}


