package de.schlaich.gunnar.rhapsody.vs;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.List;

import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPComment;
import com.telelogic.rhapsody.core.IRPComponent;
import com.telelogic.rhapsody.core.IRPConfiguration;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPOperation;
import com.telelogic.rhapsody.core.IRPProfile;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPUnit;
import com.telelogic.rhapsody.core.RPUserPlugin;

//import de.schlaich.gunnar.rhapsody.USM.CUSMPlugin;
import de.schlaich.gunnar.rhapsody.utilities.ASTHelper;

public class VSPlugin extends RPUserPlugin
{

	public static final String PROFILE_NAME = "VSProfile";
	private IRPApplication myRhapsody = null;
	private IRPProfile myProfile = null;
	private String myStartupDirectory = null;
	private String mySolutionPath = null;

	private static final String locateOperationCmd = "locateOperation";
	private static final String locateInRhapsodyCmd = "locateInRhapsody";
	private static final String compileCmd = "compile";

	public VSPlugin()
	{
		// TODO Auto-generated constructor stub
	}
	
	private String getBuildDate()
	{
		try
		{
			String jarPath = VSPlugin.class.getProtectionDomain().getCodeSource().getLocation().getPath();
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

		ASTHelper.setTraceAction(this::trace);

		myRhapsody = rpyApplication;
		trace("VS Plugin started");
		trace("Build date: " + getBuildDate());

		IRPProject activeProject = myRhapsody.activeProject();

		if (activeProject == null)
		{
			trace("no active Project!");
			return;
		}

		IRPUnit projectUnit = activeProject.getSaveUnit();

		String projectPath = projectUnit.getCurrentDirectory();

		List<IRPComponent> components = activeProject.getComponents().toList();
		for (IRPComponent component : components)
		{
			trace(component.getBuildType());
			if (component.getBuildType().equals("executable"))
			{
				List<IRPConfiguration> configs = component.getConfigurations().toList();
				for (IRPConfiguration config : configs)
				{
					if (config.getName().equals("DefaultConfig"))
					{
						mySolutionPath = config.getDirectory(1, "");
						mySolutionPath = mySolutionPath + "/" + activeProject.getName() + "AppWorkspace.sln"; // is
																												// there
																												// a
																												// better
																												// solution?

						break;
					}
				}
				break;
			}
		}

		// search for profile
		List<IRPProfile> profiles = activeProject.getProfiles().toList();

		for (IRPProfile profile : profiles)
		{
			if (profile.getName().equals(PROFILE_NAME))
			{
				myProfile = profile;
				break;
			}
		}

		if (myProfile == null)
		{
			trace("Profile " + PROFILE_NAME + " not found");
			return;
		}

		IRPUnit profileUnit = myProfile.getSaveUnit();
		myStartupDirectory = profileUnit.getCurrentDirectory();

	}

	@Override
	public void RhpPluginInvokeItem()
	{

	}

	@Override
	public void OnMenuItemSelect(String menuItem)
	{

		if (menuItem.equals("View in Visual Studio"))
		{
			viewInVisualStudio(null);
			return;
		}
		if (menuItem.equals("View in Rhapsody"))
		{
			viewInRhapsody();
			return;
		}
		if (menuItem.equals("Compile in Visual Studio"))
		{
			compile(null);
			return;
		}

	}

	private void trace(String aMsg)
	{
		myRhapsody.writeToOutputWindow("Log", "VisualStudioExtension: " + aMsg + "\n");
		System.out.println(aMsg);
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

	public void viewInVisualStudio(IRPOperation aOperation)
	{

		trace("View in Visual Studio");

		IRPOperation selectedOperation = aOperation;

		if (selectedOperation == null)
		{
			IRPModelElement selected = myRhapsody.getSelectedElement();

			// operation...
			if (selected instanceof IRPOperation == false)
			{
				return;
			}

			selectedOperation = (IRPOperation) selected;
		}
		IRPModelElement selectedOwner = selectedOperation.getOwner();

		if (selectedOwner instanceof IRPClass == false)
		{
			return;
		}

		IRPClass selectedClass = (IRPClass) selectedOwner;
		String path = ASTHelper.getSourcePath(selectedClass, myRhapsody);

		if ((selectedOperation.isATemplate() == 1) || (selectedOperation.getIsInline() == 1))
		{
			path = path + ".h";
		}
		else
		{
			path = path + ".cpp";
		}

		path = path.replace('/', '\\');

		String exe = myStartupDirectory + "\\VSControl.exe";

		int offset = ASTHelper.getSourceOffset(selectedOperation, myRhapsody);

		trace(path + " " + offset);

		mySolutionPath = mySolutionPath.replace('/', '\\');

		trace("SolutionPath: " + mySolutionPath);

		try
		{
			ProcessBuilder pb = new ProcessBuilder(exe, locateOperationCmd, path, Integer.toString(offset),
					mySolutionPath);
			Process process = pb.start();
			InputStream inputStream = process.getInputStream();
			// InputStream errorStream = process.getErrorStream();

			BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream));
			// BufferedReader errrorReader = new BufferedReader(new
			// InputStreamReader(errorStream));

			String inputLine;
			String errorLine;
			while ((inputLine = inputReader.readLine()) != null)
			{
				trace("VSControl.exe: " + inputLine);
			}

			int exitCode = process.waitFor();
			trace("VSControl Exit Code: " + exitCode);
		}
		catch (IOException | InterruptedException iox)
		{
			trace("Exception: " + iox.getMessage());
			iox.printStackTrace();
		}

	}

