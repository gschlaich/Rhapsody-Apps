package apps;

import org.fife.ui.autocomplete.AbstractCompletionProvider;
import org.fife.ui.autocomplete.VariableCompletion;

import com.telelogic.rhapsody.core.IRPClassifier;

public class RhapsodyLocalVariableCompletion extends VariableCompletion implements RhapsodyClassifier {

	
	IRPClassifier myClassifier;
	boolean myIsReference;
	
	public RhapsodyLocalVariableCompletion(AbstractCompletionProvider provider,String aName,IRPClassifier aClassifier, boolean aIsReference ) {
		super(provider, aName, aClassifier.getName());
		myClassifier = aClassifier;
		myIsReference = aIsReference;
	}
	
	@Override
	public IRPClassifier getIRPClassifier() {
		
		return myClassifier;
	}

	@Override
	public boolean isReference() {
		// TODO Auto-generated method stub
		return myIsReference;
	}
	

}