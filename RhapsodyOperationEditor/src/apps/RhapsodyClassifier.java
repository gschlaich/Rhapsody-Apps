package apps;

import java.util.List;

import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPModelElement;

public interface RhapsodyClassifier {
	
	IRPClassifier getIRPClassifier();
	
	IRPModelElement getElement();
	
	List<IRPClassifier> getNestedClassifiers();
	
	boolean isPointer();
	
}
