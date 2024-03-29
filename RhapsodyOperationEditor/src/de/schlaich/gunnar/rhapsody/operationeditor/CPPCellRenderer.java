package de.schlaich.gunnar.rhapsody.operationeditor;

import javax.swing.JList;

import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionCellRenderer;
import org.fife.ui.autocomplete.FunctionCompletion;
import org.fife.ui.autocomplete.VariableCompletion;

public class CPPCellRenderer extends CompletionCellRenderer{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	

	/**
	 * Constructor.
	 */
	public CPPCellRenderer() {
		
	}


	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("rawtypes")
	@Override
	protected void prepareForOtherCompletion(JList list,
			Completion c, int index, boolean selected, boolean hasFocus) {
		super.prepareForOtherCompletion(list, c, index, selected, hasFocus);
		
	}


	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("rawtypes")
	@Override
	protected void prepareForVariableCompletion(JList list,
			VariableCompletion vc, int index, boolean selected,
			boolean hasFocus) {
		super.prepareForVariableCompletion(list, vc, index, selected,
										hasFocus);
		
	}


	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("rawtypes")
	@Override
	protected void prepareForFunctionCompletion(JList list,
			FunctionCompletion fc, int index, boolean selected,
			boolean hasFocus) {
		super.prepareForFunctionCompletion(list, fc, index, selected,
										hasFocus);
		
	}


}
	
