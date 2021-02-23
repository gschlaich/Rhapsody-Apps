package apps;

import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;

import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPCollection;
import com.telelogic.rhapsody.core.IRPPackage;

public class RhapsodyNamespaceCompletion extends BasicCompletion implements RhapsodyClassifier
{
	
	IRPPackage myPackage;
	
	public RhapsodyNamespaceCompletion(CompletionProvider aProvider ,IRPPackage aPackage) 
	{
		super(aProvider, aPackage.getNamespace(), aPackage.getDescription() );
		myPackage = aPackage;
	}

	public IRPCollection getClassifiers()
	{
		return myPackage.getClasses();
	}

	@Override
	public IRPClassifier getIRPClassifier() {
		return null;
	}
	
	@Override
	public Icon getIcon() {
		
		Icon ret = new ImageIcon(myPackage.getIconFileName().replace('\\', '/'));
		
		return ret;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IRPClassifier> getNestedClassifiers() {
		return myPackage.getNestedClassifiers().toList();
	}

	@Override
	public boolean isReference() {
		// TODO Auto-generated method stub
		return false;
	}

}
