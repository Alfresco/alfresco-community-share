package org.alfresco.web.site.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ResourcePathValidationFilter implements Filter {

    private static final List<String> BLOCKED_PATH_FRAGMENTS = List.of(
            "..",
            "web-inf",
            "meta-inf",
            "classes/",
            "config/",
            "alfresco/module/",
            "alfresco/templates/"
    );

    private static final List<String> BLOCKED_EXTENSIONS = List.of(
            ".properties",
            ".xml",
            ".class",
            ".jsp",
            ".ftl"
    );

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String uri = request.getRequestURI();
        String decoded = URLDecoder.decode(uri, StandardCharsets.UTF_8);
        String sanitizedUrl = StringUtils.cleanPath(decoded).toLowerCase();

        if(isBlackListedUrl(sanitizedUrl))
        {
            System.out.println("BlackListed Url : " + sanitizedUrl);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }
        filterChain.doFilter(request, response);
    }

    private boolean isBlackListedUrl(String path)
    {
        return BLOCKED_PATH_FRAGMENTS.stream().anyMatch(path::contains) || BLOCKED_EXTENSIONS.stream().anyMatch(path::endsWith);
    }
}
