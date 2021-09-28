package RhapsodyUtilities;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fife.ui.autocomplete.Completion;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTEnumerationSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTEnumerationSpecifier.IASTEnumerator;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNodeSelector;
import org.eclipse.cdt.core.dom.ast.IASTPointer;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateId;
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
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSimpleDeclaration;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTTemplateId;
import org.eclipse.cdt.internal.core.index.EmptyCIndex;
import org.eclipse.cdt.internal.core.parser.scanner.CharArray;
import org.eclipse.cdt.internal.core.parser.scanner.InternalFileContent;
import org.eclipse.core.runtime.CoreException;

import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPType;

import apps.ClassifierCompletionProvider;
import apps.RhapsodyClassifierCompletion;

public class ASTHelper 
{
	
	private static String DefaultFilename = "SourceCode.cpp";
	private static String Prolog = "void checkOperation() { \n";
	private static String Epilog = "\n }";
	
	
	public static IASTTranslationUnit getTranslationUnit(String aOperationBody, ClassifierCompletionProvider aClassifierCompletionProvider)
	{
		StringBuilder code = new StringBuilder();
		if(aClassifierCompletionProvider!=null)
		{
			List<Completion> completions = aClassifierCompletionProvider.getCompletions();
			IRPClassifier cl = aClassifierCompletionProvider.getClassifier();
			String ownNameSpace = RhapsodyOperation.getNamespace(cl);
			for(Completion c : completions)
			{
				if(c instanceof RhapsodyClassifierCompletion)
				{
					RhapsodyClassifierCompletion rcc = (RhapsodyClassifierCompletion)c;
					IRPClassifier irpc = rcc.getIRPClassifier(false);
					if(irpc instanceof IRPClass)
					{
						String namespace = RhapsodyOperation.getNamespace(irpc);
						if((namespace!=null)&&(namespace.equals("")==false)&&(namespace.equals(ownNameSpace)==false))
						{
							code.append("\nnamespace ");
							code.append(namespace);
							code.append(" { \n");
							code.append("    class ");
							code.append(irpc.getName());
							code.append(";\n}\n");	
						}
						else
						{
							code.append("\nclass ");
							code.append(irpc.getName());
							code.append(";\n");
						}
					
					}
					
				}
			}
			
		}
		
		code.append(Prolog);
		code.append(aOperationBody);
		code.append(Epilog);
		String operation = code.toString();
		FileContent fileContent = new InternalFileContent(DefaultFilename, new CharArray(operation));
		
		int options = 0;
		
		options |= GPPLanguage.OPTION_IS_SOURCE_UNIT;
		options |= GPPLanguage.OPTION_IS_SOURCE_UNIT;
    	options |= ITranslationUnit.AST_SKIP_ALL_HEADERS;
    	options |= GPPLanguage.OPTION_NO_IMAGE_LOCATIONS;
    	
    	Map<String, String> definedMacros = new HashMap<String, String>();
    	
    	String[] includePaths = new String[0];
        IScannerInfo info = new ScannerInfo(definedMacros,includePaths);
        IParserLogService log = new DefaultLogService();
        IIndex index = EmptyCIndex.INSTANCE; // or can be null
        //final IIndexManager indexManager= CCorePlugin.getIndexManager();
        //IIndex index = indexManager.getIndex(fTranslationUnit.getCProject(),IIndexManager.ADD_EXTENSION_FRAGMENTS_EDITOR);
        
        IncludeFileContentProvider emptyIncludes = IncludeFileContentProvider.getEmptyFilesProvider();
        
        try 
        {
            // Using: org.eclipse.cdt.internal.core.dom.parser.cpp.GNUCPPSourceParser
        	IASTTranslationUnit translationUnit = GPPLanguage.getDefault().getASTTranslationUnit(fileContent, info, emptyIncludes, index, options , log);
        	return translationUnit;
        } 
        catch (CoreException e) 
        {
            e.printStackTrace();
        }
        return null;	
	}
	
	public static IASTNode getNodeAtPostion(int aStart, int aEnd, IASTTranslationUnit atranslationUnit )
	{
		int pos = aStart+Prolog.length();
		int offset = aEnd-aStart;
		IASTNodeSelector nodeSelector = atranslationUnit.getNodeSelector(null);
		return nodeSelector.findEnclosingNode(pos, offset);
	}
	
	public static String[] parseLanguageEnumType(IRPType aType)
	{
		ArrayList<String> ret = new ArrayList<String>();
		IASTTranslationUnit translationUnit = getTranslationUnit(aType);
		
		//IASTEnumerationSpecifier enumSpec = getEnumerationSpecifier(translationUnit);
		
		ASTNode<IASTEnumerationSpecifier> parse = new ASTNode<IASTEnumerationSpecifier>(IASTEnumerationSpecifier.class);
		
		IASTEnumerationSpecifier enumSpec = parse.getNode(translationUnit);
		
		if(enumSpec==null)
		{
			return null;
		}
		IASTEnumerator[] enumerators = enumSpec.getEnumerators();
		for(IASTEnumerator enumerator : enumerators)
		{
			String enumName = enumerator.getName().toString();
			ret.add(enumName);
		}
		
		return ret.toArray(new String[0]);
	}
	
