package de.schlaich.gunnar.rhapsody.utilities;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.FunctionCompletion;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTEnumerationSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTEnumerationSpecifier.IASTEnumerator;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNodeSelector;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTPointer;
import org.eclipse.cdt.core.dom.ast.IASTProblem;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateId;
import org.eclipse.cdt.core.dom.ast.gnu.cpp.GPPLanguage;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.core.parser.DefaultLogService;
import org.eclipse.cdt.core.parser.FileContent;
import org.eclipse.cdt.core.parser.IParserLogService;
import org.eclipse.cdt.core.parser.IProblem;
import org.eclipse.cdt.core.parser.IScannerInfo;
import org.eclipse.cdt.core.parser.IncludeFileContentProvider;
import org.eclipse.cdt.core.parser.ScannerInfo;
import org.eclipse.cdt.internal.core.index.EmptyCIndex;
import org.eclipse.cdt.internal.core.parser.scanner.CharArray;
import org.eclipse.cdt.internal.core.parser.scanner.InternalFileContent;
import org.eclipse.core.runtime.CoreException;

import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPArgument;
import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPComponent;
import com.telelogic.rhapsody.core.IRPConfiguration;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPOperation;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPType;
import com.telelogic.rhapsody.core.IRPUnit;

import de.schlaich.gunnar.rhapsody.completion.RhapsodyClassifierCompletion;
import de.schlaich.gunnar.rhapsody.completionprovider.ClassifierCompletionProvider;

public class ASTHelper 
{
	
	private static String DefaultFilename = "SourceCode.cpp";
	public final static String Prolog = "void checkOperation() { \n";
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
	
