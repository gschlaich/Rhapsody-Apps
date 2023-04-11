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
 * ContainerLookAndFeel
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2023-03-29T20:08:21.572793104Z[GMT]")
public class ContainerLookAndFeel {
  @SerializedName("background")
  private String background = null;

  @SerializedName("backgroundAttachment")
  private String backgroundAttachment = null;

  @SerializedName("backgroundBlendMode")
  private String backgroundBlendMode = null;

  @SerializedName("backgroundClip")
  private String backgroundClip = null;

  @SerializedName("backgroundColor")
  private String backgroundColor = null;

  @SerializedName("backgroundImage")
  private String backgroundImage = null;

  @SerializedName("backgroundOrigin")
  private String backgroundOrigin = null;

  @SerializedName("backgroundPosition")
  private String backgroundPosition = null;

  @SerializedName("backgroundRepeat")
  private String backgroundRepeat = null;

  @SerializedName("backgroundSize")
  private String backgroundSize = null;

  @SerializedName("padding")
  private String padding = null;

  @SerializedName("borderRadius")
  private String borderRadius = null;

  public ContainerLookAndFeel background(String background) {
    this.background = background;
    return this;
  }

   /**
   * Get background
   * @return background
  **/
  @Schema(required = true, description = "")
  public String getBackground() {
    return background;
  }

  public void setBackground(String background) {
    this.background = background;
  }

  public ContainerLookAndFeel backgroundAttachment(String backgroundAttachment) {
    this.backgroundAttachment = backgroundAttachment;
    return this;
  }

   /**
   * Get backgroundAttachment
   * @return backgroundAttachment
  **/
  @Schema(description = "")
  public String getBackgroundAttachment() {
    return backgroundAttachment;
  }

  public void setBackgroundAttachment(String backgroundAttachment) {
    this.backgroundAttachment = backgroundAttachment;
  }

  public ContainerLookAndFeel backgroundBlendMode(String backgroundBlendMode) {
    this.backgroundBlendMode = backgroundBlendMode;
    return this;
  }

   /**
   * Get backgroundBlendMode
   * @return backgroundBlendMode
  **/
  @Schema(description = "")
  public String getBackgroundBlendMode() {
    return backgroundBlendMode;
  }

  public void setBackgroundBlendMode(String backgroundBlendMode) {
    this.backgroundBlendMode = backgroundBlendMode;
  }

  public ContainerLookAndFeel backgroundClip(String backgroundClip) {
    this.backgroundClip = backgroundClip;
    return this;
  }

   /**
   * Get backgroundClip
   * @return backgroundClip
  **/
  @Schema(description = "")
  public String getBackgroundClip() {
    return backgroundClip;
  }

  public void setBackgroundClip(String backgroundClip) {
    this.backgroundClip = backgroundClip;
  }

  public ContainerLookAndFeel backgroundColor(String backgroundColor) {
    this.backgroundColor = backgroundColor;
    return this;
  }

   /**
   * Get backgroundColor
   * @return backgroundColor
  **/
  @Schema(required = true, description = "")
  public String getBackgroundColor() {
    return backgroundColor;
  }

  public void setBackgroundColor(String backgroundColor) {
    this.backgroundColor = backgroundColor;
  }

  public ContainerLookAndFeel backgroundImage(String backgroundImage) {
    this.backgroundImage = backgroundImage;
    return this;
  }

   /**
   * Get backgroundImage
   * @return backgroundImage
  **/
  @Schema(required = true, description = "")
  public String getBackgroundImage() {
    return backgroundImage;
  }

  public void setBackgroundImage(String backgroundImage) {
    this.backgroundImage = backgroundImage;
  }

  public ContainerLookAndFeel backgroundOrigin(String backgroundOrigin) {
    this.backgroundOrigin = backgroundOrigin;
    return this;
  }

   /**
   * Get backgroundOrigin
   * @return backgroundOrigin
  **/
  @Schema(description = "")
  public String getBackgroundOrigin() {
    return backgroundOrigin;
  }

  public void setBackgroundOrigin(String backgroundOrigin) {
    this.backgroundOrigin = backgroundOrigin;
  }

  public ContainerLookAndFeel backgroundPosition(String backgroundPosition) {
    this.backgroundPosition = backgroundPosition;
    return this;
  }

   /**
   * Get backgroundPosition
   * @return backgroundPosition
  **/
  @Schema(description = "")
  public String getBackgroundPosition() {
    return backgroundPosition;
  }

