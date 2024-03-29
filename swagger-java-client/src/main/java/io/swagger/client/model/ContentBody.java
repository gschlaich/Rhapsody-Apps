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
import io.swagger.client.model.ContentBody;
import io.swagger.client.model.ContentBodyExpandable;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.IOException;
/**
 * ContentBody
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2023-03-29T20:08:21.572793104Z[GMT]")
public class ContentBody {
  @SerializedName("view")
  private ContentBody view = null;

  @SerializedName("export_view")
  private ContentBody exportView = null;

  @SerializedName("styled_view")
  private ContentBody styledView = null;

  @SerializedName("storage")
  private ContentBody storage = null;

  @SerializedName("wiki")
  private ContentBody wiki = null;

  @SerializedName("editor")
  private ContentBody editor = null;

  @SerializedName("editor2")
  private ContentBody editor2 = null;

  @SerializedName("anonymous_export_view")
  private ContentBody anonymousExportView = null;

  @SerializedName("atlas_doc_format")
  private ContentBody atlasDocFormat = null;

  @SerializedName("dynamic")
  private ContentBody dynamic = null;

  @SerializedName("raw")
  private ContentBody raw = null;

  @SerializedName("_expandable")
  private ContentBodyExpandable _expandable = null;

  public ContentBody view(ContentBody view) {
    this.view = view;
    return this;
  }

   /**
   * Get view
   * @return view
  **/
  @Schema(description = "")
  public ContentBody getView() {
    return view;
  }

  public void setView(ContentBody view) {
    this.view = view;
  }

  public ContentBody exportView(ContentBody exportView) {
    this.exportView = exportView;
    return this;
  }

   /**
   * Get exportView
   * @return exportView
  **/
  @Schema(description = "")
  public ContentBody getExportView() {
    return exportView;
  }

  public void setExportView(ContentBody exportView) {
    this.exportView = exportView;
  }

  public ContentBody styledView(ContentBody styledView) {
    this.styledView = styledView;
    return this;
  }

   /**
   * Get styledView
   * @return styledView
  **/
  @Schema(description = "")
  public ContentBody getStyledView() {
    return styledView;
  }

  public void setStyledView(ContentBody styledView) {
    this.styledView = styledView;
  }

  public ContentBody storage(ContentBody storage) {
    this.storage = storage;
    return this;
  }

   /**
   * Get storage
   * @return storage
  **/
  @Schema(description = "")
  public ContentBody getStorage() {
    return storage;
  }

  public void setStorage(ContentBody storage) {
    this.storage = storage;
  }

  public ContentBody wiki(ContentBody wiki) {
    this.wiki = wiki;
    return this;
  }

   /**
   * Get wiki
   * @return wiki
  **/
  @Schema(description = "")
  public ContentBody getWiki() {
    return wiki;
  }

  public void setWiki(ContentBody wiki) {
    this.wiki = wiki;
  }

  public ContentBody editor(ContentBody editor) {
    this.editor = editor;
    return this;
  }

   /**
   * Get editor
   * @return editor
  **/
  @Schema(description = "")
  public ContentBody getEditor() {
    return editor;
  }

  public void setEditor(ContentBody editor) {
    this.editor = editor;
  }

  public ContentBody editor2(ContentBody editor2) {
    this.editor2 = editor2;
    return this;
  }

   /**
   * Get editor2
   * @return editor2
  **/
  @Schema(description = "")
  public ContentBody getEditor2() {
    return editor2;
  }

  public void setEditor2(ContentBody editor2) {
    this.editor2 = editor2;
  }

  public ContentBody anonymousExportView(ContentBody anonymousExportView) {
    this.anonymousExportView = anonymousExportView;
    return this;
  }

   /**
   * Get anonymousExportView
   * @return anonymousExportView
  **/
  @Schema(description = "")
  public ContentBody getAnonymousExportView() {
    return anonymousExportView;
  }

  public void setAnonymousExportView(ContentBody anonymousExportView) {
    this.anonymousExportView = anonymousExportView;
  }

  public ContentBody atlasDocFormat(ContentBody atlasDocFormat) {
    this.atlasDocFormat = atlasDocFormat;
    return this;
  }

   /**
   * Get atlasDocFormat
   * @return atlasDocFormat
  **/
  @Schema(description = "")
  public ContentBody getAtlasDocFormat() {
    return atlasDocFormat;
  }

  public void setAtlasDocFormat(ContentBody atlasDocFormat) {
    this.atlasDocFormat = atlasDocFormat;
  }

  public ContentBody dynamic(ContentBody dynamic) {
    this.dynamic = dynamic;
    return this;
  }

   /**
   * Get dynamic
   * @return dynamic
  **/
  @Schema(description = "")
  public ContentBody getDynamic() {
    return dynamic;
  }

  public void setDynamic(ContentBody dynamic) {
    this.dynamic = dynamic;
  }

  public ContentBody raw(ContentBody raw) {
    this.raw = raw;
    return this;
  }

   /**
   * Get raw
   * @return raw
  **/
  @Schema(description = "")
  public ContentBody getRaw() {
    return raw;
  }

  public void setRaw(ContentBody raw) {
    this.raw = raw;
  }

  public ContentBody _expandable(ContentBodyExpandable _expandable) {
    this._expandable = _expandable;
    return this;
  }

   /**
   * Get _expandable
   * @return _expandable
  **/
  @Schema(description = "")
  public ContentBodyExpandable getExpandable() {
    return _expandable;
  }

  public void setExpandable(ContentBodyExpandable _expandable) {
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
    ContentBody contentBody = (ContentBody) o;
    return Objects.equals(this.view, contentBody.view) &&
        Objects.equals(this.exportView, contentBody.exportView) &&
        Objects.equals(this.styledView, contentBody.styledView) &&
        Objects.equals(this.storage, contentBody.storage) &&
        Objects.equals(this.wiki, contentBody.wiki) &&
        Objects.equals(this.editor, contentBody.editor) &&
        Objects.equals(this.editor2, contentBody.editor2) &&
        Objects.equals(this.anonymousExportView, contentBody.anonymousExportView) &&
        Objects.equals(this.atlasDocFormat, contentBody.atlasDocFormat) &&
        Objects.equals(this.dynamic, contentBody.dynamic) &&
        Objects.equals(this.raw, contentBody.raw) &&
        Objects.equals(this._expandable, contentBody._expandable);
  }

  @Override
  public int hashCode() {
    return Objects.hash(view, exportView, styledView, storage, wiki, editor, editor2, anonymousExportView, atlasDocFormat, dynamic, raw, _expandable);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ContentBody {\n");
    
    sb.append("    view: ").append(toIndentedString(view)).append("\n");
    sb.append("    exportView: ").append(toIndentedString(exportView)).append("\n");
    sb.append("    styledView: ").append(toIndentedString(styledView)).append("\n");
    sb.append("    storage: ").append(toIndentedString(storage)).append("\n");
    sb.append("    wiki: ").append(toIndentedString(wiki)).append("\n");
    sb.append("    editor: ").append(toIndentedString(editor)).append("\n");
    sb.append("    editor2: ").append(toIndentedString(editor2)).append("\n");
    sb.append("    anonymousExportView: ").append(toIndentedString(anonymousExportView)).append("\n");
    sb.append("    atlasDocFormat: ").append(toIndentedString(atlasDocFormat)).append("\n");
    sb.append("    dynamic: ").append(toIndentedString(dynamic)).append("\n");
    sb.append("    raw: ").append(toIndentedString(raw)).append("\n");
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
