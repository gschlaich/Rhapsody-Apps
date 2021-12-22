package de.schlaich.gunnar.parser;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;

import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.parser.AbstractParser;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParseResult;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParserNotice;
import org.fife.ui.rsyntaxtextarea.parser.ParseResult;
import org.fife.ui.rsyntaxtextarea.parser.Parser;
import org.fife.ui.rsyntaxtextarea.parser.ParserNotice;
import org.fife.ui.rtextarea.Gutter;
import org.fife.ui.rtextarea.GutterIconInfo;

import com.github.difflib.DiffUtils;
import com.github.difflib.algorithm.DiffException;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Chunk;
import com.github.difflib.patch.DeltaType;
import com.github.difflib.patch.Patch;

import de.schlaich.gunnar.rhapsody.utilities.ASTHelper;
import de.schlaich.gunnar.rhapsody.utilities.RhapsodyOperation;

public class DiffParser extends AbstractParser {
	
	
	private List<String> myOrigLines;
	private DefaultParseResult myResult;
	private static final Color COLOR = Color.darkGray;
	private Gutter myGutter;
	private Icon myChangedIcon = null;
	private List<GutterIconInfo> myInfos = null;
	

	public DiffParser(String aOrigDoc, Gutter aGutter) 
	{
		myResult = new DefaultParseResult(this);
		myOrigLines = ASTHelper.getLines(aOrigDoc);
		myGutter = aGutter;
		myChangedIcon = RhapsodyOperation.getIcon("RhapsodyIcons_111.gif");
		myInfos = new ArrayList<GutterIconInfo>();
	}
	
	
	public void update(String aOrigDoc)
	{
		myOrigLines = ASTHelper.getLines(aOrigDoc);
		myResult.clearNotices();
		for(GutterIconInfo info:myInfos)
		{
			myGutter.removeTrackingIcon(info);
		}

	}

	

	@Override
	public ParseResult parse(RSyntaxDocument aDoc, String style) 
	{
		Element root = aDoc.getDefaultRootElement();
		int lineCount = root.getElementCount();
		myResult.clearNotices();
		myResult.setParsedLines(0, lineCount-1);
		
		for(GutterIconInfo info:myInfos)
		{
			myGutter.removeTrackingIcon(info);
		}

		String currentDoc="";
		try 
		{
			currentDoc = aDoc.getText(0, aDoc.getLength());
		} 
		catch (BadLocationException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    List<String> currentLines = ASTHelper.getLines(currentDoc);
	       
	    try 
	    {
			if((myOrigLines==null)||(currentLines==null))
			{
				return myResult;
			}
	    	Patch<String> patch = DiffUtils.diff(myOrigLines, currentLines);
			
			for (AbstractDelta<String> delta : patch.getDeltas()) 
			{
				DeltaType type = delta.getType();
				Chunk<String> target = delta.getTarget();
				Chunk<String> source = delta.getSource();
				List<String> targetLines = target.getLines();
				List<String> sourceLines = source.getLines();
				int lineNr = target.getPosition();
				int i=0;
				for(String targetLine:targetLines)
				{
					String sourceLine = "";
					if(sourceLines.size()>i)
					{
						sourceLine = sourceLines.get(i);
					}
					ChangeNotice cn = new ChangeNotice(this, type.toString()+" from:\n "+sourceLine+"\n to: \n" + targetLine, lineNr);
					cn.setLevel(ParserNotice.Level.WARNING);
					cn.setShowInEditor(false);
					cn.setColor(COLOR);
					
					try 
					{
						GutterIconInfo info = myGutter.addLineTrackingIcon(lineNr,myChangedIcon, type.toString());
						myInfos.add(info);
						
						
					} 
					catch (BadLocationException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					myResult.addNotice(cn);

					i++;
					lineNr++;
					
				}
				if(targetLines.isEmpty())
				{
					
					ChangeNotice cn = new ChangeNotice(this, type.toString(), target.getPosition());
					cn.setLevel(ParserNotice.Level.WARNING);
					cn.setShowInEditor(false);
					cn.setColor(COLOR);
					
					try 
					{
						GutterIconInfo info = myGutter.addLineTrackingIcon(lineNr,myChangedIcon, type.toString());
						myInfos.add(info);
						
						
					} 
					catch (BadLocationException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					myResult.addNotice(cn);
				}
				
				
				
	
				System.out.println("Line: " + (delta.getTarget().getPosition()+1)+ " "+ type.toString());
				
			}      
		} 
	    catch (DiffException e) 
	    {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	   
		return myResult;
	}
	
	/**
	 * A parser notice that signifies a task.  This class is here so we can
	 * treat tasks specially and show them in the
	 * {@link org.fife.ui.rsyntaxtextarea.ErrorStrip} even though they are
	 * <code>INFO</code>-level and marked as "don't show in editor."
	 */
	public static class ChangeNotice extends DefaultParserNotice 
	{

		public ChangeNotice(Parser parser, String message, int line) 
		{
			super(parser, message, line);
		}

	}
	
	

}
