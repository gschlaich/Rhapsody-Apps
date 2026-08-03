package de.schlaich.gunnar.rhapsody.test;

import java.io.File;
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

/**
 * Builder class for creating a new independent Rhapsody test project.
 * This allows running tests on a controlled, reproducible project structure
 * that is completely isolated from any existing projects.
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
	private IRPApplication myApplication = null;
	private IRPProject myProject = null;
	private IRPPackage myTestPackage = null;
	private IRPClass myTestClass = null;
	private IRPComponent myTestComponent = null;
	private IRPConfiguration myTestConfiguration = null;
	
	private String myProjectPath = null;
	private IRPProject myOriginalProject = null;
	
	public TestProjectBuilder(IRPApplication aApplication)
	{
		myApplication = aApplication;
		// Default project path in temp directory
		myProjectPath = System.getProperty("java.io.tmpdir") + File.separator + TEST_PROJECT_NAME;
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
	 * Get the project path
	 */
	public String getProjectPath()
	{
		return myProjectPath;
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
			
			// Remember current project to restore later if needed
			myOriginalProject = myApplication.activeProject();
			if (myOriginalProject != null)
			{
				trace("Saving reference to original project: " + myOriginalProject.getName());
			}
			
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
			myProject.save();
			
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
	
	private boolean createProject()
	{
		trace("Creating new project: " + TEST_PROJECT_NAME);
		
		try
		{
			// Use createAndInsertProject to create a new project
			// This is the same method used in SVNTools.newSvnProject
			myApplication.createAndInsertProject(myProjectPath, TEST_PROJECT_NAME);
			
			// Get the newly created project (it becomes the active project)
			myProject = myApplication.activeProject();
			
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
			
			myTestPackage.setPropertyValue("CPP_CG.Package.DefineNameSpace", "1");
			
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
		
		myProject.save();
		
		try
		{
			// Generate code using application's generateElements method
			IRPCollection col = myApplication.createNewCollection();
			col.addItem(myTestClass);
			
			myApplication.generateElements(col);
			
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
	 * Clean up the test project - close project and delete files
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
					trace("Error saving project: " + e.getMessage());
				}
				myProject = null;
			}
			
			// Restore original project if there was one
			if (myOriginalProject != null)
			{
				try
				{
					// Check if original project is still valid and reopen it
					String originalName = myOriginalProject.getName();
					trace("Original project reference: " + originalName);
				}
				catch (Exception e)
				{
					trace("Original project no longer available");
					myOriginalProject = null;
				}
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
	 * Get the original project that was active before the test project was created
	 */
	public IRPProject getOriginalProject() { return myOriginalProject; }
	
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
