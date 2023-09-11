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

public class PlantUMLGenerator {
	
	
	
	private String myPlantUml;
	private int myRootLevel;
	private IRPDiagram myDiagram = null;
	
	private enum viewElement { viewNone, viewPublic, viewProtected, viewAll, viewVirtual };

	
	private List<String> myClasses = new ArrayList<String>();
			
	private boolean addClass(String aClassName)
	{
		for(String className : myClasses )
		{
			if(className.equals(aClassName))
			{
				return false;
			}
		}
		
		myClasses.add(aClassName);
		return true;
	}
	
	public PlantUMLGenerator(IRPModelElement aIRPElement, boolean aGenerateInheritanceHierarchy) 
	{
		
		if(aGenerateInheritanceHierarchy)
		{
			if(aIRPElement instanceof IRPClass)
			{
				StringBuffer plantStringBuffer = new StringBuffer();
				plantStringBuffer.append(PlantUMLElements.myStartUML);
				IRPClass irpClass = (IRPClass) aIRPElement;
				generateInheritanceHierarchy(irpClass, plantStringBuffer,true,true);
				plantStringBuffer.append(PlantUMLElements.myEndUML);
				myPlantUml = plantStringBuffer.toString();
			}
			
			
		}
		else
		{
			generateDiagram(aIRPElement);
		}
		
		
	}
	
	@SuppressWarnings("unchecked")
	private void generateInheritanceHierarchy(IRPClassifier aIRPClass, StringBuffer aPlantStringBuffer, boolean aGenerateBase, boolean aGenerateDerivated )
	{
		
		
		String className = aIRPClass.getName();
		
		if(addClass(className)==false)
		{
			return;
		}
		
		
		String nameSpace = getNameSpace(aIRPClass);
		
		
		if(nameSpace.isEmpty()==false)
		{
		
			aPlantStringBuffer.append(PlantUMLElements.myNamespace);
			aPlantStringBuffer.append(nameSpace);
			aPlantStringBuffer.append(PlantUMLElements.myBraceOpen);
		}
		if(className.startsWith("I"))
		{
			aPlantStringBuffer.append(PlantUMLElements.myInterface);
		}
		else
		{
			aPlantStringBuffer.append(PlantUMLElements.myClass);
		}
		
		aPlantStringBuffer.append(className);
		
		if(nameSpace.isEmpty()==false)
		{
			aPlantStringBuffer.append(PlantUMLElements.myBraceClose);
		}
		else
		{
			aPlantStringBuffer.append("\n");
		}
		
		List<IRPGeneralization> generalizations = aIRPClass.getGeneralizations().toList();
		
		if(aGenerateBase)
		{
			for(IRPGeneralization generalization : generalizations)
			{
				IRPClassifier baseClass =  generalization.getBaseClass();
				
					generateInheritanceHierarchy(baseClass, aPlantStringBuffer, true, false);		
					aPlantStringBuffer.append(baseClass.getName());
					aPlantStringBuffer.append(PlantUMLElements.myGeneralization);
					aPlantStringBuffer.append(className);
					aPlantStringBuffer.append("\n");
	
			}
		}
		
		if(aGenerateDerivated)
		{	
			List<IRPModelElement> references = aIRPClass.getReferences().toList();
			for(IRPModelElement reference : references)
			{
				if(reference instanceof IRPGeneralization)
				{
					IRPGeneralization generalization = (IRPGeneralization)reference;
					
					IRPClassifier derivatedClass = generalization.getDerivedClass();

					generateInheritanceHierarchy(derivatedClass, aPlantStringBuffer, false, true);
					
					aPlantStringBuffer.append(className);
					aPlantStringBuffer.append(PlantUMLElements.myGeneralization);
					aPlantStringBuffer.append(derivatedClass.getName());
					aPlantStringBuffer.append("\n");
					
				}
				
			}
		}
	}
			

	
	
