package de.schlaich.gunnar.rhapsody.completionprovider;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;

import org.apache.commons.imaging.icc.IccProfileParser;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.Util;
import com.telelogic.rhapsody.core.IRPAttribute;
import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPCollection;
import com.telelogic.rhapsody.core.IRPDependency;
import com.telelogic.rhapsody.core.IRPEvent;
import com.telelogic.rhapsody.core.IRPEventReception;
import com.telelogic.rhapsody.core.IRPGeneralization;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPOperation;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPRelation;
import com.telelogic.rhapsody.core.IRPStereotype;

import de.schlaich.gunnar.rhapsody.completion.RhapsodyAttributeCompletion;
import de.schlaich.gunnar.rhapsody.completion.RhapsodyClassifier;
import de.schlaich.gunnar.rhapsody.completion.RhapsodyClassifierCompletion;
import de.schlaich.gunnar.rhapsody.completion.RhapsodyLocalVariableCompletion;
import de.schlaich.gunnar.rhapsody.completion.RhapsodyNamespaceCompletion;
import de.schlaich.gunnar.rhapsody.completion.RhapsodyOperationCompletion;
import de.schlaich.gunnar.rhapsody.completion.RhapsodyRelationCompletion;
import de.schlaich.gunnar.rhapsody.utilities.RhapsodyOperation;



public class ClassifierCompletionProvider extends DefaultCompletionProvider {
	
	private IRPClassifier myClassifier;
	private visibility myVisibility;
	private String myClassifierName;
	private long myStartTime = 0;
	
	private int myCompletionCount = 0;
	
	private LocalCompletionProvider myLocalCompletionProvider;
	
	private int myBaseRelevance = 100;
	
	private IRPPackage myNameSpace = null;
	
	private static Map<IRPClassifier,ClassifierCompletionProvider> ClassifierCompletionProviders = null;
	
	public enum visibility {
		v_private,
		v_protected,
		v_public
	}
	
	private ClassifierCompletionProvider(IRPClassifier aClassifier, visibility aVisibility, boolean addType) {
		myClassifier = aClassifier;
		myVisibility = aVisibility;
		myStartTime = new Date().getTime();
		super.setParameterizedCompletionParams('(', ", ", ')');
		myClassifierName = myClassifier.getName();
		
		RhapsodyLocalVariableCompletion thisCompletion = new RhapsodyLocalVariableCompletion(this, "this", myClassifier, addType);
		addCompletion(thisCompletion);
		myNameSpace = RhapsodyOperation.getNamespacePackage(myClassifier);
		
		createClassCompletion(myClassifier, myVisibility, addType);		
	}
	
	private ClassifierCompletionProvider()
	{
		super();
	}
	
	public static ClassifierCompletionProvider GetProvider(IRPClassifier aClassifier, visibility aVisibility, boolean addType)
	{
		ClassifierCompletionProvider ret = null;
		
		if(ClassifierCompletionProviders==null)
		{
			ClassifierCompletionProviders = new HashMap<IRPClassifier, ClassifierCompletionProvider>();
		}
		
		ret = ClassifierCompletionProviders.get(aClassifier);
		if(ret==null)
		{
			ret = new ClassifierCompletionProvider(aClassifier, aVisibility, addType);
			ClassifierCompletionProviders.put(aClassifier, ret);
		}
		
		return ret;
	}
	
	public int getBaseRelevance() {
		return myBaseRelevance;
	}
	
	public void setBaseRelevance(int aBaseRelevance) {
		this.myBaseRelevance = aBaseRelevance;
	}
	
