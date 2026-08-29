package org.socom.secservice.web;

import lombok.Data;
import org.socom.secservice.entities.AppRole;
import org.socom.secservice.entities.AppUser;
import org.socom.secservice.services.AccountService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/account")
public class AccountRestController {
    private final AccountService accountService;

    public AccountRestController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping(path = "/users")
    public List<AppUser> appUsers(){
        return accountService.listUsers();
    }

    @PostMapping(path = "/users")
    public AppUser saveUser(@RequestBody AppUser appUser){
        return accountService.addNewUser(appUser);
    }

    @PostMapping(path = "/roles")
    public AppRole saveRole(@RequestBody AppRole appRole){
        return accountService.addNewRole(appRole);
    }

    @PostMapping(path = "/addRoleToUser")
    public void addRoleToUser(@RequestBody RoleUserFrom RoleUserFrom){
        accountService.addRoleToUser(RoleUserFrom.getUserName(),RoleUserFrom.getRoleName());
    }
    @GetMapping(path = "/users/{userName}")
    public AppUser getUser(@PathVariable String userName){
        return accountService.loadUserByUserName(userName);
    }


    @Data
    public static class RoleUserFrom{
        private String userName;
        private String roleName;
    }
}
