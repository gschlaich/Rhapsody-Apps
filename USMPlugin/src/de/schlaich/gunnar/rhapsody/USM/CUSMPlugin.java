package de.schlaich.gunnar.rhapsody.USM;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileView;

import com.telelogic.rhapsody.core.HYPNameType;
import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPComponent;
import com.telelogic.rhapsody.core.IRPConfiguration;
import com.telelogic.rhapsody.core.IRPFile;
import com.telelogic.rhapsody.core.IRPHyperLink;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPOperation;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPProfile;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPRequirement;
import com.telelogic.rhapsody.core.IRPTableView;
import com.telelogic.rhapsody.core.IRPUnit;
import com.telelogic.rhapsody.core.RPUserPlugin;

import de.schlaich.gunnar.aiTools.GeminiAPIClient;
import de.schlaich.gunnar.aiTools.mcp.McpStarter;
import de.schlaich.gunnar.aiTools.mcp.jsonModels.JsonModelTester;
import de.schlaich.gunnar.rhapsody.MsgCreator.CCreateMessage;
import de.schlaich.gunnar.rhapsody.plantUMLView.PlantUMLStarter;
import de.schlaich.gunnar.rhapsody.relation.CRhapsodyRelation;
import de.schlaich.gunnar.rhapsody.roundtrip.COperationalRoundtrip;
import de.schlaich.gunnar.rhapsody.roundtrip.CGoogleTestRoundTrip;
import de.schlaich.gunnar.rhapsody.utilities.ASTHelper;
import de.schlaich.gunnar.rhapsody.utilities.BuildTools;
import de.schlaich.gunnar.rhapsody.utilities.MarkdownEditorPreview;
import de.schlaich.gunnar.rhapsody.utilities.MarkdownViewer;
import de.schlaich.gunnar.rhapsody.utilities.RhapsodyHelper;
import de.schlaich.gunnar.rhapsody.utilities.RhapsodyPreferences;
import de.schlaich.gunnar.rhapsody.utilities.RhapsodyReverseEngineering;
import de.schlaich.gunnar.rhapsody.utilities.SVNTools;
import de.schlaich.gunnar.rhapsody.utilities.SelectionHistory;
import de.schlaich.gunnar.rhapsody.utilities.StaticCodeAnalysis;
import de.schlaich.gunnar.rhapsody.utilities.USMConfiguration;
import de.schlaich.gunnar.rhapsody.utilities.WriterTemplateParser;
import de.schlaich.gunnar.rhapsody.utilities.generateInitCode.CodeGenerator;

public class CUSMPlugin extends RPUserPlugin
{

	private IRPApplication myRhapsody = null;
	private IRPProfile myProfile = null;
	

	private SVNTools mySVNTools = null;

	public static final String PlantUmlCmd = "PlantUML";
	public static final String RoundtripCmd = "Operational Roundtrip";
	public static final String SearchElementCmd = "Search Element";
	public static final String SelectRelationCmd = "Select Relation";
	public static final String CreateMsgCmd = "Create Message";
	public static final String AddParamMsgCmd = "Add Parameter to Message";
	public static final String MovePackageCmd = "Move Package to SVN";
	public static final String SetActiveCmd = "Set Active";
	public static final String LocateActiveCmd = "Locate Active";
	public static final String SetComponentDependencyCmd = "Set Component Dependency";
	public static final String ScriptRunnerCmd = "Scriptrunner";
	public static final String BuildAllCmd = "Build All";
	public static final String JiraIssueCmd = "Jira";
	public static final String JiraChangedCmd = "Jira Changed";
	public static final String StaticCodeAnalyzeCmd = "Code Analyze";
	public static final String StaticCodeAnalyzeClearCmd = "Clear Analyze";
	public static final String RunMFileCmd = "Run m File";
	public static final String EditMFileCmd = "Edit m File";
	public static final String ConvertToRelativePathCmd = "Relative Path";
	public static final String GenerateInitCodeCmd = "Generate Init code";
	public static final String GetInitCodeOfClassCmd = "Get init code of class";
	public static final String SetInitCodeOfClassCmd = "Set init code of class";
	public static final String DiffHeadCmd = "Diff Head";
	public static final String DiffTrunkCmd = "Diff Trunk";
	public static final String DiffCmd = "Diff";
	public static final String LogCmd = "Show Log";
	public static final String ShowHistoryCmd = "Show History";
	public static final String StatisticCmd = "Change Statistic";
	public static final String StatisticCmd2 = "Change Statistics";
	public static final String LOCStatisticCmd = "Lines of Code";
	public static final String CommitCmd = "Commit";
	public static final String ExplorerCmd = "Explorer";
	public static final String DiffHeadReportCmd = "Diff Report Head";
	public static final String DiffTrunkReportCmd = "Diff Report Trunk";
	public static final String GetLockCmd = "Get Lock";
	public static final String ExportTableCmd = "Export Table";
	public static final String AddLibraryCmd = "Add Library";
	public static final String AddIncludePathCmd = "Add include path";
	public static final String AddConfigCmd = "Add Configuration";
	public static final String AddLibraryLinksCmd = "Show Library links";
	public static final String CompareOperationHeadCmd = "Compare Operation Head";
	public static final String CodeComplexityCmd = "Code Complexity";
	public static final String BlameCmd = "Blame";
	public static final String OperationEditorCmd = "OperationEditor";
	public static final String TortoiseLogCmd = "Tortoise Log";
	public static final String GeminiDescribeCmd = "Add Description";
	public static final String UpdateDatabaseCmd = "Update Database";
	public static final String libraryProperty = "CPP_CG.Package.USMLibraries";
	public static final String IncludeProperty = "CPP_CG.Package.USMIncludePath";
	public static final String MarkdownEditorCmd = "Markdown Editor";
	public static final String ReverseEngineeringCmd = "Reverse Engineering";
	public static final String GoogleTestRoundTripCmd = "GoogleTest Roundtrip";
	public static final String FormatCmd = "Format Code";
	public static final String ParseElementCmd = "Parse Element";
	public static final String MCPStartCmd = "MCP Start";
	public static final String MCPStopCmd = "MCP Stop";
	public static final String JsonExportCmd = "JSON export";
	public static final String JsonCopyCmd = "JSON copy";
	public static final String JsonPasteCmd = "JSON paste";
	public static final String JsonSchemaCmd = "JSON Schema";
	public static final String RunBatchCmd = "Run Batch";
	public static final String RunAllBatchesCmd = "Run all Batches";
	public static final String ListMetaClassesCmd = "List MetaClasses";
	public static final String NextCmd = "Next Selected";
	public static final String BackCmd = "Back Selected";
	public static final String NextChangedCmd = "Next Changed";
	public static final String BackChangedCmd = "Back Changed";
	public static final String ShowGUIDCmd = "Show GUID";
	public static final String ShowChangeHistoryCmd = "Show Change History";
	public static final String ShowMarkdownCmd = "Show Markdown";
	public static final String ActivateHistoryCmd = "Activate History";
	public static final String DeactivateHistoryCmd = "Deactivate History";
	public static final String CopyToAppDataCmd = "Copy to AppData";
	public static final String GetOperationLocationCmd = "Get Operation Location";


