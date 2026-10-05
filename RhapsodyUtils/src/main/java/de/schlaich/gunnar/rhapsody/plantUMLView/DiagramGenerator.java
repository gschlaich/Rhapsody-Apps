package de.schlaich.gunnar.rhapsody.plantUMLView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.telelogic.rhapsody.core.IRPArgument;
import com.telelogic.rhapsody.core.IRPAttribute;
import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPClassifierRole;
import com.telelogic.rhapsody.core.IRPConnector;
import com.telelogic.rhapsody.core.IRPDependency;
import com.telelogic.rhapsody.core.IRPDiagram;
import com.telelogic.rhapsody.core.IRPGeneralization;
import com.telelogic.rhapsody.core.IRPGraphElement;
import com.telelogic.rhapsody.core.IRPGraphicalProperty;
import com.telelogic.rhapsody.core.IRPGuard;
import com.telelogic.rhapsody.core.IRPMessage;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPObjectModelDiagram;
import com.telelogic.rhapsody.core.IRPOperation;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPRelation;
import com.telelogic.rhapsody.core.IRPState;
import com.telelogic.rhapsody.core.IRPStateVertex;
import com.telelogic.rhapsody.core.IRPStatechart;
import com.telelogic.rhapsody.core.IRPStereotype;
import com.telelogic.rhapsody.core.IRPTemplateInstantiation;
import com.telelogic.rhapsody.core.IRPTemplateInstantiationParameter;
import com.telelogic.rhapsody.core.IRPTemplateParameter;
import com.telelogic.rhapsody.core.IRPTransition;
import com.telelogic.rhapsody.core.IRPTrigger;

/**
 * Abstrakte Basisklasse für Diagramm-Generatoren (PlantUML, Mermaid, etc.).
 * Enthält die gesamte gemeinsame Logik für alle Diagramm-Generierung.
 * Subklassen müssen nur getElements() implementieren.
 */
public abstract class DiagramGenerator
{
	protected String myDiagram;
	protected int myRootLevel;
	protected IRPDiagram myDiagramElement = null;
	protected List<String> myClasses = new ArrayList<String>();

	protected enum ViewElement
	{
		viewNone, viewPublic, viewProtected, viewAll, viewVirtual
	};

	/**
	 * Abstrakte Methode - muss von Subklassen implementiert werden,
	 * um die spezifischen Syntax-Elemente zurückzugeben
	 */
	protected abstract DiagramElements getElements();

	public DiagramGenerator(IRPModelElement aIRPElement, boolean aGenerateInheritanceHierarchy)
	{
		if (aGenerateInheritanceHierarchy)
		{
			if (aIRPElement instanceof IRPClass)
			{
				StringBuffer diagramStringBuffer = new StringBuffer();
				diagramStringBuffer.append(getElements().getStartUML());
				diagramStringBuffer.append("\n");
				IRPClass irpClass = (IRPClass) aIRPElement;
				generateInheritanceHierarchy(irpClass, diagramStringBuffer, true, true);
				diagramStringBuffer.append(getElements().getEndUML());
				myDiagram = diagramStringBuffer.toString();
			}
		}
		else
		{
			generateDiagram(aIRPElement);
		}
	}

	public String getDiagram()
	{
		return myDiagram;
	}

	protected boolean addClass(String aClassName)
	{
		for (String className : myClasses)
		{
			if (className.equals(aClassName))
			{
				return false;
			}
		}
		myClasses.add(aClassName);
		return true;
	}

