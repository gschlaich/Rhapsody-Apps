package de.schlaich.gunnar.rhapsody.utilities;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
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
	
	private Path myTempPath = null;

	public SVNTools(IRPApplication aApplication, Consumer<String> aTraceAction)
	{
		myTraceAction = aTraceAction;
		myApplication = aApplication;

	}
	
	private boolean hasChanged(IRPModelElement aSelected, int aRevision1, int aRevision2)
    {
        List<IRPModelElement> changes = diffmerge(aSelected, aRevision1, aRevision2, true, false);
        if (changes.size() > 0)
        {
            return true;
        }
        return false;
    }
	
	public void showChangeList(IRPModelElement aSelected, int aLimit, boolean aIsMonth)
	{
		List<logRow> changesUnit = readHistory(aSelected, aLimit, aIsMonth);
		
		List<HistoryRow > changes = new ArrayList<HistoryRow>();
		
		logRow lastRow = null;
		for (logRow row : changesUnit)
		{
			int revision = row.getRevision();
			if (lastRow != null)
			{
				int lastRevision = lastRow.getRevision();
				trace("Check -- Revision: " + revision + " LastRevision: " + lastRevision);
				
				List<IRPModelElement> changedElements = diffmerge(aSelected,lastRevision, revision, true, false);
				
				if (changedElements.size() > 0)
				{
					changes.add(new HistoryRow(lastRow, changedElements));
					trace("Revision: " + lastRevision + " has changes");
				}
				else
				{
					trace("Revision: " + lastRevision + " has no changes");
				}
				
			}
			lastRow = row;
		}
		
		File sbsTempDir = getSBSTempDir();
		for (File f : sbsTempDir.listFiles())
		{
			f.delete();
		}
	
		Object[][] data = new Object[changes.size()][5];
		
		trace("------------------------------ Changes:"       );
		for (HistoryRow hrow : changes)
		{
			logRow row = hrow.getLogRow();
			List<IRPModelElement> changedElements = hrow.getChangedElements();
			
			String changedElementsString = "";
			
			for (IRPModelElement element : changedElements)
			{
				changedElementsString += "["+element.getMetaClass()+" "+element.getName()+"] ";
				
			}
			
			String message = row.myMessage;
			
			String regex = "\\bUSM-\\d[\\w_]*\\b"; // Startet mit USM-, gefolgt von Zahlen und optional weiteren Zeichen oder Unterstrichen

	        // Pattern und Matcher erstellen
	        Pattern pattern = Pattern.compile(regex);
	        Matcher matcher = pattern.matcher(message);

	        // Falls ein Treffer vorhanden ist
	        if (matcher.find()) {
	            // Das gefundene Wort
	            message = matcher.group();

	            // Unterstriche durch Leerzeichen ersetzen
	            message = message.replace("_", " ");
	        }
		
			trace("Revision: " + row.getRevision() + " Author: " + row.myAuthor + " Date: " + row.myDate+ " Elements: " + changedElementsString + " Message: " + message);
			
			data[changes.indexOf(hrow)][0] = row.getRevision();
			data[changes.indexOf(hrow)][1] = row.myAuthor;
			data[changes.indexOf(hrow)][2] = row.myDate;
			data[changes.indexOf(hrow)][3] = message;
			data[changes.indexOf(hrow)][4] = changedElementsString;
		
		}


		 // Spaltennamen für die Tabelle
        String[] columnNames = {"Revision", "Author", "Date", "Jira", "Changed Elements"};

        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
        JTable table = new JTable(tableModel);

        // Tabelle in ein ScrollPane einfügen
        JScrollPane scrollPane = new JScrollPane(table);

        // Scrollbar-Strategie festlegen
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); // Horizontal scrollbar nur bei Bedarf
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);     // Vertikal scrollbar nur bei Bedarf

        // Dialog erstellen
        JDialog dialog = new JDialog();
        

		ImageIcon icon = new ImageIcon(aSelected.getIconFileName());
		dialog.setIconImage(icon.getImage());
		dialog.setTitle("SVN History for "+ aSelected.getMetaClass() + " " + aSelected.getName());
		
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // Dialog-Layout festlegen
        dialog.setLayout(new BorderLayout());
        dialog.add(scrollPane, BorderLayout.CENTER);

        // Größe des Fensters festlegen
        dialog.setSize(600, 400); // Breite: 600px, Höhe: 400px
        dialog.setLocationRelativeTo(null); // Fenster zentrieren

        // Dialog anzeigen
        dialog.setVisible(true);
		
	}
	
	public void showChangeStatistic(IRPModelElement aSelected, int aLimitMonths)
    {
		
		Map<IRPModelElement, Integer> changedElementsMap = new HashMap<IRPModelElement, Integer>();
		
		List<logRow> changesUnit = readHistory(aSelected, aLimitMonths, true);
		
		logRow lastRow = null;
		for (logRow row : changesUnit)
		{
			int revision = row.getRevision();
			if (lastRow != null)
			{
				int lastRevision = lastRow.getRevision();

				List<IRPModelElement> changedElements = diffmerge(aSelected, lastRevision, revision, true, false);
				
				for (IRPModelElement element : changedElements)
                {
					if (changedElementsMap.containsKey(element))
                    {
                    	changedElementsMap.put(element, changedElementsMap.get(element)+1);
                    }
                    else
                    {
                    	changedElementsMap.put(element, 1);
                    }
                }
			}
			lastRow = row;
		}
		
		File sbsTempDir = getSBSTempDir();
		for (File f : sbsTempDir.listFiles())
		{
			f.delete();
		}

		
		if (changedElementsMap.size() == 0)
		{
			trace("No changes found");
			return;
		}

		
		JTreeNode rootTreeNode = new JTreeNode(aSelected,changedElementsMap.get(aSelected));
        
    	DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootTreeNode);
    	
		addToTree(changedElementsMap, root, aSelected);
		
		JTree tree = new JTree(root);
		
		tree.setCellRenderer(new CustomTreeCellRenderer());
		
		tree.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				if (e.getClickCount() == 2)
				{
					DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
					if (selectedNode != null)
					{
						JTreeNode node = (JTreeNode) selectedNode.getUserObject();
						IRPModelElement element = node.getElement();
						showChangeList(element, 24, true);
					}
				}
			}
		});

		
		JScrollPane scrollPane = new JScrollPane(tree);

		// Scrollbar-Strategie festlegen
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); // Horizontal scrollbar
																								// nur bei Bedarf
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); // Vertikal scrollbar nur
																							// bei Bedarf

		// Dialog erstellen
		JDialog dialog = new JDialog();
		
		ImageIcon icon = new ImageIcon(aSelected.getIconFileName());
		dialog.setIconImage(icon.getImage());
		dialog.setTitle("SVN Statistic for "+ aSelected.getMetaClass() + " " + aSelected.getName());
		
		
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		// Dialog-Layout festlegen
		dialog.setLayout(new BorderLayout());
		dialog.add(scrollPane, BorderLayout.CENTER);

		// Größe des Fensters festlegen
		dialog.setSize(600, 400); // Breite: 600px, Höhe: 400px
		dialog.setLocationRelativeTo(null); // Fenster zentrieren

		// Dialog anzeigen
		dialog.setVisible(true);
	
		
	
    }
	
	
	private void addToTree(Map<IRPModelElement, Integer> aMap, DefaultMutableTreeNode aNode, IRPModelElement aElement)
	{
		trace("Add to tree: " + aElement.getName());
		
		List<IRPModelElement> elements = aElement.getNestedElements().toList();
		List<JTreeNode> nodeList = new ArrayList<JTreeNode>();
		
		for(IRPModelElement element : elements)
		{
			if (aMap.containsKey(element))
			{
				JTreeNode node = new JTreeNode(element, aMap.get(element));
				nodeList.add(node);
			}
		}
		
		nodeList.sort(Collections.reverseOrder());
		
		for (JTreeNode node : nodeList)
		{
			DefaultMutableTreeNode child = new DefaultMutableTreeNode(node);
			aNode.add(child);
			addToTree(aMap, child, node.getElement());
		}
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

		List<IRPModelElement> changedElements = diffmerge(aSelected, -1, -1, true, true);
		for (IRPModelElement e : changedElements)
		{
			anchorModel(jiraReq, e);
		}


		return jiraReq;

	}

	public List<IRPModelElement> diffmerge(IRPModelElement aSelected, int aRevision, int aRevisionSource, boolean aReport, boolean addToFavorites)
	{

		List<IRPModelElement> ret = new ArrayList<IRPModelElement>();
		
		mySaveUnit = aSelected.getSaveUnit();
		if (mySaveUnit == null)
		{
			return ret;
		}

		File directory = new File(mySaveUnit.getCurrentDirectory());
		File sbsFile = new File(directory, mySaveUnit.getFilename());
		File reportFile = null;
		
		File tempDir = getTempDir();
		
		if (aReport == true)
		{
			String reportFileName = "report_"+mySaveUnit.getFullPathName()+"_"+aRevision+"_"+aRevisionSource+".txt";
			reportFileName = reportFileName.replace("::", "_");
			
			reportFile = new File(tempDir, reportFileName);
			
			if (reportFile.exists() == true)
			{
				ret = parseReport(reportFile, aSelected, addToFavorites);
				return ret;
			}
		}
		
		
		if (aRevisionSource > 0)
		{
			sbsFile = getVersion(aSelected, aRevisionSource);
		}
		
		if (sbsFile.exists() == false)
		{
			trace(sbsFile.toString() + " does not exist");
			return ret;
		}

		File share = new File(System.getenv("OMROOT"));

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
		
		
		
		

		ProcessBuilder processBuilder = null;
		try
		{

			if (aReport == true)
			{
				
					reportFile.createNewFile();
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
					ret = parseReport(reportFile, aSelected, addToFavorites);
					trace("end changes");

					// BufferedReader reportReader = new BufferedReader( new
					// FileReader(reportFile));
					// while((line = reportReader.readLine())!=null)
					// {
					// trace(line);
					// }
					// reportReader.close();

					

				}
			}

		}
		catch (Exception e)
		{
			trace(e.toString());
		}
		
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
			
			String unitName = unit.getFullPathName();
			unitName = unitName.replace("::", "_");

			File sbsTempDir = getSBSTempDir();

			tempFile = new File(sbsTempDir, unitName+"_"+Integer.toString(revision)+".sbs");
			
			//tempFile = File.createTempFile(unitName, ".sbs", tempDir);
			
			if(tempFile.exists()==false)
            {
                
			
				tempFile.createNewFile();
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

	public List<logRow> readHistory(IRPModelElement aSelected, int aLimit, boolean aIsMonth)
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
				if (aIsMonth == true)
				{
					
					LocalDate currentDate = LocalDate.now();
					LocalDate startDate = currentDate.minusMonths(aLimit);
					
					String currentDateString = currentDate.format(DateTimeFormatter.ISO_DATE);
					String startDateString = startDate.format(DateTimeFormatter.ISO_DATE);
					
					String dateString = "{"+currentDateString+"}:{"+startDateString+"}";
					
					//svn log -r {2022-12-23}:{2024-12-23} --xml PFE.sbs
					
					processBuilder = new ProcessBuilder("svn", "log", "-r", dateString, "--xml", fileName);
				}
				else
				{
					processBuilder = new ProcessBuilder("svn", "log", "--limit", Integer.toString(aLimit), "--xml",
							fileName);
				}
				
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

	private File getTempDir()
	{
		File globalTempDir = new File(System.getProperty("java.io.tmpdir"));
		File tempDir = new File(globalTempDir, "tempSVNTools");
		if (tempDir.exists() == false)
		{
			tempDir.mkdir();
		}
		return tempDir;
	}
 	
	private File getSBSTempDir()
	{
		File tempDir = getTempDir();
		File sbsTempDir = new File(tempDir, "sbs");
		if (sbsTempDir.exists() == false)
		{
			sbsTempDir.mkdir();
		}
		return sbsTempDir;
	}
	

	private enum actionEnum {
		added, removed, changed, unchanged
	};

	private List<IRPModelElement> parseReport(File aReportFile, IRPModelElement selected, boolean addToFavorites)
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
					action = actionEnum.changed;
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

		if (addToFavorites == true)
		{
			addToFavorites(ret);
		}
		

		return ret;

	}

	private int getBaseRevision(IRPModelElement aSelected)
	{
		List<logRow> history = readHistory(aSelected, -1, false);

		logRow lastRow = history.get(history.size() - 1);

		int revision = lastRow.getRevision();

		return revision;
	}

	public void diffMergeBase(IRPModelElement aSelected, boolean aReport)
	{
		int revision = getBaseRevision(aSelected);
		trace("Base Revision: " + revision);
		diffmerge(aSelected, revision, -1, aReport, true);

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
	
	public class HistoryRow
	{
		private logRow myLogRow = null;
		private List<IRPModelElement> myChangedElements = null;
		
		public HistoryRow(logRow aLogRow, List<IRPModelElement> aChangedElements)
        {
            myLogRow = aLogRow;
            myChangedElements = aChangedElements;
        }
		
		public logRow getLogRow()
		{
			return myLogRow;
		}
		
		public List<IRPModelElement> getChangedElements()
		{
			return myChangedElements;
		}
		
	}
	
	private class JTreeNode implements Comparable<JTreeNode>
	{
		private IRPModelElement myElement = null;
		private int myChanges = 0;
		private boolean myFound = false;
		
		public JTreeNode(IRPModelElement aElement, int aChanges)
		{
			myElement = aElement;
			myChanges = aChanges;
		}
		
		public JTreeNode(IRPModelElement aElement)
		{
			myElement = aElement;
			myChanges = 0;
		}
		
		public void setChanges(int aChanges)
        {
            myChanges = aChanges;
        }
		
		public IRPModelElement getElement()
		{
			return myElement;
		}
		
		public int getChanges()
		{
			return myChanges;
		}
		
		public String toString()
		{
			return myElement.getName() + " [" + myElement.getMetaClass() + "] Changes: " + myChanges;
		}
		
		public boolean isFound()
		{
			return myFound;
		}
		
		public void setFound(boolean aFound)
		{
			myFound = aFound;
		}

		@Override
		public int compareTo(JTreeNode o)
		{
			return myChanges - o.getChanges();       
		}
	}
	
	private class CustomTreeCellRenderer extends DefaultTreeCellRenderer
	{
		private static final long serialVersionUID = 1L;

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
				boolean leaf, int row, boolean hasFocus)
		{
			super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
			
			DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) value;
			
			JTreeNode node = (JTreeNode) treeNode.getUserObject();
			
			IRPModelElement element = node.getElement();
			String iconPath = element.getIconFileName();
			
			if (iconPath != null)
			{
				setIcon(new ImageIcon(iconPath));
			}
			
			return this;
		}
		
	}
}
