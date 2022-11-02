package apps;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.eclipse.swt.internal.webkit.JSClassDefinition;

import com.github.difflib.DiffUtils;
import com.github.difflib.algorithm.DiffException;
import com.github.difflib.algorithm.myers.MyersDiff;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;
import com.github.difflib.text.DiffRow;
import com.github.difflib.text.DiffRow.Tag;
import com.github.difflib.text.DiffRowGenerator;
import com.ibm.rhapsody.apps.*;
import com.telelogic.rhapsody.core.*;

import de.schlaich.gunnar.parser.DiffParser;
import de.schlaich.gunnar.rhapsody.utilities.ASTHelper;
import de.schlaich.gunnar.rhapsody.utilities.RhapsodyHelper;
import de.schlaich.gunnar.rhapsody.utilities.RhapsodyOperation;

import com.ibm.rhapsody.apps.TriggerAdapter.Trigger;

public class MainApp extends App implements ActionListener {
	
	/*
	* This method is called on invoking an app inside Rhapsody.
	* rhapsody - Instance of an active Rhapsody application 
	* selected - Selected element in Rhapsody
	*/
	
	private static IRPClass myClass = null;
	private String myDiffResults;
	private static MainApp myApp;
	private static JFrame myFrame;
	private List<IRPOperation> myChangedOperations;
	
	
	@SuppressWarnings("unchecked")
	public void execute(IRPApplication rhapsody, IRPModelElement selected) 
	{
		this.rhapsody = rhapsody;
		//first only with classes
		if(selected instanceof IRPClass == false)
		{
			return;
		}
		
		myClass = (IRPClass)selected;

		List<IRPOperation> operations = myClass.getOperations().toList();
				
		DiffRowGenerator generator;
		
		StringBuilder diffStringBuilder = new StringBuilder();
		
		diffStringBuilder.append("<html>\n<body>\n");
		
		myChangedOperations = new ArrayList<IRPOperation>(); 
	
		
		try
		{
		
			int foundOperations = 0;
			DiffRowGenerator.Builder builder = DiffRowGenerator.create();
			builder.showInlineDiffs(true);
			builder.inlineDiffByWord(true);
			generator = builder.build();
			
			String nameSpace = RhapsodyOperation.getNamespace(myClass);
			String sourcePath = ASTHelper.getSourcePath(myClass, rhapsody);
			Map<String,String> functions = ASTHelper.getFunctiondefinitions(sourcePath+".cpp", nameSpace);
			
			
			for(IRPOperation operation:operations)
			{
				if(operation.getIsAbstract()==1)
				{
					continue;
				}
				String sourceCode = "";
				
				if((operation.isATemplate()==1)||(operation.getIsInline()==1))
				{
					sourceCode = ASTHelper.getSourceText(operation, rhapsody);
				}
				else
				{
					sourceCode = ASTHelper.getSourceFromMap(functions, operation);
				}
				
				
				if(sourceCode==null)
				{
					rhapsody.writeToOutputWindow("log", "Operation: \" " + operation.getImplementationSignature() + " \" not found/component active!\n");
					diffStringBuilder.append("<table>\n <tbody>\n");
					diffStringBuilder.append("<tr>no source found / component inactive on " + operation.getImplementationSignature() +"</tr>\n");
					diffStringBuilder.append("</table>\n </tbody>\n");
					continue;
				}
				
				foundOperations++;
				
				List<String> sourceLines = ASTHelper.getLines(sourceCode);
				
				List<String> bodyLines = ASTHelper.getLines(operation.getBody());
				
				if(bodyLines == null)
				{
					bodyLines = new ArrayList<String>();
					bodyLines.add("");
				}
				
				if(sourceLines == null)
				{
					sourceLines = new ArrayList<String>();
					sourceLines.add("");
				}
				
				
				List<DiffRow> rows = generator.generateDiffRows(bodyLines,sourceLines);
				
				
				if(hasDiff(rows)==false)
				{
					diffStringBuilder.append("<table>\n <tbody>\n");
					diffStringBuilder.append("<tr>no changes: " + operation.getImplementationSignature() +"</tr>\n");
					diffStringBuilder.append("</table>\n </tbody>\n");
					
					continue;
				}
				
				
				
				diffStringBuilder.append("<table>\n <tbody>\n");
				//diffStringBuilder.append("<tr><th>"+RhapsodyOperation.getOperation(operation)+"</th></tr>\n");
				diffStringBuilder.append("<tr><th>"+operation.getImplementationSignature()+"</th></tr>\n");
				diffStringBuilder.append("</table>\n </tbody>\n");
				diffStringBuilder.append("<table>\n <tbody>\n");
				diffStringBuilder.append("<tr><th style=\"width: 50px;\">Line</th><th style=\"width: 50%;\" >Model</th><th style=\"width: 50%;\">Source</th><th>Action</th></tr>\n");
				int line = 0;
				int changes = 0;
				for (DiffRow row : rows) 
				{
				   line++;
				   Tag tag = row.getTag();
				
				   
				   if(tag==tag.EQUAL)
				   {
					   continue;
				   }
				   
				   
				  
				   if(row.getOldLine().trim().isEmpty()&&row.getNewLine().trim().isEmpty())
				   {
					   continue;
				   }
				    
				   if(row.getNewLine().contains("<span class=\"editNewInline\"></span>"))
				   {
					   continue;
				   }
				  
				    
				   //diffOperator.append()
				   diffStringBuilder.append("<tr><td style=\"width: 50px;\">"+line+"</td><td>" + row.getOldLine() + "</td><td>" + row.getNewLine() + "</td><td>"+tag.name()+"</td></tr>\n");
				   changes++;
				}
				
				diffStringBuilder.append("</tbody>\n </table>\n");
				if(changes==0)
				{
					continue;
				}
				myChangedOperations.add(operation);
				
		
			}
			
			diffStringBuilder.append("</body> </html>\n");
			
			if(foundOperations==0)
			{
				rhapsody.writeToOutputWindow("log", "Class not active\n");
				
				String lufSystem = UIManager.getSystemLookAndFeelClassName();
				
				try {
					UIManager.setLookAndFeel(lufSystem);
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
				
				JOptionPane.showMessageDialog(null, "Class not active");
				return;
			}
			if(myChangedOperations.size()==0)
			{
				rhapsody.writeToOutputWindow("log", "No operation changed\n");
				JOptionPane.showMessageDialog(null, "No operation changed");
				return;
			}
			
			
			createWindow(myClass.getName(),diffStringBuilder.toString());
			
			
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
	}	
	
	private boolean hasDiff(List<DiffRow> rows)
	{
		boolean ret = false;
		for(DiffRow row : rows)
		{
			if(row.getTag()==Tag.EQUAL)
			{
				continue;
			}
			if(row.getOldLine().trim().isEmpty()&&row.getNewLine().trim().isEmpty())
			{
			   continue;
			}
			
			ret = true;
			break;
		}
		return ret;
	}
	
	private boolean hasDiff(IRPOperation operation)
	{
		
		boolean ret = false;
		DiffRowGenerator.Builder builder = DiffRowGenerator.create();
		builder.showInlineDiffs(true);
		builder.inlineDiffByWord(true);
		DiffRowGenerator generator = builder.build();
		String sourceCode = ASTHelper.getSourceText(operation, rhapsody);
		List<String> sourceLines = ASTHelper.getLines(sourceCode);	
		List<String> bodyLines = ASTHelper.getLines(operation.getBody());
		
		if(sourceLines==null)
		{
			sourceLines = new ArrayList<String>();
			sourceLines.add("");
		}
		
		if(bodyLines==null)
		{
			bodyLines = new ArrayList<String>();
			bodyLines.add("");
		}
		
		try {
			List<DiffRow> rows = generator.generateDiffRows(bodyLines,sourceLines);
			ret = hasDiff(rows);
		} catch (DiffException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ret;
	}
	
    /*
     *  Debug app by launching it as "Java Application" is Eclipse.
	 *  Note: Select an element in Rhapsody in order to simulate launching app on a selected element in the browser.
     */	
	public static void main(String[] args) {
		myApp = new MainApp();
		
		String connectionstring = null;
		
		if (args.length >= 1) 
		{
			connectionstring = args[0];
		}
		
		RhapsodyHelper.executeApp(myApp, connectionstring);
		
		
	}
	
	
	 private static void createWindow(String aClassName, String aContent) {    
	     myFrame = new JFrame("Get from Source: " + aClassName);

	     myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	      
	     Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	     
	     String imageName = myClass.getIconFileName();
		imageName = imageName.replace("\\", "/");
		
		List<BufferedImage> icons = new ArrayList<BufferedImage>();
		File f = new File(imageName);
		try 
		{
			icons = Imaging.getAllBufferedImages(f);
			
		} 
		catch (ImageReadException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
			//icons =  ICODecoder.read(f);
		
		Image img = null;
		
		if(icons.size()>0)
		{
			img = icons.get(icons.size()-1);
		}
		
			
	      
	     if(img!=null)
	     {
	    	 myFrame.setIconImage(img);
	     }
      
	      
	     
	      createUI(myFrame,aContent);
      
	      myFrame.setLocation(dim.width/2-myFrame.getSize().width/2, dim.height/2-myFrame.getSize().height/2);

	      myFrame.setVisible(true);
	   }

	   private static void createUI(final JFrame frame, String aContent)
	   {  
		   
		   String lufSystem = UIManager.getSystemLookAndFeelClassName();
			
			
		   try {
			UIManager.setLookAndFeel(lufSystem);
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
		
	      
		  frame.setLayout(new BorderLayout());
		  JPanel buttonPanel = new JPanel(); 
		  
		  JButton okButton = new JButton("source to model");
		  buttonPanel.add(okButton);
		  
		  okButton.setActionCommand("ok");
		  okButton.addActionListener(myApp);
				
		  JButton cancelButton = new JButton("Cancel");
		  buttonPanel.add(cancelButton);
		  
		  
		  cancelButton.setActionCommand("Cancel");
		  cancelButton.addActionListener(myApp);
				
			
		  buttonPanel.setVisible(true);
		  
		  frame.add(buttonPanel,BorderLayout.SOUTH);
		  
		  JPanel panel = new JPanel();
	      LayoutManager layout = new FlowLayout();  
	      panel.setLayout(layout);       

	      JEditorPane jEditorPane = new JEditorPane();
	      jEditorPane.setEditable(false); 
	      
	      HTMLEditorKit kit = new HTMLEditorKit();
	      jEditorPane.setEditorKit(kit);
	      
	      StyleSheet styleSheet = kit.getStyleSheet();
	      
	      styleSheet.addRule("body {font-family:monospace; font-size: 9px; }");
	      styleSheet.addRule("table {width: 100%; border-style: solid; }");
	      styleSheet.addRule("th {background-color: gray; }");
	      styleSheet.addRule("td {background-color: #dddddd;}");
	      styleSheet.addRule("span {background-color: #ffaa55; }");
	      
	      
	      Document doc = kit.createDefaultDocument();
	      jEditorPane.setDocument(doc);
	      jEditorPane.setText(aContent);
	      jEditorPane.setAutoscrolls(true);
	     
	     
	      
	      
	      JScrollPane jScrollPane = new JScrollPane(jEditorPane);
	      
	      Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	      jScrollPane.setSize(dim.width*3/4, dim.height*3/4);      
	  
	      //jScrollPane.setSize(1000,600);

	      frame.setSize(jScrollPane.getSize());
	      panel.add(jScrollPane);
	      
	      frame.getContentPane().add(jScrollPane, BorderLayout.CENTER);
	     
	   }

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		String command = e.getActionCommand();
		
		if(myClass.isReadOnly()==0)
		{
			if(command.equals("ok"))
			{
				for(IRPOperation operation:myChangedOperations)
				{
					if(hasDiff(operation))
					{
						String sourceCode = ASTHelper.getSourceText(operation, rhapsody);
						if((sourceCode==null) ||sourceCode.isEmpty())
						{
							continue;
						}
						operation.setBody(sourceCode);
					}
				}
				
				myFrame.dispose();
			}
			
		}
		
		if(command.equals("Cancel"))
		{
			myFrame.dispose();
		}
		
		
		
	}  
	
}
