package schlaich.gunnar.rhapsody_Rest_api;



import com.telelogic.rhapsody.core.*;

import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class App
{
	public static void main(String[] args)
	{
		SpringApplication.run(App.class, args);
	}
}



class RhapsodyElementDTO
{
	public String name;
	public String guid;
	public String type;
	public List<RhapsodyElementDTO> nestedElements;
	public List<String> attributes;
	public List<String> operations;

	public RhapsodyElementDTO(String name, String guid, String type)
	{
		this.name = name;
		this.guid = guid;
		this.type = type;
		this.nestedElements = new ArrayList<>();
		this.attributes = new ArrayList<>();
		this.operations = new ArrayList<>();
	}

	public void addNestedElement(RhapsodyElementDTO child)
	{
		this.nestedElements.add(child);
	}
}

@RestController
@RequestMapping("/rhapsody")
class RhapsodyController
{

	private IRPApplication app = null;
	private IRPProject project = null;

	public RhapsodyController()
	{
		this.app = RhapsodyAppServer.getActiveRhapsodyApplication();
	}

	@PostMapping("/open")
	public String openProject(@RequestParam String path)
	{
		try
		{
			project = app.openProject(path);
			return "Projekt geöffnet: " + project.getName();
		}
		catch (Exception e)
		{
			return "Fehler beim Öffnen des Projekts: " + e.getMessage();
		}
	}

	@GetMapping("/version")
	public String getVersion()
	{
		return app.version();
	}


	@GetMapping("/debug")
	public Map<String, String> test() {
	    Map<String, String> result = new HashMap<>();
	    result.put("status", "ok");
	    return result;
	}
	
	@SuppressWarnings("unchecked")
	@GetMapping("/projects")
	public String listProjects()
	{
		List<IRPProject> projects = app.getProjects().toList();
		String projectsString = "";
		
		for (IRPProject project : projects)
		{
			projectsString += project.getName() + ", ";
		}
		
		return projectsString;
	
	}
	
	@GetMapping("/activeProject")
	public RhapsodyElementDTO getActiveProject()
	{
		try
		{
			if (project == null)
			{
				project = app.activeProject();
			}
			RhapsodyElementDTO dto = new RhapsodyElementDTO(project.getName(), project.getGUID(), project.getMetaClass());
			return dto;
		}
		catch (Exception e)
		{
			return new RhapsodyElementDTO("Fehler", "unbekannt", e.getMessage());
		}
	}

	@GetMapping("/element/{guid}")
	public RhapsodyElementDTO getElementByGuid(@PathVariable String guid)
	{
		try
		{
			IRPModelElement element = project.findElementByGUID(guid);
			if (element == null)
			{
				if (project.getGUID().equals(guid))
				{
					element = project;
				}
				else
                {
				
					return new RhapsodyElementDTO("Nicht gefunden", guid, "unbekannt");
                }
			}
			return buildRecursiveDTO(element);
		}
		catch (Exception e)
		{
			return new RhapsodyElementDTO("Fehler", guid, e.getMessage());
		}
	}

	private RhapsodyElementDTO buildRecursiveDTO(IRPModelElement element)
	{
		RhapsodyElementDTO dto = new RhapsodyElementDTO(element.getName(), element.getGUID(), element.getMetaClass());

		if (element instanceof IRPClassifier)
		{
			IRPClassifier classifier = (IRPClassifier) element;

			List<?> attributes = classifier.getAttributes().toList();
			for (Object attr : attributes)
			{
				if (attr instanceof IRPModelElement)
				{
					dto.attributes.add(((IRPModelElement) attr).getName());
				}
			}

			List<?> operations = classifier.getOperations().toList();
			for (Object op : operations)
			{
				if (op instanceof IRPModelElement)
				{
					dto.operations.add(((IRPModelElement) op).getName());
				}
			}
		}

		List<?> nested = element.getNestedElements().toList();
		for (Object obj : nested)
		{
			if (obj instanceof IRPModelElement)
			{
				dto.addNestedElement(buildRecursiveDTO((IRPModelElement) obj));
			}
		}
		return dto;
	}
}
