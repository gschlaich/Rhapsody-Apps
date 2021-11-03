package apps;

import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.swing.JScrollPane;

import com.ibm.rhapsody.apps.*;
import com.telelogic.rhapsody.core.*;
import net.sourceforge.plantuml.*;




public class MainApp extends App {
	
	/*
	* This method is called on invoking an app inside Rhapsody.
	* rhapsody - Instance of an active Rhapsody application 
	* selected - Selected element in Rhapsody
	*/
	public void execute(IRPApplication rhapsody, IRPModelElement selected) {
		PlantUMLGenerator gen = new PlantUMLGenerator(selected);
		//System.out.print(gen.getPlanUml());
		StringSelection stringSelection = new StringSelection(gen.getPlanUml());
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, null);
		
		final ByteArrayOutputStream png = new ByteArrayOutputStream();
		SourceStringReader reader = new SourceStringReader(gen.getPlanUml());
		
		
		String imageName = selected.getIconFileName();
		imageName = imageName.replace("\\", "/");
		Toolkit kit = Toolkit.getDefaultToolkit();
		Image img = kit.createImage(imageName);
		
		
		try {
			String desc = reader.outputImage(png).getDescription();
			byte[] image = png.toByteArray();
			
			
			
			JScrollPane pane = new JScrollPane(
			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

			Point location = MouseInfo.getPointerInfo().getLocation(); 
			int x = (int) location.getX();
			int y = (int) location.getY();
			showPNG sp = new showPNG(image);
			//sp.setContentPane(pane);
			sp.setLocation(location);
			sp.setIconImage(img);
			sp.setTitle(selected.getName());
			sp.setVisible(true);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}	
	
    /*
     *  Debug app by launching it as "Java Application" is Eclipse.
	 *  Note: Select an element in Rhapsody in order to simulate launching app on a selected element in the browser.
     */	
	public static void main(String[] args) {
		MainApp app = new MainApp();
		app.invokeFromMain();
	}		
}
