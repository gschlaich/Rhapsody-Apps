package apps;

import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.fife.ui.autocomplete.AbstractCompletionProvider;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.VariableCompletion;

import com.telelogic.rhapsody.core.IRPArgument;
import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPType;

import RhapsodyUtilities.RhapsodyOperation;

public class RhapsodyArgumentCompletion extends VariableCompletion implements RhapsodyClassifier {
	
	IRPArgument myArgument;
	IRPClassifier myClassifier;
	boolean myIsPointer = false;
	
	public RhapsodyArgumentCompletion(CompletionProvider provider, IRPArgument aArgument) {
		super(provider, aArgument.getName(), RhapsodyOperation.getArgumentType(aArgument));
		myArgument = aArgument;
		
		setSummary(myArgument.getDescription());
		
		setIcon( RhapsodyOperation.getIcon(myArgument));
		
		setDefinedIn(aArgument.getOwner().getFullPathNameIn());
		
		//add also type to completion
		AbstractCompletionProvider abstractProvider = (AbstractCompletionProvider)provider;
		
		String direction = myArgument.getArgumentDirection();
		String ReturnPattern = myArgument.getPropertyValue("CPP_CG.Class."+direction);
		if(getIRPClassifier() instanceof IRPType)
		{
			ReturnPattern = myArgument.getPropertyValue("CPP_CG.Type."+direction);
		}
		
		myIsPointer = ReturnPattern.endsWith("*");
		
		
		
		if((myArgument.getType()!=null)&&(abstractProvider!=null))
		{
			RhapsodyClassifierCompletion rc = new RhapsodyClassifierCompletion(provider, myArgument.getType());
			abstractProvider.addCompletion(rc);
			myClassifier = rc.getIRPClassifier();
			myIsPointer = rc.isPointer();
		}
		
	}
	
	

	@Override
	public IRPClassifier getIRPClassifier() {
		return myClassifier;
	}

	@Override
	public boolean isPointer() {
		
		return myIsPointer;

	}
	
	
	@Override
	public String getType() 
	{
		return RhapsodyOperation.getArgumentType(myArgument);
	}

	

	@SuppressWarnings("unchecked")
	@Override
	public List<IRPClassifier> getNestedClassifiers() {
		return getIRPClassifier().getNestedClassifiers().toList();
	}



	@Override
	public IRPModelElement getElement() {
		
		return myArgument;
	}

	

	

}
