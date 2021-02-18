package apps;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;

import com.telelogic.rhapsody.core.IRPClassifier;

public class RhapsodyClassifierCompletion extends BasicCompletion implements RhapsodyClassifier {

	
	IRPClassifier myClassifier;
	
	public RhapsodyClassifierCompletion( CompletionProvider aProvider, IRPClassifier aClassifier) {
		super(aProvider, aClassifier.getName(), aClassifier.getDescription());
		myClassifier = aClassifier;
		
	}

	@Override
	public IRPClassifier getIRPClassifier() {
		
		return myClassifier;
	}
	
	
	
	@Override
	public Icon getIcon() {
		
		Icon ret = new ImageIcon(myClassifier.getIconFileName().replace('\\', '/'));
		
		return ret;
	}

	@Override
	public boolean isReference() {
		return false;
	}

}