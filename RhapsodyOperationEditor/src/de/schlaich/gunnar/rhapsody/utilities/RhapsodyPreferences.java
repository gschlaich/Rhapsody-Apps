package de.schlaich.gunnar.rhapsody.utilities;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import com.telelogic.rhapsody.core.IRPModelElement;

public class RhapsodyPreferences {
	
	private Preferences myPrefs;
	private String myConnectingString = "";
	static RhapsodyPreferences myRhapsodyPreferences = null;
	
	public RhapsodyPreferences() {
		myPrefs = Preferences.userRoot().node(this.getClass().getName());
	}
	
	public void setConnectingString(String aConnectingString)
	{
		myConnectingString = aConnectingString;
	}
	
	private String setKey(String aGuid)
	{
		return myConnectingString+aGuid;
	}
	
	
	public void setRhapsodyModelElement(IRPModelElement aModelElement)
	{
		String guid = aModelElement.getGUID();
	
		myPrefs.putBoolean(setKey(guid), true);

	}
	
	public boolean checkRhapsodyModelElement(IRPModelElement aModelElement)
	{
		
		if(isDebug())
		{
			return false;
		}
		String guid = aModelElement.getGUID();
		return myPrefs.getBoolean(setKey(guid),false);
	}
	
	
	
	public void clearRhapsodyModelElement(IRPModelElement aModelElement)
	{
		String guid = aModelElement.getGUID();
		
		myPrefs.remove(setKey(guid));
		
	}
	
	public void clearRhapsodyModelElement(String aGuid)
	{
		myPrefs.remove(setKey(aGuid));
		
	}
	
	public void clear()
	{
		try {
			myPrefs.clear();
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
