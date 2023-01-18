package scot.gov.dis.sso.oidcclients.privateclient.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

@Controller
class ErrorController {
    @GetMapping("/error")
    public Mono<String> getError() {
        return Mono.just("error");
    }
}
