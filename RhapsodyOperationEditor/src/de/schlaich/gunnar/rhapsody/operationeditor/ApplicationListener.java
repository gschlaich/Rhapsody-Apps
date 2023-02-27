package de.schlaich.gunnar.rhapsody.operationeditor;

import javax.swing.JFrame;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPDiagram;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPOperation;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.RPApplicationListener;

public class ApplicationListener extends RPApplicationListener {

	
	private JFrame myFrame;
	private IRPOperation myOperation;
	private IRPApplication myRhapsody;
	private IRPProject myProject;
	private RSyntaxTextArea myTextarea;
	private StartAutoCompletion myStartAutoCompletion;
	
	
	 public ApplicationListener(JFrame aFrame, 
			 IRPOperation aOperation, 
			 IRPApplication aRhapsody, 
			 RSyntaxTextArea aTextarea, 
			 StartAutoCompletion aStartAutoCompletion ) {
		myOperation = aOperation;
		
		myFrame = aFrame;
		
		myRhapsody = aRhapsody;
		myProject = myRhapsody.activeProject();
		if(myProject!=null)
		{
			myProject.setNotifyPluginOnElementsChanged(1);
		} 
		
		myTextarea = aTextarea;
		myStartAutoCompletion = aStartAutoCompletion;
		
	}
	
	
	@Override
	public boolean afterAddElement(IRPModelElement pModelElement) {
		
		if(pModelElement.getOwner().equals(myOperation)||pModelElement.getOwner().equals(myOperation.getOwner()))
		{
			System.out.println("something changed... recalculate?");
		}
		return false;
	}

	@Override
	public boolean afterProjectClose(String bstrProjectName) {
		// TODO Auto-generated method stub
		// TODO Close editor...
		
		return false;
	}

	@Override
	public boolean beforeProjectClose(IRPProject pProject) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getId() {
		return "OperationEditor";
	}

	@Override
	public boolean onDiagramOpen(IRPDiagram pDiagram) {
		// TODO Auto-generated method stub
		return false;
	}
	

	@Override
	public boolean onDoubleClick(IRPModelElement pModelElement) {
		
		if(pModelElement.equals(myOperation))
		{
			if(myFrame.getState()==java.awt.Frame.ICONIFIED)
			{
				myFrame.setState(java.awt.Frame.NORMAL);
			}
			
			myFrame.setAlwaysOnTop(true);
			new java.util.Timer().schedule( 
			        new java.util.TimerTask() {
			            @Override
			            public void run() {
			               myFrame.setAlwaysOnTop(false);
			               myFrame.toFront();
			            }
			        }, 
			        1000 
			);
			return true;
		
		}
		return false;
	}

	@Override
	public boolean onFeaturesOpen(IRPModelElement pModelElement) {
				
		return false;
		
	}

	
	/**
	 * the method that will be called upon "onElementsChanged" event
	 * occurs in Rhapsody. 
	 * Returned value is ignored.
	 * It will show a message with a list of the elements that were changed.
	 */  
	@Override
	public boolean onElementsChanged(String GUIDs) 
	{
		
		if(myOperation==null)
		{
			return false;
		}
		
		if (GUIDs.trim().length() == 0)
		{
			return true;
		}
	
		String[] GUIDsArray = GUIDs.split(",");
		
		
		for (int i = 0;  i < GUIDsArray.length; i++)
		{
			IRPModelElement currElement = myProject.findElementByGUID(GUIDsArray[i].trim());
			if(currElement==null)
			{
				continue;
			}
			
			if(currElement.equals(myOperation))
			{
				
				if(myTextarea.getText().equals(myOperation.getBody()))
				{
					return false;
				}
				
				myRhapsody.writeToOutputWindow("log", "Operation \"" + myOperation.getImplementationSignature() + "\" changed!\n");
				
				myTextarea.setText(myOperation.getBody());
				myStartAutoCompletion.updateCompletionProvider();
				
				
				return true;
			}
		}
		
		
		
		return false;
	}
	
}
