package apps;

import java.io.Console;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTPointerOperator;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNameSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPBinding;
import org.eclipse.cdt.core.dom.ast.gnu.cpp.GPPLanguage;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.core.parser.DefaultLogService;
import org.eclipse.cdt.core.parser.FileContent;
import org.eclipse.cdt.core.parser.IParserLogService;
import org.eclipse.cdt.core.parser.IScannerInfo;
import org.eclipse.cdt.core.parser.IncludeFileContentProvider;
import org.eclipse.cdt.core.parser.ScannerInfo;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTQualifiedName;
import org.eclipse.cdt.internal.core.index.EmptyCIndex;
import org.eclipse.cdt.internal.core.parser.scanner.CharArray;
import org.eclipse.cdt.internal.core.parser.scanner.InternalFileContent;
import org.eclipse.core.runtime.CoreException;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.VariableCompletion;

import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPOperation;

import RhapsodyUtilities.ASTHelper;

public class LocalCompletionProvider extends DefaultCompletionProvider
{
	
	private ClassifierCompletionProvider itsCompletionProvider = null;
	private String myOperationBody;
	
	
	
	public LocalCompletionProvider(String aOperationBody, ClassifierCompletionProvider aCompletionProvider) {
		itsCompletionProvider = aCompletionProvider;
		createLocalCompletions(aOperationBody);
	}
	
	private void createLocalCompletions(String aOperationBody)
	{
		myOperationBody = aOperationBody;
		IASTTranslationUnit astTranslationUnit = ASTHelper.getTranslationUnit(aOperationBody);
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
	
	private void collectCompletions(IASTNode aNode)
	{
        
        if(aNode instanceof IASTSimpleDeclaration){
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
        				
        				Completion c = itsCompletionProvider.getFirstCompletion(name.toString());
        				if((c!=null)&&(c instanceof RhapsodyClassifier))
        				{
        					classifier = (RhapsodyClassifier)c;
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
        							break;
        						}
        					}
        				}
        				else
        				{
        					//something went wrong
        					break;
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
            			irpClassifier = rcc.getIRPClassifier();
            		}
        		}
        			
        		IASTDeclarator[] declarators =  simpleDeclaration.getDeclarators();
        		if(irpClassifier!=null)
        		{
        			
        			
        			for(IASTDeclarator declarator : declarators)
        			{
        				IASTPointerOperator[] pointers = declarator.getPointerOperators();
        				boolean isReference = false;
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
        				this.addCompletion(vc);
        			}
        		}
       
        	}
        	else
        	{
        		int offset = specifier.getFileLocation().getNodeOffset();
        		int length = specifier.getFileLocation().getNodeLength();
        		String classifierName = myOperationBody.substring(offset, offset+length);
        		IASTDeclarator[] declarators =  simpleDeclaration.getDeclarators();
    			for(IASTDeclarator declarator : declarators)
    			{
    				
    				VariableCompletion vc = new VariableCompletion(this, declarator.getName().toString(),classifierName);
    				this.addCompletion(vc);
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
