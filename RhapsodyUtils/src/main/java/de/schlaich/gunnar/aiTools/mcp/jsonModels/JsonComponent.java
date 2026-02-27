package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.telelogic.rhapsody.core.IRPComponent;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;

public class JsonComponent extends JsonUnit
{

//		java.lang.String 	getAdditionalSources()
	@JsonProperty("additionalSources")
	protected String additionalSources = null;
//		    Returns the additional sources defined for the component.
//		java.lang.String 	getBuildType()
	@JsonProperty("buildType")
	protected String buildType = null;
//		    Returns the build type of the component - Library, Executable, or Analysis.
//		

//		java.lang.String 	getIncludePath()
	@JsonProperty("includePath")
	protected String includePath = null;
//		    Returns the include path defined for the component.
//		java.lang.String 	getLibraries()
	@JsonProperty("libraries")
	protected String libraries = null;
//		    get property libraries
//	
//		java.lang.String 	getPath(int fullPath)
	@JsonProperty("path")
	protected String path = null;
//		    get property path
//		
//		
//		IRPCollection 	getScopeElements()
	@JsonProperty("scopeElements")
	protected List<JsonModelElementBase> scopeElements;
//		    Returns a collection of all the model elements in the scope of the component.

//		
//		java.lang.String 	getStandardHeaders()
	@JsonProperty("standardHeaders")
	protected String standardHeaders = null;
//		    Returns the standard headers defined for the component.
//		
//		IRPCollection 	getVariationPoints()
	@JsonProperty("variationPoints")
	protected List<JsonModelElementBase> variationPoints;
//		    Returns a collection of the variation points that are included in the scope of the component.
//		

	public JsonComponent(IRPModelElement aModelElement, int level)
	{
		super(aModelElement, level);
		if (aModelElement == null)
		{
			return;
		}
		if (!(aModelElement instanceof IRPComponent))
		{
			return;
		}

		IRPComponent theComponent = (IRPComponent) aModelElement;

		additionalSources = theComponent.getAdditionalSources();
		buildType = theComponent.getBuildType();
		includePath = theComponent.getIncludePath();
		libraries = theComponent.getLibraries();
		path = theComponent.getPath(1);
		scopeElements = convertToJsonModelElementBaseList(theComponent.getScopeElements());
		standardHeaders = theComponent.getStandardHeaders();
		variationPoints = convertToJsonModelElementBaseList(theComponent.getVariationPoints());

	}

	public JsonComponent()
	{

	}

	
	@Override
	public IRPModelElement createModelElement(IRPModelElement parent)
	{
		if (parent instanceof IRPProject)
		{
			IRPProject theProject = (IRPProject) parent;
			return theProject.addComponent(name);
		}
		else if (parent instanceof IRPComponent)
		{
			IRPComponent theComponent = (IRPComponent) parent;
			return theComponent.addNestedComponent(name);
		}
		return null;
	}
	
	@Override
	public void setAttributes(IRPModelElement aModelElement, IRPProject aProject, ImportMode aImportMode)
    {
        super.setAttributes(aModelElement, aProject, aImportMode);

        if (aModelElement instanceof IRPComponent == false)
        {
            return;
        }

        IRPComponent theComponent = (IRPComponent) aModelElement;

        if (isSet(additionalSources))
        {
            theComponent.setAdditionalSources(additionalSources);
        }
        if (isSet(buildType))
        {
            theComponent.setBuildType(buildType);
        }
        if (isSet(includePath))
        {
            theComponent.setIncludePath(includePath);
        }
        if (isSet(libraries))
        {
            theComponent.setLibraries(libraries);
        }
        if (isSet(standardHeaders))
        {
            theComponent.setStandardHeaders(standardHeaders);
        }
        
		if (scopeElements.isEmpty()==false)
		{
			for (JsonModelElementBase jsonScopeElement : scopeElements)
			{
				IRPModelElement e = jsonScopeElement.getReference(aProject);
				theComponent.addScopeElement(e);
			}
		}
    }
	

}
