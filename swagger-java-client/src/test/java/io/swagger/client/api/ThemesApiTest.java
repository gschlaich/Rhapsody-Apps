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

import io.swagger.client.model.Theme;
import io.swagger.client.model.ThemeArray;
import io.swagger.client.model.ThemeUpdate;
import org.junit.Test;
import org.junit.Ignore;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * API tests for ThemesApi
 */
@Ignore
public class ThemesApiTest {

    private final ThemesApi api = new ThemesApi();

    /**
     * Get global theme
     *
     * Returns the globally assigned theme.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: None
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void getGlobalThemeTest() throws Exception {
        Theme response = api.getGlobalTheme();

        // TODO: test validations
    }
    /**
     * Get space theme
     *
     * Returns the theme selected for a space, if one is set. If no space theme is set, this means that the space is inheriting the global look and feel settings.  **[Permissions required](https://confluence.atlassian.com/x/_AozKw)**: ‘View’ permission for the space.
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void getSpaceThemeTest() throws Exception {
        String spaceKey = null;
        Theme response = api.getSpaceTheme(spaceKey);

        // TODO: test validations
    }
    /**
     * Get theme
     *
     * Returns a theme. This includes information about the theme name, description, and icon.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: None
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void getThemeTest() throws Exception {
        String themeKey = null;
        Theme response = api.getTheme(themeKey);

        // TODO: test validations
    }
    /**
     * Get themes
     *
     * Returns all themes, not including the default theme.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: None
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void getThemesTest() throws Exception {
        Integer start = null;
        Integer limit = null;
        ThemeArray response = api.getThemes(start, limit);

        // TODO: test validations
    }
    /**
     * Reset space theme
     *
     * Resets the space theme. This means that the space will inherit the global look and feel settings  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: &#x27;Admin&#x27; permission for the space.
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void resetSpaceThemeTest() throws Exception {
        String spaceKey = null;
        api.resetSpaceTheme(spaceKey);

        // TODO: test validations
    }
    /**
     * Set space theme
     *
     * Sets the theme for a space. Note, if you want to reset the space theme to the default Confluence theme, use the &#x27;Reset space theme&#x27; method instead of this method.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: &#x27;Admin&#x27; permission for the space.
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void setSpaceThemeTest() throws Exception {
        ThemeUpdate body = null;
        String spaceKey = null;
        Theme response = api.setSpaceTheme(body, spaceKey);

        // TODO: test validations
    }
}