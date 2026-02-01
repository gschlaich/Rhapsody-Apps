package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.telelogic.rhapsody.core.IRPModelElement;

import de.schlaich.gunnar.aiTools.mcp.jsonModels.JsonModelElementBase.MetaClass;

public class JsonCache
{

	private Consumer<String> myTraceAction = null;
	Map<String, JsonModelElementBase> myCachedElements = null;
	
	public JsonCache(Consumer<String> aTraceAction)
	{
		myTraceAction = aTraceAction;
		myCachedElements = new HashMap<>();
	}
	
	public void addToCache(IRPModelElement aModelElement, MetaClass aMetaClass)
	{
		
		if (aModelElement == null)
		{
			return;
		}
		
		
		trace("generating " + aMetaClass + " List..");

		List<IRPModelElement> nestedElements = aModelElement.getNestedElementsByMetaClass(aMetaClass.toString(), 1).toList();
		
		trace("generate JsonElements..");
		
		
		List<JsonModelElementBase> jsonElements = new ArrayList<>();
		
		
		for (IRPModelElement theElement : nestedElements)
		{
			
			JsonModelElementBase jsonElement = new JsonModelElementBase(theElement, 0);
			jsonElements.add(jsonElement);
		}
		
		trace("adding to cache..");
		
		for (JsonModelElementBase theJsonElement : jsonElements)
		{
			myCachedElements.put(theJsonElement.getGuid(), theJsonElement);
		}
		
		trace("cache size: " + myCachedElements.size());
		
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
