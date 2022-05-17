package scot.gov.dis.sso.oidcclients.privateclient.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

import java.util.Map;

@Controller
public class HomeController {
    @GetMapping("/")
    public Mono<String> getHome(final Model model, @AuthenticationPrincipal final OidcUser principal) {
        model.addAttribute("claims", principal == null ? Map.of(): principal.getClaims());
        return Mono.just("index");
    }
}