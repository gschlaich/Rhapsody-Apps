package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.telelogic.rhapsody.core.IRPFile;
import com.telelogic.rhapsody.core.IRPFileFragment;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;

public class JsonFileFragment extends JsonModelElement
{
	
//		IRPModelElement 	getFragmentElement()
//		    get property fragmentElement
//		java.lang.String 	getFragmentText()
//		    get property fragmentText
//		java.lang.String 	getFragmentType()
//		    get property fragmentType
	
	enum FragmentType
	{
		 undefFragment, textFragment, implFragment, specFragment, moduleFragment
	}
	
	@JsonProperty("fragmentElement")
	protected JsonModelElementBase fragmentElement = null;
	@JsonProperty("fragmentText")
	protected String fragmentText = null;
	@JsonProperty("fragmentType")
	protected FragmentType fragmentType = FragmentType.undefFragment;
	
	

	public JsonFileFragment(IRPModelElement aModelElement, int level)
	{
		super(aModelElement, level);
		if (aModelElement == null)
		{
			return;
		}
		
		if (aModelElement instanceof IRPFileFragment)
		{
			IRPFileFragment theFragment = (IRPFileFragment) aModelElement;

			if (theFragment.getFragmentElement() != null)
			{
				fragmentElement = new JsonModelElementBase(theFragment.getFragmentElement());
			}

			fragmentText = theFragment.getFragmentText();
			
			fragmentType = FragmentType.valueOf(theFragment.getFragmentType());

		}
	}

	public JsonFileFragment()
	{
		
	}
	
	public IRPModelElement toModelElement(JsonModelElementBase parent, IRPProject project, ImportMode importMode)
	{
		
		if (importMode == ImportMode.reference)
		{
			return super.toModelElement(parent, project, importMode);
		}
		
		IRPModelElement parentElement = parent.toModelElement(project, ImportMode.reference);
		
		IRPModelElement returnElement = null;
		
		if (parentElement == null)
		{
			trace("JsonFileFragment can only be created under IRPFile parent.");
			return null;
		}
		
		if (parentElement instanceof IRPFile == false)
		{
			trace("JsonFileFragment can only be created under IRPFile parent.");
			return null;
		}
		
		IRPFile parentFile = (IRPFile) parentElement;
		
		//IRPFileFragment theFragment = (IRPFileFragment) super.toModelElement(parent, project);
		
		if (fragmentElement != null)
		{
			
			IRPModelElement existingElement = fragmentElement.toModelElement(project, ImportMode.reference);
			if (existingElement == null)
			{
				trace("JsonFileFragment cannot create fragment because fragmentElement could not be found.");
				return null;
			}
			
			parentFile.addModelElement(existingElement, fragmentType.toString());
			
			List<IRPFileFragment> fragments = parentFile.getFileFragments().toList();
			
			for(IRPFileFragment frag : fragments)
            {
                if (frag.getFragmentElement() != null)
                {
                    if (frag.getFragmentElement().getGUID().equals(existingElement.getGUID()))
                    {
                        returnElement = frag;
                    }
                }
            }
	
		}

		else if (isSet(fragmentText))
		{
			parentFile.addTextElement(fragmentText);
			
			List<IRPFileFragment> fragments = parentFile.getFileFragments().toList();
			
			for (IRPFileFragment frag :fragments)
			{
				if (frag.getFragmentText().equals(fragmentText))
				{
					returnElement = frag;
				}
			}
		}
		
		return returnElement;


		
	}
	
	

}
