package de.schlaich.gunnar.rhapsody.plantUMLView;

import com.telelogic.rhapsody.core.IRPClassifierRole;
import com.telelogic.rhapsody.core.IRPModelElement;

/**
 * Mermaid Diagram Generator - Minimal Implementation Erbt ALL logic from
 * DiagramGenerator, implements only getElements()
 */
public class MermaidGenerator extends DiagramGenerator
{
	private MermaidElements elements = null;

	public MermaidGenerator(IRPModelElement aIRPElement, boolean aGenerateInheritanceHierarchy)
	{
		super(aIRPElement, aGenerateInheritanceHierarchy);
	}

	@Override
	protected DiagramElements getElements()
	{
		if (elements == null)
		{
			elements = new MermaidElements();
		}
		return elements;
	}

	public String getMermaidDiagram()
	{
		return getDiagram();
	}

	@Override
	protected String getClassifierRoleName(IRPClassifierRole aClassifierRole)
	{
		String name = aClassifierRole.getName();
		if (name.startsWith(":"))
		{
			name = name.substring(1);
		}
		return name;
	}

}
