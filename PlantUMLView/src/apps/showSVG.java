package apps;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintWriter;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.svg.SVGDocument;


public class showSVG extends JFrame {
	
	private showSVG myShowSVG = null;
	private SVGDocument mySVGDocument = null;
	private int myMenuSize = 48;
	
	public showSVG(String aSVGString) 
	{
		myShowSVG = this;
		

		try 
		{
			File tempFile = File.createTempFile("temp", ".svg");
			PrintWriter out = new PrintWriter(tempFile);
			out.println(aSVGString);
			out.flush();
			out.close();
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				
			
			
			String parser = XMLResourceDescriptor.getXMLParserClassName();
			SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);
			
			   
			mySVGDocument = factory.createSVGDocument(tempFile.toURI().toString());
			
			tempFile.deleteOnExit();
			   
			int widthsvg = (int)mySVGDocument.getRootElement().getWidth().getBaseVal().getValue();
			int heightsvg = (int)mySVGDocument.getRootElement().getHeight().getBaseVal().getValue();
			
			int width = widthsvg;
			int height = heightsvg+myMenuSize;
			
			Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
			 	
			if(width>size.width)
			{
				height = ((heightsvg*size.width)/width)+myMenuSize;
				width = size.width;
			}
			
			if(height>size.height)
			{
				width = (width*size.height)/heightsvg;
				height = size.height;
			}
	
			setLocation(size.width/2-width/2,size.height/2-height/2);

			

	        final JSVGCanvas canvas = new JSVGCanvas();
	        
	        setSize(width,height);
	        
	        canvas.setSVGDocument(mySVGDocument);
	        
	       
	        getContentPane().add(canvas);
	        
	        
	        //add menue
	     
	        JMenuBar menuBar = new JMenuBar();

	        
	        JMenu fileMenu = new JMenu("File");
	        JMenuItem saveItem = new JMenuItem("save SVG");
	       
	        fileMenu.add(saveItem);
	
	        menuBar.add(fileMenu);
	       
	        setJMenuBar(menuBar);
	        
	        saveItem.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					JFileChooser fileChooser = new JFileChooser();
	                fileChooser.setDialogTitle("Save SVG as");
	                fileChooser.setFileFilter(new FileNameExtensionFilter("SVG files", "svg"));
	                int userSelection = fileChooser.showSaveDialog(myShowSVG);
	                if (userSelection == JFileChooser.APPROVE_OPTION) {
						try {
							File fileToSave = fileChooser.getSelectedFile();

							Transformer transformer;

							transformer = TransformerFactory.newInstance().newTransformer();

							StreamResult output = new StreamResult(fileToSave);
							Source input = new DOMSource(mySVGDocument);
							transformer.transform(input, output);
						}

						catch (Exception ex) {
							ex.printStackTrace();
						}
	                }
					
				}
			});

	        
	        
	       
	        //canvas.setURI(tempFile.toURI().toString());
	        
	        
	        
	        
				
				

	        
	        
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
		
		
		
	}

}
