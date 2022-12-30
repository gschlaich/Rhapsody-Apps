package de.schlaich.gunnar.rhapsody.utilities;

import com.ibm.rhapsody.apps.App;
import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPComponent;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.RhapsodyAppServer;

public class RhapsodyHelper 
{
	public static void executeApp(App aApp, String aConnectionstring)
	{
		IRPApplication actualApp = null;
		
		if(aConnectionstring!=null)
		{
			try
			{
				actualApp = RhapsodyAppServer.getActiveRhapsodyApplicationByID(aConnectionstring);
			}
			catch(Exception e)
			{
				System.out.println("connectionstring "+ aConnectionstring + " is not an active rhapsody application ");
			}
		}
		else
		{
			System.out.println("no connectionstring set");
		}
		
		if(actualApp==null)
		{
        
			aApp.invokeFromMain();
			return;
		}
		
		IRPModelElement selectedElement = actualApp.getSelectedElement();
		
		
		if(aConnectionstring!=null)
		{
			//actualApp.writeToOutputWindow("log", "ConnectiongString: " + aConnectionstring + "\n");
		}
		else
		{
			actualApp.writeToOutputWindow("log", "ConnectiongString was null\n");
		}
		
		actualApp.writeToOutputWindow("log", "start...\n");
		
		//mainApp.setRhapsody(actualApp);
		//actualApp.executeCommand("RhpLocateinModelAction", null, null);
		//mainApp.invoke(selectedElement);
		aApp.execute(actualApp, selectedElement);
	}
	
	/*
	 * Sets the Component which has scope of aElement active. 
	 * Works only when package and component has the same name. (USM)
	 * 
	 */
	public static boolean setActive(IRPModelElement aSelectedElement, IRPApplication aRhapsody)
	{
		
		IRPModelElement selectedElement = aSelectedElement;
		IRPProject project = aRhapsody.activeProject();
		if(project==null)
		{
			return false;
		}
		while((selectedElement instanceof IRPPackage)==false)
		{
			selectedElement = selectedElement.getOwner();
			if(selectedElement==null)
			{
				return false;
			}
		}
		
		IRPPackage selectedPackage = (IRPPackage)selectedElement;
		
		IRPComponent selectedComponent = null;
		
		while(selectedComponent==null)
		{
			selectedComponent = (IRPComponent)project.findNestedElementRecursive(selectedPackage.getName(), "Component");
			if(selectedComponent==null)
			{
				selectedElement = selectedPackage.getOwner();
				if(selectedElement==null)
				{
					return false;
				}
				if((selectedElement instanceof IRPPackage)==false)
				{
					return false;
				}
				selectedPackage = (IRPPackage)selectedElement;
			}
		}
		
		if(project.getActiveComponent().equals(selectedComponent))
		{
			return true;
		}
		
		project.setActiveComponent(selectedComponent);
		return true;
		
	}

}
