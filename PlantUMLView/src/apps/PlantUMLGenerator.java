package apps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.management.InstanceAlreadyExistsException;

import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPObjectModelDiagram;
import com.telelogic.rhapsody.core.IRPOperation;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPRelation;
import com.telelogic.rhapsody.core.IRPStereotype;
import com.telelogic.rhapsody.core.IRPTemplateInstantiation;
import com.telelogic.rhapsody.core.IRPTemplateInstantiationParameter;
import com.telelogic.rhapsody.core.IRPTemplateParameter;

import net.sourceforge.plantuml.activitydiagram3.InstructionPartition;

import com.telelogic.rhapsody.core.IRPArgument;
import com.telelogic.rhapsody.core.IRPAssociationClass;
import com.telelogic.rhapsody.core.IRPAttribute;
import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPClassifierRole;
import com.telelogic.rhapsody.core.IRPDependency;
import com.telelogic.rhapsody.core.IRPDiagram;
import com.telelogic.rhapsody.core.IRPFile;
import com.telelogic.rhapsody.core.IRPGeneralization;
import com.telelogic.rhapsody.core.IRPMessage;

public class PlantUMLGenerator {
	
	
	
	private String myPlantUml;
	private int myRootLevel;
	private boolean myFromDiagram;
	
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
	
	public PlantUMLGenerator(IRPModelElement aIRPElement) 
	{
		myRootLevel = 2;
		myFromDiagram = false;
		
		
		StringBuffer plantStringBuffer = new StringBuffer();
		
		
		plantStringBuffer.append(PlantUMLElements.myStartUML);
		if(aIRPElement instanceof IRPObjectModelDiagram)
		{
			generate((IRPObjectModelDiagram) aIRPElement, plantStringBuffer, myRootLevel);
		}
		else if(aIRPElement instanceof IRPDiagram)
		{
			myFromDiagram = true;
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
	
	public String getPlanUml()
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
				
				if((aLevel>-1)&&(aLevel!=myRootLevel))
				{
				//hidden link between all nested packages
				if(linkPackage!=null)
				{
					aPlantStringBuffer.append(linkPackage.getName());
					aPlantStringBuffer.append(PlantUMLElements.myHiddenLink);
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
		if(aClassifier==null)
		{
			return false;
		}
		String classifierName = aClassifier.getName();

		if(addClass(classifierName)==false)
		{
			return false;
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
		for(IRPAttribute attribute:attributes)
		{
			generate(attribute, aPlantStringBuffer, aLevel);
		}
		//operations
		List<IRPOperation> operations = aClassifier.getOperations().toList();
		for(IRPOperation operation:operations)
		{
			generate(operation, aPlantStringBuffer, aLevel);
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
			generate((IRPClassifier)dependent,aPlantStringBuffer,aLevel+1);
			generate((IRPClassifier)dependsOn,aPlantStringBuffer,aLevel+1);
		}
		else if((dependent instanceof IRPPackage)&&(dependsOn instanceof IRPPackage))
		{
			generate((IRPPackage)dependent,aPlantStringBuffer,aLevel+1);
			generate((IRPPackage)dependsOn,aPlantStringBuffer,aLevel+1);
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
		
		
		/*
		if(aOperation.getIsVirtual()==0)
		{
			return false;
		}
		*/
		if(aOperation.getVisibility().equals("public")==false)
		{
			if(aOperation.getIsVirtual()==0)
			{
				return false;
			}	
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
		if(aAttribute.getVisibility().equals("public")==false)
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
	
	

}

class PlantUMLElements {
	
	public static String myStartUML = "\n@startuml\n\n";
	public static String myEndUML = "\n\n@enduml\n";
	public static String myClass = "class ";
	public static String myInterface = "interface ";
	public static String myAssociation = " --> ";
	public static String myGeneralization = " <|-- ";
	public static String myComposition = " *--> ";
	public static String myAggregation = " o--> ";
	public static String myPackage = "Package ";
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
	public static String myHiddenLink = "  -[hidden]--> ";
	
	
	
	
	
	
}
