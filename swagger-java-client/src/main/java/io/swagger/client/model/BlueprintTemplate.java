/*
 * The Confluence Cloud REST API
 * This document describes the REST API and resources provided by Confluence. The REST APIs are for developers who want to integrate Confluence into their application and for administrators who want to script interactions with the Confluence server.Confluence's REST APIs provide access to resources (data entities) via URI paths. To use a REST API, your application will make an HTTP request and parse the response. The response format is JSON. Your methods will be the standard HTTP methods like GET, PUT, POST and DELETE. Because the REST API is based on open standards, you can use any web development language to access the API.
 *
 * OpenAPI spec version: 1.0.0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package io.swagger.client.model;

import java.util.Objects;
import java.util.Arrays;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.swagger.client.model.BlueprintTemplateExpandable;
import io.swagger.client.model.BlueprintTemplateOriginalTemplate;
import io.swagger.client.model.ContentTemplateBody;
import io.swagger.client.model.GenericLinks;
import io.swagger.client.model.Label;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * BlueprintTemplate
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2023-03-29T20:08:21.572793104Z[GMT]")
public class BlueprintTemplate {
  @SerializedName("templateId")
  private String templateId = null;

  @SerializedName("originalTemplate")
  private BlueprintTemplateOriginalTemplate originalTemplate = null;

  @SerializedName("referencingBlueprint")
  private String referencingBlueprint = null;

  @SerializedName("name")
  private String name = null;

  @SerializedName("description")
  private String description = null;

  @SerializedName("space")
  private Map<String, Object> space = null;

  @SerializedName("labels")
  private List<Label> labels = new ArrayList<Label>();

  @SerializedName("templateType")
  private String templateType = null;

  @SerializedName("editorVersion")
  private String editorVersion = null;

  @SerializedName("body")
  private ContentTemplateBody body = null;

  @SerializedName("_expandable")
  private BlueprintTemplateExpandable _expandable = null;

  @SerializedName("_links")
  private GenericLinks _links = null;

  public BlueprintTemplate templateId(String templateId) {
    this.templateId = templateId;
    return this;
  }

   /**
   * Get templateId
   * @return templateId
  **/
  @Schema(required = true, description = "")
  public String getTemplateId() {
    return templateId;
  }

  public void setTemplateId(String templateId) {
    this.templateId = templateId;
  }

  public BlueprintTemplate originalTemplate(BlueprintTemplateOriginalTemplate originalTemplate) {
    this.originalTemplate = originalTemplate;
    return this;
  }

   /**
   * Get originalTemplate
   * @return originalTemplate
  **/
  @Schema(required = true, description = "")
  public BlueprintTemplateOriginalTemplate getOriginalTemplate() {
    return originalTemplate;
  }

  public void setOriginalTemplate(BlueprintTemplateOriginalTemplate originalTemplate) {
    this.originalTemplate = originalTemplate;
  }

  public BlueprintTemplate referencingBlueprint(String referencingBlueprint) {
    this.referencingBlueprint = referencingBlueprint;
    return this;
  }

   /**
   * Get referencingBlueprint
   * @return referencingBlueprint
  **/
  @Schema(required = true, description = "")
  public String getReferencingBlueprint() {
    return referencingBlueprint;
  }

  public void setReferencingBlueprint(String referencingBlueprint) {
    this.referencingBlueprint = referencingBlueprint;
  }

  public BlueprintTemplate name(String name) {
    this.name = name;
    return this;
  }

   /**
   * Get name
   * @return name
  **/
  @Schema(required = true, description = "")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public BlueprintTemplate description(String description) {
    this.description = description;
    return this;
  }

   /**
   * Get description
   * @return description
  **/
  @Schema(required = true, description = "")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public BlueprintTemplate space(Map<String, Object> space) {
    this.space = space;
    return this;
  }

  public BlueprintTemplate putSpaceItem(String key, Object spaceItem) {
    if (this.space == null) {
      this.space = new HashMap<String, Object>();
    }
    this.space.put(key, spaceItem);
    return this;
  }

   /**
   * Get space
   * @return space
  **/
  @Schema(description = "")
  public Map<String, Object> getSpace() {
    return space;
  }

  public void setSpace(Map<String, Object> space) {
    this.space = space;
  }

  public BlueprintTemplate labels(List<Label> labels) {
    this.labels = labels;
    return this;
  }

  public BlueprintTemplate addLabelsItem(Label labelsItem) {
    this.labels.add(labelsItem);
    return this;
  }

   /**
   * Get labels
   * @return labels
  **/
  @Schema(required = true, description = "")
  public List<Label> getLabels() {
    return labels;
  }

  public void setLabels(List<Label> labels) {
    this.labels = labels;
  }

  public BlueprintTemplate templateType(String templateType) {
    this.templateType = templateType;
    return this;
  }

   /**
   * Get templateType
   * @return templateType
  **/
  @Schema(required = true, description = "")
  public String getTemplateType() {
    return templateType;
  }

  public void setTemplateType(String templateType) {
    this.templateType = templateType;
  }

  public BlueprintTemplate editorVersion(String editorVersion) {
    this.editorVersion = editorVersion;
    return this;
  }

   /**
   * Get editorVersion
   * @return editorVersion
  **/
  @Schema(description = "")
  public String getEditorVersion() {
    return editorVersion;
  }

  public void setEditorVersion(String editorVersion) {
    this.editorVersion = editorVersion;
  }

  public BlueprintTemplate body(ContentTemplateBody body) {
    this.body = body;
    return this;
  }

   /**
   * Get body
   * @return body
  **/
  @Schema(description = "")
  public ContentTemplateBody getBody() {
    return body;
  }

  public void setBody(ContentTemplateBody body) {
    this.body = body;
  }

  public BlueprintTemplate _expandable(BlueprintTemplateExpandable _expandable) {
    this._expandable = _expandable;
    return this;
  }

   /**
   * Get _expandable
   * @return _expandable
  **/
  @Schema(description = "")
  public BlueprintTemplateExpandable getExpandable() {
    return _expandable;
  }

  public void setExpandable(BlueprintTemplateExpandable _expandable) {
    this._expandable = _expandable;
  }

  public BlueprintTemplate _links(GenericLinks _links) {
    this._links = _links;
    return this;
  }

   /**
   * Get _links
   * @return _links
  **/
  @Schema(required = true, description = "")
  public GenericLinks getLinks() {
    return _links;
  }

  public void setLinks(GenericLinks _links) {
    this._links = _links;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BlueprintTemplate blueprintTemplate = (BlueprintTemplate) o;
    return Objects.equals(this.templateId, blueprintTemplate.templateId) &&
        Objects.equals(this.originalTemplate, blueprintTemplate.originalTemplate) &&
        Objects.equals(this.referencingBlueprint, blueprintTemplate.referencingBlueprint) &&
        Objects.equals(this.name, blueprintTemplate.name) &&
        Objects.equals(this.description, blueprintTemplate.description) &&
        Objects.equals(this.space, blueprintTemplate.space) &&
        Objects.equals(this.labels, blueprintTemplate.labels) &&
        Objects.equals(this.templateType, blueprintTemplate.templateType) &&
        Objects.equals(this.editorVersion, blueprintTemplate.editorVersion) &&
        Objects.equals(this.body, blueprintTemplate.body) &&
        Objects.equals(this._expandable, blueprintTemplate._expandable) &&
        Objects.equals(this._links, blueprintTemplate._links);
  }

  @Override
  public int hashCode() {
    return Objects.hash(templateId, originalTemplate, referencingBlueprint, name, description, space, labels, templateType, editorVersion, body, _expandable, _links);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BlueprintTemplate {\n");
    
    sb.append("    templateId: ").append(toIndentedString(templateId)).append("\n");
    sb.append("    originalTemplate: ").append(toIndentedString(originalTemplate)).append("\n");
    sb.append("    referencingBlueprint: ").append(toIndentedString(referencingBlueprint)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    space: ").append(toIndentedString(space)).append("\n");
    sb.append("    labels: ").append(toIndentedString(labels)).append("\n");
    sb.append("    templateType: ").append(toIndentedString(templateType)).append("\n");
    sb.append("    editorVersion: ").append(toIndentedString(editorVersion)).append("\n");
    sb.append("    body: ").append(toIndentedString(body)).append("\n");
    sb.append("    _expandable: ").append(toIndentedString(_expandable)).append("\n");
    sb.append("    _links: ").append(toIndentedString(_links)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}
