package br.com.chatbot.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import br.com.chatbot.config.security.filter.SecurityFilter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
// @EnableWebSecurity permitiria autorizazao nos proprios controllers
public class SecurityConfigurations {

        private final SecurityFilter securityFilter;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                return http
                                .csrf(AbstractHttpConfigurer::disable)
                                .sessionManagement(sm -> sm
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(

                                        "/login",
                                        "/health-check",
                                        "/error",
                                        "/v3/api-docs",
                                        "/v3/api-docs/**",
                                        "/swagger-ui/**",
                                        "/swagger-ui.html")
                                                .permitAll()

                                                .requestMatchers(HttpMethod.GET,
                                                                "/mensagens/**")
                                                .permitAll()

                                                .requestMatchers(
                                                                "/mensagens/**",
                                                                "/usuarios/**")
                                                .hasAnyRole("ADMIN", "OWNER")

                                                .requestMatchers(
                                                                "/auth-logins/**")
                                                .hasRole("OWNER")

                                                .anyRequest()
                                                .authenticated())
                                .addFilterBefore(
                                                securityFilter,
                                                UsernamePasswordAuthenticationFilter.class)
                                .build();
        }

        @Bean
        public AuthenticationManager authenticationManager(
                        AuthenticationConfiguration configuration) throws Exception {
                return configuration.getAuthenticationManager();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}
