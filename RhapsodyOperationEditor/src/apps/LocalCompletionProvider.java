package apps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.Util;
import org.fife.ui.autocomplete.VariableCompletion;

import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPProject;

import RhapsodyUtilities.ASTHelper;
import RhapsodyUtilities.RhapsodyOperation;

public class LocalCompletionProvider extends DefaultCompletionProvider
{
	
	private ClassifierCompletionProvider itsCompletionProvider = null;
	private String myOperationBody;
	
	
	
	public LocalCompletionProvider(String aOperationBody, ClassifierCompletionProvider aCompletionProvider) {
		itsCompletionProvider = aCompletionProvider;
		createLocalCompletions(aOperationBody,aCompletionProvider);
	}
	
	private void createLocalCompletions(String aOperationBody, ClassifierCompletionProvider aCompletionProvider)
	{
		myOperationBody = aOperationBody;
		IASTTranslationUnit astTranslationUnit = ASTHelper.getTranslationUnit(aOperationBody, aCompletionProvider);
		if(astTranslationUnit!=null)
		{
			collectCompletions(astTranslationUnit);
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
	
	private void collectCompletions(IASTNode aNode)
	{
        
        if(aNode instanceof IASTSimpleDeclaration)
        {
        	boolean isReference = false;
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
        					isReference = false;
        				}
        				else if(c instanceof RhapsodyClassifier)
        				{
        					classifier = (RhapsodyClassifier)c;
        					isReference = classifier.isPointer();
        					irpClassifier = classifier.getIRPClassifier();
        				}
        				else if(classifier!=null)
        				{	
        					List<IRPClassifier> classifiers = classifier.getNestedClassifiers();
        					for(IRPClassifier ic : classifiers)
        					{
        						if(ic.getName().equals(name.toString()))
        						{
        							irpClassifier = ic;
        							isReference = false;
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
											isReference=true;
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
        			IASTDeclarator[] declarators =  simpleDeclaration.getDeclarators();
            		if((c!=null)&&(c instanceof RhapsodyClassifierCompletion))
            		{
            			RhapsodyClassifier rcc = (RhapsodyClassifier)c;

            			isReference = rcc.isPointer();
            			irpClassifier = rcc.getIRPClassifier();

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
        					isReference = true;
        				}
        				RhapsodyLocalVariableCompletion rlvc = new RhapsodyLocalVariableCompletion(this, declarator.getName().toString(), irpClassifier,isReference);
        				this.addCompletion(rlvc);
        			}
        		}
        		else
        		{
        			
        			
        			System.out.println("No Rhapsody classifier found for "+ classifierName);
        			
        			BasicCompletion bc = new BasicCompletion(this, classifierName);
        			this.addCompletion(bc);
        			
        			for(IASTDeclarator declarator : declarators)
        			{
        				
        				VariableCompletion vc = new VariableCompletion(this, declarator.getName().toString(), classifierName);
        				vc.setDefinedIn("local variable");
        				this.addCompletion(vc);
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
			    				
			    				VariableCompletion vc = new VariableCompletion(this, declarator.getName().toString(),classifierName);
			    				vc.setDefinedIn("local variable");
			    				this.addCompletion(vc);
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
