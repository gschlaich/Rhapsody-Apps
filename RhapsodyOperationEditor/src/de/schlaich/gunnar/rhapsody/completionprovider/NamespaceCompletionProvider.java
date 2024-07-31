package de.schlaich.gunnar.rhapsody.completionprovider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.imaging.common.mylzw.MyBitOutputStream;
import org.fife.ui.autocomplete.AbstractCompletionProvider;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.Util;

import com.telelogic.rhapsody.core.IRPAttribute;
import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPEvent;
import com.telelogic.rhapsody.core.IRPFile;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPModule;
import com.telelogic.rhapsody.core.IRPOperation;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPStereotype;
import com.telelogic.rhapsody.core.IRPType;
import com.telelogic.rhapsody.core.IRPVariable;

import de.schlaich.gunnar.rhapsody.completion.RhapsodyClassifierCompletion;
import de.schlaich.gunnar.rhapsody.completion.RhapsodyNamespaceCompletion;
import de.schlaich.gunnar.rhapsody.completion.RhapsodyOperationCompletion;
import de.schlaich.gunnar.rhapsody.completion.RhapsodyTypeCompletion;
import de.schlaich.gunnar.rhapsody.completion.RhapsodyVariableCompletion;

public class NamespaceCompletionProvider extends DefaultCompletionProvider {

	private IRPPackage myPackage = null;
	private String myPackageName = null;
	private String myNamespaceName = null;
	
	Set<AbstractCompletionProvider> myOtherproviders = new HashSet<AbstractCompletionProvider>();
	
	private static Map<IRPPackage,NamespaceCompletionProvider> NamespaceCompletionProviders = null;
	
	public static NamespaceCompletionProvider GetNameSpaceProvider(RhapsodyNamespaceCompletion aRhapsodyNamespaceCompletion)
	{
		IRPPackage lpackage = aRhapsodyNamespaceCompletion.getPackage();
		NamespaceCompletionProvider ret = null;
		if(NamespaceCompletionProviders==null)
		{
			NamespaceCompletionProviders = new HashMap<IRPPackage,NamespaceCompletionProvider>();
		}
		ret = NamespaceCompletionProviders.get(lpackage);
		if(ret==null)
		{
			ret = new NamespaceCompletionProvider(lpackage,aRhapsodyNamespaceCompletion);
			System.out.println("--------------- New  NamespaceCompletionProvider of Package "+ lpackage.getName());
			NamespaceCompletionProviders.put(lpackage, ret);
		}
		else
		{
			System.out.println("--------------- NamespaceCompletionProvider already exists: "+ lpackage.getName());
		}
		return ret;
	}
	
	public static List<NamespaceCompletionProvider> GetNameSpaceCompletionProviders()
	{
		if((NamespaceCompletionProviders==null)||(NamespaceCompletionProviders.isEmpty()))
		{
			return new ArrayList<NamespaceCompletionProvider>();
		}
		return new ArrayList<NamespaceCompletionProvider>(NamespaceCompletionProviders.values());
	}
	
	public static Completion GetCompletion(String aToken)
	{
		List<Completion> l = GetCompletions(aToken);
		if((l!=null)&&(l.isEmpty()==false))
		{
			return l.get(0);
		}
		return null;
	}
	
	public static List<Completion> GetCompletions(String aToken)
	{
		if(NamespaceCompletionProviders==null)
		{
			return null;
		}
		for(NamespaceCompletionProvider provider: NamespaceCompletionProviders.values())
		{
			List<Completion> l = provider.getCompletionByInputText(aToken);
			if((l!=null)&&(l.isEmpty()==false))
			{
				return l;
			}
			
		}	
		return null;
	}
	
	
	private NamespaceCompletionProvider(IRPPackage aPackage, RhapsodyNamespaceCompletion aRhapsodyNamespaceCompletion)
	{
		super.setParameterizedCompletionParams('(', ", ", ')');
		myPackage = aPackage;
		
		myNamespaceName = myPackage.getNamespace();
		myPackageName = myPackage.getName();
		
		if(hasNoNamespace())
		{
			//leave empty!
			return;
		}
		
		addElements(aPackage,this);
		
	}
	
	public String toString()
	{
		if(hasNoNamespace())
		{
			return myPackageName;
		}
		return myNamespaceName;
	}
	
