package org.alfresco.web.site.servlet.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.alfresco.web.site.TaskUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
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
@Conditional(AIMSEnabled.class)
public class AppConfig {
    private static final Log logger = LogFactory.getLog(AppConfig.class);

    @Value("${aims.realm}")
    private String realm;
    @Value("${aims.resource}")
    private String clientId;
    @Value("${aims.secret:#{'${aims.resource}'}}")
    private String clientSecret;
    @Value("${aims.authServerUrl}")
    private String authUrl;
    @Value("${aims.principalAttribute:sub}")
    private String principalAttribute;

    @Bean
    public OAuth2AuthorizedClientRepository authorizedClientRepository(
            OAuth2AuthorizedClientService authorizedClientService) {
        return new
                AuthenticatedPrincipalOAuth2AuthorizedClientRepository(authorizedClientService);
    }

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService(
            ClientRegistrationRepository clientRegistrationRepository) {
        return new
            InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new
                InMemoryClientRegistrationRepository(this.clientRegistration());
    }

    @Bean
    public AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientServiceAndManager (
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService authorizedClientService) {

        OAuth2AuthorizedClientProvider authorizedClientProvider =
                OAuth2AuthorizedClientProviderBuilder.builder()
                        .authorizationCode()
                        .build();

        AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager =
                new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                        clientRegistrationRepository, authorizedClientService);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return authorizedClientManager;
    }

    private ClientRegistration clientRegistration() {
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
        /**
         * On the contrary this implementation is setting the endpoints so that Share does not
         * call Idp for Getting endpoints
         */
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
