package de.schlaich.gunnar.rhapsody.plantUMLView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

import de.schlaich.gunnar.rhapsody.utilities.RhapsodyHelper;

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

public class MermaidGenerator
{

	private String myMermaidDiagram;
	private int myRootLevel;
	private IRPDiagram myDiagram = null;

	private enum viewElement {
		viewNone, viewPublic, viewProtected, viewAll, viewVirtual
	};

	private List<String> myClasses = new ArrayList<String>();

	private boolean addClass(String aClassName)
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

	public MermaidGenerator(IRPModelElement aIRPElement, boolean aGenerateInheritanceHierarchy)
	{

		if (aGenerateInheritanceHierarchy)
		{
			if (aIRPElement instanceof IRPClass)
			{
				StringBuffer mermaidStringBuffer = new StringBuffer();
				mermaidStringBuffer.append(MermaidElements.myStartUML);
				mermaidStringBuffer.append("\n");
				IRPClass irpClass = (IRPClass) aIRPElement;
				generateInheritanceHierarchy(irpClass, mermaidStringBuffer, true, true);
				mermaidStringBuffer.append(MermaidElements.myEndUML);
				myMermaidDiagram = mermaidStringBuffer.toString();
			}

		}
		else
		{
			generateDiagram(aIRPElement);
		}

	}

	@SuppressWarnings("unchecked")
	private void generateInheritanceHierarchy(IRPClassifier aIRPClass, StringBuffer aMermaidStringBuffer,
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
			aMermaidStringBuffer.append(MermaidElements.myPackage);
			aMermaidStringBuffer.append(nameSpace);
			aMermaidStringBuffer.append(MermaidElements.myBraceOpen);
		}
		
		if (className.startsWith("I"))
		{
			aMermaidStringBuffer.append(MermaidElements.myInterface);
		}
		else
		{
			aMermaidStringBuffer.append(MermaidElements.myClass);
		}

		aMermaidStringBuffer.append(className);

		if (nameSpace.isEmpty() == false)
		{
			aMermaidStringBuffer.append(MermaidElements.myBraceClose);
		}
		else
		{
			aMermaidStringBuffer.append("\n");
		}

		List<IRPGeneralization> generalizations = aIRPClass.getGeneralizations().toList();

