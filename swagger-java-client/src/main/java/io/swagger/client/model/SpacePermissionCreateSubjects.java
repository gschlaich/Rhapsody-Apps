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
import io.swagger.client.model.SpacePermissionCreateSubjectsGroup;
import io.swagger.client.model.SpacePermissionCreateSubjectsUser;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.IOException;
/**
 * The users and/or groups that the permission applies to.
 */
@Schema(description = "The users and/or groups that the permission applies to.")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2023-03-29T20:08:21.572793104Z[GMT]")
public class SpacePermissionCreateSubjects {
  @SerializedName("user")
  private SpacePermissionCreateSubjectsUser user = null;

  @SerializedName("group")
  private SpacePermissionCreateSubjectsGroup group = null;

  public SpacePermissionCreateSubjects user(SpacePermissionCreateSubjectsUser user) {
    this.user = user;
    return this;
  }

   /**
   * Get user
   * @return user
  **/
  @Schema(description = "")
  public SpacePermissionCreateSubjectsUser getUser() {
    return user;
  }

  public void setUser(SpacePermissionCreateSubjectsUser user) {
    this.user = user;
  }

  public SpacePermissionCreateSubjects group(SpacePermissionCreateSubjectsGroup group) {
    this.group = group;
    return this;
  }

   /**
   * Get group
   * @return group
  **/
  @Schema(description = "")
  public SpacePermissionCreateSubjectsGroup getGroup() {
    return group;
  }

  public void setGroup(SpacePermissionCreateSubjectsGroup group) {
    this.group = group;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SpacePermissionCreateSubjects spacePermissionCreateSubjects = (SpacePermissionCreateSubjects) o;
    return Objects.equals(this.user, spacePermissionCreateSubjects.user) &&
        Objects.equals(this.group, spacePermissionCreateSubjects.group);
  }

  @Override
  public int hashCode() {
    return Objects.hash(user, group);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SpacePermissionCreateSubjects {\n");
    
    sb.append("    user: ").append(toIndentedString(user)).append("\n");
    sb.append("    group: ").append(toIndentedString(group)).append("\n");
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