	@SuppressWarnings("unchecked")
	protected void generateInheritanceHierarchy(IRPClassifier aIRPClass, StringBuffer aDiagramStringBuffer,
			boolean aGenerateBase, boolean aGenerateDerivated)
	{
		String className = aIRPClass.getName();

		if (addClass(className) == false)
		{
			return;
		}

		String nameSpace = getNameSpace(aIRPClass);

		if (nameSpace.isEmpty() == false)
		{
			aDiagramStringBuffer.append(getElements().getPackage());
			aDiagramStringBuffer.append(nameSpace);
			aDiagramStringBuffer.append(getElements().getBraceOpen());
		}

		if (className.startsWith("I"))
		{
			aDiagramStringBuffer.append(getElements().getInterface());
		}
		else
		{
			aDiagramStringBuffer.append(getElements().getClass_());
		}

		aDiagramStringBuffer.append(className);

		if (nameSpace.isEmpty() == false)
		{
			aDiagramStringBuffer.append(getElements().getBraceClose());
		}
		else
		{
			aDiagramStringBuffer.append("\n");
		}

		List<IRPGeneralization> generalizations = aIRPClass.getGeneralizations().toList();

		if (aGenerateBase)
		{
			for (IRPGeneralization generalization : generalizations)
			{
				IRPClassifier baseClass = generalization.getBaseClass();
				generateInheritanceHierarchy(baseClass, aDiagramStringBuffer, true, false);
				aDiagramStringBuffer.append(baseClass.getName());
				aDiagramStringBuffer.append(getElements().getGeneralization());
				aDiagramStringBuffer.append(className);
				aDiagramStringBuffer.append("\n");
			}
		}

		if (aGenerateDerivated)
		{
			List<IRPModelElement> references = aIRPClass.getReferences().toList();
			for (IRPModelElement reference : references)
			{
				if (reference instanceof IRPGeneralization)
				{
					IRPGeneralization generalization = (IRPGeneralization) reference;
					IRPClassifier derivatedClass = generalization.getDerivedClass();
					generateInheritanceHierarchy(derivatedClass, aDiagramStringBuffer, false, true);
					aDiagramStringBuffer.append(className);
					aDiagramStringBuffer.append(getElements().getGeneralization());
					aDiagramStringBuffer.append(derivatedClass.getName());
					aDiagramStringBuffer.append("\n");
				}
			}
		}
	}

	private void generateDiagram(IRPModelElement aIRPElement)
	{
		myRootLevel = 2;
		StringBuffer diagramStringBuffer = new StringBuffer();
		diagramStringBuffer.append(getElements().getStartUML());
		diagramStringBuffer.append("\n");

		if (aIRPElement instanceof IRPObjectModelDiagram)
		{
			generateObjectModelDiagram((IRPObjectModelDiagram) aIRPElement, diagramStringBuffer, myRootLevel);
		}
		else if (aIRPElement instanceof IRPStatechart)
		{
			generateStatechart((IRPStatechart) aIRPElement, diagramStringBuffer, myRootLevel);
		}
		else if (aIRPElement instanceof IRPDiagram)
		{
			generateGenericDiagram((IRPDiagram) aIRPElement, diagramStringBuffer, myRootLevel);
		}
		else
		{
			generateElement(aIRPElement, diagramStringBuffer, myRootLevel);
		}

		diagramStringBuffer.append(getElements().getEndUML());
		myDiagram = diagramStringBuffer.toString();
	}

	protected void generateElement(IRPModelElement aIRPElement, StringBuffer aDiagramStringBuffer, int aLevel)
	{
		if (aIRPElement instanceof IRPClassifier)
		{
			generateClassifier((IRPClassifier) aIRPElement, aDiagramStringBuffer, aLevel);
		}
		else if (aIRPElement instanceof IRPPackage)
		{
			generatePackage((IRPPackage) aIRPElement, aDiagramStringBuffer, aLevel);
		}
		else if (aIRPElement instanceof IRPAttribute)
		{
			generateAttribute((IRPAttribute) aIRPElement, aDiagramStringBuffer, aLevel);
		}
		else if (aIRPElement instanceof IRPClassifierRole)
		{
			generateClassifierRole((IRPClassifierRole) aIRPElement, aDiagramStringBuffer, aLevel);
		}
		else if (aIRPElement instanceof IRPDependency)
		{
			generateDependency((IRPDependency) aIRPElement, aDiagramStringBuffer, aLevel);
		}
		else if (aIRPElement instanceof IRPGeneralization)
		{
			generateGeneralization((IRPGeneralization) aIRPElement, aDiagramStringBuffer, aLevel);
		}
		else if (aIRPElement instanceof IRPOperation)
		{
			generateOperation((IRPOperation) aIRPElement, aDiagramStringBuffer, aLevel);
		}
		else if (aIRPElement instanceof IRPRelation)
		{
			generateRelation((IRPRelation) aIRPElement, aDiagramStringBuffer, aLevel);
		}
		else
		{
			System.out.println(aIRPElement.getName());
		}
	}

