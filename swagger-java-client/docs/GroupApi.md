# GroupApi

All URIs are relative to *//your-domain.atlassian.net*

Method | HTTP request | Description
------------- | ------------- | -------------
[**addUserToGroup**](GroupApi.md#addUserToGroup) | **POST** /wiki/rest/api/group/user | Add member to group
[**addUserToGroupByGroupId**](GroupApi.md#addUserToGroupByGroupId) | **POST** /wiki/rest/api/group/userByGroupId | Add member to group by groupId
[**createGroup**](GroupApi.md#createGroup) | **POST** /wiki/rest/api/group | Create new user group
[**getGroupByGroupId**](GroupApi.md#getGroupByGroupId) | **GET** /wiki/rest/api/group/by-id | Get group
[**getGroupByName**](GroupApi.md#getGroupByName) | **GET** /wiki/rest/api/group/{groupName} | Get group
[**getGroupByQueryParam**](GroupApi.md#getGroupByQueryParam) | **GET** /wiki/rest/api/group/by-name | Get group
[**getGroupMembers**](GroupApi.md#getGroupMembers) | **GET** /wiki/rest/api/group/{groupName}/member | Get group members
[**getGroupMembersByGroupId**](GroupApi.md#getGroupMembersByGroupId) | **GET** /wiki/rest/api/group/{groupId}/membersByGroupId | Get group members
[**getGroups**](GroupApi.md#getGroups) | **GET** /wiki/rest/api/group | Get groups
[**getMembersByQueryParam**](GroupApi.md#getMembersByQueryParam) | **GET** /wiki/rest/api/group/member | Get group members
[**removeGroup**](GroupApi.md#removeGroup) | **DELETE** /wiki/rest/api/group | Delete user group
[**removeGroupById**](GroupApi.md#removeGroupById) | **DELETE** /wiki/rest/api/group/by-id | Delete user group
[**removeMemberFromGroup**](GroupApi.md#removeMemberFromGroup) | **DELETE** /wiki/rest/api/group/user | Remove member from group
[**removeMemberFromGroupByGroupId**](GroupApi.md#removeMemberFromGroupByGroupId) | **DELETE** /wiki/rest/api/group/userByGroupId | Remove member from group using group id
[**searchGroups**](GroupApi.md#searchGroups) | **GET** /wiki/rest/api/group/picker | Search groups by partial query

<a name="addUserToGroup"></a>
# **addUserToGroup**
> addUserToGroup(body, name)

Add member to group

Adds a user as a member in a group.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: User must be a site admin.

### Example
```java
// Import classes:
//import io.swagger.client.ApiClient;
//import io.swagger.client.ApiException;
//import io.swagger.client.Configuration;
//import io.swagger.client.auth.*;
//import io.swagger.client.api.GroupApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();
// Configure HTTP basic authorization: basicAuth
HttpBasicAuth basicAuth = (HttpBasicAuth) defaultClient.getAuthentication("basicAuth");
basicAuth.setUsername("YOUR USERNAME");
basicAuth.setPassword("YOUR PASSWORD");

// Configure OAuth2 access token for authorization: oAuthDefinitions
OAuth oAuthDefinitions = (OAuth) defaultClient.getAuthentication("oAuthDefinitions");
oAuthDefinitions.setAccessToken("YOUR ACCESS TOKEN");

GroupApi apiInstance = new GroupApi();
AccountId body = new AccountId(); // AccountId | AccountId of the user who needs to be added as member.
String name = "name_example"; // String | Name of the group whose membership is updated
try {
    apiInstance.addUserToGroup(body, name);
} catch (ApiException e) {
    System.err.println("Exception when calling GroupApi#addUserToGroup");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **body** | [**AccountId**](AccountId.md)| AccountId of the user who needs to be added as member. |
 **name** | **String**| Name of the group whose membership is updated |

### Return type

null (empty response body)

### Authorization

[basicAuth](../README.md#basicAuth)[oAuthDefinitions](../README.md#oAuthDefinitions)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: Not defined

<a name="addUserToGroupByGroupId"></a>
# **addUserToGroupByGroupId**
> addUserToGroupByGroupId(body, groupId)

Add member to group by groupId

Adds a user as a member in a group represented by its groupId  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: User must be a site admin.

### Example
```java
// Import classes:
//import io.swagger.client.ApiClient;
//import io.swagger.client.ApiException;
//import io.swagger.client.Configuration;
//import io.swagger.client.auth.*;
//import io.swagger.client.api.GroupApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();
// Configure HTTP basic authorization: basicAuth
HttpBasicAuth basicAuth = (HttpBasicAuth) defaultClient.getAuthentication("basicAuth");
basicAuth.setUsername("YOUR USERNAME");
basicAuth.setPassword("YOUR PASSWORD");

// Configure OAuth2 access token for authorization: oAuthDefinitions
OAuth oAuthDefinitions = (OAuth) defaultClient.getAuthentication("oAuthDefinitions");
oAuthDefinitions.setAccessToken("YOUR ACCESS TOKEN");

GroupApi apiInstance = new GroupApi();
AccountId body = new AccountId(); // AccountId | AccountId of the user who needs to be added as member.
String groupId = "groupId_example"; // String | GroupId of the group whose membership is updated
try {
    apiInstance.addUserToGroupByGroupId(body, groupId);
} catch (ApiException e) {
    System.err.println("Exception when calling GroupApi#addUserToGroupByGroupId");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **body** | [**AccountId**](AccountId.md)| AccountId of the user who needs to be added as member. |
 **groupId** | **String**| GroupId of the group whose membership is updated |

### Return type

null (empty response body)

### Authorization

[basicAuth](../README.md#basicAuth)[oAuthDefinitions](../README.md#oAuthDefinitions)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: Not defined

<a name="createGroup"></a>
# **createGroup**
> Group createGroup(body)

Create new user group

Creates a new user group.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: User must be a site admin.

### Example
```java
// Import classes:
//import io.swagger.client.ApiClient;
//import io.swagger.client.ApiException;
//import io.swagger.client.Configuration;
//import io.swagger.client.auth.*;
//import io.swagger.client.api.GroupApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();
// Configure HTTP basic authorization: basicAuth
HttpBasicAuth basicAuth = (HttpBasicAuth) defaultClient.getAuthentication("basicAuth");
basicAuth.setUsername("YOUR USERNAME");
basicAuth.setPassword("YOUR PASSWORD");

// Configure OAuth2 access token for authorization: oAuthDefinitions
OAuth oAuthDefinitions = (OAuth) defaultClient.getAuthentication("oAuthDefinitions");
oAuthDefinitions.setAccessToken("YOUR ACCESS TOKEN");

GroupApi apiInstance = new GroupApi();
GroupName body = new GroupName(); // GroupName | Name of the group that is to be created.
try {
    Group result = apiInstance.createGroup(body);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling GroupApi#createGroup");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **body** | [**GroupName**](GroupName.md)| Name of the group that is to be created. |

### Return type

[**Group**](Group.md)

### Authorization

[basicAuth](../README.md#basicAuth)[oAuthDefinitions](../README.md#oAuthDefinitions)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="getGroupByGroupId"></a>
# **getGroupByGroupId**
> Group getGroupByGroupId(id)

Get group

Returns a user group for a given group id.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: Permission to access the Confluence site (&#x27;Can use&#x27; global permission).

### Example
```java
// Import classes:
//import io.swagger.client.ApiClient;
//import io.swagger.client.ApiException;
//import io.swagger.client.Configuration;
//import io.swagger.client.auth.*;
//import io.swagger.client.api.GroupApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();
// Configure HTTP basic authorization: basicAuth
HttpBasicAuth basicAuth = (HttpBasicAuth) defaultClient.getAuthentication("basicAuth");
basicAuth.setUsername("YOUR USERNAME");
basicAuth.setPassword("YOUR PASSWORD");

// Configure OAuth2 access token for authorization: oAuthDefinitions
OAuth oAuthDefinitions = (OAuth) defaultClient.getAuthentication("oAuthDefinitions");
oAuthDefinitions.setAccessToken("YOUR ACCESS TOKEN");

GroupApi apiInstance = new GroupApi();
String id = "id_example"; // String | The id of the group.
try {
    Group result = apiInstance.getGroupByGroupId(id);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling GroupApi#getGroupByGroupId");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **String**| The id of the group. |

### Return type

[**Group**](Group.md)

### Authorization

[basicAuth](../README.md#basicAuth)[oAuthDefinitions](../README.md#oAuthDefinitions)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getGroupByName"></a>
# **getGroupByName**
> Group getGroupByName(groupName)

Get group

Returns a user group for a given group name.  Use updated Get group API  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: Permission to access the Confluence site (&#x27;Can use&#x27; global permission).

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.GroupApi;


GroupApi apiInstance = new GroupApi();
String groupName = "groupName_example"; // String | The name of the group. This is the same as the group name shown in the Confluence administration console.
try {
    Group result = apiInstance.getGroupByName(groupName);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling GroupApi#getGroupByName");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **groupName** | **String**| The name of the group. This is the same as the group name shown in the Confluence administration console. |

### Return type

[**Group**](Group.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getGroupByQueryParam"></a>
# **getGroupByQueryParam**
> Group getGroupByQueryParam(name)

Get group

Returns a user group for a given group name.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: Permission to access the Confluence site (&#x27;Can use&#x27; global permission).

### Example
```java
// Import classes:
//import io.swagger.client.ApiClient;
//import io.swagger.client.ApiException;
//import io.swagger.client.Configuration;
//import io.swagger.client.auth.*;
//import io.swagger.client.api.GroupApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();
// Configure HTTP basic authorization: basicAuth
HttpBasicAuth basicAuth = (HttpBasicAuth) defaultClient.getAuthentication("basicAuth");
basicAuth.setUsername("YOUR USERNAME");
basicAuth.setPassword("YOUR PASSWORD");

// Configure OAuth2 access token for authorization: oAuthDefinitions
OAuth oAuthDefinitions = (OAuth) defaultClient.getAuthentication("oAuthDefinitions");
oAuthDefinitions.setAccessToken("YOUR ACCESS TOKEN");

GroupApi apiInstance = new GroupApi();
String name = "name_example"; // String | The name of the group. This is the same as the group name shown in the Confluence administration console.
try {
    Group result = apiInstance.getGroupByQueryParam(name);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling GroupApi#getGroupByQueryParam");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **name** | **String**| The name of the group. This is the same as the group name shown in the Confluence administration console. |

### Return type

[**Group**](Group.md)

### Authorization

[basicAuth](../README.md#basicAuth)[oAuthDefinitions](../README.md#oAuthDefinitions)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getGroupMembers"></a>
# **getGroupMembers**
> UserArray getGroupMembers(groupName, start, limit)

Get group members

Returns the users that are members of a group.  Use updated Get group API  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: Permission to access the Confluence site (&#x27;Can use&#x27; global permission).

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.GroupApi;


GroupApi apiInstance = new GroupApi();
String groupName = "groupName_example"; // String | The name of the group to be queried for its members.
Integer start = 0; // Integer | The starting index of the returned users.
Integer limit = 200; // Integer | The maximum number of users to return per page. Note, this may be restricted by fixed system limits.
try {
    UserArray result = apiInstance.getGroupMembers(groupName, start, limit);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling GroupApi#getGroupMembers");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **groupName** | **String**| The name of the group to be queried for its members. |
 **start** | **Integer**| The starting index of the returned users. | [optional] [default to 0] [enum: 0]
 **limit** | **Integer**| The maximum number of users to return per page. Note, this may be restricted by fixed system limits. | [optional] [default to 200] [enum: 0]

### Return type

[**UserArray**](UserArray.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getGroupMembersByGroupId"></a>
# **getGroupMembersByGroupId**
> UserArray getGroupMembersByGroupId(groupId, start, limit, shouldReturnTotalSize)

Get group members

Returns the users that are members of a group.  Use updated Get group API  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: Permission to access the Confluence site (&#x27;Can use&#x27; global permission).

### Example
```java
// Import classes:
//import io.swagger.client.ApiClient;
//import io.swagger.client.ApiException;
//import io.swagger.client.Configuration;
//import io.swagger.client.auth.*;
//import io.swagger.client.api.GroupApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();
// Configure HTTP basic authorization: basicAuth
HttpBasicAuth basicAuth = (HttpBasicAuth) defaultClient.getAuthentication("basicAuth");
basicAuth.setUsername("YOUR USERNAME");
basicAuth.setPassword("YOUR PASSWORD");

// Configure OAuth2 access token for authorization: oAuthDefinitions
OAuth oAuthDefinitions = (OAuth) defaultClient.getAuthentication("oAuthDefinitions");
oAuthDefinitions.setAccessToken("YOUR ACCESS TOKEN");

GroupApi apiInstance = new GroupApi();
String groupId = "groupId_example"; // String | The id of the group to be queried for its members.
Integer start = 0; // Integer | The starting index of the returned users.
Integer limit = 200; // Integer | The maximum number of users to return per page. Note, this may be restricted by fixed system limits.
Boolean shouldReturnTotalSize = false; // Boolean | Whether to include total size parameter in the results. Note, fetching total size property is an expensive operation; use it if your use case needs this value.
try {
    UserArray result = apiInstance.getGroupMembersByGroupId(groupId, start, limit, shouldReturnTotalSize);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling GroupApi#getGroupMembersByGroupId");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **groupId** | **String**| The id of the group to be queried for its members. |
 **start** | **Integer**| The starting index of the returned users. | [optional] [default to 0] [enum: 0]
 **limit** | **Integer**| The maximum number of users to return per page. Note, this may be restricted by fixed system limits. | [optional] [default to 200] [enum: 0]
 **shouldReturnTotalSize** | **Boolean**| Whether to include total size parameter in the results. Note, fetching total size property is an expensive operation; use it if your use case needs this value. | [optional] [default to false]

### Return type

[**UserArray**](UserArray.md)

### Authorization

[basicAuth](../README.md#basicAuth)[oAuthDefinitions](../README.md#oAuthDefinitions)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getGroups"></a>
# **getGroups**
> GroupArrayWithLinks getGroups(start, limit, accessType)

Get groups

Returns all user groups. The returned groups are ordered alphabetically in ascending order by group name.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: Permission to access the Confluence site (&#x27;Can use&#x27; global permission).

### Example
```java
// Import classes:
//import io.swagger.client.ApiClient;
//import io.swagger.client.ApiException;
//import io.swagger.client.Configuration;
//import io.swagger.client.auth.*;
//import io.swagger.client.api.GroupApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();
// Configure HTTP basic authorization: basicAuth
HttpBasicAuth basicAuth = (HttpBasicAuth) defaultClient.getAuthentication("basicAuth");
basicAuth.setUsername("YOUR USERNAME");
basicAuth.setPassword("YOUR PASSWORD");

// Configure OAuth2 access token for authorization: oAuthDefinitions
OAuth oAuthDefinitions = (OAuth) defaultClient.getAuthentication("oAuthDefinitions");
oAuthDefinitions.setAccessToken("YOUR ACCESS TOKEN");

GroupApi apiInstance = new GroupApi();
Integer start = 0; // Integer | The starting index of the returned groups.
Integer limit = 200; // Integer | The maximum number of groups to return per page. Note, this may be restricted by fixed system limits.
String accessType = "accessType_example"; // String | The group permission level for which to filter results.
try {
    GroupArrayWithLinks result = apiInstance.getGroups(start, limit, accessType);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling GroupApi#getGroups");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **start** | **Integer**| The starting index of the returned groups. | [optional] [default to 0] [enum: 0]
 **limit** | **Integer**| The maximum number of groups to return per page. Note, this may be restricted by fixed system limits. | [optional] [default to 200] [enum: 0]
 **accessType** | **String**| The group permission level for which to filter results. | [optional] [enum: user, admin, site-admin]

### Return type

[**GroupArrayWithLinks**](GroupArrayWithLinks.md)

### Authorization

[basicAuth](../README.md#basicAuth)[oAuthDefinitions](../README.md#oAuthDefinitions)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getMembersByQueryParam"></a>
# **getMembersByQueryParam**
> UserArray getMembersByQueryParam(name, start, limit, shouldReturnTotalSize)

Get group members

Returns the users that are members of a group.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: Permission to access the Confluence site (&#x27;Can use&#x27; global permission).

### Example
```java
// Import classes:
//import io.swagger.client.ApiClient;
//import io.swagger.client.ApiException;
//import io.swagger.client.Configuration;
//import io.swagger.client.auth.*;
//import io.swagger.client.api.GroupApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();
// Configure HTTP basic authorization: basicAuth
HttpBasicAuth basicAuth = (HttpBasicAuth) defaultClient.getAuthentication("basicAuth");
basicAuth.setUsername("YOUR USERNAME");
basicAuth.setPassword("YOUR PASSWORD");

// Configure OAuth2 access token for authorization: oAuthDefinitions
OAuth oAuthDefinitions = (OAuth) defaultClient.getAuthentication("oAuthDefinitions");
oAuthDefinitions.setAccessToken("YOUR ACCESS TOKEN");

GroupApi apiInstance = new GroupApi();
String name = "name_example"; // String | The name of the group to be queried for its members.
Integer start = 0; // Integer | The starting index of the returned users.
Integer limit = 200; // Integer | The maximum number of users to return per page. Note, this is restricted by fixed system limit of 200 which is to say if the limit parameter exceeds 200, this API will return a maximum of 200 users per page.
Boolean shouldReturnTotalSize = false; // Boolean | Whether to include total size parameter in the results. Note, fetching total size property is an expensive operation; use it if your use case needs this value.
try {
    UserArray result = apiInstance.getMembersByQueryParam(name, start, limit, shouldReturnTotalSize);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling GroupApi#getMembersByQueryParam");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **name** | **String**| The name of the group to be queried for its members. |
 **start** | **Integer**| The starting index of the returned users. | [optional] [default to 0] [enum: 0]
 **limit** | **Integer**| The maximum number of users to return per page. Note, this is restricted by fixed system limit of 200 which is to say if the limit parameter exceeds 200, this API will return a maximum of 200 users per page. | [optional] [default to 200] [enum: 0]
 **shouldReturnTotalSize** | **Boolean**| Whether to include total size parameter in the results. Note, fetching total size property is an expensive operation; use it if your use case needs this value. | [optional] [default to false]

### Return type

[**UserArray**](UserArray.md)

### Authorization

[basicAuth](../README.md#basicAuth)[oAuthDefinitions](../README.md#oAuthDefinitions)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="removeGroup"></a>
# **removeGroup**
> removeGroup(name)

Delete user group

Delete user group.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: User must be a site admin.

### Example
```java
// Import classes:
//import io.swagger.client.ApiClient;
//import io.swagger.client.ApiException;
//import io.swagger.client.Configuration;
//import io.swagger.client.auth.*;
//import io.swagger.client.api.GroupApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();
// Configure HTTP basic authorization: basicAuth
HttpBasicAuth basicAuth = (HttpBasicAuth) defaultClient.getAuthentication("basicAuth");
basicAuth.setUsername("YOUR USERNAME");
basicAuth.setPassword("YOUR PASSWORD");

// Configure OAuth2 access token for authorization: oAuthDefinitions
OAuth oAuthDefinitions = (OAuth) defaultClient.getAuthentication("oAuthDefinitions");
oAuthDefinitions.setAccessToken("YOUR ACCESS TOKEN");

GroupApi apiInstance = new GroupApi();
String name = "name_example"; // String | Name of the group to delete.
try {
    apiInstance.removeGroup(name);
} catch (ApiException e) {
    System.err.println("Exception when calling GroupApi#removeGroup");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **name** | **String**| Name of the group to delete. |

### Return type

null (empty response body)

### Authorization

[basicAuth](../README.md#basicAuth)[oAuthDefinitions](../README.md#oAuthDefinitions)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

<a name="removeGroupById"></a>
# **removeGroupById**
> removeGroupById(id)

Delete user group

Delete user group.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: User must be a site admin.

### Example
```java
// Import classes:
//import io.swagger.client.ApiClient;
//import io.swagger.client.ApiException;
//import io.swagger.client.Configuration;
//import io.swagger.client.auth.*;
//import io.swagger.client.api.GroupApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();
// Configure HTTP basic authorization: basicAuth
HttpBasicAuth basicAuth = (HttpBasicAuth) defaultClient.getAuthentication("basicAuth");
basicAuth.setUsername("YOUR USERNAME");
basicAuth.setPassword("YOUR PASSWORD");

// Configure OAuth2 access token for authorization: oAuthDefinitions
OAuth oAuthDefinitions = (OAuth) defaultClient.getAuthentication("oAuthDefinitions");
oAuthDefinitions.setAccessToken("YOUR ACCESS TOKEN");

GroupApi apiInstance = new GroupApi();
String id = "id_example"; // String | Id of the group to delete.
try {
    apiInstance.removeGroupById(id);
} catch (ApiException e) {
    System.err.println("Exception when calling GroupApi#removeGroupById");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **String**| Id of the group to delete. |

### Return type

null (empty response body)

### Authorization

[basicAuth](../README.md#basicAuth)[oAuthDefinitions](../README.md#oAuthDefinitions)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

<a name="removeMemberFromGroup"></a>
# **removeMemberFromGroup**
> removeMemberFromGroup(name, key, username, accountId)

Remove member from group

Remove user as a member from a group.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: User must be a site admin.

### Example
```java
// Import classes:
//import io.swagger.client.ApiClient;
//import io.swagger.client.ApiException;
//import io.swagger.client.Configuration;
//import io.swagger.client.auth.*;
//import io.swagger.client.api.GroupApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();
// Configure HTTP basic authorization: basicAuth
HttpBasicAuth basicAuth = (HttpBasicAuth) defaultClient.getAuthentication("basicAuth");
basicAuth.setUsername("YOUR USERNAME");
basicAuth.setPassword("YOUR PASSWORD");

// Configure OAuth2 access token for authorization: oAuthDefinitions
OAuth oAuthDefinitions = (OAuth) defaultClient.getAuthentication("oAuthDefinitions");
oAuthDefinitions.setAccessToken("YOUR ACCESS TOKEN");

GroupApi apiInstance = new GroupApi();
String name = "name_example"; // String | Name of the group whose membership is updated.
String key = "key_example"; // String | This parameter is no longer available and will be removed from the documentation soon. Use `accountId` instead. See the [deprecation notice](/cloud/confluence/deprecation-notice-user-privacy-api-migration-guide/) for details.
String username = "username_example"; // String | This parameter is no longer available and will be removed from the documentation soon. Use `accountId` instead. See the [deprecation notice](/cloud/confluence/deprecation-notice-user-privacy-api-migration-guide/) for details.
String accountId = "accountId_example"; // String | The account ID of the user. The accountId uniquely identifies the user across all Atlassian products. For example, `384093:32b4d9w0-f6a5-3535-11a3-9c8c88d10192`.
try {
    apiInstance.removeMemberFromGroup(name, key, username, accountId);
} catch (ApiException e) {
    System.err.println("Exception when calling GroupApi#removeMemberFromGroup");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **name** | **String**| Name of the group whose membership is updated. |
 **key** | **String**| This parameter is no longer available and will be removed from the documentation soon. Use &#x60;accountId&#x60; instead. See the [deprecation notice](/cloud/confluence/deprecation-notice-user-privacy-api-migration-guide/) for details. | [optional]
 **username** | **String**| This parameter is no longer available and will be removed from the documentation soon. Use &#x60;accountId&#x60; instead. See the [deprecation notice](/cloud/confluence/deprecation-notice-user-privacy-api-migration-guide/) for details. | [optional]
 **accountId** | **String**| The account ID of the user. The accountId uniquely identifies the user across all Atlassian products. For example, &#x60;384093:32b4d9w0-f6a5-3535-11a3-9c8c88d10192&#x60;. | [optional]

### Return type

null (empty response body)

### Authorization

[basicAuth](../README.md#basicAuth)[oAuthDefinitions](../README.md#oAuthDefinitions)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

<a name="removeMemberFromGroupByGroupId"></a>
# **removeMemberFromGroupByGroupId**
> removeMemberFromGroupByGroupId(groupId, key, username, accountId)

Remove member from group using group id

Remove user as a member from a group.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: User must be a site admin.

### Example
```java
// Import classes:
//import io.swagger.client.ApiClient;
//import io.swagger.client.ApiException;
//import io.swagger.client.Configuration;
//import io.swagger.client.auth.*;
//import io.swagger.client.api.GroupApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();
// Configure HTTP basic authorization: basicAuth
HttpBasicAuth basicAuth = (HttpBasicAuth) defaultClient.getAuthentication("basicAuth");
basicAuth.setUsername("YOUR USERNAME");
basicAuth.setPassword("YOUR PASSWORD");

// Configure OAuth2 access token for authorization: oAuthDefinitions
OAuth oAuthDefinitions = (OAuth) defaultClient.getAuthentication("oAuthDefinitions");
oAuthDefinitions.setAccessToken("YOUR ACCESS TOKEN");

GroupApi apiInstance = new GroupApi();
String groupId = "groupId_example"; // String | Id of the group whose membership is updated.
String key = "key_example"; // String | This parameter is no longer available and will be removed from the documentation soon. Use `accountId` instead. See the [deprecation notice](/cloud/confluence/deprecation-notice-user-privacy-api-migration-guide/) for details.
String username = "username_example"; // String | This parameter is no longer available and will be removed from the documentation soon. Use `accountId` instead. See the [deprecation notice](/cloud/confluence/deprecation-notice-user-privacy-api-migration-guide/) for details.
String accountId = "accountId_example"; // String | The account ID of the user. The accountId uniquely identifies the user across all Atlassian products. For example, `384093:32b4d9w0-f6a5-3535-11a3-9c8c88d10192`.
try {
    apiInstance.removeMemberFromGroupByGroupId(groupId, key, username, accountId);
} catch (ApiException e) {
    System.err.println("Exception when calling GroupApi#removeMemberFromGroupByGroupId");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **groupId** | **String**| Id of the group whose membership is updated. |
 **key** | **String**| This parameter is no longer available and will be removed from the documentation soon. Use &#x60;accountId&#x60; instead. See the [deprecation notice](/cloud/confluence/deprecation-notice-user-privacy-api-migration-guide/) for details. | [optional]
 **username** | **String**| This parameter is no longer available and will be removed from the documentation soon. Use &#x60;accountId&#x60; instead. See the [deprecation notice](/cloud/confluence/deprecation-notice-user-privacy-api-migration-guide/) for details. | [optional]
 **accountId** | **String**| The account ID of the user. The accountId uniquely identifies the user across all Atlassian products. For example, &#x60;384093:32b4d9w0-f6a5-3535-11a3-9c8c88d10192&#x60;. | [optional]

### Return type

null (empty response body)

### Authorization

[basicAuth](../README.md#basicAuth)[oAuthDefinitions](../README.md#oAuthDefinitions)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

<a name="searchGroups"></a>
# **searchGroups**
> GroupArrayWithLinks searchGroups(query, start, limit, shouldReturnTotalSize)

Search groups by partial query

Get search results of groups by partial query provided.

### Example
```java
// Import classes:
//import io.swagger.client.ApiClient;
//import io.swagger.client.ApiException;
//import io.swagger.client.Configuration;
//import io.swagger.client.auth.*;
//import io.swagger.client.api.GroupApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();
// Configure HTTP basic authorization: basicAuth
HttpBasicAuth basicAuth = (HttpBasicAuth) defaultClient.getAuthentication("basicAuth");
basicAuth.setUsername("YOUR USERNAME");
basicAuth.setPassword("YOUR PASSWORD");

// Configure OAuth2 access token for authorization: oAuthDefinitions
OAuth oAuthDefinitions = (OAuth) defaultClient.getAuthentication("oAuthDefinitions");
oAuthDefinitions.setAccessToken("YOUR ACCESS TOKEN");

GroupApi apiInstance = new GroupApi();
String query = "query_example"; // String | the search term used to query results.
Integer start = 0; // Integer | The starting index of the returned groups.
Integer limit = 200; // Integer | The maximum number of groups to return per page. Note, this is restricted to a maximum limit of 200 groups.
Boolean shouldReturnTotalSize = false; // Boolean | Whether to include total size parameter in the results. Note, fetching total size property is an expensive operation; use it if your use case needs this value.
try {
    GroupArrayWithLinks result = apiInstance.searchGroups(query, start, limit, shouldReturnTotalSize);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling GroupApi#searchGroups");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **query** | **String**| the search term used to query results. |
 **start** | **Integer**| The starting index of the returned groups. | [optional] [default to 0] [enum: 0]
 **limit** | **Integer**| The maximum number of groups to return per page. Note, this is restricted to a maximum limit of 200 groups. | [optional] [default to 200] [enum: 0]
 **shouldReturnTotalSize** | **Boolean**| Whether to include total size parameter in the results. Note, fetching total size property is an expensive operation; use it if your use case needs this value. | [optional] [default to false]

### Return type

[**GroupArrayWithLinks**](GroupArrayWithLinks.md)

### Authorization

[basicAuth](../README.md#basicAuth)[oAuthDefinitions](../README.md#oAuthDefinitions)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

