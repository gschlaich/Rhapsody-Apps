package de.schlaich.gunnar.aiTools.mcp.jsonModels;


import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import de.schlaich.gunnar.aiTools.mcp.jsonModels.JsonModelElementBase.MetaClass;



@JsonTypeInfo( use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "Type") 
@JsonSubTypes({ 
	@JsonSubTypes.Type(value = JsonAvailableMetaClass.class, name = "AvailableMetaClasses") 
	})


public class JsonAvailableMetaClass
{

	@JsonProperty("availableMetaClasses")
	List<MetaClass> availableMetaClasses = null;
	
	public JsonAvailableMetaClass()
	{
		JsonModelFactory factory = JsonModelFactory.Instance();
		
		availableMetaClasses = factory.getRegisteredMetaClasses();
	}
	
	public String toJsonString() throws IOException
	{
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		String jsonString = objectMapper.writeValueAsString(this);
		return jsonString;
	}



}

