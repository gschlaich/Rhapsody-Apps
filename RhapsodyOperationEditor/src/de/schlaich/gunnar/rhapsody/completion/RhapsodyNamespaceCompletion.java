package de.schlaich.gunnar.rhapsody.completion;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;

import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPPackage;

import de.schlaich.gunnar.rhapsody.utilities.RhapsodyOperation;

public class RhapsodyNamespaceCompletion extends BasicCompletion implements RhapsodyClassifier 
{
	
	IRPPackage myPackage;
	private String definedIn;
	
	public RhapsodyNamespaceCompletion(CompletionProvider aProvider ,IRPPackage aPackage) 
	{
		super(aProvider, aPackage.getNamespace());
		
		setSummary( aPackage.getDescription());
		setShortDescription(aPackage.getDescription());
		
		
		myPackage = aPackage;
		
		definedIn = myPackage.getOwner().getFullPathNameIn();
	}

	public List<IRPClassifier> getClassifiers()
	{
		return getClassifiers(myPackage);
	}
	
	private List<IRPClassifier> getClassifiers(IRPPackage aPackage)
	{
		List<IRPClassifier> ret = new ArrayList<IRPClassifier>();
		
		@SuppressWarnings("unchecked")
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
	public IRPClassifier getIRPClassifier(boolean aPointer) {
			return null;
	}
	
	@Override
	public Icon getIcon() {		
		return RhapsodyOperation.getIcon(myPackage);
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
	public List<IRPClassifier> getNestedClassifiers(boolean aPointer) {
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
	
	public IRPPackage getPackage()
	{
		return myPackage;
	}
	
	/**
	 * Returns where this variable is defined.
	 *
	 * @return Where this variable is defined.
	 * @see #setDefinedIn(String)
	 */
	public String getDefinedIn() {
		return definedIn;
	}
	
	/**
	 * Returns the name of this variable.
	 *
	 * @return The name.
	 */
	public String getName() {
		return getReplacementText();
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getSummary() {
		StringBuilder sb = new StringBuilder();
		possiblyAddDescription(sb);
		possiblyAddDefinedIn(sb);
		return sb.toString();
	}


	
	

	/**
	 * Adds some HTML describing where this package is defined, if this
	 * information is known.
	 *
	 * @param sb The buffer to append to.
	 */
	protected void possiblyAddDefinedIn(StringBuilder sb) {
		if (definedIn!=null) {
			sb.append("<hr>Defined in:"); // TODO: Localize me
			sb.append(" <em>").append(definedIn).append("</em>");
		}
	}


	/**
	 * Adds the description text as HTML to a buffer, if a description is
	 * defined.
	 *
	 * @param sb The buffer to append to.
	 * @return Whether there was a description to add.
	 */
	protected boolean possiblyAddDescription(StringBuilder sb) {
		if (getShortDescription()!=null) {
			sb.append("<hr><br>");
			sb.append(getShortDescription());
			sb.append("<br><br><br>");
			return true;
		}
		return false;
	}

	@Override
	public boolean isValue() {
		// TODO Auto-generated method stub
		return true;
	}


}
