package apps;

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
import com.telelogic.rhapsody.core.IRPOperation;

import apps.ClassifierCompletionProvider.visibility;

public class RhapsodyOperationCompletion extends FunctionCompletion implements RhapsodyClassifier {

	IRPOperation myOperation;
	
	@SuppressWarnings("unchecked")
	public RhapsodyOperationCompletion(CompletionProvider aProvider, IRPOperation aOperation, visibility aVisibility) {
		super(aProvider, aOperation.getName(), aOperation.getReturnTypeDeclaration());
		myOperation = aOperation;
		
		List<IRPArgument> arguments = myOperation.getArguments().toList();
		List<Parameter> parameters = new ArrayList<Parameter>();
		
		AbstractCompletionProvider abstractProvider = (AbstractCompletionProvider)aProvider;
		
		
		for(IRPArgument argument : arguments)
		{
			if((abstractProvider != null) &&( aVisibility == visibility.v_private ))
			{

				RhapsodyClassifierCompletion rc = new RhapsodyClassifierCompletion(aProvider, argument.getType());
				abstractProvider.addCompletion(rc);
			}
			
			ParameterizedCompletion.Parameter p = new ParameterizedCompletion.Parameter(argument.getType().getName(), argument.getName());
			p.setDescription(argument.getDescription());
			
			parameters.add(p);
		}
		
		if((getIRPClassifier()!=null)&&(abstractProvider!=null)&&( aVisibility == visibility.v_private ))
		{
			RhapsodyClassifierCompletion rc = new RhapsodyClassifierCompletion(aProvider, getIRPClassifier());
			abstractProvider.addCompletion(rc);
		}
		
		super.setShortDescription(myOperation.getDescription());
		super.setParams(parameters);
		
	}
	
	@Override
	public IRPClassifier getIRPClassifier() {
		return myOperation.getReturns();
	}
	
	@Override
	public Icon getIcon() {
		
		Icon ret = new ImageIcon(myOperation.getIconFileName());
		
		return ret;
	}

	@Override
	public boolean isReference() {
		String ReturnPattern = myOperation.getPropertyValue("CPP_CG.Type.ReturnType");
		return(ReturnPattern.endsWith("*"));
	}
	
	

}
