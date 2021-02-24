package apps;

import java.util.List;

import com.telelogic.rhapsody.core.IRPClassifier;

public interface RhapsodyClassifier {
	
	IRPClassifier getIRPClassifier();
	
	List<IRPClassifier> getNestedClassifiers();
	
	boolean isReference();
	
}
