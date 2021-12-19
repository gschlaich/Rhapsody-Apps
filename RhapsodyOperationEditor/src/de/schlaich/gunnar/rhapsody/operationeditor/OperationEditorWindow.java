package de.schlaich.gunnar.rhapsody.operationeditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFieldReference;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.ErrorStrip;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;
import org.fife.ui.rsyntaxtextarea.Style;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.parser.TaskTagParser;
import org.fife.ui.rtextarea.Gutter;
import org.fife.ui.rtextarea.RTextScrollPane;

import com.github.difflib.DiffUtils;
import com.github.difflib.algorithm.DiffException;
import com.github.difflib.patch.Patch;
import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPCollection;
import com.telelogic.rhapsody.core.IRPDependency;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPOperation;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPStereotype;
import com.telelogic.rhapsody.core.IRPUnit;

import apps.MainApp;
import de.schlaich.gunnar.parser.CppParser;
import de.schlaich.gunnar.parser.DiffParser;
import de.schlaich.gunnar.rhapsody.completion.RhapsodyClassifier;
import de.schlaich.gunnar.rhapsody.completionprovider.ClassifierCompletionProvider;
import de.schlaich.gunnar.rhapsody.completionprovider.LocalCompletionProvider;
import de.schlaich.gunnar.rhapsody.completionprovider.ClassifierCompletionProvider.visibility;
import de.schlaich.gunnar.rhapsody.utilities.ASTHelper;
import de.schlaich.gunnar.rhapsody.utilities.RhapsodyOperation;
import de.schlaich.gunnar.rhapsody.utilities.RhapsodyPreferences;

public class OperationEditorWindow extends JRootPane implements HyperlinkListener, ActionListener, SyntaxConstants { 

	private RSyntaxTextArea myTextArea;
	private AutoCompletion myAutoComplete;
	private JFrame myFrame;
	private IRPOperation mySelectedOperation;
	private IRPApplication myApplication;
	private SyntaxScheme myScheme;
	private StartAutoCompletion myStartAutoCompletion;
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public OperationEditorWindow(IRPApplication rhapsody, IRPModelElement selected) {
		
		myApplication = rhapsody;
		mySelectedOperation = null;
		
		AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory)TokenMakerFactory.getDefaultInstance();
		atmf.putMapping("text/RhapsodyCPP", "de.schlaich.gunnar.parser.RhapsodyTokenMaker");
		
		
		
		
	    if(selected instanceof IRPOperation )
		{
			mySelectedOperation = (IRPOperation) selected;	
		}
	    else
	    {
	    	//only operations work...
	    	return;
	    }
	   
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
		myTextArea.setCodeFoldingEnabled(false);
		myTextArea.setTabsEmulated(true);
		myTextArea.setTabSize(4);
		myTextArea.addHyperlinkListener(this);
		
		RTextScrollPane scrollPane = new RTextScrollPane(myTextArea, true);
		ErrorStrip es = new ErrorStrip(myTextArea);
		JPanel temp = new JPanel(new BorderLayout());
		temp.add(scrollPane);
		temp.add(es, BorderLayout.LINE_END);
		

		contentPane.add(temp);
		
		
		
		
		setContentPane(contentPane);
		scrollPane.setIconRowHeaderEnabled(true);
		
		myTextArea.setText(mySelectedOperation.getBody());
		
		TaskTagParser taskTagParser = new TaskTagParser();
		myTextArea.addParser(taskTagParser);
		
		
		
		myStartAutoCompletion = new StartAutoCompletion(myTextArea, myAutoComplete, mySelectedOperation, myApplication);
		
		myStartAutoCompletion.start();
		
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
		
