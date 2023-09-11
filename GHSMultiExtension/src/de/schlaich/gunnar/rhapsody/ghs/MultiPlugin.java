package de.schlaich.gunnar.rhapsody.ghs;


import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPConfiguration;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPOperation;
import com.telelogic.rhapsody.core.IRPProfile;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.RPUserPlugin;
import com.telelogic.rhapsody.core.RhapsodyAppServer;

import de.schlaich.gunnar.rhapsody.utilities.ASTHelper;
import de.schlaich.gunnar.rhapsody.utilities.RhapsodyHelper;
import de.schlaich.gunnar.rhapsody.utilities.RhapsodyOperation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;

public class MultiPlugin extends RPUserPlugin {
	
	public static String PROFILE_NAME = "GHSMultiProfile";
	public static final String VIEW_MULTI_DEBUGGER_CMD = "View in Multi Debugger";
	public static final String VIEW_MULTI_EDITOR_CMD = "View in Multi Editor";
	public static final String OPEN_MULTI_CMD = "Open in GHS Multi";
	private String myCmd = "C:\\ghs\\multi_716\\mpythonrun";
	private String myMultiCmd = "C:\\ghs\\multi_716\\multi.exe";
	private String myArgsDebugView = " -s \"dw = winreg.GetDebugger()\" -s \"dw.RunCommands('e {0} ')\"";
	private String myArgDebugView1 = "\"dw = winreg.GetDebugger()\"";
	private String myArgDebugView2Begin = "\"dw.RunCommands('e ";
	private String myArgDebugView2End = "')\"";
	private String myArgEditView1 = "\"editor = GHS_Editor()\"";
	private String myArgEditView2Begin = "\"ew = editor.OpenFile('";
	private String myArgEditView2End = "')\"";
	private String myArgEditView3Begin = "\"ew.MoveCursor(";
	private String myArgEditView3End = ")\"";
	
	private IRPApplication myRhapsody = null;
	private IRPProfile myProfile = null;

	public MultiPlugin() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void RhpPluginInit(IRPApplication rpyApplication) {
		// TODO Auto-generated method stub
		
		myRhapsody = rpyApplication;
		trace("Start");

	}

	@Override
	public void RhpPluginInvokeItem() {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnMenuItemSelect(String menuItem) {
		// TODO Auto-generated method stub
		if(menuItem.equals(VIEW_MULTI_DEBUGGER_CMD))
		{
			viewInDebugger(null);
			return;
		}
		if(menuItem.equals(VIEW_MULTI_EDITOR_CMD))
		{
			viewInEditor(null);
			return;
		}
		if(menuItem.equals(OPEN_MULTI_CMD))
		{
			openGHSProject();
			return;
		}

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
	
	private void trace(String aMsg)
	{
		myRhapsody.writeToOutputWindow("Log", "MultiPlugin: " + aMsg+"\n");
		System.out.println("MultiPlugin: " + aMsg);
	}
	
	public void viewInDebugger(IRPOperation aOperation)
	{
		trace("Start viewInDebugger");
		String nameSpace = null;
		String component = null;
		IRPModelElement selected = aOperation;
		
		if(selected==null)
		{
			selected = myRhapsody.getSelectedElement();
		}
			
		
		nameSpace = RhapsodyOperation.getNamespace(selected);
		component = selected.getName();
		selected = selected.getOwner();
		
			
		while(selected instanceof IRPClass)
		{
			component = selected.getName()+"::"+component;
			selected = selected.getOwner();
		}
			
		if(nameSpace!=null)
		{
			component = nameSpace+"::"+component;
		}
		
		
		String arg2 = myArgDebugView2Begin+component+myArgDebugView2End;
		
		//build string...
		//String args = MessageFormat.format(myArgs, component);
		
		runCmd(myArgDebugView1,arg2,null);
	
		
	}

	private void runCmd(String aArgs1, String aArgs2, String aArgs3) 
	{
		if(aArgs3==null)
		{
			trace("run "+myCmd+" "+" -s "+aArgs1+" -s "+aArgs2);
		}
		else
		{
			trace("run "+myCmd+" "+" -s "+aArgs1+" -s "+aArgs2+ " -s "+ aArgs3);
		}
	
		try 
		{
			ProcessBuilder pb;
			if(aArgs3==null)
			{
				pb = new ProcessBuilder(myCmd, "-noconsole", "-s", aArgs1, "-s", aArgs2);
			}
			else
			{
				pb = new ProcessBuilder(myCmd, "-noconsole", "-s", aArgs1, "-s", aArgs2, "-s", aArgs3);
			}
			Process process = pb.start();
			InputStream inputStream = process.getInputStream();
			//InputStream errorStream = process.getErrorStream();
			
			BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream));
			//BufferedReader errrorReader = new BufferedReader(new InputStreamReader(errorStream));
			
			String inputLine;
			String errorLine;
			while((inputLine = inputReader.readLine()) != null)
			{
				trace(inputLine);
			}
			
			int exitCode = process.waitFor();
			trace("Exit Code: "+exitCode);
		} 
		catch (IOException | InterruptedException iox) 
		{
			trace("Exception: " + iox.getMessage());
			iox.printStackTrace();
		}
	}
	
	public void viewInEditor(IRPOperation aOperation)
	{
		trace("Start viewInEditor");
		IRPModelElement selected = aOperation;
		if(selected==null)
		{
			selected = myRhapsody.getSelectedElement();
		}
		if(selected instanceof IRPOperation == false)
		{
			trace("No Operation selected");
			return;
		}
		
		IRPOperation selectedOperation = (IRPOperation)selected;
		
		IRPModelElement selectedOwner = selectedOperation.getOwner();
		
		if (selectedOwner instanceof IRPClass == false) {
			trace("Owner is nat a class");
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
		
		String args2 = myArgEditView2Begin+path+myArgEditView2End;
		
		int line = ASTHelper.getSourceOffset(selectedOperation, myRhapsody);

		String args3 = myArgEditView3Begin+line+myArgEditView3End;
		
		runCmd(myArgEditView1, args2, args3);
		
	}
	
	private void openGHSProject()
	{
		IRPModelElement selected = myRhapsody.getSelectedElement();
		if(selected instanceof IRPProject == false)
		{
			trace("No Project selected");
			return;
		}
		
		IRPProject project = (IRPProject)selected;
		
		IRPConfiguration config = RhapsodyHelper.getProjectConfig(project, "DefaultConfig"); //which config?
		
		if(config==null)
		{
			trace("ProjectPath of "+project.getName()+" not found");
			return;
		}
		
		String projectEnding = "AppWorkspaceD9.gpj";
		if(config.getBuildSet().equals("Release"))
		{
			projectEnding = "AppWorkspaceR9.gpj";
		}
		
		String projectName = config.getDirectory(1, "")+"/"+project.getName()+projectEnding; //is there a better solution?
		
		File projectFile = new File(projectName);
		
		if(projectFile.exists()==false)
		{
			
			trace("could not find Project file in " + projectName);
			return;
			
		}
		
		//run multi...
		try 
		{
			ProcessBuilder pb  = new ProcessBuilder(myMultiCmd, projectName);
		
			pb.start(); //fire and forget

		} 
		catch (IOException iox) 
		{
			trace("Exception: " + iox.getMessage());
			iox.printStackTrace();
		}
		
		
	}

}
