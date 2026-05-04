package de.schlaich.gunnar.rhapsody.utilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.JOptionPane;

import org.eclipse.cdt.core.dom.ast.ASTTypeUtil;
import org.eclipse.cdt.core.dom.ast.IASTComment;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTArrayDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTArrayModifier;
import org.eclipse.cdt.core.dom.ast.IASTAttribute;
import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTEnumerationSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTEnumerationSpecifier.IASTEnumerator;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTPointer;
import org.eclipse.cdt.core.dom.ast.IASTPointerOperator;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorMacroDefinition;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTStandardFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTAliasDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTLinkageSpecification;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTReferenceOperator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTypeId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTVisibilityLabel;
import org.eclipse.cdt.core.dom.ast.gnu.cpp.GPPLanguage;
import org.eclipse.cdt.core.parser.DefaultLogService;
import org.eclipse.cdt.core.parser.FileContent;
import org.eclipse.cdt.core.parser.IParserLogService;
import org.eclipse.cdt.core.parser.IScannerInfo;
import org.eclipse.cdt.core.parser.IncludeFileContentProvider;
import org.eclipse.cdt.core.parser.ScannerInfo;
import org.eclipse.core.runtime.CoreException;

import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPArgument;
import com.telelogic.rhapsody.core.IRPAttribute;
import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPComponent;
import com.telelogic.rhapsody.core.IRPEnumerationLiteral;
import com.telelogic.rhapsody.core.IRPFile;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPOperation;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPType;

public class RhapsodyReverseEngineering
{

	private Consumer<String> myTraceAction = null;
	private IRPApplication myApplication = null;
	
	private IRPPackage myNamespaceElement = null;
	
	private IRPFile mySourceArtifact = null;

	/** Maps end-line-number → cleaned comment text, built once per file. */
	private Map<Integer, String> myCommentMap = new HashMap<>();
	
	private static RhapsodyReverseEngineering myRhapsodyReverseEngineering = null;
	
	public static RhapsodyReverseEngineering getRhapsodyReverseEngineering(Consumer<String> aTraceAction, IRPApplication aApplication)
	{
		if (myRhapsodyReverseEngineering == null)
		{
			myRhapsodyReverseEngineering = new RhapsodyReverseEngineering(aTraceAction, aApplication);
		}
		return myRhapsodyReverseEngineering;
	}
	
	private RhapsodyReverseEngineering(Consumer<String> aTraceAction, IRPApplication aApplication)
	{
		myTraceAction = aTraceAction;
		myApplication = aApplication;
	}
	
