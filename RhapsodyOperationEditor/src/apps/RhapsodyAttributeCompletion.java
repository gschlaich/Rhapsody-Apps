package apps;

import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.fife.ui.autocomplete.AbstractCompletionProvider;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.VariableCompletion;

import com.telelogic.rhapsody.core.IRPAttribute;
import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPVariable;

import RhapsodyUtilities.RhapsodyOperation;

public class RhapsodyAttributeCompletion extends VariableCompletion implements RhapsodyClassifier {

	private IRPAttribute myAttribute;
	private IRPClassifier myClassifier;
	
	public RhapsodyAttributeCompletion(CompletionProvider provider, IRPAttribute aAttribute) {
		super(provider, aAttribute.getName(), aAttribute.getType().getName());
		myAttribute = aAttribute;
		
		setShortDescription(myAttribute.getDescription());
		setDefinedIn(myAttribute.getOwner().getFullPathNameIn());
		
		//add also type to completion
		AbstractCompletionProvider abstractProvider = (AbstractCompletionProvider)provider;
		if((myAttribute.getType()!=null)&&(abstractProvider!=null))
		{
			RhapsodyClassifierCompletion rc = new RhapsodyClassifierCompletion(provider, myAttribute.getType());
			abstractProvider.addCompletion(rc);
			myClassifier = rc.getIRPClassifier();
		}
		else
		{
			myClassifier = myAttribute.getType();
		}
	}
	
	@Override
	public Icon getIcon() {
		return  RhapsodyOperation.getIcon(myAttribute);
	}
	
			
	@Override
	public IRPClassifier getIRPClassifier() {
		return myClassifier;
	}

	@Override
	public boolean isPointer() {
		return (myAttribute.getIsReference()==1);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IRPClassifier> getNestedClassifiers() {
		return getIRPClassifier().getNestedClassifiers().toList();
	}

	@Override
	public IRPModelElement getElement() {	
		return myAttribute;
	}

}