package de.schlaich.gunnar.rhapsody.vs;

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

import de.schlaich.gunnar.rhapsody.utilities.ASTHelper;

public class VSPlugin extends RPUserPlugin {

	public static final String PROFILE_NAME = "VSProfile";
	private IRPApplication myRhapsody = null;
	private IRPProfile myProfile = null;
	private String myStartupDirectory = null;
	private String mySolutionPath = null;
	
	public VSPlugin() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void RhpPluginInit(IRPApplication rpyApplication) {
		
		myRhapsody = rpyApplication;
		trace("VS Plugin started");
		
		
		
		
		IRPProject activeProject = myRhapsody.activeProject();
		
		if(activeProject==null)
		{
			trace("no active Project!");
			return;
		}
		
		IRPUnit projectUnit = activeProject.getSaveUnit();
		
		String projectPath = projectUnit.getCurrentDirectory();
		
		List<IRPComponent> components = activeProject.getComponents().toList();
		for(IRPComponent component : components)
		{
			trace(component.getBuildType());
			if(component.getBuildType().equals("executable"))
			{
				List<IRPConfiguration> configs =  component.getConfigurations().toList();
				for(IRPConfiguration config:configs)
				{
					if(config.getName().equals("DefaultConfig"))
					{
						mySolutionPath = config.getDirectory(1,"");
						mySolutionPath = mySolutionPath+"/"+activeProject.getName()+"AppWorkspace.sln"; //is there a better solution?
						
						break;
					}
				}
				break;
			}
		}
		
		
		//search for profile
		List<IRPProfile> profiles = activeProject.getProfiles().toList();
		
		for(IRPProfile profile:profiles)
		{
			if(profile.getName().equals(PROFILE_NAME))
			{
				myProfile = profile;
				break;
			}
		}
		
		if(myProfile==null)
		{
			trace("Profile " + PROFILE_NAME + " not found");
			return;	
		}
		
		IRPUnit profileUnit = myProfile.getSaveUnit();
		myStartupDirectory = profileUnit.getCurrentDirectory();
		
	}

	@Override
	public void RhpPluginInvokeItem() {
		
		

	}

	@Override
	public void OnMenuItemSelect(String menuItem) {
		
		
		if(menuItem.equals("View in Visual Studio"))
		{
			viewInVisualStudio(null);
		}

	}
	
	private void trace(String aMsg)
	{
		myRhapsody.writeToOutputWindow("Log", "VisualStudioExtendion: " + aMsg + "\n");
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
	
	public void viewInVisualStudio(IRPOperation aOperation)
	{
		
		trace("View in Visual Studio 2");

		IRPOperation selectedOperation = aOperation;
		
		if(selectedOperation==null)
		{
			IRPModelElement selected = myRhapsody.getSelectedElement();
			
			//operation...
			if (selected instanceof IRPOperation == false) 
			{
				return;
			}
	
			selectedOperation = (IRPOperation) selected;
		}
		IRPModelElement selectedOwner = selectedOperation.getOwner();
		
		if (selectedOwner instanceof IRPClass == false) {
			return;
		}

		IRPClass selectedClass = (IRPClass) selectedOwner;
		String path = ASTHelper.getSourcePath(selectedClass, myRhapsody);
		
		if((selectedOperation.isATemplate()==1)||(selectedOperation.getIsInline()==1))
		{
			path = path+".h";
		}
		else
		{
			path = path+".cpp";
		}
		
		path = path.replace('/', '\\');
		
		String cmd = myStartupDirectory+"\\VSControl.exe";

		int offset = ASTHelper.getSourceOffset(selectedOperation, myRhapsody);

		
		trace(path + " " + offset);
		
		mySolutionPath = mySolutionPath.replace('/', '\\');
		
		trace("SolutionPath: "+ mySolutionPath);
		
		
		try 
		{
			ProcessBuilder pb = new ProcessBuilder(cmd, path, Integer.toString(offset), mySolutionPath);
			Process process = pb.start();
			InputStream inputStream = process.getInputStream();
			//InputStream errorStream = process.getErrorStream();
			
			BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream));
			//BufferedReader errrorReader = new BufferedReader(new InputStreamReader(errorStream));
			
			String inputLine;
			String errorLine;
			while((inputLine = inputReader.readLine()) != null)
			{
				trace("VSControl.exe: " + inputLine);
			}
			
			int exitCode = process.waitFor();
			trace("VSControl Exit Code: "+exitCode);
		} 
		catch (IOException | InterruptedException iox) 
		{
			trace("Exception: " + iox.getMessage());
			iox.printStackTrace();
		}

	}


}
