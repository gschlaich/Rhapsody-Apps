package de.schlaich.gunnar.rhapsody.utilities;

import java.util.prefs.Preferences;

import com.telelogic.rhapsody.core.IRPModelElement;

public class RhapsodyPreferences {
	
	Preferences myPrefs;
	static RhapsodyPreferences myRhapsodyPreferences = null;
	
	public RhapsodyPreferences() {
		myPrefs = Preferences.userRoot().node(this.getClass().getName());
	}
	
	public void setRhapsodyModelElement(IRPModelElement aModelElement)
	{
		String guid = aModelElement.getGUID();
	
		myPrefs.putBoolean(guid, true);

	}
	
	public boolean checkRhapsodyModelElement(IRPModelElement aModelElement)
	{
		
		if(isDebug())
		{
			return false;
		}
		String guid = aModelElement.getGUID();
		return myPrefs.getBoolean(guid,false);
	}
	
	
	public void clearRhapsodyModelElement(IRPModelElement aModelElement)
	{
		String guid = aModelElement.getGUID();
		
		myPrefs.remove(guid);
		
	}
	
	public void clearRhapsodyModelElement(String aGuid)
	{
		myPrefs.remove(aGuid);
	}
	
	private boolean isDebug()
	{
		 boolean ret = java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;
			 return ret;
	}
	
	
	public static RhapsodyPreferences Get()
	{
		if(myRhapsodyPreferences==null)
		{
			myRhapsodyPreferences = new RhapsodyPreferences();
		}
		
		return myRhapsodyPreferences;
		
	}

}
