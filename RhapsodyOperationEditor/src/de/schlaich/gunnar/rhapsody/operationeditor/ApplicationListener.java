package de.schlaich.gunnar.rhapsody.operationeditor;

import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import com.github.difflib.DiffUtils;
import com.github.difflib.algorithm.DiffException;
import com.github.difflib.patch.Patch;
import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPDiagram;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPOperation;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.RPApplicationListener;

import de.schlaich.gunnar.rhapsody.utilities.ASTHelper;

public class ApplicationListener extends RPApplicationListener {

	
	private JFrame myFrame;
	private IRPOperation myOperation;
	private IRPApplication myRhapsody;
	private IRPProject myProject;
	private RSyntaxTextArea myTextarea;
	private StartAutoCompletion myStartAutoCompletion;
	private boolean myExitOnClose = true;
	private boolean myAutomaticUpdate = false;
	
	
	 public ApplicationListener(JFrame aFrame, 
			 IRPOperation aOperation, 
			 IRPApplication aRhapsody, 
			 RSyntaxTextArea aTextarea, 
			 StartAutoCompletion aStartAutoCompletion,
			 boolean aExitOnClose) {
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
		myExitOnClose = aExitOnClose;
		
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
	
	
	public boolean textChanged()
	{
		List<String> editorLines = ASTHelper.getLines(myTextarea.getText());
		List<String> bodyLines = ASTHelper.getLines(myOperation.getBody());
		try 
		{
			if((bodyLines==null)||(editorLines==null))
			{
				return false;
			}
			Patch<String> patch = DiffUtils.diff(bodyLines, editorLines);
			return(patch.getDeltas().isEmpty()==false);
		} 
		catch (DiffException e) {
			
			e.printStackTrace();
		}
		
		return false;
		
		
	}


	@Override
	public boolean beforeProjectClose(IRPProject pProject) {
		if(myProject.equals(pProject))
		{
			if(textChanged())
			{
				int n = JOptionPane.showConfirmDialog(
					    null,
					    "Apply Changes on "+myOperation.getName()+"?",
					    "Project Close",
					    JOptionPane.YES_NO_OPTION);
				
				if(n==0)
				{
					myOperation.setBody(myTextarea.getText());
				}
			}
			
			myFrame.dispose();
			
			if(myExitOnClose)
			{
				System.exit(0);
			}
			
			
			
		}
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
			            	myFrame.requestFocus();
			            	myFrame.toFront();
			            	myFrame.setAlwaysOnTop(false);
			               
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
		
		//no automatic update...
		if(myAutomaticUpdate==false)
		{
			return false;
		}
		
		if(myOperation==null)
		{
			return false;
		}
		
		if (GUIDs.trim().length() == 0)
		{
			return true;
		}
		
		//start Thread...
		
		Thread elementChangeThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				
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
							return;
						}
						
						myRhapsody.writeToOutputWindow("log", "Operation \"" + myOperation.getImplementationSignature() + "\" changed!\n");
						
						myTextarea.setText(myOperation.getBody());
						myStartAutoCompletion.updateCompletionProvider();
						
						
						return;
					}
				}
				
				
			}
		});
		
		
		elementChangeThread.run();
	
		return false;
	}
	
}
