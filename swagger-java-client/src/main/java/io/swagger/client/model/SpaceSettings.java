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
import io.swagger.client.model.SpaceSettingsEditor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.IOException;
/**
 * SpaceSettings
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2023-03-29T20:08:21.572793104Z[GMT]")
public class SpaceSettings {
  @SerializedName("routeOverrideEnabled")
  private Boolean routeOverrideEnabled = null;

  @SerializedName("editor")
  private SpaceSettingsEditor editor = null;

  @SerializedName("spaceKey")
  private String spaceKey = null;

  @SerializedName("_links")
  private GenericLinks _links = null;

  public SpaceSettings routeOverrideEnabled(Boolean routeOverrideEnabled) {
    this.routeOverrideEnabled = routeOverrideEnabled;
    return this;
  }

   /**
   * Defines whether an override for the space home should be used. This is used in conjunction with a space theme provided by an app. For example, if this property is set to true, a theme can display a page other than the space homepage when users visit the root URL for a space. This property allows apps to provide content-only theming without overriding the space home.
   * @return routeOverrideEnabled
  **/
  @Schema(required = true, description = "Defines whether an override for the space home should be used. This is used in conjunction with a space theme provided by an app. For example, if this property is set to true, a theme can display a page other than the space homepage when users visit the root URL for a space. This property allows apps to provide content-only theming without overriding the space home.")
  public Boolean isRouteOverrideEnabled() {
    return routeOverrideEnabled;
  }

  public void setRouteOverrideEnabled(Boolean routeOverrideEnabled) {
    this.routeOverrideEnabled = routeOverrideEnabled;
  }

  public SpaceSettings editor(SpaceSettingsEditor editor) {
    this.editor = editor;
    return this;
  }

   /**
   * Get editor
   * @return editor
  **/
  @Schema(description = "")
  public SpaceSettingsEditor getEditor() {
    return editor;
  }

  public void setEditor(SpaceSettingsEditor editor) {
    this.editor = editor;
  }

  public SpaceSettings spaceKey(String spaceKey) {
    this.spaceKey = spaceKey;
    return this;
  }

   /**
   * Get spaceKey
   * @return spaceKey
  **/
  @Schema(description = "")
  public String getSpaceKey() {
    return spaceKey;
  }

  public void setSpaceKey(String spaceKey) {
    this.spaceKey = spaceKey;
  }

  public SpaceSettings _links(GenericLinks _links) {
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
    SpaceSettings spaceSettings = (SpaceSettings) o;
    return Objects.equals(this.routeOverrideEnabled, spaceSettings.routeOverrideEnabled) &&
        Objects.equals(this.editor, spaceSettings.editor) &&
        Objects.equals(this.spaceKey, spaceSettings.spaceKey) &&
        Objects.equals(this._links, spaceSettings._links);
  }

  @Override
  public int hashCode() {
    return Objects.hash(routeOverrideEnabled, editor, spaceKey, _links);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SpaceSettings {\n");
    
    sb.append("    routeOverrideEnabled: ").append(toIndentedString(routeOverrideEnabled)).append("\n");
    sb.append("    editor: ").append(toIndentedString(editor)).append("\n");
    sb.append("    spaceKey: ").append(toIndentedString(spaceKey)).append("\n");
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
