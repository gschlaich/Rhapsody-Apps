

import java.util.List;
import javax.swing.JFrame;
import com.ibm.rhapsody.apps.*;
import com.telelogic.rhapsody.core.*;

import de.schlaich.gunnar.rhapsody.roundtrip.COperationalRoundtrip;
import de.schlaich.gunnar.rhapsody.utilities.RhapsodyHelper;

public class MainApp extends App {
	
	/*
	* This method is called on invoking an app inside Rhapsody.
	* rhapsody - Instance of an active Rhapsody application 
	* selected - Selected element in Rhapsody
	*/
	
	private static IRPClass myClass = null;
	private String myDiffResults;
	private static MainApp myApp;
	private static IRPProject myProject;
	private static JFrame myFrame;
	private static IRPComponent myActiveComponent;
	private List<IRPOperation> myChangedOperations;
	
	
	@SuppressWarnings("unchecked")
	public void execute(IRPApplication rhapsody, IRPModelElement selected) 
	{
		COperationalRoundtrip opRoundtrip = new COperationalRoundtrip();
		
		opRoundtrip.startRoundtrip(rhapsody, selected, true);

	}	
	
	
	
	
    /*
     *  Debug app by launching it as "Java Application" is Eclipse.
	 *  Note: Select an element in Rhapsody in order to simulate launching app on a selected element in the browser.
     */	
	public static void main(String[] args) {
		myApp = new MainApp();
		
		String connectionstring = null;
		
		if (args.length >= 1) 
		{
			connectionstring = args[0];
		}
		
		RhapsodyHelper.executeApp(myApp, connectionstring);
		
		
	}
	
	
	
	
}
