package com.lkws.ttt.testvalues;

import com.lkws.ttt.datatransferobjects.RegisterDTO;
import com.lkws.ttt.model.User;

import java.util.List;

public class UserTestValues {

    public static final String LOGGED_IN_TEST_USER = "user1";

    public static User loggedInTestUser = new User(LOGGED_IN_TEST_USER, "password1");
    public static User user2 = new User("user2", "password2");
    public static User user3 = new User("user3", "password3");

    public static User brokeUser = new User("userBroke", "password4", 0);

    public static RegisterDTO createUser = new RegisterDTO("user", "password");
    public static User createdUser = new User("user", "passwordEncrypt");

    public static List<User> userList = List.of(loggedInTestUser, user2, user3);

}
