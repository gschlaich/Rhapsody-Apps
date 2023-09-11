package de.schlaich.gunnar.rhapsody.completionprovider;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;

import org.eclipse.cdt.core.dom.ast.ExpansionOverlapsBoundaryException;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTPointerOperator;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNameSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateId;
import org.eclipse.cdt.core.parser.IToken;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTQualifiedName;
import org.fife.ui.autocomplete.AbstractCompletionProvider;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.ParameterizedCompletion;
import org.fife.ui.autocomplete.Util;
import org.fife.ui.autocomplete.VariableCompletion;

import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPProject;

import de.schlaich.gunnar.rhapsody.completion.ASTUtilities;
import de.schlaich.gunnar.rhapsody.completion.RhapsodyClassifier;
import de.schlaich.gunnar.rhapsody.completion.RhapsodyClassifierCompletion;
import de.schlaich.gunnar.rhapsody.completion.RhapsodyLocalVariableCompletion;
import de.schlaich.gunnar.rhapsody.utilities.ASTHelper;
import de.schlaich.gunnar.rhapsody.utilities.RhapsodyOperation;

public class LocalCompletionProvider extends DefaultCompletionProvider
{
	
	private ClassifierCompletionProvider itsCompletionProvider = null;
	
	private ParseCompletionProvider itsParseCompletionProvider = null;
	
	public LocalCompletionProvider(String aOperationBody, ClassifierCompletionProvider aCompletionProvider) {
		itsCompletionProvider = aCompletionProvider;
		itsParseCompletionProvider = new ParseCompletionProvider();
		createLocalCompletions(aOperationBody,aCompletionProvider);	
	}
	
	private void createLocalCompletions(String aOperationBody, ClassifierCompletionProvider aCompletionProvider)
	{
		IASTTranslationUnit astTranslationUnit = ASTUtilities.getTranslationUnit(aOperationBody, aCompletionProvider);
		if(astTranslationUnit!=null)
		{
			collectCompletions(astTranslationUnit);
		}
		
	}
	
	public void removeTempCompletions()
	{
		completions.clear();
	}
	
	
	
