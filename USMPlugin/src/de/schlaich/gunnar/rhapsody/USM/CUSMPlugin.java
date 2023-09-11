package de.schlaich.gunnar.rhapsody.USM;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPComponent;
import com.telelogic.rhapsody.core.IRPConfiguration;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPOperation;
import com.telelogic.rhapsody.core.IRPProfile;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPUnit;
import com.telelogic.rhapsody.core.RPUserPlugin;

import de.schlaich.gunnar.rhapsody.plantUMLView.PlantUMLStarter;
import de.schlaich.gunnar.rhapsody.roundtrip.COperationalRoundtrip;
import de.schlaich.gunnar.rhapsody.utilities.ASTHelper;

public class CUSMPlugin extends RPUserPlugin {

	
	private IRPApplication myRhapsody = null;
	private IRPProfile myProfile = null;
	
	public static final String PlantUmlCmd = "PlantUML";
	public static final String RoundtripCmd = "Operational Roundtrip";
	
	public CUSMPlugin() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void RhpPluginInit(IRPApplication rpyApplication) {
		
		myRhapsody = rpyApplication;
		trace("started");
		
		
		
		
		IRPProject activeProject = myRhapsody.activeProject();
		
		if(activeProject==null)
		{
			trace("no active Project!");
			return;
		}
		
		
		
	}

	@Override
	public void RhpPluginInvokeItem() {
		
		

	}

	@Override
	public void OnMenuItemSelect(String menuItem) {
		
		
		IRPModelElement selected = myRhapsody.getSelectedElement();
		
		if(selected==null)
		{
			trace("no element selected");
			return;
		}
		
		if(menuItem.equals(PlantUmlCmd))
		{
			PlantUMLStarter.startPlantUML(myRhapsody, selected);
			return;
		}
		if(menuItem.equals(RoundtripCmd))
		{
			COperationalRoundtrip roundtrip = new COperationalRoundtrip();
			roundtrip.startRoundtrip(myRhapsody, selected);
		}

	}
	
	private void trace(String aMsg)
	{
		myRhapsody.writeToOutputWindow("Log", "USMPlugin: " + aMsg + "\n");
		System.out.println(aMsg);
	}
	
	
	@Override
	public void OnTrigger(String trigger) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean RhpPluginCleanup() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void RhpPluginFinalCleanup() {
		// TODO Auto-generated method stub

	}
	

}
