package de.schlaich.gunnar.rhapsody.utilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.rhapsody.apps.App;
import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPArgument;
import com.telelogic.rhapsody.core.IRPAttribute;
import com.telelogic.rhapsody.core.IRPCollection;
import com.telelogic.rhapsody.core.IRPComponent;
import com.telelogic.rhapsody.core.IRPConfiguration;
import com.telelogic.rhapsody.core.IRPControlledFile;
import com.telelogic.rhapsody.core.IRPDependency;
import com.telelogic.rhapsody.core.IRPEvent;
import com.telelogic.rhapsody.core.IRPGeneralization;
import com.telelogic.rhapsody.core.IRPHyperLink;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPOperation;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPRelation;
import com.telelogic.rhapsody.core.IRPSearchManager;
import com.telelogic.rhapsody.core.IRPSearchQuery;
import com.telelogic.rhapsody.core.IRPStereotype;
import com.telelogic.rhapsody.core.IRPType;
import com.telelogic.rhapsody.core.IRPUnit;
import com.telelogic.rhapsody.core.RhapsodyAppServer;



public class RhapsodyHelper 
{
	public static void executeApp(App aApp, String aConnectionstring)
	{
		IRPApplication actualApp = null;
		
		if(aConnectionstring!=null)
		{
			try
			{
				actualApp = RhapsodyAppServer.getActiveRhapsodyApplicationByID(aConnectionstring);
			}
			catch(Exception e)
			{
				System.out.println("connectionstring "+ aConnectionstring + " is not an active rhapsody application ");
			}
		}
		else
		{
			System.out.println("no connectionstring set");
		}
		
		if(actualApp==null)
		{
        
			aApp.invokeFromMain();
			return;
		}
		
		IRPModelElement selectedElement = actualApp.getSelectedElement();
		
		
		if(aConnectionstring!=null)
		{
			//actualApp.writeToOutputWindow("log", "ConnectiongString: " + aConnectionstring + "\n");
		}
		else
		{
			actualApp.writeToOutputWindow("log", "ConnectiongString was null\n");
		}
		
		actualApp.writeToOutputWindow("log", "start...\n");
		
		//mainApp.setRhapsody(actualApp);
		//actualApp.executeCommand("RhpLocateinModelAction", null, null);
		//mainApp.invoke(selectedElement);
		aApp.execute(actualApp, selectedElement);
	}
	
	/*
	 * Sets the Component which has scope of aElement active. 
	 * Works only when package and component has the same name. (USM)
	 * 
	 */
	public static boolean setActive(IRPModelElement aSelectedElement, IRPApplication aRhapsody)
	{
		
		IRPModelElement selectedElement = aSelectedElement;
		IRPProject project = aRhapsody.activeProject();
		if(project==null)
		{
			return false;
		}
		while((selectedElement instanceof IRPPackage)==false)
		{
			selectedElement = selectedElement.getOwner();
			if(selectedElement==null)
			{
				return false;
			}
		}
		
		IRPPackage selectedPackage = (IRPPackage)selectedElement;
		
		IRPComponent selectedComponent = null;
		
		while(selectedComponent==null)
		{
			selectedComponent = (IRPComponent)project.findNestedElementRecursive(selectedPackage.getName(), "Component");
			if(selectedComponent==null)
			{
				selectedElement = selectedPackage.getOwner();
				if(selectedElement==null)
				{
					return false;
				}
				if((selectedElement instanceof IRPPackage)==false)
				{
					return false;
				}
				selectedPackage = (IRPPackage)selectedElement;
			}
		}
		
		if(project.getActiveComponent().equals(selectedComponent))
		{
			return true;
		}
		
		project.setActiveComponent(selectedComponent);
		return true;
		
	}
	
