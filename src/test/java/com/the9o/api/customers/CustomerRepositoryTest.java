package com.the9o.api.customers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.the9o.api.customers.domain.Customer;
import com.the9o.api.customers.repo.CustomerRepository;

@ActiveProfiles("test")
@WebMvcTest(CustomerRepository.class)
@AutoConfigureMockMvc
public class CustomerRepositoryTest {

	@Value("${api.user}")
	private String API_USER;

	@Value("${api.pass}")
	private String API_PASS;

	private static final String JSON_PAYLOAD = "{ \"firstName\":\"Nino\", \"lastName\":\"Tan Cardoso\", \"email\":\"nino@the9o.com\", \"gender\":\"Male\", \"jobTitle\":\"Developer\"}";

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private CustomerRepository customerRpository;

	@Test
	public void testCreateCustomer() throws Exception {
		Customer customer = new Customer("Test", "Test", "test@test.com", "Male", "Tester");
		when(customerRpository.save(any(Customer.class))).thenReturn(customer);

		mockMvc.perform(post("/customers").with(user(API_USER).password(API_PASS).roles("ADMIN")).with(csrf())
				.contentType(MediaType.APPLICATION_JSON).content(JSON_PAYLOAD)).andExpect(status().isCreated())
				.andExpect(jsonPath("$.firstName").value("Nino"));
	}

	@Test
	public void testGetCustomer() throws Exception {
		mockMvc.perform(get("/").with(user(API_USER).password(API_PASS).roles("ADMIN")).with(csrf()))
			.andExpect(status().isOk());

	}

}