	private final long myStartTimeNanos = System.nanoTime();
	
	private SelectionHistory mySelectionHistory = null;
	
	public CUSMPlugin()
	{
		// TODO Auto-generated constructor stub
	}

	private SVNTools getSVNTools()
	{
		if (mySVNTools == null)
		{
			mySVNTools = new SVNTools(myRhapsody, this::trace);
		}

		return mySVNTools;

	}

	private String getBuildDate()
	{
		try
		{
			String jarPath = CUSMPlugin.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			String decodedPath = URLDecoder.decode(jarPath, "UTF-8");
			File jarFile = new File(decodedPath);
			if (jarFile.exists())
			{
				long lastModified = jarFile.lastModified();
				Date date = new Date(lastModified);
				return date.toString();
			}
			else
			{
				return "JAR-File not found";
			}
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
			return "Error while getting build date";
		}
	}

	@Override
	public void RhpPluginInit(IRPApplication rpyApplication)
	{

		
	
		myRhapsody = rpyApplication;
		trace("started");
		trace("Build date: " + getBuildDate());

		IRPProject activeProject = myRhapsody.activeProject();
		
		

		if (activeProject == null)
		{
			trace("no active Project!");
			return;
		}
		
		RhapsodyPreferences.setUILightmode();
		
		mySelectionHistory = new SelectionHistory(this::trace, myRhapsody);
		
		//mySelectionHistory.connect(myRhapsody);
		
		activeProject.setNotifyPluginOnElementsChanged(1);
		
		
		
		//run autorun batch files
		runBatchFiles(activeProject);
		

	}
	
	private void runBatchFiles(IRPProject project)
    {
		List<IRPHyperLink> batchFiles = project.getNestedElementsByMetaClass("HyperLink", 1).toList();
		
		for (IRPHyperLink batchFile : batchFiles)
		{
			if(batchFile.getUserDefinedMetaClass().equals("BatchFile"))
			{
				RhapsodyHelper.runBatch(batchFile, this::trace);
			}
		}
				
	}
	
	
	

	public void OnElementsChanged(String GUIDsList)
	{
		String[] elementsGuids = GUIDsList.split(",");
		IRPProject currentActiveProject = myRhapsody.activeProject();
		if (currentActiveProject != null)
		{
			for (int i = 0; i < elementsGuids.length; i++)
			{
				if (elementsGuids[i].length() > 0)
				{
					IRPModelElement currentElement = currentActiveProject.findElementByGUID(elementsGuids[i]);
					if (currentElement == null)
					{
						myRhapsody.writeToOutputWindow("Log", "Deleted element with GUID: " + elementsGuids[i] + "\n");
					}
					else
					{
						myRhapsody.writeToOutputWindow("Log", "Element: " + currentElement.getFullPathName() + " ("
								+ elementsGuids[i] + ") was changed\n");
					}
				}
			}
		}
	}

	@Override
	public void RhpPluginInvokeItem()
	{

	}

