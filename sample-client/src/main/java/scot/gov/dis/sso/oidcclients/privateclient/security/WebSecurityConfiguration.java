package scot.gov.dis.sso.oidcclients.privateclient.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthorizationCodeReactiveAuthenticationManager;
import org.springframework.security.oauth2.client.endpoint.NimbusJwtClientAuthenticationParametersConverter;
import org.springframework.security.oauth2.client.endpoint.WebClientReactiveAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.WebClientReactiveRefreshTokenTokenResponseClient;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;

@EnableWebFluxSecurity
class WebSecurityConfiguration {

    @Value("${jwk.keypair}")
    private String jwkKeyPair;

    @Autowired
    private ServerOAuth2AuthorizationRequestResolver authorizationRequestResolver;

    @Autowired
    private ServerAuthenticationFailureHandler authenticationFailureHandler;

    @Autowired
    public ReactiveClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    public AccessTokenJwkResolver accessTokenJwkResolver;


    @Bean
    WebClientReactiveAuthorizationCodeTokenResponseClient webClientReactiveAuthorizationCodeTokenResponseClient() {
        WebClientReactiveAuthorizationCodeTokenResponseClient codeTokenResponseClient = new WebClientReactiveAuthorizationCodeTokenResponseClient();
        codeTokenResponseClient.addParametersConverter(new NimbusJwtClientAuthenticationParametersConverter<>(accessTokenJwkResolver.getAccessTokenJwkResolver()));
        return codeTokenResponseClient;
    }


    @Bean
    public ReactiveOAuth2AuthorizedClientManager authorizedClientManager(
            ReactiveClientRegistrationRepository clientRegistrationRepository,
            ServerOAuth2AuthorizedClientRepository authorizedClientRepository) {

        WebClientReactiveRefreshTokenTokenResponseClient refreshTokenTokenResponseClient =
                new WebClientReactiveRefreshTokenTokenResponseClient();

        refreshTokenTokenResponseClient.addParametersConverter(new NimbusJwtClientAuthenticationParametersConverter<>(accessTokenJwkResolver.getAccessTokenJwkResolver()));

        ReactiveOAuth2AuthorizedClientProvider authorizedClientProvider =
                ReactiveOAuth2AuthorizedClientProviderBuilder.builder()
                        .authorizationCode()
                        .refreshToken(configurer -> configurer.accessTokenResponseClient(refreshTokenTokenResponseClient))
                        .build();

        DefaultReactiveOAuth2AuthorizedClientManager authorizedClientManager =
                new DefaultReactiveOAuth2AuthorizedClientManager(
                        clientRegistrationRepository, authorizedClientRepository);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return authorizedClientManager;
    }


    @Bean
    SecurityWebFilterChain configure(ServerHttpSecurity http) throws Exception {
        return http
                .authorizeExchange()
                .pathMatchers("/", "/error").permitAll()
                .pathMatchers("/**").authenticated()

                .and()
                .authenticationManager(new OAuth2AuthorizationCodeReactiveAuthenticationManager(webClientReactiveAuthorizationCodeTokenResponseClient()))

                .oauth2Login()
                .authorizationRequestResolver(authorizationRequestResolver)
                .authenticationFailureHandler(authenticationFailureHandler)
                .and()
                .logout()
                .disable()
                .build();
    }


}