	public static void locateActivePackage(IRPApplication rhapsody, IRPModelElement selected)
	{
		IRPComponent component = null;
		
		if((selected!=null)&&(selected instanceof IRPComponent))
		{
			component = (IRPComponent)selected;
		}
		else
		{
			IRPProject project  = rhapsody.activeProject();
			
			if(project==null)
			{
				return;
			}
			
			component = project.getActiveComponent();
		}
		
		if(component==null)
		{
			return;
		}
		
		@SuppressWarnings("unchecked")
		List<IRPModelElement> elements = component.getScopeElements().toList();
		rhapsody.selectModelElements(component.getScopeElements());
		if(elements.isEmpty()==false)
		{
			elements.get(0).locateInBrowser();
		}
		
	}
	
	
	public static void setComponentDependency(IRPApplication rhapsody, IRPModelElement selected)
	{
		if((selected instanceof IRPDependency)==false)
		{
			return;
		}
		
		IRPUnit unit = selected.getSaveUnit();
		String componentName = unit.getName();
		IRPProject project = rhapsody.activeProject();
		
		IRPDependency dependency = (IRPDependency)selected;
		
		IRPModelElement dependsOn = dependency.getDependsOn();
		
		if(dependsOn==null)
		{
			return;
		}
		
		if((dependsOn instanceof IRPPackage) == false)
		{
			return;
		}
		
		IRPUnit dependsOnUnit = dependsOn.getSaveUnit();
		
		String dependsOnName = dependsOnUnit.getName();
		
		System.out.println("Depends: " + dependsOnName);
		
		IRPComponent dependsOnComponent = (IRPComponent)project.findNestedElementRecursive(dependsOnName, "Component");
		
		if(dependsOnComponent==null)
		{
			return;
		}
		
		IRPComponent component = (IRPComponent)project.findNestedElementRecursive(componentName, "Component");
		
		if(component==null)
		{
			return;
		}
		
		
		IRPDependency compDep = component.addDependencyTo(dependsOnComponent);
		
		if(compDep==null)
		{
			return;
		}
		
		compDep.addStereotype("Usage", "Dependency");
		rhapsody.writeToOutputWindow("log", "Added Dependency from " + componentName + "to" + dependsOnName + "\n");
		rhapsody.writeToOutputWindow("log", "Generate and build " + componentName + "\n");
		
		project.setActiveComponent(component);
		rhapsody.generate();
		rhapsody.build();
	}
	
	static enum eVisibility
	{
		ePrivate,
		eProtected,
		ePublic
	}
	
