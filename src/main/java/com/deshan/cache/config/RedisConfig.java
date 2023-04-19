package com.deshan.cache.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.deshan.cache.util.IAMAuthTokenRequest;
import com.deshan.cache.util.RedisIAMAuthCredentialsProvider;
import com.deshan.cache.util.RedisStaticCredentialsProvider;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.RedisCredentialsProvider;
import io.lettuce.core.protocol.ProtocolVersion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import java.time.Duration;


@Configuration
@EnableRedisRepositories
public class RedisConfig {

    @Value("${redis.user}")
    private String userId;

    @Value("${redis.replication.group}")
    private String replicationGroupId;

    @Value("${redis.replication.group.region}")
    private String region;


    @Value("${redis.host}")
    private String host;

    @Value("${redis.password}")
    private String password;

    @Value("${redis.port}")
    private int port;
    


    @Bean
    public LettuceConnectionFactory redisConnectionFactory(){

        LettuceClientConfiguration lettuceClientConfiguration = LettuceClientConfiguration.builder()
                .commandTimeout(Duration.ofSeconds(10))
                .shutdownTimeout(Duration.ofSeconds(10))
                .clientOptions(ClientOptions.builder()
                        .protocolVersion(ProtocolVersion.RESP2)
                        .build())
                .useSsl()
                .build();

        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        RedisIAMAuthCredentialsProvider iamAuthCredentialsProvider = (RedisIAMAuthCredentialsProvider) getCredentialsProvider();
        redisStandaloneConfiguration.setPassword(iamAuthCredentialsProvider.getIamAuthToken());
        redisStandaloneConfiguration.setHostName(host);
        redisStandaloneConfiguration.setPort(port);
        redisStandaloneConfiguration.setUsername(userId);

        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(redisStandaloneConfiguration ,lettuceClientConfiguration);

        connectionFactory.afterPropertiesSet();
        return connectionFactory;


    }

    private RedisCredentialsProvider getCredentialsProvider() {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(userId),
                "userId cannot be be null or emtpy");

        // Use a static username and password
        if (!Strings.isNullOrEmpty(password)) {
            return new RedisStaticCredentialsProvider(userId, password);
        }

        Preconditions.checkArgument(!Strings.isNullOrEmpty(replicationGroupId),
                "replicationGroupId cannot be be null or emtpy");

        // Create a default AWS Credentials provider.
        // This will look for AWS credentials defined in environment variables or system properties.
        AWSCredentialsProvider awsCredentialsProvider = new DefaultAWSCredentialsProviderChain();

        // Create an IAM Auth Token request. Once this request is signed it can be used as an
        // IAM Auth token for Elasticache Redis.
        IAMAuthTokenRequest iamAuthTokenRequest = new IAMAuthTokenRequest(userId, replicationGroupId, region);

        // Create a Redis credentials provider using IAM credentials.
        return new RedisIAMAuthCredentialsProvider(
                userId, iamAuthTokenRequest, awsCredentialsProvider);
    }

    /**
     * Bean for get redis template.
     *
     * @return a {@link RedisTemplate} object
     */
    @Bean
    public RedisTemplate<String, Object> template() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashValueSerializer((new GenericJackson2JsonRedisSerializer()));
        template.afterPropertiesSet();
        return template;
    }
}

