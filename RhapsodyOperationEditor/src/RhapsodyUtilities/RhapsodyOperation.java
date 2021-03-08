package RhapsodyUtilities;

import java.util.List;

import com.telelogic.rhapsody.core.IRPArgument;
import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPOperation;
import com.telelogic.rhapsody.core.IRPType;

public class RhapsodyOperation {
	
	public static String getArgumentType(IRPArgument aArgument)
	{
		
		String onTheFly = aArgument.getDeclaration();
		
		if((onTheFly!=null)&&(!onTheFly.isEmpty()))
		{
			return aArgument.getDeclaration();
		}
		
		IRPClassifier argClassifier = aArgument.getType();
		
		String direction = aArgument.getArgumentDirection();
		String propertyPath =  "CPP_CG.Class."+ direction;
		if(argClassifier instanceof IRPType)
		{
			propertyPath = "CPP_CG.Type."+direction;
		}
		String pattern = argClassifier.getPropertyValue(propertyPath);
		
		
		if(argClassifier instanceof IRPType)
		{
			pattern = argClassifier.getPropertyValue(propertyPath);
		}
		
		//check if property of aArgument is overwritten
		try 
		{
			pattern = aArgument.getPropertyValueExplicit(propertyPath);
		} 
		catch (Exception e) { }
		
		String argType =  pattern.replace("$type", argClassifier.getName());
		
		
		return argType;
	}
	
	public static String getReturnType(IRPOperation aOperation)
	{
		
		
		IRPClassifier returnType = aOperation.getReturns();
		
		String propertyPath = "CPP_CG.Class.ReturnType";
		
		
		if(returnType instanceof IRPType)
		{
			propertyPath = "CPP_CG.Type.ReturnType";
		}
		
		String returnPattern = "";
		try
		{
			returnPattern = returnType.getPropertyValue(propertyPath);
		}
		catch (Exception e) {
			return aOperation.getReturnTypeDeclaration();
		}
		
		try
		{
			returnPattern = aOperation.getPropertyValueExplicit(propertyPath);
		}
		catch (Exception e) { }
		
		String ret = returnPattern.replace("$type", returnType.getName());

		return ret;
	}
	
	public static List<IRPArgument> getArguments(IRPOperation aOperation)
	{
		@SuppressWarnings("unchecked")
		List<IRPArgument> arguments = aOperation.getArguments().toList();
		return arguments;
	}
	
	public static String getArgument(IRPArgument aArgument)
	{
		return getArgumentType(aArgument)+" "+aArgument.getName();
	}
	
	public static String getOperation(IRPOperation aOperation)
	{
		StringBuffer sBuffer = new StringBuffer();
		
		if(aOperation.getIsStatic()==1)
		{
			sBuffer.append("static ");
		}
		
		if(aOperation.getIsVirtual()==1)
		{
			sBuffer.append("virtual ");
		}
		
		sBuffer.append(getReturnType(aOperation));
		sBuffer.append(" ");
		sBuffer.append(aOperation.getOwner().getName());
		sBuffer.append("::");
		sBuffer.append(aOperation.getName());
		sBuffer.append("(");
		boolean setComma = false;
		for(IRPArgument argument:getArguments(aOperation))
		{
			if(setComma)
			{
				sBuffer.append(", ");
			}
			else
			{
				setComma = true;
			}
			sBuffer.append(getArgument(argument));
		}
		sBuffer.append(")");
		if(aOperation.getIsConst()==1)
		{
			sBuffer.append(" const");
		}
		
		return sBuffer.toString();
	}

}


