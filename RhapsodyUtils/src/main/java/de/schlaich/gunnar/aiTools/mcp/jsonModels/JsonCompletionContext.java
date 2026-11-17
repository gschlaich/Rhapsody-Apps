package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;

@JsonTypeInfo( use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "Type") 
@JsonSubTypes({
	@JsonSubTypes.Type(value = JsonCompletionContext.class, name = "CompletionContext")
	
	})

public class JsonCompletionContext
{
	
	@JsonProperty("contextName")
	protected String contextName = null;
	
	@JsonProperty("context")
	protected JsonModelElementBase context = null;
	
	@JsonProperty("referenceElements")
	protected List<JsonModelElementBase> myReferenceElements = null;
	
	public JsonCompletionContext(String contextName, JsonModelElementBase context, Collection<JsonModelElementBase> referenceElements)
	{
		this.contextName = contextName;
		this.context = context;
		myReferenceElements = new ArrayList<JsonModelElementBase>();

		for(JsonModelElementBase referenceElement : referenceElements)
		{
			if(referenceElement.getGuid().equals(context.getGuid())==false)
			{
				myReferenceElements.add(referenceElement);
			}
		}
	}
	
	public String toJsonString() throws IOException
	{
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		//objectMapper.enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
		String jsonString = objectMapper.writeValueAsString(this);
		return jsonString;
	}

}
