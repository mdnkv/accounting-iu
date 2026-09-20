package dev.mednikov.accounting.users.controllers;

import dev.mednikov.accounting.users.dto.UserDto;
import dev.mednikov.accounting.users.dto.UserDtoMapper;
import dev.mednikov.accounting.users.models.User;
import dev.mednikov.accounting.users.services.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/current")
    public @ResponseBody UserDto getCurrentUser(@AuthenticationPrincipal Jwt jwt){
        User currentUser = this.userService.getOrCreateUser(jwt);
        UserDtoMapper mapper = new UserDtoMapper();
        return mapper.apply(currentUser);
    }

}
