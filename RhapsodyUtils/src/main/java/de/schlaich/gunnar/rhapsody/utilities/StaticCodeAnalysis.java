package de.schlaich.gunnar.rhapsody.utilities;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import org.eclipse.cdt.core.IProcessList;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPCollection;
import com.telelogic.rhapsody.core.IRPComment;
import com.telelogic.rhapsody.core.IRPComponent;
import com.telelogic.rhapsody.core.IRPConfiguration;
import com.telelogic.rhapsody.core.IRPDependency;
import com.telelogic.rhapsody.core.IRPHyperLink;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPOperation;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPState;
import com.telelogic.rhapsody.core.IRPStatechart;
import com.telelogic.rhapsody.core.IRPTag;
import com.telelogic.rhapsody.core.IRPTransition;
import com.telelogic.rhapsody.core.IRPUnit;

public class StaticCodeAnalysis {
	

	private IRPApplication myApplication = null;
	
	private static final String EchoOff = "@echo off\n";
	private static final String ClangCmd = "clang-tidy";
	private static final String CppCheckCmd = "cppcheck";
	private static final String CppCheckPlatform = "--platform=win32A";
	private static final String CppCheckEnable = "--enable=warning,performance,style,portability";
	private static final String CPPCheckWarning = "--enable=warning";
	private static final String CppCheckVerbose = "--verbose";
	private static final String CppCheckStd = "--std=c++14";
	private static final String PrecompiledInclude = "-I../../../../../Development/ExternalSource/PrecompiledHeader";
	private static final String OxfInclude = "/LangCpp";
	private static final String OmThreadInclude = "-I../../../../../Development/ExternalSource/oxf/oxf";
	private static final String OsConfigInclude = "/LangCpp/osconfig/WIN32";
	
	private static final String FlawfinderPath = "J:/Utilities/flawfinder-2.0.19"; //TODO: correct path...
	private static final String FlawfinderCmd = "flawfinder";
	private static final String FlawFinderCSV = "--csv";
	
	public static final String ConfigName = "DefaultConfig";
	
	private Consumer<String> myTraceAction = null;
	
	private Path myUSMPath = null;
	
	
	
	private Map<IRPComponent, ComponentIncludes> myComponents = null;

	private static StaticCodeAnalysis myStaticCodeAnalysis = null;

	public StaticCodeAnalysis(IRPApplication aApp, Consumer<String> aTraceAction) {
		myTraceAction = aTraceAction;
		myApplication = aApp;
		
		try
		{
			myUSMPath = RhapsodyHelper.getUSMPath(myApplication.activeProject());
		}
		catch(IOException e)
		{
			trace(e.getMessage());
				
		}
		
		
		myComponents = new HashMap<IRPComponent,ComponentIncludes>();
	}
	
	
	public static StaticCodeAnalysis get(IRPApplication aApp, Consumer<String> aTraceAction)
	{
		if(myStaticCodeAnalysis==null)
		{
			myStaticCodeAnalysis = new StaticCodeAnalysis(aApp, aTraceAction);
		}
		
		return myStaticCodeAnalysis;
	}
	
	public static void formatOperation(IRPOperation aOperation,  Consumer<String> aTraceAction)
	{
		if (aOperation == null)
		{
			return;
		}

		String source = aOperation.getBody();

		if (source == null)
		{
			return;
		}

		String dest = formatString(source);

		if (dest.equals(source) == false)
		{
			aTraceAction.accept("Formatting operation [" + aOperation.getImplementationSignature()+"]");
		}
		else
		{
			return;
		}	
		
		if (dest.equals("") == false)
		{
			aOperation.setBody(dest);
		}
	}
	
	public static void formatClassifier(IRPClassifier aClassifier, Consumer<String> aTraceAction)
	{
		if (aClassifier == null)
		{
			return;
		}
		
		List<IRPOperation> operations = aClassifier.getOperations().toList();
		for (IRPOperation op : operations)
		{
			
			formatOperation(op, aTraceAction);
		}

	}
	
	
	public static String formatString(String aSource)
	{
		Process p;
		String ret = "";
		try
		{
			p = Runtime.getRuntime().exec("clang-format --style=Microsoft");
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

			BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

			BufferedWriter stdOut = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));

			stdOut.write(aSource);
			stdOut.close();
			String s;

