package scot.gov.dis.sso.oidcclients.privateclient.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

@Controller
public class LogoutController {

    @Value("${spring.security.oauth2.client.registration.dis.post-logout-redirect-uri}")
    private String postLogoutRedirectUri;

    @Value("${spring.security.oauth2.client.provider.dis.end-session-uri}")
    private String endSessionUri;

    @GetMapping("/logout")
    public Mono<ResponseEntity<Void>> getLogout(final Model model, @AuthenticationPrincipal final OidcUser principal) {
        String idToken = principal.getIdToken().getTokenValue();

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(endSessionUri)
                .queryParam("post_logout_redirect_uri", postLogoutRedirectUri)
                .queryParam("id_token_hint", idToken);

        URI endSessionRequestURI = uriComponentsBuilder.build().toUri();
        return Mono.just(ResponseEntity.status(HttpStatus.FOUND)
                .location(endSessionRequestURI)
                .build());
    }

    @GetMapping("/logout/callback")
    Mono<ResponseEntity<Void>> getCallback(final ServerHttpRequest serverHttpRequest, final Model model) {
        String deleteSessionCookie = "SESSION=; Max-Age=0; httpOnly=true; path=/";
        model.addAttribute("id_token", null);
        model.addAttribute("claims", null);

        return Mono.just(ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/"))
                .header(HttpHeaders.SET_COOKIE, deleteSessionCookie)
                .build());
    }

    @PostMapping("/logout/back-channel")
    Mono<ResponseEntity<Void>> logout(final ServerHttpRequest serverHttpRequest, final Model model) {
        System.out.println("back-channel logout request received");
        return Mono.just(ResponseEntity.status(200).header(HttpHeaders.CACHE_CONTROL, "no-store").build());
    }
}
