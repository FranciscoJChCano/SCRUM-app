package com.SCRUM.APP.controller;

import com.SCRUM.APP.model.ERole;
import com.SCRUM.APP.model.User;
import com.SCRUM.APP.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {

    @Mock
    private UserService userService;
    private MockMvc mockMvc;
    private User user;
    private List<User> userList;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        user = new User();
        user.setId(1L);
        user.setUsername("john_doe");
        user.setEmail("john@example.com");
        user.setPassword("password");
        user.setRole(ERole.USER);
        userList = new ArrayList<>();
        userList.add(user);
    }

    @Test
    void testCreateUser() throws Exception {
        when(userService.createUser(any(User.class))).thenReturn(user);

        String userJson = "{"
                + "\"id\": 1,"
                + "\"username\": \"john_doe\","
                + "\"email\": \"john@example.com\","
                + "\"password\": \"password\","
                + "\"role\": \"USER\""
                + "}";

        mockMvc.perform(post("/api/v1/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(content().json(userJson));
    }

    @Test
    void testGetAllUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(userList);

        String userJson = "["
                + "{"
                + "\"id\": 1,"
                + "\"username\": \"john_doe\","
                + "\"email\": \"john@example.com\","
                + "\"password\": \"password\","
                + "\"role\": \"USER\""
                + "}]";

        mockMvc.perform(get("/api/v1/users/list")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(userJson));
    }

    @Test
    void testGetUserById() throws Exception {
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));

        String userJson = "{"
                + "\"id\": 1,"
                + "\"username\": \"john_doe\","
                + "\"email\": \"john@example.com\","
                + "\"password\": \"password\","
                + "\"role\": \"USER\""
                + "}";

        mockMvc.perform(get("/api/v1/users/list/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(userJson));
    }

    @Test
    void testUpdateUser() throws Exception {
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setUsername("john_doe_updated");
        updatedUser.setEmail("john_updated@example.com");
        updatedUser.setPassword("new_password");
        updatedUser.setRole(ERole.ADMIN);
        when(userService.updateUser(anyLong(), any(User.class))).thenReturn(updatedUser);

        String userJson = "{"
                + "\"id\": 1,"
                + "\"username\": \"john_doe_updated\","
                + "\"email\": \"john_updated@example.com\","
                + "\"password\": \"new_password\","
                + "\"role\": \"ADMIN\""
                + "}";

        mockMvc.perform(put("/api/v1/users/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(content().json(userJson));
    }

    @Test
    void testDeleteUser() throws Exception {
        doNothing().when(userService).deleteUser(anyLong());

        mockMvc.perform(delete("/api/v1/users/delete/1"))
                .andExpect(status().isNoContent());
    }

}
