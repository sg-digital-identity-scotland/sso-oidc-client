package scot.gov.dis.sso.oidcclients.privateclient.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthorizationCodeReactiveAuthenticationManager;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;

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
                .build();
    }


}
