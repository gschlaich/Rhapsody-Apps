package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.victools.jsonschema.generator.Module;
import com.github.victools.jsonschema.generator.Option;
import com.github.victools.jsonschema.generator.OptionPreset;
import com.github.victools.jsonschema.generator.SchemaGenerationContext;
import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfig;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.generator.SchemaVersion;
import com.github.victools.jsonschema.generator.TypeScope;
import com.github.victools.jsonschema.generator.impl.DefinitionKey;
import com.github.victools.jsonschema.module.jackson.JacksonModule;
import com.github.victools.jsonschema.generator.naming.*;
import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;

import de.schlaich.gunnar.aiTools.mcp.jsonModels.JsonModelElementBase.ImportMode;
import de.schlaich.gunnar.aiTools.mcp.jsonModels.JsonModelElementBase.MetaClass;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.jackson.databind.JsonNode;

public class JsonModelFactory
{

	private Consumer<String> myTraceAction = null;
	private Map<MetaClass, Class<?>> factoryMap;
	Class<?> defaultClass = JsonModelElement.class;

	private Set<MetaClass> unregisteredMetaClass = new java.util.HashSet<MetaClass>();

	private static JsonModelFactory instance = null;

	IRPApplication myApp = null;
	
	private List<JsonModelElementBase> itsReferenceElements = new ArrayList<JsonModelElementBase>();

	private JsonModelFactory(IRPApplication aApp, Consumer<String> aTraceAction)
	{
		myTraceAction = aTraceAction;

		JsonModelElementBase.SetTraceAction(myTraceAction);

		factoryMap = new HashMap<MetaClass, Class<?>>();
		myApp = aApp;
		registerJsonClass(MetaClass.Package, JsonPackage.class);
		registerJsonClass(MetaClass.Class, JsonClass.class);
		registerJsonClass(MetaClass.LiteralSpecification, JsonLiteralSpecification.class);
		registerJsonClass(MetaClass.Operation, JsonOperation.class);
		registerJsonClass(MetaClass.Attribute, JsonAttribute.class);
		registerJsonClass(MetaClass.Variable, JsonVariable.class);
		registerJsonClass(MetaClass.Dependency, JsonDependency.class);
		registerJsonClass(MetaClass.Generalization, JsonGeneralization.class);
		registerJsonClass(MetaClass.Stereotype, JsonStereotype.class);
		registerJsonClass(MetaClass.Type, JsonType.class);
		// registerJsonClass(MetaClass.AssociationEnd, JsonAssociationClass.class);
		registerJsonClass(MetaClass.ObjectModelDiagram, JsonObjectModelDiagram.class);
		registerJsonClass(MetaClass.Argument, JsonArgument.class);
		registerJsonClass(MetaClass.AssociationEnd, JsonRelation.class);
		registerJsonClass(MetaClass.Object, JsonInstance.class);
		registerJsonClass(MetaClass.Statechart, JsonStatechart.class);
		registerJsonClass(MetaClass.State, JsonState.class);
		registerJsonClass(MetaClass.Transition, JsonTransition.class);
		registerJsonClass(MetaClass.DefaultTransition, JsonDefaultTransition.class);
		registerJsonClass(MetaClass.Guard, JsonGuard.class);
		registerJsonClass(MetaClass.Action, JsonAction.class);
		registerJsonClass(MetaClass.EventReception, JsonEventReception.class);
		registerJsonClass(MetaClass.Event, JsonEvent.class);
		registerJsonClass(MetaClass.Component, JsonComponent.class);
		registerJsonClass(MetaClass.HyperLink, JsonHyperLink.class);
		registerJsonClass(MetaClass.Module, JsonModule.class);
		registerJsonClass(MetaClass.File, JsonFile.class);
		registerJsonClass(MetaClass.FileElement, JsonFileFragment.class);
		registerJsonClass(MetaClass.Comment, JsonComment.class);
		registerJsonClass(MetaClass.Requirement, JsonRequirement.class);
		registerJsonClass(MetaClass.Constraint, JsonConstraint.class);
		registerJsonClass(MetaClass.Tag, JsonTag.class);
		registerJsonClass(MetaClass.StatechartDiagram, JsonStatechartDiagram.class);
		registerJsonClass(MetaClass.Project, JsonProject.class);
		registerJsonClass(MetaClass.Profile, JsonProfile.class);
		registerJsonClass(MetaClass.EnumerationLiteral, JsonEnumerationLiteral.class);
		registerJsonClass(MetaClass.InterfaceItemTrigger, JsonTrigger.class);
		registerJsonClass(MetaClass.Timeout, JsonTrigger.class);
		registerJsonClass(MetaClass.Connector, JsonConnector.class);
		registerJsonClass(MetaClass.Condition, JsonConnector.class);

	}

