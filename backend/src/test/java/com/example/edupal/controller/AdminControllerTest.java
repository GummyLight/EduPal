package com.example.edupal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.edupal.common.Result;
import com.example.edupal.dto.request.RegisterRequest;
import com.example.edupal.dto.response.ListUserResponse;
import com.example.edupal.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.mockito.Mockito.*;

@WebMvcTest(AdminController.class)
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
    }

    @Test
    public void registerUserWithValidDataReturnsSuccess() throws Exception {
        RegisterRequest request = new RegisterRequest("testUser", "123", "password", "test@example.com", 0, null, null, null, null);

        when(authService.registerUser(anyString(), anyString(), anyString(), anyString(), anyInt()))
                .thenReturn(new Result(true, "注册成功"));

        mockMvc.perform(MockMvcRequestBuilders.post("/admin/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("注册成功"));

        verify(authService, times(1)).registerUser(anyString(), anyString(), anyString(), anyString(), anyInt());
    }

    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}