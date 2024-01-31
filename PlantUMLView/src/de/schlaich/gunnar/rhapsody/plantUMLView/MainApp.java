package de.schlaich.gunnar.rhapsody.plantUMLView;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import javax.swing.JScrollPane;

import com.ibm.rhapsody.apps.*;
import com.telelogic.rhapsody.core.*;

import de.schlaich.gunnar.rhapsody.utilities.RhapsodyHelper;
import net.sourceforge.plantuml.*;
import net.sourceforge.plantuml.core.DiagramDescription;





public class MainApp extends App {
	
	/*
	* This method is called on invoking an app inside Rhapsody.
	* rhapsody - Instance of an active Rhapsody application 
	* selected - Selected element in Rhapsody
	*/
	
	static IRPApplication myRhapsody = null;
	
	public void execute(IRPApplication rhapsody, IRPModelElement selected) {
		
		myRhapsody = rhapsody;
		PlantUMLStarter.startPlantUML(rhapsody, selected, true);
		
	}
		
		
//		String imageName = selected.getIconFileName();
//		imageName = imageName.replace("\\", "/");
//		Toolkit kit = Toolkit.getDefaultToolkit();
//		Image img = kit.createImage(imageName);
//		
//		
//		try 
//		{
//			String desc = reader.outputImage(png).getDescription();
//			byte[] image = png.toByteArray();
//			
//			
//			/*
//			JScrollPane pane = new JScrollPane(
//			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
//			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//			*/
//			
//			
//			
//			showPNG sp = new showPNG(image);
//			//sp.setContentPane(pane);
//			//sp.setLocation(location);
//			sp.setIconImage(img);
//			sp.setTitle(selected.getName());
//			sp.setVisible(true);
//			
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
		
	
	
    /*
     *  Debug app by launching it as "Java Application" is Eclipse.
	 *  Note: Select an element in Rhapsody in order to simulate launching app on a selected element in the browser.
     */	
	public static void main(String[] args) {
		MainApp myApp = new MainApp();
		String connectionstring = null;
		
		if (args.length >= 1) {
			connectionstring = args[0];

		}
		
		RhapsodyHelper.executeApp(myApp, connectionstring);
	}
	
	public static void StackTrace(Exception e)
	{
		e.printStackTrace();
		
		if(myRhapsody==null)
		{
			
			return;
		}
		
		StackTraceElement[] elements = e.getStackTrace();
		
		for(StackTraceElement element: elements)
		{
			myRhapsody.writeToOutputWindow("Log", element.toString()); 
		}
		

	}

}
