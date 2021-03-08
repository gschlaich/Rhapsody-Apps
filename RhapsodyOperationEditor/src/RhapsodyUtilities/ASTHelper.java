package RhapsodyUtilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.cdt.core.dom.ast.IASTEnumerationSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTEnumerationSpecifier.IASTEnumerator;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNodeSelector;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
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

import com.telelogic.rhapsody.core.IRPType;

public class ASTHelper {
	
	private static String DefaultFilename = "SourceCode.c";
	private static String Prolog = "void checkOperation() { \n";
	private static String Epilog = "\n }";
	
	
	public static IASTTranslationUnit getTranslationUnit(String aOperationBody)
	{
		StringBuilder code = new StringBuilder();
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
		String declaration = aType.getDeclaration();
		declaration = declaration.replace("%s", (aType.getName()+";"));
		
		IASTTranslationUnit translationUnit = getTranslationUnit(declaration);
		
		IASTEnumerationSpecifier enumSpec = getEnumerationSpecifier(translationUnit);
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
	
	
}
