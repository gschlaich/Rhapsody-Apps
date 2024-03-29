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

import io.swagger.client.model.BlueprintTemplateArray;
import io.swagger.client.model.ContentTemplate;
import io.swagger.client.model.ContentTemplateArray;
import org.junit.Test;
import org.junit.Ignore;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * API tests for TemplateApi
 */
@Ignore
public class TemplateApiTest {

    private final TemplateApi api = new TemplateApi();

    /**
     * Create content template
     *
     * Creates a new content template. Note, blueprint templates cannot be created via the REST API.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: &#x27;Admin&#x27; permission for the space to create a space template or &#x27;Confluence Administrator&#x27; global permission to create a global template.
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void createContentTemplateTest() throws Exception {
        Map<String, Object> body = null;
        ContentTemplate response = api.createContentTemplate(body);

        // TODO: test validations
    }
    /**
     * Get blueprint templates
     *
     * Returns all templates provided by blueprints. Use this method to retrieve all global blueprint templates or all blueprint templates in a space.  Note, all global blueprints are inherited by each space. Space blueprints can be customised without affecting the global blueprints.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: &#x27;View&#x27; permission for the space to view blueprints for the space and permission to access the Confluence site (&#x27;Can use&#x27; global permission) to view global blueprints.
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void getBlueprintTemplatesTest() throws Exception {
        String spaceKey = null;
        Integer start = null;
        Integer limit = null;
        List<String> expand = null;
        BlueprintTemplateArray response = api.getBlueprintTemplates(spaceKey, start, limit, expand);

        // TODO: test validations
    }
    /**
     * Get content template
     *
     * Returns a content template. This includes information about template, like the name, the space or blueprint that the template is in, the body of the template, and more.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: &#x27;View&#x27; permission for the space to view space templates and permission to access the Confluence site (&#x27;Can use&#x27; global permission) to view global templates.
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void getContentTemplateTest() throws Exception {
        String contentTemplateId = null;
        ContentTemplate response = api.getContentTemplate(contentTemplateId);

        // TODO: test validations
    }
    /**
     * Get content templates
     *
     * Returns all content templates. Use this method to retrieve all global content templates or all content templates in a space.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: &#x27;View&#x27; permission for the space to view space templates and permission to access the Confluence site (&#x27;Can use&#x27; global permission) to view global templates.
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void getContentTemplatesTest() throws Exception {
        String spaceKey = null;
        Integer start = null;
        Integer limit = null;
        List<String> expand = null;
        ContentTemplateArray response = api.getContentTemplates(spaceKey, start, limit, expand);

        // TODO: test validations
    }
    /**
     * Remove template
     *
     * Deletes a template. This results in different actions depending on the type of template:  - If the template is a content template, it is deleted. - If the template is a modified space-level blueprint template, it reverts to the template inherited from the global-level blueprint template. - If the template is a modified global-level blueprint template, it reverts to the default global-level blueprint template.   Note, unmodified blueprint templates cannot be deleted.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**:         &#x27;Admin&#x27; permission for the space to delete a space template or &#x27;Confluence Administrator&#x27;         global permission to delete a global template.
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void removeTemplateTest() throws Exception {
        String contentTemplateId = null;
        api.removeTemplate(contentTemplateId);

        // TODO: test validations
    }
    /**
     * Update content template
     *
     * Updates a content template. Note, blueprint templates cannot be updated via the REST API.  **[Permissions](https://confluence.atlassian.com/x/_AozKw) required**: &#x27;Admin&#x27; permission for the space to update a space template or &#x27;Confluence Administrator&#x27; global permission to update a global template.
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void updateContentTemplateTest() throws Exception {
        Map<String, Object> body = null;
        ContentTemplate response = api.updateContentTemplate(body);

        // TODO: test validations
    }
}
