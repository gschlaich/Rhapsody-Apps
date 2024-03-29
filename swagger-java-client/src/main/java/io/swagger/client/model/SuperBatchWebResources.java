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
import io.swagger.client.model.SuperBatchWebResourcesTags;
import io.swagger.client.model.SuperBatchWebResourcesUris;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * SuperBatchWebResources
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2023-03-29T20:08:21.572793104Z[GMT]")
public class SuperBatchWebResources {
  @SerializedName("uris")
  private SuperBatchWebResourcesUris uris = null;

  @SerializedName("tags")
  private SuperBatchWebResourcesTags tags = null;

  @SerializedName("metatags")
  private String metatags = null;

  @SerializedName("_expandable")
  private Map<String, Object> _expandable = null;

  public SuperBatchWebResources uris(SuperBatchWebResourcesUris uris) {
    this.uris = uris;
    return this;
  }

   /**
   * Get uris
   * @return uris
  **/
  @Schema(description = "")
  public SuperBatchWebResourcesUris getUris() {
    return uris;
  }

  public void setUris(SuperBatchWebResourcesUris uris) {
    this.uris = uris;
  }

  public SuperBatchWebResources tags(SuperBatchWebResourcesTags tags) {
    this.tags = tags;
    return this;
  }

   /**
   * Get tags
   * @return tags
  **/
  @Schema(description = "")
  public SuperBatchWebResourcesTags getTags() {
    return tags;
  }

  public void setTags(SuperBatchWebResourcesTags tags) {
    this.tags = tags;
  }

  public SuperBatchWebResources metatags(String metatags) {
    this.metatags = metatags;
    return this;
  }

   /**
   * Get metatags
   * @return metatags
  **/
  @Schema(description = "")
  public String getMetatags() {
    return metatags;
  }

  public void setMetatags(String metatags) {
    this.metatags = metatags;
  }

  public SuperBatchWebResources _expandable(Map<String, Object> _expandable) {
    this._expandable = _expandable;
    return this;
  }

  public SuperBatchWebResources putExpandableItem(String key, Object _expandableItem) {
    if (this._expandable == null) {
      this._expandable = new HashMap<String, Object>();
    }
    this._expandable.put(key, _expandableItem);
    return this;
  }

   /**
   * Get _expandable
   * @return _expandable
  **/
  @Schema(description = "")
  public Map<String, Object> getExpandable() {
    return _expandable;
  }

  public void setExpandable(Map<String, Object> _expandable) {
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
    SuperBatchWebResources superBatchWebResources = (SuperBatchWebResources) o;
    return Objects.equals(this.uris, superBatchWebResources.uris) &&
        Objects.equals(this.tags, superBatchWebResources.tags) &&
        Objects.equals(this.metatags, superBatchWebResources.metatags) &&
        Objects.equals(this._expandable, superBatchWebResources._expandable);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uris, tags, metatags, _expandable);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SuperBatchWebResources {\n");
    
    sb.append("    uris: ").append(toIndentedString(uris)).append("\n");
    sb.append("    tags: ").append(toIndentedString(tags)).append("\n");
    sb.append("    metatags: ").append(toIndentedString(metatags)).append("\n");
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
