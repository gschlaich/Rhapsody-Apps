package de.schlaich.gunnar.rhapsody.ghs;

import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPAttribute;
import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPCollection;
import com.telelogic.rhapsody.core.IRPComment;
import com.telelogic.rhapsody.core.IRPComponent;
import com.telelogic.rhapsody.core.IRPConfiguration;
import com.telelogic.rhapsody.core.IRPDependency;
import com.telelogic.rhapsody.core.IRPFile;
import com.telelogic.rhapsody.core.IRPHyperLink;
import com.telelogic.rhapsody.core.IRPLink;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPOperation;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPProfile;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPRequirement;
import com.telelogic.rhapsody.core.IRPStereotype;
import com.telelogic.rhapsody.core.IRPTableView;
import com.telelogic.rhapsody.core.IRPTag;
import com.telelogic.rhapsody.core.IRPUnit;
import com.telelogic.rhapsody.core.RPUserPlugin;
import com.telelogic.rhapsody.core.RhapsodyAppServer;

//import de.schlaich.gunnar.rhapsody.USM.CUSMPlugin;
import de.schlaich.gunnar.rhapsody.utilities.ASTHelper;
import de.schlaich.gunnar.rhapsody.utilities.RhapsodyHelper;
import de.schlaich.gunnar.rhapsody.utilities.RhapsodyOperation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

public class MultiPlugin extends RPUserPlugin
{

	public static String PROFILE_NAME = "GHSMultiProfile";
	public static final String VIEW_MULTI_DEBUGGER_CMD = "View in Multi Debugger";
	public static final String VIEW_MULTI_EDITOR_CMD = "View in Multi Editor";
	public static final String OPEN_MULTI_CMD = "Open in GHS Multi";
	public static final String COMPILE_MULTI_CMD = "Compile in GHS Multi";
	public static final String VIEW_MULTI_RHAPSODY_CMD = "View in Rhapsody";
	public static final String SET_BREAKPOINT_CMD = "Set Breakpoint";
	
	// Ozone Commands
	public static final String OZONE_CONNECT_CMD = "Ozone Connect";
	public static final String OZONE_DISCONNECT_CMD = "Ozone Disconnect";
	public static final String OZONE_VIEW_CMD = "View in Ozone";
	public static final String OZONE_SET_BREAKPOINT_CMD = "Ozone Set Breakpoint";
	public static final String OZONE_DELETE_BREAKPOINT_CMD = "Ozone Delete Breakpoint";
	public static final String OZONE_DELETE_ALL_BREAKPOINTS_CMD = "Ozone Delete All Breakpoints";
	public static final String OZONE_START_CMD = "Start Ozone";

	private static final String CompilerIssue = "CompilerIssue";
	private static final String BREAK_POINT_META_NAME = "BreakPoint";
	private static final String BREAK_POINT_COLLECTION_META_NAME = "BreakPointCollection";

	//environment variables
	
	//private String myGhsPath = "C:\\ghs\\multi_814\\";
	private String myCmd = "mpythonrun";
	private String myMultiCmd = "multi.exe";
	private String myArgsDebugView = " -s \"dw = winreg.GetDebugger()\" -s \"dw.RunCommands('e {0} ')\"";
	private String myArgDebugView1 = "\"dw = winreg.GetDebugger()\"";
	private String myArgDebugView2Begin = "\"dw.RunCommands('e ";
	private String myArgSetBreakPoint2Begin = "\"dw.RunCommands('b ";
	private String myArgDebugView2End = "')\"";
	private String myArgEditView1 = "\"editor = GHS_Editor()\"";
	private String myArgEditView2Begin = "\"ew = editor.OpenFile('";
	private String myArgEditView2End = "')\"";
	private String myArgEditView3Begin = "\"ew.MoveCursor(";
	private String myArgEditView3End = ")\"";
	private String myArgCompile1 = "\"pw = winreg.GetProjectManagerWindow()\"";
	private String myArgCompile2Begin = "\"pw.BuildFile('";
	private String myArgCompile2End = "')\"";
	// private String myArgCompile3 = "\"pw.DumpTextFieldValue('tv_status')\"";
	private String myArgCompile3 = "\"w = winreg.GetWindowByName('Build Details')\"";
	private String myArgCompile4 = "\"w.DumpWidget('tv_messages')\"";
	private String myArgBuldAll = "\"pw.BuildProj(False,True)\"";

	private IRPApplication myRhapsody = null;
	private IRPProfile myProfile = null;
	private OzoneSupport myOzoneSupport = null;

	public MultiPlugin()
	{
		// TODO Auto-generated constructor stub
	}
	
	private String getBuildDate()
	{
		try
		{
			String jarPath = MultiPlugin.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			String decodedPath = URLDecoder.decode(jarPath, "UTF-8");
			File jarFile = new File(decodedPath);
			if (jarFile.exists())
			{
				long lastModified = jarFile.lastModified();
				Date date = new Date(lastModified);
				return date.toString();
			}
			else
			{
				return "JAR-File not found";
			}
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
			return "Error while getting build date";
		}
	}


	@Override
	public void RhpPluginInit(IRPApplication rpyApplication)
	{
		// TODO Auto-generated method stub

		myRhapsody = rpyApplication;
		trace("Start");
		trace("Build Date: " + getBuildDate());
		
		ASTHelper.setTraceAction(this::trace);

	}

