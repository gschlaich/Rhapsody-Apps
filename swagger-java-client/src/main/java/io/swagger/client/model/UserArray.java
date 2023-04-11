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
import io.swagger.client.model.GenericLinks;
import io.swagger.client.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/**
 * UserArray
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2023-03-29T20:08:21.572793104Z[GMT]")
public class UserArray implements OneOfContentRestrictionUpdateRestrictionsUser {
  @SerializedName("results")
  private List<User> results = new ArrayList<User>();

  @SerializedName("start")
  private Integer start = null;

  @SerializedName("limit")
  private Integer limit = null;

  @SerializedName("size")
  private Integer size = null;

  @SerializedName("totalSize")
  private Long totalSize = 0l;

  @SerializedName("_links")
  private GenericLinks _links = null;

  public UserArray results(List<User> results) {
    this.results = results;
    return this;
  }

  public UserArray addResultsItem(User resultsItem) {
    this.results.add(resultsItem);
    return this;
  }

   /**
   * Get results
   * @return results
  **/
  @Schema(required = true, description = "")
  public List<User> getResults() {
    return results;
  }

  public void setResults(List<User> results) {
    this.results = results;
  }

  public UserArray start(Integer start) {
    this.start = start;
    return this;
  }

   /**
   * Get start
   * @return start
  **/
  @Schema(description = "")
  public Integer getStart() {
    return start;
  }

  public void setStart(Integer start) {
    this.start = start;
  }

  public UserArray limit(Integer limit) {
    this.limit = limit;
    return this;
  }

   /**
   * Get limit
   * @return limit
  **/
  @Schema(description = "")
  public Integer getLimit() {
    return limit;
  }

  public void setLimit(Integer limit) {
    this.limit = limit;
  }

  public UserArray size(Integer size) {
    this.size = size;
    return this;
  }

   /**
   * Get size
   * @return size
  **/
  @Schema(description = "")
  public Integer getSize() {
    return size;
  }

  public void setSize(Integer size) {
    this.size = size;
  }

  public UserArray totalSize(Long totalSize) {
    this.totalSize = totalSize;
    return this;
  }

   /**
   * This property will return total count of the objects before pagination is applied. This value is returned if &#x60;shouldReturnTotalSize&#x60; is set to &#x60;true&#x60;.
   * @return totalSize
  **/
  @Schema(description = "This property will return total count of the objects before pagination is applied. This value is returned if `shouldReturnTotalSize` is set to `true`.")
  public Long getTotalSize() {
    return totalSize;
  }

  public void setTotalSize(Long totalSize) {
    this.totalSize = totalSize;
  }

  public UserArray _links(GenericLinks _links) {
    this._links = _links;
    return this;
  }

   /**
   * Get _links
   * @return _links
  **/
  @Schema(description = "")
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
    UserArray userArray = (UserArray) o;
    return Objects.equals(this.results, userArray.results) &&
        Objects.equals(this.start, userArray.start) &&
        Objects.equals(this.limit, userArray.limit) &&
        Objects.equals(this.size, userArray.size) &&
        Objects.equals(this.totalSize, userArray.totalSize) &&
        Objects.equals(this._links, userArray._links);
  }

  @Override
  public int hashCode() {
    return Objects.hash(results, start, limit, size, totalSize, _links);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserArray {\n");
    
    sb.append("    results: ").append(toIndentedString(results)).append("\n");
    sb.append("    start: ").append(toIndentedString(start)).append("\n");
    sb.append("    limit: ").append(toIndentedString(limit)).append("\n");
    sb.append("    size: ").append(toIndentedString(size)).append("\n");
    sb.append("    totalSize: ").append(toIndentedString(totalSize)).append("\n");
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
