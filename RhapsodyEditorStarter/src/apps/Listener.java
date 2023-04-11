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
			return startEditor(pModelElement);
		}
		return false;
	}
	
	

	@Override
	public boolean afterProjectClose(String bstrProjectName) {
		if(bstrProjectName.equals(myProjectName))
		{
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

	@Override
	public boolean onDoubleClick(IRPModelElement pModelElement) 
	{
		if(myPrefs.getStartEditorOnDoubleClick())
		{
			return startEditor(pModelElement);
		}
		return false;
		
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
				OperationEditorWindow.Run(myApplication, entryAction, false);
				
				
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
			startEditor(pModelElement);
		}
		return false;
	}

}
