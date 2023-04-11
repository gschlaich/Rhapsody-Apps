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
import java.util.Map;
/**
 * WebResourceDependenciesUris
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2023-03-29T20:08:21.572793104Z[GMT]")
public class WebResourceDependenciesUris {
  @SerializedName("all")
  private OneOfWebResourceDependenciesUrisAll all = null;

  @SerializedName("css")
  private OneOfWebResourceDependenciesUrisCss css = null;

  @SerializedName("js")
  private OneOfWebResourceDependenciesUrisJs js = null;

  @SerializedName("_expandable")
  private Map _expandable = null;

  public WebResourceDependenciesUris all(OneOfWebResourceDependenciesUrisAll all) {
    this.all = all;
    return this;
  }

   /**
   * Get all
   * @return all
  **/
  @Schema(description = "")
  public OneOfWebResourceDependenciesUrisAll getAll() {
    return all;
  }

  public void setAll(OneOfWebResourceDependenciesUrisAll all) {
    this.all = all;
  }

  public WebResourceDependenciesUris css(OneOfWebResourceDependenciesUrisCss css) {
    this.css = css;
    return this;
  }

   /**
   * Get css
   * @return css
  **/
  @Schema(description = "")
  public OneOfWebResourceDependenciesUrisCss getCss() {
    return css;
  }

  public void setCss(OneOfWebResourceDependenciesUrisCss css) {
    this.css = css;
  }

  public WebResourceDependenciesUris js(OneOfWebResourceDependenciesUrisJs js) {
    this.js = js;
    return this;
  }

   /**
   * Get js
   * @return js
  **/
  @Schema(description = "")
  public OneOfWebResourceDependenciesUrisJs getJs() {
    return js;
  }

  public void setJs(OneOfWebResourceDependenciesUrisJs js) {
    this.js = js;
  }

  public WebResourceDependenciesUris _expandable(Map _expandable) {
    this._expandable = _expandable;
    return this;
  }

   /**
   * Get _expandable
   * @return _expandable
  **/
  @Schema(description = "")
  public Map getExpandable() {
    return _expandable;
  }

  public void setExpandable(Map _expandable) {
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
    WebResourceDependenciesUris webResourceDependenciesUris = (WebResourceDependenciesUris) o;
    return Objects.equals(this.all, webResourceDependenciesUris.all) &&
        Objects.equals(this.css, webResourceDependenciesUris.css) &&
        Objects.equals(this.js, webResourceDependenciesUris.js) &&
        Objects.equals(this._expandable, webResourceDependenciesUris._expandable);
  }

  @Override
  public int hashCode() {
    return Objects.hash(all, css, js, _expandable);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class WebResourceDependenciesUris {\n");
    
    sb.append("    all: ").append(toIndentedString(all)).append("\n");
    sb.append("    css: ").append(toIndentedString(css)).append("\n");
    sb.append("    js: ").append(toIndentedString(js)).append("\n");
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