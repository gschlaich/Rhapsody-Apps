package apps;

import java.util.Comparator;

import com.telelogic.rhapsody.core.IRPClassifier;

public class RhapsodyClassifierItem implements Comparable<RhapsodyClassifierItem>
{
	
	private IRPClassifier myIRPClassifier = null; 
	
	public RhapsodyClassifierItem(IRPClassifier aClassifier) {
		myIRPClassifier = aClassifier;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return myIRPClassifier.getName();
	}
	
	@Override
	public int compareTo(RhapsodyClassifierItem arg0) {
		return this.toString().compareTo(arg0.toString());
	}
	


}
