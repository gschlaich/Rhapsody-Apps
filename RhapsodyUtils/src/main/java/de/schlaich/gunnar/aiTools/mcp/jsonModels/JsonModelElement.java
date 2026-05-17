package de.schlaich.gunnar.aiTools.mcp.jsonModels;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.cdt.internal.core.model.Parent;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;

import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPStereotype;

public class JsonModelElement extends JsonModelElementBase {

	@JsonProperty("newTermStereotype")
	protected JsonModelElementBase newTermStereotype = null;

	@JsonProperty("description")
	protected String description = "";

	@JsonProperty("stereotypes")
	protected List<JsonModelElementBase> stereotypes;

	@JsonProperty("nestedElements")
	protected List<JsonModelElementBase> nestedElements;

	@JsonProperty("changedProperties")
	protected Map<String, String> changedProperties;

	@JsonProperty("owner")
	protected JsonModelElementBase owner = null;

	@JsonProperty("external")
	protected boolean external = false;

	public JsonModelElement(IRPModelElement aModelElement, int level) {
		super(aModelElement, level);

		IRPModelElement ownerElement = aModelElement.getOwner();

		if (ownerElement != null) {
			owner = new JsonModelElementBase(ownerElement);
		}

		getNestedElements(aModelElement);

		stereotypes = convertToJsonModelElementBaseList(aModelElement.getStereotypes());

		external = aModelElement.getIsExternal() != 0;

		this.changedProperties = getOverriddenProperties(aModelElement);

		description = aModelElement.getDescription();

		if (aModelElement.getNewTermStereotype() != null) {
			newTermStereotype = new JsonModelElementBase(aModelElement.getNewTermStereotype());
		}

	}

	protected void getNestedElements(IRPModelElement aModelElement) {
		nestedElements = convertToJsonModelElementList(aModelElement.getNestedElements());
	}

	private Map<String, String> getOverriddenProperties(IRPModelElement aModelElement) {
		List<String> overriddenProperties = aModelElement.getOverriddenProperties(0).toList();

		Map<String, String> ret = new HashMap<String, String>();
		if (overriddenProperties != null && overriddenProperties.size() > 0) {

			for (String property : overriddenProperties) {
				String[] propertyKeyValue = property.split(":", 2);
				ret.put(propertyKeyValue[0], propertyKeyValue[1]);
			}
		}
		return ret;
	}

	public JsonModelElement() {

	}

	/*
	 * 
	 * public IRPModelElement toModelElement(JsonModelElementBase aRootElement,
	 * IRPProject aProject, ImportMode aImportMode) {
	 * 
	 * 
	 * IRPModelElement returnElement = null;
	 * 
	 * if (aImportMode == ImportMode.create) { IRPModelElement parentElement =
	 * aRootElement.toModelElement((JsonModelElementBase)null, aProject,
	 * ImportMode.reference); returnElement = createModelElement(parentElement); }
	 * else { returnElement = super.toModelElement(aRootElement, aProject,
	 * ImportMode.reference); return returnElement; }
	 * 
	 * if (returnElement == null) { return null; }
	 * 
	 * 
	 * setAttributes(returnElement, aProject, aImportMode);
	 * 
	 * return returnElement; }
	 * 
	 */

	@Override
	public void setAttributes(IRPModelElement aModelElement, IRPProject aProject, ImportMode aImportMode) {

		super.setAttributes(aModelElement, aProject, aImportMode);

		addNestedElements(aProject, aImportMode);

		// set description
		if (isSet(description)) {
			aModelElement.setDescription(this.description);
		}

		setStereotypes(aModelElement, aProject, aImportMode);

		setProperties(aModelElement, aImportMode);

	}

	protected void addNestedElements(IRPProject aProject, ImportMode aImportMode) {
		for (JsonModelElementBase jsonNestedElement : this.nestedElements) {
			jsonNestedElement.toModelElement(this, aProject, aImportMode);

		}
	}

	protected void setStereotypes(IRPModelElement aModelElement, IRPProject aProject, ImportMode aImportMode) {

		if (aImportMode == ImportMode.create) {
			if (aModelElement.getStereotypes().toList().size() > 0) {
				trace("On creation, the model must not have any stereotypes yet");
				return;
			}
		}

		if (aImportMode == ImportMode.reference) {
			// do nothing
			return;
		}

		// add new ones below
		for (JsonModelElementBase jsonStereotype : this.stereotypes) {

			IRPModelElement modelElement = jsonStereotype.getReference(aProject);

			if (modelElement != null) {
				IRPStereotype stereotype = (IRPStereotype) modelElement;

				if (stereotype != null) {
					aModelElement.addSpecificStereotype(stereotype);
				}

			}

		}

	}

	protected void setProperties(IRPModelElement aModelElement, ImportMode aImportMode) {

		if (aImportMode == ImportMode.reference) 
		{
			// do nothing
			return;
		}
		
		if (changedProperties == null || changedProperties.size() == 0) 
		{
			// no changed properties, so nothing to do
			return;
		}

		Set<String> keySet = changedProperties.keySet();

		for (String key : keySet) {
			String propertyValue = changedProperties.get(key);
			String actualValue = aModelElement.getPropertyValue(key);
			if (actualValue.equals(propertyValue) == false) {
				aModelElement.setPropertyValue(key, propertyValue);
			}
		}

	}

	public static JsonModelElement GetJsonModelElement(IRPModelElement aModelElement, int level) {
		if (aModelElement instanceof IRPModelElement) {
			return new JsonModelElement(aModelElement, level);
		}

		return null;

	}

	List<JsonModelElementBase> getTemplates() {
		// todo
		return null;
	}

	List<JsonModelElementBase> getStereotypes() {
		return getFromList(nestedElements, MetaClass.Stereotype);
	}

	List<JsonModelElementBase> getHyperLinks() {
		return getFromList(nestedElements, MetaClass.HyperLink);
	}

	List<JsonModelElementBase> getLocalTags() {
		return getFromList(nestedElements, MetaClass.Tag);
	}

	List<JsonModelElementBase> getOwnedDependencies() {
		return getFromList(nestedElements, MetaClass.Dependency);
	}

	JsonModelElementBase getNewTermStereotype() {
		return newTermStereotype;
	}

	@Override
	public List<JsonModelElementBase> getNestedElements() {
		return nestedElements;
	}

	/*
	 * public String toJsonString() throws IOException { ObjectMapper objectMapper =
	 * new ObjectMapper(); objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
	 * return objectMapper.writeValueAsString(this); }
	 */
}
