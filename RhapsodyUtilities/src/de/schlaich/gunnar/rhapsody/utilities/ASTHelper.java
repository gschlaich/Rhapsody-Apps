package de.schlaich.gunnar.rhapsody.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.rmi.server.Operation;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.eclipse.cdt.core.dom.ast.ExpansionOverlapsBoundaryException;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTEnumerationSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTEnumerationSpecifier.IASTEnumerator;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNodeSelector;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTPointer;
import org.eclipse.cdt.core.dom.ast.IASTProblem;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNameSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateId;
import org.eclipse.cdt.core.dom.ast.gnu.cpp.GPPLanguage;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.model.CoreModelUtil;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.core.parser.DefaultLogService;
import org.eclipse.cdt.core.parser.FileContent;
import org.eclipse.cdt.core.parser.IParserLogService;
import org.eclipse.cdt.core.parser.IProblem;
import org.eclipse.cdt.core.parser.IScannerInfo;
import org.eclipse.cdt.core.parser.IToken;
import org.eclipse.cdt.core.parser.IncludeFileContentProvider;
import org.eclipse.cdt.core.parser.ScannerInfo;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionDeclarator;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTNamespaceDefinition;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTQualifiedName;
import org.eclipse.cdt.internal.core.dom.rewrite.util.ASTNodes;
import org.eclipse.cdt.internal.core.index.EmptyCIndex;
import org.eclipse.cdt.internal.core.parser.scanner.CharArray;
import org.eclipse.cdt.internal.core.parser.scanner.InternalFileContent;
import org.eclipse.core.runtime.CoreException;

import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPArgument;
import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPComment;
import com.telelogic.rhapsody.core.IRPComponent;
import com.telelogic.rhapsody.core.IRPConfiguration;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPOperation;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPProfile;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPTableView;
import com.telelogic.rhapsody.core.IRPType;
import com.telelogic.rhapsody.core.IRPUnit;

public class ASTHelper
{

	private static String DefaultFilename = "SourceCode.cpp";
	public final static String Prolog = "void checkOperation() { \n";
	private static String Epilog = "\n }";

	private static Consumer<String> myTraceAction = null;

	public static void setTraceAction(Consumer<String> aAction)
	{
		myTraceAction = aAction;
	}

	private static void trace(String aMessage)
	{
		if (myTraceAction == null)
		{
			// no traceaction set...
			System.out.println("[AST trace not set] " + aMessage);
			return;
		}

		aMessage = "ASTHelper: " + aMessage;

		myTraceAction.accept(aMessage);
	}

	public static IASTTranslationUnit getTranslationUnitFromBody(String aOperationBody)
	{
		StringBuilder code = new StringBuilder();
		code.append(Prolog);
		code.append(aOperationBody);
		code.append(Epilog);
		String operation = code.toString();
		FileContent fileContent = new InternalFileContent(DefaultFilename, new CharArray(operation));
		return getTranslationUnit(fileContent);
	}

	public static IASTTranslationUnit getTranslationUnit(String aPath)
	{
		FileContent fileContent = FileContent.createForExternalFileLocation(aPath);
		return getTranslationUnit(fileContent);
	}

	public static String getOperationName(String aPath, int aLine)
	{
		IASTTranslationUnit translationUnit = getTranslationUnit(aPath);
		if (translationUnit == null)
		{
			return null;
		}
		IASTFunctionDefinition functionDefinition = getFunctionDefinition(aLine, translationUnit);
		if (functionDefinition == null)
		{
			return null;
		}
		return getOperationName(functionDefinition);
	}

	public static String[] getClassNamesOfOperation(String aPath, int aLine)
	{
		IASTTranslationUnit translationUnit = getTranslationUnit(aPath);
		if (translationUnit == null)
		{
			return null;
		}
		IASTFunctionDefinition functionDefinition = getFunctionDefinition(aLine, translationUnit);
		if (functionDefinition == null)
		{
			return null;
		}
		return getClassNames(functionDefinition);
	}

	public static String getOperationSignature(String aPath, int aLine)
	{
		IASTTranslationUnit translationUnit = getTranslationUnit(aPath);
		if (translationUnit == null)
		{
			return null;
		}
		IASTFunctionDefinition functionDefinition = getFunctionDefinition(aLine, translationUnit);
		if (functionDefinition == null)
		{
			return null;
		}
		return getSignature(functionDefinition);
	}

	public static String getOperationNameSpace(String aPath, int aLine)
	{
		IASTTranslationUnit translationUnit = getTranslationUnit(aPath);
		if (translationUnit == null)
		{
			return null;
		}

		CPPASTNamespaceDefinition namespaceDefinition = getNamespaceDefinition(aLine, translationUnit);
		if (namespaceDefinition == null)
		{
			return null;
		}

		return namespaceDefinition.getName().toString();

	}

