package de.schlaich.gunnar.rhapsody.operationeditor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.ToolFactory;
import org.eclipse.cdt.core.formatter.CodeFormatter;
import org.eclipse.cdt.core.formatter.DefaultCodeFormatterOptions;
import org.eclipse.cdt.internal.formatter.CCodeFormatter;
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
	
	public String formatEclipse(String aSource) 
	{

		// int kind, String source, int offset, int length, int indentationLevel, String lineSeparator
		String formattedSource = aSource;
		try
		{
			//get default options...
			
			DefaultCodeFormatterOptions defaultOptions = new DefaultCodeFormatterOptions(null);
			
			Map<String, String> options = defaultOptions.getMap();
			
			//HashMap<String, String> options = CCorePlugin.getOptions();
			final CodeFormatter myCodeFormatter = ToolFactory.createDefaultCodeFormatter(options);
			
			
			//CodeFormatter myCodeFormatter =  new CCodeFormatter();
			//CodeFormatter myCodeFormatter = org.eclipse.cdt.core.ToolFactory.createCodeFormatter(null);
			
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
			
			formattedSource = document.get();
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
		return formattedSource;
	}
	
	public String format(String aSource) {
        Process p;
        String ret = "";
		try
		{
			p = Runtime.getRuntime().exec("clang-format --style=Microsoft");
	        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

           BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
           
           BufferedWriter stdOut = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
           
           stdOut.write(aSource);
           stdOut.close();
           String s;
          
           while ((s = stdInput.readLine()) != null) {
               ret = ret.concat(s).concat("\n");
           }
           ret = ret.trim();
           p.destroy();
	   		
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			ret = aSource;
		}
		return ret;
	}
		

}