	@Override
	public void RhpPluginInvokeItem()
	{
		// TODO Auto-generated method stub

	}
	
	
	private boolean setMyCmd()
	{
		String ghsPathRunning = getGhsPath();
		if (ghsPathRunning == null || ghsPathRunning.isEmpty())
		{
			trace("Multi not running");
			return false;
		}
		
		//myGhsPath = ghsPathRunning;
		//myCmd = myGhsPath + "mpythonrun";
		//myMultiCmd = myGhsPath + "multi.exe";
		return true;
		
	}

	@Override
	public void OnMenuItemSelect(String menuItem)
	{

		
		
		IRPModelElement selected = myRhapsody.getSelectedElement();

		if (menuItem.equals(VIEW_MULTI_DEBUGGER_CMD))
		{
			viewInDebugger(selected);
			return;
		}
		if (menuItem.equals(VIEW_MULTI_EDITOR_CMD))
		{
			viewInEditor(null);
			return;
		}
		if (menuItem.equals(OPEN_MULTI_CMD))
		{
			openGHSProject();
			return;
		}
		if (menuItem.equals(COMPILE_MULTI_CMD))
		{
			compile(null);
			return;
		}
		if (menuItem.equals(VIEW_MULTI_RHAPSODY_CMD))
		{
			viewInRhapsody();
			return;
		}
		if (menuItem.equals(SET_BREAKPOINT_CMD))
		{
			setBreakPoint(selected);
			return;
		}
		
		// Ozone Commands
		if (menuItem.equals(OZONE_CONNECT_CMD))
		{
			ozoneConnect();
			return;
		}
		if (menuItem.equals(OZONE_DISCONNECT_CMD))
		{
			ozoneDisconnect();
			return;
		}
		if (menuItem.equals(OZONE_VIEW_CMD))
		{
			ozoneView(selected);
			return;
		}
		if (menuItem.equals(OZONE_SET_BREAKPOINT_CMD))
		{
			ozoneSetBreakpoint(selected);
			return;
		}
		if (menuItem.equals(OZONE_DELETE_BREAKPOINT_CMD))
		{
			ozoneDeleteBreakpoint(selected);
			return;
		}
		if (menuItem.equals(OZONE_DELETE_ALL_BREAKPOINTS_CMD))
		{
			ozoneDeleteAllBreakpoints();
			return;
		}
		if (menuItem.equals(OZONE_START_CMD))
		{
			ozoneStart();
			return;
		}
		
		trace("Unknown Command: \"" + menuItem + "\"");

	}

	@Override
	public void OnTrigger(String trigger)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean RhpPluginCleanup()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void RhpPluginFinalCleanup()
	{
		// TODO Auto-generated method stub

	}

	private void trace(String aMsg)
	{
		myRhapsody.writeToOutputWindow("Log", "MultiPlugin: " + aMsg + "\n");
		System.out.println("MultiPlugin: " + aMsg);
	}
	
	// ==================== Ozone Methods ====================
	
	/**
	 * Connect to Ozone debugger
	 */
	private void ozoneConnect()
	{
		trace("Connecting to Ozone...");
		
		if (myOzoneSupport != null && myOzoneSupport.isConnected())
		{
			trace("Already connected to Ozone");
			return;
		}
		
		if (myOzoneSupport == null)
		{
			myOzoneSupport = new OzoneSupport(myRhapsody, this::trace);
		}
		
		if (myOzoneSupport.connect())
		{
			trace("Successfully connected to Ozone");
		}
		else
		{
			trace("Failed to connect to Ozone - make sure Ozone is running with telnet server enabled");
			myOzoneSupport = null;
		}
	}
	
	/**
	 * Disconnect from Ozone debugger
	 */
	private void ozoneDisconnect()
	{
		trace("Disconnecting from Ozone...");
		
		if (myOzoneSupport == null)
		{
			trace("Not connected to Ozone");
			return;
		}
		
		myOzoneSupport.disconnect();
		myOzoneSupport = null;
		trace("Disconnected from Ozone");
	}
	
	/**
	 * View the selected operation in Ozone debugger
	 */
	private void ozoneView(IRPModelElement aElement)
	{
		trace("View in Ozone: " + (aElement != null ? aElement.getName() : "null"));
		
		if (myOzoneSupport == null || !myOzoneSupport.isConnected())
		{
			trace("Not connected to Ozone - connecting now...");
			ozoneConnect();
			
			if (myOzoneSupport == null || !myOzoneSupport.isConnected())
			{
				trace("Could not connect to Ozone");
				return;
			}
		}
		
		if (aElement == null)
		{
			trace("No element selected");
			return;
		}
		
		if (aElement instanceof IRPOperation)
		{
			IRPOperation operation = (IRPOperation) aElement;
			if (myOzoneSupport.view(operation))
			{
				trace("Opened operation in Ozone: " + operation.getName());
			}
			else
			{
				trace("Failed to open operation in Ozone");
			}
		}
		else
		{
			trace("Selected element is not an operation: " + aElement.getMetaClass());
		}
	}
	
