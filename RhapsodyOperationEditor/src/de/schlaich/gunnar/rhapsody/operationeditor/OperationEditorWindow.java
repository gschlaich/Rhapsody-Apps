package de.schlaich.gunnar.rhapsody.operationeditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.TextArea;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
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
import com.ibm.icu.util.ULocale.Minimize;
import com.ibm.rhapsody.apps.ui.SearchRunDialog;
import com.telelogic.rhapsody.core.IRPAction;
import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPArgument;
import com.telelogic.rhapsody.core.IRPAttribute;
import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPCollection;
import com.telelogic.rhapsody.core.IRPComment;
import com.telelogic.rhapsody.core.IRPDependency;
import com.telelogic.rhapsody.core.IRPDiagram;
import com.telelogic.rhapsody.core.IRPEvent;
import com.telelogic.rhapsody.core.IRPEventReception;
import com.telelogic.rhapsody.core.IRPInterfaceItem;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPOperation;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPProfile;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPStatechart;
import com.telelogic.rhapsody.core.IRPStereotype;
import com.telelogic.rhapsody.core.IRPTag;
import com.telelogic.rhapsody.core.IRPTransition;
import com.telelogic.rhapsody.core.IRPTrigger;
import com.telelogic.rhapsody.core.IRPType;
import com.telelogic.rhapsody.core.IRPUnit;
import com.telelogic.rhapsody.core.RPApplicationListener;

import apps.MainApp;
import de.schlaich.gunnar.parser.CodeAnalysisParser;
import de.schlaich.gunnar.parser.CppParser;
import de.schlaich.gunnar.parser.DiffParser;
import de.schlaich.gunnar.rhapsody.completion.ASTUtilities;
import de.schlaich.gunnar.rhapsody.completion.RhapsodyClassifier;
import de.schlaich.gunnar.rhapsody.completionprovider.ClassifierCompletionProvider;
import de.schlaich.gunnar.rhapsody.completionprovider.LocalCompletionProvider;
import de.schlaich.gunnar.rhapsody.ghs.MultiPlugin;
import de.schlaich.gunnar.rhapsody.completionprovider.ClassifierCompletionProvider.visibility;
import de.schlaich.gunnar.rhapsody.utilities.ASTHelper;
import de.schlaich.gunnar.rhapsody.utilities.HistoryControl;
import de.schlaich.gunnar.rhapsody.utilities.HistoryElement;
import de.schlaich.gunnar.rhapsody.utilities.RhapsodyHelper;
import de.schlaich.gunnar.rhapsody.utilities.RhapsodyOperation;
import de.schlaich.gunnar.rhapsody.utilities.RhapsodyPreferences;
import de.schlaich.gunnar.rhapsody.vs.VSPlugin;

public class OperationEditorWindow extends JRootPane implements HyperlinkListener, ActionListener, SyntaxConstants { 

	private RSyntaxTextArea myTextArea;
	private AutoCompletion myAutoComplete;
	private JFrame myFrame;
	private IRPOperation mySelectedOperation;
	private IRPApplication myApplication;
	private SyntaxScheme myScheme;
	private StartAutoCompletion myStartAutoCompletion;
	private String myGuid;
	private IRPAction myAction = null;
	private ApplicationListener myApplicationListener = null;
	private boolean myExitOnClose = true;

	private static Map<IRPModelElement,OperationEditorWindow>  myOperationWindows = null;
	

	private static final long serialVersionUID = 1L;
	
	
	public static OperationEditorWindow Get(IRPModelElement aModelElement) {
		
		if(myOperationWindows==null)
		{
			return null;
		}
		return myOperationWindows.get(aModelElement);
	}
	
