package de.schlaich.gunnar.rhapsody.utilities;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.math.MathContext;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import javax.swing.text.html.HTMLEditorKit.Parser;
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

import com.github.difflib.DiffUtils;
import com.github.difflib.algorithm.myers.MyersDiff;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Chunk;
import com.github.difflib.patch.DeltaType;
import com.github.difflib.patch.DiffException;
import com.github.difflib.patch.Patch;
import com.github.difflib.text.DiffRow;
import com.github.difflib.text.DiffRowGenerator;
import com.github.difflib.text.DiffRow.Tag;
import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPAttribute;
import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPCollection;
import com.telelogic.rhapsody.core.IRPComponent;
import com.telelogic.rhapsody.core.IRPGuard;
import com.telelogic.rhapsody.core.IRPHyperLink;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPOperation;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPProfile;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPRequirement;
import com.telelogic.rhapsody.core.IRPSearchManager;
import com.telelogic.rhapsody.core.IRPSearchQuery;
import com.telelogic.rhapsody.core.IRPSelection;
import com.telelogic.rhapsody.core.IRPState;
import com.telelogic.rhapsody.core.IRPStatechart;
import com.telelogic.rhapsody.core.IRPTransition;
import com.telelogic.rhapsody.core.IRPTrigger;
import com.telelogic.rhapsody.core.IRPUnit;
import com.telelogic.rhapsody.core.RhapsodyAppServer;

