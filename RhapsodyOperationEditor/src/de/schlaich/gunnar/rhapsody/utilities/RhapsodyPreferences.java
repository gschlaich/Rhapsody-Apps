package de.schlaich.gunnar.rhapsody.utilities;

import java.util.HashSet;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import com.telelogic.rhapsody.core.IRPModelElement;

public class RhapsodyPreferences {
	
	private Preferences myPrefs = null;
	private String myConnectingString = "";
	static RhapsodyPreferences myRhapsodyPreferences = null;
	private boolean myUseLocalGUID = false;
	private Set<String> myGUIDSet = null;
	
	
	
	
	public RhapsodyPreferences(boolean aUseLocalGUID)
	{
		myPrefs = Preferences.userRoot().node(this.getClass().getName());
		
		if(aUseLocalGUID)
		{
			setUseLocalGUID();
		}

		
	}
	
	public void setUseLocalGUID()
	{
		myUseLocalGUID = true;
		
		if(myGUIDSet==null)
		{
			myGUIDSet = new HashSet<String>();
		}
	}
	
	public boolean isStarted(String aConnectingString)
	{
		myConnectingString =aConnectingString;
		
		boolean ret = myPrefs.getBoolean("Starter_" + aConnectingString, false);
		
		if(isDebug())
		{
			return false;
		}
		
		if(ret==false)
		{
			myPrefs.putBoolean("Starter_"+ aConnectingString, true);
		}
		return ret;
	}
	
	@Override
	protected void finalize() throws Throwable {
		
		if(myConnectingString!=null)
		{
			removeStarter();
		}
		
		super.finalize();
	}
	
	public void removeStarter()
	{
		if(myConnectingString!=null)
		{
			myPrefs.remove("Starter_"+myConnectingString);
			myConnectingString = null;
		}
	}
	
	public void setConnectingString(String aConnectingString)
	{
		myConnectingString = aConnectingString;
	}
	
	private String setKey(String aGuid)
	{
		return myConnectingString+aGuid;
	}
	
	public void setGPTApiKey(String aApiKey)
	{
		myPrefs.put("GPTApiKey", aApiKey);
	}
	
	public String getGPTApiKey()
	{
		return myPrefs.get("GPTApiKey", null);
	}
	
	public boolean getStartEditorOnDoubleClick()
	{
		return myPrefs.getBoolean("StartEditorOnDoubleClick", true);
	}
	
	public void setStartEditorOnDoubleClick(boolean aStart)
	{
		myPrefs.putBoolean("StartEditorOnDoubleClick", aStart);
	}
	
	public boolean getStartEditorOnFeatureOpen()
	{
		return myPrefs.getBoolean("StartEditorOnFeaturesOpen", false);
	}
	
	public void setStartEditorOnFeatureOpen(boolean aStart)
	{
		myPrefs.putBoolean("StartEditorOnFeaturesOpen", aStart);
	}
	
	public boolean getStartEditorAfterElementAdded()
	{
		return myPrefs.getBoolean("StartEditorAfterElementAdded", false);
	}
	
	public void setStartEditorAfterElementAdded(boolean aStart)
	{
		myPrefs.putBoolean("StartEditorAfterElementAdded", aStart);
	}
	
	public boolean getEditorOnStartup()
	{
		return myPrefs.getBoolean("GetEditorOnStart", false);
	}
	
	public void setEditorOnStartup(boolean onStart)
	{
		myPrefs.putBoolean("GetEditorOnStart", onStart);
	}

	public void setRhapsodyModelElement(IRPModelElement aModelElement)
	{
		String guid = aModelElement.getGUID();
		if(myUseLocalGUID)
		{
			myGUIDSet.add(guid);
		}
		myPrefs.putBoolean(setKey(guid), true);

	}
	
	public boolean checkRhapsodyModelElement(IRPModelElement aModelElement)
	{
		
		if(isDebug())
		{
			return false;
		}
		String guid = aModelElement.getGUID();
		if(myUseLocalGUID)
		{
			return myGUIDSet.contains(guid);
		}
		return myPrefs.getBoolean(setKey(guid),false);
	}
	
	
	
	
	
	public void clearRhapsodyModelElement(IRPModelElement aModelElement)
	{
		String guid = aModelElement.getGUID();
		
		if(myUseLocalGUID)
		{
			myGUIDSet.remove(guid);
			return;
		}
		
		myPrefs.remove(setKey(guid));
		return;
		
	}
	
	public void clearRhapsodyModelElement(String aGuid)
	{
		if(myUseLocalGUID)
		{
			myGUIDSet.remove(aGuid);
			return;
		}
		myPrefs.remove(setKey(aGuid));
		
	}
	
	public void clear()
	{
		String gptApiKey = getGPTApiKey();
		
		try {
			myPrefs.clear();
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//restore API key
		setGPTApiKey(gptApiKey);
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
			myRhapsodyPreferences = new RhapsodyPreferences(false);
		}
		
		return myRhapsodyPreferences;
		
	}
	
	
	
	public static RhapsodyPreferences Get(boolean aUseLocalGUID)
	{
		if(myRhapsodyPreferences==null)
		{
			myRhapsodyPreferences = new RhapsodyPreferences(aUseLocalGUID);	
		}
		else
		{
			if(aUseLocalGUID)
			{
				myRhapsodyPreferences.setUseLocalGUID();
			}
		}
		return myRhapsodyPreferences;
				
	}

}
