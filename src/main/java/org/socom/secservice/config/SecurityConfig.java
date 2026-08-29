package org.socom.secservice.config;

import org.socom.secservice.filters.JwtAuthenticationFilter;
import org.socom.secservice.services.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    //private final JwtAuthenticationFilter jwtAuthenticationFilter; // 🔥 Injecter le filtre

    // Injection du UserDetailsService créé à l'étape 1
    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
        //this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Désactiver la protection CSRF car elle repose sur les sessions
                .csrf(AbstractHttpConfigurer::disable) // Requis pour pouvoir utiliser la console H2 ou faire des POST/PUT, csrf utilise la session
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                // Définir la politique de session sur STATELESS
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // 🔥 DÉSACTIVER formLogin (cause principale des 302)
                // .formLogin(Customizer.withDefaults())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll() //// Autorise l'accès à toutes les pages (y compris H2 et vos APIs)
                        .requestMatchers("/api/auth/login").permitAll()
                        .anyRequest().authenticated()
                )

                // Ajouter le filtre JWT
                .addFilter(jwtAuthenticationFilter())// 🔥 Ajouter le filtre d'authentification (gère le login)
                // Requis pour afficher l'interface H2 dans les frames
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        return new JwtAuthenticationFilter(authenticationManager(null));
    }


    // 🔥 Bean pour créer le filtre avec l'AuthenticationManager

    // Déclaration de l'encodeur de mot de passe utilisé pour la vérification
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean obligatoire si vous gérez le login manuellement via un Controller REST
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) {
        return authConfig.getAuthenticationManager();
    }


}