	@Override
	public void OnMenuItemSelect(String menuItem)
	{

		IRPModelElement selected = myRhapsody.getSelectedElement();
		
		if (selected != null)
		{
			trace("Selected Menuitem: " + menuItem + " for " + selected.getName() + " of type " + selected.getMetaClass());
		}
		else
		{
			trace("Selected Menuitem: " + menuItem + " no selected element");
		}
		

		if (menuItem.contains(LocateActiveCmd))
		{
			RhapsodyHelper.locateActivePackage(myRhapsody, selected);
			return;
		}	

		if (menuItem.contains(PlantUmlCmd))
		{
			PlantUMLStarter.startPlantUML(myRhapsody, selected, false);
			return;
		}
		if (menuItem.contains(RoundtripCmd))
		{
			COperationalRoundtrip roundtrip = new COperationalRoundtrip();
			roundtrip.startRoundtrip(myRhapsody, selected, false);
			return;
		}
		
		if (menuItem.contains(GoogleTestRoundTripCmd))
		{
			CGoogleTestRoundTrip typeRoundTrip = CGoogleTestRoundTrip.getInstance(myRhapsody);
			if (typeRoundTrip.startRoundTrip((IRPFile) selected) == false)
			{
				trace("Type Roundtrip failed");
			}
			return;
			
		}
		
		if (menuItem.contains(SearchElementCmd))
		{
			RhapsodyHelper.searchElement(myRhapsody, selected);
			return;
		}
		if (menuItem.contains(SelectRelationCmd))
		{
			CRhapsodyRelation relation = new CRhapsodyRelation();
			relation.execute(myRhapsody, selected, false);
			return;
		}
		if (menuItem.contains(CreateMsgCmd))
		{
			CCreateMessage createMessage = new CCreateMessage();
			createMessage.execute(myRhapsody, selected);
			return;
		}
		if (menuItem.contains(AddParamMsgCmd))
		{
			CCreateMessage createMessage = new CCreateMessage();
			createMessage.addParameter(myRhapsody, selected);
			return;
		}
		if (menuItem.contains(MovePackageCmd))
		{
			RhapsodyHelper.movePackageToRepository(myRhapsody, selected);
			return;
		}
		if (menuItem.contains(SetActiveCmd))
		{
			RhapsodyHelper.setActive(selected, myRhapsody);
			return;
		}

		if (menuItem.contains(ScriptRunnerCmd))
		{
			RhapsodyHelper.scriptRunner(myRhapsody, selected);
			return;
		}
		if (menuItem.contains(BuildAllCmd))
		{
			BuildTools bt = new BuildTools(myRhapsody);
			bt.buildAll();
			return;
		}
		if (menuItem.contains(SetComponentDependencyCmd))
		{
			RhapsodyHelper.setComponentDependency(myRhapsody, selected);
			return;
		}

		if (menuItem.contains(JiraChangedCmd))
		{

			getSVNTools().anchorAllChanges(selected);
			return;

		}

		if (menuItem.contains(JiraIssueCmd))
		{

			IRPRequirement jiraReq = getSVNTools().setActualJiraIssue(selected);
			if (jiraReq == null)
			{
				trace("Could not get Jira Issue");
				return;
			}

			trace("Jira Issue: " + jiraReq.getName() + ": " + jiraReq.getSpecification());
			return;
		}

		if (menuItem.contains(StaticCodeAnalyzeCmd))
		{
			String result = StaticCodeAnalysis.Analyze(selected, myRhapsody, this::trace);

			if (result == null)
			{
				trace("Analyze failed");
			}

			trace("Analyze: " + result);
			
			

			return;
		}

		if (menuItem.contains(StaticCodeAnalyzeClearCmd))
		{
			StaticCodeAnalysis.Clear(selected, myRhapsody, this::trace);
			return;
		}

		if (menuItem.contains(RunMFileCmd))
		{
			if (selected instanceof IRPHyperLink == false)
			{
				trace("No m-File");
				return;
			}

			IRPHyperLink c = (IRPHyperLink) selected;

			String absolutePath = RhapsodyHelper.getAbsolutePath(c);

			if (absolutePath == null)
			{
				trace("Could not generate absolute Path from " + c.getURL());
				return;
			}

			File mFile = new File(absolutePath);

			if (mFile.exists() == false)
			{
				trace("File " + mFile.getPath() + " does not exist");
				return;
			}

			// build cmd...

			String script = "\"cd('" + mFile.getParent() + "'); run('" + mFile.getName() + "'); waitfor(h);\"";

			trace(script);

			ProcessBuilder pb = new ProcessBuilder("Octave", "--eval", script);

			try
			{
				// pb.directory(mFile.getParentFile());

				trace("Execute M File");
				Process p = pb.start();
//				InputStream inputStream = p.getInputStream();
//				InputStream errorStream = p.getErrorStream();
//				BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream));
//				BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
//				String line;
//	            StringBuilder output = new StringBuilder();
//	            StringBuilder errorOut = new StringBuilder();
//	            while ((line = inputReader.readLine()) != null) 
//	            {
//	                output.append(line);
//	            }
//	            while ((line = errorReader.readLine())!= null)
//	            {
//	            	trace(line);
//	            }
//	            if(output.length()>0)
//	            {
//	            	trace("Output:");
//	            	trace(output.toString());
//	            }

			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			trace("end");
			return;
		}

		if (menuItem.contains(EditMFileCmd))
		{
			trace("not yet implemented");
			return;
		}

		if (menuItem.contains(ConvertToRelativePathCmd))
		{

			if (selected instanceof IRPHyperLink == false)
			{
				return;
			}

			IRPHyperLink selectedLink = (IRPHyperLink) selected;

			if (selectedLink.getSaveUnit().isReadOnly() == 1)
			{
				trace(selectedLink.getSaveUnit().getName() + " is readOnly!");
				return;
			}

			String relativePath = RhapsodyHelper.getRelativePath(selectedLink);

			if (relativePath == null)
			{
				trace("Could not generate relative Path from " + selectedLink.getURL());
				return;
			}

			File lFile = new File(selectedLink.getURL());

			selectedLink.setURL(relativePath);
			selectedLink.setDisplayOption(HYPNameType.RP_HYP_FREETEXT, lFile.getName());

			return;
		}

		if (menuItem.contains(GenerateInitCodeCmd))
		{
			IRPProject p = selected.getProject();

			if (p == null)
			{
				return;
			}

			CodeGenerator generator = new CodeGenerator(this::trace);

			generator.generateSortedList(p);

			generator.generateInitcode();

			return;

		}

		if (menuItem.contains(GetInitCodeOfClassCmd))
		{

			if (selected instanceof IRPClass == false)
			{
				return;
			}

			IRPClass selectedClass = (IRPClass) selected;

			CodeGenerator generator = new CodeGenerator(this::trace);

			String initCode = generator.getInitCodeForClass(selectedClass);

			if (initCode == null)
			{
				return;
			}

			trace(initCode);

			return;
		}

		if (menuItem.contains(SetInitCodeOfClassCmd))
		{
			if (selected instanceof IRPClass == false)
			{
				return;
			}

			IRPClass selectedClass = (IRPClass) selected;

			CodeGenerator generator = new CodeGenerator(this::trace);

			String initCode = generator.updateEntry(selectedClass);

			if (initCode == null)
			{
				return;
			}

			trace(initCode);

			return;
		}

		if (menuItem.contains(DiffHeadCmd))
		{

			getSVNTools().diffmerge(selected, -1, -1, false, false);

			return;
		}
		
		if (menuItem.contains(DiffCmd))
        {

            getSVNTools().diffTreeDialog(selected);
            return;
        }

		if (menuItem.contains(CompareOperationHeadCmd))
		{
			if (selected instanceof IRPOperation == false)
			{
				trace("No Operation selected");
				return;
			}

			IRPOperation operation = (IRPOperation) selected;
			getSVNTools().compareOperationVersions(operation, -1, -1);
			return;
		}

		if (menuItem.contains(DiffHeadReportCmd))
		{
			getSVNTools().diffmerge(selected, -1, -1, true, true);
			return;
		}

		if (menuItem.contains(DiffTrunkCmd))
		{
			//getSVNTools().diffMergeBase(selected, false);
			return;
		}

		if (menuItem.contains(DiffTrunkReportCmd))
		{
			getSVNTools().diffMergeBase(selected, true);
			return;

		}

		if (menuItem.contains(GetLockCmd))
		{
			getSVNTools().getLock(selected);
			return;
		}

		if (menuItem.contains(ShowHistoryCmd))
		{
			getSVNTools().showChangeList(selected, 800, false, 0, 0);
			return;
		}

		if (menuItem.contains(LogCmd))
		{

			SVNTools svn = getSVNTools();

			svn.showLog(selected);

			return;
		}
		if (menuItem.contains(TortoiseLogCmd))
		{
			getSVNTools().showTortoiseLog(selected);
			return;
		}
		
		if (menuItem.contains(StatisticCmd2))
		{
			SVNTools svn = getSVNTools();
			svn.showChangeStatistic(selected, 800, false, 0, 0);
			return;
		}

		if (menuItem.contains(StatisticCmd))
		{
			SVNTools svn = getSVNTools();
			svn.showChangeStatistic(selected, 36, true, 0, 0);
			return;
		}
			

		if (menuItem.contains(LOCStatisticCmd))
		{
			SVNTools svn = getSVNTools();
			svn.showLOCStatistic(selected);
			return;
		}

		if (menuItem.contains(CommitCmd))
		{
			getSVNTools().commit(selected);
			return;
		}

		if (menuItem.contains(ExplorerCmd))
		{
			IRPUnit unit = selected.getSaveUnit();
			if (unit == null)
			{
				return;
			}
			String directory = unit.getCurrentDirectory();
			String sbsFile = unit.getFilename();
			System.out.println(directory);
			try
			{
				Runtime.getRuntime().exec("explorer.exe /select," + directory + "\\" + sbsFile);
			}
			catch (IOException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			return;

		}

		if (menuItem.contains(ExportTableCmd))
		{
			if (selected instanceof IRPTableView == false)
			{
				trace("not tableView");
				return;
			}

			IRPTableView tableView = (IRPTableView) selected;

			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogTitle("Save as");

			// Filter f�r .csv, .html und .xml Dateien
			fileChooser.addChoosableFileFilter(new FileTypeFilter(".csv", "CSV Files"));
			fileChooser.addChoosableFileFilter(new FileTypeFilter(".html", "HTML Files"));
			fileChooser.addChoosableFileFilter(new FileTypeFilter(".xml", "XML Files"));
			fileChooser.setAcceptAllFileFilterUsed(false);

			int userSelection = fileChooser.showSaveDialog(null);

			if (userSelection != JFileChooser.APPROVE_OPTION)
			{
				trace("cancel save");
				return;
			}

			File fileToSave = fileChooser.getSelectedFile();
			String filePath = fileToSave.getAbsolutePath();
			String fileType = getFileExtension(fileToSave);

			String contentFormat = IRPTableView.ContentFormat.CSV;

			if (fileType.equals("html"))
			{
				contentFormat = IRPTableView.ContentFormat.HTML;
			}
			else if (fileType.equals("xml"))
			{
				contentFormat = IRPTableView.ContentFormat.XML;
			}

			String content = tableView.getContent(contentFormat);

			try
			{

				FileWriter fileWriter = new FileWriter(filePath);

				fileWriter.write(content);

				fileWriter.close();
			}
			catch (IOException e)
			{
				trace(e.getMessage());
			}

			trace("File " + fileToSave.getName() + " saved");
			return;

		}
		if (menuItem.contains(AddLibraryCmd))
		{
			
			
			
			if (selected instanceof IRPPackage == false)
			{
				trace("No Package selected");
				return;
			}

			
			
			SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	                //Turn off metal's use of bold fonts
	                
	                try
					{
						addLibrary(selected);
					}
					catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            }
	        });
			
			return;
			

			
		}
		if (menuItem.contains(AddIncludePathCmd))
		{
			if (selected instanceof IRPPackage == false)
			{
				trace("No Package selected");
				return;
			}

			SwingUtilities.invokeLater(new Runnable() 
			{

				public void run() 
				{
                        
                    addInclude(selected, false);
				}
			
			
			});
			return;
		}