	public boolean hasNoNamespace()
	{
		return(myNamespaceName.equals(""));
	}
	
	
	public void addToOtherProvider(AbstractCompletionProvider aProvider)
	{
		
		if(myOtherproviders.contains(aProvider)==true)
		{
			System.out.println("Provider ("+aProvider.toString()+") already exists");
			return;
		}
		
		myOtherproviders.add(aProvider);

		long started = System.currentTimeMillis();
		
		addElements(myPackage, aProvider);
		
		/*
		for(Completion c : completions )
		{
			if(c instanceof RhapsodyClassifierCompletion)
			{
				RhapsodyClassifierCompletion rccOld = (RhapsodyClassifierCompletion) c;
				RhapsodyClassifierCompletion rccNew = new RhapsodyClassifierCompletion(aProvider, rccOld.getIRPClassifier(rccOld.isPointer()),false,false);
				aProvider.addCompletion(rccNew);
			}
			else if(c instanceof RhapsodyTypeCompletion)
			{
				RhapsodyTypeCompletion rtcOld = (RhapsodyTypeCompletion)c;
				RhapsodyTypeCompletion rtcNew = new RhapsodyTypeCompletion(aProvider,rtcOld.getType());
				aProvider.addCompletion(rtcNew);
			}
			else if(c instanceof RhapsodyOperationCompletion)
			{
				RhapsodyOperationCompletion rocOld = (RhapsodyOperationCompletion)c;
				RhapsodyOperationCompletion rocNew = new RhapsodyOperationCompletion(aProvider, rocOld.getOperation(), false);
			}
			else if(c instanceof RhapsodyVariableCompletion)
			{
				RhapsodyVariableCompletion rvcOld = (RhapsodyVariableCompletion)c;
				RhapsodyVariableCompletion rvcNew = new RhapsodyVariableCompletion(aProvider, rvcOld.getVariable(), false);
			}
		}
		*/
		
		long ended = System.currentTimeMillis();
		
		System.out.println("addtoOtherProvider time: " + (ended-started)/1000 + "s");
		
	}
	
	
	@SuppressWarnings("unchecked")
	private void addElements(IRPPackage aPackage, AbstractCompletionProvider aProvider)
	{
		String namespacename = aPackage.getNamespace();
		List<IRPModule> modules = aPackage.getModules().toList();
		for(IRPModule module:modules)
		{
			System.out.println("Namespace: " + namespacename + " Module: " + module.getName());
			List<IRPModelElement> elements = module.getAllNestedElements().toList();
			for(IRPModelElement element:elements)
			{
				String metaClass = element.getMetaClass();
				
				if(metaClass.equals("Operation"))
				{
					IRPOperation function = (IRPOperation)element;
					RhapsodyOperationCompletion roc = new RhapsodyOperationCompletion(aProvider, function, false);
					aProvider.addCompletion(roc);					
				}
				if(metaClass.equals("Type"))
				{
					IRPType type = (IRPType)element;
					RhapsodyTypeCompletion rtc = new RhapsodyTypeCompletion(aProvider, type);
					aProvider.addCompletion(rtc);
				}
				if(metaClass.equals("Attribute"))
				{
					IRPAttribute variable = (IRPAttribute)element;
					RhapsodyVariableCompletion rvc = new RhapsodyVariableCompletion(aProvider, variable, false);
					aProvider.addCompletion(rvc);
				}
				
			}
		}
			
		for(IRPClass rclass : (List<IRPClass>)aPackage.getClasses().toList())
		{	
			if(rclass!=null)
			{
				RhapsodyClassifierCompletion rcc = new RhapsodyClassifierCompletion(aProvider, rclass, false, false );
				aProvider.addCompletion(rcc);
			}
		}
		for(IRPPackage p : (List<IRPPackage>)aPackage.getPackages().toList())
		{
			if(p!=null)
			{
				if((namespacename==null)||(namespacename.equals(myNamespaceName)))
				{
					addElements(p,aProvider);
				}
			}
		}
		for(IRPType t : (List<IRPType>)aPackage.getTypes().toList())
		{
			if(t!=null)
			{
				RhapsodyTypeCompletion rtc = new RhapsodyTypeCompletion(aProvider, t);
				aProvider.addCompletion(rtc);
			}
		}
		for(IRPVariable v: (List<IRPVariable>)aPackage.getGlobalVariables().toList())
		{
			if(v!=null)
			{
				RhapsodyVariableCompletion rvc = new RhapsodyVariableCompletion(aProvider, v, false);
				aProvider.addCompletion(rvc);
				
			}
		}
		
		
		for(IRPOperation f: (List<IRPOperation>)aPackage.getGlobalFunctions().toList())
		{
			if(f!=null)
			{
				RhapsodyOperationCompletion roc = new RhapsodyOperationCompletion(aProvider, f, false);
				aProvider.addCompletion(roc);
			}
		}
					
	}
	
	public List<Completion> getCompletions()
	{
		return completions;
	}
	
	
	@Override
	public void addCompletion(Completion aCompletion)
	{
	
		if(checkCompletionAdded(completions, aCompletion))
		{
			return;
		}
		
		super.addCompletion(aCompletion);

	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Completion> getCompletionByInputText(String inputText) {
		
		List<Completion> retVal = new ArrayList<Completion>();
		if (inputText!=null) {

			int index = Collections.binarySearch(completions, inputText, comparator);
			if (index<0) { // No exact match
				index = -index - 1;
			}
			else {
				// If there are several overloads for the function being
				// completed, Collections.binarySearch() will return the index
				// of one of those overloads, but we must return all of them,
				// so search backward until we find the first one.
				int pos = index - 1;
				while (pos>0 &&
						comparator.compare(completions.get(pos), inputText)==0) {
					retVal.add(completions.get(pos));
					pos--;
				}
			}

			while (index<completions.size()) {
				Completion c = completions.get(index);
				if (Util.startsWithIgnoreCase(c.getInputText(), inputText)) {
					retVal.add(c);
					index++;
				}
				else {
					break;
				}
			}

		}

		return retVal;
	}
	
	
	private boolean checkCompletionAdded(List<Completion> aCompletionList, Completion aCompletion)
	{
		int ret = Collections.binarySearch(aCompletionList, aCompletion);
		
		if(ret>=0)
		{
			return true;
		}
		
		return false;
		
	}
	

}