	private void trace(String aMessage)
	{
		if (myTraceAction == null)
		{
			// no traceaction set...
			return;
		}

		aMessage = "ReverseEngineering: " + aMessage;

		myTraceAction.accept(aMessage);
	}

	
	public void update(IRPPackage selected) 
	{
		
		String includePath = null;
		
		try
		{
			includePath = selected.getPropertyValueExplicit("CPP_CG.Package.USMIncludePath");
		}
		catch (Exception e)
		{
			trace("Property CPP_CG.Package.USMIncludePath not set for Package: " + selected.getName());
			return;
		}
		
		Path usmPath = null;
		
		
		if (includePath == null || includePath.isEmpty())
		{
			trace("No IncludePath set for Package: " + selected.getName());
			return;
		}
		
		trace("Update Package: " + selected.getName() + " IncludePath: " + includePath);
		
		IRPProject project = selected.getProject();
		
		if (project == null)
		{
			trace("No Project found for Package: " + selected.getName());
			return;
		}
		
		
		//teile includepath auf, ist mt ; getrennt
		String[] includePaths = includePath.split(";");
		
		
		//get correct path
		try
		{
			usmPath = RhapsodyHelper.getUSMPath(project);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		
		String usmPathString = usmPath.toString();
		
		for (String path : includePaths)
		{
			
			if(path.isEmpty())
			{
				continue;
			}
			
			path = path.replace("<usm_root>", usmPathString);
		

			trace("IncludePath: " + path);
		
			File includePathFile = new File(path);
			
			String packageName = includePathFile.getName();
			
			List<IRPPackage> nestedPackages = selected.getNestedElementsByMetaClass("Package", 0).toList();
			
			IRPPackage externalPackage = null; 
			
			for (IRPPackage p : nestedPackages)
			{
				if (p.getName().equals(packageName))
				{
					trace("Nested Package already exists: " + packageName);
					externalPackage = p;
					break;
				}
			}
			
			if(externalPackage == null)
			{
				trace("Creating nested Package: " + packageName);
				externalPackage = selected.addNestedPackage(packageName);
				externalPackage.setSeparateSaveUnit(0);
				externalPackage.addStereotype("External", "Package");
			}
			
			
		
			if (includePathFile.exists()==false)
			{
				trace("IncludePath does not exist: " + includePath);
				continue;
			}
		
			//find all header files in the includePath
			File[] headerFiles = includePathFile.listFiles((dir, name) -> name.endsWith(".h") || name.endsWith(".hpp"));
		
			for (File headerFile : headerFiles)
			{
				trace("Found Header File: " + headerFile.getAbsolutePath());
				
				
				int result = JOptionPane.showConfirmDialog(null, "Found Header File: " + headerFile.getAbsolutePath() + "\nDo you want to import this file?", "Import Header File", JOptionPane.YES_NO_OPTION);
				
				if (result != JOptionPane.YES_OPTION)
				{
					trace("User skipped import of: " + headerFile.getAbsolutePath());
					continue;
				}
			
				parseHeaderFile(headerFile,selected, externalPackage);
			
			}
		}
	}
	
	public void parseHeaderFile(File aHeaderFile, IRPPackage aPackage, IRPPackage aExternPackage)
	{
		
		trace("---------------------------------------------- "+aHeaderFile.getName()+" ---------------------------------------------------------------------");
		String headerName = aHeaderFile.getName();
		
		mySourceArtifact = (IRPFile)aExternPackage.findNestedElement(headerName, "File");
		if (mySourceArtifact == null)
		{
			trace("Creating source artifact for file: " + aHeaderFile.getName());
			
			//get header name without extension
			
			headerName = headerName.substring(0, headerName.lastIndexOf('.'));
			
			mySourceArtifact = (IRPFile)aExternPackage.addNewAggr("File", headerName);
			
		}

		//TODO: This comes from property CPP_CG::Package::USMIncludePath	
		//FileContent fileContent = FileContent.createForExternalFileLocation("J:\\USM\\Development\\ExternalSource\\lwip_141\\src\\include\\lwip\\tcp_test.h");
		FileContent fileContent = FileContent.createForExternalFileLocation(aHeaderFile.getAbsolutePath());
		
		
		Map definedSymbols = new HashMap();
		String[] includePaths = new String[0];
		IScannerInfo info = new ScannerInfo(definedSymbols, includePaths);
		IParserLogService log = new DefaultLogService();
		
		
		
		
		
		IncludeFileContentProvider emptyIncludes = IncludeFileContentProvider.getEmptyFilesProvider();
		
		int opts = 8;
		IASTTranslationUnit translationUnit;
		try 
		{
			translationUnit = GPPLanguage.getDefault().getASTTranslationUnit(fileContent, info, emptyIncludes, null, opts, log);
			
			// ── Build comment map for description lookup ───────────────────────────
			buildCommentMap(translationUnit);

			// ── Includes ──────────────────────────────────────────────────────────
			IASTPreprocessorIncludeStatement[] includes = translationUnit.getIncludeDirectives();
			for (IASTPreprocessorIncludeStatement include : includes)
			{
				trace("[INCLUDE] " + include.getName());
			}

			// ── Defines (Macros) ───────────────────────────────────────────────────
			IASTPreprocessorMacroDefinition[] macros =
					translationUnit.getMacroDefinitions();
			for (IASTPreprocessorMacroDefinition macro : macros)
			{
				String macroName = macro.getName().toString();
				String expansion = macro.getExpansion().trim();
				if (expansion.isEmpty())
				{
					trace("[DEFINE]  " + macroName);
				}
				else
				{
					trace("[DEFINE]  " + macroName + " = " + expansion);
				}

				if (macroName.isEmpty()) continue;

				// ── Add define as IRPType to Rhapsody model ────────────────────
				IRPType existingType = (IRPType) aExternPackage.findNestedElement(macroName, "Type");
				if (existingType == null)
				{
					IRPType newType = (IRPType) aExternPackage.addNewAggr("Type", macroName);
					if (newType != null)
					{
						newType.setKind("language");
						newType.setDeclaration("#define %s "+expansion);
						String macroComment = findCommentBefore(macro.getFileLocation() != null ? macro.getFileLocation().getStartingLineNumber() : 0);
						if (macroComment != null) newType.setDescription(macroComment);
						mySourceArtifact.addModelElement(newType, "specFragment");
						trace("  -> Type (Define) created: " + macroName + (expansion.isEmpty() ? "" : " = " + expansion));
					}
					else
					{
						trace("  -> Type (Define) could not be created: " + macroName);
					}
				}
				else
				{
					trace("  -> Type (Define) already exists: " + macroName);
				}
			}

			ASTVisitor visitor = new ASTVisitor()
			{
				@Override
				public int visit(IASTDeclaration d)
				{
					// ── skip inline function bodies (only show declarations) ──────
					if (d instanceof IASTFunctionDefinition)
					{
						IASTFunctionDefinition def = (IASTFunctionDefinition) d;
						IASTFunctionDeclarator fdec = def.getDeclarator();
						String ret  = def.getDeclSpecifier().getRawSignature().trim();
						String name = fdec.getName().toString();
						String args = buildArgString(fdec);
						trace("[FUNCTION] " + ret + " " + name + "(" + args + ")");
						return PROCESS_SKIP;
					}

					if (d instanceof IASTSimpleDeclaration)
					{
						IASTSimpleDeclaration sd = (IASTSimpleDeclaration) d;
						IASTDeclSpecifier spec = sd.getDeclSpecifier();

						// ── Enum ───────────────────────────────────────────────────
						if (spec instanceof IASTEnumerationSpecifier)
						{
							IASTEnumerationSpecifier enumSpec = (IASTEnumerationSpecifier) spec;

							// Name can be on the specifier (e.g. "enum MyEnum { }") OR on the
							// declarator (e.g. "typedef enum { } MyEnum;")
							String enumName = enumSpec.getName().toString();
							if (enumName.isEmpty())
							{
								IASTDeclarator[] decls = sd.getDeclarators();
								if (decls != null && decls.length > 0 && decls[0].getName() != null)
								{
									enumName = decls[0].getName().toString();
								}
							}
							trace("[ENUM]    " + (enumName.isEmpty() ? "<anonymous>" : enumName));

							//StringBuilder enumLiterals = new StringBuilder();
							Pair<String, String> enumLiteral = new Pair<String,String>("", "");
							List<Pair<String, String>> enumLiterals = new java.util.ArrayList<>();

							for (IASTEnumerationSpecifier.IASTEnumerator en : enumSpec.getEnumerators())
							{
								IASTExpression val = en.getValue();
								String enName = en.getName().toString();
								String enValue = (val != null) ? val.getRawSignature().trim() : "";
								int enLine = en.getFileLocation() != null ? en.getFileLocation().getStartingLineNumber() : 0;
								String enComment = findCommentBefore(enLine);
								enumLiterals.add(new Pair<String, String>(enName, enValue) {
									public String comment() { return enComment; }
								});
								
							}

							// ── Add enum as IRPType (Enumeration) to Rhapsody model ──
							if (!enumName.isEmpty())
							{
								IRPType existingEnum = (IRPType) aExternPackage.findNestedElement(enumName, "Type");
								if (existingEnum == null)
								{
									IRPType newEnum = (IRPType) aExternPackage.addNewAggr("Type", enumName);
									if (newEnum != null)
									{
										newEnum.setKind("Enumeration");
										String enumComment = findCommentBefore(sd.getFileLocation() != null ? sd.getFileLocation().getStartingLineNumber() : 0);
										if (enumComment != null) newEnum.setDescription(enumComment);
										for(Pair<String, String> literal : enumLiterals)
										{
											String literalName = literal.first();
											String literalValue = literal.second();
											if (literalValue.isEmpty())
											{
												trace("  -> Adding Enumeration Literal: " + literalName);
											}
											else
											{
												trace("  -> Adding Enumeration Literal: " + literalName + " = " + literalValue);
											}
											IRPEnumerationLiteral l = newEnum.addEnumerationLiteral(literalName);
											l.setValue(literalValue);
											if (literal instanceof Pair && ((Pair<?,?>) literal).getClass() != Pair.class)
											{
												try {
													String litComment = (String) literal.getClass().getMethod("comment").invoke(literal);
													if (litComment != null) l.setDescription(litComment);
												} catch (Exception ignored) {}
											}
										}

										mySourceArtifact.addModelElement(newEnum, "specFragment");
										trace("  -> Type (Enumeration) created: " + enumName);
									}
									else
									{
										trace("  -> Type (Enumeration) could not be created: " + enumName);
									}
								}
								else
								{
									trace("  -> Type (Enumeration) already exists: " + enumName);
								}
							}
							return PROCESS_SKIP;
						}

						// ── typedef / using ────────────────────────────────────────
						if (spec.getStorageClass() == IASTDeclSpecifier.sc_typedef)
						{
							// Skip typedef enum / typedef struct – handled by their own blocks
							if (!(spec instanceof IASTEnumerationSpecifier)
									&& !(spec instanceof IASTCompositeTypeSpecifier))
							{
								for (IASTDeclarator dec : sd.getDeclarators())
								{
									String typedefName = dec.getName().toString();
									String targetType  = buildQuickType(spec, dec);
									trace("[TYPEDEF] " + targetType + " -> " + typedefName);

									if (typedefName.isEmpty()) continue;

									IRPType existingTypedef = (IRPType) aExternPackage.findNestedElement(typedefName, "Type");
									if (existingTypedef == null)
									{
										IRPType newTypedef = (IRPType) aExternPackage.addNewAggr("Type", typedefName);
										if (newTypedef != null)
										{
											newTypedef.setKind("language");
											// Build declaration: "typedef <targetType> <name>;"
											// Replace the typedef name with %s as placeholder
											String decl = "typedef " + targetType + " " + typedefName + ";";
											decl = decl.replace(typedefName, "%s");
											newTypedef.setDeclaration(decl);
											String typedefComment = findCommentBefore(sd.getFileLocation() != null ? sd.getFileLocation().getStartingLineNumber() : 0);
											if (typedefComment != null) newTypedef.setDescription(typedefComment);
											mySourceArtifact.addModelElement(newTypedef, "specFragment");
											trace("  -> Type (typedef) created: " + typedefName + " = " + targetType);
										}
										else
										{
											trace("  -> Type (typedef) could not be created: " + typedefName);
										}
									}
									else
									{
										trace("  -> Type (typedef) already exists: " + typedefName);
									}
								}
							}
							return PROCESS_SKIP;
						}

						// ── class / struct ────────────────────────────────────────
						if (spec instanceof IASTCompositeTypeSpecifier)
						{
							IASTCompositeTypeSpecifier ct = (IASTCompositeTypeSpecifier) spec;
							trace("[CLASS]   " + ct.getName().toString());
							return PROCESS_SKIP;
						}

						// ── Functions or Variables ─────────────────────────────────
						String baseType = spec.getRawSignature().trim();
						for (IASTDeclarator dec : sd.getDeclarators())
						{
							if (dec instanceof IASTFunctionDeclarator)
							{
								IASTFunctionDeclarator fdec = (IASTFunctionDeclarator) dec;
								List<argumentPair> args = extractArguments(fdec);
								//String args = buildArgString(fdec);
								trace("[FUNCTION] " + baseType + " " + fdec.getName().toString() + "(" + args + ")");
								
								String funcName = fdec.getName().toString();
								if(aExternPackage.findNestedElement(funcName, "Operation") == null)
								{
								
									IRPOperation globalFunction = aExternPackage.addGlobalFunction(fdec.getName().toString());
									
									globalFunction.setReturnTypeDeclaration(baseType);
									String funcComment = findCommentBefore(sd.getFileLocation() != null ? sd.getFileLocation().getStartingLineNumber() : 0);
									if (funcComment != null) globalFunction.setDescription(funcComment);
									
									mySourceArtifact.addModelElement(globalFunction, "specFragment");
									
									for (argumentPair arg : args)
									{
										String argType = arg.getType();
										String argName = arg.getName();
										trace("  -> Adding argument: " + argType + " " + argName);
										IRPArgument newArg = globalFunction.addArgument(argName);
										
										IRPProject project = aExternPackage.getProject();
										IRPClassifier type = project.findClass(argType);
										if (type == null)
										{
											type = project.findType(argType);
										}
										if (type != null)
										{
											
										
											
											newArg.setType(type);
											if(arg.isReference==true)
											{
												trace("Argument is reference: " + argName);
												
											}
											
										}
										else
										{
											trace("Type not found: " + argType + " - setting on the fly type");
											newArg.setTypeDeclaration(argType);
										}
									}
									
									
									
								}
							}
							else
							{
								String varName = dec.getName().toString();
								String fullType = buildQuickType(spec, dec);
								String dims = "";
								if (dec instanceof IASTArrayDeclarator)
								{
									StringBuilder sb = new StringBuilder();
									for (IASTArrayModifier am : ((IASTArrayDeclarator) dec).getArrayModifiers())
									{
										sb.append("[");
										if (am.getConstantExpression() != null)
										{
											sb.append(am.getConstantExpression().getRawSignature().trim());
										}
										sb.append("]");
									}
									dims = sb.toString();
								}

								trace("[VARIABLE] " + fullType + " " + varName + dims);

								if (varName.isEmpty()) continue;

								// ── Add global variable to Rhapsody model ──────────
								IRPAttribute globalVar = aExternPackage.addGlobalVariable(varName);
										

								if (globalVar != null)
								{
									String typeDecl = fullType + dims;
									IRPProject project = aExternPackage.getProject();
									IRPClassifier classifier = project.findClass(fullType);
									if (classifier == null)
									{
										classifier = project.findType(fullType);
									}
									if (classifier != null)
									{
										globalVar.setType(classifier);
									}
									else
									{
										globalVar.setTypeDeclaration(typeDecl);
									}
									String varComment = findCommentBefore(sd.getFileLocation() != null ? sd.getFileLocation().getStartingLineNumber() : 0);
									if (varComment != null) globalVar.setDescription(varComment);
									trace("  -> GlobalVariable created: " + varName + " : " + typeDecl);
									
									mySourceArtifact.addModelElement(globalVar, "specFragment");
								}
								else
								{
									trace("  -> GlobalVariable could not be created: " + varName);
								}
							}
						}
						return PROCESS_SKIP;
					}

					// ── namespace alias / using declarations etc. ──────────────────
					if (d instanceof ICPPASTAliasDeclaration)
					{
						ICPPASTAliasDeclaration ad = (ICPPASTAliasDeclaration) d;
						trace("[ALIAS]   " + ad.getAlias().toString() + " = " + ad.getMappingTypeId().getRawSignature().trim());
						return PROCESS_SKIP;
					}

					return PROCESS_CONTINUE;
				}

				@Override
				public int visit(ICPPASTNamespaceDefinition ns)
				{
					trace("[NAMESPACE] " + ns.getName().getRawSignature());
					return PROCESS_CONTINUE;
				}

				// helper: build comma-separated argument string from extracted list
				
				private String buildArgString(IASTFunctionDeclarator fdec)
				{
					List<argumentPair> args = extractArguments(fdec);
					StringBuilder sb = new StringBuilder();
					for (argumentPair arg : args)
					{
						if (sb.length() > 0) sb.append(", ");
						sb.append(arg.getType());
						if (!arg.getName().isEmpty()) sb.append(" ").append(arg.getName());
					}
					if (fdec instanceof IASTStandardFunctionDeclarator
							&& ((IASTStandardFunctionDeclarator) fdec).takesVarArgs())
					{
						if (sb.length() > 0) sb.append(", ");
						sb.append("...");
					}
					return sb.toString();
				}

			};
			
			visitor.shouldVisitDeclarations = true;
			visitor.shouldVisitNamespaces = true;
		
			
			
			trace("------------Start Visitor:");
			
			translationUnit.accept(visitor);
			
		}
		catch (CoreException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	}
		
	private String buildQuickType(IASTDeclSpecifier spec, IASTDeclarator dec)
	{
		StringBuilder sb = new StringBuilder(spec.getRawSignature().trim());

		if (dec != null)
		{
			for (IASTPointerOperator op : dec.getPointerOperators())
			{
				if (op instanceof IASTPointer)
				{
					sb.append(" *");
					if (((IASTPointer) op).isConst()) sb.append(" const");
					if (((IASTPointer) op).isVolatile()) sb.append(" volatile");
				}
				else if (op instanceof ICPPASTReferenceOperator)
				{
					sb.append(((ICPPASTReferenceOperator) op).isRValueReference() ? " &&" : " &");
				}
			}
		}
		return sb.toString().replaceAll("\\s+", " ").trim();
	}

	/**
	 * Collects all comments from the translation unit into {@link #myCommentMap}.
	 * The map key is the <em>last</em> line number of the comment so that a lookup
	 * with the first line of the following declaration finds the preceding comment.
	 */
	private void buildCommentMap(IASTTranslationUnit tu)
	{
		myCommentMap.clear();
		for (IASTComment c : tu.getComments())
		{
			IASTFileLocation loc = c.getFileLocation();
			if (loc == null) continue;
			int endLine = loc.getStartingLineNumber() + countNewlines(new String(c.getComment()));
			String raw = new String(c.getComment()).trim();
			String text = raw
					.replaceAll("^/\\*+\\s*", "")
					.replaceAll("\\s*\\*+/$", "")
					.replaceAll("(?m)^\\s*\\*\\s?", "")
					.replaceAll("^//+\\s*", "")
					.trim();
			if (!text.isEmpty())
			{
				myCommentMap.put(endLine, text);
			}
		}
	}

	private static int countNewlines(String s)
	{
		int n = 0;
		for (char ch : s.toCharArray()) if (ch == '\n') n++;
		return n;
	}

	/**
	 * Returns the comment text whose last line is directly above
	 * {@code declarationStartLine}, or {@code null} if none found.
	 */
	private String findCommentBefore(int declarationStartLine)
	{
		for (int offset = 0; offset <= 2; offset++)
		{
			String comment = myCommentMap.get(declarationStartLine - 1 - offset);
			if (comment != null) return comment;
		}
		return null;
	}

	private IRPClass parseClass(IASTCompositeTypeSpecifier ct, IRPModelElement aParent)
	{	
		String className = ct.getName().toString();
		if (className.isEmpty())
		{
			trace("Class name is empty, skipping.");
			return null;
		}
		
		trace("-------------------------Parsing class: " + className);

		// Check if class already exists
		IRPClass existingClass = (IRPClass) aParent.findNestedElementRecursive(className, "Class");
		
		if (existingClass == null)
		{
			//create new class
			trace("Class " + className + " not found, creating new class.");
			existingClass = (IRPClass)aParent.addNewAggr("Class", className);
		}
		
		/* Ausgangssichtbarkeit */
        int currentVis = ICPPASTVisibilityLabel.v_private;
        
        if(ct.getKey() == ICPPASTCompositeTypeSpecifier.k_struct)
        {
            currentVis = ICPPASTVisibilityLabel.v_public;
        }
          

        for (IASTDeclaration member : ct.getMembers()) 
        {
        	trace("Member: " + member.getRawSignature());
        	trace("Member Type: " + member.toString());
        	
        	if(member instanceof ICPPASTVisibilityLabel)
        	{
        		currentVis = ((ICPPASTVisibilityLabel) member).getVisibility();
        	}
        	
        	if (member instanceof IASTFunctionDefinition)
        	{
        	    IASTFunctionDefinition def = (IASTFunctionDefinition) member;
        	    trace("Function Definition: " + def.getDeclarator().getName().toString());
        	    IASTFunctionDeclarator sig = def.getDeclarator();   // Signatur
        	    IASTStatement          body = def.getBody();        // Rumpf
        	}
        	else if (member instanceof IASTSimpleDeclaration) 
        	{
        	    IASTSimpleDeclaration decl = (IASTSimpleDeclaration) member;
        	    
        	    IASTDeclarator[] declarators =  decl.getDeclarators();
				for (IASTDeclarator declarator : declarators)
				{
							
					trace(" declarator: " + declarator.getName().toString());
					trace(" declarator type: " + declarator.toString());
					
					if (declarator instanceof IASTFunctionDeclarator)
					{
						// Function Declarator
						IASTFunctionDeclarator funcDec = (IASTFunctionDeclarator) declarator;
						String funcName = funcDec.getName().toString();

						trace("Function Declarator: " + funcName);
						
						// Parse arguments
						List<argumentPair> arguments = parseArguments((IASTStandardFunctionDeclarator) funcDec);
						
						// Check if function already exists
						List<IRPOperation> operations = existingClass.getOperations().toList();
						
						boolean operationExists = false;
						
						for(IRPOperation op : operations)
                        {
							
							String opName = op.getName();
							if(opName.equals(funcName))
							{
								trace("Operation already exists: " + opName);

								// Check if arguments match
								List<IRPArgument> args = op.getArguments().toList();
								if (args.size() == arguments.size())
								{
									boolean argsMatch = true;
									for (int i = 0; i < args.size(); i++)
									{
										IRPArgument arg = args.get(i);
										argumentPair pair = arguments.get(i);

										if (!arg.getName().equals(pair.getName())
												|| !arg.getType().getName().equals(pair.getType()))
										{
											argsMatch = false;
											break;
										}
									}

									if (argsMatch)
									{
										trace("Arguments match for operation: " + opName);
										operationExists = true;
										continue;
									}
									
								}
								else
								{
									trace("Argument count does not match for operation: " + opName
											+ " - updating arguments.");

								}

								break;
							}
                            
                        }
						
						// If operation does not exist, create it
						if (!operationExists)
						{
							trace("Creating new operation: " + funcName);
							IRPOperation newOperation = existingClass.addOperation(funcName);

							// Set visibility
							switch (currentVis)
							{
							case ICPPASTVisibilityLabel.v_public:
								newOperation.setVisibility("Public");
								break;
							case ICPPASTVisibilityLabel.v_protected:
								newOperation.setVisibility("Protected");
								break;
							case ICPPASTVisibilityLabel.v_private:
								newOperation.setVisibility("Private");
								break;
							default:
								newOperation.setVisibility("Package");
								break;
							}

							// Add arguments
							setArguments(existingClass, arguments, newOperation);
							
						}
						

					}
					else
					{
						// Variable or other type of declarator
						trace("Variable or other type of declarator: " + declarator.getName().toString());
					}
					
				}
        	    
        	   
        	}
        }
        
        return null;
		
        
       
    }

	private void setArguments(IRPModelElement parent, List<argumentPair> arguments, IRPOperation newOperation)
	{
		for (argumentPair arg : arguments)
		{
			IRPArgument newArg = newOperation.addArgument(arg.getName());

			IRPProject project = parent.getProject();
			
			IRPClassifier type = project.findClass(arg.getType());
			if (type == null)
			{
				
				type = project.findType(arg.getType());
			}

			if (type != null)
			{
				if(arg.isReference)
		        {
		            trace("Argument is reference: " + arg.getName());
		            //newArg.
		        }
				
				newArg.setType(type);
				
				
				
			}
			else
			{
				trace("Type not found: " + arg.getType() + " - setting on the fly type");
				newArg.setTypeDeclaration(arg.getType());
			}
		}
	}
	
	
	
	
	/**
	 * Extracts function parameters from any IASTFunctionDeclarator as a typed list.
	 * Each entry contains the fully qualified type (including pointer/reference operators)
	 * and the parameter name.
	 */
	public List<argumentPair> extractArguments(IASTFunctionDeclarator fdec)
	{
		if (!(fdec instanceof IASTStandardFunctionDeclarator))
		{
			return java.util.Collections.emptyList();
		}
		return parseArguments((IASTStandardFunctionDeclarator) fdec);
	}

	private List<argumentPair> parseArguments(IASTStandardFunctionDeclarator fdec) 
	{

	    List<argumentPair> arguments = new java.util.ArrayList<>();
        
        // Iteriere über die Parameter der Funktion
        for (IASTParameterDeclaration p : fdec.getParameters()) 
        {
            // Typ exakt so, wie er im Quelltext steht
            String type = p.getDeclSpecifier().getRawSignature().trim();

            // Name (kann leer sein, z. B. in Funktions­zeigern oder 'void f(int)')
            String name = "";
            IASTDeclarator pd = p.getDeclarator();
            if (pd != null && pd.getName() != null)
            {
                name = pd.getName().toString();
            }
            
            argumentPair pair = new argumentPair(type, name);
            buildQuickType(p, pair);
            
            arguments.add(pair);
        }
        
        return arguments;
	}
	
	private void buildQuickType(IASTParameterDeclaration p, argumentPair aPair) 
	{
	    

	    /* 1. Teil: Basistyp + führende Qualifier -------------------------- */
	    IASTDeclSpecifier spec = p.getDeclSpecifier();
	        
	    /* 2. Teil: Pointer/Reference-Operatoren --------------------------- */
	    IASTDeclarator d = p.getDeclarator();
	    if (d != null) {
	        for (IASTPointerOperator op : d.getPointerOperators()) {

	            if (op instanceof IASTPointer) 
	            {  
	            	aPair.isPointer = true;
	                
	                IASTPointer ptr = (IASTPointer) op;
	                aPair.isConst =  ptr.isConst();   
	                aPair.isVolatile =  ptr.isVolatile();
	            }
	            else if (op instanceof ICPPASTReferenceOperator) 
	            {  
	            	ICPPASTReferenceOperator ref = (ICPPASTReferenceOperator) op;
	            	aPair.isReference = ref.isRValueReference();
	            }
	        }

	        /* 3. Teil (optional): Array-Deklaratoren ---------------------- */
	        if (d instanceof IASTArrayDeclarator) 
	        {            
	            
	            aPair.isArray = true;
	        }
	    }

	}
	
	
//		for (IASTParameterDeclaration p : fdec.getParameters()) 
//	    {
//	        // ► Typ exakt so, wie er im Quelltext steht
//	        String type = p.getDeclSpecifier().getRawSignature().trim();
//
//	        // ► Name (kann leer sein, z. B. in Funktions­zeigern oder 'void f(int)')
//	        String name = "";
//	        IASTDeclarator pd = p.getDeclarator();
//	        if (pd != null && pd.getName() != null)
//	        {
//	            name = pd.getName().toString();
//	        }
//	        
//	        // check if param exists
//	        List<IRPArgument> arguments = aOperation.getArguments().toList();
//	        
//			for (IRPArgument arg : arguments)
//			{
//				String argName = arg.getName();
//				String argType = arg.getType().getName();
//				
//				if(argName.equals(name) && argType.equals(type))
//                {
//                    trace("Argument already exists: " + name + " - " + type);
//                   
//                }
//				else
//				{
//					// add new argument
//					trace("Adding new Argument: " + name + " - " + type);
//					IRPArgument newArg = aOperation.addArgument(name);
//					
//					IRPProject project = aOperation.getProject();
//					IRPType t = project.findType(type);
//					if (t != null)
//					{
//						newArg.setType(t);
//					}
//					else
//					{
//						IRPClass c = project.findClass(type);
//						if (c != null)
//						{
//							newArg.setType(c);
//						}
//						else
//						{
//							trace("Type not found: " + type + " - setting on the fly type");
//							
//							newArg.setTypeDeclaration(type);
//						}
//					}
//					
//					
//				}
//				
//				
//			}

	       
	    
	   
	
	
	
		

}

class argumentPair
{
    String type;
    String name;
    public boolean isReference = false;
    public boolean isPointer = false;
    public boolean isConst = false;
    public boolean isVolatile = false;
    public boolean isArray = false;
    public boolean isTemplate = false;
    
    
    
    public argumentPair(String aType, String aName)
    {
        type = aType;
        name = aName;
    }
    
    public String getType()
    {
        return type;
    }
    
    public String getName()
    {
        return name;
    }
}

/**
 * Generic immutable pair of two values.
 * @param <A> type of the first value
 * @param <B> type of the second value
 */
class Pair<A, B>
{
    private final A first;
    private final B second;

    public Pair(A aFirst, B aSecond)
    {
        this.first  = aFirst;
        this.second = aSecond;
    }

    public A first()  { return first;  }
    public B second() { return second; }

    @Override
    public String toString()
    {
        return "(" + first + ", " + second + ")";
    }
}
