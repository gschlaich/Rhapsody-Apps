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


import io.swagger.client.model.SpacePermissionCustomContent;
import io.swagger.client.model.SpacePermissionV2;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpacePermissionsApi {
    private ApiClient apiClient;

    public SpacePermissionsApi() {
        this(Configuration.getDefaultApiClient());
    }

    public SpacePermissionsApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Build call for addCustomContentPermissions
     * @param body The permissions to be created. (required)
     * @param spaceKey The key of the space to be queried for its content. (required)
     * @param progressListener Progress listener
     * @param progressRequestListener Progress request listener
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public com.squareup.okhttp.Call addCustomContentPermissionsCall(SpacePermissionCustomContent body, String spaceKey, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = body;
        
        // create path and map variables
        String localVarPath = "/wiki/rest/api/space/{spaceKey}/permission/custom-content"
            .replaceAll("\\{" + "spaceKey" + "\\}", apiClient.escapeString(spaceKey.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();

        Map<String, String> localVarHeaderParams = new HashMap<String, String>();

        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = {
            
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
    private com.squareup.okhttp.Call addCustomContentPermissionsValidateBeforeCall(SpacePermissionCustomContent body, String spaceKey, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        // verify the required parameter 'body' is set
        if (body == null) {
            throw new ApiException("Missing the required parameter 'body' when calling addCustomContentPermissions(Async)");
        }
        // verify the required parameter 'spaceKey' is set
        if (spaceKey == null) {
            throw new ApiException("Missing the required parameter 'spaceKey' when calling addCustomContentPermissions(Async)");
        }
        
        com.squareup.okhttp.Call call = addCustomContentPermissionsCall(body, spaceKey, progressListener, progressRequestListener);
        return call;

        
        
        
        
    }

    /**
     * Add new custom content permission to space
     * Adds new custom content permission to space.  If the permission to be added is a group permission, the group can be identified by its group name or group id.  Note: Only apps can access this REST resource and only make changes to the respective app permissions.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: &#x27;Admin&#x27; permission for the space.
     * @param body The permissions to be created. (required)
     * @param spaceKey The key of the space to be queried for its content. (required)
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public void addCustomContentPermissions(SpacePermissionCustomContent body, String spaceKey) throws ApiException {
        addCustomContentPermissionsWithHttpInfo(body, spaceKey);
    }

    /**
     * Add new custom content permission to space
     * Adds new custom content permission to space.  If the permission to be added is a group permission, the group can be identified by its group name or group id.  Note: Only apps can access this REST resource and only make changes to the respective app permissions.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: &#x27;Admin&#x27; permission for the space.
     * @param body The permissions to be created. (required)
     * @param spaceKey The key of the space to be queried for its content. (required)
     * @return ApiResponse&lt;Void&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<Void> addCustomContentPermissionsWithHttpInfo(SpacePermissionCustomContent body, String spaceKey) throws ApiException {
        com.squareup.okhttp.Call call = addCustomContentPermissionsValidateBeforeCall(body, spaceKey, null, null);
        return apiClient.execute(call);
    }

    /**
     * Add new custom content permission to space (asynchronously)
     * Adds new custom content permission to space.  If the permission to be added is a group permission, the group can be identified by its group name or group id.  Note: Only apps can access this REST resource and only make changes to the respective app permissions.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: &#x27;Admin&#x27; permission for the space.
     * @param body The permissions to be created. (required)
     * @param spaceKey The key of the space to be queried for its content. (required)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call addCustomContentPermissionsAsync(SpacePermissionCustomContent body, String spaceKey, final ApiCallback<Void> callback) throws ApiException {

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

        com.squareup.okhttp.Call call = addCustomContentPermissionsValidateBeforeCall(body, spaceKey, progressListener, progressRequestListener);
        apiClient.executeAsync(call, callback);
        return call;
    }
    /**
     * Build call for addPermissionToSpace
     * @param body The permission to be created. (required)
     * @param spaceKey The key of the space to be queried for its content. (required)
     * @param progressListener Progress listener
     * @param progressRequestListener Progress request listener
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public com.squareup.okhttp.Call addPermissionToSpaceCall(Map<String, Object> body, String spaceKey, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = body;
        
        // create path and map variables
        String localVarPath = "/wiki/rest/api/space/{spaceKey}/permission"
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
    private com.squareup.okhttp.Call addPermissionToSpaceValidateBeforeCall(Map<String, Object> body, String spaceKey, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        // verify the required parameter 'body' is set
        if (body == null) {
            throw new ApiException("Missing the required parameter 'body' when calling addPermissionToSpace(Async)");
        }
        // verify the required parameter 'spaceKey' is set
        if (spaceKey == null) {
            throw new ApiException("Missing the required parameter 'spaceKey' when calling addPermissionToSpace(Async)");
        }
        
        com.squareup.okhttp.Call call = addPermissionToSpaceCall(body, spaceKey, progressListener, progressRequestListener);
        return call;

        
        
        
        
    }

    /**
     * Add new permission to space
     * Adds new permission to space.  If the permission to be added is a group permission, the group can be identified by its group name or group id.  Note: Apps cannot access this REST resource - including when utilizing user impersonation.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: &#x27;Admin&#x27; permission for the space.
     * @param body The permission to be created. (required)
     * @param spaceKey The key of the space to be queried for its content. (required)
     * @return SpacePermissionV2
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public SpacePermissionV2 addPermissionToSpace(Map<String, Object> body, String spaceKey) throws ApiException {
        ApiResponse<SpacePermissionV2> resp = addPermissionToSpaceWithHttpInfo(body, spaceKey);
        return resp.getData();
    }

    /**
     * Add new permission to space
     * Adds new permission to space.  If the permission to be added is a group permission, the group can be identified by its group name or group id.  Note: Apps cannot access this REST resource - including when utilizing user impersonation.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: &#x27;Admin&#x27; permission for the space.
     * @param body The permission to be created. (required)
     * @param spaceKey The key of the space to be queried for its content. (required)
     * @return ApiResponse&lt;SpacePermissionV2&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<SpacePermissionV2> addPermissionToSpaceWithHttpInfo(Map<String, Object> body, String spaceKey) throws ApiException {
        com.squareup.okhttp.Call call = addPermissionToSpaceValidateBeforeCall(body, spaceKey, null, null);
        Type localVarReturnType = new TypeToken<SpacePermissionV2>(){}.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * Add new permission to space (asynchronously)
     * Adds new permission to space.  If the permission to be added is a group permission, the group can be identified by its group name or group id.  Note: Apps cannot access this REST resource - including when utilizing user impersonation.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: &#x27;Admin&#x27; permission for the space.
     * @param body The permission to be created. (required)
     * @param spaceKey The key of the space to be queried for its content. (required)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call addPermissionToSpaceAsync(Map<String, Object> body, String spaceKey, final ApiCallback<SpacePermissionV2> callback) throws ApiException {

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

        com.squareup.okhttp.Call call = addPermissionToSpaceValidateBeforeCall(body, spaceKey, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<SpacePermissionV2>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
    /**
     * Build call for removePermission
     * @param spaceKey The key of the space to be queried for its content. (required)
     * @param id Id of the permission to be deleted. (required)
     * @param progressListener Progress listener
     * @param progressRequestListener Progress request listener
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public com.squareup.okhttp.Call removePermissionCall(String spaceKey, Integer id, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = null;
        
        // create path and map variables
        String localVarPath = "/wiki/rest/api/space/{spaceKey}/permission/{id}"
            .replaceAll("\\{" + "spaceKey" + "\\}", apiClient.escapeString(spaceKey.toString()))
            .replaceAll("\\{" + "id" + "\\}", apiClient.escapeString(id.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();

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
    private com.squareup.okhttp.Call removePermissionValidateBeforeCall(String spaceKey, Integer id, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        // verify the required parameter 'spaceKey' is set
        if (spaceKey == null) {
            throw new ApiException("Missing the required parameter 'spaceKey' when calling removePermission(Async)");
        }
        // verify the required parameter 'id' is set
        if (id == null) {
            throw new ApiException("Missing the required parameter 'id' when calling removePermission(Async)");
        }
        
        com.squareup.okhttp.Call call = removePermissionCall(spaceKey, id, progressListener, progressRequestListener);
        return call;

        
        
        
        
    }

    /**
     * Remove a space permission
     * Removes a space permission. Note that removing Read Space permission for a user or group will remove all the space permissions for that user or group.  Note: Apps cannot access this REST resource - including when utilizing user impersonation.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: &#x27;Admin&#x27; permission for the space.
     * @param spaceKey The key of the space to be queried for its content. (required)
     * @param id Id of the permission to be deleted. (required)
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public void removePermission(String spaceKey, Integer id) throws ApiException {
        removePermissionWithHttpInfo(spaceKey, id);
    }

    /**
     * Remove a space permission
     * Removes a space permission. Note that removing Read Space permission for a user or group will remove all the space permissions for that user or group.  Note: Apps cannot access this REST resource - including when utilizing user impersonation.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: &#x27;Admin&#x27; permission for the space.
     * @param spaceKey The key of the space to be queried for its content. (required)
     * @param id Id of the permission to be deleted. (required)
     * @return ApiResponse&lt;Void&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<Void> removePermissionWithHttpInfo(String spaceKey, Integer id) throws ApiException {
        com.squareup.okhttp.Call call = removePermissionValidateBeforeCall(spaceKey, id, null, null);
        return apiClient.execute(call);
    }

    /**
     * Remove a space permission (asynchronously)
     * Removes a space permission. Note that removing Read Space permission for a user or group will remove all the space permissions for that user or group.  Note: Apps cannot access this REST resource - including when utilizing user impersonation.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: &#x27;Admin&#x27; permission for the space.
     * @param spaceKey The key of the space to be queried for its content. (required)
     * @param id Id of the permission to be deleted. (required)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call removePermissionAsync(String spaceKey, Integer id, final ApiCallback<Void> callback) throws ApiException {

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

        com.squareup.okhttp.Call call = removePermissionValidateBeforeCall(spaceKey, id, progressListener, progressRequestListener);
        apiClient.executeAsync(call, callback);
        return call;
    }
}