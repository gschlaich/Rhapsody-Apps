package apps;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

//import org.apache.commons.imaging.Imaging;

import com.ibm.rhapsody.apps.*;
import com.telelogic.rhapsody.core.*;

import de.schlaich.gunnar.rhapsody.completionprovider.ClassifierCompletionProvider;
import de.schlaich.gunnar.rhapsody.completionprovider.NamespaceCompletionProvider;
//import de.schlaich.gunnar.rhapsody.ghs.MultiPlugin;
import de.schlaich.gunnar.rhapsody.operationeditor.LoadInIDE;
import de.schlaich.gunnar.rhapsody.operationeditor.OperationEditorWindow;
import de.schlaich.gunnar.rhapsody.utilities.HistoryControl;
import de.schlaich.gunnar.rhapsody.utilities.HistoryElement;
import de.schlaich.gunnar.rhapsody.utilities.RhapsodyHelper;
import de.schlaich.gunnar.rhapsody.utilities.RhapsodyPreferences;

public class MainApp extends App implements HistoryControl
{

	private Listener myListener = null;
	private JFrame myFrame = null;
	private JButton myButtonStatistic = null;
	private JButton myButtonConfig = null;
	private JButton myHistoryForward = null;
	private JButton myHistoryBack = null;
	private JComboBox<HistoryElement> myHistoryComboBox = null;
	private List<HistoryElement> myHistoryList = null;
	private int myHistoryListPos = 0;
	private String myConnectingString = null;

	public void execute(IRPApplication rhapsody, IRPModelElement selected)
	{
		execute(rhapsody, selected, false);
	}

	@Override
	protected void println(String text)
	{
		System.out.println(text);
		super.println(text);
	}

