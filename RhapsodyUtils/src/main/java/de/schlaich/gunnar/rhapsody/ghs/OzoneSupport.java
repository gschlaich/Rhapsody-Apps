package de.schlaich.gunnar.rhapsody.ghs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.function.Consumer;

import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPOperation;

import de.schlaich.gunnar.rhapsody.utilities.ASTHelper;
import de.schlaich.gunnar.rhapsody.utilities.ASTHelper.SourceLocation;

public class OzoneSupport
{

	private IRPApplication myApplication = null;
	private Consumer<String> myTraceAction = null;

	private Socket socket = null;
	private BufferedReader reader = null;
	private BufferedWriter writer = null;
	String host = "localhost";
	int port = 19200;

	public OzoneSupport(IRPApplication aApplication, Consumer<String> aTraceAction)
	{
		myTraceAction = aTraceAction;
		myApplication = aApplication;
		
		trace("OzoneSupport initialized (not connected yet)");
	}
	
	/**
	 * Connect to the Ozone debugger
	 * @return true if connection was successful
	 */
	public boolean connect()
	{
		if (isConnected())
		{
			trace("Already connected to Ozone");
			return true;
		}

		try
		{
			socket = new Socket(host, port);
			socket.setSoTimeout(5000); // Timeout für Lese-Operationen

			reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF_8"));
			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF_8"));

			trace("Connected to Ozone at " + host + ":" + port);
			return true;
		}
		catch(IOException e)
		{
			trace("Failed to connect to Ozone: " + e.getMessage());
			cleanup();
			return false;
		}
	}

	private void cleanup()
	{
		try
		{
			if (writer != null)
				writer.close();
			if (reader != null)
				reader.close();
			if (socket != null)
				socket.close();
		}
		catch(IOException e)
		{
			trace("Error during cleanup: " + e.getMessage());
		}
		finally
		{
			writer = null;
			reader = null;
			socket = null;
		}
	}

	private void trace(String aMessage)
	{
		if (myTraceAction == null)
		{
			// no traceaction set...
			return;
		}

		aMessage = "Ozone: " + aMessage;

		myTraceAction.accept(aMessage);
	}

	private boolean sendCommand(String aCommand, int maxRetries)
	{
		for (int i = 0; i <= maxRetries; i++)
		{
			if (!isConnected())
			{
				trace("Not connected, attempt " + (i + 1) + "/" + (maxRetries + 1));
				if (i < maxRetries)
				{
					reconnect();
					continue;
				}
				return false;
			}

			try
			{
				writer.write(aCommand);
				writer.newLine();
				writer.flush();
				trace("Command sent successfully");
				return true;
			}
			catch(IOException e)
			{
				trace("Error sending command (attempt " + (i + 1) + "): " + e.getMessage());
				cleanup();

				if (i < maxRetries)
				{
					reconnect();
				}
			}
		}
		return false;
	}

	private void reconnect()
	{
		trace("Attempting to reconnect...");
		cleanup();

		try
		{
			Thread.sleep(1000); // Kurze Pause vor Reconnect
			socket = new Socket(host, port);
			socket.setSoTimeout(5000);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF_8"));
			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF_8"));
			trace("Reconnected successfully");
		}
		catch(IOException | InterruptedException e)
		{
			trace("Reconnect failed: " + e.getMessage());
		}
	}

	private boolean sendCommand(String aCommand)
	{
		return sendCommand(aCommand, 2); // 2 Wiederholungsversuche
	}

	private String sendCommandWithResponse(String aCommand)
	{
		if (sendCommand(aCommand))
		{
			try
			{
				String response = reader.readLine();
				trace("Response: " + response);
				return response;
			}
			catch(IOException e)
			{
				trace("Error reading response: " + e.getMessage());
			}
		}
		return null;
	}

	public boolean isConnected()
	{
		return socket != null && !socket.isClosed() && socket.isConnected();
	}

	public boolean checkConnection()
	{
		if (!isConnected())
		{
			trace("Not connected to Ozone");
			return false;
		}

		// Optional: Test-Befehl senden
		try
		{
			writer.write("\n");
			writer.flush();
			trace("Connection check successful");
			return true;
		}
		catch(IOException e)
		{
			trace("Connection check failed: " + e.getMessage());
			return false;
		}
	}

	public void disconnect()
	{
		try
		{
			if (writer != null)
			{
				writer.close();
			}
			if (reader != null)
			{
				reader.close();
			}
			if (socket != null)
			{
				socket.close();
			}
			trace("Disconnected from Ozone");
		}
		catch(IOException e)
		{
			trace("Error closing connection: " + e.getMessage());
		}
	}

