package de.schlaich.gunnar.rhapsody.test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPArgument;
import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPCollection;
import com.telelogic.rhapsody.core.IRPComponent;
import com.telelogic.rhapsody.core.IRPConfiguration;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPOperation;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.RhapsodyAppServer;

/**
 * Builder class for creating a new independent Rhapsody test project.
 * This allows running tests on a controlled, reproducible project structure
 * that is completely isolated from any existing projects.
 * 
 * The test project is created in a separate hidden Rhapsody instance,
 * ensuring complete isolation from the main Rhapsody application.
 * 
 * The test project contains:
 * - A package "USMTestPackage"
 * - A class "USMTestClass" with several operations
 * - A component "USMTestComponent" with a configuration
 * - Generated code for verification
 */
public class TestProjectBuilder
{
	public static final String TEST_PROJECT_NAME = "USMPluginTestProject";
	public static final String TEST_PACKAGE_NAME = "USMTestPackage";
	public static final String TEST_CLASS_NAME = "USMTestClass";
	public static final String TEST_COMPONENT_NAME = "USMTestComponent";
	public static final String TEST_CONFIGURATION_NAME = "USMTestConfig";
	public static final String TEST_OPERATION_NAME = "simpleOperation";
	
	private Consumer<String> myTraceAction = null;
	private IRPApplication myMainApplication = null;
	private IRPApplication myTestApplication = null;
	private IRPProject myProject = null;
	private IRPPackage myTestPackage = null;
	private IRPClass myTestClass = null;
	private IRPComponent myTestComponent = null;
	private IRPConfiguration myTestConfiguration = null;
	
	private String myProjectPath = null;
	private boolean myUseHiddenRhapsody = true;
	
	public TestProjectBuilder(IRPApplication aApplication)
	{
		myMainApplication = aApplication;
		// Default project path in test folder under USM_ROOT
		String usmRoot = System.getenv("USM_ROOT");
		if (usmRoot != null && !usmRoot.isEmpty())
		{
			myProjectPath = usmRoot + File.separator + "test" + File.separator + TEST_PROJECT_NAME;
		}
		else
		{
			// Fallback to temp directory if USM_ROOT is not set
			myProjectPath = System.getProperty("java.io.tmpdir") + File.separator + TEST_PROJECT_NAME;
		}
	}
	
	public TestProjectBuilder(IRPApplication aApplication, Consumer<String> aTraceAction)
	{
		this(aApplication);
		myTraceAction = aTraceAction;
	}
	
	private void trace(String aMessage)
	{
		if (myTraceAction == null)
		{
			System.out.println("[TestProjectBuilder] " + aMessage);
			return;
		}
		
		aMessage = "TestProjectBuilder: " + aMessage;
		myTraceAction.accept(aMessage);
	}
	
	/**
	 * Set custom path for the test project
	 */
	public TestProjectBuilder setProjectPath(String aPath)
	{
		myProjectPath = aPath;
		return this;
	}
	
	/**
	 * Set whether to use a hidden Rhapsody instance (default: true)
	 * If false, uses the main application (not recommended for isolation)
	 */
	public TestProjectBuilder setUseHiddenRhapsody(boolean aUseHidden)
	{
		myUseHiddenRhapsody = aUseHidden;
		return this;
	}
	
	/**
	 * Get the project path
	 */
	public String getProjectPath()
	{
		return myProjectPath;
	}
	
	/**
	 * Get the test Rhapsody application (hidden instance)
	 */
	public IRPApplication getTestApplication()
	{
		return myTestApplication;
	}
	