	public OperationEditorWindow(IRPApplication rhapsody, IRPOperation aOperation, IRPAction aAction, boolean aExitOnClose) {
		
		if(myOperationWindows==null)
		{
			myOperationWindows = new HashMap<IRPModelElement,OperationEditorWindow>();
		}
		
		myOperationWindows.put(aOperation, this);
		
		myAction = aAction;
		myExitOnClose = aExitOnClose;
		
		JFrame frame = new JFrame (RhapsodyOperation.getOperation(aOperation));
		
		frame.setLayout(new BorderLayout());
		
		JPanel buttonPanel = new JPanel();
		
		JButton locateButton = new JButton("Locate");
		buttonPanel.add(locateButton);
		
		JButton openFeatureButton = new JButton("Open Feature");
		buttonPanel.add(openFeatureButton);
		
		JButton explorerButton = new JButton("Explorer");
		buttonPanel.add(explorerButton);
		
		JButton vsIdeButton = null;
		JButton multiIdeEditButton = null;
		JButton multiIdeDebugButton = null;
		JButton multiCompileButton = null;
		JButton multiBuildButton = null;
			
		LoadInIDE ide = LoadInIDE.Instance(myApplication);
		
		if(ide.isVSIde())
		{
			vsIdeButton = new JButton("View in VS");
			buttonPanel.add(vsIdeButton);
		}
		
		if(ide.isMultiIde())
		{
			multiIdeEditButton = new JButton("Multi Edit");
			buttonPanel.add(multiIdeEditButton);
			multiIdeDebugButton = new JButton("Multi Debug");
			buttonPanel.add(multiIdeDebugButton);
			multiCompileButton = new JButton("Compile");
			buttonPanel.add(multiCompileButton);
			multiBuildButton = new JButton("Build");
			buttonPanel.add(multiBuildButton);
			
		}
		
		
		
		JButton updateButton = new JButton("Update");
		buttonPanel.add(updateButton);
	
		JButton generateButton = new JButton("Generate");
		buttonPanel.add(generateButton);
		
		JButton roundTripButton = new JButton("RoundTrip");
		buttonPanel.add(roundTripButton);
		
		JButton revertButton = new JButton("Revert");
		buttonPanel.add(revertButton);
		
		JButton formatButton = new JButton("Format");
		buttonPanel.add(formatButton);
		
		JButton applyButton = new JButton("Apply");
		buttonPanel.add(applyButton);
		
		JButton okButton = new JButton("ok");
		buttonPanel.add(okButton);
			
		JButton cancelButton = new JButton("Cancel");
		buttonPanel.add(cancelButton);
		
		buttonPanel.setVisible(true);
		
		
		OperationEditorWindow oew = this;
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
		
		openFeatureButton.setActionCommand("openFeature");
		openFeatureButton.addActionListener(oew);
		
		generateButton.setActionCommand("generate");
		generateButton.addActionListener(oew);
		
		explorerButton.setActionCommand("explorer");
		explorerButton.addActionListener(oew);
		
		
		if(vsIdeButton!=null)
		{
			vsIdeButton.setActionCommand("IDE");
			vsIdeButton.addActionListener(oew);
		}
		if(multiIdeDebugButton!=null)
		{
			multiIdeDebugButton.setActionCommand("IDEDebug");
			multiIdeDebugButton.addActionListener(oew);
		}
		
		if(multiIdeEditButton!=null)
		{
			multiIdeEditButton.setActionCommand("IDE");
			multiIdeEditButton.addActionListener(oew);
		}
		
		if(multiCompileButton!=null)
		{
			multiCompileButton.setActionCommand("compile");
			multiCompileButton.addActionListener(oew);
		}
		
		if(multiBuildButton!=null)
		{
			multiBuildButton.setActionCommand("build");
			multiBuildButton.addActionListener(oew);
		}		
		
		updateButton.setActionCommand("update");
		updateButton.addActionListener(oew);
		
		roundTripButton.setActionCommand("roundTrip");
		roundTripButton.addActionListener(oew);
		
		revertButton.setActionCommand("revert");
		revertButton.addActionListener(oew);
		
		formatButton.setActionCommand("format");
		formatButton.addActionListener(oew);
		
		
		if(myAction!=null)
		{
			locateButton.setEnabled(false);
			generateButton.setEnabled(false);
			roundTripButton.setEnabled(false);
		}
		
		//frame.add(editorPanel);
		frame.add(buttonPanel,BorderLayout.SOUTH);
		
		String imageName = aOperation.getIconFileName();
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
			
			
		Image img = null;
		
		if(icons.size()>0)
		{
			img = icons.get(icons.size()-1);
		}
		
		
		if(img!=null)
		{
			frame.setIconImage(img);
		}
		
		if(aExitOnClose)
		{
			frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		}
		else
		{
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		}
	    
	    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		
		
	  	int factorW = 7;
	  	int factorH = 16;
	  		    
	  			
	  			
	  	int rows = (int)(dim.height/factorH*0.7);
	  	int cols = (int)(dim.width/factorW*0.5);
	  			
	  	
        if(aOperation.isReadOnly()!=0)
		{
			okButton.setEnabled(false);			
			applyButton.setEnabled(false);
		}
	
		myApplication = rhapsody;
		mySelectedOperation = null;
		
		AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory)TokenMakerFactory.getDefaultInstance();
		atmf.putMapping("text/RhapsodyCPP", "de.schlaich.gunnar.parser.RhapsodyTokenMaker");

	   
		mySelectedOperation = aOperation;	
		myGuid = aOperation.getGUID();
		
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
		