	public static JsonModelFactory Instance(IRPApplication aApp, Consumer<String> aTraceAction)
	{

		if (instance == null)
		{

			instance = new JsonModelFactory(aApp, aTraceAction);

		}

		return instance;
	}

	public List<MetaClass> getRegisteredMetaClasses()
	{
		return new ArrayList<MetaClass>(factoryMap.keySet());
	}
	
	public List<String> getRegisteredMetaClassNames()
	{
		List<String> ret = new ArrayList<String>();
		for (MetaClass mc : factoryMap.keySet())
		{
			ret.add(mc.name());
		}
		return ret;
	}
	
	public Boolean addReferenceElement(JsonModelElementBase aElement)
	{
		
		return itsReferenceElements.add(aElement);
	}
	
	public void traceReferenceElements()
    {
        trace("Reference Elements:");
        
		for (JsonModelElementBase elem : itsReferenceElements)
		{
			trace("Reference element " + elem.getName() + " guid " + elem.getGuid());
			trace("     Element fullName: " + elem.getFullName());
			trace("     Element metaclass: " + elem.getMetaclass());
			trace("     Element type:      " + elem.getClass().getSimpleName());
		}
        
    }
	
	public Boolean updateReferenceElement(String oldGuid, IRPModelElement aElement)
	{
		
		Boolean ret = false;
		for (JsonModelElementBase elem : itsReferenceElements)
		{
			if (elem.getGuid().equals(oldGuid))
			{
				//trace("Updating reference element " + elem.getName() + " old guid " + oldGuid + " to new guid " + aElement.getGUID());
				//trace("     Element fullName:              " + aElement.getFullPathName());
				elem.setGuid(aElement.getGUID());
				elem.setFullName(aElement.getFullPathName());
				//trace("     Element fullName after update: " + elem.getFullName());
				ret = true;

				
			}
		}
		
		return ret;		
	}
	
	public void clearReferenceElements()
	{
		itsReferenceElements.clear();
	}
	
	

	public String generateJsonSchemaForMetaClassName(String aMetaClassName)
	{
		MetaClass metaClass = null;
		try
		{
			metaClass = MetaClass.valueOf(aMetaClassName);
		}
		catch (IllegalArgumentException e)
		{
			trace("MetaClass " + aMetaClassName + " not found in MetaClass enum");
			return null;
		}
		return generateJsonSchemaForMetaClass(metaClass);
	}

	public String generateJsonSchemaForMetaClass(MetaClass aMetaClass)
	{
		Class<?> clazz = factoryMap.get(aMetaClass);
		if (clazz == null)
		{
			trace("No class registered for MetaClass " + aMetaClass);
			return null;
		}
		return generateJsonSchema(clazz);
	}

	public String _generateJsonSchema(Class<?> clazz)
	{
		String schemaString = null;
		try
		{

			JsonNode schema = generateSchema(clazz);

			ObjectMapper mapper = new ObjectMapper();
			schemaString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(schema);
		}
		catch (Exception e)
		{
			trace("Exception generating json schema for " + clazz.getSimpleName() + ": " + e.getMessage());
			e.printStackTrace();
		}

		return schemaString;
	}

	public String __generateJsonSchema(Class<?> clazz)
	{
		String schemaString = null;
		try
		{

			SchemaDefinitionNamingStrategy fqnNaming = new SchemaDefinitionNamingStrategy()
			{
				@Override
				public String getDefinitionNameForKey(DefinitionKey key, SchemaGenerationContext ctx)
				{
					// erasedType ist die "reale" Klasse hinter Generics/Scopes
					String ret = key.getType().getErasedType().getName(); // fully qualified

					trace(ret);

					return ret;
				}
			};

			SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2020_12,
					OptionPreset.PLAIN_JSON);

			configBuilder.forTypesInGeneral().withDefinitionNamingStrategy(fqnNaming);

			configBuilder.with(new JacksonModule());

			SchemaGeneratorConfig config = configBuilder.build();
			SchemaGenerator generator = new SchemaGenerator(config);
			JsonNode schema = generator.generateSchema(clazz);

			ObjectMapper mapper = new ObjectMapper();
			schemaString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(schema);
		}
		catch (Exception e)
		{
			trace("Exception generating json schema for " + clazz.getSimpleName() + ": " + e.getMessage());
			e.printStackTrace();
		}

