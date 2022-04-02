package de.schlaich.gunnar.serializable.completion;

import java.io.Serializable;

public class SerializableVariableCompletion  extends SerializableBasicCompletion implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The variable's type.
	 */
	private String type;

	/**
	 * What library (for example) this variable is defined in.
	 */
	private String definedIn;

	public SerializableVariableCompletion(String name, String type, String shortDesc, String summary ) {
		super(name, shortDesc, summary);
	}

}
