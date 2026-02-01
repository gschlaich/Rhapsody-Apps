package de.schlaich.gunnar.aiTools.mcp.tools;

import java.util.Map;
import java.util.function.Consumer;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import de.schlaich.gunnar.aiTools.mcp.RhapsodyClient;

public class AvailableMetaClassesTool extends Tool
{

	private final RhapsodyClient rh;
	
	
	public AvailableMetaClassesTool(RhapsodyClient rh, Consumer<String> aTraceAction)
	{
		
		super("rhapsody-MetaClasses", "Provide a list of all supported metaclasses", new LinkedHashMap<String, Object>()
		{
			{
				put("$schema", "http://json-schema.org/draft-07/schema#");
				put("type", "object");
				put("properties", new LinkedHashMap<String, Object>());
				put("additionalProperties", Boolean.FALSE);
				
				
			}
		}, aTraceAction);
			
		this.rh = rh;
	}

	@Override
	public Object call(Map<String, Object> args) throws Exception
	{
		
		if (!args.isEmpty())
	    {
	        throw new IllegalArgumentException("This tool does not accept parameters");
	    }

		
		
		List<String> metaClasses =  rh.getAllMetaClassNames();
		Map<String, Object> out = new LinkedHashMap<String, Object>();
		out.put("content", metaClasses);
		out.put("count", metaClasses.size());
		return out;
		
		
	}

}