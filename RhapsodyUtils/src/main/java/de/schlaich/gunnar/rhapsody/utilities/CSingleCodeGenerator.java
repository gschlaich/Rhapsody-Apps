package de.schlaich.gunnar.rhapsody.utilities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPCollection;
import com.telelogic.rhapsody.core.IRPComponent;
import com.telelogic.rhapsody.core.IRPConfiguration;
import com.telelogic.rhapsody.core.IRPDependency;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPUnit;
import com.telelogic.rhapsody.core.RhapsodyAppServer;

/**
 * Generator class for creating a standalone project from a single package.
 * 
 * This class creates a new Rhapsody project that contains:
 * - The specified package (added as reference)
 * - All dependency packages (recursively resolved)
 * - A component with configuration for code generation
 * 
 * The generated project can be used to compile/generate code for a single
 * package in isolation, which is useful for:
 * - Faster incremental builds
 * - Testing individual packages
 * - Generating code without affecting the main project
 */
public class CSingleCodeGenerator
{
	public static final String GENERATOR_PROJECT_NAME = "SingleCodeGenProject";
	public static final String GENERATOR_COMPONENT_NAME = "SingleCodeGenComponent";
	public static final String GENERATOR_CONFIGURATION_NAME = "SingleCodeGenConfig";
	
	private Consumer<String> myTraceAction = null;
	private IRPApplication myMainApplication = null;
	private IRPApplication myGeneratorApplication = null;
	private IRPProject myGeneratorProject = null;
	private IRPComponent myComponent = null;
	private IRPConfiguration myConfiguration = null;
	
	private IRPPackage mySourcePackage = null;
	private Set<IRPPackage> myResolvedPackages = new HashSet<>();
	
	private String myProjectPath = null;
	private boolean myUseHiddenRhapsody = true;
	
	/**
	 * Create a new SingleCodeGenerator
	 * @param aApplication The main Rhapsody application
	 */
	public CSingleCodeGenerator(IRPApplication aApplication)
	{
		myMainApplication = aApplication;
		initProjectPath();
	}
	
	/**
	 * Create a new SingleCodeGenerator with trace output
	 * @param aApplication The main Rhapsody application
	 * @param aTraceAction Consumer for trace messages
	 */
	public CSingleCodeGenerator(IRPApplication aApplication, Consumer<String> aTraceAction)
	{
		this(aApplication);
		myTraceAction = aTraceAction;
	}
	
	private void initProjectPath()
	{
		// Default project path in test folder under USM_ROOT
		String usmRoot = System.getenv("USM_ROOT");
		if (usmRoot != null && !usmRoot.isEmpty())
		{
			myProjectPath = usmRoot + File.separator + "generated" + File.separator + GENERATOR_PROJECT_NAME;
		}
		else
		{
			// Fallback to temp directory if USM_ROOT is not set
			myProjectPath = System.getProperty("java.io.tmpdir") + File.separator + GENERATOR_PROJECT_NAME;
		}
	}
	
	private void trace(String aMessage)
	{
		if (myTraceAction == null)
		{
			System.out.println("[SingleCodeGenerator] " + aMessage);
			return;
		}
		
		aMessage = "SingleCodeGenerator: " + aMessage;
		myTraceAction.accept(aMessage);
	}
	
	/**
	 * Set custom path for the generator project
	 */
	public CSingleCodeGenerator setProjectPath(String aPath)
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
	 * Set whether to use a hidden Rhapsody instance (default: true)
	 */
	public CSingleCodeGenerator setUseHiddenRhapsody(boolean aUseHidden)
	{
		myUseHiddenRhapsody = aUseHidden;
		return this;
	}
	
	/**
	 * Get the generator Rhapsody application (hidden instance)
	 */
	public IRPApplication getGeneratorApplication()
	{
		return myGeneratorApplication;
	}
	
	/**
	 * Get the generator project
	 */
	public IRPProject getGeneratorProject()
	{
		return myGeneratorProject;
	}
	
	/**
	 * Get the configuration directory where code is generated
	 */
	public String getGeneratedCodeDirectory()
	{
		if (myConfiguration != null)
		{
			return myConfiguration.getDirectory(1, "");
		}
		return null;
	}
	
