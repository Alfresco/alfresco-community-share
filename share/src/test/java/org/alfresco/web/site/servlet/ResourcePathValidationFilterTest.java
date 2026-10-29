/*
 * Copyright 2005 - 2025 Alfresco Software Limited.
 *
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of the paid license agreement will prevail.
 * Otherwise, the software is provided under the following open source license terms:
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
 */
package org.alfresco.web.site.servlet;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.FilterChain;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.Test;

public class ResourcePathValidationFilterTest
{
    private final ResourcePathValidationFilter filter = new ResourcePathValidationFilter();

    @Test
    public void shouldBlockTraversal() throws Exception
    {
        assertBlocked("/share/page/resource/..;/WEB-INF/web.xml");
        assertBlocked("/share/res/..;/WEB-INF/web.xml");
        assertBlocked("/share/p/resource/..;/WEB-INF/web.xml");
        assertBlocked("/share/res/../WEB-INF/web.xml");
        assertBlocked("/share/res/%2e%2e/WEB-INF/web.xml");
    }

    /**
     * Variants of the "..;" bypass and other encodings that collapse to a dot-only path segment.
     */
    @Test
    public void shouldBlockTraversalSegmentVariants() throws Exception
    {
        assertBlocked("/share/res/..;jsessionid=1234/WEB-INF/web.xml");
        assertBlocked("/share/res/..;foo=bar/WEB-INF/web.xml");
        assertBlocked("/share/res/%2e%2e;/WEB-INF/web.xml");
        assertBlocked("/share/res/.%2e/WEB-INF/web.xml");
        assertBlocked("/share/res/%2e./WEB-INF/web.xml");
        assertBlocked("/share/res/..%2fWEB-INF%2fweb.xml");
        assertBlocked("/share/res/..%5cWEB-INF%5cweb.xml");
        assertBlocked("/share/res/..%00/WEB-INF/web.xml");
        assertBlocked("/share/res/..../WEB-INF/web.xml");
        assertBlocked("/share/res/images/../../WEB-INF/web.xml");
        assertBlocked("/share/res\\..\\WEB-INF\\web.xml");
    }

    /**
     * A malformed %-escape cannot be canonicalised, so the request must fail closed rather than
     * propagate an IllegalArgumentException out of the filter.
     */
    @Test
    public void shouldBlockMalformedEscapes() throws Exception
    {
        assertBlocked("/share/res/%u002e%u002e/WEB-INF/web.xml");
        assertBlocked("/share/res/%zz/web.xml");
        assertBlocked("/share/res/%2/web.xml");
    }

    /**
     * Traversal is detected per segment, so file names containing consecutive dots must still be
     * served. Repository content frequently uses such names.
     */
    @Test
    public void shouldNotBlockConsecutiveDotsInsideSegments() throws Exception
    {
        assertAllowed("/share/proxy/alfresco/api/node/content/workspace/SpacesStore/abc-123/report..final.pdf");
        assertAllowed("/share/proxy/alfresco/api/node/content/workspace/SpacesStore/abc-123/v1..2.docx");
        assertAllowed("/share/proxy/alfresco/api/node/content/workspace/SpacesStore/abc-123/..hidden.txt");
        assertAllowed("/share/res/components/documentlibrary/doclist..min.js");
    }

    @Test
    public void shouldBlockProtectedLocations() throws Exception
    {
        assertBlocked("/share/res/web-inf/web.xml");
        assertBlocked("/share/res/WEB-INF/classes/alfresco/share-config.xml");
        assertBlocked("/share/res/META-INF/MANIFEST.MF");
    }

    @Test
    public void shouldBlockProtectedExtensionsOnResourcePaths() throws Exception
    {
        assertBlocked("/share/res/messages/webscripts.properties");
        assertBlocked("/share/page/resource/surf.xml");
        assertBlocked("/share/res/templates/page.ftl");
        assertBlocked("/share/p/resource/foo.class");
    }

