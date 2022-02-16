package de.schlaich.gunnar.rhapsody.completion;

import java.util.List;

import org.fife.ui.autocomplete.AbstractCompletionProvider;
import org.fife.ui.autocomplete.VariableCompletion;

import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPModelElement;

public class RhapsodyLocalVariableCompletion extends VariableCompletion implements RhapsodyClassifier {

	
	IRPClassifier myClassifier;
	IRPClassifier myClassifierPointer;
	boolean myIsPointer = false;
	boolean myIsValue = true;
	
	public RhapsodyLocalVariableCompletion(AbstractCompletionProvider provider,String aName,IRPClassifier aClassifier, boolean aIsPointer ) {
		super(provider, aName, aClassifier.getName());
		myClassifier = aClassifier;
		myClassifierPointer = aClassifier;
		setDefinedIn("local");
		

		
		
		RhapsodyClassifierCompletion c = new RhapsodyClassifierCompletion(provider, myClassifier,false,false);
		
		if(aIsPointer==false)
		{
			myIsPointer = c.isPointer();
			myIsValue = c.isValue();
	
			myClassifier = c.getIRPClassifier(false);
			myClassifierPointer = c.getIRPClassifier(true);
		}
		else
		{
			myIsPointer = true;
			myIsValue = false;
			myClassifier = null;
			myClassifierPointer = c.getIRPClassifier(false);
		}
		
			
	}
	
	@Override
	public IRPClassifier getIRPClassifier(boolean aPointer) 
	{
		if(aPointer)
		{
			return myClassifierPointer;
		}
		return myClassifier;
	}

	@Override
	public boolean isPointer() {
		// TODO Auto-generated method stub
		
		return myIsPointer;
	}
	
	@SuppressWarnings("unchecked")
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
	public IRPModelElement getElement() 
	{	
		if(myIsPointer)
		{
			return myClassifierPointer;
		}
		return myClassifier;
	}

	

	@Override
	public boolean isValue() {
		
		return myIsValue;
	}


}
