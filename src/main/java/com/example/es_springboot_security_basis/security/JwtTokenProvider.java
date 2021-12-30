package com.example.es_springboot_security_basis.security;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Component
public class JwtTokenProvider {

    private final UserDetailsService userDetailsService;

    // Выносятся в Spring Config
    @Value("${jwt.header}")
    private String authorizationHeader;
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.expiration}")
    private long validityMilliseconds;

    public JwtTokenProvider(@Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    // Для безопасности
    @PostConstruct
    protected void init() {
        secretKey = Base64
                .getEncoder()
                .encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
    }


    // Метод для создания токена
    public String createToken(String username, String role) {

        // Claims - претензии, требования
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("role", role);

        //Дата создания + 100500 * 1000 (сек.)
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityMilliseconds * 1000);

        // Получение токена
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)                               // когда был создан токен
                .setExpiration(validity)                        // срок действия
                .signWith(SignatureAlgorithm.HS256, secretKey)  // алгоритм шифрования, секретный ключ
                .compact();
    }

    // Метод валидации токена - проверка на корректность
    public boolean validateToken(String token) throws JwtAuthenticationException {
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);

            return !claimsJws
                    .getBody()
                    .getExpiration()
                    .before(new Date()); // true (валиден), если дата окончания действия токена после чем дата сейчас

        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtAuthenticationException("Jwt token is expired or invalid", HttpStatus.UNAUTHORIZED);
        }
    }

    // Метод получения username из токена
    public String getUsername(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }


    // Метод получения аутентификации из токена необходимо получить UserDetails
    public Authentication getAuthentication(String token) {
        UserDetails userDetails =
                this
                        .userDetailsService
                        .loadUserByUsername(getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // Метод нужен будет для контроллера - получение токена из запроса
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader(authorizationHeader);
    }

}
