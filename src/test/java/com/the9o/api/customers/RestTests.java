package com.the9o.api.customers;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.the9o.api.customers.domain.Customer;



@RunWith(SpringRunner.class)
@SpringBootTest
public class RestTests {
	
	static final String JSON_PAYLOAD = "{ \"firstName\":\"Nino\", \"lastName\":\"Tan Cardoso\", \"email\":\"nino@the9o.com\", \"gender\":\"Male\", \"jobTitle\":\"Developer\"}";
	static final String JSON_UPDATE_PAYLOAD = "{ \"firstName\":\"Nino2\", \"lastName\":\"Tan Cardoso2\", \"email\":\"nino@the9o.com\", \"gender\":\"Male\", \"jobTitle\":\"Developer\"}";
	
	
	@Autowired 
	WebApplicationContext context;
	
	@Autowired 
	FilterChainProxy filterChain;
	
	private static final String REST_BASE_PATH = "/";
	private static final String CUSTOMERS_PATH = REST_BASE_PATH + "customers";
	
	private static String apiUser = "admin";
	private static String apiPass = "password";
	
	private static String API_CREDENTIALS = "Basic "+new String(Base64.encode((apiUser+":"+apiPass).getBytes()));
	

	MockMvc mvc;

	@Before
	public void setUp() {
		this.mvc = webAppContextSetup(context).addFilters(filterChain).build();
		SecurityContextHolder.clearContext();
	}

	@Test
	public void allowsPublicAccessToRoot() throws Exception {

		mvc.perform(get(REST_BASE_PATH).
				accept(MediaTypes.HAL_JSON)).
				andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON)).
				andExpect(status().isOk()).
				andDo(print());
	}

	@Test
	public void rejectsPublicPostAccessToCustomers() throws Exception {

		mvc.perform(post(CUSTOMERS_PATH).
				content(JSON_PAYLOAD).
				accept(MediaTypes.HAL_JSON)).
				andExpect(status().isUnauthorized()).
				andDo(print());
	}
	
	@Test
	public void rejectsPublicPutAccessToCustomers() throws Exception {

		mvc.perform(put(CUSTOMERS_PATH).
				content(JSON_PAYLOAD).
				accept(MediaTypes.HAL_JSON)).
				andExpect(status().isUnauthorized()).
				andDo(print());
	}
	
	@Test
	public void rejectsPublicPatchAccessToCustomers() throws Exception {

		mvc.perform(patch(CUSTOMERS_PATH).
				content(JSON_PAYLOAD).
				accept(MediaTypes.HAL_JSON)).
				andExpect(status().isUnauthorized()).
				andDo(print());
	}
	
	@Test
	public void allowsAdminPostAccessToCustomers() throws Exception {

		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaTypes.HAL_JSON_VALUE);
		headers.set(HttpHeaders.AUTHORIZATION, API_CREDENTIALS);
		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

		String location = mvc
				.perform(post(CUSTOMERS_PATH).
						content(JSON_PAYLOAD).
						headers(headers)).
				andExpect(status().isCreated()).
				andDo(print()).
				andReturn().getResponse().getHeader(HttpHeaders.LOCATION);

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new Jackson2HalModule()); 
		mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false); 

		String content = mvc.perform(get(location)).
				andReturn().getResponse().getContentAsString();
		
		Customer customer = mapper.readValue(content, Customer.class);

		assertThat(customer.getFirstName(), is("Nino"));
		assertThat(customer.getLastName(), is("Tan Cardoso"));
		assertThat(customer.getEmail(), is("nino@the9o.com"));
		assertThat(customer.getGender(), is("Male"));
		assertThat(customer.getJobTitle(), is("Developer"));
	}
	
	
	@Test
	public void allowsAdminPutAccessToCustomers() throws Exception {

		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaTypes.HAL_JSON_VALUE);
		headers.set(HttpHeaders.AUTHORIZATION, API_CREDENTIALS);

		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

		// Create
		String location = mvc
				.perform(post(CUSTOMERS_PATH).
						content(JSON_PAYLOAD).
						headers(headers)).
				andExpect(status().isCreated()).
				andDo(print()).
				andReturn().getResponse().getHeader(HttpHeaders.LOCATION);
		
		
		// Update 
		mvc.perform(put(location).
			content(JSON_UPDATE_PAYLOAD).
			headers(headers)).
			andExpect(status().isOk()).
			andDo(print()).
			andReturn().getResponse().getHeader(HttpHeaders.LOCATION);

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new Jackson2HalModule()); 
		mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false); 

		String content = mvc.perform(get(location)).
				andReturn().getResponse().getContentAsString();
		
		Customer customer = mapper.readValue(content, Customer.class);

		assertThat(customer.getFirstName(), is("Nino2"));
		assertThat(customer.getLastName(), is("Tan Cardoso2"));
		assertThat(customer.getEmail(), is("nino@the9o.com"));
		assertThat(customer.getGender(), is("Male"));
		assertThat(customer.getJobTitle(), is("Developer"));
	}
	
	
	@Test
	public void allowsAdminPatchAccessToCustomers() throws Exception {

		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaTypes.HAL_JSON_VALUE);
		headers.set(HttpHeaders.AUTHORIZATION, API_CREDENTIALS);

		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

		// Create
		String location = mvc
				.perform(post(CUSTOMERS_PATH).
						content(JSON_PAYLOAD).
						headers(headers)).
				andExpect(status().isCreated()).
				andDo(print()).
				andReturn().getResponse().getHeader(HttpHeaders.LOCATION);
		
		
		// Update 
		mvc.perform(patch(location).
			content(JSON_UPDATE_PAYLOAD).
			headers(headers)).
			andExpect(status().isOk()).
			andDo(print()).
			andReturn().getResponse().getHeader(HttpHeaders.LOCATION);

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new Jackson2HalModule()); 
		mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false); 

		String content = mvc.perform(get(location)).
				andReturn().getResponse().getContentAsString();
		
		Customer customer = mapper.readValue(content, Customer.class);

		assertThat(customer.getFirstName(), is("Nino2"));
		assertThat(customer.getLastName(), is("Tan Cardoso2"));
		assertThat(customer.getEmail(), is("nino@the9o.com"));
		assertThat(customer.getGender(), is("Male"));
		assertThat(customer.getJobTitle(), is("Developer"));
	}
	
	
	@Test
	public void allowsAdminDeleteAccessToCustomers() throws Exception {

		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaTypes.HAL_JSON_VALUE);
		headers.set(HttpHeaders.AUTHORIZATION, API_CREDENTIALS);

		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

		// Create
		String location = mvc
				.perform(post(CUSTOMERS_PATH).
						content(JSON_PAYLOAD).
						headers(headers)).
				andExpect(status().isCreated()).
				andDo(print()).
				andReturn().getResponse().getHeader(HttpHeaders.LOCATION);
		
		
		// Delete 
		mvc.perform(delete(location).
			headers(headers)).
			andExpect(status().isNoContent()).
			andDo(print());
		
		
	}
	
}