		if (menuItem.contains(AddConfigCmd))
		{

			trace("Add Configuration");
			if (selected instanceof IRPProject == false)
			{
				trace("No Project selected");
				return;
			}

			IRPProject project = (IRPProject) selected;

			USMConfiguration config = USMConfiguration.Instance(myRhapsody, this::trace);
			config.loadConfiguration(project);
			config.addLibraryLinks(project);
			config.addIncludeLinks(project);

			trace("End Add Configuration");

			return;
		}
		
		if (menuItem.contains(CopyToAppDataCmd))
		{
			trace("Copy to AppData");
			if (selected instanceof IRPHyperLink == false)
			{
				trace("No Data File selected");
				return;
			}
			
			IRPHyperLink link = (IRPHyperLink) selected;
			
	
			USMConfiguration config = USMConfiguration.Instance(myRhapsody, this::trace);
			
			config.copyToAppData(link);
			
			
			trace("End Copy to AppData");

			return;
		}

		if (menuItem.contains(AddLibraryLinksCmd))
		{
			trace("Add Library Links");
			if (selected instanceof IRPProject == false)
			{
				trace("No Project selected");
				return;
			}

			IRPProject project = (IRPProject) selected;

			USMConfiguration config = USMConfiguration.Instance(myRhapsody, this::trace);
			config.addLibraryLinks(project);

			trace("End Add Library Links");

			return;
		}

