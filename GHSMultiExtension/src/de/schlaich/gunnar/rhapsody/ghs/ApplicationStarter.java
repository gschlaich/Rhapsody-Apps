package de.schlaich.gunnar.rhapsody.ghs;

import java.util.List;

import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.RhapsodyAppServer;



public class ApplicationStarter {

	public ApplicationStarter() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// create an instance of my plug-in
		MultiPlugin multiPlugin = new MultiPlugin();
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
		multiPlugin.RhpPluginInit(app);
		// simulate a call to the plug-in
		multiPlugin.RhpPluginInvokeItem();
		
		//multiPlugin.OnMenuItemSelect(MultiPlugin.VIEW_MULTI_DEBUGGER_CMD);
		
		if(app.getSelectedElement() instanceof IRPProject)
		{
			multiPlugin.OnMenuItemSelect(MultiPlugin.OPEN_MULTI_CMD);
		}
		else
		{
		
			multiPlugin.OnMenuItemSelect(MultiPlugin.VIEW_MULTI_EDITOR_CMD);
		}
		


	}

}
