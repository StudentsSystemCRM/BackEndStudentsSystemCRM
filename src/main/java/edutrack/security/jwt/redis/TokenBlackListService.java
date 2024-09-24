package edutrack.security.jwt.redis;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class TokenBlackListService {
    RedisTemplate<String, String> redisTemplate;

    public void addToBlackList(String token, long expirationTimeMillis) {
        long ttl = expirationTimeMillis - System.currentTimeMillis();
        redisTemplate.opsForValue().set(token, "blacklisted", ttl, TimeUnit.MILLISECONDS); // save token to Redis with ttl
    }

    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(token)); // check token in Redis
    }
}
