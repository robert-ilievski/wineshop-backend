package com.systems.integrated.wineshopbackend.service.impl;

import com.systems.integrated.wineshopbackend.models.users.AuthToken;
import com.systems.integrated.wineshopbackend.models.users.User;
import com.systems.integrated.wineshopbackend.repository.AuthTokenJPARepository;
import com.systems.integrated.wineshopbackend.repository.UserJPARepository;
import com.systems.integrated.wineshopbackend.service.intef.AuthTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthTokenServiceImpl implements AuthTokenService {

    private final AuthTokenJPARepository authTokenJPARepository;
    private final UserJPARepository userJPARepository;

    @Value("${wineShop.authToken.dateExpires}")
    private Integer seconds;

    @Override
    public AuthToken createAuthToken(Long userId, String type) {
        AuthToken authToken = findByUserId(userId);

        String token = UUID.randomUUID().toString();
        LocalDateTime localDateTime = LocalDateTime.now();

        if (Objects.nonNull(authToken)) {
            authToken = updateAuthToken(authToken, token, localDateTime);
            return authToken;
        }

        User user = userJPARepository.getById(userId);

        AuthToken newAuthToken = AuthToken.builder()
                .token(token)
                .dateCreated(localDateTime)
                .dateExpires(localDateTime.plusSeconds(300))
                .user(user)
                .type(type)
                .build();

        authTokenJPARepository.save(newAuthToken);
        return newAuthToken;
    }

    @Override
    public AuthToken updateAuthToken(AuthToken authToken) {
        String token = UUID.randomUUID().toString();
        LocalDateTime dateTimeNow = LocalDateTime.now();

        authToken.setToken(token);
        authToken.setDateCreated(dateTimeNow);
        authToken.setDateExpires(dateTimeNow.plusSeconds(seconds));

        authTokenJPARepository.save(authToken);

        return authToken;
    }

    @Override
    public boolean validateToken(String token) {
        AuthToken authToken = authTokenJPARepository.findAuthTokenByToken(token).orElse(null);
        return isTokenFound(authToken) && !isTokenExpired(authToken);
    }

    @Override
    public AuthToken findByUserId(Long userId) {
        User user = userJPARepository.getById(userId);
        AuthToken authToken = authTokenJPARepository.findAuthTokenByUser(user).orElse(null);
        return authToken;
    }

    @Override
    public AuthToken findByToken(String token) {
        AuthToken authToken = authTokenJPARepository.findAuthTokenByToken(token).orElse(null);
        return authToken;
    }

    private AuthToken updateAuthToken(AuthToken authToken, String token, LocalDateTime localDateTime) {
        authToken.setToken(token);
        authToken.setDateCreated(localDateTime);
        authToken.setDateExpires(localDateTime.plusSeconds(300));

        authTokenJPARepository.save(authToken);
        return authToken;
    }

    private boolean isTokenFound(AuthToken authToken) {
        return Objects.nonNull(authToken);
    }

    private boolean isTokenExpired(AuthToken authToken) {
        final LocalDateTime localDateTime = LocalDateTime.now();
        return localDateTime.isAfter(authToken.getDateExpires());
    }
}
