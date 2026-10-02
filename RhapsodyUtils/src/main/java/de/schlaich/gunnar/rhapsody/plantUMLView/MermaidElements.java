package de.schlaich.gunnar.rhapsody.plantUMLView;

/**
 * Mermaid diagram syntax elements.
 * This class provides the same diagram elements as PlantUMLElements, but in Mermaid syntax.
 */
public class MermaidElements
{
	// Start and End
	public static String myStartUML = "classDiagram\n";
	public static String myEndUML = "\n";
	
	// Class and Interface
	public static String myClass = "class ";
	public static String myInterface = "class ";
	
	// Relations and Associations
	public static String myAssociation = " --> ";
	public static String myGeneralization = " --|> ";
	public static String myComposition = " *-- ";
	public static String myAggregation = " o-- ";
	
	// Package and Namespace
	public static String myPackage = "namespace ";
	public static String myNamespace = "namespace ";
	
	// Brackets and Braces
	public static String myBraceOpen = " {\n";
	public static String myBraceClose = "\n}\n";
	public static String myContent = " : ";
	
	// Visibility
	public static String myPublic = "+";
	public static String myPrivate = "-";
	public static String myProtected = "#";
	
	// Brackets
	public static String myBracketOpen = "(";
	public static String myBracketClose = ")\n";
	
	// Relations
	public static String myRelation = " --> ";
	
	// Operations and Attributes
	public static String myAbstractOperation = " {abstract} ";
	public static String myStatic = " {static} ";
	
	// Dependencies
	public static String myDependency = " -.-> ";
	
	// Messages (for sequence diagrams)
	public static String myMessage = " -> ";
	public static String myCreateMessage = " --> ";
	public static String myRMessage = " <- ";
	public static String myRCreateMessage = " <-- ";
	
	// Participants and Actors
	public static String myParticipant = "participant ";
	public static String myActor = "actor ";
	
	// States (for state diagrams)
	public static String myState = "state ";
	public static String myRootState = "[*] ";
	public static String myTerminationState = "[*] ";
	public static String myTransition = " --> ";
	
	// Hidden Links
	public static String myHiddenDownLink = " -.- ";
	public static String myHiddenLeftLink = " -.- ";
	
	// Conditionals and History
	public static String myConditional = " <<choice>> ";
	public static String myHistory = " <<choice>> ";
	
	// Separators
	public static String mySeparator = "::";
	public static String mySetSeparator = "";
	
	// Stereotypes
	public static String myStereotypeOpen = " <<";
	public static String myStereotypeClose = ">> ";
}
