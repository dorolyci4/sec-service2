package org.socom.secservice.services;

import org.socom.secservice.entities.AppRole;
import org.socom.secservice.entities.AppUser;
import org.socom.secservice.repository.AppRoleRepository;
import org.socom.secservice.repository.AppUserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {
    private PasswordEncoder passwordEncoder;
    private AppUserRepository appUserRepository;
    private AppRoleRepository appRoleRepository;

    public AccountServiceImpl(@Lazy PasswordEncoder passwordEncoder, AppUserRepository appUserRepository, AppRoleRepository appRoleRepository) {
        this.passwordEncoder = passwordEncoder;
        this.appUserRepository = appUserRepository;
        this.appRoleRepository = appRoleRepository;
    }

    @Override
    public AppUser addNewUser(AppUser appUser) {
        if (appUser != null) {
            String password = appUser.getPassword();
            appUser.setPassword(passwordEncoder.encode(password));
            return appUserRepository.save(appUser);
        } else
            return null;
    }

    @Override
    public AppRole addNewRole(AppRole appRole) {

        return appRoleRepository.save(appRole);
    }

    @Override
    public void addRoleToUser(String userName, String roleName) {
        AppUser appUser = appUserRepository.findByUserName(userName);
        AppRole appRole = appRoleRepository.findByRoleName(roleName);
        appUser.getAppRoles().add(appRole);
    }

    @Override
    public AppUser loadUserByUserName(String userName) {

        return appUserRepository.findByUserName(userName);
    }

    @Override
    public List<AppUser> listUsers() {

        return appUserRepository.findAll();
    }
}
