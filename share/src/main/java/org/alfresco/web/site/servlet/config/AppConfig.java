package org.alfresco.web.site.servlet.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nimbusds.oauth2.sdk.GrantType;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.as.AuthorizationServerMetadata;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import net.minidev.json.JSONObject;
import org.alfresco.web.site.TaskUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthenticatedPrincipalOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

@Configuration
public class AppConfig {
    private static final Log logger = LogFactory.getLog(AppConfig.class);
    private final String realm;
    private final String clientId;
    private final String clientSecret;
    private final String authUrl;
    private final String principalAttribute;
    private final AIMSConfig aimsConfig;
    private static final String REALMS = "realms";
    private static final RestTemplate rest = new RestTemplate();
    private static final ParameterizedTypeReference<Map<String, Object>> typeReference = new ParameterizedTypeReference<Map<String, Object>>() {};

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
    public ClientRegistrationRepository clientRegistrationRepository() throws ParseException {
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

    private ClientRegistration clientRegistration() throws ParseException {
        if(aimsConfig.isEnabled()) {
            String realm_url = authUrl + "/realms/" + realm;
            String issuer = getIssuer(URI.create(realm_url),realm).get();
            /**
             * This implementation is primarily getting all the endpoints on the fly during startup
             */
            AtomicReference<ClientRegistration.Builder> builder = new AtomicReference<>();
            TaskUtils.retry(10, 1000, logger,
                () -> builder.set(getRfc8414Builder(URI.create(issuer)
                    ,URI.create(realm_url+"/.well-known/openid-configuration")).get()));

            return
                builder.get()
                    .registrationId(clientId)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .scope("openid")
                    .redirectUri("*")
                    .userNameAttributeName(principalAttribute)
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
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

    private static Optional<String> getIssuer(URI realm_url, String realm) throws ParseException {

        URI uri = UriComponentsBuilder.fromUri(realm_url)
            .replacePath(realm_url.getPath() + "/.well-known/openid-configuration")
            .build(Collections.emptyMap());

        RequestEntity<Void> request = RequestEntity.get(uri).build();
        String configuration = rest.exchange(request, String.class).getBody();

        OIDCProviderMetadata metadata = OIDCProviderMetadata.parse(configuration);

        return Optional.of(Optional.of(metadata)
            .map(OIDCProviderMetadata::getIssuer)
            .map(Issuer::getValue)
            .orElse(UriComponentsBuilder.fromUriString(realm_url.toString())
                .pathSegment(REALMS, realm)
                .build()
                .toString()));
    }

    private static Supplier<ClientRegistration.Builder> getRfc8414Builder(URI issuer, URI uri) {
        return () -> {
            RequestEntity<Void> request = RequestEntity.get(uri).build();
            Map<String, Object> configuration = (Map)rest.exchange(request, typeReference).getBody();
            AuthorizationServerMetadata metadata = parse(configuration, AuthorizationServerMetadata::parse);
            ClientRegistration.Builder builder = withProviderConfiguration(metadata, issuer.toASCIIString());
            URI jwkSetUri = metadata.getJWKSetURI();
            if (jwkSetUri != null) {
                builder.jwkSetUri(jwkSetUri.toASCIIString());
            }

            String userinfoEndpoint = (String)configuration.get("userinfo_endpoint");
            if (userinfoEndpoint != null) {
                builder.userInfoUri(userinfoEndpoint);
            }

            return builder;
        };
    }

    private static ClientRegistration.Builder withProviderConfiguration(AuthorizationServerMetadata metadata, String issuer) {
        String metadataIssuer = metadata.getIssuer().getValue();
        if (!issuer.equals(metadataIssuer)) {
            throw new IllegalStateException("The Issuer \"" + metadataIssuer + "\" provided in the configuration metadata did not match the requested issuer \"" + issuer + "\"");
        } else {
            String name = URI.create(issuer).getHost();
            ClientAuthenticationMethod method = getClientAuthenticationMethod(issuer, metadata.getTokenEndpointAuthMethods());
            List<GrantType> grantTypes = metadata.getGrantTypes();
            if (grantTypes != null && !grantTypes.contains(GrantType.AUTHORIZATION_CODE)) {
                throw new IllegalArgumentException("Only AuthorizationGrantType.AUTHORIZATION_CODE is supported. The issuer \"" + issuer + "\" returned a configuration of " + grantTypes);
            } else {
                List<String> scopes = getScopes(metadata);
                Map<String, Object> configurationMetadata = new LinkedHashMap(metadata.toJSONObject());
                return ClientRegistration.withRegistrationId(name)
                    .userNameAttributeName("sub")
                    .scope(scopes)
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .clientAuthenticationMethod(method)
                    .redirectUri("{baseUrl}/{action}/oauth2/code/{registrationId}")
                    .authorizationUri(metadata.getAuthorizationEndpointURI().toASCIIString())
                    .providerConfigurationMetadata(configurationMetadata)
                    .tokenUri(metadata.getTokenEndpointURI().toASCIIString())
                    .clientName(issuer);
            }
        }
    }

    private static ClientAuthenticationMethod getClientAuthenticationMethod(String issuer, List<com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod> metadataAuthMethods) {
        if (metadataAuthMethods != null &&
            !metadataAuthMethods.contains(com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod.CLIENT_SECRET_BASIC)) {
            if (metadataAuthMethods.contains(com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod.CLIENT_SECRET_POST)) {
                return ClientAuthenticationMethod.CLIENT_SECRET_POST;
            } else if (metadataAuthMethods.contains(com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod.NONE)) {
                return ClientAuthenticationMethod.NONE;
            } else {
                throw new IllegalArgumentException("Only ClientAuthenticationMethod.BASIC, ClientAuthenticationMethod.POST and ClientAuthenticationMethod.NONE are supported. The issuer \"" + issuer + "\" returned a configuration of " + metadataAuthMethods);
            }
        } else {
            return ClientAuthenticationMethod.CLIENT_SECRET_BASIC;
        }
    }

    private static List<String> getScopes(AuthorizationServerMetadata metadata) {
        Scope scope = metadata.getScopes();
        return scope == null ? Collections.singletonList("openid") : scope.toStringList();
    }

    private static <T> T parse(Map<String, Object> body, ThrowingFunction<JSONObject, T, ParseException> parser) {
        try {
            return parser.apply(new JSONObject(body));
        } catch (ParseException var3) {
            throw new RuntimeException(var3);
        }
    }

    private interface ThrowingFunction<S, T, E extends Throwable> {
        T apply(S src) throws E;
    }
}
