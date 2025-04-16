package ua.diploma.projectmanager.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;

import java.time.Duration;

@Repository
@Slf4j
public class RedisRepository {

    private final Jedis jedis;

    public RedisRepository() {
        this.jedis = new Jedis("localhost", 6379);
    }

    public void saveToken(String username, Duration TTL, String token) {
        jedis.setex(username, TTL.getSeconds(), token);
    }

    public void deleteToken(String username) {
        log.info("Deleting token for user: {}", username);

        if (jedis.exists(username)) {
            jedis.del(username);
            log.info("Token deleted for user: {}", username);
        } else {
            log.info("Token not found for user: {}", username);
        }
    }

    public boolean isTokenExists(String token) {
        return jedis.keys("*").stream()
                .anyMatch(key -> token.equals(jedis.get(key)));
    }
}
