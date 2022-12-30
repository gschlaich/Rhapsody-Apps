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
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPRelation;

import de.schlaich.gunnar.rhapsody.utilities.RhapsodyOperation;

public class RhapsodyRelationCompletion extends VariableCompletion implements RhapsodyClassifier{

	private IRPRelation myRelation;
	public RhapsodyRelationCompletion(CompletionProvider provider, IRPRelation aRelation, boolean addType) {
		super(provider, aRelation.getName(), aRelation.getOtherClass().getName());
		myRelation = aRelation;
		
		
		AbstractCompletionProvider abstractProvider = (AbstractCompletionProvider)provider;
		
		myRelation.getDescription();
		
		if(addType==false)
		{
			return;
		}
		
		//add also type to completion
		
		IRPPackage namespace = RhapsodyOperation.getNamespacePackage(myRelation);
		
		//TODO: clean up this chaos with container an otherClass...

		if((getIRPClassifier(isPointer())!=null)&&(abstractProvider!=null))
		{
			IRPPackage classifiernamespace = RhapsodyOperation.getNamespacePackage(getIRPClassifier(isPointer()));
			
			if((classifiernamespace==null)||(classifiernamespace.equals(namespace)))
			{		
				RhapsodyClassifierCompletion rc = new RhapsodyClassifierCompletion(provider, getIRPClassifier(isPointer()));
				abstractProvider.addCompletion(rc);
	
			}
			else if(isContainer())
			{
				
				RhapsodyClassifierCompletion rc = new RhapsodyClassifierCompletion(provider,myRelation.getOtherClass());
				abstractProvider.addCompletion(rc);
			}
		}
		
		
	}
	
	private boolean isVector()
	{
		
		if(myRelation.getMultiplicity().equals("*"))
		{
			if(myRelation.getQualifier().equals(""))
			{
				if(myRelation.getQualifierType()==null)
				{
					return true;
				}
				
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
			if(myRelation.getQualifierType()!=null)
			{
				return true;
			}
			
		}
		return false;
	}
	
	@Override
	protected void addDefinitionString(StringBuilder sb) {
		// TODO Auto-generated method stub
		
		sb.append("<html><b>").append(getType()).append(" ").append(myRelation.getName()).append("</b>");
		//super.addDefinitionString(sb);
	}
	
	
	@Override
	public String getType() {
		//TODO check if relation is Vector or List...
		if(isVector())
		{
			return "std::vector ("+myRelation.getOtherClass().getName()+"*)";
		}
		if(isMap())
		{
			if(myRelation.getQualifierType()!=null)
			{
				return "std::map ("+myRelation.getQualifierType()+", "+myRelation.getOtherClass().getName()+"*)";
			}
			else
			{
				return "std::map ("+myRelation.getQualifier()+", "+myRelation.getOtherClass().getName()+"*)";
			}
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
	
	private boolean isContainer() 
	{
		if(isVector())
		{
			return true;
		}
		if(isMap())
		{
			return true;
		}
		return false;
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
