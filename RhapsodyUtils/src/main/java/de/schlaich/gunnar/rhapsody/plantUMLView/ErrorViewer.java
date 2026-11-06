package de.schlaich.gunnar.rhapsody.plantUMLView;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

/**
 * ErrorViewer - Displays HTML error messages from Kroki API in a window
 */
public class ErrorViewer extends JFrame
{

	private static final long serialVersionUID = 1L;

	public ErrorViewer(String htmlContent, boolean aExitOnClose)
	{
		if (aExitOnClose)
		{
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
		else
		{
			this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		}

		// Create an editor pane to display HTML
		JEditorPane editorPane = new JEditorPane();
		editorPane.setContentType("text/html");
		editorPane.setText(htmlContent);
		editorPane.setEditable(false);
		editorPane.setBackground(Color.WHITE);
		editorPane.setCaretPosition(0); // Scroll to top

		// Add scroll pane for large content
		JScrollPane scrollPane = new JScrollPane(editorPane);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		// Calculate window size
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = Math.min(800, screenSize.width - 100);
		int height = Math.min(600, screenSize.height - 100);

		if (width < 400)
		{
			width = 400;
		}
		if (height < 300)
		{
			height = 300;
		}

		// Center the window on screen
		setLocation(screenSize.width / 2 - width / 2, screenSize.height / 2 - height / 2);

		this.setSize(width, height);
		this.getContentPane().add(scrollPane);
	}
}
