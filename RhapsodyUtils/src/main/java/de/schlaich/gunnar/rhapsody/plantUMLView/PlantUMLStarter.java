package de.schlaich.gunnar.rhapsody.plantUMLView;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPModelElement;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

public class PlantUMLStarter {
	
	

	public PlantUMLStarter() {
		// TODO Auto-generated constructor stub
	}
	
public static void startPlantUML(IRPApplication rhapsody, IRPModelElement selected, boolean aExitOnClose, Boolean aShowMermaid) 
{
		
		// Erstelle die neuen Generatoren
		PlantUMLGenerator plantUMLGen = new PlantUMLGenerator(selected, false);
		MermaidGenerator mermaidGen = new MermaidGenerator(selected, false);
		
		
		String diagramStr = aShowMermaid ? mermaidGen.getMermaidDiagram() : plantUMLGen.getPlantUml();
		
		// Kopiere UML-Diagramm in die Zwischenablage
		StringSelection stringSelection = new StringSelection(diagramStr);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, null);
		
		String imageName = selected.getIconFileName();
		imageName = imageName.replace("\\", "/");
		Toolkit kit = Toolkit.getDefaultToolkit();
		Image img = kit.createImage(imageName);
		
		// Display Mermaid diagram if requested
		if (aShowMermaid) {
			showPNG viewer = MermaidViewer.displayMermaidDiagram(mermaidGen.getMermaidDiagram(), selected.getName(), aExitOnClose);
			viewer.setIconImage(img);
			viewer.setVisible(true);
			
			return;
		}
		
		// Otherwise, display PlantUML diagram
		final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		SourceStringReader reader = new SourceStringReader(plantUMLGen.getPlantUml());
	
		
				
		try 
		{
			FileFormat fileFormat = FileFormat.PNG;
			reader.outputImage(outStream, 0, new FileFormatOption(fileFormat));
			
			//String desc = reader.outputImage(outStream).getDescription();
			byte[] image = outStream.toByteArray();
			
			outStream.reset();
			
			fileFormat = FileFormat.SVG;
			reader.outputImage(outStream, 0, new FileFormatOption(fileFormat));
			String ascii = new String(outStream.toByteArray(), StandardCharsets.UTF_8);
			
			System.out.println(ascii);
			
			
			
			showPNG sp = new showPNG(image, aExitOnClose);
			//sp.setContentPane(pane);
			//sp.setLocation(location);
			sp.setIconImage(img);
			sp.setTitle(selected.getName());
			sp.setVisible(true);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//}
		
		
	}	

}