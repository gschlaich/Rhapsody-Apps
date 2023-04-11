package apps;

import java.util.List;

import com.ibm.rhapsody.apps.*;
import com.telelogic.rhapsody.core.*;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.SpaceApi;
import io.swagger.client.model.Space;
import io.swagger.client.model.SpaceArray;



public class MainApp extends App {
	
	

	
	/*
	* This method is called on invoking an app inside Rhapsody.
	* rhapsody - Instance of an active Rhapsody application 
	* selected - Selected element in Rhapsody
	*/
@SuppressWarnings("unchecked")
public void execute(IRPApplication rhapsody, IRPModelElement selected) {
		
		
		//TODO Enter code here
	
		rhapsody.writeToOutputWindow("Log", "Template app. Enter code here!");
		
		String baseUrl = "https://confluence.bernina.com";
		String username = "";
		String password = "";
		
		JerseyJiraRestClientFactory factory = new JerseyJiraRestClientFactory();

		ApiClient apiClient = new ApiClient();
		//apiClient.setUsername(username);
	    //apiClient.setPassword(password);
	    apiClient.setDebugging(true);
	    apiClient.setBasePath(baseUrl);
	    
	 // Find my space
	    String MY_NAME = "Gunnar Schlaich";
	    SpaceApi spaceApi = new SpaceApi(apiClient);
	    SpaceArray spaceArray;
		try {
			spaceArray = spaceApi.getSpaces(
			    null,  // spaceKey
			    null,  // spaceId
			    null,  // type
			    null,  // status
			    null,  // label
			    null,  // favourite
			    null,  // favouriteUserKey
			    null,  // expand
			    0,     // start
			    1000);
		
	    List<Space> results = spaceArray.getResults();
	    Space mySpace = null;
	    for (Space space : results) {
	        if (space.getName().equals(MY_NAME)) {
	            mySpace = space;
	            break;
	        }
	    }
	    if (mySpace != null) {
	        System.out.println("Found space for " +
	            MY_NAME + ": " + mySpace.getKey());
	    }
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // limit

		
	
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
