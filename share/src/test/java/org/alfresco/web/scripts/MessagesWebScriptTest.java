package org.alfresco.web.scripts;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.alfresco.web.site.EditionInfo;
import org.alfresco.web.site.EditionInterceptor;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.surf.util.I18NUtil;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

/**
 * ACS-12729 - the browser-side message resolver knows nothing about the licence edition, so
 * MessagesWebScript has to pick the edition's component link and publish it under the generic
 * key the URL templates reference. Without it every documentation link reaches the browser
 * with ${acs_component_link} still in it, and docs.hyland.com answers 400.
 */
public class MessagesWebScriptTest
{
    private static final Pattern PLACEHOLDER = Pattern.compile("\\$\\{([^}]+)}");

    /** Matches the Alfresco.messages.global["key"] = "value"; assignments emitted by getMessagesSuffix. */
    private static final Pattern ASSIGNMENT =
            Pattern.compile("Alfresco\\.messages\\.global\\[\"([^\"]+)\"\\] = \"((?:[^\"\\\\]|\\\\.)*)\";");

    /** The real bundle - it is both the mocked message source and where the expected values come from. */
    private static final Properties DOC_URLS = loadDocumentationUrls();

    @Test
    public void enterpriseEditionPublishesEnterpriseComponentLinks() throws Exception
    {
        Map<String, String> messages = messagesFor(EditionInfo.ENTERPRISE_EDITION);

        assertEquals(DOC_URLS.getProperty("enterprise_link"), messages.get("acs_component_link"));
        assertEquals(DOC_URLS.getProperty("enterprise_governance_link"), messages.get("ags_component_link"));
    }

    @Test
    public void communityEditionPublishesCommunityComponentLinks() throws Exception
    {
        Map<String, String> messages = messagesFor(EditionInfo.UNKNOWN_EDITION);

        assertEquals(DOC_URLS.getProperty("community_link"), messages.get("acs_component_link"));
        assertEquals(DOC_URLS.getProperty("community_governance_link"), messages.get("ags_component_link"));
    }

    /** Whatever a URL template references has to be a key the browser can look up. */
    @Test
    public void everyPlaceholderAUrlTemplateReferencesIsPublished() throws Exception
    {
        Map<String, String> messages = messagesFor(EditionInfo.ENTERPRISE_EDITION);

        for (String name : DOC_URLS.stringPropertyNames())
        {
            Matcher placeholders = PLACEHOLDER.matcher(DOC_URLS.getProperty(name));
            while (placeholders.find())
            {
                String key = placeholders.group(1);
                assertTrue(name + " references ${" + key + "} but it is not in Alfresco.messages.global,"
                        + " so the browser leaves it unresolved", messages.containsKey(key));
            }
        }
    }

    /**
     * Production serves the bundle through the parent's final generateMessages(String) plus
     * getMessagesSuffix(), never the generateMessages(req,res,locale) override, so the suffix has to
     * publish the enterprise component links or the browser sees ${acs_component_link} verbatim.
     */
    @Test
    public void enterpriseEditionSuffixPublishesEnterpriseComponentLinks() throws Exception
    {
        Map<String, String> assignments = suffixAssignmentsFor(EditionInfo.ENTERPRISE_EDITION);

        assertEquals(DOC_URLS.getProperty("enterprise_link"), assignments.get("acs_component_link"));
        assertEquals(DOC_URLS.getProperty("enterprise_governance_link"), assignments.get("ags_component_link"));
    }

    @Test
    public void communityEditionSuffixPublishesCommunityComponentLinks() throws Exception
    {
        Map<String, String> assignments = suffixAssignmentsFor(EditionInfo.UNKNOWN_EDITION);

        assertEquals(DOC_URLS.getProperty("community_link"), assignments.get("acs_component_link"));
        assertEquals(DOC_URLS.getProperty("community_governance_link"), assignments.get("ags_component_link"));
    }