	private void generateDiagram(IRPModelElement aIRPElement)
	{
		myRootLevel = 2;
		
		
		
		StringBuffer plantStringBuffer = new StringBuffer();
		
		
		plantStringBuffer.append(PlantUMLElements.myStartUML);
		if(aIRPElement instanceof IRPObjectModelDiagram)
		{
			generate((IRPObjectModelDiagram) aIRPElement, plantStringBuffer, myRootLevel);
		}
		else if(aIRPElement instanceof IRPStatechart)
		{
			generate((IRPStatechart) aIRPElement, plantStringBuffer, myRootLevel);
		}
		else if(aIRPElement instanceof IRPDiagram)
		{
			
			generate((IRPDiagram) aIRPElement, plantStringBuffer, myRootLevel);
		}
		else
		{
			generateElement(aIRPElement, plantStringBuffer, myRootLevel);
		}
		
		plantStringBuffer.append(PlantUMLElements.myEndUML);
		
		myPlantUml = plantStringBuffer.toString();
		
	}

	private void generateElement(IRPModelElement aIRPElement, StringBuffer aPlantStringBuffer, int aLevel) {
		if(aIRPElement instanceof IRPClassifier)
		{
			generate((IRPClassifier) aIRPElement, aPlantStringBuffer, aLevel);
		}		
		else if(aIRPElement instanceof IRPPackage)
		{
			generate((IRPPackage)aIRPElement, aPlantStringBuffer, aLevel);
		}
		else if(aIRPElement instanceof IRPAttribute)
		{
			generate((IRPAttribute) aIRPElement, aPlantStringBuffer, aLevel);
		}
		else if(aIRPElement instanceof IRPClassifierRole)
		{
			generate((IRPClassifierRole) aIRPElement, aPlantStringBuffer, aLevel);
		}
		else if(aIRPElement instanceof IRPDependency)
		{
			generate((IRPDependency)aIRPElement, aPlantStringBuffer, aLevel);
		}
		else if(aIRPElement instanceof IRPGeneralization)
		{
			generate((IRPGeneralization) aIRPElement, aPlantStringBuffer, aLevel);
		}
		else if(aIRPElement instanceof IRPOperation)
		{
			generate((IRPOperation)aIRPElement, aPlantStringBuffer, aLevel);
		}
		else if(aIRPElement instanceof IRPRelation)
		{
			generate((IRPRelation)aIRPElement, aPlantStringBuffer, aLevel);
		}
		else
		{
			System.out.println(aIRPElement.getName());
		}
	}
	
	public String getPlantUml()
	{
		return myPlantUml;
	}
	
	
	private boolean generate(IRPPackage aPackage, StringBuffer aPlantStringBuffer, long aLevel)
	{
		if(aPackage==null)
		{
			return false;
		}
		
		aPlantStringBuffer.append(PlantUMLElements.myPackage);
		aPlantStringBuffer.append(aPackage.getName());
		aPlantStringBuffer.append(PlantUMLElements.myBraceOpen);
		if(aLevel>0)
		{
			
			//check for nested packages
			IRPPackage linkPackage = null;
			
			List<IRPPackage> packages = aPackage.getPackages().toList();
			for(IRPPackage p:packages)
			{
				generate(p,aPlantStringBuffer,aLevel-1);
				
				if((aLevel>-1))
				{
					//hidden link between all nested packages
					if(linkPackage!=null)
					{
						aPlantStringBuffer.append(linkPackage.getName());
						if(aLevel==myRootLevel)
						{
							aPlantStringBuffer.append(PlantUMLElements.myHiddenDownLink);
						}
						else
						{
							aPlantStringBuffer.append(PlantUMLElements.myHiddenLeftLink);
						}
						
						aPlantStringBuffer.append(p.getName());
						aPlantStringBuffer.append("\n");
					}
					linkPackage = p;
				}
			}

		}
		aPlantStringBuffer.append(PlantUMLElements.myBraceClose);
		aPlantStringBuffer.append("\n");
		
		
		
		if(aLevel>=myRootLevel)
		{
			List<IRPDependency> dependencies = aPackage.getDependencies().toList();
			for(IRPDependency d:dependencies)
			{
				generate(d, aPlantStringBuffer, 0);
			}
		}
			
		
		
		
		
		return true;
	}
	
