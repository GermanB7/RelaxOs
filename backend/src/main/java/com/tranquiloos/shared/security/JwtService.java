package com.tranquiloos.shared.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tranquiloos.shared.error.UnauthorizedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

	private static final Base64.Encoder BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
	private static final Base64.Decoder BASE64_URL_DECODER = Base64.getUrlDecoder();

	private final ObjectMapper objectMapper;
	private final String jwtSecret;
	private final long expirationMinutes;

	public JwtService(
			ObjectMapper objectMapper,
			@Value("${app.security.jwt.secret:${JWT_SECRET:change_me_dev_secret_at_least_32_chars}}") String jwtSecret,
			@Value("${app.security.jwt.expiration-minutes:${JWT_EXPIRATION_MINUTES:1440}}") long expirationMinutes) {
		if (jwtSecret == null || jwtSecret.length() < 32) {
			throw new IllegalStateException("JWT secret must be at least 32 characters");
		}
		this.objectMapper = objectMapper;
		this.jwtSecret = jwtSecret;
		this.expirationMinutes = expirationMinutes;
	}

	public String generateToken(Long userId, String email) {
		Instant now = Instant.now();
		Map<String, Object> header = Map.of("alg", "HS256", "typ", "JWT");
		Map<String, Object> claims = new LinkedHashMap<>();
		claims.put("sub", String.valueOf(userId));
		claims.put("email", email);
		claims.put("iat", now.getEpochSecond());
		claims.put("exp", now.plusSeconds(expirationMinutes * 60).getEpochSecond());

		String headerPart = encodeJson(header);
		String claimsPart = encodeJson(claims);
		String signature = sign(headerPart + "." + claimsPart);
		return headerPart + "." + claimsPart + "." + signature;
	}

	public AuthenticatedUser validateAndExtract(String token) {
		String[] parts = token.split("\\.");
		if (parts.length != 3) {
			throw new UnauthorizedException("Invalid token");
		}
		String unsignedToken = parts[0] + "." + parts[1];
		if (!constantTimeEquals(sign(unsignedToken), parts[2])) {
			throw new UnauthorizedException("Invalid token");
		}
		Map<String, Object> claims = decodeJson(parts[1]);
		long exp = ((Number) claims.get("exp")).longValue();
		if (Instant.now().getEpochSecond() > exp) {
			throw new UnauthorizedException("Token has expired");
		}
		Long userId = Long.valueOf((String) claims.get("sub"));
		String email = (String) claims.get("email");
		return new AuthenticatedUser(userId, email);
	}

	private String encodeJson(Map<String, Object> value) {
		try {
			return BASE64_URL_ENCODER.encodeToString(objectMapper.writeValueAsBytes(value));
		} catch (Exception exception) {
			throw new IllegalStateException("Could not encode JWT", exception);
		}
	}

	private Map<String, Object> decodeJson(String encoded) {
		try {
			return objectMapper.readValue(BASE64_URL_DECODER.decode(encoded), new TypeReference<>() {
			});
		} catch (Exception exception) {
			throw new UnauthorizedException("Invalid token");
		}
	}

	private String sign(String value) {
		try {
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
			return BASE64_URL_ENCODER.encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
		} catch (Exception exception) {
			throw new IllegalStateException("Could not sign JWT", exception);
		}
	}

	private boolean constantTimeEquals(String expected, String actual) {
		return MessageDigestUtil.constantTimeEquals(expected.getBytes(StandardCharsets.UTF_8), actual.getBytes(StandardCharsets.UTF_8));
	}
}
