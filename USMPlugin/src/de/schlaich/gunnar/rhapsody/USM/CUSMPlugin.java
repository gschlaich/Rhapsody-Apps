package de.schlaich.gunnar.rhapsody.USM;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPComponent;
import com.telelogic.rhapsody.core.IRPConfiguration;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPOperation;
import com.telelogic.rhapsody.core.IRPProfile;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPRequirement;
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

public class CUSMPlugin extends RPUserPlugin {

	
	private IRPApplication myRhapsody = null;
	private IRPProfile myProfile = null;
	
	public static final String PlantUmlCmd = "PlantUML";
	public static final String RoundtripCmd = "Operational Roundtrip";
	public static final String SearchElementCmd = "Search Element";
	public static final String SelectRelationCmd = "Select Relation";
	public static final String CreateMsgCmd = "Create Message";
	public static final String MovePackageCmd = "Move Package to SVN";
	public static final String SetActiveCmd = "Set Active";
	public static final String LocateActiveCmd = "Locate Active";
	public static final String SetComponentDependencyCmd="Set Component Dependency";
	public static final String ScriptRunnerCmd = "Scriptrunner";
	public static final String BuildAllCmd = "Build All";
	public static final String JiraIssueCmd = "Jira";
	public static final String JiraChangedCmd = "Jira Changed";
	public static final String StaticCodeAnalyzeCmd = "Code Analyze";
	public static final String StaticCodeAnalyzeClearCmd = "Clear Analyze";
	
	public CUSMPlugin() {
		// TODO Auto-generated constructor stub
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
			
			SVNTools.anchorAllChanges(myRhapsody, selected);
			return;
			
		}
		
		if(menuItem.contains(JiraIssueCmd))
		{
			
			IRPRequirement jiraReq = SVNTools.setActualJiraIssue(myRhapsody, selected);
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
			String result = StaticCodeAnalysis.Analyze(selected, myRhapsody);
			
			if(result==null)
			{
				trace("Analyze failed");
			}
			
			trace("Analyze: "+ result);
			
			return;
		}
		if(menuItem.contains(StaticCodeAnalyzeClearCmd))
		{
			StaticCodeAnalysis.Clear(selected,myRhapsody);
			return;
		}
		
		trace("menue item unknown");
		
	}
	
	private void trace(String aMsg)
	{
		myRhapsody.writeToOutputWindow("Log", "USMPlugin: " + aMsg + "\n");
		System.out.println(aMsg);
	}
	
	
	@Override
	public void OnTrigger(String trigger) {
		// TODO Auto-generated method stub

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
