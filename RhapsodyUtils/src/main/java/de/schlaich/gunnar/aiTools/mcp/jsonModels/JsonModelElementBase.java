package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPCollection;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPState;
import com.telelogic.rhapsody.core.IRPType;

import de.schlaich.gunnar.aiTools.mcp.jsonModels.JsonModelElementBase.MetaClass;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.ObjectMapper; 
import com.fasterxml.jackson.databind.SerializationFeature; 
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.validation.constraints.NotNull;



// AcceptEventAction,AcceptTimeEvent,ActionBlock,ActivityDiagram,Actor,Argument,Association,AssociationEnd,Attribute,CallOperation,Class,ClassifierRole,Cleanup,CombinedFragment,Comment,CommunicationDiagram,Component,ComponentDiagram,ComponentInstance,Condition,ConditionMark,Configuration,Connector,Constraint,Constructor,ControlledFile,DefaultTransition,Dependency,DeploymentDiagram,Destructor,EnumerationLiteral,Event,ExecutionOccurrence,File,Flow,Folder,Generalization,HyperLink,Initializer,InstanceSlot,InstanceSpecification,InteractionOccurrence,InteractionOperand,ItemFlow,Link,MatrixLayout,MatrixView,Message,Module,Node,Object,ObjectModelDiagram,ObjectNode,Operation,Package,PanelDiagram,Pin,Port,Profile,Project,Reception,ReferenceActivity,Requirement,SequenceDiagram,State,Statechart,Stereotype,StructureDiagram,Swimlane,SysMLPort,TableLayout,TableView,Tag,Transition,TriggeredOperation,Type,UseCase,UseCaseDiagram


@JsonTypeInfo( use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "Type") 
@JsonSubTypes({
	@JsonSubTypes.Type(value = JsonModelElement.class, name = "ModelElement"),
	@JsonSubTypes.Type(value = JsonClass.class, name = "Class"), 
	@JsonSubTypes.Type(value = JsonPackage.class, name = "Package"), 
	@JsonSubTypes.Type(value = JsonLiteralSpecification.class, name = "LiteralSpecification"),  
	@JsonSubTypes.Type(value = JsonModelElementBase.class, name = "ModelElementStub"),
	@JsonSubTypes.Type(value = JsonOperation.class, name = "Operation"),
	@JsonSubTypes.Type(value = JsonVariable.class, name = "Variable"),
	@JsonSubTypes.Type(value = JsonAttribute.class, name = "Attribute"),
	@JsonSubTypes.Type(value = JsonDependency.class, name = "Dependency"),
	@JsonSubTypes.Type(value = JsonGeneralization.class, name = "Generalization"),
	@JsonSubTypes.Type(value = JsonStereotype.class, name = "Stereotype"),
	@JsonSubTypes.Type(value = JsonType.class, name = "type"),
	@JsonSubTypes.Type(value = JsonAssociationClass.class, name = "AssociationClass"),
	@JsonSubTypes.Type(value = JsonDiagram.class, name = "Diagram"),
	@JsonSubTypes.Type(value = JsonObjectModelDiagram.class, name = "ObjectModelDiagram"),
	@JsonSubTypes.Type(value = JsonArgument.class, name = "Argument"),
	@JsonSubTypes.Type(value = JsonRelation.class, name = "AssociationEnd"),
	@JsonSubTypes.Type(value = JsonInstance.class, name = "Object"),
	@JsonSubTypes.Type(value = JsonStatechart.class, name = "Statechart"),
	@JsonSubTypes.Type(value = JsonState.class, name = "State"),
	@JsonSubTypes.Type(value = JsonTransition.class, name = "Transition"),
	@JsonSubTypes.Type(value = JsonGuard.class, name = "Guard"),
	@JsonSubTypes.Type(value = JsonDefaultTransition.class, name = "DefaultTransition"),
	@JsonSubTypes.Type(value = JsonAction.class, name = "Action"),
	@JsonSubTypes.Type(value = JsonEventReception.class, name = "EventReception"),
	@JsonSubTypes.Type(value = JsonEvent.class, name = "Event"),
	@JsonSubTypes.Type(value = JsonInterfaceItem.class, name = "InterfaceItem"),
	@JsonSubTypes.Type(value = JsonUnit.class, name = "Unit"),
	@JsonSubTypes.Type(value = JsonComponent.class, name = "Component"),
	@JsonSubTypes.Type(value = JsonStateVertex.class, name = "StateVertex"),
	@JsonSubTypes.Type(value = JsonHyperLink.class, name = "HyperLink"),
	@JsonSubTypes.Type(value = JsonModule.class, name = "Module"),
	@JsonSubTypes.Type(value = JsonFile.class, name = "File"),
	@JsonSubTypes.Type(value = JsonFileFragment.class, name = "FileFragment"),
	@JsonSubTypes.Type(value = JsonComment.class, name = "Comment"),
	@JsonSubTypes.Type(value = JsonConstraint.class, name = "Constraint"),
	@JsonSubTypes.Type(value = JsonRequirement.class, name = "Requirement"),
	@JsonSubTypes.Type(value = JsonTag.class, name = "Tag"),
	@JsonSubTypes.Type(value = JsonStatechartDiagram.class, name = "StatechartDiagram"),
	@JsonSubTypes.Type(value = JsonProject.class, name = "Project"),
	@JsonSubTypes.Type(value = JsonProfile.class, name = "Profile"),
	@JsonSubTypes.Type(value = JsonEnumerationLiteral.class, name = "EnumerationLiteral")
		
	}) 


