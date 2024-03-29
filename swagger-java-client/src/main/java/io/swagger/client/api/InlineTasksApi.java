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

import io.swagger.client.ApiCallback;
import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.Configuration;
import io.swagger.client.Pair;
import io.swagger.client.ProgressRequestBody;
import io.swagger.client.ProgressResponseBody;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;


import io.swagger.client.model.Task;
import io.swagger.client.model.TaskPageResponse;
import io.swagger.client.model.TaskStatusUpdate;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InlineTasksApi {
    private ApiClient apiClient;

    public InlineTasksApi() {
        this(Configuration.getDefaultApiClient());
    }

    public InlineTasksApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Build call for getTaskById
     * @param inlineTaskId Global ID of the inline task (required)
     * @param progressListener Progress listener
     * @param progressRequestListener Progress request listener
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public com.squareup.okhttp.Call getTaskByIdCall(String inlineTaskId, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = null;
        
        // create path and map variables
        String localVarPath = "/wiki/rest/api/inlinetasks/{inlineTaskId}"
            .replaceAll("\\{" + "inlineTaskId" + "\\}", apiClient.escapeString(inlineTaskId.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();

        Map<String, String> localVarHeaderParams = new HashMap<String, String>();

        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = {
            "application/json"
        };
        final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) localVarHeaderParams.put("Accept", localVarAccept);

        final String[] localVarContentTypes = {
            
        };
        final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
        localVarHeaderParams.put("Content-Type", localVarContentType);

        if(progressListener != null) {
            apiClient.getHttpClient().networkInterceptors().add(new com.squareup.okhttp.Interceptor() {
                @Override
                public com.squareup.okhttp.Response intercept(com.squareup.okhttp.Interceptor.Chain chain) throws IOException {
                    com.squareup.okhttp.Response originalResponse = chain.proceed(chain.request());
                    return originalResponse.newBuilder()
                    .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                    .build();
                }
            });
        }

        String[] localVarAuthNames = new String[] { "basicAuth", "oAuthDefinitions" };
        return apiClient.buildCall(localVarPath, "GET", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAuthNames, progressRequestListener);
    }
    
    @SuppressWarnings("rawtypes")
    private com.squareup.okhttp.Call getTaskByIdValidateBeforeCall(String inlineTaskId, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        // verify the required parameter 'inlineTaskId' is set
        if (inlineTaskId == null) {
            throw new ApiException("Missing the required parameter 'inlineTaskId' when calling getTaskById(Async)");
        }
        
        com.squareup.okhttp.Call call = getTaskByIdCall(inlineTaskId, progressListener, progressRequestListener);
        return call;

        
        
        
        
    }

    /**
     * Get inline task based on global ID
     * Deprecated, use [Confluence&#x27;s v2 API](https://developer.atlassian.com/cloud/confluence/rest/v2/intro/).  Returns inline task based on the global ID.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: Permission to view the content associated with the task.
     * @param inlineTaskId Global ID of the inline task (required)
     * @return Task
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public Task getTaskById(String inlineTaskId) throws ApiException {
        ApiResponse<Task> resp = getTaskByIdWithHttpInfo(inlineTaskId);
        return resp.getData();
    }

    /**
     * Get inline task based on global ID
     * Deprecated, use [Confluence&#x27;s v2 API](https://developer.atlassian.com/cloud/confluence/rest/v2/intro/).  Returns inline task based on the global ID.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: Permission to view the content associated with the task.
     * @param inlineTaskId Global ID of the inline task (required)
     * @return ApiResponse&lt;Task&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<Task> getTaskByIdWithHttpInfo(String inlineTaskId) throws ApiException {
        com.squareup.okhttp.Call call = getTaskByIdValidateBeforeCall(inlineTaskId, null, null);
        Type localVarReturnType = new TypeToken<Task>(){}.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * Get inline task based on global ID (asynchronously)
     * Deprecated, use [Confluence&#x27;s v2 API](https://developer.atlassian.com/cloud/confluence/rest/v2/intro/).  Returns inline task based on the global ID.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: Permission to view the content associated with the task.
     * @param inlineTaskId Global ID of the inline task (required)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call getTaskByIdAsync(String inlineTaskId, final ApiCallback<Task> callback) throws ApiException {

        ProgressResponseBody.ProgressListener progressListener = null;
        ProgressRequestBody.ProgressRequestListener progressRequestListener = null;

        if (callback != null) {
            progressListener = new ProgressResponseBody.ProgressListener() {
                @Override
                public void update(long bytesRead, long contentLength, boolean done) {
                    callback.onDownloadProgress(bytesRead, contentLength, done);
                }
            };

            progressRequestListener = new ProgressRequestBody.ProgressRequestListener() {
                @Override
                public void onRequestProgress(long bytesWritten, long contentLength, boolean done) {
                    callback.onUploadProgress(bytesWritten, contentLength, done);
                }
            };
        }

        com.squareup.okhttp.Call call = getTaskByIdValidateBeforeCall(inlineTaskId, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<Task>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
    /**
     * Build call for searchTasks
     * @param start The starting offset for the results. (optional, default to 0)
     * @param limit The number of results to be returned. (optional, default to 20)
     * @param spaceKey The space key of a space. Multiple space keys can be specified. (optional)
     * @param pageId The page id of a page. Multiple page ids can be specified. (optional)
     * @param assignee Account ID of a user to whom a task is assigned. Multiple users can be specified. (optional)
     * @param creator Account ID of a user to who created a task. Multiple users can be specified. (optional)
     * @param completedUser Account ID of a user who completed a task. Multiple users can be specified. (optional)
     * @param duedateFrom Start of date range based on due dates (inclusive). (optional)
     * @param duedateTo End of date range based on due dates (inclusive). (optional)
     * @param createdateFrom Start of date range based on create dates (inclusive). (optional)
     * @param createdateTo End of date range based on create dates (inclusive). (optional)
     * @param completedateFrom Start of date range based on complete dates (inclusive). (optional)
     * @param completedateTo End of date range based on complete dates (inclusive). (optional)
     * @param status The status of the task. (checked/unchecked) (optional)
     * @param progressListener Progress listener
     * @param progressRequestListener Progress request listener
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public com.squareup.okhttp.Call searchTasksCall(Integer start, Integer limit, String spaceKey, String pageId, String assignee, String creator, String completedUser, Long duedateFrom, Long duedateTo, Long createdateFrom, Long createdateTo, Long completedateFrom, Long completedateTo, String status, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = null;
        
        // create path and map variables
        String localVarPath = "/wiki/rest/api/inlinetasks/search";

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        if (start != null)
        localVarQueryParams.addAll(apiClient.parameterToPair("start", start));
        if (limit != null)
        localVarQueryParams.addAll(apiClient.parameterToPair("limit", limit));
        if (spaceKey != null)
        localVarQueryParams.addAll(apiClient.parameterToPair("spaceKey", spaceKey));
        if (pageId != null)
        localVarQueryParams.addAll(apiClient.parameterToPair("pageId", pageId));
        if (assignee != null)
        localVarQueryParams.addAll(apiClient.parameterToPair("assignee", assignee));
        if (creator != null)
        localVarQueryParams.addAll(apiClient.parameterToPair("creator", creator));
        if (completedUser != null)
        localVarQueryParams.addAll(apiClient.parameterToPair("completedUser", completedUser));
        if (duedateFrom != null)
        localVarQueryParams.addAll(apiClient.parameterToPair("duedateFrom", duedateFrom));
        if (duedateTo != null)
        localVarQueryParams.addAll(apiClient.parameterToPair("duedateTo", duedateTo));
        if (createdateFrom != null)
        localVarQueryParams.addAll(apiClient.parameterToPair("createdateFrom", createdateFrom));
        if (createdateTo != null)
        localVarQueryParams.addAll(apiClient.parameterToPair("createdateTo", createdateTo));
        if (completedateFrom != null)
        localVarQueryParams.addAll(apiClient.parameterToPair("completedateFrom", completedateFrom));
        if (completedateTo != null)
        localVarQueryParams.addAll(apiClient.parameterToPair("completedateTo", completedateTo));
        if (status != null)
        localVarQueryParams.addAll(apiClient.parameterToPair("status", status));

        Map<String, String> localVarHeaderParams = new HashMap<String, String>();

        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = {
            "application/json"
        };
        final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) localVarHeaderParams.put("Accept", localVarAccept);

        final String[] localVarContentTypes = {
            
        };
        final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
        localVarHeaderParams.put("Content-Type", localVarContentType);

        if(progressListener != null) {
            apiClient.getHttpClient().networkInterceptors().add(new com.squareup.okhttp.Interceptor() {
                @Override
                public com.squareup.okhttp.Response intercept(com.squareup.okhttp.Interceptor.Chain chain) throws IOException {
                    com.squareup.okhttp.Response originalResponse = chain.proceed(chain.request());
                    return originalResponse.newBuilder()
                    .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                    .build();
                }
            });
        }

        String[] localVarAuthNames = new String[] { "basicAuth", "oAuthDefinitions" };
        return apiClient.buildCall(localVarPath, "GET", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAuthNames, progressRequestListener);
    }
    
    @SuppressWarnings("rawtypes")
    private com.squareup.okhttp.Call searchTasksValidateBeforeCall(Integer start, Integer limit, String spaceKey, String pageId, String assignee, String creator, String completedUser, Long duedateFrom, Long duedateTo, Long createdateFrom, Long createdateTo, Long completedateFrom, Long completedateTo, String status, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        
        com.squareup.okhttp.Call call = searchTasksCall(start, limit, spaceKey, pageId, assignee, creator, completedUser, duedateFrom, duedateTo, createdateFrom, createdateTo, completedateFrom, completedateTo, status, progressListener, progressRequestListener);
        return call;

        
        
        
        
    }

    /**
     * Get inline tasks based on search parameters
     * Deprecated, use [Confluence&#x27;s v2 API](https://developer.atlassian.com/cloud/confluence/rest/v2/intro/).  Returns inline tasks based on the search query.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: Permission to access the Confluence site (&#x27;Can use&#x27; global permission). Only tasks in contents that the user has permission to view are returned.
     * @param start The starting offset for the results. (optional, default to 0)
     * @param limit The number of results to be returned. (optional, default to 20)
     * @param spaceKey The space key of a space. Multiple space keys can be specified. (optional)
     * @param pageId The page id of a page. Multiple page ids can be specified. (optional)
     * @param assignee Account ID of a user to whom a task is assigned. Multiple users can be specified. (optional)
     * @param creator Account ID of a user to who created a task. Multiple users can be specified. (optional)
     * @param completedUser Account ID of a user who completed a task. Multiple users can be specified. (optional)
     * @param duedateFrom Start of date range based on due dates (inclusive). (optional)
     * @param duedateTo End of date range based on due dates (inclusive). (optional)
     * @param createdateFrom Start of date range based on create dates (inclusive). (optional)
     * @param createdateTo End of date range based on create dates (inclusive). (optional)
     * @param completedateFrom Start of date range based on complete dates (inclusive). (optional)
     * @param completedateTo End of date range based on complete dates (inclusive). (optional)
     * @param status The status of the task. (checked/unchecked) (optional)
     * @return TaskPageResponse
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public TaskPageResponse searchTasks(Integer start, Integer limit, String spaceKey, String pageId, String assignee, String creator, String completedUser, Long duedateFrom, Long duedateTo, Long createdateFrom, Long createdateTo, Long completedateFrom, Long completedateTo, String status) throws ApiException {
        ApiResponse<TaskPageResponse> resp = searchTasksWithHttpInfo(start, limit, spaceKey, pageId, assignee, creator, completedUser, duedateFrom, duedateTo, createdateFrom, createdateTo, completedateFrom, completedateTo, status);
        return resp.getData();
    }

    /**
     * Get inline tasks based on search parameters
     * Deprecated, use [Confluence&#x27;s v2 API](https://developer.atlassian.com/cloud/confluence/rest/v2/intro/).  Returns inline tasks based on the search query.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: Permission to access the Confluence site (&#x27;Can use&#x27; global permission). Only tasks in contents that the user has permission to view are returned.
     * @param start The starting offset for the results. (optional, default to 0)
     * @param limit The number of results to be returned. (optional, default to 20)
     * @param spaceKey The space key of a space. Multiple space keys can be specified. (optional)
     * @param pageId The page id of a page. Multiple page ids can be specified. (optional)
     * @param assignee Account ID of a user to whom a task is assigned. Multiple users can be specified. (optional)
     * @param creator Account ID of a user to who created a task. Multiple users can be specified. (optional)
     * @param completedUser Account ID of a user who completed a task. Multiple users can be specified. (optional)
     * @param duedateFrom Start of date range based on due dates (inclusive). (optional)
     * @param duedateTo End of date range based on due dates (inclusive). (optional)
     * @param createdateFrom Start of date range based on create dates (inclusive). (optional)
     * @param createdateTo End of date range based on create dates (inclusive). (optional)
     * @param completedateFrom Start of date range based on complete dates (inclusive). (optional)
     * @param completedateTo End of date range based on complete dates (inclusive). (optional)
     * @param status The status of the task. (checked/unchecked) (optional)
     * @return ApiResponse&lt;TaskPageResponse&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<TaskPageResponse> searchTasksWithHttpInfo(Integer start, Integer limit, String spaceKey, String pageId, String assignee, String creator, String completedUser, Long duedateFrom, Long duedateTo, Long createdateFrom, Long createdateTo, Long completedateFrom, Long completedateTo, String status) throws ApiException {
        com.squareup.okhttp.Call call = searchTasksValidateBeforeCall(start, limit, spaceKey, pageId, assignee, creator, completedUser, duedateFrom, duedateTo, createdateFrom, createdateTo, completedateFrom, completedateTo, status, null, null);
        Type localVarReturnType = new TypeToken<TaskPageResponse>(){}.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * Get inline tasks based on search parameters (asynchronously)
     * Deprecated, use [Confluence&#x27;s v2 API](https://developer.atlassian.com/cloud/confluence/rest/v2/intro/).  Returns inline tasks based on the search query.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: Permission to access the Confluence site (&#x27;Can use&#x27; global permission). Only tasks in contents that the user has permission to view are returned.
     * @param start The starting offset for the results. (optional, default to 0)
     * @param limit The number of results to be returned. (optional, default to 20)
     * @param spaceKey The space key of a space. Multiple space keys can be specified. (optional)
     * @param pageId The page id of a page. Multiple page ids can be specified. (optional)
     * @param assignee Account ID of a user to whom a task is assigned. Multiple users can be specified. (optional)
     * @param creator Account ID of a user to who created a task. Multiple users can be specified. (optional)
     * @param completedUser Account ID of a user who completed a task. Multiple users can be specified. (optional)
     * @param duedateFrom Start of date range based on due dates (inclusive). (optional)
     * @param duedateTo End of date range based on due dates (inclusive). (optional)
     * @param createdateFrom Start of date range based on create dates (inclusive). (optional)
     * @param createdateTo End of date range based on create dates (inclusive). (optional)
     * @param completedateFrom Start of date range based on complete dates (inclusive). (optional)
     * @param completedateTo End of date range based on complete dates (inclusive). (optional)
     * @param status The status of the task. (checked/unchecked) (optional)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call searchTasksAsync(Integer start, Integer limit, String spaceKey, String pageId, String assignee, String creator, String completedUser, Long duedateFrom, Long duedateTo, Long createdateFrom, Long createdateTo, Long completedateFrom, Long completedateTo, String status, final ApiCallback<TaskPageResponse> callback) throws ApiException {

        ProgressResponseBody.ProgressListener progressListener = null;
        ProgressRequestBody.ProgressRequestListener progressRequestListener = null;

        if (callback != null) {
            progressListener = new ProgressResponseBody.ProgressListener() {
                @Override
                public void update(long bytesRead, long contentLength, boolean done) {
                    callback.onDownloadProgress(bytesRead, contentLength, done);
                }
            };

            progressRequestListener = new ProgressRequestBody.ProgressRequestListener() {
                @Override
                public void onRequestProgress(long bytesWritten, long contentLength, boolean done) {
                    callback.onUploadProgress(bytesWritten, contentLength, done);
                }
            };
        }

        com.squareup.okhttp.Call call = searchTasksValidateBeforeCall(start, limit, spaceKey, pageId, assignee, creator, completedUser, duedateFrom, duedateTo, createdateFrom, createdateTo, completedateFrom, completedateTo, status, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<TaskPageResponse>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
    /**
     * Build call for updateTaskById
     * @param body The updated task status. (required)
     * @param inlineTaskId Global ID of the inline task to update (required)
     * @param progressListener Progress listener
     * @param progressRequestListener Progress request listener
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public com.squareup.okhttp.Call updateTaskByIdCall(TaskStatusUpdate body, String inlineTaskId, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = body;
        
        // create path and map variables
        String localVarPath = "/wiki/rest/api/inlinetasks/{inlineTaskId}"
            .replaceAll("\\{" + "inlineTaskId" + "\\}", apiClient.escapeString(inlineTaskId.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();

        Map<String, String> localVarHeaderParams = new HashMap<String, String>();

        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = {
            "application/json"
        };
        final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) localVarHeaderParams.put("Accept", localVarAccept);

        final String[] localVarContentTypes = {
            "application/json"
        };
        final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
        localVarHeaderParams.put("Content-Type", localVarContentType);

        if(progressListener != null) {
            apiClient.getHttpClient().networkInterceptors().add(new com.squareup.okhttp.Interceptor() {
                @Override
                public com.squareup.okhttp.Response intercept(com.squareup.okhttp.Interceptor.Chain chain) throws IOException {
                    com.squareup.okhttp.Response originalResponse = chain.proceed(chain.request());
                    return originalResponse.newBuilder()
                    .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                    .build();
                }
            });
        }

        String[] localVarAuthNames = new String[] { "basicAuth", "oAuthDefinitions" };
        return apiClient.buildCall(localVarPath, "PUT", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAuthNames, progressRequestListener);
    }
    
    @SuppressWarnings("rawtypes")
    private com.squareup.okhttp.Call updateTaskByIdValidateBeforeCall(TaskStatusUpdate body, String inlineTaskId, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        // verify the required parameter 'body' is set
        if (body == null) {
            throw new ApiException("Missing the required parameter 'body' when calling updateTaskById(Async)");
        }
        // verify the required parameter 'inlineTaskId' is set
        if (inlineTaskId == null) {
            throw new ApiException("Missing the required parameter 'inlineTaskId' when calling updateTaskById(Async)");
        }
        
        com.squareup.okhttp.Call call = updateTaskByIdCall(body, inlineTaskId, progressListener, progressRequestListener);
        return call;

        
        
        
        
    }

    /**
     * Update inline task given global ID
     * Updates an inline tasks status given its global ID  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: Permission to update the content associated with the task.
     * @param body The updated task status. (required)
     * @param inlineTaskId Global ID of the inline task to update (required)
     * @return Task
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public Task updateTaskById(TaskStatusUpdate body, String inlineTaskId) throws ApiException {
        ApiResponse<Task> resp = updateTaskByIdWithHttpInfo(body, inlineTaskId);
        return resp.getData();
    }

    /**
     * Update inline task given global ID
     * Updates an inline tasks status given its global ID  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: Permission to update the content associated with the task.
     * @param body The updated task status. (required)
     * @param inlineTaskId Global ID of the inline task to update (required)
     * @return ApiResponse&lt;Task&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<Task> updateTaskByIdWithHttpInfo(TaskStatusUpdate body, String inlineTaskId) throws ApiException {
        com.squareup.okhttp.Call call = updateTaskByIdValidateBeforeCall(body, inlineTaskId, null, null);
        Type localVarReturnType = new TypeToken<Task>(){}.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * Update inline task given global ID (asynchronously)
     * Updates an inline tasks status given its global ID  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: Permission to update the content associated with the task.
     * @param body The updated task status. (required)
     * @param inlineTaskId Global ID of the inline task to update (required)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call updateTaskByIdAsync(TaskStatusUpdate body, String inlineTaskId, final ApiCallback<Task> callback) throws ApiException {

        ProgressResponseBody.ProgressListener progressListener = null;
        ProgressRequestBody.ProgressRequestListener progressRequestListener = null;

        if (callback != null) {
            progressListener = new ProgressResponseBody.ProgressListener() {
                @Override
                public void update(long bytesRead, long contentLength, boolean done) {
                    callback.onDownloadProgress(bytesRead, contentLength, done);
                }
            };

            progressRequestListener = new ProgressRequestBody.ProgressRequestListener() {
                @Override
                public void onRequestProgress(long bytesWritten, long contentLength, boolean done) {
                    callback.onUploadProgress(bytesWritten, contentLength, done);
                }
            };
        }

        com.squareup.okhttp.Call call = updateTaskByIdValidateBeforeCall(body, inlineTaskId, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<Task>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
}