	public static IASTTranslationUnit getTranslationUnit(String aPath)
	{
		FileContent fileContent = FileContent.createForExternalFileLocation(aPath);
		int options = 0;
		
		options |= GPPLanguage.OPTION_IS_SOURCE_UNIT;
		options |= GPPLanguage.OPTION_IS_SOURCE_UNIT;
    	options |= ITranslationUnit.AST_SKIP_ALL_HEADERS;
    	options |= GPPLanguage.OPTION_NO_IMAGE_LOCATIONS;
    	
    	Map<String, String> definedMacros = new HashMap<String, String>();
    	
    	definedMacros.put("GUARD_OPERATION","");
    	
    	
    	
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
	
	
	public static String parseLanguageTypedef(IRPType aType)
	{
		String ret = null;
		
		IASTTranslationUnit translationUnit = getTranslationUnit(aType);
		
		
		ASTNode<ICPPASTTemplateId> parseTemplate = new ASTNode<ICPPASTTemplateId>(ICPPASTTemplateId.class);
		
		ICPPASTTemplateId templateId = parseTemplate.getNode(translationUnit);
		/*
		ASTNode<ICPPASTQualifiedName> parseQualifiedName = new ASTNode<ICPPASTQualifiedName>(ICPPASTQualifiedName.class);
		ICPPASTQualifiedName qualifiedName = parseQualifiedName.getNode(translationUnit);
		*/
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
	
	public static List<String> getLines(String aText) 
	{
		List<String> ret = null;
		
		if(aText==null || aText.isEmpty())
		{
			return null;
		}
		
		try
		{		
			BufferedReader origBR = new BufferedReader(new StringReader(aText));
			String line;
			ret= new ArrayList<String>();
			while((line = origBR.readLine())!=null)
			{
				line.replace((char)0x09, ' ');
				line = line.trim();
				ret.add(line);
			}
		} 
		catch (IOException e) 
		{
			
			e.printStackTrace();
		}
		
		
		
		//remove empty lines at the end of the source
		while(ret.size()!=0 && ret.get(ret.size()-1).isEmpty())
		{
			ret.remove(ret.size()-1);
		}
		
		return ret;
	}
	
	

	

	public static IASTTranslationUnit getTranslationUnit(IRPType aType) {
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
	
	public static List<IASTProblem> getProblems(IASTNode aNode)
	{
			
		ASTNode<IASTProblem> parseProblem = new ASTNode<IASTProblem>(IASTProblem.class);
		return parseProblem.getNodes(aNode);
		
	}
	
	public static List<IASTSimpleDeclaration> getSimpleDeclarations(IASTNode aNode)
	{
		ASTNode<IASTSimpleDeclaration> parseSimpleDeclaration = new ASTNode<IASTSimpleDeclaration>(IASTSimpleDeclaration.class);
		return parseSimpleDeclaration.getNodes(aNode);
	}
	
	public static List<IASTNamedTypeSpecifier> getNamedTypeSpecifiers(IASTNode aNode)
	{
		ASTNode<IASTNamedTypeSpecifier> parseNamedTypeSpecifiers = new ASTNode<IASTNamedTypeSpecifier>(IASTNamedTypeSpecifier.class);
		return parseNamedTypeSpecifiers.getNodes(aNode);
	}
	
	public static IASTFunctionDeclarator getFunctionDeclarator(IASTNode aNode)
	{
		ASTNode<IASTFunctionDeclarator> parseFunctionDeclarator = new ASTNode<IASTFunctionDeclarator>(IASTFunctionDeclarator.class);
		return parseFunctionDeclarator.getNode(aNode);
	}
	
	public static List<IASTParameterDeclaration> getParameterDeclaration(IASTNode aNode)
	{
		ASTNode<IASTParameterDeclaration> parseParameterDeclaration = new ASTNode<IASTParameterDeclaration>(IASTParameterDeclaration.class);
		return parseParameterDeclaration.getNodes(aNode);
	}
	
	public static ICPPASTQualifiedName getQualifiedName(IASTNode aNode)
	{
		ASTNode<ICPPASTQualifiedName> parseQualifiedName = new ASTNode<ICPPASTQualifiedName>(ICPPASTQualifiedName.class);
		return parseQualifiedName.getNode(aNode);
	}
	
	public static IASTDeclarator getDeclarator(IASTNode aNode)
	{
		ASTNode<IASTDeclarator> parseDeclarator = new ASTNode<IASTDeclarator>(IASTDeclarator.class);
		return parseDeclarator.getNode(aNode);
	}
	/*
	public static FunctionCompletion getFunctionCompletion(IASTSimpleDeclaration aSimpleDeclaration, CompletionProvider aProvider)
	{
		IASTFunctionDeclarator functionDeclarator = getFunctionDeclarator(aSimpleDeclaration);
		
		
		
		if(functionDeclarator==null)
		{
			return null; //no function
		}
		
		String functionName = functionDeclarator.getName().toString();
		
		IASTDeclSpecifier returnDecl = aSimpleDeclaration.getDeclSpecifier(); 
		if(returnDecl instanceof IASTSimpleDeclaration)
		{
			IASTSimpleDeclSpecifier sReturnDecl = (IASTSimpleDeclSpecifier)returnDecl;
			sReturnDecl.getDeclTypeExpression().to
		}
		
		String returnType = returnDecl.get
		
		FunctionCompletion fc = new FunctionCompletion(aProvider, functionName, returnType);
		
	}
	
	public static IASTSimpleDeclSpecifier getSimpleDeclSpecifier(IASTSimpleDeclaration aSimpleDeclaration)
	{
		return aSimpleDeclaration.getDeclSpecifier();
	}
	
	*/
	
	public static Map<String,String> getFunctiondefinitions(String aSourcePath, String aNameSpace)
	{
		
		HashMap<String,String> ret = new HashMap<>();
		
		IASTTranslationUnit translationUnit = getTranslationUnit(aSourcePath);
		
		IASTNode source = translationUnit;
		
		if(aNameSpace!=null)
		{
			//parse for namespace
			ASTNode<ICPPASTNamespaceDefinition> parseNameSpace = new ASTNode<ICPPASTNamespaceDefinition>(ICPPASTNamespaceDefinition.class);
			List<ICPPASTNamespaceDefinition> nameSpaceDefinitions = parseNameSpace.getNodes(translationUnit);
			ICPPASTNamespaceDefinition theNameSpaceDefinition=null;
			for(ICPPASTNamespaceDefinition nameSpaceDefiniton:nameSpaceDefinitions)
			{
				if(aNameSpace.equals(nameSpaceDefiniton.getName().toString()))
				{
					theNameSpaceDefinition = nameSpaceDefiniton;
					break;
				}
			}
			
			if(theNameSpaceDefinition==null)
			{
				//namespace not found
				return ret;
			}
			source = theNameSpaceDefinition;
		}
		
		ASTNode<IASTFunctionDefinition> parseFunctionDefinition = new ASTNode<IASTFunctionDefinition>(IASTFunctionDefinition.class);
		List<IASTFunctionDefinition> functionDefinitions = parseFunctionDefinition.getNodes(source);
		for(IASTFunctionDefinition functionDefinition:functionDefinitions)
		{
			IASTFunctionDeclarator functionDeclarator = functionDefinition.getDeclarator();
			
			String rawSignature = functionDeclarator.getRawSignature();
			
			if(rawSignature.startsWith("*"))
			{
				rawSignature = rawSignature.substring(1);
			}
			
			rawSignature = rawSignature.replaceAll("\\s+", "");
			
			String functionSource = getSourceFromFunction(functionDefinition, aSourcePath);
			
			System.out.println("Map Signature: "+rawSignature);
			
			ret.put(rawSignature, functionSource);
			
		}

		return ret;
		
	}
	
	public static String getSourceFromMap(Map<String,String> aMap, IRPOperation aOperation)
	{
		
		IRPModelElement element = aOperation.getOwner();
		if (element instanceof IRPClass==false) 
		{
			System.out.println("owner of operation is not a class!");
			return null;
		}
		
		IRPClass selectedClass = (IRPClass) element;
		
		String operationSignature = getOperationSignature(aOperation, selectedClass.getName());
		
		String ret = aMap.get(operationSignature);
		if(ret==null)
		{
			System.out.println("Not found signature " + operationSignature);
		}
		
		
		return ret;
		
	}
	
	public static String getOperationBody(String aPath, String aNameSpace, String aClassName, IRPOperation aOperation)
	{
		
		String operationSignature = getOperationSignature(aOperation, aClassName);
		
		IASTTranslationUnit translationUnit = getTranslationUnit(aPath);
		
		IASTNode source = translationUnit;
		
		if(aNameSpace!=null)
		{
			//parse for namespace
			ASTNode<ICPPASTNamespaceDefinition> parseNameSpace = new ASTNode<ICPPASTNamespaceDefinition>(ICPPASTNamespaceDefinition.class);
			List<ICPPASTNamespaceDefinition> nameSpaceDefinitions = parseNameSpace.getNodes(translationUnit);
			ICPPASTNamespaceDefinition theNameSpaceDefinition=null;
			for(ICPPASTNamespaceDefinition nameSpaceDefiniton:nameSpaceDefinitions)
			{
				if(aNameSpace.equals(nameSpaceDefiniton.getName().toString()))
				{
					theNameSpaceDefinition = nameSpaceDefiniton;
					break;
				}
			}
			
			if(theNameSpaceDefinition==null)
			{
				//namespace not found
				return null;
			}
			source = theNameSpaceDefinition;
		}
		
		ASTNode<IASTFunctionDefinition> parseFunctionDefinition = new ASTNode<IASTFunctionDefinition>(IASTFunctionDefinition.class);
		List<IASTFunctionDefinition> functionDefinitions = parseFunctionDefinition.getNodes(source);
		IASTFunctionDefinition theFunctionDefinition=null;
		for(IASTFunctionDefinition functionDefinition:functionDefinitions)
		{
			IASTFunctionDeclarator functionDeclarator = functionDefinition.getDeclarator();
			
			String rawSignature = functionDeclarator.getRawSignature();
			
			rawSignature = rawSignature.replaceAll("\\s+", "");
			
			if(rawSignature.equals(operationSignature))
			{
				theFunctionDefinition = functionDefinition;
				break;
			}
		}
			
		
		if(theFunctionDefinition==null)
		{
			
			return null;
		}
		
		
		//check for syntax error
		/*
		List<IASTProblem> problems = ASTHelper.getProblems(source);
		
		if(problems.isEmpty()==false)
		{
			//there is a syntax error in the source... no roundtrip
			for(IProblem problem:problems)
			{
				System.out.println("Error: "+ problem.getMessageWithLocation());
			}
			return null;
		}
		*/
		
		IASTFunctionDeclarator theFunctionDeclarator = theFunctionDefinition.getDeclarator();
		
		String rawSignature = theFunctionDeclarator.getRawSignature();
		
		
		//System.out.println("Raw Signature: "+ rawSignature);

		
		rawSignature = rawSignature.replaceAll("\\s+", "");
		
		if(rawSignature.equals(operationSignature)==false)
		{
			System.out.println("different Signatures:");
			System.out.println("Raw Signature: " + rawSignature);
			System.out.println("Op. Signature: " + operationSignature);		
		
		}
			
		// get the operation body
		return getSourceFromFunction(theFunctionDefinition, aPath);
	}

	public static String getOperationSignature(IRPOperation aOperation, String aClassName) 
	{
		String operationSignature = aOperation.getImplementationSignature();
		
		//check for constructor of an reactive class
		if(aOperation.getIsCtor()==1)
		{
			IRPModelElement owner = aOperation.getOwner();
			if(owner instanceof IRPClass)
			{
				IRPClass ownerClass = (IRPClass)owner;
				if(ownerClass.getIsReactive()==1)
				{
					operationSignature = operationSignature.substring(0, operationSignature.length()-1)+", IOxfActive* theActiveContext )";
					//System.out.println("Reactive Constructor: " + operationSignature);
					
				}
			}
		}
		
		
		int startIndex = operationSignature.indexOf(aOperation.getName());
		if(startIndex>=0)
		{
			operationSignature = operationSignature.substring(startIndex);
			operationSignature = aClassName+"::"+operationSignature;
		}
		
		//System.out.println("Op. Signature: " + operationSignature);
		operationSignature = operationSignature.replaceAll("\\s+", "");
		return operationSignature;
	}

	private static String getSourceFromFunction(IASTFunctionDefinition aFunctionDefinition, String aPath) {
		IASTStatement functionBody = aFunctionDefinition.getBody();
		
		IASTFileLocation fileLocation = functionBody.getFileLocation();
		
		
		int offset = fileLocation.getNodeOffset();
		int length = fileLocation.getNodeLength();
		
		try
		{

			InputStream in = new FileInputStream(aPath);
			
			byte[] bbody = new byte[length];
			
			bbody[0] = 'x';
			in.skip(offset);
			in.read(bbody, 0, length);
	
			String strOperation = new String(bbody);
			
			StringReader sr = new StringReader(strOperation);
			
			BufferedReader  br = new BufferedReader(sr);
			
			ArrayList<String> sl = new ArrayList<String>();
			
			
			String line; 
			String ret = "";
			
			while((line = br.readLine())!=null)
			{
				sl.add(line);
			}
			
			int i=0;
			int tabPos = -1;
			
			for(String checkTab:sl)
			{
				tabPos = checkTab.indexOf("//#[");
				i++;
				if(tabPos!=(-1))
				{
					break;
				}
			}
			
			
			
			for(; i<sl.size(); i++)
			{
				
				line = sl.get(i);
				if(line.contains("//#]"))
				{
					break;
				}
				if(line.length()>tabPos)
				{
					if(line.getBytes()[0]!=0x09)
					{	
						line = line.substring(tabPos);
					}
					else if(line.getBytes()[1]==0x09)
					{
						line = line.substring(2);					
					}
					
						
				}
				
				ret = ret+line+"\n";
			}
			
			
			in.close();
		
			return ret;
		
		} 
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "";
	}
	
	
	
	public static String getSourceText(IRPOperation aOperation, IRPApplication aApplication)
	{
	
		IRPModelElement element = aOperation.getOwner();
		if (element instanceof IRPClass==false) 
		{
			System.out.println("owner of operation is not a class!");
			return null;
		}
		
		IRPClass selectedClass = (IRPClass) element;
		String filePath = getSourcePath(selectedClass, aApplication);
		if((aOperation.isATemplate()==1)||(aOperation.getIsInline()==1))
		{
			filePath = filePath+".h";
		}
		else
		{
			filePath = filePath+".cpp";
		}

		if((filePath.endsWith("cpp")||filePath.endsWith("h"))==false)
		{
			System.out.println("file not found / component not active: " + selectedClass.getName());
			return null;
		}
		
		String nameSpace = RhapsodyOperation.getNamespace(selectedClass);
		
		String className = selectedClass.getName();
		IRPModelElement owner = selectedClass.getOwner();
		while(owner instanceof IRPClass)
		{
			className = owner.getName()+"::"+className;
			owner = owner.getOwner();
		}
		
		String roundTripText = ASTHelper.getOperationBody(filePath, nameSpace, className, aOperation);
		return roundTripText;
	}

	
	@SuppressWarnings("unchecked")
	public static String getSourcePath(IRPClass aClass, IRPApplication aApplication) 
	{	
		List<IRPProject> projects = aApplication.getProjects().toList();
		
		IRPComponent activeComponent = null;
		
		for(IRPProject project:projects)
		{
			activeComponent = project.getActiveComponent();
			if(activeComponent!=null)
			{
				break;
			}
		}
		String filePath = null;
		
		List<IRPConfiguration> configurations = activeComponent.getConfigurations().toList();
		for(IRPConfiguration configuration:configurations)
		{
			filePath = configuration.getDirectory(1,"");
		}
		
		List<IRPModelElement> scopeElements = activeComponent.getScopeElements().toList();
		
		
		IRPModelElement selectedElement = aClass;
		
		while(selectedElement.getOwner() instanceof IRPClass)
		{
			
			selectedElement = selectedElement.getOwner();
		}
		
		for(IRPModelElement scopeElement: scopeElements)
		{
			if(scopeElement.equals(selectedElement))
			{
				
				filePath = filePath+"\\"+selectedElement.getName();
				
				break;
			}	
		}
		return filePath;
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
		//first check on higher level
		for(IASTNode node :nodes)
		{
			if(myType.isAssignableFrom(node.getClass()))
			{
				return myType.cast(node);
			}
		}
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
	
	public List<T> getNodes(IASTNode aNode)
	{
		ArrayList<T> ret = new ArrayList<T>();
		if(aNode==null)
		{
			return ret;
		}
		
		if(myType.isAssignableFrom(aNode.getClass()))
		{
			ret.add(myType.cast(aNode));
			return ret;
		}
		
		IASTNode[] nodes = aNode.getChildren();
		for(IASTNode node : nodes)
		{
			ret.addAll(getNodes(node));
		}
		
		return ret;
			
	}
	
}