	public boolean view(IRPOperation aOperation)
	{
		if (aOperation == null)
		{
			trace("Operation is null");
			return false;
		}
		File sourcePath = ASTHelper.getSourcePath(aOperation, myApplication, ".cpp");
		
		if (!isConnected())
		{
			trace("Not connected to Ozone");
			return false;
		}
		
		// Get the source location using ASTHelper
		SourceLocation location = ASTHelper.getOperationSourceLocation(aOperation, myApplication);
		
		if (location == null)
		{
			trace("Could not find source location for operation: " + aOperation.getName());
			return false;
		}
		
		String filePath = location.getFilePath();
		int lineNumber = location.getLineNumber();
		
		trace("Opening " + aOperation.getName() + " at " + filePath + ":" + lineNumber);
		
		// Send the view command to Ozone
		// Ozone command format: View.Source <filename>#<linenumber>
		String command = "Show.Source (\"" + filePath + ":" + lineNumber + "\")";
		
		return sendCommand(command);
	}
	
	/**
	 * Set a breakpoint at the start of an operation in Ozone
	 * @param aOperation The operation where to set the breakpoint
	 * @return true if breakpoint was set successfully
	 */
	public boolean setBreakpoint(IRPOperation aOperation)
	{
		return setBreakpoint(aOperation, 0);
	}
	
	/**
	 * Set a breakpoint at a specific offset within an operation in Ozone
	 * @param aOperation The operation where to set the breakpoint
	 * @param aOffset Line offset from the start of the operation (0 = start of operation)
	 * @return true if breakpoint was set successfully
	 */
	public boolean setBreakpoint(IRPOperation aOperation, int aOffset)
	{
		if (aOperation == null)
		{
			trace("Operation is null");
			return false;
		}
		
		if (!isConnected())
		{
			trace("Not connected to Ozone");
			return false;
		}
		
		// Get the source location using ASTHelper
		SourceLocation location = ASTHelper.getOperationSourceLocation(aOperation, myApplication);
		
		if (location == null)
		{
			trace("Could not find source location for operation: " + aOperation.getName());
			return false;
		}
		
		String filePath = location.getFilePath();
		int lineNumber = location.getLineNumber() + aOffset;
		
		trace("Setting breakpoint for " + aOperation.getName() + " at " + filePath + ":" + lineNumber + " (offset: " + aOffset + ")");
		
		// Send the breakpoint command to Ozone
		// Ozone command format: Break.Set <filename> /<linenumber>
		String command = "Break.SetOnSrc (\"" + filePath + ":" + lineNumber + "\")";
		
		return sendCommand(command);
	}
	
	/**
	 * Set a breakpoint at a specific file and line number in Ozone
	 * @param aFilePath Path to the source file
	 * @param aLineNumber Line number where to set the breakpoint
	 * @return true if breakpoint was set successfully
	 */
	public boolean setBreakpoint(String aFilePath, int aLineNumber)
	{
		if (aFilePath == null || aFilePath.isEmpty())
		{
			trace("File path is null or empty");
			return false;
		}
		
		if (!isConnected())
		{
			trace("Not connected to Ozone");
			return false;
		}
		
		trace("Setting breakpoint at " + aFilePath + ":" + aLineNumber);
		
		// Send the breakpoint command to Ozone
		String command = "Break.SetOnSrc (\"" + aFilePath + ":" + aLineNumber + "\")";
		
		return sendCommand(command);
	}
	
	/**
	 * Delete a breakpoint at the start of an operation in Ozone
	 * @param aOperation The operation where to delete the breakpoint
	 * @return true if breakpoint was deleted successfully
	 */
	public boolean deleteBreakpoint(IRPOperation aOperation)
	{
		if (aOperation == null)
		{
			trace("Operation is null");
			return false;
		}
		
		if (!isConnected())
		{
			trace("Not connected to Ozone");
			return false;
		}
		
		// Get the source location using ASTHelper
		SourceLocation location = ASTHelper.getOperationSourceLocation(aOperation, myApplication);
		
		if (location == null)
		{
			trace("Could not find source location for operation: " + aOperation.getName());
			return false;
		}
		
		String filePath = location.getFilePath();
		int lineNumber = location.getLineNumber();
		
		trace("Deleting breakpoint for " + aOperation.getName() + " at " + filePath + ":" + lineNumber);
		
		// Send the delete breakpoint command to Ozone
		String command = "Break.ClearOnSrc (\"" + filePath + ":" + lineNumber + "\")";
		
		return sendCommand(command);
	}
	
	/**
	 * Delete all breakpoints in Ozone
	 * @return true if command was sent successfully
	 */
	public boolean deleteAllBreakpoints()
	{
		if (!isConnected())
		{
			trace("Not connected to Ozone");
			return false;
		}
		
		trace("Deleting all breakpoints");
		
		// Send the delete all breakpoints command to Ozone
		String command = "Break.ClearAll";
		
		return sendCommand(command);
	}

