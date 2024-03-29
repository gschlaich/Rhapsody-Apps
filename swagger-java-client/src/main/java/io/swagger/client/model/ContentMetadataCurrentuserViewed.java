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
import org.threeten.bp.OffsetDateTime;
/**
 * ContentMetadataCurrentuserViewed
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2023-03-29T20:08:21.572793104Z[GMT]")
public class ContentMetadataCurrentuserViewed {
  @SerializedName("lastSeen")
  private OffsetDateTime lastSeen = null;

  @SerializedName("friendlyLastSeen")
  private String friendlyLastSeen = null;

  public ContentMetadataCurrentuserViewed lastSeen(OffsetDateTime lastSeen) {
    this.lastSeen = lastSeen;
    return this;
  }

   /**
   * Get lastSeen
   * @return lastSeen
  **/
  @Schema(description = "")
  public OffsetDateTime getLastSeen() {
    return lastSeen;
  }

  public void setLastSeen(OffsetDateTime lastSeen) {
    this.lastSeen = lastSeen;
  }

  public ContentMetadataCurrentuserViewed friendlyLastSeen(String friendlyLastSeen) {
    this.friendlyLastSeen = friendlyLastSeen;
    return this;
  }

   /**
   * Get friendlyLastSeen
   * @return friendlyLastSeen
  **/
  @Schema(description = "")
  public String getFriendlyLastSeen() {
    return friendlyLastSeen;
  }

  public void setFriendlyLastSeen(String friendlyLastSeen) {
    this.friendlyLastSeen = friendlyLastSeen;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ContentMetadataCurrentuserViewed contentMetadataCurrentuserViewed = (ContentMetadataCurrentuserViewed) o;
    return Objects.equals(this.lastSeen, contentMetadataCurrentuserViewed.lastSeen) &&
        Objects.equals(this.friendlyLastSeen, contentMetadataCurrentuserViewed.friendlyLastSeen);
  }

  @Override
  public int hashCode() {
    return Objects.hash(lastSeen, friendlyLastSeen);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ContentMetadataCurrentuserViewed {\n");
    
    sb.append("    lastSeen: ").append(toIndentedString(lastSeen)).append("\n");
    sb.append("    friendlyLastSeen: ").append(toIndentedString(friendlyLastSeen)).append("\n");
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
