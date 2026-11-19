package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonModelSerializer
{

	public enum JsonFormat
	{
		FULL, COMPACT, INTTERFACE;

		public static JsonFormat fromString(String value)
		{
			if (value == null)
			{
				return FULL;
			}

			try
			{
				return valueOf(value.toUpperCase(java.util.Locale.ROOT));
			}
			catch(IllegalArgumentException e)
			{
				throw new IllegalArgumentException("Unsupported JSON format: " + value);
			}
		}
	}

	private final ObjectMapper mapper = new ObjectMapper();

	// Rhapsody management properties not needed by Copilot.
	private static final Set<String> EXCLUDED_FIELDS_COMPACT = new HashSet<>(
			Arrays.asList(
					"addToModelMode", 
					"cMHeader", 
					"cMState", 
					"currentDirectory", 
					"filename", 
					"includeInNextLoad",
					"lastModifiedTime", 
					"isReadOnly", 
					"isReferenceUnit", 
					"isSeparateSaveUnit", 
					"isStub", 
					"external",
					"owner",
					"fullName"
					));
	
	private static final Set<String> EXCLUDED_FIELDS_INTERFACE = new HashSet<>(
			Arrays.asList(
					"body"
				
					));

	

	public String serialize(JsonModelElementBase element, JsonFormat format) throws JsonProcessingException
	{

		if (format == JsonFormat.FULL)
		{
			// Preserve existing serialization behavior.
			return mapper.writeValueAsString(element);
		}
		
		// Convert DTO into a mutable JSON tree.
		JsonNode tree = mapper.valueToTree(element);
		
		if(format == JsonFormat.COMPACT)
		{
			serializeCompact(tree);
		}
		else if(format == JsonFormat.INTTERFACE)
		{
			serializeInterface(tree);
		}

		return mapper.writeValueAsString(tree);
	}

	private void serializeCompact(JsonNode node)
	{

		if (node.isObject())
		{
			ObjectNode object = (ObjectNode) node;

			Iterator<Map.Entry<String, JsonNode>> iterator = object.fields();

			while (iterator.hasNext())
			{
				Map.Entry<String, JsonNode> entry = iterator.next();

				String name = entry.getKey();
				JsonNode value = entry.getValue();

				if (EXCLUDED_FIELDS_COMPACT.contains(name))
				{
					iterator.remove();
					continue;
				}

				// Keep operation bodies, even when empty.
				if ("body".equals(name))
				{
					continue;
				}

				
				// Recursively process nested DTOs.
				serializeCompact(value);

				if (value.isContainerNode() && value.size() == 0)
				{
					iterator.remove();
				}
			}
		}
		else if (node.isArray())
		{
			ArrayNode array = (ArrayNode) node;

			for (JsonNode child : array)
			{
				serializeCompact(child);
			}
		}
	}
	
	private void serializeInterface(JsonNode node)
	{

		if (node.isObject())
		{
			ObjectNode object = (ObjectNode) node;

			Iterator<Map.Entry<String, JsonNode>> iterator = object.fields();

			while (iterator.hasNext())
			{
				Map.Entry<String, JsonNode> entry = iterator.next();

				String name = entry.getKey();
				JsonNode value = entry.getValue();

				if (EXCLUDED_FIELDS_COMPACT.contains(name))
				{
					iterator.remove();
					continue;
				}
				
				if (EXCLUDED_FIELDS_INTERFACE.contains(name))
				{
					iterator.remove();
					continue;
				}
				
				

				
				// Recursively process nested DTOs.
				serializeInterface(value);

				if (value.isContainerNode() && value.size() == 0)
				{
					iterator.remove();
				}
			}
		}
		else if (node.isArray())
		{
			ArrayNode array = (ArrayNode) node;

			for (JsonNode child : array)
			{
				serializeInterface(child);
			}
		}
	}
	
	
}
