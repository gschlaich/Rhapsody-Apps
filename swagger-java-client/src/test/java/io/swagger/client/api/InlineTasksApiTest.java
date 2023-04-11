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

import io.swagger.client.model.Task;
import io.swagger.client.model.TaskPageResponse;
import io.swagger.client.model.TaskStatusUpdate;
import org.junit.Test;
import org.junit.Ignore;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * API tests for InlineTasksApi
 */
@Ignore
public class InlineTasksApiTest {

    private final InlineTasksApi api = new InlineTasksApi();

    /**
     * Get inline task based on global ID
     *
     * Deprecated, use [Confluence&#x27;s v2 API](https://developer.atlassian.com/cloud/confluence/rest/v2/intro/).  Returns inline task based on the global ID.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: Permission to view the content associated with the task.
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void getTaskByIdTest() throws Exception {
        String inlineTaskId = null;
        Task response = api.getTaskById(inlineTaskId);

        // TODO: test validations
    }
    /**
     * Get inline tasks based on search parameters
     *
     * Deprecated, use [Confluence&#x27;s v2 API](https://developer.atlassian.com/cloud/confluence/rest/v2/intro/).  Returns inline tasks based on the search query.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: Permission to access the Confluence site (&#x27;Can use&#x27; global permission). Only tasks in contents that the user has permission to view are returned.
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void searchTasksTest() throws Exception {
        Integer start = null;
        Integer limit = null;
        String spaceKey = null;
        String pageId = null;
        String assignee = null;
        String creator = null;
        String completedUser = null;
        Long duedateFrom = null;
        Long duedateTo = null;
        Long createdateFrom = null;
        Long createdateTo = null;
        Long completedateFrom = null;
        Long completedateTo = null;
        String status = null;
        TaskPageResponse response = api.searchTasks(start, limit, spaceKey, pageId, assignee, creator, completedUser, duedateFrom, duedateTo, createdateFrom, createdateTo, completedateFrom, completedateTo, status);

        // TODO: test validations
    }
    /**
     * Update inline task given global ID
     *
     * Updates an inline tasks status given its global ID  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: Permission to update the content associated with the task.
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void updateTaskByIdTest() throws Exception {
        TaskStatusUpdate body = null;
        String inlineTaskId = null;
        Task response = api.updateTaskById(body, inlineTaskId);

        // TODO: test validations
    }
}