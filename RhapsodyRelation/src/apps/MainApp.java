package apps;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.management.InstanceAlreadyExistsException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
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
	
	private static MainApp myApp;
	
	private JFrame myFrame;
	private AutocompleteJComboBox myCombobox;
	private IRPClassifier mySelectedClassifier;
	private IRPProject myActiveProject;
	
	
	/*
	* This method is called on invoking an app inside Rhapsody.
	* rhapsody - Instance of an active Rhapsody application 
	* selected - Selected element in Rhapsody
	*/
	
	public void execute(IRPApplication rhapsody, IRPModelElement selected) {
		
		
		/*
		
		String lufSystem = UIManager.getSystemLookAndFeelClassName();
		
		

		try 
		{
			UIManager.setLookAndFeel(lufSystem);
		} 
		catch (Exception e) 
		{	
			e.printStackTrace();
		} 
		
		*/
		

		
		myFrame = new JFrame();
		
		
		
		String imageName = selected.getIconFileName();
		imageName = imageName.replace("\\", "/");
		
		List<BufferedImage> icons = new ArrayList<BufferedImage>();
		File f = new File(imageName);
		try 
		{
			icons = Imaging.getAllBufferedImages(f);
			
		} 
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		Image img = null;
		
		if(icons.size()>0)
		{
			img = icons.get(icons.size()-1);
		}
		
			
	      
	     if(img!=null)
	     {
	    	 myFrame.setIconImage(img);
	     }
      
		
		
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myFrame.setTitle("Add Element to "+selected.getMetaClass()+": "+selected.getName());
       
        myFrame.setSize(650, 120);
        JPanel panel = new JPanel();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
 
       
        
        myActiveProject = rhapsody.activeProject();
        
        if(selected instanceof IRPClassifier == false)
        {
        	return;
        }
 
      
        mySelectedClassifier = (IRPClassifier)selected;
        
        RhapsodyModelElementSearchable rcs = new RhapsodyModelElementSearchable(mySelectedClassifier);
        
        myCombobox = new AutocompleteJComboBox(rcs);
        myCombobox.setSize(600, 100);
        
        //JComboBox wird Panel hinzugefügt
        panel.add(myCombobox);
        
        myFrame.setLayout(new BorderLayout());
        
		JPanel buttonPanel = new JPanel(); 
		
		boolean isWritable = mySelectedClassifier.isReadOnly()==0;
		
		JButton featuresButton = new JButton("Features..."); 
        featuresButton.addActionListener(new ActionListener() { 
      	  public void actionPerformed(ActionEvent e) {    
  		    IRPModelElement selectedElement = getSelectedElement();
  		    if(selectedElement==null)
  		    {
  		    	return;
  		    }
  		    selectedElement.openFeaturesDialog(0);
  		    
  		  } 
  		} );
        buttonPanel.add(featuresButton);
        
        
		
        JButton addDependencyButton = new JButton("Add Dependency"); 
        addDependencyButton.addActionListener(new ActionListener() { 
      	  public void actionPerformed(ActionEvent e) {    
  		    addDependency();
  		  } 
  		} );
        addDependencyButton.setEnabled(isWritable);
        buttonPanel.add(addDependencyButton);
        
        JButton addAssociationButton = new JButton("Add Association");
        
        addAssociationButton.addActionListener(new ActionListener() { 
      	  public void actionPerformed(ActionEvent e) {    
  		    addAssociation();
  		  } 
  		} );
        addAssociationButton.setEnabled(isWritable);
        buttonPanel.add(addAssociationButton);
        
        JButton addAttributeButton = new JButton("Add Attribute");
        addAttributeButton.addActionListener(new ActionListener() { 
        	  public void actionPerformed(ActionEvent e) {    
        		    addAttribute();
        		  } 
        		} );
        addAttributeButton.setEnabled(isWritable);
        buttonPanel.add(addAttributeButton);
        
        JButton addGeneralizationButton = new JButton("Add Generalization");
        addGeneralizationButton.addActionListener(new ActionListener() { 
        	  public void actionPerformed(ActionEvent e) {    
        		    addGeneralization();
        		  } 
        		} );
        addGeneralizationButton.setEnabled(isWritable);
        buttonPanel.add(addGeneralizationButton);
        
        
        myFrame.setLocation(dim.width/2-myFrame.getSize().width/2, dim.height/2-myFrame.getSize().height/2);
 
        myFrame.add(panel,BorderLayout.CENTER);
        myFrame.add(buttonPanel,BorderLayout.SOUTH);
        myFrame.setVisible(true);
	}
	
	
    /*
     *  Debug app by launching it as "Java Application" is Eclipse.
	 *  Note: Select an element in Rhapsody in order to simulate launching app on a selected element in the browser.
     */	
	public static void main(String[] args) 
	{
		
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
			//actualApp.writeToOutputWindow("log", "ConnectiongString: " + aConnectionstring + "\n");
		}
		else
		{
			actualApp.writeToOutputWindow("log", "ConnectiongString was null\n");
		}
		
		//actualApp.writeToOutputWindow("log", "start...\n");
		
		aApp.execute(actualApp, selectedElement);
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


	private IRPModelElement getSelectedElement() {
		Object selectedItem = myCombobox.getSelectedItem();

		System.out.println(selectedItem.toString());
		
		if(selectedItem instanceof RhapsodyModelElementItem == false)
		{
			System.out.println("Selected item has wrong type!");
			return null;
		}
		
		RhapsodyModelElementItem selectedElementItem = (RhapsodyModelElementItem)selectedItem;			
		
		 //check if dependency already there...
		
		IRPModelElement selectedElement = selectedElementItem.getModelElement();
		return selectedElement;
	}


	private void addDependency() 
	{
		IRPModelElement selectedElement = getSelectedElement();
		
		if(selectedElement==null)
		{
			return;
		}
		
		List<IRPDependency> dependencies = mySelectedClassifier.getDependencies().toList();
		
		for(IRPDependency dependency:dependencies)
		{
			if(dependency.getDependsOn().equals(selectedElement))
			{
				System.out.println("Dependency is already there");
				return;
			}
		}
		
		IRPDependency newDependency = mySelectedClassifier.addDependencyTo(selectedElement);
		List<IRPStereotype> stereoTypes = myActiveProject.getAllStereotypes().toList();
		for(IRPStereotype stereoType:stereoTypes)
		{
			if(stereoType.getName().equals("Usage"))
			{
				newDependency.addSpecificStereotype(stereoType);
				break;
			}
		}
		newDependency.openFeaturesDialog(0);

	}
	
	private void addAssociation()
	{
		
		IRPModelElement selectedElement = getSelectedElement();
		
		if(selectedElement==null)
		{
			return;
		}
		
		//set name of the new relation
		String baseAssociationName = "its"+selectedElement.getName();
		String associationName = baseAssociationName;
		
		int count = 0;
		while(mySelectedClassifier.findRelation(associationName)!=null)
		{
			associationName = baseAssociationName+"_"+count;
			count++;
		}
		
		if(selectedElement instanceof IRPClassifier == false)
		{
			System.out.println("Associations only with Classifiers");
			return;
		}
		
		IRPClassifier selectedClassifier = (IRPClassifier) selectedElement;
		
		IRPRelation newRelation = mySelectedClassifier.addUnidirectionalRelationTo(selectedClassifier, associationName, "Association", "1", "");
		
		newRelation.openFeaturesDialog(0);
		
	}
	
	private void addAttribute()
	{
		IRPModelElement selectedElement = getSelectedElement();
		if(selectedElement==null)
		{
			return;
		}
		
		String baseAttributeName = "my"+selectedElement.getName();
		String attributeName = baseAttributeName;
		int count = 0;
		while(mySelectedClassifier.findAttribute(attributeName)!=null)
		{
			attributeName = baseAttributeName+"_"+count;
			count++;
		}
		
		if(selectedElement instanceof IRPClassifier == false)
		{
			System.out.println("Associations only with Classifiers");
			return;
		}
		
		IRPClassifier selectedClassifier = (IRPClassifier) selectedElement;

		IRPAttribute newAttribute = mySelectedClassifier.addAttribute(attributeName);
		newAttribute.setType(selectedClassifier);
		newAttribute.openFeaturesDialog(0);
		
	}
	
	private void addGeneralization()
	{
		IRPModelElement selectedElement = getSelectedElement();
		if(selectedElement==null)
		{
			return;
		}
		if(selectedElement instanceof IRPClassifier == false)
		{
			System.out.println("Associations only with Classifiers");
			return;
		}
		IRPClassifier selectedClassifier = (IRPClassifier) selectedElement;
		
		mySelectedClassifier.addGeneralization(selectedClassifier);
		IRPGeneralization newGeneralization = mySelectedClassifier.findGeneralization(selectedClassifier.getName());
		newGeneralization.openFeaturesDialog(0);
		
		

	}
}
