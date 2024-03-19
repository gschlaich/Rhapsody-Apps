package de.schlaich.gunnar.rhapsody.utilities.generateInitCode;

import java.util.List;

import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPComponent;
import com.telelogic.rhapsody.core.IRPConfiguration;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPProject;

public class InitCodeGenerator {

	
	
	
	
	public InitCodeGenerator() {
		// TODO Auto-generated constructor stub
	}
	
	
	
	
	public boolean generateInitCode(IRPProject aProject, String aConfigurationName)
	{
		
		//find main component
		
		List<IRPComponent> components =  aProject.getComponents().toList();
		
		IRPComponent mainComponent = null;
		
		for(IRPComponent component : components)
		{
			if(component.getBuildType().equals("Executable"))
			{
				mainComponent =component;
				break;
			}
		}
		
		if(mainComponent==null)
		{
			return false;
		}
		
		IRPConfiguration mainConfiguration = mainComponent.findConfiguration(aConfigurationName);
		
		if(mainConfiguration==null)
		{
			return false;
		}

		return true;
	}
	
	
	
	
	 private boolean isSubsystemMgrClass(IRPClass aClass)
	 {
	     return (aClass.getPropertyValue("CPP_CG.Class.USMIsSubSystemMGR") == "True");
	 }
	 
	 private boolean isSubsystemPackate(IRPPackage aPackage)
	 {
		 return (aPackage.getPropertyValue("CPP_CG.Package.USMGenerateMGRInstance") == "True");
	 }

}
