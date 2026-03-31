package de.schlaich.gunnar.rhapsody.utilities;

import java.util.ArrayList;
import java.util.List;
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
	
	private List<IRPModelElement> myPushHistory = new ArrayList<IRPModelElement>();
	private List<IRPModelElement> myPopHistory = new ArrayList<IRPModelElement>();
	
	private Stack<IRPModelElement> myHistoryStack = new Stack<IRPModelElement>();
	private Stack<IRPModelElement> myForwardStack = new Stack<IRPModelElement>();

	public SelectionHistory(Consumer<String> aTraceAction, IRPApplication aRhapsody)
	{
		myRhapsody = aRhapsody;
		myTraceAction = aTraceAction;
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
		
		if(myForwardStack.isEmpty())
		{
			trace("Forward stack is empty");
			return;
		}
		
		IRPCollection selection = myRhapsody.createNewCollection();
		
		IRPModelElement element = myForwardStack.pop();
		
		if(element==null)
		{
			return;
		}
		selection.addItem(element);
		trace(" Next: Selected: " + element.getDisplayName());
		myRhapsody.selectModelElements(selection);
	}
	
	public void back()
	{
		if(myHistoryStack.isEmpty())
		{
			trace("Back stack is empty");
			return;
		}
		IRPCollection selection = myRhapsody.createNewCollection();
		IRPModelElement element = myHistoryStack.pop();
		element = myHistoryStack.pop();
		if(element==null)
		{
			return;
		}
		myForwardStack.push(element);
		selection.addItem(element);
		trace("Back: Selected: " + element.getDisplayName());
		myRhapsody.selectModelElements(selection);
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

		IRPModelElement element = myRhapsody.getSelectedElement();
		
		if(myHistoryStack.isEmpty()==false)
		{
			IRPModelElement top = myHistoryStack.peek();
			if(top.equals(element)==false)
			{
				addToHistory(element);
			}
		}
		else
		{
			addToHistory(element);
		}

		return false;
	}
	
	private void addToHistory(IRPModelElement element)
	{
		myHistoryStack.push(element);
		myForwardStack.clear();
		trace("Selected: " + element.getDisplayName() + "Stack size: " + myHistoryStack.size());
	}

}
