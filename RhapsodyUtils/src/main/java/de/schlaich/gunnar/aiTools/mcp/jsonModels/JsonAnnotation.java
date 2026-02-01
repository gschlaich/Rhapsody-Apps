package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.telelogic.rhapsody.core.IRPAnnotation;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;

public class JsonAnnotation extends JsonUnit
{

//		IRPCollection 	getAnchoredByMe()
//		      Gets the list of model elements that are anchored to the annotation.
//		java.lang.String 	getBody()
//		      Gets the text of the specification for the annotation.
//		java.lang.String 	getSpecification()
//		      Gets the text of the specification for the annotation.
//		java.lang.String 	getSpecificationRTF()
//		      Returns the specification of the annotation in RTF format.
//	  	int 	isSpecificationRTF()
//	  			Checks whether the specification is in RTF format

	@JsonProperty("anchoredByMe")
	protected List<JsonModelElementBase> anchoredByMe = null;
	@JsonProperty("body")
	protected String body = null;
	@JsonProperty("specification")
	protected String specification = null;
	@JsonProperty("isSpecificationRTF")
	protected boolean isSpecificationRTF = false;

	public JsonAnnotation(IRPModelElement aModelElement, int level)
	{
		super(aModelElement, level);
		if (aModelElement == null)
		{
			return;
		}
		if (aModelElement instanceof IRPAnnotation)
		{
			IRPAnnotation annotation = (IRPAnnotation) aModelElement;

			this.body = annotation.getBody();

			this.isSpecificationRTF = annotation.isSpecificationRTF() != 0;

			if (isSpecificationRTF)
			{
				this.specification = annotation.getSpecificationRTF();
			}
			else
			{
				this.specification = annotation.getSpecification();
			}

			anchoredByMe = convertToJsonModelElementBaseList(annotation.getAnchoredByMe());

		}
	}

	public JsonAnnotation()
	{

	}

	public IRPModelElement toModelElement(JsonModelElementBase parent, IRPProject project, ImportMode importMode)
	{
		IRPModelElement modelElement = super.toModelElement(parent, project, importMode);
		if (modelElement == null)
		{
			return null;
		}

		if (importMode == ImportMode.reference)
		{
			return modelElement;
		}

		if (modelElement instanceof IRPAnnotation == false)
		{
			return null;
		}

		IRPAnnotation annotation = (IRPAnnotation) modelElement;

		annotation.setBody(this.body);

		if (this.isSpecificationRTF)
		{
			annotation.setSpecificationRTF(this.specification);
		}
		else
		{
			annotation.setSpecification(this.specification);
		}

		if (importMode == ImportMode.update)
		{
			List<IRPModelElement> existingAnchors = annotation.getAnchoredByMe().toList();
			for (IRPModelElement anchor : existingAnchors)
			{
				annotation.removeAnchor(anchor);
			}
		}
		else if (importMode == ImportMode.create)
		{
			if (annotation.getAnchoredByMe().toList().size() > 0)
			{
				trace("On creation, the annotation must not have any anchored elements yet");
				return modelElement;
			}
		}

		for (JsonModelElementBase jsonElem : anchoredByMe)
		{
			IRPModelElement elem = jsonElem.toModelElement( project, ImportMode.reference);
			if (elem != null)
			{
				annotation.addAnchor(elem);
			}
		}

		return modelElement;
	}

}
