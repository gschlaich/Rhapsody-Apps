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
 * SpaceExpandable
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2023-03-29T20:08:21.572793104Z[GMT]")
public class SpaceExpandable {
  @SerializedName("settings")
  private String settings = null;

  @SerializedName("metadata")
  private String metadata = null;

  @SerializedName("operations")
  private String operations = null;

  @SerializedName("lookAndFeel")
  private String lookAndFeel = null;

  @SerializedName("permissions")
  private String permissions = null;

  @SerializedName("icon")
  private String icon = null;

  @SerializedName("description")
  private String description = null;

  @SerializedName("theme")
  private String theme = null;

  @SerializedName("history")
  private String history = null;

  @SerializedName("homepage")
  private String homepage = null;

  @SerializedName("identifiers")
  private String identifiers = null;

  public SpaceExpandable settings(String settings) {
    this.settings = settings;
    return this;
  }

   /**
   * Get settings
   * @return settings
  **/
  @Schema(description = "")
  public String getSettings() {
    return settings;
  }

  public void setSettings(String settings) {
    this.settings = settings;
  }

  public SpaceExpandable metadata(String metadata) {
    this.metadata = metadata;
    return this;
  }

   /**
   * Get metadata
   * @return metadata
  **/
  @Schema(description = "")
  public String getMetadata() {
    return metadata;
  }

  public void setMetadata(String metadata) {
    this.metadata = metadata;
  }

  public SpaceExpandable operations(String operations) {
    this.operations = operations;
    return this;
  }

   /**
   * Get operations
   * @return operations
  **/
  @Schema(description = "")
  public String getOperations() {
    return operations;
  }

  public void setOperations(String operations) {
    this.operations = operations;
  }

  public SpaceExpandable lookAndFeel(String lookAndFeel) {
    this.lookAndFeel = lookAndFeel;
    return this;
  }

   /**
   * Get lookAndFeel
   * @return lookAndFeel
  **/
  @Schema(description = "")
  public String getLookAndFeel() {
    return lookAndFeel;
  }

  public void setLookAndFeel(String lookAndFeel) {
    this.lookAndFeel = lookAndFeel;
  }

  public SpaceExpandable permissions(String permissions) {
    this.permissions = permissions;
    return this;
  }

   /**
   * Get permissions
   * @return permissions
  **/
  @Schema(description = "")
  public String getPermissions() {
    return permissions;
  }

  public void setPermissions(String permissions) {
    this.permissions = permissions;
  }

  public SpaceExpandable icon(String icon) {
    this.icon = icon;
    return this;
  }

   /**
   * Get icon
   * @return icon
  **/
  @Schema(description = "")
  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public SpaceExpandable description(String description) {
    this.description = description;
    return this;
  }

   /**
   * Get description
   * @return description
  **/
  @Schema(description = "")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public SpaceExpandable theme(String theme) {
    this.theme = theme;
    return this;
  }

   /**
   * Get theme
   * @return theme
  **/
  @Schema(description = "")
  public String getTheme() {
    return theme;
  }

  public void setTheme(String theme) {
    this.theme = theme;
  }

  public SpaceExpandable history(String history) {
    this.history = history;
    return this;
  }

   /**
   * Get history
   * @return history
  **/
  @Schema(description = "")
  public String getHistory() {
    return history;
  }

  public void setHistory(String history) {
    this.history = history;
  }

  public SpaceExpandable homepage(String homepage) {
    this.homepage = homepage;
    return this;
  }

   /**
   * Get homepage
   * @return homepage
  **/
  @Schema(description = "")
  public String getHomepage() {
    return homepage;
  }

  public void setHomepage(String homepage) {
    this.homepage = homepage;
  }

  public SpaceExpandable identifiers(String identifiers) {
    this.identifiers = identifiers;
    return this;
  }

   /**
   * Get identifiers
   * @return identifiers
  **/
  @Schema(description = "")
  public String getIdentifiers() {
    return identifiers;
  }

  public void setIdentifiers(String identifiers) {
    this.identifiers = identifiers;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SpaceExpandable spaceExpandable = (SpaceExpandable) o;
    return Objects.equals(this.settings, spaceExpandable.settings) &&
        Objects.equals(this.metadata, spaceExpandable.metadata) &&
        Objects.equals(this.operations, spaceExpandable.operations) &&
        Objects.equals(this.lookAndFeel, spaceExpandable.lookAndFeel) &&
        Objects.equals(this.permissions, spaceExpandable.permissions) &&
        Objects.equals(this.icon, spaceExpandable.icon) &&
        Objects.equals(this.description, spaceExpandable.description) &&
        Objects.equals(this.theme, spaceExpandable.theme) &&
        Objects.equals(this.history, spaceExpandable.history) &&
        Objects.equals(this.homepage, spaceExpandable.homepage) &&
        Objects.equals(this.identifiers, spaceExpandable.identifiers);
  }

  @Override
  public int hashCode() {
    return Objects.hash(settings, metadata, operations, lookAndFeel, permissions, icon, description, theme, history, homepage, identifiers);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SpaceExpandable {\n");
    
    sb.append("    settings: ").append(toIndentedString(settings)).append("\n");
    sb.append("    metadata: ").append(toIndentedString(metadata)).append("\n");
    sb.append("    operations: ").append(toIndentedString(operations)).append("\n");
    sb.append("    lookAndFeel: ").append(toIndentedString(lookAndFeel)).append("\n");
    sb.append("    permissions: ").append(toIndentedString(permissions)).append("\n");
    sb.append("    icon: ").append(toIndentedString(icon)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    theme: ").append(toIndentedString(theme)).append("\n");
    sb.append("    history: ").append(toIndentedString(history)).append("\n");
    sb.append("    homepage: ").append(toIndentedString(homepage)).append("\n");
    sb.append("    identifiers: ").append(toIndentedString(identifiers)).append("\n");
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