	/**
	 * Generate code for the specified package.
	 * Creates a new project, resolves dependencies, and generates the code.
	 * 
	 * @param aPackage The package to generate code for
	 * @return true if code generation was successful
	 */
	public boolean generate(IRPPackage aPackage)
	{
		if (aPackage == null)
		{
			trace("Package is null");
			return false;
		}
		
		mySourcePackage = aPackage;
		trace("Generating code for package: " + aPackage.getName());
		
		try
		{
			// Clean up any existing generator project
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
			
			// Start hidden Rhapsody instance if configured
			if (myUseHiddenRhapsody)
			{
				myGeneratorApplication = createHiddenRhapsodyApp();
				if (myGeneratorApplication == null)
				{
					trace("Failed to start hidden Rhapsody instance");
					return false;
				}
			}
			else
			{
				myGeneratorApplication = myMainApplication;
			}
			
			// Create the generator project
			if (!createProject())
			{
				return false;
			}
			
			// Resolve and add all dependencies
			myResolvedPackages.clear();
			resolveDependencies(aPackage);
			
			trace("Resolved " + myResolvedPackages.size() + " dependent packages");
			
			// Add the source package and all dependencies to the project
			if (!addPackagesToProject())
			{
				return false;
			}
			
			// Create component and configuration
			if (!createComponentAndConfiguration())
			{
				return false;
			}
			
			// Save the project
			myGeneratorProject.save();
			
			// Generate code
			if (!generateCode())
			{
				return false;
			}
			
			trace("Code generation completed successfully");
			trace("Generated code directory: " + getGeneratedCodeDirectory());
			return true;
		}
		catch (Exception e)
		{
			trace("Error during code generation: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Recursively resolve all dependencies of a package
	 */
	@SuppressWarnings("unchecked")
	private void resolveDependencies(IRPPackage aPackage)
	{
		if (aPackage == null || myResolvedPackages.contains(aPackage))
		{
			return;
		}
		
		// Add this package to resolved set
		myResolvedPackages.add(aPackage);
		trace("Resolving dependencies for: " + aPackage.getName());
		
		// Get all dependencies of this package
		List<IRPDependency> dependencies = aPackage.getDependencies().toList();
		
		for (IRPDependency dependency : dependencies)
		{
			IRPModelElement dependsOn = dependency.getDependsOn();
			
			if (dependsOn instanceof IRPPackage)
			{
				IRPPackage dependentPackage = (IRPPackage) dependsOn;
				trace("  Found dependency: " + dependentPackage.getName());
				
				// Recursively resolve dependencies of this package
				resolveDependencies(dependentPackage);
			}
		}
		
		// Also check nested packages
		List<IRPPackage> nestedPackages = aPackage.getPackages().toList();
		for (IRPPackage nested : nestedPackages)
		{
			resolveDependencies(nested);
		}
	}
	
	/**
	 * Create a hidden Rhapsody application instance
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
		ProcessBuilder processBuilder = new ProcessBuilder(rhapsodyExe.getAbsolutePath(), "-hiddenui");
		
		try
		{
			processBuilder.start();
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
			Thread.sleep(3000);
		}
		catch (InterruptedException e)
		{
			trace("Wait interrupted: " + e.getMessage());
			return null;
		}
		
		// Find the newly started Rhapsody instance
		List<String> idList = RhapsodyAppServer.getActiveRhapsodyApplicationIDList();
		trace("Found " + idList.size() + " Rhapsody instances");
		
		for (String id : idList)
		{
			IRPApplication app = RhapsodyAppServer.getActiveRhapsodyApplicationByID(id);
			if (app != null && app.activeProject() == null)
			{
				tempApp = app;
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
	
	/**
	 * Create the generator project
	 */
	private boolean createProject()
	{
		trace("Creating generator project: " + GENERATOR_PROJECT_NAME);
		
		try
		{
			myGeneratorApplication.createAndInsertProject(myProjectPath, GENERATOR_PROJECT_NAME);
			myGeneratorProject = myGeneratorApplication.activeProject();
			
			if (myGeneratorProject == null)
			{
				trace("Failed to create project");
				return false;
			}
			
			trace("Project created: " + myGeneratorProject.getName());
			return true;
		}
		catch (Exception e)
		{
			trace("Exception creating project: " + e.getMessage());
			return false;
		}
	}
	
	/**
	 * Add the source package and all resolved dependencies to the project
	 */
	private boolean addPackagesToProject()
	{
		trace("Adding packages to project...");
		
		try
		{
			for (IRPPackage pkg : myResolvedPackages)
			{
				// Get the file path of the package (SBS file)
				IRPUnit unit = pkg.getSaveUnit();
				if (unit == null)
				{
					trace("  Package has no save unit: " + pkg.getName());
					continue;
				}
				
				String filePath = unit.getCurrentDirectory() + File.separator + unit.getFilename();
				File packageFile = new File(filePath);
				
				if (!packageFile.exists())
				{
					trace("  Package file not found: " + filePath);
					continue;
				}
				
				trace("  Adding package: " + pkg.getName() + " from " + filePath);
				
				// Add the package as a reference
				myGeneratorApplication.addToModelEx(
					packageFile.getAbsolutePath(), 
					IRPApplication.AddToModel_Mode.AS_REFERENCE, 
					1,  // recursive
					0   // don't add dependencies automatically
				);
			}
			
			trace("All packages added to project");
			return true;
		}
		catch (Exception e)
		{
			trace("Exception adding packages: " + e.getMessage());
			return false;
		}
	}
	
	/**
	 * Create component and configuration for code generation
	 */
	private boolean createComponentAndConfiguration()
	{
		trace("Creating component and configuration...");
		
		try
		{
			// Create component
			myComponent = (IRPComponent) myGeneratorProject.addNewAggr("Component", GENERATOR_COMPONENT_NAME);
			
			if (myComponent == null)
			{
				trace("Failed to create component");
				return false;
			}
			
			// Add the source package to component scope
			myComponent.addScopeElement(mySourcePackage);
			trace("Added source package to component scope: " + mySourcePackage.getName());
			
			// Also add all dependency packages to scope
			for (IRPPackage pkg : myResolvedPackages)
			{
				if (pkg != mySourcePackage)
				{
					myComponent.addScopeElement(pkg);
					trace("Added dependency to component scope: " + pkg.getName());
				}
			}
			
			// Create configuration
			myConfiguration = (IRPConfiguration) myComponent.addNewAggr("Configuration", GENERATOR_CONFIGURATION_NAME);
			
			if (myConfiguration == null)
			{
				trace("Failed to create configuration");
				return false;
			}
			
			// Set as active component
			myGeneratorProject.setActiveComponent(myComponent);
			
			trace("Component and configuration created");
			return true;
		}
		catch (Exception e)
		{
			trace("Exception creating component: " + e.getMessage());
			return false;
		}
	}
	
	/**
	 * Generate code for all classes in the source package
	 */
	private boolean generateCode()
	{
		trace("Generating code...");
		
		try
		{
			// Save project before generation
			myGeneratorProject.save();
			
			// Generate code for the source package
			IRPCollection col = myGeneratorApplication.createNewCollection();
			col.addItem(mySourcePackage);
			
			myGeneratorApplication.generateElements(col);
			
			// Wait for generation to complete
			Thread.sleep(3000);
			
			trace("Code generation completed");
			return true;
		}
		catch (Exception e)
		{
			trace("Exception during code generation: " + e.getMessage());
			return false;
		}
	}
	
	/**
	 * Clean up the generator project - close project, quit hidden Rhapsody, and delete files
	 */
	public void cleanup()
	{
		trace("Cleaning up generator project...");
		
		try
		{
			// Close the project
			if (myGeneratorProject != null)
			{
				try
				{
					myGeneratorProject.save();
					myGeneratorProject.close();
				}
				catch (Exception e)
				{
					trace("Error closing project: " + e.getMessage());
				}
				myGeneratorProject = null;
			}
			
			// Quit hidden Rhapsody instance
			if (myUseHiddenRhapsody && myGeneratorApplication != null)
			{
				try
				{
					trace("Quitting hidden Rhapsody instance");
					myGeneratorApplication.quit();
				}
				catch (Exception e)
				{
					trace("Error quitting Rhapsody: " + e.getMessage());
				}
				myGeneratorApplication = null;
			}
			
			// Wait for Rhapsody to close
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
			myComponent = null;
			myConfiguration = null;
			myResolvedPackages.clear();
			
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
	 * Get all resolved dependency packages
	 */
	public Set<IRPPackage> getResolvedPackages()
	{
		return new HashSet<>(myResolvedPackages);
	}
}