			while ((s = stdInput.readLine()) != null)
			{
				ret = ret.concat(s).concat("\n");
			}
			ret = ret.trim();
			p.destroy();

		}
		catch (IOException e)
		{
			e.printStackTrace();
			ret = aSource;
		}
		return ret;
	}
	
	
	
	
	private void trace(String aMessage)
	{
		if(myTraceAction==null)
		{
			//no traceaction set...
			return;
		}
		
		aMessage = "CLang: "+aMessage;
		
		myTraceAction.accept(aMessage);
	}
	
	
	
	
	public static String Analyze(IRPModelElement aSelected, IRPApplication aApplication, Consumer<String> aTraceAction)
	{
		
		if(aSelected==null)
		{
			return "failed";
		}
		
		IRPProject project = aSelected.getProject();
		StaticCodeAnalysis sca = get(aApplication, aTraceAction);
		
		if(aSelected instanceof IRPClass)
		{
			
			IRPClass selectedClass = (IRPClass) aSelected; 

			sca.clang(selectedClass, project);
			sca.cppTest(selectedClass, project);
			sca.flawfinder(selectedClass, project);
			return "ok";
		}
		else if(aSelected instanceof IRPPackage)
		{
			
			Analyze((IRPPackage)aSelected, sca);
			return "ok";
		}
		
		return "failed";	
	}
	
	public static String Clear(IRPModelElement aSelected, IRPApplication aApplication, Consumer<String> aTraceAction)
	{
		if(aSelected==null)
		{
			return "failed";
		}
		
		IRPProject project = aSelected.getProject();
		StaticCodeAnalysis sca = get(aApplication, aTraceAction);
		IRPComponent component = project.getActiveComponent();
		
		if(component==null)
		{
			return "failed";
		}
		
		sca.clear(component);
		
		return "ok";
	}
	
	//calculateCodeComplexity(selected, myRhapsody, this::trace);
	public static String calculateCodeComplexity(IRPModelElement aSelected, IRPApplication aApplication, Consumer<String> aTraceAction)
    {
		if (aSelected == null)
		{
			return "failed";
		}
		
		LizardWrapper lizard = new LizardWrapper(aApplication, aTraceAction);
		
		if (aSelected instanceof IRPClass)
		{
			IRPClass selectedClass = (IRPClass) aSelected;
			lizard.calculateComplexity(selectedClass);
		}
		
		
		
		
		return "ok";
		
	}
			
	
	private void clear(IRPComponent aComponent)
	{
		if(myComponents.remove(aComponent)==null)
		{
			trace("No Component found");
		}
	}
	
	
	
	@SuppressWarnings("unchecked")
	private static String Analyze(IRPPackage aPackage, StaticCodeAnalysis aSca)
	{
		String ret = "";
		
		List<IRPClass> classes = aPackage.getClasses().toList();
		for(IRPClass c : classes)
		{
			ret+=aSca.clang(c, aPackage.getProject());
			ret+=aSca.cppTest(c, aPackage.getProject());
			ret+=aSca.flawfinder(c, aPackage.getProject());
		}
		
		List<IRPPackage> packages = aPackage.getPackages().toList();
		
		for(IRPPackage p : packages)
		{
			ret+=Analyze(p, aSca);
		}
		
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public String clang(IRPClass aClass, IRPProject aProject)
	{
		IRPComponent activeComponent = aProject.getActiveComponent();
		
		trace("Clang: Class "+aClass.getName()+": ");
		
		
		if(aClass.getOwner() instanceof IRPClass)
		{
			trace("Nested class! - exit");
			return null;
		}
		
		String fileName = aClass.getName()+".cpp";
		
		if(aClass.isATemplate()==1)
		{
			fileName = aClass.getName()+".h";
		}
		
		
		try {
			
			File workingFolder = RhapsodyHelper.getActiveDefaultPath(aClass);
		
			String omRoot = System.getenv("OMROOT");
			
			
			ComponentIncludes componentIncludes = null;
			
			if(myComponents.containsKey(activeComponent))
			{
				componentIncludes = myComponents.get(activeComponent);
			}
			else
			{
				componentIncludes = new ComponentIncludes(activeComponent);
				myComponents.put(activeComponent, componentIncludes);
			}
			
			
			List<String> params = new ArrayList<String>();
			
			
			List<String> checks = new ArrayList<String>();
			
			
			
//			checks.add("clang-analyzer-*");
//			checks.add("readability-*,-readability-identifier-length,-readability-simplify-boolean-expr");
//			checks.add("modernize-*,-modernize-use-trailing-return-type,-modernize-use-auto,-modernize-use-nullptr");
//			checks.add("bugprone-*");
//			checks.add("cppcoreguidelines-*,-cppcoreguidelines-pro-type-cstyle-cast,-cppcoreguidelines-prefer-member-initializer,-cppcoreguidelines-owning-memory");
//			checks.add("misc-*,-misc-include-cleaner");
//			checks.add("performance-*,-performance-implicit-cast-in-loop");
//			checks.add("portability-*");
//			checks.add("clang-analyzer-cplusplus*");
//			checks.add("");
			
			//enable all checks
			//checks.add("*");
			
			
			
			IRPTag clangTag = aClass.getTag("clang_tidy");
			
			String checkString = "";
			if(clangTag!=null)
			{
				checkString = clangTag.getValue();
			}
			
			
			/*
			for (String check : checks)
			{
				if (checkString.equals("") == false)
				{
					checkString += ",";
				}
				checkString += check;
			}
			*/
			trace("Checks: " + checkString);
			
			
			
			
			
			
			
			
			
			params.add(fileName);
			//params.add("--checks=clang-analyzer-*,readability-*,-readability-identifier-length,-readability-simplify-boolean-expr,modernize-*,-modernize-use-trailing-return-type,-modernize-use-auto,-modernize-use-nullptr,bugprone-*,cppcoreguidelines-*,cppcoreguidelines-pro-type-cstyle-cast,-cppcoreguidelines-prefer-member-initializer,-cppcoreguidelines-owning-memory,misc-*,-misc-include-cleaner");
			params.add("--config="+checkString);
			params.add("--");
			params.add("-std=c++14");
			params.add(PrecompiledInclude);
			params.add("-I"+omRoot+OxfInclude);
			params.add("-I../../../../../Development/ExternalSource/oxf");
			params.add(OmThreadInclude);
			params.add("-I"+omRoot+OsConfigInclude);
			params.addAll(componentIncludes.getIncludes());
			
			//trace(params.toString());
			
			 ProcessBuilder processBuilder = new ProcessBuilder(ClangCmd);
			 
	         processBuilder.directory(workingFolder);
	         processBuilder.command().addAll(params);
	         
	         processBuilder.redirectErrorStream(true); 
			
	         Process process = processBuilder.start();
	         InputStream inputStream = process.getInputStream();
	         
	         List<IRPComment> comments = aClass.getNestedElementsByMetaClass("Comment", 0).toList();
	         
	         for(IRPComment comment:comments)
	         {
	        	 if(comment.getUserDefinedMetaClass().equals("CodeAnalysisIssue"))
	        	 {
	        		 comment.deleteFromProject();
	        	 }
	         }
	         
	        
			
			BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream));
			String line;
            //StringBuilder output = new StringBuilder();
            List<String> output = new ArrayList<String>();
			
			while ((line = inputReader.readLine()) != null) 
            {
            	output.add(line);
            }


			int exitCode = process.waitFor();
			
			System.out.println("ExitCode " + exitCode);
	
			
			for(String l:output)
			{
				//trace(l);
				
				Pattern pattern = Pattern.compile("^(.*):(\\d+):(\\d+):\\s*(note|warning|error):\\s*(.*)");  
			    Matcher matcher = pattern.matcher(l);
			    
			    IASTTranslationUnit translationUnit =  ASTHelper.getTranslationUnit(workingFolder.getAbsolutePath()+"/"+fileName);
				if(translationUnit==null)
				{
					return null;
				}
				
			    
			    if (matcher.find()) 
			    {
		            // Extrahiere Informationen
		            String fName= matcher.group(1);
		            int lineNumber = Integer.parseInt(matcher.group(2));
		            int positionInLine = Integer.parseInt(matcher.group(3));
		            String errorLevel = matcher.group(4);
		            String infoText = matcher.group(5);
		            
		            
		            IASTFunctionDefinition operationDefinition = ASTHelper.getFunctionDefinition(lineNumber, translationUnit);
		            String operationName = ASTHelper.getOperationName(operationDefinition);
		            String operationSignature = ASTHelper.getOperationSignature(operationDefinition);
		            
		            
		            int offset = (ASTHelper.getOffset(operationDefinition, lineNumber));
		            
		            createIssue(aClass, errorLevel, infoText, operationName, offset, positionInLine);
		            
		           
		            trace("Clang: ---------------------------------------");
		            trace("File: " + fName);
		            trace("Line: " + lineNumber);
		            trace("Operation: "+ operationSignature);			
					trace("Offset: "+ offset);
		            trace("Column: " + positionInLine);
		            trace("Errorlevel: " + errorLevel);
		            trace("Infotext: " + infoText);
		            trace("----------------------------------------------");
		            
		            
		           
		            
		            
		            
			    }

				
			}

			
		}
					
			
			/*

            // Batchdatei als Prozess ausf�hren
           
           
            process.waitFor();
            */
        
		catch (IOException | InterruptedException e) 
		{
            e.printStackTrace();
        }
		
		
		return null;
	
	
		}

	
	public String cppTest(IRPClass aClass, IRPProject aProject) 
	{
	
		trace("CppTest: Class "+aClass.getName()+": ");
		
		String fileName = aClass.getName()+".cpp";
		
		if(aClass.isATemplate()==1)
		{
			fileName = aClass.getName()+".h";
		}
		
		File workingFolder = RhapsodyHelper.getActiveDefaultPath(aClass);
		
		File workingFile = new File(workingFolder,fileName);
		
		try {
			

			String omRoot = System.getenv("OMROOT");
			
			IRPComponent activeComponent = aProject.getActiveComponent(); 
			
			ComponentIncludes componentIncludes = null;
			
			if(myComponents.containsKey(activeComponent))
			{
				componentIncludes = myComponents.get(activeComponent);
			}
			else
			{
				componentIncludes = new ComponentIncludes(activeComponent);
				myComponents.put(activeComponent, componentIncludes);
			}
			
			
			
			List<String> params = new ArrayList<String>();
			
			
			params.add(CppCheckPlatform);
			params.add(CppCheckEnable);
			params.add(CppCheckStd);
			//params.add(PrecompiledInclude);
			//params.add("-I"+omRoot+OxfInclude);
			//params.add("-I../../../../../Development/ExternalSource/oxf");
			//params.add(OmThreadInclude);
			//params.add("-I"+omRoot+OsConfigInclude);
			//params.addAll(componentIncludes.getIncludes());
			params.add(fileName);
			
			trace(params.toString());
			
			 ProcessBuilder processBuilder = new ProcessBuilder(CppCheckCmd);
			 
	         processBuilder.directory(workingFolder);
	         processBuilder.command().addAll(params);
	         
	         processBuilder.redirectErrorStream(true); 
	        
	   
	        Process process = processBuilder.start();
		
	        InputStream inputStream = process.getInputStream();
	        
	        
	        BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream));
			String line;
            //StringBuilder output = new StringBuilder();
            List<String> output = new ArrayList<String>();
			
			while ((line = inputReader.readLine()) != null) 
            {
            	trace(line);
				output.add(line);
            }
			
			

			int exitCode = process.waitFor();
			
			
	        String patternString = "(.*):(\\d+):(\\d+): (error|warning|style): (.*)";

	        
	        Pattern pattern = Pattern.compile(patternString);
	        
	        
	        IASTTranslationUnit translationUnit =  ASTHelper.getTranslationUnit(workingFile.getAbsolutePath());

			
			for(String l:output)
			{
				Matcher matcher = pattern.matcher(l);
				if(matcher.find())
				{
					 String name = matcher.group(1);
			         int lineNumber = Integer.parseInt(matcher.group(2));
			         int columnNumber = Integer.parseInt(matcher.group(3));
			         String errorType = matcher.group(4);
			         String errorMessage = matcher.group(5);
			         
			         trace("CppCheck: ------------------------------------");
			         trace(errorType);
			         trace("LineNumber:" + lineNumber);
			         trace("errorMessage: "+ errorMessage);
			         trace("----------------------------------------------");
			         
			         IASTFunctionDefinition operationDefinition = ASTHelper.getFunctionDefinition(lineNumber, translationUnit);
			         String operationName = ASTHelper.getOperationName(operationDefinition);
			            
			         int offset = (ASTHelper.getOffset(operationDefinition, lineNumber)-1);
			            
			         createIssue(aClass, errorType, errorMessage, operationName, offset, columnNumber);
     
			         
				}
			}
			
			
			trace("end cppCheck");
			
			
			
        
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (InterruptedException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       

		
		return null;
	}
	
	public String flawfinder(IRPClass aClass, IRPProject aProject)
	{
		trace("Flawfinder: Class "+aClass.getName()+": ");
		
		String fileName = aClass.getName()+".cpp";
		
		if(aClass.isATemplate()==1)
		{
			fileName = aClass.getName()+".h";
		}
		
		File workingFolder = RhapsodyHelper.getActiveDefaultPath(aClass);
		
		File workingFile = new File(workingFolder,fileName);
		try 
		{
			
			trace( FlawfinderCmd +" " + FlawFinderCSV + " " + workingFile.getAbsolutePath());
			
			
			ProcessBuilder processBuilder = new ProcessBuilder(FlawfinderCmd, FlawFinderCSV, workingFile.getAbsolutePath());
			 
	        //processBuilder.directory(new File(FlawfinderPath));
	   
	        Process process = processBuilder.start();
		
	        InputStream inputStream = process.getInputStream();
	        InputStream errorStream = process.getErrorStream();
	        
	        BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream));
			String line;
            //StringBuilder output = new StringBuilder();
            List<String> output = new ArrayList<String>();
			
			while ((line = inputReader.readLine()) != null) 
            {
            	trace(line);
				output.add(line);
            }
			
			BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
			while((line= errorReader.readLine()) != null)
			{
				trace(line);
				output.add(line);
			}
			

			int exitCode = process.waitFor();
			
			
			//File,Line,Column,DefaultLevel,Level,Category,Name,Warning,Suggestion,Note,CWEs,Context,Fingerprint,ToolVersion,RuleId,HelpUri
			
			IASTTranslationUnit translationUnit =  ASTHelper.getTranslationUnit(workingFile.getAbsolutePath());

				
			for(String l:output)
			{
				if(l.contains(workingFile.getAbsolutePath()))
				{
					
					 String[] fields = l.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

	       
					if (fields.length >= 16) 
					{
						String file = fields[0];
						int lineNumber = Integer.parseInt(fields[1]);
						int column = Integer.parseInt(fields[2]);
						int defaultLevel = Integer.parseInt(fields[3]);
						int level = Integer.parseInt(fields[4]);
						String category = fields[5];
						String name = fields[6];
						String warning = fields[7];
						String suggestion = fields[8];
						String note = fields[9];
						String cwes = fields[10];
						String context = fields[11];
						String fingerprint = fields[12];
						String toolVersion = fields[13];
						String ruleId = fields[14];
						String helpUri = fields[15];
						
						String message = category+" "+name+"\n"+warning + "\n" + suggestion;
						IASTFunctionDefinition operationDefinition = ASTHelper.getFunctionDefinition(lineNumber, translationUnit);
				        String operationName = ASTHelper.getOperationName(operationDefinition);
				            
				        int offset = (ASTHelper.getOffset(operationDefinition, lineNumber)-1);
				        
				        IRPComment issue = createIssue(aClass, "warning", message, operationName, offset, column);
				        
				        if(issue!=null)
				        {
				        	IRPModelElement e = issue.addNewAggr("HyperLink", cwes);
				        	if(e!=null&&e instanceof IRPHyperLink)
				        	{
				        		IRPHyperLink h = (IRPHyperLink)e;
				        		h.setURL(helpUri);
				        	}
				        }
						
						

					}
					
				}
			}
			
			
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
			
		
		
		
		
		
		
		return null;
	}
	
	
	
	@SuppressWarnings("unchecked")
	private IRPComment createIssue(IRPClass aClass, String errorLevel, String infoText, String operationName,
			int offset, int column)
	{
		if (operationName == null)
		{
			trace("OperationName is null");
			return null;
		}

		IRPUnit unit = aClass.getSaveUnit();
		if (unit == null)
		{
			return null;
		}

		if (unit.isReadOnly() == 1)
		{
			trace("Can not add Issue - Unit is read only");
			return null;
		}

		if (errorLevel.contains("note"))
		{
			return null;
		}

		if (offset <= 0)
		{
			offset = 1;
		}

		Pattern pattern = Pattern.compile("\\[(.*?)\\]");
		Matcher matcher = pattern.matcher(infoText);

		if (matcher.find() == true)
		{
			String errorType = matcher.group(1);
			
			errorLevel = errorType.substring(0);
			errorLevel = errorLevel.replace(",", "_");
			errorLevel = errorLevel.replace(" ", "_");
			errorLevel = errorLevel.replace("-", "_");
			
			
			
		}
		
		String operationNameD = operationName.replace("~", "_");

		String issueName = errorLevel + "_" + operationNameD + "_" + offset;
		
		issueName = issueName.replace("=", "_equal_");
		issueName = issueName.replace(">", "_greater_");
		issueName = issueName.replace("<", "_less_");
		issueName = issueName.replace("+", "_plus_");
		issueName = issueName.replace("-", "_minus_");
		issueName = issueName.replace("&", "_and_");
		issueName = issueName.replace("|", "_or_");
		issueName = issueName.replace("!", "_not_");
		
		

		List<IRPComment> issues = aClass.getNestedElementsByMetaClass("Comment", 0).toList();
		
		for (IRPComment issue : issues)
		{
			if (issue.getName().equals(issueName))
			{
				return null;
			}
		}
		

        IRPComment staticAnalysisIssue = (IRPComment) aClass.addNewAggr("CodeAnalysisIssue", issueName);
        
        
        
        staticAnalysisIssue.setDescription(infoText);
        staticAnalysisIssue.setBody(errorLevel);
        staticAnalysisIssue.setSpecification(operationName+" "+offset+" "+column);
        
        if(staticAnalysisIssue!=null)
        {
            //get the operation...
            addAnchor(aClass, operationName, staticAnalysisIssue); 
            
           
            
        }
        return staticAnalysisIssue;
	}

	private void addAnchor(IRPClass aClass, String operationName, IRPComment staticAnalysisIssue) 
	{
		
		
		List<IRPOperation> ops = aClass.getOperations().toList();
		
		boolean foundOperation = false;
		
		for(IRPOperation op:ops)
		{
			if(op.getName().equals(operationName))
			{
				staticAnalysisIssue.addAnchor(op); 
				foundOperation = true;
			}
		}
		
		if(foundOperation==false)
		{
			List<IRPClass> nestedClasses = aClass.getNestedElementsByMetaClass("Class", 0).toList();
			for(IRPClass c:nestedClasses)
			{
				addAnchor(c, operationName, staticAnalysisIssue);
			}
		}	
	}
}


