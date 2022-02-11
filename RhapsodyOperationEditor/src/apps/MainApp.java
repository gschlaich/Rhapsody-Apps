package apps;



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
		MainApp app = new MainApp();
		app.invokeFromMain();
	}
	
	

	public final void onFinish(){
	    Preferences prefs;
	    prefs = Preferences.userRoot().node(this.getClass().getName());
	    prefs.remove(myGuid);
	}

	
	
	
	
	
	
}
