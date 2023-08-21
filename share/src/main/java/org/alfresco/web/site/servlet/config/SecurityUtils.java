package org.alfresco.web.site.servlet.config;

import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.security.web.util.ThrowableAnalyzer;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import jakarta.servlet.ServletException;
import java.util.Map;

public class SecurityUtils {

    public static boolean isAuthorizationResponse(MultiValueMap<String, String> request) {
        return isAuthorizationResponseSuccess(request) || isAuthorizationResponseError(request);
    }

    static boolean isAuthorizationResponseSuccess(MultiValueMap<String, String> request) {
        return StringUtils.hasText(request.getFirst("code")) && StringUtils.hasText(request.getFirst("state"));
    }

    static boolean isAuthorizationResponseError(MultiValueMap<String, String> request) {
        return StringUtils.hasText(request.getFirst("error")) && StringUtils.hasText(request.getFirst("state"));
    }


    public static MultiValueMap<String, String> toMultiMap(Map<String, String[]> map) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap(map.size());
        map.forEach((key, values) -> {
            if (values.length > 0) {
                String[] var3 = values;
                int var4 = values.length;

                for(int var5 = 0; var5 < var4; ++var5) {
                    String value = var3[var5];
                    params.add(key, value);
                }
            }

        });
        return params;
    }

    public static OAuth2AuthorizationResponse convert(MultiValueMap<String, String> request, String redirectUri) {
        String code = request.getFirst("code");
        String errorCode = request.getFirst("error");
        String state = request.getFirst("state");
        if (StringUtils.hasText(code)) {
            return OAuth2AuthorizationResponse.success(code).redirectUri(redirectUri).state(state).build();
        } else {
            String errorDescription = request.getFirst("error_description");
            String errorUri = request.getFirst("error_uri");
            return OAuth2AuthorizationResponse.error(errorCode).redirectUri(redirectUri).errorDescription(errorDescription).errorUri(errorUri).state(state).build();
        }
    }

    public static final class DefaultThrowableAnalyzer extends ThrowableAnalyzer {
        public DefaultThrowableAnalyzer() {
        }

        protected void initExtractorMap() {
            super.initExtractorMap();
            this.registerExtractor(ServletException.class, (throwable) -> {
                ThrowableAnalyzer.verifyThrowableHierarchy(throwable, ServletException.class);
                return ((ServletException)throwable).getRootCause();
            });
        }
    }
}
