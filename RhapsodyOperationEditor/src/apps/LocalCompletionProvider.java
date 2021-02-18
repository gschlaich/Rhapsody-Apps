package apps;

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

public class LocalCompletionProvider extends DefaultCompletionProvider
{
	private String defaultFileName = "SourceCode.c";
	private int options = 0;
	Map<String, String> definedMacros = new HashMap<String, String>();
	private ClassifierCompletionProvider itsCompletionProvider = null;
	
	
	public LocalCompletionProvider(String aOperationBody, ClassifierCompletionProvider aCompletionProvider) {
		itsCompletionProvider = aCompletionProvider;
		createLocalCompletions(aOperationBody);
	}
	
	private void createLocalCompletions(String aOperationBody)
	{
		StringBuilder code = new StringBuilder();
		code.append("void ");
		code.append("checkOperation");
		code.append("() { \n");
		code.append(aOperationBody);
		code.append("\n }");
		
		FileContent fileContent = new InternalFileContent(defaultFileName, new CharArray(code.toString()));
		
		enableOption(GPPLanguage.OPTION_IS_SOURCE_UNIT);
    	enableOption(ITranslationUnit.AST_SKIP_ALL_HEADERS);
    	enableOption(GPPLanguage.OPTION_NO_IMAGE_LOCATIONS);
    	
    	String[] includePaths = new String[0];
        IScannerInfo info = new ScannerInfo(definedMacros,includePaths);
        IParserLogService log = new DefaultLogService();
        IIndex index = EmptyCIndex.INSTANCE; // or can be null
        //final IIndexManager indexManager= CCorePlugin.getIndexManager();
        //IIndex index = indexManager.getIndex(fTranslationUnit.getCProject(),IIndexManager.ADD_EXTENSION_FRAGMENTS_EDITOR);
        
        IncludeFileContentProvider emptyIncludes = IncludeFileContentProvider.getEmptyFilesProvider();
        
        try {
            // Using: org.eclipse.cdt.internal.core.dom.parser.cpp.GNUCPPSourceParser
        	IASTTranslationUnit translationUnit = GPPLanguage.getDefault().getASTTranslationUnit(fileContent, info, emptyIncludes, index, options , log);
        	collectCompletions(translationUnit);
        	//collectMacrosAndComments(translationUnit);
        	
        } catch (CoreException e) {
            e.printStackTrace();
        }
		
	}
	
	public void enableOption(int option){
        options |= option;
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
        		String classifierName = namedSpec.getName().getLastName().toString();
        		
        		//find classifier 
        		
        		Completion c = itsCompletionProvider.getFirstCompletion(classifierName);
        		IASTDeclarator[] declarators =  simpleDeclaration.getDeclarators();
        		if((c!=null)&&(c instanceof RhapsodyClassifierCompletion))
        		{
        			RhapsodyClassifierCompletion rcc = (RhapsodyClassifierCompletion)c;
        			
        			for(IASTDeclarator declarator : declarators)
        			{
        				IASTPointerOperator[] pointers = declarator.getPointerOperators();
        				boolean isReference = false;
        				if((pointers!=null) && (pointers.length>0))
        				{
        					isReference = true;
        				}
        				RhapsodyLocalVariableCompletion rlvc = new RhapsodyLocalVariableCompletion(this, declarator.getName().toString(), rcc.getIRPClassifier(),isReference);
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
        		IASTDeclarator[] declarators =  simpleDeclaration.getDeclarators();
    			for(IASTDeclarator declarator : declarators)
    			{
    				BasicCompletion bc = new BasicCompletion(this, declarator.getName().toString());
    				System.out.println("Only basic for: " + declarator.getName());
    				this.addCompletion(bc);
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