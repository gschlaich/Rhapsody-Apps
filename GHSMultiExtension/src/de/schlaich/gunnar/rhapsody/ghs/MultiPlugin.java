package de.schlaich.gunnar.rhapsody.ghs;


import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPCollection;
import com.telelogic.rhapsody.core.IRPComment;
import com.telelogic.rhapsody.core.IRPComponent;
import com.telelogic.rhapsody.core.IRPConfiguration;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPOperation;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPProfile;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPUnit;
import com.telelogic.rhapsody.core.RPUserPlugin;
import com.telelogic.rhapsody.core.RhapsodyAppServer;

import de.schlaich.gunnar.rhapsody.utilities.ASTHelper;
import de.schlaich.gunnar.rhapsody.utilities.RhapsodyHelper;
import de.schlaich.gunnar.rhapsody.utilities.RhapsodyOperation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;


public class MultiPlugin extends RPUserPlugin {
	
	public static String PROFILE_NAME = "GHSMultiProfile";
	public static final String VIEW_MULTI_DEBUGGER_CMD = "View in Multi Debugger";
	public static final String VIEW_MULTI_EDITOR_CMD = "View in Multi Editor";
	public static final String OPEN_MULTI_CMD = "Open in GHS Multi";
	public static final String COMPILE_MULTI_CMD = "Compile in GHS Multi";
	private static final String CompilerIssue = "CompilerIssue";
	private String myCmd = "C:\\ghs\\multi_716\\mpythonrun";
	private String myMultiCmd = "C:\\ghs\\multi_716\\multi.exe";
	private String myArgsDebugView = " -s \"dw = winreg.GetDebugger()\" -s \"dw.RunCommands('e {0} ')\"";
	private String myArgDebugView1 = "\"dw = winreg.GetDebugger()\"";
	private String myArgDebugView2Begin = "\"dw.RunCommands('e ";
	private String myArgDebugView2End = "')\"";
	private String myArgEditView1 = "\"editor = GHS_Editor()\"";
	private String myArgEditView2Begin = "\"ew = editor.OpenFile('";
	private String myArgEditView2End = "')\"";
	private String myArgEditView3Begin = "\"ew.MoveCursor(";
	private String myArgEditView3End = ")\"";
	private String myArgCompile1 = "\"pw = winreg.GetProjectManagerWindow()\"";
	private String myArgCompile2Begin = "\"pw.BuildFile('";
	private String myArgCompile2End = "')\"";
	//private String myArgCompile3 = "\"pw.DumpTextFieldValue('tv_status')\"";
	private String myArgCompile3 = "\"w = winreg.GetWindowByName('Build Details')\"";
	private String myArgCompile4 = "\"w.DumpWidget('tv_messages')\"";
	private String myArgBuldAll = "\"pw.BuildProj(False,True)\"";
	
	
	private IRPApplication myRhapsody = null;
	private IRPProfile myProfile = null;

	public MultiPlugin() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void RhpPluginInit(IRPApplication rpyApplication) {
		// TODO Auto-generated method stub
		
		myRhapsody = rpyApplication;
		trace("Start");

	}