	/**
	 * Set a breakpoint at the selected operation in Ozone.
	 * Supports IRPOperation, BreakPoint metaclass (IRPComment with offset), 
	 * and BreakPointCollection metaclass.
	 */
	private void ozoneSetBreakpoint(IRPModelElement aElement)
	{
		trace("Set Breakpoint in Ozone: " + (aElement != null ? aElement.getName() : "null"));
		
		if (!ensureOzoneConnected())
		{
			return;
		}
		
		if (aElement == null)
		{
			trace("No element selected");
			return;
		}
		
		// Handle BreakPoint metaclass (IRPComment with Offset tag)
		if (aElement instanceof IRPComment)
		{
			IRPComment comment = (IRPComment) aElement;
			ozoneSetBreakpointFromComment(comment);
			return;
		}
		
		// Search for BreakPoint comments in the element
		List<IRPComment> comments = aElement.getNestedElementsByMetaClass("Comment", 1).toList();
		boolean foundBreakpoint = false;
		
		for (IRPComment comment : comments)
		{
			if (comment.getUserDefinedMetaClass().equals(BREAK_POINT_META_NAME))
			{
				ozoneSetBreakpointFromComment(comment);
				foundBreakpoint = true;
			}
		}
		
		if (foundBreakpoint)
		{
			return;
		}
		
		// Handle IRPOperation directly
		if (aElement instanceof IRPOperation)
		{
			IRPOperation operation = (IRPOperation) aElement;
			if (myOzoneSupport.setBreakpoint(operation))
			{
				trace("Breakpoint set for operation: " + operation.getName());
			}
			else
			{
				trace("Failed to set breakpoint for operation");
			}
		}
		else
		{
			trace("Selected element is not an operation or breakpoint: " + aElement.getMetaClass());
		}
	}
	
	/**
	 * Set breakpoint in Ozone from a BreakPoint comment (with offset)
	 */
	private void ozoneSetBreakpointFromComment(IRPComment aBreakpoint)
	{
		// Handle BreakPointCollection
		if (aBreakpoint.getUserDefinedMetaClass().equals(BREAK_POINT_COLLECTION_META_NAME))
		{
			ozoneSetBreakpointCollection(aBreakpoint);
			return;
		}
		
		// Must be a BreakPoint
		if (!aBreakpoint.getUserDefinedMetaClass().equals(BREAK_POINT_META_NAME))
		{
			trace("Comment is not a BreakPoint: " + aBreakpoint.getUserDefinedMetaClass());
			return;
		}
		
		if (!(aBreakpoint.getOwner() instanceof IRPOperation))
		{
			trace("Owner of BreakPoint has to be an operation");
			return;
		}
		
		IRPTag offsetTag = aBreakpoint.getTag("Offset");
		int offset = 0;
		
		if (offsetTag != null)
		{
			try
			{
				offset = Integer.parseInt(offsetTag.getValue());
			}
			catch (NumberFormatException e)
			{
				trace("Invalid offset value: " + offsetTag.getValue());
			}
		}
		
		IRPOperation owner = (IRPOperation) aBreakpoint.getOwner();
		
		if (myOzoneSupport.setBreakpoint(owner, offset))
		{
			trace("Breakpoint set for " + owner.getName() + " at offset " + offset);
		}
		else
		{
			trace("Failed to set breakpoint for " + owner.getName());
		}
	}
	
	/**
	 * Set breakpoints from a BreakPointCollection in Ozone
	 */
	private void ozoneSetBreakpointCollection(IRPComment aBreakpointCollection)
	{
		if (!aBreakpointCollection.getUserDefinedMetaClass().equals(BREAK_POINT_COLLECTION_META_NAME))
		{
			return;
		}
		
		trace("Setting breakpoints from collection: " + aBreakpointCollection.getName());
		
		List<IRPModelElement> breakPoints = aBreakpointCollection.getAnchoredByMe().toList();
		for (IRPModelElement element : breakPoints)
		{
			if (element instanceof IRPComment)
			{
				IRPComment breakpoint = (IRPComment) element;
				ozoneSetBreakpointFromComment(breakpoint);
			}
		}
	}
	
