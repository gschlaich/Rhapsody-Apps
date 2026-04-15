package de.schlaich.gunnar.rhapsody.utilities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.function.Consumer;

import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPCollection;
import com.telelogic.rhapsody.core.IRPDiagram;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.RPApplicationListener;

public class SelectionHistory extends RPApplicationListener
{

	private Consumer<String> myTraceAction = null;
	private IRPApplication myRhapsody = null;
	private IRPProject myProject = null;
	
	private Stack<String> myHistoryStack = new Stack<String>();
	private Stack<String> myForwardStack = new Stack<String>();
	
	private Stack<String> myLastChangesStack = new Stack<String>();
	private Stack<String> myLastChangesForwardStack = new Stack<String>();
	
	
	
	private boolean isNavigating = false;

	public SelectionHistory(Consumer<String> aTraceAction, IRPApplication aRhapsody)
	{
		myRhapsody = aRhapsody;
		myTraceAction = aTraceAction;
		myProject = myRhapsody.activeProject();
		trace("Initialized");
	}

	private void trace(String aMessage)
	{
		if (myTraceAction == null)
		{

			return;
		}

		aMessage = "SelectionHistory: " + aMessage;

		myTraceAction.accept(aMessage);
	}
	
	public void next()
	{
		
		isNavigating = true;
		if(myForwardStack.isEmpty())
		{
			trace("Forward stack is empty");
			return;
		}
		
		IRPCollection selection = myRhapsody.createNewCollection();
	
		IRPModelElement element = pop(myForwardStack);
		
		if(element==null)
		{
			return;
		}
		selection.addItem(element);
		trace(" Next: Selected: " + element.getDisplayName());
		myRhapsody.selectModelElements(selection);
		addToHistory(element);
	}
	
	public void back()
	{
		isNavigating = true;
		if(myHistoryStack.isEmpty())
		{
			trace("Back stack is empty");
			return;
		}
		IRPCollection selection = myRhapsody.createNewCollection();
		IRPModelElement element = pop(myHistoryStack);
		element = pop(myHistoryStack);
		if(element==null)
		{
			return;
		}
		push(myForwardStack, element);
		selection.addItem(element);
		trace("Back: Selected: " + element.getDisplayName());
		trace("Back stack size: " + myHistoryStack.size() + " Forward stack size: " + myForwardStack.size());
		myRhapsody.selectModelElements(selection);
	}
	
	private IRPModelElement pop(Stack<String> stack)
	{
		if(stack.isEmpty())
		{
			return null;
		}
		
		String elementGUID = stack.pop();
		
		IRPModelElement element = myProject.findElementByGUID(elementGUID);
		
		if(element == null)
		{
			return null;
		}
		
		
		return element;
		
		
	}
	
	private boolean push(Stack<String> stack, IRPModelElement element)
	{
		if(element == null)
		{
			return false;
		}
		
		String elementGUID = element.getGUID();
		
		if(stack.isEmpty()==false)
		{
			String topElementGUID = stack.peek();
			if(topElementGUID.equals(elementGUID))
			{
				return false;
			}
		}

		stack.push(elementGUID);
		return true;
	}

	public void nextChanged()
	{
		IRPModelElement element = pop(myLastChangesForwardStack);
		if(element==null)
		{
			return;
		}
		push(myLastChangesStack, element);
		IRPCollection selection = myRhapsody.createNewCollection();
		selection.addItem(element);
		trace("Next Changed: Selected: " + element.getDisplayName());
	}
	
	public void backChanged()
	{
		IRPModelElement element = pop(myLastChangesStack);
		if(element==null)
		{
			return;
		}
		push(myLastChangesForwardStack, element);
		IRPCollection selection = myRhapsody.createNewCollection();
		selection.addItem(element);
		trace("Back Changed: Selected: " + element.getDisplayName());
		myRhapsody.selectModelElements(selection);
	}

	
	@Override
	public boolean onElementsChanged(String elementsGUIDs)
	{
		
		if (elementsGUIDs.trim().length() == 0)
			return true;
	
		String[] GUIDsArray = elementsGUIDs.split(",");
		
		IRPProject rhpProj = myRhapsody.activeProject();
		String elementsChanged = "";
		
		List<IRPModelElement> changedElements = new ArrayList<IRPModelElement>();
		
		for (String guid : GUIDsArray)
		{
			IRPModelElement currElement = rhpProj.findElementByGUID(guid.trim());
			if (currElement != null)
			{
				changedElements.add(currElement);
			}
			else
			{
				trace("Could not find element with GUID: " + guid);
			}
		}
		
		
		
		// Build once for O(1) membership checks when filtering nested changed elements.
		Set<IRPModelElement> changedElementSet = new HashSet<IRPModelElement>(changedElements);

		for (IRPModelElement changedElement : changedElements)
		{
			boolean addToStack = true;
			List<IRPModelElement> nestedElements = changedElement.getNestedElements().toList();

			for (IRPModelElement nestedElement : nestedElements)
			{
				if (changedElementSet.contains(nestedElement))
				{
					addToStack = false;
					break;
				}
			}

			if (addToStack)
			{
				trace("Adding to changes stack: " + changedElement.getDisplayName());
				addToChangeHistory(changedElement);
			}
			else
			{
				trace("Skipped adding to changes stack (nested change): " + changedElement.getDisplayName());
			}
		}

		
		return true;
		
		
	}

	@Override
	public boolean afterAddElement(IRPModelElement pModelElement)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean afterProjectClose(String bstrProjectName)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean beforeProjectClose(IRPProject pProject)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getId()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onDiagramOpen(IRPDiagram pDiagram)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onDoubleClick(IRPModelElement pModelElement)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFeaturesOpen(IRPModelElement pModelElement)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onSelectionChanged()
	{
		if (myRhapsody == null)
		{
			return false;
		}
		if(isNavigating)
		{
			trace("Navigation in progress. Skip selection change.");
			isNavigating = false;
			return false;
		}

		IRPModelElement element = myRhapsody.getSelectedElement();
		
		addToHistory(element);

		return false;
	}
	
	private void addToHistory(IRPModelElement element)
	{
		if(push(myHistoryStack, element)==false)
		{
			return;
		}
		myForwardStack.clear();
		
		trace ("Added to history: " + element.getDisplayName() + " Back stack size: " + myHistoryStack.size());
		
	}
	
	private void addToChangeHistory(IRPModelElement element)
	{
		if(push(myLastChangesStack, element)==false)
		{
			return;
		}
		myLastChangesForwardStack.clear();
		
		trace ("Added to changes history: " + element.getDisplayName() + " Back stack size: " + myLastChangesStack.size());
	}
	
	public boolean afterProjectOpen(IRPProject project)
	{
		myProject = project;
		trace("Project opened: " + project.getName());
		return true;
	}
	
	public void showChangeHistory()
	{
		trace("Change History:");
		for(String guid : myLastChangesStack)
		{
			IRPModelElement element = myProject.findElementByGUID(guid);
			if(element != null)
			{
				trace(" - " + element.getDisplayName());
			}
			else
			{
				trace(" - Could not find element with GUID: " + guid);
			}
		}
	}

}
