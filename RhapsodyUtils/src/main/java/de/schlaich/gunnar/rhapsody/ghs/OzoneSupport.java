package de.schlaich.gunnar.rhapsody.ghs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
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

		try
		{
			socket = new Socket(host, port);
			socket.setSoTimeout(5000); // Timeout f�r Lese-Operationen

			reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF_8"));
			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF_8"));

			trace("Connected to Ozone at " + host + ":" + port);
		}
		catch(IOException e)
		{
			trace("Failed to connect to Ozone: " + e.getMessage());
			cleanup();
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
		String command = "View.Source " + filePath + "#" + lineNumber;
		
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
		String command = "Break.Set " + filePath + " /" + lineNumber;
		
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
		String command = "Break.Set " + aFilePath + " /" + aLineNumber;
		
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
		String command = "Break.Delete " + filePath + " /" + lineNumber;
		
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
		String command = "Break.Delete *";
		
		return sendCommand(command);
	}

}
