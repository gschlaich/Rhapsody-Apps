package apps;



import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;
import java.util.prefs.Preferences;

import com.ibm.rhapsody.apps.*;
import com.telelogic.rhapsody.core.*;

import de.schlaich.gunnar.rhapsody.operationeditor.OperationEditorWindow;


public class MainApp extends App {
	
	/*
	* This method is called on invoking an app inside Rhapsody.
	* rhapsody - Instance of an active Rhapsody application 
	* selected - Selected element in Rhapsody
	*/
	
	private String myGuid;
	
	public void execute(IRPApplication rhapsody, IRPModelElement selected) {
	
		
		if((selected instanceof IRPOperation) == false)
		{
			return;
		}
		
		//IRPApplication rhpApp = RhapsodyAppServer.getActiveRhapsodyApplicationByID(arg0)
		
		
		try 
		{
			OperationEditorWindow.Run(rhapsody, selected, this);
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
	}
	


	/*
     *  Debug app by launching it as "Java Application" is Eclipse.
	 *  Note: Select an element in Rhapsody in order to simulate launching app on a selected element in the browser.
     */	
	public static void main(String[] args) {
		
		String pid = "";
		
		String name = ManagementFactory.getRuntimeMXBean().getName();
		RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
		
		String[] pidHost = name.split("@");
		if(pidHost.length==2)
		{
			pid = pidHost[0];
		}
		
		System.out.println("Pid: "+pid);
		
		List<String> applicationIDList = RhapsodyAppServer.getActiveRhapsodyApplicationIDList();
		
		
		for(String applicationID : applicationIDList)
		{
			System.out.println("ApplicationID: "+applicationID);
			IRPApplication app = RhapsodyAppServer.getActiveRhapsodyApplicationByID(applicationID);
			String connectingString = app.getApplicationConnectionString();
			System.out.println("ConnectiongString: "+ connectingString);
			app.writeToOutputWindow("log", "Pid: "+pid+" ApplicationID: "+applicationID + "\n");
			System.out.println("Selected Element " + app.getSelectedElement().getClass().toString());
			System.out.println("Application Status: " + app.getApplicationStatus());
			
			
		}
		
		
	
		
		
		
		MainApp app = new MainApp();
		app.invokeFromMain();
		
		
		
		
	}
	
	

	public final void onFinish(){
	    Preferences prefs;
	    prefs = Preferences.userRoot().node(this.getClass().getName());
	    prefs.remove(myGuid);
	}

	
	
	
	
	
	
}
