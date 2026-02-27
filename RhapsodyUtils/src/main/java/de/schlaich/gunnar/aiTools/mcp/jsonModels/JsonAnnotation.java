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

//	public IRPModelElement toModelElement(JsonModelElementBase parent, IRPProject project, ImportMode importMode)
//	{
//		IRPModelElement modelElement = super.toModelElement(parent, project, importMode);
//		if (modelElement == null)
//		{
//			return null;
//		}
//
//		if (importMode == ImportMode.reference)
//		{
//			return modelElement;
//		}
//
//
//		setAttributes(modelElement, project, importMode);
//		
//		return modelElement;
//	}
//	
	
	
	@Override
	public void setAttributes(IRPModelElement aModelElement, IRPProject aProject, ImportMode aImportMode)
    {
		super.setAttributes(aModelElement, aProject, aImportMode);

        if (aModelElement instanceof IRPAnnotation == false)
        {
            return;
        }

        IRPAnnotation annotation = (IRPAnnotation) aModelElement;

        annotation.setBody(this.body);

        if (this.isSpecificationRTF)
        {
            annotation.setSpecificationRTF(this.specification);
        }
        else
        {
            annotation.setSpecification(this.specification);
        }

        
        for (JsonModelElementBase jsonElem : anchoredByMe)
        {
            IRPModelElement elem = jsonElem.getReference(aProject);
            if (elem != null)
            {
                annotation.addAnchor(elem);
            }
        }
        
        
    }

}
