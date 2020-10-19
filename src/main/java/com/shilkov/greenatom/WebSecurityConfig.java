package com.shilkov.greenatom;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/**").hasRole("filemanager").and().formLogin().permitAll();
		http.csrf().disable();
	}

	@SuppressWarnings("deprecation")
	@Bean
	@Override
	public UserDetailsService userDetailsService() {
		UserDetails user1 = User.withDefaultPasswordEncoder().username("Антон").password("1111").roles("filemanager")
				.build();
		
		UserDetails user2 = User.withDefaultPasswordEncoder().username("Михаил").password("1234").roles("filemanager")
				.build();
		
		UserDetails user3 = User.withDefaultPasswordEncoder().username("Григорий").password("1111").roles("filemanager")
				.build();

		return new InMemoryUserDetailsManager(user1, user2, user3);
	}
}