	public static void movePackageToRepository(IRPApplication rhapsody, IRPModelElement selected) {

		rhapsody.writeToOutputWindow("Log", "Move selected Package into the correct Repository folder\n");

		if (selected instanceof IRPPackage == false) {
			rhapsody.writeToOutputWindow("Log", "Selected element is not a Package - exit\n");
			return;
		}

		IRPUnit selectedUnit = selected.getSaveUnit();

		if (selectedUnit.getName().equals(selected.getName()) == false) {
			rhapsody.writeToOutputWindow("Log", "Unit Name is not same as Package name\n");
			return;
		}

		String pathUnitStr = selectedUnit.getCurrentDirectory();

		Path pathUnit = FileSystems.getDefault().getPath(pathUnitStr);

		if (pathUnit.endsWith(selectedUnit.getName()) == false) {
			rhapsody.writeToOutputWindow("Log", "Unit is not in a separate Directory\n");
			return;
		}

		System.out.println("Name saveUnit: " + selectedUnit.getName());

		IRPProject project = rhapsody.activeProject();
		if (project == null) {
			rhapsody.writeToOutputWindow("Log", "No project loaded\n");
			return;
		}

		project.save();

		IRPCollection dependencies = selectedUnit.getDependencies();
		if (dependencies.getCount() > 0) 
		{
			List<IRPDependency> dependenciesList = dependencies.toList();
			for (IRPDependency dependency : dependenciesList) 
			{
				System.out.println("Dependency: " + dependency.getName());
			}

			// rhapsody.writeToOutputWindow("Log", "Could not move Unit. There are
			// Dependencies!");
			// return;
		}

		String currentDirectory = selectedUnit.getCurrentDirectory();
		if (currentDirectory.contains("UniversalSewingMachine_rpy") == true) 
		{
			rhapsody.writeToOutputWindow("Log", "Unit is already part of the repository - exit\n");
			return;
		}

		String fileName = selectedUnit.getFilename();
		String unitName = selectedUnit.getName();

		Path sourceFile = FileSystems.getDefault().getPath(currentDirectory, fileName);
		Path source = sourceFile.getParent();

		Path sourceDesignView = source;

		Path f = sourceDesignView.getFileName();

		while (f.toString().equals("DesignView") == false) 
		{
			sourceDesignView = sourceDesignView.getParent();
			f = sourceDesignView.getFileName();
		}

		Path relative = sourceDesignView.relativize(source);

		Path usmRoot = source.getParent();
		String usmRootFileName = usmRoot.getFileName().toString();

		while (usmRoot != null && (usmRootFileName.equals("GeneratedProjects") == false)) 
		{
			usmRoot = usmRoot.getParent();
			usmRootFileName = usmRoot.getFileName().toString();

		}

		if (usmRoot == null) 
		{
			System.out.println("did not found USMRoot");
			rhapsody.writeToOutputWindow("Log", "did not found USMRoot\n");
			return;
		}

		usmRoot = usmRoot.getParent();

		Path destination = FileSystems.getDefault().getPath(
				usmRoot.toString() + "\\Development\\RhapsodyModel\\UniversalSewingMachine_rpy\\DesignView",
				relative.toString());
		Path destinationFile = FileSystems.getDefault().getPath(destination.toString(), fileName);

		System.out.println("Source: " + source.toString());
		System.out.println("SourceFile: " + sourceFile.toString());
		System.out.println("Destination: " + destination.toString());

		File destinationFolder = new File(destination.toString());
		if (destinationFolder.exists()) 
		{
			System.out.println("Folder already exists - exit");
			rhapsody.writeToOutputWindow("Log", "Folder already exists - exit\n");
			return;
		}

		destinationFolder.mkdir();

		try {
			Files.copy(sourceFile, destinationFile);
		} catch (IOException e) {

			e.printStackTrace();
			rhapsody.writeToOutputWindow("Log", "Copy failed: " + e.getMessage() + "\n");
			return;
		}

		rhapsody.writeToOutputWindow("Log",
				"copied " + sourceFile.toString() + " to \n       " + destinationFile + "\n");

		// remove package from project

		rhapsody.writeToOutputWindow("Log", "Load Package from Repository.... ");

		IRPProject p = selected.getProject();

		if (p == null) {
			return;
		}

		IRPModelElement owner = selectedUnit.getOwner();

		selectedUnit.deleteFromProject();

		rhapsody.addToModelEx(destinationFile.toString(), IRPApplication.AddToModel_Mode.AS_UNIT_WITHOUT_COPY, 1, 0);

		rhapsody.writeToOutputWindow("Log", " ok\n");

		IRPModelElement addedElement = p.findElementByFileName(destination.toString(), fileName);

		System.out.println(addedElement.getName());

		if (addedElement instanceof IRPUnit == false) {
			System.out.println("addedElement is not unit");
			return;
		}

		IRPUnit addedUnit = (IRPUnit) addedElement;

		addedUnit.setOwner(owner);

		rhapsody.writeToOutputWindow("Log",
				"Moved Package " + addedUnit.getName() + " added to " + owner.getName() + "\n");

		List<IRPComponent> components = p.getComponents().toList();

		if (components.size() != 1) {
			rhapsody.writeToOutputWindow("Log", "Not 1 rootComponent\n");
			return;
		}

		for (IRPComponent c : components) {
			IRPComponent newComponent = c.addNestedComponent(unitName);
			newComponent.addStereotype("Library", "Component");
			newComponent.setBuildType("Library");
			newComponent.addScopeElement(addedUnit);
			IRPConfiguration config = newComponent.findConfiguration("DefaultConfig");
			if (config != null) {
				config.setStatechartImplementation("Reuseable");
			}

			p.setActiveComponent(newComponent);
			rhapsody.writeToOutputWindow("Log", "Created new Component " + newComponent.getName() + "\n");

		}
	}
	
