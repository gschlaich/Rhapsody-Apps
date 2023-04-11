package apps;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

import com.ibm.rhapsody.apps.*;
import com.telelogic.rhapsody.core.*;


public class MainApp extends App {
	
	

	
	/*
	* This method is called on invoking an app inside Rhapsody.
	* rhapsody - Instance of an active Rhapsody application 
	* selected - Selected element in Rhapsody
	*/
@SuppressWarnings("unchecked")
public void execute(IRPApplication rhapsody, IRPModelElement selected) {
		
		
		//TODO Enter code here
	
		rhapsody.writeToOutputWindow("Log", "Move selected Package into the correct Repository folder\n");
		
		if(selected instanceof IRPPackage == false)
		{
			rhapsody.writeToOutputWindow("Log", "Selected element is not a Package - exit\n");
			return;
		}
		
		IRPUnit selectedUnit = selected.getSaveUnit();
		
		
		
		String currentDirectory = selectedUnit.getCurrentDirectory();
		if(currentDirectory.contains("UniversalSewingMachine_rpy")==true)
		{
			rhapsody.writeToOutputWindow("Log", "Unit is already part of the repository - exit\n");
			return;
		}
		
		String fileName = selectedUnit.getFilename();
		String unitName = selectedUnit.getName();

		Path sourceFile = FileSystems.getDefault().getPath(currentDirectory,fileName);
		Path source = sourceFile.getParent();
		
		Path sourceDesignView = source;

		Path f = sourceDesignView.getFileName();
		
		while(f.toString().equals("DesignView")==false)
		{
			sourceDesignView = sourceDesignView.getParent();
			f = sourceDesignView.getFileName();
		}
		
		Path relative = sourceDesignView.relativize(source);
		
		
		
		
		
		Path destination = FileSystems.getDefault().getPath("J:\\USM\\Development\\RhapsodyModel\\UniversalSewingMachine_rpy\\DesignView",relative.toString());
		Path destinationFile = FileSystems.getDefault().getPath(destination.toString(),fileName);
		
		System.out.println("Source: " + source.toString());
		System.out.println("SourceFile: " + sourceFile.toString());
		System.out.println("Destination: " +destination.toString());
		
		
		File destinationFolder = new File(destination.toString());
		if(destinationFolder.exists())
		{
			System.out.println("Folder already exists - exit");
			rhapsody.writeToOutputWindow("Log", "Folder already exists - exit\n");
			return;
		}
		
		destinationFolder.mkdir();
		
		

		try 
		{
				Files.copy(sourceFile, destinationFile);
		} 
		catch (IOException e) 
		{
			
			e.printStackTrace();
			rhapsody.writeToOutputWindow("Log", "Copy failed: " + e.getMessage() + "\n");
			return;
		}
		
		rhapsody.writeToOutputWindow("Log", "copied " + sourceFile.toString() + " to \n       " + destinationFile + "\n");
		
		//remove package from project
		
		rhapsody.writeToOutputWindow("Log", "Load Package from Repository.... ");
		
		IRPProject p = selected.getProject();
		
		if(p==null)
		{
			return;
		}
		
		IRPModelElement owner = selectedUnit.getOwner();
		selectedUnit.deleteFromProject();
		
		rhapsody.addToModelEx(destinationFile.toString(),IRPApplication.AddToModel_Mode.AS_UNIT_WITHOUT_COPY, 1, 0);
		
		rhapsody.writeToOutputWindow("Log", " ok\n");
		
		IRPModelElement addedElement = p.findElementByFileName(destination.toString(), fileName);
		
		System.out.println(addedElement.getName());
		
		if(addedElement instanceof IRPUnit == false)
		{
			System.out.println("addedElement is not unit");
			return;
		}
		
		
		IRPUnit addedUnit = (IRPUnit)addedElement;
		
		addedUnit.setOwner(owner);
		
		rhapsody.writeToOutputWindow("Log", "Moved Package " + addedUnit.getName() + " added to "+owner.getName()+"\n");
		
		
		
		List<IRPComponent> components = p.getComponents().toList();
		
		if(components.size()!=1)
		{
			rhapsody.writeToOutputWindow("Log", "Not 1 rootComponent\n");
			return;
		}
		
		for(IRPComponent c:components)
		{
			IRPComponent newComponent = c.addNestedComponent(unitName);
			newComponent.addStereotype("Library", "Component");
			newComponent.setBuildType("Library");
			newComponent.addScopeElement(addedUnit);
			IRPConfiguration config = newComponent.findConfiguration("DefaultConfig");
			if(config!=null)
			{
				config.setStatechartImplementation("Reuseable");
			}
			
			p.setActiveComponent(newComponent);		
			rhapsody.writeToOutputWindow("Log", "Created new Component " +newComponent.getName()+"\n");
			
		}
		
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
