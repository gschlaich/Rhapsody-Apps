package apps;

import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.fife.ui.autocomplete.AbstractCompletionProvider;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;

import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPGeneralization;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPType;

import RhapsodyUtilities.ASTHelper;

public class RhapsodyClassifierCompletion extends BasicCompletion implements RhapsodyClassifier {

	
	IRPClassifier myClassifier;
	
	public RhapsodyClassifierCompletion( CompletionProvider aProvider, IRPClassifier aClassifier) {
		super(aProvider, aClassifier.getName());
		myClassifier = aClassifier;	
		setSummary( aClassifier.getDescription());
		setIcon(new ImageIcon(myClassifier.getIconFileName().replace('\\', '/')));
		
		
		AbstractCompletionProvider abstractProvider = (AbstractCompletionProvider)aProvider;
		if(abstractProvider!=null)
		{
			List<IRPGeneralization> gs = myClassifier.getGeneralizations().toList();
			for(IRPGeneralization g : gs)
			{
				RhapsodyClassifierCompletion rcc = new RhapsodyClassifierCompletion(aProvider, g.getBaseClass());
				abstractProvider.addCompletion(rcc);
			}
		}
		
		
		if(myClassifier instanceof IRPType)
		{
			
			IRPType rType = (IRPType)myClassifier;
			if(rType.isKindLanguage()==1)
			{
				if(rType.isEnum()==1)
				{
					String[] enumNames = ASTHelper.parseLanguageEnumType(rType);
					for(String enumName : enumNames)
					{
						BasicCompletion ebc = new BasicCompletion(abstractProvider, enumName);
						ebc.setSummary("enum from " + rType.getFullPathNameIn());
						ebc.setIcon(this.getIcon());
						
						abstractProvider.addCompletion(ebc);
					}
				}
			}
	
		}
		
		
	}

	@Override
	public IRPClassifier getIRPClassifier() {
		
		return myClassifier;
	}
	
	@Override
	public String toString() {
		return this.getInputText();
	}
	
	@Override
	public boolean isReference() {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IRPClassifier> getNestedClassifiers() {
		return getIRPClassifier().getNestedClassifiers().toList();
	}

	@Override
	public IRPModelElement getElement() {
		return myClassifier;
	}


}