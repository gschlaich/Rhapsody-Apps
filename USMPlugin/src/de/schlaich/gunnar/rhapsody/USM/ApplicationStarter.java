package de.schlaich.gunnar.rhapsody.USM;

import java.util.List;

import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.RhapsodyAppServer;

public class ApplicationStarter {

	public ApplicationStarter() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {

		// create an instance of my plug-in
		CUSMPlugin myVSPlugin = new CUSMPlugin();
		// get Rhapsody application that is currently running
		IRPApplication app = RhapsodyAppServer.getActiveRhapsodyApplication();
		
		if(app==null)
		{
			System.out.println("No Rhapsody Running! - exit!");
			
			List idList = RhapsodyAppServer.getActiveRhapsodyApplicationIDList();
		
			for(Object id : idList)
			{
				System.out.println("id: " + id.toString());
			}
			
			return;
		}
		
		// init the plug-in
		myVSPlugin.RhpPluginInit(app);
		// simulate a call to the plug-in
		myVSPlugin.RhpPluginInvokeItem();
		
		//myVSPlugin.OnMenuItemSelect(CUSMPlugin.PlantUmlCmd);
		myVSPlugin.OnMenuItemSelect(CUSMPlugin.RoundtripCmd);

	}

}
