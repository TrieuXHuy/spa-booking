package com.group8.spabooking;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group8.spabooking.service.PasswordService;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SpabookingApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private PasswordService passwordService;

	@Test
	void contextLoads() {
	}

	@Test
	void supportsExistingBcryptAdminPassword() {
		String adminPasswordHash = new BCryptPasswordEncoder().encode("admin123");

		assertTrue(passwordService.matches("admin123", adminPasswordHash));
	}

	@Test
	void loginSeededAdminWithDefaultPassword() throws Exception {
		Map<String, String> loginRequest = Map.of(
				"username", "admin",
				"password", "admin123");

		mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(loginRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Đăng nhập thành công"))
				.andExpect(jsonPath("$.user.username").value("admin"))
				.andExpect(jsonPath("$.user.role.name").value("ADMIN"));
	}

	@Test
	void registerThenLoginWithUsernameAndPassword() throws Exception {
		Map<String, String> registerRequest = Map.of(
				"fullName", "Nguyen Van A",
				"username", "customer01",
				"password", "secret123",
				"email", "customer01@example.com",
				"phone", "0900000001",
				"gender", "Nam");

		mockMvc.perform(post("/api/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(registerRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Đăng ký tài khoản thành công"))
				.andExpect(jsonPath("$.user.username").value("customer01"))
				.andExpect(jsonPath("$.user.role.name").value("CUSTOMER"));

		Map<String, String> loginRequest = Map.of(
				"username", "customer01",
				"password", "secret123");

		mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(loginRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Đăng nhập thành công"))
				.andExpect(jsonPath("$.user.username").value("customer01"))
				.andExpect(jsonPath("$.user.role.name").value("CUSTOMER"));

		Map<String, String> emailLoginRequest = Map.of(
				"username", "customer01@example.com",
				"password", "secret123");

		mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(emailLoginRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Đăng nhập thành công"))
				.andExpect(jsonPath("$.user.username").value("customer01"))
				.andExpect(jsonPath("$.user.role.name").value("CUSTOMER"));
	}

}