		if (aGenerateBase)
		{
			for (IRPGeneralization generalization : generalizations)
			{
				IRPClassifier baseClass = generalization.getBaseClass();

				generateInheritanceHierarchy(baseClass, aMermaidStringBuffer, true, false);
				aMermaidStringBuffer.append(baseClass.getName());
				aMermaidStringBuffer.append(MermaidElements.myGeneralization);
				aMermaidStringBuffer.append(className);
				aMermaidStringBuffer.append("\n");

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

					generateInheritanceHierarchy(derivatedClass, aMermaidStringBuffer, false, true);

					aMermaidStringBuffer.append(className);
					aMermaidStringBuffer.append(MermaidElements.myGeneralization);
					aMermaidStringBuffer.append(derivatedClass.getName());
					aMermaidStringBuffer.append("\n");

				}

			}
		}
	}

	private void generateDiagram(IRPModelElement aIRPElement)
	{
		myRootLevel = 2;

		StringBuffer mermaidStringBuffer = new StringBuffer();

		mermaidStringBuffer.append(MermaidElements.myStartUML);
		mermaidStringBuffer.append("\n");

		if (aIRPElement instanceof IRPObjectModelDiagram)
		{
			generate((IRPObjectModelDiagram) aIRPElement, mermaidStringBuffer, myRootLevel);
		}
		else if (aIRPElement instanceof IRPStatechart)
		{
			generate((IRPStatechart) aIRPElement, mermaidStringBuffer, myRootLevel);
		}
		else if (aIRPElement instanceof IRPDiagram)
		{

			generate((IRPDiagram) aIRPElement, mermaidStringBuffer, myRootLevel);
		}
		else
		{
			generateElement(aIRPElement, mermaidStringBuffer, myRootLevel);
		}

		mermaidStringBuffer.append(MermaidElements.myEndUML);

		myMermaidDiagram = mermaidStringBuffer.toString();

	}

	private void generateElement(IRPModelElement aIRPElement, StringBuffer aMermaidStringBuffer, int aLevel)
	{
		if (aIRPElement instanceof IRPClassifier)
		{
			generate((IRPClassifier) aIRPElement, aMermaidStringBuffer, aLevel);
		}
		else if (aIRPElement instanceof IRPPackage)
		{
			generate((IRPPackage) aIRPElement, aMermaidStringBuffer, aLevel);
		}
		else if (aIRPElement instanceof IRPAttribute)
		{
			generate((IRPAttribute) aIRPElement, aMermaidStringBuffer, aLevel);
		}
		else if (aIRPElement instanceof IRPClassifierRole)
		{
			generate((IRPClassifierRole) aIRPElement, aMermaidStringBuffer, aLevel);
		}
		else if (aIRPElement instanceof IRPDependency)
		{
			generate((IRPDependency) aIRPElement, aMermaidStringBuffer, aLevel);
		}
		else if (aIRPElement instanceof IRPGeneralization)
		{
			generate((IRPGeneralization) aIRPElement, aMermaidStringBuffer, aLevel);
		}
		else if (aIRPElement instanceof IRPOperation)
		{
			generate((IRPOperation) aIRPElement, aMermaidStringBuffer, aLevel);
		}
		else if (aIRPElement instanceof IRPRelation)
		{
			generate((IRPRelation) aIRPElement, aMermaidStringBuffer, aLevel);
		}
		else
		{
			System.out.println(aIRPElement.getName());
		}
	}

	public String getMermaidDiagram()
	{
		return myMermaidDiagram;
	}

	private boolean generate(IRPPackage aPackage, StringBuffer aMermaidStringBuffer, long aLevel)
	{
		if (aPackage == null)
		{
			return false;
		}

		aMermaidStringBuffer.append(MermaidElements.myPackage);
		aMermaidStringBuffer.append(aPackage.getName());
		aMermaidStringBuffer.append(MermaidElements.myBraceOpen);
		if (aLevel > 0)
		{

			// check for nested packages
			IRPPackage linkPackage = null;

			List<IRPPackage> packages = aPackage.getPackages().toList();
			for (IRPPackage p : packages)
			{
				generate(p, aMermaidStringBuffer, aLevel - 1);

				if ((aLevel > -1))
				{
					// hidden link between all nested packages
					if (linkPackage != null)
					{
						aMermaidStringBuffer.append(linkPackage.getName());
						if (aLevel == myRootLevel)
						{
							aMermaidStringBuffer.append(MermaidElements.myHiddenDownLink);
						}
						else
						{
							aMermaidStringBuffer.append(MermaidElements.myHiddenLeftLink);
						}

						aMermaidStringBuffer.append(p.getName());
						aMermaidStringBuffer.append("\n");
					}
					linkPackage = p;
				}
			}

		}
		aMermaidStringBuffer.append(MermaidElements.myBraceClose);
		aMermaidStringBuffer.append("\n");

		if (aLevel >= myRootLevel)
		{
			List<IRPDependency> dependencies = aPackage.getDependencies().toList();
			for (IRPDependency d : dependencies)
			{
				generate(d, aMermaidStringBuffer, 0);
			}
		}

		return true;
	}

	@SuppressWarnings("unchecked")
	private boolean generate(IRPObjectModelDiagram aObjectModelDiagramm, StringBuffer aMermaidStringBuffer, long aLevel)
	{
		if (aObjectModelDiagramm == null)
		{
			return false;
		}
		myDiagram = aObjectModelDiagramm;
		

		List<IRPModelElement> elements = aObjectModelDiagramm.getElementsInDiagram().toList();
		for (IRPModelElement element : elements)
		{
			System.out.print(element.getClass().getName());
			System.out.print(" ");
			System.out.println(element.getName());

			if (element instanceof IRPClass)
			{
				generate((IRPClassifier) element, aMermaidStringBuffer, 0);
			}
			if (element instanceof IRPGeneralization)
			{
				generate((IRPGeneralization) element, aMermaidStringBuffer, 0);
			}
			if (element instanceof IRPRelation)
			{
				generate((IRPRelation) element, aMermaidStringBuffer, 0);
			}
			if (element instanceof IRPDependency)
			{
				generate((IRPDependency) element, aMermaidStringBuffer, 0);
			}
			if (element instanceof IRPOperation)
			{

				IRPOperation operation = (IRPOperation) element;
				IRPModelElement owner = element.getOwner();

				if (owner instanceof IRPClassifier)
				{

					String namespace = getNameSpace(owner);

					aMermaidStringBuffer.append(MermaidElements.myClass);
					aMermaidStringBuffer.append(namespace);
					aMermaidStringBuffer.append(MermaidElements.mySeparator);
					aMermaidStringBuffer.append(owner.getName());
					aMermaidStringBuffer.append(MermaidElements.myBraceOpen);

					if (operation.getIsStatic() == 1)
					{
						aMermaidStringBuffer.append("{static} ");
					}
					if (operation.getIsAbstract() == 1)
					{
						aMermaidStringBuffer.append("{abstract} ");
					}

					if (operation.getVisibility().equals("public"))
					{
						aMermaidStringBuffer.append("+ ");
					}
					else if (operation.getVisibility().equals("private"))
					{
						aMermaidStringBuffer.append("- ");
					}
					else if (operation.getVisibility().equals("protected"))
					{
						aMermaidStringBuffer.append("# ");
					}
					else
					{
						aMermaidStringBuffer.append("~ ");
					}

					addStereotype(aMermaidStringBuffer, operation);

					if (operation.getIsDtor() == 1)
					{
						aMermaidStringBuffer.append("\\");
					}
					aMermaidStringBuffer.append(operation.getSignature());
					if (operation.getReturns() != null)
					{
						aMermaidStringBuffer.append(":");
						aMermaidStringBuffer.append(operation.getReturns().getName());
					}
					aMermaidStringBuffer.append(MermaidElements.myBraceClose);
				}
			}
			if (element instanceof IRPAttribute)
			{
				IRPAttribute attribute = (IRPAttribute) element;
				
				
				IRPModelElement o = element.getOwner();
				
				if(o instanceof IRPClassifier == false)
				{
					continue;
				}
				
				IRPClassifier owner = (IRPClassifier)o;
				
				
				

				if (owner instanceof IRPClassifier)
				{

					if(elements.contains(owner)==false)
					{
						continue;
						
					}
					
					
					String namespace = getNameSpace(owner);

					aMermaidStringBuffer.append(MermaidElements.myClass);
					aMermaidStringBuffer.append(namespace);
					aMermaidStringBuffer.append(MermaidElements.mySeparator);
					aMermaidStringBuffer.append(owner.getName());
					aMermaidStringBuffer.append(MermaidElements.myBraceOpen);


					if (attribute.getIsStatic() == 1)
					{
						aMermaidStringBuffer.append("{static} ");
					}

					if (attribute.getVisibility().equals("public"))
					{
						aMermaidStringBuffer.append("+ ");
					}
					else if (attribute.getVisibility().equals("private"))
					{
						aMermaidStringBuffer.append("- ");
					}
					else if (attribute.getVisibility().equals("protected"))
					{
						aMermaidStringBuffer.append("# ");
					}
					else
					{
						aMermaidStringBuffer.append("~ ");
					}

					aMermaidStringBuffer.append(attribute.getName());
					aMermaidStringBuffer.append(":");
					aMermaidStringBuffer.append(attribute.getType().getName());
					addStereotype(aMermaidStringBuffer, attribute);
					aMermaidStringBuffer.append(MermaidElements.myBraceClose);
				}
			}

		}

		return true;
	}

	private void addStereotype(StringBuffer aMermaidStringBuffer, IRPModelElement element)
	{
		List<IRPStereotype> stereotypes = element.getStereotypes().toList();
		if (stereotypes.size() > 0)
		{
			aMermaidStringBuffer.append(MermaidElements.myStereotypeOpen);

			for (IRPStereotype stereotype : stereotypes)
			{
				aMermaidStringBuffer.append(stereotype.getName());
				if (stereotypes.indexOf(stereotype) < stereotypes.size() - 1)
				{
					aMermaidStringBuffer.append(", ");
				}
			}
			
			aMermaidStringBuffer.append(MermaidElements.myStereotypeClose);
		}
	}

	private boolean generate(IRPDiagram aDiagramm, StringBuffer aMermaidStringBuffer, long aLevel)
	{
		if (aDiagramm == null)
		{
			return false;
		}

		myDiagram = aDiagramm;

		Map<Integer, String> messages = new HashMap<Integer, String>();

		List<IRPModelElement> elements = aDiagramm.getElementsInDiagram().toList();

		for (IRPModelElement element : elements)
		{
			System.out.print(element.getClass().getName());
			System.out.print(" ");
			System.out.println(element.getName());

			if (element instanceof IRPClass)
			{
				generate((IRPClassifier) element, aMermaidStringBuffer, 0);
			}
			if (element instanceof IRPGeneralization)
			{
				generate((IRPGeneralization) element, aMermaidStringBuffer, 0);
			}
			if (element instanceof IRPRelation)
			{
				generate((IRPRelation) element, aMermaidStringBuffer, 0);
			}
			if (element instanceof IRPDependency)
			{
				generate((IRPDependency) element, aMermaidStringBuffer, 0);
			}
			if (element instanceof IRPClassifierRole)
			{
				generate((IRPClassifierRole) element, aMermaidStringBuffer, 0);
			}
			if (element instanceof IRPMessage)
			{
				generate((IRPMessage) element, messages, 0);
			}

		}

		for (int i = 1; i <= messages.size(); ++i)
		{
			String message = messages.get(i);

			if (message != null)
			{
				aMermaidStringBuffer.append(message);
			}

		}

		return true;
	}

	private boolean generate(IRPClassifierRole aClassifierRole, StringBuffer aMermaidStringBuffer, long aLevel)
	{
		if (aClassifierRole == null)
		{
			return false;
		}

		String roleType = aClassifierRole.getRoleType();

		if (roleType.equals("ACTOR"))
		{
			aMermaidStringBuffer.append(MermaidElements.myActor);
		}
		else
		{
			aMermaidStringBuffer.append(MermaidElements.myParticipant);
		}

		aMermaidStringBuffer.append("\"");
		aMermaidStringBuffer.append(aClassifierRole.getName());
		aMermaidStringBuffer.append("\"\n");

		return true;
	}

	private boolean generate(IRPMessage aMessage, Map<Integer, String> aMessages, long aLevel)
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
		System.out.println(aMessage.getMessageType());
		if (aMessage.getMessageType().equals("CREATE"))
		{
			messageStringBuffer.append(MermaidElements.myCreateMessage);
		}
		else
		{
			messageStringBuffer.append(MermaidElements.myMessage);
		}

		messageStringBuffer.append("\"");
		messageStringBuffer.append(aMessage.getTarget().getName());
		messageStringBuffer.append("\"");
		messageStringBuffer.append(" : ");
		messageStringBuffer.append(aMessage.getName());
		messageStringBuffer.append(MermaidElements.myBracketOpen);

		Iterator<String> i = aMessage.getActualParameterList().toList().iterator();

		while (i.hasNext())
		{
			messageStringBuffer.append(i.next());
			if (i.hasNext())
			{
				messageStringBuffer.append(", ");
			}
		}

		messageStringBuffer.append(MermaidElements.myBracketClose);
		messageStringBuffer.append("\n");

		aMessages.put(sequenceNr, messageStringBuffer.toString());

		return true;
	}

	private boolean generate(IRPClassifier aClassifier, StringBuffer aMermaidStringBuffer, long aLevel)
	{

		viewElement viewAttribute = viewElement.viewPublic;
		viewElement viewOperation = viewElement.viewPublic;

		if (aLevel <= 0)
		{
			viewAttribute = viewElement.viewNone;
			viewOperation = viewElement.viewVirtual;
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

		if (myDiagram != null)
		{
			List<IRPGraphElement> graphElements = myDiagram.getCorrespondingGraphicElements(aClassifier).toList();
			if (graphElements.size() > 0)
			{
				IRPGraphElement graphElement = graphElements.get(0);

				IRPGraphicalProperty p = graphElement.getGraphicalProperty("OperationsDisplay");
				viewOperation = getViewElement(p);
				p = graphElement.getGraphicalProperty("AttributeDisplay");
				viewAttribute = getViewElement(p);

			}
			else
			{
				// diagram does not show class...
				return false;
			}
		}

		String nameSpace = getNameSpace(aClassifier);

		if (nameSpace.isEmpty() == false)
		{

			aMermaidStringBuffer.append(MermaidElements.myPackage);
			aMermaidStringBuffer.append(nameSpace);
			aMermaidStringBuffer.append(MermaidElements.myBraceOpen);
		}

		if (classifierName.startsWith("I"))
		{
			aMermaidStringBuffer.append(MermaidElements.myInterface);
		}
		else if (aClassifier instanceof IRPClass == false)
		{
			aMermaidStringBuffer.append(MermaidElements.myInterface);
		}
		else
		{
			aMermaidStringBuffer.append(MermaidElements.myClass);
		}

		aMermaidStringBuffer.append(classifierName);
		addStereotype(aMermaidStringBuffer, aClassifier);

		// Template...
		List<IRPTemplateParameter> templateParameters = aClassifier.getTemplateParameters().toList();

		if (templateParameters.size() > 0)
		{
			aMermaidStringBuffer.append("<");

			Iterator<IRPTemplateParameter> i = templateParameters.iterator();
			while (i.hasNext())
			{
				IRPTemplateParameter templateParameter = i.next();
				aMermaidStringBuffer.append(templateParameter.getName());
				if (i.hasNext())
				{
					aMermaidStringBuffer.append(", ");
				}
			}
			aMermaidStringBuffer.append(">");
		}

		aMermaidStringBuffer.append(MermaidElements.myBraceOpen);

		// attributes

		List<IRPAttribute> attributes = aClassifier.getAttributes().toList();
		if (viewAttribute != viewElement.viewNone)
		{
			for (IRPAttribute attribute : attributes)
			{
				if (viewAttribute == viewElement.viewPublic)
				{
					if (attribute.getVisibility().equals("public") == false)
					{
						continue;
					}
				}
				// generate(attribute, aMermaidStringBuffer, aLevel);
			}
		}
		// operations
		List<IRPOperation> operations = aClassifier.getOperations().toList();
		if (viewOperation != viewElement.viewNone)
		{
			for (IRPOperation operation : operations)
			{
				if (viewOperation == viewElement.viewPublic)
				{
					if (operation.getVisibility().equals("public") == false)
					{
						continue;
					}
				}
				else if (viewOperation == viewElement.viewVirtual)
				{
					if (operation.getIsVirtual() != 1)
					{
						continue;
					}
					if (operation.getVisibility().equals("public") == false)
					{
						continue;
					}
				}
				// generate(operation, aMermaidStringBuffer, aLevel);
			}
		}

		aMermaidStringBuffer.append(MermaidElements.myBraceClose);
		if (nameSpace.isEmpty() == false)
		{
			aMermaidStringBuffer.append(MermaidElements.myBraceClose);
		}
		aMermaidStringBuffer.toString();

		if (aLevel >= 1)
		{
			// generalizations
			List<IRPGeneralization> generalizations = aClassifier.getGeneralizations().toList();
			for (IRPGeneralization generlization : generalizations)
			{
				generate(generlization, aMermaidStringBuffer, 0);
			}
			// relations
			List<IRPRelation> relations = aClassifier.getRelations().toList();
			for (IRPRelation relation : relations)
			{
				generate(relation, aMermaidStringBuffer, 0);
			}
			// dependencies
			List<IRPDependency> dependencies = aClassifier.getDependencies().toList();
			for (IRPDependency dependency : dependencies)
			{
				generate(dependency, aMermaidStringBuffer, 0);
			}

			for (IRPAttribute attribute : attributes)
			{
				IRPClassifier classifier = attribute.getType();
				generateDependentClassifier(aClassifier, classifier, aMermaidStringBuffer);
			}
			for (IRPOperation operation : operations)
			{
				// return
				IRPClassifier retClassifier = operation.getReturns();
				generateDependentClassifier(aClassifier, retClassifier, aMermaidStringBuffer);
				List<IRPArgument> arguments = operation.getArguments().toList();
				for (IRPArgument argument : arguments)
				{
					IRPClassifier argClassifier = argument.getType();
					generateDependentClassifier(aClassifier, argClassifier, aMermaidStringBuffer);
				}

			}

			// template Instantiation
			IRPTemplateInstantiation instantiation = aClassifier.getTi();
			if (instantiation != null)
			{
				List<IRPTemplateInstantiationParameter> tips = instantiation.getTemplateInstantiationParameters()
						.toList();

				if (tips.isEmpty() == false)
				{
					IRPModelElement template = aClassifier.getOfTemplate();
					List<IRPTemplateParameter> tps = template.getTemplateParameters().toList();

					generateElement(template, aMermaidStringBuffer, 0);

					aMermaidStringBuffer.append("\n");
					aMermaidStringBuffer.append(aClassifier.getName());
					aMermaidStringBuffer.append(" \" << Instantiation: ");

					for (IRPTemplateInstantiationParameter tip : tips)
					{
						aMermaidStringBuffer.append(tip.getArgValue());
						aMermaidStringBuffer.append(" ");
					}
					aMermaidStringBuffer.append(">> \" ");
					aMermaidStringBuffer.append(MermaidElements.myDependency);
					aMermaidStringBuffer.append(template.getName());
					aMermaidStringBuffer.append("\n");

				}
			}

		}

		return true;
	}

	private viewElement getViewElement(IRPGraphicalProperty p)
	{
		viewElement ret;
		String value = p.getValue();

		if (value.equals("None"))
		{
			ret = viewElement.viewNone;
		}
		else if (value.equals("Explicit"))
		{
			ret = viewElement.viewNone;
		}
		else if (value.equals("All"))
		{
			ret = viewElement.viewAll;
		}
		else if (value.equals("Public"))
		{
			ret = viewElement.viewPublic;
		}
		else
		{
			ret = viewElement.viewNone;
		}

		return ret;
	}

	private void generateDependentClassifier(IRPClassifier aClassifierSource, IRPClassifier aClassifierDependent,
			StringBuffer aMermaidStringBuffer)
	{
		if ((aClassifierDependent instanceof IRPClass) == false)
		{
			return;
		}
		if (generate(aClassifierDependent, aMermaidStringBuffer, 0) == true)
		{
			aMermaidStringBuffer.append(aClassifierSource.getName());
			aMermaidStringBuffer.append(MermaidElements.myDependency);
			aMermaidStringBuffer.append(aClassifierDependent.getName());
			aMermaidStringBuffer.append("\n");
		}
	}

	private void generate(IRPRelation aRelation, StringBuffer aMermaidStringBuffer, int aLevel)
	{
		IRPClassifier otherClass = aRelation.getOtherClass();
		generate(otherClass, aMermaidStringBuffer, 0);
		IRPClassifier classifier = aRelation.getOfClass();
		generate(classifier, aMermaidStringBuffer, 0);
		aMermaidStringBuffer.append(classifier.getName());

		String qualifier = aRelation.getQualifier();
		if (qualifier.equals("") == false)
		{
			aMermaidStringBuffer.append(" \"");
			aMermaidStringBuffer.append(qualifier);
			aMermaidStringBuffer.append("\" ");
		}

		aMermaidStringBuffer.append(getRelationType(aRelation));
		aMermaidStringBuffer.append(" \"");
		aMermaidStringBuffer.append(aRelation.getMultiplicity());
		aMermaidStringBuffer.append("\" ");
		aMermaidStringBuffer.append(otherClass.getName());
		aMermaidStringBuffer.append(" : ");
		aMermaidStringBuffer.append(aRelation.getName());
		aMermaidStringBuffer.append("\n");
	}

	private void generate(IRPGeneralization aGeneralization, StringBuffer aMermaidStringBuffer, long aLevel)
	{
		IRPClassifier baseClass = aGeneralization.getBaseClass();
		generate(baseClass, aMermaidStringBuffer, 0);
		aMermaidStringBuffer.append(baseClass.getName());
		aMermaidStringBuffer.append(MermaidElements.myGeneralization);
		aMermaidStringBuffer.append(aGeneralization.getDerivedClass().getName());
		aMermaidStringBuffer.append("\n");
	}

	private void generate(IRPDependency aDependency, StringBuffer aMermaidStringBuffer, long aLevel)
	{
		IRPModelElement dependent = aDependency.getDependent();
		IRPModelElement dependsOn = aDependency.getDependsOn();

		// at the moment only classes..
		if ((dependent instanceof IRPClass) && (dependsOn instanceof IRPClass))
		{
			generate((IRPClassifier) dependent, aMermaidStringBuffer, 0);
			generate((IRPClassifier) dependsOn, aMermaidStringBuffer, 0);
		}
		else if ((dependent instanceof IRPPackage) && (dependsOn instanceof IRPPackage))
		{
			generate((IRPPackage) dependent, aMermaidStringBuffer, 0);
			generate((IRPPackage) dependsOn, aMermaidStringBuffer, 0);
		}
		else
		{
			return;
		}

		aMermaidStringBuffer.append(dependent.getName());
		
		aMermaidStringBuffer.append(" \"");
		addStereotype(aMermaidStringBuffer, aDependency);
		aMermaidStringBuffer.append("\" ");
		
		
		aMermaidStringBuffer.append(MermaidElements.myDependency);
		aMermaidStringBuffer.append(dependsOn.getName());
		aMermaidStringBuffer.append("\n");

	}

	private String getRelationType(IRPRelation aRelation)
	{
		String relationType = aRelation.getRelationType();
		if (relationType.equals("Association"))
		{
			return MermaidElements.myAssociation;
		}
		if (relationType.equals("Aggregation"))
		{
			return MermaidElements.myAggregation;
		}
		if (relationType.equals("Composition"))
		{
			return MermaidElements.myComposition;
		}
		return "";
	}

	private String getVisibility(String aVisbility)
	{
		if (aVisbility.equals("private"))
		{
			return MermaidElements.myPrivate;
		}
		else if (aVisbility.equals("public"))
		{
			return MermaidElements.myPublic;
		}
		else if (aVisbility.equals("protected"))
		{
			return MermaidElements.myProtected;
		}
		return "";

	}

	private boolean generate(IRPOperation aOperation, StringBuffer aMermaidStringBuffer, long aLevel)
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

		aMermaidStringBuffer.append(getVisibility(aOperation.getVisibility()));

		if (aOperation.getIsAbstract() == 1)
		{
			aMermaidStringBuffer.append(MermaidElements.myAbstractOperation);
		}

		if (aOperation.getIsStatic() == 1)
		{
			aMermaidStringBuffer.append(MermaidElements.myStatic);
		}

		aMermaidStringBuffer.append(operationReturnValue);
		aMermaidStringBuffer.append(" ");
		aMermaidStringBuffer.append(operationName);
		aMermaidStringBuffer.append(MermaidElements.myBracketOpen);
		aMermaidStringBuffer.append(MermaidElements.myBracketClose);

		return true;

	}

	private boolean generate(IRPAttribute aAttribute, StringBuffer aMermaidStringBuffer, long aLevel)
	{
		if (aAttribute == null)
		{
			return false;
		}

		aMermaidStringBuffer.append(getVisibility(aAttribute.getVisibility()));

		if (aAttribute.getIsStatic() == 1)
		{
			aMermaidStringBuffer.append(MermaidElements.myStatic);
		}

		aMermaidStringBuffer.append(aAttribute.getType().getName());
		aMermaidStringBuffer.append(" ");
		aMermaidStringBuffer.append(aAttribute.getName());
		aMermaidStringBuffer.append("\n");

		return true;

	}

	private IRPPackage getPackage(IRPModelElement aElement)
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

	private String getNameSpace(IRPModelElement aElement)
	{
		IRPPackage ret = getPackage(aElement);
		if (ret == null)
		{
			return "";
		}
		return ret.getNamespace();
	}

	private boolean generate(IRPStatechart aStatechart, StringBuffer aMermaidStringBuffer, int aLevel)
	{
		if (aStatechart == null)
		{
			return false;
		}

		List<IRPModelElement> elements = aStatechart.getElementsInDiagram().toList();

		for (IRPModelElement element : elements)
		{
			System.out.println("Element Type: " + element.getClass().getName() + " Name: " + element.getName());

			if (element instanceof IRPStateVertex)
			{
				generate((IRPStateVertex) element, aMermaidStringBuffer, aLevel);
			}

			if (element instanceof IRPTransition)
			{
				IRPTransition transition = (IRPTransition) element;
				if (transition.getItsStatechart().equals(aStatechart))
				{
					generate((IRPTransition) element, aMermaidStringBuffer, aLevel);
				}
			}

		}

		return true;
	}

	private boolean generate(IRPTransition aTransition, StringBuffer aMermaidStringBuffer, int aLevel)
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
				aMermaidStringBuffer.append(MermaidElements.myRootState);
				aMermaidStringBuffer.append(MermaidElements.myTransition);
				aMermaidStringBuffer.append(aTransition.getItsTarget().getName());
				aMermaidStringBuffer.append("\n");
			}
			else
			{
				aMermaidStringBuffer.append(MermaidElements.myState);
				aMermaidStringBuffer.append(defaultState.getName());
				aMermaidStringBuffer.append(MermaidElements.myBraceOpen);
				aMermaidStringBuffer.append(MermaidElements.myRootState);
				aMermaidStringBuffer.append(MermaidElements.myTransition);
				aMermaidStringBuffer.append(aTransition.getItsTarget().getName());
				aMermaidStringBuffer.append(MermaidElements.myBraceClose);
				aMermaidStringBuffer.append("\n");

			}
		}
		else
		{
			aMermaidStringBuffer.append(aTransition.getItsSource().getName());
			aMermaidStringBuffer.append(MermaidElements.myTransition);
			aMermaidStringBuffer.append(aTransition.getItsTarget().getName());
			IRPTrigger trigger = aTransition.getItsTrigger();
			if (trigger != null)
			{
				aMermaidStringBuffer.append(" : ");
				aMermaidStringBuffer.append(aTransition.getItsTrigger().getBody());
			}
			IRPGuard guard = aTransition.getItsGuard();
			if (guard != null)
			{
				if (trigger == null)
				{
					aMermaidStringBuffer.append(" : ");
				}
				aMermaidStringBuffer.append(" [");
				aMermaidStringBuffer.append(guard.getBody());
				aMermaidStringBuffer.append("] ");

			}
			aMermaidStringBuffer.append("\n");
		}

		return true;
	}

	private boolean generate(IRPStateVertex aState, StringBuffer aMermaidStringBuffer, int aLevel)
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

		drawStateOpen(aState.getParent(), aMermaidStringBuffer);

		aMermaidStringBuffer.append(MermaidElements.myState);
		aMermaidStringBuffer.append(aState.getName());
		if (aState instanceof IRPConnector)
		{
			IRPConnector connector = (IRPConnector) aState;
			if (connector.getConnectorType().equals("Condition"))
			{
				aMermaidStringBuffer.append(MermaidElements.myConditional);
			}
			else if (connector.getConnectorType().equals("History"))
			{
				aMermaidStringBuffer.append(MermaidElements.myHistory);
			}
		}
		aMermaidStringBuffer.append("\n");

		drawStateClose(aState.getParent(), aMermaidStringBuffer);

		return true;
	}

	private void drawStateOpen(IRPState aState, StringBuffer aMermaidStringBuffer)
	{
		if (aState.isRoot() == 1)
		{
			return;
		}
		IRPState parentState = aState.getParent();
		drawStateOpen(parentState, aMermaidStringBuffer);

		aMermaidStringBuffer.append(MermaidElements.myState);
		aMermaidStringBuffer.append(aState.getName());
		aMermaidStringBuffer.append(MermaidElements.myBraceOpen);

	}

	private void drawStateClose(IRPState aState, StringBuffer aMermaidStringBuffer)
	{
		if (aState.isRoot() == 1)
		{
			return;
		}
		IRPState parentState = aState.getParent();
		drawStateClose(parentState, aMermaidStringBuffer);

		aMermaidStringBuffer.append(MermaidElements.myBraceClose);

	}

}
