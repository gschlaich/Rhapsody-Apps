package de.schlaich.gunnar.rhapsody.completion;

import java.util.List;

import org.apache.commons.imaging.formats.tiff.constants.GeoTiffTagConstants;
import org.fife.ui.autocomplete.AbstractCompletionProvider;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;

import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPType;

import de.schlaich.gunnar.rhapsody.utilities.ASTHelper;
import de.schlaich.gunnar.rhapsody.utilities.RhapsodyOperation;

public class RhapsodyTypeCompletion extends BasicCompletion implements RhapsodyClassifier {

	IRPType myType;
	
	public RhapsodyTypeCompletion(CompletionProvider provider, IRPType aType) {
		super(provider, aType.getName(), aType.getDescription());
		myType = aType;
		
		if(myType.isKindLanguage()==1)
		{
			String[] literals = ASTHelper.parseLanguageEnumType(myType);
			if((provider instanceof AbstractCompletionProvider)&&(literals!=null))
			{
				AbstractCompletionProvider abstractProvider = (AbstractCompletionProvider)provider;
				for(String literal : literals)
				{
					RhapsodyEnumLiteralCompletion relc = new RhapsodyEnumLiteralCompletion(provider, myType, literal);
					abstractProvider.addCompletion(relc);
				}
			}
		}
		
		setIcon( RhapsodyOperation.getIcon(myType));
		
	}
	
	
	@Override
	public IRPClassifier getIRPClassifier(boolean aPointer) {
		
		return myType.getTypedefBaseType();
	}

	@Override
	public IRPModelElement getElement() {
		
		return myType;
	}
	
	public IRPType getType() {
		
		return myType;
	}

	@Override
	public List<IRPClassifier> getNestedClassifiers(boolean aPointer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isPointer() {
		return false;
	}


	@Override
	public boolean isValue() {
		return true;
	}

}
