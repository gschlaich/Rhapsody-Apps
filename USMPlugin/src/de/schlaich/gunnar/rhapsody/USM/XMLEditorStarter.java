package de.schlaich.gunnar.rhapsody.USM;

import java.util.List;

import com.ibm.rhapsody.apps.*;
import com.telelogic.rhapsody.core.*;

import de.schlaich.gunnar.rhapsody.utilities.XmlEditor;


public class XMLEditorStarter extends App {
	
	

	
	/*
	* This method is called on invoking an app inside Rhapsody.
	* rhapsody - Instance of an active Rhapsody application 
	* selected - Selected element in Rhapsody
	*/
@SuppressWarnings("unchecked")
public void execute(IRPApplication rhapsody, IRPModelElement selected) {
		
	
		
		if(selected instanceof IRPHyperLink == false)
		{
			rhapsody.writeToOutputWindow("log", "Selected element is not a Hyperlink. Please select a Hyperlink and try again.\n");
			return;
		}	
		
		IRPHyperLink link = (IRPHyperLink) selected;
		XmlEditor.openHyperLink(link, rhapsody);
	
	
	}	
	
	
	
    /*
     *  Debug app by launching it as "Java Application" is Eclipse.
	 *  Note: Select an element in Rhapsody in order to simulate launching app on a selected element in the browser.
     */	
	public static void main(String[] args) {
		
		XMLEditorStarter app = new XMLEditorStarter();
		String connectionstring = null;
		
		if (args.length >= 1) 
		{
			connectionstring = args[0];
		}
		executeApp(app, connectionstring);
		
	}	
	
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
			actualApp.writeToOutputWindow("log", "ConnectiongString: " + aConnectionstring + "\n");
		}
		else
		{
			actualApp.writeToOutputWindow("log", "ConnectiongString was null\n");
		}
		
		actualApp.writeToOutputWindow("log", "start...\n");
		
		aApp.execute(actualApp, selectedElement);
	}
}
