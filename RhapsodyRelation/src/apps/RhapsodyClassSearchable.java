package apps;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPProject;

public class RhapsodyClassSearchable implements Searchable<RhapsodyClassifierItem, String> 
{

	IRPClassifier myClassifier = null;
	List<RhapsodyClassifierItem> myClassifierItems = null;
	
	
	@SuppressWarnings("unchecked")
	public RhapsodyClassSearchable(IRPClassifier aClassifier) 
	{
		
		myClassifier = aClassifier;
		
		//IRPProject project = myClassifier.getProject();
		//myClassifiers = project.getNestedElementsByMetaClass("Class", 1).toList();
		//myClassifiers.sort(IRPClassfierComparator);
		
		
		
		
		myClassifierItems = new ArrayList<RhapsodyClassifierItem>();
		
		
		IRPModelElement owner = myClassifier.getOwner();
		
		
		while((owner instanceof IRPPackage)==false)
		{
			owner = owner.getOwner();
		}
		
		IRPPackage classifierPackage = (IRPPackage)owner;
		
		while((classifierPackage.getNamespace()==null)||(classifierPackage.getNamespace().equals("")))
		{
			classifierPackage = (IRPPackage)classifierPackage.getOwner();
		}
		
		
		//collect classes
		List<IRPClassifier> classifiers;

		classifiers = classifierPackage.getNestedElementsByMetaClass("Class", 1).toList();
		
		for(IRPClassifier classifier:classifiers)
		{
			myClassifierItems.add(new RhapsodyClassifierItem(classifier));
			
		}
		
		Collections.sort(myClassifierItems);
		
		
		
		
		//look for dependencies
		
		
		
		
			
	}
	
	
	@Override
	public Collection<RhapsodyClassifierItem> search(String value) 
	{
		
		List<RhapsodyClassifierItem> ret = new ArrayList<RhapsodyClassifierItem>();
		for(RhapsodyClassifierItem classifier:myClassifierItems)
		{
			String classifierName = classifier.toString();
			if(classifierName.indexOf(value)==0)
			{
				ret.add(classifier);
			}
		}
		
		return ret;
		
	}
	
	
	

}
