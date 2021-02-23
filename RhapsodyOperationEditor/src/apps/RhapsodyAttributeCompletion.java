package apps;

import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.fife.ui.autocomplete.AbstractCompletionProvider;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.VariableCompletion;

import com.telelogic.rhapsody.core.IRPAttribute;
import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPVariable;

public class RhapsodyAttributeCompletion extends VariableCompletion implements RhapsodyClassifier {

	private IRPAttribute myAttribute;
	
	public RhapsodyAttributeCompletion(CompletionProvider provider, IRPAttribute aAttribute) {
		super(provider, aAttribute.getName(), aAttribute.getType().getName());
		myAttribute = aAttribute;
		
		setShortDescription(myAttribute.getDescription());
		
		//add also type to completion
		AbstractCompletionProvider abstractProvider = (AbstractCompletionProvider)provider;
		if((getIRPClassifier()!=null)&&(abstractProvider!=null))
		{
			RhapsodyClassifierCompletion rc = new RhapsodyClassifierCompletion(provider, getIRPClassifier());
			abstractProvider.addCompletion(rc);
		}
	}
	
	@Override
	public Icon getIcon() {
		Icon ret = new ImageIcon(myAttribute.getIconFileName().replace("\\", "/"));
		return ret;
	}
	
			
	@Override
	public IRPClassifier getIRPClassifier() {
		return myAttribute.getType();
	}

	@Override
	public boolean isReference() {
		return (myAttribute.getIsReference()==1);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IRPClassifier> getNestedClassifiers() {
		return getIRPClassifier().getNestedClassifiers().toList();
	}

}