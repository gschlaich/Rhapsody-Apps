package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPStereotype;

public class JsonStereotype extends JsonClassifier
{

	/*
	java.lang.String 	getIcon()
          Gets the full path for the image file that is associated with this stereotype.
 	int 	getIsNewTerm()
          Checks whether the stereotype is a "new term" stereotype.
 	java.lang.String 	getOfMetaClass()
          Gets the names of the metaclasses that the stereotype can be applied to.
	 */
	@JsonProperty("icon")
	protected String icon = null;
	@JsonProperty("isNewTerm")
	protected boolean isNewTerm = false;
	@JsonProperty("ofMetaClass")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected List<MetaClass> ofMetaClass = null;
	
	public JsonStereotype(IRPModelElement aModelElement, int level)
	{
		super(aModelElement, level);
		
		if (aModelElement == null)
		{
			return;
		}
		if (aModelElement.getClass().getSimpleName().equals("IRPStereotype"))
		{
			IRPStereotype theStereotype = (IRPStereotype) aModelElement;
			icon = theStereotype.getIcon();
			isNewTerm = theStereotype.getIsNewTerm() == 1;
			
			String metaClasses[] = theStereotype.getOfMetaClass().split(",");
			
			ofMetaClass = new ArrayList<MetaClass>();
			
			for (String mc : metaClasses)
            {
                ofMetaClass.add(MetaClass.valueOf(mc));
            }
			
			
		}
		else
		{
			// not a stereotype
		}
	}

	public JsonStereotype()
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

		if (elem instanceof IRPStereotype == false)
		{
			return null;
		}

		if (importMode == ImportMode.reference)
		{
			return elem;
		}

		IRPStereotype theStereotype = (IRPStereotype) elem;
		
		
		theStereotype.setIsNewTerm(this.isNewTerm ? 1 : 0);
		
		
		//first remove all meta classes
		String actualMetaClasses[] = theStereotype.getOfMetaClass().split(",");
		
		for (String mc : actualMetaClasses)
        {
            theStereotype.removeMetaClass(mc);
        }
		
		for (MetaClass mc : this.ofMetaClass)
        {
            theStereotype.addMetaClass(mc.name());
        }
		
		
		return elem;
	}

}
