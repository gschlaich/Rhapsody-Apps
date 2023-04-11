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
import io.swagger.client.model.SpacePermissionCreate;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * This is the request object used when creating a new space.
 */
@Schema(description = "This is the request object used when creating a new space.")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2023-03-29T20:08:21.572793104Z[GMT]")
public class SpaceCreate extends HashMap<String, Object> {
  @SerializedName("key")
  private String key = null;

  @SerializedName("name")
  private String name = null;

  @SerializedName("description")
  private SpaceDescriptionCreate description = null;

  @SerializedName("permissions")
  private List<SpacePermissionCreate> permissions = null;

  public SpaceCreate key(String key) {
    this.key = key;
    return this;
  }

   /**
   * The key for the new space. Format: See [Space keys](https://confluence.atlassian.com/x/lqNMMQ).
   * @return key
  **/
  @Schema(required = true, description = "The key for the new space. Format: See [Space keys](https://confluence.atlassian.com/x/lqNMMQ).")
  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public SpaceCreate name(String name) {
    this.name = name;
    return this;
  }

   /**
   * The name of the new space.
   * @return name
  **/
  @Schema(required = true, description = "The name of the new space.")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public SpaceCreate description(SpaceDescriptionCreate description) {
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

  public SpaceCreate permissions(List<SpacePermissionCreate> permissions) {
    this.permissions = permissions;
    return this;
  }

  public SpaceCreate addPermissionsItem(SpacePermissionCreate permissionsItem) {
    if (this.permissions == null) {
      this.permissions = new ArrayList<SpacePermissionCreate>();
    }
    this.permissions.add(permissionsItem);
    return this;
  }

   /**
   * The permissions for the new space. If no permissions are provided, the [Confluence default space permissions](https://confluence.atlassian.com/x/UAgzKw#CreateaSpace-Spacepermissions) are applied. Note that if permissions are provided, the space is created with only the provided set of permissions, not including the default space permissions. Space permissions can be modified after creation using the space permissions endpoints, and a private space can be created using the create private space endpoint.
   * @return permissions
  **/
  @Schema(description = "The permissions for the new space. If no permissions are provided, the [Confluence default space permissions](https://confluence.atlassian.com/x/UAgzKw#CreateaSpace-Spacepermissions) are applied. Note that if permissions are provided, the space is created with only the provided set of permissions, not including the default space permissions. Space permissions can be modified after creation using the space permissions endpoints, and a private space can be created using the create private space endpoint.")
  public List<SpacePermissionCreate> getPermissions() {
    return permissions;
  }

  public void setPermissions(List<SpacePermissionCreate> permissions) {
    this.permissions = permissions;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SpaceCreate spaceCreate = (SpaceCreate) o;
    return Objects.equals(this.key, spaceCreate.key) &&
        Objects.equals(this.name, spaceCreate.name) &&
        Objects.equals(this.description, spaceCreate.description) &&
        Objects.equals(this.permissions, spaceCreate.permissions) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, name, description, permissions, super.hashCode());
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SpaceCreate {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    key: ").append(toIndentedString(key)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    permissions: ").append(toIndentedString(permissions)).append("\n");
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
