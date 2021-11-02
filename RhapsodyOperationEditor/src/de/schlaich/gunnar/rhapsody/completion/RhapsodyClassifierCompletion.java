package de.schlaich.gunnar.rhapsody.completion;

import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.text.JTextComponent;

import org.fife.ui.autocomplete.AbstractCompletionProvider;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;

import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPEnumerationLiteral;
import com.telelogic.rhapsody.core.IRPGeneralization;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPOperation;
import com.telelogic.rhapsody.core.IRPTemplateInstantiation;
import com.telelogic.rhapsody.core.IRPTemplateInstantiationParameter;
import com.telelogic.rhapsody.core.IRPTemplateParameter;
import com.telelogic.rhapsody.core.IRPType;

import de.schlaich.gunnar.rhapsody.utilities.ASTHelper;
import de.schlaich.gunnar.rhapsody.utilities.RhapsodyOperation;

public class RhapsodyClassifierCompletion extends BasicCompletion implements RhapsodyClassifier {

	
	private IRPClassifier myClassifier;
	private IRPClassifier myClassifierPointer;
	private boolean myIsPointer = false;
	private boolean myIsValue = true;
	
	private String definedIn;

	
	public RhapsodyClassifierCompletion( CompletionProvider aProvider, IRPClassifier aClassifier)
	{
		this(aProvider,aClassifier,true);
	}
	
	@SuppressWarnings("unchecked")
	public RhapsodyClassifierCompletion( CompletionProvider aProvider, IRPClassifier aClassifier, boolean aExtractEnum) {
		super(aProvider, aClassifier.getName());
		myClassifier = aClassifier;	
		setSummary( aClassifier.getDescription());
		setShortDescription(aClassifier.getDescription());
		
		setIcon(RhapsodyOperation.getIcon(aClassifier));
		
		setDefinedIn(aClassifier.getOwner().getFullPathNameIn());
		
		AbstractCompletionProvider abstractProvider = (AbstractCompletionProvider)aProvider;
		if(abstractProvider!=null)
		{
			List<IRPGeneralization> gs = myClassifier.getGeneralizations().toList();
			for(IRPGeneralization g : gs)
			{
				RhapsodyClassifierCompletion rcc = new RhapsodyClassifierCompletion(aProvider, g.getBaseClass());
				abstractProvider.addCompletion(rcc);
			}
		}
		
		
		if(myClassifier instanceof IRPType)
		{
			
			IRPType rType = (IRPType)myClassifier;
			if(rType.isKindLanguage()==1)
			{
				if(aExtractEnum &&(rType.isEnum()==1))
				{
					String[] enumNames = ASTHelper.parseLanguageEnumType(rType);
					if(enumNames==null)
					{
						return;
					}
					for(String enumName : enumNames)
					{
						RhapsodyEnumLiteralCompletion rlc = new RhapsodyEnumLiteralCompletion(abstractProvider, rType, enumName);
						abstractProvider.addCompletion(rlc);
					}
				}
				else if(rType.getIsTypedef()==1)
				{
					setSummary(rType.getDeclaration().replace("%s", rType.getName()));
					String classifierName = ASTHelper.parseLanguageTypedef(rType);

					if(classifierName!=null)
					{
						IRPClassifier rc = RhapsodyOperation.findClassifier(myClassifier, classifierName);
						myClassifier = rc;
						if(ASTHelper.isPointer(rType))
						{
							myIsPointer = true;
							myIsValue = false;
						}
						
						if(rc!=null)
						{
							if(rc.isATemplate()==1)
							{
								if(rc.findNestedElement("operator->", "Operation")!=null)
								{
									List<String> templateInstantiations = ASTHelper.getLanguageTypedefTemplateArguments(rType);
									if(templateInstantiations!=null && templateInstantiations.size()!=0)
									{
										for(String cs:templateInstantiations)
										{
											IRPClassifier trc = RhapsodyOperation.findClassifier(rType, cs);
											if(trc!=null)
											{
												myClassifierPointer = trc;
												myIsPointer=true;
												break;
											}
										}
									}	
								}
								
							}
							
						}

					}
				}
			}
			else if(aExtractEnum && (rType.isKindEnumeration()==1))
			{
				List<IRPEnumerationLiteral> literals = rType.getEnumerationLiterals().toList();
				for(IRPEnumerationLiteral literal:literals)
				{
					RhapsodyEnumLiteralCompletion rlc = new RhapsodyEnumLiteralCompletion(abstractProvider, literal);
					abstractProvider.addCompletion(rlc);
				}
				
			}
			else if (rType.isKindTypedef()==1)
			{
				myClassifier = rType.getTypedefBaseType();
				if(rType.isPointer()==1)
				{
					myIsPointer = true;
					myClassifierPointer = myClassifier;
					myClassifier = null;
					myIsValue = false;
				}
			}
	
		}
		else
		{
			//check for template instantiation
			IRPTemplateInstantiation ti = myClassifier.getTi();
			if(ti!=null)
			{
				IRPModelElement t = ti.getOfTemplate();
			
				if((t!=null)&&(t instanceof IRPClassifier))
				{
					myClassifier = (IRPClassifier)t;
					
					if(myClassifier.findNestedElement("operator->", "Operation")!=null)
					{
						List<IRPTemplateInstantiationParameter> params = ti.getTemplateInstantiationParameters().toList();// t.getTemplateParameters().toList();
						if(params.isEmpty()==false)
						{
							//hacky
							IRPTemplateInstantiationParameter param = params.get(0);
							myClassifierPointer = param.getType();
							myIsPointer = true;
						}	
					}
				}
			}
		}
	}