		if (menuItem.contains(CodeComplexityCmd))
		{
			if (selected instanceof IRPClass == false)
			{
				trace("No Class selected");
				return;
			}

			StaticCodeAnalysis.calculateCodeComplexity(selected, myRhapsody, this::trace);

			return;
		}

		if (menuItem.contains(BlameCmd))
		{
			if (selected instanceof IRPOperation == false)
			{
				trace("No Operation selected");
				return;
			}

			getSVNTools().blame((IRPOperation) selected, 100, false);

			return;
		}

		if (menuItem.contains(OperationEditorCmd))
		{
			if (selected instanceof IRPOperation == false)
			{
				trace("No Operation selected");
				return;
			}

			IRPOperation operation = (IRPOperation) selected;

			RhapsodyHelper.startOperationEditor(operation, myRhapsody, this::trace);

			return;
		}
		
		if (menuItem.contains(GeminiDescribeCmd))
		{
			
			GeminiAPIClient geminiAPIClient = new GeminiAPIClient(this::trace, myRhapsody);
			
			if (selected instanceof IRPOperation == true)
			{
				IRPOperation operation = (IRPOperation) selected;
				geminiAPIClient.generateDescription(operation);
				selected.setDescription(MarkdownEditorPreview.showDialog(null, selected.getDescription()));
				
				
				
			}
			else if (selected instanceof IRPClass == true)
			{
				IRPClass c = (IRPClass) selected;
				geminiAPIClient.generateDescription(c);
				selected.setDescription(MarkdownEditorPreview.showDialog(null, selected.getDescription()));
			}
			else	
			{
				trace("No Operation or Class selected");
				
		
			}

			return;
		}
		
		if (menuItem.contains(UpdateDatabaseCmd))
		{
			SVNTools svnTools = getSVNTools();
			
			trace("Update SVN Database");
			svnTools.updateDatabase();
			
		}
		
