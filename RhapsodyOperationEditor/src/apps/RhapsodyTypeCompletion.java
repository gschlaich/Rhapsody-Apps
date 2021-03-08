package apps;

import java.util.List;

import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;

import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPType;

import RhapsodyUtilities.ASTHelper;

public class RhapsodyTypeCompletion extends BasicCompletion implements RhapsodyClassifier {

	IRPType myType;
	
	public RhapsodyTypeCompletion(CompletionProvider provider, IRPType aType) {
		super(provider, aType.getName(), aType.getDescription());
		myType = aType;
		
		if(myType.isKindLanguage()==1)
		{
			ASTHelper.parseLanguageEnumType(myType);
		}
		
	}
	

	@Override
	public IRPClassifier getIRPClassifier() {
		
		return myType.getTypedefBaseType();
	}

	@Override
	public IRPModelElement getElement() {
		
		return myType;
	}

	@Override
	public List<IRPClassifier> getNestedClassifiers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isReference() {
		return false;
	}

}
