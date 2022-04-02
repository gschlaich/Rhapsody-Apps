package de.schlaich.gunnar.serializable.completion;

import java.io.Serializable;

import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;

public class SerializableBasicCompletion implements Serializable {
	
	private String replacementText;
	private String shortDesc;
	private String summary;
	
	private int relevance;
	
	public SerializableBasicCompletion(String replacementText, String shortDesc, String summary) {
		this.replacementText = replacementText;
		this.shortDesc = shortDesc;
		this.summary = summary;
		
	}
	
	BasicCompletion getCompletion(CompletionProvider provider) {
		BasicCompletion ret = new BasicCompletion(provider, replacementText, shortDesc, summary);
		ret.setRelevance(relevance);
		return ret;
	}
	
}
