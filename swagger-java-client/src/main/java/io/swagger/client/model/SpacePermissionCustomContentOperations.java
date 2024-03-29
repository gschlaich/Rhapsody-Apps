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
 * SpacePermissionCustomContentOperations
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2023-03-29T20:08:21.572793104Z[GMT]")
public class SpacePermissionCustomContentOperations {
  /**
   * The operation type
   */
  @JsonAdapter(KeyEnum.Adapter.class)
  public enum KeyEnum {
    READ("read"),
    CREATE("create"),
    DELETE("delete");

    private String value;

    KeyEnum(String value) {
      this.value = value;
    }
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }
    public static KeyEnum fromValue(String input) {
      for (KeyEnum b : KeyEnum.values()) {
        if (b.value.equals(input)) {
          return b;
        }
      }
      return null;
    }
    public static class Adapter extends TypeAdapter<KeyEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final KeyEnum enumeration) throws IOException {
        jsonWriter.value(String.valueOf(enumeration.getValue()));
      }

      @Override
      public KeyEnum read(final JsonReader jsonReader) throws IOException {
        Object value = jsonReader.nextString();
        return KeyEnum.fromValue((String)(value));
      }
    }
  }  @SerializedName("key")
  private KeyEnum key = null;

  @SerializedName("target")
  private String target = null;

  @SerializedName("access")
  private Boolean access = null;

  public SpacePermissionCustomContentOperations key(KeyEnum key) {
    this.key = key;
    return this;
  }

   /**
   * The operation type
   * @return key
  **/
  @Schema(required = true, description = "The operation type")
  public KeyEnum getKey() {
    return key;
  }

  public void setKey(KeyEnum key) {
    this.key = key;
  }

  public SpacePermissionCustomContentOperations target(String target) {
    this.target = target;
    return this;
  }

   /**
   * The custom content type
   * @return target
  **/
  @Schema(required = true, description = "The custom content type")
  public String getTarget() {
    return target;
  }

  public void setTarget(String target) {
    this.target = target;
  }

  public SpacePermissionCustomContentOperations access(Boolean access) {
    this.access = access;
    return this;
  }

   /**
   * Grant or restrict access
   * @return access
  **/
  @Schema(required = true, description = "Grant or restrict access")
  public Boolean isAccess() {
    return access;
  }

  public void setAccess(Boolean access) {
    this.access = access;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SpacePermissionCustomContentOperations spacePermissionCustomContentOperations = (SpacePermissionCustomContentOperations) o;
    return Objects.equals(this.key, spacePermissionCustomContentOperations.key) &&
        Objects.equals(this.target, spacePermissionCustomContentOperations.target) &&
        Objects.equals(this.access, spacePermissionCustomContentOperations.access);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, target, access);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SpacePermissionCustomContentOperations {\n");
    
    sb.append("    key: ").append(toIndentedString(key)).append("\n");
    sb.append("    target: ").append(toIndentedString(target)).append("\n");
    sb.append("    access: ").append(toIndentedString(access)).append("\n");
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
