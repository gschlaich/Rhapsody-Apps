package de.schlaich.gunnar.rhapsody.plantUMLView;

/**
 * PlantUML diagram syntax elements.
 * Implementiert das DiagramElements Interface für PlantUML-spezifische Syntax.
 */
public class PlantUMLElements implements DiagramElements
{
	// Start and End
	@Override
	public String getStartUML(DiagramType aDiagramType)
	{
		return "@startuml\n";
	}

	@Override
	public String getEndUML()
	{
		return "@enduml\n";
	}

	// Class and Interface
	@Override
	public String getClass_()
	{
		return "class ";
	}

	@Override
	public String getInterface()
	{
		return "interface ";
	}

	// Relations and Associations
	@Override
	public String getAssociation()
	{
		return " --> ";
	}

	@Override
	public String getGeneralization()
	{
		return " <|-- ";
	}

	@Override
	public String getComposition()
	{
		return " *--> ";
	}

	@Override
	public String getAggregation()
	{
		return " o--> ";
	}

	// Package and Namespace
	@Override
	public String getPackage()
	{
		return "package ";
	}

	@Override
	public String getNamespace()
	{
		return "namespace ";
	}

	// Brackets and Braces
	@Override
	public String getBraceOpen()
	{
		return " {\n";
	}

	@Override
	public String getBraceClose()
	{
		return "\n}\n";
	}

	@Override
	public String getContent()
	{
		return " : ";
	}

	// Visibility
	@Override
	public String getPublic()
	{
		return "+";
	}

	@Override
	public String getPrivate()
	{
		return "-";
	}

	@Override
	public String getProtected()
	{
		return "#";
	}

	// Brackets
	@Override
	public String getBracketOpen()
	{
		return "(";
	}

	@Override
	public String getBracketClose()
	{
		return ")\n";
	}

	// Relations
	@Override
	public String getRelation()
	{
		return " --> ";
	}

	// Operations and Attributes
	@Override
	public String getAbstractOperation()
	{
		return "{abstract} ";
	}

	@Override
	public String getStatic()
	{
		return "{static} ";
	}

	// Dependencies
	@Override
	public String getDependency()
	{
		return " ..> ";
	}

	// Messages (for sequence diagrams)
	@Override
	public String getMessage()
	{
		return " -> ";
	}

	@Override
	public String getCreateMessage()
	{
		return " --> ";
	}

	@Override
	public String getRMessage()
	{
		return " <- ";
	}

	@Override
	public String getRCreateMessage()
	{
		return " <-- ";
	}

	// Participants and Actors
	@Override
	public String getParticipant()
	{
		return "participant ";
	}

	@Override
	public String getActor()
	{
		return "actor ";
	}

	// States (for state diagrams)
	@Override
	public String getState()
	{
		return "state ";
	}

	@Override
	public String getRootState()
	{
		return "[*] ";
	}

	@Override
	public String getTerminationState()
	{
		return "[*] ";
	}

	@Override
	public String getTransition()
	{
		return " --> ";
	}

	// Hidden Links
	@Override
	public String getHiddenDownLink()
	{
		return " -[hidden]down- ";
	}

	@Override
	public String getHiddenLeftLink()
	{
		return " -[hidden]left- ";
	}

	// Conditionals and History
	@Override
	public String getConditional()
	{
		return " <<choice>> ";
	}

	@Override
	public String getHistory()
	{
		return " <<history>> ";
	}

	// Separators
	@Override
	public String getSeparator()
	{
		return "::";
	}

	@Override
	public String getSetSeparator()
	{
		return "";
	}

	// Stereotypes
	@Override
	public String getStereotypeOpen()
	{
		return " <<";
	}

	@Override
	public String getStereotypeClose()
	{
		return ">> ";
	}
}
