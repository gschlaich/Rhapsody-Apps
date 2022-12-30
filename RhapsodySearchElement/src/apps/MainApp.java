package apps;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.rhapsody.apps.*;
import com.telelogic.rhapsody.core.*;


public class MainApp extends App {
	
	

static enum eVisibility
	{
		ePrivate,
		eProtected,
		ePublic
	}
	
	/*
	* This method is called on invoking an app inside Rhapsody.
	* rhapsody - Instance of an active Rhapsody application 
	* selected - Selected element in Rhapsody
	*/
@SuppressWarnings("unchecked")
public void execute(IRPApplication rhapsody, IRPModelElement selected) {
		
		//List<IRPModelElement> scopes = new ArrayList<IRPModelElement>();
		
		searchElement(rhapsody, selected);
	
	}

public void searchElement(IRPApplication rhapsody, IRPModelElement selected) {
	Map<String,IRPModelElement> scopes = new HashMap<String,IRPModelElement>();
	

	eVisibility visibility = eVisibility.ePublic; 
	boolean getOwner = false;
	
	if(rhapsody==null)
	{
		System.out.println("no rhapsody");
	}
	
	if(selected==null)
	{
		System.out.println("no selected");
	}

	rhapsody.writeToOutputWindow("Log", "Search model element of type " + selected.getMetaClass());
	
	if(selected instanceof IRPPackage)
	{
		rhapsody.writeToOutputWindow("Log", "Search works not with " + selected.getMetaClass());
	}
	
	
	IRPModelElement owner = selected;
	
	if(selected instanceof IRPOperation)
	{
		IRPOperation operation = (IRPOperation)selected;
		if(operation.getVisibility().equals("Private"))
		{
			visibility = eVisibility.ePrivate;
		}
		else if(operation.getVisibility().equals("Protected"))
		{
			visibility = eVisibility.eProtected;
		}
		getOwner = true;
	}
	else if(selected instanceof IRPAttribute)
	{
		IRPAttribute attribute = (IRPAttribute)selected;
		if(attribute.getVisibility().equals("Private"))
		{
			visibility = eVisibility.ePrivate;
		}
		else if(attribute.getVisibility().equals("Protected"))
		{
			visibility = eVisibility.eProtected;
		}
		getOwner = true;		
	}
	else if(selected instanceof IRPRelation)
	{
		IRPRelation relation = (IRPRelation)selected;
		if(relation.getVisibility().equals("Private"))
		{
			visibility = eVisibility.ePrivate;
		}
		else if(relation.getVisibility().equals("Protected"))
		{
			visibility = eVisibility.eProtected;
		}
		getOwner = true;
	}
	
	else if(selected instanceof IRPType)
	{
		getOwner = true;
		scopes.put(selected.getFullPathName(),selected);
	}
	else if(selected instanceof IRPEvent)
	{
		IRPModelElement evowner = selected;;
		while(evowner instanceof IRPPackage == false)
		{
			evowner = evowner.getOwner();
			if(evowner==null)
			{
				break;
			}
		}
		if(evowner!=null)
		{
			IRPPackage evpack = (IRPPackage)evowner;
			List<IRPModelElement> elements = evpack.getNestedElementsByMetaClass("Class", 1).toList();
			for(IRPModelElement e:elements)
			{
				scopes.put(e.getFullPathName(), e);
			}
		}
		
		
		
	}
	
	if(getOwner==true)
	{
		owner = selected.getOwner();
	}
	
	
	scopes.put(owner.getFullPathName(),owner);
		
	List<IRPModelElement> references = owner.getReferences().toList();
	for(IRPModelElement reference:references)
	{
		System.out.println(reference.getMetaClass()+" "+reference.getName()+" "+reference.toString());
		
		if(reference instanceof IRPRelation)
		{
			if(visibility!=eVisibility.ePublic)
			{
				continue;
			}
			IRPRelation relation = (IRPRelation)reference;
			scopes.put(relation.getFullPathName(),relation);
			IRPModelElement ofClass = relation.getOfClass();
			scopes.put(ofClass.getFullPathName(),ofClass);
			
			
		}
		else if(reference instanceof IRPGeneralization)
		{
			if(visibility==eVisibility.ePrivate)
			{
				continue;
			}
			IRPGeneralization generalization = (IRPGeneralization)reference;
			IRPModelElement derivedClass = generalization.getDerivedClass();
			scopes.put(derivedClass.getFullPathName(),derivedClass);
			getGeneralization(scopes, derivedClass);
		}
		else if(reference instanceof IRPDependency)
		{
			if(visibility!=eVisibility.ePublic)
			{
				continue;
			}
			IRPDependency dependency = (IRPDependency)reference;
			scopes.put(dependency.getFullPathName(), dependency);
			IRPModelElement dependent = dependency.getDependent();
			scopes.put(dependent.getFullPathName(), dependent);
			if(dependent instanceof IRPPackage)
			{
				continue;
			}
			scopes.put(dependent.getFullPathName(),dependent);
		}
		else if(reference instanceof IRPOperation)
		{
			if(visibility!=eVisibility.ePublic)
			{
				continue;
			}
			IRPModelElement operationOwner = reference.getOwner();
			scopes.put(operationOwner.getFullPathName(),operationOwner);
		}
		else if(reference instanceof IRPAttribute)
		{
			if(visibility!=eVisibility.ePublic)
			{
				continue;
			}
			IRPModelElement attributeOwner = reference.getOwner();
			scopes.put(attributeOwner.getFullPathName(),attributeOwner);
		}
		else if(reference instanceof IRPArgument)
		{
			if(visibility!=eVisibility.ePublic)
			{
				continue;
			}
			IRPArgument argument = (IRPArgument)reference;
			IRPModelElement op = argument.getOwner();
			scopes.put(op.getFullPathName(),op);
		}
		else
		{
			scopes.put(reference.getFullPathName(), reference);
		}
	}
	
	
	
	IRPSearchManager mgr = rhapsody.getSearchManager();
	IRPSearchQuery query = mgr.createSearchQuery();
	
	
	Set<Map.Entry<String, IRPModelElement>> set = scopes.entrySet();
	
	for (Map.Entry<String, IRPModelElement> pair : set)
	{
	     System.out.println("Scope: " + pair.getValue().getMetaClass()+" "+pair.getValue().getName()+" Path: "+pair.getKey());
	     query.addSearchScope(pair.getValue());
	}
	
	// set fields to search...
	
	query.addFilterSearchInField(IRPSearchQuery.SearchInField.NAME);
	query.addFilterSearchInField(IRPSearchQuery.SearchInField.OPERATION_BODIES);
	query.addFilterSearchInField(IRPSearchQuery.SearchInField.INITIAL_VALUE);
	query.addFilterSearchInField(IRPSearchQuery.SearchInField.ENUMERATION_LITERAL_VALUE);
	query.addFilterSearchInField(IRPSearchQuery.SearchInField.TYPE_DECLARATIONS_AND_REFERENCES);
	query.addFilterSearchInField(IRPSearchQuery.SearchInField.TRANSITION_LABEL);
	
	query.addFilterElementType("Class");
	query.addFilterElementType("Attribute");
	query.addFilterElementType("Variable");
	query.addFilterElementType("Argument");
	query.addFilterElementType("Operation");
	query.addFilterElementType("Relation");
	query.addFilterElementType("Type");
	query.addFilterElementType("Constructor");
	query.addFilterElementType("Destructor");
	query.addFilterElementType("State");
	query.addFilterElementType("Statechart");
	query.addFilterElementType("Dependency");
	query.addFilterElementType("Generalization");
	query.addFilterElementType("Association");
	query.addFilterElementType("AssociationEnd");
	query.addFilterElementType("Event");
	query.addFilterElementType("Transition");
	
	query.setMatchWholeWord(0);
	
	query.setSearchText(selected.getName());
	
	mgr.searchAndShowResults(query);
	
}

