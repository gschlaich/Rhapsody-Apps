package apps;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;

import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.FunctionCompletion;
import org.fife.ui.autocomplete.ParameterizedCompletion;
import org.fife.ui.autocomplete.Util;
import org.fife.ui.autocomplete.VariableCompletion;
import org.fife.ui.autocomplete.ParameterizedCompletion.Parameter;

import com.telelogic.rhapsody.core.IRPArgument;
import com.telelogic.rhapsody.core.IRPAttribute;
import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPCollection;
import com.telelogic.rhapsody.core.IRPDependency;
import com.telelogic.rhapsody.core.IRPEvent;
import com.telelogic.rhapsody.core.IRPEventReception;
import com.telelogic.rhapsody.core.IRPGeneralization;
import com.telelogic.rhapsody.core.IRPInterfaceItem;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPOperation;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPRelation;
import com.telelogic.rhapsody.core.IRPStatechart;

import RhapsodyUtilities.RhapsodyOperation;



public class ClassifierCompletionProvider extends DefaultCompletionProvider {
	
	private IRPClassifier myClassifier;
	private visibility myVisibility;
	private String myClassifierName;
	
	private LocalCompletionProvider myLocalCompletionProvider;
	
	private int myBaseRelevance = 100;
	
	enum visibility {
		v_private,
		v_protected,
		v_public
	}
	
	public ClassifierCompletionProvider(IRPClassifier aClassifier, visibility aVisibility) {
		myClassifier = aClassifier;
		myVisibility = aVisibility;
		super.setParameterizedCompletionParams('(', ", ", ')');
		myClassifierName = myClassifier.getName();
		createClassCompletion(myClassifier, myVisibility);		
	}
	
	public ClassifierCompletionProvider()
	{
		super();
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
	private void createClassCompletion(IRPClassifier aClassifier, visibility aVisibility) {

		//IRPCollection operations = myClass.getOperations();
		
		RhapsodyClassifierCompletion rcm = new RhapsodyClassifierCompletion(this, aClassifier);
		addCompletion(rcm);
		
		List<IRPOperation> operations = aClassifier.getOperations().toList();
		
		for (IRPOperation operation  : operations) 
		{
			
			if(operation.getVisibility().equals("private")&&(aVisibility!=visibility.v_private)){
				continue;
			}
			if(operation.getVisibility().equals("protected")&&(aVisibility==visibility.v_public)) {
				continue;
			}
			
				RhapsodyOperationCompletion oc = new RhapsodyOperationCompletion(this, operation);
				oc.setDefinedIn(aClassifier.getName());
				oc.setRelevance(myBaseRelevance);
				addCompletion(oc);
			
		}
		
		//check if class has a state chart
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
						//RhapsodyClassifierCompletion ercc = new RhapsodyClassifierCompletion(this, er);
						
						//this.addCompletion(ercc);
						IRPEvent ev = er.getEvent();
						RhapsodyClassifierCompletion ercc = new RhapsodyClassifierCompletion(this, ev);
						this.addCompletion(ercc);
						//RhapsodyOperationCompletion evc = new RhapsodyOperationCompletion(this, ev);
						//this.addCompletion(evc);
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
			
			RhapsodyAttributeCompletion rvc = new RhapsodyAttributeCompletion(this, attribute);
			rvc.setRelevance(myBaseRelevance-10);
			addCompletion(rvc);
			
		}
		
		//Relations
		List<IRPRelation> relations = aClassifier.getRelations().toList();
		
		for(IRPRelation relation: relations)
		{
			RhapsodyRelationCompletion rrc = new RhapsodyRelationCompletion(this, relation);
			rrc.setRelevance(myBaseRelevance-10);
			
			addCompletion(rrc);
			
		}
		
		//Dependencies
		List<IRPDependency> dependencies = aClassifier.getDependencies().toList();
		for(IRPDependency dependency : dependencies)
		{
			IRPModelElement modelElement = dependency.getDependsOn();
			
			if((aVisibility == visibility.v_public)&&(dependency.getPropertyValue("CG.Dependency.UsageType").equals("Implementation")))
			{
				continue;
			}
			
			
			
			if(modelElement instanceof IRPClassifier)
			{
				IRPClassifier classifier = (IRPClassifier)modelElement;
				RhapsodyClassifierCompletion rcc = new RhapsodyClassifierCompletion(this, classifier);
				rcc.setRelevance(myBaseRelevance-10);
				addCompletion(rcc);
			}
			else
			{
				BasicCompletion bc = new BasicCompletion(this, dependency.getName());
				bc.setShortDescription(dependency.getDescription());
				addCompletion(bc);
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
				RhapsodyClassifierCompletion rcc = new RhapsodyClassifierCompletion(this, baseClass);
				rcc.setIcon(new ImageIcon(generalization.getIconFileName().replace("\\","/")));
				addCompletion(rcc);
				if(aVisibility==visibility.v_public)
				{
					createClassCompletion(baseClass, visibility.v_public);
				}
				else
				{
					createClassCompletion(baseClass, visibility.v_protected);
				}
			}
		}
		
		//nested classes
		List<IRPClassifier> nestedClasses = aClassifier.getNestedClassifiers().toList();
		for(IRPClassifier nestedClass : nestedClasses)
		{
			RhapsodyClassifierCompletion rcc = new RhapsodyClassifierCompletion(this, nestedClass);
			rcc.setRelevance(myBaseRelevance-10);
			addCompletion(rcc);
		}
			

	}
	
	
	
	
	
