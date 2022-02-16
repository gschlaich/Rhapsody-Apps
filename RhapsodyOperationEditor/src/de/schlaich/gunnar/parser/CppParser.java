package de.schlaich.gunnar.parser;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTNodeLocation;
import org.eclipse.cdt.core.dom.ast.IASTProblem;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.parser.AbstractParser;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParseResult;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParserNotice;
import org.fife.ui.rsyntaxtextarea.parser.ExtendedHyperlinkListener;
import org.fife.ui.rsyntaxtextarea.parser.ParseResult;
import org.fife.ui.rtextarea.Gutter;
import org.fife.ui.rtextarea.GutterIconInfo;

import de.schlaich.gunnar.rhapsody.completionprovider.ClassifierCompletionProvider;
import de.schlaich.gunnar.rhapsody.completionprovider.NamespaceCompletionProvider;
import de.schlaich.gunnar.rhapsody.utilities.ASTHelper;
import de.schlaich.gunnar.rhapsody.utilities.RhapsodyOperation;

public class CppParser extends AbstractParser implements ExtendedHyperlinkListener
{

	private DefaultParseResult myResult = null;
	private ClassifierCompletionProvider myClassifierCompletionProvider;
	private Gutter myGutter = null;
	private Icon myErrorIcon = null;
	private List<GutterIconInfo> myInfos = null;
	
	
	public CppParser(ClassifierCompletionProvider aProvider, Gutter aGutter)
	{
		myResult = new DefaultParseResult(this);
		myClassifierCompletionProvider = aProvider;
		myGutter = aGutter;
		myErrorIcon = RhapsodyOperation.getIcon("RhapsodyIcons_110.gif");
		myInfos = new ArrayList<GutterIconInfo>();
		
	}
	
	@Override
	public ParseResult parse(RSyntaxDocument aDoc, String aStyle) 
	{
		
		Element root = aDoc.getDefaultRootElement();
		int lineCount = root.getElementCount();
		myResult.clearNotices();
		// Always spell check all lines, for now.
		myResult.setParsedLines(0, lineCount-1);
		
		for(GutterIconInfo info:myInfos)
		{
			myGutter.removeTrackingIcon(info);
		}
		
		
		String text;
		try 
		{
			text = aDoc.getText(0, aDoc.getLength());
			IASTTranslationUnit unit = ASTHelper.getTranslationUnit(text, null);	
			List<IASTProblem> problems = ASTHelper.getProblems(unit);
			
			for(IASTProblem problem : problems)
			{
				
				
				String message = "C++ Syntax ";
				
				if(problem.isError())
				{
					message = message + "Error: ";
				}
				else
				{
					message = message + "Warning: ";
				}
				
				int id = problem.getID();
				message = message + Integer.toString(id);
				
				int length = problem.getSourceEnd() - problem.getSourceStart();
				int pos = problem.getSourceStart()-ASTHelper.Prolog.length();
				int line = problem.getSourceLineNumber()-2;
				
				System.out.println(line+ " " + pos + " " + length);
				
				GutterIconInfo info = myGutter.addLineTrackingIcon(line,myErrorIcon, message);
				myInfos.add(info);
				
				DefaultParserNotice notice = null;
				
				if(line>lineCount)
				{
					line = lineCount;
				}
				
				if(length<=0)
				{
					notice = new DefaultParserNotice(this, message, line);
				}
				else
				{
					notice = new DefaultParserNotice(this, message, line, pos, length);
				}

				
				myResult.addNotice(notice);
				
			}
			
			new ArrayList<String>();
			List<IASTNamedTypeSpecifier> namedSpecifiers = ASTHelper.getNamedTypeSpecifiers(unit);
			for(IASTNamedTypeSpecifier namedSpecifier:namedSpecifiers)
			{
				DefaultParserNotice notice = null;
				IASTName astName = namedSpecifier.getName().getLastName();
				String typeName = astName.toString();
				if(myClassifierCompletionProvider.getFirstCompletion(typeName)==null)
				{
					if(NamespaceCompletionProvider.GetCompletion(typeName)==null)
					{
						//unknown type
						
						int length = 0;
						int pos = 0;
						
						IASTNodeLocation[] locations = astName.getNodeLocations();
						
						if(locations.length>0)
						{
							length = locations[0].getNodeLength();
							pos = locations[0].getNodeOffset()-ASTHelper.Prolog.length();
						}
						
						
						
						System.out.println("unknown type: " + typeName);
						notice = new DefaultParserNotice(this, "unknown type: "+typeName, 0, pos, length);
						notice.setColor(Color.ORANGE);
						
						myResult.addNotice(notice);
					}
				}
				
			}
			
			
			System.out.println("End problems");
			
		} 
		catch (BadLocationException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
		return myResult;
		
	}

	@Override
	public void linkClicked(RSyntaxTextArea arg0, HyperlinkEvent arg1) {
		// TODO Auto-generated method stub
		
	}
	

}
