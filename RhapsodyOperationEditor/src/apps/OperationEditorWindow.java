package apps;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import javax.swing.UnsupportedLookAndFeelException;
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
import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Style;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;
import org.fife.ui.rtextarea.RTextScrollPane;

import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPArgument;
import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPCollection;
import com.telelogic.rhapsody.core.IRPComponent;
import com.telelogic.rhapsody.core.IRPDependency;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPOperation;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPSearchManager;
import com.telelogic.rhapsody.core.IRPSearchQuery;
import com.telelogic.rhapsody.core.IRPStereotype;
import com.telelogic.rhapsody.core.IRPType;

import RhapsodyUtilities.ASTHelper;
import RhapsodyUtilities.RhapsodyOperation;

import com.ibm.rhapsody.apps.App;

import apps.ClassifierCompletionProvider.visibility;
import parser.CppParser;
import parser.RhapsodyTokenMaker;
import parser.TypeParser;

public class OperationEditorWindow extends JRootPane implements HyperlinkListener, ActionListener, SyntaxConstants { 

	private RSyntaxTextArea myTextArea;
	private AutoCompletion myAutoComplete;
	private JFrame myFrame;
	private IRPOperation mySelectedOperation;
	private ClassifierCompletionProvider myClassifierCompletionProvider;
	private IRPApplication myApplication;
	private SyntaxScheme myScheme;
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public OperationEditorWindow(IRPApplication rhapsody, IRPModelElement selected) {
		
		myApplication = rhapsody;
		mySelectedOperation = null;
		
		AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory)TokenMakerFactory.getDefaultInstance();
		atmf.putMapping("text/RhapsodyCPP", "parser.RhapsodyTokenMaker");
		
		
		
		
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

	    int factorW = 7;
	    int factorH = 16;
	    
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		
		int rows = (int)(dim.height/factorH*0.7);
		int cols = (int)(dim.width/factorW*0.5);
		
