package com.lkws.ttt.controller;

import com.lkws.ttt.datatransferobjects.LoginDTO;
import com.lkws.ttt.datatransferobjects.RegisterDTO;
import com.lkws.ttt.datatransferobjects.TokenDTO;
import com.lkws.ttt.datatransferobjects.UserDTO;
import com.lkws.ttt.model.User;
import com.lkws.ttt.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Instant;

import static java.util.stream.Collectors.joining;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    // How long the token is valid for (in seconds)
    // Currently set to 10 hours: 10 * 60 * 60
    private static final long TOKEN_VALIDITY_TIME = 36000L;
    private static final String JWT_ISSUER = "ttt.backend";
    public static final String JWT_ROLES = "roles";
    public static final String JWT_ROLE_PREFIX = "";

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;

    private final UserService userService;

    /**
     * POST /login
     *
     * @param login login information
     * @return user token or unauthorized response code
     */
    @Operation(summary = "Login to receive user token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = {@Content(mediaType = "application/json", schema =
                    @Schema(implementation = TokenDTO.class))}),
            @ApiResponse(responseCode = "401", description = "Wrong username/password",
                    content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody @Valid LoginDTO login) {
        try {
            var authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(login.username(), login.password()));
            var user = (User) authentication.getPrincipal();

            String roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(joining(" "));

            JwtClaimsSet claims = JwtClaimsSet.builder()
                    .issuer(JWT_ISSUER)
                    .issuedAt(Instant.now())
                    .expiresAt(Instant.now().plusSeconds(TOKEN_VALIDITY_TIME))
                    // Set token subject to username
                    // Should be unique, otherwise id can be used
                    .subject(user.getUsername())
                    .claim(JWT_ROLES, roles)
                    .build();

            String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

            return ResponseEntity.ok().body(TokenDTO.of(token));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * POST /register
     *
     * @param request information about user to register
     * @return user token or unauthorized response code
     */
    @Operation(summary = "Register a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Registration successful, newly created user will be returned",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class))}),
    })
    @PostMapping("/register")
    public UserDTO register(@RequestBody @Valid RegisterDTO request) {
        return UserDTO.of(userService.create(request));
    }

}
