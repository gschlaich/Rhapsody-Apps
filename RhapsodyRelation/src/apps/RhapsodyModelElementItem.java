package apps;

import java.util.Comparator;

import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPModelElement;

public class RhapsodyModelElementItem implements Comparable<RhapsodyModelElementItem>
{
	
	private IRPModelElement myModelElement = null; 
	private String mySearchString;
	
	public RhapsodyModelElementItem(IRPModelElement aModelElement, String aSearchString) {
		myModelElement = aModelElement;
		mySearchString = aSearchString;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return mySearchString;
	}
	
	@Override
	public int compareTo(RhapsodyModelElementItem arg0) {
		return this.toString().compareTo(arg0.toString());
	}
	
	public IRPModelElement getModelElement()
	{
		return myModelElement;
	}
	


}
