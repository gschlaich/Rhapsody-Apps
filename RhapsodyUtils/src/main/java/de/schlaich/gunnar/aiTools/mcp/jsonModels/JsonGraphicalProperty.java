package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.telelogic.rhapsody.core.IRPGraphicalProperty;

@JsonTypeInfo( use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "Type") 
public class JsonGraphicalProperty
{

//	  	java.lang.String 	getInterfaceName()
//		      get property interfaceName
//		java.lang.String 	getKey()
//		      get property key
//		java.lang.String 	getValue()
//		      get property value
		
	@JsonProperty("interfaceName")
	protected String interfaceName = null;
	@JsonProperty("key")
	protected String key = null;
	@JsonProperty("value")
	protected String value = null;
	
	
	public JsonGraphicalProperty(IRPGraphicalProperty aGraphicalProperty)
	{
		if (aGraphicalProperty == null)
		{
			return;
		}
		this.interfaceName = aGraphicalProperty.getInterfaceName();
		this.key = aGraphicalProperty.getKey();
		this.value = aGraphicalProperty.getValue();
	}
	
	public JsonGraphicalProperty()
	{
		// TODO Auto-generated constructor stub
	}
	
	public boolean hasValue()
	{
		return (this.value != null && this.value.isEmpty() == false);
	}

}
