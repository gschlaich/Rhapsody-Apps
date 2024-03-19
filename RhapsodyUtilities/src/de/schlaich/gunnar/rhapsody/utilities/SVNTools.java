package de.schlaich.gunnar.rhapsody.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPComponent;
import com.telelogic.rhapsody.core.IRPHyperLink;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProfile;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPRequirement;
import com.telelogic.rhapsody.core.IRPUnit;

public class SVNTools {

	
	public static final String SVNCommand = "svn";
	public static final String SVNCommandInfo = "info";
	public static final String SVNParamShowItem = "--show-item";
	public static final String SVNParamRelativeURL = "relative-url";
	public static final String JiraIssueName = "JiraIssue";
	public static final String JiraProfileName = "JiraProfile";
	public static final String SearchPatternUSM = "/USM-(\\d+)";
	public static final String SearchPatternTitle = "USM-\\d+_(.*?)(?=\\/)";
	public static final String HyperLinkStart = "https://jira.bernina.com/browse/";
	
	
	public SVNTools() {
		// TODO Auto-generated constructor stub
	}
	
	public static IRPRequirement getActualJiraElement(IRPModelElement aSelected)
	{

		if(aSelected==null)
		{
			return null;
		}
		
		IRPUnit unit = aSelected.getSaveUnit();
		
		if(unit==null)
		{
			return null;
		}
		
		
		IRPProject project = aSelected.getProject();
		
		if(project == null)
		{
			return null;
		}
		
		String projectpath = unit.getCurrentDirectory();
		File usmFile = new File(projectpath);
		if(usmFile.exists()==false)
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
            while ((line = inputReader.readLine()) != null) {
                output.append(line);
            }

			
            String url = output.toString();
			
			int exitCode = p.waitFor();
			
			Pattern pattern = Pattern.compile(SearchPatternUSM);
	        Matcher matcher = pattern.matcher(url);
	        
	        String jiraId = null;
	        String jiraTitle = null;
	        
	        // Suche nach dem Muster im Eingabestring
	        if (matcher.find()==false)
	        {
	        	System.out.println("Pattern not found");
	        	return null;
	        }
	        	           
	        jiraId = "USM-"+matcher.group(1);

	        pattern = Pattern.compile(SearchPatternTitle);
	        matcher = pattern.matcher(url);
	        if(matcher.find()==false)
	        {
	        	System.out.println("Pattern not found");
	        	return null;
	        }
	        
	        jiraTitle = matcher.group(1).replace('_', ' ');
	        
	        List<IRPProfile> profiles = project.getProfiles().toList();
	        
	        IRPProfile jiraProfile = null;
	        
	        for(IRPProfile profile : profiles)
	        {
	        	if(profile.getName().equals(JiraProfileName))
	        	{
	        		jiraProfile = profile;
	        		break;
	        	}
	        }
	        
	        if(jiraProfile==null)
	        {
	        	return null;
	        }
	        
	        //check if requirement already exists
	        List<IRPRequirement> requirements =  jiraProfile.getNestedElementsByMetaClass("Requirement", 0).toList();
	        
	       
	        
	        for(IRPRequirement j: requirements)
	        {
	        	if(j.getName().equals(jiraId))
	        	{
	        		return j;
	        	}
	        }
	        
	        IRPModelElement jiraModelElement = jiraProfile.addNewAggr("JiraIssue", jiraId);
	        
	        if(jiraModelElement==null)
	        {
	        	return null;
	        }
        
	        if(jiraModelElement instanceof IRPModelElement ==false)
	        {
	        	return null;
	        }
	        
	        IRPRequirement jiraReq = (IRPRequirement) jiraModelElement;
        
	        jiraReq.setRequirementID(jiraId);
	        jiraReq.setSpecification(jiraTitle);
	        
	        //add Hyperlink to jira
	        IRPModelElement hyperlinkElement = jiraReq.addNewAggr("HyperLink", HyperLinkStart+jiraId);
	        
	        if(hyperlinkElement != null)
	        {
	        
		        if(hyperlinkElement instanceof IRPHyperLink)
		        {
		        	IRPHyperLink hyperLink = (IRPHyperLink) hyperlinkElement;
		        	hyperLink.setURL(HyperLinkStart+jiraId);
		        }
	        }
	        
	        return jiraReq;

		}
		catch (InterruptedException | IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	
	public static IRPRequirement setActualJiraIssue(IRPApplication aRhapsody, IRPModelElement aSelected)
	{
		
		IRPRequirement jiraReq = getActualJiraElement(aSelected);
		
		if(jiraReq==null)
		{
			return null;
		}
	   
        List<IRPModelElement> anchors = jiraReq.getAnchoredByMe().toList();
        
        boolean isAnchored = false;
        for(IRPModelElement anchor : anchors)
        {
        	if(anchor.equals(aSelected))
        	{
        		isAnchored = true;
        		break;
        	}
        }
        
        if(isAnchored==false)
        {
        	jiraReq.addAnchor(aSelected);
        }

		return jiraReq;
			
	}
	
	public static void anchorModel(IRPRequirement aJiraReq, IRPModelElement aModelElement)
	{
		List<IRPModelElement> anchors = aJiraReq.getAnchoredByMe().toList();
		
        
        for(IRPModelElement anchor : anchors)
        {
        	if(anchor.equals(aModelElement))
        	{
        		 //System.out.println("Anchor " + aModelElement.getName()+" already exists");
        		return;
        	}
        }
        
        System.out.println("Anchor " + aModelElement.getName());
        aJiraReq.addAnchor(aModelElement);
        

	}
	
	@SuppressWarnings("unchecked")
	public static IRPRequirement anchorAllChanges(IRPApplication aRhapsody, IRPModelElement aSelected)
	{
		
		IRPProject project = aSelected.getProject();
		
		IRPRequirement jiraReq = getActualJiraElement(aSelected);
		
		if(jiraReq == null)
		{
			return null;
		}
		
		IRPComponent component = project.getActiveComponent();
		
		if(component==null)
		{
			return jiraReq;
		}
		
		
		List<IRPModelElement> scopeElements = component.getScopeElements().toList();
		for(IRPModelElement scopeElement : scopeElements)
		{
			
			if(scopeElement.isModified()==1)
			{
				List<IRPModelElement> nestedElements = scopeElement.getNestedElements().toList();
				for(IRPModelElement nestedElement : nestedElements)
				{
					anchorModel(jiraReq, scopeElement);
					if(nestedElement.isModified()==1)
					{
						anchorModel(jiraReq, nestedElement);
					}
				}
				
				
			}
		}
		
		return jiraReq;
		
	}

}
