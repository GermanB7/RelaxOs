package com.tranquiloos.users.application;

import com.tranquiloos.shared.error.UnauthorizedException;
import com.tranquiloos.shared.security.AuthenticatedUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserProvider {

	public Long currentUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()
				|| !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {
			throw new UnauthorizedException("Authentication is required");
		}
		return user.id();
	}
}
