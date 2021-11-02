package de.schlaich.gunnar.rhapsody.operationeditor;

import java.awt.Point;
import java.util.List;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;

import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;

public class SourceCompletionProvider extends DefaultCompletionProvider {
	
	
	public SourceCompletionProvider() {
		
		super();
	}
	
	public SourceCompletionProvider(String[] words)
	{
		super(words);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Completion> getCompletionsAt(JTextComponent tc, Point p) {
		
		return super.getCompletionsAt(tc, p);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<Completion> getCompletionsImpl(JTextComponent comp) {
		
		// importing completions
		String text = getAlreadyEnteredText(comp);
		if(text.length()==0)
		{
			text = getTextForImport(comp);
			if(text.length() == 0 )
			{
				return super.getCompletionsImpl(comp);
			}
		}
		
		if (text.indexOf("->")==-1) 
		{
			return super.getCompletionsImpl(comp);
		}
		
		
		//System.out.println("import completion for " + text );
		
		return null;
		
	}

	public String getTextForImport(JTextComponent comp) {

		Document doc = comp.getDocument();

		int dot = comp.getCaretPosition();
		Element root = doc.getDefaultRootElement();
		int index = root.getElementIndex(dot);
		Element elem = root.getElement(index);
		int start = elem.getStartOffset();
		int len = dot-start;
		try {
			doc.getText(start, len, seg);
		} catch (BadLocationException ble) {
			ble.printStackTrace();
			return EMPTY_STRING;
		}
		
		
		return len==0 ? EMPTY_STRING : new String(seg.array,start,len);

	}
	
	
	

}