	public void viewInRhapsody()
	{
		trace("View in Rhapsody");

		String cmd = myStartupDirectory + "\\VSControl.exe";
		mySolutionPath = mySolutionPath.replace('/', '\\');

		trace("cmd: " + cmd);

		trace(locateInRhapsodyCmd + " \"\" 0 " + mySolutionPath);

		// String path =
		// "J:\\USM43\\GeneratedProjects\\SimulatorBernina790Pro\\SimulatorBernina790ProApp\\DLC\\DefaultConfig\\CDataLoggingClient.cpp";
		String path = null;
		int line = 188;

		try
		{
			ProcessBuilder pb = new ProcessBuilder(cmd, locateInRhapsodyCmd, "none", "0", mySolutionPath);
			Process process = pb.start();
			InputStream inputStream = process.getInputStream();
			// InputStream errorStream = process.getErrorStream();

			BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream));
			// BufferedReader errrorReader = new BufferedReader(new
			// InputStreamReader(errorStream));

			String inputLine;
			String errorLine;
			while ((inputLine = inputReader.readLine()) != null)
			{
				trace("VSControl.exe: " + inputLine);
				if (inputLine.startsWith("Aktuelle Datei:") == true)
				{
					path = inputLine.substring(16);
				}
				if (inputLine.startsWith("Aktuelle Zeile:") == true)
				{
					line = Integer.parseInt(inputLine.substring(16));
				}
			}

			int exitCode = process.waitFor();
			trace("VSControl Exit Code: " + exitCode);
		}
		catch (IOException | InterruptedException iox)
		{
			trace("Exception: " + iox.getMessage());
			iox.printStackTrace();
		}

		if (path == null)
		{
			trace("Path not found!");
			return;
		}

		trace("Path: " + path + " Line: " + line);
		IRPProject activeProject = myRhapsody.activeProject();
		if (activeProject == null)
		{
			trace("No active project!");
			return;
		}

		trace("Start ASTHelper.getOperation()");

		try
		{

			IRPOperation operation = ASTHelper.getOperation(activeProject, path, line);
			if (operation == null)
			{
				trace("Operation not found!");
				return;
			}
			trace("Operation: " + operation.getFullPathName());

			operation.locateInBrowser();

		}
		catch (Exception ex)
		{
			trace("Exception " + ex.getMessage());
		}

	}

	public void compile(IRPClass aClass)
	{
		trace("Compile");
		IRPProject activeProject = myRhapsody.activeProject();
		if (activeProject == null)
		{
			trace("No active Project!");
			return;
		}

		IRPClass selectedClass = aClass;

		if (selectedClass == null)
		{
			IRPModelElement selected = myRhapsody.getSelectedElement();

			// operation...
			if (selected instanceof IRPOperation == true)
			{
				IRPModelElement selectedOwner = selected.getOwner();
				if (selectedOwner instanceof IRPClass)
				{
					selectedClass = (IRPClass) selectedOwner;
				}
			}
			else if (selected instanceof IRPClass == true)
			{
				selectedClass = (IRPClass) selected;
			}
			else
			{
				trace("No class selected!");
				return;
			}
		}

		// remove all compiler issues
		ASTHelper.deleteCompilerIssues(selectedClass, "compilerIssue");

		myRhapsody.generateElements(myRhapsody.getListOfSelectedElements());

		String path = ASTHelper.getSourcePath(selectedClass, myRhapsody);

		path = path + ".cpp";

		path = path.replace('/', '\\');

		String exe = myStartupDirectory + "\\VSControl.exe";

		mySolutionPath = mySolutionPath.replace('/', '\\');

		trace("SolutionPath: " + mySolutionPath);
		
		boolean viewTable = false;

		try
		{
			ProcessBuilder pb = new ProcessBuilder(exe, compileCmd, path, Integer.toString(0), mySolutionPath);
			Process process = pb.start();
			InputStream inputStream = process.getInputStream();
			// InputStream errorStream = process.getErrorStream();

			BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream));
			// BufferedReader errrorReader = new BufferedReader(new
			// InputStreamReader(errorStream));

			String inputLine;
			String errorLine;
			while ((inputLine = inputReader.readLine()) != null)
			{
				trace("VSControl.exe: " + inputLine);
				// search for input line starting with "#Error"
				if (inputLine.startsWith("#Error") == true)
				{
					// split the line on "|" into strings
					String[] parts = inputLine.split("\\|");
					String errorLevel = "Warning";
					if (parts[1].equals("vsBuildErrorLevelHigh") == true)
					{
						errorLevel = "Error";
					}

					System.out.println("Parts[1]: " + parts[1]);
					System.out.println("Parts[2]: " + parts[2]);
					System.out.println("Parts[3]: " + parts[3]);
					System.out.println("Parts[4]: " + parts[4]);
					
					viewTable = true;

					IRPComment issue = ASTHelper.createIssue(activeProject, parts[3], Integer.parseInt(parts[4]),
							errorLevel, parts[2], "CompilerIssue");
				}
			}

			int exitCode = process.waitFor();
			trace("VSControl Exit Code: " + exitCode);
		}
		catch (IOException | InterruptedException iox)
		{
			trace("Exception: " + iox.getMessage());
			iox.printStackTrace();
		}
		
		if (viewTable == true)
		{
			ASTHelper.viewCompilerIssues(activeProject);
		}

	}

}