	private boolean generate(IRPObjectModelDiagram aObjectModelDiagramm, StringBuffer aPlantStringBuffer, long aLevel)
	{
		if(aObjectModelDiagramm==null)
		{
			return false;
		}
		myDiagram = aObjectModelDiagramm;
		
		/*
		
		List<IRPModelElement> nes = aObjectModelDiagramm.getNestedElements().toList();
		System.out.println(".............");
		for(IRPModelElement ne:nes)
		{
			System.out.print(ne.getClass().getName());
			System.out.print(" ");
			System.out.println(ne.getName());
			
		}
		System.out.println(".............");
		
		List<IRPGraphElement> ges= aObjectModelDiagramm.getGraphicalElements().toList();
		for(IRPGraphElement ge:ges)
		{
			System.out.print(ge.getClass().getName());
			System.out.print(" ");
			System.out.println(ge.getInterfaceName());
			
			List<IRPGraphicalProperty> gps = ge.getAllGraphicalProperties().toList();
			for(IRPGraphicalProperty gp:gps)
			{
				System.out.print(" "+gp.getInterfaceName());
				System.out.print(" "+gp.getKey());
				System.out.print(" ");
				System.out.println(gp.getValue());
			}
			
			
		}
		
		*/
		
		List<IRPModelElement> elements = aObjectModelDiagramm.getElementsInDiagram().toList();
		for(IRPModelElement element:elements)
		{
			System.out.print(element.getClass().getName());
			System.out.print(" ");
			System.out.println(element.getName());
			
			if(element instanceof IRPClass)
			{
				generate((IRPClassifier)element, aPlantStringBuffer, 0);
			}
			if(element instanceof IRPGeneralization)
			{
				generate((IRPGeneralization)element,aPlantStringBuffer,0);
			}
			if(element instanceof IRPRelation)
			{
				generate((IRPRelation)element,aPlantStringBuffer,0);
			}
			if(element instanceof IRPDependency)
			{
				generate((IRPDependency)element,aPlantStringBuffer,0);
			}
			
		}
		
		
		return true;
	}
	
	private boolean generate(IRPDiagram aDiagramm, StringBuffer aPlantStringBuffer, long aLevel)
	{
		if(aDiagramm==null)
		{
			return false;
		}
		
		myDiagram = aDiagramm;

		
		Map<Integer,String> messages = new HashMap<Integer, String>();
		
		List<IRPModelElement> elements = aDiagramm.getElementsInDiagram().toList();
		
		
		for(IRPModelElement element:elements)
		{
			System.out.print(element.getClass().getName());
			System.out.print(" ");
			System.out.println(element.getName());

			if(element instanceof IRPClass)
			{
				generate((IRPClassifier)element, aPlantStringBuffer, 0);
			}
			if(element instanceof IRPGeneralization)
			{
				generate((IRPGeneralization)element,aPlantStringBuffer,0);
			}
			if(element instanceof IRPRelation)
			{
				generate((IRPRelation)element,aPlantStringBuffer, 0);
			}
			if(element instanceof IRPDependency)
			{
				generate((IRPDependency)element,aPlantStringBuffer,0);
			}
			if(element instanceof IRPClassifierRole)
			{
				generate((IRPClassifierRole)element,aPlantStringBuffer,0);
			}
			if(element instanceof IRPMessage)
			{
				generate((IRPMessage) element, messages, 0);
			}
			
		}
		
		for(int i=1; i<=messages.size();++i)
		{
			String message = messages.get(i);
			
			if(message!=null)
			{
				aPlantStringBuffer.append(message);
			}
			
		}
		
		
		return true;
	}
	
	private boolean generate(IRPClassifierRole aClassifierRole, StringBuffer aPlantStringBuffer, long aLevel)
	{
		if(aClassifierRole==null)
		{
			return false;
		}
		
		String roleType = aClassifierRole.getRoleType();
		
		if(roleType.equals("ACTOR"))
		{
			aPlantStringBuffer.append(PlantUMLElements.myActor);
		}
		else
		{
			aPlantStringBuffer.append(PlantUMLElements.myParticipant);
		}
		
		aPlantStringBuffer.append("\"");
		aPlantStringBuffer.append(aClassifierRole.getName());
		aPlantStringBuffer.append("\"\n");
		
		
		return true;
	}
	