class ComponentIncludes
{
	
	private static final String IncludeBegin = "-I../../";
	private static final String IncludeEnd = "/DefaultConfig";
	
	private IRPComponent myComponent = null;
	private Set<IRPComponent> myDependencies = null;
	private Set<String> myManualIncludes = null;
	private String usmRoot = null;
	
	
	public ComponentIncludes(IRPComponent aComponent) {
		myComponent = aComponent;
		myDependencies = new HashSet<IRPComponent>();
		myManualIncludes = new HashSet<String>();
		
		//usmRoot = System.getenv("USM_ROOT");
		
		IRPProject p = myComponent.getProject();
		
		IRPUnit u = p.getSaveUnit();
		String ppath = p.getCurrentDirectory();
		
		File pFile = new File(ppath);
		File rootFile = new File(pFile.getParent());
		
		usmRoot = rootFile.getParent();
		
		collectDependencies(myComponent);
		
		
		
	}
	
	private void collectDependencies(IRPComponent aComponent)
	{
		List<IRPDependency> dependencies = aComponent.getDependencies().toList();
		for(IRPDependency dependency : dependencies)
		{
			IRPModelElement m = dependency.getDependsOn();
			if(m instanceof IRPComponent)
			{
				IRPComponent component = (IRPComponent)m;
				
				//add configuration includes
				List<IRPConfiguration> configs = component.getConfigurations().toList();
				IRPConfiguration config = null;
				
				for(IRPConfiguration c:configs)
				{
					if(c.getName().equals(StaticCodeAnalysis.ConfigName))
					{
						config = c;
					}

				}
				
				if(config!=null)
				{
			
					
					String includePath = config.getIncludePath();
					if((includePath!=null)&&(includePath.equals("")==false))
					{
						String[] includes = includePath.split(",");
						for(String include:includes)
						{
							include = include.replace("<usm_root>", usmRoot);
							include = include.replace("\\\\", "/");
							include = include.replace("\\","/");
							
							myManualIncludes.add(include);
							System.out.println("Add include: "+include+"");
						}
						
					}
					
				}
				
				
				if(myDependencies.add(component)==true)
				{
					System.out.println("Add component: " + component.getName());
					collectDependencies(component);
				}
			}
		}
	}
	