	public IRPClassifier getClassifier()
	{
		return myClassifier;
	}
	
	
	@SuppressWarnings("unchecked")
	public void createClassCompletion(IRPClassifier aClassifier, visibility aVisibility, boolean addType) {

		//IRPCollection operations = myClass.getOperations();
		if(addType)
		{
			IRPPackage classifiernamespace = RhapsodyOperation.getNamespacePackage(aClassifier);
			if((classifiernamespace==null)||(classifiernamespace==myNameSpace))
			{
				RhapsodyClassifierCompletion rcm = new RhapsodyClassifierCompletion(this, aClassifier);
				addCompletion(rcm);
			}
		}
		
		List<IRPOperation> operations = aClassifier.getOperations().toList();
		
		
		
		for (IRPOperation operation  : operations) 
		{
			
			if(operation.getVisibility().equals("private")&&(aVisibility!=visibility.v_private))
			{
				continue;
			}
			if(operation.getVisibility().equals("protected")&&(aVisibility==visibility.v_public)) 
			{
				continue;
			}
				
			RhapsodyOperationCompletion oc = new RhapsodyOperationCompletion(this, operation, addType);
			oc.setDefinedIn(aClassifier.getFullPathNameIn());
			oc.setRelevance(myBaseRelevance);
			addCompletion(oc);
			
		}
		
		
		//check if class has a state chart
		if(addType)
		{
			if(aClassifier instanceof IRPClass)
			{
				IRPClass c = (IRPClass)aClassifier;
				
				if(c.getIsReactive()==1)
				{
					List<IRPModelElement> elements = c.getNestedElements().toList();
					for(IRPModelElement e : elements)
					{
						if(e instanceof IRPEventReception)
						{
							
							IRPEventReception er = (IRPEventReception)e;
							
							IRPEvent ev = er.getEvent();
							
							IRPPackage evNameSpace = RhapsodyOperation.getNamespacePackage(ev);
							if((evNameSpace==null)||(evNameSpace==myNameSpace))
							{
								RhapsodyClassifierCompletion ercc = new RhapsodyClassifierCompletion(this, ev);
								this.addCompletion(ercc);
							}
							
						}
					}
				}
				
			}
		}
		
		
		
		//attributes
		List<IRPAttribute> attributes = aClassifier.getAttributes().toList();
		
		for(IRPAttribute attribute : attributes)
		{
			
			if(attribute.getVisibility().equals("private")&&(myVisibility!=visibility.v_private)){
				continue;
			}
			if(attribute.getVisibility().equals("protected")&&(myVisibility==visibility.v_public)) {
				continue;
			}
			
			RhapsodyAttributeCompletion rvc = new RhapsodyAttributeCompletion(this, attribute, addType);
			rvc.setRelevance(myBaseRelevance-10);
			addCompletion(rvc);
			
		}
		
		//Relations
		List<IRPRelation> relations = aClassifier.getRelations().toList();
		
		for(IRPRelation relation: relations)
		{
			RhapsodyRelationCompletion rrc = new RhapsodyRelationCompletion(this, relation, addType);
			rrc.setRelevance(myBaseRelevance-10);
			
			addCompletion(rrc);
			
		}
		
		//Dependencies
		if(addType)
		{
		
			List<IRPDependency> dependencies = aClassifier.getDependencies().toList();
			for(IRPDependency dependency : dependencies)
			{
				System.out.println(dependency.getName());
				
				IRPModelElement modelElement = dependency.getDependsOn();
				
				if((aVisibility == visibility.v_public)&&(dependency.getPropertyValue("CG.Dependency.UsageType").equals("Implementation")))
				{
					continue;
				}
				
				
				if(modelElement instanceof IRPClassifier)
				{
					IRPClassifier classifier = (IRPClassifier)modelElement;
					if(classifier.getLanguage().equals("C"))
					{
						//this is not a class! 
						if(aVisibility == visibility.v_private)
						{
							createClassCompletion(classifier, aVisibility, addType);
						}
					}
					else
					{
						if(addType)
						{
							IRPPackage classifiernamespace = RhapsodyOperation.getNamespacePackage(classifier);
							if((classifiernamespace==null)||(classifiernamespace==myNameSpace))
							{
								RhapsodyClassifierCompletion rcc = new RhapsodyClassifierCompletion(this, classifier,false,false);
								rcc.setRelevance(myBaseRelevance-10);
								addCompletion(rcc);
							}
						}
					}
				}
				else
				{
					BasicCompletion bc = new BasicCompletion(this, dependency.getName());
					bc.setShortDescription(dependency.getDescription());
					addCompletion(bc);
				}
				
			}
		}
		
		myBaseRelevance--;
		
		
		//generalizations		
		IRPCollection generalizations = aClassifier.getGeneralizations();
		for(Object g : generalizations.toList())
		{
			if(g instanceof IRPGeneralization)
			{
				IRPGeneralization generalization = (IRPGeneralization)g;
				IRPClassifier baseClass = generalization.getBaseClass();
				if(addType)
				{
					IRPPackage baseclassnamespace = RhapsodyOperation.getNamespacePackage(baseClass);
					if((baseclassnamespace==null)||(baseclassnamespace==myNameSpace))
					{
						RhapsodyClassifierCompletion rcc = new RhapsodyClassifierCompletion(this, baseClass);
						rcc.setIcon(new ImageIcon(generalization.getIconFileName().replace("\\","/")));
						addCompletion(rcc);
					}
				}
				if(aVisibility==visibility.v_public)
				{
					createClassCompletion(baseClass, visibility.v_public, addType);
				}
				else
				{
					createClassCompletion(baseClass, visibility.v_protected, addType);
				}
			}
		}
		
		
		//nested classes
		List<IRPClassifier> nestedClasses = aClassifier.getNestedClassifiers().toList();
		for(IRPClassifier nestedClass : nestedClasses)
		{
			RhapsodyClassifierCompletion rcc = new RhapsodyClassifierCompletion(this, nestedClass,true,false);
			rcc.setRelevance(myBaseRelevance-10);
			addCompletion(rcc);
		}
			

	}
	
	
	
	
	