public class JsonModelElementBase
{
		
	private static Consumer<String> myTraceAction = null;
	
	
	public enum MetaClass {
		ModelElement,
		AcceptEventAction, 
		AcceptTimeEvent,
		Action,
		ActionBlock, 
		ActivityDiagram, 
		Actor, 
		Argument, 
		Association, 
		AssociationEnd,
		Attribute, 
		CallOperation, 
		Class, 
		ClassifierRole, 
		Cleanup, 
		CombinedFragment, 
		Comment, 
		CommunicationDiagram,
		Component, 
		ComponentDiagram, 
		ComponentInstance, 
		Condition, 
		ConditionMark, 
		Configuration, 
		Connector, 
		Constraint,
		Constructor, 
		ControlledFile, 
		DefaultTransition, 
		Dependency, 
		DeploymentDiagram, 
		Destructor, 
		EnumerationLiteral,
		Event, 
		EventReception,
		ExecutionOccurrence, 
		File, 
		FileElement,
		Flow, 
		Folder, 
		Generalization,
		Guard,
		HyperLink, 
		InterfaceItemTrigger,
		Initializer, 
		InstanceSlot,
		InstanceSpecification, 
		InteractionOccurrence, 
		InteractionOperand, 
		ItemFlow, 
		Link, 
		LiteralSpecification,
		MatrixLayout, 
		MatrixView,
		Message, 
		Module, 
		Node, 
		Object, 
		ObjectModelDiagram, 
		ObjectNode, 
		Operation, 
		Package, 
		PanelDiagram, 
		Pin, 
		Port,
		Profile, 
		Project, 
		Reception, 
		ReferenceActivity, 
		Requirement, 
		SequenceDiagram, 
		State, 
		Statechart,
		StatechartDiagram,
		Stereotype, 
		StructureDiagram, 
		Swimlane, 
		SysMLPort, 
		TableLayout, 
		TableView, 
		Tag,
		TemplateInstantiation,
		Transition,
		TriggeredOperation, 
		Type, 
		UseCase, 
		UseCaseDiagram, 
		Variable
	}
	
	public enum ImportMode {
		create, update, reference, remove
	}

	@JsonProperty(value = "Type", required = true)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected String type = null;
	
	@NotNull
	@JsonProperty(value = "metaclass", required = true)
	protected MetaClass metaClass = null;
	
