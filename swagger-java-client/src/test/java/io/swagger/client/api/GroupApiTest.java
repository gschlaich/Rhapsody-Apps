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

import io.swagger.client.model.AccountId;
import io.swagger.client.model.Group;
import io.swagger.client.model.GroupArrayWithLinks;
import io.swagger.client.model.GroupName;
import io.swagger.client.model.UserArray;
import org.junit.Test;
import org.junit.Ignore;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * API tests for GroupApi
 */
@Ignore
public class GroupApiTest {

    private final GroupApi api = new GroupApi();

    /**
     * Add member to group
     *
     * Adds a user as a member in a group.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: User must be a site admin.
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void addUserToGroupTest() throws Exception {
        AccountId body = null;
        String name = null;
        api.addUserToGroup(body, name);

        // TODO: test validations
    }
    /**
     * Add member to group by groupId
     *
     * Adds a user as a member in a group represented by its groupId  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: User must be a site admin.
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void addUserToGroupByGroupIdTest() throws Exception {
        AccountId body = null;
        String groupId = null;
        api.addUserToGroupByGroupId(body, groupId);

        // TODO: test validations
    }
    /**
     * Create new user group
     *
     * Creates a new user group.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: User must be a site admin.
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void createGroupTest() throws Exception {
        GroupName body = null;
        Group response = api.createGroup(body);

        // TODO: test validations
    }
    /**
     * Get group
     *
     * Returns a user group for a given group id.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: Permission to access the Confluence site (&#x27;Can use&#x27; global permission).
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void getGroupByGroupIdTest() throws Exception {
        String id = null;
        Group response = api.getGroupByGroupId(id);

        // TODO: test validations
    }
    /**
     * Get group
     *
     * Returns a user group for a given group name.  Use updated Get group API  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: Permission to access the Confluence site (&#x27;Can use&#x27; global permission).
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void getGroupByNameTest() throws Exception {
        String groupName = null;
        Group response = api.getGroupByName(groupName);

        // TODO: test validations
    }
    /**
     * Get group
     *
     * Returns a user group for a given group name.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: Permission to access the Confluence site (&#x27;Can use&#x27; global permission).
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void getGroupByQueryParamTest() throws Exception {
        String name = null;
        Group response = api.getGroupByQueryParam(name);

        // TODO: test validations
    }
    /**
     * Get group members
     *
     * Returns the users that are members of a group.  Use updated Get group API  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: Permission to access the Confluence site (&#x27;Can use&#x27; global permission).
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void getGroupMembersTest() throws Exception {
        String groupName = null;
        Integer start = null;
        Integer limit = null;
        UserArray response = api.getGroupMembers(groupName, start, limit);

        // TODO: test validations
    }
    /**
     * Get group members
     *
     * Returns the users that are members of a group.  Use updated Get group API  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: Permission to access the Confluence site (&#x27;Can use&#x27; global permission).
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void getGroupMembersByGroupIdTest() throws Exception {
        String groupId = null;
        Integer start = null;
        Integer limit = null;
        Boolean shouldReturnTotalSize = null;
        UserArray response = api.getGroupMembersByGroupId(groupId, start, limit, shouldReturnTotalSize);

        // TODO: test validations
    }
    /**
     * Get groups
     *
     * Returns all user groups. The returned groups are ordered alphabetically in ascending order by group name.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: Permission to access the Confluence site (&#x27;Can use&#x27; global permission).
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void getGroupsTest() throws Exception {
        Integer start = null;
        Integer limit = null;
        String accessType = null;
        GroupArrayWithLinks response = api.getGroups(start, limit, accessType);

        // TODO: test validations
    }
    /**
     * Get group members
     *
     * Returns the users that are members of a group.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: Permission to access the Confluence site (&#x27;Can use&#x27; global permission).
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void getMembersByQueryParamTest() throws Exception {
        String name = null;
        Integer start = null;
        Integer limit = null;
        Boolean shouldReturnTotalSize = null;
        UserArray response = api.getMembersByQueryParam(name, start, limit, shouldReturnTotalSize);

        // TODO: test validations
    }
    /**
     * Delete user group
     *
     * Delete user group.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: User must be a site admin.
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void removeGroupTest() throws Exception {
        String name = null;
        api.removeGroup(name);

        // TODO: test validations
    }
    /**
     * Delete user group
     *
     * Delete user group.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: User must be a site admin.
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void removeGroupByIdTest() throws Exception {
        String id = null;
        api.removeGroupById(id);

        // TODO: test validations
    }
    /**
     * Remove member from group
     *
     * Remove user as a member from a group.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: User must be a site admin.
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void removeMemberFromGroupTest() throws Exception {
        String name = null;
        String key = null;
        String username = null;
        String accountId = null;
        api.removeMemberFromGroup(name, key, username, accountId);

        // TODO: test validations
    }
    /**
     * Remove member from group using group id
     *
     * Remove user as a member from a group.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: User must be a site admin.
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void removeMemberFromGroupByGroupIdTest() throws Exception {
        String groupId = null;
        String key = null;
        String username = null;
        String accountId = null;
        api.removeMemberFromGroupByGroupId(groupId, key, username, accountId);

        // TODO: test validations
    }
    /**
     * Search groups by partial query
     *
     * Get search results of groups by partial query provided.
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void searchGroupsTest() throws Exception {
        String query = null;
        Integer start = null;
        Integer limit = null;
        Boolean shouldReturnTotalSize = null;
        GroupArrayWithLinks response = api.searchGroups(query, start, limit, shouldReturnTotalSize);

        // TODO: test validations
    }
}