	@Override
	public void addCompletion(Completion aCompletion)
	{
		//check if completion already added
		//TODO optimize search
		for(Completion storedCompetion: completions)
		{
			if(storedCompetion.compareTo(aCompletion)==0)
			{
				//do not add
				return;
			}
		}
		super.addCompletion(aCompletion);
	}
	
	public Completion getFirstCompletion(String aCompletionName)
	{
		//TODO optimize search
		for(Completion completion: completions)
		{
			if(completion.getInputText().equals(aCompletionName))
			{
				//do not add
				return completion;
			}
		}
		return null;
		
	}
	
	@Override
	public List<Completion> getCompletionsAt(JTextComponent tc, Point p) {
		
		return super.getCompletionsAt(tc, p);
	}
	
	/**
	 * {@inheritDoc}
	 */
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
		
		if ((text.indexOf("->")==-1)&&(text.indexOf(".")==-1)&&(text.indexOf("::")==-1))
		{
			List<Completion> ret = super.getCompletionsImpl(comp);
			if(myLocalCompletionProvider!=null)
			{
				List<Completion> local = myLocalCompletionProvider.getCompletions(comp);
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
		boolean ref = false;
		boolean st = false;
		
		//TODO search from end for string
		
		int endDD = text.lastIndexOf("::");
		int endRef = text.lastIndexOf("->");
		int end = text.lastIndexOf(".");
		
		int startSearch=end+1;
		
		
		if(endRef>end)
		{
			ref = true;
			end = endRef;
			startSearch = end+2;
		}
		if(endDD>end)
		{
			st = true;
			end = endDD;
			ref = false;
			startSearch = end+2;
		}
		
		lookForProvider = text.substring(0, end);
		
		
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
		
		if((outerCompletionList!=null)&&(outerCompletionList.size()==1))
		{
			c = outerCompletionList.get(0);
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
			if(rc.isPointer()!=ref)
			{
				//no completion fit...
				return cs;
			}
			
			//check for visibility
			
			visibility v = visibility.v_public;
			
			List<IRPGeneralization> gens = myClassifier.getGeneralizations().toList();
			for(IRPGeneralization gen:gens)
			{
				System.out.println(gen.getBaseClass().getName());
				if(gen.getBaseClass().equals(rc.getIRPClassifier()))
				{
					v = visibility.v_protected;
					break;
				}
			}
			
			ClassifierCompletionProvider ccp = new ClassifierCompletionProvider(rc.getIRPClassifier(), v);
				
				System.out.println("searchText  " + searchText );
				return ccp.getSubCompletions(searchText);
		}
		else if((c!=null)&&(c instanceof RhapsodyNamespaceCompletion))
		{
				
			if(st==false)
			{
				return cs;
			}
			
			RhapsodyNamespaceCompletion rnc = (RhapsodyNamespaceCompletion)c;
			List<IRPClassifier> classifiers = rnc.getClassifiers();
			for(IRPClassifier classifier:classifiers)
			{
				//cs.addAll(getSubCompletions(classifier.getName()));
				if(classifier!=null)
				{
					RhapsodyClassifierCompletion rcc = new RhapsodyClassifierCompletion(myLocalCompletionProvider, classifier, true);
					myLocalCompletionProvider.addCompletion(rcc);
					cs.add(rcc);	
				}
				else
				{
					boolean stop=true;
				}
				
			}
			return myLocalCompletionProvider.getCompletionByInputText(searchText);	
				
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
