package de.schlaich.gunnar.rhapsody.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.RhapsodyAppServer;

/**
 * Test framework for Rhapsody plugin commands.
 * 
 * Supports two modes:
 * 1. Integration mode: Tests with running Rhapsody instance
 * 2. Mock mode: Tests without Rhapsody using recorded results
 * 
 * Usage:
 * <pre>
 * RhapsodyTestRunner runner = new RhapsodyTestRunner();
 * runner.addTest("GetOperationLocation", () -> {
 *     // Test code here 
 *     return testResult;
 * });
 * runner.runAllTests();
 * </pre>
 */
public class RhapsodyTestRunner
{
	private final List<TestCase> testCases = new ArrayList<>();
	private final List<TestResult> results = new ArrayList<>();
	private IRPApplication application;
	private Consumer<String> logAction;
	private boolean verboseMode = true;
	
	/**
	 * Represents a single test case
	 */
	public static class TestCase
	{
		private final String name;
		private final String description;
		private final TestAction action;
		private final boolean requiresRhapsody;
		
		public TestCase(String name, String description, TestAction action, boolean requiresRhapsody)
		{
			this.name = name;
			this.description = description;
			this.action = action;
			this.requiresRhapsody = requiresRhapsody;
		}
		
		public String getName() { return name; }
		public String getDescription() { return description; }
		public TestAction getAction() { return action; }
		public boolean requiresRhapsody() { return requiresRhapsody; }
	}
	
	/**
	 * Functional interface for test actions
	 */
	@FunctionalInterface
	public interface TestAction
	{
		TestResult execute(IRPApplication app) throws Exception;
	}
	
	/**
	 * Result of a test execution
	 */
	public static class TestResult
	{
		private final String testName;
		private final boolean success;
		private final String message;
		private final long executionTimeMs;
		private final Exception exception;
		
		public TestResult(String testName, boolean success, String message, long executionTimeMs)
		{
			this(testName, success, message, executionTimeMs, null);
		}
		
		public TestResult(String testName, boolean success, String message, long executionTimeMs, Exception exception)
		{
			this.testName = testName;
			this.success = success;
			this.message = message;
			this.executionTimeMs = executionTimeMs;
			this.exception = exception;
		}
		
		public String getTestName() { return testName; }
		public boolean isSuccess() { return success; }
		public String getMessage() { return message; }
		public long getExecutionTimeMs() { return executionTimeMs; }
		public Exception getException() { return exception; }
		
		public static TestResult success(String testName, String message, long timeMs)
		{
			return new TestResult(testName, true, message, timeMs);
		}
		
		public static TestResult failure(String testName, String message, long timeMs)
		{
			return new TestResult(testName, false, message, timeMs);
		}
		
		public static TestResult failure(String testName, Exception e, long timeMs)
		{
			return new TestResult(testName, false, e.getMessage(), timeMs, e);
		}
	}
	
	public RhapsodyTestRunner()
	{
		this.logAction = System.out::println;
	}
	
	public void setLogAction(Consumer<String> logAction)
	{
		this.logAction = logAction;
	}
	
	public void setVerboseMode(boolean verbose)
	{
		this.verboseMode = verbose;
	}
	
	private void log(String message)
	{
		if (logAction != null)
		{
			logAction.accept(message);
		}
	}
	
	/**
	 * Helper method to repeat a character (Java 8 compatible)
	 */
	private static String repeatChar(char c, int count)
	{
		StringBuilder sb = new StringBuilder(count);
		for (int i = 0; i < count; i++)
		{
			sb.append(c);
		}
		return sb.toString();
	}
	
	/**
	 * Connect to running Rhapsody instance
	 */
	public boolean connectToRhapsody()
	{
		try
		{
			application = RhapsodyAppServer.getActiveRhapsodyApplication();
			if (application != null)
			{
				log("Connected to Rhapsody: " + application.activeProject().getName());
				return true;
			}
		}
		catch (Exception e)
		{
			log("Failed to connect to Rhapsody: " + e.getMessage());
		}
		return false;
	}
	
	public IRPApplication getApplication()
	{
		return application;
	}
	
	/**
	 * Add a test case that requires Rhapsody
	 */
	public void addTest(String name, String description, TestAction action)
	{
		testCases.add(new TestCase(name, description, action, true));
	}
	
	/**
	 * Add a test case that doesn't require Rhapsody
	 */
	public void addUnitTest(String name, String description, TestAction action)
	{
		testCases.add(new TestCase(name, description, action, false));
	}
	
	/**
	 * Run all registered tests
	 */
	public List<TestResult> runAllTests()
	{
		results.clear();
		
		boolean rhapsodyAvailable = (application != null) || connectToRhapsody();
		
		log(repeatChar('=', 60));
		log("Running " + testCases.size() + " tests");
		log("Rhapsody available: " + rhapsodyAvailable);
		log(repeatChar('=', 60));
		
		int passed = 0;
		int failed = 0;
		int skipped = 0;
		
		for (TestCase testCase : testCases)
		{
			if (testCase.requiresRhapsody() && !rhapsodyAvailable)
			{
				log("[SKIP] " + testCase.getName() + " - Rhapsody not available");
				skipped++;
				continue;
			}
			
			TestResult result = runTest(testCase);
			results.add(result);
			
			if (result.isSuccess())
			{
				passed++;
				log("[PASS] " + testCase.getName() + " (" + result.getExecutionTimeMs() + "ms)");
				if (verboseMode && result.getMessage() != null)
				{
					log("       " + result.getMessage());
				}
			}
			else
			{
				failed++;
				log("[FAIL] " + testCase.getName() + " (" + result.getExecutionTimeMs() + "ms)");
				log("       " + result.getMessage());
				if (result.getException() != null && verboseMode)
				{
					result.getException().printStackTrace();
				}
			}
		}
		
		log(repeatChar('=', 60));
		log("Results: " + passed + " passed, " + failed + " failed, " + skipped + " skipped");
		log(repeatChar('=', 60));
		
		return results;
	}
	
	private TestResult runTest(TestCase testCase)
	{
		long startTime = System.currentTimeMillis();
		try
		{
			TestResult result = testCase.getAction().execute(application);
			long endTime = System.currentTimeMillis();
			return new TestResult(
				testCase.getName(),
				result.isSuccess(),
				result.getMessage(),
				endTime - startTime,
				result.getException()
			);
		}
		catch (Exception e)
		{
			long endTime = System.currentTimeMillis();
			return TestResult.failure(testCase.getName(), e, endTime - startTime);
		}
	}
	
	/**
	 * Get test results summary as string
	 */
	public String getSummary()
	{
		StringBuilder sb = new StringBuilder();
		int passed = 0;
		int failed = 0;
		
		for (TestResult result : results)
		{
			if (result.isSuccess()) passed++;
			else failed++;
		}
		
		sb.append("Test Summary: ")
		  .append(passed).append(" passed, ")
		  .append(failed).append(" failed out of ")
		  .append(results.size()).append(" tests");
		
		return sb.toString();
	}
	
	/**
	 * Check if all tests passed
	 */
	public boolean allTestsPassed()
	{
		return results.stream().allMatch(TestResult::isSuccess);
	}
}
