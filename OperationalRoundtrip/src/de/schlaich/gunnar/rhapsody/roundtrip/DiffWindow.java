package de.schlaich.gunnar.rhapsody.roundtrip;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;

public class DiffWindow extends JRootPane implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub

	}
	
	public DiffWindow(String aDiff)
	{
		
		int factorW = 7;
	    int factorH = 16;
	    
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		
		int rows = (int)(dim.height/factorH*0.7);
		int cols = (int)(dim.width/factorW*0.5);
		
		this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
		
		
		JPanel panel = new JPanel();
	    LayoutManager layout = new FlowLayout();  
	    panel.setLayout(layout);       

	     JEditorPane jEditorPane = new JEditorPane();
	     jEditorPane.setEditable(false);  
	     
	     jEditorPane.setText(aDiff);
	     
	     
	     JScrollPane jScrollPane = new JScrollPane(jEditorPane);
	     jScrollPane.setPreferredSize(new Dimension(540,480));      

	     panel.add(jScrollPane);
	     contentPane.add(panel, BorderLayout.CENTER);  
	     
	     setContentPane(contentPane);
	     
	     
	      
	     setSize(540, 480);      
	     
	   setVisible(true);
		
		
			
		
	}

}
