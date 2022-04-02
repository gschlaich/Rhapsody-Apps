package de.schlaich.gunnar.rhapsody.completion;

import java.util.ArrayList;
import java.util.List;

import org.fife.ui.autocomplete.AbstractCompletionProvider;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.FunctionCompletion;
import org.fife.ui.autocomplete.ParameterizedCompletion;
import com.telelogic.rhapsody.core.IRPArgument;
import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPInterfaceItem;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPOperation;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPType;

import de.schlaich.gunnar.rhapsody.utilities.RhapsodyOperation;

public class RhapsodyOperationCompletion extends FunctionCompletion implements RhapsodyClassifier{

	IRPInterfaceItem myInterfaceItem;
	IRPPackage myNameSpacePackage = null;
	
	
	public RhapsodyOperationCompletion(CompletionProvider aProvider, IRPInterfaceItem aOperation) {
		super(aProvider, aOperation.getName(),aOperation instanceof IRPOperation ?  RhapsodyOperation.getReturnType((IRPOperation)aOperation):"void");
		init(aProvider, aOperation, true);
		
	}
	
	public RhapsodyOperationCompletion(CompletionProvider aProvider, IRPInterfaceItem aOperation, boolean addType) {
		super(aProvider, aOperation.getName(),aOperation instanceof IRPOperation ?  RhapsodyOperation.getReturnType((IRPOperation)aOperation):"void");
		init(aProvider, aOperation, addType);
		
	}

	@SuppressWarnings("unchecked")
	private void init(CompletionProvider aProvider, IRPInterfaceItem aOperation, boolean addType) {
		myInterfaceItem = aOperation;
		
		
		List<Parameter> parameters = new ArrayList<Parameter>();
		
		setIcon(RhapsodyOperation.getIcon(aOperation));
		
		AbstractCompletionProvider abstractProvider = (AbstractCompletionProvider)aProvider;
		
		setShortDescription(myInterfaceItem.getDescription());
		
		myNameSpacePackage = RhapsodyOperation.getNamespacePackage(myInterfaceItem);
		
		
		List<IRPArgument> arguments = myInterfaceItem.getArguments().toList();
		for(IRPArgument argument : arguments)
		{
			if(abstractProvider != null)
			{
				if(addType)
				{
					IRPPackage argNameSpace = RhapsodyOperation.getNamespacePackage(argument);
					if((argNameSpace==null)||(argNameSpace.equals(myNameSpacePackage)))
					{
							
						RhapsodyClassifierCompletion rc = new RhapsodyClassifierCompletion(aProvider, argument.getType());
						abstractProvider.addCompletion(rc);
					}
				}
			}
			
			ParameterizedCompletion.Parameter p = new ParameterizedCompletion.Parameter( RhapsodyOperation.getArgumentType(argument), argument.getName());
			p.setDescription(argument.getDescription());
			parameters.add(p);
		}
		
		if(addType)
		{
			
			if((getIRPClassifier(false)!=null)&&(abstractProvider!=null))
			{
				IRPPackage retNameSpace = RhapsodyOperation.getNamespacePackage(getIRPClassifier(false));
				if((retNameSpace==null)||(retNameSpace.equals(myNameSpacePackage)))
				{
					RhapsodyClassifierCompletion rc = new RhapsodyClassifierCompletion(aProvider, getIRPClassifier(false));
					abstractProvider.addCompletion(rc);
				}
			}
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
	
	/*

	@Override
	protected void possiblyAddDefinedIn(StringBuilder sb) {
		// TODO Auto-generated method stub
		super.possiblyAddDefinedIn(sb);
	}

	@Override
	protected boolean possiblyAddDescription(StringBuilder sb) {
		
			sb.append("<hr><br>");
			sb.append(myInterfaceItem.getToolTipHTML());
			//sb.append(getShortDescription());
			sb.append("<br><br><br>");
			return true;
		
	}

	@Override
	public void setDefinedIn(String definedIn) {
		// TODO Auto-generated method stub
		super.setDefinedIn(definedIn);
	}
	
	*/
	
	
	
}
