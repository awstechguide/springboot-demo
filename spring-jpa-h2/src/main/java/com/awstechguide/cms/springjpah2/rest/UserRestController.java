package com.awstechguide.cms.springjpah2.rest;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.awstechguide.cms.springjpah2.dto.UserProfile;
import com.awstechguide.cms.springjpah2.entity.User;
import com.awstechguide.cms.springjpah2.repository.UserRepository;
import com.awstechguide.cms.springjpah2.service.UserService;


@RestController
@RequestMapping("/user")
public class UserRestController {

    @Autowired
    private UserRepository repository;
    
    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    
    
    @PostMapping("/test/create")
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserProfile userProfile) {
    	String encryptedPwd = passwordEncoder.encode(userProfile.getUser().getPassword());
    	userProfile.getUser().setPassword(encryptedPwd);
        return  userService.saveUser(userProfile);
    }
    
    @PutMapping("/test/update/{id}")
    public ResponseEntity<Object> updateUser(@Valid @RequestBody UserProfile profile) {    	
    	return userService.updateUser(profile);
    }
    
    @DeleteMapping("/test/delete/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }

    
    @GetMapping("/test/find/{userName}")
    public User getUserByUserName(@PathVariable String userName) {
    	System.out.println("calling getUserByUserName");
    	return repository.findByUserName(userName).get();
    }
    
    /*

    @PostMapping("/join")
    public User joinGroup(@RequestBody User user) {
        user.setRoles(RoleConstant.DEFAULT_ROLE);//USER
        String encryptedPwd = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPwd);
        System.out.println("username "+user.getUserName()+" Passwd "+user.getPassword() + "role "+user.getRoles());
               
        return repository.save(user);
       // return "Hi " + user.getUserName() + " welcome to group !";
    }
    //If loggedin user is ADMIN -> ADMIN OR MODERATOR
    //If loggedin user is MODERATOR -> MODERATOR

    @GetMapping("/access/{userId}/{userRole}")
    //@Secured("ROLE_ADMIN")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MODERATOR')")
    public String giveAccessToUser(@PathVariable int userId, @PathVariable String userRole, Principal principal) {
        User user = repository.findById(userId).get();
        List<String> activeRoles = getRolesByLoggedInUser(principal);
        String newRole = "";
        if (activeRoles.contains(userRole)) {
            newRole = user.getRoles() + "," + userRole;
            user.setRoles(newRole);
        }
        repository.save(user);
        return "Hi " + user.getUserName() + " New Role assign to you by " + principal.getName();
    }


    @GetMapping
    @Secured("ROLE_ADMIN")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<User> loadUsers() {
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

    private User getLoggedInUser(Principal principal) {
        return repository.findByUserName(principal.getName()).get();
    }
    
    */
}