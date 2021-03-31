package apps;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;

import org.eclipse.cdt.core.dom.ast.IASTExpressionStatement;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTExpressionStatement;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFieldReference;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionCallExpression;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.FunctionCompletion;
import org.fife.ui.autocomplete.LanguageAwareCompletionProvider;
import org.fife.ui.autocomplete.ParameterizedCompletion.Parameter;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPArgument;
import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPDependency;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPOperation;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPSearchManager;
import com.telelogic.rhapsody.core.IRPSearchQuery;
import com.telelogic.rhapsody.core.IRPType;

import RhapsodyUtilities.ASTHelper;
import RhapsodyUtilities.RhapsodyOperation;

import com.ibm.rhapsody.apps.App;

import apps.ClassifierCompletionProvider.visibility;

public class OperationEditorWindow extends JRootPane implements HyperlinkListener, ActionListener, SyntaxConstants { 

	private RSyntaxTextArea myTextArea;
	private AutoCompletion myAutoComplete;
	private JFrame myFrame;
	private IRPOperation mySelectedOperation;
	private ClassifierCompletionProvider myClassifierCompletionProvider;

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public OperationEditorWindow(IRPApplication rhapsody, IRPModelElement selected) {
		
		
		mySelectedOperation = null;
	    
	    if(selected instanceof IRPOperation )
		{
			mySelectedOperation = (IRPOperation) selected;	
		}
	    else
	    {
	    	//only operations work...
	    	return;
	    }
	    
	    
	    IRPClassifier selectedClass = (IRPClassifier)mySelectedOperation.getOwner();
	 
		
		myTextArea = new RSyntaxTextArea(40, 120);
	    myTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS);
	    myTextArea.setCaretPosition(0);
		myTextArea.requestFocusInWindow();
		myTextArea.setMarkOccurrences(true);
		myTextArea.setCodeFoldingEnabled(true);
		myTextArea.setTabsEmulated(true);
		myTextArea.setTabSize(3);
		myTextArea.addHyperlinkListener(this);
		/*
		if(mySelectedOperation.isReadOnly()==1)
		{
			myTextArea.setEnabled(false);
			myTextArea.setBackground(Color.lightGray);
		}
		*/
		
		
		CompletionProvider provider = createCompletionProvider(mySelectedOperation);
		
		
		
		// Install auto-completion onto our text area.
		myAutoComplete = new AutoCompletion(provider);
		myAutoComplete.setListCellRenderer(new CPPCellRenderer());
		myAutoComplete.setShowDescWindow(true);
		myAutoComplete.setParameterAssistanceEnabled(true);
		
		myAutoComplete.setAutoCompleteEnabled(true);
		myAutoComplete.setAutoActivationEnabled(true);
		myAutoComplete.setAutoActivationDelay(750);
		//ac.setExternalURLHandler(new JavadocUrlHandler());
		
		//ac.setParamChoicesRenderer(new JavaParamListCellRenderer());
		
		JPopupMenu popup = myTextArea.getPopupMenu();
	    popup.addSeparator();
	    popup.add(new JMenuItem(new OpenFeature(myClassifierCompletionProvider)));
	      
		
		
		
		myAutoComplete.install(myTextArea);
		contentPane.add(new RTextScrollPane(myTextArea, true));
	    
		setContentPane(contentPane);

		myTextArea.setText(mySelectedOperation.getBody());
		myTextArea.requestFocusInWindow();
		
		
		
		Runnable focusInWindow = new Runnable() {
			
			@Override
			public void run() {
				myTextArea.requestFocusInWindow();
				
			}
		};
		
