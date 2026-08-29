package org.socom.secservice.services;

import org.socom.secservice.entities.AppUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountService accountService; // Injection de votre repository

    public CustomUserDetailsService(AccountService accountService) {
        this.accountService = accountService;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 1. Recherche de l'utilisateur dans la base de données
        AppUser appUser = accountService.loadUserByUserName(username);

        // Correction ici : Si l'utilisateur n'existe pas, on lève l'exception attendue par Spring Security
        if (appUser == null) {
            throw new UsernameNotFoundException("Utilisateur non trouvé : " + username);
        }

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        if (appUser.getAppRoles() != null) {
            appUser.getAppRoles().forEach(r -> authorities.add(new SimpleGrantedAuthority(r.getRoleName())));
        }

        // 2. Conversion et retour de l'objet UserDetails requis par Spring Security
        return new User(appUser.getUserName(), appUser.getPassword(), authorities);
    }


}
