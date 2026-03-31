package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import com.telelogic.rhapsody.core.IRPModelElement;


public class JsonClassifierRole extends JsonModelElement
{
	
//			IRPClassifier 	getFormalClassifier()
//			      Returns the classifier (for example, class or actor) that the lifeline realizes.
//			IRPInstance 	getFormalInstance()
//			      For cases where a lifeline represents an object and not just a classifier, returns the object that is realized by the lifeline.
//			IRPSequenceDiagram 	getReferencedSequenceDiagram()
//			      Returns the sequence diagram referenced by the lifeline.
//			IRPCollection 	getReferencingClassifierRolesRecursively()
//			      Returns a collection of all the lifelines in referenced sequence diagrams.
//			java.lang.String 	getRoleType()
//			      Returns a string representing the type of the classifier role, for example, CLASS for elements of type IRPClass and ACTOR for elements of type IRPActor.
//				java.lang.String 	getType()
//		
		
	
	public JsonClassifierRole(IRPModelElement aModelElement, int level)
	{
		super(aModelElement, level);
	}
	
	public JsonClassifierRole()
	{
		
	}

}
