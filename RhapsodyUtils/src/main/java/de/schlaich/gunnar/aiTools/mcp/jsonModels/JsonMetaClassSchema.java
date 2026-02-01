package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import de.schlaich.gunnar.aiTools.mcp.jsonModels.JsonModelElementBase.MetaClass;

@JsonTypeInfo( use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "Type") 
@JsonSubTypes({ 
	@JsonSubTypes.Type(value = JsonMetaClassSchema.class, name = "Schema") 
	})


public class JsonMetaClassSchema
{

	
	@JsonProperty("metaClass")
	protected MetaClass metaClass = null;
	
	
	@JsonProperty("schemaBase64")
	protected String schemaBase64 = null;
	
	public JsonMetaClassSchema(MetaClass metaClass)
	{
		this.metaClass = metaClass;
		
		
		JsonModelFactory factory = JsonModelFactory.Instance();
		
		if (factory == null)
		{
			return;
		}
		
		schemaBase64 = factory.generateJsonSchemaForMetaClass(metaClass);

	}
	
	public String toJsonString() throws IOException
	{
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		String jsonString = objectMapper.writeValueAsString(this);
		return jsonString;
	}

}
