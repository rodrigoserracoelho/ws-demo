package io.surisoft.demo.ws.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import io.surisoft.demo.ws.exception.WebApplicationSecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.Map;

@Component
@Slf4j
public class WebApplicationAuthorization implements Authorization {

    @Autowired
    private JWKSet jwkSet;

    @Override
    public boolean isAuthorized(String authorization) {

        ConfigurableJWTProcessor jwtProcessor = new DefaultJWTProcessor();
        JWKSource keySource = new ImmutableJWKSet(jwkSet);
        JWSAlgorithm expectedJWSAlg = JWSAlgorithm.RS256;
        JWSKeySelector<SecurityContext> keySelector = new JWSVerificationKeySelector<>(expectedJWSAlg, keySource);
        jwtProcessor.setJWSKeySelector(keySelector);
        try {
            JWTClaimsSet claimsSet = jwtProcessor.process(authorization, null);
            log.info(claimsSet.getIssuer());
            log.info(claimsSet.getSubject());
            return true;
        } catch (ParseException |  BadJOSEException | JOSEException e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public String getAuthorization(Object object) throws WebApplicationSecurityException {
        Map<String, Object> nativeHeaders = (Map<String, Object>) object;
        LinkedList<String> authorizationList = (LinkedList<String>) nativeHeaders.get("Authorization");
        String authorization = authorizationList.get(0);
        if(authorization == null) {
            throw new WebApplicationSecurityException("No authorization present");
        }
        return authorization;
    }

    @Override
    public String getClaims(Object object) {
        return null;
    }
}
