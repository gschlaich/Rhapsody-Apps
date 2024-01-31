package de.schlaich.gunnar.rhapsody.utilities;

import java.util.List;

import javax.swing.Icon;

import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPArgument;
import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPDependency;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPOperation;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPType;

public class RhapsodyOperation {
	
	private static IconProvider myIconProvider;
	
	static
	{
		myIconProvider = new IconProvider();
	}
	
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
		
		
		String pattern =  argClassifier.getPropertyValue(propertyPath);
		
		try
		{
			pattern =  aArgument.getPropertyValueExplicit(propertyPath);
		}
		catch(Exception e)
		{
			
		}
		
		
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
		
		if((ret==null)||(ret.isEmpty()))
		{
			ret = aOperation.getReturnTypeDeclaration();
			
		}

		return ret;
	}
	
	public static IRPPackage getPackage(IRPModelElement aElement)
	{
		if(aElement==null)
		{
			return null;
		}
		
		if(aElement instanceof IRPPackage)
		{
			return (IRPPackage) aElement;
		}
		else
		{
			return getPackage(aElement.getOwner());
		}
	}

	public static IRPPackage getNamespacePackage(IRPModelElement aElement)
	{
		IRPPackage p = getPackage(aElement);
		
		if(p==null)
		{
			return null;
		}
		
		if(p.getNamespace().equals(""))
		{
			//packages has no namespace!
			return p;
		}
		
		if(p.getPropertyValue("CPP_CG.Package.DefineNameSpace").equals("False"))
		{
			return getNamespacePackage(p.getOwner());
		}
		return p;
	}
	
	public static String getNamespace(IRPModelElement aElement)
	{
		
		IRPPackage nameSpacePackage = getNamespacePackage(aElement);
		if(nameSpacePackage==null)
		{
			return null;
		}
		
		return nameSpacePackage.getNamespace();
		
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
		return aOperation.getImplementationSignature();
	}
	
	public static String getOperation_(IRPOperation aOperation)
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
	
	
	@SuppressWarnings("unchecked")
	public static IRPClassifier findClassifier(IRPClassifier aClassifier, String aName)
	{
		IRPClassifier ret = null;
		
		IRPModelElement owner = aClassifier.getOwner();
		System.out.println("Owner: " + owner.getName());
		System.out.println("Classifier:" + aClassifier.getName());
		
		
		if(owner instanceof IRPPackage)
		{
			ret = findClassifier((IRPPackage)owner, aName);
		}
		else if(owner instanceof IRPClassifier)
		{
			ret = findClassifier((IRPClassifier)owner,aName);
		}

		if(ret==null)
		{
			List<IRPDependency> dependencies = owner.getDependencies().toList();
			for( IRPDependency dependency:dependencies)
			{
				IRPModelElement element = dependency.getDependsOn();
				if(element instanceof IRPPackage)
				{
					ret = findClassifier((IRPPackage)element, aName);
					if(ret!=null)
					{
						break;
					}					
				}
			}
		}
		
		if(ret == null)
		{
			//search in the whole model
			IRPProject proj = aClassifier.getProject();
			
			if(proj!=null)
			{
				ret = (IRPClassifier)proj.findAllByName(aName, "Class");
			}
		}

		return ret;
		
	}
	
	
	
	
	public static IRPClassifier findClassifier(IRPPackage aPackage, String aName)
	{
		
		IRPClassifier ret = null;
		if(aPackage!=null)
		{
			ret = aPackage.findClass(aName);
			if(ret==null)
			{
				ret = aPackage.findType(aName);
			}
		}
		
		return ret;
	}
	
	
	public static Icon getIcon(IRPModelElement eElement)
	{
		return myIconProvider.getIcon(eElement);
	}
	
	public static Icon getIcon(String aFileName)
	{
		return myIconProvider.getIcon(aFileName);
	}
	
}