	private boolean generate(IRPMessage aMessage,Map<Integer,String> aMessages, long aLevel)
	{
		if(aMessage == null)
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
		if(aMessage.getMessageType().equals("CREATE"))
		{
			messageStringBuffer.append(PlantUMLElements.myCreateMessage);
		}
		else
		{
			messageStringBuffer.append(PlantUMLElements.myMessage);
		}
		
		messageStringBuffer.append("\"");
		messageStringBuffer.append(aMessage.getTarget().getName());
		messageStringBuffer.append("\"");
		messageStringBuffer.append(" : ");
		messageStringBuffer.append(aMessage.getName());
		messageStringBuffer.append(PlantUMLElements.myBracketOpen);
		
		
		Iterator<String> i = aMessage.getActualParameterList().toList().iterator();
		  
		while(i.hasNext())
		{
			messageStringBuffer.append(i.next());
			if(i.hasNext())
			{
				messageStringBuffer.append(", ");
			}
		}
		
		messageStringBuffer.append(PlantUMLElements.myBracketClose);
		messageStringBuffer.append("\n");
		

		aMessages.put(sequenceNr, messageStringBuffer.toString());
		
		return true;
	}
	
	
	
	
	private boolean generate(IRPClassifier aClassifier, StringBuffer aPlantStringBuffer, long aLevel)
	{
		
		
		viewElement viewAttribute = viewElement.viewPublic;
		viewElement viewOperation = viewElement.viewPublic;
		
		if(aLevel<=0)
		{
			viewAttribute = viewElement.viewNone;
			viewOperation = viewElement.viewVirtual;
		}
		
		
		if(aClassifier==null)
		{
			return false;
		}
		String classifierName = aClassifier.getName();

		if(addClass(classifierName)==false)
		{
			return false;
		}
		
		if(myDiagram!=null)
		{
			List<IRPGraphElement> graphElements = myDiagram.getCorrespondingGraphicElements(aClassifier).toList();
			if(graphElements.size()>0)
			{
				IRPGraphElement graphElement = graphElements.get(0);
				
				
				IRPGraphicalProperty p = graphElement.getGraphicalProperty("OperationsDisplay");
				viewOperation = getViewElement(p);
				p=graphElement.getGraphicalProperty("AttributeDisplay");
				viewAttribute = getViewElement(p);

			}
			else
			{
				//diagram does not show class...
				return false;
			}
		}
		

		String nameSpace = getNameSpace(aClassifier);
		
		if(nameSpace.isEmpty()==false)
		{
		
			aPlantStringBuffer.append(PlantUMLElements.myPackage);
			aPlantStringBuffer.append(nameSpace);
			aPlantStringBuffer.append(PlantUMLElements.myBraceOpen);
		}
		if(classifierName.startsWith("I"))
		{
			aPlantStringBuffer.append(PlantUMLElements.myInterface);
		}
		else
		{
			aPlantStringBuffer.append(PlantUMLElements.myClass);
		}
		
		aPlantStringBuffer.append(classifierName);

		//Template...
		List<IRPTemplateParameter> templateParameters = aClassifier.getTemplateParameters().toList();
		
		if(templateParameters.size()>0)
		{
			aPlantStringBuffer.append("<");
			
			Iterator<IRPTemplateParameter> i = templateParameters.iterator();
			while(i.hasNext())
			{
				IRPTemplateParameter templateParameter = i.next();
				aPlantStringBuffer.append(templateParameter.getName());
				if(i.hasNext())
				{
					aPlantStringBuffer.append(", ");
				}
			}
			aPlantStringBuffer.append(">");
		}
		
		aPlantStringBuffer.append(PlantUMLElements.myBraceOpen);
		
		//attributes
		
		List<IRPAttribute> attributes = aClassifier.getAttributes().toList();
		if(viewAttribute!=viewElement.viewNone)
		{
			for(IRPAttribute attribute:attributes)
			{
				if(viewAttribute==viewElement.viewPublic)
				{
					if(attribute.getVisibility().equals("public")==false)
					{
						continue;
					}
				}
				generate(attribute, aPlantStringBuffer, aLevel);
			}
		}
		//operations
		List<IRPOperation> operations = aClassifier.getOperations().toList();
		if(viewOperation!=viewElement.viewNone)
		{
			for(IRPOperation operation:operations)
			{
				if(viewOperation==viewElement.viewPublic)
				{
					if(operation.getVisibility().equals("public")==false)
					{
						continue;
					}
				}
				else if(viewOperation == viewElement.viewVirtual)
				{
					if(operation.getIsVirtual()!=1)
					{
						continue;
					}
					if(operation.getVisibility().equals("public")==false)
					{
						continue;
					}
				}
				generate(operation, aPlantStringBuffer, aLevel);
			}
		}
		
		
		aPlantStringBuffer.append(PlantUMLElements.myBraceClose);
		if(nameSpace.isEmpty()==false)
		{
			aPlantStringBuffer.append(PlantUMLElements.myBraceClose);	
		}
		aPlantStringBuffer.toString();
		
		if(aLevel>=1)
		{
			//generalizations
			List<IRPGeneralization> generalizations = aClassifier.getGeneralizations().toList();
			for(IRPGeneralization generlization:generalizations)
			{
				generate(generlization, aPlantStringBuffer, 0);
			}
			// relations
			List<IRPRelation> relations = aClassifier.getRelations().toList();
			for(IRPRelation relation:relations)
			{
				generate(relation, aPlantStringBuffer, 0);
			}
			// dependencies
			List<IRPDependency> dependencies = aClassifier.getDependencies().toList();
			for(IRPDependency dependency:dependencies)
			{
				generate(dependency,aPlantStringBuffer,0);
			}
			
			for(IRPAttribute attribute:attributes)
			{
				IRPClassifier classifier = attribute.getType();
				generateDependentClassifier(aClassifier, classifier, aPlantStringBuffer);
			}
			for(IRPOperation operation:operations)
			{
				//return
				IRPClassifier retClassifier = operation.getReturns();
				generateDependentClassifier(aClassifier, retClassifier, aPlantStringBuffer);
				List<IRPArgument> arguments = operation.getArguments().toList();
				for(IRPArgument argument:arguments)
				{
					IRPClassifier argClassifier = argument.getType();
					generateDependentClassifier(aClassifier, argClassifier, aPlantStringBuffer);
				}
				
			}
			
			
			//template Instantiation
			IRPTemplateInstantiation instantiation = aClassifier.getTi();
			if(instantiation!=null)
			{
				List<IRPTemplateInstantiationParameter> tips = instantiation.getTemplateInstantiationParameters().toList();
				
				if(tips.isEmpty()==false)
				{
					IRPModelElement template = aClassifier.getOfTemplate();
					List<IRPTemplateParameter> tps = template.getTemplateParameters().toList();
					
					generateElement(template, aPlantStringBuffer, 0);
					
					aPlantStringBuffer.append("\n");
					aPlantStringBuffer.append(aClassifier.getName());
					aPlantStringBuffer.append(" \" << Instantiation: ");
					
					for(IRPTemplateInstantiationParameter tip:tips)
					{
						aPlantStringBuffer.append(tip.getArgValue());
						aPlantStringBuffer.append(" ");
					}
					aPlantStringBuffer.append(">> \" ");
					aPlantStringBuffer.append(PlantUMLElements.myDependency);
					aPlantStringBuffer.append(template.getName());
					aPlantStringBuffer.append("\n");
					
					
				}
			}
			
			/*
			List<IRPModelElement> elements = aClassifier.getReferences().toList();
			for(IRPModelElement element:elements)
			{
				generateElement(element, aPlantStringBuffer, 0);
			}
			*/
			
			
				
			
		}
		
		
		return true;
	}

