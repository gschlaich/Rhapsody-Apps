package de.schlaich.gunnar.rhapsody.utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.github.difflib.algorithm.myers.MyersDiff;
import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPCollection;
import com.telelogic.rhapsody.core.IRPComponent;
import com.telelogic.rhapsody.core.IRPHyperLink;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProfile;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPRequirement;
import com.telelogic.rhapsody.core.IRPSearchManager;
import com.telelogic.rhapsody.core.IRPSearchQuery;
import com.telelogic.rhapsody.core.IRPSelection;
import com.telelogic.rhapsody.core.IRPUnit;

public class SVNTools
{

	public static final String SVNCommand = "svn";
	public static final String SVNCommandInfo = "info";
	public static final String SVNParamShowItem = "--show-item";
	public static final String SVNParamRelativeURL = "relative-url";
	public static final String JiraIssueName = "JiraIssue";
	public static final String JiraProfileName = "JiraProfile";
	public static final String SearchPatternUSM = "/USM-(\\d+)";
	public static final String SearchPatternTitle = "USM-\\d+_(.*?)(?=\\/)";
	public static final String HyperLinkStart = "https://berninaag.atlassian.net/jira/software/c/projects/USM/issues/";

	private Consumer<String> myTraceAction = null;
	private Object added;
	private IRPApplication myApplication = null;

	private String myJiraTitle = null;
	private String myJiraId = null;
	private String myReport = null;
	private String myURL = null;
	private IRPModelElement mySelected = null;
	private IRPUnit mySaveUnit = null;

	public SVNTools(IRPApplication aApplication, Consumer<String> aTraceAction)
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

		aMessage = "SVN: " + aMessage;

