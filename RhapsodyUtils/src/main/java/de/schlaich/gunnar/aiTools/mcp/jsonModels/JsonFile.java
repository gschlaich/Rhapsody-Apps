package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.telelogic.rhapsody.core.IRPFile;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;

public class JsonFile extends JsonModelElement
{

//	  	IRPCollection 	getElements()
//		      get property elements
//		IRPCollection 	getFileFragments()
//		      get property fileFragments
//		IRPCollection 	getFiles()
//		      get property files
//		java.lang.String 	getFileType()
//		      get property fileType
//		java.lang.String 	getImpName(int includingPath)
//		      method getImpName
//		java.lang.String 	getPath(int fullPath)
//		      get property path
//		java.lang.String 	getSpecName(int includingPath)
//		      method getSpecName
//		int 	isEmpty()
//		      method isEmpty
	
	
	enum FileType
	{
		logical, implementation, specification, other
	}
	
	
//	@JsonProperty("elements")
//	protected List<JsonModelElementBase> elements;
//	@JsonProperty("fileFragments")
//	protected List<JsonModelElementBase> fileFragments;
//	@JsonProperty("files")
	protected List<JsonModelElementBase> files;
	@JsonProperty("fileType")
	protected FileType fileType;
	@JsonProperty("path")
	protected String path;
	@JsonProperty("empty")
	protected boolean empty;
	@JsonProperty("impName")
	protected String impName;
	@JsonProperty("specName")
	protected String specName;
	
	
	
	public JsonFile(IRPModelElement aModelElement, int level)
	{
		super(aModelElement, level);
		if (aModelElement == null)
		{
			return;
		}
		if ((aModelElement instanceof IRPFile)==false)
		{
			return;
		}
		
		IRPFile theFile = (IRPFile) aModelElement;
		//elements = convertToJsonModelElementBaseList(theFile.getElements());
		//fileFragments = convertToJsonModelElementBaseList(theFile.getFileFragments());
		files = convertToJsonModelElementBaseList(theFile.getFiles());
		fileType =  FileType.valueOf(theFile.getFileType());
		path = theFile.getPath(0);
		empty = (theFile.isEmpty() != 0);
		impName = theFile.getImpName(0);
	}

	public JsonFile()
	{
		
	}
	
	public IRPModelElement toModelElement(JsonModelElementBase parent, IRPProject project, ImportMode importMode)
	{
		IRPModelElement model = super.toModelElement(parent, project, importMode);
		
		if (model == null)
		{
			return null;
		}
		
		if (model instanceof IRPFile == false)
		{
			return null;
		}
		
		IRPFile theFile = (IRPFile) model;
		
		if (importMode == ImportMode.reference)
		{
			return theFile;
		}
		
		
		
		theFile.setFileType(fileType.name());
		
		// TODO implement setting of the other properties
		
		// elements
		// fileFragments
		// files

		// fileType
		// path
		// empty
		// impName
		// specName

		return theFile;
	}

}
