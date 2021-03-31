package apps;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;

import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPCollection;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPPackage;

public class RhapsodyNamespaceCompletion extends BasicCompletion implements RhapsodyClassifier
{
	
	IRPPackage myPackage;
	
	public RhapsodyNamespaceCompletion(CompletionProvider aProvider ,IRPPackage aPackage) 
	{
		super(aProvider, aPackage.getNamespace());
		setSummary(aPackage.getDescriptionPlainText());
		
		myPackage = aPackage;
	}

	public List<IRPClassifier> getClassifiers()
	{
		return getClassifiers(myPackage);
	}
	
	private List<IRPClassifier> getClassifiers(IRPPackage aPackage)
	{
		List<IRPClassifier> ret = new ArrayList<IRPClassifier>();
		
		List<IRPModelElement> elements = aPackage.getNestedClassifiers().toList();
		for(IRPModelElement element:elements)
		{
			if(element instanceof IRPClassifier)
			{
				ret.add((IRPClassifier)element);
			}
			else if(element instanceof IRPPackage)
			{
				IRPPackage p = (IRPPackage)element;
				if(p.getNamespace().endsWith(myPackage.getNamespace()))
				{
					ret.addAll(getClassifiers(p));
				}
			}
		}
		return ret;
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
	
	private List<IRPClassifier> getNestedClassifiers(IRPPackage aPackage) {
		List<?> classifiers = aPackage.getNestedClassifiers().toList();
		List<IRPClassifier> ret = new ArrayList<IRPClassifier>();
		for(Object co:classifiers)
		{
			if(co instanceof IRPClassifier)
			{
				ret.add((IRPClassifier)co);
			}
			else if(co instanceof IRPPackage)
			{
				ret.addAll(getNestedClassifiers((IRPPackage)co));
			}
		}	
		return ret;
	}
	
	@Override
	public List<IRPClassifier> getNestedClassifiers() {
		return getNestedClassifiers(myPackage);
		
	}

	@Override
	public boolean isPointer() {
		
		return false;
	}

	@Override
	public IRPModelElement getElement() {
		
		return myPackage;
	}

}
