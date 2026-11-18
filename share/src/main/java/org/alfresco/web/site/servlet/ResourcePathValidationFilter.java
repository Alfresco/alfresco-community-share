package org.alfresco.web.site.servlet;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

public class ResourcePathValidationFilter implements Filter {

    private static final Log LOGGER = LogFactory.getLog(ResourcePathValidationFilter.class);
    private static final List<String> RESOURCE_PATH_PREFIXES = List.of("/res/", "/page/resource/", "/p/resource/");

    private static final String PATH_SEPARATORS = "/\\";

    private static final List<String> BLOCKED_SEGMENTS = List.of("web-inf", "meta-inf");

    private static final List<String> BLOCKED_RESOURCE_SEGMENTS = List.of("classes");

    private static final List<String> ALLOWED_RESOURCE_EXTENSIONS = List.of(
            // scripts, styles and data consumed by the browser
            ".js", ".mjs", ".css", ".map", ".json", ".html", ".htm", ".ts", ".wasm",
            // images
            ".png", ".gif", ".jpg", ".jpeg", ".bmp", ".webp", ".avif", ".svg", ".ico", ".cur",
            // fonts
            ".woff", ".woff2", ".ttf", ".otf", ".eot",
            // media and viewer assets
            ".mp3", ".mp4", ".webm", ".ogg", ".wav", ".m4a", ".pdf", ".bcmap", ".swf"
    );

    /** Trailing dots and spaces are discarded by Windows, so they must not hide the real extension. */
    private static final String SEGMENT_PADDING = ". \t";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String uri = targetUri(request);
        String decodedUri;
        String canonicalPath;
        String contextPath;
        try
        {
            decodedUri = decode(uri);
            canonicalPath = StringUtils.cleanPath(collapseSeparators(decode(stripPathParameters(uri))));
            contextPath = collapseSeparators(decode(targetContextPath(request)));
        }
        catch (IllegalArgumentException malformedEscape)
        {
            // A malformed %-escape cannot be canonicalised, so it cannot be validated: fail closed.
            if(LOGGER.isInfoEnabled())
            {
                LOGGER.info(String.format("Blocked malformed resource path %s", sanitiseForLog(uri)));
            }
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }
        String sanitizedUrl = toContextRelativePath(contextPath, canonicalPath);

