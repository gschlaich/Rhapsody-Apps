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
 * A user that the restriction will be applied to. Either the &#x60;username&#x60; or the &#x60;userKey&#x60; must be specified to identify the user.
 */
@Schema(description = "A user that the restriction will be applied to. Either the `username` or the `userKey` must be specified to identify the user.")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2023-03-29T20:08:21.572793104Z[GMT]")
public class AddContentRestrictionRestrictionsUser {
  /**
   * Set to &#x27;known&#x27;.
   */
  @JsonAdapter(TypeEnum.Adapter.class)
  public enum TypeEnum {
    KNOWN("known"),
    UNKNOWN("unknown"),
    ANONYMOUS("anonymous"),
    USER("user");

    private String value;

    TypeEnum(String value) {
      this.value = value;
    }
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }
    public static TypeEnum fromValue(String input) {
      for (TypeEnum b : TypeEnum.values()) {
        if (b.value.equals(input)) {
          return b;
        }
      }
      return null;
    }
    public static class Adapter extends TypeAdapter<TypeEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final TypeEnum enumeration) throws IOException {
        jsonWriter.value(String.valueOf(enumeration.getValue()));
      }

      @Override
      public TypeEnum read(final JsonReader jsonReader) throws IOException {
        Object value = jsonReader.nextString();
        return TypeEnum.fromValue((String)(value));
      }
    }
  }  @SerializedName("type")
  private TypeEnum type = null;

  @SerializedName("username")
  private String username = null;

  @SerializedName("userKey")
  private String userKey = null;

  @SerializedName("accountId")
  private String accountId = null;

  public AddContentRestrictionRestrictionsUser type(TypeEnum type) {
    this.type = type;
    return this;
  }

   /**
   * Set to &#x27;known&#x27;.
   * @return type
  **/
  @Schema(required = true, description = "Set to 'known'.")
  public TypeEnum getType() {
    return type;
  }

  public void setType(TypeEnum type) {
    this.type = type;
  }

  public AddContentRestrictionRestrictionsUser username(String username) {
    this.username = username;
    return this;
  }

   /**
   * Get username
   * @return username
  **/
  @Schema(description = "")
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public AddContentRestrictionRestrictionsUser userKey(String userKey) {
    this.userKey = userKey;
    return this;
  }

   /**
   * Get userKey
   * @return userKey
  **/
  @Schema(description = "")
  public String getUserKey() {
    return userKey;
  }

  public void setUserKey(String userKey) {
    this.userKey = userKey;
  }

  public AddContentRestrictionRestrictionsUser accountId(String accountId) {
    this.accountId = accountId;
    return this;
  }

   /**
   * Get accountId
   * @return accountId
  **/
  @Schema(required = true, description = "")
  public String getAccountId() {
    return accountId;
  }

  public void setAccountId(String accountId) {
    this.accountId = accountId;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AddContentRestrictionRestrictionsUser addContentRestrictionRestrictionsUser = (AddContentRestrictionRestrictionsUser) o;
    return Objects.equals(this.type, addContentRestrictionRestrictionsUser.type) &&
        Objects.equals(this.username, addContentRestrictionRestrictionsUser.username) &&
        Objects.equals(this.userKey, addContentRestrictionRestrictionsUser.userKey) &&
        Objects.equals(this.accountId, addContentRestrictionRestrictionsUser.accountId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, username, userKey, accountId);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AddContentRestrictionRestrictionsUser {\n");
    
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    username: ").append(toIndentedString(username)).append("\n");
    sb.append("    userKey: ").append(toIndentedString(userKey)).append("\n");
    sb.append("    accountId: ").append(toIndentedString(accountId)).append("\n");
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
