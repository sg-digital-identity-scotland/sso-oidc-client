package scot.gov.dis.sso.oidcclients.privateclient.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

import java.net.URI;

@Controller
public class RefreshAccessTokenController {

    @GetMapping("/refresh_access_token")
    public Mono<ResponseEntity<Void>> refreshAccessToken(final Model model, @AuthenticationPrincipal final OidcUser principal, @RegisteredOAuth2AuthorizedClient("dis") OAuth2AuthorizedClient authorizedClient) {

        // This will cause the Access token to be refreshed, if it is due to expire in the next 60 seconds.
        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();

        return Mono.just(ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/"))
                .build());
    }
}
