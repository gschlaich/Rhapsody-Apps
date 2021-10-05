package RhapsodyUtilities;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.telelogic.rhapsody.core.IRPArgument;
import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPCollection;
import com.telelogic.rhapsody.core.IRPDependency;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPOperation;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPType;
import com.telelogic.rhapsody.core.RPClassifier;

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
		
		if((ret==null)||(ret.isEmpty()))
		{
			ret = aOperation.getReturnTypeDeclaration();
			
		}

		return ret;
	}
	
	
	public static String getNamespace(IRPModelElement aElement)
	{
		if(aElement==null)
		{
			return null;
		}
		
		
		if((aElement instanceof IRPPackage)==false)
		{
			return getNamespace(aElement.getOwner());
		}
		else
		{
			IRPPackage p = (IRPPackage)aElement;
			String ret = p.getNamespace();
			if((ret==null)||(ret.equals("")))
			{
				return getNamespace(aElement.getOwner());
			}
			return ret;
		}
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
	
	
	public static IRPClassifier findClassifier(IRPClassifier aClassifier, String aName)
	{
		IRPClassifier ret = null;
		
		IRPModelElement owner = aClassifier.getOwner();
		//System.out.println("Owner: " + owner.getName());
		//System.out.println("Classifier:" + aClassifier.getName());
		
		
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
	
}


