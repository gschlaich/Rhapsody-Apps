package de.schlaich.gunnar.rhapsody.utilities;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.cdt.core.IProcessList;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPComment;
import com.telelogic.rhapsody.core.IRPComponent;
import com.telelogic.rhapsody.core.IRPConfiguration;
import com.telelogic.rhapsody.core.IRPDependency;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPOperation;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPUnit;

public class StaticCodeAnalysis {
	

	private IRPApplication myApplication = null;
	
	private static final String EchoOff = "@echo off\n";
	private static final String ClangCmd = "clang-tidy";
	private static final String CppCheckCmd = "cppcheck";
	private static final String CppCheckPlatform = "--platform=win32A";
	private static final String PrecompiledInclude = "-I../../../../../Development/ExternalSource/PrecompiledHeader";
	private static final String OxfInclude = "/LangCpp";
	private static final String OmThreadInclude = "-I../../../../../Development/ExternalSource/oxf/oxf";
	private static final String OsConfigInclude = "/LangCpp/osconfig/WIN32";
	
	private static final String FlawfinderPath = "J:/Utilities/flawfinder-2.0.19"; //TODO: correct path...
	private static final String FlawfinderCmd = "flawfinder.py";
	private static final String FlawFinderCSV = "--csv";
	
	public static final String ConfigName = "DefaultConfig";
	
	
	
	private Map<IRPComponent, ComponentIncludes> myComponents = null;

	private static StaticCodeAnalysis myStaticCodeAnalysis = null;

	public StaticCodeAnalysis(IRPApplication aApp) {
		myApplication = aApp;
		myComponents = new HashMap<IRPComponent,ComponentIncludes>();
	}
	
	public static StaticCodeAnalysis get(IRPApplication aApp)
	{
		if(myStaticCodeAnalysis==null)
		{
			myStaticCodeAnalysis = new StaticCodeAnalysis(aApp);
		}
		
		return myStaticCodeAnalysis;
	}
	
	
	public void trace(String aString)
	{
		System.out.println(aString);
		
		if(myApplication!=null)
		{
			myApplication.writeToOutputWindow("Log", "StaticCodeAnalysis: " + aString+"\n");
		}		
	}
	
	public static String Analyze(IRPModelElement aSelected, IRPApplication aApplication)
	{
		
		
		if(aSelected==null)
		{
			return "failed";
		}
		
		IRPProject project = aSelected.getProject();
		StaticCodeAnalysis sca = get(aApplication);
		
		if(aSelected instanceof IRPClass)
		{
			
			IRPClass selectedClass = (IRPClass) aSelected; 

			sca.clang(selectedClass, project);
			sca.cppTest(selectedClass, project);
			return "ok";
		}
		else if(aSelected instanceof IRPPackage)
		{
			
			Analyze((IRPPackage)aSelected, sca);
			return "ok";
		}
		
		return "failed";	
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
		
		String fileName = aClass.getName()+".cpp";
		
		if(aClass.isATemplate()==1)
		{
			fileName = aClass.getName()+".h";
		}
		
		
		
		/*
		boolean isActive = false;
		
		List<IRPClass> classes = activeComponent.getScopeElementsByCategory("Class").toList();
		for(IRPClass c : classes)
		{
			if(c.equals(aClass))
			{
				isActive = true;
				break;
			}
		}
		
		if(isActive==false)
		{
			return null;
		}
		
		*/
		
		
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
			
			params.add(fileName);
			params.add("--");
			params.add(PrecompiledInclude);
			params.add("-I"+omRoot+OxfInclude);
			params.add("-I../../../../../Development/ExternalSource/oxf");
			params.add(OmThreadInclude);
			params.add("-I"+omRoot+OsConfigInclude);
			params.addAll(componentIncludes.getIncludes());
			
			System.out.println("Workingfolder: "+workingFolder);
			
			 ProcessBuilder processBuilder = new ProcessBuilder(ClangCmd);
			 
	         processBuilder.directory(workingFolder);
	         processBuilder.command().addAll(params);
			
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
		            
		            int offset = (ASTHelper.getOffset(operationDefinition, lineNumber)-1);
		            
		            createIssue(aClass, errorLevel, infoText, operationName, offset, positionInLine);
		            
		           
		            trace("Clang: ---------------------------------------");
		            trace("File: " + fName);
		            trace("Line: " + lineNumber);
		            trace("Operation: "+ operationName);			
					trace("Offset: "+ offset);
		            trace("Column: " + positionInLine);
		            trace("Errorlevel: " + errorLevel);
		            trace("Infotext: " + infoText);
		            trace("----------------------------------------------");
		            
		            
		           
		            
		            
		            
			    }

				
			}

			
		}
					
			
			/*

            // Batchdatei als Prozess ausführen
           
           
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
		
			trace(CppCheckCmd +" " + CppCheckPlatform + " " + fileName);
			
			ProcessBuilder processBuilder = new ProcessBuilder(CppCheckCmd, CppCheckPlatform, fileName);
			 
	        processBuilder.directory(workingFolder.getAbsoluteFile());
	   
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
			
			
	        String patternString = "(.*):(\\d+):(\\d+): (error|warning): (.*)";

	        
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
			
			trace(FlawfinderCmd +" " + FlawFinderCSV + " " + fileName);
			
			
			
			
			ProcessBuilder processBuilder = new ProcessBuilder(FlawfinderCmd, FlawFinderCSV, workingFile.getAbsolutePath());
			 
	        processBuilder.directory(new File(FlawfinderPath));
	   
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
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
			
		
		
		
		
		
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public void createIssue(IRPClass aClass, String errorLevel, String infoText, String operationName, int offset, int column) 
	{
		if(operationName==null)
		{
			return;
		}
		
		IRPUnit unit = aClass.getSaveUnit();
		if(unit==null)
		{
			return;
		}
		
		if(unit.isReadOnly()==1)
		{
			return;
		}
			
		if(errorLevel.contains("note"))
		{
			return;
		}
			
		        
		String issueName = errorLevel+"_"+operationName+"_"+offset;
		       
        List<IRPComment> issues = aClass.getNestedElementsByMetaClass("Comment", 0).toList();
        for(IRPComment issue:issues)
        {
        	if(issue.getName().equals(issueName))
        	{
        		return;
        	}
        }
		                
        IRPComment staticAnalysisIssue = (IRPComment) aClass.addNewAggr("CodeAnalysisIssue", issueName);
        
        staticAnalysisIssue.setDescription(infoText);
        staticAnalysisIssue.setBody(errorLevel);
        staticAnalysisIssue.setSpecification(operationName+" "+offset+" "+column);
        
        if(staticAnalysisIssue!=null)
        {
            //get the operation...
            List<IRPOperation> ops = aClass.getOperations().toList();
            for(IRPOperation op:ops)
            {
            	if(op.getName().equals(operationName))
            	{
            		staticAnalysisIssue.addAnchor(op);            		
            	}
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