	public static void scriptRunner(IRPApplication rhapsody, IRPModelElement selected)
	{
		String myScriptRunnerPath = "J:/Utilities/CSharpTools/Applications/ScriptRunner/bin/Release/ScriptRunner.exe";
		String scriptPath = "";
		if(selected instanceof IRPControlledFile )
		{
			IRPControlledFile controlledFile = (IRPControlledFile)selected;
			IRPStereotype stereoType = controlledFile.getNewTermStereotype();
			
			if(stereoType == null)
			{
				rhapsody.writeToOutputWindow("Log", "has no new Term Stereotype - not a bcl file\n");
				return;
			}
			
			if(stereoType.getName().equals("BCLScript")==false)
			{
				rhapsody.writeToOutputWindow("Log", "wrong stereotype (" + stereoType.getName() + ")  - not a bcl file\n");
				return; 
			}
			
			rhapsody.writeToOutputWindow("Log", "Start scriptrunner with script " + controlledFile.getName()+"\n");
			
			scriptPath = controlledFile.getFullPathFileName();
			


		}
		else if(selected instanceof IRPHyperLink)
		{
			IRPHyperLink hyperLink = (IRPHyperLink)selected;
			IRPStereotype stereoType = hyperLink.getNewTermStereotype();
			if(stereoType==null)
			{
				rhapsody.writeToOutputWindow("Log", "has no new Term Stereotype - not a bcl file\n");
				return;
			}
			if(stereoType.getName().equals("BCLScriptExt")==false)
			{
				rhapsody.writeToOutputWindow("Log", "wrong stereotype (" + stereoType.getName() + ")  - not a bcl file\n");
				return; 
			}
			
			
			//check if hyperlink is an absolute path...
			rhapsody.writeToOutputWindow("Log","Working Directory = " + System.getProperty("user.dir") +"\n");
			rhapsody.writeToOutputWindow("Log","USM_ROOT = " + System.getenv("USM_ROOT") +"\n");
			
			String usm_root = System.getenv("USM_ROOT");
			scriptPath = hyperLink.getURL();
			if(scriptPath.startsWith(usm_root))
			{
				//we have an absolute path...
				rhapsody.writeToOutputWindow("Log","change from = " + scriptPath +"\n");
				scriptPath = "..\\.."+scriptPath.substring(usm_root.length());
				rhapsody.writeToOutputWindow("Log","To = " + scriptPath +"\n");
				if(hyperLink.getSaveUnit().isReadOnly()==0)
				{
					hyperLink.setURL(scriptPath);
				}

			}
	
			rhapsody.writeToOutputWindow("Log", "Start scriptrunner with script " + scriptPath +"\n");
			
			
		}
		else
		{
			return;
		}
		
		
		//call the scriptrunner...
		/*
		 * J:/Utilities/CSharpTools/Applications/ScriptRunner/bin/Release/ScriptRunner.exe script=j:\USM\Development\RhapsodyModel\UniversalSewingMachine_rpy\DesignView\HardwareDevices\HDInterfaces\WIFI\enableWifi.bcl
		 */
		String[] params = new String [2];
		
		params[0] = myScriptRunnerPath;
		params[1] = "script="+scriptPath;
		

		try 
		{
			
			Process p = Runtime.getRuntime().exec(params);
			
		} catch (IOException e) 
		{
			rhapsody.writeToOutputWindow("Log", "Exception: "+e.getMessage()+"\n"); 
		}
		
		
		
	}
	
	
	public static void searchElement(IRPApplication rhapsody, IRPModelElement selected) {
		Map<String,IRPModelElement> scopes = new HashMap<String,IRPModelElement>();
		

		eVisibility visibility = eVisibility.ePublic; 
		boolean getOwner = false;
		
		if(rhapsody==null)
		{
			System.out.println("no rhapsody");
		}
		
		if(selected==null)
		{
			System.out.println("no selected");
		}

		rhapsody.writeToOutputWindow("Log", "Search model element of type " + selected.getMetaClass());
		
		if(selected instanceof IRPPackage)
		{
			rhapsody.writeToOutputWindow("Log", "Search works not with " + selected.getMetaClass());
		}
		
		
		IRPModelElement owner = selected;
		
		if(selected instanceof IRPOperation)
		{
			IRPOperation operation = (IRPOperation)selected;
			if(operation.getVisibility().equals("Private"))
			{
				visibility = eVisibility.ePrivate;
			}
			else if(operation.getVisibility().equals("Protected"))
			{
				visibility = eVisibility.eProtected;
			}
			getOwner = true;
		}
		else if(selected instanceof IRPAttribute)
		{
			IRPAttribute attribute = (IRPAttribute)selected;
			if(attribute.getVisibility().equals("Private"))
			{
				visibility = eVisibility.ePrivate;
			}
			else if(attribute.getVisibility().equals("Protected"))
			{
				visibility = eVisibility.eProtected;
			}
			getOwner = true;		
		}
		else if(selected instanceof IRPRelation)
		{
			IRPRelation relation = (IRPRelation)selected;
			if(relation.getVisibility().equals("Private"))
			{
				visibility = eVisibility.ePrivate;
			}
			else if(relation.getVisibility().equals("Protected"))
			{
				visibility = eVisibility.eProtected;
			}
			getOwner = true;
		}
		
		else if(selected instanceof IRPType)
		{
			getOwner = true;
			scopes.put(selected.getFullPathName(),selected);
		}
		else if(selected instanceof IRPEvent)
		{
			IRPModelElement evowner = selected;;
			while(evowner instanceof IRPPackage == false)
			{
				evowner = evowner.getOwner();
				if(evowner==null)
				{
					break;
				}
			}
			if(evowner!=null)
			{
				IRPPackage evpack = (IRPPackage)evowner;
				List<IRPModelElement> elements = evpack.getNestedElementsByMetaClass("Class", 1).toList();
				for(IRPModelElement e:elements)
				{
					scopes.put(e.getFullPathName(), e);
				}
			}
			
			
			
		}
		
		if(getOwner==true)
		{
			owner = selected.getOwner();
		}
		
		
		scopes.put(owner.getFullPathName(),owner);
			
		List<IRPModelElement> references = owner.getReferences().toList();
		for(IRPModelElement reference:references)
		{
			System.out.println(reference.getMetaClass()+" "+reference.getName()+" "+reference.toString());
			
			if(reference instanceof IRPRelation)
			{
				if(visibility!=eVisibility.ePublic)
				{
					continue;
				}
				IRPRelation relation = (IRPRelation)reference;
				scopes.put(relation.getFullPathName(),relation);
				IRPModelElement ofClass = relation.getOfClass();
				scopes.put(ofClass.getFullPathName(),ofClass);
				
				
			}
			else if(reference instanceof IRPGeneralization)
			{
				if(visibility==eVisibility.ePrivate)
				{
					continue;
				}
				IRPGeneralization generalization = (IRPGeneralization)reference;
				IRPModelElement derivedClass = generalization.getDerivedClass();
				scopes.put(derivedClass.getFullPathName(),derivedClass);
				getGeneralization(scopes, derivedClass);
			}
			else if(reference instanceof IRPDependency)
			{
				if(visibility!=eVisibility.ePublic)
				{
					continue;
				}
				IRPDependency dependency = (IRPDependency)reference;
				scopes.put(dependency.getFullPathName(), dependency);
				IRPModelElement dependent = dependency.getDependent();
				scopes.put(dependent.getFullPathName(), dependent);
				if(dependent instanceof IRPPackage)
				{
					continue;
				}
				scopes.put(dependent.getFullPathName(),dependent);
			}
			else if(reference instanceof IRPOperation)
			{
				if(visibility!=eVisibility.ePublic)
				{
					continue;
				}
				IRPModelElement operationOwner = reference.getOwner();
				scopes.put(operationOwner.getFullPathName(),operationOwner);
			}
			else if(reference instanceof IRPAttribute)
			{
				if(visibility!=eVisibility.ePublic)
				{
					continue;
				}
				IRPModelElement attributeOwner = reference.getOwner();
				scopes.put(attributeOwner.getFullPathName(),attributeOwner);
			}
			else if(reference instanceof IRPArgument)
			{
				if(visibility!=eVisibility.ePublic)
				{
					continue;
				}
				IRPArgument argument = (IRPArgument)reference;
				IRPModelElement op = argument.getOwner();
				scopes.put(op.getFullPathName(),op);
			}
			else
			{
				scopes.put(reference.getFullPathName(), reference);
			}
		}
		
		
		
		IRPSearchManager mgr = rhapsody.getSearchManager();
		IRPSearchQuery query = mgr.createSearchQuery();
		
		
		Set<Map.Entry<String, IRPModelElement>> set = scopes.entrySet();
		
		for (Map.Entry<String, IRPModelElement> pair : set)
		{
		     System.out.println("Scope: " + pair.getValue().getMetaClass()+" "+pair.getValue().getName()+" Path: "+pair.getKey());
		     query.addSearchScope(pair.getValue());
		}
		
		// set fields to search...
		
		query.addFilterSearchInField(IRPSearchQuery.SearchInField.NAME);
		query.addFilterSearchInField(IRPSearchQuery.SearchInField.OPERATION_BODIES);
		query.addFilterSearchInField(IRPSearchQuery.SearchInField.INITIAL_VALUE);
		query.addFilterSearchInField(IRPSearchQuery.SearchInField.ENUMERATION_LITERAL_VALUE);
		query.addFilterSearchInField(IRPSearchQuery.SearchInField.TYPE_DECLARATIONS_AND_REFERENCES);
		query.addFilterSearchInField(IRPSearchQuery.SearchInField.TRANSITION_LABEL);
		
		query.addFilterElementType("Class");
		query.addFilterElementType("Attribute");
		query.addFilterElementType("Variable");
		query.addFilterElementType("Argument");
		query.addFilterElementType("Operation");
		query.addFilterElementType("Relation");
		query.addFilterElementType("Type");
		query.addFilterElementType("Constructor");
		query.addFilterElementType("Destructor");
		query.addFilterElementType("State");
		query.addFilterElementType("Statechart");
		query.addFilterElementType("Dependency");
		query.addFilterElementType("Generalization");
		query.addFilterElementType("Association");
		query.addFilterElementType("AssociationEnd");
		query.addFilterElementType("Event");
		query.addFilterElementType("Transition");
		
		query.setMatchWholeWord(0);
		
		query.setSearchText(selected.getName());
		
		mgr.searchAndShowResults(query);
		
	}