	private viewElement getViewElement(IRPGraphicalProperty p) {
		viewElement ret;
		String value = p.getValue();
		
		if(value.equals("None"))
		{
			ret = viewElement.viewNone;
		}
		else if(value.equals("Explicit"))
		{
			ret = viewElement.viewNone;
		}
		else if(value.equals("All"))
		{
			ret = viewElement.viewAll;
		}
		else if(value.equals("Public"))
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
			StringBuffer aPlantStringBuffer) {
		if((aClassifierDependent instanceof IRPClass)==false)
		{
			return;
		}
		if(generate(aClassifierDependent, aPlantStringBuffer, 0)==true)
		{
			aPlantStringBuffer.append(aClassifierSource.getName());
			aPlantStringBuffer.append(PlantUMLElements.myDependency);
			aPlantStringBuffer.append(aClassifierDependent.getName());
			aPlantStringBuffer.append("\n");
		}
	}

	private void generate(IRPRelation aRelation, StringBuffer aPlantStringBuffer, int aLevel) {
		IRPClassifier otherClass = aRelation.getOtherClass();		
		generate(otherClass, aPlantStringBuffer, 0);
		IRPClassifier classifier = aRelation.getOfClass();
		generate(classifier, aPlantStringBuffer, 0);
		aPlantStringBuffer.append(classifier.getName());
		
		String qualifier = aRelation.getQualifier();
		if(qualifier.equals("")==false)
		{
			aPlantStringBuffer.append(" \"");
			aPlantStringBuffer.append(qualifier);
			aPlantStringBuffer.append("\" ");
		}
		
		aPlantStringBuffer.append(getRelationType(aRelation));
		aPlantStringBuffer.append(" \"");
		aPlantStringBuffer.append(aRelation.getMultiplicity());
		aPlantStringBuffer.append("\" ");
		aPlantStringBuffer.append(otherClass.getName());
		aPlantStringBuffer.append(" : ");
		aPlantStringBuffer.append(aRelation.getName());
		aPlantStringBuffer.append("\n");
	}

