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

package io.swagger.client.api;

import io.swagger.client.model.Version;
import io.swagger.client.model.VersionArray;
import io.swagger.client.model.VersionRestore;
import org.junit.Test;
import org.junit.Ignore;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * API tests for ContentVersionsApi
 */
@Ignore
public class ContentVersionsApiTest {

    private final ContentVersionsApi api = new ContentVersionsApi();

    /**
     * Delete content version
     *
     * Delete a historical version. This does not delete the changes made to the content in that version, rather the changes for the deleted version are rolled up into the next version. Note, you cannot delete the current version.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: Permission to update the content.
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void deleteContentVersionTest() throws Exception {
        String id = null;
        Integer versionNumber = null;
        api.deleteContentVersion(id, versionNumber);

        // TODO: test validations
    }
    /**
     * Get content version
     *
     * Deprecated, use [Confluence&#x27;s v2 API](https://developer.atlassian.com/cloud/confluence/rest/v2/intro/).  Returns a version for a piece of content.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: Permission to view the content. If the content is a blog post, &#x27;View&#x27; permission for the space is required.
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void getContentVersionTest() throws Exception {
        String id = null;
        Integer versionNumber = null;
        List<String> expand = null;
        Version response = api.getContentVersion(id, versionNumber, expand);

        // TODO: test validations
    }
    /**
     * Get content versions
     *
     * Deprecated, use [Confluence&#x27;s v2 API](https://developer.atlassian.com/cloud/confluence/rest/v2/intro/).  Returns the versions for a piece of content in descending order.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: Permission to view the content. If the content is a blog post, &#x27;View&#x27; permission for the space is required.
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void getContentVersionsTest() throws Exception {
        String id = null;
        Integer start = null;
        Integer limit = null;
        List<String> expand = null;
        VersionArray response = api.getContentVersions(id, start, limit, expand);

        // TODO: test validations
    }
    /**
     * Restore content version
     *
     * Restores a historical version to be the latest version. That is, a new version is created with the content of the historical version.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: Permission to update the content.
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void restoreContentVersionTest() throws Exception {
        VersionRestore body = null;
        String id = null;
        List<String> expand = null;
        Version response = api.restoreContentVersion(body, id, expand);

        // TODO: test validations
    }
}
