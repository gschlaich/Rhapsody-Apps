package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPCollection;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPState;
import com.telelogic.rhapsody.core.IRPType;

import de.schlaich.gunnar.aiTools.mcp.jsonModels.JsonModelElementBase.ImportMode;
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

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "Type")
@JsonSubTypes(
{ @JsonSubTypes.Type(value = JsonModelElement.class, name = "ModelElement"),
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
		@JsonSubTypes.Type(value = JsonEnumerationLiteral.class, name = "EnumerationLiteral"),
		@JsonSubTypes.Type(value = JsonTrigger.class, name = "InterfaceItemTrigger"),
		@JsonSubTypes.Type(value = JsonConnector.class, name = "Connector"),
		@JsonSubTypes.Type(value = JsonConnector.class, name = "Condition"),
		@JsonSubTypes.Type(value = JsonConnector.class, name = "HistoryConnector"),
		@JsonSubTypes.Type(value = JsonConnector.class, name = "JunctionConnector"),
		@JsonSubTypes.Type(value = JsonCollaboration.class, name = "Collaboration"),
		@JsonSubTypes.Type(value = JsonMessage.class, name = "Message"),
		@JsonSubTypes.Type(value = JsonClassifierRole.class, name = "ClassifierRole")

})

public class JsonModelElementBase
{

	private static Consumer<String> myTraceAction = null;

	public enum MetaClass
	{
		ModelElement, AcceptEventAction, AcceptTimeEvent, Action, ActionBlock, ActivityDiagram, Actor, Argument,
		Association, AssociationEnd, Attribute, CallOperation, Class, ClassifierRole, Cleanup, CombinedFragment,
		Comment, CommunicationDiagram, Component, ComponentDiagram, ComponentInstance, Condition, ConditionMark,
		Configuration, Connector, Constraint, Constructor, ControlledFile, DefaultTransition, Dependency,
		DeploymentDiagram, Destructor, EnumerationLiteral, Event, EventReception, ExecutionOccurrence, File,
		FileElement, Flow, Folder, Generalization, Guard, HistoryConnector, HyperLink, InterfaceItemTrigger,
		Initializer, InstanceSlot, InstanceSpecification, InteractionOccurrence, InteractionOperand, ItemFlow,
		JunctionConnector, Link, LiteralSpecification, MatrixLayout, MatrixView, Message, Module, Node, Object,
		ObjectModelDiagram, ObjectNode, Operation, Package, PanelDiagram, Pin, Port, Profile, Project, Reception,
		ReferenceActivity, Requirement, SequenceDiagram, State, Statechart, StatechartDiagram, Stereotype,
		StructureDiagram, Swimlane, SysMLPort, TableLayout, TableView, Tag, TemplateInstantiation, Transition,
		TriggeredOperation, Type, UseCase, UseCaseDiagram, Variable, Trigger, Timeout, Collaboration
	}

	public enum ImportMode
	{
		create, reference
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

	public void setAttributes(IRPModelElement aModelElement, IRPProject aProject, ImportMode aImportMode)
	{

		if (aProject.findElementByGUID(this.getGuid()) == null)
		{
			// use old GUID when not already used (paste from other branch or update (delete
			// and paste))
			aModelElement.setGUID(this.getGuid());
		}
		else
		{
			String oldGuid = guid;
			guid = aModelElement.getGUID();

			JsonModelFactory factory = JsonModelFactory.Instance();
			factory.updateReferenceElement(oldGuid, aModelElement);
		}

		this.fullName = aModelElement.getFullPathName();

	}

