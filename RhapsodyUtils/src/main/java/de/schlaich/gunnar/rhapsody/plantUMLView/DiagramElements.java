package de.schlaich.gunnar.rhapsody.plantUMLView;

/**
 * Interface für Diagramm-Syntax-Elemente.
 * Ermöglicht verschiedene Implementierungen (PlantUML, Mermaid, etc.)
 */
public interface DiagramElements
{
	
	enum DiagramType {
		CLASS_DIAGRAM,
		SEQUENCE_DIAGRAM,
		STATE_CHART
	}
	
	// Start und End
	String getStartUML(DiagramType aDiagramType);
	String getEndUML();

	// Klasse und Interface
	String getClass_();
	String getInterface();

	// Relationen und Assoziationen
	String getAssociation();
	String getGeneralization();
	String getComposition();
	String getAggregation();

	// Package und Namespace
	String getPackage();
	String getNamespace();

	// Klammern und Braces
	String getBraceOpen();
	String getBraceClose();
	String getContent();

	// Sichtbarkeit
	String getPublic();
	String getPrivate();
	String getProtected();

	// Brackets
	String getBracketOpen();
	String getBracketClose();

	// Relationen
	String getRelation();

	// Operationen und Attribute
	String getAbstractOperation();
	String getStatic();

	// Abhängigkeiten
	String getDependency();

	// Nachrichten (für Sequenzdiagramme)
	String getMessage();
	String getCreateMessage();
	String getRMessage();
	String getRCreateMessage();

	// Teilnehmer und Aktoren
	String getParticipant();
	String getActor();

	// States (für Zustandsdiagramme)
	String getState();
	String getRootState();
	String getTerminationState();
	String getTransition();

	// Hidden Links
	String getHiddenDownLink();
	String getHiddenLeftLink();

	// Conditionals und History
	String getConditional();
	String getHistory();

	// Separators
	String getSeparator();
	String getSetSeparator();

	// Stereotypes
	String getStereotypeOpen();
	String getStereotypeClose();
}
