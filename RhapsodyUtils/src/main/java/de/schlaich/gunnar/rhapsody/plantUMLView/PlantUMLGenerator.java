package de.schlaich.gunnar.rhapsody.plantUMLView;

import com.telelogic.rhapsody.core.IRPModelElement;

/**
 * PlantUML Diagram Generator - Minimal Implementation
 * Erbt ALL logic from DiagramGenerator, implements only getElements()
 */
public class PlantUMLGenerator extends DiagramGenerator
{
	private PlantUMLElements elements = null;

	public PlantUMLGenerator(IRPModelElement aIRPElement, boolean aGenerateInheritanceHierarchy)
	{
		super(aIRPElement, aGenerateInheritanceHierarchy);
	}

	@Override
	protected DiagramElements getElements()
	{
		if(elements == null)
		{
			elements = new PlantUMLElements();
		}
		return elements;
	}

	public String getPlantUml()
	{
		return getDiagram();
	}
}