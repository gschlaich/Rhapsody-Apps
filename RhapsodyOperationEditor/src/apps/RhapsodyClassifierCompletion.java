package apps;

import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.fife.ui.autocomplete.AbstractCompletionProvider;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;

import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPEnumerationLiteral;
import com.telelogic.rhapsody.core.IRPGeneralization;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPTemplateInstantiation;
import com.telelogic.rhapsody.core.IRPType;

import RhapsodyUtilities.ASTHelper;
import RhapsodyUtilities.RhapsodyOperation;

public class RhapsodyClassifierCompletion extends BasicCompletion implements RhapsodyClassifier {

	
	private IRPClassifier myClassifier;
	private boolean myIsPointer = false;
	
	public RhapsodyClassifierCompletion( CompletionProvider aProvider, IRPClassifier aClassifier)
	{
		this(aProvider,aClassifier,false);
	}
	
	@SuppressWarnings("unchecked")
	public RhapsodyClassifierCompletion( CompletionProvider aProvider, IRPClassifier aClassifier, boolean aExtractEnum) {
		super(aProvider, aClassifier.getName());
		myClassifier = aClassifier;	
		setSummary( aClassifier.getDescription());
		setIcon(new ImageIcon(myClassifier.getIconFileName().replace('\\', '/')));
		
		
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
						myIsPointer = ASTHelper.isPointer(rType);
						if(rc!=null)
						{
							
							myClassifier = rc;
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
					System.out.println(myClassifier.getName());
					
				}
			}
		}
			
		
		
	}

	@Override
	public IRPClassifier getIRPClassifier() {
		
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
	public List<IRPClassifier> getNestedClassifiers() {
		return getIRPClassifier().getNestedClassifiers().toList();
	}

	@Override
	public IRPModelElement getElement() {
		return myClassifier;
	}


}