	@JsonProperty("guid")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected String guid = null;
	
	
	@NotNull
	@JsonProperty(value = "name", required = true)
	protected String name = null;
	
	@JsonProperty(value = "fullName", required = true)
	protected String fullName = null;
	
	
	private int myLevel = 0;
	
	private static final String CREATION_PREFIX = "__create_";
	protected String creationName = null;
	
	

	public JsonModelElementBase(IRPModelElement aIRPModelElement)
	{
		this(aIRPModelElement, 0);
	}
	
	
	public JsonModelElementBase(IRPModelElement aIRPModelElement, int aLevel)
	{
		
		myLevel = aLevel;
		
		String metaClassName = aIRPModelElement.getMetaClass();

		try
		{
			metaClass = MetaClass.valueOf(metaClassName);
		}
		catch(IllegalArgumentException e)
		{
			metaClass = MetaClass.ModelElement;
		}
		
		name = aIRPModelElement.getName();
		
		guid = aIRPModelElement.getGUID();
		
		fullName = aIRPModelElement.getFullPathName();	
	}
	
	protected void trace(String aMessage)
	{
		Trace(aMessage);
	}
	
	protected static void Trace(String aMessage)
	{
		if (myTraceAction == null)
		{
			// no traceaction set...
			return;
		}

		aMessage = "JsonModel: " + aMessage;

		myTraceAction.accept(aMessage);
	}
	
	public static void SetTraceAction(Consumer<String> aTraceAction)
	{
		myTraceAction = aTraceAction;
	}
	
	protected int getLevel()
	{
		return myLevel;
	}
	

	
	public JsonModelElementBase()
	{
		
		JsonModelFactory factory = JsonModelFactory.Instance();
		
		factory.addReferenceElement(this);
		
	}
	
