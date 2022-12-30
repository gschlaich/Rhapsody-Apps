package apps;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
	private IRPClassifier mySelectedClassifier = null;
	private IRPOperation mySelectedOperation = null;
	private IRPPackage mySelectedPackage = null;
	private IRPUnit mySelectedUnit = null;
	private IRPProject myActiveProject;
	private RPApplicationListener myRelationListener;
	private RhapsodyModelElementSearchable myRhapsodyModelSearchable;
	private JButton myFeaturesButton = null;
	private JButton myAddDependencyButton = null;
	private JButton myAddAssociationButton = null;
	private JButton myAddGeneralizationButton = null;
	private JButton myAddArgumentButton = null;
	private JButton myAddAttributeButton = null;
	private JButton myChangeReturnButton = null;
	private boolean myIsWritable = false; 
	
	
	
	/*
	* This method is called on invoking an app inside Rhapsody.
	* rhapsody - Instance of an active Rhapsody application 
	* selected - Selected element in Rhapsody
	*/
	
	public void execute(IRPApplication rhapsody, IRPModelElement selected) {
		
		
		
		/*
		 * Text search does not work correct... with windows
		 * 
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
        
        if(selected instanceof IRPOperation)
        {
        	mySelectedOperation = (IRPOperation)selected;
        }
        
        else if(selected instanceof IRPClassifier)
        {
        	mySelectedClassifier = (IRPClassifier)selected;
        }
        
        else if(selected instanceof IRPPackage)
        {
        	mySelectedPackage = (IRPPackage)selected;
        }
        else 
        {
        	return;
        }
        
        mySelectedUnit = (IRPUnit)selected;
 
      
        
        
     
        
        myRhapsodyModelSearchable = new RhapsodyModelElementSearchable(mySelectedUnit);
        
        myRelationListener = new RelationListener(myFrame, mySelectedUnit ,myRhapsodyModelSearchable);
     	myRelationListener.connect(rhapsody);
       
        
        myCombobox = new AutocompleteJComboBox(myRhapsodyModelSearchable);
        myCombobox.setSize(600, 100);
        
        myCombobox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(myCombobox.getSelectedItem()==null)
				{
					myFeaturesButton.setEnabled(false);
					myAddDependencyButton.setEnabled(false);
					if(myAddAssociationButton!=null)
					{
						myAddAssociationButton.setEnabled(false);
					}
					if(myAddGeneralizationButton!=null)
					{
						myAddGeneralizationButton.setEnabled(false);
					}
					if(myAddArgumentButton!=null)
					{
						myAddArgumentButton.setEnabled(false);
					}
					if(myAddAttributeButton!=null)
					{
						myAddAttributeButton.setEnabled(false);
					}
					if(myChangeReturnButton!=null)
					{
						myChangeReturnButton.setEnabled(false);
					}
					return;
				}
				
				
				
				Collection<RhapsodyModelElementItem> c =  myRhapsodyModelSearchable.search(myCombobox.getSelectedItem().toString());
				if(c.size()==1)
				{
					RhapsodyModelElementItem item = (RhapsodyModelElementItem)c.toArray()[0];
					myFeaturesButton.setEnabled(true);
					myAddDependencyButton.setEnabled(myIsWritable);
					if(myAddAssociationButton!=null)
					{
						if(item.getModelElement() instanceof IRPClassifier)
						{
							myAddAssociationButton.setEnabled(myIsWritable);
						}
						
					}
					if(myAddGeneralizationButton!=null)
					{
						if(item.getModelElement() instanceof IRPClass)
						{
							myAddGeneralizationButton.setEnabled(myIsWritable);
						}
					}
					if(myAddArgumentButton!=null)
					{
						if(item.getModelElement() instanceof IRPClassifier)
						{
							myAddArgumentButton.setEnabled(myIsWritable);
						}
					}
					if(myChangeReturnButton!=null)
					{
						if(item.getModelElement() instanceof IRPClassifier)
						{
							myChangeReturnButton.setEnabled(myIsWritable);
						}
					}
					if(myAddAttributeButton!=null)
					{
						if(item.getModelElement() instanceof IRPClassifier)
						{
							myAddAttributeButton.setEnabled(myIsWritable);
						}
					}
					
				}
				else
				{
					myFeaturesButton.setEnabled(false);
					myAddDependencyButton.setEnabled(false);
					if(myAddAssociationButton!=null)
					{
						myAddAssociationButton.setEnabled(false);
					}
					if(myAddGeneralizationButton!=null)
					{
						myAddGeneralizationButton.setEnabled(false);
					}
					if(myAddArgumentButton!=null)
					{
						myAddArgumentButton.setEnabled(false);
					}
					if(myChangeReturnButton!=null)
					{
						myChangeReturnButton.setEnabled(false);
					}
					if(myAddAttributeButton!=null)
					{
						myAddAttributeButton.setEnabled(false);
					}
				}
				
				
			}
		});
        
        
        panel.add(myCombobox);
        
        myFrame.setLayout(new BorderLayout());
        
        
        myFrame.addMouseListener(new MouseListener() {
        	

			@Override
			public void mouseClicked(MouseEvent e) {
				
				if(e.getButton()>1)
				{
					mySelectedClassifier.locateInBrowser();
					
					if(myRhapsodyModelSearchable!=null)
					{
						myRhapsodyModelSearchable.clearRelationList();
						myRhapsodyModelSearchable.createRelationList();
					}
					
				}
				
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
        });
        
        
        
       
        
		JPanel buttonPanel = new JPanel(); 
		
		
		myIsWritable = mySelectedUnit.isReadOnly()==0;
		
		myFeaturesButton = new JButton("Features..."); 
		myFeaturesButton.setEnabled(false);
		myFeaturesButton.addActionListener(new ActionListener() { 
      	  public void actionPerformed(ActionEvent e) {    
  		    IRPModelElement selectedElement = getSelectedElement();
  		    if(selectedElement==null)
  		    {
  		    	return;
  		    }
  		    selectedElement.openFeaturesDialog(0);
  		    
  		  } 
  		} );
        buttonPanel.add(myFeaturesButton);
        
        
		
        myAddDependencyButton = new JButton("Add Dependency"); 
        myAddDependencyButton.setEnabled(false);
        myAddDependencyButton.addActionListener(new ActionListener() { 
      	  public void actionPerformed(ActionEvent e) {    
  		    addDependency();
  		  } 
  		} );
        
        buttonPanel.add(myAddDependencyButton);
        
        if(mySelectedClassifier!=null)
        {
        	myAddAssociationButton = new JButton("Add Association");
        	myAddAssociationButton.setEnabled(false);
        	myAddAssociationButton.addActionListener(new ActionListener() { 
        		public void actionPerformed(ActionEvent e) {    
        			addAssociation();
        		} 
        	} );
        
        	
        	buttonPanel.add(myAddAssociationButton);
       
        
	        myAddAttributeButton = new JButton("Add Attribute");
	        myAddAttributeButton.setEnabled(false);
	        myAddAttributeButton.addActionListener(new ActionListener() { 
	        	  public void actionPerformed(ActionEvent e) {    
	        		    addAttribute();
	        		  } 
	        		} );
	       
	        buttonPanel.add(myAddAttributeButton);
	        
	        if(mySelectedClassifier instanceof IRPClass)
	        {
	        
	        	myAddGeneralizationButton = new JButton("Add Generalization");
	        	myAddGeneralizationButton.setEnabled(false);
	        	myAddGeneralizationButton.addActionListener(new ActionListener() { 
	        	  public void actionPerformed(ActionEvent e) {    
	        		    addGeneralization();
	        		  } 
	        		} );
	        	
	        	buttonPanel.add(myAddGeneralizationButton);
	        }
        }
        else if(mySelectedOperation!=null)
        {
        	myAddArgumentButton = new JButton("Add Argument");
        	myAddArgumentButton.setEnabled(false);
        	myAddArgumentButton.addActionListener(new ActionListener() {
        		  public void actionPerformed(ActionEvent e) {    
        		addArgument();
        		  }
        	});
        	
        	buttonPanel.add(myAddArgumentButton);
        	
        	myChangeReturnButton = new JButton("Change Return type (" + mySelectedOperation.getReturns().getName() +")");
        	myChangeReturnButton.setEnabled(false);
        	myChangeReturnButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					changeReturnType();
					
				}
        		
        	});
        	buttonPanel.add(myChangeReturnButton);
        }
        
        
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
			actualApp.writeToOutputWindow("log", "ConnectionString was null\n");
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
		
		IRPModelElement selectedElement = selectedElementItem.getModelElement();
		return selectedElement;
	}


	@SuppressWarnings("unchecked")
	private void addDependency() 
	{
		IRPModelElement selectedElement = getSelectedElement();
		
		if(selectedElement==null)
		{
			return;
		}
		
		List<IRPDependency> dependencies = mySelectedUnit.getDependencies().toList();
		
		for(IRPDependency dependency:dependencies)
		{
			if(dependency.getDependsOn().equals(selectedElement))
			{
				System.out.println("Dependency is already there");
				return;
			}
		}
		
		IRPDependency newDependency = mySelectedUnit.addDependencyTo(selectedElement);
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
		if(selectedElement instanceof IRPClass == false)
		{
			System.out.println("Generalizations only with Classifiers");
			return;
		}
		IRPClassifier selectedClassifier = (IRPClassifier) selectedElement;
		
		mySelectedClassifier.addGeneralization(selectedClassifier);
		IRPGeneralization newGeneralization = mySelectedClassifier.findGeneralization(selectedClassifier.getName());
		newGeneralization.openFeaturesDialog(0);
	
	}
	private void addArgument()
	{
		IRPModelElement selectedElement = getSelectedElement();
		if(selectedElement==null)
		{
			return;
		}
		if(selectedElement instanceof IRPClassifier == false)
		{
			System.out.println("Arguments only with classifiers");
			return;
		}
		
		IRPClassifier selectedClassifier = (IRPClassifier) selectedElement;
		
		String argumentName = "a"+selectedClassifier.getName();
		
		IRPArgument argument = mySelectedOperation.addArgument(argumentName);
		
		argument.setType(selectedClassifier);
		mySelectedOperation.openFeaturesDialog(0);
		
	}
	
	private void changeReturnType()
	{
		IRPModelElement selectedElement = getSelectedElement();
		if(selectedElement==null)
		{
			return;
		}
		if(selectedElement instanceof IRPClassifier == false)
		{
			System.out.println("Return type only classifiers");
			return;
		}
		
		mySelectedOperation.setReturns((IRPClassifier)selectedElement);
		myChangeReturnButton.setText("Change Return type ("+selectedElement.getName()+")");
		mySelectedOperation.openFeaturesDialog(0);
		
		
	}
	
}

class RelationListener extends RPApplicationListener
{

	
	private IRPModelElement mySelectedElement;
	private JFrame myFrame;
	private RhapsodyModelElementSearchable myRhapsodyModelElementSearchable;
	
	public RelationListener(JFrame aFrame, IRPModelElement aSelectedModelElement, RhapsodyModelElementSearchable aRhapsodyModelElementSearchable) 
	{
		myFrame = aFrame;
		mySelectedElement = aSelectedModelElement;
		myRhapsodyModelElementSearchable = aRhapsodyModelElementSearchable;
	}
	
	@Override
	public boolean afterAddElement(IRPModelElement pModelElement) {
		
		
		return false;
	}

	@Override
	public boolean afterProjectClose(String bstrProjectName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean beforeProjectClose(IRPProject pProject) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onDiagramOpen(IRPDiagram pDiagram) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onDoubleClick(IRPModelElement pModelElement) {
		// TODO Auto-generated method stub
		if(pModelElement==null)
		{
			return false;
		}
		if(mySelectedElement==null)
		{
			return false;
		}
		
		
		if(pModelElement.equals(mySelectedElement))
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
			               myFrame.setAlwaysOnTop(false);
			               myFrame.toFront();
			            }
			        }, 
			        1000 
			);
			//refresh relations
			if(myRhapsodyModelElementSearchable!=null)
			{
				myRhapsodyModelElementSearchable.clearRelationList();
				myRhapsodyModelElementSearchable.createRelationList();
			}
			return true;
			
		}
		
		return false;
	}

	@Override
	public boolean onFeaturesOpen(IRPModelElement pModelElement) {
		return false;
	
	}
	
}