import de.schlaich.gunnar.aiTools.mcp.jsonModels.JsonModelTester;

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

	private RevisionDatabase myRevisionDatabase = null;

	private Map<String, List<logRow>> myLogMap = new HashMap<>();

	public SVNTools(IRPApplication aApplication, Consumer<String> aTraceAction)
	{
		myTraceAction = aTraceAction;
		myApplication = aApplication;

		if (updateDataBaseFile() == false)
		{
			trace("Error updating database file");
		}

		try
		{
			myRevisionDatabase = new RevisionDatabase(getTempDir(), aTraceAction);
		}
		catch(SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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

	public void showChangeList(IRPModelElement aSelected, int aLimit, boolean aIsMonth, int aRevisionStart,
			int aRevisionEnd)
	{
		List<logRow> changesUnit = readHistory(aSelected, aLimit, aIsMonth, aRevisionStart, aRevisionEnd);

		List<HistoryRow> changes = new ArrayList<HistoryRow>();

		logRow lastRow = null;

		trace("showChangeList size: " + changesUnit.size());

		for (logRow row : changesUnit)
		{
			int revision = row.getRevision();
			if (lastRow != null)
			{
				int lastRevision = lastRow.getRevision();
				// trace("Check -- Revision: " + revision + " LastRevision: " + lastRevision);

				List<IRPModelElement> changedElements = diffmerge(aSelected, lastRevision, revision, true, false);

				if (changedElements.size() > 0)
				{
					List<ChangedElement> changedElementsList = new ArrayList<ChangedElement>();
					for (IRPModelElement element : changedElements)
					{
						ChangedElement changedElement = new ChangedElement(element);
						changedElementsList.add(changedElement);
					}

					changes.add(new HistoryRow(lastRow, revision, changedElementsList));
					trace("Revision: " + lastRevision + " has changes");
				}
				else
				{
					// trace("Revision: " + lastRevision + " has no changes");
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

		trace("------------------------------ Changes:");
		for (HistoryRow hrow : changes)
		{

			logRow row = hrow.getLogRow();

			List<ChangedElement> changedElements = hrow.getChangedElements();

			/*
			 * 
			 * String changedElementsString = "";
			 * 
			 * 
			 * for (ChangedElement element : changedElements) { changedElementsString +=
			 * element.toString()+", ";
			 * 
			 * }
			 * 
			 * changedElementsString = changedElementsString.substring(0,
			 * changedElementsString.length()-2);
			 */

			String message = row.myMessage;

			String regex = "\\bUSM-\\d[\\w_:-]*\\b"; // Startet mit USM-, gefolgt von Zahlen und optional weiteren
														// Zeichen oder Unterstrichen

			// Pattern und Matcher erstellen
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(message);

			// Falls ein Treffer vorhanden ist
			if (matcher.find())
			{
				// Das gefundene Wort
				message = matcher.group();

				message = message.replace(":", "");
				// Unterstriche durch Leerzeichen ersetzen
				message = message.replace("_", " ");

			}

			// trace("Revision: " + row.getRevision() + " Author: " + row.myAuthor + " Date:
			// " + row.myDate+ " Elements: " + changedElementsString + " Message: " +
			// message);

			String formattedDate = row.myDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));

			data[changes.indexOf(hrow)][0] = new String(row.getRevision() + " " + hrow.getLastRevision());
			data[changes.indexOf(hrow)][1] = row.myAuthor;
			data[changes.indexOf(hrow)][2] = formattedDate;
			data[changes.indexOf(hrow)][3] = message;
			data[changes.indexOf(hrow)][4] = changedElements;

		}

		// Spaltennamen f�r die Tabelle
		String[] columnNames =
		{ "Revision", "Author", "Date", "Jira", "Changed Elements" };

		DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
		JTable table = new JTable(tableModel);

		// Get the TableColumnModel from the JTable
		TableColumnModel columnModel = table.getColumnModel();

		// Apply the custom cell renderer to the "Message" column
		columnModel.getColumn(0).setCellRenderer(new LinkCellRenderer());
		columnModel.getColumn(3).setCellRenderer(new LinkCellRenderer());

		FontMetrics fontMetrics = table.getFontMetrics(table.getFont());

		int maxWidthColumn[] = new int[table.getColumnCount()];

		for (int row = 0; row < table.getRowCount(); row++)
		{
			for (int column = 0; column < table.getColumnCount(); column++)
			{
				Object value = table.getValueAt(row, column);
				if (value != null)
				{
					int width = fontMetrics.stringWidth(value.toString());
					if (width > maxWidthColumn[column])
					{
						maxWidthColumn[column] = width + 10;
					}
				}
			}
		}

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();

		if (maxWidthColumn[3] > screenSize.width / 4)
		{
			maxWidthColumn[3] = screenSize.width / 4;
		}

		for (int column = 0; column < table.getColumnCount(); column++)
		{
			columnModel.getColumn(column).setPreferredWidth(maxWidthColumn[column]);
		}

		table.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				int column = table.columnAtPoint(e.getPoint());
				int row = table.rowAtPoint(e.getPoint());

				// Check if the clicked column is the "Revision" column
				if (column == 0 && row >= 0)
				{
					String revString = (String) table.getValueAt(row, column);

					String[] revs = revString.split(" ");

					int currentRevision = Integer.parseInt(revs[1]);
					int previousRevision = Integer.parseInt(revs[0]);

					// Call the diffmerge method with the two revision numbers
					List<IRPModelElement> changedElements = diffmerge(aSelected, previousRevision, currentRevision,
							false, false);
				}
				else if (column == 3 && row >= 0)
				{
					String jira = (String) table.getValueAt(row, column);

					if (jira != null)
					{
						String regex = "\\bUSM-\\d+\\b";
						Pattern pattern = Pattern.compile(regex);
						Matcher matcher = pattern.matcher(jira);

						if (matcher.find())
						{
							String jiraIssueId = matcher.group();
							String url = "https://berninaag.atlassian.net/browse/" + jiraIssueId;

							// Open the URL in the default web browser
							try
							{
								java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
							}
							catch(Exception ex)
							{
								ex.printStackTrace();
							}
						}

					}
				}
				else if (column == 4 && row >= 0)
				{

					List<ChangedElement> elements = (List<ChangedElement>) table.getValueAt(row, column);
					// String elementsString = (String) table.getValueAt(row, column);
					// String[] elements = elementsString.split(", ");

					// Calculate the clicked element based on mouse position
					int clickX = e.getX();
					int startX = table.getCellRect(row, column, true).x;
					int elementIndex = -1;
					int currentX = startX;

					int i = 0;

					for (; i < elements.size(); i++)
					{
						currentX += table.getFontMetrics(table.getFont()).stringWidth(
								elements.get(i).toString() + table.getFontMetrics(table.getFont()).stringWidth(", "));
						if (clickX < currentX)
						{
							elementIndex = i;
							break;
						}
					}

					if (elementIndex != -1)
					{

						IRPModelElement element = elements.get(i).getElement();

						// element.locateInBrowser();

						String elementName = elements.get(i).toString();

						trace("Clicked element: " + elementName);

						if (element instanceof IRPOperation)
						{
							String revString = (String) table.getValueAt(row, 0);

							String[] revs = revString.split(" ");

							int currentRevision = Integer.parseInt(revs[1]);
							int previousRevision = Integer.parseInt(revs[0]);

							IRPOperation operation = (IRPOperation) element;

							compareOperationVersions(operation, previousRevision, currentRevision);
						}
						else if (element instanceof IRPAttribute)
						{
							String revString = (String) table.getValueAt(row, 0);

							String[] revs = revString.split(" ");

							int currentRevision = Integer.parseInt(revs[1]);
							int previousRevision = Integer.parseInt(revs[0]);
							IRPAttribute attribute = (IRPAttribute) element;
							compareAttributeVersion(attribute, currentRevision, previousRevision);
						}
						else
						{
							// show in statistics
							String revString = (String) table.getValueAt(row, 0);

							String[] revs = revString.split(" ");

							int currentRevision = Integer.parseInt(revs[1]);
							int previousRevision = Integer.parseInt(revs[0]);

							showChangeStatistic(element, 20, false, currentRevision, previousRevision);
						}

					}
				}
			}
		});

		// Tabelle in ein ScrollPane einf�gen
		JScrollPane scrollPane = new JScrollPane(table);

		// Scrollbar-Strategie festlegen
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); // Horizontal scrollbar nur
																								// bei Bedarf
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); // Vertikal scrollbar nur bei
																							// Bedarf

		// Dialog erstellen
		JDialog dialog = new JDialog();

		ImageIcon icon = new ImageIcon(aSelected.getIconFileName());
		dialog.setIconImage(icon.getImage());
		dialog.setTitle("SVN History for " + aSelected.getMetaClass() + " " + aSelected.getName());

		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		// Dialog-Layout festlegen
		dialog.setLayout(new BorderLayout());
		dialog.add(scrollPane, BorderLayout.CENTER);

		// Gr��e des Fensters festlegen

		int width = maxWidthColumn[0] + maxWidthColumn[1] + maxWidthColumn[2] + maxWidthColumn[3] + maxWidthColumn[4];

		if (screenSize.width / 10 * 8 < width)
		{
			width = screenSize.width / 10 * 8;

			maxWidthColumn[4] = width - maxWidthColumn[0] - maxWidthColumn[1] - maxWidthColumn[2] - maxWidthColumn[3];
			columnModel.getColumn(4).setPreferredWidth(maxWidthColumn[4]); // Changed Elements column

		}

		int rowCount = table.getRowCount();
		int rowHeight = table.getRowHeight();
		int headerHeight = table.getTableHeader().getHeight();
		int height = (rowCount * rowHeight) + headerHeight + 70;

		if (screenSize.height / 10 * 8 < height)
		{
			height = screenSize.height / 10 * 8;
		}

		dialog.setSize(width, height);
		dialog.setLocationRelativeTo(null); // Fenster zentrieren

		// Dialog anzeigen
		dialog.setVisible(true);

	}

	public void showChangeStatistic(IRPModelElement aSelected, int aLimit, boolean aIsMonth, int aRevisionFrom,
			int aRevisionTo)
	{

		Map<IRPModelElement, Integer> changedElementsMap = getChangedElements(aSelected, aLimit, aIsMonth,
				aRevisionFrom, aRevisionTo);

		if (changedElementsMap.size() == 0)
		{
			JOptionPane.showMessageDialog(null, "No changes found", "Info", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		JTreeNode rootTreeNode = new JTreeNode(aSelected, changedElementsMap.get(aSelected));

		DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootTreeNode);

		addToTree(changedElementsMap, root, aSelected);

		// Create the popup menu
		JPopupMenu popupMenu = new JPopupMenu();
		JMenuItem featuresItem = new JMenuItem("Features...");
		JMenuItem locateItem = new JMenuItem("Locate");
		JMenuItem historyItem = new JMenuItem("History");
		JMenuItem blameItem = new JMenuItem("Blame");
		JMenuItem diffItem = new JMenuItem("Diff");

		blameItem.setEnabled(false);
		blameItem.setVisible(false);

		diffItem.setEnabled(false);
		diffItem.setVisible(false);

		popupMenu.add(featuresItem);
		popupMenu.add(locateItem);
		popupMenu.add(historyItem);
		popupMenu.add(blameItem);
		popupMenu.add(diffItem);

		JTree tree = new JTree(root);

		tree.setCellRenderer(new CustomTreeCellRenderer());

		// Add action listeners to menu items
		historyItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				if (selectedNode != null)
				{
					JTreeNode node = (JTreeNode) selectedNode.getUserObject();
					IRPModelElement element = node.getElement();

					showChangeList(element, aLimit, aIsMonth, aRevisionFrom, aRevisionTo);

				}

			}
		});

		blameItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{

				// Get the selected node
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				JTreeNode node = (JTreeNode) selectedNode.getUserObject();
				IRPModelElement element = node.getElement();

				if (element instanceof IRPOperation)
				{
					blame((IRPOperation) element, aLimit, aIsMonth);
				}

			}
		});

		diffItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{

				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				JTreeNode node = (JTreeNode) selectedNode.getUserObject();
				IRPModelElement element = node.getElement();

				if (element instanceof IRPOperation)
				{

					IRPOperation operation = (IRPOperation) element;

					compareOperationVersions(operation, aRevisionTo, aRevisionFrom);
				}
				else if (element instanceof IRPAttribute)
				{
					IRPAttribute attribute = (IRPAttribute) element;
					compareAttributeVersion(attribute, aRevisionTo, aRevisionFrom);
				}
				else
				{
					compareJsonOfRevisions(element, aRevisionTo, aRevisionFrom);
				}

			}
		});

		locateItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				JTreeNode node = (JTreeNode) selectedNode.getUserObject();
				IRPModelElement element = node.getElement();

				element.locateInBrowser();

			}
		});

		featuresItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				JTreeNode node = (JTreeNode) selectedNode.getUserObject();
				IRPModelElement element = node.getElement();
				element.openFeaturesDialog(0);
			}
		});

		tree.addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				if (e.isPopupTrigger())
				{
					showPopup(e);
				}
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				if (e.isPopupTrigger())
				{

					showPopup(e);
				}
			}

			private void showPopup(MouseEvent e)
			{

				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				JTreeNode node = (JTreeNode) selectedNode.getUserObject();
				IRPModelElement element = node.getElement();
				int changes = node.getChanges();

				if (element instanceof IRPOperation)
				{
					if ((changes == 1) && ((aRevisionFrom > 0) || (aRevisionTo > 0)))
					{
						diffItem.setEnabled(true);
						diffItem.setVisible(true);
						blameItem.setEnabled(false);
						blameItem.setVisible(false);
					}
					else
					{
						diffItem.setEnabled(false);
						diffItem.setVisible(false);
						blameItem.setEnabled(true);
						blameItem.setVisible(true);
					}

				}

				else if (element instanceof IRPAttribute)
				{
					if ((changes == 1) && ((aRevisionFrom > 0) || (aRevisionTo > 0)))
					{
						diffItem.setEnabled(true);
						diffItem.setVisible(true);
					}
					else
					{
						diffItem.setEnabled(false);
						diffItem.setVisible(false);
					}

					blameItem.setEnabled(false);
					blameItem.setVisible(false);
				}

				else
				{
					blameItem.setEnabled(false);
					blameItem.setVisible(false);
					diffItem.setEnabled(true);
					diffItem.setVisible(true);
				}

				popupMenu.show(e.getComponent(), e.getX(), e.getY());

			}

			public void mouseClicked(MouseEvent e)
			{
				if (e.getClickCount() == 2)
				{
					DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
					if (selectedNode != null)
					{
						JTreeNode node = (JTreeNode) selectedNode.getUserObject();
						IRPModelElement element = node.getElement();

						if (element instanceof IRPOperation)
						{
							blame((IRPOperation) element, aLimit, aIsMonth);
						}
						else
						{
							showChangeList(element, aLimit, aIsMonth, 0, 0);
						}
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
		dialog.setTitle("SVN Statistic for " + aSelected.getMetaClass() + " " + aSelected.getName());

		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		// Dialog-Layout festlegen
		dialog.setLayout(new BorderLayout());
		dialog.add(scrollPane, BorderLayout.CENTER);

		// Gr��e des Fensters festlegen
		dialog.setSize(600, 400); // Breite: 600px, H�he: 400px
		dialog.setLocationRelativeTo(null); // Fenster zentrieren

		// Dialog anzeigen
		dialog.setVisible(true);

	}

	public void updateDatabase()
	{
		IRPProject p = myApplication.activeProject();

		// list all Components
		List<IRPComponent> components = p.getNestedElementsByMetaClass("Component", 1).toList();

		int count = 0;
		int total = components.size();

		for (IRPComponent c : components)
		{
			count++;
			List<IRPModelElement> elements = c.getScopeElements().toList();
			for (IRPModelElement e : elements)
			{
				if (e instanceof IRPPackage)
				{
					IRPPackage pkg = (IRPPackage) e;
					if (pkg instanceof IRPUnit)
					{
						trace("------------------------ Update package: " + pkg.getName() + "(" + count + "/" + total
								+ ") ------------------------");
						getChangedElements(pkg, 800, false, 0, 0);
					}

				}
			}
		}

	}

	private boolean updateDataBaseFile()
	{
		IRPProject proj = myApplication.activeProject();
		if (proj == null)
		{
			trace("No project active");
			return false;
		}

		IRPModelElement usmProfile = proj.findNestedElementRecursive("USMProfile", "Profile");
		if (usmProfile == null)
		{
			trace("No USMProfile found");
			return false;
		}

		IRPUnit usmProfileUnit = usmProfile.getSaveUnit();

		if (usmProfileUnit == null)
		{
			trace("No USMProfileUnit found");
			return false;
		}

		String usmProfilePath = usmProfileUnit.getCurrentDirectory();

		String dbFileName = RevisionDatabase.GetDBFileName();

		File dbSourceFile = new File(usmProfilePath + File.separator + dbFileName);

		if (dbSourceFile.exists() == false)
		{
			trace("No DB file found");
			return false;
		}

		// get release date
		long sourceModified = dbSourceFile.lastModified();

		File dbTargetFile = new File(getTempDir() + File.separator + dbFileName);

		if (dbTargetFile.exists() == true)
		{
			long targetModified = dbTargetFile.lastModified();

			if (sourceModified <= targetModified)
			{
				trace("DB file is up to date");
				return true;
			}
		}

		trace("Copy DB file to temp dir");
		try
		{
			Files.copy(dbSourceFile.toPath(), dbTargetFile.toPath());

			// set target writeable
			dbTargetFile.setWritable(true);
			dbTargetFile.setReadable(true);

		}
		catch(IOException e)
		{
			e.printStackTrace();
			return false;
		}
		trace("DB file copied");

		return true;

	}

	private Map<IRPModelElement, Integer> getChangedElements(IRPModelElement aSelected, int aLimit, boolean aIsMonth,
			int aRevisionFrom, int aRevisionTo)
	{
		Map<IRPModelElement, Integer> changedElementsMap = new HashMap<IRPModelElement, Integer>();

		List<logRow> changesUnit = readHistory(aSelected, aLimit, aIsMonth, aRevisionFrom, aRevisionTo);

		logRow lastRow = null;

		int count = changesUnit.size();
		int i = 0;

		for (logRow row : changesUnit)
		{
			int revision = row.getRevision();

			if (lastRow != null)
			{
				int lastRevision = lastRow.getRevision();

				trace("Check -- Revision: " + revision + " LastRevision: " + lastRevision + "(" + i + "/" + count
						+ ")");

				List<IRPModelElement> changedElements = diffmerge(aSelected, lastRevision, revision, true, false);

				for (IRPModelElement element : changedElements)
				{
					if (changedElementsMap.containsKey(element))
					{
						changedElementsMap.put(element, changedElementsMap.get(element) + 1);
					}
					else
					{
						changedElementsMap.put(element, 1);
					}
				}
			}
			lastRow = row;
			i++;
		}

		File sbsTempDir = getSBSTempDir();
		for (File f : sbsTempDir.listFiles())
		{
			f.delete();
		}

		if (changedElementsMap.size() == 0)
		{
			trace("No changes found");

		}
		return changedElementsMap;
	}

	public void showLOCStatistic(IRPModelElement aSelected)
	{
		JTreeNode rootTreeNode = new JTreeNode(aSelected);

		DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootTreeNode);

		int loc = addToLOCTree(aSelected, root);

		rootTreeNode.setChanges(loc);

		JTree tree = new JTree(root);

		tree.setCellRenderer(new CustomTreeCellRenderer());

		tree.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				if (e.getButton() == MouseEvent.BUTTON3)
				{
					DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
					if (selectedNode != null)
					{
						JTreeNode node = (JTreeNode) selectedNode.getUserObject();
						IRPModelElement element = node.getElement();

						if (element instanceof IRPStatechart)
						{
							IRPStatechart statechart = (IRPStatechart) element;
							statechart.getStatechartDiagram().openDiagram();

						}
						else
						{
							// element.locateInBrowser();
							element.openFeaturesDialog(0);
						}

					}
				}
				else if (e.getClickCount() == 2)
				{
					DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
					if (selectedNode != null)
					{
						JTreeNode node = (JTreeNode) selectedNode.getUserObject();
						IRPModelElement element = node.getElement();

						if (element instanceof IRPStatechart)
						{
							IRPStatechart statechart = (IRPStatechart) element;
							statechart.getStatechartDiagram().openDiagram();

						}
						else
						{
							element.locateInBrowser();
							// element.openFeaturesDialog(0);
						}

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
		dialog.setTitle("Lines Of Code for " + aSelected.getMetaClass() + " " + aSelected.getName());

		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		// Dialog-Layout festlegen
		dialog.setLayout(new BorderLayout());
		dialog.add(scrollPane, BorderLayout.CENTER);

		// Gr��e des Fensters festlegen
		dialog.setSize(600, 400); // Breite: 600px, H�he: 400px
		dialog.setLocationRelativeTo(null); // Fenster zentrieren

		// Dialog anzeigen
		dialog.setVisible(true);

	}

	private int addToLOCTree(IRPModelElement aElement, DefaultMutableTreeNode aNode)
	{

		int loc = 0;

		if (aElement instanceof IRPOperation)
		{
			IRPOperation operation = (IRPOperation) aElement;
			String body = operation.getBody();
			String[] lines = body.split("\r\n|\r|\n");
			loc = lines.length;
		}

		else if (aElement instanceof IRPGuard)
		{
			IRPGuard guard = (IRPGuard) aElement;
			String body = guard.getBody();
			String[] lines = body.split("\r\n|\r|\n");
			loc = lines.length;
		}
		else if (aElement instanceof IRPTrigger)
		{
			IRPTrigger trigger = (IRPTrigger) aElement;
			String body = trigger.getBody();
			String[] lines = body.split("\r\n|\r|\n");
			loc = lines.length;
		}
		else
		{
			if (aElement instanceof IRPState)
			{
				IRPState state = (IRPState) aElement;
				String entryAction = state.getEntryAction();
				String exitAction = state.getExitAction();

				String[] entryLines = entryAction.split("\r\n|\r|\n");
				String[] exitLines = exitAction.split("\r\n|\r|\n");

				loc = entryLines.length + exitLines.length;
			}

			List<DefaultMutableTreeNode> nodeList = new ArrayList<DefaultMutableTreeNode>();
			List<IRPModelElement> elements = aElement.getNestedElements().toList();
			for (IRPModelElement element : elements)
			{
				JTreeNode node = new JTreeNode(element, loc);
				node.setLinesOfCode(true);

				DefaultMutableTreeNode child = new DefaultMutableTreeNode(node);
				// aNode.add(child);
				int l = addToLOCTree(element, child);

				if (l > 0)
				{
					node.setChanges(l);
					nodeList.add(child);
					loc += l;
				}
			}

			// Collections.sort(nodeList, nodeComparator);
			Collections.sort(nodeList, new Comparator<DefaultMutableTreeNode>()
			{
				public int compare(DefaultMutableTreeNode o1, DefaultMutableTreeNode o2)
				{
					JTreeNode node1 = (JTreeNode) o1.getUserObject();
					JTreeNode node2 = (JTreeNode) o2.getUserObject();

					return node2.getChanges() - node1.getChanges();

				}
			});

			for (DefaultMutableTreeNode child : nodeList)
			{
				aNode.add(child);
			}

		}
		if (loc > 0)
		{
			trace(aElement.getName() + " [" + aElement.getMetaClass() + "] lines of code: " + loc);
		}
		return loc;

	}

	private void addToTree(Map<IRPModelElement, Integer> aMap, DefaultMutableTreeNode aNode, IRPModelElement aElement)
	{
		// trace("Add to tree: " + aElement.getName());

		List<IRPModelElement> elements = aElement.getNestedElements().toList();
		List<JTreeNode> nodeList = new ArrayList<JTreeNode>();

		for (IRPModelElement element : elements)
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
		catch(InterruptedException | IOException e)
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

	public void diffTreeDialog(IRPModelElement aSelected)
	{
		// dialog for selecting the revisions
		// create a dialog
		JDialog dialog = new JDialog();
		dialog.setTitle("Select Revisions");
		dialog.setSize(400, 200);
		dialog.setLayout(new BorderLayout());
		dialog.setLocationRelativeTo(null);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		// create a panel
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(3, 2));
		dialog.add(panel, BorderLayout.CENTER);

		// create labels
		JLabel revision1Label = new JLabel("Revision 1:");
		JLabel revision2Label = new JLabel("Revision 2:");

		// create textfields
		JTextField revision1Field = new JTextField("HEAD");
		JTextField revision2Field = new JTextField("BASE");

		// add labels and textfields to panel
		panel.add(revision1Label);
		panel.add(revision1Field);
		panel.add(revision2Label);
		panel.add(revision2Field);

		// create a button
		JButton okButton = new JButton("OK");

		// add button to panel
		panel.add(okButton);

		// add action listener to button
		okButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String revision1String = revision1Field.getText();
				String revision2String = revision2Field.getText();
				if (revision1String.equals("HEAD"))
				{
					revision1String = "1";
				}
				else if (revision1String.equals("BASE"))
				{
					revision1String = "0";
				}

				if (revision2String.equals("HEAD"))
				{
					revision2String = "1";
				}
				else if (revision2String.equals("BASE"))
				{
					revision2String = "0";
				}

				int revision1 = Integer.parseInt(revision1String);
				int revision2 = Integer.parseInt(revision2String);

				diffTree(aSelected, revision1, revision2);

				dialog.dispose();
			}
		});

		dialog.setVisible(true);

	}

	public void diffTree(IRPModelElement aSelected, int aRevision1, int aRevision2)
	{

		if (aRevision1 == 0)
		{
			aRevision1 = getBaseRevision(aSelected);
		}

		List<IRPModelElement> changes = diffmerge(aSelected, aRevision1, aRevision2, true, false);

		if (changes.size() == 0)
		{
			trace("No changes found");
			return;
		}

		JTreeNode rootTreeNode = new JTreeNode(aSelected, changes.size());

		DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootTreeNode);

		Map<IRPModelElement, Integer> changesMap = new HashMap<IRPModelElement, Integer>();

		for (IRPModelElement element : changes)
		{
			changesMap.put(element, 1);
		}

		addToTree(changesMap, root, aSelected);

		JTree tree = new JTree(root);

		tree.setCellRenderer(new CustomTreeCellRenderer());

		tree.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				if (e.getButton() == MouseEvent.BUTTON3)
				{
					DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
					if (selectedNode != null)
					{
						JTreeNode node = (JTreeNode) selectedNode.getUserObject();
						IRPModelElement element = node.getElement();

						if (element instanceof IRPStatechart)
						{
							IRPStatechart statechart = (IRPStatechart) element;
							statechart.getStatechartDiagram().openDiagram();

						}
						else
						{
							element.locateInBrowser();
							// element.openFeaturesDialog(0);
						}

					}
				}
				else if (e.getClickCount() == 2)
				{
					DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
					if (selectedNode != null)
					{
						JTreeNode node = (JTreeNode) selectedNode.getUserObject();
						IRPModelElement element = node.getElement();

						if (element instanceof IRPStatechart)
						{
							IRPStatechart statechart = (IRPStatechart) element;
							statechart.getStatechartDiagram().openDiagram();

						}
						else
						{
							element.locateInBrowser();
							// element.openFeaturesDialog(0);
						}

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
		dialog.setTitle("SVN Diff for " + aSelected.getMetaClass() + " " + aSelected.getName());

		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		// Dialog-Layout festlegen
		dialog.setLayout(new BorderLayout());
		dialog.add(scrollPane, BorderLayout.CENTER);

		// Gr��e des Fensters festlegen
		dialog.setSize(600, 400); // Breite: 600px, H�he: 400px
		dialog.setLocationRelativeTo(null); // Fenster zentrieren

		// Dialog anzeigen

		dialog.setVisible(true);

	}

	public List<IRPModelElement> diffmerge(IRPModelElement aSelected, int aRevision, int aRevisionSource,
			boolean aReport, boolean addToFavorites)
	{

		List<IRPModelElement> ret = new ArrayList<IRPModelElement>();

		mySaveUnit = aSelected.getSaveUnit();
		if (mySaveUnit == null)
		{
			return ret;
		}
		
		if(aRevision == 0)
		{
			if(mySaveUnit.isReadOnly()==0)
			{
				mySaveUnit.save(0);
			}
		}
		

		IRPModelElement unitElement = (IRPModelElement) mySaveUnit;

		if (unitElement == null)
		{
			return ret;
		}

		boolean partof = false;

		if (unitElement.equals(aSelected))
		{
			partof = true;
		}

		File directory = new File(mySaveUnit.getCurrentDirectory());
		File sbsFile = new File(directory, mySaveUnit.getFilename());
		File reportFile = null;

		File tempDir = getTempDir();

		if (aReport == true)
		{
			// check if database has the report
			if (myRevisionDatabase != null)
			{
				try
				{
					if (myRevisionDatabase.hasUnitRevision(mySaveUnit.getName(), aRevision))
					{

						List<String> guids = myRevisionDatabase.getGUIDsByRevision(mySaveUnit.getName(), aRevision);

						IRPProject project = aSelected.getProject();

						for (String guid : guids)
						{

							String selectedGuid = aSelected.getGUID();

							/*
							 * 
							 * if (myRevisionDatabase.isParentOf(selectedGuid, guid)) { IRPModelElement
							 * element = project.findElementByGUID(guid); if (element != null) {
							 * ret.add(element); } }
							 */

							IRPModelElement element = project.findElementByGUID(guid);
							if (element != null)
							{
								if (partof == true)
								{

									ret.add(element);

								}
								else if (RhapsodyHelper.isPartOf(aSelected, element))
								{

									ret.add(element);

								}
							}

						}

						return ret;

					}
				}
				catch(SQLException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			trace("Load from Report!");

			String reportFileName = "report_" + mySaveUnit.getFullPathName() + "_" + aRevision + "_" + aRevisionSource
					+ ".txt";
			reportFileName = reportFileName.replace("::", "_");

			// String reportFileName = "diffmergeReport.txt";

			reportFile = new File(tempDir, reportFileName);

			/*
			 * 
			 * if (reportFile.exists() == true) { ret = parseReport(reportFile, aSelected,
			 * addToFavorites); addToDatabase(aRevision, ret); reportFile.delete(); return
			 * ret; }
			 */
		}

		if (aRevisionSource >= 0)
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

			trace("Diffmerge: " + diffMergeExe.toString() + " " + sbsFile.toString() + " " + headFile.toString());

			if (aReport == true)
			{

				while (reportFile.createNewFile() == false)
				{
					boolean isDeleted = reportFile.delete();
					if (isDeleted == false)
					{
						trace("Could not delete existing report file: " + reportFile.toString());
						break;
					}
				}
				
				
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
			process.waitFor();

			// InputStream inputStream = process.getInputStream();
			// BufferedReader reader = new BufferedReader(new
			// InputStreamReader(inputStream));

			if (reportFile != null)
			{
				if (reportFile.exists() == true)
				{

					trace("Parse Report: " + reportFile.toString());
					ret = parseReport(reportFile, aSelected, addToFavorites);

					addToDatabase(aRevision, ret);

					boolean isDeleted = reportFile.delete();
					if (isDeleted == false)
					{
						trace("Could not delete report file: " + reportFile.toString());
					}

				}
			}

		}
		catch(Exception e)
		{
			trace(e.toString());
		}

		return ret;

	}

	private void addToDatabase(int aRevision, List<IRPModelElement> aModelelements)
	{
		if(aRevision==0)
		{
			trace("Local changes, do not add to database");
			return;
		}
		trace("Add elements to database: " + aRevision);
		if (myRevisionDatabase != null)
		{
			try
			{
				if (myRevisionDatabase.addUnitRevision(mySaveUnit.getName(), aRevision) == false)
				{
					trace("Unknown revision in database: " + aRevision);
					return;
				}

				for (IRPModelElement element : aModelelements)
				{

					boolean newGuid = myRevisionDatabase.addGUID(element.getGUID(), mySaveUnit.getName());
					myRevisionDatabase.addRevisionToGUID(element.getGUID(), aRevision);

					/*
					 * if(newGuid) { resolveOwnership(element); } else {
					 * trace("Element already exists in database: " + element.getName()); }
					 */
				}

			}
			catch(SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void resolveOwnership(IRPModelElement aModelElement)
	{

		try
		{
			if (myRevisionDatabase == null)
			{
				return;
			}

			IRPModelElement unit = aModelElement.getSaveUnit();
			if (unit == null)
			{
				return;
			}

			if (myRevisionDatabase == null)
			{
				return;
			}

			// get all guids with the same unit
			List<String> guids = null;

			guids = myRevisionDatabase.getGUIDsByUnit(unit.getName());

			if (guids == null)
			{
				return;
			}

			String aGuid = aModelElement.getGUID();

			trace("Resolve ownership for: " + aModelElement.getName() + " elements: " + guids.size());

			for (String guid : guids)
			{
				if (myRevisionDatabase.isParentOf(aGuid, guid))
				{
					continue;
				}
				if (myRevisionDatabase.isParentOf(guid, aGuid))
				{
					continue;
				}

				IRPModelElement element = aModelElement.getProject().findElementByGUID(guid);
				if (element != null)
				{
					if (RhapsodyHelper.isPartOf(aModelElement, element))
					{
						myRevisionDatabase.addChildtoGuid(aGuid, guid);
					}
					else if (RhapsodyHelper.isPartOf(element, aModelElement))
					{
						myRevisionDatabase.addChildtoGuid(guid, aGuid);
					}

				}
			}
		}
		catch(SQLException e)
		{
			trace(e.toString());
			e.printStackTrace();
		}

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
		String unitName = unit.getFullPathName();
		unitName = unitName.replace("::", "_");

		File sbsTempDir = getSBSTempDir();

		tempFile = new File(sbsTempDir, unitName + "_" + Integer.toString(revision) + ".sbs");
		if (revision == 0)
		{
			// local changes
			if (tempFile.exists() == false)
			{

				File currentFileAbsolute = new File(currentDirectory, fileName).getAbsoluteFile();
				
				
				if (currentFileAbsolute.exists() == false)
				{
					trace("Current file does not exist: " + currentFileAbsolute.toString());
					return null;
				}
				
				try
				{
					Files.copy(currentFileAbsolute.toPath(), tempFile.toPath());
					trace("Local version copied: " + tempFile.toString());
				}
				catch(IOException e)
				{
					trace(e.toString());
					trace("File: " + currentFile.toPath().toString());
					trace("local directory: " + currentDirectory.toString());
					e.printStackTrace();
				}
			}
		}
		else
		{

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
			// trace("Current directory: " + processBuilder.directory().toString());

			try
			{

				// tempFile = new File(sbsTempDir, unitName + "_" + Integer.toString(revision) +
				// ".sbs");

				// tempFile = File.createTempFile(unitName, ".sbs", tempDir);

				if (tempFile.exists() == false)
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
			catch(Exception e)
			{
				trace(e.toString());
			}
		}

		return tempFile;
	}

	public static LocalDateTime parseIsoDateTime(String dateString)
	{
		DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
		LocalDateTime dateTime = LocalDateTime.parse(dateString, formatter);
		return dateTime;
	}

	private static String getFirstLine(String str)
	{
		// Teilt den String an jedem Zeilenumbruch und gibt das erste Element zur�ck
		String[] lines = str.split("\\R", 2); // "\\R" ist ein Zeilenumbruch-Metazeichen
		return lines.length > 0 ? lines[0] : null; // Pr�ft, ob �berhaupt eine Zeile vorhanden ist
	}

	public List<Map<String, Object>> readHistoryAsMaps(IRPModelElement aSelected, int aLimit)
	{
		List<logRow> rows = readHistory(aSelected, aLimit, false, 0, 0);
		if (rows == null)
		{
			return null;
		}
		List<Map<String, Object>> result = new ArrayList<>();
		for (logRow row : rows)
		{
			Map<String, Object> entry = new LinkedHashMap<>();
			entry.put("revision", row.getRevision());
			entry.put("author", row.getAuthor());
			entry.put("date", row.getDateTime());
			entry.put("message", row.getMessage());
			result.add(entry);
		}
		return result;
	}

	public List<logRow> readHistory(IRPModelElement aSelected, int aLimit, boolean aIsMonth, int aRevisionFrom,
			int aRevisionTo)
	{
		try
		{

			IRPUnit unit = aSelected.getSaveUnit();

			if (unit == null)
			{
				return null;
			}

			String fileName = unit.getFilename();
			
			if(fileName.endsWith(".sbs")==false)
			{
				trace("File is not a .sbs file: " + fileName);
				fileName = fileName.substring(0, fileName.lastIndexOf('.')) + ".sbs";
			}
			
			
			String directory = unit.getCurrentDirectory();

			File currentFile = new File(fileName);
			File currentDirectory = new File(directory);

			// svn log --limit 10 --xml
			ProcessBuilder processBuilder = null;

			/// check for local changes
			/// svn status PFE.sbs
			///

			processBuilder = new ProcessBuilder("svn", "status", fileName);
			processBuilder.directory(currentDirectory);
			Process statusProcess = processBuilder.start();
			InputStream statusInputStream = statusProcess.getInputStream();
			BufferedReader statusReader = new BufferedReader(new InputStreamReader(statusInputStream));
			statusProcess.waitFor(1000, TimeUnit.MILLISECONDS);
			boolean hasLocalChanges = false;
			String statusLine;
			while ((statusLine = statusReader.readLine()) != null)
			{
				
				if (statusLine.startsWith("M") || statusLine.startsWith("A") || statusLine.startsWith("D"))
				{
					hasLocalChanges = true;
					break;
				}
			}

			if (aLimit > 0)
			{
				if (aIsMonth == true)
				{

					LocalDate currentDate = LocalDate.now();
					LocalDate startDate = currentDate.minusMonths(aLimit);
					currentDate = currentDate.plusDays(1);

					String currentDateString = currentDate.format(DateTimeFormatter.ISO_DATE);
					String startDateString = startDate.format(DateTimeFormatter.ISO_DATE);

					String dateString = "{" + currentDateString + "}:{" + startDateString + "}";

					// svn log -r {2022-12-23}:{2024-12-23} --xml PFE.sbs

					processBuilder = new ProcessBuilder("svn", "log", "-r", dateString, "--xml", fileName);

				}
				else if (aRevisionFrom > 0)
				{
					// svn log --limit 10 --xml -r 1234 PFE.sbs

					processBuilder = new ProcessBuilder("svn", "log", "-r",
							Integer.toString(aRevisionTo) + ":" + Integer.toString(aRevisionFrom), "--limit",
							Integer.toString(aLimit), "--xml", fileName);

				}
				else
				{
					processBuilder = new ProcessBuilder("svn", "log", "--limit", Integer.toString(aLimit), "--xml",
							fileName);
				}

			}
			else
			{
				processBuilder = new ProcessBuilder("svn", "log", "--xml", fileName);
			}

			trace("Log Command: " + processBuilder.command().toString());
			trace("Log Directory: " + currentDirectory.toString());

			processBuilder.directory(currentDirectory);

			Process process = processBuilder.start();
			InputStream inputStream = process.getInputStream();
			InputStream errorStream = process.getErrorStream();

			process.waitFor(2000, TimeUnit.MILLISECONDS);

			if (errorStream.available() > 0)
			{
				BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
				StringBuilder errorOutput = new StringBuilder();
				String line;
				while ((line = errorReader.readLine()) != null)
				{
					errorOutput.append(line).append("\n");
				}
				trace("Error Output: " + errorOutput.toString());
			}
//			if(aRevision > 0)
//			{
//				if (inputStream.available() > 0)
//				{
//					BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream));
//					StringBuilder inputOutput = new StringBuilder();
//					String line;
//					while ((line = inputReader.readLine()) != null)
//					{
//						inputOutput.append("XML ").append(line).append("\n");
//					}
//					trace("Input Output: " + inputOutput.toString());
//				}
//			}

			List<logRow> ret = new ArrayList<logRow>();

			if (hasLocalChanges)
			{
				logRow localChangeRow = new logRow("0", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME), "",
						"There are local changes.");
				ret.add(localChangeRow);
			}

			ret.addAll(parseLog(inputStream));

			myLogMap.put(unit.getGUID(), ret);

			return ret;

		}
		catch(Exception e)
		{
			trace(e.toString());
			e.printStackTrace();
		}

		return new ArrayList<logRow>();
	}

	private List<logRow> parseLog(InputStream inputStream)
			throws ParserConfigurationException, SAXException, IOException
	{
		List<logRow> ret = new ArrayList<logRow>();

		if (inputStream == null)
		{
			return ret;
		}

		trace("ParseLog InputStream Size: " + inputStream.available());

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

		if (inputStream.available() <= 0)
		{
			return ret;
		}

		Document doc = dBuilder.parse(inputStream);

		// Wurzelelement bekommen
		Element root = doc.getDocumentElement();

		// Knotenliste der "Changes" bekommen
		NodeList logEntries = root.getElementsByTagName("logentry");
		trace("Parse Log entries: " + logEntries.getLength());

		if (myRevisionDatabase != null)
		{
			myRevisionDatabase.reopenConnection();
		}

		for (int i = 0; i < logEntries.getLength(); i++)
		{
			Node logentry = logEntries.item(i);
			if (logentry.getNodeType() == Node.ELEMENT_NODE)
			{
				Element logentryElement = (Element) logentry;
				String revision = logentryElement.getAttribute("revision");

				String dateISO = logentryElement.getElementsByTagName("date").item(0).getTextContent();
				LocalDateTime date = parseIsoDateTime(dateISO);
				String author = logentryElement.getElementsByTagName("author").item(0).getTextContent();
				String message = logentryElement.getElementsByTagName("msg").item(0).getTextContent();

				// trace("Revision: " + revision + " Author: " + author + " date: "
				// + date.format(DateTimeFormatter.ISO_DATE) + " Msg: " +
				// getFirstLine(message));

				logRow row = new logRow(revision, dateISO, author, message);

				ret.add(row);

				// add Revison to database
				if (myRevisionDatabase != null)
				{
					try
					{

						String messageShort = getFirstLine(message);

						myRevisionDatabase.addRevision(Integer.parseInt(revision), author, dateISO, messageShort,
								row.getJiraIssue());

					}
					catch(SQLException e)
					{
						trace(e.toString());
						e.printStackTrace();
					}
				}

			}
		}

		return ret;
	}

	public void showTortoiseLog(IRPModelElement aSelected)
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
			processBuilder.start();

		}
		catch(Exception e)
		{
			trace(e.getMessage());
		}

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

		List<logRow> changesUnit = readHistory(aSelected, 200, false, 0, 0);

		// create a dialog which shows the changes as the tortoise log does
		JDialog dialog = new JDialog();
		dialog.setTitle("SVN Log for " + unit.getName());
		dialog.setSize(800, 600);
		dialog.setLayout(new BorderLayout());
		dialog.setLocationRelativeTo(null);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		// set icon of the dialog
		ImageIcon icon = new ImageIcon(unit.getIconFileName());
		dialog.setIconImage(icon.getImage());

		// create a table model
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn("Revision");
		model.addColumn("Author");
		model.addColumn("Date");
		model.addColumn("Jira Issue");

		// add the changes to the table model
		for (logRow row : changesUnit)
		{
			model.addRow(new Object[]
			{ row.getRevision(), row.getAuthor(), row.getDateTime(), row.getJiraIssue() });
		}

		// create a table
		JTable table = new JTable(model);

		// add header to table
		JTableHeader header = table.getTableHeader();
		dialog.add(header, BorderLayout.NORTH);

		// table collumn width for the revision column is set to 50
		TableColumn column = table.getColumnModel().getColumn(0);
		column.setMaxWidth(50);

		column = table.getColumnModel().getColumn(1);
		column.setMaxWidth(70);

		column = table.getColumnModel().getColumn(2);
		column.setMinWidth(120);
		column.setMaxWidth(120);

		// set header bold
		Font headerFont = header.getFont();
		header.setFont(headerFont.deriveFont(Font.BOLD));

		// set the table to be not editable
		table.setDefaultEditor(Object.class, null);

		// add the table to a scrollpane
		JScrollPane scrollPane = new JScrollPane(table);

		// add the scrollpane to the dialog
		dialog.add(scrollPane, BorderLayout.CENTER);

		// text in column 3 looks like a hyperlink
		table.getColumnModel().getColumn(3).setCellRenderer(new JiraIssueRenderer());

		// add dropdown menu to the table
		JPopupMenu popupMenu = new JPopupMenu();
		JMenuItem showTree = new JMenuItem("Show Diff Tree");
		popupMenu.add(showTree);
		JMenuItem showStatistics = new JMenuItem("Show Statistics");
		popupMenu.add(showStatistics);
		JMenuItem showHistory = new JMenuItem("Show History");
		popupMenu.add(showHistory);
		JMenuItem showBlame = new JMenuItem("Show Blame");
		popupMenu.add(showBlame);

		if (aSelected instanceof IRPOperation == false)
		{
			showBlame.setEnabled(false);
		}

		showTree.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{

				int row = table.getSelectedRow();

				if (row >= 0)
				{
					int revision = (int) table.getValueAt(row, 0);

					diffTree(aSelected, -1, revision);
				}
			}
		});

		showStatistics.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				int row = table.getSelectedRow();

				if (row >= 0)
				{

					showChangeStatistic(aSelected, row, false, 0, 0);

				}
			}
		});

		showHistory.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				int row = table.getSelectedRow();

				if (row >= 0)
				{
					showChangeList(aSelected, row, false, 0, 0);
				}
			}
		});

		showBlame.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				int i = table.getSelectedRow();

				if (aSelected instanceof IRPOperation == false)
				{
					return;
				}

				IRPOperation operation = (IRPOperation) aSelected;

				blame(operation, i, false);

			}
		});

		// Add mouse listener to the table
		table.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				if (e.isPopupTrigger())
				{
					showPopup(e);
				}
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				if (e.isPopupTrigger())
				{
					showPopup(e);
				}
			}

			private void showPopup(MouseEvent e)
			{
				int row = table.rowAtPoint(e.getPoint());
				int column = table.columnAtPoint(e.getPoint());

				table.setRowSelectionInterval(row, row);
				popupMenu.show(e.getComponent(), e.getX(), e.getY());

			}
		});

		// when click on colmn 3 open the jira issue

		table.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				// only when click on column 3
				if (table.columnAtPoint(e.getPoint()) == 3)
				{
					int row = table.getSelectedRow();
					logRow log = changesUnit.get(row);
					log.openJiraUrl();
				}

			}
		});

		table.setComponentPopupMenu(popupMenu);

		// show the dialog
		dialog.setVisible(true);

	}

	class JiraIssueRenderer extends DefaultTableCellRenderer
	{
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column)
		{
			JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
					column);
			label.setText("<html><u>" + value + "</u></html>");
			return label;
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
		catch(Exception e)
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
		catch(Exception e)
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

	private File getRhapsodyTempDir()
	{
		File tempDir = getTempDir();
		File rhapsodyTempDir = new File(tempDir, "rhapsody");
		if (rhapsodyTempDir.exists() == false)
		{
			rhapsodyTempDir.mkdir();
		}
		return rhapsodyTempDir;
	}

	private enum actionEnum
	{
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
							// trace(type + " " + fullPath.toString());
						}
					}

				}

			}
			
			reportReader.close();

			myReport = reportBuffer.toString();

		}
		catch(Exception e)
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
		List<logRow> history = readHistory(aSelected, -1, false, 0, 0);

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
		catch(Exception e)
		{
			// TODO: handle exception
		}

	}

	public void compareAttributeVersion(IRPAttribute aAttribute, int aRevisionA, int aRevisionB)
	{

		IRPApplication tempApp = null;

		tempApp = createTempRhapsodyApp();

		if (tempApp == null)
		{
			trace("TempApp not created");
			return;
		}

		IRPProject defaultProject = aAttribute.getProject();

		File fileA = getVersion(aAttribute, aRevisionA);

		trace("File A: " + fileA.toString());

		File fileB = null;

		if (aRevisionB == -1)
		{
			IRPUnit unit = aAttribute.getSaveUnit();
			fileB = new File(unit.getCurrentDirectory(), unit.getFilename());

		}
		else
		{
			fileB = getVersion(aAttribute, aRevisionB);
		}

		trace("File B: " + fileB.toString());

		File rhapsodyTempDir = getRhapsodyTempDir();

		if (fileA.exists() == false)
		{
			trace("File A does not exist");
			tempApp.quit();
			return;
		}

		if (fileB.exists() == false)
		{
			trace("File B does not exist");
			tempApp.quit();
			return;
		}

		String guid = aAttribute.getGUID();

		IRPProject svnProjectA = newSvnProject(tempApp, fileA, rhapsodyTempDir, "ProjectA");

		IRPModelElement elementA = svnProjectA.findElementByGUID(guid);

		if (elementA == null)
		{
			trace("Element A not found");
			tempApp.quit();
			return;

		}

		IRPProject svnProjectB = newSvnProject(tempApp, fileB, rhapsodyTempDir, "ProjectB");
		IRPModelElement elementB = svnProjectB.findElementByGUID(guid);

		if (elementB == null)
		{
			trace("Element B not found");
			tempApp.quit();
			return;
		}

		IRPAttribute attributeA = (IRPAttribute) elementA;
		IRPAttribute attributeB = (IRPAttribute) elementB;

		showAttributeDiff(attributeA, attributeB, aRevisionA, aRevisionB);

		defaultProject.becomeActiveProject();
		svnProjectA.close();
		svnProjectB.close();

		tempApp.quit();

	}
	
	public String getJsonFromRevision(IRPModelElement aModelElement, int aRevision)
	{
		
		IRPApplication tempApp = null;
		tempApp = createTempRhapsodyApp();
		if (tempApp == null)
		{
			trace("TempApp not created");
			return null;
		}
		
		IRPProject defaultProject = aModelElement.getProject();
		File file = getVersion(aModelElement, aRevision);
		trace("File: " + file.toString());
		if(file.exists() == false)
		{
			trace("File does not exist");
			tempApp.quit();
			return null;
		}
		
		String guid = aModelElement.getGUID();
		File rhapsodyTempDir = getRhapsodyTempDir();
		IRPProject svnProject = newSvnProject(tempApp, file, rhapsodyTempDir, "ProjectA");
		IRPModelElement element = svnProject.findElementByGUID(guid);
		
		if (element == null)
		{
			trace("Element not found");
			svnProject.close();
			tempApp.quit();
			return null;
		}
		
		JsonModelTester tester = JsonModelTester.Instance(tempApp, this::trace);

		String jsonModel = tester.getJson(element);
		
		svnProject.close();
		
		tempApp.quit();
		
		
		
		return jsonModel;
	}
	
	public void compareJsonOfRevisions(IRPModelElement aModelElement, int aRevisionA, int aRevisionB)
	{
		
		if(aRevisionA == aRevisionB)
		{
			trace("Both revisions are the same");
			return;
		}
		
		trace("Getting JSON for revision A: " + aRevisionA);
		String jsonA = getJsonFromRevision(aModelElement, aRevisionA);
		
		File tempFileA = new File(getTempDir(), "revisionA.json");
		try
		{
			Files.write(tempFileA.toPath(), jsonA.getBytes());
		}
		catch(Exception e)
		{
			trace(e.getMessage());
			return;
		}
				
		trace("Getting JSON for revision B: " + aRevisionB);
		String jsonB = getJsonFromRevision(aModelElement, aRevisionB);
		
		File tempFileB = new File(getTempDir(), "revisionB.json");
		try
		{
			Files.write(tempFileB.toPath(), jsonB.getBytes());
		}
		catch(Exception e)
		{
			trace(e.getMessage());
			return;
		}
		
		File ccrcFile = new File(System.getenv("OMROOT"), "etc\\ccrc_diff\\win32\\ccrc_cleardiffmrg.exe");
		if (ccrcFile.exists() == false)
		{
			trace("ClearCase CCRC DiffMerge executable not found at: " + ccrcFile.toString());
			return;
		}

		ProcessBuilder processBuilder = new ProcessBuilder(ccrcFile.getAbsolutePath(),
				tempFileA.getAbsolutePath(), tempFileB.getAbsolutePath());

		try
		{
			Process process = processBuilder.start();
		}
		catch(IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@SuppressWarnings("unchecked")
	public void compareOperationVersions(IRPModelElement aModelElement, int aRevisionA, int aRevisionB)
	{

		IRPApplication tempApp = null;

		tempApp = createTempRhapsodyApp();

		if (tempApp == null)
		{
			trace("TempApp not created");
			return;
		}

		IRPProject defaultProject = aModelElement.getProject();

		File fileA = getVersion(aModelElement, aRevisionA);

		trace("File A: " + fileA.toString());

		File fileB = null;

		if (aRevisionB == -1)
		{
			IRPUnit unit = aModelElement.getSaveUnit();
			fileB = new File(unit.getCurrentDirectory(), unit.getFilename());

		}
		else
		{
			fileB = getVersion(aModelElement, aRevisionB);
		}

		trace("File B: " + fileB.toString());

		File rhapsodyTempDir = getRhapsodyTempDir();

		if (fileA.exists() == false)
		{
			trace("File A does not exist");
			tempApp.quit();
			return;
		}

		if (fileB.exists() == false)
		{
			trace("File B does not exist");
			tempApp.quit();
			return;
		}

		String guid = aModelElement.getGUID();

		IRPProject svnProjectA = newSvnProject(tempApp, fileA, rhapsodyTempDir, "ProjectA");

		IRPModelElement elementA = svnProjectA.findElementByGUID(guid);

		IRPOperation operationA = null;

		if (elementA == null)
		{
			trace("Element A not found");

		}
		else
		{
			operationA = (IRPOperation) elementA;
		}

		IRPProject svnProjectB = newSvnProject(tempApp, fileB, rhapsodyTempDir, "ProjectB");
		IRPModelElement elementB = svnProjectB.findElementByGUID(guid);

		IRPOperation operationB = null;

		if (elementB == null)
		{
			trace("Element B not found");
		}
		else
		{
			operationB = (IRPOperation) elementB;
		}

		if (aModelElement instanceof IRPOperation == false)
		{
			trace("Selected element is not an Operation");
			tempApp.quit();
			return;
		}

		IRPOperation aOperation = (IRPOperation) aModelElement;

		OperationDiff oDiff = new OperationDiff(guid);
		oDiff.setOperationA(operationA, aRevisionA);
		oDiff.setOperationB(operationB, aRevisionB);

		defaultProject.becomeActiveProject();
		svnProjectA.close();
		svnProjectB.close();

		tempApp.quit();

		if (oDiff.showClearCaseDiff() == false)
		{
			viewHtml(aOperation, oDiff);
		}

	}

	private void viewHtml(IRPOperation aOperation, OperationDiff oDiff)
	{
		String htmlOutput = oDiff.generateHTMLOutput();

		JDialog dialog = new JDialog();
		ImageIcon icon = new ImageIcon(aOperation.getIconFileName());
		dialog.setIconImage(icon.getImage());
		dialog.setTitle("Diff of Operation " + aOperation.getName());

		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();

		int width = screenSize.width / 4 * 3;
		int height = screenSize.height / 4 * 3;

		dialog.setSize(width, height);
		dialog.setLocationRelativeTo(null);

		JEditorPane editorPane = new JEditorPane();
		editorPane.setEditable(false);

		HTMLEditorKit kit = new HTMLEditorKit();
		editorPane.setEditorKit(kit);

		StyleSheet styleSheet = kit.getStyleSheet();

		styleSheet.addRule("body {font-family:monospace; font-size: 9px; color: #000000; background-color: #ffffff }");
		styleSheet.addRule("th {background-color: gray; }");
		// styleSheet.addRule("td {background-color: #dddddd;}");
		styleSheet.addRule("span {background-color: #ffdd66; }");
		// styleSheet.addRule(".editNewInline { white-space: pre; }");

		styleSheet.addRule("table {" + "width: 100%;" + "border-style: solid;" + "border-collapse: collapse;"
				+ "border-spacing: 0;" + "}");

		styleSheet.addRule("td {" + "background-color: #dddddd;" + "border: 1px solid gray;" + "padding: 1px;"
				+ "line-height: 5px;" + "}");

		javax.swing.text.Document doc = kit.createDefaultDocument();

		editorPane.setDocument(doc);
		editorPane.setAutoscrolls(true);
		editorPane.setEditable(false);
		editorPane.setText(htmlOutput);

		dialog.add(new JScrollPane(editorPane), BorderLayout.CENTER);
		dialog.setVisible(true);
	}

	private IRPApplication createTempRhapsodyApp()
	{

		IRPApplication tempApp = null;
		File rhapsodyShareFile = new File(System.getenv("OMROOT"));

		if (rhapsodyShareFile.exists() == false)
		{
			trace("Rhapsody Share file does not exist");
			return null;
		}

		File rhapsodyFile = rhapsodyShareFile.getParentFile();

		if (rhapsodyFile.exists() == false)
		{
			trace("Rhapsody file does not exist");
			return null;
		}

		ProcessBuilder processBuilder = new ProcessBuilder(rhapsodyFile.getAbsolutePath() + "\\rhapsody.exe",
				"-hiddenui");

		try
		{
			Process p = processBuilder.start();
		}
		catch(IOException e)
		{
			trace(e.getMessage());
			return null;
		}

		// wait for rhapsody to start
		try
		{
			Thread.sleep(1000);
		}
		catch(InterruptedException e)
		{
			trace(e.getMessage());
			return null;
		}

		List<String> idList = RhapsodyAppServer.getActiveRhapsodyApplicationIDList();

		for (String id : idList)
		{
			IRPApplication app = RhapsodyAppServer.getActiveRhapsodyApplicationByID(id);
			if (app.activeProject() == null)
			{
				tempApp = app;// no project loaded..
			}
		}

		if (tempApp == null)
		{
			trace("No empty Rhapsody Application found");
			return null;
		}

		tempApp.setHiddenUI(true);
		return tempApp;
	}

	public void blame(IRPOperation aOperation, int aLimit, boolean aIsMonth)
	{
		blame(aOperation, aLimit, aIsMonth, 0);
	}

	public void blame(IRPOperation aOperation, int aLimit, boolean aIsMonth, int aStartsWith)
	{
		trace("Blame Limit: " + aLimit + " IsMonth: " + aIsMonth + " StartsWith: " + aStartsWith);

		if (aOperation == null)
		{
			trace("Operation is null");
		}
		if (aOperation == null)
		{
			trace("Operation is null");
			return;
		}

		IRPProject defaultProject = aOperation.getProject();

		IRPApplication tempApp = createTempRhapsodyApp();

		if (tempApp == null)
		{
			trace("TempApp not created");
			return;
		}

		String guid = aOperation.getGUID();

		File rhapsodyTempDir = getRhapsodyTempDir();

		if (rhapsodyTempDir.exists() == false)
		{
			trace("Rhapsody Temp Dir does not exist");
			return;
		}

		List<logRow> history = readHistory(aOperation, aLimit, aIsMonth, 0, 0);
		List<logRow> changes = new ArrayList<logRow>();

		logRow lastRow = null;

		trace("Check history Size: " + history.size());

		for (logRow row : history)
		{
			int revision = row.getRevision();

			if (lastRow != null)
			{
				int lastRevision = lastRow.getRevision();
				// trace("Check -- Revision: " + revision + " LastRevision: " + lastRevision);

				List<IRPModelElement> changedElements = diffmerge(aOperation, lastRevision, revision, true, false);

				List<ChangedElement> changedElementsList = new ArrayList<ChangedElement>();
				for (IRPModelElement element : changedElements)
				{
					ChangedElement changedElement = new ChangedElement(element);
					changedElementsList.add(changedElement);
				}

				if (changedElementsList.size() > 0)
				{

					changes.add(lastRow);
					trace("Revision: " + lastRevision + " has changes");
				}
				else
				{
					// trace("Revision: " + lastRevision + " has no changes");
				}
			}

			lastRow = row;
		}

		/*
		 * 
		 * List<logRow> changesUnit = readHistory(aSelected, aLimitMonths, true);
		 * 
		 * logRow lastRow = null; for (logRow row : changesUnit) { int revision =
		 * row.getRevision(); if (lastRow != null) { int lastRevision =
		 * lastRow.getRevision();
		 * 
		 * List<IRPModelElement> changedElements = diffmerge(aSelected, lastRevision,
		 * revision, true, false);
		 * 
		 * for (IRPModelElement element : changedElements) { if
		 * (changedElementsMap.containsKey(element)) { changedElementsMap.put(element,
		 * changedElementsMap.get(element) + 1); } else {
		 * changedElementsMap.put(element, 1); } } } lastRow = row;
		 * 
		 * }
		 * 
		 */

		List<OperationItem> operationList = new ArrayList<OperationItem>();

		int index = 0;

		for (logRow row : changes)
		{
			File file = getVersion(aOperation, row.getRevision());
			if (file.exists() == false)
			{
				trace("File Revision " + row.getRevision() + " does not exist");
				continue;
			}

			IRPProject svnProject = newSvnProject(tempApp, file, rhapsodyTempDir, "Project" + row.getRevision());

			IRPModelElement element = svnProject.findElementByGUID(guid);

			IRPOperation operation = null;

			if (element == null)
			{
				trace("Element not found");

			}
			else
			{
				operation = (IRPOperation) element;
				OperationItem item = new OperationItem(operation, row, index + 1);
				operationList.add(item);
			}

			svnProject.close();
			index++;

		}

		defaultProject.becomeActiveProject();
		tempApp.quit();

		logRow currentRow = new logRow("-1", LocalDateTime.now().toString(), "", "");

		OperationItem currentItem = new OperationItem(aOperation, currentRow, 0);

		if (aStartsWith > 0)
		{
			currentItem = operationList.get(aStartsWith - 1);
			currentItem.setIndex(0);

			List<OperationItem> operationListTemp = new ArrayList<OperationItem>();
			int newIndex = 1;
			for (int i = aStartsWith; i < operationList.size(); i++, newIndex++)
			{
				OperationItem item = operationList.get(i);
				item.setIndex(newIndex);
				operationListTemp.add(item);
			}
			operationList = operationListTemp;
		}

		List<String> sourceLinesActual = currentItem.getBody();
		List<blameLine> blameLines = new ArrayList<blameLine>();

		for (String line : sourceLinesActual)
		{
			// replace Tabs with spaces
			line = line.replaceAll("\t", "    ");

			blameLine bLine = new blameLine(line);
			blameLines.add(bLine);
		}

		DiffRowGenerator.Builder generatorBilder = DiffRowGenerator.create();

		// generatorBilder.showInlineDiffs(false);
		generatorBilder.reportLinesUnchanged(true);
		generatorBilder.ignoreWhiteSpaces(true);
		DiffRowGenerator generator = generatorBilder.build();

		for (OperationItem item : operationList)
		{
			logRow row = item.getLogRow();
			List<String> sourceLinesVersioned = item.getBody();
			try
			{
				List<DiffRow> diffRows = generator.generateDiffRows(sourceLinesVersioned, sourceLinesActual);

				for (blameLine bLine : blameLines)
				{

					int lineNumber = 0;

					for (; lineNumber < diffRows.size(); lineNumber++)
					{
						DiffRow diffRow = diffRows.get(lineNumber);
						if (bLine.getLine().replaceAll("\\s+", "").equals(diffRow.getOldLine().replaceAll("\\s+", "")))
						{

							bLine.setOperationItem(item);
							break;

						}
					}
				}

			}
			catch(Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		for (blameLine bLine : blameLines)
		{
			if (bLine.getOperationItem() == null)
			{
				bLine.setOperationItem(currentItem);
			}
		}

		/*
		 * 
		 * for (blameLine bLine : blameLines) { logRow row = bLine.getLogRow(); if (row
		 * != null) { trace(row.getRevision() + " " + row.getAuthor() + " " +
		 * bLine.getLine()); } else { trace(" " + bLine.getLine()); } }
		 * 
		 */

		// dialog, where the blame is shown

		// set currentItem as first item

		List<OperationItem> newOperationList = new ArrayList<OperationItem>();
		newOperationList.add(currentItem);
		newOperationList.addAll(operationList);

		showBlameDialog(blameLines, aOperation, newOperationList, aStartsWith);

		/*
		 * 
		 * OperationItem current = operationList.get(0);
		 * 
		 * Annotate<logRow> annotate = new Annotate<logRow>(current.getLogRow(),
		 * current.getBody());
		 * 
		 * for (int i = 1; i < operationList.size(); i++) { OperationItem item =
		 * operationList.get(i); try { annotate.addRevision(item.getLogRow(),
		 * item.getBody()); } catch (DiffException e) { // TODO Auto-generated catch
		 * block e.printStackTrace(); }
		 * 
		 * }
		 * 
		 * List<logRow> revisions = annotate.getAnnotatedRevisions(); int[] lineNumbers
		 * = annotate.getAnnotatedLineNumbers();
		 * 
		 * 
		 * List<String> currentSource = current.getBody();
		 * 
		 * for (int i = 0; i < revisions.size(); i++) {
		 * 
		 * logRow row = revisions.get(i); int lineNumber = lineNumbers[i];
		 * 
		 * if (row == null) { trace(" no Change :" + currentSource.get(i) ); } else {
		 * trace(row.getRevision() + " " + row.getAuthor() + ": " + currentSource.get(i)
		 * ); }
		 * 
		 * }
		 * 
		 */

	}

	public void showBlameDialog(List<blameLine> blameLines, IRPOperation aOperation, List<OperationItem> aOperationList,
			int aStartsWith)
	{
		String[] columnNames =
		{ "Revision", "Author", "Date", "Line", "Source" };
		Object[][] data = new Object[blameLines.size()][5];

		for (int i = 0; i < blameLines.size(); i++)
		{
			blameLine line = blameLines.get(i);
			logRow row = line.getLogRow();
			if (row == null)
			{
				data[i][0] = -1;
				data[i][1] = "";
				data[i][2] = "";
				data[i][3] = i;
				data[i][4] = line.getLine();
				continue;
			}
			data[i][0] = row.getRevision();
			data[i][1] = row.getAuthor();
			data[i][2] = row.getDateTime();
			// write line as 001, 002, 003
			data[i][3] = String.format("%03d", i);

			data[i][4] = line.getLine();
		}

		DefaultTableModel tableModel = new DefaultTableModel(data, columnNames)
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		};

		CustomJTable table = new CustomJTable(tableModel, blameLines);

		table.setDefaultRenderer(Object.class, new CustomTableCellRenderer());
		table.setIntercellSpacing(new Dimension(0, 0)); // Remove lines

		// Set custom header renderer
		JTableHeader header = table.getTableHeader();
		header.setDefaultRenderer(new BoldHeaderRenderer());

		// Adjust column widths
		for (int col = 0; col < 3; col++)
		{
			TableColumn column = table.getColumnModel().getColumn(col);
			int maxWidth = 50;
			column.setWidth(maxWidth);
			column.setMaxWidth(maxWidth * 2);
			column.setMinWidth(maxWidth);
		}

		TableColumn column = table.getColumnModel().getColumn(3);
		int maxWidth = 12;
		column.setWidth(maxWidth);
		column.setMaxWidth(maxWidth * 2);
		column.setMinWidth(maxWidth);

		// Create the popup menu
		JPopupMenu popupMenu = new JPopupMenu();
		JMenuItem compareItem = new JMenuItem("compare");
		JMenuItem jiraItem = new JMenuItem("jira");
		JMenuItem diffmergeItem = new JMenuItem("diffmerge");
		JMenuItem blameItem = new JMenuItem("blame from here");
		popupMenu.add(compareItem);
		popupMenu.add(jiraItem);
		popupMenu.add(blameItem);

		// Add action listeners to menu items
		compareItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{

				compareFunction(blameLines.get(table.getSelectedRow()), aOperation, aOperationList);
			}
		});

		jiraItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				blameLine line = blameLines.get(table.getSelectedRow());
				if (line != null)
				{
					logRow row = line.getLogRow();
					if (row != null)
					{
						row.openJiraUrl();
					}
				}
			}
		});

		blameItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				blameLine line = blameLines.get(table.getSelectedRow());
				if (line != null)
				{
					OperationItem item = line.getOperationItem();
					if (item != null)
					{
						trace("Blame from here: " + item.getIndex());
						blame(aOperation, 100, false, item.getIndex() + aStartsWith);
					}
				}
			}
		});

		// Add mouse listener to the table
		table.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				if (e.isPopupTrigger())
				{
					showPopup(e);
				}
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				if (e.isPopupTrigger())
				{
					showPopup(e);
				}
			}

			private void showPopup(MouseEvent e)
			{
				int row = table.rowAtPoint(e.getPoint());
				int column = table.columnAtPoint(e.getPoint());
				if (column > 1)
				{
					table.setRowSelectionInterval(row, row);
					popupMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});

		JScrollPane scrollPane = new JScrollPane(table);
		table.setFillsViewportHeight(true);

		JDialog dialog = new JDialog();

		String endDateTime = aOperationList.get(0).getLogRow().getDate()
				.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
		String startDateTime = aOperationList.get(aOperationList.size() - 1).getLogRow().getDate()
				.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));

		dialog.setTitle("Blame of " + aOperation.getName() + " from " + startDateTime + " to " + endDateTime);
		ImageIcon icon = new ImageIcon(aOperation.getIconFileName());
		dialog.setIconImage(icon.getImage());
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setLayout(new BorderLayout());
		dialog.add(scrollPane, BorderLayout.CENTER);
		dialog.setSize(1200, 800);
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
	}

	private void compareFunction(blameLine aLine, IRPOperation aOperation, List<OperationItem> aOperationList)
	{
		OperationItem item = aLine.getOperationItem();

		int index = item.getIndex();

		if (index >= aOperationList.size() - 1)
		{
			trace("No previous version found");
			return;
		}

		OperationItem previous = aOperationList.get(index + 1);

		File previousFile = null;
		try
		{
			previousFile = File.createTempFile(aOperation.getName() + "_" + previous.getLogRow().getRevision() + " _",
					".cpp");

			File versionedFile = File
					.createTempFile(aOperation.getName() + "_" + aLine.getLogRow().getRevision() + " _", ".cpp");

			FileWriter writer = new FileWriter(previousFile);
			List<String> sourceLines = previous.getBody();
			for (String line : sourceLines)
			{
				writer.write(line + "\n");
			}
			writer.close();

			writer = new FileWriter(versionedFile);
			sourceLines = item.getBody();
			for (String line : sourceLines)
			{
				writer.write(line + "\n");
			}
			writer.close();

			// starte "$OMROOT\etc\ccrc_diff\win32\ccrc_cleardiffmrg.exe" $source1 $source2
			File ccrcFile = new File(System.getenv("OMROOT"), "etc\\ccrc_diff\\win32\\ccrc_cleardiffmrg.exe");
			if (ccrcFile.exists() == false)
			{
				return;
			}

			ProcessBuilder processBuilder = new ProcessBuilder(ccrcFile.getAbsolutePath(),
					previousFile.getAbsolutePath(), versionedFile.getAbsolutePath());

			processBuilder.start();

		}
		catch(IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static class CustomJTable extends JTable
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private List<blameLine> blameLines;

		public CustomJTable(DefaultTableModel model, List<blameLine> blameLines)
		{
			super(model);
			this.blameLines = blameLines;
		}

		@Override
		public String getToolTipText(MouseEvent e)
		{
			java.awt.Point p = e.getPoint();
			int rowIndex = rowAtPoint(p);
			int colIndex = columnAtPoint(p);

			if (colIndex == 0)
			{
				logRow row = blameLines.get(rowIndex).getLogRow();
				if (row != null)
				{
					return "<html>" + row.getJiraIssue() + "</html>";
				}
				return "";
			}

			return super.getToolTipText(e);
		}
	}

	private static class CustomTableCellRenderer extends DefaultTableCellRenderer
	{
		Map<Integer, Color> rowColorMap = new HashMap<>();
		List<Color> rowColors = Arrays.asList(Color.WHITE, Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW,
				Color.ORANGE, Color.PINK, Color.CYAN);
		int colorIndex = 0;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column)
		{
			Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			int revision = (int) table.getValueAt(row, 0);

			c.setBackground(getColorForRevision(revision));

			if (column == 3)
			{
				c.setBackground(getColorForRevision(revision).brighter().darker());
			}

			if (column == 4)
			{
				c.setFont(c.getFont().deriveFont(Font.BOLD));
				c.setBackground(getColorForRevision(revision));
			}

			return c;
		}

		private Color getColorForRevision(int revision)
		{
			// Customize this method to return different colors based on the revision

			Color rowColor = rowColorMap.get(revision);

			if (rowColor == null)
			{
				rowColor = rowColors.get(colorIndex);

				int r = rowColor.getRed();
				int g = rowColor.getGreen();
				int b = rowColor.getBlue();

				// Calculate the luminance of the color

				double luminance = 0; // 0.299 * r + 0.587 * g + 0.114 * b;

				// If the luminance is too low, adjust the color to be lighter
				if (luminance < 128)
				{
					r = Math.min(r + 180, 255);
					g = Math.min(g + 180, 255);
					b = Math.min(b + 180, 255);
				}

				r = Math.max(r - 30, 0);
				g = Math.max(g - 30, 0);
				b = Math.max(b - 30, 0);

				rowColor = new Color(r, g, b);

				colorIndex++;
				if (colorIndex >= rowColors.size())
				{
					colorIndex = 0;
				}

				rowColorMap.put(revision, rowColor);
			}

			return rowColor;

		}
	}

	private static class BoldHeaderRenderer extends DefaultTableCellRenderer
	{
		public BoldHeaderRenderer()
		{
			// setHorizontalAlignment(SwingConstants.CENTER);
			setFont(getFont().deriveFont(Font.BOLD));
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column)
		{
			Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			c.setFont(c.getFont().deriveFont(Font.BOLD));
			return c;
		}
	}

	private IRPProject newSvnProject(IRPApplication aApp, File aSBSFile, File aProjectDir, String aProjectName)
	{
		aApp.createAndInsertProject(aProjectDir.getAbsolutePath(), aProjectName);

		IRPProject svnProject = aApp.activeProject();

		aApp.addToModelEx(aSBSFile.getAbsolutePath(), IRPApplication.AddToModel_Mode.AS_REFERENCE, 1, 0);

		return svnProject;
	}

	public boolean showAttributeDiff(IRPAttribute aAttributeA, IRPAttribute aAttributeB, int aRevisionA, int aRevisionB)
	{

		WriterTemplateParser parser = new WriterTemplateParser(myTraceAction);

		String attributeAString = parser.parse(aAttributeA, true);
		String attributeBString = parser.parse(aAttributeB, true);

		try
		{

			File tempFileA = File.createTempFile(aAttributeA.getName() + "_" + aRevisionA + "_", ".cpp");
			BufferedWriter writer = new BufferedWriter(new FileWriter(tempFileA));
			writer.write(attributeAString);
			writer.close();

			File tempFileB = File.createTempFile(aAttributeB.getName() + "_" + aRevisionB + "_", ".cpp");
			writer = new BufferedWriter(new FileWriter(tempFileB));
			writer.write(attributeBString);
			writer.close();

			File ccrcFile = new File(System.getenv("OMROOT"), "etc\\ccrc_diff\\win32\\ccrc_cleardiffmrg.exe");
			if (ccrcFile.exists() == false)
			{
				trace("ClearCase diff tool not found");
				return false;
			}

			ProcessBuilder processBuilder = new ProcessBuilder(ccrcFile.getAbsolutePath(), tempFileA.getAbsolutePath(),
					tempFileB.getAbsolutePath());

			Process process = processBuilder.start();
		}
		catch(IOException e)
		{
			trace(e.getMessage());
			return false;
		}

		return true;
	}

	enum DiffType
	{
		unchanged, changed, added, removed, unknown
	}

	private class blameLine
	{
		private String myline = "";

		private OperationItem myOperationItem = null;

		public blameLine(String aLine)
		{
			myline = aLine;

		}

		public void setOperationItem(OperationItem aOperationItem)
		{
			myOperationItem = aOperationItem;
		}

		public String getLine()
		{
			return myline;
		}

		public OperationItem getOperationItem()
		{
			return myOperationItem;
		}

		public logRow getLogRow()
		{
			if (myOperationItem != null)
			{
				return myOperationItem.getLogRow();
			}
			else
			{
				return null;
			}
		}
	}

	private class OperationDiff
	{

		private String myGUID = "";
		private String mySignatureA = "";
		private String mySignatureB = "";
		private String myBodyA = "";
		private String myBodyB = "";
		private String myOperationNameA = "";
		private String myOperationNameB = "";
		private int myRevisionA = -1;
		private int myRevisionB = -1;

		private DiffType myDiffType = DiffType.unknown;

		public OperationDiff(String aGUID)
		{
			myGUID = aGUID;
		}

		public DiffType getDiffType()
		{
			return myDiffType;
		}

		public String getGuid()
		{
			return myGUID;
		}

		public boolean setOperationA(IRPOperation aOperation, int aRevision)
		{
			myRevisionA = aRevision;
			myDiffType = DiffType.unknown;
			if (aOperation == null)
			{
				myDiffType = DiffType.added;
				return false;
			}
			if (aOperation.getGUID().equals(myGUID) == false)
			{
				return false;
			}

			mySignatureA = aOperation.getSignature();

			IRPClassifier retType = aOperation.getReturns();

			mySignatureA = retType.getName() + " " + mySignatureA;

			myBodyA = aOperation.getBody();

			myOperationNameA = aOperation.getName();

			checkdiff();

			return true;
		}

		public boolean setOperationB(IRPOperation aOperation, int aRevision)
		{

			myRevisionB = aRevision;
			myDiffType = DiffType.unknown;
			if (aOperation == null)
			{
				myDiffType = DiffType.removed;
				return false;
			}
			if (aOperation.getGUID().equals(myGUID) == false)
			{
				return false;
			}

			mySignatureB = aOperation.getSignature();
			IRPClassifier retType = aOperation.getReturns();
			mySignatureB = retType.getName() + " " + mySignatureB;

			myBodyB = aOperation.getBody();

			myOperationNameB = aOperation.getName();

			checkdiff();

			return true;
		}

		private void checkdiff()
		{
			if (mySignatureA == null || mySignatureA.equals(""))
			{
				myDiffType = DiffType.added;
				return;
			}

			if (mySignatureB == null || mySignatureB.equals(""))
			{
				myDiffType = DiffType.removed;
				return;
			}

			if (mySignatureA.equals(mySignatureB) == false)
			{
				myDiffType = DiffType.changed;
			}
			else if (myBodyA.equals(myBodyB) == false)
			{
				myDiffType = DiffType.changed;
			}
			else
			{
				myDiffType = DiffType.unchanged;
			}
		}

		public boolean showClearCaseDiff()
		{
			// erzeuge zwei temp Dateien

			List<String> sourceLinesA = ASTHelper.getLines(myBodyA, false);
			List<String> sourceLinesB = ASTHelper.getLines(myBodyB, false);

			File tempFileA = null;
			File tempFileB = null;
			try
			{
				tempFileA = File.createTempFile(myOperationNameA + "_" + myRevisionA + "_", ".cpp");
				BufferedWriter writer = new BufferedWriter(new FileWriter(tempFileA));
				writer.write(mySignatureA);
				writer.newLine();
				writer.write("{");
				writer.newLine();
				writer.newLine();
				// writer.write(myBodyA);
				for (String line : sourceLinesA)
				{
					writer.write("    ");
					writer.write(line);
					writer.newLine();
				}

				writer.newLine();
				writer.write("}");
				writer.close();

				tempFileB = File.createTempFile(myOperationNameB + "_" + myRevisionB + "_", ".cpp");
				writer = new BufferedWriter(new FileWriter(tempFileB));
				writer.write(mySignatureB);
				writer.newLine();
				writer.write("{");
				writer.newLine();
				writer.newLine();
				// writer.write(myBodyB);
				for (String line : sourceLinesB)
				{
					writer.write("    ");
					writer.write(line);
					writer.newLine();
				}
				writer.newLine();
				writer.write("}");
				writer.close();

				// use winmerge "Program Files\WinMerge\WinMergeU when available"
//				File winMergeFile = new File("C:\\Program Files\\WinMerge\\WinMergeU.exe");
//				
//				if(winMergeFile.exists() == true)
//				{
//				
//				   ProcessBuilder processBuilder = new ProcessBuilder(winMergeFile.getAbsolutePath(), tempFileA.getAbsolutePath(), tempFileB.getAbsolutePath());
//				   Process process = processBuilder.start();
//				    
//				}
//				else
				{

					// starte "$OMROOT\etc\ccrc_diff\win32\ccrc_cleardiffmrg.exe" $source1 $source2
					File ccrcFile = new File(System.getenv("OMROOT"), "etc\\ccrc_diff\\win32\\ccrc_cleardiffmrg.exe");
					if (ccrcFile.exists() == false)
					{
						trace("ClearCase diff tool not found");
						return false;
					}

					ProcessBuilder processBuilder = new ProcessBuilder(ccrcFile.getAbsolutePath(),
							tempFileA.getAbsolutePath(), tempFileB.getAbsolutePath());

					Process process = processBuilder.start();
				}

//			    InputStream inputStream = process.getInputStream();
//			    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//			    String line;
//				while ((line = reader.readLine()) != null)
//				{
//					trace(line);
//				}
			}
			catch(IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			return true;

		}

		public String generateHTMLOutput()
		{
			StringBuilder diffStringBuilder = new StringBuilder();

			DiffRowGenerator generator = DiffRowGenerator.create().showInlineDiffs(true)
					// .mergeOriginalRevised(true)
					// .inlineDiffByWord(true)
					// .oldTag(f -> "~") //introduce markdown style for strikethrough
					// .newTag(f -> "<b>") //introduce markdown style for bold

					.build();

			diffStringBuilder.append("<html>\n<body>\n");

			try
			{

				List<String> sourceLinesA = ASTHelper.getLines(myBodyA, false);
				List<String> sourceLinesB = ASTHelper.getLines(myBodyB, false);
				List<DiffRow> rows = generator.generateDiffRows(sourceLinesA, sourceLinesB);

				diffStringBuilder.append("<table>\n");
				diffStringBuilder.append("<tr><th style=\"width: 50%;\"> " + mySignatureA + " rev. + " + myRevisionA
						+ " </th><th style=\"width: 50%;\"> " + mySignatureB + " rev. " + myRevisionB
						+ "</th><th>Change</th></tr>\n");

				if (hasDiff(rows) == true)
				{
					for (DiffRow row : rows)
					{
						diffStringBuilder.append("<tr>\n");
						diffStringBuilder.append("<td><pre>");
						diffStringBuilder.append(row.getOldLine());
						diffStringBuilder.append("</pre></td>\n");
						diffStringBuilder.append("<td><pre>");
						diffStringBuilder.append(row.getNewLine());
						diffStringBuilder.append("</pre></td>\n");
						diffStringBuilder.append("<td>");
						Tag tag = row.getTag();
						if (tag == Tag.EQUAL)
						{
							diffStringBuilder.append("");
						}
						else if (tag == Tag.CHANGE)
						{
							diffStringBuilder.append("C");
						}
						else if (tag == Tag.DELETE)
						{
							diffStringBuilder.append("-");
						}
						else if (tag == Tag.INSERT)
						{
							diffStringBuilder.append("+");
						}

						diffStringBuilder.append("</td>\n");
						diffStringBuilder.append("</tr>\n");
					}

				}
				else
				{
					diffStringBuilder.append("<tr><td>no changes</td><td>no changes </td><td></td></tr>\n");
				}

				diffStringBuilder.append("</table>\n");

			}
			catch(Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			diffStringBuilder.append("</body>\n</html>\n");

			return diffStringBuilder.toString();

		}

		private boolean hasDiff(List<DiffRow> rows)
		{
			boolean ret = false;
			for (DiffRow row : rows)
			{
				if (row.getTag() == Tag.EQUAL)
				{
					continue;
				}
				if (row.getOldLine().trim().isEmpty() && row.getNewLine().trim().isEmpty())
				{
					continue;
				}

				ret = true;
				break;
			}
			return ret;
		}

	}

	public class HistoryRow
	{
		private logRow myLogRow = null;
		private List<ChangedElement> myChangedElements = null;
		private int myLastRevision = 0;

		public HistoryRow(logRow aLogRow, int aLastRevision, List<ChangedElement> aChangedElements)
		{
			myLogRow = aLogRow;
			myChangedElements = aChangedElements;
			myLastRevision = aLastRevision;
		}

		public logRow getLogRow()
		{
			return myLogRow;
		}

		public List<ChangedElement> getChangedElements()
		{
			return myChangedElements;
		}

		public int getLastRevision()
		{
			return myLastRevision;
		}

	}

	/**
	 * Generates an annotated version of a revision based on a list of older
	 * revisions, like <tt>cvs annotate</tt> or <tt>svn blame</tt>.
	 * 
	 * @author <a href="schierlm@gmx.de">Michael Schierl</a>
	 * 
	 * @param <R> Type of the revision metadata
	 */
	public class Annotate<R>
	{

		private final List<R> revisions;
		private final int[] lineNumbers;
		private R currentRevision;
		private final List<String> currentLines;
		private final List<Integer> currentLineMap;

		/**
		 * Creates a new annotation generator.
		 * 
		 * @param revision    Revision metadata for the revision to be annotated
		 * @param targetLines Lines of the revision to be annotated
		 */
		public Annotate(R revision, List<String> targetLines)
		{
			revisions = new ArrayList<R>();
			lineNumbers = new int[targetLines.size()];
			currentRevision = revision;
			currentLines = new ArrayList<String>(targetLines);
			currentLineMap = new ArrayList<Integer>();
			for (int i = 0; i < lineNumbers.length; i++)
			{
				lineNumbers[i] = -1;
				revisions.add(null);
				currentLineMap.add(i);
			}
		}

		/**
		 * Check whether there are still lines that are unannotated. In that case, more
		 * older revisions should be retrieved and passed to the function. Note that as
		 * soon as you pass an empty revision, all lines will be annotated (with a later
		 * revision), therefore if you do not have any more revisions, pass an empty
		 * revision to annotate the rest of the lines.
		 */
		public boolean areLinesUnannotated()
		{
			for (int i = 0; i < lineNumbers.length; i++)
			{
				if (lineNumbers[i] == -1 || revisions.get(i) == null)
					return true;
			}
			return false;
		}

		/**
		 * Add the previous revision and update annotation info.
		 * 
		 * @param revision Revision metadata for this revision
		 * @param lines    Lines of this revision
		 * @throws DiffException
		 */
		public void addRevision(R revision, List<String> lines) throws DiffException
		{
			Patch patch;

			patch = DiffUtils.diff(currentLines, lines);

			int lineOffset = 0; // remember number of already deleted/added lines

			List<AbstractDelta<String>> deltas = patch.getDeltas();

			for (AbstractDelta<String> d : deltas)
			{

				Chunk<String> original = d.getSource();

				Chunk<String> revised = d.getTarget();

				int pos = original.getPosition() + lineOffset;
				// delete lines
				for (int i = 0; i < original.size(); i++)
				{
					int origLine = currentLineMap.remove(pos);
					currentLines.remove(pos);
					if (origLine != -1)
					{
						lineNumbers[origLine] = original.getPosition() + i;
						revisions.set(origLine, currentRevision);
					}
				}
				for (int i = 0; i < revised.size(); i++)
				{
					currentLines.add(pos + i, revised.getLines().get(i));
					currentLineMap.add(pos + i, -1);
				}
				lineOffset += revised.size() - original.size();
			}

			currentRevision = revision;
			if (!currentLines.equals(lines))
				throw new RuntimeException("Patch application failed");
		}

		/**
		 * Return the result of the annotation. It will be a List of the same length as
		 * the target revision, for which every entry states the revision where the line
		 * appeared last.
		 */
		public List<R> getAnnotatedRevisions()
		{
			return Collections.unmodifiableList(revisions);
		}

		/**
		 * Return the result of the annotation. It will be a List of the same length as
		 * the target revision, for which every entry states the line number in the
		 * revision where the line appeared last.
		 */
		public int[] getAnnotatedLineNumbers()
		{
			return (int[]) lineNumbers.clone();
		}
	}

	private class JTreeNode implements Comparable<JTreeNode>
	{
		private IRPModelElement myElement = null;
		private int myChanges = 0;
		private boolean myLinesOfCode = false;

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
			if (myLinesOfCode)
			{
				return myElement.getName() + " [" + myElement.getMetaClass() + "] Lines of Code: " + myChanges;
			}
			else
			{
				return myElement.getName() + " [" + myElement.getMetaClass() + "] Changes: " + myChanges;
			}

		}

		public void setLinesOfCode(boolean aLinesOfCode)
		{
			myLinesOfCode = aLinesOfCode;
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

// Custom cell renderer to make text look like a hyperlink
	@SuppressWarnings("serial")
	public class LinkCellRenderer extends DefaultTableCellRenderer
	{
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column)
		{
			Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if ((column == 0) || (column == 3))
			{
				cell.setForeground(Color.BLUE);
				cell.setFont(cell.getFont()
						.deriveFont(Collections.singletonMap(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON)));
			}
			return cell;
		}
	}

	public class ChangedElement
	{
		private IRPModelElement myElement = null;

		public ChangedElement(IRPModelElement aElement)
		{
			myElement = aElement;

		}

		public IRPModelElement getElement()
		{
			return myElement;
		}

		public String toString()
		{
			return myElement.getName();
		}
	}

}

class logRow
{
	protected int myRevision = 0;
	protected String myAuthor = "";
	protected LocalDateTime myDate;
	protected String myMessage = "";

	public logRow(String aRevision, String aDate, String aAuthor, String aMessage)
	{
		myRevision = Integer.parseInt(aRevision);

		myDate = SVNTools.parseIsoDateTime(aDate);
		myAuthor = aAuthor;
		myMessage = aMessage;
	}

	public int getRevision()
	{
		return myRevision;
	}

	public String getAuthor()
	{
		return myAuthor;
	}

	public LocalDateTime getDate()
	{
		return myDate;
	}

	public String getDateTime()
	{
		return myDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
	}

	public String getMessage()
	{
		return myMessage;
	}

	public String getJiraUrl()
	{

		String jiraIssue = getJiraIssue();

		if (jiraIssue != null)
		{
			String regex = "\\bUSM-\\d+\\b";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(jiraIssue);

			if (matcher.find())
			{
				String jiraIssueId = matcher.group();
				String url = "https://berninaag.atlassian.net/browse/" + jiraIssueId;
				return url;
			}
		}
		return "";
	}

	public void openJiraUrl()
	{

		String url = getJiraUrl();
		System.out.println(url);
		if (url != null && url.isEmpty() == false)
		{

			try
			{
				java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}

		}
	}

	public String getJiraIssue()
	{
		// String regex = "\\bUSM-\\d[\\w_:-]*\\b"; // Startet mit USM-, gefolgt von
		// Zahlen und optional weiteren
		// Zeichen oder Unterstrichen

		// regex so anpassen dass USM auch nicht am Anfang stehen kann
		String regex = "\\b[\\w_:-]*USM-\\d[\\w_:-]*\\b";

		// Pattern und Matcher erstellen
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(myMessage.split("\n")[0]);

		// Falls ein Treffer vorhanden ist
		if (matcher.find())
		{
			// Das gefundene Wort
			String message = matcher.group();

			message = message.replace(":", "");
			// Unterstriche durch Leerzeichen ersetzen
			message = message.replace("_", " ");

			return message;

		}

		return myMessage.substring(0, Math.min(myMessage.length(), 100));

	}

}

class OperationItem
{

	private logRow myLogRow = null;
	private String myName = "";
	private String mySignature = "";
	private String myBody = "";
	private String myGUID = "";
	private int myIndex = -1;

	public OperationItem(IRPOperation aOperation, logRow aLogRow, int aIndex)
	{
		myName = aOperation.getName();

		myIndex = aIndex;

		myGUID = aOperation.getGUID();

		myLogRow = aLogRow;

		mySignature = aOperation.getSignature();

		IRPClassifier retType = aOperation.getReturns();

		if (retType == null)
		{
			mySignature = "void " + mySignature;
		}
		else
		{
			mySignature = retType.getName() + " " + mySignature;
		}

		myBody = mySignature + " {\n";
		myBody += aOperation.getBody();
		myBody += "\n}\n";

	}

	public String getName()
	{
		return myName;
	}

	public String getSignature()
	{
		return mySignature;
	}

	public List<String> getBody()
	{
		List<String> lines = ASTHelper.getLines(myBody, false);
		for (int i = 1; i < lines.size() - 1; i++)
		{
			lines.set(i, "    " + lines.get(i));
		}

		return lines;
	}

	public String getBodyAsString()
	{
		return myBody;
	}

	public String getGUID()
	{
		return myGUID;
	}

	public logRow getLogRow()
	{
		return myLogRow;
	}

	public int getIndex()
	{
		return myIndex;
	}

	public void setIndex(int aIndex)
	{
		myIndex = aIndex;
	}

}
