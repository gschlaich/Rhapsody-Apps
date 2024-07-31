package de.schlaich.gunnar.rhapsody.USM;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.UIManager;

import com.telelogic.rhapsody.core.HYPNameType;
import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPComponent;
import com.telelogic.rhapsody.core.IRPConfiguration;
import com.telelogic.rhapsody.core.IRPControlledFile;
import com.telelogic.rhapsody.core.IRPHyperLink;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPOperation;
import com.telelogic.rhapsody.core.IRPProfile;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPRequirement;
import com.telelogic.rhapsody.core.IRPTableView;
import com.telelogic.rhapsody.core.IRPUnit;
import com.telelogic.rhapsody.core.RPUserPlugin;

import de.schlaich.gunnar.rhapsody.CCreateMessage;
import de.schlaich.gunnar.rhapsody.plantUMLView.PlantUMLStarter;
import de.schlaich.gunnar.rhapsody.relation.CRhapsodyRelation;
import de.schlaich.gunnar.rhapsody.roundtrip.COperationalRoundtrip;
import de.schlaich.gunnar.rhapsody.utilities.ASTHelper;
import de.schlaich.gunnar.rhapsody.utilities.BuildTools;
import de.schlaich.gunnar.rhapsody.utilities.RhapsodyHelper;
import de.schlaich.gunnar.rhapsody.utilities.SVNTools;
import de.schlaich.gunnar.rhapsody.utilities.StaticCodeAnalysis;
import de.schlaich.gunnar.rhapsody.utilities.generateInitCode.CodeGenerator;

public class CUSMPlugin extends RPUserPlugin {

	
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
	public static final String HistoryCmd = "History";
	public static final String ExplorerCmd = "Explorer";
	public static final String DiffHeadReportCmd = "Diff Report Head";
	public static final String DiffTrunkReportCmd = "Diff Report Trunk";
	public static final String GetLockCmd = "Get lock";
	public static final String ExportTableCmd = "Export Table";
	public static final String AddLibraryCmd = "Add Library";
	public static final String AddIncludePathCmd = "Add include path";
	
	
	
	public CUSMPlugin() {
		// TODO Auto-generated constructor stub
	}
	
	private SVNTools getSVNTools()
	{
		if(mySVNTools==null)
		{
			mySVNTools = new SVNTools(myRhapsody, this::trace);
		}
		
		return mySVNTools;
		
	}

