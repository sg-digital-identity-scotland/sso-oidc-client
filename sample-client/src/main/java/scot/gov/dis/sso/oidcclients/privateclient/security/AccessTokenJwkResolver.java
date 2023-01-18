package scot.gov.dis.sso.oidcclients.privateclient.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.Requirement;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.util.Base64URL;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;

@Component
public class AccessTokenJwkResolver {

    @Value("${jwk.keypair}")
    private String jwkKeyPair;

    public Function<ClientRegistration, JWK> getAccessTokenJwkResolver() {
        Function<ClientRegistration, JWK> jwkResolver = (clientRegistration) -> {
            ObjectMapper mapper = new ObjectMapper();
            try {
                Map<String, String> map = mapper.readValue(jwkKeyPair, Map.class);
                Base64URL publicKey = new Base64URL(map.get("n"));
                Base64URL publicExponent = new Base64URL(map.get("e"));
                Base64URL firstPrimeFactor = new Base64URL(map.get("p"));
                Base64URL secondPrimeFactor = new Base64URL(map.get("q"));
                Base64URL privateExponent = new Base64URL(map.get("d"));
                Base64URL firstCRTCoefficient = new Base64URL(map.get("qi"));
                Base64URL firstFactorCRTExponent = new Base64URL(map.get("dp"));
                Base64URL secondFactorCRTExponent = new Base64URL(map.get("dq"));
                KeyUse use = new KeyUse(map.get("use"));
                Algorithm alg =new Algorithm(map.get("alg"), Requirement.REQUIRED);
                String kid = map.get("kid");

                return new RSAKey.Builder(publicKey, publicExponent)
                        .firstPrimeFactor(firstPrimeFactor)
                        .secondPrimeFactor(secondPrimeFactor)
                        .privateExponent(privateExponent)
                        .firstCRTCoefficient(firstCRTCoefficient)
                        .firstFactorCRTExponent(firstFactorCRTExponent)
                        .secondFactorCRTExponent(secondFactorCRTExponent)
                        .keyUse(use)
                        .algorithm(alg)
                        .keyID(kid)
                        .build();

            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return null;
            }
        };

        return jwkResolver;
    }
}
