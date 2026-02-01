package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPRequirement;

public class JsonRequirement extends JsonAnnotation
{

	// java.lang.String getRequirementID()
	// Returns the ID that was set for the requirement.

	@JsonProperty("requirementID")
	String requirementID = null;

	public JsonRequirement(IRPModelElement aModelElement, int level)
	{
		super(aModelElement, level);
		if (aModelElement == null)
		{
			return;
		}
		if (aModelElement instanceof IRPRequirement)
		{
			IRPRequirement requirement = (IRPRequirement) aModelElement;

			this.requirementID = requirement.getRequirementID();
		}
	}

	public JsonRequirement()
	{
		// TODO Auto-generated constructor stub
	}

	public IRPModelElement toModelElement(JsonModelElementBase parent, IRPProject project, ImportMode importMode)
	{
		IRPModelElement elem = super.toModelElement(parent, project, importMode);

		if (elem == null)
		{
			return null;
		}

		if (elem instanceof IRPRequirement == false)
		{
			return null;
		}

		if (importMode == ImportMode.reference)
		{
			return elem;
		}

		IRPRequirement requirement = (IRPRequirement) elem;

		requirement.setRequirementID(this.requirementID);

		return elem;

	}

}
