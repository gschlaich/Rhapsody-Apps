package de.schlaich.gunnar.rhapsody.utilities.generateInitCode;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPComponent;
import com.telelogic.rhapsody.core.IRPConfiguration;
import com.telelogic.rhapsody.core.IRPDependency;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPProject;

import de.schlaich.gunnar.rhapsody.utilities.RhapsodyHelper;
import de.schlaich.gunnar.rhapsody.utilities.RhapsodyOperation;



public class CodeGenerator {

	private ArrayList<IRPComponent> myComponents;
	private IRPComponent myRootComponent;
	private Consumer<String> myTraceAction;
	
	private static final String usmInitProperty = "CPP_CG.Class.USMInitializationCode";
	private static final String usmIsGeneratedByClassFactory = "CPP_CG.Class.USMIsGeneratedByClassFactory";
	private static final String usmIsSubSystemMgr = "CPP_CG.Class.USMIsSubSystemMGR";
	private static final String usmCleanUpCode = "CPP_CG.Class.USMCleanupCode";
	
	public CodeGenerator(Consumer<String> aTraceAction) {
		myTraceAction = aTraceAction;
	}
	
	private void trace(String aMessage)
	{
		myTraceAction.accept(aMessage);
	}

	public String updateEntry(IRPClass aClass)
	{
		
		String initCodeStr = initCode(aClass);
		trace("New init code: \n "+initCodeStr);
		
		return setNewInitCode(aClass, initCodeStr);
	
	}