	// ============ KONKRETE IMPLEMENTIERUNGEN - Gemeinsamer Code ============

	protected boolean generatePackage(IRPPackage aPackage, StringBuffer aDiagramStringBuffer, long aLevel)
	{
		if (aPackage == null)
		{
			return false;
		}

		aDiagramStringBuffer.append(getElements().getPackage());
		aDiagramStringBuffer.append(aPackage.getName());
		aDiagramStringBuffer.append(getElements().getBraceOpen());

		if (aLevel > 0)
		{
			IRPPackage linkPackage = null;
			List<IRPPackage> packages = aPackage.getPackages().toList();
			for (IRPPackage p : packages)
			{
				generatePackage(p, aDiagramStringBuffer, aLevel - 1);

				if ((aLevel > -1))
				{
					if (linkPackage != null)
					{
						aDiagramStringBuffer.append(linkPackage.getName());
						if (aLevel == myRootLevel)
						{
							aDiagramStringBuffer.append(getElements().getHiddenDownLink());
						}
						else
						{
							aDiagramStringBuffer.append(getElements().getHiddenLeftLink());
						}
						aDiagramStringBuffer.append(p.getName());
						aDiagramStringBuffer.append("\n");
					}
					linkPackage = p;
				}
			}
		}

		aDiagramStringBuffer.append(getElements().getBraceClose());
		aDiagramStringBuffer.append("\n");

		if (aLevel >= myRootLevel)
		{
			List<IRPDependency> dependencies = aPackage.getDependencies().toList();
			for (IRPDependency d : dependencies)
			{
				generateDependency(d, aDiagramStringBuffer, 0);
			}
		}

		return true;
	}

	protected boolean generateObjectModelDiagram(IRPObjectModelDiagram aObjectModelDiagram,
			StringBuffer aDiagramStringBuffer, long aLevel)
	{
		if (aObjectModelDiagram == null)
		{
			return false;
		}

		myDiagramElement = aObjectModelDiagram;
		List<IRPModelElement> elements = aObjectModelDiagram.getElementsInDiagram().toList();

		for (IRPModelElement element : elements)
		{
			if (element instanceof IRPClass)
			{
				generateClassifier((IRPClassifier) element, aDiagramStringBuffer, 0);
			}
			if (element instanceof IRPGeneralization)
			{
				generateGeneralization((IRPGeneralization) element, aDiagramStringBuffer, 0);
			}
			if (element instanceof IRPRelation)
			{
				generateRelation((IRPRelation) element, aDiagramStringBuffer, 0);
			}
			if (element instanceof IRPDependency)
			{
				generateDependency((IRPDependency) element, aDiagramStringBuffer, 0);
			}
		}

		return true;
	}

	protected boolean generateGenericDiagram(IRPDiagram aDiagram, StringBuffer aDiagramStringBuffer, long aLevel)
	{
		if (aDiagram == null)
		{
			return false;
		}

		myDiagramElement = aDiagram;
		Map<Integer, String> messages = new HashMap<Integer, String>();
		List<IRPModelElement> elements = aDiagram.getElementsInDiagram().toList();

		for (IRPModelElement element : elements)
		{
			if (element instanceof IRPClass)
			{
				generateClassifier((IRPClassifier) element, aDiagramStringBuffer, 0);
			}
			if (element instanceof IRPGeneralization)
			{
				generateGeneralization((IRPGeneralization) element, aDiagramStringBuffer, 0);
			}
			if (element instanceof IRPRelation)
			{
				generateRelation((IRPRelation) element, aDiagramStringBuffer, 0);
			}
			if (element instanceof IRPDependency)
			{
				generateDependency((IRPDependency) element, aDiagramStringBuffer, 0);
			}
			if (element instanceof IRPClassifierRole)
			{
				generateClassifierRole((IRPClassifierRole) element, aDiagramStringBuffer, 0);
			}
			if (element instanceof IRPMessage)
			{
				generateMessage((IRPMessage) element, messages, 0);
			}
		}

		for (int i = 1; i <= messages.size(); ++i)
		{
			String message = messages.get(i);
			if (message != null)
			{
				aDiagramStringBuffer.append(message);
			}
		}

		return true;
	}