		myTraceAction.accept(aMessage);
	}

	public IRPRequirement getActualJiraElement(IRPModelElement aSelected)
	{

		if (aSelected == null)
		{
			return null;
		}

		mySelected = aSelected;

		mySaveUnit = aSelected.getSaveUnit();

		if (mySaveUnit == null)
		{
			return null;
		}

		IRPProject project = aSelected.getProject();

		if (project == null)
		{
			return null;
		}

		String projectpath = mySaveUnit.getCurrentDirectory();
		File usmFile = new File(projectpath);
		if (usmFile.exists() == false)
		{
			return null;
		}

		ProcessBuilder pb = new ProcessBuilder(SVNCommand, SVNCommandInfo, SVNParamShowItem, SVNParamRelativeURL);
		pb.directory(usmFile);

		Process p;
		try
		{
			p = pb.start();
			InputStream inputStream = p.getInputStream();
			BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream));
			String line;
			StringBuilder output = new StringBuilder();
			while ((line = inputReader.readLine()) != null)
			{
				output.append(line);
			}

			myURL = output.toString();

			int exitCode = p.waitFor();
		}
		catch (InterruptedException | IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		Pattern pattern = Pattern.compile(SearchPatternUSM);
		Matcher matcher = pattern.matcher(myURL);

		// Suche nach dem Muster im Eingabestring
		if (matcher.find() == false)
		{
			System.out.println("Pattern not found");
			return null;
		}

		myJiraId = "USM-" + matcher.group(1);

		pattern = Pattern.compile(SearchPatternTitle);
		matcher = pattern.matcher(myURL);
		if (matcher.find() == false)
		{
			System.out.println("Pattern not found");
			return null;
		}

		myJiraTitle = matcher.group(1).replace('_', ' ');

		return getRequirement(project);

	}

	private IRPRequirement getRequirement(IRPProject project)
	{
		List<IRPProfile> profiles = project.getProfiles().toList();

		IRPProfile jiraProfile = null;

		for (IRPProfile profile : profiles)
		{
			if (profile.getName().equals(JiraProfileName))
			{
				jiraProfile = profile;
				break;
			}
		}

		if (jiraProfile == null)
		{
			return null;
		}

		// check if requirement already exists
		List<IRPRequirement> requirements = jiraProfile.getNestedElementsByMetaClass("Requirement", 0).toList();

		for (IRPRequirement j : requirements)
		{
			if (j.getName().equals(myJiraId))
			{
				return j;
			}
		}

		IRPModelElement jiraModelElement = jiraProfile.addNewAggr("JiraIssue", myJiraId);

		if (jiraModelElement == null)
		{
			return null;
		}

		if (jiraModelElement instanceof IRPModelElement == false)
		{
			return null;
		}

		IRPRequirement jiraReq = (IRPRequirement) jiraModelElement;

		jiraReq.setRequirementID(myJiraId);
		jiraReq.setSpecification(myJiraTitle);

		// add Hyperlink to jira
		IRPModelElement hyperlinkElement = jiraReq.addNewAggr("HyperLink", HyperLinkStart + myJiraId);

		if (hyperlinkElement != null)
		{

			if (hyperlinkElement instanceof IRPHyperLink)
			{
				IRPHyperLink hyperLink = (IRPHyperLink) hyperlinkElement;
				hyperLink.setURL(HyperLinkStart + myJiraId);
			}
		}

		return jiraReq;
	}

	public IRPRequirement setActualJiraIssue(IRPModelElement aSelected)
	{

		IRPRequirement jiraReq = getActualJiraElement(aSelected);

		if (jiraReq == null)
		{
			return null;
		}

		List<IRPModelElement> anchors = jiraReq.getAnchoredByMe().toList();

		boolean isAnchored = false;
		for (IRPModelElement anchor : anchors)
		{
			if (anchor.equals(aSelected))
			{
				isAnchored = true;
				break;
			}
		}

		if (isAnchored == false)
		{
			jiraReq.addAnchor(aSelected);
		}

		return jiraReq;

	}

	public void anchorModel(IRPRequirement aJiraReq, IRPModelElement aModelElement)
	{
		List<IRPModelElement> anchors = aJiraReq.getAnchoredByMe().toList();

		for (IRPModelElement anchor : anchors)
		{
			if (anchor.equals(aModelElement))
			{
				// System.out.println("Anchor " + aModelElement.getName()+" already exists");
				return;
			}
		}

		System.out.println("Anchor " + aModelElement.getName());
		aJiraReq.addAnchor(aModelElement);

	}

	@SuppressWarnings("unchecked")
	public IRPRequirement anchorAllChanges(IRPModelElement aSelected)
	{
		IRPProject project = aSelected.getProject();

		IRPRequirement jiraReq = getActualJiraElement(aSelected);

		if (jiraReq == null)
		{
			return null;
		}

		IRPComponent component = project.getActiveComponent();

		if (component == null)
		{
			return jiraReq;
		}

		List<IRPModelElement> changedElements = diffmerge(aSelected, -1, true);
		for (IRPModelElement e : changedElements)
		{
			anchorModel(jiraReq, e);
		}


		return jiraReq;

	}

	public List<IRPModelElement> diffmerge(IRPModelElement aSelected, int aRevision, boolean aReport)
	{

		List<IRPModelElement> ret = new ArrayList<>();

		mySaveUnit = aSelected.getSaveUnit();
		if (mySaveUnit == null)
		{
			return ret;
		}

		File directory = new File(mySaveUnit.getCurrentDirectory());
		File sbsFile = new File(directory, mySaveUnit.getFilename());
		if (sbsFile.exists() == false)
		{
			trace(sbsFile.toString() + " does not exist");
			return ret;
		}

		File share = new File(System.getenv("OMROOT"));

		// trace("Working dir: " + share.getParent());

		File headFile = getVersion(aSelected, aRevision);

		File diffMergeExe = new File(share.getParentFile(), "diffmerge.exe");

		if (diffMergeExe.exists() == false)
		{
			trace(diffMergeExe + " does not exist");
			return ret;
		}

		if (diffMergeExe.canExecute() == false)
		{
			trace(diffMergeExe + " is not a exe");
			return null;
		}

		// Diffmerge.exe <file1> <file2> -compare
		// trace(diffMergeExe.toString()+" "+sbsFile.toString()+"
		// "+headFile.toString()+" -xcompare");
		File reportFile = null;

		ProcessBuilder processBuilder = null;
		try
		{

			if (aReport == true)
			{
				reportFile = File.createTempFile("report", ".txt");
				processBuilder = new ProcessBuilder(diffMergeExe.toString(), sbsFile.toString(), headFile.toString(),
						"-uname", sbsFile.toString(), "-compare", "-diffReport", reportFile.toString());
			}
			else
			{
				processBuilder = new ProcessBuilder(diffMergeExe.toString(), sbsFile.toString(), headFile.toString(),
						"-uname", sbsFile.toString(), "-xcompare");
			}

			processBuilder.redirectErrorStream(true);

			Process process = processBuilder.start();
			InputStream inputStream = process.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

			String line;
			while ((line = reader.readLine()) != null)
			{
				// trace(line);
			}

			if (reportFile != null)
			{
				if (reportFile.exists() == true)
				{

					trace("Changed in " + aSelected.getName());
					ret = parseReport(reportFile, aSelected);
					trace("end changes");

					// BufferedReader reportReader = new BufferedReader( new
					// FileReader(reportFile));
					// while((line = reportReader.readLine())!=null)
					// {
					// trace(line);
					// }
					// reportReader.close();

					reportFile.delete();

				}
			}

		}
		catch (Exception e)
		{
			trace(e.toString());
		}

		headFile.delete();
		return ret;

	}

	public File getVersion(IRPModelElement aSelected, int revision)
	{

		IRPUnit unit = aSelected.getSaveUnit();

		if (unit == null)
		{
			return null;
		}

		String fileName = unit.getFilename();
		String directory = unit.getCurrentDirectory();

		File currentFile = new File(fileName);
		File currentDirectory = new File(directory);

		File tempFile = null;

		ProcessBuilder processBuilder = null;

		if (revision < 0)
		{
			processBuilder = new ProcessBuilder("svn", "cat", "-r", "HEAD", fileName);
		}
		else
		{
			processBuilder = new ProcessBuilder("svn", "cat", "-r", Integer.toString(revision), fileName);
		}

		processBuilder.redirectErrorStream(true);
		processBuilder.directory(currentDirectory);
		trace("Current directory: " + processBuilder.directory().toString());

		try
		{
			tempFile = File.createTempFile(unit.getName(), ".sbs");
			Process process = processBuilder.start();
			InputStream inputStream = process.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

			String line;
			while ((line = reader.readLine()) != null)
			{
				writer.write(line);
				writer.newLine();
			}

			writer.close();
			reader.close();

			int exitCode = process.waitFor();
			if (exitCode == 0)
			{
				trace("SVN cmd success " + tempFile.toString() + "  stored.");
			}
			else
			{
				trace("SVN cmd failed. Exit-Code: " + exitCode);
			}

		}
		catch (Exception e)
		{
			trace(e.toString());
		}

		return tempFile;
	}

	private static LocalDateTime parseIsoDateTime(String dateString)
	{
		DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
		LocalDateTime dateTime = LocalDateTime.parse(dateString, formatter);
		return dateTime;
	}

	private static String getFirstLine(String str)
	{
		// Teilt den String an jedem Zeilenumbruch und gibt das erste Element zurück
		String[] lines = str.split("\\R", 2); // "\\R" ist ein Zeilenumbruch-Metazeichen
		return lines.length > 0 ? lines[0] : null; // Prüft, ob überhaupt eine Zeile vorhanden ist
	}

	public List<logRow> readHistory(IRPModelElement aSelected, int aLimit)
	{
		try
		{

			IRPUnit unit = aSelected.getSaveUnit();

			if (unit == null)
			{
				return null;
			}

			String fileName = unit.getFilename();
			String directory = unit.getCurrentDirectory();

			File currentFile = new File(fileName);
			File currentDirectory = new File(directory);

			// svn log --limit 10 --xml
			ProcessBuilder processBuilder = null;

			if (aLimit > 0)
			{
				processBuilder = new ProcessBuilder("svn", "log", "--limit", Integer.toString(aLimit), "--xml",
						fileName);
			}
			else
			{
				processBuilder = new ProcessBuilder("svn", "log", "--stop-on-copy", "--xml", fileName);
			}
			processBuilder.directory(currentDirectory);

			Process process = processBuilder.start();
			InputStream inputStream = process.getInputStream();

			return parseLog(inputStream);

		}
		catch (Exception e)
		{
			trace(e.toString());
			e.printStackTrace();
		}

		return null;
	}

	private List<logRow> parseLog(InputStream inputStream)
			throws ParserConfigurationException, SAXException, IOException
	{
		List<logRow> ret = new ArrayList<logRow>();
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(inputStream);

		// Wurzelelement bekommen
		Element root = doc.getDocumentElement();

		// Knotenliste der "Changes" bekommen
		NodeList logEntries = root.getElementsByTagName("logentry");
		for (int i = 0; i < logEntries.getLength(); i++)
		{
			Node logentry = logEntries.item(i);
			if (logentry.getNodeType() == Node.ELEMENT_NODE)
			{
				Element logentryElement = (Element) logentry;
				String dateISO = logentryElement.getElementsByTagName("date").item(0).getTextContent();
				LocalDateTime date = parseIsoDateTime(dateISO);
				String revision = logentryElement.getAttribute("revision");
				String author = logentryElement.getElementsByTagName("author").item(0).getTextContent();
				String message = logentryElement.getElementsByTagName("msg").item(0).getTextContent();

				trace("Revision: " + revision + "  Author: " + author + " date: "
						+ date.format(DateTimeFormatter.ISO_DATE) + " Msg: " + getFirstLine(message));

				ret.add(new logRow(revision, dateISO, author, message));
				// Daten zur Tabelle hinzufügen
				// model.addRow(new Object[]{date, revision, action, author, message});
			}
		}

		return ret;
	}

	public void showLog(IRPModelElement aSelected)
	{
		if (aSelected == null)
		{
			return;
		}
		IRPUnit unit = aSelected.getSaveUnit();
		if (unit == null)
		{
			return;
		}
		String fileName = unit.getFilename();
		String directory = unit.getCurrentDirectory();
		File currentFile = new File(fileName);
		File currentDirectory = new File(directory);

		ProcessBuilder processBuilder = new ProcessBuilder("TortoiseProc.exe", "/command:log", "/path:" + fileName);
		processBuilder.redirectErrorStream(true);
		processBuilder.directory(currentDirectory);

		try
		{
			Process p = processBuilder.start();

			InputStream inputStream = p.getInputStream();
			BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream));
			String line;

			while ((line = inputReader.readLine()) != null)
			{
				trace(line);
			}
		}
		catch (Exception e)
		{
			trace(e.getMessage());
		}

	}
	

 	public void commit(IRPModelElement aSelected)
	{
		if (aSelected == null)
		{
			return;
		}
		
		mySaveUnit = aSelected.getSaveUnit();

		IRPRequirement req = anchorAllChanges(mySaveUnit);

		if (req == null)
		{
			return;
		}

		
		if (myReport == null)
		{
			return;
		}

		String fileName = mySaveUnit.getFilename();

		String message = myJiraId + ": " + myJiraTitle + "\n";
		message += "<" + fileName + ">\n";
		message += myReport;
		message += "</" + fileName + ">";

		String directory = mySaveUnit.getCurrentDirectory();

		File currentFile = new File(fileName);
		File currentDirectory = new File(directory);

		// TortoiseProc.exe /command:commit /path:"C:\Pfad\zu\Ihrer\Arbeitskopie"
		// /logmsg:"Ihre Commit-Nachricht hier" /closeonend:1
		ProcessBuilder processBuilder = new ProcessBuilder("TortoiseProc.exe", "/command:commit", "/path:" + fileName,
				"/logmsg:\"" + message + "\"");

		// ProcessBuilder processBuilder = new ProcessBuilder("svn", "commit", "-m",
		// message, fileName);

		processBuilder.redirectErrorStream(true);
		processBuilder.directory(currentDirectory);

		try
		{
			Process p = processBuilder.start();

			InputStream inputStream = p.getInputStream();
			BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream));
			String line;

			while ((line = inputReader.readLine()) != null)
			{
				trace(line);
			}
		}
		catch (Exception e)
		{

		}

	}
 	
 	public void update(IRPModelElement aSelected)
 	{
		if (aSelected == null)
		{
			return;
		}

		IRPUnit unit = aSelected.getSaveUnit();
		if (unit == null)
		{
			return;
		}
		String fileName = unit.getFilename();
		String directory = unit.getCurrentDirectory();
		File currentFile = new File(fileName);
		File currentDirectory = new File(directory);

		ProcessBuilder processBuilder = new ProcessBuilder("svn", "update", fileName);
		processBuilder.redirectErrorStream(true);
		processBuilder.directory(currentDirectory);

		try
		{
			Process p = processBuilder.start();

			InputStream inputStream = p.getInputStream();
			BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream));
			String line;

			while ((line = inputReader.readLine()) != null)
			{
				trace(line);
			}
		}
		catch (Exception e)
		{
			trace(e.getMessage());
		}
	}
 		

	private enum actionEnum {
		added, removed, changed, unchanged
	};

	private List<IRPModelElement> parseReport(File aReportFile, IRPModelElement selected)
	{

		List<IRPModelElement> ret = new ArrayList<IRPModelElement>();
		try
		{
			// List<String> lines = Files.readAllLines(aReportFile.toPath());
			Map<Integer, String> hierarchy = new HashMap<Integer, String>();
			// Pattern pattern = Pattern.compile("^(\\s*)([<>]+)\\s*( Class | Operation |
			// Attribute | Association End | Event | Package | Generalisation | Statechart |
			// State | Argument )\\s*([^\\s]+)");
			Pattern pattern = Pattern.compile(
					"^(\\s*)([<>]+)\\s*(Differences found for\\s)?(Class|Operation|Constructor|Attribute|Stereotype|Event|Function|Package|Comment|Generalization|StatechartDiagram|Statechart|State|Argument|Requirement|Reception|Transition|Dependency|Type|File|type|Association End)\\s*([^\\s:]+)");

			IRPUnit unit = selected.getSaveUnit();
			String unitPath = unit.getOwner().getFullPathName();

			BufferedReader reportReader = new BufferedReader(new FileReader(aReportFile));
			String line = null;

			StringBuffer reportBuffer = new StringBuffer();

			while ((line = reportReader.readLine()) != null)
			{

				Matcher matcher = pattern.matcher(line);
				if (matcher.find() == false)
				{

					continue;
				}

				int level = matcher.group(1).length() / 3;
				String op = matcher.group(2);

				actionEnum action = actionEnum.unchanged;

				if (op.contains(">>"))
				{
					action = actionEnum.unchanged;
				}
				else if (op.contains("<>"))
				{
					action = actionEnum.changed;
				}
				else if (op.contains(">"))
				{
					action = actionEnum.added;
				}
				else if (op.contains("<"))
				{
					action = actionEnum.removed;
				}

				String type = matcher.group(4);

				if (type == "Association End")
				{
					type = "AssociationEnd";
				}

				String name = matcher.group(5);

				hierarchy.put(level, name);

				// trace("Level: "+Integer.toString(level) + " Type: " + type + " Name: "+
				// name);

				StringBuilder fullPath = new StringBuilder();

				for (int i = 0; i <= level; i++)
				{
					reportBuffer.append("    ");
					fullPath.append(hierarchy.get(i));
					if (i < (level))
					{
						fullPath.append("::");
					}
				}
				if (action == actionEnum.unchanged)
				{
					reportBuffer.append(type + " " + name + ":");
				}
				else
				{
					reportBuffer.append(action + " " + type + " " + name);
				}
				reportBuffer.append("\n");

				if (action != actionEnum.unchanged)
				{
					String searchString = unitPath + "::" + fullPath.toString();

					IRPModelElement element = unit.findElementsByFullName(searchString, type);
					if (element != null)
					{
						if (RhapsodyHelper.isPartOf(selected, element) == true)
						{
							ret.add(element);
							fullPath.append(" ");
							fullPath.append(action);
							trace(type + " " + fullPath.toString());
						}
					}

				}

			}

			myReport = reportBuffer.toString();

		}
		catch (Exception e)
		{
			trace(e.getMessage());
		}

		ret = RhapsodyHelper.isPartOf(selected, ret);

		addToFavorites(ret);

		return ret;

	}

	private int getBaseRevision(IRPModelElement aSelected)
	{
		List<logRow> history = readHistory(aSelected, -1);

		logRow lastRow = history.get(history.size() - 1);

		int revision = lastRow.getRevision();

		return revision;
	}

	public void diffMergeBase(IRPModelElement aSelected, boolean aReport)
	{
		int revision = getBaseRevision(aSelected);
		trace("Base Revision: " + revision);
		diffmerge(aSelected, revision, aReport);

	}

	private void addToFavorites(List<IRPModelElement> aModels)
	{

		if (myApplication == null)
		{
			return;
		}

		IRPCollection modelCollection = myApplication.createNewCollection();

		for (IRPModelElement model : aModels)
		{
			modelCollection.addItem(model);
		}

		myApplication.selectModelElements(modelCollection);
		myApplication.addSelectedToFavorites();

	}

	public void getLock(IRPModelElement aSelected)
	{

		if (aSelected == null)
		{
			trace("Selected element is null!");
			return;
		}

		IRPUnit unit = aSelected.getSaveUnit();

		if (unit == null)
		{
			trace(aSelected.getName() + " has no save Unit");
			return;
		}

		String fileName = unit.getFilename();
		String directory = unit.getCurrentDirectory();

		File currentFile = new File(fileName);
		File currentDirectory = new File(directory);

		ProcessBuilder processBuilder = null;
		// svn lock src/main.c
		processBuilder = new ProcessBuilder("svn", "lock", fileName);

		processBuilder.redirectErrorStream(true);
		processBuilder.directory(currentDirectory);

		try
		{
			Process p = processBuilder.start();

			InputStream inputStream = p.getInputStream();
			BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream));
			String line;

			while ((line = inputReader.readLine()) != null)
			{
				trace(line);
			}
		}
		catch (Exception e)
		{
			// TODO: handle exception
		}
	}

	public class logRow
	{
		private int myRevision = 0;
		private String myAuthor = "";
		private LocalDateTime myDate;
		private String myMessage = "";

		public logRow(String aRevision, String aDate, String aAuthor, String aMessage)
		{
			myRevision = Integer.parseInt(aRevision);
			myDate = parseIsoDateTime(aDate);
			myAuthor = aAuthor;
			myMessage = aMessage;
		}

		public int getRevision()
		{
			return myRevision;
		}

	}

}
