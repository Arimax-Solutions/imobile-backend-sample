package com.I_Mobile_Crazy.I_Mobile_Crazy.controller;

import com.I_Mobile_Crazy.I_Mobile_Crazy.entity.User;
import com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom.UserService;
import com.I_Mobile_Crazy.I_Mobile_Crazy.util.payload.respond.StandardResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @author : Chanuka Weerakkody
 * @since : 20.1.1
 **/
@Slf4j
@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
@CrossOrigin
public class UserController {
    private final UserService userService;

    @DeleteMapping("/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponse> deleteUser(@PathVariable Integer id) {
        userService.delete(id);
        return new ResponseEntity<>(
                new StandardResponse(200, "User Deleted", null), HttpStatus.OK);
    }

    @GetMapping
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponse> getAllUsers() {
        return new ResponseEntity<>(
                new StandardResponse(200, "Users Fetched", userService.findAll()), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponse> getUserById(@PathVariable Integer id) {
        return new ResponseEntity<>(
                new StandardResponse(200, "User Fetched", userService.findById(id)), HttpStatus.OK);
    }


    @PutMapping("/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponse> updateUser(@PathVariable("id") Integer id, @RequestBody User updatedUserData) {
        try {
            updatedUserData.setUser_id(id);
            userService.update(updatedUserData);

            return ResponseEntity.ok(new StandardResponse(200, "User updated successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new StandardResponse(500, "Failed to update user", null));
        }
    }
}
