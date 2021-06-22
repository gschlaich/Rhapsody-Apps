package apps;

import java.util.List;

import javax.swing.ImageIcon;

import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;

import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPEnumerationLiteral;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPType;

public class RhapsodyEnumLiteralCompletion extends BasicCompletion implements RhapsodyClassifier {

	IRPType myIRPType;
	IRPEnumerationLiteral myEnumerationLiteral;

	
	public RhapsodyEnumLiteralCompletion(CompletionProvider provider, IRPType aIRPType, String aEnumItemName) {
		super(provider, aEnumItemName);
		myIRPType = aIRPType;
		
		setIcon(new ImageIcon(myIRPType.getIconFileName().replace('\\', '/')));
		setShortDescription(myIRPType.getName());
		//setSummary(myIRPType.getDescription());
	}
	
	public RhapsodyEnumLiteralCompletion(CompletionProvider provider, IRPEnumerationLiteral aEnumerationLiteral) {
		super(provider, aEnumerationLiteral.getName());
		myEnumerationLiteral = aEnumerationLiteral;
		myIRPType = (IRPType)aEnumerationLiteral.getOwner();
		setShortDescription(myIRPType.getName());
		setIcon(new ImageIcon(myEnumerationLiteral.getIconFileName().replace('\\', '/')));
		setSummary(myEnumerationLiteral.getDescription());
		
	}


	@Override
	public IRPClassifier getIRPClassifier() {
		return myIRPType;
	}

	@Override
	public IRPModelElement getElement() {
		return myEnumerationLiteral;
	}

	@Override
	public List<IRPClassifier> getNestedClassifiers() {
		if(myIRPType==null)
		{
			return null;
		}
		return myIRPType.getNestedElements().toList();
	}

	@Override
	public boolean isPointer() {
		return false;
	}

}
