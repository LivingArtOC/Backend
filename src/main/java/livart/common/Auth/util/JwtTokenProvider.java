package livart.common.Auth.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import livart.common.dto.enums.Provider;
import livart.common.dto.enums.Role;
import livart.common.exception.CustomException;
import livart.common.exception.ErrorCode;
import livart.common.Auth.JwtLoginProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {

    private final Key key;
    private final JwtLoginProperties jwtProperties;

    public JwtTokenProvider(JwtLoginProperties jwtProperties) {
        this.jwtProperties = jwtProperties;

        // secretKey 값이 null이거나 빈 문자열인 경우 예외 처리
        String secretKey = jwtProperties.getSecretKey();
        if (secretKey == null || secretKey.isEmpty()) {
            throw new CustomException(ErrorCode.NULL_INPUT_JWT_KEY);
        }

        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generate(String subject, Role role, Provider provider, Date expiredAt) {
        return Jwts.builder()
                .setSubject(subject)
                .claim("role", role.name())
                .claim("provider", provider.name())
                .setIssuedAt(new Date())
                .setExpiration(expiredAt)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String extractSubject(String accessToken) {
        Claims claims = parseClaims(accessToken);
        return claims.getSubject();
    }

    public Role extractRole(String token) {
        String role = parseClaims(token).get("role", String.class);
        return Role.valueOf(role);
    }

    public Provider extractProvider(String token) {
        String provider = parseClaims(token).get("provider", String.class);
        return Provider.valueOf(provider);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.TOKEN_EXPIRED);
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException e) {
            throw new CustomException(ErrorCode.TOKEN_INVALID);
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
    public Long getAccessExpiration() {
        return jwtProperties.getAccessExpiration();
    }

    public Long getRefreshExpiration() {
        return jwtProperties.getRefreshExpiration();
    }
}

