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
import io.swagger.client.model.Breadcrumb;
import io.swagger.client.model.ContainerSummary;
import io.swagger.client.model.Content;
import io.swagger.client.model.Space;
import io.swagger.client.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.threeten.bp.OffsetDateTime;
/**
 * SearchResult
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2023-03-29T20:08:21.572793104Z[GMT]")
public class SearchResult {
  @SerializedName("content")
  private Content content = null;

  @SerializedName("user")
  private User user = null;

  @SerializedName("space")
  private Space space = null;

  @SerializedName("title")
  private String title = null;

  @SerializedName("excerpt")
  private String excerpt = null;

  @SerializedName("url")
  private String url = null;

  @SerializedName("resultParentContainer")
  private ContainerSummary resultParentContainer = null;

  @SerializedName("resultGlobalContainer")
  private ContainerSummary resultGlobalContainer = null;

  @SerializedName("breadcrumbs")
  private List<Breadcrumb> breadcrumbs = new ArrayList<Breadcrumb>();

  @SerializedName("entityType")
  private String entityType = null;

  @SerializedName("iconCssClass")
  private String iconCssClass = null;

  @SerializedName("lastModified")
  private OffsetDateTime lastModified = null;

  @SerializedName("friendlyLastModified")
  private String friendlyLastModified = null;

  @SerializedName("score")
  private BigDecimal score = null;

  public SearchResult content(Content content) {
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

  public SearchResult user(User user) {
    this.user = user;
    return this;
  }

   /**
   * Get user
   * @return user
  **/
  @Schema(description = "")
  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public SearchResult space(Space space) {
    this.space = space;
    return this;
  }

   /**
   * Get space
   * @return space
  **/
  @Schema(description = "")
  public Space getSpace() {
    return space;
  }

  public void setSpace(Space space) {
    this.space = space;
  }

  public SearchResult title(String title) {
    this.title = title;
    return this;
  }

   /**
   * Get title
   * @return title
  **/
  @Schema(required = true, description = "")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public SearchResult excerpt(String excerpt) {
    this.excerpt = excerpt;
    return this;
  }

   /**
   * Get excerpt
   * @return excerpt
  **/
  @Schema(required = true, description = "")
  public String getExcerpt() {
    return excerpt;
  }

  public void setExcerpt(String excerpt) {
    this.excerpt = excerpt;
  }

  public SearchResult url(String url) {
    this.url = url;
    return this;
  }

   /**
   * Get url
   * @return url
  **/
  @Schema(required = true, description = "")
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public SearchResult resultParentContainer(ContainerSummary resultParentContainer) {
    this.resultParentContainer = resultParentContainer;
    return this;
  }

   /**
   * Get resultParentContainer
   * @return resultParentContainer
  **/
  @Schema(description = "")
  public ContainerSummary getResultParentContainer() {
    return resultParentContainer;
  }

  public void setResultParentContainer(ContainerSummary resultParentContainer) {
    this.resultParentContainer = resultParentContainer;
  }

  public SearchResult resultGlobalContainer(ContainerSummary resultGlobalContainer) {
    this.resultGlobalContainer = resultGlobalContainer;
    return this;
  }

   /**
   * Get resultGlobalContainer
   * @return resultGlobalContainer
  **/
  @Schema(description = "")
  public ContainerSummary getResultGlobalContainer() {
    return resultGlobalContainer;
  }

  public void setResultGlobalContainer(ContainerSummary resultGlobalContainer) {
    this.resultGlobalContainer = resultGlobalContainer;
  }

  public SearchResult breadcrumbs(List<Breadcrumb> breadcrumbs) {
    this.breadcrumbs = breadcrumbs;
    return this;
  }

  public SearchResult addBreadcrumbsItem(Breadcrumb breadcrumbsItem) {
    this.breadcrumbs.add(breadcrumbsItem);
    return this;
  }

   /**
   * Get breadcrumbs
   * @return breadcrumbs
  **/
  @Schema(required = true, description = "")
  public List<Breadcrumb> getBreadcrumbs() {
    return breadcrumbs;
  }

  public void setBreadcrumbs(List<Breadcrumb> breadcrumbs) {
    this.breadcrumbs = breadcrumbs;
  }

  public SearchResult entityType(String entityType) {
    this.entityType = entityType;
    return this;
  }

   /**
   * Get entityType
   * @return entityType
  **/
  @Schema(required = true, description = "")
  public String getEntityType() {
    return entityType;
  }

  public void setEntityType(String entityType) {
    this.entityType = entityType;
  }

  public SearchResult iconCssClass(String iconCssClass) {
    this.iconCssClass = iconCssClass;
    return this;
  }

   /**
   * Get iconCssClass
   * @return iconCssClass
  **/
  @Schema(required = true, description = "")
  public String getIconCssClass() {
    return iconCssClass;
  }

  public void setIconCssClass(String iconCssClass) {
    this.iconCssClass = iconCssClass;
  }

  public SearchResult lastModified(OffsetDateTime lastModified) {
    this.lastModified = lastModified;
    return this;
  }

   /**
   * Get lastModified
   * @return lastModified
  **/
  @Schema(required = true, description = "")
  public OffsetDateTime getLastModified() {
    return lastModified;
  }

  public void setLastModified(OffsetDateTime lastModified) {
    this.lastModified = lastModified;
  }

  public SearchResult friendlyLastModified(String friendlyLastModified) {
    this.friendlyLastModified = friendlyLastModified;
    return this;
  }

   /**
   * Get friendlyLastModified
   * @return friendlyLastModified
  **/
  @Schema(description = "")
  public String getFriendlyLastModified() {
    return friendlyLastModified;
  }

  public void setFriendlyLastModified(String friendlyLastModified) {
    this.friendlyLastModified = friendlyLastModified;
  }

  public SearchResult score(BigDecimal score) {
    this.score = score;
    return this;
  }

   /**
   * Get score
   * @return score
  **/
  @Schema(description = "")
  public BigDecimal getScore() {
    return score;
  }

  public void setScore(BigDecimal score) {
    this.score = score;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SearchResult searchResult = (SearchResult) o;
    return Objects.equals(this.content, searchResult.content) &&
        Objects.equals(this.user, searchResult.user) &&
        Objects.equals(this.space, searchResult.space) &&
        Objects.equals(this.title, searchResult.title) &&
        Objects.equals(this.excerpt, searchResult.excerpt) &&
        Objects.equals(this.url, searchResult.url) &&
        Objects.equals(this.resultParentContainer, searchResult.resultParentContainer) &&
        Objects.equals(this.resultGlobalContainer, searchResult.resultGlobalContainer) &&
        Objects.equals(this.breadcrumbs, searchResult.breadcrumbs) &&
        Objects.equals(this.entityType, searchResult.entityType) &&
        Objects.equals(this.iconCssClass, searchResult.iconCssClass) &&
        Objects.equals(this.lastModified, searchResult.lastModified) &&
        Objects.equals(this.friendlyLastModified, searchResult.friendlyLastModified) &&
        Objects.equals(this.score, searchResult.score);
  }

  @Override
  public int hashCode() {
    return Objects.hash(content, user, space, title, excerpt, url, resultParentContainer, resultGlobalContainer, breadcrumbs, entityType, iconCssClass, lastModified, friendlyLastModified, score);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SearchResult {\n");
    
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
    sb.append("    user: ").append(toIndentedString(user)).append("\n");
    sb.append("    space: ").append(toIndentedString(space)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    excerpt: ").append(toIndentedString(excerpt)).append("\n");
    sb.append("    url: ").append(toIndentedString(url)).append("\n");
    sb.append("    resultParentContainer: ").append(toIndentedString(resultParentContainer)).append("\n");
    sb.append("    resultGlobalContainer: ").append(toIndentedString(resultGlobalContainer)).append("\n");
    sb.append("    breadcrumbs: ").append(toIndentedString(breadcrumbs)).append("\n");
    sb.append("    entityType: ").append(toIndentedString(entityType)).append("\n");
    sb.append("    iconCssClass: ").append(toIndentedString(iconCssClass)).append("\n");
    sb.append("    lastModified: ").append(toIndentedString(lastModified)).append("\n");
    sb.append("    friendlyLastModified: ").append(toIndentedString(friendlyLastModified)).append("\n");
    sb.append("    score: ").append(toIndentedString(score)).append("\n");
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