	private static IASTEnumerationSpecifier getEnumerationSpecifier(IASTNode aNode)
	{
		if(aNode==null)
		{
			return null;
		}
		if(aNode instanceof IASTEnumerationSpecifier)
		{
			return (IASTEnumerationSpecifier)aNode;
		}
		IASTNode[] nodes = aNode.getChildren();
		for(IASTNode n : nodes)
		{
			IASTEnumerationSpecifier ret =  getEnumerationSpecifier(n);
			if(ret!=null)
			{
				return ret;
			}
		}
		return null;
	}
	
	public static String parseLanguageTypedef(IRPType aType)
	{
		String ret = null;
		
		IASTTranslationUnit translationUnit = getTranslationUnit(aType);
		
		
		ASTNode<ICPPASTTemplateId> parseTemplate = new ASTNode<ICPPASTTemplateId>(ICPPASTTemplateId.class);
		
		ICPPASTTemplateId templateId = parseTemplate.getNode(translationUnit);
		
		if(templateId!=null)
		{
			return templateId.getTemplateName().toString();
		}
		ASTNode<IASTSimpleDeclaration> parseTypedef = new ASTNode<IASTSimpleDeclaration>(IASTSimpleDeclaration.class);
		IASTSimpleDeclaration simpleDeclaration = parseTypedef.getNode(translationUnit);
		if(simpleDeclaration!=null)
		{
			IASTDeclSpecifier specifier = simpleDeclaration.getDeclSpecifier();
        	if(specifier instanceof IASTNamedTypeSpecifier)
        	{
        		IASTNamedTypeSpecifier namedSpec = (IASTNamedTypeSpecifier)specifier;
        		ret = namedSpec.getName().getLastName().toString();
        	}
        		
		}

		return ret;
	}
	
	public static List<String> getLanguageTypedefTemplateArguments(IRPType aType)
	{

		IASTTranslationUnit translationUnit = getTranslationUnit(aType);
		
		ASTNode<ICPPASTTemplateId> parseTemplate =  new ASTNode<ICPPASTTemplateId>(ICPPASTTemplateId.class);
		
		ICPPASTTemplateId templateId = parseTemplate.getNode(translationUnit);
		
		if(templateId==null)
		{
			return null;

		}
		
		return getTemplateArguments(templateId);
		
	}
	
	public static String parseLanguageTypedefNameSpace(IRPType aType)
	{
		
		IASTTranslationUnit translationUnit = getTranslationUnit(aType);
		
		ASTNode<ICPPASTQualifiedName> parseTemplate = new ASTNode<ICPPASTQualifiedName>(ICPPASTQualifiedName.class);
		
		ICPPASTQualifiedName qualifiedName = parseTemplate.getNode(translationUnit);
		
		if(qualifiedName==null)
		{
			return null;	
		}
		return qualifiedName.getLastName().toString();
	}
	

	private static IASTTranslationUnit getTranslationUnit(IRPType aType) {
		String declaration = aType.getDeclaration();
		declaration = declaration.replace("%s", (aType.getName()+";"));
		IASTTranslationUnit translationUnit = getTranslationUnit(declaration,null);
		return translationUnit;
	}
	
	public static boolean isPointer(IRPType aType)
	{
		boolean ret = false;
		IASTTranslationUnit translationUnit = getTranslationUnit(aType);
		ASTNode<IASTPointer> parseTemplate = new ASTNode<IASTPointer>(IASTPointer.class);
		IASTPointer pointer = parseTemplate.getNode(translationUnit);
		
		ret = (pointer!=null);
		
		return ret;
	}
	
	
	private static <T extends IASTNode> T getNode(IASTNode aNode, Class<T> aType)
	{
		if(aNode==null)
		{
			return null;
		}
		return aType.cast(aNode);
		
		
		
	}
	
	public static List<String> getTemplateArguments(ICPPASTTemplateId aTemplateId)
	{
		List<String> ret = new ArrayList<String>();
		IASTNode[] nodes = aTemplateId.getTemplateArguments();
		
		ASTNode<ICPPASTQualifiedName> parseQualifiedName = new ASTNode<ICPPASTQualifiedName>(ICPPASTQualifiedName.class);
		ICPPASTQualifiedName qualifiedName;
		
		for(IASTNode n:nodes)
		{
			qualifiedName = parseQualifiedName.getNode(n);
			if(qualifiedName!=null)
			{
				ret.add(qualifiedName.getLastName().toString());
			}
		}
		
		return ret;
		
	}
	

	

}

class ASTNode<T>
{
	public Class<T> myType;
	
	public ASTNode(Class<T> aType) 
	{
		myType = aType;
	}
	
	public T getNode(IASTNode aNode)
	{
		if(aNode==null)
		{
			return null;
		}

		if(myType.isAssignableFrom(aNode.getClass()))
		{
			return myType.cast(aNode);
		}
	
		IASTNode[] nodes = aNode.getChildren();
		for(IASTNode node : nodes)
		{
			T n =  getNode(node);
			if(n!=null)
			{
				return n;
			}
		}
			
		return null;
	}
}

