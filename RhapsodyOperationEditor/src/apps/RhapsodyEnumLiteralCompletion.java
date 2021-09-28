package apps;

import java.util.List;

import javax.swing.ImageIcon;

import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;

import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPEnumerationLiteral;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPType;

import RhapsodyUtilities.RhapsodyOperation;

public class RhapsodyEnumLiteralCompletion extends BasicCompletion implements RhapsodyClassifier {

	IRPType myIRPType;
	IRPEnumerationLiteral myEnumerationLiteral;
	
	private String definedIn;

	
	public RhapsodyEnumLiteralCompletion(CompletionProvider provider, IRPType aIRPType, String aEnumItemName) {
		super(provider, aEnumItemName);
		myIRPType = aIRPType;
		
		setIcon( RhapsodyOperation.getIcon(myIRPType));
		setShortDescription(myIRPType.getName());
		//setSummary(myIRPType.getDescription());
		definedIn = myIRPType.getFullPathNameIn();
	}
	
	public RhapsodyEnumLiteralCompletion(CompletionProvider provider, IRPEnumerationLiteral aEnumerationLiteral) {
		super(provider, aEnumerationLiteral.getName());
		myEnumerationLiteral = aEnumerationLiteral;
		myIRPType = (IRPType)aEnumerationLiteral.getOwner();
		setShortDescription(myIRPType.getName());
		setIcon(new ImageIcon(myEnumerationLiteral.getIconFileName().replace('\\', '/')));
		setSummary(myEnumerationLiteral.getDescription());
		
		definedIn = myIRPType.getOwner().getFullPathNameIn();
		
	}


	@Override
	public IRPClassifier getIRPClassifier(boolean aPointer) {
		if(aPointer)
		{
			return null;
		}
		return myIRPType;
	}

	@Override
	public IRPModelElement getElement() {
		return myEnumerationLiteral;
	}

	@Override
	public List<IRPClassifier> getNestedClassifiers(boolean aPointer) {
		IRPClassifier classifier = getIRPClassifier(aPointer);
		if(classifier==null)
		{
			return null;
		}
		return classifier.getNestedClassifiers().toList();
	}
	

	@Override
	public boolean isPointer() {
		return false;
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
		return true;
	}


}
