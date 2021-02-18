package apps;

import org.fife.ui.autocomplete.AbstractCompletionProvider;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.VariableCompletion;

import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPRelation;

public class RhapsodyRelationCompletion extends VariableCompletion implements RhapsodyClassifier {

	private IRPRelation myRelation;
	public RhapsodyRelationCompletion(CompletionProvider provider, IRPRelation aRelation) {
		super(provider, aRelation.getName(), aRelation.getOtherClass().getName());
		myRelation = aRelation;
		
		
		//add also type to completion
		AbstractCompletionProvider abstractProvider = (AbstractCompletionProvider)provider;
		if((getIRPClassifier()!=null)&&(abstractProvider!=null))
		{
			RhapsodyClassifierCompletion rc = new RhapsodyClassifierCompletion(provider, getIRPClassifier());
			abstractProvider.addCompletion(rc);
		}
		
		
	}
	
	
	@Override
	public String getType() {
		//TODO check if relation is Vector or List...
		
		return getIRPClassifier().getName();	
	}
	

	@Override
	public IRPClassifier getIRPClassifier() {
		return myRelation.getOtherClass();
		
	}


	@Override
	public boolean isReference() {
		return true;
	}

}
