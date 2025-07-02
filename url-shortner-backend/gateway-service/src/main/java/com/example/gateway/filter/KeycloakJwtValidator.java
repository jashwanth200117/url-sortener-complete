package com.example.gateway.filter;

import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.JWTClaimsSet;

import java.net.URL;
import java.text.ParseException;
import java.util.Map;

public class KeycloakJwtValidator {

    private final ConfigurableJWTProcessor<SecurityContext> jwtProcessor;

    public KeycloakJwtValidator(String jwksUrl) throws Exception {
        JWKSource<SecurityContext> keySource = new RemoteJWKSet<>(new URL(jwksUrl));
        jwtProcessor = new DefaultJWTProcessor<>();
        jwtProcessor.setJWSKeySelector(new com.nimbusds.jose.proc.JWSVerificationKeySelector<>(
                com.nimbusds.jose.JWSAlgorithm.RS256, keySource));
    }

    public Map<String, Object> validateToken(String token) throws Exception {
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWTClaimsSet claimsSet = jwtProcessor.process(signedJWT, null);
        return claimsSet.getClaims(); // returns Map<String, Object>
    }

}