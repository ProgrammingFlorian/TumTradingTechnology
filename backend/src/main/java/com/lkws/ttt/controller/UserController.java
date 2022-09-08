package com.lkws.ttt.controller;


import com.lkws.ttt.datatransferobjects.UserDTO;
import com.lkws.ttt.model.Authority;
import com.lkws.ttt.model.UserNotFoundException;
import com.lkws.ttt.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private final UserService userService;

    /**
     * GET /user
     *
     * @param authentication user authentication
     * @return the logged-in user
     */
    @Operation(summary = "Get logged-in user information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Found user",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "User not found. This should not happen",
                    content = @Content)
    })
    @GetMapping("/user")
    public ResponseEntity<UserDTO> getUser(Authentication authentication) {
        var username = authentication.getName();
        var user = userService.getUser(username).orElseThrow(() -> new UserNotFoundException("Logged-in user not found"));
        return new ResponseEntity<>(UserDTO.of(user), HttpStatus.OK);
    }

    /**
     * GET /users
     *
     * @return all existing users
     */
    @Operation(summary = "Get information of all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Found users",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = List.class)))
                    }),
            @ApiResponse(responseCode = "204", description = "There are no users",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content)
    })
    // Example of fine-tuned permissions
    @PreAuthorize("hasAuthority(\"" + Authority.USER + "\")")
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        var users = userService.getAllUsers();
        if (users.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            var userDTOs = users.stream().map(UserDTO::of).toList();
            return new ResponseEntity<>(userDTOs, HttpStatus.OK);
        }
    }

    @GetMapping("/users/cash")
    public ResponseEntity<Double> getCashOfUser(Authentication authentication) {
        var username = authentication.getName();
        var  user = userService.getUser(username).orElseThrow(() -> new UserNotFoundException("User not found"));
        return new ResponseEntity<>(user.getCash(), HttpStatus.OK);
    }
}