	/**
	 * Start Ozone debugger with the specified project name.
	 * Prepares the .jdebug configuration file if necessary:
	 * - Checks if <projektname>.jdebug exists
	 * - If not, copies from ../../../../Development/Target/Calendula/Segger/Calendula.jdebug
	 * - Renames the file to <projektname>.jdebug
	 * - Replaces @@APP_ELF@@ with <projektname>AppD9.elf in the file
	 * - Also copies SAMA7D65.JLinkScript from the same folder if it doesn't exist
	 * - Starts Ozone with the .jdebug file as argument
	 * 
	 * @param aProjectName The project name (used for configuration file naming)
	 * @param aWorkingFolder The working directory where Ozone should be started
	 * @return true if Ozone was started successfully
	 */
	public boolean startOzone(String aProjectName, String aWorkingFolder)
	{
		if (aProjectName == null || aProjectName.isEmpty())
		{
			trace("Project name is null or empty");
			return false;
		}
		
		if (aWorkingFolder == null || aWorkingFolder.isEmpty())
		{
			trace("Working folder is null or empty");
			return false;
		}
		
		try
		{
			// Use the provided working folder
			trace("Working folder: " + aWorkingFolder);
			
			// Construct paths
			String jdebugFileName = aProjectName + ".jdebug";
			String jdebugFilePath = aWorkingFolder + File.separator + jdebugFileName;
			
			File jdebugFile = new File(jdebugFilePath);
			
			// Check if .jdebug file exists
			if (!jdebugFile.exists())
			{
				trace("File " + jdebugFileName + " not found, preparing it...");
				
				// Construct the source path for the template file
				String templatePath = aWorkingFolder + File.separator + 
					"../../../../Development/Target/Calendula/Segger/Calendula.jdebug";
				
				File templateFile = new File(templatePath);
				
				if (!templateFile.exists())
				{
					trace("Template file not found at: " + templatePath);
					return false;
				}
				
				trace("Copying template from: " + templateFile.getAbsolutePath());
				
				// Copy the template file
				Files.copy(templateFile.toPath(), Paths.get(jdebugFilePath), 
					StandardCopyOption.REPLACE_EXISTING);
				
				trace("File copied to: " + jdebugFilePath);
				
				// Read the file content and replace @@APP_ELF@@ with <projektname>AppD9.elf
				String content = new String(Files.readAllBytes(Paths.get(jdebugFilePath)), 
					StandardCharsets.UTF_8);
				
				String elfName = aProjectName + "AppD9.elf";
				content = content.replace("@@APP_ELF@@", elfName);
				
				trace("Replacing @@APP_ELF@@ with: " + elfName);
				
				// Write the modified content back
				Files.write(Paths.get(jdebugFilePath), content.getBytes(StandardCharsets.UTF_8));
				
				trace("File modified and saved");
			}
			else
			{
				trace("File " + jdebugFileName + " already exists");
			}
			
			// Copy SAMA7D65.JLinkScript if it doesn't exist
			String jlinkScriptFileName = "SAMA7D65.JLinkScript";
			String jlinkScriptFilePath = aWorkingFolder + File.separator + jlinkScriptFileName;
			File jlinkScriptFile = new File(jlinkScriptFilePath);
			
			if (!jlinkScriptFile.exists())
			{
				trace("File " + jlinkScriptFileName + " not found, copying it...");
				
				// Construct the source path for the JLink script file
				String jlinkScriptSourcePath = aWorkingFolder + File.separator + 
					"../../../../Development/Target/Calendula/Segger/SAMA7D65.JLinkScript";
				
				File jlinkScriptSourceFile = new File(jlinkScriptSourcePath);
				
				if (jlinkScriptSourceFile.exists())
				{
					trace("Copying JLink script from: " + jlinkScriptSourceFile.getAbsolutePath());
					
					// Copy the JLink script file
					Files.copy(jlinkScriptSourceFile.toPath(), Paths.get(jlinkScriptFilePath), 
						StandardCopyOption.REPLACE_EXISTING);
					
					trace("JLink script file copied to: " + jlinkScriptFilePath);
				}
				else
				{
					trace("JLink script source file not found at: " + jlinkScriptSourcePath);
				}
			}
			else
			{
				trace("File " + jlinkScriptFileName + " already exists");
			}
			
			// Start Ozone with the .jdebug file as argument
			trace("Starting Ozone with: " + jdebugFileName);
			
			ProcessBuilder pb = new ProcessBuilder("ozone", jdebugFilePath);
			pb.directory(new File(aWorkingFolder));
			
			Process process = pb.start();
			
			trace("Ozone started successfully");
			
			return true;
		}
		catch (IOException e)
		{
			trace("Error starting Ozone: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
		catch (Exception e)
		{
			trace("Unexpected error: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Start Ozone debugger with the specified project name.
	 * Uses the current working directory.
	 * 
	 * @param aProjectName The project name (used for configuration file naming)
	 * @return true if Ozone was started successfully
	 * @deprecated Use startOzone(String aProjectName, String aWorkingFolder) instead
	 */
	@Deprecated
	public boolean startOzone(String aProjectName)
	{
		String currentDir = System.getProperty("user.dir");
		return startOzone(aProjectName, currentDir);
	}

}
