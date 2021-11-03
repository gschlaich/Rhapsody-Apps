package apps;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class showPNG extends JFrame {
	
	 public showPNG(byte[] aImage) 
	 { 
		 this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		 
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
		 
		 
		 
		 this.setSize(width,height);
		 JLabel label = new JLabel();  
		 label.setIcon(icon);  
		 panel.add(label); 

		 this.getContentPane().add(panel);   
		 
		 
	} 

}


