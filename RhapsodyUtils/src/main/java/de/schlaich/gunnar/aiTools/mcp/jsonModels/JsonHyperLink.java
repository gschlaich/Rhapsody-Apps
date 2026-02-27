package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.telelogic.rhapsody.core.HYPNameType;
import com.telelogic.rhapsody.core.IRPHyperLink;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;

public class JsonHyperLink extends JsonDependency
{

//		IRPModelElement 	getTarget()
//		      Returns the target model element if the hyperlink points to a model element.
//		java.lang.String 	getTextToDisplay()
//		      Returns the text that is displayed for the hyperlink.
//		char 	getTextToDisplayType()
//		      Returns the type of text that is displayed for the hyperlink.
//		java.lang.String 	getURL()
//		      Returns the target URL if the hyperlink points to a URL.
	
//		static char 	RP_HYP_FREETEXT
//		     show user defined name
//		static char 	RP_HYP_LABELTEXT
//		     show hyperlink target label
//		static char 	RP_HYP_NAMETEXT
//		     show hyperlink target name
//		static char 	RP_HYP_TAGVALUETEXT
//		     show hyperlink target tag value

	
	
	enum TextToDisplayType 
	{
		RP_HYP_FREETEXT,
		RP_HYP_LABELTEXT,
		RP_HYP_NAMETEXT,
		RP_HYP_TAGVALUETEXT
		
	}
	
	@JsonProperty("target")
	protected JsonModelElementBase  target = null;
	@JsonProperty("textToDisplay")
	protected String textToDisplay = null;
	@JsonProperty("textToDisplayType")
	protected TextToDisplayType textToDisplayType = null;
	@JsonProperty("URL")
	protected String URL = null;
	
	
	public JsonHyperLink(IRPModelElement aModelElement, int level)
	{
		super(aModelElement, level);
		if (aModelElement == null)
		{
			return;
		}
		
		if (!(aModelElement instanceof IRPHyperLink))
		{
			return;
		}
		
		IRPHyperLink theHyperLink = (IRPHyperLink) aModelElement;
		
		if (theHyperLink.getTarget() != null)
		{
			target = new JsonModelElementBase(theHyperLink.getTarget());
		}
		textToDisplay = theHyperLink.getTextToDisplay();
		
		char type = theHyperLink.getTextToDisplayType();
		switch (type)
		{
		case HYPNameType.RP_HYP_FREETEXT:
			textToDisplayType = TextToDisplayType.RP_HYP_FREETEXT;
			break;
		case HYPNameType.RP_HYP_LABELTEXT:
			textToDisplayType = TextToDisplayType.RP_HYP_LABELTEXT;
			break;
		case HYPNameType.RP_HYP_NAMETEXT:
			textToDisplayType = TextToDisplayType.RP_HYP_NAMETEXT;
			break;
		case HYPNameType.RP_HYP_TAGVALUETEXT:
			textToDisplayType = TextToDisplayType.RP_HYP_TAGVALUETEXT;
			break;
		}
		
		URL = theHyperLink.getURL();

	}

	public JsonHyperLink()
	{
		
	}
	
	
	
	@Override
	public void setAttributes(IRPModelElement aModelElement, IRPProject aProject, ImportMode aImportMode)
    {
        super.setAttributes(aModelElement, aProject, aImportMode);
        
        if (aModelElement instanceof IRPHyperLink == false)
        {
            return;
        }
        
        IRPHyperLink theHyperLink = (IRPHyperLink) aModelElement;
        
        if (target != null)
        {
            theHyperLink.setTarget(target.getReference(aProject));
        }
        else
        {
            theHyperLink.setTarget(null);
        }
        
        
        String text = null;

        if (isSet(textToDisplay))
        {
            text = textToDisplay;
                
        }
        
        char displayType = 0;

        if (textToDisplayType != null)
        {
            switch (textToDisplayType)
            {
            case RP_HYP_FREETEXT:
                displayType = HYPNameType.RP_HYP_FREETEXT;
                break;
            case RP_HYP_LABELTEXT:
                displayType = HYPNameType.RP_HYP_LABELTEXT;
                break;
            case RP_HYP_NAMETEXT:
                displayType = HYPNameType.RP_HYP_NAMETEXT;
                break;
            case RP_HYP_TAGVALUETEXT:
                displayType = HYPNameType.RP_HYP_TAGVALUETEXT;
                break;
            }
            
            theHyperLink.setDisplayOption(displayType, text);
            
        }
    }

}
