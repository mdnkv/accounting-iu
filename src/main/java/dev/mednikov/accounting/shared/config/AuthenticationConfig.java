package dev.mednikov.accounting.shared.config;

import dev.mednikov.accounting.shared.auth.RoleConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
//@Profile({"prod", "dev"})
@EnableWebSecurity
@EnableMethodSecurity
public class AuthenticationConfig {

    private final RoleConverter roleConverter;

    public AuthenticationConfig(RoleConverter roleConverter) {
        this.roleConverter = roleConverter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error/**").permitAll()
                        .anyRequest().authenticated()
                )
                .cors(Customizer.withDefaults())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(roleConverter)));

        return http.build();
    }

}
