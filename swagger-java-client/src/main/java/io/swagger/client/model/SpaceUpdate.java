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
import io.swagger.client.model.SpaceDescriptionCreate;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
/**
 * The properties of a space that can be updated.
 */
@Schema(description = "The properties of a space that can be updated.")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2023-03-29T20:08:21.572793104Z[GMT]")
public class SpaceUpdate extends HashMap<String, Object> {
  @SerializedName("name")
  private String name = null;

  @SerializedName("description")
  private SpaceDescriptionCreate description = null;

  @SerializedName("homepage")
  private Object homepage = null;

  @SerializedName("type")
  private String type = null;

  @SerializedName("status")
  private String status = null;

  public SpaceUpdate name(String name) {
    this.name = name;
    return this;
  }

   /**
   * The updated name of the space.
   * @return name
  **/
  @Schema(description = "The updated name of the space.")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public SpaceUpdate description(SpaceDescriptionCreate description) {
    this.description = description;
    return this;
  }

   /**
   * Get description
   * @return description
  **/
  @Schema(description = "")
  public SpaceDescriptionCreate getDescription() {
    return description;
  }

  public void setDescription(SpaceDescriptionCreate description) {
    this.description = description;
  }

  public SpaceUpdate homepage(Object homepage) {
    this.homepage = homepage;
    return this;
  }

   /**
   * The updated homepage for this space
   * @return homepage
  **/
  @Schema(description = "The updated homepage for this space")
  public Object getHomepage() {
    return homepage;
  }

  public void setHomepage(Object homepage) {
    this.homepage = homepage;
  }

  public SpaceUpdate type(String type) {
    this.type = type;
    return this;
  }

   /**
   * The updated type for this space.
   * @return type
  **/
  @Schema(description = "The updated type for this space.")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public SpaceUpdate status(String status) {
    this.status = status;
    return this;
  }

   /**
   * The updated status for this space.
   * @return status
  **/
  @Schema(description = "The updated status for this space.")
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SpaceUpdate spaceUpdate = (SpaceUpdate) o;
    return Objects.equals(this.name, spaceUpdate.name) &&
        Objects.equals(this.description, spaceUpdate.description) &&
        Objects.equals(this.homepage, spaceUpdate.homepage) &&
        Objects.equals(this.type, spaceUpdate.type) &&
        Objects.equals(this.status, spaceUpdate.status) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, description, homepage, type, status, super.hashCode());
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SpaceUpdate {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    homepage: ").append(toIndentedString(homepage)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
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