	public MetaClass getMetaclass()
	{
		if (metaClass == null)
		{
			
		}
		
		
		return metaClass;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getGuid()
	{
		return guid;
	}
	
	public void setGuid(String aGuid)
	{
		guid = aGuid;
	}
	
	public String getFullName()
	{
		return fullName;
	}
	
	public void setFullName(String aFullName)
	{
		fullName = aFullName;
	}
	
	public String toJsonString() throws IOException
	{
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		String jsonString = objectMapper.writeValueAsString(this);
		return jsonString;
	}
	
	public IRPModelElement toModelElement(IRPProject aProject, ImportMode aImportMode)
	{
		return toModelElement((IRPModelElement) null, aProject, aImportMode);
	}
	
	
	public IRPModelElement toModelElement(JsonModelElementBase aRootElement, IRPProject aProject, ImportMode aImportMode)
	{
		
		IRPModelElement rootElement = null;
		
		if (aRootElement != null)
		{
			String guid = aRootElement.getGuid();
			rootElement = aProject.findElementByGUID(guid);
		}
		
		return toModelElement(rootElement, aProject, aImportMode);	
	
	}
	
	public IRPModelElement createModelElement(JsonModelElementBase rootElement, IRPProject aProject, ImportMode aImportMode)
	{
		IRPModelElement ret = toModelElement(rootElement, aProject, aImportMode);
		
		if(ret==null)
		{
			trace("createModelElement: Failed to create element " + this.getName() + " of type " + this.getMetaclass().name());
			return null;
		}
		
		if (ret.getName().equals(this.getName()) == false)
		{
			try
			{
				ret.setName(this.getName());
			}
			catch (Exception e)
			{
				trace("createModelElement: Exception setting name to " + this.getName() + ": " + e.getMessage());
			}
			
		}
		
		return ret;
	}
	
	
	public IRPModelElement toModelElement(IRPModelElement rootElement, IRPProject aProject, ImportMode aImportMode)
	{

		String metaClassName = this.getMetaclass().name();
		
		IRPModelElement returnElement = null;

		if(aImportMode== ImportMode.remove)
		{
			returnElement = aProject.findElementByGUID(this.getGuid());
			
			if (returnElement == null)
			{
				trace("toModelElement: Element with GUID " + this.getGuid() + " not found in remove mode.");
				return null;
			}
			
			returnElement.deleteFromProject();
			
			return null;
			
		}
		
		else if (aImportMode == ImportMode.create)
		{
			
			creationName =  this.getName();
			
			if(rootElement == null)
			{
				trace("toModelElement: Root element is null in create mode.");
				return null;
			}
			

			if (isSet(name) == false)
			{
				trace("toModelElement: Name not set for element with GUID: " + this.getGuid() + " in create mode.");
				return null;
			}
			
			
			if(rootElement.findNestedElement(this.getName(), metaClass.name())!=null)
			{
				trace("Element already exists under root element " + rootElement.getFullPathName() + " with name "
						+ this.getName() + " and metaclass " + metaClass.name() + " in create mode.");
				
				creationName = CREATION_PREFIX + this.getName();
		
			}
			
			try
			{
			
				if( rootElement instanceof IRPPackage)
                {
					IRPPackage rootPackage = (IRPPackage)rootElement;
					if (metaClass == MetaClass.Operation)
					{
						returnElement = rootPackage.addGlobalFunction(creationName);
					}
					else if (metaClass == MetaClass.Attribute)
					{
						returnElement = rootPackage.addGlobalVariable(creationName);
					}
                }
				
				if (rootElement instanceof IRPClassifier)
				{
					IRPClassifier rootClassifier = (IRPClassifier) rootElement;
					if (metaClass == MetaClass.Statechart)
					{
						returnElement = rootClassifier.addStatechart();
						creationName = returnElement.getName();
					}		
				}
				
				
				
				if(this.metaClass==MetaClass.EnumerationLiteral)
				{
					if(rootElement instanceof IRPType)
					{
						IRPType t = (IRPType) rootElement;
						
						t.setKind("Enumeration");
						returnElement = t.addEnumerationLiteral(this.name);
					}
				}
				
				
				
				if(returnElement == null)
				{
					returnElement = rootElement.addNewAggr(metaClassName,creationName);	
				}
				
			}
			catch (Exception e)
			{
				trace("Exception at call of addNewAttr creating new element of type " + metaClassName + ": " + e.getMessage());
				trace("Root element: " + rootElement.getFullPathName() + "type: " + rootElement.getMetaClass());

				if(this.metaClass == MetaClass.Attribute)
				{
					metaClassName = "Variable";
				}
				if (this.metaClass == MetaClass.Operation)
				{
					metaClassName = "Function";
				}
				
				try
                {
                    returnElement = rootElement.addNewAggr(metaClassName,this.getName());		
                }
                catch (Exception ex)
                {
                    trace("Exception creating new element of type " + metaClassName + ": " + ex.getMessage());
                    return null;
                }
	
				
			}
			
			if (returnElement == null)
			{
				trace("toModelElement: Failed to create new element of type " + metaClassName + " under root element "
						+ rootElement.getFullPathName());
				
				return null;
			}
			
			String oldGuid = guid;
			
			if(aProject.findElementByGUID(this.getGuid())==null)
			{
				//use old GUID when not already used (paste from other branch or update (delete and paste))
				returnElement.setGUID(this.getGuid());
			}
			else
			{
				this.guid = returnElement.getGUID();
			}
			this.fullName = returnElement.getFullPathName();
			
			JsonModelFactory factory = JsonModelFactory.Instance();
			
			factory.updateReferenceElement(oldGuid, returnElement);
			
		}

		
		else if (aImportMode == ImportMode.reference)
		{
			
			returnElement = aProject.findElementByGUID(this.getGuid());
			
			if (returnElement == null)
			{
				trace("toModelElement: Element with GUID: " + this.getGuid() + " not found in reference mode.");
				
				//try to find element via full name
				if (isSet(this.getFullName()) == false)
				{
					trace("toModelElement: FullName not set for element with GUID: " + this.getGuid()
							+ " in reference mode.");
					
					return null;
				}
				returnElement = aProject.findElementsByFullName(this.getFullName(), this.metaClass.name());
				
				if (returnElement == null)
				{
					trace("toModelElement: Element with FullName: " + this.getFullName()
							+ " not found in reference mode.");
					return null;
				}

			}
			
//			if (returnElement.getFullPathName().equals(this.getFullName()) == false)
//			{
//				trace("toModelElement: FullName mismatch for element with GUID " + this.getGuid()
//						+ " in reference mode.");
//				
//				return null;
//			}
			
			if (returnElement.getMetaClass().equals(metaClassName) == false)
			{
				trace("toModelElement: Metaclass mismatch for element with GUID " + returnElement.getGUID()
						+ " in reference mode.");
				
				return null;
			}
		}
		
		else if (aImportMode == ImportMode.update)
		{
			returnElement = aProject.findElementByGUID(this.getGuid());
			
			
			if (returnElement == null)
			{
				trace("toModelElement: Element with GUID: " + this.getGuid() + " not found in update mode.");
				return null;
			}
			
			if(isSet(name))
			{
				returnElement.setName(this.getName());
			}

			this.fullName = returnElement.getFullPathName();
			

			if (returnElement.getMetaClass().equals(metaClassName) == false)
			{
				trace("toModelElement: Metaclass mismatch for element with GUID " + this.getGuid()
						+ " in update mode.");
				return null;
			}
		}

		return returnElement;
	
	}
	
	
	public boolean setName(String aName)
	{
		if (isSet(aName) == false)
		{
			return false;
		}
		
		fullName = null; // will be updated when converting to model element

		name = aName;
		return true;
	}
	
	public boolean clearGUID()
	{
		guid = null;
		return true;
	}
	
	protected boolean isSet(String aString)
	{
		if (aString == null || aString.isEmpty())
		{
			return false;
		}
		return true;
	}
	
	
	protected IRPModelElement modelElementFromGUID(IRPProject aProject)
	{
		if (isSet(this.guid) == false)
		{
			return null;
		}

		IRPModelElement modelElement = aProject.findElementByGUID(guid);
		return modelElement;
	}

	protected List<JsonModelElementBase> convertToJsonModelElementBaseList(IRPCollection aCollection)
	{
		List<JsonModelElementBase> jsonModelElements = new ArrayList<JsonModelElementBase>();
		List<IRPModelElement> modelElements = aCollection.toList();
		for (IRPModelElement modelElement : modelElements)
		{
			JsonModelElementBase jsonModelElement = new JsonModelElementBase(modelElement);
			jsonModelElements.add(jsonModelElement);
		}
		return jsonModelElements;
	}
	
	
	
	protected List<JsonModelElementBase> convertToJsonModelElementList(IRPCollection aCollection)
	{
		if(myLevel>0)
		{
			return convertToJsonModelElementBaseList(aCollection);
		}
		
		List<JsonModelElementBase> theList = new ArrayList<JsonModelElementBase>();

		if (aCollection == null)
		{
			return theList;
		}
		
		int nextLevel = this.getLevel();

		for (Object obj : aCollection.toList())
		{
			if (obj instanceof IRPModelElement)
			{
				JsonModelElementBase jsonME = JsonModelFactory.Instance().getJsonModelElement((IRPModelElement) obj, nextLevel);
				
			
				
				theList.add(jsonME);
			}
		}

		return theList;
	}
	
	
	
	
	
	protected List<JsonModelElementBase> getFromList(List<JsonModelElementBase> aList, MetaClass aMetaClass)
	{
		List<JsonModelElementBase> JsonElements = new ArrayList<JsonModelElementBase>();
        for (JsonModelElementBase jsonModelElement : aList)
        {
            if (jsonModelElement.getMetaclass() == aMetaClass)
            {
            	JsonElements.add(jsonModelElement);
            }
        }
        return JsonElements;
	}
}
