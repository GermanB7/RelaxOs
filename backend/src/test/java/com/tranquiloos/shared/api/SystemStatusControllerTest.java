package com.tranquiloos.shared.api;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SystemStatusController.class)
@AutoConfigureMockMvc(addFilters = false)
class SystemStatusControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void returnsFoundationStatus() throws Exception {
		mockMvc.perform(get("/api/v1/system/status"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.app", is("TranquiloOS")))
				.andExpect(jsonPath("$.status", is("UP")))
				.andExpect(jsonPath("$.version", is("0.0.1")));
	}
}