	protected boolean generateClassifier(IRPClassifier aClassifier, StringBuffer aDiagramStringBuffer, long aLevel)
	{
		ViewElement viewAttribute = ViewElement.viewPublic;
		ViewElement viewOperation = ViewElement.viewPublic;

		if (aLevel <= 0)
		{
			viewAttribute = ViewElement.viewNone;
			viewOperation = ViewElement.viewVirtual;
		}

		if (aClassifier == null)
		{
			return false;
		}

		String classifierName = aClassifier.getName();

		if (addClass(classifierName) == false)
		{
			return false;
		}

		if (myDiagramElement != null)
		{
			List<IRPGraphElement> graphElements = myDiagramElement.getCorrespondingGraphicElements(aClassifier).toList();
			if (graphElements.size() > 0)
			{
				IRPGraphElement graphElement = graphElements.get(0);
				viewOperation = getViewElement(graphElement.getGraphicalProperty("OperationsDisplay"));
				viewAttribute = getViewElement(graphElement.getGraphicalProperty("AttributeDisplay"));
			}
			else
			{
				return false;
			}
		}

		String nameSpace = getNameSpace(aClassifier);

		if (nameSpace.isEmpty() == false)
		{
			aDiagramStringBuffer.append(getElements().getPackage());
			aDiagramStringBuffer.append(nameSpace);
			aDiagramStringBuffer.append(getElements().getBraceOpen());
		}

		if (classifierName.startsWith("I"))
		{
			aDiagramStringBuffer.append(getElements().getInterface());
		}
		else if (aClassifier instanceof IRPClass == false)
		{
			aDiagramStringBuffer.append(getElements().getInterface());
		}
		else
		{
			aDiagramStringBuffer.append(getElements().getClass_());
		}

		aDiagramStringBuffer.append(classifierName);
		addStereotype(aDiagramStringBuffer, aClassifier);

		List<IRPTemplateParameter> templateParameters = aClassifier.getTemplateParameters().toList();
		if (templateParameters.size() > 0)
		{
			aDiagramStringBuffer.append("<");
			Iterator<IRPTemplateParameter> i = templateParameters.iterator();
			while (i.hasNext())
			{
				IRPTemplateParameter templateParameter = i.next();
				aDiagramStringBuffer.append(templateParameter.getName());
				if (i.hasNext())
				{
					aDiagramStringBuffer.append(", ");
				}
			}
			aDiagramStringBuffer.append(">");
		}

		aDiagramStringBuffer.append(getElements().getBraceOpen());
		aDiagramStringBuffer.append(getElements().getBraceClose());
		if (nameSpace.isEmpty() == false)
		{
			aDiagramStringBuffer.append(getElements().getBraceClose());
		}

		if (aLevel >= 1)
		{
			List<IRPGeneralization> generalizations = aClassifier.getGeneralizations().toList();
			for (IRPGeneralization generalization : generalizations)
			{
				generateGeneralization(generalization, aDiagramStringBuffer, 0);
			}

			List<IRPRelation> relations = aClassifier.getRelations().toList();
			for (IRPRelation relation : relations)
			{
				generateRelation(relation, aDiagramStringBuffer, 0);
			}

			List<IRPDependency> dependencies = aClassifier.getDependencies().toList();
			for (IRPDependency dependency : dependencies)
			{
				generateDependency(dependency, aDiagramStringBuffer, 0);
			}

			List<IRPAttribute> attributes = aClassifier.getAttributes().toList();
			for (IRPAttribute attribute : attributes)
			{
				IRPClassifier classifier = attribute.getType();
				generateDependentClassifier(aClassifier, classifier, aDiagramStringBuffer);
			}

			List<IRPOperation> operations = aClassifier.getOperations().toList();
			for (IRPOperation operation : operations)
			{
				IRPClassifier retClassifier = operation.getReturns();
				generateDependentClassifier(aClassifier, retClassifier, aDiagramStringBuffer);
				List<IRPArgument> arguments = operation.getArguments().toList();
				for (IRPArgument argument : arguments)
				{
					IRPClassifier argClassifier = argument.getType();
					generateDependentClassifier(aClassifier, argClassifier, aDiagramStringBuffer);
				}
			}

			IRPTemplateInstantiation instantiation = aClassifier.getTi();
			if (instantiation != null)
			{
				List<IRPTemplateInstantiationParameter> tips = instantiation.getTemplateInstantiationParameters().toList();

				if (tips.isEmpty() == false)
				{
					IRPModelElement template = aClassifier.getOfTemplate();
					generateElement(template, aDiagramStringBuffer, 0);

					aDiagramStringBuffer.append("\n");
					aDiagramStringBuffer.append(aClassifier.getName());
					aDiagramStringBuffer.append(" \" << Instantiation: ");

					for (IRPTemplateInstantiationParameter tip : tips)
					{
						aDiagramStringBuffer.append(tip.getArgValue());
						aDiagramStringBuffer.append(" ");
					}
					aDiagramStringBuffer.append(">> \" ");
					aDiagramStringBuffer.append(getElements().getDependency());
					aDiagramStringBuffer.append(template.getName());
					aDiagramStringBuffer.append("\n");
				}
			}
		}

		return true;
	}

