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
import io.swagger.client.model.ContentBodyCreate;
import io.swagger.client.model.ContentBodyCreateStorage;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.IOException;
/**
 * The updated body of the content. Does not apply to attachments. If you are not sure how to generate these formats, you can create a page in the Confluence application, retrieve the content using [Get content](#api-content-get), and expand the desired content format, e.g. &#x60;expand&#x3D;body.storage&#x60;.
 */
@Schema(description = "The updated body of the content. Does not apply to attachments. If you are not sure how to generate these formats, you can create a page in the Confluence application, retrieve the content using [Get content](#api-content-get), and expand the desired content format, e.g. `expand=body.storage`.")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2023-03-29T20:08:21.572793104Z[GMT]")
public class ContentUpdateBody {
  @SerializedName("view")
  private ContentBodyCreate view = null;

  @SerializedName("export_view")
  private ContentBodyCreate exportView = null;

  @SerializedName("styled_view")
  private ContentBodyCreate styledView = null;

  @SerializedName("storage")
  private ContentBodyCreateStorage storage = null;

  @SerializedName("editor")
  private ContentBodyCreate editor = null;

  @SerializedName("editor2")
  private ContentBodyCreate editor2 = null;

  @SerializedName("wiki")
  private ContentBodyCreate wiki = null;

  @SerializedName("atlas_doc_format")
  private ContentBodyCreate atlasDocFormat = null;

  @SerializedName("anonymous_export_view")
  private ContentBodyCreate anonymousExportView = null;

  public ContentUpdateBody view(ContentBodyCreate view) {
    this.view = view;
    return this;
  }

   /**
   * Get view
   * @return view
  **/
  @Schema(description = "")
  public ContentBodyCreate getView() {
    return view;
  }

  public void setView(ContentBodyCreate view) {
    this.view = view;
  }

  public ContentUpdateBody exportView(ContentBodyCreate exportView) {
    this.exportView = exportView;
    return this;
  }

   /**
   * Get exportView
   * @return exportView
  **/
  @Schema(description = "")
  public ContentBodyCreate getExportView() {
    return exportView;
  }

  public void setExportView(ContentBodyCreate exportView) {
    this.exportView = exportView;
  }

  public ContentUpdateBody styledView(ContentBodyCreate styledView) {
    this.styledView = styledView;
    return this;
  }

   /**
   * Get styledView
   * @return styledView
  **/
  @Schema(description = "")
  public ContentBodyCreate getStyledView() {
    return styledView;
  }

  public void setStyledView(ContentBodyCreate styledView) {
    this.styledView = styledView;
  }

  public ContentUpdateBody storage(ContentBodyCreateStorage storage) {
    this.storage = storage;
    return this;
  }

   /**
   * Get storage
   * @return storage
  **/
  @Schema(description = "")
  public ContentBodyCreateStorage getStorage() {
    return storage;
  }

  public void setStorage(ContentBodyCreateStorage storage) {
    this.storage = storage;
  }

  public ContentUpdateBody editor(ContentBodyCreate editor) {
    this.editor = editor;
    return this;
  }

   /**
   * Get editor
   * @return editor
  **/
  @Schema(description = "")
  public ContentBodyCreate getEditor() {
    return editor;
  }

  public void setEditor(ContentBodyCreate editor) {
    this.editor = editor;
  }

  public ContentUpdateBody editor2(ContentBodyCreate editor2) {
    this.editor2 = editor2;
    return this;
  }

   /**
   * Get editor2
   * @return editor2
  **/
  @Schema(description = "")
  public ContentBodyCreate getEditor2() {
    return editor2;
  }

  public void setEditor2(ContentBodyCreate editor2) {
    this.editor2 = editor2;
  }

  public ContentUpdateBody wiki(ContentBodyCreate wiki) {
    this.wiki = wiki;
    return this;
  }

   /**
   * Get wiki
   * @return wiki
  **/
  @Schema(description = "")
  public ContentBodyCreate getWiki() {
    return wiki;
  }

  public void setWiki(ContentBodyCreate wiki) {
    this.wiki = wiki;
  }

  public ContentUpdateBody atlasDocFormat(ContentBodyCreate atlasDocFormat) {
    this.atlasDocFormat = atlasDocFormat;
    return this;
  }

   /**
   * Get atlasDocFormat
   * @return atlasDocFormat
  **/
  @Schema(description = "")
  public ContentBodyCreate getAtlasDocFormat() {
    return atlasDocFormat;
  }

  public void setAtlasDocFormat(ContentBodyCreate atlasDocFormat) {
    this.atlasDocFormat = atlasDocFormat;
  }

  public ContentUpdateBody anonymousExportView(ContentBodyCreate anonymousExportView) {
    this.anonymousExportView = anonymousExportView;
    return this;
  }

   /**
   * Get anonymousExportView
   * @return anonymousExportView
  **/
  @Schema(description = "")
  public ContentBodyCreate getAnonymousExportView() {
    return anonymousExportView;
  }

  public void setAnonymousExportView(ContentBodyCreate anonymousExportView) {
    this.anonymousExportView = anonymousExportView;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ContentUpdateBody contentUpdateBody = (ContentUpdateBody) o;
    return Objects.equals(this.view, contentUpdateBody.view) &&
        Objects.equals(this.exportView, contentUpdateBody.exportView) &&
        Objects.equals(this.styledView, contentUpdateBody.styledView) &&
        Objects.equals(this.storage, contentUpdateBody.storage) &&
        Objects.equals(this.editor, contentUpdateBody.editor) &&
        Objects.equals(this.editor2, contentUpdateBody.editor2) &&
        Objects.equals(this.wiki, contentUpdateBody.wiki) &&
        Objects.equals(this.atlasDocFormat, contentUpdateBody.atlasDocFormat) &&
        Objects.equals(this.anonymousExportView, contentUpdateBody.anonymousExportView);
  }

  @Override
  public int hashCode() {
    return Objects.hash(view, exportView, styledView, storage, editor, editor2, wiki, atlasDocFormat, anonymousExportView);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ContentUpdateBody {\n");
    
    sb.append("    view: ").append(toIndentedString(view)).append("\n");
    sb.append("    exportView: ").append(toIndentedString(exportView)).append("\n");
    sb.append("    styledView: ").append(toIndentedString(styledView)).append("\n");
    sb.append("    storage: ").append(toIndentedString(storage)).append("\n");
    sb.append("    editor: ").append(toIndentedString(editor)).append("\n");
    sb.append("    editor2: ").append(toIndentedString(editor2)).append("\n");
    sb.append("    wiki: ").append(toIndentedString(wiki)).append("\n");
    sb.append("    atlasDocFormat: ").append(toIndentedString(atlasDocFormat)).append("\n");
    sb.append("    anonymousExportView: ").append(toIndentedString(anonymousExportView)).append("\n");
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