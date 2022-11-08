package apps;

import com.ibm.rhapsody.apps.*;
import com.telelogic.rhapsody.core.*;


public class MainApp extends App {
	
	

	
	/*
	* This method is called on invoking an app inside Rhapsody.
	* rhapsody - Instance of an active Rhapsody application 
	* selected - Selected element in Rhapsody
	*/
	
	
	public void execute(IRPApplication rhapsody, IRPModelElement selected) {
		
		
		if((selected instanceof IRPDependency)==false)
		{
			return;
		}
		
		IRPUnit unit = selected.getSaveUnit();
		String componentName = unit.getName();
		IRPProject project = rhapsody.activeProject();
		
		IRPDependency dependency = (IRPDependency)selected;
		
		IRPModelElement dependsOn = dependency.getDependsOn();
		
		if(dependsOn==null)
		{
			return;
		}
		
		if((dependsOn instanceof IRPPackage) == false)
		{
			return;
		}
		
		IRPUnit dependsOnUnit = dependsOn.getSaveUnit();
		
		String dependsOnName = dependsOnUnit.getName();
		
		System.out.println("Depends: " + dependsOnName);
		
		IRPComponent dependsOnComponent = (IRPComponent)project.findNestedElementRecursive(dependsOnName, "Component");
		
		if(dependsOnComponent==null)
		{
			return;
		}
		
		IRPComponent component = (IRPComponent)project.findNestedElementRecursive(componentName, "Component");
		
		if(component==null)
		{
			return;
		}
		
		
		IRPDependency compDep = component.addDependencyTo(dependsOnComponent);
		
		if(compDep==null)
		{
			return;
		}
		
		compDep.addStereotype("Usage", "Dependency");
		rhapsody.writeToOutputWindow("log", "Added Dependency from " + componentName + "to" + dependsOnName + "\n");
	
	}	
    /*
     *  Debug app by launching it as "Java Application" is Eclipse.
	 *  Note: Select an element in Rhapsody in order to simulate launching app on a selected element in the browser.
     */	
	public static void main(String[] args) {
		
		MainApp app = new MainApp();
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
