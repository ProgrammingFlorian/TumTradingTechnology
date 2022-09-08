package com.lkws.ttt.controller;

import com.lkws.ttt.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.Optional;

import static com.lkws.ttt.testvalues.UserTestValues.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
@ContextConfiguration(classes = UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser(username = LOGGED_IN_TEST_USER)
    public void testGetUser() throws Exception {
        // given
        when(userService.getUser(loggedInTestUser.getUsername()))
                .thenReturn(Optional.of(loggedInTestUser));
        // when
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/user");
        // then
        String result = mockMvc.perform(requestBuilder).andReturn().getResponse().getContentAsString();
        JSONAssert.assertEquals("{\"username\":\"user1\", \"cash\":50000.0}", result, false);
    }

    @Test
    public void testGetUserUnauthorized() throws Exception {
        // when
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/user");
        // then
        assertEquals(401, mockMvc.perform(requestBuilder).andReturn().getResponse().getStatus());
    }

    @Test
    @WithMockUser(username = LOGGED_IN_TEST_USER)
    public void testGetAllUsers() throws Exception {
        // given
        when(userService.getAllUsers())
                .thenReturn(userList);
        // when
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/users");
        // then
        String result = mockMvc.perform(requestBuilder).andReturn().getResponse().getContentAsString();
        JSONAssert.assertEquals("[{\"username\":\"user1\", \"cash\":50000.0}," +
                "{\"username\":\"user2\", \"cash\":50000.0}," +
                "{\"username\":\"user3\", \"cash\":50000.0}]", result, false);
    }

    @Test
    @WithMockUser(username = LOGGED_IN_TEST_USER)
    public void testGetAllUsersEmpty() throws Exception {
        // given
        when(userService.getAllUsers())
                .thenReturn(Collections.emptyList());
        // when
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/users");
        // then
        assertEquals(204, mockMvc.perform(requestBuilder).andReturn().getResponse().getStatus());
    }
}
