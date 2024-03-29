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
 * ContentCreateSpaceDescriptionExpandable
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2023-03-29T20:08:21.572793104Z[GMT]")
public class ContentCreateSpaceDescriptionExpandable {
  @SerializedName("view")
  private String view = null;

  @SerializedName("plain")
  private String plain = null;

  public ContentCreateSpaceDescriptionExpandable view(String view) {
    this.view = view;
    return this;
  }

   /**
   * Get view
   * @return view
  **/
  @Schema(description = "")
  public String getView() {
    return view;
  }

  public void setView(String view) {
    this.view = view;
  }

  public ContentCreateSpaceDescriptionExpandable plain(String plain) {
    this.plain = plain;
    return this;
  }

   /**
   * Get plain
   * @return plain
  **/
  @Schema(description = "")
  public String getPlain() {
    return plain;
  }

  public void setPlain(String plain) {
    this.plain = plain;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ContentCreateSpaceDescriptionExpandable contentCreateSpaceDescriptionExpandable = (ContentCreateSpaceDescriptionExpandable) o;
    return Objects.equals(this.view, contentCreateSpaceDescriptionExpandable.view) &&
        Objects.equals(this.plain, contentCreateSpaceDescriptionExpandable.plain);
  }

  @Override
  public int hashCode() {
    return Objects.hash(view, plain);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ContentCreateSpaceDescriptionExpandable {\n");
    
    sb.append("    view: ").append(toIndentedString(view)).append("\n");
    sb.append("    plain: ").append(toIndentedString(plain)).append("\n");
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
