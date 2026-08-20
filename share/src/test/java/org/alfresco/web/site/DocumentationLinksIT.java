/*
 * #%L
 * Alfresco Repository
 * %%
 * Copyright (C) 2005 - 2026 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of
 * the paid license agreement will prevail.  Otherwise, the software is
 * provided under the following open source license terms:
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.alfresco.web.site;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Builds every documentation link Share can generate from
 * {@code alfresco/documentationUrl.properties} - one per topic id and, for entries that carry a
 * component placeholder, one per licence edition - and checks each one actually resolves on
 * docs.hyland.com instead of error page.
 * <p>
 * Makes real outbound HTTP calls, so it is opt-in only: run with {@code -Pintegration-tests}.
 */
public class DocumentationLinksIT
{
    private static final Log logger = LogFactory.getLog(DocumentationLinksIT.class);

    private static final String PROPERTIES_RESOURCE = "alfresco/documentationUrl.properties";
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{([^}]+)}");
    private static final Pattern DYNAMIC_KEY_PATTERN = Pattern.compile("(.+)_dynamic$");
    /** Links for a feature that doesn't exist in Community at all - the community variant is expected to be unresolved. */
    private static final List<String> ENTERPRISE_ONLY_TOPICS = List.of("deauthorize.dialog.message", "license-deauthorize");
    /** Links for a feature that doesn't exist in Enterprise at all - the enterprise variant is expected to be unresolved. */
    private static final List<String> COMMUNITY_ONLY_TOPICS = List.of();

    /**
     * docs.hyland.com doesn't 404 an unknown dita:id - it redirects to this "friendly" error page
     * with a 200, so a plain status-code check would never catch a broken topic id.
     */
    private static final String UNRESOLVED_LINK_PATH = "/r/deeplink/unresolved";

    /** Generic component placeholder -> the two edition-specific property keys it stands in for. */
    private static final Map<String, String[]> COMPONENT_VARIANTS = Map.of(
            "acs_component_link", new String[] {"enterprise_link", "community_link"},
            "ags_component_link", new String[] {"enterprise_governance_link", "community_governance_link"});

    @Test
    public void allDocumentationLinksResolve() throws IOException
    {
        Properties props = loadProperties();
        HttpClient client = buildHttpClient();

        List<String> failures = new ArrayList<>();
        for (String key : props.stringPropertyNames())
        {
            Matcher matcher = DYNAMIC_KEY_PATTERN.matcher(key);
            if (!matcher.matches())
            {
                continue;
            }
            String linkName = matcher.group(1);
            String template = props.getProperty(key).trim();
            for (String variant : expandComponentVariants(linkName, template))
            {
                String url = resolvePlaceHolders(variant, props);
                checkUrl(linkName, url, client, failures);
            }
        }

        if (!failures.isEmpty())
        {
            fail("Broken documentation link(s):\n" + String.join("\n", failures));
        }
    }

    @Test
    public void unresolvedLinkRedirectsToErrorPageWithHttp200() throws IOException, InterruptedException
    {
        String bogusUrl = "https://docs.hyland.com/access?dita:id=this-topic-id-does-not-exist&vrm_version=26.2&component=Alfresco%20Content%20Services";
        HttpRequest request = HttpRequest.newBuilder(URI.create(bogusUrl))
                .timeout(Duration.ofSeconds(20))
                .GET()
                .build();
        HttpResponse<Void> response = buildHttpClient().send(request, HttpResponse.BodyHandlers.discarding());

        assertEquals("docs.hyland.com is expected to answer an unknown dita:id with a 200, not a 404",
                200, response.statusCode());
        assertEquals("docs.hyland.com is expected to redirect an unknown dita:id to its unresolved-link error page",
                UNRESOLVED_LINK_PATH, response.uri().getPath());
    }

    private HttpClient buildHttpClient()
    {
        return HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    private Properties loadProperties() throws IOException
    {
        Properties props = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(PROPERTIES_RESOURCE))
        {
            if (in == null)
            {
                throw new IOException("Could not find " + PROPERTIES_RESOURCE + " on the classpath");
            }
            props.load(in);
        }
        return props;
    }

    /**
     * Returns one templated string per licence edition the given link is actually expected to
     * resolve for - skipping the community variant for an {@link #ENTERPRISE_ONLY_TOPICS} link and
     * the enterprise variant for a {@link #COMMUNITY_ONLY_TOPICS} one, since those are known not to
     * exist on docs.hyland.com. Templates without a component placeholder are returned as-is.
     */
    private List<String> expandComponentVariants(String linkName, String template)
    {
        for (Map.Entry<String, String[]> entry : COMPONENT_VARIANTS.entrySet())
        {
            String placeholder = "${" + entry.getKey() + "}";
            if (template.contains(placeholder))
            {
                List<String> variants = new ArrayList<>();
                for (String editionKey : entry.getValue())
                {
                    boolean isCommunityVariant = editionKey.startsWith("community");
                    if (isCommunityVariant && ENTERPRISE_ONLY_TOPICS.contains(linkName))
                    {
                        continue;
                    }
                    if (!isCommunityVariant && COMMUNITY_ONLY_TOPICS.contains(linkName))
                    {
                        continue;
                    }
                    variants.add(template.replace(placeholder, "${" + editionKey + "}"));
                }
                return variants;
            }
        }
        // no component placeholder in this template - nothing to vary, just the one URL
        return List.of(template);
    }

    /** Same recursive ${key} substitution as DocumentUrlResolver, just backed by the raw Properties. */
    private String resolvePlaceHolders(String message, Properties props)
    {
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(message);
        StringBuffer result = new StringBuffer();
        boolean replaced = false;
        while (matcher.find())
        {
            String key = matcher.group(1);
            String value = props.getProperty(key);
            String replacement;
            if (value != null && !value.isEmpty())
            {
                replacement = resolvePlaceHolders(value, props);
                replaced = true;
            }
            else
            {
                replacement = "${" + key + "}";
            }
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);
        return replaced ? resolvePlaceHolders(result.toString(), props) : result.toString();
    }

    private void checkUrl(String linkName, String url, HttpClient client, List<String> failures)
    {
        logger.info("Visiting documentation link: " + url);
        try
        {
            HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                    .timeout(Duration.ofSeconds(20))
                    .GET()
                    .build();
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            if (response.statusCode() >= 400)
            {
                failures.add(linkName + " -> " + url + " returned HTTP " + response.statusCode());
            }
            else if (UNRESOLVED_LINK_PATH.equals(response.uri().getPath()))
            {
                failures.add(linkName + " -> " + url + " redirected to the unresolved-link error page: " + response.uri());
            }
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            failures.add(linkName + " -> " + url + " interrupted: " + e.getMessage());
        }
        catch (IOException | RuntimeException e)
        {
            failures.add(linkName + " -> " + url + " failed: " + e.getMessage());
        }
    }
}
