package org.socom.secservice.services;

import org.socom.secservice.entities.AppRole;
import org.socom.secservice.entities.AppUser;

import java.util.List;

public interface AccountService {
    AppUser addNewUser(AppUser appUser);
    AppRole addNewRole(AppRole appRole);
    void addRoleToUser(String userName,String roleName);
    AppUser loadUserByUserName(String userName);
    List<AppUser> listUsers();

}
