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

import javax.print.attribute.TextSyntax;
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

import de.schlaich.gunnar.rhapsody.completionprovider.ClassifierCompletionProvider;
import de.schlaich.gunnar.rhapsody.completionprovider.ClassifierCompletionProvider.visibility;
import de.schlaich.gunnar.rhapsody.operationeditor.CPPCellRenderer;
import de.schlaich.gunnar.rhapsody.operationeditor.OperationEditorWindow;


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
			
			StackTraceElement[] stes = ex.getStackTrace();
			
			printToRhapsody("--------------------------- Exception occured: ");
			for(StackTraceElement ste:stes)
			{
				printToRhapsody(ste.toString());
			}
			
			
			printToRhapsody(ex.toString());
			printToRhapsody("--------------------------- Exception end");
			
			
		}
		
		
		
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