	public IRPComponent getComponent()
	{
		return myComponent;
	}
	
	List<String> getIncludes()
	{
		ArrayList<String> includes = new ArrayList<String>();
		
		for(IRPComponent component :  myDependencies)
		{
			includes.add(IncludeBegin + component.getName() +IncludeEnd);		
		}
		
		for(String absoluteInclude : myManualIncludes)
		{
			includes.add("-I"+absoluteInclude);
		}
		
		return includes;
	
	}
		
}

class LizardWrapper
{
	private static final String LizardCmd = "lizard";
	private IRPApplication myApplication = null;
	private Consumer<String> myTraceAction = null;

	public LizardWrapper(IRPApplication aApplication, Consumer<String> aTraceAction)
	{
		myApplication = aApplication;
		myTraceAction = aTraceAction;
	}
	
	private void trace(String aMessage)
	{
		if(myTraceAction==null)
		{
			//no traceaction set...
			return;
		}
		
		aMessage = "Lizard: " + aMessage;
		
		myTraceAction.accept(aMessage);
	}

	List<LizardData> calculateComplexity(IRPClass aClass)
	{
		
		
		
		List<LizardData> data = new ArrayList<LizardData>();
		
		
		//String className = RhapsodyOperation.getNamespace(aClass)+"::"+aClass.getName();
		
		
		
		
		String fileName = aClass.getName()+".cpp";
		
		if (aClass.getOwner() instanceof IRPClass)
		{
			trace("Nested class!");
			
			fileName = aClass.getOwner().getName()+".cpp";
			
		}
		
		if(aClass.isATemplate()==1)
		{
			fileName = aClass.getName()+".h";
			if (aClass.getOwner() instanceof IRPClass)
            {
                trace("Nested class!");
                
                fileName = aClass.getOwner().getName()+".h";
            }
		}
		
		
		
		if(aClass.getIsReactive()==1)
		{
			IRPStatechart stateChart = aClass.getStatechart();
			
			if (stateChart != null)
			{
				
			
				List<IRPState> states = stateChart.getNestedElementsByMetaClass("State", 1).toList();
				List<IRPTransition> transitions = stateChart.getNestedElementsByMetaClass("Transition", 1).toList();
			
				int stateCount = states.size();
				int transitionCount = transitions.size();
			
				// TODO: add statechart complexity...
				trace("Statechart: " + stateChart.getName() + " States: " + stateCount + " Transitions: " + transitionCount);
				//  V(G) = E - N + 2P
				//  E = number of edges ( transitions )
				//  N = number of nodes ( states )
				int vG = transitionCount - stateCount + 2;
			
				trace("Statechart Complexity: " + vG);
			
				LizardData d = new LizardData(stateChart, 0, vG, stateCount, transitionCount, 0);
				data.add(d);
			
			}

		}
		
		
	
		try
		{
			IRPCollection generateCollection = myApplication.getListOfSelectedElements();
			
			myApplication.generateElements(generateCollection);
			
			File workingFolder = RhapsodyHelper.getActiveDefaultPath(aClass);
			
			ProcessBuilder processBuilder = new ProcessBuilder(LizardCmd, fileName, "--csv");
			processBuilder.directory(workingFolder);
			Process process = processBuilder.start();

			InputStream inputStream = process.getInputStream();
			BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream));
			String line;
			while ((line = inputReader.readLine()) != null)
			{
				String[] values = line.split(","); 
				if (values.length >= 6)
				{
					String name = values[7].replace('"', ' ').trim();
					//alle leerzeichen entfernen...
					name = name.replaceAll("\\s+", "");
					
					
					
					//String opName = name.substring(name.indexOf(className)+className.length()+2);
					
					String opName = name.substring(name.lastIndexOf("::")+2);
					
					//trace("Operation: " + opName);
					
					IRPModelElement element = aClass.findNestedElement(opName, "Operation");
					
					if(element==null)
					{
						
						element = aClass.findNestedElement(opName, "TriggeredOperation");
										
					}
					
					
					
					if (element instanceof IRPOperation)
					{
						IRPOperation operation = (IRPOperation) element;
						int ncloc = Integer.parseInt(values[0]);
						int cyclomaticComplexity = Integer.parseInt(values[1]);
						int tokenCount = Integer.parseInt(values[2]);
						int parameterCount = Integer.parseInt(values[3]);
						int lineCount = Integer.parseInt(values[4]);

						LizardData d = new LizardData(operation, ncloc, cyclomaticComplexity, tokenCount,
								parameterCount, lineCount);
						data.add(d);

						trace(d.toString());
						
						// stelle die werte in einem Dialogfenster als Tabelle dar...
	
						
					}

				}
				
				//System.out.println(line);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		
		
		int sumCC = 0;
		int sumNclOC = 0;
		int sumTokenCount = 0;
		int sumLineCount = 0;
		
		for (LizardData d : data)
		{
			if(d.getCyclomaticComplexity()>1)
			{
				sumCC += d.getCyclomaticComplexity();
			}
			sumNclOC += d.getNcloc();
			sumTokenCount += d.getTokenCount();
			sumLineCount += d.getLineCount();
			
		}	
		
		
		LizardData d = new LizardData(aClass, sumNclOC, sumCC, sumTokenCount, 0, sumLineCount);
		data.add(d);
		
		
		//sort data by cyclomatic complexity...
		data.sort((a, b) -> Integer.compare(b.getCyclomaticComplexity(), a.getCyclomaticComplexity()));
				
		
		showDataInTable(data, aClass);
		
		
		return data;
	}
	
