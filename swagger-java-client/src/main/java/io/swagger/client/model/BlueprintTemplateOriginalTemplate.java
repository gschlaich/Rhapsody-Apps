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
 * BlueprintTemplateOriginalTemplate
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2023-03-29T20:08:21.572793104Z[GMT]")
public class BlueprintTemplateOriginalTemplate {
  @SerializedName("pluginKey")
  private String pluginKey = null;

  @SerializedName("moduleKey")
  private String moduleKey = null;

  public BlueprintTemplateOriginalTemplate pluginKey(String pluginKey) {
    this.pluginKey = pluginKey;
    return this;
  }

   /**
   * Get pluginKey
   * @return pluginKey
  **/
  @Schema(required = true, description = "")
  public String getPluginKey() {
    return pluginKey;
  }

  public void setPluginKey(String pluginKey) {
    this.pluginKey = pluginKey;
  }

  public BlueprintTemplateOriginalTemplate moduleKey(String moduleKey) {
    this.moduleKey = moduleKey;
    return this;
  }

   /**
   * Get moduleKey
   * @return moduleKey
  **/
  @Schema(required = true, description = "")
  public String getModuleKey() {
    return moduleKey;
  }

  public void setModuleKey(String moduleKey) {
    this.moduleKey = moduleKey;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BlueprintTemplateOriginalTemplate blueprintTemplateOriginalTemplate = (BlueprintTemplateOriginalTemplate) o;
    return Objects.equals(this.pluginKey, blueprintTemplateOriginalTemplate.pluginKey) &&
        Objects.equals(this.moduleKey, blueprintTemplateOriginalTemplate.moduleKey);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pluginKey, moduleKey);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BlueprintTemplateOriginalTemplate {\n");
    
    sb.append("    pluginKey: ").append(toIndentedString(pluginKey)).append("\n");
    sb.append("    moduleKey: ").append(toIndentedString(moduleKey)).append("\n");
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