	public IRPModelElement toModelElement(JsonModelElementBase aRootElement, IRPProject aProject,
			ImportMode aImportMode)
	{

		if (aProject == null)
		{
			trace("toModelElement: Project is null.");
			return null;
		}

		if (aImportMode == ImportMode.reference)
		{
			return getReference(aProject);
		}

		if (aRootElement == null)
		{
			trace("toModelElement: Root element is null in create mode.");
			return null;
		}

		IRPModelElement parentElement = aRootElement.getReference(aProject);

		if (parentElement == null)
		{
			trace("toModelElement: Failed to get reference for root element with GUID: " + aRootElement.getGuid()
					+ " in create mode.");
			return null;
		}

		IRPModelElement modelElement = createModelElement(parentElement);

		if (modelElement == null)
		{
			trace("toModelElement: Failed to create element " + this.getName() + " of type "
					+ this.getMetaclass().name() + " in create mode.");
			return null;
		}

		setAttributes(modelElement, aProject, aImportMode);

		return modelElement;

	}

//	public IRPModelElement getModelElement(JsonModelElementBase rootElement, IRPProject aProject, ImportMode aImportMode)
//	{
//		IRPModelElement ret = toModelElement(rootElement, aProject, aImportMode);
//		
//		if(ret==null)
//		{
//			trace("createModelElement: Failed to create element " + this.getName() + " of type " + this.getMetaclass().name());
//			return null;
//		}
//		
//		if (ret.getName().equals(this.getName()) == false)
//		{
//			try
//			{
//				ret.setName(this.getName());
//			}
//			catch (Exception e)
//			{
//				trace("createModelElement: Exception setting name to " + this.getName() + ": " + e.getMessage());
//			}
//			
//		}
//		
//		return ret;
//	}

	protected IRPModelElement createModelElement(IRPModelElement aParentElement)
	{

		IRPModelElement modelElement = null;

		if (aParentElement == null)
		{
			trace("createModelElement: Parent element is null.");
			return null;
		}

		if (isSet(name) == false)
		{
			trace("toModelElement: Name not set for element with GUID: " + this.getGuid() + " in create mode.");
			return null;
		}

		creationName = this.getName();

		if (checkElementExist(aParentElement))
		{
			creationName = CREATION_PREFIX + this.getName();
		}

		try
		{

			modelElement = aParentElement.addNewAggr(metaClass.name(), creationName);
		}
		catch(Exception e)
		{
			trace("Exception creating new element of type " + metaClass.name() + ": " + e.getMessage());
		}

		return modelElement;

	}

	protected boolean checkElementExist(IRPModelElement aParentElement)
	{
		if (aParentElement.findNestedElement(this.getName(), metaClass.name()) != null)
		{
			trace("Element already exists under root element " + aParentElement.getFullPathName() + " with name "
					+ this.getName() + " and metaclass " + metaClass.name() + " in create mode.");

			return true;

		}

		return false;
	}

	public IRPModelElement getReference(IRPProject aProject)
	{
		IRPModelElement returnElement;
		returnElement = aProject.findElementByGUID(this.getGuid());

		if (returnElement == null)
		{
			trace("toModelElement: Element with GUID: " + this.getGuid() + " not found in reference mode.");

			// try to find element via full name
			if (isSet(this.getFullName()) == false)
			{
				trace("toModelElement: FullName not set for element with GUID: " + this.getGuid()
						+ " in reference mode.");

				return null;
			}
			returnElement = aProject.findElementsByFullName(this.getFullName(), this.metaClass.name());

			if (returnElement == null)
			{
				trace("toModelElement: Element with FullName: " + this.getFullName() + " not found in reference mode.");
				return null;
			}

		}

		if (returnElement.getMetaClass().equals(this.metaClass.name()) == false)
		{
			trace("toModelElement: Metaclass mismatch for element with GUID " + returnElement.getGUID()
					+ " in reference mode.");

			return null;
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

	protected List<IRPModelElement> convertToModelElementList(List<JsonModelElementBase> jsonModelElements,
			IRPProject aProject, ImportMode aImportMode)
	{

		List<IRPModelElement> modelElements = new ArrayList<IRPModelElement>();
		for (JsonModelElementBase jsonModelElement : jsonModelElements)
		{
			IRPModelElement element = jsonModelElement.toModelElement(this, aProject, aImportMode);
			if (element != null)
			{
				modelElements.add(element);
			}
		}

		return modelElements;

	}

	protected List<JsonModelElementBase> convertToJsonModelElementList(IRPCollection aCollection)
	{
		if (myLevel > 0)
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
				JsonModelElementBase jsonME = JsonModelFactory.Instance().getJsonModelElement((IRPModelElement) obj,
						nextLevel);

				theList.add(jsonME);

			}
		}

		return theList;
	}

	public JsonModelElementBase createJsonModelElement(IRPModelElement modelElement, int level)
	{
		return JsonModelFactory.Instance().getJsonModelElement(modelElement, level);
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

	public List<JsonModelElementBase> getNestedElements()
	{
		return new ArrayList<JsonModelElementBase>();
	}

}
