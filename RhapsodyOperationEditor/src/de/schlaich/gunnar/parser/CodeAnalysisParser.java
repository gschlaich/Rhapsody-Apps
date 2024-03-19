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
import org.fife.ui.rtextarea.Gutter;
import org.fife.ui.rtextarea.GutterIconInfo;

import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPComment;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPOperation;

import de.schlaich.gunnar.rhapsody.completionprovider.ClassifierCompletionProvider;
import de.schlaich.gunnar.rhapsody.utilities.ASTHelper;
import de.schlaich.gunnar.rhapsody.utilities.RhapsodyOperation;

public class CodeAnalysisParser extends AbstractParser {

	private IRPOperation myOperation = null;
	private Gutter myGutter = null;
	private Icon myErrorIcon = null;
	private Icon myWarningIcon = null;
	private List<GutterIconInfo> myInfos = null;
	private DefaultParseResult myResult;
	
	public CodeAnalysisParser(IRPOperation aOperation, Gutter aGutter) 
	{
		myOperation = aOperation;
		myGutter = aGutter;
		myErrorIcon = RhapsodyOperation.getIcon("RhapsodyIcons_110.gif");
		myWarningIcon = RhapsodyOperation.getIcon("RhapsodyIcons_205.gif");
		myInfos = new ArrayList<GutterIconInfo>();
		myResult = new DefaultParseResult(this);
		
		
	}
	
	public void clear()
	{
		myResult.clearNotices();
		for(GutterIconInfo info:myInfos)
		{
			myGutter.removeTrackingIcon(info);
		}

	}

	
	

	@Override
	public ParseResult parse(RSyntaxDocument doc, String style) 
	{
		
		IRPModelElement o = myOperation.getOwner();
		
		
		if(o instanceof IRPClass == false)
		{
			return null;
		}
		
		IRPClass owner = (IRPClass)o;
		
		IRPModelElement oo = owner.getOwner();
		
		if(oo instanceof IRPClass)
		{
			owner = (IRPClass)oo;
		}
		
		Element root = doc.getDefaultRootElement();
		int lineCount = root.getElementCount();
		myResult.clearNotices();
		// Always spell check all lines, for now.
		myResult.setParsedLines(0, lineCount-1);
		
		for(GutterIconInfo info:myInfos)
		{
			myGutter.removeTrackingIcon(info);
		}
		
		List<IRPComment> comments = owner.getNestedElementsByMetaClass("Comment", 0).toList();
		for(IRPComment comment : comments)
		{
			
				List<IRPModelElement> anchors = comment.getAnchoredByMe().toList();
				for(IRPModelElement anchor:anchors)
				{
					if(anchor.equals(myOperation))
					{
						String specs = comment.getSpecification();
						
						String[] parts = specs.split("\\s+");

					        
					        if (parts.length >= 2) {
					           
					            String action = parts[0];
					            int line = Integer.parseInt(parts[1])-1;
					            
					            if(line<=lineCount)
					            {
					            
					            	DefaultParserNotice notice = null;
					            	
					            	String message;
					            	Color messageColor = Color.RED;
					            	
					            	GutterIconInfo info;
					            	
					            	Icon showIcon = myErrorIcon;
					            	
					            	if(comment.getName().startsWith("error"))
					            	{
					            		 message = "Error: " + comment.getDescription();
					            		 
					            	}
					            	else
					            	{
					            		message = "Warning: " + comment.getDescription();
					            		messageColor = Color.YELLOW;
					            		showIcon = myWarningIcon;
					            	}

					            	
									try {
										info = myGutter.addLineTrackingIcon(line,showIcon, message);
										myInfos.add(info);
									} catch (BadLocationException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
					            	
					            	notice = new DefaultParserNotice(this, message, line);
					            	notice.setColor(messageColor);
					            	myResult.addNotice(notice);
					            }
					            
					        
					        }
						
						
						
						
					}
				}
			
		}
		
		
		return myResult;
		
	}

}