	@Override
	public void RhpPluginInit(IRPApplication rpyApplication) {
		
		myRhapsody = rpyApplication;
		trace("started");

		IRPProject activeProject = myRhapsody.activeProject();
		
		if(activeProject==null)
		{
			trace("no active Project!");
			return;
		}
		
		try 
		{
            // Setze das Look and Feel auf das System-Look-and-Feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } 
		catch (Exception e) 
		{
            e.printStackTrace();
        }

	}
	
	public void OnElementsChanged(String GUIDsList)
	{
		String[] elementsGuids = GUIDsList.split(",");
		IRPProject currentActiveProject = myRhapsody.activeProject();
		if (currentActiveProject != null)
		{
			for (int i = 0; i<elementsGuids.length; i++)
			{
				if (elementsGuids[i].length() > 0)
				{
					IRPModelElement currentElement = 
						currentActiveProject.findElementByGUID(elementsGuids[i]);
					if (currentElement == null)
					{
						myRhapsody.writeToOutputWindow("Log", 
								"Deleted element with GUID: "+elementsGuids[i]+"\n");
					}
					else
					{
						myRhapsody.writeToOutputWindow("Log", 
								"Element: "+currentElement.getFullPathName()+" ("+
											elementsGuids[i]+") was changed\n");
					}
				}
			}
		}
	}

	@Override
	public void RhpPluginInvokeItem() {
		
		

	}

	@Override
	public void OnMenuItemSelect(String menuItem) {
		
		trace("Selected: "+menuItem);
		
		IRPModelElement selected = myRhapsody.getSelectedElement();
		
		if(menuItem.contains(LocateActiveCmd))
		{
			RhapsodyHelper.locateActivePackage(myRhapsody, selected);
			return;
		}
		
		if(selected==null)
		{
			trace("no element selected");
			return;
		}
		
		if(menuItem.contains(PlantUmlCmd))
		{
			PlantUMLStarter.startPlantUML(myRhapsody, selected, false);
			return;
		}
		if(menuItem.contains(RoundtripCmd))
		{
			COperationalRoundtrip roundtrip = new COperationalRoundtrip();
			roundtrip.startRoundtrip(myRhapsody, selected, false);
			return;
		}
		if(menuItem.contains(SearchElementCmd))
		{
			RhapsodyHelper.searchElement(myRhapsody, selected);
			return;
		}
		if(menuItem.contains(SelectRelationCmd))
		{
			CRhapsodyRelation relation = new CRhapsodyRelation();
			relation.execute(myRhapsody, selected, false);
			return;
		}
		if(menuItem.contains(CreateMsgCmd))
		{
			CCreateMessage createMessage = new CCreateMessage();
			createMessage.execute(myRhapsody, selected);
			return;
		}
		if(menuItem.contains(AddParamMsgCmd))
        {
            CCreateMessage createMessage = new CCreateMessage();
            createMessage.addParameter(myRhapsody, selected);
            return;
        }
		if(menuItem.contains(MovePackageCmd))
		{
			RhapsodyHelper.movePackageToRepository(myRhapsody, selected);
			return;
		}
		if(menuItem.contains(SetActiveCmd))
		{
			RhapsodyHelper.setActive(selected, myRhapsody);
			return;
		}
		
		if(menuItem.contains(ScriptRunnerCmd))
		{
			RhapsodyHelper.scriptRunner(myRhapsody, selected);
			return;
		}
		if(menuItem.contains(BuildAllCmd))
		{
			BuildTools bt = new BuildTools(myRhapsody);
			bt.buildAll();
			return;
		}
		if(menuItem.contains(SetComponentDependencyCmd))
		{
			RhapsodyHelper.setComponentDependency(myRhapsody, selected);
			return;
		}
		
		if(menuItem.contains(JiraChangedCmd))
		{
			
			getSVNTools().anchorAllChanges(selected);
			return;
			
		}
		
		if(menuItem.contains(JiraIssueCmd))
		{
			
			IRPRequirement jiraReq = getSVNTools().setActualJiraIssue(selected);
			if(jiraReq==null)
			{
				trace("Could not get Jira Issue");
				return;
			}
			
			trace("Jira Issue: " + jiraReq.getName() + ": " + jiraReq.getSpecification());
			return;
		}
		
		
		
		if(menuItem.contains(StaticCodeAnalyzeCmd))
		{
			String result = StaticCodeAnalysis.Analyze(selected, myRhapsody, this::trace);
			
			if(result==null)
			{
				trace("Analyze failed");
			}
			
			trace("Analyze: "+ result);
			
			return;
		}
		
		if(menuItem.contains(StaticCodeAnalyzeClearCmd))
		{
			StaticCodeAnalysis.Clear(selected, myRhapsody, this::trace);
			return;
		}
		
		if(menuItem.contains(RunMFileCmd))
		{
			if(selected instanceof IRPHyperLink == false) 
			{
				trace("No m-File");
				return;
			}
			
			IRPHyperLink c = (IRPHyperLink)selected; 
			
			String absolutePath = RhapsodyHelper.getAbsolutePath(c);
			
			if(absolutePath==null)
			{
				trace("Could not generate absolute Path from "+ c.getURL());
				return;
			}
			
			File mFile = new File(absolutePath);
			
			if(mFile.exists()==false)
			{
				trace("File " + mFile.getPath() + " does not exist");
				return;
			}
		
			//build cmd...

			String script = "\"cd('"+ mFile.getParent() + "'); run('"+mFile.getName()+"'); waitfor(h);\"";
			
			trace(script);
			
			ProcessBuilder pb = new ProcessBuilder("Octave", "--eval", script);
				
			try 
			{
				//pb.directory(mFile.getParentFile());
				
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
		
		if(menuItem.contains(EditMFileCmd))
		{
			trace("not yet implemented");
			return;
		}
		
		if(menuItem.contains(ConvertToRelativePathCmd))
		{
			
			if(selected instanceof IRPHyperLink == false)
			{
				return;
			}
						
			IRPHyperLink selectedLink = (IRPHyperLink) selected;

			if(selectedLink.getSaveUnit().isReadOnly()==1)
			{
				trace(selectedLink.getSaveUnit().getName() + " is readOnly!");
				return;
			}
			
			String relativePath = RhapsodyHelper.getRelativePath(selectedLink);
			
			if(relativePath==null)
			{
				trace("Could not generate relative Path from " + selectedLink.getURL());
				return;
			}

			File lFile = new File(selectedLink.getURL());
			
			selectedLink.setURL(relativePath);
			selectedLink.setDisplayOption(HYPNameType.RP_HYP_FREETEXT, lFile.getName());
			
			return;
		}
		
		if(menuItem.contains(GenerateInitCodeCmd))
		{
			IRPProject p = selected.getProject();
			
			if(p==null)
			{
				return;
			}
			
			CodeGenerator generator = new CodeGenerator(this::trace);
			
			generator.generateSortedList(p);
			
			generator.generateInitcode();
			
			return;
			
		}
		
		if(menuItem.contains(GetInitCodeOfClassCmd))
		{
			
			if(selected instanceof IRPClass == false)
			{
				return;
			}
			
			IRPClass selectedClass = (IRPClass)selected;
					
			CodeGenerator generator = new CodeGenerator(this::trace);
			
			String initCode = generator.getInitCodeForClass(selectedClass);
			
			if(initCode==null)
			{
				return;
			}
			
			trace(initCode);
			
			return;
		}
		
		if(menuItem.contains(SetInitCodeOfClassCmd))
		{
			if(selected instanceof IRPClass == false)
			{
				return;
			}
			
			IRPClass selectedClass = (IRPClass)selected;
			
			CodeGenerator generator = new CodeGenerator(this::trace);
			
			String initCode = generator.updateEntry(selectedClass);
			
			if(initCode == null)
			{
				return;
			}
			
			trace(initCode);
			
			return;
		}
		
		if(menuItem.contains(DiffHeadCmd))
		{
			
			getSVNTools().diffmerge(selected,-1,false);
	
			return;
		}
		
		if(menuItem.contains(DiffHeadReportCmd))
		{
			getSVNTools().diffmerge(selected, -1, true);
			return;
		}
		
		if(menuItem.contains(DiffTrunkCmd))
		{
			getSVNTools().diffMergeBase(selected, false);
			return;
		}
		
		if(menuItem.contains(DiffTrunkReportCmd))
		{
			getSVNTools().diffMergeBase(selected, true);
			return;
			
		}
		
		if(menuItem.contains(GetLockCmd))
		{
			getSVNTools().getLock(selected);
			return;
		}
		
		if(menuItem.contains(HistoryCmd))
		{
			
			
			SVNTools svn = getSVNTools();
			
			List<SVNTools.logRow> list = svn.readHistory(selected, 10);
			
			for(SVNTools.logRow row:list)
			{
				trace("Revision: "+row.getRevision());
			}
			
			return;
		}
		
		if(menuItem.contains(ExplorerCmd))
		{
			IRPUnit unit = selected.getSaveUnit();
			if(unit == null)
			{
				return;
			}
			String directory = unit.getCurrentDirectory();
			String sbsFile = unit.getFilename();
			System.out.println(directory);
			try 
			{
				Runtime.getRuntime().exec("explorer.exe /select," + directory+"\\"+sbsFile);
			} 
			catch (IOException e1) 
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			return;
			
		}
		
		if(menuItem.contains(ExportTableCmd))
		{
			if(selected instanceof IRPTableView ==false)
			{
				trace("not tableView");
				return;
			}
			
			IRPTableView tableView = (IRPTableView)selected;
			
			JFileChooser fileChooser = new JFileChooser();
		    fileChooser.setDialogTitle("Save as");

	        // Filter für .csv, .html und .xml Dateien
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
	        
	        if(fileType.equals("html"))
	        {
	        	contentFormat =  IRPTableView.ContentFormat.HTML;
	        }
	        else if(fileType.equals("xml"))
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
	        catch(IOException e)
	        {
	        	trace(e.getMessage());
	        }

	        trace("File "+ fileToSave.getName() + " saved");
	        return;
        
	    }
		if(menuItem.contains(AddLibraryCmd))
		{
			JFileChooser fileChooser = new JFileChooser();
	        
	        
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
            
            return;
		}


		trace("menue item unknown");
		
	}

	private  String getFileExtension(File file) 
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
		myRhapsody.writeToOutputWindow("Log", "USMPlugin: " + aMsg + "\n");
		System.out.println(aMsg);
	}
	
	
	@Override
	public void OnTrigger(String trigger) {
		// TODO Auto-generated method stub
		trace(trigger);

	}

	@Override
	public boolean RhpPluginCleanup() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void RhpPluginFinalCleanup() {
		// TODO Auto-generated method stub

	}
	
	public String get_UserDefinedImplementation(IRPModelElement cellElement, Integer row, Integer column) {
		return cellElement.getOwner().getDescription();
	}
	
	public IRPModelElement get_UserDefinedImplementation1(IRPModelElement cellElement, Integer row, Integer column) 
	{
		
		IRPRequirement req = (IRPRequirement)cellElement;
		if(req==null)
		{
			return null;
		}
		
		req.getAnchoredByMe();
		
		return null;
		
	}

	

}

class FileTypeFilter extends javax.swing.filechooser.FileFilter {

    private String extension;
    private String description;

    public FileTypeFilter(String extension, String description) {
        this.extension = extension;
        this.description = description;
    }

    @Override
    public boolean accept(File file) {
        if (file.isDirectory()) {
            return true;
        }
        return file.getName().toLowerCase().endsWith(extension);
    }

    @Override
    public String getDescription() {
        return description + String.format(" (*%s)", extension);
    }
}