	protected boolean generateRelation(IRPRelation aRelation, StringBuffer aDiagramStringBuffer, int aLevel)
	{
		IRPClassifier otherClass = aRelation.getOtherClass();
		generateClassifier(otherClass, aDiagramStringBuffer, 0);
		IRPClassifier classifier = aRelation.getOfClass();
		generateClassifier(classifier, aDiagramStringBuffer, 0);

		aDiagramStringBuffer.append(classifier.getName());

		String qualifier = aRelation.getQualifier();
		if (qualifier.equals("") == false)
		{
			aDiagramStringBuffer.append(" \"");
			aDiagramStringBuffer.append(qualifier);
			aDiagramStringBuffer.append("\" ");
		}

		aDiagramStringBuffer.append(getRelationType(aRelation));
		aDiagramStringBuffer.append(" \"");
		aDiagramStringBuffer.append(aRelation.getMultiplicity());
		aDiagramStringBuffer.append("\" ");
		aDiagramStringBuffer.append(otherClass.getName());
		aDiagramStringBuffer.append(" : ");
		aDiagramStringBuffer.append(aRelation.getName());
		aDiagramStringBuffer.append("\n");

		return true;
	}

	protected boolean generateGeneralization(IRPGeneralization aGeneralization, StringBuffer aDiagramStringBuffer, long aLevel)
	{
		IRPClassifier baseClass = aGeneralization.getBaseClass();
		generateClassifier(baseClass, aDiagramStringBuffer, 0);
		aDiagramStringBuffer.append(baseClass.getName());
		aDiagramStringBuffer.append(getElements().getGeneralization());
		aDiagramStringBuffer.append(aGeneralization.getDerivedClass().getName());
		aDiagramStringBuffer.append("\n");

		return true;
	}

	protected boolean generateDependency(IRPDependency aDependency, StringBuffer aDiagramStringBuffer, long aLevel)
	{
		IRPModelElement dependent = aDependency.getDependent();
		IRPModelElement dependsOn = aDependency.getDependsOn();

		if ((dependent instanceof IRPClass) && (dependsOn instanceof IRPClass))
		{
			generateClassifier((IRPClassifier) dependent, aDiagramStringBuffer, 0);
			generateClassifier((IRPClassifier) dependsOn, aDiagramStringBuffer, 0);
		}
		else if ((dependent instanceof IRPPackage) && (dependsOn instanceof IRPPackage))
		{
			generatePackage((IRPPackage) dependent, aDiagramStringBuffer, 0);
			generatePackage((IRPPackage) dependsOn, aDiagramStringBuffer, 0);
		}
		else
		{
			return false;
		}

		aDiagramStringBuffer.append(dependent.getName());
		aDiagramStringBuffer.append(" \"");
		addStereotype(aDiagramStringBuffer, aDependency);
		aDiagramStringBuffer.append("\" ");
		aDiagramStringBuffer.append(getElements().getDependency());
		aDiagramStringBuffer.append(dependsOn.getName());
		aDiagramStringBuffer.append("\n");

		return true;
	}

