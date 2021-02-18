package apps;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.LanguageAwareCompletionProvider;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPOperation;
import com.ibm.rhapsody.apps.App;

import apps.ClassifierCompletionProvider.visibility;

public class OperationEditorWindow extends JRootPane implements HyperlinkListener, SyntaxConstants { 

	private RSyntaxTextArea textArea;
	private AutoCompletion ac;
	

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public OperationEditorWindow(IRPApplication rhapsody, IRPModelElement selected) {
		
		//JPanel contentpane = new JPanel(new BorderLayout());
		//ep = new JEditorPane("text/html", null);
		//contentPane.add(ep, BorderLayout.NORTH);
		
		IRPOperation selectedOperation = null;
	    
	    if(selected instanceof IRPOperation )
		{
			selectedOperation = (IRPOperation) selected;	
		}
	    else
	    {
	    	//only operations work...
	    	return;
	    }
	    

	   
		
		textArea = new RSyntaxTextArea(40, 80);
	    textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS);
	    textArea.setCaretPosition(0);
		textArea.requestFocusInWindow();
		textArea.setMarkOccurrences(true);
		textArea.setCodeFoldingEnabled(true);
		textArea.setTabsEmulated(true);
		textArea.setTabSize(3);
		textArea.addHyperlinkListener(this);
		
		
		
		CompletionProvider provider = createCompletionProvider(selectedOperation);
		
		
		
		// Install auto-completion onto our text area.
		ac = new AutoCompletion(provider);
		ac.setListCellRenderer(new CPPCellRenderer());
		ac.setShowDescWindow(true);
		ac.setParameterAssistanceEnabled(true);
		
		ac.setAutoCompleteEnabled(true);
		ac.setAutoActivationEnabled(true);
		ac.setAutoActivationDelay(750);
		//ac.setExternalURLHandler(new JavadocUrlHandler());
		
		//ac.setParamChoicesRenderer(new JavaParamListCellRenderer());
		
		
		
		ac.install(textArea);
		contentPane.add(new RTextScrollPane(textArea, true));
	    
		setContentPane(contentPane);

		textArea.setText(selectedOperation.getBody());
		textArea.requestFocusInWindow();
		
		
		
		Runnable focusInWindow = new Runnable() {
			
			@Override
			public void run() {
				textArea.requestFocusInWindow();
				
			}
		};
		
		SwingUtilities.invokeLater(focusInWindow);
		
		
		
		
	    
	    
		
	}
	
	public static void Run(IRPApplication rhapsody, IRPModelElement selected, MainApp aMainApp)
	{
		
		aMainApp.printToRhapsody("Edit Operation of " + selected.getName());
		JFrame frame = new JFrame ("Edit Operation of "+selected.getName());
	
	    frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
	    frame.getContentPane().add (new OperationEditorWindow(rhapsody,selected));
	    frame.pack();
	    frame.setVisible (true);
	    
	}
	
	/**
	 * Creates the completion provider for a C editor.  This provider can be
	 * shared among multiple editors.
	 * @param aClass 
	 *
	 * @return The provider.
	 */
	private CompletionProvider createCompletionProvider(IRPOperation selectedOperation) {

		IRPClass selectedClass = (IRPClass) selectedOperation.getOwner();
		
		// Create the provider used when typing code.
		ClassifierCompletionProvider codeCP = new ClassifierCompletionProvider(selectedClass, visibility.v_private);
		//LocalCompletionProvider localCP = new LocalCompletionProvider(selectedOperation.getBody());
		
		// The provider used when typing a string.
		CompletionProvider stringCP = createStringCompletionProvider();

		// The provider used when typing a comment.
		CompletionProvider commentCP = createCommentCompletionProvider();

		// Create the "parent" completion provider.
		LanguageAwareCompletionProvider provider = new LanguageAwareCompletionProvider(codeCP);
		provider.setStringCompletionProvider(stringCP);
		provider.setCommentCompletionProvider(commentCP);


		return provider;

	}
	
	/**
	 * Returns the completion provider to use when the caret is in a string.
	 *
	 * @return The provider.
	 * @see #createCodeCompletionProvider()
	 * @see #createCommentCompletionProvider()
	 */
	private CompletionProvider createStringCompletionProvider() {
		DefaultCompletionProvider cp = new SourceCompletionProvider();
		cp.addCompletion(new BasicCompletion(cp, "%c", "char", "Prints a character"));
		cp.addCompletion(new BasicCompletion(cp, "%i", "signed int", "Prints a signed integer"));
		cp.addCompletion(new BasicCompletion(cp, "%f", "float", "Prints a float"));
		cp.addCompletion(new BasicCompletion(cp, "%s", "string", "Prints a string"));
		cp.addCompletion(new BasicCompletion(cp, "%u", "unsigned int", "Prints an unsigned integer"));
		cp.addCompletion(new BasicCompletion(cp, "%d", "int", "Prints a decimal value"));
		cp.addCompletion(new BasicCompletion(cp, "\\n", "Newline", "Prints a newline"));
		return cp;
	}
	
	/**
	 * Returns the provider to use when in a comment.
	 *
	 * @return The provider.
	 * @see #createCodeCompletionProvider()
	 * @see #createStringCompletionProvider()
	 */
	private CompletionProvider createCommentCompletionProvider() {
		DefaultCompletionProvider cp = new DefaultCompletionProvider();
		cp.addCompletion(new BasicCompletion(cp, "TODO:", "A to-do reminder"));
		cp.addCompletion(new BasicCompletion(cp, "FIXME:", "A bug that needs to be fixed"));
		return cp;
	}

	@Override
	public void hyperlinkUpdate(HyperlinkEvent arg0) {
		// TODO Auto-generated method stub
		
	}



	

}