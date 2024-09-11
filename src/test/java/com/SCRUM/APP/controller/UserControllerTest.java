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

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    private User existingUser;
    private User newUser;
    private User updatedUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        existingUser = new User(1L, "oldUsername", "oldemail@example.com", "oldPassword", ERole.USER, Collections.emptyList(), Collections.emptyList());
        newUser = new User(null, "testuser", "test@example.com", "password", ERole.USER, Collections.emptyList(), Collections.emptyList());
        updatedUser = new User(1L, "updatedUsername", "updated@example.com", "newPassword", ERole.USER, Collections.emptyList(), Collections.emptyList());
    }

    @Test
    void testCreateUser() throws Exception {
        // Arrange
        when(userService.createUser(any(User.class))).thenReturn(newUser);

        // Act & Assert
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\",\"email\":\"test@example.com\",\"password\":\"password\",\"role\":\"USER\",\"tasks\":[],\"projectsList\":[]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.password").value("password"));

        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void testGetAllUsers() throws Exception {
        // Arrange
        when(userService.getAllUsers()).thenReturn(Arrays.asList(existingUser, newUser));

        // Act & Assert
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("oldUsername"))
                .andExpect(jsonPath("$[1].username").value("testuser"));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void testGetUserById() throws Exception {
        // Arrange
        when(userService.getUserById(1L)).thenReturn(Optional.of(existingUser));

        // Act & Assert
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("oldUsername"));

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void testGetUserByIdNotFound() throws Exception {
        // Arrange
        when(userService.getUserById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void testUpdateUser() throws Exception {
        // Arrange
        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(updatedUser);

        // Act & Assert
        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"updatedUsername\",\"email\":\"updated@example.com\",\"password\":\"newPassword\",\"role\":\"USER\",\"tasks\":[],\"projectsList\":[]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updatedUsername"))
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.password").value("newPassword"));

        verify(userService, times(1)).updateUser(eq(1L), any(User.class));
    }

    @Test
    void testUpdateUserNotFound() throws Exception {
        // Arrange
        when(userService.updateUser(eq(1L), any(User.class))).thenThrow(new RuntimeException("User not found with id 1"));

        // Act & Assert
        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"updatedUsername\",\"email\":\"updated@example.com\",\"password\":\"newPassword\",\"role\":\"USER\",\"tasks\":[],\"projectsList\":[]}"))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).updateUser(eq(1L), any(User.class));
    }

    @Test
    void testDeleteUser() throws Exception {
        // Arrange
        doNothing().when(userService).deleteUser(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    void testDeleteUserNotFound() throws Exception {
        // Arrange
        doThrow(new RuntimeException("User not found with id 1")).when(userService).deleteUser(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).deleteUser(1L);
    }
}
