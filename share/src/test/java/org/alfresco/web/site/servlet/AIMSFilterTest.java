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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.alfresco.web.site.servlet.AIMSFilter.JwtAudienceValidator;
import org.junit.Test;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

public class AIMSFilterTest
{
    private static final String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjIxNDc0ODM2NDcsImp0aSI6IjEyMzQiLCJpc3MiOiJodHRwczovL215Lmlzc3VlciIsInN1YiI6ImFiYzEyMyIsInR5cCI6IkJlYXJlciIsInByZWZlcnJlZF91c2VybmFtZSI6Im1vaGluaXNoc2FoIn0.YOvsLAZ0ZyKf4igvtBY0fsO6R1F3Xhz5IsWzsRhyOVY";

    private static final String EXPECTED_AUDIENCE = "expected-audience";

    @Test
    public void shouldNotValidateWhenIssuerAndRequiredIssuerNotEqual()
    {
        //        filter = new AIMSFilter();
        final JwtAudienceValidator audienceValidator = new JwtAudienceValidator(EXPECTED_AUDIENCE);
        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId("test_registration_id")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .clientId("test_client_id")
            .redirectUri("test_uri_template")
            .issuerUri("https://my.actualIssuer")
            .authorizationUri("http://localhost:8999/auth")
            .tokenUri("test_token_uri")
            .build();
        ClientRegistration.ProviderDetails providerDetails = clientRegistration.getProviderDetails();

        Jwt jwt = Jwt.withTokenValue(token)
            .header("alg", "none")
            .claim("sub", "abc123")
            .claim("iss", "https://my.fakeIssuer")
            .claim("preferred_username", "mohinishsah")
            .build();
        OAuth2TokenValidatorResult oAuth2TokenValidatorResult = audienceValidator.validate(jwt);
        assertTrue(oAuth2TokenValidatorResult.hasErrors());
    }

    @Test
    public void shouldValidateWhenIssuerAndRequiredIssuerAreEqual()
    {
        final JwtAudienceValidator audienceValidator = new JwtAudienceValidator(EXPECTED_AUDIENCE);
        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId("test_registration_id")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .clientId("test_client_id")
            .redirectUri("test_uri_template")
            .issuerUri("https://my.issuer")
            .authorizationUri("http://localhost:8999/auth")
            .tokenUri("test_token_uri")
            .build();

        ClientRegistration.ProviderDetails providerDetails = clientRegistration.getProviderDetails();

        Jwt jwt = Jwt.withTokenValue(token)
            .header("alg", "none")
            .claim("sub", "abc123")
            .claim("iss", "https://my.issuer")
            .claim("preferred_username", "mohinishsah")
            .audience(List.of(EXPECTED_AUDIENCE))
            .build();

        OAuth2TokenValidatorResult oAuth2TokenValidatorResult = audienceValidator.validate(jwt);
        assertFalse(oAuth2TokenValidatorResult.hasErrors());

    }

    @Test
    public void shouldFailWithNullAudience()
    {
        final JwtAudienceValidator audienceValidator = new JwtAudienceValidator(EXPECTED_AUDIENCE);

        final OAuth2TokenValidatorResult validationResult = audienceValidator.validate(tokenWithAudience(null));

        final OAuth2Error error = validationResult.getErrors()
            .iterator()
            .next();

        assertTrue(error.getDescription()
                       .contains(EXPECTED_AUDIENCE));
    }

    @Test
    public void shouldSucceedWithMatchingAudienceList()
    {
        final JwtAudienceValidator audienceValidator = new JwtAudienceValidator(EXPECTED_AUDIENCE);

        final OAuth2TokenValidatorResult validationResult =
            audienceValidator.validate(tokenWithAudience(List.of(EXPECTED_AUDIENCE)));
        assertFalse(validationResult.hasErrors());
        assertTrue(validationResult.getErrors()
                       .isEmpty());
    }

    @Test
    public void shouldSucceedWithMatchingSingleAudience()
    {
        final JwtAudienceValidator audienceValidator = new JwtAudienceValidator(EXPECTED_AUDIENCE);

        final Jwt token = Jwt.withTokenValue(UUID.randomUUID()
                                                 .toString())
            .claim("aud", EXPECTED_AUDIENCE)
            .header("JUST", "FOR TESTING")
            .build();
        final OAuth2TokenValidatorResult validationResult = audienceValidator.validate(token);
        assertFalse(validationResult.hasErrors());
        assertTrue(validationResult.getErrors()
                       .isEmpty());
    }

    private Jwt tokenWithAudience(Collection<String> audience)
    {
        return Jwt.withTokenValue(UUID.randomUUID()
                                      .toString())
            .audience(audience)
            .header("JUST", "FOR TESTING")
            .build();
    }

}
