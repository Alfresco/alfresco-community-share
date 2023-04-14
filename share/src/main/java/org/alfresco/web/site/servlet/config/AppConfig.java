package org.alfresco.web.site.servlet.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.alfresco.web.site.TaskUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ClientRegistrations;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthenticatedPrincipalOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Configuration
public class AppConfig {
    private static final Log logger = LogFactory.getLog(AppConfig.class);
    private final String realm;
    private final String clientId;
    private final String clientSecret;
    private final String authUrl;
    private final String principalAttribute;
    private final AIMSConfig aimsConfig;
    @Autowired
    public AppConfig(AIMSConfig aimsConfig) {
        this.aimsConfig = aimsConfig;
        this.realm = aimsConfig.getRealm();
        this.clientId = aimsConfig.getResource();
        this.clientSecret = aimsConfig.getSecret();
        this.authUrl = aimsConfig.getAuthServerUrl();
        this.principalAttribute = aimsConfig.getPrincipalAttribute();
    }

    @Bean
    public OAuth2AuthorizedClientRepository authorizedClientRepository(
        @Autowired(required = false) OAuth2AuthorizedClientService authorizedClientService) {
        if (null != authorizedClientService)
        return new
            AuthenticatedPrincipalOAuth2AuthorizedClientRepository(authorizedClientService);
        else
            return null;
    }

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService(
        @Autowired(required = false) ClientRegistrationRepository clientRegistrationRepository) {
        if (null != clientRegistrationRepository)
        return new
            InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
        else
            return null;
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        ClientRegistration clientRegistration = this.clientRegistration();
        if (null != clientRegistration)
            return new
                InMemoryClientRegistrationRepository(this.clientRegistration());
        else
            return null;
    }

    @Bean
    public AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientServiceAndManager (
        @Autowired(required = false) ClientRegistrationRepository clientRegistrationRepository,
        @Autowired(required = false) OAuth2AuthorizedClientService authorizedClientService) {
        if(null != clientRegistrationRepository && null != authorizedClientService) {
            OAuth2AuthorizedClientProvider authorizedClientProvider =
                OAuth2AuthorizedClientProviderBuilder.builder()
                    .authorizationCode()
                    .build();

            AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager =
                new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                    clientRegistrationRepository, authorizedClientService);
            authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

            return authorizedClientManager;
        } else
            return null;
    }

    private ClientRegistration clientRegistration() {
        if(aimsConfig.isEnabled()) {
            String realm_url = authUrl + "/realms/" + realm;
            /**
             * This implementation is primarily getting all the endpoints on the fly during startup
             */
            AtomicReference<ClientRegistration.Builder> builder = new AtomicReference<>();
            TaskUtils.retry(10, 1000, logger,
                () -> builder.set(ClientRegistrations.fromOidcIssuerLocation(realm_url)));

            return
                builder.get()
                    .registrationId(clientId)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .scope("openid")
                    .redirectUri("*")
                    .userNameAttributeName(principalAttribute)
                    .clientAuthenticationMethod(ClientAuthenticationMethod.POST)
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .clientName(clientId)
                    .build();
        } else {
            return null;
        }
    }

    @Bean
    public MappingJackson2HttpMessageConverter jsonConverter() {
        List<MediaType> supportedMediaTypes = new ArrayList<>();
        supportedMediaTypes.add(MediaType.APPLICATION_JSON);

        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.serializationInclusion(JsonInclude.Include.NON_NULL);

        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter(builder.build());
        jsonConverter.setSupportedMediaTypes(supportedMediaTypes);
        return jsonConverter;
    }
}
