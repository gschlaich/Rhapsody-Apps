package de.schlaich.gunnar.rhapsody.operationeditor;

import org.eclipse.cdt.core.formatter.CodeFormatter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

public class EditorCodeFormatter {
		
	//private CodeFormatter myCodeFormatter;
	
	public EditorCodeFormatter() {
		//myCodeFormatter = org.eclipse.cdt.core.ToolFactory.createCodeFormatter(null);
	}
	
	public String format(String aSource) 
	{

		// int kind, String source, int offset, int length, int indentationLevel, String lineSeparator
		CodeFormatter myCodeFormatter = org.eclipse.cdt.core.ToolFactory.createCodeFormatter(null);
		
		TextEdit edit = myCodeFormatter.format(0, aSource, 0, aSource.length(), 0, null);
		
		IDocument document= new Document(aSource);
		try {
			edit.apply(document);
		} 
		catch (MalformedTreeException e) {
			;
		} 
		catch (BadLocationException e) {
			;
		}
		
		String formattedSource = document.get();
		return formattedSource;
	}
		

}