	private void generate(IRPGeneralization aGeneralization, StringBuffer aPlantStringBuffer, long aLevel) 
	{
		IRPClassifier baseClass = aGeneralization.getBaseClass();
		generate(baseClass, aPlantStringBuffer, 0);
		aPlantStringBuffer.append(baseClass.getName());
		aPlantStringBuffer.append(PlantUMLElements.myGeneralization);
		aPlantStringBuffer.append(aGeneralization.getDerivedClass().getName());
		aPlantStringBuffer.append("\n");
	}
	
	private void generate(IRPDependency aDependency, StringBuffer aPlantStringBuffer, long aLevel)
	{
		IRPModelElement dependent = aDependency.getDependent();
		IRPModelElement dependsOn = aDependency.getDependsOn();
		
		//at the moment only classes..
		if((dependent instanceof IRPClass)&&(dependsOn instanceof IRPClass))
		{
			generate((IRPClassifier)dependent,aPlantStringBuffer,0);
			generate((IRPClassifier)dependsOn,aPlantStringBuffer,0);
		}
		else if((dependent instanceof IRPPackage)&&(dependsOn instanceof IRPPackage))
		{
			generate((IRPPackage)dependent,aPlantStringBuffer,0);
			generate((IRPPackage)dependsOn,aPlantStringBuffer,0);
		}
		else
		{
			return;
		}
				
		aPlantStringBuffer.append(dependent.getName());
		
		String stereoTypes = getStereoTypes(aDependency);
		
		if(stereoTypes.equals("")==false)
		{
			aPlantStringBuffer.append(" \"");
			aPlantStringBuffer.append(stereoTypes);
			aPlantStringBuffer.append("\" ");
		}
		aPlantStringBuffer.append(PlantUMLElements.myDependency);
		aPlantStringBuffer.append(dependsOn.getName());
		aPlantStringBuffer.append("\n");
		
		
		
	}
	
	private String getStereoTypes(IRPModelElement aElement)
	{
		StringBuffer ret = new StringBuffer();
		ret.append("");
		List<IRPStereotype> stereoTypes = aElement.getStereotypes().toList();
		for(IRPStereotype stereotype:stereoTypes)
		{
			ret.append("<<");
			ret.append(stereotype.getName());
			ret.append(">> ");
		}
		
		return ret.toString();
	}
	
	
	private String getRelationType(IRPRelation aRelation)
	{
		String relationType = aRelation.getRelationType();
		if(relationType.equals("Association"))
		{
			return PlantUMLElements.myAssociation;
		}
		if(relationType.equals("Aggregation"))
		{
			return PlantUMLElements.myAggregation;
		}
		if(relationType.equals("Composition"))
		{
			return PlantUMLElements.myComposition;
		}
		return "";
	}
	
	
	private String getVisibility(String aVisbility)
	{
		if(aVisbility.equals("private"))
		{
			return PlantUMLElements.myPrivate;
		}
		else if(aVisbility.equals("public"))
		{
			return PlantUMLElements.myPublic;
		}
		else if(aVisbility.equals("protected"))
		{
			return PlantUMLElements.myProtected;
		}
		return "";
			
				
	}
	
	
	private boolean generate(IRPOperation aOperation, StringBuffer aPlantStringBuffer, long aLevel)
	{
		if(aOperation==null)
		{
			return false;
		}
		
		
		
		String operationName = aOperation.getName();
		IRPClassifier ret = aOperation.getReturns();
		String operationReturnValue = "";
		if(ret!=null)
		{
			operationReturnValue = aOperation.getReturns().getName();
		}
		else
		{
			operationReturnValue = aOperation.getReturnTypeDeclaration();
		}
				
		aPlantStringBuffer.append(getVisibility(aOperation.getVisibility()));
		
		if(aOperation.getIsAbstract()==1)
		{
			aPlantStringBuffer.append(PlantUMLElements.myAbstractOperation);
		}
		
		if(aOperation.getIsStatic()==1)
		{
			aPlantStringBuffer.append(PlantUMLElements.myStatic);
		}
		
		aPlantStringBuffer.append(operationReturnValue);
		aPlantStringBuffer.append(" ");
		aPlantStringBuffer.append(operationName);
		aPlantStringBuffer.append(PlantUMLElements.myBracketOpen);
		aPlantStringBuffer.append(PlantUMLElements.myBracketClose);
		
		
		
		
		return true;
		
	}
	
