package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPUnit;

import de.schlaich.gunnar.aiTools.mcp.jsonModels.JsonModelElementBase.ImportMode;

public class JsonUnit extends JsonModelElement
{

	enum AddToModelMode {
		UNKNOWN, AS_REFERENCE,
		// A reference to the unit should be added to the model (unit cannot be
		// modified).
		AS_UNIT_WITH_COPY,
		// The unit should be added to the model and its file should be copied to the
		// project directory.
		AS_UNIT_WITHOUT_COPY
		// The unit should be added to the model as an editable unit, but its file
		// should not be copied to the project directory.
	}

	// int getAddToModelMode()
	// Returns an indication of how the unit was added to the model.
	// java.lang.String getCMHeader()
	// Returns the header used by the Configuration Management tool for the unit.
	// int getCMState()
	// Returns the configuration management state of the unit.
	// java.lang.String getCurrentDirectory()
	// Gets the name of the directory that contains the file used to store the unit.
	// java.lang.String getFilename()
	// Gets the name of the file used to store the unit.
	// int getIncludeInNextLoad()
	// Checks whether the unit is going to be loaded the next time the model is
	// loaded.
	// int getIsStub()
	// Checks whether the unit is currently unloaded.
	// java.lang.String getLanguage()
	// Gets the language of the unit.
	// java.lang.String getLastModifiedTime()
	// Returns the time at which the file representing the unit was last modified.
	// IRPCollection getNestedSaveUnits()
	// Returns a collection of any sub-elements of the unit that were saved as
	// individual files.
	// int getNestedSaveUnitsCount()
	// Returns the number of sub-elements of the unit that were saved as individual
	// files.
	// IRPCollection getStructureDiagrams()
	// Returns a collection of any structure diagrams that are sub-elements of the
	// unit.
	// int isReadOnly()
	// Checks whether the file used to store the unit is read-only.
	// int isReferenceUnit()
	// Checks whether the unit was added to the model as a reference.
	// int isSeparateSaveUnit()
	// Checks whether the current IRPUnit object is saved in its own file.

	@JsonProperty("addToModelMode")
	protected AddToModelMode addToModelMode = AddToModelMode.UNKNOWN;
	@JsonProperty("cMHeader")
	protected String cMHeader = "";
	@JsonProperty("cMState")
	protected int cMState = 0;
	@JsonProperty("currentDirectory")
	protected String currentDirectory = "";
	@JsonProperty("filename")
	protected String filename = null;
	@JsonProperty("includeInNextLoad")
	protected boolean includeInNextLoad = false;
	@JsonProperty("isStub")
	protected boolean isStub = false;
	@JsonProperty("language")
	protected String language = null;
	@JsonProperty("lastModifiedTime")
	protected String lastModifiedTime = null;

	// protected List<JsonModelElementBase> nestedSaveUnits = null;
	// protected int nestedSaveUnitsCount = 0;
	// protected List<JsonModelElementBase> structureDiagrams = null;

	@JsonProperty("isReadOnly")
	protected boolean isReadOnly = false;
	@JsonProperty("isReferenceUnit")
	protected boolean isReferenceUnit = false;
	@JsonProperty("isSeparateSaveUnit")
	protected boolean isSeparateSaveUnit = false;

	public JsonUnit(IRPModelElement aModelElement, int level)
	{
		super(aModelElement, level);
		if (aModelElement == null)
		{
			return;
		}

		if (!(aModelElement instanceof IRPUnit))
		{
			return;
		}

		IRPUnit theUnit = (IRPUnit) aModelElement;
		
		
		if(theUnit.isSeparateSaveUnit()==0)
		{
			trace( name + "("+metaClass+"): unit is not a separate save unit.");
			return;
		}
		

		switch (theUnit.getAddToModelMode())
		{
		case IRPApplication.AddToModel_Mode.AS_REFERENCE:
			addToModelMode = AddToModelMode.AS_REFERENCE;
			break;
		case IRPApplication.AddToModel_Mode.AS_UNIT_WITH_COPY:
			addToModelMode = AddToModelMode.AS_UNIT_WITH_COPY;
			break;
		case IRPApplication.AddToModel_Mode.AS_UNIT_WITHOUT_COPY:
			addToModelMode = AddToModelMode.AS_UNIT_WITHOUT_COPY;
			break;
		default:
			addToModelMode = AddToModelMode.UNKNOWN;
		}
		
		cMHeader = theUnit.getCMHeader();
		cMState = theUnit.getCMState();
		try
		{
			currentDirectory = theUnit.getCurrentDirectory();
		}
		catch (Exception e)
		{
			trace( name + "("+metaClass+"): error retrieving current of  directory: " + e.getMessage());
		}
		
		filename = theUnit.getFilename();
		includeInNextLoad = theUnit.getIncludeInNextLoad() == 1;
		isStub = theUnit.getIsStub() == 1;
		language = theUnit.getLanguage();
		lastModifiedTime = theUnit.getLastModifiedTime();
		isReadOnly = theUnit.isReadOnly() == 1;
		isReferenceUnit = theUnit.isReferenceUnit() == 1;
		isSeparateSaveUnit = theUnit.isSeparateSaveUnit() == 1;

	}

	public JsonUnit()
	{
		
	}
	
	/*
	
	
	public IRPModelElement toModelElement(JsonModelElementBase parent, IRPProject project, ImportMode aImportMode)
	{
		
		IRPModelElement parentElement = parent.getReference(project);
		IRPModelElement model = null;
		
		if (aImportMode == ImportMode.reference)
		{
			return getReference(project);
		}
		
		if (aImportMode == ImportMode.create)
		{
			model = createModelElement(parentElement);
		}
		
		
		if (model == null)
		{
			return null;
		}
		
		if (model instanceof IRPUnit == false)
		{
			return null;
		}
		
		
		IRPUnit theUnit = (IRPUnit) model;
		
		setAttributes(theUnit, project, aImportMode);

		return theUnit;
	}
	*/
	
	@Override
	public void setAttributes(IRPModelElement aModelElement, IRPProject aProject, ImportMode aImportMode)
	{
		super.setAttributes(aModelElement, aProject, aImportMode);

		if (aModelElement instanceof IRPUnit == false)
		{
			return;
		}

		IRPUnit theUnit = (IRPUnit) aModelElement;

		theUnit.setIncludeInNextLoad(includeInNextLoad ? 1 : 0);
		// theUnit.setReadOnly(isReadOnly ? 1 : 0);
		// theUnit.setLanguage(language,0);
		// theUnit.setFilename(filename);
		// theUnit.setCMHeader(cMHeader);
		theUnit.setSeparateSaveUnit(isSeparateSaveUnit ? 1 : 0);
		// theUnit.setUnitPath(currentDirectory);

	}

}
