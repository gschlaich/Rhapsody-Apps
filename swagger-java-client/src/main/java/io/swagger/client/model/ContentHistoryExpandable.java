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
 * ContentHistoryExpandable
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2023-03-29T20:08:21.572793104Z[GMT]")
public class ContentHistoryExpandable {
  @SerializedName("lastUpdated")
  private String lastUpdated = null;

  @SerializedName("previousVersion")
  private String previousVersion = null;

  @SerializedName("contributors")
  private String contributors = null;

  @SerializedName("nextVersion")
  private String nextVersion = null;

  @SerializedName("ownedBy")
  private String ownedBy = null;

  public ContentHistoryExpandable lastUpdated(String lastUpdated) {
    this.lastUpdated = lastUpdated;
    return this;
  }

   /**
   * Get lastUpdated
   * @return lastUpdated
  **/
  @Schema(description = "")
  public String getLastUpdated() {
    return lastUpdated;
  }

  public void setLastUpdated(String lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

  public ContentHistoryExpandable previousVersion(String previousVersion) {
    this.previousVersion = previousVersion;
    return this;
  }

   /**
   * Get previousVersion
   * @return previousVersion
  **/
  @Schema(description = "")
  public String getPreviousVersion() {
    return previousVersion;
  }

  public void setPreviousVersion(String previousVersion) {
    this.previousVersion = previousVersion;
  }

  public ContentHistoryExpandable contributors(String contributors) {
    this.contributors = contributors;
    return this;
  }

   /**
   * Get contributors
   * @return contributors
  **/
  @Schema(description = "")
  public String getContributors() {
    return contributors;
  }

  public void setContributors(String contributors) {
    this.contributors = contributors;
  }

  public ContentHistoryExpandable nextVersion(String nextVersion) {
    this.nextVersion = nextVersion;
    return this;
  }

   /**
   * Get nextVersion
   * @return nextVersion
  **/
  @Schema(description = "")
  public String getNextVersion() {
    return nextVersion;
  }

  public void setNextVersion(String nextVersion) {
    this.nextVersion = nextVersion;
  }

  public ContentHistoryExpandable ownedBy(String ownedBy) {
    this.ownedBy = ownedBy;
    return this;
  }

   /**
   * Get ownedBy
   * @return ownedBy
  **/
  @Schema(description = "")
  public String getOwnedBy() {
    return ownedBy;
  }

  public void setOwnedBy(String ownedBy) {
    this.ownedBy = ownedBy;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ContentHistoryExpandable contentHistoryExpandable = (ContentHistoryExpandable) o;
    return Objects.equals(this.lastUpdated, contentHistoryExpandable.lastUpdated) &&
        Objects.equals(this.previousVersion, contentHistoryExpandable.previousVersion) &&
        Objects.equals(this.contributors, contentHistoryExpandable.contributors) &&
        Objects.equals(this.nextVersion, contentHistoryExpandable.nextVersion) &&
        Objects.equals(this.ownedBy, contentHistoryExpandable.ownedBy);
  }

  @Override
  public int hashCode() {
    return Objects.hash(lastUpdated, previousVersion, contributors, nextVersion, ownedBy);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ContentHistoryExpandable {\n");
    
    sb.append("    lastUpdated: ").append(toIndentedString(lastUpdated)).append("\n");
    sb.append("    previousVersion: ").append(toIndentedString(previousVersion)).append("\n");
    sb.append("    contributors: ").append(toIndentedString(contributors)).append("\n");
    sb.append("    nextVersion: ").append(toIndentedString(nextVersion)).append("\n");
    sb.append("    ownedBy: ").append(toIndentedString(ownedBy)).append("\n");
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