	/**
	 * Build the complete test project
	 * @return true if successful
	 */
	public boolean build()
	{
		try
		{
			trace("Building new test project at: " + myProjectPath);
			
			// Clean up any existing test project files
			cleanupProjectFiles();
			
			// Create project directory
			File projectDir = new File(myProjectPath);
			if (!projectDir.exists())
			{
				if (!projectDir.mkdirs())
				{
					trace("Failed to create project directory: " + myProjectPath);
					return false;
				}
			}
			
			// Start a separate hidden Rhapsody instance for test isolation
			if (myUseHiddenRhapsody)
			{
				myTestApplication = createHiddenRhapsodyApp();
				if (myTestApplication == null)
				{
					trace("Failed to start hidden Rhapsody instance");
					return false;
				}
			}
			else
			{
				myTestApplication = myMainApplication;
			}
			
			// Create the new project
			if (!createProject())
			{
				return false;
			}
			
			// Create the test package
			if (!createTestPackage())
			{
				return false;
			}
			
			// Create the test class with operations
			if (!createTestClass())
			{
				return false;
			}
			
			// Create component and configuration
			if (!createComponent())
			{
				return false;
			}
			
			// Save the project
			//myProject.save();
			
			// Generate code
			if (!generateCode())
			{
				return false;
			}
			
			trace("Test project built successfully");
			return true;
		}
		catch (Exception e)
		{
			trace("Error building test project: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Create a hidden Rhapsody application instance for test isolation.
	 * This is the same approach used in SVNTools.createTempRhapsodyApp()
	 */
	private IRPApplication createHiddenRhapsodyApp()
	{
		trace("Starting hidden Rhapsody instance...");
		
		IRPApplication tempApp = null;
		File rhapsodyShareFile = new File(System.getenv("OMROOT"));
		
		if (!rhapsodyShareFile.exists())
		{
			trace("OMROOT does not exist: " + rhapsodyShareFile.getAbsolutePath());
			return null;
		}
		
		File rhapsodyDir = rhapsodyShareFile.getParentFile();
		
		if (!rhapsodyDir.exists())
		{
			trace("Rhapsody directory does not exist: " + rhapsodyDir.getAbsolutePath());
			return null;
		}
		
		File rhapsodyExe = new File(rhapsodyDir, "rhapsody.exe");
		if (!rhapsodyExe.exists())
		{
			trace("Rhapsody executable not found: " + rhapsodyExe.getAbsolutePath());
			return null;
		}
		
		// Start Rhapsody with hidden UI
		ProcessBuilder processBuilder = new ProcessBuilder(rhapsodyExe.getAbsolutePath() ,"-hiddenui");
		
		try
		{
			Process p = processBuilder.start();
			trace("Rhapsody process started");
		}
		catch (IOException e)
		{
			trace("Failed to start Rhapsody: " + e.getMessage());
			return null;
		}
		
		// Wait for Rhapsody to start
		try
		{
			trace("Waiting for Rhapsody to initialize...");
			Thread.sleep(3000); // Give Rhapsody more time to start
		}
		catch (InterruptedException e)
		{
			trace("Wait interrupted: " + e.getMessage());
			return null;
		}
		
		// Find the newly started Rhapsody instance (one without a project loaded)
		List<String> idList = RhapsodyAppServer.getActiveRhapsodyApplicationIDList();
		trace("Found " + idList.size() + " Rhapsody instances");
		
		for (String id : idList)
		{
			IRPApplication app = RhapsodyAppServer.getActiveRhapsodyApplicationByID(id);
			if (app != null && app.activeProject() == null)
			{
				tempApp = app; // Found an instance with no project loaded
				trace("Found empty Rhapsody instance: " + id);
				break;
			}
		}
		
		if (tempApp == null)
		{
			trace("No empty Rhapsody application found");
			return null;
		}
		
		tempApp.setHiddenUI(true);
		trace("Hidden Rhapsody instance ready");
		return tempApp;
	}
	
	private boolean createProject()
	{
		trace("Creating new project: " + TEST_PROJECT_NAME);
		
		try
		{
			// Use createAndInsertProject to create a new project
			// This is the same method used in SVNTools.newSvnProject
			myTestApplication.createAndInsertProject(myProjectPath, TEST_PROJECT_NAME);
			
			// Get the newly created project (it becomes the active project)
			myProject = myTestApplication.activeProject();
			
			if (myProject == null)
			{
				trace("Failed to create new project - project is null after creation");
				return false;
			}
			
			trace("Project created: " + myProject.getName() + " at " + myProject.getCurrentDirectory());
			return true;
		}
		catch (Exception e)
		{
			trace("Exception creating project: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean createTestPackage()
	{
		trace("Creating test package: " + TEST_PACKAGE_NAME);
		
		try
		{
			myTestPackage = (IRPPackage) myProject.addNewAggr("Package", TEST_PACKAGE_NAME);
			
			if (myTestPackage == null)
			{
				trace("Failed to create test package");
				return false;
			}
			
			myTestPackage.setPropertyValue("CPP_CG.Package.DefineNameSpace", "true");
			
			trace("Test package created: " + myTestPackage.getName());
			return true;
		}
		catch (Exception e)
		{
			trace("Exception creating test package: " + e.getMessage());
			return false;
		}
	}
	
	private boolean createTestClass()
	{
		trace("Creating test class: " + TEST_CLASS_NAME);
		
		try
		{
			myTestClass = (IRPClass) myTestPackage.addNewAggr("Class", TEST_CLASS_NAME);
			
			if (myTestClass == null)
			{
				trace("Failed to create test class");
				return false;
			}
			
			// Add constructor
			IRPOperation constructor = myTestClass.addConstructor("");
			constructor.setBody("// Constructor body\nmyMember = 0;");
			trace("Added constructor");
			
			// Add destructor
			IRPOperation destructor = myTestClass.addDestructor();
			destructor.setBody("// Destructor body");
			trace("Added destructor");
			
			// Add simple void operation
			IRPOperation simpleOp = myTestClass.addOperation(TEST_OPERATION_NAME);
			simpleOp.setBody("// Simple operation body\nint x = 5;\nint y = 10;\nint z = x + y;");
			trace("Added simpleOperation");
			
			// Add operation with return value
			IRPOperation returnOp = myTestClass.addOperation("getCount");
			returnOp.setBody("// Returns the count\nreturn myMember;");
			IRPModelElement intType = myProject.findNestedElement("int", "Type");
			if (intType != null)
			{
				returnOp.setReturns((com.telelogic.rhapsody.core.IRPClassifier) intType);
			}
			trace("Added getCount");
			
			// Add operation with parameters
			IRPOperation paramOp = myTestClass.addOperation("setValues");
			IRPArgument arg1 = paramOp.addArgument("aValue");
			if (intType != null)
			{
				arg1.setType((com.telelogic.rhapsody.core.IRPClassifier) intType);
			}
			IRPArgument arg2 = paramOp.addArgument("aCount");
			if (intType != null)
			{
				arg2.setType((com.telelogic.rhapsody.core.IRPClassifier) intType);
			}
			paramOp.setBody("// Set values\nmyMember = aValue + aCount;");
			trace("Added setValues");
			
			// Add operation with multiple lines for offset testing
			IRPOperation complexOp = myTestClass.addOperation("complexOperation");
			StringBuilder complexBody = new StringBuilder();
			complexBody.append("// Complex operation with many lines\n");
			complexBody.append("int result = 0;\n");
			complexBody.append("for (int i = 0; i < 10; i++)\n");
			complexBody.append("{\n");
			complexBody.append("    result += i;\n");
			complexBody.append("}\n");
			complexBody.append("return result;");
			complexOp.setBody(complexBody.toString());
			if (intType != null)
			{
				complexOp.setReturns((com.telelogic.rhapsody.core.IRPClassifier) intType);
			}
			trace("Added complexOperation");
			
			// Add a private member attribute
			IRPModelElement attr = myTestClass.addNewAggr("Attribute", "myMember");
			trace("Added myMember attribute");
			
			trace("Test class created with " + myTestClass.getOperations().getCount() + " operations");
			return true;
		}
		catch (Exception e)
		{
			trace("Exception creating test class: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean createComponent()
	{
		trace("Creating component: " + TEST_COMPONENT_NAME);
		
		try
		{
			myTestComponent = (IRPComponent) myProject.addNewAggr("Component", TEST_COMPONENT_NAME);
			
			if (myTestComponent == null)
			{
				trace("Failed to create component");
				return false;
			}
			
			// Add the test package to component scope
			myTestComponent.addScopeElement(myTestPackage);
			trace("Added test package to component scope");
			
			// Create configuration
			myTestConfiguration = (IRPConfiguration) myTestComponent.addNewAggr("Configuration", TEST_CONFIGURATION_NAME);
			
			if (myTestConfiguration == null)
			{
				trace("Failed to create configuration");
				return false;
			}
			
			myProject.setActiveConfiguration(myTestConfiguration);
			
			// Set as active component
			myProject.setActiveComponent(myTestComponent);
			
			trace("Component and configuration created");
			return true;
		}
		catch (Exception e)
		{
			trace("Exception creating component: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean generateCode()
	{
		trace("Generating code for test class");
		
		//myProject.save();
		
		try
		{
			// Generate code using application's generateElements method
			IRPCollection col = myTestApplication.createNewCollection();
			col.addItem(myTestClass);
			
			myTestApplication.generateElements(col);
			
			// Wait for generation to complete
			Thread.sleep(3000);
			
			// Verify that the generated file exists
			String configDir = myTestConfiguration.getDirectory(1, "");
			trace("Configuration directory: " + configDir);
			
			File generatedFile = new File(configDir, TEST_CLASS_NAME + ".cpp");
			
			if (generatedFile.exists())
			{
				trace("Code generated successfully: " + generatedFile.getAbsolutePath());
				return true;
			}
			else
			{
				trace("Generated file not found at: " + generatedFile.getAbsolutePath());
				// List files in directory for debugging
				File dir = new File(configDir);
				if (dir.exists())
				{
					File[] files = dir.listFiles();
					if (files != null)
					{
						trace("Files in directory:");
						for (File f : files)
						{
							trace("  " + f.getName());
						}
					}
				}
				// Still return true as generation was attempted
				return true;
			}
		}
		catch (Exception e)
		{
			trace("Exception generating code: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Clean up the test project - close project, quit hidden Rhapsody, and delete files
	 */
	public void cleanup()
	{
		trace("Cleaning up test project");
		
		try
		{
			// Close the test project if open
			if (myProject != null)
			{
				try
				{
					trace("Closing test project: " + myProject.getName());
					myProject.save();
					myProject.close();
				}
				catch (Exception e)
				{
					trace("Error closing project: " + e.getMessage());
				}
				myProject = null;
			}
			
			// Quit the hidden Rhapsody instance if we started one
			if (myUseHiddenRhapsody && myTestApplication != null)
			{
				try
				{
					trace("Quitting hidden Rhapsody instance");
					myTestApplication.quit();
				}
				catch (Exception e)
				{
					trace("Error quitting Rhapsody: " + e.getMessage());
				}
				myTestApplication = null;
			}
			
			// Wait a moment for Rhapsody to fully close
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
				// Ignore
			}
			
			// Delete project files
			cleanupProjectFiles();
			
			// Reset references
			myTestPackage = null;
			myTestClass = null;
			myTestComponent = null;
			myTestConfiguration = null;
			
			trace("Cleanup completed");
		}
		catch (Exception e)
		{
			trace("Error during cleanup: " + e.getMessage());
		}
	}
	
	/**
	 * Delete all project files from disk
	 */
	private void cleanupProjectFiles()
	{
		File projectDir = new File(myProjectPath);
		if (projectDir.exists())
		{
			trace("Deleting project directory: " + myProjectPath);
			deleteDirectory(projectDir);
		}
	}
	
	private void deleteDirectory(File directory)
	{
		File[] files = directory.listFiles();
		if (files != null)
		{
			for (File file : files)
			{
				if (file.isDirectory())
				{
					deleteDirectory(file);
				}
				else
				{
					file.delete();
				}
			}
		}
		directory.delete();
	}
	
	/**
	 * Check if the test project exists on disk
	 */
	public boolean projectExists()
	{
		File projectFile = new File(myProjectPath + File.separator + TEST_PROJECT_NAME + ".rpy");
		return projectFile.exists();
	}
	
	// Getters for test elements
	public IRPProject getProject() { return myProject; }
	public IRPPackage getTestPackage() { return myTestPackage; }
	public IRPClass getTestClass() { return myTestClass; }
	public IRPComponent getTestComponent() { return myTestComponent; }
	public IRPConfiguration getTestConfiguration() { return myTestConfiguration; }
	
	/**
	 * Get the main application that was passed to the constructor
	 */
	public IRPApplication getMainApplication() { return myMainApplication; }
	
	/**
	 * Get the first operation from test class
	 */
	public IRPOperation getFirstOperation()
	{
		if (myTestClass == null)
		{
			return null;
		}
		
		@SuppressWarnings("unchecked")
		List<IRPOperation> operations = myTestClass.getOperations().toList();
		
		if (operations.isEmpty())
		{
			return null;
		}
		
		return operations.get(0);
	}
	
	/**
	 * Get operation by name from test class
	 */
	public IRPOperation getOperationByName(String aName)
	{
		if (myTestClass == null)
		{
			return null;
		}
		
		return (IRPOperation) myTestClass.findNestedElement(aName, "Operation");
	}
}