		SwingUtilities.invokeLater(focusInWindow);   
		
	}

	private void getNameSpaces(DefaultCompletionProvider provider, IRPModelElement selected) {
		IRPModelElement e =  selected;
	    
	    while((e instanceof IRPPackage)==false)
	    {
	    	e = e.getOwner();
	    }
	    
	    //System.out.println(e.getPropertyValue("CPP_CG.Package.DefineNameSpace"));
	    while(e.getPropertyValue("CPP_CG.Package.DefineNameSpace").equals("False"))
	    {
	    	e = e.getOwner();
	    }
	    
	    if (e instanceof IRPPackage)
    	{
    		IRPPackage p = (IRPPackage)e;
    		
    		RhapsodyNamespaceCompletion rnc = new RhapsodyNamespaceCompletion(provider, p);
    		provider.addCompletion(rnc);
    		
    		
    		System.out.println(e.getName());
    		List<IRPDependency> dependencies = p.getDependencies().toList();
    		for(IRPDependency dependency: dependencies)
    		{
    			if(dependency.getDependsOn() instanceof IRPPackage)
    			{
    				p = (IRPPackage) dependency.getDependsOn();
    				rnc = new RhapsodyNamespaceCompletion(provider, p); 
    				provider.addCompletion(rnc);
    			}
    				
    		}    		
    	}
	}
	
	private void setFrame(JFrame aFrame)
	{
		myFrame = aFrame;
	}
	
	public static void Run(IRPApplication rhapsody, IRPModelElement selected, MainApp aMainApp)
	{
		
		if(selected instanceof IRPOperation)
		{
			IRPOperation op = (IRPOperation)selected;

			aMainApp.printToRhapsody("Edit Operation of " + selected.getName());
			
			String imageName = op.getIconFileName();
			imageName = imageName.replace("\\", "/");
			Toolkit kit = Toolkit.getDefaultToolkit();
			Image img = kit.createImage(imageName);

			JFrame frame = new JFrame (RhapsodyOperation.getOperation(op));
			frame.setLayout(new BorderLayout());
			
			JPanel buttonPanel = new JPanel();
	
			
			JButton okButton = new JButton("ok");
			buttonPanel.add(okButton);
			JButton applyButton = new JButton("Apply");
			
			buttonPanel.add(applyButton);
			JButton cancelButton = new JButton("Cancel");
			buttonPanel.add(cancelButton);
			JButton locateButton = new JButton("Locate");
			buttonPanel.add(locateButton);
			buttonPanel.setVisible(true);
			
			OperationEditorWindow oew = new OperationEditorWindow(rhapsody,selected);
			oew.setFrame(frame);
			
			
			frame.add(oew);
			
			okButton.setActionCommand("ok");
			okButton.addActionListener(oew);
			
			applyButton.setActionCommand("apply");
			applyButton.addActionListener(oew);
			
			cancelButton.setActionCommand("cancel");
			cancelButton.addActionListener(oew);
			
			locateButton.setActionCommand("locate");
			locateButton.addActionListener(oew);
			
			
			//frame.add(editorPanel);
			frame.add(buttonPanel,BorderLayout.SOUTH);
			frame.setIconImage(img);
		    frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		    //frame.getContentPane().add (new OperationEditorWindow(rhapsody,selected));
		    frame.pack();
		    ScreenMonitor.Instance.registerFrame(frame);
	        frame.setVisible(true);
	        
		}
		else
		{
			aMainApp.printToRhapsody("no operation - exit");
		}
		
		
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
		myClassifierCompletionProvider = new ClassifierCompletionProvider(selectedClass, visibility.v_private);
		//LocalCompletionProvider localCP = new LocalCompletionProvider(selectedOperation.getBody());
		
		
		@SuppressWarnings("unchecked")
		List<IRPArgument> arguments = selectedOperation.getArguments().toList();
		for(IRPArgument argument : arguments)
		{
			RhapsodyArgumentCompletion rac = new RhapsodyArgumentCompletion(myClassifierCompletionProvider, argument);
			rac.setDefinedIn(selectedOperation.getName());
			myClassifierCompletionProvider.addCompletion(rac);
		}
		
		createTraceCompletions(myClassifierCompletionProvider);
		
		getNameSpaces(myClassifierCompletionProvider, selectedOperation);
		
		// The provider used when typing a string.
		CompletionProvider stringCP = createStringCompletionProvider();

		// The provider used when typing a comment.
		CompletionProvider commentCP = createCommentCompletionProvider();

		// Create the "parent" completion provider.
		LanguageAwareCompletionProvider provider = new LanguageAwareCompletionProvider(myClassifierCompletionProvider);
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
		cp.addCompletion(new BasicCompletion(cp, "Jira Issue:", "See USM-XXX "));
		return cp;
	}
	
	private void createTraceCompletions(ClassifierCompletionProvider aCp)
	{
		
		aCp.addCompletion(new BasicCompletion(aCp, "X_TRACE_RELEASE(X_ERROR)"));
		aCp.addCompletion(new BasicCompletion(aCp, "X_TRACE_RELEASE(X_WARNING)"));
		aCp.addCompletion(new BasicCompletion(aCp, "X_TRACE_RELEASE(X_INFO)"));
		aCp.addCompletion(new BasicCompletion(aCp, "X_TRACE_DEBUG(X_ERROR)"));
		aCp.addCompletion(new BasicCompletion(aCp, "X_TRACE_DEBUG(X_WARNING)"));
		aCp.addCompletion(new BasicCompletion(aCp, "X_TRACE_DEBUG(X_INFO)"));
		aCp.addCompletion(new BasicCompletion(aCp, "X_INFO"));
		aCp.addCompletion(new BasicCompletion(aCp, "X_WARNING"));
		aCp.addCompletion(new BasicCompletion(aCp, "X_ERROR"));
		aCp.addCompletion(new BasicCompletion(aCp, "X_FATAL"));
	

	}

	@Override
	public void hyperlinkUpdate(HyperlinkEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		
		if(command.equals("ok")||command.equals("apply"))
		{
			String text = myTextArea.getText();
			if(mySelectedOperation!=null)
			{
				mySelectedOperation.setBody(text);
			}
		}
		
		if(command.equals("ok")||command.equals("cancel"))
		{	
			if(myFrame!=null)
			{
				myFrame.dispose();
			}
		}
		if(command.equals("locate"))
		{
			if(mySelectedOperation!=null)
			{
				mySelectedOperation.locateInBrowser();
			}
		}
		
	}


}

