package de.schlaich.gunnar.rhapsody.plantUMLView;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPModelElement;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.core.DiagramDescription;

public class PlantUMLStarter {
	
	

	public PlantUMLStarter() {
		// TODO Auto-generated constructor stub
	}
	
public static void startPlantUML(IRPApplication rhapsody, IRPModelElement selected) {
		
		
		PlantUMLGenerator gen = new PlantUMLGenerator(selected, false);
		//System.out.print(gen.getPlanUml());
		StringSelection stringSelection = new StringSelection(gen.getPlantUml());
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, null);
		
		final ByteArrayOutputStream png = new ByteArrayOutputStream();
		SourceStringReader reader = new SourceStringReader(gen.getPlantUml());
		
		
		
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		// Write the first image to "os"
		
		try
		{
		
			@SuppressWarnings("deprecation")
			String descSVG = reader.generateImage(os, new FileFormatOption(FileFormat.SVG));
			DiagramDescription descc = reader.generateDiagramDescription(new FileFormatOption(FileFormat.SVG));
			String descSVG2 = descc.getDescription();
			os.close();
	
			// The XML is stored into svg
			final String svg = new String(os.toByteArray(), Charset.forName("UTF-8"));
			showSVG sv = new showSVG(svg);
			sv.setVisible(true);
		
		}
		catch(IOException e)
		{
			System.out.println("SVG generation failed");
			e.printStackTrace();
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
		
	}	
	
	

}
