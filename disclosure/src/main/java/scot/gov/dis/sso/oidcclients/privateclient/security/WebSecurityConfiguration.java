package scot.gov.dis.sso.oidcclients.privateclient.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthorizationCodeReactiveAuthenticationManager;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.net.URI;

@EnableWebFluxSecurity
class WebSecurityConfiguration {

    @Autowired
    private ServerOAuth2AuthorizationRequestResolver authorizationRequestResolver;

    @Autowired
    private ServerAuthenticationFailureHandler authenticationFailureHandler;

    @Autowired
    private AccessTokenResponseClient accessTokenResponseClient;

    @Bean
    SecurityWebFilterChain configure(ServerHttpSecurity http) throws Exception {
        return http
                .authorizeExchange()
                    .pathMatchers("/", "/error").permitAll()
                    .pathMatchers("/**").authenticated()

                .and()
                    .authenticationManager(new OAuth2AuthorizationCodeReactiveAuthenticationManager(accessTokenResponseClient))
                    .oauth2Login()
                        .authorizationRequestResolver(authorizationRequestResolver)
                        .authenticationFailureHandler(authenticationFailureHandler)
                .and()
                .logout()
                .disable()
                .build();
    }


}
