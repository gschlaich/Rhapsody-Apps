package apps;

import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.fife.ui.autocomplete.AbstractCompletionProvider;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.VariableCompletion;

import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPInstance;
import com.telelogic.rhapsody.core.IRPModelElement;
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
		if(myRelation instanceof IRPInstance)
		{
			return false;
		}
		
		return true;
	}
	

	@Override
	public Icon getIcon() {
		Icon ret = new ImageIcon(myRelation.getIconFileName().replace('\\', '/'));
		return ret;
	}



	@SuppressWarnings("unchecked")
	@Override
	public List<IRPClassifier> getNestedClassifiers() {
		return getIRPClassifier().getNestedClassifiers().toList();
	}


	@Override
	public IRPModelElement getElement() {
		return myRelation;
	}


}
