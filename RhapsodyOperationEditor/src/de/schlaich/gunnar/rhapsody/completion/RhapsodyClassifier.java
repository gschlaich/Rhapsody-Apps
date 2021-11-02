package de.schlaich.gunnar.rhapsody.completion;

import java.util.List;

import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPModelElement;

public interface RhapsodyClassifier {
	
	IRPClassifier getIRPClassifier(boolean aPointer);
	
	IRPModelElement getElement();
	
	List<IRPClassifier> getNestedClassifiers(boolean aPointer);
	
	boolean isPointer();
	
	boolean isValue();
	
}
