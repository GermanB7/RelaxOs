package com.tranquiloos.shared.security;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
				.csrf(AbstractHttpConfigurer::disable)
				.cors(Customizer.withDefaults())
				.sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(HttpMethod.POST, "/api/v1/auth/register", "/api/v1/auth/login").permitAll()
						.requestMatchers(HttpMethod.GET, "/actuator/health", "/api/v1/system/status").permitAll()
						.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
						.anyRequest().authenticated())
				.exceptionHandling(exception -> exception
						.authenticationEntryPoint((request, response, authException) -> {
							response.setStatus(HttpStatus.UNAUTHORIZED.value());
							response.setContentType("application/json");
							response.getWriter().write("{\"code\":\"UNAUTHORIZED\",\"message\":\"Authentication is required\",\"details\":[]}");
						}))
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
				.httpBasic(AbstractHttpConfigurer::disable)
				.formLogin(AbstractHttpConfigurer::disable)
				.logout(AbstractHttpConfigurer::disable)
				.build();
	}

	@Bean
	UserDetailsService userDetailsService() {
		return new InMemoryUserDetailsManager();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
