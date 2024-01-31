package de.schlaich.gunnar.rhapsody.operationeditor;

import java.util.List;

import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPOperation;
import com.telelogic.rhapsody.core.IRPProfile;
import com.telelogic.rhapsody.core.IRPProject;

import de.schlaich.gunnar.rhapsody.ghs.MultiPlugin;
import de.schlaich.gunnar.rhapsody.vs.VSPlugin;

public class LoadInIDE
{

	private VSPlugin myVSPlugin = null;
	private MultiPlugin myMultiPlugin = null;
	
	private static LoadInIDE myLoadInIDE = null;
	
	public static LoadInIDE Instance(IRPApplication aApplication)
	{
		if(myLoadInIDE!=null)
		{
			return myLoadInIDE;
		}
		
		if(aApplication==null)
		{
			return null;
		}
		
		myLoadInIDE = new LoadInIDE(aApplication);
		
		return myLoadInIDE; 
			
	}
	
	private LoadInIDE(IRPApplication aApplication)
	{
		if(aApplication==null)
		{
			return;
		}
		IRPProject project = aApplication.activeProject();
		if(project==null)
		{
			return;
		}
		List<IRPProfile> profiles = project.getProfiles().toList();
		for(IRPProfile profile:profiles)
		{
			if(profile.getName().equals(VSPlugin.PROFILE_NAME))
			{
				myVSPlugin = new VSPlugin();
				myVSPlugin.RhpPluginInit(aApplication);
				break;
			}
			if(profile.getName().equals(MultiPlugin.PROFILE_NAME))
			{
				myMultiPlugin = new MultiPlugin();
				myMultiPlugin.RhpPluginInit(aApplication);
				break;
			}
		}
	}
	

	
	public boolean isMultiIde()
	{
		if(myMultiPlugin==null)
		{
			return false;
		}
		return true;
	}
	
	public boolean isVSIde()
	{
		if(myVSPlugin==null)
		{
			return false;
		}
		
		return true;
	}
	
	
	public void load(IRPOperation aOperation, boolean aDebugger)
	{
		if(myVSPlugin!=null)
		{
			myVSPlugin.viewInVisualStudio(aOperation);
			return;
		}
		if(myMultiPlugin!=null)
		{
			if(aDebugger)
			{
				myMultiPlugin.viewInDebugger(aOperation);
			}
			else
			{
				myMultiPlugin.viewInEditor(aOperation);
			}
		}
	}
	
	public void compile(IRPOperation aOperation)
	{
		if(myMultiPlugin!=null)
		{
			myMultiPlugin.compile(aOperation);
		}
	}
	
	public void build(IRPProject aProject)
	{
		if(myMultiPlugin!=null)
		{
			myMultiPlugin.compile(aProject);
		}
	}
	
	
}