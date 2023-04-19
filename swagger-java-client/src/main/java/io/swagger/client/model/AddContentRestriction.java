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
import io.swagger.client.model.AddContentRestrictionRestrictions;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.IOException;
/**
 * AddContentRestriction
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2023-03-29T20:08:21.572793104Z[GMT]")
public class AddContentRestriction {
  /**
   * The restriction operation applied to content.
   */
  @JsonAdapter(OperationEnum.Adapter.class)
  public enum OperationEnum {
    READ("read"),
    UPDATE("update");

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
  private AddContentRestrictionRestrictions restrictions = null;

  public AddContentRestriction operation(OperationEnum operation) {
    this.operation = operation;
    return this;
  }

   /**
   * The restriction operation applied to content.
   * @return operation
  **/
  @Schema(required = true, description = "The restriction operation applied to content.")
  public OperationEnum getOperation() {
    return operation;
  }

  public void setOperation(OperationEnum operation) {
    this.operation = operation;
  }

  public AddContentRestriction restrictions(AddContentRestrictionRestrictions restrictions) {
    this.restrictions = restrictions;
    return this;
  }

   /**
   * Get restrictions
   * @return restrictions
  **/
  @Schema(required = true, description = "")
  public AddContentRestrictionRestrictions getRestrictions() {
    return restrictions;
  }

  public void setRestrictions(AddContentRestrictionRestrictions restrictions) {
    this.restrictions = restrictions;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AddContentRestriction addContentRestriction = (AddContentRestriction) o;
    return Objects.equals(this.operation, addContentRestriction.operation) &&
        Objects.equals(this.restrictions, addContentRestriction.restrictions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(operation, restrictions);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AddContentRestriction {\n");
    
    sb.append("    operation: ").append(toIndentedString(operation)).append("\n");
    sb.append("    restrictions: ").append(toIndentedString(restrictions)).append("\n");
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
