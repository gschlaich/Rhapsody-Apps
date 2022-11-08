package apps;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import com.ibm.rhapsody.apps.*;
import com.telelogic.rhapsody.core.*;



public class MainApp extends App {
	
	String myScriptRunnerPath = "J:/Utilities/CSharpTools/Applications/ScriptRunner/bin/Release/ScriptRunner.exe";
	
	
	public void execute(IRPApplication rhapsody, IRPModelElement selected) 
	{
		
		String scriptPath = "";
		if(selected instanceof IRPControlledFile )
		{
			IRPControlledFile controlledFile = (IRPControlledFile)selected;
			IRPStereotype stereoType = controlledFile.getNewTermStereotype();
			
			if(stereoType == null)
			{
				rhapsody.writeToOutputWindow("Log", "has no new Term Stereotype - not a bcl file\n");
				return;
			}
			
			if(stereoType.getName().equals("BCLScript")==false)
			{
				rhapsody.writeToOutputWindow("Log", "wrong stereotype (" + stereoType.getName() + ")  - not a bcl file\n");
				return; 
			}
			
			rhapsody.writeToOutputWindow("Log", "Start scriptrunner with script " + controlledFile.getName()+"\n");
			
			scriptPath = controlledFile.getFullPathFileName();
			


		}
		else if(selected instanceof IRPHyperLink)
		{
			IRPHyperLink hyperLink = (IRPHyperLink)selected;
			IRPStereotype stereoType = hyperLink.getNewTermStereotype();
			if(stereoType==null)
			{
				rhapsody.writeToOutputWindow("Log", "has no new Term Stereotype - not a bcl file\n");
				return;
			}
			if(stereoType.getName().equals("BCLScriptExt")==false)
			{
				rhapsody.writeToOutputWindow("Log", "wrong stereotype (" + stereoType.getName() + ")  - not a bcl file\n");
				return; 
			}
			
			
			//check if hyperlink is an absolute path...
			rhapsody.writeToOutputWindow("Log","Working Directory = " + System.getProperty("user.dir") +"\n");
			rhapsody.writeToOutputWindow("Log","USM_ROOT = " + System.getenv("USM_ROOT") +"\n");
			
			String usm_root = System.getenv("USM_ROOT");
			scriptPath = hyperLink.getURL();
			if(scriptPath.startsWith(usm_root))
			{
				//we have an absolute path...
				rhapsody.writeToOutputWindow("Log","change from = " + scriptPath +"\n");
				scriptPath = "..\\.."+scriptPath.substring(usm_root.length());
				rhapsody.writeToOutputWindow("Log","To = " + scriptPath +"\n");
				if(hyperLink.getSaveUnit().isReadOnly()==0)
				{
					hyperLink.setURL(scriptPath);
				}

			}
	
			rhapsody.writeToOutputWindow("Log", "Start scriptrunner with script " + scriptPath +"\n");
			
			
		}
		else
		{
			return;
		}
		
		
		//call the scriptrunner...
		/*
		 * J:/Utilities/CSharpTools/Applications/ScriptRunner/bin/Release/ScriptRunner.exe script=j:\USM\Development\RhapsodyModel\UniversalSewingMachine_rpy\DesignView\HardwareDevices\HDInterfaces\WIFI\enableWifi.bcl
		 */
		String[] params = new String [2];
		
		params[0] = myScriptRunnerPath;
		params[1] = "script="+scriptPath;
		

		try 
		{
			
			Process p = Runtime.getRuntime().exec(params);
			
		} catch (IOException e) 
		{
			rhapsody.writeToOutputWindow("Log", "Exception: "+e.getMessage()+"\n"); 
		}
		
		
		
	}	
	
    /*
     *  Debug app by launching it as "Java Application" is Eclipse.
	 *  Note: Select an element in Rhapsody in order to simulate launching app on a selected element in the browser.
     */	
	public static void main(String[] args) 
	{
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
		
		//mainApp.setRhapsody(actualApp);
		//actualApp.executeCommand("RhpLocateinModelAction", null, null);
		//mainApp.invoke(selectedElement);
		aApp.execute(actualApp, selectedElement);
	}
}
