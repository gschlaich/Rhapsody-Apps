package de.schlaich.gunnar.rhapsody.test;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;

import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPComponent;
import com.telelogic.rhapsody.core.IRPConfiguration;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPOperation;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPProject;

import de.schlaich.gunnar.rhapsody.test.RhapsodyTestRunner.TestResult;
import de.schlaich.gunnar.rhapsody.utilities.ASTHelper;
import de.schlaich.gunnar.rhapsody.utilities.ASTHelper.SourceLocation;

/**
 * Integration tests for USMPlugin commands.
 * 
 * This test suite creates its own Rhapsody test project to ensure
 * reproducible and reliable test results. The test project contains:
 * - A test package with a test class
 * - Multiple operations for testing different scenarios
 * - A component with configuration for code generation
 * 
 * Run this class to execute all plugin tests.
 */
public class PluginCommandTests
{
	private final RhapsodyTestRunner runner;
	private Consumer<String> myTraceAction = null;
	private TestProjectBuilder myTestProjectBuilder = null;
	private boolean myCleanupAfterTests = true;
	
	public PluginCommandTests()
	{
		runner = new RhapsodyTestRunner();
		registerTests();
	}
	
	public PluginCommandTests(Consumer<String> aTraceAction)
	{
		myTraceAction = aTraceAction;
		runner = new RhapsodyTestRunner();
		runner.setLogAction(msg -> trace(msg));
		registerTests();
	}
	
	/**
	 * Set whether to cleanup the test project after tests complete
	 */
	public void setCleanupAfterTests(boolean aCleanup)
	{
		myCleanupAfterTests = aCleanup;
	}
	
	private void trace(String aMessage)
	{
		if (myTraceAction == null)
		{
			// no traceaction set...
			System.out.println("[PluginTests] " + aMessage);
			return;
		}
		
		aMessage = "PluginTests: " + aMessage;
		myTraceAction.accept(aMessage);
	}
	
	/**
	 * Setup the test project before running tests.
	 * Creates a completely new, independent test project using createAndInsertProject.
	 */
	private boolean setupTestProject(IRPApplication app)
	{
		trace("Setting up new independent test project...");
		
		myTestProjectBuilder = new TestProjectBuilder(app, myTraceAction);
		
		// Build new test project
		if (!myTestProjectBuilder.build())
		{
			trace("Failed to build test project");
			return false;
		}
		
		trace("Test project created at: " + myTestProjectBuilder.getProjectPath());
		return true;
	}
	
	/**
	 * Cleanup test project after tests - closes project and deletes files
	 */
	private void cleanupTestProject()
	{
		if (myTestProjectBuilder != null && myCleanupAfterTests)
		{
			trace("Cleaning up test project and deleting files...");
			myTestProjectBuilder.cleanup();
			trace("Cleanup complete - test project removed");
		}
		else if (!myCleanupAfterTests)
		{
			trace("Skipping cleanup (cleanup disabled) - test project remains at: " + 
				(myTestProjectBuilder != null ? myTestProjectBuilder.getProjectPath() : "unknown"));
		}
	}
	
	private void registerTests()
	{
		// Unit tests (no Rhapsody required)
		registerUnitTests();
		
		// Integration tests (Rhapsody required)
		registerIntegrationTests();
	}
	
