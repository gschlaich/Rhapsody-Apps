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
import io.swagger.client.model.AffectedObject;
import io.swagger.client.model.AuditRecordCreateAuthor;
import io.swagger.client.model.ChangedValue;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/**
 * AuditRecordCreate
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2023-03-29T20:08:21.572793104Z[GMT]")
public class AuditRecordCreate {
  @SerializedName("author")
  private AuditRecordCreateAuthor author = null;

  @SerializedName("remoteAddress")
  private String remoteAddress = null;

  @SerializedName("creationDate")
  private Long creationDate = null;

  @SerializedName("summary")
  private String summary = null;

  @SerializedName("description")
  private String description = null;

  @SerializedName("category")
  private String category = null;

  @SerializedName("sysAdmin")
  private Boolean sysAdmin = false;

  @SerializedName("affectedObject")
  private AffectedObject affectedObject = null;

  @SerializedName("changedValues")
  private List<ChangedValue> changedValues = null;

  @SerializedName("associatedObjects")
  private List<AffectedObject> associatedObjects = null;

  public AuditRecordCreate author(AuditRecordCreateAuthor author) {
    this.author = author;
    return this;
  }

   /**
   * Get author
   * @return author
  **/
  @Schema(description = "")
  public AuditRecordCreateAuthor getAuthor() {
    return author;
  }

  public void setAuthor(AuditRecordCreateAuthor author) {
    this.author = author;
  }

  public AuditRecordCreate remoteAddress(String remoteAddress) {
    this.remoteAddress = remoteAddress;
    return this;
  }

   /**
   * The IP address of the computer where the event was initiated from.
   * @return remoteAddress
  **/
  @Schema(required = true, description = "The IP address of the computer where the event was initiated from.")
  public String getRemoteAddress() {
    return remoteAddress;
  }

  public void setRemoteAddress(String remoteAddress) {
    this.remoteAddress = remoteAddress;
  }

  public AuditRecordCreate creationDate(Long creationDate) {
    this.creationDate = creationDate;
    return this;
  }

   /**
   * The creation date-time of the audit record, as a timestamp. This is converted to a date-time display in the Confluence UI. If the &#x60;creationDate&#x60; is not specified, then it will be set to the timestamp for the current date-time.
   * @return creationDate
  **/
  @Schema(description = "The creation date-time of the audit record, as a timestamp. This is converted to a date-time display in the Confluence UI. If the `creationDate` is not specified, then it will be set to the timestamp for the current date-time.")
  public Long getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Long creationDate) {
    this.creationDate = creationDate;
  }

  public AuditRecordCreate summary(String summary) {
    this.summary = summary;
    return this;
  }

   /**
   * The summary of the event, which is displayed in the &#x27;Change&#x27; column on the audit log in the Confluence UI.
   * @return summary
  **/
  @Schema(description = "The summary of the event, which is displayed in the 'Change' column on the audit log in the Confluence UI.")
  public String getSummary() {
    return summary;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  public AuditRecordCreate description(String description) {
    this.description = description;
    return this;
  }

   /**
   * A long description of the event, which is displayed in the &#x27;Description&#x27; field on the audit log in the Confluence UI.
   * @return description
  **/
  @Schema(description = "A long description of the event, which is displayed in the 'Description' field on the audit log in the Confluence UI.")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public AuditRecordCreate category(String category) {
    this.category = category;
    return this;
  }

   /**
   * The category of the event, which is displayed in the &#x27;Event type&#x27; column on the audit log in the Confluence UI.
   * @return category
  **/
  @Schema(description = "The category of the event, which is displayed in the 'Event type' column on the audit log in the Confluence UI.")
  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public AuditRecordCreate sysAdmin(Boolean sysAdmin) {
    this.sysAdmin = sysAdmin;
    return this;
  }

   /**
   * Indicates whether the event was actioned by a system administrator.
   * @return sysAdmin
  **/
  @Schema(description = "Indicates whether the event was actioned by a system administrator.")
  public Boolean isSysAdmin() {
    return sysAdmin;
  }

  public void setSysAdmin(Boolean sysAdmin) {
    this.sysAdmin = sysAdmin;
  }

  public AuditRecordCreate affectedObject(AffectedObject affectedObject) {
    this.affectedObject = affectedObject;
    return this;
  }

   /**
   * Get affectedObject
   * @return affectedObject
  **/
  @Schema(description = "")
  public AffectedObject getAffectedObject() {
    return affectedObject;
  }

  public void setAffectedObject(AffectedObject affectedObject) {
    this.affectedObject = affectedObject;
  }

  public AuditRecordCreate changedValues(List<ChangedValue> changedValues) {
    this.changedValues = changedValues;
    return this;
  }

  public AuditRecordCreate addChangedValuesItem(ChangedValue changedValuesItem) {
    if (this.changedValues == null) {
      this.changedValues = new ArrayList<ChangedValue>();
    }
    this.changedValues.add(changedValuesItem);
    return this;
  }

   /**
   * The values that were changed in the event.
   * @return changedValues
  **/
  @Schema(description = "The values that were changed in the event.")
  public List<ChangedValue> getChangedValues() {
    return changedValues;
  }

  public void setChangedValues(List<ChangedValue> changedValues) {
    this.changedValues = changedValues;
  }

  public AuditRecordCreate associatedObjects(List<AffectedObject> associatedObjects) {
    this.associatedObjects = associatedObjects;
    return this;
  }

  public AuditRecordCreate addAssociatedObjectsItem(AffectedObject associatedObjectsItem) {
    if (this.associatedObjects == null) {
      this.associatedObjects = new ArrayList<AffectedObject>();
    }
    this.associatedObjects.add(associatedObjectsItem);
    return this;
  }

   /**
   * Objects that were associated with the event. For example, if the event was a space permission change then the associated object would be the space.
   * @return associatedObjects
  **/
  @Schema(description = "Objects that were associated with the event. For example, if the event was a space permission change then the associated object would be the space.")
  public List<AffectedObject> getAssociatedObjects() {
    return associatedObjects;
  }

  public void setAssociatedObjects(List<AffectedObject> associatedObjects) {
    this.associatedObjects = associatedObjects;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AuditRecordCreate auditRecordCreate = (AuditRecordCreate) o;
    return Objects.equals(this.author, auditRecordCreate.author) &&
        Objects.equals(this.remoteAddress, auditRecordCreate.remoteAddress) &&
        Objects.equals(this.creationDate, auditRecordCreate.creationDate) &&
        Objects.equals(this.summary, auditRecordCreate.summary) &&
        Objects.equals(this.description, auditRecordCreate.description) &&
        Objects.equals(this.category, auditRecordCreate.category) &&
        Objects.equals(this.sysAdmin, auditRecordCreate.sysAdmin) &&
        Objects.equals(this.affectedObject, auditRecordCreate.affectedObject) &&
        Objects.equals(this.changedValues, auditRecordCreate.changedValues) &&
        Objects.equals(this.associatedObjects, auditRecordCreate.associatedObjects);
  }

  @Override
  public int hashCode() {
    return Objects.hash(author, remoteAddress, creationDate, summary, description, category, sysAdmin, affectedObject, changedValues, associatedObjects);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AuditRecordCreate {\n");
    
    sb.append("    author: ").append(toIndentedString(author)).append("\n");
    sb.append("    remoteAddress: ").append(toIndentedString(remoteAddress)).append("\n");
    sb.append("    creationDate: ").append(toIndentedString(creationDate)).append("\n");
    sb.append("    summary: ").append(toIndentedString(summary)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    category: ").append(toIndentedString(category)).append("\n");
    sb.append("    sysAdmin: ").append(toIndentedString(sysAdmin)).append("\n");
    sb.append("    affectedObject: ").append(toIndentedString(affectedObject)).append("\n");
    sb.append("    changedValues: ").append(toIndentedString(changedValues)).append("\n");
    sb.append("    associatedObjects: ").append(toIndentedString(associatedObjects)).append("\n");
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