		return schemaString;
	}

	private JsonNode generateSchema(Class<?> clazz)
	{
		SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_7,
				OptionPreset.PLAIN_JSON);

		// Erst die Custom NamingStrategy setzen, BEVOR das JacksonModule hinzugef�gt
		// wird
		configBuilder.forTypesInGeneral().withDefinitionNamingStrategy((type, context) ->
		{
			return type.getType().getTypeName();
		});

		// Dann das JacksonModule hinzuf�gen (ohne NamingStrategy-Option)
		JacksonModule jacksonModule = new JacksonModule();
		configBuilder.with(jacksonModule).with(Option.DEFINITIONS_FOR_ALL_OBJECTS);

		SchemaGeneratorConfig config = configBuilder.build();
		SchemaGenerator generator = new SchemaGenerator(config);

		return generator.generateSchema(clazz);
	}

	public String generateJsonSchema(Class<?> clazz)
	{

		String schemaString = null;
		try
		{
			ObjectMapper mapper = new ObjectMapper();

			JsonSchemaGenerator schemaGen = new JsonSchemaGenerator(mapper);

			// Schema f�r die Basisklasse generieren
			JsonSchema schema = schemaGen.generateSchema(clazz);

			schema.set$schema("http://json-schema.org/draft-04/schema#");

			// Schema als formatierte JSON ausgeben und speichern
			schemaString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(schema);


		}
		catch (Exception e)
		{
			trace("Exception" + " generating json schema for " + clazz.getSimpleName() + ": " + e.getMessage());
			e.printStackTrace();
		}

		return schemaString;
	}

	public String generateJsonSchemaForRegisteredClasses()
	{
		StringBuilder allSchemas = new StringBuilder();

		for (Class<?> clazz : factoryMap.values())
		{
			String schema = generateJsonSchema(clazz);
			allSchemas.append(schema).append("\n\n");
		}

		return allSchemas.toString();
	}

	public static JsonModelFactory Instance()
	{
		if (instance == null)
		{
			throw new IllegalStateException("JsonFactory not initialized. Call Instance(IRPApplication) first.");
		}

		return instance;
	}

	@SuppressWarnings("unchecked")

	public JsonModelElementBase getJsonModelElement(IRPModelElement aModelElement, int depth)
	{

		JsonModelElementBase ret = null;

		String metaClassName = aModelElement.getMetaClass();

		MetaClass[] values = MetaClass.values();

		MetaClass metaClass = MetaClass.ModelElement;

		try
		{

			metaClass = MetaClass.valueOf(metaClassName);
		}
		catch (IllegalArgumentException e)
		{
			trace("MetaClass " + metaClassName + " not found in MetaClass enum");
		}

		Class<? extends JsonModelElement> jsonRefClass = (Class<? extends JsonModelElement>) factoryMap
				.getOrDefault(metaClass, defaultClass);

		if (jsonRefClass == defaultClass)
		{

			if (!unregisteredMetaClass.contains(metaClass))
			{
				unregisteredMetaClass.add(metaClass);
				trace(" ---- No explicit json class for metaclass " + metaClass + " found. Using default.");
			}

		}

		if (jsonRefClass == null)
		{
			return null;
		}

		try
		{
			Constructor<? extends JsonModelElement> c = jsonRefClass.getConstructor(IRPModelElement.class, int.class);

			ret = c.newInstance(aModelElement, depth);
		}
		catch (Exception e)
		{
			
			trace(e.toString());
			trace("Exception creating json model element for " + metaClass + ": " + e.getMessage());
			ret = new JsonModelElementBase(aModelElement);
		}

		trace("Json Model: " + ret.getMetaclass() + " " + ret.getName());
		return ret;

	}

	public JsonModelElementBase getJsonModel(JsonModelElementBase aJsonBase)
	{
		String guid = aJsonBase.getGuid();

		IRPProject project = myApp.activeProject();
		IRPModelElement element = project.findElementByGUID(guid);

		if (element == null)
		{
			return null;
		}
		
		

		return getJsonModelElement(element, 0);

	}
	
	public IRPApplication getRhapsodyApplication()
	{
		return myApp;
	}

	public boolean registerJsonClass(MetaClass aMetaClass, Class<?> aClass)
	{
		if (factoryMap.put(aMetaClass, aClass) == null)
		{
			return false;
		}

		return true;
	}

	public JsonModelElementBase fromJson(String jsonString)
	{
		try
		{
			clearReferenceElements();
			
			ObjectMapper mapper = new ObjectMapper();
			JsonModelElementBase baseElement = mapper.readValue(jsonString, JsonModelElementBase.class);
			
			//traceReferenceElements();

			// Then convert to the appropriate type using the existing factory methods
			return baseElement;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			trace("Exception deserializing json: " + e.getMessage());

		}

		return null;

	}
	
	public IRPModelElement createModelElement(JsonModelElementBase aJsonElement, IRPModelElement parentElement, IRPProject project)	
	{
		JsonModelElementBase parentJme = getJsonModelElement(parentElement, 0);
		
		IRPModelElement ret = aJsonElement.toModelElement(parentJme, project, ImportMode.create);
		
		clearReferenceElements();
		
		return ret;
	}

	private void trace(String aMessage)
	{
		if (myTraceAction == null)
		{
			// no traceaction set...
			return;
		}

		aMessage = this.getClass().getSimpleName() + ": " + aMessage;

		myTraceAction.accept(aMessage);
	}

}
