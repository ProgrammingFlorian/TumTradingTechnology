package com.lkws.ttt.services;

import com.lkws.ttt.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.validation.ValidationException;
import java.util.Optional;

import static com.lkws.ttt.testvalues.UserTestValues.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void testGetUser() {
        // given
        when(userRepository.findByUsername(user2.getUsername())).thenReturn(Optional.ofNullable(user2));
        // when
        var result = userService.getUser(user2.getUsername());
        // then
        assertTrue(result.isPresent());
        assertEquals(user2, result.get());
    }

    @Test
    void testGetUserNotFound() {
        // given
        when(userRepository.findByUsername(user2.getUsername())).thenReturn(Optional.empty());
        // when
        var result = userService.getUser(user2.getUsername());
        // then
        assertTrue(result.isEmpty());
    }

    @Test
    void testCreateUser() {
        // given
        when(userRepository.findByUsername(createUser.username())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(createUser.password())).thenReturn(createdUser.getPassword());
        when(userRepository.save(createdUser)).thenReturn(createdUser);
        // when
        var user = userService.create(createUser);
        // then
        assertEquals(createdUser, user);
    }

    @Test
    void testCreateUserAlreadyExists() {
        // given
        when(userRepository.findByUsername(createUser.username())).thenReturn(Optional.ofNullable(createdUser));
        // when then
        assertThrows(ValidationException.class, () -> userService.create(createUser));
    }

    @Test
    void testGetAllUsers() {
        // given
        when(userRepository.findAll()).thenReturn(userList);
        // when
        var result = userService.getAllUsers();
        // then
        assertEquals(result, userList);
    }

}