  public void setBackgroundPosition(String backgroundPosition) {
    this.backgroundPosition = backgroundPosition;
  }

  public ContainerLookAndFeel backgroundRepeat(String backgroundRepeat) {
    this.backgroundRepeat = backgroundRepeat;
    return this;
  }

   /**
   * Get backgroundRepeat
   * @return backgroundRepeat
  **/
  @Schema(description = "")
  public String getBackgroundRepeat() {
    return backgroundRepeat;
  }

  public void setBackgroundRepeat(String backgroundRepeat) {
    this.backgroundRepeat = backgroundRepeat;
  }

  public ContainerLookAndFeel backgroundSize(String backgroundSize) {
    this.backgroundSize = backgroundSize;
    return this;
  }

   /**
   * Get backgroundSize
   * @return backgroundSize
  **/
  @Schema(required = true, description = "")
  public String getBackgroundSize() {
    return backgroundSize;
  }

  public void setBackgroundSize(String backgroundSize) {
    this.backgroundSize = backgroundSize;
  }

  public ContainerLookAndFeel padding(String padding) {
    this.padding = padding;
    return this;
  }

   /**
   * Get padding
   * @return padding
  **/
  @Schema(required = true, description = "")
  public String getPadding() {
    return padding;
  }

  public void setPadding(String padding) {
    this.padding = padding;
  }

  public ContainerLookAndFeel borderRadius(String borderRadius) {
    this.borderRadius = borderRadius;
    return this;
  }

   /**
   * Get borderRadius
   * @return borderRadius
  **/
  @Schema(required = true, description = "")
  public String getBorderRadius() {
    return borderRadius;
  }

  public void setBorderRadius(String borderRadius) {
    this.borderRadius = borderRadius;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ContainerLookAndFeel containerLookAndFeel = (ContainerLookAndFeel) o;
    return Objects.equals(this.background, containerLookAndFeel.background) &&
        Objects.equals(this.backgroundAttachment, containerLookAndFeel.backgroundAttachment) &&
        Objects.equals(this.backgroundBlendMode, containerLookAndFeel.backgroundBlendMode) &&
        Objects.equals(this.backgroundClip, containerLookAndFeel.backgroundClip) &&
        Objects.equals(this.backgroundColor, containerLookAndFeel.backgroundColor) &&
        Objects.equals(this.backgroundImage, containerLookAndFeel.backgroundImage) &&
        Objects.equals(this.backgroundOrigin, containerLookAndFeel.backgroundOrigin) &&
        Objects.equals(this.backgroundPosition, containerLookAndFeel.backgroundPosition) &&
        Objects.equals(this.backgroundRepeat, containerLookAndFeel.backgroundRepeat) &&
        Objects.equals(this.backgroundSize, containerLookAndFeel.backgroundSize) &&
        Objects.equals(this.padding, containerLookAndFeel.padding) &&
        Objects.equals(this.borderRadius, containerLookAndFeel.borderRadius);
  }

  @Override
  public int hashCode() {
    return Objects.hash(background, backgroundAttachment, backgroundBlendMode, backgroundClip, backgroundColor, backgroundImage, backgroundOrigin, backgroundPosition, backgroundRepeat, backgroundSize, padding, borderRadius);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ContainerLookAndFeel {\n");
    
    sb.append("    background: ").append(toIndentedString(background)).append("\n");
    sb.append("    backgroundAttachment: ").append(toIndentedString(backgroundAttachment)).append("\n");
    sb.append("    backgroundBlendMode: ").append(toIndentedString(backgroundBlendMode)).append("\n");
    sb.append("    backgroundClip: ").append(toIndentedString(backgroundClip)).append("\n");
    sb.append("    backgroundColor: ").append(toIndentedString(backgroundColor)).append("\n");
    sb.append("    backgroundImage: ").append(toIndentedString(backgroundImage)).append("\n");
    sb.append("    backgroundOrigin: ").append(toIndentedString(backgroundOrigin)).append("\n");
    sb.append("    backgroundPosition: ").append(toIndentedString(backgroundPosition)).append("\n");
    sb.append("    backgroundRepeat: ").append(toIndentedString(backgroundRepeat)).append("\n");
    sb.append("    backgroundSize: ").append(toIndentedString(backgroundSize)).append("\n");
    sb.append("    padding: ").append(toIndentedString(padding)).append("\n");
    sb.append("    borderRadius: ").append(toIndentedString(borderRadius)).append("\n");
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