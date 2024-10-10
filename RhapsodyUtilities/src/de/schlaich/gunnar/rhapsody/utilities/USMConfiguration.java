package de.schlaich.gunnar.rhapsody.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.telelogic.rhapsody.core.HYPNameType;
import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPComponent;
import com.telelogic.rhapsody.core.IRPHyperLink;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPTableView;

public class USMConfiguration
{

	private IRPApplication myApplication = null;
	private Consumer<String> myTraceAction = null;
	List<CClassClass> myClassList = null;
	List<CClassClass> myParamSetList = null;
	private Path myDataPath = null;

	Map<String, IRPComponent> myComponentMap = null;

	public USMConfiguration(IRPApplication aApplication, Consumer<String> aTraceAction)
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

		aMessage = "USMConfig: " + aMessage;

		myTraceAction.accept(aMessage);
	}

	public void loadConfiguration(IRPProject aProject)
	{
		File classCatalog = loadClassCatalog(aProject);
		if (classCatalog == null)
		{
			trace("ClassCatalog file not found!");
			return;
		}

		parseClassCatalog(classCatalog);

		File dataFileList = loadDataFile(aProject);
		if (dataFileList != null)
		{
			parseFileList(dataFileList);
		}

		loadComponents(aProject);

//		if(removeHyperlinks()==true)
//		{
//			return;
//		}

		for (CClassClass cClass : myParamSetList)
		{

			if (cClass.getName().contains("USM_PFT_"))
			{
				continue;
			}

			IRPComponent component = myComponentMap.get("PARI");
			if (myComponentMap.containsKey(cClass.getName()))
			{
				component = myComponentMap.get(cClass.getName());
				trace("Set for Component found: " + component.getName());

			}
			if (component == null)
			{
				continue;
			}

			boolean addLink = true;
			List<IRPHyperLink> links = component.getHyperLinks().toList();
			for (IRPHyperLink link : links)
			{
				if (link.getName().equals(cClass.getName()))
				{
					addLink = false;
				}
			}

			if (addLink == false)
			{
				continue;
			}

			int objectId = getObjectId(cClass.getPath().toFile());

			trace("Paramset: " + cClass.getName() + " ObjectId: " + objectId);

			IRPHyperLink link = (IRPHyperLink) component.addNewAggr("ParameterSet", cClass.getName());
			link.setURL("https://clickonce.bernina.com/UsmParameters/Parameterset/Details/" + objectId);
			link.setDisplayOption(HYPNameType.RP_HYP_FREETEXT, cClass.getName());

		}

		// check if any part of the path is a component name

		List<CClassClass> notAdded = new ArrayList<>();

		for (CClassClass cClass : myClassList)
		{
			boolean addedLink = false;
			String[] pathParts = cClass.getPath().toString().split("\\\\");
			for (String part : pathParts)
			{
				if (myComponentMap.containsKey(part))
				{
					addedLink = true;
					IRPComponent component = myComponentMap.get(part);
					trace("Component found: " + component.getName() + ": " + cClass.getName());

					List<IRPHyperLink> linkd = component.getHyperLinks().toList();
					boolean addLink = true;

					String fileName = cClass.getPath().getFileName().toString();

					for (IRPHyperLink link : linkd)
					{
						if (link.getName().equals(fileName))
						{
							trace("Link already exists: " + link.getName());
							addLink = false;

						}
					}

					if (addLink == true)
					{

						IRPHyperLink link = (IRPHyperLink) component.addNewAggr("USMDataItem", fileName);
						link.setURL(cClass.getPath().toString());
						link.setName(fileName);
						link.setDisplayOption(HYPNameType.RP_HYP_FREETEXT, fileName);
					}

				}
			}
			if (addedLink == false)
			{
				notAdded.add(cClass);
			}
		}

		for (CClassClass cClass : notAdded)
		{
			boolean addedLink = false;
			String[] pathParts = cClass.getPath().toString().split("\\\\");
			for (String part : pathParts)
			{
				for (IRPComponent component : myComponentMap.values())
				{
					if (part.contains(component.getName()))
					{
						addedLink = true;
						trace("Component found: " + component.getName() + ": " + cClass.getName());

						List<IRPHyperLink> linkd = component.getHyperLinks().toList();
						boolean addLink = true;

						String fileName = cClass.getPath().getFileName().toString();

						for (IRPHyperLink link : linkd)
						{
							if (link.getName().equals(fileName))
							{
								trace("Link already exists: " + link.getName());
								addLink = false;

							}
						}

						if (addLink == true)
						{

							IRPHyperLink link = (IRPHyperLink) component.addNewAggr("USMDataItem", fileName);
							link.setURL(cClass.getPath().toString());
							link.setName(fileName);
							link.setDisplayOption(HYPNameType.RP_HYP_FREETEXT, fileName);
						}

					}
				}

			}
			if (addedLink == false)
			{
				trace("----No component found for: " + cClass.getPath().toString());

			}

		}

		// show table
		IRPTableView usmDataView = null;

		List<IRPTableView> tableViews = aProject.getNestedElementsByMetaClass("TableView", 1).toList();
		for (IRPTableView tableView : tableViews)
		{
			if (tableView.getName().equals("USMDataView"))
			{
				usmDataView = tableView;
				break;
			}
		}

		usmDataView.open();

	}

	public void addLibraryLinks(IRPProject aProject)
	{
		if (aProject == null)
		{
			trace("No project found!");
			return;
		}

		loadComponents(aProject);
		
		for(IRPComponent component : myComponentMap.values())
        {
            
			
			
			@SuppressWarnings("unchecked")
			List<IRPPackage> packages = component.getScopeElementsByCategory("Package").toList();
            for(IRPPackage pack:packages)
            {
            	try
            	{
            		String usmLibraries = pack.getPropertyValue("CPP_CG.Package.USMLibraries");
            		if(usmLibraries!=null && usmLibraries.length()>0)
            		{
            			// split the string by comma
            			String[] libraries = usmLibraries.split(",");
            			for(String library : libraries)
            			{
            				
            					// replace "<usm_root>" by the path of the USM root
            					library = library.replace("<usm_root>", RhapsodyHelper.getUSMPath(aProject).toString());
            					// remove leading and trailing spaces
            					library = library.trim();
            					Path path = Paths.get(library);
            					
            					trace("Library: " + path.toString());
            					
            					Path libraryParent = path.getParent();
            					
            					//IRPHyperLink link = (IRPHyperLink) component.addNewAggr("USMLibrary", path.getFileName().toString());
            					//link.setURL(path.toString());
            					//link.setName(path.getFileName().toString());
            					//link.setDisplayOption(HYPNameType.RP_HYP_FREETEXT, path.getFileName().toString());

            			}
            			
            		}
            		
            	}
            	catch(Exception e)
				{
					trace(e.getMessage());
					continue;
				}
            }
        }

	}

	public boolean removeHyperlinks()
	{
		if (myComponentMap == null)
		{
			return false;
		}

		myComponentMap.forEach((key, value) ->
		{
			List<IRPHyperLink> links = value.getHyperLinks().toList();
			for (IRPHyperLink link : links)
			{
				trace("Remove Link: " + link.getName());
				link.deleteFromProject();
			}
		});

		return true;

	}

	private void loadComponents(IRPProject aProject)
	{
		// List<IRPComponent> components = aProject.getComponents().toList();
		List<IRPComponent> components = aProject.getNestedElementsByMetaClass("Component", 1).toList();

		myComponentMap = new HashMap<>();

		for (IRPComponent component : components)
		{
			myComponentMap.put(component.getName(), component);
		}

		IRPComponent hoopComponent = myComponentMap.get("HGD");
		if (hoopComponent != null)
		{
			myComponentMap.put("HoopData", hoopComponent);
		}

		IRPComponent uiComponent = myComponentMap.get("UIM70");
		if (uiComponent != null)
		{
			myComponentMap.put("UserInterfaces", uiComponent);
		}
		else
		{
			uiComponent = myComponentMap.get("UIM43");
			if (uiComponent != null)
			{
				myComponentMap.put("UserInterfaces", uiComponent);
			}
		}

		IRPComponent esp32Component = myComponentMap.get("ESP32");
		if (esp32Component != null)
		{
			myComponentMap.put("esp32", esp32Component);
			myComponentMap.put("Certificates", esp32Component);

		}

	}

	private File loadClassCatalog(IRPProject aProject)
	{

		if (aProject == null)
		{
			trace("No project found!");
			return null;
		}

		try
		{
			List<IRPPackage> packages = aProject.getNestedElementsByMetaClass("Package", 0).toList();
			IRPPackage designView = null;
			for (IRPPackage pack : packages)
			{
				if ("DesignView".equals(pack.getName()))
				{
					designView = pack;
					break;
				}
			}

			// get Version
			String impleStr = designView.getPropertyValue("CG.Class.USMApplicationImplementation");
			String interfaceStr = designView.getPropertyValue("CG.Class.USMApplicationInterface");
			String ApplIteration = designView.getPropertyValue("CG.Class.USMApplicationIteration");

			long implementation = Long.parseLong(impleStr);
			long interfaceId = Long.parseLong(interfaceStr);
			long iteration = Long.parseLong(ApplIteration);

			Path usmPath = RhapsodyHelper.getUSMPath(aProject);

			// Bernina790Pro_46.00.00_1.xml

			String classCatalogFileName = String.format("%s_%02d.%02d.%02d_1.xml", aProject.getName(), iteration,
					interfaceId, implementation);

			trace("Catalog file: " + classCatalogFileName);

			// Data folder: J:\USM46\Development\RhapsodyModel\Data
			Path relativePath = Paths.get("Development", "RhapsodyModel", "Data");

			myDataPath = usmPath.resolve(relativePath);

			File classCatalogFile = myDataPath.resolve(classCatalogFileName).toFile();

			if (classCatalogFile.exists() == false)
			{
				trace("ClassCatalog file not found: " + classCatalogFile);
				return null;
			}

			trace("ClassCatalog file found: " + classCatalogFile);

			return classCatalogFile;

		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			trace(e.getMessage());
			return null;
		}

	}

	private File loadDataFile(IRPProject aProject)
	{

		if (aProject == null)
		{
			trace("No project found!");
			return null;
		}

		try
		{
			List<IRPPackage> packages = aProject.getNestedElementsByMetaClass("Package", 0).toList();
			IRPPackage designView = null;
			for (IRPPackage pack : packages)
			{
				if ("DesignView".equals(pack.getName()))
				{
					designView = pack;
					break;
				}
			}

			// get Version
			String impleStr = designView.getPropertyValue("CG.Class.USMApplicationImplementation");
			String interfaceStr = designView.getPropertyValue("CG.Class.USMApplicationInterface");
			String ApplIteration = designView.getPropertyValue("CG.Class.USMApplicationIteration");

			long implementation = Long.parseLong(impleStr);
			long interfaceId = Long.parseLong(interfaceStr);
			long iteration = Long.parseLong(ApplIteration);

			Path usmPath = RhapsodyHelper.getUSMPath(aProject);

			// Bernina790Pro_46.00.00_1.xml

			String listFileName = String.format("%s_%02d.%02d.%02d_dataFiles.txt", aProject.getName(), iteration,
					interfaceId, implementation);

			trace("Data list file: " + listFileName);

			// Data folder: J:\USM46\Development\RhapsodyModel\Data
			Path relativePath = Paths.get("Development", "RhapsodyModel", "Data");

			myDataPath = usmPath.resolve(relativePath);

			File dataListFile = myDataPath.resolve(listFileName).toFile();

			if (dataListFile.exists() == false)
			{
				trace("DataList file not found: " + dataListFile);
				return null;
			}

			trace("DataList file found: " + dataListFile);

			return dataListFile;

		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			trace(e.getMessage());
			return null;
		}

	}

	private void parseClassCatalog(File aClassCatalogFile)
	{
		try
		{

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();

			// Parse die XML-Datei in ein DOM-Dokument
			Document doc = builder.parse(aClassCatalogFile);
			doc.getDocumentElement().normalize();

			// Liste zum Speichern der CClassClass-Objekte
			myClassList = new ArrayList<>();
			myParamSetList = new ArrayList<>();

			// Finde alle Element-Tags mit dem Typ CClassClass
			NodeList nodeList = doc.getElementsByTagName("Element");

			for (int i = 0; i < nodeList.getLength(); i++)
			{
				Node node = nodeList.item(i);

				if (node.getNodeType() == Node.ELEMENT_NODE)
				{
					Element element = (Element) node;

					// Nur Elemente mit dem TYPE "CClassClass" verarbeiten
					if ("CClassClass".equals(element.getAttribute("TYPE")))
					{
						int clsId = Integer.parseInt(element.getElementsByTagName("ClsId").item(0).getTextContent());
						String name = element.getElementsByTagName("Name").item(0).getTextContent().replace("\"", "");
						String archiveName = element.getElementsByTagName("ArchiveName").item(0).getTextContent()
								.replace("\"", "");
						String importFileName = element.getElementsByTagName("ImportFileName").item(0).getTextContent()
								.replace("\"", "").replace("\\\\", "\\");
						String importFileExt = element.getElementsByTagName("ImportFileExt").item(0).getTextContent()
								.replace("\"", "");
						int nextObjId = Integer
								.parseInt(element.getElementsByTagName("nextObjIds").item(0).getTextContent());
						boolean storeCompressed = Integer.parseInt(
								element.getElementsByTagName("StoreCompressed").item(0).getTextContent()) == 1;

						if (archiveName.equals("AnyData.oba"))
						{

							String pathStr = importFileName + "1" + importFileExt;
							pathStr = pathStr.replace("\\", File.separator);
							pathStr = pathStr.substring(1);

							Path path = myDataPath.resolve(pathStr);
							// trace(" File: " + path.toString());

							if (path.toFile().exists() == false)
							{
								trace("File not found: " + path);
								continue;
							}

							CClassClass cClass = new CClassClass(clsId, name, archiveName, path, nextObjId,
									storeCompressed);

							if (path.toString().contains("ParameterSetDefinitions") == true)
							{

								myParamSetList.add(cClass);
							}
							else
							{
								myClassList.add(cClass);
							}
						}
					}
				}
			}
			/*
			 * // Ausgabe der erstellten Objekte for (CClassClass cClass : myClassList) {
			 * System.out.println(cClass); }
			 */

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void parseFileList(File aDataFileList)
	{

		try
		{
			BufferedReader br = new BufferedReader(new FileReader(aDataFileList));
			String line;
			while ((line = br.readLine()) != null)
			{
				String[] parts = line.split(" ", 2);
				if (parts.length == 2)
				{
					String action = parts[0];
					String path = parts[1];

					File file = new File(path);
					if (file.exists() == false)
					{
						trace("File not found: " + path);
						continue;
					}

					CClassClass cClass = new CClassClass(-1, file.getName(), "", Paths.get(path), -1, false);

					if (path.contains("ParameterSetDefinitions") == true)
					{

						continue;
					}

					for (CClassClass c : myClassList)
					{
						File fc = c.getPath().toFile();
						if (fc.equals(file))
						{
							continue;
						}

						trace("Add: " + file.getName());

						myClassList.add(cClass);

						break;

					}

				}
			}
		}
		catch (IOException e)
		{
			trace(e.getMessage());
		}

	}

	private int getObjectId(File aParamSetFile)
	{
		int objectId = -1;

		try
		{
			// Erstelle einen DocumentBuilder
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();

			// Parse die XML-Datei in ein DOM-Dokument
			Document doc = builder.parse(aParamSetFile);

			// Erstelle ein XPath-Objekt
			XPathFactory xpathFactory = XPathFactory.newInstance();
			XPath xpath = xpathFactory.newXPath();

			// XPath-Ausdruck, um die ObjectId im ParameterSetRef zu finden
			String expression = "//ParameterSetRef/ObjectId";

			// Führe den XPath-Ausdruck aus und finde die ObjectId
			XPathExpression xpathExpression = xpath.compile(expression);
			NodeList nodes = (NodeList) xpathExpression.evaluate(doc, XPathConstants.NODESET);

			if (nodes.getLength() > 0)
			{
				objectId = Integer.parseInt(nodes.item(0).getTextContent());
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return objectId;

	}

}

class CClassClass
{
	private int myClsId;
	private String myName;
	private Path myPath;
	private String myArchiveName;
	private int myNextObjId;
	private boolean myStoreCompressed;

	public CClassClass(int aClsId, String aName, String aArchiveName, Path aPath, int aNextObjId,
			boolean aStoreCompressed)
	{
		this.myClsId = aClsId;
		this.myName = aName;
		this.myArchiveName = aArchiveName;
		this.myPath = aPath;
		this.myNextObjId = aNextObjId;
		this.myStoreCompressed = aStoreCompressed;

	}

	public Path getPath()
	{
		return myPath;
	}

	public String getName()
	{
		return myName;
	}

	@Override
	public String toString()
	{
		return "CClassClass{" + "clsId=" + myClsId + ", name='" + myName + '\'' + ", archiveName='" + myArchiveName
				+ '\'' + ", Path" + myPath.toString() + '\'' + ", nextObjId=" + myNextObjId + ", storeCompressed="
				+ myStoreCompressed + '}';
	}
}