		this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
		
		
		
		
		myTextArea = new RSyntaxTextArea(rows, cols);
	    myTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS);
	    //myTextArea.setSyntaxEditingStyle("text/RhapsodyCPP");
	    myTextArea.setCaretPosition(0);
		myTextArea.requestFocusInWindow();
		myTextArea.setMarkOccurrences(true);
		myTextArea.setCodeFoldingEnabled(true);
		myTextArea.setTabsEmulated(true);
		myTextArea.setTabSize(4);
		myTextArea.addHyperlinkListener(this);
		
		

		contentPane.add(new RTextScrollPane(myTextArea, true));
	    
		setContentPane(contentPane);

		myTextArea.setText(mySelectedOperation.getBody());
		
		StartAutoCompletion startAutoCompletion = new StartAutoCompletion(myTextArea, myAutoComplete, mySelectedOperation, myApplication);
		
		startAutoCompletion.start();
		
		myTextArea.convertTabsToSpaces();
		myTextArea.requestFocusInWindow();
		
		setRhapsodyStyle();
		
		
		
		
		
		
		
				
		Runnable focusInWindow = new Runnable() {
			
			@Override
			public void run() {
				myTextArea.requestFocusInWindow();
				
			}
		};
		
		SwingUtilities.invokeLater(focusInWindow);   
		
	}


	private void setRhapsodyStyle() 
	{
		//settings should be similar to rhapsody
		myTextArea.setBackground(new Color(0xffffff));
		myTextArea.setCaretColor(new Color(0x000000));
		myTextArea.setCurrentLineHighlightColor(new Color(0xe8f2fe));
		myTextArea.setFadeCurrentLineHighlight(false);
		myTextArea.setMarginLineColor(new Color(0xb0b4b9));
		myTextArea.setMarkAllHighlightColor(new Color(0x6b8189));
		myTextArea.setMatchedBracketBorderColor(new Color(0xdbe0cc));
		myTextArea.setBracketMatchingEnabled(true);
		myScheme = myTextArea.getSyntaxScheme();
		
		int colorComment = 0x30a030;
		
		setTokenFgColor(SyntaxScheme.IDENTIFIER, 0x00000);
		setTokenFgColor(SyntaxScheme.RESERVED_WORD, 0x0000ff);
		setTokenFgColor(SyntaxScheme.RESERVED_WORD_2, 0x0000ff);
		setTokenFgColor(SyntaxScheme.ANNOTATION, 0x80f080);
		setTokenFgColor(SyntaxScheme.COMMENT_DOCUMENTATION, colorComment);
		setTokenFgColor(SyntaxScheme.COMMENT_EOL, colorComment);
		setTokenFgColor(SyntaxScheme.COMMENT_DOCUMENTATION, colorComment);
		setTokenFgColor(SyntaxScheme.COMMENT_MULTILINE, colorComment);
		setTokenFgColor(SyntaxScheme.COMMENT_MARKUP, colorComment);
		setTokenFgColor(SyntaxScheme.COMMENT_KEYWORD, 0xae9fbf);
		setTokenFgColor(SyntaxScheme.DATA_TYPE, 0x4040ff);
		setTokenFgColor(SyntaxScheme.VARIABLE, 0x5050a0);
		setTokenFgColor(SyntaxScheme.LITERAL_STRING_DOUBLE_QUOTE, 0xA31515);
		
		
		
		
		
		
		
		
		myTextArea.setSyntaxScheme(myScheme);
	}


	private void setTokenFgColor(int aTokenType, int aColor ) {
		
		
		Style s = myScheme.getStyle(aTokenType);
		s.foreground = new Color(aColor);
		myScheme.setStyle(aTokenType, s);
		
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
			
			aMainApp.printToRhapsody("Java Version: " + System.getProperty("java.vm.version"));
			aMainApp.printToRhapsody(System.getProperty("java.version"));
			String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());	
			aMainApp.printToRhapsody(timeStamp);
			
			String imageName = op.getIconFileName();
			imageName = imageName.replace("\\", "/");
			Toolkit kit = Toolkit.getDefaultToolkit();
			Image img = kit.createImage(imageName);

			String lufSystem = UIManager.getSystemLookAndFeelClassName();
			
			try {
				UIManager.setLookAndFeel(lufSystem);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedLookAndFeelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			JFrame frame = new JFrame (RhapsodyOperation.getOperation(op));
			frame.setLayout(new BorderLayout());
			
			JPanel buttonPanel = new JPanel();
			
			JButton locateButton = new JButton("Locate");
			buttonPanel.add(locateButton);
		
			
			
			JButton generateButton = new JButton("Generate");
			buttonPanel.add(generateButton);
			
			JButton applyButton = new JButton("Apply");
			buttonPanel.add(applyButton);
			
			JButton okButton = new JButton("ok");
			buttonPanel.add(okButton);
			
			
			
			JButton cancelButton = new JButton("Cancel");
			buttonPanel.add(cancelButton);
			
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
			
			generateButton.setActionCommand("generate");
			generateButton.addActionListener(oew);
			
			//frame.add(editorPanel);
			frame.add(buttonPanel,BorderLayout.SOUTH);
			frame.setIconImage(img);
		    frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		    //frame.getContentPane().add (new OperationEditorWindow(rhapsody,selected));
		    frame.pack();
		    ScreenMonitor.Instance.registerFrame(frame);

			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);
		    
	        frame.setVisible(true);
	        if(op.isReadOnly()!=0)
			{
				okButton.setEnabled(false);			
				applyButton.setEnabled(false);
			}	           
		}
		else
		{
			aMainApp.printToRhapsody("no operation - exit");
		}
	}
	
	
	
	
	
	
	@Override
	public void hyperlinkUpdate(HyperlinkEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(mySelectedOperation.isReadOnly()==0)
		{
			if(command.equals("ok")||command.equals("apply")||command.equals("generate"))
			{
				String text = myTextArea.getText();
				if(mySelectedOperation!=null)
				{
					mySelectedOperation.setBody(text);
				}
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
		
		if(command.equals("generate"))
		{
			//find class
			IRPModelElement element = mySelectedOperation.getOwner();
			if (element instanceof IRPClass) 
			{
				IRPClass selectedClass = (IRPClass) element;
				selectedClass.locateInBrowser();
				IRPCollection generateCollection = myApplication.getListOfSelectedElements();
				generateCollection.addItem(selectedClass);
				
				myApplication.generateElements(generateCollection);
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
        		IASTTranslationUnit translationUnit = ASTHelper.getTranslationUnit(tc.getText(),null);
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
		//System.out.println(expression.getRawSignature());
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
			IRPModelElement element = ((RhapsodyClassifier)c).getIRPClassifier(false);
			
			//System.out.println(element.getName());
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
    static public String getElementNameAtCaret(JTextComponent tc) throws BadLocationException 
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
    
    static public boolean isElementChar(char ch) {
        return Character.isLetterOrDigit(ch);
     }

	
	
}

class AddDependency extends TextAction
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8665373931943623988L;
	
	private IRPClass myClass;

	public AddDependency(IRPOperation aOperation) {
		super("Add Dependency");	

		IRPModelElement element = aOperation.getOwner();
		if(element instanceof IRPClass)
		{
			myClass = (IRPClass)element;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		
		if(myClass==null)
		{
			System.out.println("Class not defined!");
			return;
		}
		
		if(myClass.isReadOnly()==1)
		{
			System.out.println("Class is read only");
			return;
		}
		
		
		JTextComponent tc = getTextComponent(e);
		String elementName = null;

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
              elementName = OpenFeature.getElementNameAtCaret(tc);
           }
        } 
        catch (BadLocationException ble) 
        {
           ble.printStackTrace();
           UIManager.getLookAndFeel().provideErrorFeedback(tc);
           return;
        }
        
        
        IRPProject project = myClass.getProject();

        if(project==null)
        {
        	System.out.println("Project not defined");
        	return;
        }
        
        IRPClass foundClass = project.findClass(elementName);
        
        if(foundClass == null)
        {
        	System.out.println("Class " + elementName + " not found in " + project.getName());
        	return;
        }
        
        //check if dependeny already there...
        
        List<IRPDependency> dependencies = myClass.getDependencies().toList();
        
        boolean found = false;
        for(IRPDependency dependency:dependencies)
        {
        	if(dependency.getDependsOn().equals(foundClass))
        	{
        		System.out.println("Dependency is already there");
        		return;
        	}
        }
        
        IRPDependency newDependency = myClass.addDependencyTo(foundClass);
        List<IRPStereotype> stereoTypes = project.getAllStereotypes().toList();
        for(IRPStereotype stereoType:stereoTypes)
        {
        	if(stereoType.getName().equals("Usage"))
        	{
        		newDependency.addSpecificStereotype(stereoType);
        		break;
        	}
        }
        
        
        
        
        
        
        
        
        	
        
        
        
        

        

	}
		
}