	@SuppressWarnings("unchecked")
	@Override
	public void addCompletion(Completion aCompletion)
	{
		if(isInserted(aCompletion)==false)
		{
			myCompletionCount++;
			Date date = new Date();
			double time = (double)(date.getTime()-myStartTime)/1000;
			System.out.println("Insert(" + myCompletionCount +") " + aCompletion.getInputText() + ": " + time);
			try
			{
				super.addCompletion(aCompletion);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				System.out.println("Sort failed Completion: "+ aCompletion.getInputText());
			}
			completions.sort(comparator);
		}
		
	}
	
	public Completion getFirstCompletion(final String aCompletionName)
	{
		
		List<Completion> completions = super.getCompletionByInputText(aCompletionName);
		if(completions==null||completions.isEmpty())
		{
			return null;
		}
		return completions.get(0);
		
	}
	
	@Override
	public List<Completion> getCompletionsAt(JTextComponent tc, Point p) {
		
		return super.getCompletionsAt(tc, p);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected List<Completion> getCompletionsImpl(JTextComponent comp) 
	{
		List<Completion> cs = new ArrayList<Completion>();
		
		//TODO rework this function!!!!
		if(myVisibility==visibility.v_private)
		{
			// importing completions
			
			myLocalCompletionProvider = new LocalCompletionProvider(comp.getText(),this);
			
			
				
			
		}
		
		String text = getTextForImport(comp);
		
		
		
		// TODO improve this
		if(text.indexOf("(")!=-1)
		{
			text = text.substring(text.lastIndexOf("(")+1);
		}
		if(text.indexOf(" ")!=-1)
		{
			text = text.substring(text.lastIndexOf(" ")+1);
		}
		if(text.indexOf("=")!=-1)
		{
			text = text.substring(text.lastIndexOf("=")+1);
		}
		if(text.indexOf("<")!=-1)
		{
			text = text.substring(text.lastIndexOf("<")+1);
		}
		if(text.indexOf(",")!=-1)
		{
			text = text.substring(text.lastIndexOf(",")+1);
		}
		
		
		
		
		if ((text.indexOf("->")==-1)&&(text.indexOf(".")==-1)&&(text.indexOf("::")==-1))
		{
			List<Completion> ret = super.getCompletionsImpl(comp);
			if(myLocalCompletionProvider!=null)
			{
				//myLocalCompletionProvider.removeTempCompletions();
				//List<Completion> local = myLocalCompletionProvider.getCompletions(comp);
				List<Completion> local = myLocalCompletionProvider.getCompletionByInputText(text);
				if(local!=null)
				{
					if(ret!=null)
					{
						ret.addAll(local);
					}
					else
					{
						ret = local;
					}
				}
			}
			
			return ret;
		}
		
		
		String lookForProvider = "";
		boolean isPointer = false;
		boolean st = false;
		
		//TODO search from end for string
		
		int endDD = text.lastIndexOf("::");
		int endRef = text.lastIndexOf("->");
		int end = text.lastIndexOf(".");
		
		int startSearch=end+1;
		
		
		if(endRef>end)
		{
			isPointer = true;
			end = endRef;
			startSearch = end+2;
		}
		if(endDD>end)
		{
			st = true;
			end = endDD;
			isPointer = false;
			startSearch = end+2;
		}
		
		lookForProvider = text.substring(0, end);
		//try to extract namespace...
		endDD = lookForProvider.lastIndexOf("::");
		String namespacename = null;
		if(endDD>0)
		{
			namespacename = lookForProvider.substring(0, endDD);
			for(int i = namespacename.length()-1;i>=0;--i)
			{	
				
				if(isValidChar(namespacename.charAt(i))==false)
				{
					namespacename = namespacename.substring(i+1);
					break;
				}
			}
		}
		
		for(int i = lookForProvider.length()-1;i>=0;--i)
		{	
			
			if(isValidChar(lookForProvider.charAt(i))==false)
			{
				lookForProvider = lookForProvider.substring(i+1);
				break;
			}
		}
		
		
		System.out.println("lookForProvider " + lookForProvider );
		String searchText = text.substring(startSearch);
		if(searchText==null)
		{
			searchText = ""; 
		}
		List<Completion> outerCompletionList = this.getCompletionByInputText(lookForProvider);
		Completion c = null;
		if(outerCompletionList==null)
		{
			outerCompletionList = myLocalCompletionProvider.getCompletionByInputText(lookForProvider);
		}
		
		if((outerCompletionList==null)||(outerCompletionList.isEmpty()))
		{
			if(namespacename!=null)
			{
				Completion compl = getFirstCompletion(namespacename);
				if((compl!=null)&&(compl instanceof RhapsodyNamespaceCompletion))
				{
					RhapsodyNamespaceCompletion rnc = (RhapsodyNamespaceCompletion)compl;
					NamespaceCompletionProvider ncp = NamespaceCompletionProvider.GetNameSpaceProvider(rnc);
					outerCompletionList = ncp.getCompletionByInputText(lookForProvider);
				}
			}
			
			//outerCompletionList = NamespaceCompletionProvider.GetCompletions(lookForProvider);
		}
		
		if(outerCompletionList!=null)
		{
			//search for complete same..
			for(Completion cc : outerCompletionList)
			{
				if(cc.getReplacementText().equals(lookForProvider))
				{
					if(cc instanceof RhapsodyClassifier)
					{
						c = cc;
						break;
					}
				}
			}
		}
		else
		{
			//search in model
			
			IRPClassifier cl = RhapsodyOperation.findClassifier(myClassifier, lookForProvider);
			
			if(cl!=null)
			{
				c = new RhapsodyClassifierCompletion(myLocalCompletionProvider, cl);
				myLocalCompletionProvider.addCompletion(c);
			}
		}
		if((c!=null)&&((c instanceof  RhapsodyNamespaceCompletion)==false))
		{
			
			
			RhapsodyClassifier rc = (RhapsodyClassifier)c;
			
			if(isPointer)
			{
				if(rc.isPointer()==false)
				{
					//no completion fit...
					return cs;
				}
			}
			else
			{
				if(rc.isValue()==false)
				{
					return cs;
				}
			}
			
			
			//check for visibility
			
			visibility v = visibility.v_public;
			
			List<IRPGeneralization> gens = myClassifier.getGeneralizations().toList();
			for(IRPGeneralization gen:gens)
			{
				
				if(gen.getBaseClass().equals(rc.getIRPClassifier(isPointer)))
				{
					v = visibility.v_protected;
					break;
				}
			}
			
			ClassifierCompletionProvider ccp = ClassifierCompletionProvider.GetProvider(rc.getIRPClassifier(isPointer), v, false);					
			return ccp.getSubCompletions(searchText);
		}
		else if((c!=null)&&(c instanceof RhapsodyNamespaceCompletion))
		{
				
			if(st==false)
			{
				return cs;
			}
			
			RhapsodyNamespaceCompletion rnc = (RhapsodyNamespaceCompletion)c;
			NamespaceCompletionProvider nameSpaceCompletionProvider = NamespaceCompletionProvider.GetNameSpaceProvider(rnc);
			return nameSpaceCompletionProvider.getCompletionByInputText(searchText);	
				
		}
		else
		{
			return cs;
		}
	}
	

	public String getTextForImport(JTextComponent comp) {

		Document doc = comp.getDocument();

		int dot = comp.getCaretPosition();
		Element root = doc.getDefaultRootElement();
		int index = root.getElementIndex(dot);
		Element elem = root.getElement(index);
		int start = elem.getStartOffset();
		int len = dot-start;
		try {
			doc.getText(start, len, seg);
		} catch (BadLocationException ble) {
			ble.printStackTrace();
			return EMPTY_STRING;
		}
		
		
		return len==0 ? EMPTY_STRING : new String(seg.array,start,len);

	}
	
	public List<Completion> getCompletions()
	{
		return completions;
	}
	
	
	private boolean isInserted(Completion aCompletion)
	{
		if(aCompletion.getInputText().isEmpty())
		{
			return true;
		}
		if(aCompletion instanceof RhapsodyClassifier)
		{
			RhapsodyClassifier arc = (RhapsodyClassifier)aCompletion;
			
			IRPModelElement are = arc.getElement();
			/*
			if(are==null)
			{
				System.out.println("no element in :" + aCompletion.getInputText());
				return true;
			}
			*/
			
			List<Completion> foundCompletions = super.getCompletionByInputText(aCompletion.getInputText());
			if(foundCompletions==null)
			{
				return false;
			}
			for(Completion c:foundCompletions)
			{
				if(c instanceof RhapsodyClassifier)
				{
					RhapsodyClassifier rc = (RhapsodyClassifier)c;
					IRPModelElement re = rc.getElement();
					if((are!=null)&&(re!=null))
					{
						if(re.equals(are))
						{
							return true;	
						}
					}
					else
					{
						if(c.compareTo(aCompletion)==0)
						{
							return true;
						}
					}
					
				}
				
			}
		}
		else
		{
			if(getFirstCompletion(aCompletion.getInputText())!=null)
			{
				return true;
			}
		}
		return false;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Completion> getSubCompletions(String text) {

		List<Completion> retVal = new ArrayList<Completion>();
		
		if (text!=null) {

			int index = Collections.binarySearch(completions, text, comparator);
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
						comparator.compare(completions.get(pos), text)==0) {
					retVal.add(completions.get(pos));
					pos--;
				}
			}

			while (index<completions.size()) {
				Completion c = completions.get(index);
				if (Util.startsWithIgnoreCase(c.getInputText(), text)) {
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


	
	

}