	protected boolean generateClassifierRole(IRPClassifierRole aClassifierRole, StringBuffer aDiagramStringBuffer, long aLevel)
	{
		if (aClassifierRole == null)
		{
			return false;
		}

		String roleType = aClassifierRole.getRoleType();

		if (roleType.equals("ACTOR"))
		{
			aDiagramStringBuffer.append(getElements().getActor());
		}
		else
		{
			aDiagramStringBuffer.append(getElements().getParticipant());
		}

		aDiagramStringBuffer.append("\"");
		aDiagramStringBuffer.append(aClassifierRole.getName());
		aDiagramStringBuffer.append("\"\n");

		return true;
	}

	protected boolean generateMessage(IRPMessage aMessage, Map<Integer, String> aMessages, long aLevel)
	{
		if (aMessage == null)
		{
			return false;
		}

		String sequenceNrText = aMessage.getSequenceNumber();
		sequenceNrText = sequenceNrText.replaceAll("[^\\d]", "");
		int sequenceNr = Integer.parseInt(sequenceNrText);

		StringBuffer messageStringBuffer = new StringBuffer();

		messageStringBuffer.append("\"");
		messageStringBuffer.append(aMessage.getSource().getName());
		messageStringBuffer.append("\"");

		if (aMessage.getMessageType().equals("CREATE"))
		{
			messageStringBuffer.append(getElements().getCreateMessage());
		}
		else
		{
			messageStringBuffer.append(getElements().getMessage());
		}

		messageStringBuffer.append("\"");
		messageStringBuffer.append(aMessage.getTarget().getName());
		messageStringBuffer.append("\"");
		messageStringBuffer.append(" : ");
		messageStringBuffer.append(aMessage.getName());
		messageStringBuffer.append(getElements().getBracketOpen());

		Iterator<String> i = aMessage.getActualParameterList().toList().iterator();

		while (i.hasNext())
		{
			messageStringBuffer.append(i.next());
			if (i.hasNext())
			{
				messageStringBuffer.append(", ");
			}
		}

		messageStringBuffer.append(getElements().getBracketClose());
		messageStringBuffer.append("\n");

		aMessages.put(sequenceNr, messageStringBuffer.toString());

		return true;
	}

	protected boolean generateOperation(IRPOperation aOperation, StringBuffer aDiagramStringBuffer, long aLevel)
	{
		if (aOperation == null)
		{
			return false;
		}

		String operationName = aOperation.getName();
		IRPClassifier ret = aOperation.getReturns();
		String operationReturnValue = "";
		if (ret != null)
		{
			operationReturnValue = aOperation.getReturns().getName();
		}
		else
		{
			operationReturnValue = aOperation.getReturnTypeDeclaration();
		}

		aDiagramStringBuffer.append(getVisibility(aOperation.getVisibility()));

		if (aOperation.getIsAbstract() == 1)
		{
			aDiagramStringBuffer.append(getElements().getAbstractOperation());
		}

		if (aOperation.getIsStatic() == 1)
		{
			aDiagramStringBuffer.append(getElements().getStatic());
		}

		aDiagramStringBuffer.append(operationReturnValue);
		aDiagramStringBuffer.append(" ");
		aDiagramStringBuffer.append(operationName);
		aDiagramStringBuffer.append(getElements().getBracketOpen());
		aDiagramStringBuffer.append(getElements().getBracketClose());

		return true;
	}