		myFrame.addWindowListener(new WindowAdapter() {
	        public void windowClosing(WindowEvent e) 
	        {
	        	if(textChanged())
				{
					int n = JOptionPane.showConfirmDialog(
					    null,
					    "There are changes in the editor\nOK: Apply Changes to the model\nCancel: Changes are irretrievably deleted",
					    "Apply changes to model?",
					    JOptionPane.OK_CANCEL_OPTION);
				
					if(n==0)
					{
						String text = myTextArea.getText();
						if(mySelectedOperation!=null)
						{
							mySelectedOperation.setBody(text);
						}
					}
				}
	        	
	        	RhapsodyPreferences prefs = RhapsodyPreferences.Get();
				prefs.clearRhapsodyModelElement(mySelectedOperation);
	        	
	        	
	        }

	    });
		
		
	}
	
	public static void Run(IRPApplication rhapsody, IRPModelElement selected, MainApp aMainApp)
	{
		
		if(selected instanceof IRPOperation)
		{	
			
			RhapsodyPreferences prefs = RhapsodyPreferences.Get();
			
			if(prefs.checkRhapsodyModelElement(selected))
			{
				aMainApp.printToRhapsody("Operation already open");
				return;
			}
			
			prefs.setRhapsodyModelElement(selected);
			
			IRPOperation op = (IRPOperation)selected;

			aMainApp.printToRhapsody("Edit Operation of " + selected.getName());
			
			aMainApp.printToRhapsody("Java Version: " + System.getProperty("java.vm.version"));
			aMainApp.printToRhapsody(System.getProperty("java.version"));
			String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());	
			aMainApp.printToRhapsody(timeStamp);
			
			String imageName = op.getIconFileName();
			imageName = imageName.replace("\\", "/");
			
			List<BufferedImage> icons = new ArrayList<BufferedImage>();
			File f = new File(imageName);
			try 
			{
				icons = Imaging.getAllBufferedImages(f);
				
			} 
			catch (ImageReadException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
				//icons =  ICODecoder.read(f);
			
			Image img = null;
			
			if(icons.size()>0)
			{
				img = icons.get(icons.size()-1);
			}
			
			
			//Toolkit kit = Toolkit.getDefaultToolkit();
			//Image img = kit.createImage(imageName);

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
			
			JButton explorerButton = new JButton("Explorer");
			buttonPanel.add(explorerButton);
			
			JButton updateButton = new JButton("Update");
			buttonPanel.add(updateButton);
		
			JButton generateButton = new JButton("Generate");
			buttonPanel.add(generateButton);
			
			JButton roundTripButton = new JButton("RoundTrip");
			buttonPanel.add(roundTripButton);
			
			JButton revertButton = new JButton("Revert");
			buttonPanel.add(revertButton);
			
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
			
			explorerButton.setActionCommand("explorer");
			explorerButton.addActionListener(oew);
			
			updateButton.setActionCommand("update");
			updateButton.addActionListener(oew);
			
			roundTripButton.setActionCommand("roundTrip");
			roundTripButton.addActionListener(oew);
			
			revertButton.setActionCommand("revert");
			revertButton.addActionListener(oew);
			
			
			//frame.add(editorPanel);
			frame.add(buttonPanel,BorderLayout.SOUTH);
			if(img!=null)
			{
				frame.setIconImage(img);
			}
			
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
			//aMainApp.printToRhapsody("no operation - exit");
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
				
				DiffParser diffParser = myStartAutoCompletion.getDiffParser();
				if(diffParser!=null)
				{
					diffParser.update(text);
				}
				
			}
		}
		
		
		if(command.equals("cancel"))
		{
			if(textChanged())
			{
				int n = JOptionPane.showConfirmDialog(
				    null,
				    "Changes are irretrievably deleted",
				    "Discard all changes in the editor?",
				    JOptionPane.OK_CANCEL_OPTION);
			
				if(n!=0)
				{
					return;
				}
				myTextArea.setText(mySelectedOperation.getBody());
			}
		}
		
		if(command.equals("ok")||command.equals("cancel"))
		{	
			if(myFrame!=null)
			{
				myFrame.dispose();
				RhapsodyPreferences prefs = RhapsodyPreferences.Get();
				prefs.clearRhapsodyModelElement(mySelectedOperation);
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
		if(command.equals("explorer"))
		{
			IRPUnit unit = mySelectedOperation.getSaveUnit();
			String directory = unit.getCurrentDirectory();
			String sbsFile = unit.getFilename();
			System.out.println(directory);
			try 
			{
				Runtime.getRuntime().exec("explorer.exe /select," + directory+"\\"+sbsFile);
			} 
			catch (IOException e1) 
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		if(command.equals("update")||command.equals("generate"))
		{
			//may start in a worker thread?
			IRPModelElement element = mySelectedOperation.getOwner();
			if (element instanceof IRPClass) 
			{
				IRPClass selectedClass = (IRPClass) element;
				ClassifierCompletionProvider provider = StartAutoCompletion.GetClassifierCompletionProvider();
				provider.createClassCompletion(selectedClass, visibility.v_private);
			}
		}
		if(command.equals("roundTrip"))
		{
			if(textChanged())
			{
				int n = JOptionPane.showConfirmDialog(
				    null,
				    "Will overwrite changes in editor",
				    "Roundtrip from source file?",
				    JOptionPane.YES_NO_OPTION);
			
				if(n!=0)
				{
					return;
				}
			}
			String roundTripText = ASTHelper.getSourceText(mySelectedOperation, myApplication);
				
			if(roundTripText!=null)
			{
				myTextArea.setText(roundTripText);
			}
			else
			{
				
				System.out.println("An error occured. Roundtrip not possible");
			}	
		}
		if(command.equals("revert"))
		{
			
			if(textChanged())
			{
				int n = JOptionPane.showConfirmDialog(
					    null,
					    "Will overwrite changes in editor",
					    "Revert from model?",
					    JOptionPane.YES_NO_OPTION);
				
				if(n!=0)
				{
					return;
				}
				
				if(mySelectedOperation!=null)
				{
					String body = mySelectedOperation.getBody();
					myTextArea.setText(body);
				}
			}
			
		}
		
	}
	
	private boolean textChanged()
	{
		List<String> editorLines = ASTHelper.getLines(myTextArea.getText());
		List<String> bodyLines = ASTHelper.getLines(mySelectedOperation.getBody());
		try 
		{
			Patch<String> patch = DiffUtils.diff(bodyLines, editorLines);
			return(patch.getDeltas().isEmpty()==false);
		} 
		catch (DiffException e) {
			
			e.printStackTrace();
		}
		
		return false;
		
		
		
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
	
	@SuppressWarnings("unchecked")
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

	@SuppressWarnings("unchecked")
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
        
        IRPModelElement foundClass = project.findNestedElementRecursive(elementName, "Class");
 
        if(foundClass == null)
        {
        	foundClass = project.findNestedElementRecursive(elementName, "Type");
        }
        	
        if(foundClass == null)
        {
        	System.out.println("Class " + elementName + " not found in " + project.getName());
        	return;
        }
        

        //check if dependeny already there...
        
        List<IRPDependency> dependencies = myClass.getDependencies().toList();
        
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