	private String setNewInitCode(IRPClass aClass, String aContent) {
		IRPProject pr = aClass.getProject();
		String initCode = getInitCode(pr);
		
		String[] lines = initCode.split("\n");
		
		String keyComment = "// "+aClass.getName();
		String startSubSystem = "// sub system managers";
		String endComment = "//";
		
		
		StringBuilder builder = new StringBuilder();
		
		boolean foundKeyComment = false;
        boolean collecting = false;
        int lastCommentLineIndex = -1;
        
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line.trim().equals(keyComment)) {
                foundKeyComment = true;
                collecting = true;
                builder.append(line).append("\n");
                builder.append(aContent); 
                continue;
            }
            if (collecting && line.contains(endComment)) {
                collecting = false;
                builder.append(line).append("\n"); 
                continue;
            }
            if (!collecting) {
                builder.append(line).append("\n");
            }
            if (line.contains(startSubSystem)) {
                lastCommentLineIndex = builder.lastIndexOf("\n");
            }
        }
        
        if (!foundKeyComment && lastCommentLineIndex != -1) {
            
            String pre = builder.substring(0, lastCommentLineIndex - 1);
            String post = builder.substring(lastCommentLineIndex - 1);
            builder = new StringBuilder(pre);
            builder.append(aContent).append("\n");
            builder.append(post);
        }
        
        
        return builder.toString();
	}
	

	
	public void generateSortedList(IRPProject aProject)
	{
		//first sort the list
		int round = 0;
		myComponents = new ArrayList<IRPComponent>();
		
		
		List<IRPComponent>  components = aProject.getComponents().toList();
		
		if(components.size()!=1)
		{
			return;
		}
		
		for(IRPComponent c:components)
		{
			myRootComponent = c;
			components = c.getNestedComponents().toList();
			break;
		}
		
		
		while(myComponents.size()<components.size())
		{
			round++;
			trace("Round " + round);
			for(IRPComponent c:components)
			{
				if(myComponents.contains(c))
				{
					continue;
				}
				
				List<IRPDependency> dependencies =  c.getDependencies().toList();
				if(dependencies.isEmpty()==true)
				{
					trace("Add " + c.getName() + " to List");
					myComponents.add(c);
					continue;
				}
				boolean add = true;
				for(IRPDependency d : dependencies)
				{
					IRPModelElement dOn = d.getDependsOn();
					if(dOn instanceof IRPComponent)
					{
						IRPComponent dc = (IRPComponent)dOn;
						if(myComponents.contains(dc)==false)
						{
							add = false;
							break;
						}
					}
				}
				if(add==true)
				{
					trace("Add " + c.getName() + " to List");
					myComponents.add(c);
				}
			}
		}
		
	}
	
	private String fullName(IRPModelElement e)
	{
		String nameSpace = RhapsodyOperation.getNamespace(e);
		if(nameSpace==null||nameSpace.isEmpty())
		{
			return e.getName();
		}
		return nameSpace+ "::"+e.getName();
	}
	
	
	
	
	public void generateInitcode()
	{
		
		
		StringBuffer initCodeBuffer = new StringBuffer();
		StringBuffer initSubsytemsBuffer = new StringBuffer();
		StringBuffer cleanupCodeBuffer = new StringBuffer();
		
		
		initSubsytemsBuffer.append("//Subsystem Managers:\n\n");
		
		for(IRPComponent c:myComponents)
		{
			List<IRPModelElement> scopes = c.getScopeElements().toList();
			for(IRPModelElement scope : scopes)
			{
				if(scope instanceof IRPPackage)
				{
					IRPPackage p = (IRPPackage)scope;
					trace("Generate initcode for " + p.getName());
					
					List<IRPModelElement> m = p.getNestedElements().toList();
					for(IRPModelElement ne : m )
					{
						if(ne instanceof IRPClass == false)
						{
							continue;
						}
						
						IRPClass cl = (IRPClass) ne;
						
						String cinitCode = initCode(cl);
						if(cinitCode!=null&&cinitCode.isEmpty()==false)
						{
							initCodeBuffer.append("// " + cl.getName());
							initCodeBuffer.append(cinitCode);
						}
						
						initSubSystem(initSubsytemsBuffer, p, cl);
						
						cleanupCode(cleanupCodeBuffer, cl);
						
						
					}
				}
			}
		}
		
		initCodeBuffer.append(initSubsytemsBuffer);
		
		trace(initCodeBuffer.toString());
		trace("end generate initcode");
		trace(cleanupCodeBuffer.toString());
	}

	private void cleanupCode(StringBuffer aStringBuffer, IRPClass aClass) {
		String cleanupCode = aClass.getPropertyValue(usmCleanUpCode);
		if((cleanupCode != null) && (cleanupCode.isEmpty()==false))
		{
			trace("Cleanup code for "+ fullName(aClass));
			aStringBuffer.append("// cleanup "+fullName(aClass)+"\n");
			cleanupCode.replace("$FullName", fullName(aClass));
			aStringBuffer.append(cleanupCode+"\n\n");
		}
	}
	
	private String initCode(IRPClass aClass)
	{
		String ret = "";
		String classFactory = aClass.getPropertyValue(usmIsGeneratedByClassFactory);
		if(classFactory.contains("True"))
		{	
			ret = (fullName(aClass)+"::registerClassCreator();\n\n");	
		}
		
		String code = aClass.getPropertyValue(usmInitProperty);		
		if(code != null && (code.isEmpty()==false))
		{			
			code = code.replace("$FullName", fullName(aClass));
			ret+=(code+"\n\n");	
		}
		return ret;
	}

	private void initSubSystem(StringBuffer aStringBuffer, IRPPackage aPackage, IRPClass aClass) {
		String isSubSystemMgr = aClass.getPropertyValue(usmIsSubSystemMgr);
		if(isSubSystemMgr.contains("True"))
		{
			trace("Generate subSystem for " + aPackage.getName());
			String fullname = fullName(aClass);
			String instanceName = "__"+aClass.getName().toLowerCase();
			aStringBuffer.append("//"+fullname+"\n");
			aStringBuffer.append(fullname+"* "+instanceName+" = new "+fullname+"();\n");
			aStringBuffer.append(instanceName+"->startBehavior();\n");
			aStringBuffer.append(fullname+"::setAndRegister("+instanceName+");\n\n");
			
		}
	}
	
	public String getInitCodeForClass(IRPClass aClass)
	{
		IRPProject p = aClass.getProject();
		String initcode = getInitCode(p);
		
		String startComment = "// "+aClass.getName();
        String endCommentPattern = "//"; 
        StringBuilder content = new StringBuilder();
        boolean startCollecting = false;
        
        // Quellcode in Zeilen aufteilen
        String[] lines = initcode.split("\n");
        
        for (String line : lines) {
            if (line.trim().equals(startComment)) {
                startCollecting = true;
                continue; 
            }
            if (startCollecting && line.trim().startsWith(endCommentPattern)) {
                break; 
            }
            if (startCollecting) {
                content.append(line).append("\n"); // Fügt die Zeile und einen Zeilenumbruch zum StringBuilder hinzu
            }
        }
        
        return content.toString();
	}
	
	private String getInitCode(IRPProject aProject)
	{
		
		IRPConfiguration rootConfig = getRootConfig(aProject);
		
		if(rootConfig==null)
		{
			return null;
		}
		
		return rootConfig.getInitializationCode();

	}
	
	private void setInitCode(IRPProject aProject, String aInitCode)
	{
		IRPConfiguration rootConfig = getRootConfig(aProject);
		if(rootConfig==null)
		{
			return;
		}
		
		rootConfig.setInitializationCode(aInitCode);
	}

	private IRPConfiguration getRootConfig(IRPProject aProject) {
		if(aProject==null)
		{
			return null;
		}
		
		IRPComponent rootComponent = null;
		
		List<IRPComponent> components = aProject.getComponents().toList();
		for(IRPComponent component:components)
		{
			rootComponent = component;
		}
		
		if(rootComponent==null)
		{
			return null;
		}
		
		
		if(rootComponent == null)
		{
			return null;
		}
		
		List<IRPConfiguration> configs = rootComponent.getConfigurations().toList();
		
		IRPConfiguration rootConfig = null;
		
		for(IRPConfiguration config:configs)
		{
			if(config.getName().equals("DefaultConfig"))
			{
				rootConfig = config;
			}
		}
		
		if(rootConfig==null)
		{
			return null;
		}
		return rootConfig;
	}
	
	

}
