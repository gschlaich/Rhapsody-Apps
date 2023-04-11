package apps;



import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.jar.JarFile;
import java.util.prefs.Preferences;
import java.util.zip.ZipEntry;

import com.ibm.rhapsody.apps.*;
import com.telelogic.rhapsody.core.*;

import de.schlaich.gunnar.rhapsody.operationeditor.OperationEditorWindow;
import de.schlaich.gunnar.rhapsody.utilities.RhapsodyPreferences;


public class MainApp extends App {
	
	/*
	* This method is called on invoking an app inside Rhapsody.
	* rhapsody - Instance of an active Rhapsody application 
	* selected - Selected element in Rhapsody
	*/
	
	private String myGuid;
	
	
	public void execute(IRPApplication rhapsody, IRPModelElement selected) {
	
		
		Date compiledate = getClassBuildTime();
		
		if(compiledate!=null)
		{
			String timeStamp = new SimpleDateFormat("yyyy.MM.dd").format(compiledate);
			rhapsody.writeToOutputWindow("log", "\nOperationEditor Compile Date: " + timeStamp + "\n\n");
		}
		
		
		if((selected instanceof IRPOperation) == false)
		{
			if(selected instanceof IRPTransition == false)
			{
				if(selected instanceof IRPAction ==false)
				{
					return;
				}
			}
		}
	
		try 
		{
			OperationEditorWindow.Run(rhapsody, selected, true);
		}
		catch (Exception ex)
		{
			
			
			ex.printStackTrace();
			
			StackTraceElement[] stes = ex.getStackTrace();
			
			printToRhapsody("--------------------------- Exception occured: ");
			for(StackTraceElement ste:stes)
			{
				printToRhapsody(ste.toString());
			}
			
			
			printToRhapsody(ex.toString());
			printToRhapsody("--------------------------- Exception end");
			
			
		}
		
		
		
	}
	
	public void printToRhapsody(String aText)
	{
		println(aText);
		System.out.println(aText);
	}
	
	