		@SuppressWarnings("unchecked")
		private static void getGeneralization(Map<String,IRPModelElement> aScopes, IRPModelElement aModelElement )
		{
			List<IRPModelElement> references = aModelElement.getReferences().toList();
			for(IRPModelElement reference:references)
			{
				if(reference instanceof IRPGeneralization)
				{
					IRPGeneralization generalization = (IRPGeneralization)reference;
					IRPModelElement derivedClass = generalization.getDerivedClass();
					aScopes.put(derivedClass.getFullPathName(), derivedClass);
					getGeneralization(aScopes, derivedClass);
				
				}
			}
		}
		
		@SuppressWarnings("unchecked")
		public static IRPConfiguration getProjectConfig(IRPProject aProject, String aConfigName)
		{	
			List<IRPComponent> components = aProject.getComponents().toList();
			for(IRPComponent component : components)
			{
				if(component.getBuildType().equals("executable"))
				{
					List<IRPConfiguration> configs =  component.getConfigurations().toList();
					for(IRPConfiguration config:configs)
					{
						if(config.getName().equals(aConfigName))
						{
							
							return config;
							
						}
					}
					break;
				}
			}
			
			return null;
			
		}
		
		@SuppressWarnings("unchecked")
		public static IRPConfiguration getConfiguration(IRPComponent aComponent, String aConfigName)
		{
			IRPConfiguration config = null;
    		
    		
    		List<IRPConfiguration> configs = aComponent.getConfigurations().toList();
    		for(IRPConfiguration c : configs)
    		{
    			if(c.getName().equals(aConfigName))
    			{
    				config = c;
    			}
    		}
    		return config;
		}
		
		public static IRPConfiguration getDefaultConfiguration(IRPComponent aComponent)
		{
			return getConfiguration(aComponent, "DefaultConfig");
		}
		
		public static File getActiveDefaultPath(IRPModelElement aElement)
		{
			IRPProject project = aElement.getProject();
			if(project==null)
			{
				return null;
			}
			IRPComponent component = project.getActiveComponent();
			if(component == null)
			{
				return null;
			}
			
			IRPConfiguration config = getDefaultConfiguration(component);
			
			if(config == null)
			{
				return null;
			}
			
			String path = config.getPath(1);
			
			if(path == null)
			{
				return null;
			}
			
			File ret = new File(path);
			
			return ret;
			
		}
				

}