	private boolean generate(IRPAttribute aAttribute, StringBuffer aPlantStringBuffer, long aLevel)
	{
		if(aAttribute==null)
		{
			return false;
		}
		
		aPlantStringBuffer.append(getVisibility(aAttribute.getVisibility()));
		
		if(aAttribute.getIsStatic()==1)
		{
			aPlantStringBuffer.append(PlantUMLElements.myStatic);
		}
		
		aPlantStringBuffer.append(aAttribute.getType().getName());
		aPlantStringBuffer.append(" ");
		aPlantStringBuffer.append(aAttribute.getName());
		aPlantStringBuffer.append("\n");
		
		return true;
		
	}
	
	private IRPPackage getPackage(IRPModelElement aElement)
	{
		IRPPackage ret = null;
		if((aElement instanceof IRPPackage)==false)
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
		if(ret==null)
		{
			return "";
		}
		return ret.getNamespace();
	}
	
	private boolean generate(IRPStatechart aStatechart, StringBuffer aPlantStringBuffer, int aLevel)
	{
		if(aStatechart==null)
		{
			return false;
		}
		
		List<IRPModelElement> elements = aStatechart.getElementsInDiagram().toList();
		
		
		for(IRPModelElement element:elements)
		{
			System.out.println("Element Type: " + element.getClass().getName() + " Name: " + element.getName());

			if(element instanceof IRPStateVertex)
			{
				generate((IRPStateVertex)element, aPlantStringBuffer, aLevel);
			}
			
			
			
			if(element instanceof IRPTransition)
			{
				IRPTransition transition = (IRPTransition)element;
				if(transition.getItsStatechart().equals(aStatechart))
				{
					generate((IRPTransition)element,aPlantStringBuffer, aLevel);
				}
			}
			
			
		}
		
		return true;
	}
	
	private boolean generate(IRPTransition aTransition, StringBuffer aPlantStringBuffer, int aLevel)
	{
		if(aTransition==null)
		{
			return false;
		}
		
		if(aTransition.isDefaultTransition()==1)
		{
			IRPState defaultState = aTransition.getOfState();
			if(defaultState.isRoot()==1)
			{
				aPlantStringBuffer.append(PlantUMLElements.myRootState);
				aPlantStringBuffer.append(PlantUMLElements.myTransition);
				aPlantStringBuffer.append(aTransition.getItsTarget().getName());
				aPlantStringBuffer.append("\n");
			}
			else
			{
				aPlantStringBuffer.append(PlantUMLElements.myState);
				aPlantStringBuffer.append(defaultState.getName());
				aPlantStringBuffer.append(PlantUMLElements.myBraceOpen);
				aPlantStringBuffer.append(PlantUMLElements.myRootState);
				aPlantStringBuffer.append(PlantUMLElements.myTransition);
				aPlantStringBuffer.append(aTransition.getItsTarget().getName());
				aPlantStringBuffer.append(PlantUMLElements.myBraceClose);
				aPlantStringBuffer.append("\n");
				
			}
		}
		else
		{
			aPlantStringBuffer.append(aTransition.getItsSource().getName());
			aPlantStringBuffer.append(PlantUMLElements.myTransition);
			aPlantStringBuffer.append(aTransition.getItsTarget().getName());
			IRPTrigger trigger = aTransition.getItsTrigger();
			if(trigger!=null)
			{
				aPlantStringBuffer.append(" : ");
				aPlantStringBuffer.append(aTransition.getItsTrigger().getBody());
			}
			IRPGuard guard = aTransition.getItsGuard();
			if(guard!=null)
			{
				if(trigger==null)
				{
					aPlantStringBuffer.append(" : ");
				}
				aPlantStringBuffer.append(" [");
				aPlantStringBuffer.append(guard.getBody());
				aPlantStringBuffer.append("] ");
	
			}
			aPlantStringBuffer.append("\n");
		}

		return true;
	}
	
