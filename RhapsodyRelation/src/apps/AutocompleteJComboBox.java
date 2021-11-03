package apps;
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
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

    private final Searchable<RhapsodyClassifierItem, String> mySearchable;

    /**
     *
     * Constructs a new object based upon the parameter searchable
     *
     * @param s
     *
     */
    public AutocompleteJComboBox(Searchable<RhapsodyClassifierItem, String> aSearchable) 
    {
        super();
        this.mySearchable = aSearchable;
        setEditable(true);
        Component c = getEditor().getEditorComponent();

        if (c instanceof JTextComponent) {
            final JTextComponent tc = (JTextComponent) c;
            tc.getDocument().addDocumentListener(new DocumentListener() {

                @Override
                public void changedUpdate(DocumentEvent arg0) {
                }

                @Override
                public void insertUpdate(DocumentEvent arg0) {
                    System.out.println("Insert: " + arg0.getLength());
                    if (arg0.getLength() == 1) {
                        System.out.println("Update");
                        update();
                    }
                }

                @Override
                public void removeUpdate(DocumentEvent arg0) {
                    System.out.println("Remove: " + arg0.getLength());
                    if (arg0.getLength() == 1) {
                        if (tc.getText().length() > 0) {
                            System.out.println("Update2: " + tc.getText().length());
                            update();
                        }
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
                            List<RhapsodyClassifierItem> founds = new ArrayList<>(mySearchable.search(tc.getText()));
                            Set<RhapsodyClassifierItem> foundSet = new HashSet<>();

                            for (RhapsodyClassifierItem classifier  : founds) {
                                foundSet.add(classifier);
                            }

                            Collections.sort(founds);//sort alphabetically
                            setEditable(false);
                            removeAllItems();

                            //if founds contains the search text, then only add once.
                            if (!foundSet.contains(tc.getText().toLowerCase())) {
                                addItem(tc.getText());
                            }

                            for (RhapsodyClassifierItem s : founds) {
                                addItem(s);
                            }

                            setEditable(true);
                            setPopupVisible(true);
                            tc.requestFocus();
                        }
                    });
                }
            });

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
    
    
    
    

}