	@Override
	public IRPClassifier getIRPClassifier(boolean aPointer) {
		
		if(aPointer)
		{
			return myClassifierPointer;
		}
		return myClassifier;
	}
	
	@Override
	public String toString() {
		return this.getInputText();
	}
	
	@Override
	public boolean isPointer() {
		return myIsPointer;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IRPClassifier> getNestedClassifiers(boolean aPointer) {
		IRPClassifier classifier = getIRPClassifier(aPointer);
		if(classifier==null)
		{
			return null;
		}
		return classifier.getNestedClassifiers().toList();
	}

	@Override
	public IRPModelElement getElement() {
		return myClassifier;
	}
	
	
	
	/**
	 * Returns where this variable is defined.
	 *
	 * @return Where this variable is defined.
	 * @see #setDefinedIn(String)
	 */
	public String getDefinedIn() {
		return definedIn;
	}
	
	/**
	 * Returns the name of this variable.
	 *
	 * @return The name.
	 */
	public String getName() {
		return getReplacementText();
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getSummary() {
		StringBuilder sb = new StringBuilder();
		possiblyAddDescription(sb);
		possiblyAddDefinedIn(sb);
		return sb.toString();
	}


	
	

	/**
	 * Adds some HTML describing where this variable is defined, if this
	 * information is known.
	 *
	 * @param sb The buffer to append to.
	 */
	protected void possiblyAddDefinedIn(StringBuilder sb) {
		if (definedIn!=null) {
			sb.append("<hr>Defined in:"); // TODO: Localize me
			sb.append(" <em>").append(definedIn).append("</em>");
		}
	}


	/**
	 * Adds the description text as HTML to a buffer, if a description is
	 * defined.
	 *
	 * @param sb The buffer to append to.
	 * @return Whether there was a description to add.
	 */
	protected boolean possiblyAddDescription(StringBuilder sb) {
		if (getShortDescription()!=null) {
			sb.append("<hr><br>");
			sb.append(getShortDescription());
			sb.append("<br><br><br>");
			return true;
		}
		return false;
	}


	/**
	 * Sets where this variable is defined.
	 *
	 * @param definedIn Where this variable is defined.
	 * @see #getDefinedIn()
	 */
	public void setDefinedIn(String definedIn) {
		this.definedIn = definedIn;
	}

	@Override
	public boolean isValue() {
		
		return myIsValue;
	}

	
	


	

}