	private boolean generate(IRPStateVertex aState, StringBuffer aPlantStringBuffer, int aLevel)
	{
		if(aState==null)
		{
			return false;
		}
		
		if(aState instanceof IRPState)
		{
			IRPState state = (IRPState)aState;
			if(state.isRoot()==1)
			{
				return true;
			}
		}
				
		
		
		drawStateOpen(aState.getParent(), aPlantStringBuffer);
		
		aPlantStringBuffer.append(PlantUMLElements.myState);
		aPlantStringBuffer.append(aState.getName());
		if(aState instanceof IRPConnector)
		{
			IRPConnector connector = (IRPConnector)aState;
			if(connector.getConnectorType().equals("Condition"))
			{
				aPlantStringBuffer.append(PlantUMLElements.myConditional);
			}
			else if(connector.getConnectorType().equals("History"))
			{
				aPlantStringBuffer.append(PlantUMLElements.myHistory);
			}
		}
		aPlantStringBuffer.append("\n");
		
		drawStateClose(aState.getParent(), aPlantStringBuffer);
		

		return true;
	}
	
	private void drawStateOpen(IRPState aState, StringBuffer aPlantStringBuffer)
	{
		if(aState.isRoot()==1)
		{
			return;
		}
		IRPState parentState = aState.getParent();
		drawStateOpen(parentState, aPlantStringBuffer);
		
		aPlantStringBuffer.append(PlantUMLElements.myState);
		aPlantStringBuffer.append(aState.getName());
		aPlantStringBuffer.append(PlantUMLElements.myBraceOpen);
		
	}
	
	private void drawStateClose(IRPState aState, StringBuffer aPlantStringBuffer)
	{
		if(aState.isRoot()==1)
		{
			return;
		}
		IRPState parentState = aState.getParent();
		drawStateClose(parentState, aPlantStringBuffer);
		
		
		aPlantStringBuffer.append(PlantUMLElements.myBraceClose);
		
	}
	
	

}

class PlantUMLElements {
	
	//public static String myStartUML = "\n@startuml\nskinparam handwritten true\n";
	public static String myStartUML = "\n@startuml\n";
	public static String myEndUML = "\n\n@enduml\n";
	public static String myClass = "class ";
	public static String myInterface = "interface ";
	public static String myAssociation = " --> ";
	public static String myGeneralization = " <|-- ";
	public static String myComposition = " *--> ";
	public static String myAggregation = " o--> ";
	public static String myPackage = "Package ";
	public static String myNamespace = "namespace ";
	public static String myBraceOpen = " { \n";
	public static String myBraceClose = "\n } \n";
	public static String myContent = " : ";
	public static String myPublic = " + ";
	public static String myPrivate = " - ";
	public static String myProtected = " # ";
	public static String myBracketOpen = "(";
	public static String myBracketClose = ")\n ";
	public static String myRelation = " --> ";
	public static String myAbstractOperation = " {abstract} ";
	public static String myStatic = " {static} ";
	public static String myDependency = " ..> ";
	public static String myMessage = " -> ";
	public static String myCreateMessage = " --> ";
	public static String myRMessage = " <- ";
	public static String myRCreateMessage = " <-- ";
	public static String myParticipant = " participant ";
	public static String myActor = " actor ";
	public static String myState = "state ";
	public static String myRootState = "[*] ";
	public static String myTerminationState = "[*] ";
	public static String myTransition = " --> ";
	//public static String myHiddenLink = "  -[hidden]--> ";
	public static String myHiddenDownLink = " -[hidden]d--> ";
	public static String myHiddenLeftLink = " -[hidden]l--> ";
	public static String myConditional = " <<choice>> ";
	public static String myHistory = " <<choice>> ";
	
	
	
	
	
	
}
