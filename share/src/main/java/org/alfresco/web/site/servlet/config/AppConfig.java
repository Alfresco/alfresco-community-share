package org.alfresco.web.site.servlet.config;

import static org.alfresco.web.site.servlet.config.IdentityServiceMetadataKey.ACCESS_TOKEN_ISSUER;
import static org.alfresco.web.site.servlet.config.IdentityServiceMetadataKey.AUDIENCE;
import static org.alfresco.web.site.servlet.config.IdentityServiceMetadataKey.SCOPES_SUPPORTED;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.id.Identifier;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import net.minidev.json.JSONObject;

import org.alfresco.web.site.TaskUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
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
import java.util.stream.Collectors;

@Configuration
public class AppConfig
{
    private static final Log LOGGER =
        LogFactory.getLog(AppConfig.class);
    private final String clientId;
    private final String clientSecret;
    private final String principalAttribute;
    private final AIMSConfig aimsConfig;
    private final Set<String> scopes;
    private static final String REALMS = "realms";
    private static final RestTemplate rest = new RestTemplate();
    private static final String DEFAULT_ACCESS_TOKEN_ISSUER_ATTR = "issuer";
    private static final ParameterizedTypeReference<Map<String, Object>> typeReference = new ParameterizedTypeReference<Map<String, Object>>() {};

    @Autowired
    public AppConfig(AIMSConfig aimsConfig)
    {
        this.aimsConfig = aimsConfig;
        this.clientId = aimsConfig.getResource();
        this.clientSecret = aimsConfig.getSecret();
        this.principalAttribute = aimsConfig.getPrincipalAttribute();
        this.scopes = aimsConfig.getScopes();
    }

