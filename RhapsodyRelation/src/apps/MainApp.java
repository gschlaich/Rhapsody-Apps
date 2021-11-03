package apps;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;

import javax.management.InstanceAlreadyExistsException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.ibm.rhapsody.apps.*;
import com.telelogic.rhapsody.core.*;


public class MainApp extends App {
	
	/*
	* This method is called on invoking an app inside Rhapsody.
	* rhapsody - Instance of an active Rhapsody application 
	* selected - Selected element in Rhapsody
	*/
	public void execute(IRPApplication rhapsody, IRPModelElement selected) {
		JFrame meinJFrame = new JFrame();
        meinJFrame.setTitle("JComboBox Beispiel");
        meinJFrame.setSize(250, 250);
        JPanel panel = new JPanel();
 
        JLabel frage = new JLabel("Aus welchem Bundesland kommst du?");
        panel.add(frage);
 
        // Array für unsere JComboBox
        String comboBoxListe[] = {"Baden-Württemberg", "Bayern",
            "Berlin", "Brandenburg", "Bremen",
            "Hamburg", "Hessen", "Mecklenburg-Vorpommern",
            "Niedersachsen", "Nordrhein-Westfalen", "Rheinland-Pfalz",
            "Saarland", "Sachsen", "Sachsen-Anhalt",
            "Schleswig-Holstein", "Thüringen"};
 
        
        List<String> stringList = Arrays.asList(comboBoxListe);
        
        StringSearchable str = new StringSearchable(stringList);
        if(selected instanceof IRPClassifier ==false)
        {
        	return;
        }
        IRPClassifier selectedClassifier = (IRPClassifier)selected;
        
        RhapsodyClassSearchable rcs = new RhapsodyClassSearchable(selectedClassifier);
        
        AutocompleteJComboBox combobox = new AutocompleteJComboBox(rcs);
        //JComboBox mit Bundesländer-Einträgen wird erstellt
        //JComboBox bundeslandAuswahl = new JComboBox(comboBoxListe);
 
        //JComboBox wird Panel hinzugefügt
        panel.add(combobox);
 
        meinJFrame.add(panel);
        meinJFrame.setVisible(true);
	}
	
	private static String[] items   = new String[] { "Monday","Monnday","Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" };
	
    /*
     *  Debug app by launching it as "Java Application" is Eclipse.
	 *  Note: Select an element in Rhapsody in order to simulate launching app on a selected element in the browser.
     */	
	public static void main(String[] args) 
	{
		/*
		MainApp app = new MainApp();
		app.invokeFromMain();
		*/
		
		Display display = new Display();
	    Shell shell = new Shell(display);
	    shell.setText("StackOverflow");
	    shell.setLayout(new FillLayout());

	    Combo combo = new Combo(shell, SWT.BORDER);
	    for (int i = 0; i < items.length; i++)
	    {
	        combo.add(items[i]);
	    }

	    addAutoCompleteFeature(combo);

	    shell.pack();
	    shell.open();
	    while (!shell.isDisposed())
	    {
	        if (!display.readAndDispatch())
	        {
	            display.sleep();
	        }
	    }
	    display.dispose();
	}


	public static void addAutoCompleteFeature(Combo combo)
	{
	    // Add a key listener
	    
		combo.addKeyListener(new KeyListener() 
		{
			
			@Override
			public void keyReleased(org.eclipse.swt.events.KeyEvent arg0) {
				Combo cmb = ((Combo) arg0.getSource());
	            setClosestMatch(cmb);
				
			}
			
			@Override
			public void keyPressed(org.eclipse.swt.events.KeyEvent arg0) {
				if (arg0.keyCode == SWT.BS)
	            {
	                Combo cmb = ((Combo) arg0.getSource());
	                Point pt = cmb.getSelection();
	                cmb.setSelection(new Point(Math.max(0, pt.x - 1), pt.y));
	            }
				
			}
			

			private void setClosestMatch(Combo combo)
	        {
	            String str = combo.getText();
	            String[] cItems = combo.getItems();
	            // Find Item in Combo Items. If full match returns index
	            int index = -1;
	            for (int i = 0; i < cItems.length; i++)
	            {
	                if (cItems[i].toLowerCase().startsWith(str.toLowerCase()))
	                {
	                    index = i;
	                    break;
	                }
	            }

	            if (index != -1)
	            {
	                Point pt = combo.getSelection();
	                combo.select(index);
	                combo.setText(cItems[index]);
	                combo.setSelection(new Point(pt.x, cItems[index].length()));
	            }
	            else
	            {
	                combo.setText("");
	            }
	        }
	    
			
			
			
		});
	    
	        

	       

	        
	
		
		
		
	}		
}
