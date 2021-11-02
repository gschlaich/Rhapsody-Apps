package de.schlaich.gunnar.parser;

import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.modes.CPlusPlusTokenMaker;

import de.schlaich.gunnar.rhapsody.completion.RhapsodyArgumentCompletion;
import de.schlaich.gunnar.rhapsody.completion.RhapsodyAttributeCompletion;
import de.schlaich.gunnar.rhapsody.completion.RhapsodyClassifierCompletion;
import de.schlaich.gunnar.rhapsody.completion.RhapsodyLocalVariableCompletion;
import de.schlaich.gunnar.rhapsody.completion.RhapsodyNamespaceCompletion;
import de.schlaich.gunnar.rhapsody.completion.RhapsodyOperationCompletion;
import de.schlaich.gunnar.rhapsody.completion.RhapsodyRelationCompletion;
import de.schlaich.gunnar.rhapsody.completion.RhapsodyTypeCompletion;
import de.schlaich.gunnar.rhapsody.completionprovider.ClassifierCompletionProvider;
import de.schlaich.gunnar.rhapsody.operationeditor.StartAutoCompletion;

public class RhapsodyTokenMaker extends CPlusPlusTokenMaker 
{
	
	private ClassifierCompletionProvider myClassifierCompletionProvider;
	
	public RhapsodyTokenMaker()
	{
		super();
	}
	
	
	@Override
	public void addToken(char[] array, int start, int end, int tokenType, int startOffset) {
		
		if(myClassifierCompletionProvider==null)
		{
			myClassifierCompletionProvider = StartAutoCompletion.GetClassifierCompletionProvider();
		}
		
		
		if(tokenType==Token.IDENTIFIER)
		{
			//check if this is a type...
			if(end-start>1)
			{
				String text = new String(array,start,(end-start)+1);

				if(text.equals("nullptr"))
				{
					tokenType=Token.RESERVED_WORD;
				}
				
				else if(myClassifierCompletionProvider!=null)
				{
					Completion c = myClassifierCompletionProvider.getFirstCompletion(text);
					
					if(c!=null)
					{
						if(c instanceof RhapsodyClassifierCompletion)
						{
							tokenType = Token.DATA_TYPE;
						}
						else if(c instanceof RhapsodyTypeCompletion)
						{
							tokenType = Token.DATA_TYPE;
						}
						else if(c instanceof RhapsodyOperationCompletion)
						{
							tokenType = Token.FUNCTION;
							if(start>2)
							{
								if(array[start-1]=='.')
								{
									tokenType = Token.IDENTIFIER;
								}
								else if(array[start-1]=='>')
								{
									if(array[start-2]=='-')
									{
										tokenType = Token.IDENTIFIER;
									}
								}
								else if((array[start-1]==':')&&(array[start-2]==':'))
								{
									tokenType = Token.IDENTIFIER;
								}
															
							}
						}
						else if(c instanceof RhapsodyAttributeCompletion)
						{
							tokenType = Token.VARIABLE;
						}
						else if(c instanceof RhapsodyArgumentCompletion)
						{
							tokenType = Token.VARIABLE;
						}
						else if(c instanceof RhapsodyLocalVariableCompletion)
						{
							tokenType = Token.VARIABLE;
						}
						else if(c instanceof RhapsodyRelationCompletion)
						{
							tokenType = Token.VARIABLE;
						}
						else if(c instanceof RhapsodyNamespaceCompletion)
						{
							tokenType = Token.DATA_TYPE;
						}
						else if(c instanceof BasicCompletion)
						{
							tokenType = Token.RESERVED_WORD;
						}
					}
									
				}
				
			}
			
			
			
			//System.out.println(text);
		}
		super.addToken(array, start,end, tokenType, startOffset);
		
	}

	
	
	

}