	protected boolean generateAttribute(IRPAttribute aAttribute, StringBuffer aDiagramStringBuffer, long aLevel)
	{
		if (aAttribute == null)
		{
			return false;
		}

		aDiagramStringBuffer.append(getVisibility(aAttribute.getVisibility()));

		if (aAttribute.getIsStatic() == 1)
		{
			aDiagramStringBuffer.append(getElements().getStatic());
		}

		aDiagramStringBuffer.append(aAttribute.getType().getName());
		aDiagramStringBuffer.append(" ");
		aDiagramStringBuffer.append(aAttribute.getName());
		aDiagramStringBuffer.append("\n");

		return true;
	}

	protected boolean generateStatechart(IRPStatechart aStatechart, StringBuffer aDiagramStringBuffer, int aLevel)
	{
		if (aStatechart == null)
		{
			return false;
		}

		List<IRPModelElement> elements = aStatechart.getElementsInDiagram().toList();

		for (IRPModelElement element : elements)
		{
			if (element instanceof IRPStateVertex)
			{
				generateStateVertex((IRPStateVertex) element, aDiagramStringBuffer, aLevel);
			}

			if (element instanceof IRPTransition)
			{
				IRPTransition transition = (IRPTransition) element;
				if (transition.getItsStatechart().equals(aStatechart))
				{
					generateTransition((IRPTransition) element, aDiagramStringBuffer, aLevel);
				}
			}
		}

		return true;
	}

	protected boolean generateTransition(IRPTransition aTransition, StringBuffer aDiagramStringBuffer, int aLevel)
	{
		if (aTransition == null)
		{
			return false;
		}

		if (aTransition.isDefaultTransition() == 1)
		{
			IRPState defaultState = aTransition.getOfState();
			if (defaultState.isRoot() == 1)
			{
				aDiagramStringBuffer.append(getElements().getRootState());
				aDiagramStringBuffer.append(getElements().getTransition());
				aDiagramStringBuffer.append(aTransition.getItsTarget().getName());
				aDiagramStringBuffer.append("\n");
			}
			else
			{
				aDiagramStringBuffer.append(getElements().getState());
				aDiagramStringBuffer.append(defaultState.getName());
				aDiagramStringBuffer.append(getElements().getBraceOpen());
				aDiagramStringBuffer.append(getElements().getRootState());
				aDiagramStringBuffer.append(getElements().getTransition());
				aDiagramStringBuffer.append(aTransition.getItsTarget().getName());
				aDiagramStringBuffer.append(getElements().getBraceClose());
				aDiagramStringBuffer.append("\n");
			}
		}
		else
		{
			aDiagramStringBuffer.append(aTransition.getItsSource().getName());
			aDiagramStringBuffer.append(getElements().getTransition());
			aDiagramStringBuffer.append(aTransition.getItsTarget().getName());
			IRPTrigger trigger = aTransition.getItsTrigger();
			if (trigger != null)
			{
				aDiagramStringBuffer.append(" : ");
				aDiagramStringBuffer.append(aTransition.getItsTrigger().getBody());
			}
			IRPGuard guard = aTransition.getItsGuard();
			if (guard != null)
			{
				if (trigger == null)
				{
					aDiagramStringBuffer.append(" : ");
				}
				aDiagramStringBuffer.append(" [");
				aDiagramStringBuffer.append(guard.getBody());
				aDiagramStringBuffer.append("] ");
			}
			aDiagramStringBuffer.append("\n");
		}

		return true;
	}

	protected boolean generateStateVertex(IRPStateVertex aState, StringBuffer aDiagramStringBuffer, int aLevel)
	{
		if (aState == null)
		{
			return false;
		}

		if (aState instanceof IRPState)
		{
			IRPState state = (IRPState) aState;
			if (state.isRoot() == 1)
			{
				return true;
			}
		}

		drawStateOpen(aState.getParent(), aDiagramStringBuffer);

		aDiagramStringBuffer.append(getElements().getState());
		aDiagramStringBuffer.append(aState.getName());
		if (aState instanceof IRPConnector)
		{
			IRPConnector connector = (IRPConnector) aState;
			if (connector.getConnectorType().equals("Condition"))
			{
				aDiagramStringBuffer.append(getElements().getConditional());
			}
			else if (connector.getConnectorType().equals("History"))
			{
				aDiagramStringBuffer.append(getElements().getHistory());
			}
		}
		aDiagramStringBuffer.append("\n");

		drawStateClose(aState.getParent(), aDiagramStringBuffer);

		return true;
	}

