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

import io.swagger.client.model.SpaceWatchArray;
import io.swagger.client.model.UserWatch;
import io.swagger.client.model.WatchArray;
import org.junit.Test;
import org.junit.Ignore;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * API tests for ContentWatchesApi
 */
@Ignore
public class ContentWatchesApiTest {

    private final ContentWatchesApi api = new ContentWatchesApi();

    /**
     * Add content watcher
     *
     * Adds a user as a watcher to a piece of content. Choose the user by doing one of the following:  - Specify a user via a query parameter: Use the &#x60;accountId&#x60; to identify the user. - Do not specify a user: The currently logged-in user will be used.  Note, you must add the &#x60;X-Atlassian-Token: no-check&#x60; header when making a request, as this operation has XSRF protection.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: &#x27;Confluence Administrator&#x27; global permission if specifying a user, otherwise permission to access the Confluence site (&#x27;Can use&#x27; global permission).
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void addContentWatcherTest() throws Exception {
        String contentId = null;
        String key = null;
        String username = null;
        String accountId = null;
        api.addContentWatcher(contentId, key, username, accountId);

        // TODO: test validations
    }
    /**
     * Add label watcher
     *
     * Adds a user as a watcher to a label. Choose the user by doing one of the following:  - Specify a user via a query parameter: Use the &#x60;accountId&#x60; to identify the user. - Do not specify a user: The currently logged-in user will be used.  Note, you must add the &#x60;X-Atlassian-Token: no-check&#x60; header when making a request, as this operation has XSRF protection.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: &#x27;Confluence Administrator&#x27; global permission if specifying a user, otherwise permission to access the Confluence site (&#x27;Can use&#x27; global permission).
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void addLabelWatcherTest() throws Exception {
        String xAtlassianToken = null;
        String labelName = null;
        String key = null;
        String username = null;
        String accountId = null;
        api.addLabelWatcher(xAtlassianToken, labelName, key, username, accountId);

        // TODO: test validations
    }
    /**
     * Add space watcher
     *
     * Adds a user as a watcher to a space. Choose the user by doing one of the following:  - Specify a user via a query parameter: Use the &#x60;accountId&#x60; to identify the user. - Do not specify a user: The currently logged-in user will be used.  Note, you must add the &#x60;X-Atlassian-Token: no-check&#x60; header when making a request, as this operation has XSRF protection.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: &#x27;Confluence Administrator&#x27; global permission if specifying a user, otherwise permission to access the Confluence site (&#x27;Can use&#x27; global permission).
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void addSpaceWatcherTest() throws Exception {
        String xAtlassianToken = null;
        String spaceKey = null;
        String key = null;
        String username = null;
        String accountId = null;
        api.addSpaceWatcher(xAtlassianToken, spaceKey, key, username, accountId);

        // TODO: test validations
    }
    /**
     * Get content watch status
     *
     * Returns whether a user is watching a piece of content. Choose the user by doing one of the following:  - Specify a user via a query parameter: Use the &#x60;accountId&#x60; to identify the user. - Do not specify a user: The currently logged-in user will be used.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: &#x27;Confluence Administrator&#x27; global permission if specifying a user, otherwise permission to access the Confluence site (&#x27;Can use&#x27; global permission).
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void getContentWatchStatusTest() throws Exception {
        String contentId = null;
        String key = null;
        String username = null;
        String accountId = null;
        UserWatch response = api.getContentWatchStatus(contentId, key, username, accountId);

        // TODO: test validations
    }
    /**
     * Get space watchers
     *
     * Returns a list of watchers of a space
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void getWatchersForSpaceTest() throws Exception {
        String spaceKey = null;
        String start = null;
        String limit = null;
        SpaceWatchArray response = api.getWatchersForSpace(spaceKey, start, limit);

        // TODO: test validations
    }
    /**
     * Get watches for page
     *
     * Returns the watches for a page. A user that watches a page will receive receive notifications when the page is updated.  If you want to manage watches for a page, use the following &#x60;user&#x60; methods:  - [Get content watch status for user](#api-user-watch-content-contentId-get) - [Add content watch](#api-user-watch-content-contentId-post) - [Remove content watch](#api-user-watch-content-contentId-delete)  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: Permission to access the Confluence site (&#x27;Can use&#x27; global permission).
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void getWatchesForPageTest() throws Exception {
        String id = null;
        Integer start = null;
        Integer limit = null;
        WatchArray response = api.getWatchesForPage(id, start, limit);

        // TODO: test validations
    }
    /**
     * Get watches for space
     *
     * Returns all space watches for the space that the content is in. A user that watches a space will receive receive notifications when any content in the space is updated.  If you want to manage watches for a space, use the following &#x60;user&#x60; methods:  - [Get space watch status for user](#api-user-watch-space-spaceKey-get) - [Add space watch](#api-user-watch-space-spaceKey-post) - [Remove space watch](#api-user-watch-space-spaceKey-delete)  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: Permission to access the Confluence site (&#x27;Can use&#x27; global permission).
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void getWatchesForSpaceTest() throws Exception {
        String id = null;
        Integer start = null;
        Integer limit = null;
        SpaceWatchArray response = api.getWatchesForSpace(id, start, limit);

        // TODO: test validations
    }
    /**
     * Get label watch status
     *
     * Returns whether a user is watching a label. Choose the user by doing one of the following:  - Specify a user via a query parameter: Use the &#x60;accountId&#x60; to identify the user. - Do not specify a user: The currently logged-in user will be used.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: &#x27;Confluence Administrator&#x27; global permission if specifying a user, otherwise permission to access the Confluence site (&#x27;Can use&#x27; global permission).
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void isWatchingLabelTest() throws Exception {
        String labelName = null;
        String key = null;
        String username = null;
        String accountId = null;
        UserWatch response = api.isWatchingLabel(labelName, key, username, accountId);

        // TODO: test validations
    }
    /**
     * Get space watch status
     *
     * Returns whether a user is watching a space. Choose the user by doing one of the following:  - Specify a user via a query parameter: Use the &#x60;accountId&#x60; to identify the user. - Do not specify a user: The currently logged-in user will be used.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: &#x27;Confluence Administrator&#x27; global permission if specifying a user, otherwise permission to access the Confluence site (&#x27;Can use&#x27; global permission).
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void isWatchingSpaceTest() throws Exception {
        String spaceKey = null;
        String key = null;
        String username = null;
        String accountId = null;
        UserWatch response = api.isWatchingSpace(spaceKey, key, username, accountId);

        // TODO: test validations
    }
    /**
     * Remove content watcher
     *
     * Removes a user as a watcher from a piece of content. Choose the user by doing one of the following:  - Specify a user via a query parameter: Use the &#x60;accountId&#x60; to identify the user. - Do not specify a user: The currently logged-in user will be used.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: &#x27;Confluence Administrator&#x27; global permission if specifying a user, otherwise permission to access the Confluence site (&#x27;Can use&#x27; global permission).
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void removeContentWatcherTest() throws Exception {
        String xAtlassianToken = null;
        String contentId = null;
        String key = null;
        String username = null;
        String accountId = null;
        api.removeContentWatcher(xAtlassianToken, contentId, key, username, accountId);

        // TODO: test validations
    }
    /**
     * Remove label watcher
     *
     * Removes a user as a watcher from a label. Choose the user by doing one of the following:  - Specify a user via a query parameter: Use the &#x60;accountId&#x60; to identify the user. - Do not specify a user: The currently logged-in user will be used.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: &#x27;Confluence Administrator&#x27; global permission if specifying a user, otherwise permission to access the Confluence site (&#x27;Can use&#x27; global permission).
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void removeLabelWatcherTest() throws Exception {
        String labelName = null;
        String key = null;
        String username = null;
        String accountId = null;
        api.removeLabelWatcher(labelName, key, username, accountId);

        // TODO: test validations
    }
    /**
     * Remove space watch
     *
     * Removes a user as a watcher from a space. Choose the user by doing one of the following:  - Specify a user via a query parameter: Use the &#x60;accountId&#x60; to identify the user. - Do not specify a user: The currently logged-in user will be used.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: &#x27;Confluence Administrator&#x27; global permission if specifying a user, otherwise permission to access the Confluence site (&#x27;Can use&#x27; global permission).
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void removeSpaceWatchTest() throws Exception {
        String spaceKey = null;
        String key = null;
        String username = null;
        String accountId = null;
        api.removeSpaceWatch(spaceKey, key, username, accountId);

        // TODO: test validations
    }
}