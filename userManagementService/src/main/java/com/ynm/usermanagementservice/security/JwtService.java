package com.ynm.usermanagementservice.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Set;

@Service
public class JwtService {
    private final SecretKey key;
    private final String issuer;
    private final long expirationSeconds;

    public JwtService(
            @Value("${security.jwt.secret}") String base64Secret,
            @Value("${security.jwt.issuer:libreScholarHub}") String issuer,
            @Value("${security.jwt.expiration-seconds:3600}") long expirationSeconds
    ) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret));
        this.issuer = issuer;
        this.expirationSeconds = expirationSeconds;
    }

    public String generateToken(String subject, Set<String> roles, Map<String, Object> extraClaims) {
        Instant now = Instant.now();
        var builder = Jwts.builder()
                .setSubject(subject)
                .setIssuer(issuer)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(expirationSeconds)))
                .claim("roles", roles)
                .addClaims(extraClaims)
                .signWith(key, SignatureAlgorithm.HS256);
        return builder.compact();
    }
}