	/*
	 * This method is called on invoking an app inside Rhapsody. rhapsody - Instance
	 * of an active Rhapsody application selected - Selected element in Rhapsody
	 */
	@SuppressWarnings("unchecked")
	public void execute(IRPApplication rhapsody, IRPModelElement selected, boolean onStartup)
	{

		RhapsodyPreferences prefs = RhapsodyPreferences.Get(true);

		
		rhapsody.writeToOutputWindow("Log", "Build date: " + getBuildDate() + "\n");
		
		LoadInIDE loadInIde = LoadInIDE.Instance(rhapsody);

		if (myConnectingString == null)
		{
			myConnectingString = "develop";
			if (prefs.isStarted(myConnectingString))
			{
				// editorstarter is already running...
				// rhapsody.writeToOutputWindow("Log", "EditorStarter is already running");
				println("EditorStarter is already running");
				return;
			}

		}

		if (onStartup)
		{
			// check if user wants to work with the editor..

			if (prefs.getEditorOnStartup() == false)
			{
				return;
			}

		}

		if ((selected instanceof IRPOperation) == false)
		{
			if (selected instanceof IRPTransition == false)
			{
				if (selected instanceof IRPAction == false)
				{

					if (prefs.getEditorOnStartup() == false)
					{
						System.out.println("Selected no editor item - exit");
						return;
					}
				}
			}
		}

		rhapsody.writeToOutputWindow("Log", "EditorStarter starts RhapsodyOperationEditor on double click\n");

		myListener = new Listener(rhapsody, this);

		myFrame = new JFrame();

		HistoryElement.RegisterControl(this);

		IRPProject project = rhapsody.activeProject();

		if (project == null)
		{
			return;
		}

		String name = project.getName();
		String imageName = project.getIconFileName();

		IRPModelElement op = project.findAllByName("describe", "Operation");
		if (op != null)
		{
			imageName = op.getIconFileName();
		}

		imageName = imageName.replace("\\", "/");

		ImageIcon icon = new ImageIcon(imageName);

		myButtonStatistic = new JButton("Get Infos");
		myButtonConfig = new JButton("Set Config");

		myHistoryComboBox = new JComboBox<HistoryElement>();

		myHistoryComboBox.addItemListener(new ItemListener()
		{

			@Override
			public void itemStateChanged(ItemEvent e)
			{
				if ((e.getSource() == myHistoryComboBox) && (e.getStateChange() == ItemEvent.SELECTED))
				{
					HistoryElement selected = (HistoryElement) e.getItem();

					if (selected == null)
					{
						return;
					}
					try
					{
						System.out.println("itemStateChanged: " + selected.toString());
						OperationEditorWindow.Run(rhapsody, selected.getElement(), false);
					}
					catch (Exception exx)
					{
						printToRhapsody(exx.getMessage());
						exx.printStackTrace();
					}

				}

			}
		});

		// Add action listener to button

		myButtonStatistic.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{

				List<ClassifierCompletionProvider> publicProviders = ClassifierCompletionProvider.GetPublicProviders();
				for (ClassifierCompletionProvider provider : publicProviders)
				{
					rhapsody.writeToOutputWindow("Log", "Public Provider Class: " + provider.getClassifier().getName()
							+ " Elements: " + provider.getCompletions().size() + "\n");
				}
				List<ClassifierCompletionProvider> privateProviders = ClassifierCompletionProvider
						.GetPrivateProviders();
				for (ClassifierCompletionProvider provider : privateProviders)
				{
					rhapsody.writeToOutputWindow("Log", "Private Provider Class: " + provider.getClassifier().getName()
							+ " Elements: " + provider.getCompletions().size() + "\n");
				}
				List<NamespaceCompletionProvider> namespaceProviders = NamespaceCompletionProvider
						.GetNameSpaceCompletionProviders();
				for (NamespaceCompletionProvider provider : namespaceProviders)
				{
					rhapsody.writeToOutputWindow("Log", "Namespace Provider: " + provider.toString() + " Elements: "
							+ provider.getCompletions().size() + "\n");
				}

			}
		});

		myButtonConfig.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{

				new ConfigWindow();

			}
		});

		myHistoryList = new ArrayList<HistoryElement>();

		myHistoryBack = new JButton("<");
		myHistoryBack.setEnabled(false);

		myHistoryBack.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{

				if (myHistoryListPos > 0)
				{
					myHistoryForward.setEnabled(true);
					myHistoryListPos--;
					HistoryElement he = myHistoryList.get(myHistoryListPos);
					if (he == null)
					{
						// printToRhapsody("Back: NULL! " + myHistoryListPos );
						return;
					}

					// printToRhapsody("Back: Run " + he.getElement().getName() + " " +
					// myHistoryListPos );
					OperationEditorWindow.Run(rhapsody, he.getElement(), false);
					if (myHistoryListPos <= 0)
					{
						myHistoryBack.setEnabled(false);
					}

				}

			}
		});

		myHistoryForward = new JButton(">");
		myHistoryForward.setEnabled(false);

		myHistoryForward.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (myHistoryListPos < (myHistoryList.size() - 1))
				{
					myHistoryListPos++;
					myHistoryBack.setEnabled(true);
					HistoryElement he = myHistoryList.get(myHistoryListPos);
					if (he == null)
					{
						printToRhapsody("Forward: NULL! " + myHistoryListPos);
						return;
					}
					// printToRhapsody("Forward: Run " + he.getElement().getName() + " " +
					// myHistoryListPos );
					OperationEditorWindow.Run(rhapsody, he.getElement(), false);
					if (myHistoryListPos >= (myHistoryList.size() - 1))
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

		myFrame.addWindowListener(new WindowListener()
		{

			@Override
			public void windowOpened(WindowEvent e)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void windowIconified(WindowEvent e)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeiconified(WindowEvent e)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeactivated(WindowEvent e)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void windowClosing(WindowEvent e)
			{
				// TODO Auto-generated method stub
				prefs.removeStarter();

			}

			@Override
			public void windowClosed(WindowEvent e)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void windowActivated(WindowEvent e)
			{
				// TODO Auto-generated method stub

			}
		});

		myFrame.setIconImage(icon.getImage());

		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myFrame.setTitle("Operations of " + name);

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

		myFrame.setSize(dim.width, 75);

		myFrame.setVisible(true);

		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (InstantiationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (UnsupportedLookAndFeelException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// set local guid set

		myListener.connect(rhapsody);

		if (selected == null)
		{
			return;
		}

		rhapsody.writeToOutputWindow("log", "Start Editor on " + selected.getName() + "\n");
		if ((selected instanceof IRPOperation) == false)
		{
			if (selected instanceof IRPTransition == false)
			{
				if (selected instanceof IRPAction == false)
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

			for (StackTraceElement ste : stes)
			{
				rhapsody.writeToOutputWindow("log", ste.toString() + "\n");
			}

			rhapsody.writeToOutputWindow("log", ex.toString() + "\n");

		}

		/*
		 * 
		 * IRPPlugInWindow piw = rhapsody.getPlugInWindow(1, 1, 1);
		 * 
		 * piw.setDocking(1); piw.setTitle("Starter"); piw.showWindow(1);
		 * 
		 * long handle = piw.getWindowHandle();
		 * 
		 */

	}

	private String getBuildDate()
	{
		try
		{
			String jarPath = MainApp.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			String decodedPath = URLDecoder.decode(jarPath, "UTF-8");
			File jarFile = new File(decodedPath);
			if (jarFile.exists())
			{
				long lastModified = jarFile.lastModified();
				Date date = new Date(lastModified);
				return date.toString();
			}
			else
			{
				return "JAR-File not found";
			}
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
			return "Error while getting build date";
		}
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

		if ((myHistoryListPos < 0) || (myHistoryList.size() <= myHistoryListPos)
				|| (myHistoryList.get(myHistoryListPos).getElement().equals(nhe.getElement()) == false))
		{
			System.out.println("Add to HistoryList: " + aElement.getName());
			myHistoryList.add(nhe);
			myHistoryListPos = myHistoryList.size() - 1;
			if (myHistoryListPos > 0)
			{
				myHistoryBack.setEnabled(true);
			}
			myHistoryForward.setEnabled(false);

		}

		boolean inList = false;
		int itemCount = myHistoryComboBox.getItemCount();
		for (int i = 0; i < itemCount; i++)
		{
			HistoryElement he = myHistoryComboBox.getItemAt(i);
			if (he == null)
			{
				continue;
			}
			if (he.getElement().equals(aElement))
			{
				inList = true;
				// myHistoryComboBox.removeItemAt(i);
			}
		}
		if (inList == false)
		{
			myHistoryComboBox.addItem(nhe);
		}

	}

	/*
	 * Debug app by launching it as "Java Application" is Eclipse. Note: Select an
	 * element in Rhapsody in order to simulate launching app on a selected element
	 * in the browser.
	 */
	public static void main(String[] args)
	{

		MainApp app = new MainApp();
		String connectionstring = null;
		boolean onStartup = false;

		if (args.length >= 1)
		{
			connectionstring = args[0];
		}

		if (args.length >= 2)
		{
			if (args[1].equals("true"))
			{
				onStartup = true;
			}
		}

		executeApp(app, connectionstring, onStartup);

	}

	public static void executeApp(MainApp aApp, String aConnectionstring, boolean onStartup)
	{
		IRPApplication actualApp = null;

		if (aConnectionstring != null)
		{
			try
			{
				actualApp = RhapsodyAppServer.getActiveRhapsodyApplicationByID(aConnectionstring);
			}
			catch (Exception e)
			{
				System.out.println("connectionstring " + aConnectionstring + " is not an active rhapsody application ");
			}
		}
		else
		{
			System.out.println("no connectionstring set");
		}

		if (actualApp == null)
		{

			aApp.invokeFromMain();
			return;
		}

		IRPModelElement selectedElement = actualApp.getSelectedElement();

		if (aConnectionstring != null)
		{
			actualApp.writeToOutputWindow("log", "ConnectiongString: " + aConnectionstring + "\n");
			aApp.myConnectingString = aConnectionstring;

		}
		else
		{
			actualApp.writeToOutputWindow("log", "ConnectiongString was null\n");
		}

		actualApp.writeToOutputWindow("log", "start...\n");

		aApp.execute(actualApp, selectedElement, onStartup);
	}

}