	public static IRPOperation getOperation(IRPProject aProject, String aPath, int aLine)
	{
		IASTTranslationUnit translationUnit = getTranslationUnit(aPath);
		if (translationUnit == null)
		{
			return null;
		}

		String nameSpace = null;
		CPPASTNamespaceDefinition namespaceDefinition = getNamespaceDefinition(aLine, translationUnit);
		if (namespaceDefinition != null)
		{
			nameSpace = namespaceDefinition.getName().toString();
		}

		IASTFunctionDefinition functionDefinition = getFunctionDefinition(aLine, translationUnit);
		if (functionDefinition == null)
		{
			return null;
		}

		String body = getSourceFromFunction(functionDefinition);

		if (body == null)
		{
			return null;
		}

		String operationName = getOperationName(functionDefinition);

		String signature = getSignatureFromBody(body, operationName);

		if (signature == null)
		{
			return null;
		}

		System.out.println("Signature Source: " + signature);

		operationName = operationName.replace(" ", "");

		String[] classNames = getClassNames(functionDefinition);

		IRPModelElement searchElement = aProject;

		if (nameSpace != null)
		{
			IRPModelElement foundNameSpace = aProject.findNestedElementRecursive(nameSpace, "Package");
			if (foundNameSpace != null)
			{
				searchElement = foundNameSpace;
			}
		}

		for (String className : classNames)
		{
			IRPModelElement foundClass = searchElement.findNestedElementRecursive(className, "Class");
			if (foundClass != null)
			{
				searchElement = foundClass;
			}
		}

		IRPModelElement foundOperation = null;// searchElement.findNestedElement(operationName, "Operation");
		List<IRPOperation> operations = searchElement.getNestedElementsByMetaClass("Operation", 0).toList();

		for (IRPOperation op : operations)
		{
			String opSignature = op.getSignatureNoArgNames();

			if (op.getName().equals(operationName))
			{
				System.out.println("Signature Model: " + opSignature);
				if (opSignature.equals(signature))
				{
					foundOperation = op;
					break;
				}

			}

		}

		if (foundOperation == null)
		{
			for (IRPOperation op : operations)
			{

				if (operationName.equals(op.getName()))
				{
					foundOperation = op;
					break;
				}
			}
		}

		if (foundOperation == null)
		{
			System.out.println("Operation " + operationName + " not found");
			return null;
		}

		IRPOperation operation = (IRPOperation) foundOperation;

		return operation;

	}

