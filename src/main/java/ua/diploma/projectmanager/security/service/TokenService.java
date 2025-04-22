package ua.diploma.projectmanager.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import ua.diploma.projectmanager.model.User;
import ua.diploma.projectmanager.redis.RedisRepository;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtEncoder jwtEncoder;
    private final RedisRepository redisRepository;
    private final Duration TTL = Duration.ofMinutes(1);

    public String generateToken(User user) {
        var now = Instant.now();
        var claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(TTL))
                .subject(user.getUsername())
                .build();

        String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        redisRepository.saveToken(user.getUsername(), token);
        return token;
    }

    public boolean isTokenBlacklisted(String token) {
        return !redisRepository.isTokenExists(token);
    }

    public void revokeToken(String username) {
        redisRepository.deleteToken(username);
    }
}
