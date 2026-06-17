package net.ankan.ems.controller;

import jakarta.validation.Valid;
import net.ankan.ems.dto.UpdateNameRequest;
import net.ankan.ems.dto.UserResponseDto;
import net.ankan.ems.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // GET /api/users/me — own profile (ADMIN + USER)
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUser() {
        UserResponseDto currentUser = userService.getCurrentUserProfile();
        return ResponseEntity.ok(currentUser);
    }

    // PUT /api/users/me/name — update own name (ADMIN + USER)
    @PutMapping("/me/name")
    public ResponseEntity<UserResponseDto> updateName(@Valid @RequestBody UpdateNameRequest request) {
        UserResponseDto updated = userService.updateCurrentUserName(request);
        return ResponseEntity.ok(updated);
    }
}