    /**
     * Resource routes are validated against an allow list, so any asset type that is not explicitly
     * served by Share is refused even though it appears on no block list.
     */
    @Test
    public void shouldBlockUnlistedResourceTypes() throws Exception
    {
        assertBlocked("/share/res/components/preview/pdfjs/cmaps/LICENSE");
        assertBlocked("/share/res/modules/editors/tinymce/license.txt");
        assertBlocked("/share/res/modules/editors/tinymce/langs/readme.md");
        assertBlocked("/share/res/yui/swfplayer/assets/as/SWFPlayer.as");
        assertBlocked("/share/res/themes/greenTheme/yui/assets/sprite.psd");
        assertBlocked("/share/res/config/alfresco-global.conf");
        assertBlocked("/share/res/keystore/ssl.keystore");
        assertBlocked("/share/res/backup/share-config.bak");
        assertBlocked("/share/res/components");
    }

    /**
     * Windows discards trailing dots and spaces, and NTFS exposes file contents through the ::$DATA
     * stream, so neither may be used to disguise a type that is not on the allow list.
     */
    @Test
    public void shouldBlockDisguisedResourceExtensions() throws Exception
    {
        assertBlocked("/share/page/resource/surf.xml.");
        assertBlocked("/share/page/resource/surf.xml%20");
        assertBlocked("/share/res/messages/webscripts.properties.");
        assertBlocked("/share/res/components/documentlibrary/documentlist.js::$DATA");
    }

    /**
     * Every asset type Share actually serves from the webapp root must remain available.
     */
    @Test
    public void shouldAllowServedResourceTypes() throws Exception
    {
        assertAllowed("/share/res/components/documentlibrary/documentlist.js");
        assertAllowed("/share/res/css/base.css");
        assertAllowed("/share/res/images/filetypes/generic-file-32.png");
        assertAllowed("/share/res/themes/default/images/sprite.gif");
        assertAllowed("/share/res/components/images/help.svg");
        assertAllowed("/share/res/favicon.ico");
        assertAllowed("/share/res/components/preview/pdfjs/cmaps/Adobe-Japan1-UCS2.bcmap");
        assertAllowed("/share/res/components/preview/pdfjs/wasm/openjpeg.wasm");
        assertAllowed("/share/res/components/preview/WebPreviewer.swf");
        assertAllowed("/share/res/modules/editors/tinymce/skins/ui/oxide/skin.ts");
        assertAllowed("/share/res/js/lib/dojo/dojo.js.map");
        assertAllowed("/share/res/js/alfresco/cmm/forms/controls/templates/ColorPickerDropDown.html");
        assertAllowed("/share/res/ctools/SITE_CONTENT_REPORT-MOCKUP-DATA.json");
        assertAllowed("/share/res/yui/assets/skins/default/fonts/font.woff");
    }

    /**
     * The filter is mapped to /*, so the extension blocklist must not affect repository content or
     * application URLs that merely end with one of those extensions.
     */
    @Test
    public void shouldNotBlockProtectedExtensionsOutsideResourcePaths() throws Exception
    {
        assertAllowed("/share/proxy/alfresco/api/node/content/workspace/SpacesStore/abc-123/myReport.xml");
        assertAllowed("/share/proxy/alfresco/slingshot/node/content/workspace/SpacesStore/abc-123/config.properties");
        assertAllowed("/share/service/components/documentlibrary/data.xml");
        assertAllowed("/share/index.jsp");
    }

    /**
     * Repository paths may legitimately contain the protected-resource fragments, so those fragments
     * must only be enforced on resource routes. See the Alfresco data-dictionary API used by the
     * property picker, rule configuration and Model Manager.
     */
    @Test
    public void shouldNotBlockProtectedFragmentsOutsideResourcePaths() throws Exception
    {
        assertAllowed("/share/proxy/alfresco/api/classes/cm_content/properties");
        assertAllowed("/share/proxy/alfresco/api/classes/cm_folder/subclasses");
        assertAllowed("/share/proxy/alfresco/api/classes/cm_content/subclasses");
        assertAllowed("/share/proxy/alfresco/api/node/content/workspace/SpacesStore/abc-123/web-info.pdf");
        assertAllowed("/share/proxy/alfresco/api/node/content/workspace/SpacesStore/abc-123/meta-info.docx");
    }

