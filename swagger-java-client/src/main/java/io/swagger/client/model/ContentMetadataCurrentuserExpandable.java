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
 * ContentMetadataCurrentuserExpandable
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2023-03-29T20:08:21.572793104Z[GMT]")
public class ContentMetadataCurrentuserExpandable {
  @SerializedName("favourited")
  private String favourited = null;

  @SerializedName("lastmodified")
  private String lastmodified = null;

  @SerializedName("lastcontributed")
  private String lastcontributed = null;

  @SerializedName("viewed")
  private String viewed = null;

  @SerializedName("scheduled")
  private String scheduled = null;

  public ContentMetadataCurrentuserExpandable favourited(String favourited) {
    this.favourited = favourited;
    return this;
  }

   /**
   * Get favourited
   * @return favourited
  **/
  @Schema(description = "")
  public String getFavourited() {
    return favourited;
  }

  public void setFavourited(String favourited) {
    this.favourited = favourited;
  }

  public ContentMetadataCurrentuserExpandable lastmodified(String lastmodified) {
    this.lastmodified = lastmodified;
    return this;
  }

   /**
   * Get lastmodified
   * @return lastmodified
  **/
  @Schema(description = "")
  public String getLastmodified() {
    return lastmodified;
  }

  public void setLastmodified(String lastmodified) {
    this.lastmodified = lastmodified;
  }

  public ContentMetadataCurrentuserExpandable lastcontributed(String lastcontributed) {
    this.lastcontributed = lastcontributed;
    return this;
  }

   /**
   * Get lastcontributed
   * @return lastcontributed
  **/
  @Schema(description = "")
  public String getLastcontributed() {
    return lastcontributed;
  }

  public void setLastcontributed(String lastcontributed) {
    this.lastcontributed = lastcontributed;
  }

  public ContentMetadataCurrentuserExpandable viewed(String viewed) {
    this.viewed = viewed;
    return this;
  }

   /**
   * Get viewed
   * @return viewed
  **/
  @Schema(description = "")
  public String getViewed() {
    return viewed;
  }

  public void setViewed(String viewed) {
    this.viewed = viewed;
  }

  public ContentMetadataCurrentuserExpandable scheduled(String scheduled) {
    this.scheduled = scheduled;
    return this;
  }

   /**
   * Get scheduled
   * @return scheduled
  **/
  @Schema(description = "")
  public String getScheduled() {
    return scheduled;
  }

  public void setScheduled(String scheduled) {
    this.scheduled = scheduled;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ContentMetadataCurrentuserExpandable contentMetadataCurrentuserExpandable = (ContentMetadataCurrentuserExpandable) o;
    return Objects.equals(this.favourited, contentMetadataCurrentuserExpandable.favourited) &&
        Objects.equals(this.lastmodified, contentMetadataCurrentuserExpandable.lastmodified) &&
        Objects.equals(this.lastcontributed, contentMetadataCurrentuserExpandable.lastcontributed) &&
        Objects.equals(this.viewed, contentMetadataCurrentuserExpandable.viewed) &&
        Objects.equals(this.scheduled, contentMetadataCurrentuserExpandable.scheduled);
  }

  @Override
  public int hashCode() {
    return Objects.hash(favourited, lastmodified, lastcontributed, viewed, scheduled);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ContentMetadataCurrentuserExpandable {\n");
    
    sb.append("    favourited: ").append(toIndentedString(favourited)).append("\n");
    sb.append("    lastmodified: ").append(toIndentedString(lastmodified)).append("\n");
    sb.append("    lastcontributed: ").append(toIndentedString(lastcontributed)).append("\n");
    sb.append("    viewed: ").append(toIndentedString(viewed)).append("\n");
    sb.append("    scheduled: ").append(toIndentedString(scheduled)).append("\n");
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
