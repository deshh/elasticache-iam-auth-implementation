package com.deshan.cache.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "Advertiser_config%s")
public class AdvertiserConfig implements Serializable {
    @Id
    private String advertiserId;
    private String advertiserName;
    private String landingPageUrl;
}