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


import io.swagger.client.model.LabelArray;
import io.swagger.client.model.LabelCreate;
import io.swagger.client.model.LongTask;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExperimentalApi {
    private ApiClient apiClient;

    public ExperimentalApi() {
        this(Configuration.getDefaultApiClient());
    }

    public ExperimentalApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Build call for addLabelsToSpace
     * @param body The labels to add to the content. (required)
     * @param spaceKey The key of the space to add labels to. (required)
     * @param progressListener Progress listener
     * @param progressRequestListener Progress request listener
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public com.squareup.okhttp.Call addLabelsToSpaceCall(List<LabelCreate> body, String spaceKey, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = body;
        
        // create path and map variables
        String localVarPath = "/wiki/rest/api/space/{spaceKey}/label"
            .replaceAll("\\{" + "spaceKey" + "\\}", apiClient.escapeString(spaceKey.toString()));

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
        return apiClient.buildCall(localVarPath, "POST", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAuthNames, progressRequestListener);
    }
    
    @SuppressWarnings("rawtypes")
    private com.squareup.okhttp.Call addLabelsToSpaceValidateBeforeCall(List<LabelCreate> body, String spaceKey, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        // verify the required parameter 'body' is set
        if (body == null) {
            throw new ApiException("Missing the required parameter 'body' when calling addLabelsToSpace(Async)");
        }
        // verify the required parameter 'spaceKey' is set
        if (spaceKey == null) {
            throw new ApiException("Missing the required parameter 'spaceKey' when calling addLabelsToSpace(Async)");
        }
        
        com.squareup.okhttp.Call call = addLabelsToSpaceCall(body, spaceKey, progressListener, progressRequestListener);
        return call;

        
        
        
        
    }

    /**
     * Add labels to a space
     * Adds labels to a piece of content. Does not modify the existing labels.  Notes:  - Labels can also be added when creating content ([Create content](#api-content-post)). - Labels can be updated when updating content ([Update content](#api-content-id-put)). This will delete the existing labels and replace them with the labels in the request.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: Permission to update the content.
     * @param body The labels to add to the content. (required)
     * @param spaceKey The key of the space to add labels to. (required)
     * @return LabelArray
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public LabelArray addLabelsToSpace(List<LabelCreate> body, String spaceKey) throws ApiException {
        ApiResponse<LabelArray> resp = addLabelsToSpaceWithHttpInfo(body, spaceKey);
        return resp.getData();
    }

    /**
     * Add labels to a space
     * Adds labels to a piece of content. Does not modify the existing labels.  Notes:  - Labels can also be added when creating content ([Create content](#api-content-post)). - Labels can be updated when updating content ([Update content](#api-content-id-put)). This will delete the existing labels and replace them with the labels in the request.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: Permission to update the content.
     * @param body The labels to add to the content. (required)
     * @param spaceKey The key of the space to add labels to. (required)
     * @return ApiResponse&lt;LabelArray&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<LabelArray> addLabelsToSpaceWithHttpInfo(List<LabelCreate> body, String spaceKey) throws ApiException {
        com.squareup.okhttp.Call call = addLabelsToSpaceValidateBeforeCall(body, spaceKey, null, null);
        Type localVarReturnType = new TypeToken<LabelArray>(){}.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * Add labels to a space (asynchronously)
     * Adds labels to a piece of content. Does not modify the existing labels.  Notes:  - Labels can also be added when creating content ([Create content](#api-content-post)). - Labels can be updated when updating content ([Update content](#api-content-id-put)). This will delete the existing labels and replace them with the labels in the request.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: Permission to update the content.
     * @param body The labels to add to the content. (required)
     * @param spaceKey The key of the space to add labels to. (required)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call addLabelsToSpaceAsync(List<LabelCreate> body, String spaceKey, final ApiCallback<LabelArray> callback) throws ApiException {

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

        com.squareup.okhttp.Call call = addLabelsToSpaceValidateBeforeCall(body, spaceKey, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<LabelArray>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
    /**
     * Build call for deleteLabelFromSpace
     * @param spaceKey The key of the space to remove a labels from. (required)
     * @param name The name of the label to remove (required)
     * @param prefix The prefix of the label to remove. If not provided defaults to global. (optional)
     * @param progressListener Progress listener
     * @param progressRequestListener Progress request listener
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public com.squareup.okhttp.Call deleteLabelFromSpaceCall(String spaceKey, String name, String prefix, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = null;
        
        // create path and map variables
        String localVarPath = "/wiki/rest/api/space/{spaceKey}/label"
            .replaceAll("\\{" + "spaceKey" + "\\}", apiClient.escapeString(spaceKey.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        if (name != null)
        localVarQueryParams.addAll(apiClient.parameterToPair("name", name));
        if (prefix != null)
        localVarQueryParams.addAll(apiClient.parameterToPair("prefix", prefix));

        Map<String, String> localVarHeaderParams = new HashMap<String, String>();

        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = {
            
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
        return apiClient.buildCall(localVarPath, "DELETE", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAuthNames, progressRequestListener);
    }
    
    @SuppressWarnings("rawtypes")
    private com.squareup.okhttp.Call deleteLabelFromSpaceValidateBeforeCall(String spaceKey, String name, String prefix, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        // verify the required parameter 'spaceKey' is set
        if (spaceKey == null) {
            throw new ApiException("Missing the required parameter 'spaceKey' when calling deleteLabelFromSpace(Async)");
        }
        // verify the required parameter 'name' is set
        if (name == null) {
            throw new ApiException("Missing the required parameter 'name' when calling deleteLabelFromSpace(Async)");
        }
        
        com.squareup.okhttp.Call call = deleteLabelFromSpaceCall(spaceKey, name, prefix, progressListener, progressRequestListener);
        return call;

        
        
        
        
    }

    /**
     * Remove label from a space
     * 
     * @param spaceKey The key of the space to remove a labels from. (required)
     * @param name The name of the label to remove (required)
     * @param prefix The prefix of the label to remove. If not provided defaults to global. (optional)
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public void deleteLabelFromSpace(String spaceKey, String name, String prefix) throws ApiException {
        deleteLabelFromSpaceWithHttpInfo(spaceKey, name, prefix);
    }

    /**
     * Remove label from a space
     * 
     * @param spaceKey The key of the space to remove a labels from. (required)
     * @param name The name of the label to remove (required)
     * @param prefix The prefix of the label to remove. If not provided defaults to global. (optional)
     * @return ApiResponse&lt;Void&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<Void> deleteLabelFromSpaceWithHttpInfo(String spaceKey, String name, String prefix) throws ApiException {
        com.squareup.okhttp.Call call = deleteLabelFromSpaceValidateBeforeCall(spaceKey, name, prefix, null, null);
        return apiClient.execute(call);
    }

    /**
     * Remove label from a space (asynchronously)
     * 
     * @param spaceKey The key of the space to remove a labels from. (required)
     * @param name The name of the label to remove (required)
     * @param prefix The prefix of the label to remove. If not provided defaults to global. (optional)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call deleteLabelFromSpaceAsync(String spaceKey, String name, String prefix, final ApiCallback<Void> callback) throws ApiException {

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

        com.squareup.okhttp.Call call = deleteLabelFromSpaceValidateBeforeCall(spaceKey, name, prefix, progressListener, progressRequestListener);
        apiClient.executeAsync(call, callback);
        return call;
    }
    /**
     * Build call for deletePageTree
     * @param id The ID of the content which forms root of the page tree, to be deleted. (required)
     * @param progressListener Progress listener
     * @param progressRequestListener Progress request listener
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public com.squareup.okhttp.Call deletePageTreeCall(String id, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = null;
        
        // create path and map variables
        String localVarPath = "/wiki/rest/api/content/{id}/pageTree"
            .replaceAll("\\{" + "id" + "\\}", apiClient.escapeString(id.toString()));

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
        return apiClient.buildCall(localVarPath, "DELETE", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAuthNames, progressRequestListener);
    }
    
    @SuppressWarnings("rawtypes")
    private com.squareup.okhttp.Call deletePageTreeValidateBeforeCall(String id, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        // verify the required parameter 'id' is set
        if (id == null) {
            throw new ApiException("Missing the required parameter 'id' when calling deletePageTree(Async)");
        }
        
        com.squareup.okhttp.Call call = deletePageTreeCall(id, progressListener, progressRequestListener);
        return call;

        
        
        
        
    }

    /**
     * Delete page tree
     * Moves a pagetree rooted at a page to the space&#x27;s trash:  - If the content&#x27;s type is &#x60;page&#x60; and its status is &#x60;current&#x60;, it will be trashed including all its descendants. - For every other combination of content type and status, this API is not supported.  This API accepts the pageTree delete request and returns a task ID. The delete process happens asynchronously.   Response example:  &lt;pre&gt;&lt;code&gt;  {       \&quot;id\&quot; : \&quot;1180606\&quot;,       \&quot;links\&quot; : {            \&quot;status\&quot; : \&quot;/rest/api/longtask/1180606\&quot;       }  }  &lt;/code&gt;&lt;/pre&gt;  Use the &#x60;/longtask/&lt;taskId&gt;&#x60; REST API to get the copy task status.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: &#x27;Delete&#x27; permission for the space that the content is in.
     * @param id The ID of the content which forms root of the page tree, to be deleted. (required)
     * @return LongTask
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public LongTask deletePageTree(String id) throws ApiException {
        ApiResponse<LongTask> resp = deletePageTreeWithHttpInfo(id);
        return resp.getData();
    }

    /**
     * Delete page tree
     * Moves a pagetree rooted at a page to the space&#x27;s trash:  - If the content&#x27;s type is &#x60;page&#x60; and its status is &#x60;current&#x60;, it will be trashed including all its descendants. - For every other combination of content type and status, this API is not supported.  This API accepts the pageTree delete request and returns a task ID. The delete process happens asynchronously.   Response example:  &lt;pre&gt;&lt;code&gt;  {       \&quot;id\&quot; : \&quot;1180606\&quot;,       \&quot;links\&quot; : {            \&quot;status\&quot; : \&quot;/rest/api/longtask/1180606\&quot;       }  }  &lt;/code&gt;&lt;/pre&gt;  Use the &#x60;/longtask/&lt;taskId&gt;&#x60; REST API to get the copy task status.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: &#x27;Delete&#x27; permission for the space that the content is in.
     * @param id The ID of the content which forms root of the page tree, to be deleted. (required)
     * @return ApiResponse&lt;LongTask&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<LongTask> deletePageTreeWithHttpInfo(String id) throws ApiException {
        com.squareup.okhttp.Call call = deletePageTreeValidateBeforeCall(id, null, null);
        Type localVarReturnType = new TypeToken<LongTask>(){}.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * Delete page tree (asynchronously)
     * Moves a pagetree rooted at a page to the space&#x27;s trash:  - If the content&#x27;s type is &#x60;page&#x60; and its status is &#x60;current&#x60;, it will be trashed including all its descendants. - For every other combination of content type and status, this API is not supported.  This API accepts the pageTree delete request and returns a task ID. The delete process happens asynchronously.   Response example:  &lt;pre&gt;&lt;code&gt;  {       \&quot;id\&quot; : \&quot;1180606\&quot;,       \&quot;links\&quot; : {            \&quot;status\&quot; : \&quot;/rest/api/longtask/1180606\&quot;       }  }  &lt;/code&gt;&lt;/pre&gt;  Use the &#x60;/longtask/&lt;taskId&gt;&#x60; REST API to get the copy task status.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: &#x27;Delete&#x27; permission for the space that the content is in.
     * @param id The ID of the content which forms root of the page tree, to be deleted. (required)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call deletePageTreeAsync(String id, final ApiCallback<LongTask> callback) throws ApiException {

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

        com.squareup.okhttp.Call call = deletePageTreeValidateBeforeCall(id, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<LongTask>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
    /**
     * Build call for getLabelsForSpace
     * @param spaceKey The key of the space to get labels for. (required)
     * @param prefix Filters the results to labels with the specified prefix. If this parameter is not specified, then labels with any prefix will be returned.  - &#x60;global&#x60; prefix is used by labels that are on content within the provided space. - &#x60;my&#x60; prefix can be explicitly added by a user when adding a label via the UI, e.g. &#x27;my:example-label&#x27;. - &#x60;team&#x60; prefix is used for labels applied to the space. (optional)
     * @param start The starting index of the returned labels. (optional, default to 0)
     * @param limit The maximum number of labels to return per page. Note, this may be restricted by fixed system limits. (optional, default to 200)
     * @param progressListener Progress listener
     * @param progressRequestListener Progress request listener
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public com.squareup.okhttp.Call getLabelsForSpaceCall(String spaceKey, String prefix, Integer start, Integer limit, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = null;
        
        // create path and map variables
        String localVarPath = "/wiki/rest/api/space/{spaceKey}/label"
            .replaceAll("\\{" + "spaceKey" + "\\}", apiClient.escapeString(spaceKey.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        if (prefix != null)
        localVarQueryParams.addAll(apiClient.parameterToPair("prefix", prefix));
        if (start != null)
        localVarQueryParams.addAll(apiClient.parameterToPair("start", start));
        if (limit != null)
        localVarQueryParams.addAll(apiClient.parameterToPair("limit", limit));

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
    private com.squareup.okhttp.Call getLabelsForSpaceValidateBeforeCall(String spaceKey, String prefix, Integer start, Integer limit, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        // verify the required parameter 'spaceKey' is set
        if (spaceKey == null) {
            throw new ApiException("Missing the required parameter 'spaceKey' when calling getLabelsForSpace(Async)");
        }
        
        com.squareup.okhttp.Call call = getLabelsForSpaceCall(spaceKey, prefix, start, limit, progressListener, progressRequestListener);
        return call;

        
        
        
        
    }

    /**
     * Get Space Labels
     * Returns a list of labels associated with a space. Can provide a prefix as well as other filters to select different types of labels.
     * @param spaceKey The key of the space to get labels for. (required)
     * @param prefix Filters the results to labels with the specified prefix. If this parameter is not specified, then labels with any prefix will be returned.  - &#x60;global&#x60; prefix is used by labels that are on content within the provided space. - &#x60;my&#x60; prefix can be explicitly added by a user when adding a label via the UI, e.g. &#x27;my:example-label&#x27;. - &#x60;team&#x60; prefix is used for labels applied to the space. (optional)
     * @param start The starting index of the returned labels. (optional, default to 0)
     * @param limit The maximum number of labels to return per page. Note, this may be restricted by fixed system limits. (optional, default to 200)
     * @return LabelArray
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public LabelArray getLabelsForSpace(String spaceKey, String prefix, Integer start, Integer limit) throws ApiException {
        ApiResponse<LabelArray> resp = getLabelsForSpaceWithHttpInfo(spaceKey, prefix, start, limit);
        return resp.getData();
    }

    /**
     * Get Space Labels
     * Returns a list of labels associated with a space. Can provide a prefix as well as other filters to select different types of labels.
     * @param spaceKey The key of the space to get labels for. (required)
     * @param prefix Filters the results to labels with the specified prefix. If this parameter is not specified, then labels with any prefix will be returned.  - &#x60;global&#x60; prefix is used by labels that are on content within the provided space. - &#x60;my&#x60; prefix can be explicitly added by a user when adding a label via the UI, e.g. &#x27;my:example-label&#x27;. - &#x60;team&#x60; prefix is used for labels applied to the space. (optional)
     * @param start The starting index of the returned labels. (optional, default to 0)
     * @param limit The maximum number of labels to return per page. Note, this may be restricted by fixed system limits. (optional, default to 200)
     * @return ApiResponse&lt;LabelArray&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<LabelArray> getLabelsForSpaceWithHttpInfo(String spaceKey, String prefix, Integer start, Integer limit) throws ApiException {
        com.squareup.okhttp.Call call = getLabelsForSpaceValidateBeforeCall(spaceKey, prefix, start, limit, null, null);
        Type localVarReturnType = new TypeToken<LabelArray>(){}.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * Get Space Labels (asynchronously)
     * Returns a list of labels associated with a space. Can provide a prefix as well as other filters to select different types of labels.
     * @param spaceKey The key of the space to get labels for. (required)
     * @param prefix Filters the results to labels with the specified prefix. If this parameter is not specified, then labels with any prefix will be returned.  - &#x60;global&#x60; prefix is used by labels that are on content within the provided space. - &#x60;my&#x60; prefix can be explicitly added by a user when adding a label via the UI, e.g. &#x27;my:example-label&#x27;. - &#x60;team&#x60; prefix is used for labels applied to the space. (optional)
     * @param start The starting index of the returned labels. (optional, default to 0)
     * @param limit The maximum number of labels to return per page. Note, this may be restricted by fixed system limits. (optional, default to 200)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call getLabelsForSpaceAsync(String spaceKey, String prefix, Integer start, Integer limit, final ApiCallback<LabelArray> callback) throws ApiException {

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

        com.squareup.okhttp.Call call = getLabelsForSpaceValidateBeforeCall(spaceKey, prefix, start, limit, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<LabelArray>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
}