    /**
     * Traversal protection must remain unconditional, including on non-resource routes.
     */
    @Test
    public void shouldBlockTraversalOutsideResourcePaths() throws Exception
    {
        assertBlocked("/share/proxy/alfresco/api/../../WEB-INF/web.xml");
        assertBlocked("/share/service/../WEB-INF/classes/log4j.properties");
        assertBlocked("/share/proxy/alfresco/api/classes/..;/WEB-INF/web.xml");
    }

    /**
     * Tomcat resolves "../" segments before the filter chain runs, so the filter must also reject the
     * already-normalised result. "/share/proxy/alfresco/api/../../WEB-INF/web.xml" arrives as
     * "/share/proxy/WEB-INF/web.xml", which is not a resource route.
     */
    @Test
    public void shouldBlockContainerNormalisedProtectedLocations() throws Exception
    {
        assertBlocked("/share/proxy/WEB-INF/web.xml");
        assertBlocked("/share/proxy/alfresco/WEB-INF/classes/log4j.properties");
        assertBlocked("/share/service/META-INF/MANIFEST.MF");
        assertBlocked("/share/js/WEB-INF/web.xml");
        assertBlocked("/share/WEB-INF/web.xml");
        assertBlocked("/share/META-INF/MANIFEST.MF");
    }

    /**
     * The protected-resource fragments must still be enforced on the resource routes themselves.
     */
    @Test
    public void shouldStillBlockProtectedFragmentsOnResourcePaths() throws Exception
    {
        assertBlocked("/share/res/WEB-INF/web.xml");
        assertBlocked("/share/page/resource/META-INF/MANIFEST.MF");
        assertBlocked("/share/res/classes/alfresco/share-config.xml");
    }

    /**
     * The filter runs before the authentication filters, so no single sign-on entry point may be
     * refused. Covers the AIMS/Keycloak login and logout endpoints, the unauthenticated proxy used
     * during SSO, and the ";jsessionid" URL rewriting the container applies when cookies are
     * unavailable.
     */
    @Test
    public void shouldNotBlockSingleSignOnEntryPoints() throws Exception
    {
        assertAllowed("/share/page/aims-dologin");
        assertAllowed("/share/page/aims/logout");
        assertAllowed("/share/page/dologin");
        assertAllowed("/share/proxy/alfresco-noauth/api/solr/stats");
        assertAllowed("/share/page/site/swsdp/documentlibrary;jsessionid=A1B2C3D4");
        assertAllowed("/share/res/css/base.css;jsessionid=A1B2C3D4");
        assertAllowed("/share/res/components/documentlibrary/documentlist.js;jsessionid=A1B2C3D4");
        assertAllowed("/share/page/proxy/alfresco/api/classes/cm_content/properties;jsessionid=A1B2C3D4");
    }

    /**
     * An INCLUDE dispatch carries the included target in the dispatcher attributes while
     * getRequestURI() still returns the calling page, so the target must be the value validated.
     */
    @Test
    public void shouldValidateIncludeTargetRatherThanCallingRequest() throws Exception
    {
        verify(doInclude("/share/page/site/swsdp/documentlibrary", "/share/page/resource/surf.xml"))
                .sendError(anyInt(), anyString());
        verify(doInclude("/share/page/site/swsdp/documentlibrary", "/share/res/..;/WEB-INF/web.xml"))
                .sendError(anyInt(), anyString());
        verify(doInclude("/share/page/site/swsdp/documentlibrary", "/share/res/css/base.css"), never())
                .sendError(anyInt(), anyString());
    }

