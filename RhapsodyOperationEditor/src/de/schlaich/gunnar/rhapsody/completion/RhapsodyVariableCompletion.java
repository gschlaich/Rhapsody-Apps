package de.schlaich.gunnar.rhapsody.completion;

import java.util.List;

import javax.swing.Icon;

import com.telelogic.rhapsody.core.IRPAttribute;
import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPVariable;

import de.schlaich.gunnar.rhapsody.utilities.RhapsodyOperation;

import org.fife.ui.autocomplete.AbstractCompletionProvider;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.VariableCompletion;


public class RhapsodyVariableCompletion extends VariableCompletion implements RhapsodyClassifier {

	
	private IRPVariable myVariable = null;
	private IRPClassifier myClassifier=null;
	private IRPClassifier myClassifierPointer=null;
	private boolean myIsPointer = false;
	private boolean myIsValue = true;
	
	public RhapsodyVariableCompletion(CompletionProvider provider, IRPVariable aVariable) {
		super(provider, aVariable.getName(), aVariable.getType().getName());	
		init(provider, aVariable, true);
	}

	public RhapsodyVariableCompletion(CompletionProvider provider, IRPVariable aVariable, boolean addType) {
		super(provider, aVariable.getName(), aVariable.getType().getName());
		init(provider, aVariable, addType);
	}
	
	
	private void init(CompletionProvider provider, IRPVariable aVariable, boolean addType)
	{
		myVariable = aVariable;
		setShortDescription(myVariable.getDescription());
		setDefinedIn(myVariable.getOwner().getFullPathNameIn());
		
		
		//add also type to completion
		AbstractCompletionProvider abstractProvider = (AbstractCompletionProvider)provider;
		if((addType==true)&&(myVariable.getType()!=null)&&(abstractProvider!=null))
		{
			RhapsodyClassifierCompletion rc = new RhapsodyClassifierCompletion(provider, myVariable.getType(),false,false);
			abstractProvider.addCompletion(rc);
			myClassifier = rc.getIRPClassifier(false);
			myClassifierPointer = rc.getIRPClassifier(true);
			myIsPointer = rc.isPointer();
			myIsValue = rc.isValue();
		}
		else
		{
			myClassifier = myVariable.getType();
		}

	}

	@Override
	public Icon getIcon() {
		return  RhapsodyOperation.getIcon(myVariable);
	}
	
			
	@Override
	public IRPClassifier getIRPClassifier(boolean aPointer) {
		if(aPointer)
		{
			return myClassifierPointer;
		}
		return myClassifier;
	}

	@Override
	public boolean isPointer() 
	{
		return myIsPointer;
	}


	@Override
	public IRPModelElement getElement() {	
		return myVariable;
	}

	@Override
	public boolean isValue() {
		return myIsValue;
	}
	
	@SuppressWarnings("unchecked")
	@Override	
	public List<IRPClassifier> getNestedClassifiers(boolean aPointer) 
	{
		IRPClassifier classifier = getIRPClassifier(aPointer);
		if(classifier==null)
		{
			return null;
		}
		return classifier.getNestedClassifiers().toList();
	}


}
