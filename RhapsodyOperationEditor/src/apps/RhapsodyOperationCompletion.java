package apps;

import java.awt.Image;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.fife.ui.autocomplete.AbstractCompletionProvider;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.FunctionCompletion;
import org.fife.ui.autocomplete.ParameterizedCompletion;
import org.fife.ui.autocomplete.ParameterizedCompletion.Parameter;

import com.telelogic.rhapsody.core.IRPArgument;
import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPEvent;
import com.telelogic.rhapsody.core.IRPInterfaceItem;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPOperation;
import com.telelogic.rhapsody.core.IRPType;

import RhapsodyUtilities.RhapsodyOperation;

public class RhapsodyOperationCompletion extends FunctionCompletion implements RhapsodyClassifier {

	IRPInterfaceItem myInterfaceItem;
	
	
	@SuppressWarnings("unchecked")
	public RhapsodyOperationCompletion(CompletionProvider aProvider, IRPInterfaceItem aOperation) {
		super(aProvider, aOperation.getName(),aOperation instanceof IRPOperation ?  RhapsodyOperation.getReturnType((IRPOperation)aOperation):"void");
		myInterfaceItem = aOperation;
		
		List<IRPArgument> arguments = myInterfaceItem.getArguments().toList();
		List<Parameter> parameters = new ArrayList<Parameter>();
		
		setIcon(RhapsodyOperation.getIcon(aOperation));
		
		AbstractCompletionProvider abstractProvider = (AbstractCompletionProvider)aProvider;
		
		setShortDescription(myInterfaceItem.getDescription());
		
		
		for(IRPArgument argument : arguments)
		{
			if(abstractProvider != null)
			{

				RhapsodyClassifierCompletion rc = new RhapsodyClassifierCompletion(aProvider, argument.getType());
				abstractProvider.addCompletion(rc);
			}
			
			ParameterizedCompletion.Parameter p = new ParameterizedCompletion.Parameter( RhapsodyOperation.getArgumentType(argument), argument.getName());
			p.setDescription(argument.getDescription());
			parameters.add(p);
		}
		
		if((getIRPClassifier(false)!=null)&&(abstractProvider!=null))
		{
			RhapsodyClassifierCompletion rc = new RhapsodyClassifierCompletion(aProvider, getIRPClassifier(false));
			abstractProvider.addCompletion(rc);
		}
		
		
		
		super.setShortDescription(myInterfaceItem.getDescription());
		super.setParams(parameters);
		
	}
	
	@Override
	public IRPClassifier getIRPClassifier(boolean aPointer) {
		if(myInterfaceItem instanceof IRPOperation)
		{
			IRPOperation op = (IRPOperation)myInterfaceItem;
			return op.getReturns();
		}
		
		return null;
	}
	

	@Override
	public boolean isPointer() {
		String ReturnPattern = myInterfaceItem.getPropertyValue("CPP_CG.Class.ReturnType");
		if(getIRPClassifier(false) instanceof IRPType)
		{
			ReturnPattern = myInterfaceItem.getPropertyValue("CPP_CG.Type.ReturnType");
		}
		return(ReturnPattern.endsWith("*"));
	}

	@Override
	public List<IRPClassifier> getNestedClassifiers(boolean aPointer) {
		//return empty list
		return new ArrayList<IRPClassifier>();
	}

	@Override
	public IRPModelElement getElement() {
		return myInterfaceItem;
	}

	@Override
	public boolean isValue() {
		// TODO Auto-generated method stub
		return !isPointer();
	}

}