	private void drawStateOpen(IRPState aState, StringBuffer aDiagramStringBuffer)
	{
		if (aState.isRoot() == 1)
		{
			return;
		}
		IRPState parentState = aState.getParent();
		drawStateOpen(parentState, aDiagramStringBuffer);

		aDiagramStringBuffer.append(getElements().getState());
		aDiagramStringBuffer.append(aState.getName());
		aDiagramStringBuffer.append(getElements().getBraceOpen());
	}

	private void drawStateClose(IRPState aState, StringBuffer aDiagramStringBuffer)
	{
		if (aState.isRoot() == 1)
		{
			return;
		}
		IRPState parentState = aState.getParent();
		drawStateClose(parentState, aDiagramStringBuffer);

		aDiagramStringBuffer.append(getElements().getBraceClose());
	}

	// ============ HILFSMETHODEN ============

	protected void addStereotype(StringBuffer aDiagramStringBuffer, IRPModelElement element)
	{
		List<IRPStereotype> stereotypes = element.getStereotypes().toList();
		if (stereotypes.size() > 0)
		{
			aDiagramStringBuffer.append(getElements().getStereotypeOpen());

			for (IRPStereotype stereotype : stereotypes)
			{
				aDiagramStringBuffer.append(stereotype.getName());
				if (stereotypes.indexOf(stereotype) < stereotypes.size() - 1)
				{
					aDiagramStringBuffer.append(", ");
				}
			}

			aDiagramStringBuffer.append(getElements().getStereotypeClose());
		}
	}

	protected String getVisibility(String aVisibility)
	{
		if (aVisibility.equals("private"))
		{
			return getElements().getPrivate();
		}
		else if (aVisibility.equals("public"))
		{
			return getElements().getPublic();
		}
		else if (aVisibility.equals("protected"))
		{
			return getElements().getProtected();
		}
		return "";
	}

	protected IRPPackage getPackage(IRPModelElement aElement)
	{
		IRPPackage ret = null;
		if ((aElement instanceof IRPPackage) == false)
		{
			ret = getPackage(aElement.getOwner());
		}
		else
		{
			ret = (IRPPackage) aElement;
		}
		return ret;
	}

	protected String getNameSpace(IRPModelElement aElement)
	{
		IRPPackage ret = getPackage(aElement);
		if (ret == null)
		{
			return "";
		}
		return ret.getNamespace();
	}

	protected ViewElement getViewElement(IRPGraphicalProperty p)
	{
		ViewElement ret;
		String value = p.getValue();

		if (value.equals("None"))
		{
			ret = ViewElement.viewNone;
		}
		else if (value.equals("Explicit"))
		{
			ret = ViewElement.viewNone;
		}
		else if (value.equals("All"))
		{
			ret = ViewElement.viewAll;
		}
		else if (value.equals("Public"))
		{
			ret = ViewElement.viewPublic;
		}
		else
		{
			ret = ViewElement.viewNone;
		}

		return ret;
	}

	protected void generateDependentClassifier(IRPClassifier aClassifierSource, IRPClassifier aClassifierDependent,
			StringBuffer aDiagramStringBuffer)
	{
		if ((aClassifierDependent instanceof IRPClass) == false)
		{
			return;
		}
		if (generateClassifier(aClassifierDependent, aDiagramStringBuffer, 0) == true)
		{
			aDiagramStringBuffer.append(aClassifierSource.getName());
			aDiagramStringBuffer.append(getElements().getDependency());
			aDiagramStringBuffer.append(aClassifierDependent.getName());
			aDiagramStringBuffer.append("\n");
		}
	}

	protected String getRelationType(IRPRelation aRelation)
	{
		String relationType = aRelation.getRelationType();
		if (relationType.equals("Association"))
		{
			return getElements().getAssociation();
		}
		if (relationType.equals("Aggregation"))
		{
			return getElements().getAggregation();
		}
		if (relationType.equals("Composition"))
		{
			return getElements().getComposition();
		}
		return "";
	}
}