	private void registerUnitTests()
	{
		runner.addUnitTest(
			"SourceLocation_Creation",
			"Test SourceLocation class creation and getters",
			app -> {
				SourceLocation loc = new SourceLocation("C:\\test\\file.cpp", 42);
				
				if (!"C:\\test\\file.cpp".equals(loc.getFilePath()))
				{
					return TestResult.failure("SourceLocation_Creation", "FilePath mismatch", 0);
				}
				if (loc.getLineNumber() != 42)
				{
					return TestResult.failure("SourceLocation_Creation", "LineNumber mismatch", 0);
				}
				
				return TestResult.success("SourceLocation_Creation", "SourceLocation created correctly", 0);
			}
		);
		
		runner.addUnitTest(
			"SourceLocation_ToString",
			"Test SourceLocation toString format",
			app -> {
				SourceLocation loc = new SourceLocation("C:\\test\\file.cpp", 42);
				String expected = "C:\\test\\file.cpp:42";
				
				if (!expected.equals(loc.toString()))
				{
					return TestResult.failure("SourceLocation_ToString", 
						"Expected: " + expected + ", Got: " + loc.toString(), 0);
				}
				
				return TestResult.success("SourceLocation_ToString", "toString format correct", 0);
			}
		);
		
		runner.addUnitTest(
			"ASTHelper_GetLines",
			"Test ASTHelper.getLines method",
			app -> {
				List<String> lines = ASTHelper.getLines("line1\nline2\nline3", false);
				
				if (lines.size() != 3)
				{
					return TestResult.failure("ASTHelper_GetLines", 
						"Expected 3 lines, got " + lines.size(), 0);
				}
				
				return TestResult.success("ASTHelper_GetLines", "getLines works correctly", 0);
			}
		);
		
		runner.addUnitTest(
			"ASTHelper_GetLines_TabConversion",
			"Test tab to space conversion",
			app -> {
				List<String> lines = ASTHelper.getLines("\tindented", true);
				
				if (!lines.get(0).equals("    indented"))
				{
					return TestResult.failure("ASTHelper_GetLines_TabConversion", 
						"Tab conversion failed: " + lines.get(0), 0);
				}
				
				return TestResult.success("ASTHelper_GetLines_TabConversion", "Tab conversion works", 0);
			}
		);
		
		runner.addUnitTest(
			"ASTHelper_NullParameters",
			"Test getOperationSourceLocation with null parameters",
			app -> {
				SourceLocation result1 = ASTHelper.getOperationSourceLocation(null, app);
				if (result1 != null)
				{
					return TestResult.failure("ASTHelper_NullParameters", 
						"Should return null for null operation", 0);
				}
				
				return TestResult.success("ASTHelper_NullParameters", "Null handling correct", 0);
			}
		);
	}
	
