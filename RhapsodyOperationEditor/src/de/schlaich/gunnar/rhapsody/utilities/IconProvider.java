package de.schlaich.gunnar.rhapsody.utilities;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.telelogic.rhapsody.core.IRPModelElement;

public class IconProvider {
	
	private Map<String, Image> myImageMap;
	private String myPath = null;
	
	
	private static final Map<String, String> myOverlayLookup;
    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put("RhapsodyIcons_512.gif", "RhapsodyIcons_64.gif");
        aMap.put("RhapsodyIcons_256.gif", "RhapsodyIcons_63.gif");
        myOverlayLookup = Collections.unmodifiableMap(aMap);
    }
	
	public IconProvider() {
		myImageMap = new HashMap<String, Image>();
		
	}
	
	private void setPath(String aPath)
	{
		if(myPath==null)
		{
			File file = new File(aPath);
			myPath = file.getParent();
		}
	}
	
	
	public Icon getIcon(IRPModelElement aElement)
	{
		String backgroundPath = aElement.getIconFileName();
		
		setPath(backgroundPath);
		
		String backgroundFileName = getFilename(backgroundPath);
		Image backgroundImage = getImage(backgroundFileName);
		
		
		String overlayPath = aElement.getOverlayIconFileName();
		
		String overlayFileName = getFilename(overlayPath);
		
		
		String newOverlayFileName = myOverlayLookup.get(overlayFileName);
		if(newOverlayFileName!=null)
		{
			Image overlayImage = getImage(newOverlayFileName);
			if(backgroundImage!=null&&overlayImage!=null)
			{
				//combine both
				 final BufferedImage combinedImage = new BufferedImage( 		 
			                backgroundImage.getWidth(null), 
			                backgroundImage.getHeight(null), 
			                BufferedImage.TYPE_INT_ARGB );
			     Graphics2D g = combinedImage.createGraphics();
			     g.drawImage(backgroundImage,0,0,null);
			     g.drawImage(overlayImage,0,0,null);
			     g.dispose();
			        
			        return new ImageIcon(combinedImage);
			}
	
		}
		else if(overlayFileName.equals("RhapsodyIcons_0.gif")==false)
		{
			System.out.println("Overlay: "+overlayFileName);
		}
		
		
		if(backgroundImage!=null)
		{
			 return new ImageIcon(backgroundImage);
		}
		else
		{
			//empty icon?
			return new ImageIcon();
		}
	}
	
	public Icon getIcon(String aFileName)
	{
		Image image = getImage(aFileName);
		if(image!=null)
		{
			return new ImageIcon(image);
		}
		
		return new ImageIcon();
	}
	
	private Image getImage(String aFileName)
	{
		Image image = myImageMap.get(aFileName);
		if(image==null)
		{
			String path = myPath+File.separator+aFileName;
			image = readImage(path);
			if(image!=null)
			{
				myImageMap.put(aFileName, image);
			}
			
		}
		
		return image;
	}
	
	private Image readImage(String aPath)
	{
		File file = new File(aPath);
		BufferedImage image = null;
		try 
		{
			image = ImageIO.read(file);
		} 
		catch (IOException e) 
		{	
			e.printStackTrace();
						
		}
		return image;
	}
	
	private String getFilename(String aPath)
	{
		File file = new File(aPath);
		return file.getName();
			
	}
	
	
	

}
