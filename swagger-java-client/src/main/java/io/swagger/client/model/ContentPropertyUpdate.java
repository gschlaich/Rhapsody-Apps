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
import java.util.HashMap;
import java.util.Map;
/**
 * ContentPropertyUpdate
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2023-03-29T20:08:21.572793104Z[GMT]")
public class ContentPropertyUpdate extends HashMap<String, Object> {
  @SerializedName("value")
  private OneOfContentPropertyUpdateValue value = null;

  @SerializedName("version")
  private Map version = null;

  public ContentPropertyUpdate value(OneOfContentPropertyUpdateValue value) {
    this.value = value;
    return this;
  }

   /**
   * The value of the content property. This can be empty or a complex object.
   * @return value
  **/
  @Schema(required = true, description = "The value of the content property. This can be empty or a complex object.")
  public OneOfContentPropertyUpdateValue getValue() {
    return value;
  }

  public void setValue(OneOfContentPropertyUpdateValue value) {
    this.value = value;
  }

  public ContentPropertyUpdate version(Map version) {
    this.version = version;
    return this;
  }

   /**
   * Get version
   * @return version
  **/
  @Schema(required = true, description = "")
  public Map getVersion() {
    return version;
  }

  public void setVersion(Map version) {
    this.version = version;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ContentPropertyUpdate contentPropertyUpdate = (ContentPropertyUpdate) o;
    return Objects.equals(this.value, contentPropertyUpdate.value) &&
        Objects.equals(this.version, contentPropertyUpdate.version) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value, version, super.hashCode());
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ContentPropertyUpdate {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
    sb.append("    version: ").append(toIndentedString(version)).append("\n");
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
