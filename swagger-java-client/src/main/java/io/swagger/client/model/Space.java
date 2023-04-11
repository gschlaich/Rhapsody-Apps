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
import io.swagger.client.model.Content;
import io.swagger.client.model.ContentCreateSpaceHistory;
import io.swagger.client.model.GenericLinks;
import io.swagger.client.model.Icon;
import io.swagger.client.model.LookAndFeel;
import io.swagger.client.model.OperationCheckResult;
import io.swagger.client.model.SpaceDescription;
import io.swagger.client.model.SpaceExpandable;
import io.swagger.client.model.SpaceMetadata;
import io.swagger.client.model.SpacePermission;
import io.swagger.client.model.SpaceSettings;
import io.swagger.client.model.Theme;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/**
 * Space
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2023-03-29T20:08:21.572793104Z[GMT]")
public class Space implements OneOfRelationSource, OneOfRelationTarget {
  @SerializedName("id")
  private Long id = null;

  @SerializedName("key")
  private String key = null;

  @SerializedName("name")
  private String name = null;

  @SerializedName("icon")
  private Icon icon = null;

  @SerializedName("description")
  private SpaceDescription description = null;

  @SerializedName("homepage")
  private Content homepage = null;

  @SerializedName("type")
  private String type = null;

  @SerializedName("metadata")
  private SpaceMetadata metadata = null;

  @SerializedName("operations")
  private List<OperationCheckResult> operations = null;

  @SerializedName("permissions")
  private List<SpacePermission> permissions = null;

  @SerializedName("status")
  private String status = null;

  @SerializedName("settings")
  private SpaceSettings settings = null;

  @SerializedName("theme")
  private Theme theme = null;

  @SerializedName("lookAndFeel")
  private LookAndFeel lookAndFeel = null;

  @SerializedName("history")
  private ContentCreateSpaceHistory history = null;

  @SerializedName("_expandable")
  private SpaceExpandable _expandable = null;

  @SerializedName("_links")
  private GenericLinks _links = null;

  public Space id(Long id) {
    this.id = id;
    return this;
  }

   /**
   * Get id
   * @return id
  **/
  @Schema(description = "")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Space key(String key) {
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

  public Space name(String name) {
    this.name = name;
    return this;
  }

   /**
   * Get name
   * @return name
  **/
  @Schema(required = true, description = "")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Space icon(Icon icon) {
    this.icon = icon;
    return this;
  }

   /**
   * Get icon
   * @return icon
  **/
  @Schema(description = "")
  public Icon getIcon() {
    return icon;
  }

  public void setIcon(Icon icon) {
    this.icon = icon;
  }

  public Space description(SpaceDescription description) {
    this.description = description;
    return this;
  }

   /**
   * Get description
   * @return description
  **/
  @Schema(description = "")
  public SpaceDescription getDescription() {
    return description;
  }

  public void setDescription(SpaceDescription description) {
    this.description = description;
  }

  public Space homepage(Content homepage) {
    this.homepage = homepage;
    return this;
  }

   /**
   * Get homepage
   * @return homepage
  **/
  @Schema(description = "")
  public Content getHomepage() {
    return homepage;
  }

  public void setHomepage(Content homepage) {
    this.homepage = homepage;
  }

  public Space type(String type) {
    this.type = type;
    return this;
  }

   /**
   * Get type
   * @return type
  **/
  @Schema(required = true, description = "")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Space metadata(SpaceMetadata metadata) {
    this.metadata = metadata;
    return this;
  }

   /**
   * Get metadata
   * @return metadata
  **/
  @Schema(description = "")
  public SpaceMetadata getMetadata() {
    return metadata;
  }

  public void setMetadata(SpaceMetadata metadata) {
    this.metadata = metadata;
  }

  public Space operations(List<OperationCheckResult> operations) {
    this.operations = operations;
    return this;
  }

  public Space addOperationsItem(OperationCheckResult operationsItem) {
    if (this.operations == null) {
      this.operations = new ArrayList<OperationCheckResult>();
    }
    this.operations.add(operationsItem);
    return this;
  }

   /**
   * Get operations
   * @return operations
  **/
  @Schema(description = "")
  public List<OperationCheckResult> getOperations() {
    return operations;
  }

  public void setOperations(List<OperationCheckResult> operations) {
    this.operations = operations;
  }

  public Space permissions(List<SpacePermission> permissions) {
    this.permissions = permissions;
    return this;
  }

  public Space addPermissionsItem(SpacePermission permissionsItem) {
    if (this.permissions == null) {
      this.permissions = new ArrayList<SpacePermission>();
    }
    this.permissions.add(permissionsItem);
    return this;
  }

   /**
   * Get permissions
   * @return permissions
  **/
  @Schema(description = "")
  public List<SpacePermission> getPermissions() {
    return permissions;
  }

  public void setPermissions(List<SpacePermission> permissions) {
    this.permissions = permissions;
  }

  public Space status(String status) {
    this.status = status;
    return this;
  }

   /**
   * Get status
   * @return status
  **/
  @Schema(required = true, description = "")
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Space settings(SpaceSettings settings) {
    this.settings = settings;
    return this;
  }

   /**
   * Get settings
   * @return settings
  **/
  @Schema(description = "")
  public SpaceSettings getSettings() {
    return settings;
  }

  public void setSettings(SpaceSettings settings) {
    this.settings = settings;
  }

  public Space theme(Theme theme) {
    this.theme = theme;
    return this;
  }

   /**
   * Get theme
   * @return theme
  **/
  @Schema(description = "")
  public Theme getTheme() {
    return theme;
  }

  public void setTheme(Theme theme) {
    this.theme = theme;
  }

  public Space lookAndFeel(LookAndFeel lookAndFeel) {
    this.lookAndFeel = lookAndFeel;
    return this;
  }

   /**
   * Get lookAndFeel
   * @return lookAndFeel
  **/
  @Schema(description = "")
  public LookAndFeel getLookAndFeel() {
    return lookAndFeel;
  }

  public void setLookAndFeel(LookAndFeel lookAndFeel) {
    this.lookAndFeel = lookAndFeel;
  }

  public Space history(ContentCreateSpaceHistory history) {
    this.history = history;
    return this;
  }

   /**
   * Get history
   * @return history
  **/
  @Schema(description = "")
  public ContentCreateSpaceHistory getHistory() {
    return history;
  }

  public void setHistory(ContentCreateSpaceHistory history) {
    this.history = history;
  }

  public Space _expandable(SpaceExpandable _expandable) {
    this._expandable = _expandable;
    return this;
  }

   /**
   * Get _expandable
   * @return _expandable
  **/
  @Schema(required = true, description = "")
  public SpaceExpandable getExpandable() {
    return _expandable;
  }

  public void setExpandable(SpaceExpandable _expandable) {
    this._expandable = _expandable;
  }

  public Space _links(GenericLinks _links) {
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


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Space space = (Space) o;
    return Objects.equals(this.id, space.id) &&
        Objects.equals(this.key, space.key) &&
        Objects.equals(this.name, space.name) &&
        Objects.equals(this.icon, space.icon) &&
        Objects.equals(this.description, space.description) &&
        Objects.equals(this.homepage, space.homepage) &&
        Objects.equals(this.type, space.type) &&
        Objects.equals(this.metadata, space.metadata) &&
        Objects.equals(this.operations, space.operations) &&
        Objects.equals(this.permissions, space.permissions) &&
        Objects.equals(this.status, space.status) &&
        Objects.equals(this.settings, space.settings) &&
        Objects.equals(this.theme, space.theme) &&
        Objects.equals(this.lookAndFeel, space.lookAndFeel) &&
        Objects.equals(this.history, space.history) &&
        Objects.equals(this._expandable, space._expandable) &&
        Objects.equals(this._links, space._links);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, key, name, icon, description, homepage, type, metadata, operations, permissions, status, settings, theme, lookAndFeel, history, _expandable, _links);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Space {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    key: ").append(toIndentedString(key)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    icon: ").append(toIndentedString(icon)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    homepage: ").append(toIndentedString(homepage)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    metadata: ").append(toIndentedString(metadata)).append("\n");
    sb.append("    operations: ").append(toIndentedString(operations)).append("\n");
    sb.append("    permissions: ").append(toIndentedString(permissions)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    settings: ").append(toIndentedString(settings)).append("\n");
    sb.append("    theme: ").append(toIndentedString(theme)).append("\n");
    sb.append("    lookAndFeel: ").append(toIndentedString(lookAndFeel)).append("\n");
    sb.append("    history: ").append(toIndentedString(history)).append("\n");
    sb.append("    _expandable: ").append(toIndentedString(_expandable)).append("\n");
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