	private void showDataInTable(List<LizardData> data, IRPClass aSelected) {
        String[] columnNames = {"Element", "Ncloc", "Cyclomatic Complexity", "Token Count", "Parameter Count", "Line Count"};
        Object[][] tableData = new Object[data.size()][7];
		
        for (int i = 0; i < data.size(); i++) {
            LizardData d = data.get(i);
            //tableData[i][0] = new Object[] { new ImageIcon(d.getOperation().getIconFileName()), d.getName() };
            tableData[i][0] = d.getOperation();
            tableData[i][1] = d.getNcloc();
            tableData[i][2] = d.getCyclomaticComplexity();
            tableData[i][3] = d.getTokenCount();
            tableData[i][4] = d.getParameterCount();
            tableData[i][5] = d.getLineCount();
        }
        
       
        

        JTable table = new JTable(tableData, columnNames);
        table.setDefaultRenderer(Object.class, new CustomTableCellRenderer());
        
        // Set custom header renderer
        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new CustomHeaderRenderer());
        
        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);

        JDialog dialog = new JDialog();
        ImageIcon icon = new ImageIcon(aSelected.getIconFileName());
		dialog.setIconImage(icon.getImage());
        dialog.setTitle("Lizard Data of Class " + aSelected.getName());
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLayout(new BorderLayout());
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.setSize(800, 400);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        
        //when click on a row, the operation should be selected in the model...
		table.getSelectionModel().addListSelectionListener(e ->
		{
			int selectedRow = table.getSelectedRow();
			if (selectedRow >= 0)
			{
				LizardData d = data.get(selectedRow);
				IRPClassifier operation = d.getOperation();
				operation.openFeaturesDialog(0);
			}
		});
        
    }
	
	
	
	private static class CustomTableCellRenderer extends DefaultTableCellRenderer {
	    @Override
	    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	        if (column == 0 && value instanceof IRPClassifier) 
	        {
	            IRPClassifier operation = (IRPClassifier) value;
	            setIcon(new ImageIcon(operation.getIconFileName()));
	            setText(operation.getName());
	            
				if (operation instanceof IRPStatechart)
				{
					c.setFont(c.getFont().deriveFont(Font.ITALIC));
				}
				else
				{
					c.setFont(c.getFont().deriveFont(Font.PLAIN));
				}
	            
	        }
	        else 
	        {
	            setIcon(null);
	            int cyclomaticComplexity = (int) table.getValueAt(row, 2);
	            if (cyclomaticComplexity > 20) {
	                c.setFont(c.getFont().deriveFont(Font.BOLD));
	            } else {
	                c.setFont(c.getFont().deriveFont(Font.PLAIN));
	            }
	        }
	        return c;
	    }
	}
	
	/*
	
	private static class CustomTableCellRenderer extends DefaultTableCellRenderer {
	    @Override
	    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	        if (column == 0 && value instanceof Object[]) {
	            Object[] values = (Object[]) value;
	            setIcon((ImageIcon) values[0]);
	            setText((String) values[1]);
	        } else {
	            setIcon(null);
	            int cyclomaticComplexity = (int) table.getValueAt(row, 2);
	            if (cyclomaticComplexity > 20) {
	                c.setFont(c.getFont().deriveFont(Font.BOLD));
	            } else {
	                c.setFont(c.getFont().deriveFont(Font.PLAIN));
	            }
	        }
	        return c;
	    }
	}
	
	*/

	
	
	private static class CustomHeaderRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            c.setFont(c.getFont().deriveFont(Font.BOLD));
            return c;
        }
    }

	
		
}


