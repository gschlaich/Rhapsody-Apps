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
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.IOException;
/**
 * ChangedValue
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2023-03-29T20:08:21.572793104Z[GMT]")
public class ChangedValue {
  @SerializedName("name")
  private String name = null;

  @SerializedName("oldValue")
  private String oldValue = null;

  @SerializedName("hiddenOldValue")
  private String hiddenOldValue = null;

  @SerializedName("newValue")
  private String newValue = null;

  @SerializedName("hiddenNewValue")
  private String hiddenNewValue = null;

  public ChangedValue name(String name) {
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

  public ChangedValue oldValue(String oldValue) {
    this.oldValue = oldValue;
    return this;
  }

   /**
   * Get oldValue
   * @return oldValue
  **/
  @Schema(required = true, description = "")
  public String getOldValue() {
    return oldValue;
  }

  public void setOldValue(String oldValue) {
    this.oldValue = oldValue;
  }

  public ChangedValue hiddenOldValue(String hiddenOldValue) {
    this.hiddenOldValue = hiddenOldValue;
    return this;
  }

   /**
   * Get hiddenOldValue
   * @return hiddenOldValue
  **/
  @Schema(description = "")
  public String getHiddenOldValue() {
    return hiddenOldValue;
  }

  public void setHiddenOldValue(String hiddenOldValue) {
    this.hiddenOldValue = hiddenOldValue;
  }

  public ChangedValue newValue(String newValue) {
    this.newValue = newValue;
    return this;
  }

   /**
   * Get newValue
   * @return newValue
  **/
  @Schema(required = true, description = "")
  public String getNewValue() {
    return newValue;
  }

  public void setNewValue(String newValue) {
    this.newValue = newValue;
  }

  public ChangedValue hiddenNewValue(String hiddenNewValue) {
    this.hiddenNewValue = hiddenNewValue;
    return this;
  }

   /**
   * Get hiddenNewValue
   * @return hiddenNewValue
  **/
  @Schema(description = "")
  public String getHiddenNewValue() {
    return hiddenNewValue;
  }

  public void setHiddenNewValue(String hiddenNewValue) {
    this.hiddenNewValue = hiddenNewValue;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ChangedValue changedValue = (ChangedValue) o;
    return Objects.equals(this.name, changedValue.name) &&
        Objects.equals(this.oldValue, changedValue.oldValue) &&
        Objects.equals(this.hiddenOldValue, changedValue.hiddenOldValue) &&
        Objects.equals(this.newValue, changedValue.newValue) &&
        Objects.equals(this.hiddenNewValue, changedValue.hiddenNewValue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, oldValue, hiddenOldValue, newValue, hiddenNewValue);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChangedValue {\n");
    
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    oldValue: ").append(toIndentedString(oldValue)).append("\n");
    sb.append("    hiddenOldValue: ").append(toIndentedString(hiddenOldValue)).append("\n");
    sb.append("    newValue: ").append(toIndentedString(newValue)).append("\n");
    sb.append("    hiddenNewValue: ").append(toIndentedString(hiddenNewValue)).append("\n");
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