	/*
     *  Debug app by launching it as "Java Application" is Eclipse.
	 *  Note: Select an element in Rhapsody in order to simulate launching app on a selected element in the browser.
     */	
	public static void main(String[] args) {
		

		String pid = "";
		
		String connectionstring = null;
		
		if (args.length >= 1) {
			connectionstring = args[0];

		}
		
		IRPApplication actualApp = null;
		
		if(connectionstring!=null)
		{
			try
			{
				actualApp = RhapsodyAppServer.getActiveRhapsodyApplicationByID(connectionstring);
			}
			catch(Exception e)
			{
				System.out.println("connectionstring "+ connectionstring + " is not an active rhapsody application ");
			}
		}
		else
		{
			System.out.println("no connectionstring set");
		}
		
		if(actualApp == null)
		{
		
			String name = ManagementFactory.getRuntimeMXBean().getName();
			RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
			
			String[] pidHost = name.split("@");
			if(pidHost.length==2)
			{
				pid = pidHost[0];
			}
			
			System.out.println("Pid: "+pid);
			
			List<String> applicationIDList = RhapsodyAppServer.getActiveRhapsodyApplicationIDList();
			
			List<IRPApplication> possibleApplications = new ArrayList<IRPApplication>();
			
			
			for(String applicationID : applicationIDList)
			{
				System.out.println("ApplicationID: "+applicationID);
				IRPApplication app = RhapsodyAppServer.getActiveRhapsodyApplicationByID(applicationID);
				String connectingString = app.getApplicationConnectionString();
				System.out.println("ConnectiongString: "+ connectingString);
				app.writeToOutputWindow("log", "Pid: "+pid+" ApplicationID: "+applicationID + "\n");
				System.out.println("Selected Element " + app.getSelectedElement().getClass().toString());
				System.out.println("Application Status: " + app.getApplicationStatus());
				
				if(app.getSelectedElement() instanceof IRPOperation)
				{
					possibleApplications.add(app);
					app.writeToOutputWindow("log", "Possible Operation: " + app.getSelectedElement().getName()+"\n" );
				}
				else if(app.getSelectedElement() instanceof IRPTransition)
				{
					possibleApplications.add(app);
					app.writeToOutputWindow("log", "Possible Transition: " + app.getSelectedElement().getName()+"\n" );
				}
				else if(app.getSelectedElement() instanceof IRPState)
				{
					possibleApplications.add(app);
					app.writeToOutputWindow("log", "Possible State: " + app.getSelectedElement().getName()+"\n" );
				}
				
			}
			
			if(possibleApplications.size()==0)
			{
				RhapsodyAppServer.getActiveRhapsodyApplication().writeToOutputWindow("log", "Could not start Editor. Wrong element selected\n");
			}
			
			if(possibleApplications.size()==1)
			{
				actualApp = possibleApplications.get(0);
			}
			
			else
			{
				for(IRPApplication app:possibleApplications)
				{
					if(app.getIsHiddenUI()==1)
					{
						//app without ui is never the selected app
						possibleApplications.remove(app);
						continue;
					}
					if(app.isRhapsodyCL()==1)
					{
						possibleApplications.remove(app);
						continue;
					}
					
					IRPInterfaceItem iitem = (IRPInterfaceItem) app.getSelectedElement();
					if(iitem==null)
					{
						break;
					}
					
					IRPModelElement owner = iitem.getOwner();
					
					
					
					
					IRPProject project = iitem.getProject();
					IRPComponent activeComponent = project.getActiveComponent();
					List<IRPModelElement> scopeElements =  activeComponent.getScopeElements().toList();
					for(IRPModelElement element:scopeElements)
					{
						if(owner.equals(element))
						{
							System.out.println("ScopeElement: " + element.getName());
							actualApp = app;
							break;
						}
						
					}
					
					if(actualApp!=null)
					{
						break;
					}
					
				}
			
				
				if(actualApp==null)
				{
					if(possibleApplications.size()>=1)
					{
						actualApp = possibleApplications.get(0);
					}
					
				}
				
			}
			
			
			if(actualApp==null)
			{
				actualApp = RhapsodyAppServer.getActiveRhapsodyApplication();
				if(actualApp==null)
				{
					System.out.println("No Rhapsody instance running");
					return;
				}
				actualApp.writeToOutputWindow("log", "Could not start Editor. Wrong element selected\n");
				return;
			}
		}
			
		
		
		MainApp mainApp = new MainApp();
		IRPModelElement selectedElement = actualApp.getSelectedElement();
		
		
		
		
		/*
		IRPProject project = actualApp.activeProject();
		if(project==null)
		{
			return;
		}
		IRPComponent activeComponent = project.getActiveComponent();
		if(activeComponent!=null)
		{
			actualApp.selectModelElements(null);
		}
		
		*/
		if(connectionstring!=null)
		{
			//actualApp.writeToOutputWindow("log", "ConnectiongString: " + connectionstring + "\n");
			
			RhapsodyPreferences prefs = RhapsodyPreferences.Get();
			prefs.setConnectingString(connectionstring);
			
		}
		else
		{
			actualApp.writeToOutputWindow("log", "ConnectiongString was null\n");
		}
		
		
		if(selectedElement instanceof IRPState)
		{
			String actionStr = null;
			if(args.length>1)
			{
				actionStr = args[1];
			}
			IRPState state = (IRPState)selectedElement;
			IRPAction action = null;
			
			
			
			if((actionStr!=null)&&(actionStr.equals("exit")))
			{
				action = state.getTheExitAction();
				 if(action==null)
				 {
						state.setExitAction("");
				 }
				 action = state.getTheExitAction();
				action.setName("onExit");
			}
			else
			{
				 action = state.getTheEntryAction();
				 if(action==null)
				 {
						state.setEntryAction("");
				 }
				 action = state.getTheEntryAction();
				 action.setName("onEntry");
				 
			}
			
			mainApp.execute(actualApp, action);
						
		}
		else
		{
		
		
			mainApp.execute(actualApp, selectedElement);
		}
		
		

		
		
		
	}
	
	

	public final void onFinish()
	{
	    Preferences prefs;
	    prefs = Preferences.userRoot().node(this.getClass().getName());
	    prefs.remove(myGuid);
	}
	
	
	/**
	 * Handles files, jar entries, and deployed jar entries in a zip file (EAR).
	 * @return The date if it can be determined, or null if not.
	 */
	private static Date getClassBuildTime() 
	{
	    Date d = null;
	    Class<?> currentClass = new Object() {}.getClass().getEnclosingClass();
	    URL resource = currentClass.getResource(currentClass.getSimpleName() + ".class");
	    if (resource == null) 
	    {
	    	return d;
	    }
	    if (resource.getProtocol().equals("file")) 
	    {
	    	try 
	    	{
	    		d = new Date(new File(resource.toURI()).lastModified());
	        } 
	    	catch (URISyntaxException ignored) 
	    	{ 
	    		return d;
	    	}
	    	
	    } 
	    else if (resource.getProtocol().equals("jar")) 
	    {
	    	String path = resource.getPath();
	        d = new Date( new File(path.substring(5, path.indexOf("!"))).lastModified() );    
	    } 
	    else if (resource.getProtocol().equals("zip")) 
	    {
	    	String path = resource.getPath();
	        File jarFileOnDisk = new File(path.substring(0, path.indexOf("!")));
	           
	        try(JarFile jf = new JarFile (jarFileOnDisk)) 
	        {
	        	ZipEntry ze = jf.getEntry (path.substring(path.indexOf("!") + 2));//Skip the ! and the /
	            long zeTimeLong = ze.getTime ();
	            Date zeTimeDate = new Date(zeTimeLong);
	            d = zeTimeDate;
	        } 
	        catch (IOException|RuntimeException ignored) 
	        {
	        	return d;
	        }
	    }  
	    return d;
	}

	
	
	
	
	
	
}
