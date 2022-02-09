package de.schlaich.gunnar.rhapsody.completion;

import java.util.List;

import javax.swing.Icon;
import org.fife.ui.autocomplete.AbstractCompletionProvider;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.VariableCompletion;

import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPInstance;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPRelation;

import de.schlaich.gunnar.rhapsody.utilities.RhapsodyOperation;

public class RhapsodyRelationCompletion extends VariableCompletion implements RhapsodyClassifier {

	private IRPRelation myRelation;
	public RhapsodyRelationCompletion(CompletionProvider provider, IRPRelation aRelation) {
		super(provider, aRelation.getName(), aRelation.getOtherClass().getName());
		myRelation = aRelation;
		
		
		AbstractCompletionProvider abstractProvider = (AbstractCompletionProvider)provider;
		if(myRelation.getMultiplicity().equals("*"))
		{
			//container...
			if(myRelation.getQualifier().equals("")==false)
			{
				
				
			}
			else 
			{
				//list or vector... assume vector
				//if()
			}
		}
		
		//add also type to completion
		
		if((getIRPClassifier(isPointer())!=null)&&(abstractProvider!=null))
		{
			RhapsodyClassifierCompletion rc = new RhapsodyClassifierCompletion(provider, getIRPClassifier(isPointer()));
			abstractProvider.addCompletion(rc);
		}
		
		
	}
	
	private boolean isVector()
	{
		if(myRelation.getMultiplicity().equals("*"))
		{
			if(myRelation.getQualifier().equals(""))
			{
				
				return true;
				
			}	
		}
		return false;
	}
	
	private boolean isMap()
	{
		if(myRelation.getMultiplicity().equals("*"))
		{
			if(myRelation.getQualifier().equals("")==false)
			{
				return true;
				
			}	
		}
		return false;
	}
	
	
	@Override
	public String getType() {
		//TODO check if relation is Vector or List...
		if(isVector())
		{
			return "vector";
		}
		if(isMap())
		{
			return "map";
		}
		return getIRPClassifier(isPointer()).getName();	
	}
	

	@Override
	public IRPClassifier getIRPClassifier(boolean isPointer) 
	{
		if(isVector())
		{
			IRPClass vectorClass = myRelation.getProject().findClass("vector");
			return vectorClass;
		}
		if(isMap())
		{
			IRPClass mapClass = myRelation.getProject().findClass("map");
			return mapClass;
		}
		return myRelation.getOtherClass();
		
	}


	@Override
	public boolean isPointer() {
		if(isVector())
		{
			return false;
		}
		if(isMap())
		{
			return false;
		}
			
		if(myRelation instanceof IRPInstance)
		{
			return false;
		}
		
		return true;
	}
	

	@Override
	public Icon getIcon() {
		return  RhapsodyOperation.getIcon(myRelation);
	}



	@SuppressWarnings("unchecked")
	@Override
	public List<IRPClassifier> getNestedClassifiers(boolean isPointer) {
		return getIRPClassifier(isPointer).getNestedClassifiers().toList();
	}


	@Override
	public IRPModelElement getElement() {
		return myRelation;
	}

	@Override
	public boolean isValue() {
		// TODO Auto-generated method stub
		return !isPointer();
	}


}