		if (menuItem.contains(MarkdownEditorCmd))
		{
			selected.setDescription(MarkdownEditorPreview.showDialog(null, selected.getDescription()));
			return;
		}
		
		
		if (menuItem.contains(ReverseEngineeringCmd))
		{
			
			if(selected instanceof IRPPackage == false)
            {
                trace("No Package selected");
                return;
            }
			
			IRPPackage rpackage = (IRPPackage) selected;
			
			RhapsodyReverseEngineering reverseEngineering = RhapsodyReverseEngineering.getRhapsodyReverseEngineering(this::trace, myRhapsody);
			
			if (reverseEngineering == null)
			{
				trace("Reverse Engineering not available");
				return;
			}
			
			reverseEngineering.update(rpackage);
			
			trace("Reverse Engineering finished");
			
			
			return;
		}
		
		if (menuItem.contains(FormatCmd))
		{
			
			if (selected instanceof IRPOperation)
			{
				IRPOperation operation = (IRPOperation) selected;
				StaticCodeAnalysis.formatOperation(operation, this::trace);
				return;
			}
			if (selected instanceof IRPClassifier)
			{
				IRPClassifier classifier = (IRPClassifier) selected;
				StaticCodeAnalysis.formatClassifier(classifier, this::trace);
				return;
			}
			

			trace("No Classifier or Operation selected for formatting ( " + selected.getMetaClass() + ")");
			
			return;
		}
		
		if (menuItem.contains(ParseElementCmd))
		{
			if (selected instanceof IRPModelElement)
			{
				IRPModelElement modelElement = (IRPModelElement) selected;
				
			
				WriterTemplateParser parser = new WriterTemplateParser(this::trace);
				
				String parsedText = parser.parse(selected, false);
				trace("Parsed Text: " + parsedText);
				return;
			}

			trace("No Model Element selected for parsing ( " + selected.getMetaClass() + ")");
			return;
		}
		
		if (menuItem.contains(MCPStartCmd))
		{
			
			McpStarter mcpStarter = McpStarter.getInstance();
			
			
			try
			{
				mcpStarter.start(myRhapsody, this::trace);
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return;
		}
		
		if (menuItem.contains(MCPStopCmd))
		{
			McpStarter mcpStarter = McpStarter.getInstance();
			mcpStarter.stop();
			
			return;
		}
		
		if (menuItem.contains(JsonExportCmd))
        {
            if (selected instanceof IRPModelElement)
            {
                IRPModelElement modelElement = (IRPModelElement) selected;
                
                JsonModelTester tester = JsonModelTester.Instance(myRhapsody, this::trace);
                
                
                tester.getJson(modelElement);
                
                
            }
            return;
        }
		
		if (menuItem.contains(JsonCopyCmd))
		{
			if (selected instanceof IRPModelElement)
			{
				IRPModelElement modelElement = (IRPModelElement) selected;

				JsonModelTester tester = JsonModelTester.Instance(myRhapsody, this::trace);

				String jsonModel = tester.getJson(modelElement);
				
				StringSelection stringSelection = new StringSelection(jsonModel);
				
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(stringSelection, null);

			}
			return;
		}
		
		if(menuItem.contains(JsonPasteCmd))
		{
			if (selected instanceof IRPModelElement)
			{
				IRPModelElement modelElement = (IRPModelElement) selected;

				JsonModelTester tester = JsonModelTester.Instance(myRhapsody, this::trace);
				
				

				//String jsonModel = tester.getJson(modelElement);
				
				//StringSelection stringSelection = new StringSelection(jsonModel);
				
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				//StringSelection stringSelection =  
						
						
				Transferable contents = clipboard.getContents(null);
				
				if(contents == null)
				{
					trace("No contents in clipboard");
					return;
				}
				
				if(contents.isDataFlavorSupported(DataFlavor.stringFlavor) == false)
                {
                    trace("No string in clipboard");
                }
				
				String jsonModel;
				try
				{
					IRPProject project = selected.getProject();
					
					jsonModel = (String) contents.getTransferData(DataFlavor.stringFlavor);
					tester.getRhapsodyModelElementFromJson(jsonModel, selected, project);
					trace("----Model updated from JSON-----");
				}
				catch (UnsupportedFlavorException e)
				{
					// TODO Auto-generated catch block
					trace("No string in clipboard");
					trace(e.getMessage());
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					trace("Error reading clipboard");
					trace(e.getMessage());
				}
				
				
				
				

			}
			return;
			
		}
		
		if (menuItem.contains(JsonSchemaCmd))
		{
			if (selected instanceof IRPModelElement)
			{
				IRPModelElement modelElement = (IRPModelElement) selected;

				JsonModelTester tester = JsonModelTester.Instance(myRhapsody, this::trace);

				String schema = tester.getJsonSchema(modelElement);
				StringSelection stringSelection = new StringSelection(schema);
				
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(stringSelection, null);

			}
			return;
		}
		
		if (menuItem.contains(RunBatchCmd))
		{
			trace("Run Batch");

			RhapsodyHelper.runBatch(selected, this::trace);

			return;
		}
		
		if (menuItem.contains(RunAllBatchesCmd))
		{
			IRPProject activeProject = myRhapsody.activeProject();
			if (activeProject == null)
			{
				trace("no active Project!");
				return;
			}

			runBatchFiles(activeProject);

			return;
		}
		
		if (menuItem.contains(ListMetaClassesCmd))
		{
			JsonModelTester tester = JsonModelTester.Instance(myRhapsody, this::trace);

			String schema = tester.listAllMetaClasses();
			trace("JSON Schema: " + schema);
			return;
		}
		
		if (menuItem.contains(NextCmd))
		{
			mySelectionHistory.next();
			return;
		}
		
		if (menuItem.contains(BackCmd))
		{
			mySelectionHistory.back();
			return;
		}
		
		if (menuItem.contains(NextChangedCmd))
		{
			mySelectionHistory.nextChanged();
			return;
		}
		
		if (menuItem.contains(BackChangedCmd))
		{
			mySelectionHistory.backChanged();
			return;
		}
		
		if (menuItem.contains(ShowGUIDCmd))
		{
			if (selected == null)
			{
				trace("No element selected");
				return;
			}
			trace("GUID of " + selected.getFullPathName() + ": \"" + selected.getGUID() + "\"");
			return;
		}
		
		if (menuItem.contains(ShowChangeHistoryCmd))
		{
			if (selected == null)
			{
				trace("No element selected");
				return;
			}
			mySelectionHistory.showChangeHistory();
			return;
		}
		
		
		if (menuItem.contains(ShowMarkdownCmd))
		{
			
			if (selected instanceof IRPHyperLink == false)
			{
				trace("No markdown File");
				return;
			}

			IRPHyperLink c = (IRPHyperLink) selected;

			String absolutePath = RhapsodyHelper.getAbsolutePath(c);

			if (absolutePath == null)
			{
				trace("Could not generate absolute Path from " + c.getURL());
				return;
			}

			File markdownFile = new File(absolutePath);

			if (markdownFile.exists() == false)
			{
				trace("File " + markdownFile.getPath() + " does not exist");
				return;
			}
			
			String markdownContent;
			
			try
			{
				markdownContent = new String(Files.readAllBytes(markdownFile.toPath()));
			}
			catch (IOException e)
			{
				trace("Error reading markdown file: " + e.getMessage());
				return;
			}
			
			MarkdownViewer.ShowDialog(null, markdownContent);
			
			//MarkdownEditorPreview.showDialog(null, markdownContent);
			return;
		}
		if(menuItem.contains(ActivateHistoryCmd))
		{
			mySelectionHistory.connect(myRhapsody);
			return;
		}
		if(menuItem.contains(DeactivateHistoryCmd))
		{
			mySelectionHistory.disconnect();
			return;
		}
		
		if(menuItem.contains(GetOperationLocationCmd))
		{
			if (selected instanceof IRPOperation == false)
			{
				trace("No Operation selected");
				return;
			}
			
			IRPOperation operation = (IRPOperation) selected;
			
			ASTHelper.SourceLocation location = ASTHelper.getOperationSourceLocation(operation, myRhapsody);
			
			if (location == null)
			{
				trace("Could not find source location for operation: " + operation.getName());
				return;
			}
			
			trace("Operation: " + operation.getName());
			trace("File Path: " + location.getFilePath());
			trace("Line Number: " + location.getLineNumber());
			trace("Full Location: " + location.toString());
			
			return;
		}
		

		trace("menue item unknown");

	}

