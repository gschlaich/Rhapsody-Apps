package de.schlaich.gunnar.aiTools.copilot;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

public class AiCompletion
{

	private final RSyntaxTextArea editor;
	private final CopilotRestClient client;

	private String contextId;
	private String methodName;
	private String methodSignature;

	public AiCompletion(RSyntaxTextArea editor)
	{

		this.editor = editor;
		this.client = new CopilotRestClient();

		// Ctrl+Alt+Space triggers AI completion.
		KeyStroke shortcut = KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, KeyEvent.CTRL_DOWN_MASK | KeyEvent.ALT_DOWN_MASK);

		editor.getInputMap(JComponent.WHEN_FOCUSED).put(shortcut, "aiCompletion");

		editor.getActionMap().put("aiCompletion", new AbstractAction()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				requestCompletion();
			}
		});
	}

	public void setContext(String contextId, String methodName, String methodSignature)
	{

		this.contextId = contextId;
		this.methodName = methodName;
		this.methodSignature = methodSignature;
	}

	private void requestCompletion()
	{

		if (contextId == null)
		{
			return;
		}

		final String currentContextId = contextId;
		final String currentMethodName = methodName;
		final String currentMethodSignature = methodSignature;

		final String text = editor.getText();
		final int offset = editor.getCaretPosition();

		final String prefix = text.substring(0, offset);
		final String suffix = text.substring(offset);

		new SwingWorker<String, Void>()
		{

			@Override
			protected String doInBackground() throws Exception
			{

				return client.complete(currentContextId, currentMethodName, currentMethodSignature, prefix, suffix);
			}

			@Override
			protected void done()
			{

				try
				{
					String completion = get();

					if (completion == null || completion.isEmpty())
					{
						return;
					}

					// Do not insert into changed documents.
					if (!text.equals(editor.getText()) || offset != editor.getCaretPosition()
							|| !currentContextId.equals(contextId))
					{
						return;
					}

					// First prototype:
					// Show the suggestion before inserting it.
					Object[] options =
					{ "Insert", "Cancel" };

					int result = JOptionPane.showOptionDialog(editor, completion, "GitHub Copilot Suggestion",
							JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

					if (result == JOptionPane.OK_OPTION && text.equals(editor.getText())
							&& offset == editor.getCaretPosition() && currentContextId.equals(contextId))
					{

						editor.replaceSelection(completion);
					}

				}
				catch(Exception e)
				{
					JOptionPane.showMessageDialog(editor, e.getMessage(), "Copilot Error", JOptionPane.ERROR_MESSAGE);
				}
			}

		}.execute();
	}
}
