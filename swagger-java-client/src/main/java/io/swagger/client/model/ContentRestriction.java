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
import io.swagger.client.model.ContentRestrictionExpandable;
import io.swagger.client.model.ContentRestrictionRestrictions;
import io.swagger.client.model.GenericLinks;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.IOException;
/**
 * ContentRestriction
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2023-03-29T20:08:21.572793104Z[GMT]")
public class ContentRestriction {
  /**
   * Gets or Sets operation
   */
  @JsonAdapter(OperationEnum.Adapter.class)
  public enum OperationEnum {
    ADMINISTER("administer"),
    COPY("copy"),
    CREATE("create"),
    DELETE("delete"),
    EXPORT("export"),
    MOVE("move"),
    PURGE("purge"),
    PURGE_VERSION("purge_version"),
    READ("read"),
    RESTORE("restore"),
    UPDATE("update"),
    USE("use");

    private String value;

    OperationEnum(String value) {
      this.value = value;
    }
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }
    public static OperationEnum fromValue(String input) {
      for (OperationEnum b : OperationEnum.values()) {
        if (b.value.equals(input)) {
          return b;
        }
      }
      return null;
    }
    public static class Adapter extends TypeAdapter<OperationEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final OperationEnum enumeration) throws IOException {
        jsonWriter.value(String.valueOf(enumeration.getValue()));
      }

      @Override
      public OperationEnum read(final JsonReader jsonReader) throws IOException {
        Object value = jsonReader.nextString();
        return OperationEnum.fromValue((String)(value));
      }
    }
  }  @SerializedName("operation")
  private OperationEnum operation = null;

  @SerializedName("restrictions")
  private ContentRestrictionRestrictions restrictions = null;

  @SerializedName("content")
  private Content content = null;

  @SerializedName("_expandable")
  private ContentRestrictionExpandable _expandable = null;

  @SerializedName("_links")
  private GenericLinks _links = null;

  public ContentRestriction operation(OperationEnum operation) {
    this.operation = operation;
    return this;
  }

   /**
   * Get operation
   * @return operation
  **/
  @Schema(required = true, description = "")
  public OperationEnum getOperation() {
    return operation;
  }

  public void setOperation(OperationEnum operation) {
    this.operation = operation;
  }

  public ContentRestriction restrictions(ContentRestrictionRestrictions restrictions) {
    this.restrictions = restrictions;
    return this;
  }

   /**
   * Get restrictions
   * @return restrictions
  **/
  @Schema(description = "")
  public ContentRestrictionRestrictions getRestrictions() {
    return restrictions;
  }

  public void setRestrictions(ContentRestrictionRestrictions restrictions) {
    this.restrictions = restrictions;
  }

  public ContentRestriction content(Content content) {
    this.content = content;
    return this;
  }

   /**
   * Get content
   * @return content
  **/
  @Schema(description = "")
  public Content getContent() {
    return content;
  }

  public void setContent(Content content) {
    this.content = content;
  }

  public ContentRestriction _expandable(ContentRestrictionExpandable _expandable) {
    this._expandable = _expandable;
    return this;
  }

   /**
   * Get _expandable
   * @return _expandable
  **/
  @Schema(required = true, description = "")
  public ContentRestrictionExpandable getExpandable() {
    return _expandable;
  }

  public void setExpandable(ContentRestrictionExpandable _expandable) {
    this._expandable = _expandable;
  }

  public ContentRestriction _links(GenericLinks _links) {
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
    ContentRestriction contentRestriction = (ContentRestriction) o;
    return Objects.equals(this.operation, contentRestriction.operation) &&
        Objects.equals(this.restrictions, contentRestriction.restrictions) &&
        Objects.equals(this.content, contentRestriction.content) &&
        Objects.equals(this._expandable, contentRestriction._expandable) &&
        Objects.equals(this._links, contentRestriction._links);
  }

  @Override
  public int hashCode() {
    return Objects.hash(operation, restrictions, content, _expandable, _links);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ContentRestriction {\n");
    
    sb.append("    operation: ").append(toIndentedString(operation)).append("\n");
    sb.append("    restrictions: ").append(toIndentedString(restrictions)).append("\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
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
