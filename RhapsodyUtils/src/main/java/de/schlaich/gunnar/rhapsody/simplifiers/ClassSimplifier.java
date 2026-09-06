package de.schlaich.gunnar.rhapsody.simplifiers;

import java.util.function.Consumer;

import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.RPCodeGenSimplifier;

public class ClassSimplifier extends RPCodeGenSimplifier
{

	private Consumer<String> myTraceAction = null;
	
	public ClassSimplifier(Consumer<String> aTraceAction)
	{
		myTraceAction = aTraceAction;
	}
	
	public void trace(String aMessage)
	{
		
		aMessage = "ClassSimplifier: " + aMessage;
		
		
		if(myTraceAction!=null)
		{
			myTraceAction.accept(aMessage);
		}
	}
	
	@Override
	public void beginSimplification()
	{
		trace("beginSimplification");
		

	}

	@Override
	public void doAbort()
	{
		trace("doAbort");

	}

	@Override
	public void doExit()
	{
		trace("doExit");

	}

	@Override
	public void endSimplification()
	{
		trace("endSimplification");

	}

	@Override
	public void postSimplify(IRPModelElement userElement, IRPModelElement mainSimplifiedElement,
			String simplificationRequested)
	{
		trace("postSimplify");

	}

	@Override
	public void simplify(IRPModelElement userElement, IRPModelElement simplifiedElementOwner,
			String simplificationRequested)
	{
		trace("simplify");

	}

}
