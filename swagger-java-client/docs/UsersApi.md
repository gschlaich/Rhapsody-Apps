# UsersApi

All URIs are relative to *//your-domain.atlassian.net*

Method | HTTP request | Description
------------- | ------------- | -------------
[**getAnonymousUser**](UsersApi.md#getAnonymousUser) | **GET** /wiki/rest/api/user/anonymous | Get anonymous user
[**getBulkUserLookup**](UsersApi.md#getBulkUserLookup) | **GET** /wiki/rest/api/user/bulk | Get multiple users using ids
[**getBulkUserMigration**](UsersApi.md#getBulkUserMigration) | **GET** /wiki/rest/api/user/bulk/migration | Get user accountIds
[**getCurrentUser**](UsersApi.md#getCurrentUser) | **GET** /wiki/rest/api/user/current | Get current user
[**getGroupMembershipsForUser**](UsersApi.md#getGroupMembershipsForUser) | **GET** /wiki/rest/api/user/memberof | Get group memberships for user
[**getPrivacyUnsafeUserEmail**](UsersApi.md#getPrivacyUnsafeUserEmail) | **GET** /wiki/rest/api/user/email | Get user email address
[**getPrivacyUnsafeUserEmailBulk**](UsersApi.md#getPrivacyUnsafeUserEmailBulk) | **GET** /wiki/rest/api/user/email/bulk | Get user email addresses in batch
[**getUser**](UsersApi.md#getUser) | **GET** /wiki/rest/api/user | Get user

<a name="getAnonymousUser"></a>
# **getAnonymousUser**
> UserAnonymous getAnonymousUser(expand)

Get anonymous user

Returns information about how anonymous users are represented, like the profile picture and display name.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: Permission to access the Confluence site (&#x27;Can use&#x27; global permission).

### Example
```java
// Import classes:
//import io.swagger.client.ApiClient;
//import io.swagger.client.ApiException;
//import io.swagger.client.Configuration;
//import io.swagger.client.auth.*;
//import io.swagger.client.api.UsersApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();
// Configure HTTP basic authorization: basicAuth
HttpBasicAuth basicAuth = (HttpBasicAuth) defaultClient.getAuthentication("basicAuth");
basicAuth.setUsername("YOUR USERNAME");
basicAuth.setPassword("YOUR PASSWORD");

// Configure OAuth2 access token for authorization: oAuthDefinitions
OAuth oAuthDefinitions = (OAuth) defaultClient.getAuthentication("oAuthDefinitions");
oAuthDefinitions.setAccessToken("YOUR ACCESS TOKEN");

UsersApi apiInstance = new UsersApi();
List<String> expand = Arrays.asList("expand_example"); // List<String> | A multi-value parameter indicating which properties of the user to expand.    - `operations` returns the operations that the user is allowed to do.
try {
    UserAnonymous result = apiInstance.getAnonymousUser(expand);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling UsersApi#getAnonymousUser");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **expand** | [**List&lt;String&gt;**](String.md)| A multi-value parameter indicating which properties of the user to expand.    - &#x60;operations&#x60; returns the operations that the user is allowed to do. | [optional] [enum: operations]

### Return type

[**UserAnonymous**](UserAnonymous.md)

### Authorization

[basicAuth](../README.md#basicAuth)[oAuthDefinitions](../README.md#oAuthDefinitions)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getBulkUserLookup"></a>
# **getBulkUserLookup**
> BulkUserLookupArray getBulkUserLookup(accountId, expand, limit)

Get multiple users using ids

Returns user details for the ids provided in request.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: Permission to access the Confluence site (&#x27;Can use&#x27; global permission).

### Example
```java
// Import classes:
//import io.swagger.client.ApiClient;
//import io.swagger.client.ApiException;
//import io.swagger.client.Configuration;
//import io.swagger.client.auth.*;
//import io.swagger.client.api.UsersApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();
// Configure HTTP basic authorization: basicAuth
HttpBasicAuth basicAuth = (HttpBasicAuth) defaultClient.getAuthentication("basicAuth");
basicAuth.setUsername("YOUR USERNAME");
basicAuth.setPassword("YOUR PASSWORD");

// Configure OAuth2 access token for authorization: oAuthDefinitions
OAuth oAuthDefinitions = (OAuth) defaultClient.getAuthentication("oAuthDefinitions");
oAuthDefinitions.setAccessToken("YOUR ACCESS TOKEN");

UsersApi apiInstance = new UsersApi();
String accountId = "accountId_example"; // String | A list of accountId's of users to be returned.
List<String> expand = Arrays.asList("expand_example"); // List<String> | A multi-value parameter indicating which properties of the user to expand.    - `operations` returns the operations that the user is allowed to do.   - personalSpace returns the user's personal space, if it exists.
Integer limit = 56; // Integer | The maximum number of results returned. Currently API returns 200 results max. If more that 200 ids are passed first 200 will be returned.
try {
    BulkUserLookupArray result = apiInstance.getBulkUserLookup(accountId, expand, limit);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling UsersApi#getBulkUserLookup");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **accountId** | **String**| A list of accountId&#x27;s of users to be returned. |
 **expand** | [**List&lt;String&gt;**](String.md)| A multi-value parameter indicating which properties of the user to expand.    - &#x60;operations&#x60; returns the operations that the user is allowed to do.   - personalSpace returns the user&#x27;s personal space, if it exists. | [optional] [enum: operations, personalSpace]
 **limit** | **Integer**| The maximum number of results returned. Currently API returns 200 results max. If more that 200 ids are passed first 200 will be returned. | [optional] [enum: 1, 200]

### Return type

[**BulkUserLookupArray**](BulkUserLookupArray.md)

### Authorization

[basicAuth](../README.md#basicAuth)[oAuthDefinitions](../README.md#oAuthDefinitions)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getBulkUserMigration"></a>
# **getBulkUserMigration**
> MigratedUserArray getBulkUserMigration(key, username, start, limit)

Get user accountIds

Returns the accountIds for the users specified in the key or username parameters. Note that multiple key and username parameters can be specified.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: &#x27;Confluence Administrator&#x27; global permission if specifying a user, otherwise permission to access the Confluence site (&#x27;Can use&#x27; global permission).

### Example
```java
// Import classes:
//import io.swagger.client.ApiClient;
//import io.swagger.client.ApiException;
//import io.swagger.client.Configuration;
//import io.swagger.client.auth.*;
//import io.swagger.client.api.UsersApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();
// Configure HTTP basic authorization: basicAuth
HttpBasicAuth basicAuth = (HttpBasicAuth) defaultClient.getAuthentication("basicAuth");
basicAuth.setUsername("YOUR USERNAME");
basicAuth.setPassword("YOUR PASSWORD");

// Configure OAuth2 access token for authorization: oAuthDefinitions
OAuth oAuthDefinitions = (OAuth) defaultClient.getAuthentication("oAuthDefinitions");
oAuthDefinitions.setAccessToken("YOUR ACCESS TOKEN");

UsersApi apiInstance = new UsersApi();
List<String> key = Arrays.asList("key_example"); // List<String> | The key of a user. To specify multiple users, pass multiple key parameters separated by ampersands. For example, key=mia&key=alana. Required if username isn't provided. Cannot be provided if username is present.
List<String> username = Arrays.asList("username_example"); // List<String> | The username of a user. To specify multiple users, pass multiple username parameters separated by ampersands. For example, username=mia&username=alana. Required if key isn't provided. Cannot be provided if key is present.
Integer start = 0; // Integer | The index of the first item to return in a page of results (page offset).
Integer limit = 200; // Integer | The maximum number of results to return per page. Note, this may be restricted by fixed system limits.
try {
    MigratedUserArray result = apiInstance.getBulkUserMigration(key, username, start, limit);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling UsersApi#getBulkUserMigration");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **key** | [**List&lt;String&gt;**](String.md)| The key of a user. To specify multiple users, pass multiple key parameters separated by ampersands. For example, key&#x3D;mia&amp;key&#x3D;alana. Required if username isn&#x27;t provided. Cannot be provided if username is present. | [optional]
 **username** | [**List&lt;String&gt;**](String.md)| The username of a user. To specify multiple users, pass multiple username parameters separated by ampersands. For example, username&#x3D;mia&amp;username&#x3D;alana. Required if key isn&#x27;t provided. Cannot be provided if key is present. | [optional]
 **start** | **Integer**| The index of the first item to return in a page of results (page offset). | [optional] [default to 0] [enum: 0]
 **limit** | **Integer**| The maximum number of results to return per page. Note, this may be restricted by fixed system limits. | [optional] [default to 200] [enum: 0]

### Return type

[**MigratedUserArray**](MigratedUserArray.md)

### Authorization

[basicAuth](../README.md#basicAuth)[oAuthDefinitions](../README.md#oAuthDefinitions)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getCurrentUser"></a>
# **getCurrentUser**
> User getCurrentUser(expand)

Get current user

Returns the currently logged-in user. This includes information about the user, like the display name, userKey, account ID, profile picture, and more.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: Permission to access the Confluence site (&#x27;Can use&#x27; global permission).

### Example
```java
// Import classes:
//import io.swagger.client.ApiClient;
//import io.swagger.client.ApiException;
//import io.swagger.client.Configuration;
//import io.swagger.client.auth.*;
//import io.swagger.client.api.UsersApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();
// Configure HTTP basic authorization: basicAuth
HttpBasicAuth basicAuth = (HttpBasicAuth) defaultClient.getAuthentication("basicAuth");
basicAuth.setUsername("YOUR USERNAME");
basicAuth.setPassword("YOUR PASSWORD");

// Configure OAuth2 access token for authorization: oAuthDefinitions
OAuth oAuthDefinitions = (OAuth) defaultClient.getAuthentication("oAuthDefinitions");
oAuthDefinitions.setAccessToken("YOUR ACCESS TOKEN");

UsersApi apiInstance = new UsersApi();
List<String> expand = Arrays.asList("expand_example"); // List<String> | A multi-value parameter indicating which properties of the user to expand.    - `operations` returns the operations that the user is allowed to do.   - personalSpace returns the user's personal space, if it exists.   - `isExternalCollaborator` returns whether the user is an external collaborator user
try {
    User result = apiInstance.getCurrentUser(expand);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling UsersApi#getCurrentUser");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **expand** | [**List&lt;String&gt;**](String.md)| A multi-value parameter indicating which properties of the user to expand.    - &#x60;operations&#x60; returns the operations that the user is allowed to do.   - personalSpace returns the user&#x27;s personal space, if it exists.   - &#x60;isExternalCollaborator&#x60; returns whether the user is an external collaborator user | [optional] [enum: operations, personalSpace, isExternalCollaborator]

### Return type

[**User**](User.md)

### Authorization

[basicAuth](../README.md#basicAuth)[oAuthDefinitions](../README.md#oAuthDefinitions)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getGroupMembershipsForUser"></a>
# **getGroupMembershipsForUser**
> GroupArrayWithLinks getGroupMembershipsForUser(accountId, start, limit)

Get group memberships for user

Returns the groups that a user is a member of.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: Permission to access the Confluence site (&#x27;Can use&#x27; global permission).

### Example
```java
// Import classes:
//import io.swagger.client.ApiClient;
//import io.swagger.client.ApiException;
//import io.swagger.client.Configuration;
//import io.swagger.client.auth.*;
//import io.swagger.client.api.UsersApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();
// Configure HTTP basic authorization: basicAuth
HttpBasicAuth basicAuth = (HttpBasicAuth) defaultClient.getAuthentication("basicAuth");
basicAuth.setUsername("YOUR USERNAME");
basicAuth.setPassword("YOUR PASSWORD");

// Configure OAuth2 access token for authorization: oAuthDefinitions
OAuth oAuthDefinitions = (OAuth) defaultClient.getAuthentication("oAuthDefinitions");
oAuthDefinitions.setAccessToken("YOUR ACCESS TOKEN");

UsersApi apiInstance = new UsersApi();
String accountId = "accountId_example"; // String | The account ID of the user. The accountId uniquely identifies the user across all Atlassian products. For example, `384093:32b4d9w0-f6a5-3535-11a3-9c8c88d10192`.
Integer start = 0; // Integer | The starting index of the returned groups.
Integer limit = 200; // Integer | The maximum number of groups to return per page. Note, this may be restricted by fixed system limits.
try {
    GroupArrayWithLinks result = apiInstance.getGroupMembershipsForUser(accountId, start, limit);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling UsersApi#getGroupMembershipsForUser");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **accountId** | **String**| The account ID of the user. The accountId uniquely identifies the user across all Atlassian products. For example, &#x60;384093:32b4d9w0-f6a5-3535-11a3-9c8c88d10192&#x60;. |
 **start** | **Integer**| The starting index of the returned groups. | [optional] [default to 0] [enum: 0]
 **limit** | **Integer**| The maximum number of groups to return per page. Note, this may be restricted by fixed system limits. | [optional] [default to 200] [enum: 0]

### Return type

[**GroupArrayWithLinks**](GroupArrayWithLinks.md)

### Authorization

[basicAuth](../README.md#basicAuth)[oAuthDefinitions](../README.md#oAuthDefinitions)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getPrivacyUnsafeUserEmail"></a>
# **getPrivacyUnsafeUserEmail**
> AccountIdEmailRecord getPrivacyUnsafeUserEmail(accountId)

Get user email address

Returns a user&#x27;s email address. This API is only available to apps approved by Atlassian, according to these [guidelines](https://community.developer.atlassian.com/t/guidelines-for-requesting-access-to-email-address/27603).  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: Permission to access the Confluence site (&#x27;Can use&#x27; global permission).

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.UsersApi;


UsersApi apiInstance = new UsersApi();
String accountId = "accountId_example"; // String | The account ID of the user, which uniquely identifies the user across all Atlassian products. For example, `384093:32b4d9w0-f6a5-3535-11a3-9c8c88d10192`. Required.
try {
    AccountIdEmailRecord result = apiInstance.getPrivacyUnsafeUserEmail(accountId);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling UsersApi#getPrivacyUnsafeUserEmail");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **accountId** | **String**| The account ID of the user, which uniquely identifies the user across all Atlassian products. For example, &#x60;384093:32b4d9w0-f6a5-3535-11a3-9c8c88d10192&#x60;. Required. |

### Return type

[**AccountIdEmailRecord**](AccountIdEmailRecord.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getPrivacyUnsafeUserEmailBulk"></a>
# **getPrivacyUnsafeUserEmailBulk**
> AccountIdEmailRecordArray getPrivacyUnsafeUserEmailBulk(accountId)

Get user email addresses in batch

Returns user email addresses for a set of accountIds. This API is only available to apps approved by Atlassian, according to these [guidelines](https://community.developer.atlassian.com/t/guidelines-for-requesting-access-to-email-address/27603).  Any accounts which are not available will not be included in the result.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: Permission to access the Confluence site (&#x27;Can use&#x27; global permission).

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.UsersApi;


UsersApi apiInstance = new UsersApi();
List<String> accountId = Arrays.asList("accountId_example"); // List<String> | The account IDs of the users.
try {
    AccountIdEmailRecordArray result = apiInstance.getPrivacyUnsafeUserEmailBulk(accountId);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling UsersApi#getPrivacyUnsafeUserEmailBulk");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **accountId** | [**List&lt;String&gt;**](String.md)| The account IDs of the users. |

### Return type

[**AccountIdEmailRecordArray**](AccountIdEmailRecordArray.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getUser"></a>
# **getUser**
> User getUser(accountId, expand)

Get user

Returns a user. This includes information about the user, such as the display name, account ID, profile picture, and more. The information returned may be restricted by the user&#x27;s profile visibility settings.  **Note:** to add, edit, or delete users in your organization, see the [user management REST API](/cloud/admin/user-management/about/).  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: Permission to access the Confluence site (&#x27;Can use&#x27; global permission).

### Example
```java
// Import classes:
//import io.swagger.client.ApiClient;
//import io.swagger.client.ApiException;
//import io.swagger.client.Configuration;
//import io.swagger.client.auth.*;
//import io.swagger.client.api.UsersApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();
// Configure HTTP basic authorization: basicAuth
HttpBasicAuth basicAuth = (HttpBasicAuth) defaultClient.getAuthentication("basicAuth");
basicAuth.setUsername("YOUR USERNAME");
basicAuth.setPassword("YOUR PASSWORD");

// Configure OAuth2 access token for authorization: oAuthDefinitions
OAuth oAuthDefinitions = (OAuth) defaultClient.getAuthentication("oAuthDefinitions");
oAuthDefinitions.setAccessToken("YOUR ACCESS TOKEN");

UsersApi apiInstance = new UsersApi();
String accountId = "accountId_example"; // String | The account ID of the user. The accountId uniquely identifies the user across all Atlassian products. For example, `384093:32b4d9w0-f6a5-3535-11a3-9c8c88d10192`.
List<String> expand = Arrays.asList("expand_example"); // List<String> | A multi-value parameter indicating which properties of the user to expand.    - `operations` returns the operations that the user is allowed to do.   - personalSpace returns the user's personal space, if it exists.   - `isExternalCollaborator` returns whether the user is an external collaborator user
try {
    User result = apiInstance.getUser(accountId, expand);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling UsersApi#getUser");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **accountId** | **String**| The account ID of the user. The accountId uniquely identifies the user across all Atlassian products. For example, &#x60;384093:32b4d9w0-f6a5-3535-11a3-9c8c88d10192&#x60;. |
 **expand** | [**List&lt;String&gt;**](String.md)| A multi-value parameter indicating which properties of the user to expand.    - &#x60;operations&#x60; returns the operations that the user is allowed to do.   - personalSpace returns the user&#x27;s personal space, if it exists.   - &#x60;isExternalCollaborator&#x60; returns whether the user is an external collaborator user | [optional] [enum: operations, personalSpace, isExternalCollaborator]

### Return type

[**User**](User.md)

### Authorization

[basicAuth](../README.md#basicAuth)[oAuthDefinitions](../README.md#oAuthDefinitions)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