	public static IASTTranslationUnit getTranslationUnit(FileContent fileContent)
	{

		int options = 0;

		options |= GPPLanguage.OPTION_IS_SOURCE_UNIT;
		options |= GPPLanguage.OPTION_IS_SOURCE_UNIT;
		options |= ITranslationUnit.AST_SKIP_ALL_HEADERS;
		options |= GPPLanguage.OPTION_NO_IMAGE_LOCATIONS;

		Map<String, String> definedMacros = new HashMap<String, String>();

		definedMacros.put("GUARD_OPERATION", "");

		String[] includePaths = new String[0];
		IScannerInfo info = new ScannerInfo(definedMacros, includePaths);
		IParserLogService log = new DefaultLogService();
		IIndex index = EmptyCIndex.INSTANCE; // or can be null
		// final IIndexManager indexManager= CCorePlugin.getIndexManager();
		// IIndex index =
		// indexManager.getIndex(fTranslationUnit.getCProject(),IIndexManager.ADD_EXTENSION_FRAGMENTS_EDITOR);

		IncludeFileContentProvider emptyIncludes = IncludeFileContentProvider.getEmptyFilesProvider();

		try
		{
			// Using: org.eclipse.cdt.internal.core.dom.parser.cpp.GNUCPPSourceParser
			IASTTranslationUnit translationUnit = GPPLanguage.getDefault().getASTTranslationUnit(fileContent, info,
					emptyIncludes, index, options, log);
			return translationUnit;

		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static IASTFunctionDefinition getFunctionDefinition(int aLine, IASTNode aNode)
	{

		// System.out.println("Node: " + aNode.getClass().getName());

		IASTFileLocation nodeLocation = aNode.getFileLocation();

		// System.out.println("Node Location: " + nodeLocation.getStartingLineNumber() +
		// " - " + nodeLocation.getEndingLineNumber());

		if (nodeLocation.getStartingLineNumber() <= aLine)
		{
			if (nodeLocation.getEndingLineNumber() >= aLine)
			{
				if (aNode instanceof IASTFunctionDefinition)
				{
					return (IASTFunctionDefinition) aNode;
				}
				else
				{
					for (IASTNode node : aNode.getChildren())
					{
						IASTFunctionDefinition functionDefinition = getFunctionDefinition(aLine, node);
						if (functionDefinition != null)
						{
							return functionDefinition;
						}
					}
				}
			}
		}

		// not found...
		return null;
	}

	public static CPPASTNamespaceDefinition getNamespaceDefinition(int aLine, IASTNode aNode)
	{
		IASTFileLocation nodeLocation = aNode.getFileLocation();

		if (nodeLocation.getStartingLineNumber() <= aLine)
		{
			if (nodeLocation.getEndingLineNumber() >= aLine)
			{
				if (aNode instanceof CPPASTNamespaceDefinition)
				{
					return (CPPASTNamespaceDefinition) aNode;
				}
			}
		}

		for (IASTNode node : aNode.getChildren())
		{
			CPPASTNamespaceDefinition namespaceDefinition = getNamespaceDefinition(aLine, node);
			if (namespaceDefinition != null)
			{
				return namespaceDefinition;
			}
		}
		// not found...
		return null;
	}

	public static String getOperationName(IASTFunctionDefinition aFunctionDefinition)
	{

		if (aFunctionDefinition == null)
		{
			return null;
		}

		IASTFunctionDeclarator functionDeclarator = aFunctionDefinition.getDeclarator();

		IASTName astName = functionDeclarator.getName();

		String operationName = astName.getLastName().toString();

		// remove spaces
		operationName = operationName.replace(" ", "");

		return operationName;

	}

	public static String getOperationArguments(IASTFunctionDefinition aFunctionDefinition)
	{
		if (aFunctionDefinition == null)
		{
			return null;
		}

		IASTFunctionDeclarator functionDeclarator = aFunctionDefinition.getDeclarator();

		IASTNode[] nodes = functionDeclarator.getChildren();

		StringBuilder ret = new StringBuilder();

		for (IASTNode node : nodes)
		{
			if (node instanceof IASTParameterDeclaration)
			{
				IASTParameterDeclaration parameterDeclaration = (IASTParameterDeclaration) node;

				if (ret.length() > 0)
				{
					ret.append(", ");
				}
				ret.append(parameterDeclaration.getRawSignature());

			}
		}
		return ret.toString();
	}

	public static String getOperationSignature(IASTFunctionDefinition aFunctionDefinition)
	{
		if (aFunctionDefinition == null)
		{
			return null;
		}

		IASTFunctionDeclarator functionDeclarator = aFunctionDefinition.getDeclarator();

		return functionDeclarator.getRawSignature();

	}

	public static String[] getClassNames(IASTFunctionDefinition aFunctionDefinition)
	{
		if (aFunctionDefinition == null)
		{
			return null;
		}

		IASTFunctionDeclarator functionDeclarator = aFunctionDefinition.getDeclarator();

		CPPASTFunctionDeclarator cppFunctionDeclarator = (CPPASTFunctionDeclarator) functionDeclarator;

		if (cppFunctionDeclarator == null)
		{
			return null;
		}

		CPPASTQualifiedName cppQualifiedName = (CPPASTQualifiedName) cppFunctionDeclarator.getName();
		if (cppQualifiedName == null)
		{
			return null;
		}
		ICPPASTNameSpecifier[] qualifiererNames = cppQualifiedName.getQualifier();
		if (qualifiererNames == null)
		{
			return null;
		}

		String[] classNames = new String[qualifiererNames.length];
		for (int i = 0; i < qualifiererNames.length; i++)
		{
			classNames[i] = qualifiererNames[i].toString();
		}

		return classNames;

	}

	public static String getSignature(IASTFunctionDefinition aFunctionDefinition)
	{
		if (aFunctionDefinition == null)
		{
			return null;
		}

		IASTFunctionDeclarator functionDeclarator = aFunctionDefinition.getDeclarator();

		IASTName astName = functionDeclarator.getName();

		String signature = astName.toString();

		return signature;

	}

	public static int getOffset(String aPath, int aLine)
	{
		IASTTranslationUnit translationUnit = getTranslationUnit(aPath);
		if (translationUnit == null)
		{
			return -1;
		}
		IASTFunctionDefinition functionDefinition = getFunctionDefinition(aLine, translationUnit);
		if (functionDefinition == null)
		{
			return -1;
		}
		return getOffset(functionDefinition, aLine);
	}
	
	public static void viewCompilerIssues(IRPProject aProject)
	{
		
		IRPProfile usmProfile = null;
		List<IRPProfile> profiles =  aProject.getProfiles().toList();
		for (IRPProfile p : profiles)
		{
			if (p.getName().equals("USMProfile"))
			{
				usmProfile = p;
				break;
			}
			
		}
		if (usmProfile == null)
		{
			trace("USMProfile not found");
			return;
		}
		
		List<IRPTableView> tables=  usmProfile.getNestedElementsByMetaClass("TableView",0).toList();
		
		for (IRPTableView t : tables)
		{
			if (t.getName().equals("CompilerIssues"))
			{
				t.open();
				break;
			}
		}
	}

	public static IRPComment createIssue(IRPProject aProject, String aPath, int aLine, String aErrorLevel,
			String aMessage, String aIssueType)
	{

		IRPModelElement element = getOperation(aProject, aPath, aLine);
		if (element == null)
		{
			return null;
		}

		if (element instanceof IRPOperation == false)
		{
			return null;
		}

		IRPOperation operation = (IRPOperation) element;
		IRPModelElement owner = operation.getOwner();
		if (owner == null)
		{
			return null;
		}

		if (owner instanceof IRPClass == false)
		{
			return null;
		}

		IRPClass ownerClass = (IRPClass) owner;

		int offset = getOffset(aPath, aLine);
		if (offset == -1)
		{
			return null;
		}

		String operationName = operation.getName();

		String issueName = aErrorLevel + "_" + operationName + "_" + offset;

		List<IRPComment> issues = owner.getNestedElementsByMetaClass("Comment", 0).toList();
		for (IRPComment issue : issues)
		{
			if (issue.getUserDefinedMetaClass().equals(aIssueType))
			{
				if (issue.getName().equals(issueName))
				{
					return null;
				}
			}
		}

		if (owner.getSaveUnit().isReadOnly() == 1)
		{
			trace("Could not add a compilerissue, class " + owner.getName() + " is read only");
			return null;
		}

		IRPComment compilerIssue = (IRPComment) owner.addNewAggr(aIssueType, issueName);

		if (compilerIssue == null)
		{
			return null;
		}

		compilerIssue.setDescription(aMessage);
		compilerIssue.setBody(aErrorLevel);
		compilerIssue.setSpecification(operation.getName() + " " + offset);
		compilerIssue.addAnchor(operation);
		return compilerIssue;

	}

	public static void deleteCompilerIssues(IRPClass aClass, String aIssueType)
	{

		if (aClass.isReadOnly() == 1)
		{
			trace("Could not delete compiler issues, class " + aClass.getName() + " is read only");
			return;
		}

		List<IRPComment> comments = aClass.getNestedElementsByMetaClass("Comment", 0).toList();

		for (IRPComment comment : comments)
		{
			if (comment.getUserDefinedMetaClass().equals(aIssueType))
			{

				comment.deleteFromProject();

			}
		}
		// delete from all nested elements
		List<IRPClass> classes = aClass.getNestedElementsByMetaClass("Class", 0).toList();
		for (IRPClass c : classes)
		{
			deleteCompilerIssues(c, aIssueType);
		}

	}

	public void deleteCompilerIssues(IRPPackage aPackage, String aIssueType)
    {

        if (aPackage.isReadOnly() == 1)
        {
            return;
        }

        trace("delete CompilerIssue from " + aPackage.getName());

		List<IRPClass> classes = aPackage.getNestedElementsByMetaClass("Class", 0).toList();

		if (aPackage.isReadOnly() == 1)
		{
			return;
		}

		for (IRPClass c : classes)
		{
			trace("delete CompilerIssue from " + c.getName());
			deleteCompilerIssues(c, aIssueType);
		}
	}

	public static int getOffset(IASTFunctionDefinition aFunctionDefinition, int aLine)
	{
		if (aFunctionDefinition == null)
		{

			return 0;
		}

		IASTFileLocation fileLocation = aFunctionDefinition.getFileLocation();

		int startLine = fileLocation.getStartingLineNumber() + 1;

		int offset = aLine - startLine;
		return offset;

	}

	public static IASTNode getNodeAtPosition(int aStart, int aEnd, IASTTranslationUnit atranslationUnit)
	{
		int pos = aStart + Prolog.length();
		int offset = aEnd - aStart;
		IASTNodeSelector nodeSelector = atranslationUnit.getNodeSelector(null);
		return nodeSelector.findEnclosingNode(pos, offset);
	}

	public static String[] parseLanguageEnumType(IRPType aType)
	{
		ArrayList<String> ret = new ArrayList<String>();
		IASTTranslationUnit translationUnit = getTranslationUnit(aType);

		// IASTEnumerationSpecifier enumSpec = getEnumerationSpecifier(translationUnit);

		ASTNode<IASTEnumerationSpecifier> parse = new ASTNode<IASTEnumerationSpecifier>(IASTEnumerationSpecifier.class);

		IASTEnumerationSpecifier enumSpec = parse.getNode(translationUnit);

		if (enumSpec == null)
		{
			return null;
		}
		IASTEnumerator[] enumerators = enumSpec.getEnumerators();
		for (IASTEnumerator enumerator : enumerators)
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
		 * ASTNode<ICPPASTQualifiedName> parseQualifiedName = new
		 * ASTNode<ICPPASTQualifiedName>(ICPPASTQualifiedName.class);
		 * ICPPASTQualifiedName qualifiedName =
		 * parseQualifiedName.getNode(translationUnit);
		 */
		if (templateId != null)
		{
			return templateId.getTemplateName().toString();
		}
		ASTNode<IASTSimpleDeclaration> parseTypedef = new ASTNode<IASTSimpleDeclaration>(IASTSimpleDeclaration.class);
		IASTSimpleDeclaration simpleDeclaration = parseTypedef.getNode(translationUnit);
		if (simpleDeclaration != null)
		{
			IASTDeclSpecifier specifier = simpleDeclaration.getDeclSpecifier();
			if (specifier instanceof IASTNamedTypeSpecifier)
			{
				IASTNamedTypeSpecifier namedSpec = (IASTNamedTypeSpecifier) specifier;
				ret = namedSpec.getName().getLastName().toString();
			}

		}

		return ret;
	}

	public static List<String> getLanguageTypedefTemplateArguments(IRPType aType)
	{

		IASTTranslationUnit translationUnit = getTranslationUnit(aType);

		ASTNode<ICPPASTTemplateId> parseTemplate = new ASTNode<ICPPASTTemplateId>(ICPPASTTemplateId.class);

		ICPPASTTemplateId templateId = parseTemplate.getNode(translationUnit);

		if (templateId == null)
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

		if (qualifiedName == null)
		{
			return null;
		}
		return qualifiedName.getLastName().toString();
	}

	public static List<String> getLines(String aText)
	{
		List<String> ret = null;

		if (aText == null || aText.isEmpty())
		{
			return null;
		}

		try
		{
			BufferedReader origBR = new BufferedReader(new StringReader(aText));
			String line;
			ret = new ArrayList<String>();
			while ((line = origBR.readLine()) != null)
			{
				line.replace((char) 0x09, ' ');
				line = line.trim();
				ret.add(line);
			}
		}
		catch (IOException e)
		{

			e.printStackTrace();
		}

		// remove empty lines at the end of the source
		while (ret.size() != 0 && ret.get(ret.size() - 1).isEmpty())
		{
			ret.remove(ret.size() - 1);
		}

		return ret;
	}

	public static IASTTranslationUnit getTranslationUnit(IRPType aType)
	{
		String declaration = aType.getDeclaration();
		declaration = declaration.replace("%s", (aType.getName() + ";"));
		IASTTranslationUnit translationUnit = getTranslationUnitFromBody(declaration);
		return translationUnit;
	}

	public static boolean isPointer(IRPType aType)
	{
		boolean ret = false;
		IASTTranslationUnit translationUnit = getTranslationUnit(aType);
		ASTNode<IASTPointer> parseTemplate = new ASTNode<IASTPointer>(IASTPointer.class);
		IASTPointer pointer = parseTemplate.getNode(translationUnit);

		ret = (pointer != null);

		return ret;
	}

	public static List<String> getTemplateArguments(ICPPASTTemplateId aTemplateId)
	{
		List<String> ret = new ArrayList<String>();
		IASTNode[] nodes = aTemplateId.getTemplateArguments();

		ASTNode<ICPPASTQualifiedName> parseQualifiedName = new ASTNode<ICPPASTQualifiedName>(
				ICPPASTQualifiedName.class);
		ICPPASTQualifiedName qualifiedName;

		for (IASTNode n : nodes)
		{
			qualifiedName = parseQualifiedName.getNode(n);
			if (qualifiedName != null)
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
		ASTNode<IASTSimpleDeclaration> parseSimpleDeclaration = new ASTNode<IASTSimpleDeclaration>(
				IASTSimpleDeclaration.class);
		return parseSimpleDeclaration.getNodes(aNode);
	}

	public static List<IASTNamedTypeSpecifier> getNamedTypeSpecifiers(IASTNode aNode)
	{
		ASTNode<IASTNamedTypeSpecifier> parseNamedTypeSpecifiers = new ASTNode<IASTNamedTypeSpecifier>(
				IASTNamedTypeSpecifier.class);
		return parseNamedTypeSpecifiers.getNodes(aNode);
	}

	public static IASTFunctionDeclarator getFunctionDeclarator(IASTNode aNode)
	{
		ASTNode<IASTFunctionDeclarator> parseFunctionDeclarator = new ASTNode<IASTFunctionDeclarator>(
				IASTFunctionDeclarator.class);
		return parseFunctionDeclarator.getNode(aNode);
	}

	public static List<IASTParameterDeclaration> getParameterDeclaration(IASTNode aNode)
	{
		ASTNode<IASTParameterDeclaration> parseParameterDeclaration = new ASTNode<IASTParameterDeclaration>(
				IASTParameterDeclaration.class);
		return parseParameterDeclaration.getNodes(aNode);
	}

	public static ICPPASTQualifiedName getQualifiedName(IASTNode aNode)
	{
		ASTNode<ICPPASTQualifiedName> parseQualifiedName = new ASTNode<ICPPASTQualifiedName>(
				ICPPASTQualifiedName.class);
		return parseQualifiedName.getNode(aNode);
	}

	public static IASTDeclarator getDeclarator(IASTNode aNode)
	{
		ASTNode<IASTDeclarator> parseDeclarator = new ASTNode<IASTDeclarator>(IASTDeclarator.class);
		return parseDeclarator.getNode(aNode);
	}
	/*
	 * public static FunctionCompletion getFunctionCompletion(IASTSimpleDeclaration
	 * aSimpleDeclaration, CompletionProvider aProvider) { IASTFunctionDeclarator
	 * functionDeclarator = getFunctionDeclarator(aSimpleDeclaration);
	 * 
	 * 
	 * 
	 * if(functionDeclarator==null) { return null; //no function }
	 * 
	 * String functionName = functionDeclarator.getName().toString();
	 * 
	 * IASTDeclSpecifier returnDecl = aSimpleDeclaration.getDeclSpecifier();
	 * if(returnDecl instanceof IASTSimpleDeclaration) { IASTSimpleDeclSpecifier
	 * sReturnDecl = (IASTSimpleDeclSpecifier)returnDecl;
	 * sReturnDecl.getDeclTypeExpression().to }
	 * 
	 * String returnType = returnDecl.get
	 * 
	 * FunctionCompletion fc = new FunctionCompletion(aProvider, functionName,
	 * returnType);
	 * 
	 * }
	 * 
	 * public static IASTSimpleDeclSpecifier
	 * getSimpleDeclSpecifier(IASTSimpleDeclaration aSimpleDeclaration) { return
	 * aSimpleDeclaration.getDeclSpecifier(); }
	 * 
	 */

	public static Map<String, String> getFunctiondefinitions(String aSourcePath, String aNameSpace, String aClassName)
	{

		HashMap<String, String> ret = new HashMap<>();

		IASTTranslationUnit translationUnit = getTranslationUnit(aSourcePath);

		IASTNode source = translationUnit;

		if (aNameSpace != null)
		{
			// parse for namespace
			ASTNode<ICPPASTNamespaceDefinition> parseNameSpace = new ASTNode<ICPPASTNamespaceDefinition>(
					ICPPASTNamespaceDefinition.class);
			List<ICPPASTNamespaceDefinition> nameSpaceDefinitions = parseNameSpace.getNodes(translationUnit);
			ICPPASTNamespaceDefinition theNameSpaceDefinition = null;
			for (ICPPASTNamespaceDefinition nameSpaceDefiniton : nameSpaceDefinitions)
			{
				if (aNameSpace.equals(nameSpaceDefiniton.getName().toString()))
				{
					theNameSpaceDefinition = nameSpaceDefiniton;
					break;
				}
			}

			if (theNameSpaceDefinition == null)
			{
				// namespace not found
				return ret;
			}
			source = theNameSpaceDefinition;
		}

		ASTNode<IASTFunctionDefinition> parseFunctionDefinition = new ASTNode<IASTFunctionDefinition>(
				IASTFunctionDefinition.class);
		List<IASTFunctionDefinition> functionDefinitions = parseFunctionDefinition.getNodes(source);
		for (IASTFunctionDefinition functionDefinition : functionDefinitions)
		{
			IASTFunctionDeclarator functionDeclarator = functionDefinition.getDeclarator();

			IASTName name = functionDeclarator.getName();

			if (name instanceof CPPASTQualifiedName)
			{
				CPPASTQualifiedName qName = (CPPASTQualifiedName) name;

				ICPPASTNameSpecifier[] spec = qName.getQualifier();

				if (spec[spec.length - 1].toString().equals(aClassName) == false)
				{
					continue;
				}

			}

			Map.Entry<String, String> e = getSourceFromFunction(functionDefinition, aSourcePath);

			if (e == null)
			{
				continue;
			}

			trace("Map Signature: " + e.getKey());

			ret.put(e.getKey(), e.getValue());

		}

		return ret;

	}

	public static String getFunctionSignature(IASTFunctionDefinition aFunctionDefinition)
	{
		IASTFunctionDeclarator functionDeclarator = aFunctionDefinition.getDeclarator();
		return functionDeclarator.getRawSignature();
	}

	public static String getSourceFromMap(Map<String, String> aMap, IRPOperation aOperation)
	{

		IRPModelElement element = aOperation.getOwner();
		if (element instanceof IRPClass == false)
		{
			trace("owner of operation is not a class!");
			return null;
		}

		IRPClass selectedClass = (IRPClass) element;

		String operationSignature = getOperationSignature(aOperation, selectedClass.getName());

		String ret = aMap.get(operationSignature);
		if (ret == null)
		{
			trace("Not found signature " + operationSignature);
		}

		return ret;

	}

	public static String getOperationBody(String aPath, String aNameSpace, String aClassName, IRPOperation aOperation)
	{

		String operationSignature = getOperationSignature(aOperation, aClassName);

		IASTTranslationUnit translationUnit = getTranslationUnit(aPath);

		IASTNode source = translationUnit;

		if (aNameSpace != null)
		{
			// parse for namespace
			ASTNode<ICPPASTNamespaceDefinition> parseNameSpace = new ASTNode<ICPPASTNamespaceDefinition>(
					ICPPASTNamespaceDefinition.class);
			List<ICPPASTNamespaceDefinition> nameSpaceDefinitions = parseNameSpace.getNodes(translationUnit);
			ICPPASTNamespaceDefinition theNameSpaceDefinition = null;
			for (ICPPASTNamespaceDefinition nameSpaceDefiniton : nameSpaceDefinitions)
			{
				if (aNameSpace.equals(nameSpaceDefiniton.getName().toString()))
				{
					theNameSpaceDefinition = nameSpaceDefiniton;
					break;
				}
			}

			if (theNameSpaceDefinition == null)
			{
				// namespace not found
				return null;
			}
			source = theNameSpaceDefinition;
		}

		ASTNode<IASTFunctionDefinition> parseFunctionDefinition = new ASTNode<IASTFunctionDefinition>(
				IASTFunctionDefinition.class);
		List<IASTFunctionDefinition> functionDefinitions = parseFunctionDefinition.getNodes(source);

		Map.Entry<String, String> theFunctionEntry = null;
		for (IASTFunctionDefinition functionDefinition : functionDefinitions)
		{
			IASTFunctionDeclarator functionDeclarator = functionDefinition.getDeclarator();

			IASTName name = functionDeclarator.getName();

			if (name instanceof CPPASTQualifiedName)
			{
				CPPASTQualifiedName qName = (CPPASTQualifiedName) name;

				ICPPASTNameSpecifier[] spec = qName.getQualifier();

				if (spec[spec.length - 1].toString().equals(aClassName) == false)
				{
					continue;
				}

			}

			Map.Entry<String, String> functionEntry = getSourceFromFunction(functionDefinition, aPath);

			String rawSignature = functionEntry.getKey();

			if (rawSignature.equals(operationSignature))
			{
				theFunctionEntry = functionEntry;
				break;
			}
		}

		// check for syntax error
		/*
		 * List<IASTProblem> problems = ASTHelper.getProblems(source);
		 * 
		 * if(problems.isEmpty()==false) { //there is a syntax error in the source... no
		 * roundtrip for(IProblem problem:problems) { System.out.println("Error: "+
		 * problem.getMessageWithLocation()); } return null; }
		 */

		if (theFunctionEntry == null)
		{
			System.out.println("different Signatures");
			return "";
		}

		return theFunctionEntry.getValue();
	}

	public static String getOperationSignature(IRPOperation aOperation, String aClassName)
	{
		return aOperation.getSignatureNoArgNames();
	}

	/*
	 * public static String getOperationSignature(IRPOperation aOperation, String
	 * aClassName) { String operationSignature =
	 * aOperation.getImplementationSignature();
	 * 
	 * //System.out.println("Test Signature: " +aOperation.getSignature());
	 * 
	 * //check for constructor of an reactive class if(aOperation.getIsCtor()==1) {
	 * IRPModelElement owner = aOperation.getOwner(); if(owner instanceof IRPClass)
	 * { IRPClass ownerClass = (IRPClass)owner; if(ownerClass.getIsReactive()==1) {
	 * operationSignature = operationSignature.substring(0,
	 * operationSignature.length()-1)+", IOxfActive* theActiveContext )";
	 * //System.out.println("Reactive Constructor: " + operationSignature); } } }
	 * 
	 * 
	 * int startIndex = operationSignature.indexOf(aOperation.getName());
	 * if(startIndex>=0) { operationSignature =
	 * operationSignature.substring(startIndex); operationSignature =
	 * aClassName+"::"+operationSignature; }
	 * 
	 * //System.out.println("Op. Signature: " + operationSignature);
	 * operationSignature = operationSignature.replaceAll("\\s+", ""); return
	 * operationSignature; }
	 * 
	 */

	private static int getOffsetFromFunction(IASTFunctionDefinition aFunctionDefinition, String aPath)
	{

		IASTStatement functionBody = aFunctionDefinition.getBody();
		if (functionBody == null)
		{
			return -1;
		}
		IASTFileLocation fileLocation = functionBody.getFileLocation();

		int lineNumber = fileLocation.getStartingLineNumber();

		return lineNumber;

	}

	public static String getSourceFromFunction(IASTFunctionDefinition aFunctionDefinition)
	{
		IASTStatement statement = aFunctionDefinition.getBody();
		if (statement == null)
		{
			return null;
		}

		String body = statement.getRawSignature();
		return body;
	}

	public static String getSignatureFromBody(String aBody, String aFunctionName)
	{
		StringReader sr = new StringReader(aBody);
		BufferedReader br = new BufferedReader(sr);
		String signature = "";
		String line;
		try
		{
			while ((line = br.readLine()) != null)
			{
				if (line.contains("//#[")) // start of the operation
				{
					signature = line.substring(line.indexOf(aFunctionName));
					trace("Signature: " + signature);
					break;
				}
			}
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return signature;

	}

	@SuppressWarnings("rawtypes")
	private static Map.Entry<String, String> getSourceFromFunction(IASTFunctionDefinition aFunctionDefinition,
			String aPath)
	{

		IASTStatement functionBody = aFunctionDefinition.getBody();
		if (functionBody == null)
		{

			return null;
		}
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

			BufferedReader br = new BufferedReader(sr);

			ArrayList<String> sl = new ArrayList<String>();

			String line;
			String value = "";
			String signature = "";

			while ((line = br.readLine()) != null)
			{
				sl.add(line);
			}

			int i = 0;
			int tabPos = -1;

			for (String checkTab : sl)
			{
				tabPos = checkTab.indexOf("//#[");
				i++;
				if (tabPos != (-1))
				{
					// get signature - parse //#[ operation adapterEnabled(std::string)
					int startSignature = -1;
					String operationMarker = "//#[ operation ";
					startSignature = checkTab.indexOf(operationMarker);
					if (startSignature > 0)
					{
						startSignature = tabPos + operationMarker.length();
						signature = checkTab.substring(startSignature);

					}

					break;
				}
			}

			for (; i < sl.size(); i++)
			{

				line = sl.get(i);
				if (line.contains("//#]"))
				{
					break;
				}
				if (line.length() > tabPos)
				{
					if (line.getBytes()[0] != 0x09)
					{
						line = line.substring(tabPos);
					}
					else if (line.getBytes()[1] == 0x09)
					{
						line = line.substring(2);
					}

				}

				value = value + line + "\n";
			}

			in.close();

			Map.Entry<String, String> ret;

			ret = new AbstractMap.SimpleEntry(signature, value);

			return ret;

		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public static String getSourceText(IRPOperation aOperation, IRPApplication aApplication)
	{

		IRPModelElement element = aOperation.getOwner();
		if (element instanceof IRPClass == false)
		{
			System.out.println("owner of operation is not a class!");
			return null;
		}

		IRPClass selectedClass = (IRPClass) element;
		String filePath = getSourcePath(selectedClass, aApplication);
		if ((aOperation.isATemplate() == 1) || (aOperation.getIsInline() == 1))
		{
			filePath = filePath + ".h";
		}
		else
		{
			filePath = filePath + ".cpp";
		}

		if ((filePath.endsWith("cpp") || filePath.endsWith("h")) == false)
		{
			trace("file not found / component not active: " + selectedClass.getName());
			return null;
		}

		String nameSpace = RhapsodyOperation.getNamespace(selectedClass);

		String className = selectedClass.getName();
		IRPModelElement owner = selectedClass.getOwner();
		while (owner instanceof IRPClass)
		{
			className = owner.getName() + "::" + className;
			owner = owner.getOwner();
		}

		String roundTripText = ASTHelper.getOperationBody(filePath, nameSpace, className, aOperation);
		return roundTripText;
	}

	public static int getSourceOffset(IRPOperation aOperation, IRPApplication aApplication)
	{
		IRPModelElement element = aOperation.getOwner();
		if (element instanceof IRPClass == false)
		{
			trace("owner of operation is not a class!");
			return -1;
		}

		IRPClass selectedClass = (IRPClass) element;
		String filePath = getSourcePath(selectedClass, aApplication);
		if ((aOperation.isATemplate() == 1) || (aOperation.getIsInline() == 1))
		{
			filePath = filePath + ".h";
		}
		else
		{
			filePath = filePath + ".cpp";
		}

		if ((filePath.endsWith("cpp") || filePath.endsWith("h")) == false)
		{
			trace("file not found / component not active: " + selectedClass.getName());
			return -1;
		}

		String nameSpace = RhapsodyOperation.getNamespace(selectedClass);

		String className = selectedClass.getName();
		IRPModelElement owner = selectedClass.getOwner();
		while (owner instanceof IRPClass)
		{
			className = owner.getName() + "::" + className;
			owner = owner.getOwner();
		}

		String operationSignature = aOperation.getSignatureNoArgNames();

		IASTTranslationUnit translationUnit = getTranslationUnit(filePath);

		IASTNode source = translationUnit;

		if (nameSpace != null)
		{
			// parse for namespace
			ASTNode<ICPPASTNamespaceDefinition> parseNameSpace = new ASTNode<ICPPASTNamespaceDefinition>(
					ICPPASTNamespaceDefinition.class);
			List<ICPPASTNamespaceDefinition> nameSpaceDefinitions = parseNameSpace.getNodes(translationUnit);
			ICPPASTNamespaceDefinition theNameSpaceDefinition = null;
			for (ICPPASTNamespaceDefinition nameSpaceDefiniton : nameSpaceDefinitions)
			{
				if (nameSpace.equals(nameSpaceDefiniton.getName().toString()))
				{
					theNameSpaceDefinition = nameSpaceDefiniton;
					break;
				}
			}

			if (theNameSpaceDefinition == null)
			{
				// namespace not found
				return -1;
			}
			source = theNameSpaceDefinition;
		}

		ASTNode<IASTFunctionDefinition> parseFunctionDefinition = new ASTNode<IASTFunctionDefinition>(
				IASTFunctionDefinition.class);
		List<IASTFunctionDefinition> functionDefinitions = parseFunctionDefinition.getNodes(source);

		Map.Entry<String, String> theFunctionEntry = null;
		for (IASTFunctionDefinition functionDefinition : functionDefinitions)
		{
			Map.Entry<String, String> functionEntry = getSourceFromFunction(functionDefinition, filePath);

			String rawSignature = functionEntry.getKey();

			if (rawSignature.equals(operationSignature))
			{
				return getOffsetFromFunction(functionDefinition, filePath);
			}
		}

		return -1;

	}

	@SuppressWarnings("unchecked")
	public static String getSourcePath(IRPClass aClass, IRPApplication aApplication)
	{
		List<IRPProject> projects = aApplication.getProjects().toList();

		IRPComponent activeComponent = null;

		for (IRPProject project : projects)
		{
			activeComponent = project.getActiveComponent();
			if (activeComponent != null)
			{
				break;
			}
		}
		String filePath = null;

		List<IRPConfiguration> configurations = activeComponent.getConfigurations().toList();
		for (IRPConfiguration configuration : configurations)
		{
			filePath = configuration.getDirectory(1, "");
		}

		List<IRPModelElement> scopeElements = activeComponent.getScopeElements().toList();

		IRPModelElement selectedElement = aClass;

		while (selectedElement.getOwner() instanceof IRPClass)
		{

			selectedElement = selectedElement.getOwner();
		}

		for (IRPModelElement scopeElement : scopeElements)
		{
			if (scopeElement.equals(selectedElement))
			{

				filePath = filePath + "\\" + selectedElement.getName();

				break;
			}
		}

		return filePath;
	}

	public static String getSourcePath(IRPPackage aPackage, IRPApplication aApplication)
	{
		List<IRPProject> projects = aApplication.getProjects().toList();

		IRPComponent activeComponent = null;

		for (IRPProject project : projects)
		{
			activeComponent = project.getActiveComponent();
			if (activeComponent != null)
			{
				break;
			}
		}

		String filePath = null;

		List<IRPConfiguration> configurations = activeComponent.getConfigurations().toList();
		for (IRPConfiguration configuration : configurations)
		{
			filePath = configuration.getDirectory(1, "");
		}

		if (filePath == null)
		{
			return null;
		}

		List<IRPModelElement> scopeElements = activeComponent.getScopeElements().toList();
		for (IRPModelElement scopeElement : scopeElements)
		{
			if (scopeElement.equals(aPackage))
			{
				filePath = filePath + "\\" + activeComponent.getName();
				return filePath;
			}
		}

		return null;
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
		if (aNode == null)
		{
			return null;
		}

		if (myType.isAssignableFrom(aNode.getClass()))
		{
			return myType.cast(aNode);
		}

		IASTNode[] nodes = aNode.getChildren();
		// first check on higher level
		for (IASTNode node : nodes)
		{
			if (myType.isAssignableFrom(node.getClass()))
			{
				return myType.cast(node);
			}
		}
		for (IASTNode node : nodes)
		{
			T n = getNode(node);
			if (n != null)
			{
				return n;
			}
		}

		return null;
	}

	public List<T> getNodes(IASTNode aNode)
	{
		ArrayList<T> ret = new ArrayList<T>();
		if (aNode == null)
		{
			return ret;
		}

		if (myType.isAssignableFrom(aNode.getClass()))
		{
			ret.add(myType.cast(aNode));
			return ret;
		}

		IASTNode[] nodes = aNode.getChildren();
		for (IASTNode node : nodes)
		{
			ret.addAll(getNodes(node));
		}

		return ret;

	}

}
