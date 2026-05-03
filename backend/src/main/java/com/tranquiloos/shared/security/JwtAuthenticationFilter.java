package com.tranquiloos.shared.security;

import java.io.IOException;
import java.util.List;

import com.tranquiloos.shared.error.UnauthorizedException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;

	public JwtAuthenticationFilter(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String authorization = request.getHeader("Authorization");
		if (authorization == null || !authorization.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}
		try {
			AuthenticatedUser user = jwtService.validateAndExtract(authorization.substring(7));
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, List.of());
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authentication);
			filterChain.doFilter(request, response);
		} catch (UnauthorizedException exception) {
			SecurityContextHolder.clearContext();
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");
			response.getWriter().write("{\"code\":\"UNAUTHORIZED\",\"message\":\"Invalid or expired token\",\"details\":[]}");
		}
	}
}
