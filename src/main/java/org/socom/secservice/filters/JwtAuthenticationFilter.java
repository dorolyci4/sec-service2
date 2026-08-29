package org.socom.secservice.filters;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Date;
import java.util.stream.Collectors;

//@Component
@SuppressWarnings("NullableProblems")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    //private AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.setAuthenticationManager(authenticationManager);
        this.setFilterProcessesUrl("/api/auth/login");
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        System.out.println("attemptAuthentication");
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || password == null) {
            System.out.println("❌ Username ou password null !");
            throw new RuntimeException("Username et password requis");
        }
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);


        try {
            Authentication auth = this.getAuthenticationManager().authenticate(authenticationToken);
            System.out.println("✅ Authentification réussie !");
            return auth;
        } catch (Exception e) {
            System.out.println("❌ Erreur d'authentification: " + e.getMessage());
            throw e;
        }


        // return authenticationManager.authenticate(authenticationToken);
    }


    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        System.out.println("successfulAuthentication");
        User user = (User) authResult.getPrincipal();
        Algorithm algorithm = Algorithm.HMAC256("mysecret1234");
        assert user != null;
        String jwtAccessToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 5 * 60 * 1000))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .sign(algorithm);


        response.addHeader("Authorization", jwtAccessToken);  // ✅ CORRECT
        //response.addHeader("Authorization", "Bearer " + jwtAccessToken);
        //response.setContentType("application/json");
        //response.getWriter().write("{\"token\": \"" + jwtAccessToken + "\"}");
        // 🔥 BODY - Ajoutez ces 3 lignes pour voir le token dans Postman
        response.setContentType("application/json");
        response.getWriter().write("{\"token\": \"" + jwtAccessToken + "\"}");
    }
}
