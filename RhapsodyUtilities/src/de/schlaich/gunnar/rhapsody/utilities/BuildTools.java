package de.schlaich.gunnar.rhapsody.utilities;

import java.io.File;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPCollection;
import com.telelogic.rhapsody.core.IRPComponent;
import com.telelogic.rhapsody.core.IRPConfiguration;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPUnit;

public class BuildTools {
	
	private IRPApplication myApplication = null;

	public BuildTools(IRPApplication aApplication) {
		myApplication = aApplication;
	}
	
	public boolean buildAll()
	{
		
		List<IRPProject> projects = myApplication.getProjects().toList();
		for(IRPProject project : projects )
		{
			if(buildProject(project)==false)
			{
				return false;
			}
		}
		
		return true;
	}
		
	
	private boolean buildProject(IRPProject aProject)
	{
	
		
		try 
		{
			trace("Start generate files of "+aProject.getName());
			List<IRPComponent> components =  aProject.getComponents().toList();
			for(IRPComponent component : components)
			{
				buildComponent(aProject, component, 0);
			}
			
			trace("generation of "+ aProject.getName()+" finished!");
		} catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
	
	private void trace(String aText)
	{
		myApplication.writeToOutputWindow("log", "BuildTools: "+aText +"\n");
		System.out.println(aText);
	}
	
	
	private boolean buildComponent(IRPProject aProject, IRPComponent aComponent, int aLevel)
	{
		
		//aProject.setActiveComponent(aComponent);
		//HashMap<String,IRPUnit> units = new HashMap<String,IRPUnit>();
		
		
		
		trace("Check for generate: "+aComponent.getName()+"...");
		
		IRPUnit componentUnit = aComponent.getSaveUnit();
		System.out.println("Component Unit Name: "+componentUnit.getFilename()+ " Last Modified: " + componentUnit.getLastModifiedTime());
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd.yyyy::HH:mm:ss");
		
		String filePath = null;
		
		List<IRPConfiguration> configurations = aComponent.getConfigurations().toList();
		for (IRPConfiguration configuration : configurations) {
			filePath = configuration.getDirectory(1, "");
		}
		
		if(filePath==null)
		{
			return false;
		}

		@SuppressWarnings("unchecked")
		List<IRPModelElement> elements = aComponent.getScopeElements().toList();
		
		boolean needsBuild = false;
		
		//check for project file (.cfg)
		
		//Todo: read properties to identify extensions of the Projectfile
		//Property CppCG::Configuration::Environment = ExternalMakeTemplate
		//Property CppCG::ExternalMakeTemplate::MakeExtension = .cfg
		
		
		String makeExtension = ".cfg";
		
		String makeFileName = aComponent.getName()+makeExtension;
		
		File componentFolder  = new File(filePath);
		
		File makeFile = new File(componentFolder,makeFileName);
		
		if(makeFile.exists()==false)
		{
			needsBuild = true;
		}
		else
		{
			//check date and time
			Date dateMakeFile = new Date(makeFile.lastModified());
			
			List<IRPPackage> packages = aComponent.getScopeElementsByCategory("Package").toList();
			for(IRPPackage p : packages)
			{
				IRPUnit unit = p.getSaveUnit();
				String modifiedString = unit.getLastModifiedTime();
				Date dateUnit = null;
				try 
				{
					dateUnit = dateFormat.parse(modifiedString);
				} 
				catch (ParseException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
				
				if(dateUnit.compareTo(dateMakeFile)>0)
				{
					String formattedDate = dateFormat.format(dateMakeFile);

					System.out.println("Source Class: " + makeFile.getName() + " Last Modified: " + formattedDate +
							" Unit: " + unit.getName() + " Last Modified: " + unit.getLastModifiedTime() );
				
					needsBuild = true;
					break;
					
				}
				
			}

		}
		
		
		
		/*
		 * 
		 * check only cfg file (faster?)
		 * 
		for(IRPModelElement element : elements)
		{
			
			if(element instanceof IRPClass == false)
			{
				continue;
			}
			
			if(element.getIsExternal()==1)
			{
				continue;
			}
			
			
			
			IRPUnit elementUnit = element.getSaveUnit();
			
			
			String elementPath = filePath+"\\"+element.getName()+".cpp";
			File file = new File(elementPath);
			if(file.exists())
			{
				Date dateSource = new Date(file.lastModified());
				Date dateUnit;
				
				
				try 
				{
					String modifiedString = elementUnit.getLastModifiedTime();
					dateUnit = dateFormat.parse(modifiedString);
					
					if(dateUnit.compareTo(dateSource)>0)
					{
					     
						String formattedDate = dateFormat.format(dateSource);

						System.out.println("Source Class: " + file.getName() + " Last Modified: " + formattedDate +
								" Unit: " + elementUnit.getName() + " Last Modified: " + elementUnit.getLastModifiedTime() );
					
						needsBuild = true;
						break;
					}
	
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			
			}
			else
			{
				System.out.println(element.getName() + " No source");
			}
			
			
		}
		
		*/
		
		if(needsBuild == true)
		{
			trace("Generate ...");
			aProject.setActiveComponent(aComponent);
			try {
				myApplication.regenerate();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			if(aLevel>0)
			{
				myApplication.build();
			}
			trace("...ok");
			
		}
		else
		{
			trace( "... is up to date");
		}
		
		
		aLevel++;
		
		List<IRPComponent> components = aComponent.getNestedComponents().toList();
		int nrOfElements = components.size();
		int index = 0;
		for(IRPComponent component : components)
		{
			index++;
			trace(index+"/"+nrOfElements);
			if(buildComponent(aProject, component, aLevel)==false)
			{
				return false;
			}
		}
		
		return true;
		
	}

}
