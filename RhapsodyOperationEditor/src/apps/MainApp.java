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
		
	}	
	
    /*
     *  Debug app by launching it as "Java Application" is Eclipse.
	 *  Note: Select an element in Rhapsody in order to simulate launching app on a selected element in the browser.
     */	
	public static void main(String[] args) {
		MainApp app = new MainApp();
		app.invokeFromMain();
	}		
}