    /** Runs the webscript against a repository of the given edition and returns Alfresco.messages.global. */
    private Map<String, String> messagesFor(String edition) throws IOException, JSONException
    {
        try (MockedStatic<ThreadLocalRequestContext> context = mockStatic(ThreadLocalRequestContext.class);
             MockedStatic<I18NUtil> i18n = mockStatic(I18NUtil.class))
        {
            RequestContext requestContext = mock(RequestContext.class);
            when(requestContext.getValue(EditionInterceptor.EDITION_INFO)).thenReturn(editionInfo(edition));
            context.when(ThreadLocalRequestContext::getRequestContext).thenReturn(requestContext);
            i18n.when(() -> I18NUtil.getAllMessages(any())).thenReturn(asMap(DOC_URLS));

            WebScriptRequest request = mock(WebScriptRequest.class);
            when(request.getServerPath()).thenReturn("http://localhost:8080");

            String javascript = new MessagesWebScript()
                    .generateMessages(request, mock(WebScriptResponse.class), "en");
            return parseMessagesGlobal(javascript);
        }
    }

    private EditionInfo editionInfo(String edition) throws JSONException
    {
        return new EditionInfo("{\"licenseMode\":\"" + edition + "\",\"licenseHolder\":\"UNKNOWN\"}");
    }

    /** Runs getMessagesSuffix (the production code path) and returns the Alfresco.messages.global[...] assignments. */
    private Map<String, String> suffixAssignmentsFor(String edition) throws IOException, JSONException
    {
        try (MockedStatic<ThreadLocalRequestContext> context = mockStatic(ThreadLocalRequestContext.class);
             MockedStatic<I18NUtil> i18n = mockStatic(I18NUtil.class))
        {
            RequestContext requestContext = mock(RequestContext.class);
            when(requestContext.getValue(EditionInterceptor.EDITION_INFO)).thenReturn(editionInfo(edition));
            context.when(ThreadLocalRequestContext::getRequestContext).thenReturn(requestContext);
            i18n.when(() -> I18NUtil.getAllMessages(any())).thenReturn(asMap(DOC_URLS));

            WebScriptRequest request = mock(WebScriptRequest.class);
            when(request.getServerPath()).thenReturn("http://localhost:8080");

            String javascript = new MessagesWebScript()
                    .getMessagesSuffix(request, mock(WebScriptResponse.class), "en");
            return parseAssignments(javascript);
        }
    }

    private Map<String, String> parseAssignments(String javascript)
    {
        Map<String, String> assignments = new HashMap<>();
        Matcher matcher = ASSIGNMENT.matcher(javascript);
        while (matcher.find())
        {
            assignments.put(matcher.group(1), matcher.group(2));
        }
        return assignments;
    }

    private Map<String, String> parseMessagesGlobal(String javascript)
    {
        String marker = "Alfresco.messages.global = ";
        String json = javascript.substring(javascript.indexOf(marker) + marker.length());
        // JSONTokener reads the one object and ignores whatever the webscript appends after it
        JSONObject parsed = new JSONObject(new JSONTokener(json));

        Map<String, String> messages = new HashMap<>();
        for (String key : parsed.keySet())
        {
            messages.put(key, parsed.getString(key));
        }
        return messages;
    }

    private static Map<String, String> asMap(Properties properties)
    {
        Map<String, String> map = new HashMap<>();
        properties.stringPropertyNames().forEach(name -> map.put(name, properties.getProperty(name)));
        return map;
    }

    private static Properties loadDocumentationUrls()
    {
        Properties properties = new Properties();
        try (InputStream in = MessagesWebScriptTest.class.getClassLoader()
                .getResourceAsStream("alfresco/documentationUrl.properties"))
        {
            if (in == null)
            {
                throw new IllegalStateException("alfresco/documentationUrl.properties is not on the classpath");
            }
            properties.load(in);
        }
        catch (IOException e)
        {
            throw new IllegalStateException("Could not read alfresco/documentationUrl.properties", e);
        }
        return properties;
    }
}