	@Override
	public void addCompletion(Completion aCompletion)
	{
		if(itsParseCompletionProvider.completionExists(aCompletion))
		{
			return;
		}
		if(checkCompletionAdded(completions, aCompletion))
		{
			return;
		}
		
		super.addCompletion(aCompletion);
		
		/*
		
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
		*/
	}
	
	
	private boolean checkCompletionAdded(List<Completion> aCompletionList, Completion aCompletion)
	{
		int ret = Collections.binarySearch(aCompletionList, aCompletion);
		
		if(ret>=0)
		{
			return true;
		}
		
		return false;
		
		/*
		for(Completion storedCompletion : aCompletionList)
		{
			if(storedCompletion.compareTo(aCompletion)==0)
			{
				return true;
			}
		}
		return false;
		*/
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Completion> getCompletionByInputText(String inputText) {
		
		List<Completion> retVal = new ArrayList<Completion>();
		
		List<Completion> fromParse = itsParseCompletionProvider.getCompletionByInputText(inputText);
		
		if(fromParse!=null)
		{
			retVal.addAll(fromParse);
		}

		
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
	
	
	public Completion getFirstCompletion(final String aCompletionName)
	{
		
		List<Completion> completions = getCompletionByInputText(aCompletionName);
		if(completions==null||completions.isEmpty())
		{
			return null;
		}
		return completions.get(0);
		
	}
	
	private void collectCompletions(IASTNode aNode)
	{
        
        // TODO: rewrite this operation ( use ASTHelper )
		if(aNode instanceof IASTSimpleDeclaration)
        {
        	boolean isPointer = false;
        	IASTSimpleDeclaration simpleDeclaration = (IASTSimpleDeclaration)aNode;
        	IASTDeclSpecifier specifier =  simpleDeclaration.getDeclSpecifier();
        	if(specifier instanceof IASTNamedTypeSpecifier)
        	{
        		IASTNamedTypeSpecifier namedSpec = (IASTNamedTypeSpecifier)specifier;
        		IRPClassifier irpClassifier = null;
        		String classifierName = namedSpec.getName().getLastName().toString();
        		if(namedSpec.getName() instanceof CPPASTQualifiedName)
        		{
        			CPPASTQualifiedName qName = (CPPASTQualifiedName)namedSpec.getName();
        			ICPPASTNameSpecifier[] names = qName.getAllSegments();
        			RhapsodyClassifier classifier = null;
        			
        			
        			for(ICPPASTNameSpecifier name : names)
        			{
        				String completionName;
        				List<String> templateArguments = new ArrayList<String>();
        				if(name instanceof ICPPASTTemplateId)
        				{
        					ICPPASTTemplateId templateId = (ICPPASTTemplateId)name;
        					IASTName templateName = templateId.getTemplateName();
        					completionName = templateName.toString();
        					templateArguments = ASTHelper.getTemplateArguments(templateId);
        				}
        				else
        				{
        					completionName = name.toString();
        				}
        				
        				
        				Completion	c = itsCompletionProvider.getFirstCompletion(completionName);
        				if(c==null)
        				{
        					irpClassifier = RhapsodyOperation.findClassifier(itsCompletionProvider.getClassifier(), completionName);
        					isPointer = false;
        				}
        				else if(c instanceof RhapsodyClassifier)
        				{
        					classifier = (RhapsodyClassifier)c;
        					isPointer = classifier.isPointer();
        					irpClassifier = classifier.getIRPClassifier(isPointer);
        				}
        				else if(classifier!=null)
        				{	
        					List<IRPClassifier> classifiers = classifier.getNestedClassifiers(isPointer);
        					for(IRPClassifier ic : classifiers)
        					{
        						if(ic.getName().equals(name.toString()))
        						{
        							irpClassifier = ic;
        							isPointer = false;
        							break;
        						}
        					}
        				}
        				if(irpClassifier!=null)
        				{
        					if(irpClassifier.isATemplate()==1)
                			{
                				if(irpClassifier.findNestedElement("operator->", "Operation")!=null)
                				{
                					for(String arg:templateArguments)
									{
										IRPClassifier trc = RhapsodyOperation.findClassifier(irpClassifier.getProject(), arg);
										if(trc!=null)
										{
											irpClassifier = trc;
											isPointer=true;
											break;
										}
									}
	
                				}
                				
                			}

        				}
        				
        				
        			}
        			
        		}
        		else
        		{
        			Completion c = itsCompletionProvider.getFirstCompletion(classifierName);
        			simpleDeclaration.getDeclarators();
            		if((c!=null)&&(c instanceof RhapsodyClassifierCompletion))
            		{
            			RhapsodyClassifier rcc = (RhapsodyClassifier)c;

            			isPointer = rcc.isPointer();
            			irpClassifier = rcc.getIRPClassifier(isPointer);

            		}
        		}
        			
        		IASTDeclarator[] declarators =  simpleDeclaration.getDeclarators();
        		
        		if(irpClassifier==null)
        		{
        			// no dependency which fits, try to find it in the model
        			irpClassifier = RhapsodyOperation.findClassifier(itsCompletionProvider.getClassifier(), classifierName);
        		}
        		if(irpClassifier==null)
        		{
        			// hacky... search in whole project...
        			IRPProject project = itsCompletionProvider.getClassifier().getProject();
        			irpClassifier = RhapsodyOperation.findClassifier(project, classifierName);
        		}
        		if(irpClassifier!=null)
        		{
        			for(IASTDeclarator declarator : declarators)
        			{
        				IASTPointerOperator[] pointers = declarator.getPointerOperators();
        				
        				if((pointers!=null) && (pointers.length>0))
        				{
        					isPointer = true;
        				}
        				RhapsodyLocalVariableCompletion rlvc = new RhapsodyLocalVariableCompletion(itsParseCompletionProvider, declarator.getName().toString(), irpClassifier,isPointer);
        				itsParseCompletionProvider.addCompletion(rlvc);
        				
        			}
        		}
        		else
        		{
        			
        			
        			System.out.println("No Rhapsody classifier found for "+ classifierName);
        			
        			BasicCompletion bc = new BasicCompletion(itsParseCompletionProvider, classifierName);
        			itsParseCompletionProvider.addCompletion(bc);
        			
        			for(IASTDeclarator declarator : declarators)
        			{
        				
        				VariableCompletion vc = new VariableCompletion(itsParseCompletionProvider, declarator.getName().toString(), classifierName);
        				vc.setDefinedIn("local variable");
        				itsParseCompletionProvider.addCompletion(vc);
        				
        			}
        		}
       
        	}
        	else
        	{
        		
        		String classifierName = "";
        		try {
					//classifierName = specifier.getSyntax().getImage();
					
					IToken token = specifier.getSyntax();
					if(token!=null)
					{
						classifierName = token.getImage();
						if(classifierName!=null)
						{
							IASTDeclarator[] declarators =  simpleDeclaration.getDeclarators();
			    			for(IASTDeclarator declarator : declarators)
			    			{
			    				
			    				VariableCompletion vc = new VariableCompletion(itsParseCompletionProvider, declarator.getName().toString(),classifierName);
			    				vc.setDefinedIn("local variable");
			    				itsParseCompletionProvider.addCompletion(vc);
			    			}
							
						}
					}					
				} 
        		catch (ExpansionOverlapsBoundaryException e) 
        		{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		
        		
        	}
        }
        else
        {
        	IASTNode[] children = aNode.getChildren();
        	for(IASTNode node : children)
        	{
        		collectCompletions(node);
        	}   
        }   
	}    
	
}

class ParseCompletionProvider extends DefaultCompletionProvider
{

	@Override
	public List<Completion> getCompletionByInputText(String inputText) {
		
		List<Completion> retVal = new ArrayList<Completion>();
		
		if (inputText!=null) {

			@SuppressWarnings("unchecked")
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
	
	@Override
	public void addCompletion(Completion aCompletion)
	{
		if(completionExists(aCompletion))
		{
			return;
		}
		super.addCompletion(aCompletion);
	}
	
	public boolean completionExists(Completion aCompletion)
	{
		if(Collections.binarySearch(completions, aCompletion)>=0)
		{
			return true;
		}
		return false;
	}
	
	public List<Completion> getCompletions()
	{
		return completions;
	}
	
	
	
}
