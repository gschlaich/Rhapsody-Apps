package apps;

import com.telelogic.rhapsody.core.IRPAction;
import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPDiagram;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPOperation;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPState;
import com.telelogic.rhapsody.core.IRPTransition;
import com.telelogic.rhapsody.core.RPApplicationListener;

import de.schlaich.gunnar.rhapsody.operationeditor.OperationEditorWindow;
import de.schlaich.gunnar.rhapsody.utilities.RhapsodyPreferences;

public class Listener extends RPApplicationListener {
	
	private IRPApplication myApplication = null;
	private MainApp myMainApp = null;
	private String myProjectName = null;
	private IRPProject myProject = null;
	
	
	private RhapsodyPreferences myPrefs = null;
	

	public Listener(IRPApplication aApplication, MainApp aMainApp) {
		myApplication = aApplication;
		myMainApp = aMainApp;
		
		myProject = myApplication.activeProject();
		if(myProject != null)
		{
			myProjectName = myProject.getName();
		}
		
		myPrefs = RhapsodyPreferences.Get(true);
		
	}
	
	
	

	@Override
	public boolean afterAddElement(IRPModelElement pModelElement) {
		if(myPrefs.getStartEditorAfterElementAdded())
		{
			return startEditorThread(pModelElement);
		}
		return false;
	}
	
	

	@Override
	public boolean afterProjectClose(String bstrProjectName) {
		if(bstrProjectName.equals(myProjectName))
		{
			
			myPrefs.removeStarter();
			
			System.exit(0);
		}
		
		return true;
	}

	@Override
	public boolean beforeProjectClose(IRPProject pProject) {
		
		return false;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onDiagramOpen(IRPDiagram pDiagram) {
		// TODO Auto-generated method stub
		return false;
	}
	
	private boolean checkModel(IRPModelElement pModelElement)
	{
		if((pModelElement instanceof IRPOperation) == false)
		{
			if(pModelElement instanceof IRPTransition == false)
			{
				if(pModelElement instanceof IRPAction == false)
				{
					
					if(pModelElement instanceof IRPState == false)
					{
						return false;
					}
				}
			}
		}
		
		return true;
	}

	@Override
	public boolean onDoubleClick(IRPModelElement pModelElement) 
	{
		
		if(checkModel(pModelElement)==false)
		{
			return false;
		}
		
		if(myPrefs.getStartEditorOnDoubleClick())
		{
			return startEditorThread(pModelElement);
		}
		return false;
		
	}
	
	
	private boolean startEditorThread(IRPModelElement aModelElement)
	{
		
		Thread t = new Thread(new Runnable() {
		    public void run() {
		        
		        startEditor(aModelElement);
		    }
		});

		t.start(); // Thread starten

		return true;
	}
	
	

	private boolean startEditor(IRPModelElement aModelElement) {
		
		myApplication.writeToOutputWindow("log", "Start Editor on " + aModelElement.getName()+" Type: " + aModelElement.getMetaClass()+"\n");
		
		if(aModelElement instanceof IRPState)
		{
			IRPState state = (IRPState)aModelElement;
			IRPAction entryAction = state.getTheEntryAction();
			IRPAction exitAction = state.getTheExitAction();
			
			try
			{
				if(entryAction==null)
				{
					state.setEntryAction("");
				}
				entryAction.setName("entryAction");
				OperationEditorWindow.Run(myApplication, entryAction, false);
				
				if(exitAction==null)
				{
					state.setExitAction("");
				}
				exitAction.setName("exitAction");
				OperationEditorWindow.Run(myApplication, exitAction, false);
				
			}
			catch(Exception e)
			{
				
			}
		}
		
		if((aModelElement instanceof IRPOperation) == false)
		{
			if(aModelElement instanceof IRPTransition == false)
			{
				if(aModelElement instanceof IRPAction ==false)
				{
					return false;
				}
			}
		}
		
		
		
	
		try 
		{
			OperationEditorWindow.Run(myApplication, aModelElement, false);
			
		}
		catch (Exception ex)
		{
			
			
			ex.printStackTrace();
			
			StackTraceElement[] stes = ex.getStackTrace();
			
		
			for(StackTraceElement ste:stes)
			{
				myApplication.writeToOutputWindow("log", ste.toString()+"\n");
			}
			
			
			myApplication.writeToOutputWindow("log", ex.toString()+"\n");

		}
		
		
				
		return false;
	}

	@Override
	public boolean onFeaturesOpen(IRPModelElement pModelElement) {
		if(myPrefs.getStartEditorOnFeatureOpen())
		{
			startEditorThread(pModelElement);
		}
		return false;
	}

}
