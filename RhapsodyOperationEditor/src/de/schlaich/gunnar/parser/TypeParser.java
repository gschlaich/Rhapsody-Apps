package de.schlaich.gunnar.parser;

import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.parser.AbstractParser;
import org.fife.ui.rsyntaxtextarea.parser.ParseResult;

public class TypeParser extends AbstractParser {
	
	
	AutoCompletion myAutocompletion;
	
	public TypeParser( AutoCompletion aAutoCompletion)
	{
		myAutocompletion = aAutoCompletion;
		
	}

	@Override
	public ParseResult parse(RSyntaxDocument aDoc, String aStyle) {
		
		
		
		for(Token t:aDoc)
		{
			if(t.getType()==Token.IDENTIFIER)
			{
				System.out.println("Token text: " + t.getLexeme() + " Type: " + t.getType());
				
				if(t.getLexeme().equals("CMsgEBYStitchingStopped"))
				{
					t.setType(Token.DATA_TYPE);
					
				}
			}
			
		}
		
		
		
		
		
		
		
		
		return null;
	}

}
