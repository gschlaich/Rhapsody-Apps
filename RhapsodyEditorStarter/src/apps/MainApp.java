package apps;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.imaging.Imaging;

import com.ibm.rhapsody.apps.*;
import com.telelogic.rhapsody.core.*;

import de.schlaich.gunnar.rhapsody.completionprovider.ClassifierCompletionProvider;
import de.schlaich.gunnar.rhapsody.completionprovider.NamespaceCompletionProvider;
import de.schlaich.gunnar.rhapsody.operationeditor.OperationEditorWindow;
import de.schlaich.gunnar.rhapsody.utilities.HistoryControl;
import de.schlaich.gunnar.rhapsody.utilities.HistoryElement;
import de.schlaich.gunnar.rhapsody.utilities.RhapsodyHelper;
import de.schlaich.gunnar.rhapsody.utilities.RhapsodyPreferences;


public class MainApp extends App  implements HistoryControl{
	
	private Listener myListener = null;
	private JFrame myFrame = null;
	private JButton myButtonStatistic = null;
	private JButton myButtonConfig = null;
	private JButton myHistoryForward = null;
	private JButton myHistoryBack = null;
	private JComboBox<HistoryElement> myHistoryComboBox = null;
	private List<HistoryElement> myHistoryList = null;
	private int myHistoryListPos = 0;
	
	
	
	
	/*
	* This method is called on invoking an app inside Rhapsody.
	* rhapsody - Instance of an active Rhapsody application 
	* selected - Selected element in Rhapsody
	*/
@SuppressWarnings("unchecked")
public void execute(IRPApplication rhapsody, IRPModelElement selected) {
		
		
		
	
		rhapsody.writeToOutputWindow("Log", "EditorStarter starts RhapsodyOperationEditor on double click\n");
		
		myListener = new Listener(rhapsody,this);
		
		myFrame = new JFrame();
		
		HistoryElement.RegisterControl(this);
		
		IRPProject project = rhapsody.activeProject();
		
		if(project==null)
		{
			return;
		}
		
		String name = project.getName();
		String imageName = project.getIconFileName();
		
		IRPModelElement op = project.findAllByName("describe", "Operation");
		if(op!=null)
		{
			imageName = op.getIconFileName();
		}
		
		
		imageName = imageName.replace("\\", "/");

		ImageIcon icon = new ImageIcon(imageName);
		
		
		myButtonStatistic = new JButton("Get Infos");
		myButtonConfig = new JButton("Set Config");
        
		myHistoryComboBox = new JComboBox<HistoryElement>();
		
		
		myHistoryComboBox.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if((e.getSource()==myHistoryComboBox)&&(e.getStateChange() == ItemEvent.SELECTED))
				{
					HistoryElement selected = (HistoryElement) e.getItem();
					
					if(selected==null)
					{
						return;
					}
					try
					{
						System.out.println("itemStateChanged: " + selected.toString() );
						OperationEditorWindow.Run(rhapsody, selected.getElement(), false);
					}
					catch(Exception exx)
					{
						printToRhapsody(exx.getMessage());
						exx.printStackTrace();
					}
					
				}
				
				
			}
		});
			
			

        // Add action listener to button
      
        myButtonStatistic.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	
            	List<ClassifierCompletionProvider> publicProviders =  ClassifierCompletionProvider.GetPublicProviders();
            	for(ClassifierCompletionProvider provider:publicProviders)
            	{
            		rhapsody.writeToOutputWindow("Log", "Public Provider Class: " + provider.getClassifier().getName() + " Elements: " + provider.getCompletions().size() + "\n");
            	}
            	List<ClassifierCompletionProvider> privateProviders =  ClassifierCompletionProvider.GetPrivateProviders();
            	for(ClassifierCompletionProvider provider:privateProviders)
            	{
            		rhapsody.writeToOutputWindow("Log", "Private Provider Class: " + provider.getClassifier().getName() + " Elements: " + provider.getCompletions().size()  + "\n");
            	}
            	List<NamespaceCompletionProvider> namespaceProviders = NamespaceCompletionProvider.GetNameSpaceCompletionProviders();
            	for(NamespaceCompletionProvider provider:namespaceProviders)
            	{
            		rhapsody.writeToOutputWindow("Log", "Namespace Provider: " + provider.toString() + " Elements: " + provider.getCompletions().size()  + "\n");
            	}
                
              
            }
        });
        
        myButtonConfig.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				
					new ConfigWindow();
				
				
			}
		});
        
        myHistoryList = new ArrayList<HistoryElement>();
        
       
        
        myHistoryBack = new JButton("<");
        myHistoryBack.setEnabled(false);
        
        myHistoryBack.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(myHistoryListPos>0)
				{
					myHistoryForward.setEnabled(true);
					myHistoryListPos--;
					HistoryElement he =  myHistoryList.get(myHistoryListPos);
					if(he==null)
					{
						//printToRhapsody("Back: NULL! " + myHistoryListPos  );
						return;
					}
					
					//printToRhapsody("Back: Run " + he.getElement().getName() + " " + myHistoryListPos  );
					OperationEditorWindow.Run(rhapsody, he.getElement(), false);
					if(myHistoryListPos<=0)
					{
						myHistoryBack.setEnabled(false);
					}
	
				}
				
				
			}
		});
        
        myHistoryForward = new JButton(">");
        myHistoryForward.setEnabled(false);
        
        myHistoryForward.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(myHistoryListPos<(myHistoryList.size()-1))
				{
					myHistoryListPos++;
					myHistoryBack.setEnabled(true);
					HistoryElement he = myHistoryList.get(myHistoryListPos);
					if(he==null)
					{
						printToRhapsody("Forward: NULL! " + myHistoryListPos  );
						return;
					}
					//printToRhapsody("Forward: Run " + he.getElement().getName() + " " + myHistoryListPos  );
					OperationEditorWindow.Run(rhapsody, he.getElement(), false);
					if(myHistoryListPos>=(myHistoryList.size()-1))
					{
						myHistoryForward.setEnabled(false);
					}
				}
				
				
			}
		});
        
        

        // Add components to the window
        JPanel panel = new JPanel();
        
        panel.add(myHistoryBack);
        panel.add(myHistoryForward);
        panel.add(myHistoryComboBox);
        panel.add(myButtonStatistic);
        panel.add(myButtonConfig);
        myFrame.add(panel);
		
      
	    myFrame.setIconImage(icon.getImage());
		
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myFrame.setTitle("Operations of "+name);
        
        myFrame.setSize(900, 75);
        
        myFrame.setVisible(true);
        
        try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
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
        
        
        //set local guid set
        RhapsodyPreferences prefs = RhapsodyPreferences.Get(true);

       
        myListener.connect(rhapsody);
        
        rhapsody.writeToOutputWindow("log", "Start Editor on " + selected.getName()+"\n");
		if((selected instanceof IRPOperation) == false)
		{
			if(selected instanceof IRPTransition == false)
			{
				if(selected instanceof IRPAction ==false)
				{
					return; 
				}
			}
		}
		
		
	
		try 
		{
			OperationEditorWindow.Run(rhapsody, selected, false);
			
		}
		catch (Exception ex)
		{
			
			
			ex.printStackTrace();
			
			StackTraceElement[] stes = ex.getStackTrace();
			
		
			for(StackTraceElement ste:stes)
			{
				rhapsody.writeToOutputWindow("log", ste.toString()+"\n");
			}
			
			
			rhapsody.writeToOutputWindow("log", ex.toString()+"\n");

		}
		
		IRPPlugInWindow piw =  rhapsody.getPlugInWindow(1, 1, 1);
		
		piw.setDocking(1);
		piw.setTitle("Starter");
		piw.showWindow(1);
		
		long handle = piw.getWindowHandle();
		
		
				
		
	}

        
	

	public void printToRhapsody(String aText)
	{
		println(aText);
		System.out.println(aText);
	}
	
	
	@Override
	public void addToHistory(IRPModelElement aElement)
	{
		HistoryElement nhe = new HistoryElement(aElement);

		
		
		if(
				(myHistoryListPos<0) ||
				(myHistoryList.size()<=myHistoryListPos)||
				(myHistoryList.get(myHistoryListPos).getElement().equals(nhe.getElement())==false))
		{
			System.out.println("Add to HistoryList: " + aElement.getName());
			myHistoryList.add(nhe);
			myHistoryListPos = myHistoryList.size()-1;
			if(myHistoryListPos>0)
			{
				myHistoryBack.setEnabled(true);
			}
			myHistoryForward.setEnabled(false);
		
		}
			
		boolean inList = false;
		int itemCount = myHistoryComboBox.getItemCount();
		for(int i=0;i<itemCount;i++)
		{
			HistoryElement he = myHistoryComboBox.getItemAt(i);
			if(he==null)
			{
				continue;
			}
			if(he.getElement().equals(aElement))
			{
				inList = true;
				//myHistoryComboBox.removeItemAt(i);
			}
		}
		if(inList==false)
		{
			myHistoryComboBox.addItem(nhe);
		}

	}
	
	

	

	
    /*
     *  Debug app by launching it as "Java Application" is Eclipse.
	 *  Note: Select an element in Rhapsody in order to simulate launching app on a selected element in the browser.
     */	
	public static void main(String[] args) {
		
		MainApp app = new MainApp();
		String connectionstring = null;
		
		if (args.length >= 1) 
		{
			connectionstring = args[0];
		}
		executeApp(app, connectionstring);
		
	}	
	
	public static void executeApp(App aApp, String aConnectionstring)
	{
		IRPApplication actualApp = null;
		
		if(aConnectionstring!=null)
		{
			try
			{
				actualApp = RhapsodyAppServer.getActiveRhapsodyApplicationByID(aConnectionstring);
			}
			catch(Exception e)
			{
				System.out.println("connectionstring "+ aConnectionstring + " is not an active rhapsody application ");
			}
		}
		else
		{
			System.out.println("no connectionstring set");
		}
		
		if(actualApp==null)
		{
        
			aApp.invokeFromMain();
			return;
		}
		
		IRPModelElement selectedElement = actualApp.getSelectedElement();
		
		
		if(aConnectionstring!=null)
		{
			actualApp.writeToOutputWindow("log", "ConnectiongString: " + aConnectionstring + "\n");
		}
		else
		{
			actualApp.writeToOutputWindow("log", "ConnectiongString was null\n");
		}
		
		actualApp.writeToOutputWindow("log", "start...\n");
		
		aApp.execute(actualApp, selectedElement);
	}




	
}

