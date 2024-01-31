package de.schlaich.gunnar.rhapsody.plantUMLView;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class showPNG extends JFrame {
	
	 public showPNG(byte[] aImage, boolean aExitOnClose) 
	 { 
		 if(aExitOnClose)
		 {
			 this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		 }
		 else
		 {
			 this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
		 }
		 
		 JPanel panel = new JPanel();  
		 //panel.setSize(500,640);
		 panel.setBackground(Color.WHITE);  
		 ImageIcon icon = new ImageIcon(aImage);
		 int width = icon.getIconWidth();
		 int height = icon.getIconHeight()+50;
		 
		 Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		 
		 
		 if(width>size.width)
		 {
			 width = size.width;
		 }
		 if(width<250)
		 {
			 width = 250;
		 }
		 
		 
		 if(height>size.height)
		 {
			 height = size.height;
		 }
		 
		 
	
			
		setLocation(size.width/2-width/2,size.height/2-height/2);
			
			
			
			
		 
		 
		 this.setSize(width,height);
		 JLabel label = new JLabel();  
		 label.setIcon(icon);  
		 panel.add(label); 

		 this.getContentPane().add(panel);   
		 
		 
	} 

}