	@SuppressWarnings("unchecked")
	private void addInclude(IRPModelElement selected, boolean addToComponent)
	{
		Path usmRoot = null;
		try
		{
			usmRoot = RhapsodyHelper.getUSMPath(selected.getProject());

		}
		catch (Exception e)
		{
			trace(e.getMessage());
			return;
		}

		JFileChooser fileChooser = createDetailFileChooser(
				usmRoot.resolve("Development").resolve("ExternalSource").toFile());

		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		fileChooser.setAcceptAllFileFilterUsed(false);
		
		int userSelection = fileChooser.showOpenDialog(null);

		if (userSelection != JFileChooser.APPROVE_OPTION)
		{
			trace("Cancel...");
			return;
		}
		File directoryToSave = fileChooser.getSelectedFile();
		trace("Folder: " + directoryToSave.getAbsolutePath());

		Path path = Paths.get(directoryToSave.getAbsolutePath());

		path = usmRoot.relativize(path);

		String libString = "<usm_root>\\" + path.toString();

		libString = libString.replace("\\", "\\\\");

		String delimiter = ";";

		int dotIndex = libString.lastIndexOf('.');
		if (dotIndex > 0)
		{
			libString = libString.substring(0, dotIndex);
		}

		trace("Include Path: " + libString);

		String libPropertyValue = selected.getPropertyValue(IncludeProperty);

		String[] libArray = libPropertyValue.split(delimiter);

		for (String s : libArray)
		{
			
			if (s.trim().equals(libString))
			{
				trace("Include path already added");
				return;
			}
		}
		
		if(libArray.length == 1 && libArray[0].length() == 0)
		{
			libPropertyValue = libString;
		}
		else
		{
			libPropertyValue += delimiter+'\n'+ libString;
		}
		trace("set Include Path: " + libPropertyValue);

		selected.setPropertyValue(IncludeProperty, libPropertyValue);
		
		if (addToComponent == false)
		{
			return;
		}
		
		// change in Component configuration as well
		
		IRPProject project = selected.getProject();
		
		List<IRPComponent> components = project.getNestedElementsByMetaClass("Component", 1).toList();
		
		for (IRPComponent component : components)
		{
			List<IRPModelElement> scopeElements = component.getScopeElementsByCategory("Package").toList();
			
			
			//trace("Check Component " + component.getName() + " with " + scopeElements.size() + " scope elements");
			
			for(IRPModelElement elem : scopeElements)
            {
                if(elem instanceof IRPPackage == false)
				{
					continue;
				}
				trace(elem.getName() + " of type " + elem.getMetaClass());
				if(elem.equals(selected))
                {
			
					//trace("Update Include path in Component " + component.getName());
                	IRPConfiguration config = component.findConfiguration("DefaultConfig");
					if(config != null)
	                {
						config.setIncludePath(libPropertyValue);
	                }
					else
					{
						trace("No DefaultConfig for Component " + component.getName());
					}
	
					break;
	            }

            }
		}
	}
	

