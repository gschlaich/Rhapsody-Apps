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

public class RhapsodyArgumentCompletion extends VariableCompletion implements RhapsodyClassifier {
	
	IRPArgument myArgument;
	
	public RhapsodyArgumentCompletion(CompletionProvider provider, IRPArgument aArgument) {
		super(provider, aArgument.getName(), aArgument.getType().getName());
		myArgument = aArgument;
		
		setSummary(myArgument.getDescription());
		
		String iconPath = myArgument.getIconFileName();
		iconPath = iconPath.replace("\\", "/");
		Icon icon = new ImageIcon(iconPath);
		setIcon(icon);
		
		
		
		//add also type to completion
		AbstractCompletionProvider abstractProvider = (AbstractCompletionProvider)provider;
		if((getIRPClassifier()!=null)&&(abstractProvider!=null))
		{
			RhapsodyClassifierCompletion rc = new RhapsodyClassifierCompletion(provider, getIRPClassifier());
			abstractProvider.addCompletion(rc);
		}
		
	}
	
	

	@Override
	public IRPClassifier getIRPClassifier() {
		return myArgument.getType();
	}

	@Override
	public boolean isPointer() {
		
		String direction = myArgument.getArgumentDirection();
		String ReturnPattern = myArgument.getPropertyValue("CPP_CG.Class."+direction);
		if(getIRPClassifier() instanceof IRPType)
		{
			ReturnPattern = myArgument.getPropertyValue("CPP_CG.Type."+direction);
		}
		
		return(ReturnPattern.endsWith("*"));

	}
	
	
	@Override
	public String getType() 
	{
		String direction = myArgument.getArgumentDirection();
		String pattern = myArgument.getPropertyValue("CPP_CG.Class."+direction);
		if(getIRPClassifier() instanceof IRPType)
		{
			pattern = myArgument.getPropertyValue("CPP_CG.Type."+direction);
		}
		
		return pattern.replaceFirst("$Type", getIRPClassifier().getName());
		
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