	private void registerIntegrationTests()
	{
		// Setup test - this must run first
		runner.addTest(
			"Setup_TestProject",
			"Create and setup the test project for subsequent tests",
			app -> 
			{
				if (!setupTestProject(app))
				{
					return TestResult.failure("Setup_TestProject", 
						"Failed to setup test project", 0);
				}
				
				// Verify project was created correctly
				if (myTestProjectBuilder.getProject() == null)
				{
					return TestResult.failure("Setup_TestProject", 
						"Project is null after setup", 0);
				}
				
				if (myTestProjectBuilder.getTestClass() == null)
				{
					return TestResult.failure("Setup_TestProject", 
						"TestClass is null after setup", 0);
				}
				
				return TestResult.success("Setup_TestProject", 
					"Test project created: " + myTestProjectBuilder.getProjectPath(), 0);
			}
		);
		
		runner.addTest(
			"GetOperationSourceLocation_SimpleOperation",
			"Find source location of simpleOperation in test class",
			app -> 
			{
				if (myTestProjectBuilder == null || myTestProjectBuilder.getTestClass() == null)
				{
					return TestResult.failure("GetOperationSourceLocation_SimpleOperation", 
						"Test project not setup - run Setup_TestProject first", 0);
				}
				
				IRPOperation operation = myTestProjectBuilder.getOperationByName("simpleOperation");
				if (operation == null)
				{
					return TestResult.failure("GetOperationSourceLocation_SimpleOperation", 
						"Could not find simpleOperation in test class", 0);
				}
				
				SourceLocation location = ASTHelper.getOperationSourceLocation(operation, app);
				
				if (location == null)
				{
					return TestResult.failure("GetOperationSourceLocation_SimpleOperation", 
						"Could not find source location for simpleOperation", 0);
				}
				
				// Verify file exists
				File sourceFile = new File(location.getFilePath());
				if (!sourceFile.exists())
				{
					return TestResult.failure("GetOperationSourceLocation_SimpleOperation", 
						"Source file does not exist: " + location.getFilePath(), 0);
				}
				
				// Verify line number is positive
				if (location.getLineNumber() <= 0)
				{
					return TestResult.failure("GetOperationSourceLocation_SimpleOperation", 
						"Invalid line number: " + location.getLineNumber(), 0);
				}
				
				return TestResult.success("GetOperationSourceLocation_SimpleOperation", 
					"Found: " + location.toString(), 0);
			}
		);
		
		runner.addTest(
			"GetOperationSourceLocation_Constructor",
			"Find source location of constructor in test class",
			app -> 
			{
				if (myTestProjectBuilder == null || myTestProjectBuilder.getTestClass() == null)
				{
					return TestResult.failure("GetOperationSourceLocation_Constructor", 
						"Test project not setup", 0);
				}
				
				IRPOperation operation = myTestProjectBuilder.getOperationByName(TestProjectBuilder.TEST_CLASS_NAME);
				if (operation == null)
				{
					return TestResult.failure("GetOperationSourceLocation_Constructor", 
						"Could not find constructor in test class", 0);
				}
				
				SourceLocation location = ASTHelper.getOperationSourceLocation(operation, app);
				
				if (location == null)
				{
					return TestResult.failure("GetOperationSourceLocation_Constructor", 
						"Could not find source location for constructor", 0);
				}
				
				return TestResult.success("GetOperationSourceLocation_Constructor", 
					"Found constructor at: " + location.toString(), 0);
			}
		);
		
		runner.addTest(
			"GetOperationSourceLocation_OperationWithParams",
			"Find source location of operation with parameters",
			app -> 
			{
				if (myTestProjectBuilder == null || myTestProjectBuilder.getTestClass() == null)
				{
					return TestResult.failure("GetOperationSourceLocation_OperationWithParams", 
						"Test project not setup", 0);
				}
				
				IRPOperation operation = myTestProjectBuilder.getOperationByName("setValues");
				if (operation == null)
				{
					return TestResult.failure("GetOperationSourceLocation_OperationWithParams", 
						"Could not find setValues in test class", 0);
				}
				
				SourceLocation location = ASTHelper.getOperationSourceLocation(operation, app);
				
				if (location == null)
				{
					return TestResult.failure("GetOperationSourceLocation_OperationWithParams", 
						"Could not find source location for setValues", 0);
				}
				
				return TestResult.success("GetOperationSourceLocation_OperationWithParams", 
					"Found setValues at: " + location.toString(), 0);
			}
		);
		
		runner.addTest(
			"GetOperationSourceLocation_OperationWithReturn",
			"Find source location of operation with return value",
			app -> 
			{
				if (myTestProjectBuilder == null || myTestProjectBuilder.getTestClass() == null)
				{
					return TestResult.failure("GetOperationSourceLocation_OperationWithReturn", 
						"Test project not setup", 0);
				}
				
				IRPOperation operation = myTestProjectBuilder.getOperationByName("getCount");
				if (operation == null)
				{
					return TestResult.failure("GetOperationSourceLocation_OperationWithReturn", 
						"Could not find getCount in test class", 0);
				}
				
				SourceLocation location = ASTHelper.getOperationSourceLocation(operation, app);
				
				if (location == null)
				{
					return TestResult.failure("GetOperationSourceLocation_OperationWithReturn", 
						"Could not find source location for getCount", 0);
				}
				
				return TestResult.success("GetOperationSourceLocation_OperationWithReturn", 
					"Found getCount at: " + location.toString(), 0);
			}
		);
		
		runner.addTest(
			"GetSourcePath_TestClass",
			"Test getSourcePath with test class",
			app -> 
			{
				if (myTestProjectBuilder == null || myTestProjectBuilder.getTestClass() == null)
				{
					return TestResult.failure("GetSourcePath_TestClass", 
						"Test project not setup", 0);
				}
				
				IRPClass testClass = myTestProjectBuilder.getTestClass();
				String path = ASTHelper.getSourcePath(testClass, app);
				
				if (path == null)
				{
					return TestResult.failure("GetSourcePath_TestClass", 
						"Could not determine source path for: " + testClass.getName(), 0);
				}
				
				return TestResult.success("GetSourcePath_TestClass", 
					"Path: " + path, 0);
			}
		);
		
		runner.addTest(
			"VerifyGeneratedCode_FileExists",
			"Verify that generated code file exists",
			app -> 
			{
				if (myTestProjectBuilder == null || myTestProjectBuilder.getTestConfiguration() == null)
				{
					return TestResult.failure("VerifyGeneratedCode_FileExists", 
						"Test project not setup", 0);
				}
				
				String configDir = myTestProjectBuilder.getTestConfiguration().getDirectory(1, "");
				File cppFile = new File(configDir, TestProjectBuilder.TEST_CLASS_NAME + ".cpp");
				File hFile = new File(configDir, TestProjectBuilder.TEST_CLASS_NAME + ".h");
				
				StringBuilder result = new StringBuilder();
				boolean success = true;
				
				if (cppFile.exists())
				{
					result.append("Found: ").append(cppFile.getName());
				}
				else
				{
					result.append("Missing: ").append(cppFile.getAbsolutePath());
					success = false;
				}
				
				result.append(" | ");
				
				if (hFile.exists())
				{
					result.append("Found: ").append(hFile.getName());
				}
				else
				{
					result.append("Missing: ").append(hFile.getName());
					// Header might not always be required, don't fail for this
				}
				
				if (success)
				{
					return TestResult.success("VerifyGeneratedCode_FileExists", result.toString(), 0);
				}
				else
				{
					return TestResult.failure("VerifyGeneratedCode_FileExists", result.toString(), 0);
				}
			}
		);
		
		runner.addTest(
			"VerifyLineNumbers_AreDistinct",
			"Verify that different operations have different line numbers",
			app -> 
			{
				if (myTestProjectBuilder == null || myTestProjectBuilder.getTestClass() == null)
				{
					return TestResult.failure("VerifyLineNumbers_AreDistinct", 
						"Test project not setup", 0);
				}
				
				IRPOperation op1 = myTestProjectBuilder.getOperationByName("simpleOperation");
				IRPOperation op2 = myTestProjectBuilder.getOperationByName("getCount");
				IRPOperation op3 = myTestProjectBuilder.getOperationByName("complexOperation");
				
				if (op1 == null || op2 == null || op3 == null)
				{
					return TestResult.failure("VerifyLineNumbers_AreDistinct", 
						"Could not find all test operations", 0);
				}
				
				SourceLocation loc1 = ASTHelper.getOperationSourceLocation(op1, app);
				SourceLocation loc2 = ASTHelper.getOperationSourceLocation(op2, app);
				SourceLocation loc3 = ASTHelper.getOperationSourceLocation(op3, app);
				
				if (loc1 == null || loc2 == null || loc3 == null)
				{
					return TestResult.failure("VerifyLineNumbers_AreDistinct", 
						"Could not get source locations for all operations", 0);
				}
				
				// Verify line numbers are different
				if (loc1.getLineNumber() == loc2.getLineNumber() || 
					loc2.getLineNumber() == loc3.getLineNumber() ||
					loc1.getLineNumber() == loc3.getLineNumber())
				{
					return TestResult.failure("VerifyLineNumbers_AreDistinct", 
						"Line numbers are not distinct: " + 
						loc1.getLineNumber() + ", " + 
						loc2.getLineNumber() + ", " + 
						loc3.getLineNumber(), 0);
				}
				
				return TestResult.success("VerifyLineNumbers_AreDistinct", 
					"Lines: " + loc1.getLineNumber() + ", " + 
					loc2.getLineNumber() + ", " + loc3.getLineNumber(), 0);
			}
		);
		
		// Cleanup test - this should run last
		runner.addTest(
			"Cleanup_TestProject",
			"Cleanup the test project after all tests",
			app -> 
			{
				cleanupTestProject();
				return TestResult.success("Cleanup_TestProject", "Cleanup completed", 0);
			}
		);
	}
	
	public List<TestResult> runAll()
	{
		return runner.runAllTests();
	}
	
	public boolean allPassed()
	{
		return runner.allTestsPassed();
	}
	
	public String getSummary()
	{
		return runner.getSummary();
	}
	
	/**
	 * Main entry point for running tests from command line or IDE
	 */
	public static void main(String[] args)
	{
		System.out.println("Starting Plugin Command Tests...");
		System.out.println();
		
		PluginCommandTests tests = new PluginCommandTests();
		tests.runAll();
		
		System.out.println();
		System.out.println(tests.getSummary());
		
		// Exit with appropriate code
		System.exit(tests.allPassed() ? 0 : 1);
	}
}
