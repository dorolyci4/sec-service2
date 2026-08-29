package org.socom.secservice.config;

import org.socom.secservice.services.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    // Injection du UserDetailsService créé à l'étape 1
    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .formLogin(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated() // Autorise l'accès à toutes les pages (y compris H2 et vos APIs)
                )
                //.csrf(AbstractHttpConfigurer::disable) // Requis pour pouvoir utiliser la console H2 ou faire des POST/PUT
                .headers(headers -> headers.frameOptions(frame -> frame.disable())); // Requis pour afficher l'interface H2 dans les frames

        return http.build();
    }
    // Déclaration de l'encodeur de mot de passe utilisé pour la vérification
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean obligatoire si vous gérez le login manuellement via un Controller REST
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }


}
