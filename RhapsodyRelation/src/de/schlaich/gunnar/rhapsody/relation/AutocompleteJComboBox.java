package de.schlaich.gunnar.rhapsody.relation;
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import com.telelogic.rhapsody.core.IRPClassifier;

import apps.RhapsodyModelElementItem;
import apps.Searchable;



/**
 *
 * JComboBox with an autocomplete drop down menu. This class is hard-coded for
 * String objects, but can be
 *
 * altered into a generic form to allow for any searchable item.
 *
 *
 * @author G. Cope
 *
 *
 *
 */
public class AutocompleteJComboBox extends JComboBox {

    static final long serialVersionUID = 4321421L;

    private final Searchable<RhapsodyModelElementItem, String> mySearchable;
    public String key = "";

    /**
     *
     * Constructs a new object based upon the parameter searchable
     *
     * @param s
     *
     */
    public AutocompleteJComboBox(Searchable<RhapsodyModelElementItem, String> aSearchable) 
    {
        super();
        this.mySearchable = aSearchable;
        setEditable(true);
        Component c = getEditor().getEditorComponent();

        if (c instanceof JTextComponent) {
            final JTextComponent tc = (JTextComponent) c;
            
            tc.addKeyListener(new KeyListener() {
				
				@Override
				public void keyTyped(KeyEvent e) {
					// TODO Auto-generated method stub
					 
						
						
		 
					
				}
				
				@Override
				public void keyReleased(KeyEvent e) {
					// TODO Auto-generated method stub
					
					
				}
				
				@Override
				public void keyPressed(KeyEvent e) {
					// TODO Auto-generated method stub
					
					switch(e.getKeyChar())
					{
					case KeyEvent.VK_BACK_SPACE:
						if(key.length()>0)
						 {
							 key = key.substring(0, key.length()-1);
							 update();
						 }
						 return;
					}
					
					 
					 
					 
					 
					 char c = e.getKeyChar();
					 
					 if(Character.isLetterOrDigit(c)||c==':'||c=='_'||c=='-')
					 {
						 key+=c;
						 update();
					 }
					 
					 
					 switch (c)
					 {
					 case ',':
					 case '?':
					 case '(':
					 case ')':
					 case '&':
					 case '.':
					 case '[':
					 case ']':
					 case '{':
					 case '}':
						 update();
					 }
					 
					 

					 
					
					 
					 
					
				}
			});
		
            
//            tc.getDocument().addDocumentListener(new DocumentListener() {
//
//                @Override
//                public void changedUpdate(DocumentEvent arg0) {
//                	update();
//                }
//
//                @Override
//                public void insertUpdate(DocumentEvent arg0) {
//     
//                	 update();
//                	//if (arg0.getLength() == 1) {
//                    //    update();
//                    //}
//                }
//
//                @Override
//                public void removeUpdate(DocumentEvent arg0) {
//                   
//                	 update();
//                	 /*
//                	if (arg0.getLength() == 1) {
//                    	
//                    	if (tc.getText().length() > 0) {
//                            
//                            update();
//                        }
//                    }
//                    */
//                }
//
//                
//            });
            
            
            
           


            //When the text component changes, focus is gained 
            //and the menu disappears. To account for this, whenever the focus
            //is gained by the JTextComponent and it has searchable values, we show the popup.
            tc.addFocusListener(new FocusListener() {

                @Override
                public void focusGained(FocusEvent arg0) {

                    if (tc.getText().length() > 0) {
                        setPopupVisible(true);
                    }
                }

                @Override
                public void focusLost(FocusEvent arg0) {
                }
            });
        } else {
            throw new IllegalStateException("Editing component is not a JTextComponent!");
        }
    }
    
    public void update() {
        //perform separately, as listener conflicts between the editing component
        //and JComboBox will result in an IllegalStateException due to editing 
        //the component when it is locked. 
        SwingUtilities.invokeLater(new Runnable() {

            @SuppressWarnings("unchecked")
			@Override
            public void run() {
            	
            	
            	System.out.println("Entered text: "+key);
                List<RhapsodyModelElementItem> founds = new ArrayList<>(mySearchable.search(key));
                Set<RhapsodyModelElementItem> foundSet = new HashSet<>();

                for (RhapsodyModelElementItem modelEltemtenItem  : founds) {
                    foundSet.add(modelEltemtenItem);
                }

                Collections.sort(founds);//sort alphabetically
                setEditable(false);
                removeAllItems();

                //if founds contains the search text, then only add once.
                if (!foundSet.contains(key.toLowerCase())) {
                    addItem(key);
                }

                for (RhapsodyModelElementItem s : founds) {
                    addItem(s);
                }

                setEditable(true);
                setPopupVisible(true);
                //tc.requestFocus();
            }
        });
    }
    
    
    
    

}