package com.deshan.cache.util;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.google.common.base.Suppliers;
import io.lettuce.core.RedisCredentials;
import io.lettuce.core.RedisCredentialsProvider;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Implement RedisCredentialsProvider for IAM Authentication
 * @see: https://lettuce.io/core/release/reference/index.html#redisuri.authentication
 */
@Log4j2
public class RedisIAMAuthCredentialsProvider implements RedisCredentialsProvider {

    private final String userName;
    private final IAMAuthTokenRequest iamAuthTokenRequest;

    private final AWSCredentialsProvider awsCredentialsProvider;
    private final Supplier<String> iamAuthTokenProvider;
    private static final long TOKEN_CACHE_SECONDS = 600;

    public RedisIAMAuthCredentialsProvider(String userName, IAMAuthTokenRequest iamAuthTokenRequest,
                                           AWSCredentialsProvider awsCredentialsProvider) {
        this.userName = userName;
        this.iamAuthTokenRequest = iamAuthTokenRequest;
        this.awsCredentialsProvider = awsCredentialsProvider;

        // Cache and reuse the IAM Auth token for token duration.
        iamAuthTokenProvider = Suppliers.memoizeWithExpiration(this::getIamAuthToken,
            TOKEN_CACHE_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * Returns a Redis user and IAM Auth token.
     * Implement the new credentials provider interface for Lettuce.
     */
    @Override
    public Mono<RedisCredentials> resolveCredentials() {
        // Provide Redis credentials
        RedisCredentials redisCredentials = RedisCredentials.just(userName, iamAuthTokenProvider.get());
        // Log the credentials, so we can observe how new tokens are generated on the fly.
        log.info("Using credentials: %s, %s%n", redisCredentials.getUsername(),
            String.valueOf((redisCredentials.getPassword())));
        return Mono.just(redisCredentials);
    }

    /**
     * Generate and returns a new IAM Auth token.
     * The token is signed in using the provided AWS credentials.
     */
    public String getIamAuthToken() {
        try {
            log.info("AWS IAM Token: {}", iamAuthTokenRequest.toSignedRequestUri(awsCredentialsProvider.getCredentials()));
            return iamAuthTokenRequest.toSignedRequestUri(awsCredentialsProvider.getCredentials());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Error when creating IAM Auth token", e);
        }
    }
}
