package edutrack.security.services;

import edutrack.security.entity.RefreshTokenEntity;
import edutrack.security.repository.RefreshTokenRepository;
import edutrack.user.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private AccountRepository accountRepository;

    @Value("${jwt.refreshExpirationMs}")
    private Long refreshTokenDurationMs;

    public Optional<RefreshTokenEntity> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshTokenEntity createRefreshToken(String userEmail) {
        RefreshTokenEntity refreshToken = new RefreshTokenEntity();

        refreshToken.setUserEmail(userEmail);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));

        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    public RefreshTokenEntity verifyExpiration(RefreshTokenEntity token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    @Transactional
    public void deleteByUserEmail(String userEmail) {
        refreshTokenRepository.deleteByUserEmail(userEmail);
    }
}