class OpenFeature extends TextAction
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8665373931943623988L;
	
	private ClassifierCompletionProvider myCompletionProvider;

	public OpenFeature(ClassifierCompletionProvider aCompletionProvider) {
		super("Open feature");	
		myCompletionProvider = aCompletionProvider;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JTextComponent tc = getTextComponent(e);
        String elementName = null;
        
        LocalCompletionProvider localCompletionProvider = new LocalCompletionProvider(tc.getText(), myCompletionProvider);
        
        try 
        {
           int selStart = tc.getSelectionStart();
           int selEnd = tc.getSelectionEnd();
           
           if (selStart != selEnd) 
           {
              elementName = tc.getText(selStart, selEnd - selStart);
           } 
           else 
           {
              elementName = getElementNameAtCaret(tc);
           }
        } 
        catch (BadLocationException ble) 
        {
           ble.printStackTrace();
           UIManager.getLookAndFeel().provideErrorFeedback(tc);
           return;
        }
        
        
        System.out.println(elementName);
        
        if(myCompletionProvider!=null)
        {
        	Completion c = myCompletionProvider.getFirstCompletion(elementName);
        	if((c!=null) &&( c instanceof RhapsodyClassifier))
        	{
        		RhapsodyClassifier rc = (RhapsodyClassifier)c;
        		IRPModelElement element = rc.getElement();
        		element.openFeaturesDialog(1);
        		return;
	
        	}
        	if(c==null)
        	{
        		List<Completion> cs = localCompletionProvider.getCompletionByInputText(elementName);
        		if((cs!=null)&&(cs.size()>0)&&(cs.get(0) instanceof RhapsodyClassifier))
        		{
        			c = cs.get(0);
        			IRPModelElement element = ((RhapsodyClassifier)c).getElement();
        			element.openFeaturesDialog(1);
            		return;
        		}
        	}
        	if(c==null)
        	{
        		IRPClassifier classifier = myCompletionProvider.getClassifier();
        		IRPClassifier cl = RhapsodyOperation.findClassifier(classifier, elementName);
        		if(cl!=null)
        		{
        			cl.openFeaturesDialog(1);
        			return;
        		}
        		
        	}
        	
        	if(c==null)
        	{
        		IASTTranslationUnit translationUnit = ASTHelper.getTranslationUnit(tc.getText());
        		if(translationUnit!=null)
        		{
        			IASTNode node = ASTHelper.getNodeAtPostion(tc.getSelectionStart(), tc.getSelectionEnd(), translationUnit);
        			if(node!=null)
        			{
        				
        				if(openOperationFeature(elementName, localCompletionProvider, node)==false)
        				{
        					
        				}

        			}
        		}
        	}
        }

		
	}
	
	private boolean openDeclarationFeature(String aElementName, LocalCompletionProvider aLocalCompletionProvider, IASTNode aNode)
	{
		Completion c;
		IASTNode parent = aNode.getParent();
		if(parent==null)
		{
			return false;
		}
		
		
		
		return false;
	}
	

	private boolean openOperationFeature(String aElementName, LocalCompletionProvider aLocalCompletionProvider, IASTNode aNode) 
	{
		Completion c;
		System.out.println(aNode);
		while((aNode!=null)&&((aNode instanceof CPPASTFieldReference)==false))
		{
			aNode = aNode.getParent();
		}
		
		if(aNode==null)
		{
			//try to find in Rhapsody
			
			
			return false;
		}
		
		CPPASTFieldReference fieldReference = (CPPASTFieldReference)aNode;
		ICPPASTExpression expression =  fieldReference.getFieldOwner();
		System.out.println(expression.getRawSignature());
		String classifierName = expression.getRawSignature();
		c = myCompletionProvider.getFirstCompletion(classifierName);
		if(c==null)
		{
			List<Completion> cs = aLocalCompletionProvider.getCompletionByInputText(classifierName);
			if((cs!=null)&&(cs.size()>0)&&(cs.get(0) instanceof RhapsodyClassifier))
			{
				c = cs.get(0);
			}
		}
		if((c!=null)&&(c instanceof RhapsodyClassifier))
		{
			IRPModelElement element = ((RhapsodyClassifier)c).getIRPClassifier();
			
			System.out.println(element.getName());
			List<IRPModelElement> elements = element.getNestedElements().toList();
			for(IRPModelElement me : elements)
			{
			
				if(me.getName().equals(aElementName))
				{
					me.openFeaturesDialog(1);
					return true;
				}
				
			}
			//not found...
			element.openFeaturesDialog(1);
			return true;
				
		}
		return false;
	}
	
	 /**
     * Gets the filename that the caret is sitting on. Note that this is a
     * somewhat naive implementation and assumes filenames do not contain
     * whitespace or other "funny" characters, but it will catch most common
     * filenames.
     * 
     * @param tc The text component to look at.
     * @return The filename at the caret position.
     * @throws BadLocationException Shouldn't actually happen.
     */
    public String getElementNameAtCaret(JTextComponent tc) throws BadLocationException 
    {
       int caret = tc.getCaretPosition();
       int start = caret;
       Document doc = tc.getDocument();
       while (start > 0) 
       {
          char ch = doc.getText(start - 1, 1).charAt(0);
          if (isElementChar(ch)) 
          {
             start--;
          } 
          else 
          {
             break;
          }
       }
       int end = caret;
       while (end < doc.getLength()) 
       {
          char ch = doc.getText(end, 1).charAt(0);
          if (isElementChar(ch)) 
          {
             end++;
          } 
          else 
          {
             break;
          }
       }
       return doc.getText(start, end - start);
    }
    
    public boolean isElementChar(char ch) {
        return Character.isLetterOrDigit(ch);
     }

	
	
}

