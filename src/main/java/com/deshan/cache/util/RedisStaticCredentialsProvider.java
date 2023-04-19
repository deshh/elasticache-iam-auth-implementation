package com.deshan.cache.util;

import io.lettuce.core.RedisCredentials;
import io.lettuce.core.RedisCredentialsProvider;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import reactor.core.publisher.Mono;

/**
 * Implement RedisCredentialsProvider for password-based authentication.
 * @see: https://lettuce.io/core/release/reference/index.html#redisuri.authentication
 */
public class RedisStaticCredentialsProvider implements RedisCredentialsProvider {

    private final String userName;
    private final String password;

    public RedisStaticCredentialsProvider(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    @Override
    public Mono<RedisCredentials> resolveCredentials() {
        // Provide Redis credentials
        return Mono.just(RedisCredentials.just(userName, password));
    }
}
