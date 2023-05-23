package apps;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPComponent;
import com.telelogic.rhapsody.core.IRPDependency;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPModule;
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
		
		
		createRelationList();
		
	
		
			
	}


	public void createRelationList() 
	{
		
		if(myModelElement instanceof IRPPackage)
		{
			//select all packages...
			IRPProject project = myModelElement.getProject();
			
			List<IRPModelElement> elements = project.getNestedElementsByMetaClass("Package", 1).toList();
			
			for(IRPModelElement e:elements)
			{
				myModelElementItems.add(new RhapsodyModelElementItem(e, e.getName() + "   ["+e.getMetaClass()+"]"));
			}
		
			
			Collections.sort(myModelElementItems);
			return;
			
		}
		
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
		
		String nameSpace = classifierPackage.getNamespace();
		
		boolean ossiIncluded = false;
		
		while(classifierPackage.getNamespace().equals(nameSpace))
		{
		
			collectItems("",classifierPackage,true);
			collectItems(classifierPackage.getNamespace()+"::",classifierPackage,true);
		
		
		
			//add also classifiers from dependencies
			List<IRPDependency> dependencies = classifierPackage.getDependencies().toList();
			
			for(IRPDependency dependency:dependencies)
			{
				IRPModelElement p = dependency.getDependsOn();
				if(p.getName().equals("OSSI"))
				{
					ossiIncluded = true;
				}
				
				if(p instanceof IRPPackage)
				{
					IRPPackage pa = (IRPPackage)p;
					String pNamespace = pa.getNamespace();
					if((pNamespace ==null)||(pNamespace.equals("")))
					{
						//not sure what's better:
						//collectItems(pa.getName()+"::", pa, true);
						collectItems("", pa, true);
						
					}
					else
					{
						collectItems(pNamespace+"::",pa,true);
					}
							
					
				}
				else
				{
					collectItems(p.getName()+"::",p,true);
				}
			}
			owner = classifierPackage.getOwner();
			if(owner instanceof IRPPackage)
			{
				classifierPackage =(IRPPackage)owner;
			}
			else
			{
				break;
			}
		}
		
		
		//add special packages which don't need dependencies...
		IRPProject project = myModelElement.getProject();
		if(ossiIncluded==false)
		{
			IRPModelElement ossiPackage = project.findNestedElementRecursive("OSSI", "Package");
			if(ossiPackage!=null)
			{
				collectItems("OSSI::",ossiPackage,true);
			}
		}
		/*
		IRPModelElement stdPackage = project.findNestedElementRecursive("std", "Package");
		if(stdPackage!=null)
		{
			collectItems("std::",stdPackage,true);
		}
		*/
		
		IRPModelElement predefined = project.findNestedElement("MyPredefinedTypes", "Package");
		if(predefined!=null)
		{
			collectItems("",predefined,true);
		}
		
		
		Collections.sort(myModelElementItems);
	}
	
	public void clearRelationList()
	{
		myModelElementItems.clear();
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
				if(e.getOwner() instanceof IRPModule)
				{
					collectItems(prefix,e,true);
				}
				else
				{
					collectItems(prefix+e.getName()+"::",e,true);
				}
			}
			if(e.getOwner() instanceof IRPModule)
			{
				
			}
			else
			{
				myModelElementItems.add(new RhapsodyModelElementItem(e,prefix+e.getName()+"   ["+e.getMetaClass()+" in "+e.getOwner().getName()+"]"));
			}
			
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
			myModelElementItems.add(new RhapsodyModelElementItem(e,prefix+e.getName()+"   ["+kind+" in "+e.getOwner().getName()+"]"));
		}
		
		elements = aModelElement.getNestedElementsByMetaClass("Package", 0).toList();
		for(IRPModelElement e:elements)
		{
			if(aRecursive)
			{
				IRPPackage p = (IRPPackage)e;
				String pNamespace = p.getNamespace();
				if((pNamespace==null)||(pNamespace.equals("")))
				{
					//not sure what's better:
					//collectItems(prefix+p.getName()+"::",e,true);
					collectItems("",e,true);
				}
				else if(prefix.equals(pNamespace+"::"))
				{
					collectItems(prefix,e,true);
				}
				else
				{
					collectItems(prefix+pNamespace+"::",e,true);
				}
				
			}
			myModelElementItems.add(new RhapsodyModelElementItem(e, prefix+e.getName()+"    ["+e.getMetaClass()+" in "+e.getOwner().getName()+"]"));
		}
		
		if(aModelElement instanceof IRPPackage)
		{
			IRPPackage p = (IRPPackage)aModelElement;
			List<IRPModule> modules = p.getModules().toList();
			for(IRPModule m:modules)
			{
				if(aRecursive)
				{
					
					
					String pNamespace = p.getNamespace();
					if((pNamespace==null)||(pNamespace.equals("")))
					{
						//what's correct?
						collectItems("",m,true);
					}
					else
					{
						collectItems(prefix,m,true);
					}
				}
				myModelElementItems.add(new RhapsodyModelElementItem(m, prefix+m.getName()+"   [File in "+p.getName()+"]"));
				
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