	@Override
	public void RhpPluginInvokeItem() {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnMenuItemSelect(String menuItem) {
		
		if(menuItem.equals(VIEW_MULTI_DEBUGGER_CMD))
		{
			viewInDebugger(null);
			return;
		}
		if(menuItem.equals(VIEW_MULTI_EDITOR_CMD))
		{
			viewInEditor(null);
			return;
		}
		if(menuItem.equals(OPEN_MULTI_CMD))
		{
			openGHSProject();
			return;
		}
		if(menuItem.equals(COMPILE_MULTI_CMD))
		{
			compile(null);
			return;
		}
		trace("Unknown Command: \"" + menuItem + "\"");

	}

	@Override
	public void OnTrigger(String trigger) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean RhpPluginCleanup() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void RhpPluginFinalCleanup() {
		// TODO Auto-generated method stub

	}
	
	private void trace(String aMsg)
	{
		myRhapsody.writeToOutputWindow("Log", "MultiPlugin: " + aMsg+"\n");
		System.out.println("MultiPlugin: " + aMsg);
	}
	
	
	
	private IRPOperation getSelectedOperation(IRPOperation aOperation) 
	{
		IRPModelElement selected = aOperation;
		
		if(selected==null)
		{
			selected = myRhapsody.getSelectedElement();
		}
		if(selected instanceof IRPOperation == false)
		{
			trace("No Operation selected");
			return null;
		}
		
		IRPOperation selectedOperation = (IRPOperation)selected;
		return selectedOperation;
	}
	
	private String getPath(IRPOperation aOperation)
	{
		
		IRPModelElement selectedOwner = aOperation.getOwner();
		
		if (selectedOwner instanceof IRPClass == false) 
		{
			trace("Owner is not a class");
			return null;
		}

		IRPClass selectedClass = (IRPClass) selectedOwner;
		String path = ASTHelper.getSourcePath(selectedClass, myRhapsody);
		
		if((aOperation.isATemplate()==1)||(aOperation.getIsInline()==1))
		{
			path = path+".h";
		}
		else
		{
			path = path+".cpp";
		}
		
		path = path.replace('/', '\\');
		
		
		return path;
	}
	
	private String getPath(IRPClass aClass)
	{
		String path = ASTHelper.getSourcePath(aClass, myRhapsody)+".cpp";
		path = path.replace('/', '\\');
		return path;
	}
	
	private String getPath(IRPPackage aPackage)
	{
		String path = ASTHelper.getSourcePath(aPackage, myRhapsody)+".gpj";
		path = path.replace('/', '\\');
		return path;
	}
	
	
	private List<String> runCmd(String aArgs1, String aArgs2, String aArgs3, String aArgs4) 
	{
		
		List<String> ret = new ArrayList<String>();
		if(aArgs3==null)
		{
			trace("run "+myCmd+" "+" -s "+aArgs1+" -s "+aArgs2);
		}
		else if(aArgs4==null)
		{
			trace("run "+myCmd+" "+" -s "+aArgs1+" -s "+aArgs2+ " -s "+ aArgs3);
		}
		else
		{
			trace("run "+myCmd+" "+" -s "+aArgs1+" -s "+aArgs2+ " -s "+ aArgs3+ " -s "+ aArgs4);
		}
	
		try 
		{
			ProcessBuilder pb;
			if(aArgs3==null)
			{
				pb = new ProcessBuilder(myCmd, "-noconsole", "-s", aArgs1, "-s", aArgs2);
			}
			else if(aArgs4==null)
			{
				pb = new ProcessBuilder(myCmd, "-noconsole", "-s", aArgs1, "-s", aArgs2, "-s", aArgs3);
			}
			else
			{
				pb = new ProcessBuilder(myCmd, "-noconsole", "-s", aArgs1, "-s", aArgs2, "-s", aArgs3, "-s", aArgs4);
			}
			Process process = pb.start();
			InputStream inputStream = process.getInputStream();
			//InputStream errorStream = process.getErrorStream();
			
			BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream));
			//BufferedReader errrorReader = new BufferedReader(new InputStreamReader(errorStream));
			
			String inputLine;
			while((inputLine = inputReader.readLine()) != null)
			{
				ret.add(inputLine);
				trace(inputLine);
			}
			
			int exitCode = process.waitFor();
			trace("Exit Code: "+exitCode);
		} 
		catch (IOException | InterruptedException iox) 
		{
			trace("Exception: " + iox.getMessage());
			iox.printStackTrace();
		}
		return ret;
	}
	
	public void viewInDebugger(IRPOperation aOperation)
	{
		trace("Start viewInDebugger");
		String nameSpace = null;
		String component = null;
		IRPModelElement selected = aOperation;
		
		if(selected==null)
		{
			selected = myRhapsody.getSelectedElement();
		}
			
		
		nameSpace = RhapsodyOperation.getNamespace(selected);
		component = selected.getName();
		selected = selected.getOwner();
		
			
		while(selected instanceof IRPClass)
		{
			component = selected.getName()+"::"+component;
			selected = selected.getOwner();
		}
			
		if(nameSpace!=null)
		{
			component = nameSpace+"::"+component;
		}
		
		
		String arg2 = myArgDebugView2Begin+component+myArgDebugView2End;
		
		//build string...
		//String args = MessageFormat.format(myArgs, component);
		
		runCmd(myArgDebugView1,arg2,null,null);
	
		
	}
	
	public void viewInEditor(IRPOperation aOperation)
	{
		trace("Start viewInEditor");
		
		IRPOperation selectedOperation = getSelectedOperation(aOperation);
		
		if(selectedOperation==null)
		{
			return;
		}
		
		String path = getPath(selectedOperation);
		
		if(path == null)
		{
			return;
		}
		
		String args2 = myArgEditView2Begin+path+myArgEditView2End;
		
		int line = ASTHelper.getSourceOffset(selectedOperation, myRhapsody);

		String args3 = myArgEditView3Begin+line+myArgEditView3End;
		
		runCmd(myArgEditView1, args2, args3,null);
		
	}
	
	
	public void compile(IRPModelElement aElement) {

		trace("Generate...");

		IRPCollection generateCollection = myRhapsody.getListOfSelectedElements();
			
			
		trace("Start generate");

		if (aElement == null) {
			aElement = myRhapsody.getSelectedElement();
		}
		else
		{
			generateCollection.addItem(aElement);
		}
		
		myRhapsody.generateElements(generateCollection);
		
		trace("Start compile");
		

		String path = null;

		if (aElement instanceof IRPOperation) 
		{
			
			IRPOperation aOperation = (IRPOperation) aElement;
			IRPOperation selectedOperation = getSelectedOperation(aOperation);

			if (selectedOperation == null) {
				return;
			}

			IRPModelElement owner = selectedOperation.getOwner();
			
			if(owner instanceof IRPClass)
			{
				IRPClass selectedClass = (IRPClass)owner;
				deleteCompilerIssues(selectedClass);
			}
			
			path = getPath(selectedOperation);
			
		} 		
		else if (aElement instanceof IRPClass) 
		{

			IRPClass selectedClass = (IRPClass) aElement;
			path = getPath(selectedClass);
		
			deleteCompilerIssues(selectedClass);
	
			
		} 
		else if (aElement instanceof IRPPackage)
		{
			IRPPackage selectedPackage = (IRPPackage) aElement;
			
			deleteCompilerIssues(selectedPackage);
			
			path = getPath(selectedPackage);
		}
		
		List<String> result = null;
		
		if (aElement instanceof IRPProject)
		{
			IRPProject selectedProject = (IRPProject) aElement;
			result = runCmd(myArgCompile1, myArgBuldAll, myArgCompile3, myArgCompile4);
		}
		else
		{

			if (path == null) {
				return;
			}
	
			String args2 = myArgCompile2Begin + path + myArgCompile2End;
	
			result = runCmd(myArgCompile1, args2, myArgCompile3, myArgCompile4);
		}
		
		if(result!=null)
		{
			// check for error....
			Iterator<String> i =result.iterator();
			
			File workingFolder = RhapsodyHelper.getActiveDefaultPath(aElement);
			
			
			while(i.hasNext())
			{
				String r = i.next();
				
			
				
				if(r.contains("error #")||r.contains("warning #"))
				{
					 
					//String pattern = "\"([^\"]+)\", line (\\d+): error #(\\d+): (.+)";
					//String pattern = "\"([^\"]+)\", line (\\d+): (error|warning) #(\\d+):(.+)?";
					String pattern = "\"([^\"]+)\", line (\\d+): (error|warning) #([^:]+)(.+)?";
				        Pattern re = Pattern.compile(pattern);

				        Matcher matcher = re.matcher(r);
				        if (matcher.find()) 
				        {
				            
				            String filePath = matcher.group(1);
				            int lineNumber = Integer.parseInt(matcher.group(2));
				            String errorLevel = matcher.group(3);
				            String errorCode = matcher.group(4);
				            String errorMessage = matcher.group(5);
				            if(i.hasNext())
				            {
				            	String messageEnd = " "+i.next().trim();
				            	if(errorMessage == null)
				            	{
				            		errorMessage = messageEnd;
				            	}
				            	else
				            	{
				            		errorMessage += messageEnd;
				            	}
				            }
				            
				            File f = new File(filePath);
				            
				            String fileName = f.getName();
				            
				            String className = fileName.substring(0, fileName.lastIndexOf("."));
				            
				            // Ausgabe der Ergebnisse
				            System.out.println("----------------------------------");
				            System.out.println("Class: " + className);
				            System.out.println("File: " + fileName);
				            System.out.println("Line: " + lineNumber);
				            System.out.println("errorLevel: " + errorLevel);
				            System.out.println("Errorcode: " + errorCode);
				            System.out.println("Errormessage: " + errorMessage);
				            System.out.println("----------------------------------");
				            
				            
				            
				            if(aElement instanceof IRPClass)
				            {
				            	IRPClass selectedClass = (IRPClass)aElement;
				            	createIssue(workingFolder, selectedClass, fileName, className, lineNumber, errorLevel,
										errorCode, errorMessage);
				            }
				            else if(aElement instanceof IRPPackage)
				            {
				            	IRPPackage p = (IRPPackage)aElement;
				            	List<IRPClass> classes = p.getNestedElementsByMetaClass("Class", 1).toList();
				            	for(IRPClass c:classes)
				            	{
				            		if(c.getName().equals(className))
				            		{
				            			createIssue(workingFolder, c, fileName, className, lineNumber, errorLevel, errorCode, errorMessage);
				            		}
				            	}
				            }
				            else if(aElement instanceof IRPOperation)
				            {
				            	IRPModelElement o = aElement.getOwner();
				            	if(o instanceof IRPClass)
				            	{
				            		IRPClass c = (IRPClass)o;
				            		
				            		createIssue(workingFolder, c, fileName, className, lineNumber, errorLevel, errorCode, errorMessage);
				            		
				            	}
				            }
   
				       } 
				}
			}
		}
			

	}

	public void createIssue(File aWorkingFolder, IRPClass aClass, String aFileName, String aClassName, int aLineNumber,
			String aErrorLevel, String aErrorCode, String aErrorMessage) {
		
		while(aClass.getOwner() instanceof IRPClass)
		{
			aClass = (IRPClass)aClass.getOwner();
		}

		File cppSource = new File(aWorkingFolder,aFileName);
		if(cppSource.exists())
		{
			IASTTranslationUnit translationUnit =  ASTHelper.getTranslationUnit(cppSource.getAbsolutePath());
			IASTFunctionDefinition operationDefinition = ASTHelper.getFunctionDefinition(aLineNumber, translationUnit);
	        String operationName = ASTHelper.getOperationName(operationDefinition);   
	        int offset = (ASTHelper.getOffset(operationDefinition, aLineNumber)-1);
	        
	        createIssue(aClass, aErrorLevel, aErrorCode+": "+aErrorMessage, operationName, offset); 
	        
	        
		}
			
		
	}

	public void deleteCompilerIssues(IRPPackage aPackage) {
		
		if(aPackage instanceof IRPProject)
		{
			return;
		}
		
		trace("delete CompilerIssue from " + aPackage.getName());
		
		List<IRPClass> classes = aPackage.getNestedElementsByMetaClass("Class", 1).toList();
		
		if(aPackage.isReadOnly()==1)
		{
			return;
		}
		
		for(IRPClass c:classes)
		{
			trace("delete CompilerIssue from " + c.getName());
			deleteCompilerIssues(c);
		}
	}

	public void deleteCompilerIssues(IRPClass aClass) {
			
		
		if(aClass.isReadOnly()==1)
		{
			return;
		}
		
		List<IRPComment> comments = aClass.getNestedElementsByMetaClass("Comment", 0).toList();
		 
		for(IRPComment comment:comments)
		{
			if(comment.getUserDefinedMetaClass().equals(CompilerIssue))
			{
				
				comment.deleteFromProject();
				
			}
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public void createIssue(IRPClass aClass, String errorLevel, String infoText, String operationName, int offset) 
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
		        
		String issueName = errorLevel+"_"+operationName+"_"+offset;
		       
        List<IRPComment> issues = aClass.getNestedElementsByMetaClass("Comment", 0).toList();
        for(IRPComment issue:issues)
        {
        	if(issue.getUserDefinedMetaClass().equals(CompilerIssue))
        	{
        		if(issue.getName().equals(issueName))
        		{
        			return;
        		}
        	}
        }
		                
        IRPComment compilerIssue = (IRPComment) aClass.addNewAggr(CompilerIssue, issueName);
        
        compilerIssue.setDescription(infoText);
        compilerIssue.setBody(errorLevel);
        compilerIssue.setSpecification(operationName+" "+offset);
        
        if(compilerIssue!=null)
        {
            //get the operation...
            boolean foundOperation = addCompilerIssue(aClass, operationName, compilerIssue);
            
            if(foundOperation==false)
            {
            	//check in nested class
            	List<IRPClass> nestedClasses = aClass.getNestedElementsByMetaClass("Class", 1).toList();
            	for(IRPClass nestedClass:nestedClasses)
            	{
            		addCompilerIssue(nestedClass, operationName, compilerIssue);
            	}
            	
            }
            
            
        }
	}

	public boolean addCompilerIssue(IRPClass aClass, String aOperationName, IRPComment aCompilerIssue) {
		boolean foundOperation = false; 
		List<IRPOperation> ops = aClass.getOperations().toList();
		for(IRPOperation op:ops)
		{
			if(op.getName().equals(aOperationName))
			{
				foundOperation = true;
				aCompilerIssue.addAnchor(op);            		
			}
		}
		
		if(foundOperation == false)
		{
			List<IRPClass> nestedClasses = aClass.getNestedElementsByMetaClass("Class", 0).toList();
			for(IRPClass c:nestedClasses)
			{
				foundOperation = addCompilerIssue(c, aOperationName, aCompilerIssue);
				if(foundOperation==true)
				{
					return foundOperation;
				}
			}
			return false;
			
		}
		else
		{
			return foundOperation;
		}
	}
	
	
	private void openGHSProject()
	{
		IRPModelElement selected = myRhapsody.getSelectedElement();
		if(selected instanceof IRPProject == false)
		{
			trace("No Project selected");
			return;
		}
		
		IRPProject project = (IRPProject)selected;
		
		IRPConfiguration config = RhapsodyHelper.getProjectConfig(project, "DefaultConfig"); //which config?
		
		if(config==null)
		{
			trace("ProjectPath of "+project.getName()+" not found");
			return;
		}
		
		String projectEnding = "AppWorkspaceD9.gpj";
		if(config.getBuildSet().equals("Release"))
		{
			projectEnding = "AppWorkspaceR9.gpj";
		}
		
		String projectName = config.getDirectory(1, "")+"/"+project.getName()+projectEnding; //is there a better solution?
		
		File projectFile = new File(projectName);
		
		if(projectFile.exists()==false)
		{
			
			trace("could not find Project file in " + projectName);
			return;
			
		}
		
		//run multi...
		try 
		{
			ProcessBuilder pb  = new ProcessBuilder(myMultiCmd, projectName);
		
			pb.start(); //fire and forget

		} 
		catch (IOException iox) 
		{
			trace("Exception: " + iox.getMessage());
			iox.printStackTrace();
		}
		
		
	}

}
