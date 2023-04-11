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
import io.swagger.client.model.AsyncContentBodyExpandable;
import io.swagger.client.model.AsyncContentBodyMediaToken;
import io.swagger.client.model.EmbeddedContent;
import io.swagger.client.model.GenericLinks;
import io.swagger.client.model.WebResourceDependencies;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/**
 * AsyncContentBody
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2023-03-29T20:08:21.572793104Z[GMT]")
public class AsyncContentBody {
  @SerializedName("value")
  private String value = null;

  /**
   * Gets or Sets representation
   */
  @JsonAdapter(RepresentationEnum.Adapter.class)
  public enum RepresentationEnum {
    VIEW("view"),
    EXPORT_VIEW("export_view"),
    STYLED_VIEW("styled_view"),
    STORAGE("storage"),
    EDITOR("editor"),
    EDITOR2("editor2"),
    ANONYMOUS_EXPORT_VIEW("anonymous_export_view"),
    WIKI("wiki"),
    ATLAS_DOC_FORMAT("atlas_doc_format");

    private String value;

    RepresentationEnum(String value) {
      this.value = value;
    }
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }
    public static RepresentationEnum fromValue(String input) {
      for (RepresentationEnum b : RepresentationEnum.values()) {
        if (b.value.equals(input)) {
          return b;
        }
      }
      return null;
    }
    public static class Adapter extends TypeAdapter<RepresentationEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final RepresentationEnum enumeration) throws IOException {
        jsonWriter.value(String.valueOf(enumeration.getValue()));
      }

      @Override
      public RepresentationEnum read(final JsonReader jsonReader) throws IOException {
        Object value = jsonReader.nextString();
        return RepresentationEnum.fromValue((String)(value));
      }
    }
  }  @SerializedName("representation")
  private RepresentationEnum representation = null;

  @SerializedName("renderTaskId")
  private String renderTaskId = null;

  @SerializedName("error")
  private String error = null;

  /**
   * Rerunning is reserved for when the job is working, but there is a previous run&#x27;s value in the cache. You may choose to continue polling, or use the cached value.
   */
  @JsonAdapter(StatusEnum.Adapter.class)
  public enum StatusEnum {
    WORKING("WORKING"),
    QUEUED("QUEUED"),
    FAILED("FAILED"),
    COMPLETED("COMPLETED"),
    RERUNNING("RERUNNING");

    private String value;

    StatusEnum(String value) {
      this.value = value;
    }
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }
    public static StatusEnum fromValue(String input) {
      for (StatusEnum b : StatusEnum.values()) {
        if (b.value.equals(input)) {
          return b;
        }
      }
      return null;
    }
    public static class Adapter extends TypeAdapter<StatusEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final StatusEnum enumeration) throws IOException {
        jsonWriter.value(String.valueOf(enumeration.getValue()));
      }

      @Override
      public StatusEnum read(final JsonReader jsonReader) throws IOException {
        Object value = jsonReader.nextString();
        return StatusEnum.fromValue((String)(value));
      }
    }
  }  @SerializedName("status")
  private StatusEnum status = null;

  @SerializedName("embeddedContent")
  private List<EmbeddedContent> embeddedContent = null;

  @SerializedName("webresource")
  private WebResourceDependencies webresource = null;

  @SerializedName("mediaToken")
  private AsyncContentBodyMediaToken mediaToken = null;

  @SerializedName("_expandable")
  private AsyncContentBodyExpandable _expandable = null;

  @SerializedName("_links")
  private GenericLinks _links = null;

  public AsyncContentBody value(String value) {
    this.value = value;
    return this;
  }

   /**
   * Get value
   * @return value
  **/
  @Schema(description = "")
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public AsyncContentBody representation(RepresentationEnum representation) {
    this.representation = representation;
    return this;
  }

   /**
   * Get representation
   * @return representation
  **/
  @Schema(description = "")
  public RepresentationEnum getRepresentation() {
    return representation;
  }

  public void setRepresentation(RepresentationEnum representation) {
    this.representation = representation;
  }

  public AsyncContentBody renderTaskId(String renderTaskId) {
    this.renderTaskId = renderTaskId;
    return this;
  }

   /**
   * Get renderTaskId
   * @return renderTaskId
  **/
  @Schema(description = "")
  public String getRenderTaskId() {
    return renderTaskId;
  }

  public void setRenderTaskId(String renderTaskId) {
    this.renderTaskId = renderTaskId;
  }

  public AsyncContentBody error(String error) {
    this.error = error;
    return this;
  }

   /**
   * Get error
   * @return error
  **/
  @Schema(description = "")
  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public AsyncContentBody status(StatusEnum status) {
    this.status = status;
    return this;
  }

   /**
   * Rerunning is reserved for when the job is working, but there is a previous run&#x27;s value in the cache. You may choose to continue polling, or use the cached value.
   * @return status
  **/
  @Schema(description = "Rerunning is reserved for when the job is working, but there is a previous run's value in the cache. You may choose to continue polling, or use the cached value.")
  public StatusEnum getStatus() {
    return status;
  }

  public void setStatus(StatusEnum status) {
    this.status = status;
  }

  public AsyncContentBody embeddedContent(List<EmbeddedContent> embeddedContent) {
    this.embeddedContent = embeddedContent;
    return this;
  }

  public AsyncContentBody addEmbeddedContentItem(EmbeddedContent embeddedContentItem) {
    if (this.embeddedContent == null) {
      this.embeddedContent = new ArrayList<EmbeddedContent>();
    }
    this.embeddedContent.add(embeddedContentItem);
    return this;
  }

   /**
   * Get embeddedContent
   * @return embeddedContent
  **/
  @Schema(description = "")
  public List<EmbeddedContent> getEmbeddedContent() {
    return embeddedContent;
  }

  public void setEmbeddedContent(List<EmbeddedContent> embeddedContent) {
    this.embeddedContent = embeddedContent;
  }

  public AsyncContentBody webresource(WebResourceDependencies webresource) {
    this.webresource = webresource;
    return this;
  }

   /**
   * Get webresource
   * @return webresource
  **/
  @Schema(description = "")
  public WebResourceDependencies getWebresource() {
    return webresource;
  }

  public void setWebresource(WebResourceDependencies webresource) {
    this.webresource = webresource;
  }

  public AsyncContentBody mediaToken(AsyncContentBodyMediaToken mediaToken) {
    this.mediaToken = mediaToken;
    return this;
  }

   /**
   * Get mediaToken
   * @return mediaToken
  **/
  @Schema(description = "")
  public AsyncContentBodyMediaToken getMediaToken() {
    return mediaToken;
  }

  public void setMediaToken(AsyncContentBodyMediaToken mediaToken) {
    this.mediaToken = mediaToken;
  }

  public AsyncContentBody _expandable(AsyncContentBodyExpandable _expandable) {
    this._expandable = _expandable;
    return this;
  }

   /**
   * Get _expandable
   * @return _expandable
  **/
  @Schema(description = "")
  public AsyncContentBodyExpandable getExpandable() {
    return _expandable;
  }

  public void setExpandable(AsyncContentBodyExpandable _expandable) {
    this._expandable = _expandable;
  }

  public AsyncContentBody _links(GenericLinks _links) {
    this._links = _links;
    return this;
  }

   /**
   * Get _links
   * @return _links
  **/
  @Schema(description = "")
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
    AsyncContentBody asyncContentBody = (AsyncContentBody) o;
    return Objects.equals(this.value, asyncContentBody.value) &&
        Objects.equals(this.representation, asyncContentBody.representation) &&
        Objects.equals(this.renderTaskId, asyncContentBody.renderTaskId) &&
        Objects.equals(this.error, asyncContentBody.error) &&
        Objects.equals(this.status, asyncContentBody.status) &&
        Objects.equals(this.embeddedContent, asyncContentBody.embeddedContent) &&
        Objects.equals(this.webresource, asyncContentBody.webresource) &&
        Objects.equals(this.mediaToken, asyncContentBody.mediaToken) &&
        Objects.equals(this._expandable, asyncContentBody._expandable) &&
        Objects.equals(this._links, asyncContentBody._links);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value, representation, renderTaskId, error, status, embeddedContent, webresource, mediaToken, _expandable, _links);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AsyncContentBody {\n");
    
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
    sb.append("    representation: ").append(toIndentedString(representation)).append("\n");
    sb.append("    renderTaskId: ").append(toIndentedString(renderTaskId)).append("\n");
    sb.append("    error: ").append(toIndentedString(error)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    embeddedContent: ").append(toIndentedString(embeddedContent)).append("\n");
    sb.append("    webresource: ").append(toIndentedString(webresource)).append("\n");
    sb.append("    mediaToken: ").append(toIndentedString(mediaToken)).append("\n");
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
