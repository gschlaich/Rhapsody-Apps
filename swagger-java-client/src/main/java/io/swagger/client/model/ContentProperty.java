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
import io.swagger.client.model.ContentPropertyExpandable;
import io.swagger.client.model.GenericLinks;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
/**
 * ContentProperty
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2023-03-29T20:08:21.572793104Z[GMT]")
public class ContentProperty extends HashMap<String, Object> {
  @SerializedName("id")
  private String id = null;

  @SerializedName("key")
  private String key = null;

  @SerializedName("value")
  private OneOfContentPropertyValue value = null;

  @SerializedName("version")
  private Map version = null;

  @SerializedName("_links")
  private GenericLinks _links = null;

  @SerializedName("_expandable")
  private ContentPropertyExpandable _expandable = null;

  public ContentProperty id(String id) {
    this.id = id;
    return this;
  }

   /**
   * Get id
   * @return id
  **/
  @Schema(required = true, description = "")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ContentProperty key(String key) {
    this.key = key;
    return this;
  }

   /**
   * Get key
   * @return key
  **/
  @Schema(required = true, description = "")
  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public ContentProperty value(OneOfContentPropertyValue value) {
    this.value = value;
    return this;
  }

   /**
   * The value of the content property. This can be empty or a complex object.
   * @return value
  **/
  @Schema(required = true, description = "The value of the content property. This can be empty or a complex object.")
  public OneOfContentPropertyValue getValue() {
    return value;
  }

  public void setValue(OneOfContentPropertyValue value) {
    this.value = value;
  }

  public ContentProperty version(Map version) {
    this.version = version;
    return this;
  }

   /**
   * Get version
   * @return version
  **/
  @Schema(description = "")
  public Map getVersion() {
    return version;
  }

  public void setVersion(Map version) {
    this.version = version;
  }

  public ContentProperty _links(GenericLinks _links) {
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

  public ContentProperty _expandable(ContentPropertyExpandable _expandable) {
    this._expandable = _expandable;
    return this;
  }

   /**
   * Get _expandable
   * @return _expandable
  **/
  @Schema(description = "")
  public ContentPropertyExpandable getExpandable() {
    return _expandable;
  }

  public void setExpandable(ContentPropertyExpandable _expandable) {
    this._expandable = _expandable;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ContentProperty contentProperty = (ContentProperty) o;
    return Objects.equals(this.id, contentProperty.id) &&
        Objects.equals(this.key, contentProperty.key) &&
        Objects.equals(this.value, contentProperty.value) &&
        Objects.equals(this.version, contentProperty.version) &&
        Objects.equals(this._links, contentProperty._links) &&
        Objects.equals(this._expandable, contentProperty._expandable) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, key, value, version, _links, _expandable, super.hashCode());
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ContentProperty {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    key: ").append(toIndentedString(key)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
    sb.append("    version: ").append(toIndentedString(version)).append("\n");
    sb.append("    _links: ").append(toIndentedString(_links)).append("\n");
    sb.append("    _expandable: ").append(toIndentedString(_expandable)).append("\n");
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