		myTextArea.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				HistoryElement.AddToHistory(mySelectedOperation);
	
			}
		});
		
		
		
		
		
		RTextScrollPane scrollPane = new RTextScrollPane(myTextArea, true);
		ErrorStrip es = new ErrorStrip(myTextArea);
		JPanel temp = new JPanel(new BorderLayout());
		temp.add(scrollPane);
		temp.add(es, BorderLayout.LINE_END);
		

		contentPane.add(temp);
		
		
		
		
		setContentPane(contentPane);
		scrollPane.setIconRowHeaderEnabled(true);
		
		myTextArea.setText(mySelectedOperation.getBody());
		myTextArea.convertTabsToSpaces();
		
		TaskTagParser taskTagParser = new TaskTagParser();
		myTextArea.addParser(taskTagParser);
		
		
		
		myStartAutoCompletion = new StartAutoCompletion(myTextArea, myAutoComplete, mySelectedOperation, myApplication, myFrame);
		
		myStartAutoCompletion.start();
		
		myTextArea.convertTabsToSpaces();
		myTextArea.requestFocusInWindow();
		
		setRhapsodyStyle();
		
	    ScreenMonitor.Instance.registerFrame(frame);

	  	frame.pack();
	  	
	  	frame.setLocationRelativeTo(null);
 
        frame.setVisible(true);
        
    	//now add rhapsodyListener
        
        myApplicationListener = new ApplicationListener(myFrame,mySelectedOperation,myApplication,myTextArea,myStartAutoCompletion,myExitOnClose);
        myApplicationListener.connect(myApplication);
        
        
       
		/*
		frame.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				
					myFrame.setAlwaysOnTop(false);
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		*/
		
		
		

				
		Runnable focusInWindow = new Runnable() {
			
			@Override
			public void run() {
				myTextArea.requestFocusInWindow();
				
			}
		};
		
		SwingUtilities.invokeLater(focusInWindow); 
		
		
	}
	
	public void setFocus()
	{
		if(myFrame.getState()==java.awt.Frame.ICONIFIED)
		{
			myFrame.setState(java.awt.Frame.NORMAL);
		}
		
		myFrame.setAlwaysOnTop(true);
		
		new java.util.Timer().schedule( 
		        new java.util.TimerTask() {
		            @Override
		            public void run() {
		            	myFrame.requestFocus();
		            	myFrame.toFront();
		            	myFrame.setAlwaysOnTop(false);
		               
		            }
		        }, 
		        1000 
		);
	
	}
	
	
	
	
	
	private void removeActionOperation()
	{

		if(myAction==null)
		{
			return;
		}
		
		if(myApplicationListener!=null)
		{
			myApplicationListener.disconnect();
		}
		
		
		IRPClass owner = (IRPClass)mySelectedOperation.getOwner();
		if(owner==null)
		{
			return;
		}
		
		IRPClassifier argType = null;
		
		List<IRPArgument> args = mySelectedOperation.getArguments().toList();
		if(args.size()==1)
		{
			argType = args.get(0).getType();
		}
		
		

		owner.deleteOperation(mySelectedOperation);
		
		
		if(argType!=null)
		{
			owner.deleteType(argType.getName());
		}
		
				
	}
	
	private void setActionbody(String aText)
	{
		if(myAction==null)
		{
			return;
		}
		
		if(aText.isEmpty()==false)
		{
			if(aText.charAt(0)!='\n')
			{
				aText="\n"+aText;
			}
			
		}
		
		
		myAction.setBody(aText);
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
	
	private void setDarkStyle() 
	{
		//settings should be similar to rhapsody
		myTextArea.setBackground(new Color(0x000000));
		myTextArea.setCaretColor(new Color(0xffffff));
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
	        	
	        	myOperationWindows.remove(mySelectedOperation);
	        	RhapsodyPreferences prefs = RhapsodyPreferences.Get(false);
				prefs.clearRhapsodyModelElement(mySelectedOperation);
				
				removeActionOperation();
	        	
	        	
	        }

	    });

	}
	
	public static void Run(IRPApplication rhapsody, IRPModelElement selected, boolean aExitOnClose)
	{
		
		IRPOperation op = null;
		IRPAction action = null;
		
		if(selected instanceof IRPTransition)
		{
			
			if(selected.getSaveUnit().isReadOnly()==1)
			{

				return;
			}
			
			IRPTransition transition = (IRPTransition)selected;
			
			
			//find class...
			IRPStatechart statechart = transition.getItsStatechart();

			action = transition.getItsAction();
			if(action==null)
			{
				transition.setItsAction("\n");
			}
			
			IRPClass statechartclass = (IRPClass)statechart.getItsClass();
			
			//find params...
			IRPTrigger trigger = transition.getItsTrigger();
			
			String operationName = "__action_of_transition_"+transition.getName();
			
			//check if operation already exists...
			op = (IRPOperation)statechartclass.findNestedElement(operationName, "Operation");
			if(op!=null)
			{
				statechartclass.deleteOperation(op);
			}
			
			op = statechartclass.addOperation(operationName);
			
			op.setBody(action.getBody());

			op.setPropertyValue("CG.Operation.Generate", "None");
			
			if((trigger!=null)&&(trigger.isTimeout()==0))
			{
				String triggerSource = trigger.getBody();
				
				
				IRPModelElement element = statechartclass.findNestedElement(triggerSource, "EventReception");
				
				if(element==null)
				{
					element = statechartclass.findNestedElement(triggerSource, "TriggeredOperation");
				}
				
				if(element!=null)
				{
					 
					IRPInterfaceItem eventReception = (IRPInterfaceItem)element;
					
					
					
					List<IRPArgument> arguments = eventReception.getArguments().toList();
					
					String triggerSourceName = "_"+triggerSource;
					
					IRPType paramsType = null;
					
					paramsType = (IRPType)statechartclass.findNestedElement(triggerSourceName, "Type");
					if(paramsType!=null)
					{
						statechartclass.deleteType(triggerSourceName);
					}
					
					paramsType = statechartclass.addType("_"+triggerSource);
					paramsType.setKind("Structure");
					paramsType.setPropertyValue("CG.Type.Generate", "False");
					
					
					for(IRPArgument argument:arguments)
					{
						IRPAttribute attribute = paramsType.addAttribute(argument.getName());
						attribute.setType(argument.getType());
					}

					IRPArgument params = op.addArgument("params");
					
					params.setType(paramsType);
					params.setPropertyValue("CPP_CG.Type.In", "$type*");
					
					
					
				}
				
			}
			
		}
		else if(selected instanceof IRPAction)
		{
			if(selected.getSaveUnit().isReadOnly()==1)
			{
				return;
			}
			
			
			action = (IRPAction)selected;
			IRPModelElement owner = action.getOwner();
			String actionOf = owner.getName();
			while((owner!=null)&&(owner instanceof IRPClass ==false))
			{
				owner = owner.getOwner();
			}
			
			if(owner==null)
			{
				return;
			}
			
			if(owner instanceof IRPStatechart)
			{
				
				IRPStatechart sc = (IRPStatechart)owner;
				owner = sc.getItsClass();
				//owner = owner.getOwner();
			}
			
			if(owner==null)
			{
				return;
			}
			
			
			IRPClass actionClass = (IRPClass)owner;
			
			String operationName = "__"+actionOf+"_"+action.getName();
			
			
			op = (IRPOperation)actionClass.findNestedElement(operationName, "Operation");
			
			if(op==null)
			{
				op = actionClass.addOperation(operationName);
			}
			op.setBody(action.getBody());
		}
		else if(selected instanceof IRPOperation)
		{	
			op = (IRPOperation)selected;
		}
			
		
		//HistoryElement.AddToHistory(op);
		
		
		OperationEditorWindow win = Get(op);
		if(win!=null)
		{
			win.forceParser();
			win.setFocus();
			
			return;
		}
		
		
			RhapsodyPreferences prefs = RhapsodyPreferences.Get();
			
			if(prefs.checkRhapsodyModelElement(op))
			{
				print(rhapsody, "Operation already open");
				return;
			}
			
			prefs.setRhapsodyModelElement(op);
			
			print(rhapsody, "Edit Operation of " + op.getName());
			
			print(rhapsody, "Java Version: " + System.getProperty("java.vm.version"));
			print(rhapsody, System.getProperty("java.version"));
			String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());	
			print(rhapsody, timeStamp);

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
			
			OperationEditorWindow oew = new OperationEditorWindow(rhapsody, op, action, aExitOnClose);
			oew.setFocus();
	
	}
	
	private static void print(IRPApplication aApplication, String aMessage)
	{
		aApplication.writeToOutputWindow("log", aMessage + "\n");
		System.out.println(aMessage);
	}
	
	
	public void forceParser()
	{
		myStartAutoCompletion.forceCodeAnalysisParser();
	}
	
	
	
	
	
	
	@Override
	public void hyperlinkUpdate(HyperlinkEvent arg0) {
		System.out.println("Hyperlink!");
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		try{
			
		
			if(mySelectedOperation.isReadOnly()==0)
			{
				if(command.equals("ok")||command.equals("apply")||command.equals("generate")||command.equals("compile")||command.equals("build"))
				{
					String text = myTextArea.getText();
					setActionbody(text);
					if(mySelectedOperation!=null)
					{
						mySelectedOperation.setBody(text);
					}
					
					DiffParser diffParser = myStartAutoCompletion.getDiffParser();
					if(diffParser!=null)
					{
						diffParser.update(text);
					}
					CodeAnalysisParser cParser = myStartAutoCompletion.getCodeAnalysisParser();
					if(cParser!=null)
					{
						cParser.clear();
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
			}
			
			
			myOperationWindows.remove(mySelectedOperation);
			RhapsodyPreferences prefs = RhapsodyPreferences.Get();
			prefs.clearRhapsodyModelElement(myGuid);
			removeActionOperation();
			
			if(myExitOnClose)
			{
				System.exit(0);
			}
		}
		
		if(command.equals("locate"))
		{
			if(mySelectedOperation!=null)
			{
				mySelectedOperation.locateInBrowser();
			}
		}
		
		if(command.equals("openFeature"))
		{
			if(mySelectedOperation!=null)
			{
				mySelectedOperation.openFeaturesDialog(1);
			}
		}
		
		if(command.equals("generate")|| command.equals("compile")|| command.equals("build"))
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
		if(command.equals("generate")|| command.equals("compile")||command.equals("build"))
		{
			//may start in a worker thread?
			IRPModelElement element = mySelectedOperation.getOwner();
			if (element instanceof IRPClass) 
			{
				IRPClass selectedClass = (IRPClass) element;
				ClassifierCompletionProvider provider = StartAutoCompletion.GetClassifierCompletionProvider();
				provider.createClassCompletion(selectedClass, visibility.v_private,true);
			}
		}
		if(command.equals("update"))
		{
			myStartAutoCompletion.updateCompletionProvider();
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
				
				if(myAction!=null)
				{
					String body = myAction.getBody();
					myTextArea.setText(body);
				}
				else if(mySelectedOperation!=null)
				{
					String body = mySelectedOperation.getBody();
					myTextArea.setText(body);
				}
			}
			
		}
		if(command.equals("format"))
		{
			EditorCodeFormatter f = new EditorCodeFormatter();
			String body = myTextArea.getText();
			String formatted = f.format(body);
			
			myTextArea.setText(formatted);
			
		}
		if(command.equals("IDE"))
		{
			LoadInIDE loadInIde = LoadInIDE.Instance(myApplication);
			
			if(loadInIde!=null)
			{
				loadInIde.load(mySelectedOperation, false);
			}
		}
		
		if(command.equals("IDEDebug"))
		{
			LoadInIDE loadInIde = LoadInIDE.Instance(myApplication);
			
			if(loadInIde!=null)
			{
				loadInIde.load(mySelectedOperation, true);
			}
		}
		
		if(command.equals("compile"))
		{
			LoadInIDE loadInIde = LoadInIDE.Instance(myApplication);
			if(loadInIde!=null)
			{
				loadInIde.compile(mySelectedOperation);
			}
			
		
		}
		
		if(command.equals("build"))
		{
			LoadInIDE loadInIde = LoadInIDE.Instance(myApplication);
			if(loadInIde!=null)
			{
				loadInIde.build(mySelectedOperation.getProject());
			}
			
		}
		
		}
		catch (Exception e1) 
		{
			e1.printStackTrace();
			
			JOptionPane.showMessageDialog(
				    null,
				    "Rhapsody  model already closed","Changes are irretrievably deleted",JOptionPane.ERROR_MESSAGE);
			
			myFrame.dispose();
			RhapsodyPreferences prefs = RhapsodyPreferences.Get();
			prefs.clearRhapsodyModelElement(myGuid);
			if(myExitOnClose)
			{
				System.exit(0);
			}
		}
		
	}
	
	public boolean textChanged()
	{
		List<String> editorLines = ASTHelper.getLines(myTextArea.getText());
		List<String> bodyLines = ASTHelper.getLines(mySelectedOperation.getBody());
		try 
		{
			if((bodyLines==null)||(editorLines==null))
			{
				return false;
			}
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
	private IRPApplication myApplication = null;

	public OpenFeature(ClassifierCompletionProvider aCompletionProvider, IRPApplication aApplication) {
		super("Open feature");	
		myCompletionProvider = aCompletionProvider;
		myApplication = aApplication;
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
        		openFeatureDialog(element);
        		return;
	
        	}
        	if(c==null)
        	{
        		List<Completion> cs = localCompletionProvider.getCompletionByInputText(elementName);
        		if((cs!=null)&&(cs.size()>0)&&(cs.get(0) instanceof RhapsodyClassifier))
        		{
        			c = cs.get(0);
        			IRPModelElement element = ((RhapsodyClassifier)c).getElement();
        			openFeatureDialog(element);
            		return;
        		}
        	}
        	if(c==null)
        	{
        		IRPClassifier classifier = myCompletionProvider.getClassifier();
        		IRPClassifier cl = RhapsodyOperation.findClassifier(classifier, elementName);
        		if(cl!=null)
        		{
        			
        			openFeatureDialog(cl);
        			return;
        		}
        		
        	}
        	
        	if(c==null)
        	{
        		IASTTranslationUnit translationUnit = ASTUtilities.getTranslationUnit(tc.getText(),null);
        		if(translationUnit!=null)
        		{
        			IASTNode node = ASTHelper.getNodeAtPosition(tc.getSelectionStart(), tc.getSelectionEnd(), translationUnit);
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
			RhapsodyClassifier rc = (RhapsodyClassifier)c;
			IRPModelElement element = rc.getIRPClassifier(rc.isPointer());
			
			//System.out.println(element.getName());
			List<IRPModelElement> elements = element.getNestedElements().toList();
			for(IRPModelElement me : elements)
			{
			
				if(me.getName().equals(aElementName))
				{
					openFeatureDialog(me);
					return true;
				}
				
			}
			//not found...
			
			openFeatureDialog(element);
			return true;
		
				
		}
		return false;
	}

	public void openFeatureDialog(IRPModelElement aElement) {
		if(aElement instanceof IRPOperation)
		{
			OperationEditorWindow.Run(myApplication, aElement, false);
		}
		else
		{
			aElement.openFeaturesDialog(1);
		}
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

class SearchElement extends TextAction
{
	private ClassifierCompletionProvider myCompletionProvider = null;
	private IRPApplication myRhapsodyApplication = null;
	
	public SearchElement(ClassifierCompletionProvider aCompletionProvider, IRPApplication aRhapsodyApplication) {
		super("Search Element");	
		myCompletionProvider = aCompletionProvider;
		myRhapsodyApplication = aRhapsodyApplication;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
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
              elementName = OpenFeature.getElementNameAtCaret(tc);
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
        	
        	IRPModelElement element = null;
        	Completion c = myCompletionProvider.getFirstCompletion(elementName);
        	if((c!=null) &&( c instanceof RhapsodyClassifier))
        	{
        		RhapsodyClassifier rc = (RhapsodyClassifier)c;
        		element = rc.getElement();
        	}
        	if(c==null)
        	{
        		List<Completion> cs = localCompletionProvider.getCompletionByInputText(elementName);
        		if((cs!=null)&&(cs.size()>0)&&(cs.get(0) instanceof RhapsodyClassifier))
        		{
        			c = cs.get(0);
        			element = ((RhapsodyClassifier)c).getElement();        		
        		}
        	}
        	if(c==null)
        	{
        		IRPClassifier classifier = myCompletionProvider.getClassifier();
        		element = RhapsodyOperation.findClassifier(classifier, elementName);
	
        	}
        	if(element!=null)
        	{
        		RhapsodyHelper.searchElement(myRhapsodyApplication, element);
        	}
        	
        }
	}
	
}

class FormatSelected extends TextAction
{

	private static final long serialVersionUID = -8665373931943623988L;
	
	public FormatSelected() 
	{
		super("Format Selected");
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		JTextComponent tc = getTextComponent(e);
		String selectedText = tc.getSelectedText();
		
		EditorCodeFormatter formatter = new EditorCodeFormatter();
		selectedText = formatter.format(selectedText);
		tc.replaceSelection(selectedText);

	}
		
}

class GetInfo extends TextAction
{

	private static final long serialVersionUID = -4057763339265129695L;
	private AutoCompletion myAutoCompletion = null;
	public GetInfo(AutoCompletion aAutoCompletion) {
		super("Show Info");
		myAutoCompletion = aAutoCompletion;
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		myAutoCompletion.doCompletion();
		
	}
	
}

class SetBreakpoint extends TextAction
{
	private IRPOperation myOperation = null;
	private RSyntaxTextArea myTextArea = null;
	private StartAutoCompletion myStartAutoCompletion = null;

	public SetBreakpoint(IRPOperation aOperation, RSyntaxTextArea aTextArea, StartAutoCompletion aStartAutoCompletion) {
		super("set Breakpoint");
		myOperation = aOperation;
		myTextArea = aTextArea;
		myStartAutoCompletion = aStartAutoCompletion;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		IRPProject project = myOperation.getProject();
        
        if(project==null)
        {
        	return;
        }
        
        IRPModelElement breakpointMe = project.findAllByName("BreakPoint", "Stereotype");
        
        if(breakpointMe == null)
        {
        	return;
        }
        
        if(breakpointMe instanceof IRPStereotype == false)
        {
        	return;
        }
        
        IRPStereotype breakpointSt = (IRPStereotype) breakpointMe;
        
		
		int caretPosition = myTextArea.getCaretPosition();
		
		String textUpToCursor = myTextArea.getText().substring(0, caretPosition);
        int lineNumber = textUpToCursor.split("\n").length;
        
        String breakpointName = myOperation.getName()+"_"+lineNumber;
        
        myOperation.getNestedElementsByMetaClass("Comment", 0);
        
        IRPModelElement newAggr = myOperation.addNewAggr("Comment",breakpointName);
        
        if(newAggr == null)
        {
        	System.out.println("new breakpoint is null");
        	return;
        }
        
        IRPComment bpComment = (IRPComment) newAggr;  
        
        bpComment.addStereotype("BreakPoint", "Comment");
        
        IRPTag offsetTag = bpComment.getTag("Offset");  
     
        String offsetStr = Integer.toString(lineNumber-3);
        
        bpComment.setTagValue(offsetTag, offsetStr);
        
        myStartAutoCompletion.forceCodeAnalysisParser();

	}
	
	
}



/*
class AskGPtIssue extends TextAction
{

	
	private static final long serialVersionUID = 3801585512847118816L;
	private AskIssues myAskIssues = null;
	private IRPApplication myApplication = null;
	
	public AskGPtIssue(IRPApplication aApplication) {
		super("Ask Issue ChatGPT");
		
		RhapsodyPreferences rp = RhapsodyPreferences.Get();
		
		String apiKey = rp.getGPTApiKey();
		if(apiKey == null)
		{
			return;
		}
		
		
		myAskIssues = new AskIssues(apiKey);
		myApplication = aApplication;
		
	}

	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		JTextComponent tc = getTextComponent(e);
		String selectedText = tc.getSelectedText();
		
		if(myAskIssues==null)
		{
			
			String apiKey = JOptionPane.showInputDialog(null, "Enter GPT3 api Key:");
			RhapsodyPreferences rp = RhapsodyPreferences.Get();
			rp.setGPTApiKey(apiKey);
			myAskIssues = new AskIssues(apiKey);
			
		}
		
		String answere = myAskIssues.syntaxError(selectedText);
		System.out.println("ChatGPT Answere: \n" + answere);
		myApplication.writeToOutputWindow("Log", "\n" + answere + "\n");		
		
	}
}
*/

/*

class AskGPtOptimize extends TextAction
{


	private static final long serialVersionUID = 6148671876928369847L;
	private AskIssues myAskIssues = null;
	private IRPApplication myApplication = null;
	
	public AskGPtOptimize(IRPApplication aApplication) {
		super("Optimize ChatGPT");
		
		RhapsodyPreferences rp = RhapsodyPreferences.Get();
		String apiKey = rp.getGPTApiKey();
		if(apiKey!=null)
		{
			myAskIssues = new AskIssues(apiKey);
		}
		myApplication = aApplication;
		
	}

	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		JTextComponent tc = getTextComponent(e);
		String selectedText = tc.getSelectedText();
		
		if(myAskIssues==null)
		{
			String apiKey = JOptionPane.showInputDialog(null, "Enter GPT3 api Key:");
			RhapsodyPreferences rp = RhapsodyPreferences.Get();
			rp.setGPTApiKey(apiKey);
			myAskIssues = new AskIssues(apiKey);
		}
		
		String answere = myAskIssues.optimize(selectedText);
		System.out.println("ChatGPT Answere: \n" + answere);
		myApplication.writeToOutputWindow("Log", "\n" + answere + "\n");		
		
	}
}

*/




