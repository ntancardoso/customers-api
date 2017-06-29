package com.the9o.api.customers.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	@Value("${api.user:admin}")
	private String apiUser;
	@Value("${api.pass:j7CHmTNmM!}")
	private String apiPass;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		//TODO Added Basic InMemory Auth for DEMO purpose only.
		auth.inMemoryAuthentication().
				withUser(apiUser).password(apiPass).roles("USER", "ADMIN");
	}


	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http.httpBasic().and().authorizeRequests().
				antMatchers(HttpMethod.POST, "/customers").hasRole("ADMIN").
				antMatchers(HttpMethod.PUT, "/customers/**").hasRole("ADMIN").
				antMatchers(HttpMethod.DELETE, "/customers/**").hasRole("ADMIN").
				antMatchers(HttpMethod.PATCH, "/customers/**").hasRole("ADMIN").and().
				addFilterBefore(new CorsFilter(), ChannelProcessingFilter.class).
				csrf().disable();
	}
}
