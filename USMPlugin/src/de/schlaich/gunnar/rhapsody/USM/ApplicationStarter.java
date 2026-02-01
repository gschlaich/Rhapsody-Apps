package de.schlaich.gunnar.rhapsody.USM;

import java.util.List;

import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.RhapsodyAppServer;

public class ApplicationStarter {

	public ApplicationStarter() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {

		// create an instance of my plug-in
		CUSMPlugin myUSMPlugin = new CUSMPlugin();
		// get Rhapsody application that is currently running
		IRPApplication app = RhapsodyAppServer.getActiveRhapsodyApplication();
		
		
		if(app==null)
		{
			System.out.println("No Rhapsody Running! - exit!");
			
			List idList = RhapsodyAppServer.getActiveRhapsodyApplicationIDList();
		
			for(Object id : idList)
			{
				System.out.println("id: " + id.toString());
			}
			
			return;
		}
		
		// init the plug-in
		myUSMPlugin.RhpPluginInit(app);
		// simulate a call to the plug-in
		myUSMPlugin.RhpPluginInvokeItem();
		
		//Test the commands
		
		//myUSMPlugin.OnMenuItemSelect(CUSMPlugin.SearchElementCmd);
		//myUSMPlugin.OnMenuItemSelect("Util\\PlantUML");
		//myUSMPlugin.OnMenuItemSelect(CUSMPlugin.RoundtripCmd);
		myUSMPlugin.OnMenuItemSelect(CUSMPlugin.SelectRelationCmd);
		//myUSMPlugin.OnMenuItemSelect(CUSMPlugin.BuildAllCmd);
		//myUSMPlugin.OnMenuItemSelect(CUSMPlugin.LocateActiveCmd);
		//myUSMPlugin.OnMenuItemSelect(CUSMPlugin.JiraIssueCmd);
		//myUSMPlugin.OnMenuItemSelect(CUSMPlugin.JiraChangedCmd);
		//myUSMPlugin.OnMenuItemSelect(CUSMPlugin.StaticCodeAnalyzeCmd);
		//myUSMPlugin.OnMenuItemSelect(CUSMPlugin.RunMFileCmd);
		//myUSMPlugin.OnMenuItemSelect(CUSMPlugin.ConvertToRelativePathCmd);
		//myUSMPlugin.OnMenuItemSelect(CUSMPlugin.GenerateInitCodeCmd);
		//myUSMPlugin.OnMenuItemSelect(CUSMPlugin.GetInitCodeOfClassCmd);
		//myUSMPlugin.OnMenuItemSelect(CUSMPlugin.SetInitCodeOfClassCmd);
		//myUSMPlugin.OnMenuItemSelect(CUSMPlugin.DiffHeadCmd);
		//myUSMPlugin.OnMenuItemSelect(CUSMPlugin.DiffTrunkCmd);
		//myUSMPlugin.OnMenuItemSelect(CUSMPlugin.LogCmd);
		//myUSMPlugin.OnMenuItemSelect(CUSMPlugin.ExplorerCmd);
		//myUSMPlugin.OnMenuItemSelect(CUSMPlugin.DiffTrunkReportCmd);
		//myUSMPlugin.OnMenuItemSelect(CUSMPlugin.GetLockCmd);
		//myUSMPlugin.OnMenuItemSelect(CUSMPlugin.ExportTableCmd);
		//myUSMPlugin.OnMenuItemSelect(CUSMPlugin.CreateMsgCmd);
		//myUSMPlugin.OnMenuItemSelect(CUSMPlugin.AddParamMsgCmd);
		//myUSMPlugin.OnMenuItemSelect(CUSMPlugin.AddLibraryCmd);
		//myUSMPlugin.OnMenuItemSelect(CUSMPlugin.AddIncludePathCmd);
		//myUSMPlugin.OnMenuItemSelect(CUSMPlugin.CommitCmd);
		//myUSMPlugin.OnMenuItemSelect(CUSMPlugin.AddConfigCmd);
		//myUSMPlugin.OnMenuItemSelect(CUSMPlugin.AddLibraryLinksCmd);
		//myUSMPlugin.OnMenuItemSelect(CUSMPlugin.ShowHistoryCmd);
		//myUSMPlugin.OnMenuItemSelect(CUSMPlugin.StatisticCmd);
		//myUSMPlugin.OnMenuItemSelect(CUSMPlugin.LOCStatisticCmd);
		//myUSMPlugin.OnMenuItemSelect(CUSMPlugin.CompareOperationHeadCmd);
		//myUSMPlugin.OnMenuItemSelect(CUSMPlugin.CodeComplexityCmd);
		//myUSMPlugin.OnMenuItemSelect(CUSMPlugin.BlameCmd);
		//myUSMPlugin.OnMenuItemSelect(CUSMPlugin.OperationEditorCmd);
		//myUSMPlugin.OnMenuItemSelect(CUSMPlugin.DiffCmd);
		//myUSMPlugin.OnMenuItemSelect(CUSMPlugin.TortoiseLogCmd);
		//myUSMPlugin.OnMenuItemSelect(CUSMPlugin.GeminiDescribeCmd);
		//myUSMPlugin.OnMenuItemSelect(CUSMPlugin.StatisticCmd2);
		//myUSMPlugin.OnMenuItemSelect(CUSMPlugin.UpdateDatabaseCmd);
		//myUSMPlugin.OnMenuItemSelect(CUSMPlugin.MarkdownEditorCmd);
		//myUSMPlugin.OnMenuItemSelect(CUSMPlugin.ReverseEngineeringCmd);
		//myUSMPlugin.OnMenuItemSelect(CUSMPlugin.GoogleTestRoundTripCmd);
		//myUSMPlugin.OnMenuItemSelect(CUSMPlugin.FormatCmd);
		//myUSMPlugin.OnMenuItemSelect(CUSMPlugin.ParseElementCmd);
		//myUSMPlugin.OnMenuItemSelect(CUSMPlugin.MCPStartCmd);
		//myUSMPlugin.OnMenuItemSelect(CUSMPlugin.JsonExportCmd);
		//myUSMPlugin.OnMenuItemSelect(CUSMPlugin.JsonSchemaCmd);
		//myUSMPlugin.OnMenuItemSelect(CUSMPlugin.RunBatchCmd);
		//myUSMPlugin.OnMenuItemSelect(CUSMPlugin.RunAllBatchesCmd);
		//myUSMPlugin.OnMenuItemSelect(CUSMPlugin.ListMetaClassesCmd);
		//myUSMPlugin.OnMenuItemSelect(CUSMPlugin.JsonPasteCmd);
		//myUSMPlugin.OnMenuItemSelect(CUSMPlugin.JsonCopyCmd);
		
	}

}