    @Bean
    public OAuth2AuthorizedClientRepository authorizedClientRepository(
        @Autowired(required = false) OAuth2AuthorizedClientService authorizedClientService)
    {
        if (null != authorizedClientService)
        {
            return new AuthenticatedPrincipalOAuth2AuthorizedClientRepository(authorizedClientService);
        }
        return null;
    }

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService(
        @Autowired(required = false) ClientRegistrationRepository clientRegistrationRepository)
    {
        if (null != clientRegistrationRepository)
        {
            return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
        }
        return null;
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() throws ParseException
    {
        ClientRegistration clientRegistration = this.clientRegistration();
        if (null != clientRegistration)
        {
            return new InMemoryClientRegistrationRepository(this.clientRegistration());
        }
        return null;
    }

    @Bean
    public AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientServiceAndManager(
        @Autowired(required = false) ClientRegistrationRepository clientRegistrationRepository,
        @Autowired(required = false) OAuth2AuthorizedClientService authorizedClientService)
    {
        if (null != clientRegistrationRepository && null != authorizedClientService)
        {
            OAuth2AuthorizedClientProvider authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
                .authorizationCode()
                .build();

            AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager =
                new AuthorizedClientServiceOAuth2AuthorizedClientManager(clientRegistrationRepository,
                                                                         authorizedClientService);
            authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

            return authorizedClientManager;
        }
        else
        {
            return null;
        }
    }

    private ClientRegistration clientRegistration() throws ParseException
    {
        if (aimsConfig.isEnabled())
        {
            // This implementation is primarily getting all the endpoints on the fly during startup
            AtomicReference<ClientRegistration.Builder> builder = new AtomicReference<>();
            TaskUtils.retry(10, 1000, LOGGER, () -> builder.set(createBuilder(getMetadataURI())));

            return builder.get()
                .registrationId(clientId)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .redirectUri("*")
                .userNameAttributeName(principalAttribute)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .clientName(clientId)
                .build();
        }
        return null;
    }

    @Bean
    public MappingJackson2HttpMessageConverter jsonConverter()
    {
        List<MediaType> supportedMediaTypes = new ArrayList<>();
        supportedMediaTypes.add(MediaType.APPLICATION_JSON);

        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.serializationInclusion(JsonInclude.Include.NON_NULL);

        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter(builder.build());
        jsonConverter.setSupportedMediaTypes(supportedMediaTypes);
        return jsonConverter;
    }

    private ClientRegistration.Builder createBuilder(URI metaDataUri)
    {
        RequestEntity<Void> request = RequestEntity.get(metaDataUri)
            .build();
        Map<String, Object> configuration = (Map) rest.exchange(request, typeReference)
            .getBody();
        OIDCProviderMetadata metadata = parse(configuration, OIDCProviderMetadata::parse);

        final String authUri = Optional.of(metadata)
            .map(OIDCProviderMetadata::getAuthorizationEndpointURI)
            .map(URI::toASCIIString)
            .orElse(null);

        final String issuerUri = Optional.of(metadata)
            .map(OIDCProviderMetadata::getIssuer)
            .map(Issuer::getValue)
            .orElseThrow(() -> new IllegalStateException("Issuer Url cannot be empty."));

        Map<String, Object> configurationMetadata = new LinkedHashMap<>(metadata.toJSONObject());
        return ClientRegistration.withRegistrationId("ids")
            .providerConfigurationMetadata(configurationMetadata)
            .authorizationUri(authUri)
            .issuerUri(issuerUri)
            .tokenUri(metadata.getTokenEndpointURI()
                          .toASCIIString())
            .jwkSetUri(metadata.getJWKSetURI()
                           .toASCIIString())
            .userInfoUri(metadata.getUserInfoEndpointURI()
                             .toASCIIString())
            .scope(getSupportedScopes(metadata.getScopes()))
            .providerConfigurationMetadata(createMetadata(metadata, configurationMetadata));
    }

    private static <T> T parse(Map<String, Object> body, ThrowingFunction<JSONObject, T, ParseException> parser)
    {
        try
        {
            return parser.apply(new JSONObject(body));
        }
        catch (ParseException var3)
        {
            throw new RuntimeException(var3);
        }
    }

    private interface ThrowingFunction<S, T, E extends Throwable>
    {
        T apply(S src) throws E;
    }

    private URI getMetadataURI()
    {
        String authServerUrl = aimsConfig.getAuthServerUrl();
        if (StringUtils.isEmpty(authServerUrl))
        {
            throw new IllegalArgumentException("AuthServer Url cannot be empty.");
        }
        return UriComponentsBuilder.fromUriString(authServerUrl)
            .pathSegment(".well-known", "openid-configuration")
            .build()
            .toUri();
    }

    private Set<String> getSupportedScopes(Scope scopes)
    {
        return scopes.stream()
            .filter(this::hasShareScope)
            .map(Identifier::getValue)
            .collect(Collectors.toSet());
    }

    private boolean hasShareScope(Scope.Value scope)
    {
        return scopes.contains(scope.getValue());
    }

    private Map<java.lang.String, Object> createMetadata(OIDCProviderMetadata metadata,
                                                         Map<String, Object> configurationMetadata)
    {
        String atIssuerAttribute = aimsConfig.getAtIssuerAttribute();

        if (metadata.getScopes() != null)
        {
            configurationMetadata.put(SCOPES_SUPPORTED.getValue(), metadata.getScopes());
        }
        if (StringUtils.isNotBlank(this.aimsConfig.getAudience()))
        {
            configurationMetadata.put(AUDIENCE.getValue(), this.aimsConfig.getAudience());
        }

        if (StringUtils.isNotBlank(atIssuerAttribute)
            && !DEFAULT_ACCESS_TOKEN_ISSUER_ATTR.equals(atIssuerAttribute)
            && metadata.getCustomParameters().get(atIssuerAttribute) != null)
        {
            configurationMetadata.put(ACCESS_TOKEN_ISSUER.getValue(), metadata.getCustomParameters().get(atIssuerAttribute));
        }
        else
        {
            configurationMetadata.put(ACCESS_TOKEN_ISSUER.getValue(), metadata.getIssuer().getValue());
        }

        return configurationMetadata;
    }
}
