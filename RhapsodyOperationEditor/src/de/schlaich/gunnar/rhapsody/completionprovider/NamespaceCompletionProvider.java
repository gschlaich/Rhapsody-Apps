package de.schlaich.gunnar.rhapsody.completionprovider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.Util;

import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPFile;
import com.telelogic.rhapsody.core.IRPOperation;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPType;
import com.telelogic.rhapsody.core.IRPVariable;

import de.schlaich.gunnar.rhapsody.completion.RhapsodyClassifierCompletion;
import de.schlaich.gunnar.rhapsody.completion.RhapsodyNamespaceCompletion;
import de.schlaich.gunnar.rhapsody.completion.RhapsodyOperationCompletion;
import de.schlaich.gunnar.rhapsody.completion.RhapsodyTypeCompletion;
import de.schlaich.gunnar.rhapsody.completion.RhapsodyVariableCompletion;

public class NamespaceCompletionProvider extends DefaultCompletionProvider {

	private IRPPackage myPackage = null;
	
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
			NamespaceCompletionProviders.put(lpackage, ret);
		}
		return ret;
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
		addElements(aPackage);
	
	}
	
	private void addElements(IRPPackage aPackage)
	{
		for(IRPClass rclass : (List<IRPClass>)aPackage.getClasses().toList())
		{
			if(rclass!=null)
			{
				RhapsodyClassifierCompletion rcc = new RhapsodyClassifierCompletion(this, rclass, false, false );
				addCompletion(rcc);
			}
		}
		for(IRPPackage p : (List<IRPPackage>)aPackage.getPackages().toList())
		{
			if(p!=null)
			{
				String namespacename = p.getNamespace();
				if((namespacename==null)||(namespacename.equals("")))
				{
					addElements(p);
				}
			}
		}
		for(IRPType t : (List<IRPType>)aPackage.getTypes().toList())
		{
			if(t!=null)
			{
				RhapsodyTypeCompletion rtc = new RhapsodyTypeCompletion(this, t);
				addCompletion(rtc);
			}
		}
		for(IRPVariable v: (List<IRPVariable>)aPackage.getGlobalVariables().toList())
		{
			if(v!=null)
			{
				RhapsodyVariableCompletion rvc = new RhapsodyVariableCompletion(this, v, false);
				addCompletion(rvc);
				
			}
		}
		
		for(IRPOperation f: (List<IRPOperation>)aPackage.getGlobalFunctions().toList())
		{
			if(f!=null)
			{
				System.out.println(f.getName());
				RhapsodyOperationCompletion roc = new RhapsodyOperationCompletion(this, f, false);
				addCompletion(roc);
			}
		}
					
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
