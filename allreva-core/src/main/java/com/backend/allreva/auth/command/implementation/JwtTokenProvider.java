package com.backend.allreva.auth.command.implementation;

import com.backend.allreva.auth.domain.JwtErrorCode;
import com.backend.allreva.common.exception.CustomException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final int accessTime;
    private final int refreshTime;

    public JwtTokenProvider(
            @Value("${jwt.secret-key}") final String secretKey,
            @Value("${jwt.access.expiration}") final int accessTime,
            @Value("${jwt.refresh.expiration}") final int refreshTime) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
        this.accessTime = accessTime;
        this.refreshTime = refreshTime;
    }

    public Long extractMemberId(final String token) {
        return Long.valueOf(Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject());
    }

    public void validateToken(final String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parse(token);
        } catch (SignatureException e) {
            log.warn("Invalid JWT token signature: {}", e.getMessage());
            throw new CustomException(JwtErrorCode.TOKEN_INVALID);
        } catch (MalformedJwtException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            throw new CustomException(JwtErrorCode.TOKEN_INVALID);
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT token: {}", e.getMessage());
            throw new CustomException(JwtErrorCode.TOKEN_INVALID);
        } catch (UnsupportedJwtException e) {
            log.warn("JWT token is unsupported: {}", e.getMessage());
            throw new CustomException(JwtErrorCode.TOKEN_INVALID);
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is empty: {}", e.getMessage());
            throw new CustomException(JwtErrorCode.TOKEN_INVALID);
        }
    }

    public String generateAccessToken(final String subject) {
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + accessTime);

        return Jwts.builder()
                .subject(subject)
                .issuedAt(currentDate)
                .expiration(expireDate)
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken(final String subject) {
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + refreshTime);

        return Jwts.builder()
                .subject(subject)
                .issuedAt(currentDate)
                .expiration(expireDate)
                .signWith(secretKey)
                .compact();
    }
}