	private JFileChooser createDetailFileChooser(File directory)
	{
		JFileChooser fc = new JFileChooser(directory);
		// Switch to details view
		javax.swing.Action detailsAction = fc.getActionMap().get("viewTypeDetails");
		if (detailsAction != null)
		{
			detailsAction.actionPerformed(null);
		}
		return fc;
	}

	private String getFileExtension(File file)
	{
		String fileName = file.getName();
		int lastIndexOfDot = fileName.lastIndexOf('.');
		if (lastIndexOfDot > 0 && lastIndexOfDot < fileName.length() - 1)
		{
			return fileName.substring(lastIndexOfDot + 1).toLowerCase();
		}
		return "";
	}

	private void trace(String aMsg)
	{
		///get time since start
		long currentTimeNanos = System.nanoTime();
		long elapsedTimeNanos = currentTimeNanos - myStartTimeNanos;
		long elapsedTimeMilliSeconds = elapsedTimeNanos / 1_000_000;
		double elapsedTimeSeconds = elapsedTimeMilliSeconds / 1000.0;
		
		myRhapsody.writeToOutputWindow("Log","["+elapsedTimeSeconds+"] USMPlugin: " + aMsg + "\n");
		System.out.println("["+elapsedTimeSeconds+"] USMPlugin: "+aMsg);
	}
	@Override
	public void OnTrigger(String trigger)
	{
		// TODO Auto-generated method stub
		
		trace("Ontrigger: " + trigger);

	}

	@Override
	public boolean RhpPluginCleanup()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void RhpPluginFinalCleanup()
	{
		// TODO Auto-generated method stub

	}
	
	
	private void addLibrary(IRPModelElement selected) throws IOException
	{

		Path usmRoot;
		
		usmRoot = RhapsodyHelper.getUSMPath(selected.getProject());
		
		
		JFileChooser fileChooser = new JFileChooser(
				usmRoot.resolve("Development").resolve("ExternalSource").toFile());

		// fileChooser.setFileSelectionMode(JFileChooser.);
		fileChooser.setDialogTitle("Select Library Folder");
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		

		fileChooser.setAcceptAllFileFilterUsed(false);

		int userSelection = fileChooser.showOpenDialog(null);

		if (userSelection != JFileChooser.APPROVE_OPTION)
		{
			trace("Cancel...");
			return;
		}
		File directoryToSave = fileChooser.getSelectedFile();
		trace("Folder: " + directoryToSave.getAbsolutePath());

		Path path = Paths.get(directoryToSave.getAbsolutePath());

		path = usmRoot.relativize(path);

		String libString = "<usm_root>\\" + path.toString();

		libString = libString.replace("\\", "\\\\");

		String delimiter = ";";

		int dotIndex = libString.lastIndexOf('.');
		if (dotIndex > 0)
		{
			libString = libString.substring(0, dotIndex);
		}

		trace("Library Path: " + libString);

		String libPropertyValue = selected.getPropertyValue(libraryProperty);

		String[] libArray = libPropertyValue.split(delimiter);

		for (String s : libArray)
		{
			if (s.equals(libString))
			{
				trace("Library already added");
				return;
			}
		}

		libPropertyValue += delimiter + libString;
		trace("set Library: " + libPropertyValue);

		selected.setPropertyValue(libraryProperty, libPropertyValue);

		return;
	}

	public String get_UserDefinedImplementation(IRPModelElement cellElement, Integer row, Integer column)
	{
		return cellElement.getOwner().getDescription();
	}

	public IRPModelElement get_UserDefinedImplementation1(IRPModelElement cellElement, Integer row, Integer column)
	{

		IRPRequirement req = (IRPRequirement) cellElement;
		if (req == null)
		{
			return null;
		}

		req.getAnchoredByMe();

		return null;

	}

}

class FileTypeFilter extends javax.swing.filechooser.FileFilter
{

	private String extension;
	private String description;

	public FileTypeFilter(String extension, String description)
	{
		this.extension = extension;
		this.description = description;
	}

	@Override
	public boolean accept(File file)
	{
		if (file.isDirectory())
		{
			return true;
		}
		return file.getName().toLowerCase().endsWith(extension);
	}

	@Override
	public String getDescription()
	{
		return description + String.format(" (*%s)", extension);
	}
}
