package de.schlaich.gunnar.rhapsody.plantUMLView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * MermaidViewer - Renders Mermaid diagrams to PNG using Kroki API This class
 * handles the conversion of Mermaid diagram source code to PNG images
 */
public class MermaidViewer
{

	private static final String KROKI_URL = "https://kroki.io/mermaid/png";
	private static final int CONNECTION_TIMEOUT = 15000;
	private static final int READ_TIMEOUT = 15000;

	/**
	 * Converts a Mermaid diagram source to PNG bytes using the Kroki API
	 * 
	 * @param mermaidSource The Mermaid diagram source code
	 * @return PNG image as byte array, or null if conversion fails
	 */
	public static byte[] convertMermaidToPNG(String mermaidSource)
	{
		if (mermaidSource == null || mermaidSource.trim().isEmpty())
		{
			System.err.println("Error: Mermaid source is empty");
			return null;
		}

		try
		{
			System.out.println("Converting Mermaid diagram to PNG using Kroki API...");
			System.out.println("Diagram source length: " + mermaidSource.length() + " characters");
			//System.out.println("=== Mermaid Source ===");
			//System.out.println(mermaidSource);
			//System.out.println("=== End Mermaid Source ===");

			return convertUsingKrokiPost(mermaidSource);
		}
		catch(IOException e)
		{
			System.err.println("Error converting Mermaid diagram: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Converts Mermaid diagram using Kroki API with POST request This is the
	 * recommended method for the Kroki API
	 * 
	 * @param mermaidSource The Mermaid diagram source
	 * @return PNG image as byte array, or null if conversion fails
	 * @throws IOException if conversion fails
	 */
	private static byte[] convertUsingKrokiPost(String mermaidSource) throws IOException
	{
		HttpURLConnection connection = null;
		try
		{
			URL url = new URL(KROKI_URL);
			connection = (HttpURLConnection) url.openConnection();

			// Configure connection
			connection.setRequestMethod("POST");
			connection.setConnectTimeout(CONNECTION_TIMEOUT);
			connection.setReadTimeout(READ_TIMEOUT);
			connection.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");
			connection.setRequestProperty("User-Agent", "RhapsodyMermaidViewer/1.0");
			connection.setDoOutput(true);
			connection.setDoInput(true);

			// Send the diagram source as plain text in the request body
			byte[] postBytes = mermaidSource.getBytes(StandardCharsets.UTF_8);
			connection.setRequestProperty("Content-Length", String.valueOf(postBytes.length));

			try (OutputStream os = connection.getOutputStream())
			{
				os.write(postBytes);
				os.flush();
			}

			int responseCode = connection.getResponseCode();
			System.out.println("Kroki API response code: " + responseCode);

			if (responseCode == HttpURLConnection.HTTP_OK)
			{
				// Read the PNG image data
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				byte[] data = new byte[8192];
				int nRead;

				try (java.io.InputStream is = connection.getInputStream())
				{
					while ((nRead = is.read(data, 0, data.length)) != -1)
					{
						buffer.write(data, 0, nRead);
					}
				}

				byte[] result = buffer.toByteArray();
				System.out.println("Successfully received PNG image: " + result.length + " bytes");
				return result;
			}
			else if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST)
			{
				System.err.println("Kroki API error: 400 Bad Request");
				System.err.println("Response message: " + connection.getResponseMessage());

				// Read error details
				String errorContent = readErrorContent(connection);
				if (errorContent != null && !errorContent.trim().isEmpty())
				{
			//		System.err.println("Error details: " + errorContent);
					// Display error in GUI if it's HTML
					displayErrorInWindow(errorContent);
				}
				return null;
			}
			else
			{
				//System.err.println("Kroki API error: " + responseCode + " " + connection.getResponseMessage());
				
				// Try to read and display error content for other error codes too
				String errorContent = readErrorContent(connection);
				if (errorContent != null && !errorContent.trim().isEmpty())
				{
					//System.err.println("Error details: " + errorContent);
					displayErrorInWindow(errorContent);
				}
				return null;
			}
		}
		catch(IOException e)
		{
			System.err.println("IOException while communicating with Kroki API: " + e.getMessage());
			e.printStackTrace();
			throw e;
		}
		finally
		{
			if (connection != null)
			{
				connection.disconnect();
			}
		}
	}

	/**
	 * Reads error content from the connection's error stream
	 * 
	 * @param connection The HTTP connection
	 * @return The error content as a string, or null if no error stream
	 */
	private static String readErrorContent(HttpURLConnection connection)
	{
		try (java.io.InputStream es = connection.getErrorStream())
		{
			if (es != null)
			{
				ByteArrayOutputStream errorBuffer = new ByteArrayOutputStream();
				byte[] data = new byte[1024];
				int nRead;
				while ((nRead = es.read(data, 0, data.length)) != -1)
				{
					errorBuffer.write(data, 0, nRead);
				}
				return new String(errorBuffer.toByteArray(), StandardCharsets.UTF_8);
			}
		}
		catch (IOException e)
		{
			System.err.println("Failed to read error stream: " + e.getMessage());
		}
		return null;
	}

	/**
	 * Displays error content in a window (detects if it's HTML or plain text)
	 * 
	 * @param errorContent The error content to display
	 */
	private static void displayErrorInWindow(String errorContent)
	{
		// Check if content is HTML
		if (errorContent.trim().toLowerCase().startsWith("<html") || 
		    errorContent.trim().toLowerCase().contains("<!doctype"))
		{
			ErrorViewer errorViewer = new ErrorViewer(errorContent, false);
			errorViewer.setTitle("Kroki API Error");
			errorViewer.setVisible(true);
		}
		else
		{
			// For plain text errors, create a simple HTML view
			String htmlError = "<html><body style='font-family: monospace; padding: 10px;'>"
					+ "<h2 style='color: red;'>Kroki API Error</h2>"
					+ "<pre>" + escapeHtml(errorContent) + "</pre>"
					+ "</body></html>";
			ErrorViewer errorViewer = new ErrorViewer(htmlError, false);
			errorViewer.setTitle("Kroki API Error");
			errorViewer.setVisible(true);
		}
	}

	/**
	 * Escapes HTML special characters in plain text
	 * 
	 * @param text The text to escape
	 * @return The escaped text
	 */
	private static String escapeHtml(String text)
	{
		return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")
				.replace("'", "&#39;");
	}

	/**
	 * Display Mermaid diagram in a window
	 * 
	 * @param mermaidSource The Mermaid diagram source code
	 * @param title         The window title
	 * @param aExitOnClose  Whether to exit application on window close
	 * @return showPNG frame displaying the diagram, or null if conversion fails
	 */
	public static showPNG displayMermaidDiagram(String mermaidSource, String title, boolean aExitOnClose)
	{
		byte[] imageData = convertMermaidToPNG(mermaidSource);

		if (imageData != null && imageData.length > 0)
		{
			showPNG viewer = new showPNG(imageData, aExitOnClose);
			viewer.setTitle(title != null ? title : "Mermaid Diagram");
			// viewer.setVisible(true);
			return viewer;
		}
		else
		{
			System.err.println("Failed to generate Mermaid diagram image");
			return null;
		}
	}
}
