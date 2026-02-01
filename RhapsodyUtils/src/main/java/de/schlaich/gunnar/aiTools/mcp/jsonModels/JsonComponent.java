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

	public IRPModelElement toModelElement(JsonModelElementBase parent, IRPProject project, ImportMode importMode)
	{
		IRPModelElement model = super.toModelElement(parent, project, importMode);

		if (model == null)
		{
			return null;
		}

		if (model instanceof IRPComponent == false)
		{
			return null;
		}
		
		if (importMode == ImportMode.reference)
		{
			return model;
		}

		IRPComponent theComponent = (IRPComponent) model;

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

		for (JsonModelElementBase jsonElem : scopeElements)
		{
			IRPModelElement elem = jsonElem.toModelElement( project, ImportMode.reference);
			theComponent.addScopeElement(elem);
		}

		// variants?

		return model;
	}

}
