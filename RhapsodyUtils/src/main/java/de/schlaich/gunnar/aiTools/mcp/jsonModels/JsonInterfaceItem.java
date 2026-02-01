package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.telelogic.rhapsody.core.IRPInterfaceItem;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;

public class JsonInterfaceItem extends JsonClassifier
{


//	java.lang.String 	getSignature()
//    	Returns the signature of the operation.
	
	@JsonProperty("signature")
	protected String signature = null;

	public JsonInterfaceItem(IRPModelElement aModelElement, int level)
	{
		super(aModelElement, level);
		if (aModelElement == null)
		{
			return;
		}
		if (aModelElement instanceof IRPInterfaceItem)
		{
			IRPInterfaceItem theInterfaceItem = (IRPInterfaceItem) aModelElement;
			signature = theInterfaceItem.getSignature();
		}
	}

	public JsonInterfaceItem()
	{
		// TODO Auto-generated constructor stub
	}
	
	public IRPModelElement toModelElement(JsonModelElementBase parent, IRPProject project, ImportMode importMode)
	{
		IRPInterfaceItem theInterfaceItem = (IRPInterfaceItem) super.toModelElement(parent, project, importMode);
		// signature is read-only
		return theInterfaceItem;
	}

}
