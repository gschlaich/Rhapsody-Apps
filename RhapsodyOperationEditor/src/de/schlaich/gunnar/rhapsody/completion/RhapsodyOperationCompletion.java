package de.schlaich.gunnar.rhapsody.completion;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.imaging.common.mylzw.MyBitOutputStream;
import org.fife.ui.autocomplete.AbstractCompletionProvider;
import org.fife.ui.autocomplete.Completion;
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
		
		setDefinedIn(myInterfaceItem.getOwner().getFullPathName());
		
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
	
	public IRPInterfaceItem getOperation() {
		return myInterfaceItem;
	}

	@Override
	public boolean isValue() {
		// TODO Auto-generated method stub
		return !isPointer();
	}
	
	@Override
	public int compareTo(Completion c2) {
		if (c2==this) {
			return 0;
		}
		else if (c2!=null) {
			return toString().compareToIgnoreCase(c2.toString());
		}
		return -1;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getSummary() {
		StringBuilder sb = new StringBuilder();
		possiblyAddDescription(sb);
		possiblyAddDefinedIn(sb);
		return sb.toString();
	}
	
	/**
	 * Adds some HTML describing where this package is defined, if this
	 * information is known.
	 *
	 * @param sb The buffer to append to.
	 */
	protected void possiblyAddDefinedIn(StringBuilder sb) {
		
			sb.append("<hr>Defined in:"); // TODO: Localize me
			sb.append(" <em>");
			sb.append(getDefinedIn());
			sb.append("</em>");
		
	}


	
	

	/**
	 * Adds the description text as HTML to a buffer, if a description is
	 * defined.
	 *
	 * @param sb The buffer to append to.
	 * @return Whether there was a description to add.
	 */
	protected boolean possiblyAddDescription(StringBuilder sb) {
		if (getShortDescription()!=null) {
				sb.append("<hr><br>");
				sb.append(myInterfaceItem.getToolTipHTML());
				sb.append("<br><br><br>");
				return true;	
		}
		return false;
	}
	
	
	
}