	@SuppressWarnings("unchecked")
	private void getGeneralization(Map<String,IRPModelElement> aScopes, IRPModelElement aModelElement )
	{
		List<IRPModelElement> references = aModelElement.getReferences().toList();
		for(IRPModelElement reference:references)
		{
			if(reference instanceof IRPGeneralization)
			{
				IRPGeneralization generalization = (IRPGeneralization)reference;
				IRPModelElement derivedClass = generalization.getDerivedClass();
				aScopes.put(derivedClass.getFullPathName(), derivedClass);
				getGeneralization(aScopes, derivedClass);
			
			}
		}
	}
	
	
	
	
   
	public static void main(String[] args) {
		
		MainApp app = new MainApp();
		String connectionstring = null;
		
		if (args.length >= 1) 
		{
			connectionstring = args[0];
		}
		executeApp(app, connectionstring);
		
	}	
	
	public static void executeApp(App aApp, String aConnectionstring)
	{
		IRPApplication actualApp = null;
		
		if(aConnectionstring!=null)
		{
			try
			{
				actualApp = RhapsodyAppServer.getActiveRhapsodyApplicationByID(aConnectionstring);
			}
			catch(Exception e)
			{
				System.out.println("connectionstring "+ aConnectionstring + " is not an active rhapsody application ");
			}
		}
		else
		{
			System.out.println("no connectionstring set");
		}
		
		if(actualApp==null)
		{
        
			aApp.invokeFromMain();
			return;
		}
		
		IRPModelElement selectedElement = actualApp.getSelectedElement();
		
		
		if(aConnectionstring!=null)
		{
			actualApp.writeToOutputWindow("log", "ConnectiongString: " + aConnectionstring + "\n");
		}
		else
		{
			actualApp.writeToOutputWindow("log", "ConnectiongString was null\n");
		}
		
		actualApp.writeToOutputWindow("log", "start...\n");
		
		aApp.execute(actualApp, selectedElement);
	}
}
