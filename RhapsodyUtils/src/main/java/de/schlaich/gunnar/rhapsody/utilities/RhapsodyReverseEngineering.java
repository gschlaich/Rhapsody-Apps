package de.schlaich.gunnar.rhapsody.utilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.eclipse.cdt.core.dom.ast.ASTTypeUtil;
import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTArrayDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTArrayModifier;
import org.eclipse.cdt.core.dom.ast.IASTAttribute;
import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTPointer;
import org.eclipse.cdt.core.dom.ast.IASTPointerOperator;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
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
import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPComponent;
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
		
		String includePath = selected.getPropertyValue("CPP_CG.Package.USMIncludePath");
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
		
		includePath = includePath.replace("<usm_root>", usmPathString);
		
		trace("IncludePath: " + includePath);
		
		File includePathFile = new File(includePath);
		
		if (includePathFile.exists()==false)
		{
			trace("IncludePath does not exist: " + includePath);
			return;
		}
		
		//find all header files in the includePath
		File[] headerFiles = includePathFile.listFiles((dir, name) -> name.endsWith(".h") || name.endsWith(".hpp"));
		
		for (File headerFile : headerFiles)
		{
			trace("Found Header File: " + headerFile.getAbsolutePath());
			
			parseHeaderFile(headerFile,selected);
			
		}
	}
	
	public void parseHeaderFile(File aHeaderFile, IRPPackage aPackage)
	{
		
		trace("---------------------------------------------- "+aHeaderFile.getName()+" ---------------------------------------------------------------------");
		
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
			
			IASTPreprocessorIncludeStatement[] includes = translationUnit.getIncludeDirectives();
			
			for (IASTPreprocessorIncludeStatement include : includes) 
			{
				System.out.println("include - " + include.getName());
			}
			
			ASTVisitor visitor = new ASTVisitor()
			{
				
				private boolean isC = false;
				@Override
				public int visit(IASTAttribute attribute)
				{
					trace("Attribute: " + attribute.getRawSignature());
					return PROCESS_CONTINUE;
				}
				
				@Override 
				public int visit(IASTDeclaration d)
				{
					if(d instanceof IASTSimpleDeclaration)
					{
						IASTSimpleDeclaration sd = (IASTSimpleDeclaration) d;
						IASTDeclSpecifier spec = sd.getDeclSpecifier();
						if (spec != null)
						{
							if (spec instanceof IASTCompositeTypeSpecifier)
							{
								IASTCompositeTypeSpecifier ct = (IASTCompositeTypeSpecifier) spec;
								
							}
						}
					}
					else if (d instanceof IASTPreprocessorIncludeStatement)
					{
						trace("Include: " + ((IASTPreprocessorIncludeStatement) d).getName().toString());
					}
					else if(d instanceof ICPPASTLinkageSpecification)
					{
						trace(" C - Code!");
						isC = true;
						
					}
					else
					{
						trace("Declaration: " + d.toString());
					}

					if (d instanceof IASTSimpleDeclaration) 
					{
						IASTSimpleDeclaration sd = (IASTSimpleDeclaration) d;
			            IASTDeclSpecifier spec = sd.getDeclSpecifier();
			            
			            trace("spec "+ spec.getClass().getSimpleName());
			           
			            
			            if (spec.getStorageClass() == IASTDeclSpecifier.sc_typedef) 
			            {
			                for (IASTDeclarator dec : sd.getDeclarators()) 
			                {
			                    String alias  = dec.getName().toString();        // neuer Name
			                    String target = buildQuickType(spec, dec);       // Zieltyp
			                    trace("Typedef Alias: " + alias + " -> " + target);
			                }
			            }
			            

			            // a) Klassendefinition ---------------------------------------
			            else if (spec instanceof IASTCompositeTypeSpecifier) 
			            {
		               
		                	IASTCompositeTypeSpecifier ct = (IASTCompositeTypeSpecifier) spec;
		                	parseClass(ct,myNamespaceElement);
			                
			            }
			            else
			            {
			            	
			            	/* Sind darin Funktions-Declarators? */
			                for (IASTDeclarator dec : sd.getDeclarators()) 
			                {
			                    if (dec instanceof IASTFunctionDeclarator) 
			                    {
			                        IASTFunctionDeclarator fdec = (IASTFunctionDeclarator) dec;

			                        String ret   = sd.getDeclSpecifier().getRawSignature().trim();
			                        //boolean varg = fdec.takesVarArgs();
			                        
			                        if(fdec instanceof IASTStandardFunctionDeclarator)
			                        {
			                        	IASTStandardFunctionDeclarator fstdec = (IASTStandardFunctionDeclarator) fdec;
			                        	List<argumentPair> params = parseArguments(fstdec);
			                        }
			                        
			                        
			                        //check if function already exists
			                        //List<IRPOperation> operations = myNamespaceElement.getOp
			                        
			                        
			                        IRPOperation function = (IRPOperation) myNamespaceElement.addNewAggr("Operation", fdec.getName().toString());
			                        
			                        if(function == null)
			                        {
										trace("Function not created: " + fdec.getName().toString());
										continue;
			                        }
			                        
			                        // Set return type
			                        IRPClassifier returnType = myNamespaceElement.getProject().findType(ret);
			                        
			                        
									if (returnType != null)
									{
										function.setReturns(returnType);
									}
									else
									{
									
										if(ret.equals("void")==false)
										{
											//set on the fly type
											trace("Return type not found: " + ret + " - setting on the fly type");
											function.setReturnTypeDeclaration(ret);
										}
										
									}
									
									
			                        
			                        
			                       

//			                        CppFunction fn = new CppFunction(
//			                            fdec.getName().toString(), ret, params,
//			                            sd.isInline(),                /* inline ? */
//			                            sd.getStorageClass() == IASTDeclSpecifier.sc_static,
//			                            varg);
//
//			                        currentNs().functions.add(fn);
			                    }
			                }
			            }
			        }
					
					else if (d instanceof ICPPASTAliasDeclaration) 
					{
						ICPPASTAliasDeclaration ad = (ICPPASTAliasDeclaration) d;

					    String alias  = ad.getAlias().toString();
					    
					    ICPPASTTypeId id = ad.getMappingTypeId();
					   
					    
					    String target = id.getRawSignature().trim(); 
					    
					    trace("Alias Declaration: " + alias + " -> " + target);
					   
					}
					
			        return PROCESS_CONTINUE;
			    }
				
				
				
				@Override
				public int visit(ICPPASTNamespaceDefinition namespaceDefinition)
				{
					// TODO Auto-generated method stub
					String packageName = namespaceDefinition.getName().getRawSignature();
					
					myNamespaceElement = (IRPPackage) aPackage.findNestedElementRecursive(packageName, "Package");
					
					if (myNamespaceElement == null)
					{
						trace("NamespacePackage not found: " + packageName);	
					}
					else
					{
						trace("NamespacePackage found: " + packageName + " - " + myNamespaceElement.getName());
					}
					
					
					trace("NamespaceDefinition: " + namespaceDefinition.getName().getRawSignature());
					return PROCESS_CONTINUE;
				}
				
				
				

			};
			
			visitor.shouldVisitAttributes = true;
			visitor.shouldVisitDeclarations = true;
			visitor.shouldVisitDeclSpecifiers = true;
			visitor.shouldVisitDeclarators = true;
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


