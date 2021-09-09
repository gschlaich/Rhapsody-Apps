package apps;

import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.fife.ui.autocomplete.AbstractCompletionProvider;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.VariableCompletion;

import com.telelogic.rhapsody.core.IRPAssociationClass;
import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPInstance;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPRelation;

import RhapsodyUtilities.RhapsodyOperation;

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
				//map...
				
			}
			else 
			{
				//list or vector... assume vector
				//if()
			}
		}
		
		//add also type to completion
		
		if((getIRPClassifier()!=null)&&(abstractProvider!=null))
		{
			RhapsodyClassifierCompletion rc = new RhapsodyClassifierCompletion(provider, getIRPClassifier());
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
		return getIRPClassifier().getName();	
	}
	

	@Override
	public IRPClassifier getIRPClassifier() 
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
	public List<IRPClassifier> getNestedClassifiers() {
		return getIRPClassifier().getNestedClassifiers().toList();
	}


	@Override
	public IRPModelElement getElement() {
		return myRelation;
	}


}