        if(isBlockListedUrl(decodedUri, sanitizedUrl))
        {
            if(LOGGER.isInfoEnabled())
            {
                LOGGER.info(String.format("Blocked resource path %s", sanitiseForLog(sanitizedUrl)));
            }
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }
        filterChain.doFilter(request, response);
    }

    /** Decoded paths can contain CR/LF, which would otherwise allow forged log entries. */
    private String sanitiseForLog(String path)
    {
        return path.replace('\n', '_').replace('\r', '_');
    }

    private String decode(String value)
    {
        return URLDecoder.decode(value, StandardCharsets.UTF_8).toLowerCase(Locale.ROOT);
    }

    /**
     * Tomcat collapses repeated separators when routing, so the filter must do the same or
     * "/page//resource/surf.xml" would not be recognised as the resource route it actually reaches.
     */
    private String collapseSeparators(String path)
    {
        StringBuilder collapsed = new StringBuilder(path.length());
        boolean previousWasSeparator = false;
        for (int i = 0; i < path.length(); i++)
        {
            char character = path.charAt(i);
            boolean isSeparator = PATH_SEPARATORS.indexOf(character) >= 0;
            if (isSeparator)
            {
                if (!previousWasSeparator)
                {
                    collapsed.append('/');
                }
            }
            else
            {
                collapsed.append(character);
            }
            previousWasSeparator = isSeparator;
        }
        return collapsed.toString();
    }

    private String targetUri(HttpServletRequest request)
    {
        String includedUri = (String) request.getAttribute(RequestDispatcher.INCLUDE_REQUEST_URI);
        return includedUri != null ? includedUri : request.getRequestURI();
    }

    /** The context path must come from the same dispatch as the URI, or stripping it would be wrong. */
    private String targetContextPath(HttpServletRequest request)
    {
        String includedContextPath = (String) request.getAttribute(RequestDispatcher.INCLUDE_CONTEXT_PATH);
        return includedContextPath != null ? includedContextPath : request.getContextPath();
    }

    /**
     * Servlet routing ignores path parameters, so they must be removed before the path is classified
     * or the container and this filter would disagree on which route a request belongs to. For
     * example "/page;jsessionid=abc/resource/surf.xml" is routed as "/page/resource/surf.xml".
     */
    private String stripPathParameters(String path)
    {
        if (path.indexOf(';') < 0)
        {
            return path;
        }
        StringBuilder stripped = new StringBuilder(path.length());
        boolean inPathParameter = false;
        for (int i = 0; i < path.length(); i++)
        {
            char character = path.charAt(i);
            if (character == ';')
            {
                inPathParameter = true;
            }
            else if (PATH_SEPARATORS.indexOf(character) >= 0)
            {
                inPathParameter = false;
            }
            if (!inPathParameter)
            {
                stripped.append(character);
            }
        }
        return stripped.toString();
    }

    /**
     * The context path is matched on a segment boundary so that a context of "/share" is not
     * stripped from an unrelated path such as "/sharepoint/...".
     */
    private String toContextRelativePath(String contextPath, String path)
    {
        if (!contextPath.isEmpty() && (path.equals(contextPath) || path.startsWith(contextPath + "/")))
        {
            path = path.substring(contextPath.length());
        }
        return path.startsWith("/") ? path : "/" + path;
    }

    private boolean isBlockListedUrl(String rawPath, String normalisedPath)
    {
        if (containsTraversalSegment(rawPath) || containsBlockedSegment(rawPath, BLOCKED_SEGMENTS) || containsBlockedSegment(normalisedPath, BLOCKED_SEGMENTS))
        {
            return true;
        }
        if (!isResourceRequest(normalisedPath))
        {
            return false;
        }
        return containsBlockedSegment(normalisedPath, BLOCKED_RESOURCE_SEGMENTS) || !hasAllowedResourceExtension(normalisedPath);
    }

    private boolean hasAllowedResourceExtension(String path)
    {
        String fileName = stripPadding(normaliseSegment(lastSegmentOf(path)));
        return ALLOWED_RESOURCE_EXTENSIONS.stream().anyMatch(fileName::endsWith);
    }

    private String lastSegmentOf(String path)
    {
        String fileName = "";
        StringTokenizer segments = new StringTokenizer(path, PATH_SEPARATORS);
        while (segments.hasMoreTokens())
        {
            fileName = segments.nextToken();
        }
        return fileName;
    }

    private String stripPadding(String segment)
    {
        int end = segment.length();
        while (end > 0 && SEGMENT_PADDING.indexOf(segment.charAt(end - 1)) >= 0)
        {
            end--;
        }
        return segment.substring(0, end);
    }

    /**
     * Traversal is rejected on every route, whatever the encoding, separator or path parameter used.
     */
    private boolean containsTraversalSegment(String path)
    {
        StringTokenizer segments = new StringTokenizer(path, PATH_SEPARATORS);
        while (segments.hasMoreTokens())
        {
            if (isTraversalSegment(normaliseSegment(segments.nextToken())))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Segments are compared individually rather than searching the whole path, so that names which
     * merely contain a blocked word, such as "web-info.pdf" or "myclasses", are still served.
     */
    private boolean containsBlockedSegment(String path, List<String> blockedSegments)
    {
        StringTokenizer segments = new StringTokenizer(path, PATH_SEPARATORS);
        while (segments.hasMoreTokens())
        {
            if (blockedSegments.contains(normaliseSegment(segments.nextToken())))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Drops any path parameter and the trailing control characters, spaces and NUL bytes that are
     * used to disguise a segment, leaving the value the resource loader will actually resolve.
     */
    private String normaliseSegment(String segment)
    {
        int pathParameterIndex = segment.indexOf(';');
        if (pathParameterIndex >= 0)
        {
            segment = segment.substring(0, pathParameterIndex);
        }
        return segment.trim();
    }

    /**
     * A segment is a traversal attempt when it consists solely of dots, which covers "..", "...",
     * "..;", "..;jsessionid=x" and the decoded forms of "%2e%2e".
     */
    private boolean isTraversalSegment(String segment)
    {
        if (segment.length() < 2)
        {
            return false;
        }
        for (int i = 0; i < segment.length(); i++)
        {
            if (segment.charAt(i) != '.')
            {
                return false;
            }
        }
        return true;
    }

    private boolean isResourceRequest(String path)
    {
        return RESOURCE_PATH_PREFIXES.stream().anyMatch(path::startsWith);
    }
}
