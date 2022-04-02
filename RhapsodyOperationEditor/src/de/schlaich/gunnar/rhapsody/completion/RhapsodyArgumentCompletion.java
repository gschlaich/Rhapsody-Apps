package de.schlaich.gunnar.rhapsody.completion;

import java.util.List;

import org.fife.ui.autocomplete.AbstractCompletionProvider;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.VariableCompletion;

import com.telelogic.rhapsody.core.IRPArgument;
import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPType;

import de.schlaich.gunnar.rhapsody.utilities.RhapsodyOperation;

public class RhapsodyArgumentCompletion extends VariableCompletion implements RhapsodyClassifier{
	
	IRPArgument myArgument;
	IRPClassifier myClassifier;
	IRPClassifier myClassifierPointer;
	boolean myIsPointer = false;
	boolean myIsValue = true;
	
	public RhapsodyArgumentCompletion(CompletionProvider provider, IRPArgument aArgument) {
		super(provider, aArgument.getName(), RhapsodyOperation.getArgumentType(aArgument));
		myArgument = aArgument;
		
		setSummary(myArgument.getDescription());
		
		setIcon( RhapsodyOperation.getIcon(myArgument));
		
		setDefinedIn(aArgument.getOwner().getFullPathNameIn());
		
		//add also type to completion
		AbstractCompletionProvider abstractProvider = (AbstractCompletionProvider)provider;
		
		String direction = myArgument.getArgumentDirection();
		
		String propertyName = "CPP_CG.Class."+direction;
		if(getIRPClassifier(false) instanceof IRPType)
		{
			propertyName = "CPP_CG.Type."+direction;
		}
		
		
		String codePattern = myArgument.getPropertyValue(propertyName);
		
		//TODO hacky...
		myIsPointer = codePattern.endsWith("*");
		
		//check for template...
		if(codePattern.contains("<"))
		{
			//could be template...
			int end = codePattern.indexOf("<");
			String templateName = codePattern.substring(0,end);
			System.out.println(templateName);
			//search for classifier
			
			if(templateName.contains("::"))
			{
				templateName= templateName.substring(templateName.lastIndexOf(":")+1);
			}
			
			IRPProject project = myArgument.getProject();
			IRPClass template = (IRPClass)project.findNestedElementRecursive(templateName, "Class");
			if(template!=null)
			{
				if(template.findNestedElement("operator->", "Operation")!=null)
				{
					myIsPointer = true;
				}
			}
		}
			
		
		myIsPointer = codePattern.startsWith("std::unique_ptr");
		
		
		if((myArgument.getType()!=null)&&(abstractProvider!=null))
		{
			RhapsodyClassifierCompletion rc = new RhapsodyClassifierCompletion(provider, myArgument.getType());
			abstractProvider.addCompletion(rc);
			if(myIsPointer==false)
			{
				myClassifier = rc.getIRPClassifier(false);
				myClassifierPointer = rc.getIRPClassifier(true);
				myIsPointer = rc.isPointer();
				myIsValue = rc.isValue();
			}
			else
			{
				myIsValue = false;
				myClassifier = null;
				myClassifierPointer = rc.getIRPClassifier(false);
			}
			
		}
		
	}
	
	

	@Override
	public IRPClassifier getIRPClassifier(boolean aPointer) {
		if(aPointer)
		{
			return myClassifierPointer;
		}
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
	public List<IRPClassifier> getNestedClassifiers(boolean aPointer) {
		IRPClassifier classifier = getIRPClassifier(aPointer);
		if(classifier==null)
		{
			return null;
		}
		return classifier.getNestedClassifiers().toList();
	}



	@Override
	public IRPModelElement getElement() {
		
		return myArgument;
	}



	@Override
	public boolean isValue() {
		return myIsValue;
	}

	

	

}