class LizardData
{
	
	private int myNcloc = 0;
	private int myCyclomaticComplexity = 0;
	private int myTokenCount = 0;
	private int myParameterCount = 0;
	private IRPClassifier myOperation = null;
	
	private int myLineCount = 0;

	public LizardData(IRPClassifier aOperation, int aNcloc, int aCyclomaticComplexity, int aTokenCount, int aParameterCount,
			 int aLineCount)
	{
		myOperation = aOperation;
		myNcloc = aNcloc;
		myCyclomaticComplexity = aCyclomaticComplexity;
		myTokenCount = aTokenCount;
		myParameterCount = aParameterCount;
		
		myLineCount = aLineCount;
	}

	public String getName()
	{
		return myOperation.getName();
	}
	
	public IRPClassifier getOperation()
    {
        return myOperation;
    }

	public int getNcloc()
	{
		return myNcloc;
	}

	public int getCyclomaticComplexity()
	{
		return myCyclomaticComplexity;
	}

	public int getTokenCount()
	{
		return myTokenCount;
	}

	public int getParameterCount()
	{
		return myParameterCount;
	}


	public int getLineCount()
	{
		return myLineCount;
	}

	public String toString()
	{
		return "Name: " + myOperation.getName() + " Ncloc: " + myNcloc + " CyclomaticComplexity: " + myCyclomaticComplexity
				+ " TokenCount: " + myTokenCount + " ParameterCount: " + myParameterCount +
				 " LineCount: " + myLineCount;
	}
}