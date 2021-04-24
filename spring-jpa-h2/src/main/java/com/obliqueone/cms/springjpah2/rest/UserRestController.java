package com.obliqueone.cms.springjpah2.rest;

import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.obliqueone.cms.springjpah2.common.RoleConstant;
import com.obliqueone.cms.springjpah2.entity.UserEntity;
import com.obliqueone.cms.springjpah2.repository.UserRepository;


@RestController
@RequestMapping("/user")
public class UserRestController {

    @Autowired
    private UserRepository repository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/join")
    public String joinGroup(@RequestBody UserEntity user) {
        user.setRoles(RoleConstant.DEFAULT_ROLE);//USER
        String encryptedPwd = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPwd);
        System.out.println("username "+user.getUserName()+" Passwd "+user.getPassword() + "role "+user.getRoles());
               
        repository.save(user);
        return "Hi " + user.getUserName() + " welcome to group !";
    }
    //If loggedin user is ADMIN -> ADMIN OR MODERATOR
    //If loggedin user is MODERATOR -> MODERATOR

    @GetMapping("/access/{userId}/{userRole}")
    //@Secured("ROLE_ADMIN")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MODERATOR')")
    public String giveAccessToUser(@PathVariable int userId, @PathVariable String userRole, Principal principal) {
        UserEntity user = repository.findById(userId).get();
        List<String> activeRoles = getRolesByLoggedInUser(principal);
        String newRole = "";
        if (activeRoles.contains(userRole)) {
            newRole = user.getRoles() + "," + userRole;
            user.setRoles(newRole);
        }
        repository.save(user);
        return "Hi " + user.getUserName() + " New Role assign to you by " + principal.getName();
    }
    
    @GetMapping("/find/{userName}")
    public UserEntity getUserByUserName(@PathVariable String userName) {
    	System.out.println("calling getUserByUserName");
    	return repository.findByUserName(userName).get();
    }

    @GetMapping
    @Secured("ROLE_ADMIN")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<UserEntity> loadUsers() {
        return repository.findAll();
    }

    @GetMapping("/test")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String testUserAccess() {
        return "user can only access this !";
    }

    private List<String> getRolesByLoggedInUser(Principal principal) {
        String roles = getLoggedInUser(principal).getRoles();
        List<String> assignRoles = Arrays.stream(roles.split(",")).collect(Collectors.toList());
        if (assignRoles.contains("ROLE_ADMIN")) {
            return Arrays.stream(RoleConstant.ADMIN_ACCESS).collect(Collectors.toList());
        }
        if (assignRoles.contains("ROLE_MODERATOR")) {
            return Arrays.stream(RoleConstant.MODERATOR_ACCESS).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private UserEntity getLoggedInUser(Principal principal) {
        return repository.findByUserName(principal.getName()).get();
    }
}