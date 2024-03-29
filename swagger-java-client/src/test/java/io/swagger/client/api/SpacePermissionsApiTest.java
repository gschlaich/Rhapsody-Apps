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

import io.swagger.client.model.SpacePermissionCustomContent;
import io.swagger.client.model.SpacePermissionV2;
import org.junit.Test;
import org.junit.Ignore;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * API tests for SpacePermissionsApi
 */
@Ignore
public class SpacePermissionsApiTest {

    private final SpacePermissionsApi api = new SpacePermissionsApi();

    /**
     * Add new custom content permission to space
     *
     * Adds new custom content permission to space.  If the permission to be added is a group permission, the group can be identified by its group name or group id.  Note: Only apps can access this REST resource and only make changes to the respective app permissions.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: &#x27;Admin&#x27; permission for the space.
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void addCustomContentPermissionsTest() throws Exception {
        SpacePermissionCustomContent body = null;
        String spaceKey = null;
        api.addCustomContentPermissions(body, spaceKey);

        // TODO: test validations
    }
    /**
     * Add new permission to space
     *
     * Adds new permission to space.  If the permission to be added is a group permission, the group can be identified by its group name or group id.  Note: Apps cannot access this REST resource - including when utilizing user impersonation.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: &#x27;Admin&#x27; permission for the space.
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void addPermissionToSpaceTest() throws Exception {
        Map<String, Object> body = null;
        String spaceKey = null;
        SpacePermissionV2 response = api.addPermissionToSpace(body, spaceKey);

        // TODO: test validations
    }
    /**
     * Remove a space permission
     *
     * Removes a space permission. Note that removing Read Space permission for a user or group will remove all the space permissions for that user or group.  Note: Apps cannot access this REST resource - including when utilizing user impersonation.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: &#x27;Admin&#x27; permission for the space.
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void removePermissionTest() throws Exception {
        String spaceKey = null;
        Integer id = null;
        api.removePermission(spaceKey, id);

        // TODO: test validations
    }
}
