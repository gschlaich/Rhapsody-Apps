package de.schlaich.gunnar.rhapsody.completion;

import java.util.List;

import javax.swing.Icon;

import org.fife.ui.autocomplete.AbstractCompletionProvider;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.VariableCompletion;

import com.telelogic.rhapsody.core.IRPAttribute;
import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPPackage;

import de.schlaich.gunnar.rhapsody.utilities.RhapsodyOperation;

public class RhapsodyAttributeCompletion extends VariableCompletion implements RhapsodyClassifier {

	private IRPAttribute myAttribute;
	private IRPClassifier myClassifier;
	private IRPClassifier myClassifierPointer;
	
	private boolean myIsPointer = false;
	private boolean myIsValue = true;
	
	public RhapsodyAttributeCompletion(CompletionProvider provider, IRPAttribute aAttribute, boolean addType)
	{
		super(provider, aAttribute.getName(), aAttribute.getType().getName());
		init(provider, aAttribute, addType);
		
	}
	
	public RhapsodyAttributeCompletion(CompletionProvider provider, IRPAttribute aAttribute) {
		super(provider, aAttribute.getName(), aAttribute.getType().getName());
		init(provider, aAttribute, true);
	}
	
	private void init(CompletionProvider provider, IRPAttribute aAttribute, boolean addType) 
	{
		myAttribute = aAttribute;
		
		
		setShortDescription(myAttribute.getDescription());
		setDefinedIn(myAttribute.getOwner().getFullPathNameIn());
		
		myClassifier = myAttribute.getType();
		
		//add also type to completion
		AbstractCompletionProvider abstractProvider = (AbstractCompletionProvider)provider;
		if((addType==true)&&(myAttribute.getType()!=null)&&(abstractProvider!=null))
		{
			IRPPackage nameSpace = RhapsodyOperation.getNamespacePackage(myAttribute);
			IRPPackage typeNameSpace = RhapsodyOperation.getNamespacePackage(myAttribute.getType());
			//if((typeNameSpace==null)||(typeNameSpace==nameSpace))
			{
			
				RhapsodyClassifierCompletion rc = new RhapsodyClassifierCompletion(provider, myAttribute.getType());
				abstractProvider.addCompletion(rc);
				myClassifier = rc.getIRPClassifier(false);
				myClassifierPointer = rc.getIRPClassifier(true);
				myIsPointer = rc.isPointer();
				myIsValue = rc.isValue();
			}
			/*
			else
			{
				new RhapsodyClassifierCompletion(provider, myAttribute.getType());
			}
			*/
			
		}
		else
		{
			new RhapsodyClassifierCompletion(provider, myAttribute.getType());
		}
		
			
		if((myAttribute.getIsReference()==1))
		{
			myIsPointer = true;
			
		}
	}
	
	@Override
	public Icon getIcon() {
		return  RhapsodyOperation.getIcon(myAttribute);
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
	public boolean isPointer() 
	{
		return myIsPointer;
	}

	@SuppressWarnings("unchecked")
	@Override	
	public List<IRPClassifier> getNestedClassifiers(boolean aPointer) 
	{
		IRPClassifier classifier = getIRPClassifier(aPointer);
		if(classifier==null)
		{
			return null;
		}
		return classifier.getNestedClassifiers().toList();
	}
	

	@Override
	public IRPModelElement getElement() {	
		return myAttribute;
	}

	@Override
	public boolean isValue() {
		return myIsValue;
	}

}