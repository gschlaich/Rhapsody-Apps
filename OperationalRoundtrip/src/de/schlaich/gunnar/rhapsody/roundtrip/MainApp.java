package de.schlaich.gunnar.rhapsody.roundtrip;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.eclipse.swt.internal.webkit.JSClassDefinition;

import com.github.difflib.DiffUtils;
import com.github.difflib.algorithm.DiffException;
import com.github.difflib.algorithm.myers.MyersDiff;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;
import com.github.difflib.text.DiffRow;
import com.github.difflib.text.DiffRow.Tag;
import com.github.difflib.text.DiffRowGenerator;
import com.ibm.rhapsody.apps.*;
import com.telelogic.rhapsody.core.*;

import de.schlaich.gunnar.rhapsody.utilities.ASTHelper;
import de.schlaich.gunnar.rhapsody.utilities.RhapsodyHelper;
import de.schlaich.gunnar.rhapsody.utilities.RhapsodyOperation;

import com.ibm.rhapsody.apps.TriggerAdapter.Trigger;

public class MainApp extends App {
	
	/*
	* This method is called on invoking an app inside Rhapsody.
	* rhapsody - Instance of an active Rhapsody application 
	* selected - Selected element in Rhapsody
	*/
	
	private static IRPClass myClass = null;
	private String myDiffResults;
	private static MainApp myApp;
	private static IRPProject myProject;
	private static JFrame myFrame;
	private static IRPComponent myActiveComponent;
	private List<IRPOperation> myChangedOperations;
	
	
	@SuppressWarnings("unchecked")
	public void execute(IRPApplication rhapsody, IRPModelElement selected) 
	{
		COperationalRoundtrip opRoundtrip = new COperationalRoundtrip();
		
		opRoundtrip.startRoundtrip(rhapsody, selected);

	}	
	
	
	
	
    /*
     *  Debug app by launching it as "Java Application" is Eclipse.
	 *  Note: Select an element in Rhapsody in order to simulate launching app on a selected element in the browser.
     */	
	public static void main(String[] args) {
		myApp = new MainApp();
		
		String connectionstring = null;
		
		if (args.length >= 1) 
		{
			connectionstring = args[0];
		}
		
		RhapsodyHelper.executeApp(myApp, connectionstring);
		
		
	}
	
	
	
	
}
