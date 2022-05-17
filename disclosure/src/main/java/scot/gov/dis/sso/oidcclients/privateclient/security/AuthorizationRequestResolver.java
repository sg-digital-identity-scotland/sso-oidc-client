package scot.gov.dis.sso.oidcclients.privateclient.security;

import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.PkceParameterNames;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
class AuthorizationRequestResolver implements ServerOAuth2AuthorizationRequestResolver {
    private final ServerOAuth2AuthorizationRequestResolver authorizationRequestResolver;
    private final StringKeyGenerator secureKeyGenerator =
        new Base64StringKeyGenerator(Base64.getUrlEncoder().withoutPadding(), 96);

    AuthorizationRequestResolver(final ReactiveClientRegistrationRepository clientRegistrationRepository) {
        authorizationRequestResolver = new DefaultServerOAuth2AuthorizationRequestResolver(clientRegistrationRepository);
    }

    @Override
    public Mono<OAuth2AuthorizationRequest> resolve(ServerWebExchange exchange) {
        return authorizationRequestResolver.resolve(exchange)
                .map(request -> this.authorizationRequest(request, exchange));
    }

    @Override
    public Mono<OAuth2AuthorizationRequest> resolve(ServerWebExchange exchange, String clientRegistrationId) {
        return authorizationRequestResolver.resolve(exchange, clientRegistrationId)
                .map(request -> this.authorizationRequest(request, exchange));
    }

    private OAuth2AuthorizationRequest authorizationRequest(final OAuth2AuthorizationRequest request, final ServerWebExchange exchange) {
        if (request == null) {
            return null;
        }

        Map<String, Object> additionalParameters = new HashMap<>(exchange.getRequest().getQueryParams().toSingleValueMap());
        Map<String, Object> attributes = new HashMap<>(request.getAttributes());

        // Spring Security doesn't support confidential clients with PKCE out the box
        // so have to manually add PKCE parameters.
        try {
            // Spring Security will automatically include this in token request after
            // successful authentication
            String codeVerifier = this.secureKeyGenerator.generateKey();
            attributes.put(PkceParameterNames.CODE_VERIFIER, codeVerifier);
            String codeChallenge = base64encode(sha256hash(codeVerifier));
            additionalParameters.put(PkceParameterNames.CODE_CHALLENGE, codeChallenge);
            additionalParameters.put(PkceParameterNames.CODE_CHALLENGE_METHOD, "S256");
        } catch (NoSuchAlgorithmException e) {}

        return OAuth2AuthorizationRequest.from(request)
                .attributes(attributes)
                .additionalParameters(additionalParameters)
                .build();
    }

    private byte[] sha256hash(String value) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(value.getBytes(StandardCharsets.US_ASCII));
    }

    private String base64encode(byte[] value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value);
    }
}
