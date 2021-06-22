package apps;



import java.awt.BorderLayout;
import java.awt.MouseInfo;
import java.awt.Point;
import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.print.attribute.standard.MediaSize.Other;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.FunctionCompletion;
import org.fife.ui.autocomplete.LanguageAwareCompletionProvider;
import org.fife.ui.autocomplete.ParameterizedCompletion;
import org.fife.ui.autocomplete.ParameterizedCompletion.Parameter;
import org.fife.ui.autocomplete.ShorthandCompletion;
import org.fife.ui.autocomplete.VariableCompletion;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import com.ibm.rhapsody.apps.*;
import com.ibm.rhapsody.apps.core.RAController;
import com.telelogic.rhapsody.core.*;

import apps.ClassifierCompletionProvider.visibility;


public class MainApp extends App {
	
	/*
	* This method is called on invoking an app inside Rhapsody.
	* rhapsody - Instance of an active Rhapsody application 
	* selected - Selected element in Rhapsody
	*/
	
	
	
	public void execute(IRPApplication rhapsody, IRPModelElement selected) {
	
		
		println("Start App");
		System.out.println("Start App");
		
		Runtime r = Runtime.getRuntime();
		
		
		
		//execTest(rhapsody, selected);
		
		
		try 
		{
			OperationEditorWindow.Run(rhapsody, selected, this);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			println(ex.toString());
		}
		
		
		
	}
	
	private void execTest(IRPApplication rhapsody,IRPModelElement selected)
	{
		final Display display = new Display();
	    final Shell shell = new Shell(display);
	    shell.setText("Operation Editor");
	    
	    Composite editComp = new Composite(shell, SWT.EMBEDDED); 

	    editComp.setBounds(5,5,800,600);
	    
	    java.awt.Frame EditFrame = SWT_AWT.new_Frame(editComp);
	    java.awt.Panel panel = new java.awt.Panel(new java.awt.BorderLayout());
	    
	    EditFrame.add(panel);
	   
	    
	    IRPOperation selectedOperation = null;
	    IRPClass selectedClass = null;
	    
	    if(selected instanceof IRPOperation )
		{
			selectedOperation = (IRPOperation) selected;	
		}
	    else
	    {
	    	//only operations work...
	    	return;
	    }
	    

	    selectedClass = (IRPClass) selectedOperation.getOwner();
	    
	    StringBuilder shellText = new StringBuilder();
	    
	    
	    
	    shellText.append("Edit in " );
	    shellText.append(selectedClass.getFullPathName());
	    shellText.append(": ");
	    shellText.append(selectedOperation.getImplementationSignature());
	    
	    shell.setText(shellText.toString());
	    
	    
	    JPanel contentPane = new JPanel(new BorderLayout());
	    
	    Point location = MouseInfo.getPointerInfo().getLocation(); 
		int x = (int) location.getX();
		int y = (int) location.getY();
	    
	    
	    contentPane.setLocation(location);
	    RSyntaxTextArea textArea = new RSyntaxTextArea(25, 40);
	    textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS);
	    textArea.setCaretPosition(0);
		textArea.requestFocusInWindow();
		textArea.setMarkOccurrences(true);
		textArea.setCodeFoldingEnabled(true);
		textArea.setTabsEmulated(true);
		textArea.setTabSize(3);
		
		
		CompletionProvider provider = new ClassifierCompletionProvider(selectedClass, visibility.v_public);
				
		
		// Install auto-completion onto our text area.
		AutoCompletion ac = new AutoCompletion(provider);
		ac.setListCellRenderer(new CPPCellRenderer());
		ac.setShowDescWindow(true);
		ac.setParameterAssistanceEnabled(true);
		
		ac.setAutoCompleteEnabled(true);
		ac.setAutoActivationEnabled(true);
		ac.setAutoActivationDelay(750);
		//ac.setExternalURLHandler(new JavadocUrlHandler());
		
		//ac.setParamChoicesRenderer(new JavaParamListCellRenderer());
		
		
		
		ac.install(textArea);
	    
	   contentPane.add(textArea);

	   textArea.setText(selectedOperation.getBody());
	    
	    JScrollPane scrollPane = new JScrollPane(contentPane);

	    panel.add(scrollPane);

	    shell.open();
	    while (!shell.isDisposed()) {
	      if (!display.readAndDispatch())
	        display.sleep();
	    }
	    display.dispose();
	}
	
	
	
	public void printToRhapsody(String aText)
	{
		println(aText);
	}
	


	/*
     *  Debug app by launching it as "Java Application" is Eclipse.
	 *  Note: Select an element in Rhapsody in order to simulate launching app on a selected element in the browser.
     */	
	public static void main(String[] args) {
		MainApp app = new MainApp();
		app.invokeFromMain();
	}
	
	
	
	
	
	
}