	/**
	 * Ensure Ozone is connected, connect if not
	 */
	private boolean ensureOzoneConnected()
	{
		if (myOzoneSupport == null || !myOzoneSupport.isConnected())
		{
			trace("Not connected to Ozone - connecting now...");
			ozoneConnect();
			
			if (myOzoneSupport == null || !myOzoneSupport.isConnected())
			{
				trace("Could not connect to Ozone");
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Delete breakpoint at the selected operation in Ozone
	 */
	private void ozoneDeleteBreakpoint(IRPModelElement aElement)
	{
		trace("Delete Breakpoint in Ozone: " + (aElement != null ? aElement.getName() : "null"));
		
		if (!ensureOzoneConnected())
		{
			return;
		}
		
		if (aElement == null)
		{
			trace("No element selected");
			return;
		}
		
		if (aElement instanceof IRPOperation)
		{
			IRPOperation operation = (IRPOperation) aElement;
			if (myOzoneSupport.deleteBreakpoint(operation))
			{
				trace("Breakpoint deleted for operation: " + operation.getName());
			}
			else
			{
				trace("Failed to delete breakpoint for operation");
			}
		}
		else
		{
			trace("Selected element is not an operation: " + aElement.getMetaClass());
		}
	}
	
	/**
	 * Delete all breakpoints in Ozone
	 */
	private void ozoneDeleteAllBreakpoints()
	{
		trace("Delete all Breakpoints in Ozone");
		
		if (myOzoneSupport == null || !myOzoneSupport.isConnected())
		{
			trace("Not connected to Ozone - connecting now...");
			ozoneConnect();
			
			if (myOzoneSupport == null || !myOzoneSupport.isConnected())
			{
				trace("Could not connect to Ozone");
				return;
			}
		}
		
		if (myOzoneSupport.deleteAllBreakpoints())
		{
			trace("All breakpoints deleted");
		}
		else
		{
			trace("Failed to delete all breakpoints");
		}
	}
	
	/**
	 * Start Ozone debugger with the current project name
	 */
	private void ozoneStart()
	{
		trace("Starting Ozone...");
		
		IRPProject project = myRhapsody.activeProject();
		
		if (project == null)
		{
			trace("No active project - cannot start Ozone");
			return;
		}
		
		String projectName = project.getName();
		trace("Using project name: " + projectName);
		
		// Get the working folder from project configuration
		IRPConfiguration config = RhapsodyHelper.getProjectConfig(project, "DefaultConfig");
		
		if (config == null)
		{
			trace("ProjectPath of " + projectName + " not found");
			return;
		}
		
		String workingFolder = config.getDirectory(1, "");
		trace("Working Folder: " + workingFolder);
		
		if (myOzoneSupport == null)
		{
			myOzoneSupport = new OzoneSupport(myRhapsody, this::trace);
		}
		
		if (myOzoneSupport.startOzone(projectName, workingFolder))
		{
			trace("Ozone started successfully with project: " + projectName);
		}
		else
		{
			trace("Failed to start Ozone");
		}
	}
	
	// ==================== End Ozone Methods ====================

	private IRPOperation getSelectedOperation(IRPOperation aOperation)
	{
		IRPModelElement selected = aOperation;

		if (selected == null)
		{
			selected = myRhapsody.getSelectedElement();
		}
		if (selected instanceof IRPOperation == false)
		{
			trace("No Operation selected");
			return null;
		}

		IRPOperation selectedOperation = (IRPOperation) selected;
		return selectedOperation;
	}

	private String getPath(IRPOperation aOperation)
	{

		IRPModelElement selectedOwner = aOperation.getOwner();

		if (selectedOwner instanceof IRPClass == false)
		{
			trace("Owner is not a class");
			return null;
		}

		IRPClass selectedClass = (IRPClass) selectedOwner;
		String path = ASTHelper.getSourcePath(selectedClass, myRhapsody);

		if ((aOperation.isATemplate() == 1) || (aOperation.getIsInline() == 1))
		{
			path = path + ".h";
		}
		else
		{
			path = path + ".cpp";
		}

		path = path.replace('/', '\\');

		return path;
	}

	private String getPath(IRPClass aClass)
	{
		String path = ASTHelper.getSourcePath(aClass, myRhapsody) + ".cpp";
		path = path.replace('/', '\\');
		return path;
	}

	private String getPath(IRPPackage aPackage)
	{
		String path = ASTHelper.getSourcePath(aPackage, myRhapsody) + ".gpj";
		path = path.replace('/', '\\');
		return path;
	}

	private List<String> runCmd(String aArgs1, String aArgs2, String aArgs3, String aArgs4)
	{

		List<String> ret = new ArrayList<String>();
		if (aArgs3 == null)
		{
			trace("run " + myCmd + " " + " -s " + aArgs1 + " -s " + aArgs2);
		}
		else if (aArgs4 == null)
		{
			trace("run " + myCmd + " " + " -s " + aArgs1 + " -s " + aArgs2 + " -s " + aArgs3);
		}
		else
		{
			trace("run " + myCmd + " " + " -s " + aArgs1 + " -s " + aArgs2 + " -s " + aArgs3 + " -s " + aArgs4);
		}

		try
		{
			ProcessBuilder pb;
			if (aArgs3 == null)
			{
				pb = new ProcessBuilder(myCmd, "-noconsole", "-s", aArgs1, "-s", aArgs2);
			}
			else if (aArgs4 == null)
			{
				pb = new ProcessBuilder(myCmd, "-noconsole", "-s", aArgs1, "-s", aArgs2, "-s", aArgs3);
			}
			else
			{
				pb = new ProcessBuilder(myCmd, "-noconsole", "-s", aArgs1, "-s", aArgs2, "-s", aArgs3, "-s", aArgs4);
			}
			Process process = pb.start();
			InputStream inputStream = process.getInputStream();
			// InputStream errorStream = process.getErrorStream();

			BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream));
			// BufferedReader errrorReader = new BufferedReader(new
			// InputStreamReader(errorStream));

			String inputLine;
			while ((inputLine = inputReader.readLine()) != null)
			{
				ret.add(inputLine);
				trace(inputLine);
			}

			int exitCode = process.waitFor();
			trace("Exit Code: " + exitCode);
		}
		catch (IOException | InterruptedException iox)
		{
			trace("Exception: " + iox.getMessage());
			iox.printStackTrace();
		}
		return ret;
	}

	public void viewInDebugger(IRPModelElement aElement)
	{
		trace("Start viewInDebugger Name: " +aElement.getName()+ ": Metaclass: " + aElement.getMetaClass());
		String path = null;
		if(aElement instanceof IRPHyperLink)
		{
			IRPHyperLink link = (IRPHyperLink) aElement;
			path = link.getURL();
		}
		else if(aElement instanceof IRPDependency)
		{
			IRPDependency dependency = (IRPDependency) aElement;
			IRPModelElement dependent = dependency.getDependent();
			
			path = dependent.getName()+".cpp";
		}
		else if(aElement instanceof IRPFile)
		{
			IRPFile file = (IRPFile) aElement;
			
			path = file.getPath(0);
			if (path.isEmpty())
			{
				
				path = file.getName();
				if(file.getFileType().equalsIgnoreCase("specification"))
				{
					path+= ".h";
				}
				else
				{
					path+= ".cpp";
				}
				
			}
			//path = aElement.getName();
		}
		else 
		{
			
			path = getModelElementLocation(aElement);
		}
		
		
		if(path == null)
		{
			trace("Could not determine path for element: " + aElement.getName());
			return;
		}
		

		String arg2 = myArgDebugView2Begin + path + myArgDebugView2End;

		// build string...
		// String args = MessageFormat.format(myArgs, component);

		runCmd(myArgDebugView1, arg2, null, null);

	}
	
//	public void viewInDebugger(IRPHyperLink aLink)
//	{
//		trace("Start viewInDebugger from hyperlink");
//        
//        String path = aLink.getURL();
//        String arg2 = myArgDebugView2Begin + path + myArgDebugView2End;
//        runCmd(myArgDebugView1, arg2, null, null);
//	}

	public void setBreakPoint(IRPOperation aOperation, int aOffset)
	{
		trace("Start setBreakPoint operation ");
		
		if (setMyCmd() == false)
		{
			trace("Could not set Breakpoint - Multi not running");
			return;
		}
		
		String component = getModelElementLocation(aOperation);
		if (aOffset > 0)
		{
			component = component + "#" + (aOffset + 3);
		}
		String arg2 = myArgSetBreakPoint2Begin + component + myArgDebugView2End;
		runCmd(myArgDebugView1, arg2, null, null);
	}

	public void setBreakPoint(IRPComment aBreakpoint)
	{
		trace("Start setBeakPoint from comment");

		if (aBreakpoint.getUserDefinedMetaClass().equals(BREAK_POINT_META_NAME) == false)
		{

			setBreakPointCollection(aBreakpoint);
			return;
		}

		if (aBreakpoint.getOwner() instanceof IRPOperation == false)
		{
			trace("Owner has to be an operation");
			return;
		}

		IRPTag offsetTag = aBreakpoint.getTag("Offset");

		String offsetString = offsetTag.getValue();

		int offset = Integer.parseInt(offsetString);

		IRPOperation owner = (IRPOperation) aBreakpoint.getOwner();

		setBreakPoint(owner, offset);

	}

	public void setBreakPoint(IRPModelElement aModelElement)
	{

		if (aModelElement instanceof IRPComment)
		{
			IRPComment comment = (IRPComment) aModelElement;
			setBreakPoint(comment);
		}
		// search for all comments
		List<IRPComment> comments = aModelElement.getNestedElementsByMetaClass("Comment", 1).toList();

		boolean setBreakpoint = false;

		for (IRPComment comment : comments)
		{
			if (comment.getUserDefinedMetaClass().equals(BREAK_POINT_META_NAME))
			{
				setBreakPoint(comment);
				setBreakpoint = true;
			}
		}

		if (setBreakpoint == true)
		{
			return;
		}

		if (aModelElement instanceof IRPOperation)
		{
			IRPOperation operation = (IRPOperation) aModelElement;
			setBreakPoint(operation, 0);
			return;
		}

	}

	private void setBreakPointCollection(IRPComment aBreakpointCollection)
	{
		if (aBreakpointCollection.getUserDefinedMetaClass().equals(BREAK_POINT_COLLECTION_META_NAME) == false)
		{
			return;
		}

		List<IRPModelElement> breakPoints = aBreakpointCollection.getAnchoredByMe().toList();
		for (IRPModelElement element : breakPoints)
		{

			if (element instanceof IRPComment)
			{
				IRPComment breakpoint = (IRPComment) element;
				setBreakPoint(breakpoint);
			}
		}

	}

	private String getModelElementLocation(IRPModelElement aModelElement)
	{
		String nameSpace = null;
		String component = null;
		IRPModelElement selected = aModelElement;

		if (selected == null)
		{
			selected = myRhapsody.getSelectedElement();
		}

		nameSpace = RhapsodyOperation.getNamespace(selected);
		component = selected.getName();
		selected = selected.getOwner();

		while (selected instanceof IRPClass)
		{
			
			String selectedName = selected.getName();
			selected = selected.getOwner();
			if(selectedName.equals("TopLevel"))
			{
				continue;
			}
			component = selectedName + "::" + component;
			
		}

		if (nameSpace != null)
		{
			component = nameSpace + "::" + component;
		}
		return component;
	}

	public void viewInRhapsody()
	{
		
		if (setMyCmd() == false)
		{
			trace("Could not view in Rhapsody - Multi not running");
			return;
		}
		
		String arg2 = "\"dw.GetPullDownValue('pd_proc')\"";
		List<String> result = runCmd(myArgDebugView1, arg2, null, null);

		int i = 0;

		String operationString = result.get(1);

		operationString = operationString.substring(1, operationString.length() - 1);

		trace("Operation: " + operationString);

		try
		{

			IRPModelElement selectedModel = selectOperation(operationString);
			if (selectedModel == null)
			{
				trace("Operation not found");
				return;
			}

			trace("Selected Model:" + selectedModel.getName());

			selectedModel.locateInBrowser();
		}
		catch (Exception e)
		{
			trace("Exception: " + e.getMessage());
		}

	}

	private IRPModelElement selectOperation(String aOperationName)
	{

		aOperationName = aOperationName.substring(0, aOperationName.indexOf('('));

		String[] elements = aOperationName.split("::");

		IRPModelElement searchIn = myRhapsody.activeProject();

		trace("selectOperation " + aOperationName);

		if (searchIn == null)
		{
			return null;
		}

		for (String element : elements)
		{
			// namespace...
			IRPModelElement f = searchIn.findNestedElementRecursive(element, "Package");
			if (f == null)
			{
				f = searchIn.findNestedElementRecursive(element, "Class");
				if (f == null)
				{
					f = searchIn.findNestedElementRecursive(element, "Operation");
					if (f == null)
					{
						// not found
						trace("Did not find  " + element);
						continue;
					}
				}
			}
			searchIn = f;
			trace("Found  " + searchIn.getName() + " as " + searchIn.getMetaClass());

		}

		return searchIn;

	}

	public void viewInEditor(IRPOperation aOperation)
	{
		trace("Start viewInEditor");
		
		if (setMyCmd() == false)
		{
			trace("Could not view in Editor - Multi not running");
			return;
		}

		IRPOperation selectedOperation = getSelectedOperation(aOperation);

		if (selectedOperation == null)
		{
			return;
		}

		String path = getPath(selectedOperation);

		if (path == null)
		{
			return;
		}

		String args2 = myArgEditView2Begin + path + myArgEditView2End;

		int line = ASTHelper.getSourceOffset(selectedOperation, myRhapsody);

		String args3 = myArgEditView3Begin + line + myArgEditView3End;

		runCmd(myArgEditView1, args2, args3, null);

	}
	
	private  String pickPwsh() {
        String sysroot = System.getenv("SystemRoot");
        String[] candidates = new String[] {
            sysroot + "\\Sysnative\\WindowsPowerShell\\v1.0\\powershell.exe", // erzwingt 64-bit aus 32-bit JVM
            sysroot + "\\System32\\WindowsPowerShell\\v1.0\\powershell.exe",  // 64-bit, wenn JVM 64-bit
            "powershell.exe"                                                  // Fallback
        };
        for (String c : candidates) {
            try {
                Process p = new ProcessBuilder(c, "-NoProfile", "-NonInteractive", "-Command", "echo OK").start();
                if (p.waitFor(3, TimeUnit.SECONDS) && p.exitValue() == 0) return c;
            } catch (Exception ignored) {}
        }
        return "powershell.exe";
    }

	
	
	private String getGhsPath() 
	{
		
		String path = "";
		
		String pwsh = pickPwsh();

        // PowerShell-Command: UTF-8 einschalten, nur vorhandene Pfade ausgeben
        String ps = "[Console]::OutputEncoding=[System.Text.Encoding]::UTF8; " +
                    "(Get-Process mprojmgr).Path";
                   
        
        trace("PowerShell: " + ps);

        ProcessBuilder pb = new ProcessBuilder(
            pwsh,
            "-NoProfile",
            "-NonInteractive",
            "-ExecutionPolicy", 
            "Bypass",
            "-Command", ps
        );
        

        pb.redirectErrorStream(true); // stderr -> stdout zusammenf�hren
        Process p;
		try
		{
			p = pb.start();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

        List<String> lines = new ArrayList<>();
        try 
        {
        	BufferedReader br = new BufferedReader(
        
                new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8));
        
            String line;
            try
			{
				while ((line = br.readLine()) != null) 
				{
				    trace(line);
					// BOM am ersten Zeichen ggf. entfernen
				    if (lines.isEmpty()==false)
				    {
				    	//lines.add(line);
				    }
				    else lines.add(line.replace("\uFEFF", ""));
				}
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
		catch (Exception e)
		{
			p.destroyForcibly();
			throw new RuntimeException("error reading powershell output", e);
		}

        // Sauber beenden (Timeout als Schutz)
        try
		{
			if (!p.waitFor(10, TimeUnit.SECONDS))
			{
			    p.destroyForcibly();
			    throw new RuntimeException("could not closePowerShell");
			}
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        int code = p.exitValue();
        if (lines.isEmpty()) 
        {
            System.out.println("No Output (ExitCode " + code + ").");
        } 
        else 
        {
            for (String s : lines) 
            {
                if (!s.trim().isEmpty())
                {	
                	path += s.trim();
                }
                
            }
        }
        
        File ghsExe = new File(path);
        
		if (!ghsExe.exists())
		{
			trace("GHS Multi nicht found: " + path);
			return null;
		}
		
		path = ghsExe.getParentFile().getAbsolutePath() + "\\";
        
        
        
        
        return path;
    }
    
	
	
	
	

	public void compile(IRPModelElement aElement)
	{

		
		boolean viewTable = false;
		
		trace("Generate...");

		IRPCollection generateCollection = myRhapsody.getListOfSelectedElements();

		trace("Start generate");

		if (aElement == null)
		{
			aElement = myRhapsody.getSelectedElement();
		}
		else
		{
			generateCollection.addItem(aElement);
		}

		myRhapsody.generateElements(generateCollection);

		trace("Start compile");
		

		if (setMyCmd() == false)
		{
			trace("Could not compile - Multi not running");
			return;
		}

		String path = null;

		if (aElement instanceof IRPOperation)
		{

			IRPOperation aOperation = (IRPOperation) aElement;
			IRPOperation selectedOperation = getSelectedOperation(aOperation);

			if (selectedOperation == null)
			{
				return;
			}

			IRPModelElement owner = selectedOperation.getOwner();

			if (owner instanceof IRPClass)
			{
				IRPClass selectedClass = (IRPClass) owner;
				ASTHelper.deleteCompilerIssues(selectedClass,"CompilerIssue");
				//deleteCompilerIssues(selectedClass);
			}

			path = getPath(selectedOperation);

		}
		else if (aElement instanceof IRPClass)
		{

			IRPClass selectedClass = (IRPClass) aElement;
			path = getPath(selectedClass);

			deleteCompilerIssues(selectedClass);

		}
		else if (aElement instanceof IRPPackage)
		{
			IRPPackage selectedPackage = (IRPPackage) aElement;

			deleteCompilerIssues(selectedPackage);

			path = getPath(selectedPackage);
		}

		List<String> result = null;

		if (aElement instanceof IRPProject)
		{
			IRPProject selectedProject = (IRPProject) aElement;
			result = runCmd(myArgCompile1, myArgBuldAll, myArgCompile3, myArgCompile4);
		}
		else
		{

			if (path == null)
			{
				return;
			}

			String args2 = myArgCompile2Begin + path + myArgCompile2End;

			result = runCmd(myArgCompile1, args2, myArgCompile3, myArgCompile4);
		}

		if (result != null)
		{
			// check for error....
			Iterator<String> i = result.iterator();

			File workingFolder = RhapsodyHelper.getActiveDefaultPath(aElement);
			

			while (i.hasNext())
			{
				String r = i.next();

				if (r.contains("error #") || r.contains("warning #"))
				{

					// String pattern = "\"([^\"]+)\", line (\\d+): error #(\\d+): (.+)";
					// String pattern = "\"([^\"]+)\", line (\\d+): (error|warning) #(\\d+):(.+)?";
					String pattern = "\"([^\"]+)\", line (\\d+): (error|warning) #([^:]+)(.+)?";
					Pattern re = Pattern.compile(pattern);

					Matcher matcher = re.matcher(r);
					if (matcher.find())
					{

						String filePath = matcher.group(1);
						int lineNumber = Integer.parseInt(matcher.group(2));
						String errorLevel = matcher.group(3);
						String errorCode = matcher.group(4);
						String errorMessage = matcher.group(5);
						if (i.hasNext())
						{
							String messageEnd = " " + i.next().trim();
							if (errorMessage == null)
							{
								errorMessage = messageEnd;
							}
							else
							{
								errorMessage += messageEnd;
							}
						}

						File f = new File(filePath);

						String fileName = f.getName();

						String className = fileName.substring(0, fileName.lastIndexOf("."));

						// Ausgabe der Ergebnisse
						System.out.println("----------------------------------");
						System.out.println("Class: " + className);
						System.out.println("File: " + fileName);
						System.out.println("Line: " + lineNumber);
						System.out.println("errorLevel: " + errorLevel);
						System.out.println("Errorcode: " + errorCode);
						System.out.println("Errormessage: " + errorMessage);
						System.out.println("----------------------------------");

						File absolutePath = new File(workingFolder.getParentFile(), filePath);
						
						String absolutePathString = null;
						
						try
						{
							absolutePathString = absolutePath.getCanonicalPath();
						}
						catch(IOException iox)
                        {
                            trace("Exception: " + iox.getMessage());
                            iox.printStackTrace();
                            return;
                        }
						
						ASTHelper.createIssue(myRhapsody.activeProject(), absolutePathString, lineNumber, errorLevel, errorMessage, "CompilerIssue");
						viewTable = true;

						/*
						if (aElement instanceof IRPClass)
						{
							IRPClass selectedClass = (IRPClass) aElement;
							createIssue(workingFolder, selectedClass, fileName, className, lineNumber, errorLevel,
									errorCode, errorMessage);
						}
						else if (aElement instanceof IRPPackage)
						{
							IRPPackage p = (IRPPackage) aElement;
							List<IRPClass> classes = p.getNestedElementsByMetaClass("Class", 1).toList();
							for (IRPClass c : classes)
							{
								if (c.getName().equals(className))
								{
									createIssue(workingFolder, c, fileName, className, lineNumber, errorLevel,
											errorCode, errorMessage);
								}
							}
						}
						else if (aElement instanceof IRPOperation)
						{
							IRPModelElement o = aElement.getOwner();
							if (o instanceof IRPClass)
							{
								IRPClass c = (IRPClass) o;

								createIssue(workingFolder, c, fileName, className, lineNumber, errorLevel, errorCode,
										errorMessage);

							}
						}
						*/
					}
				}
			}
		}
		
		if (viewTable == true)
		{
			IRPProject project = myRhapsody.activeProject();
			if (project == null)
			{
				return;
			}
			ASTHelper.viewCompilerIssues(project);
		}

	}

	

	public void createIssue(File aWorkingFolder, IRPClass aClass, String aFileName, String aClassName, int aLineNumber,
			String aErrorLevel, String aErrorCode, String aErrorMessage)
	{

		while (aClass.getOwner() instanceof IRPClass)
		{
			aClass = (IRPClass) aClass.getOwner();
		}

		File cppSource = new File(aWorkingFolder, aFileName);
		if (cppSource.exists())
		{
			IASTTranslationUnit translationUnit = ASTHelper.getTranslationUnit(cppSource.getAbsolutePath());
			IASTFunctionDefinition operationDefinition = ASTHelper.getFunctionDefinition(aLineNumber, translationUnit);
			String operationName = ASTHelper.getOperationName(operationDefinition);
			int offset = (ASTHelper.getOffset(operationDefinition, aLineNumber) - 1);

			createIssue(aClass, aErrorLevel, aErrorCode + ": " + aErrorMessage, operationName, offset);

		}

	}

	public void deleteCompilerIssues(IRPPackage aPackage)
	{

		if (aPackage instanceof IRPProject)
		{
			return;
		}

		trace("delete CompilerIssue from " + aPackage.getName());

		List<IRPClass> classes = aPackage.getNestedElementsByMetaClass("Class", 1).toList();

		if (aPackage.isReadOnly() == 1)
		{
			return;
		}

		for (IRPClass c : classes)
		{
			trace("delete CompilerIssue from " + c.getName());
			deleteCompilerIssues(c);
		}
	}

	public void deleteCompilerIssues(IRPClass aClass)
	{

		if (aClass.isReadOnly() == 1)
		{
			return;
		}

		List<IRPComment> comments = aClass.getNestedElementsByMetaClass("Comment", 0).toList();

		for (IRPComment comment : comments)
		{
			if (comment.getUserDefinedMetaClass().equals(CompilerIssue))
			{

				comment.deleteFromProject();

			}
		}
	}

	@SuppressWarnings("unchecked")
	public void createIssue(IRPClass aClass, String errorLevel, String infoText, String operationName, int offset)
	{
		if (operationName == null)
		{
			return;
		}

		IRPUnit unit = aClass.getSaveUnit();
		if (unit == null)
		{
			return;
		}

		if (unit.isReadOnly() == 1)
		{
			return;
		}

		String issueName = errorLevel + "_" + operationName + "_" + offset;

		List<IRPComment> issues = aClass.getNestedElementsByMetaClass("Comment", 0).toList();
		for (IRPComment issue : issues)
		{
			if (issue.getUserDefinedMetaClass().equals(CompilerIssue))
			{
				if (issue.getName().equals(issueName))
				{
					return;
				}
			}
		}

		IRPComment compilerIssue = (IRPComment) aClass.addNewAggr(CompilerIssue, issueName);

		compilerIssue.setDescription(infoText);
		compilerIssue.setBody(errorLevel);
		compilerIssue.setSpecification(operationName + " " + offset);

		if (compilerIssue != null)
		{
			// get the operation...
			boolean foundOperation = addCompilerIssue(aClass, operationName, compilerIssue);

			if (foundOperation == false)
			{
				// check in nested class
				List<IRPClass> nestedClasses = aClass.getNestedElementsByMetaClass("Class", 1).toList();
				for (IRPClass nestedClass : nestedClasses)
				{
					addCompilerIssue(nestedClass, operationName, compilerIssue);
				}

			}

		}
	}

	public boolean addCompilerIssue(IRPClass aClass, String aOperationName, IRPComment aCompilerIssue)
	{
		boolean foundOperation = false;
		List<IRPOperation> ops = aClass.getOperations().toList();
		for (IRPOperation op : ops)
		{
			if (op.getName().equals(aOperationName))
			{
				foundOperation = true;
				aCompilerIssue.addAnchor(op);
			}
		}

		if (foundOperation == false)
		{
			List<IRPClass> nestedClasses = aClass.getNestedElementsByMetaClass("Class", 0).toList();
			for (IRPClass c : nestedClasses)
			{
				foundOperation = addCompilerIssue(c, aOperationName, aCompilerIssue);
				if (foundOperation == true)
				{
					return foundOperation;
				}
			}
			return false;

		}
		else
		{
			return foundOperation;
		}
	}

	private void openGHSProject()
	{
		IRPModelElement selected = myRhapsody.getSelectedElement();
		if (selected instanceof IRPProject == false)
		{
			trace("No Project selected");
			return;
		}

		IRPProject project = (IRPProject) selected;

		IRPConfiguration config = RhapsodyHelper.getProjectConfig(project, "DefaultConfig"); // which config?

		if (config == null)
		{
			trace("ProjectPath of " + project.getName() + " not found");
			return;
		}

		String projectEnding = "AppWorkspaceD9.gpj";
		if (config.getBuildSet().equals("Release"))
		{
			projectEnding = "AppWorkspaceR9.gpj";
		}

		String projectName = config.getDirectory(1, "") + "/" + project.getName() + projectEnding; // is there a better
																									// solution?
		
		String workingfolder = config.getDirectory(1, "");
		
		trace("Working Folder: " + workingfolder);

		File projectFile = new File(projectName);

		if (projectFile.exists() == false)
		{

			trace("could not find Project file in " + projectName);
			return;

		}

		// run multi...
		try
		{
			ProcessBuilder pb = new ProcessBuilder(myMultiCmd, projectName);
			
			pb.directory(new File(workingfolder));
			pb.start(); // fire and forget

		}
		catch (IOException iox)
		{
			trace("Exception: " + iox.getMessage());
			iox.printStackTrace();
		}

	}

}
