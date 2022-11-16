package apps;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPDependency;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPStatechart;
import com.telelogic.rhapsody.core.IRPType;

public class RhapsodyModelElementSearchable implements Searchable<RhapsodyModelElementItem, String> 
{

	IRPModelElement myModelElement = null;
	List<RhapsodyModelElementItem> myModelElementItems = null;
	
	
	@SuppressWarnings("unchecked")
	public RhapsodyModelElementSearchable(IRPModelElement aModelElement) 
	{
		
		myModelElement = aModelElement;
		
		//IRPProject project = myClassifier.getProject();
		//myClassifiers = project.getNestedElementsByMetaClass("Class", 1).toList();
		//myClassifiers.sort(IRPClassfierComparator);
		
		
		
		
		myModelElementItems = new ArrayList<RhapsodyModelElementItem>();
		
		
		IRPModelElement owner = myModelElement.getOwner();
		
		
		while((owner instanceof IRPPackage)==false)
		{
			owner = owner.getOwner();
		}
		
		IRPPackage classifierPackage = (IRPPackage)owner;
		
		while((classifierPackage.getNamespace()==null)||(classifierPackage.getNamespace().equals("")))
		{
			classifierPackage = (IRPPackage)classifierPackage.getOwner();
		}
		
		
		collectItems("",classifierPackage,true);
		
		
		
		//add also classieriers from dependencies
		List<IRPDependency> dependencies = classifierPackage.getDependencies().toList();
		boolean ossiIncluded = false;
		for(IRPDependency dependency:dependencies)
		{
			IRPModelElement p = dependency.getDependsOn();
			if(p.getName().equals("OSSI"))
			{
				ossiIncluded = true;
			}
				
			collectItems(p.getName()+"::",p,true);
		}
		
		
		//add special packages which don't need dependencies...
		IRPProject project = aModelElement.getProject();
		if(ossiIncluded==false)
		{
			IRPModelElement ossiPackage = project.findNestedElementRecursive("OSSI", "Package");
			if(ossiPackage!=null)
			{
				collectItems("OSSI::",ossiPackage,true);
			}
		}
		IRPModelElement stdPackage = project.findNestedElementRecursive("std", "Package");
		if(stdPackage!=null)
		{
			collectItems("std::",stdPackage,true);
		}
		
		Collections.sort(myModelElementItems);
		
	
		
			
	}
	
	
	@SuppressWarnings("unchecked")
	private void collectItems(String prefix, IRPModelElement aModelElement, Boolean aRecursive)
	{
		List<IRPModelElement> elements;
		elements = aModelElement.getNestedElementsByMetaClass("Class", 0).toList();
		for(IRPModelElement e:elements)
		{
			if(e instanceof IRPStatechart)
			{
				continue;
			}
			
			if(aRecursive)
			{
				collectItems(prefix+e.getName()+"::",e,true);
			}
			myModelElementItems.add(new RhapsodyModelElementItem(e,prefix+e.getName()+"   ["+e.getMetaClass()+"]"));
			
		}
		
		elements = aModelElement.getNestedElementsByMetaClass("Type", 0).toList();
		for(IRPModelElement e:elements)
		{
			IRPType t = (IRPType)e;
			
			String kind = t.getKind();
			if(t.isKindLanguage()==1)
			{
				if(t.isEnum()==1)
				{
					kind = "Enumeration";
				}
				else if(t.isStruct()==1)
				{
					kind = "Struct";
				}
				else if(t.isUnion()==1)
				{
					kind = "Union";
				}
				else if(t.getIsTypedef()==1)
				{
					kind = "Typedef";
				}
				else
				{
					kind = "Type";
				}
			}
			
			String name = e.getName();
			if(name.equals(""))
			{
				continue;
			}
			myModelElementItems.add(new RhapsodyModelElementItem(e,prefix+e.getName()+"   ["+kind+"]"));
		}
		
		elements = aModelElement.getNestedElementsByMetaClass("Package", 0).toList();
		for(IRPModelElement e:elements)
		{
			if(aRecursive)
			{
				IRPPackage p = (IRPPackage)e;
				collectItems(p.getNamespace()+"::",e,true);
			}
		}
		
	}


	
	
	
	@Override
	public Collection<RhapsodyModelElementItem> search(String value) 
	{
		
		List<RhapsodyModelElementItem> ret = new ArrayList<RhapsodyModelElementItem>();
		for(RhapsodyModelElementItem modelElementItem:myModelElementItems)
		{
			String modelElementName = modelElementItem.toString();
			if(modelElementName.indexOf(value)==0)
			{
				ret.add(modelElementItem);
			}
		}
		
		return ret;
		
	}
	
	
	

}
