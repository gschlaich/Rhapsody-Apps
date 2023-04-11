package de.schlaich.gunnar.rhapsody.utilities;

import java.util.ArrayList;
import java.util.List;

import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPOperation;

public class HistoryElement
{
	private static List<HistoryControl> HistoryControls = null;
	
	public static void RegisterControl(HistoryControl aHistoryControl)
	{
		if(HistoryControls==null)
		{
			HistoryControls = new ArrayList<HistoryControl>();
		}
		
		if(HistoryControls.contains(aHistoryControl))
		{
			return;
		}
		
		HistoryControls.add(aHistoryControl);
		
	}
	
	public static boolean UnregisterControl(HistoryControl aHistoryControl)
	{
		if(HistoryControls==null)
		{
			return false;
		}
		
		return HistoryControls.remove(aHistoryControl);
	}
		
		
	public static void AddToHistory( IRPModelElement aModelElement)
	{
		for(HistoryControl control:HistoryControls)
		{
			control.addToHistory(aModelElement);
		}
	}
	
	private IRPModelElement myElement = null;
	
	public HistoryElement(IRPModelElement aElement)
	{
		myElement = aElement;
	}
	
	@Override
	public String toString()
	{
		if(myElement == null)
		{
			return "Empty";
		}
		if(myElement instanceof IRPOperation)
		{
			return RhapsodyOperation.getOperation((IRPOperation)myElement);
		}
		
		return myElement.getDisplayName();
	}
	
	@Override
	public boolean equals(Object o)
	{
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        HistoryElement e = (HistoryElement)o;
        return e.getElement().equals(getElement());  
    }
	
	@Override
	public int hashCode() {
        return myElement.hashCode();
    }
	
	
	
	
	public IRPModelElement getElement()
	{
		return myElement;
	}
	
	
}