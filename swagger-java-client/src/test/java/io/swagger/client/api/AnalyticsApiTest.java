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

import io.swagger.client.model.InlineResponse2002;
import io.swagger.client.model.InlineResponse2003;
import org.junit.Test;
import org.junit.Ignore;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * API tests for AnalyticsApi
 */
@Ignore
public class AnalyticsApiTest {

    private final AnalyticsApi api = new AnalyticsApi();

    /**
     * Get viewers
     *
     * Get the total number of distinct viewers a piece of content has.
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void getViewersTest() throws Exception {
        String contentId = null;
        String fromDate = null;
        InlineResponse2003 response = api.getViewers(contentId, fromDate);

        // TODO: test validations
    }
    /**
     * Get views
     *
     * Get the total number of views a piece of content has.
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void getViewsTest() throws Exception {
        String contentId = null;
        String fromDate = null;
        InlineResponse2002 response = api.getViews(contentId, fromDate);

        // TODO: test validations
    }
}
