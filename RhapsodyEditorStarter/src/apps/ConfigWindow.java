package apps;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import de.schlaich.gunnar.rhapsody.utilities.RhapsodyPreferences;

public class ConfigWindow {
	
	private JFrame myFrame = null;
	
	private JCheckBox myCheckboxDoubleClick = null;
	private JCheckBox myCheckboxOpenFeatures = null;
	private JCheckBox myCheckboxAfterCreate = null;
	private JButton myOkButton = null;
	private JButton myCancelButton = null;
	private ButtonGroup myStyleButtonGroup = null;
	private JRadioButton myStyleRhapsodyRadioButton = null;
	private JRadioButton myStyleDarkRadioButton = null;
	
	
	private RhapsodyPreferences myPrefs = null;

	public ConfigWindow() throws HeadlessException {
		
		
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
		
		myPrefs = RhapsodyPreferences.Get(true);
		myFrame = new JFrame("Configuration");
        myFrame.setSize(300, 200);
        
        myCheckboxDoubleClick = new JCheckBox("Editor on Double Click", myPrefs.getStartEditorOnDoubleClick());
        myCheckboxOpenFeatures = new JCheckBox("Editor on Oben Features Dialog ", myPrefs.getStartEditorOnFeatureOpen());
        myCheckboxAfterCreate = new JCheckBox("Editor after Create an Operation", myPrefs.getStartEditorAfterElementAdded());
        myStyleButtonGroup = new ButtonGroup();
        
        myStyleRhapsodyRadioButton = new JRadioButton("Rhapsody Style", true);
        myStyleRhapsodyRadioButton.setEnabled(false);
        myStyleDarkRadioButton = new JRadioButton("Dark Style", false);
        myStyleDarkRadioButton.setEnabled(false);
        myStyleButtonGroup.add(myStyleRhapsodyRadioButton);
        
        myStyleButtonGroup.add(myStyleDarkRadioButton);
        
        myOkButton = new JButton("Ok");
        myOkButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				myPrefs.setStartEditorOnDoubleClick(myCheckboxDoubleClick.isSelected());
				myPrefs.setStartEditorOnFeatureOpen(myCheckboxOpenFeatures.isSelected());
				myPrefs.setStartEditorAfterElementAdded(myCheckboxAfterCreate.isSelected());
				
				myFrame.dispose();
				
				
				
			}
		});
        myCancelButton = new JButton("Cancel");
        myCancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				myFrame.dispose();
				
			}
		});
        
        // Add components to the window
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(myCheckboxDoubleClick);
        centerPanel.add(myCheckboxOpenFeatures);
        centerPanel.add(myCheckboxAfterCreate);
        
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(myStyleRhapsodyRadioButton);
        centerPanel.add(myStyleDarkRadioButton);

        // Create panel for bottom buttons
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(myOkButton);
        bottomPanel.add(myCancelButton);

        // Add components to the window
        myFrame.add(centerPanel, BorderLayout.CENTER);
        myFrame.add(bottomPanel, BorderLayout.SOUTH);
       
        // Show the window
        myFrame.setVisible(true);
		
		
	}


}
