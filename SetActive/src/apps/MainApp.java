package apps;

import java.util.List;

import com.ibm.rhapsody.apps.*;
import com.telelogic.rhapsody.core.*;


public class MainApp extends App {
	
	

	
	/*
	* This method is called on invoking an app inside Rhapsody.
	* rhapsody - Instance of an active Rhapsody application 
	* selected - Selected element in Rhapsody
	*/
@SuppressWarnings("unchecked")
public void execute(IRPApplication rhapsody, IRPModelElement selected) {
		
		
		while((selected instanceof IRPPackage)==false)
		{
			selected = selected.getOwner();
			if(selected==null)
			{
				return;
			}
		}
		
		IRPComponent component = null;
		IRPProject project = rhapsody.activeProject();
		
		String category = selected.getMetaClass();
		System.out.println(category);
		
		String packageName = selected.getName();
		
		if(selected instanceof IRPComponent)
		{
			component = (IRPComponent)selected;
			
		}
		else
		{
			if(project==null)
			{
				return;
			}
			
			
			List<IRPComponent> apps = project.getComponents().toList();
			
			
			for(IRPComponent app:apps)
			{

				List<IRPComponent> components = app.getNestedComponents().toList();
				while(component==null)
				{
					for(IRPComponent comp:components)
					{
						if(comp.getName().equals(packageName))
						{
							component = comp;
						}
						
						// much too slow
						/*
						List<IRPModelElement> elements = comp.getScopeElements().toList();	
						for(IRPModelElement element:elements)
						{
							if(selected.equals(element))
							{
								component = comp;
								break;
							}
						}
						*/
	
						if(component!=null)
						{
							break;
						}
						
					}
					selected = selected.getOwner();
					if(selected==null)
					{
						break;
					}
					packageName = selected.getName();
					
				}
				
				if(component!=null)
				{
					break;
				}
			}
			
			
		}
		
		if(component==null)
		{
			return;
		}
		
		if(project==null)
		{
			return;
		}
		
		System.out.println("Set active Component: " + component.getName());
		rhapsody.writeToOutputWindow("Log", "Set active Component: " + component.getName()+System.getProperty("line.separator"));
		
		project.setActiveComponent(component);
		
	
	}	
	
	
	
	
    /*
     *  Debug app by launching it as "Java Application" is Eclipse.
	 *  Note: Select an element in Rhapsody in order to simulate launching app on a selected element in the browser.
     */	
	public static void main(String[] args) {
		
		MainApp app = new MainApp();
		String connectionstring = null;
		
		if (args.length >= 1) 
		{
			connectionstring = args[0];
		}
		executeApp(app, connectionstring);
		
	}	
	
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
			actualApp.writeToOutputWindow("log", "ConnectiongString: " + aConnectionstring + "\n");
		}
		else
		{
			actualApp.writeToOutputWindow("log", "ConnectiongString was null\n");
		}
		
		actualApp.writeToOutputWindow("log", "start...\n");
		
		aApp.execute(actualApp, selectedElement);
	}
}
