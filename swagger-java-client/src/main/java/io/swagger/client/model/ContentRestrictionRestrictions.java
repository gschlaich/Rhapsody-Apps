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
import io.swagger.client.model.ContentRestrictionRestrictionsExpandable;
import io.swagger.client.model.GroupArray;
import io.swagger.client.model.UserArray;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.IOException;
/**
 * ContentRestrictionRestrictions
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2023-03-29T20:08:21.572793104Z[GMT]")
public class ContentRestrictionRestrictions {
  @SerializedName("user")
  private UserArray user = null;

  @SerializedName("group")
  private GroupArray group = null;

  @SerializedName("_expandable")
  private ContentRestrictionRestrictionsExpandable _expandable = null;

  public ContentRestrictionRestrictions user(UserArray user) {
    this.user = user;
    return this;
  }

   /**
   * Get user
   * @return user
  **/
  @Schema(description = "")
  public UserArray getUser() {
    return user;
  }

  public void setUser(UserArray user) {
    this.user = user;
  }

  public ContentRestrictionRestrictions group(GroupArray group) {
    this.group = group;
    return this;
  }

   /**
   * Get group
   * @return group
  **/
  @Schema(description = "")
  public GroupArray getGroup() {
    return group;
  }

  public void setGroup(GroupArray group) {
    this.group = group;
  }

  public ContentRestrictionRestrictions _expandable(ContentRestrictionRestrictionsExpandable _expandable) {
    this._expandable = _expandable;
    return this;
  }

   /**
   * Get _expandable
   * @return _expandable
  **/
  @Schema(description = "")
  public ContentRestrictionRestrictionsExpandable getExpandable() {
    return _expandable;
  }

  public void setExpandable(ContentRestrictionRestrictionsExpandable _expandable) {
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
    ContentRestrictionRestrictions contentRestrictionRestrictions = (ContentRestrictionRestrictions) o;
    return Objects.equals(this.user, contentRestrictionRestrictions.user) &&
        Objects.equals(this.group, contentRestrictionRestrictions.group) &&
        Objects.equals(this._expandable, contentRestrictionRestrictions._expandable);
  }

  @Override
  public int hashCode() {
    return Objects.hash(user, group, _expandable);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ContentRestrictionRestrictions {\n");
    
    sb.append("    user: ").append(toIndentedString(user)).append("\n");
    sb.append("    group: ").append(toIndentedString(group)).append("\n");
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