    /**
     * Servlet routing ignores path parameters, so a parameterised prefix must not hide the route a
     * request will actually be dispatched to. "/page;jsessionid=abc/resource/surf.xml" reaches the
     * resource handler and must therefore be validated as a resource request.
     */
    @Test
    public void shouldClassifyResourceRoutesIgnoringPathParameters() throws Exception
    {
        assertBlocked("/share/page;jsessionid=abc/resource/surf.xml");
        assertBlocked("/share/p;jsessionid=abc/resource/surf.xml");
        assertBlocked("/share/res;jsessionid=abc/messages/webscripts.properties");
        assertBlocked("/share/page/resource;jsessionid=abc/surf.xml");
        assertBlocked("/share;jsessionid=abc/res/templates/page.ftl");
        assertAllowed("/share/page;jsessionid=abc/resource/widget.js");
    }

    /**
     * The container strips path parameters from the encoded URI before decoding it, so an encoded
     * separator inside a parameter value must not be able to shift the apparent route.
     */
    @Test
    public void shouldStripPathParametersBeforeDecoding() throws Exception
    {
        assertBlocked("/share/page;x=%2fignored/resource/surf.xml");
        assertBlocked("/share/p;x=%2f%2fignored/resource/surf.xml");
        assertBlocked("/share/res;x=%2fignored/messages/webscripts.properties");
        assertAllowed("/share/page;x=%2fignored/resource/widget.js");
    }

    /**
     * getContextPath() is not decoded by the container, so it must be decoded before it is compared
     * with the decoded request URI, or the context path would never be stripped and the request
     * would not be recognised as a resource request.
     */
    @Test
    public void shouldDecodeContextPathBeforeClassification() throws Exception
    {
        verify(doFilter("/sh%61re/page/resource/surf.xml", "/sh%61re")).sendError(anyInt(), anyString());
        verify(doFilter("/sh%61re/res/messages/webscripts.properties", "/sh%61re")).sendError(anyInt(), anyString());
        verify(doFilter("/sh%61re/res/css/base.css", "/sh%61re"), never()).sendError(anyInt(), anyString());
    }

    /**
     * Tomcat collapses repeated separators when routing, so they must not hide the resource route.
     */
    @Test
    public void shouldCollapseRepeatedSeparatorsBeforeClassification() throws Exception
    {
        assertBlocked("/share/page//resource/surf.xml");
        assertBlocked("/share/p//resource/surf.xml");
        assertBlocked("/share//res//messages/webscripts.properties");
        assertBlocked("/share/res///templates/page.ftl");
        assertAllowed("/share/page//resource/widget.js");
    }

    /**
     * The context path must only be stripped on a segment boundary.
     */
    @Test
    public void shouldNotStripContextPathFromUnrelatedPrefix() throws Exception
    {
        verify(doFilter("/sharepoint/res/css/base.css", "/share"), never()).sendError(anyInt(), anyString());
    }

    @Test
    public void shouldAllowLegitimateResources() throws Exception
    {
        assertAllowed("/share/res/components/documentlibrary/documentlist.js");
        assertAllowed("/share/res/css/base.css");
        assertAllowed("/share/page/user/admin/dashboard");
        assertAllowed("/share/page/site/swsdp/documentlibrary");
    }

    private void assertBlocked(String uri) throws Exception
    {
        verify(doFilter(uri)).sendError(anyInt(), anyString());
    }

    private void assertAllowed(String uri) throws Exception
    {
        verify(doFilter(uri), never()).sendError(anyInt(), anyString());
    }

    private HttpServletResponse doFilter(String uri) throws Exception
    {
        return doFilter(uri, "/share");
    }

    private HttpServletResponse doFilter(String uri, String contextPath) throws Exception
    {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getRequestURI()).thenReturn(uri);
        when(request.getContextPath()).thenReturn(contextPath);

        filter.doFilter(request, response, chain);
        return response;
    }

    private HttpServletResponse doInclude(String callingUri, String includedUri) throws Exception
    {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getRequestURI()).thenReturn(callingUri);
        when(request.getContextPath()).thenReturn("/share");
        when(request.getAttribute(RequestDispatcher.INCLUDE_REQUEST_URI)).thenReturn(includedUri);
        when(request.getAttribute(RequestDispatcher.INCLUDE_CONTEXT_PATH)).thenReturn("/share");

        filter.doFilter(request, response, chain);
        return response;
    }
}

