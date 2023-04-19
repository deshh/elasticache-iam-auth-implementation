package com.deshan.cache.repository;

import com.deshan.cache.model.AdvertiserConfig;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Log4j2
public class AdvertiserConfigRedisRepository {

    private final RedisTemplate<String, Object> template;
    private final HashOperations<String, String, AdvertiserConfig> hashOperations;

    @Autowired
    public AdvertiserConfigRedisRepository(final RedisTemplate<String, Object> template) {
        this.template = template;
        this.hashOperations = template.opsForHash();
    }

    /**
     * Store temp merchant configurations
     *
     * @param AdvertiserConfig object
     */
    public void create(final AdvertiserConfig AdvertiserConfig) {
        final String key = getKey(AdvertiserConfig.getAdvertiserId());
        template.opsForHash().put(key, AdvertiserConfig.getAdvertiserId(), AdvertiserConfig);
        template.expire(key, 60, TimeUnit.MINUTES);
        log.info(
                String.format("Advertiser Config with ID %s created", AdvertiserConfig.getAdvertiserId()));
    }

    /**
     * Get merchant configurations by merchant id
     *
     * @param merchantId
     * @return {@link AdvertiserConfig} object
     */
    public final AdvertiserConfig get(final String merchantId) {
        return hashOperations.get(getKey(merchantId), merchantId);
    }

    /**
     * Create the hash key
     *
     * @param merchantId
     * @return hash key
     */
    private String getKey(final String merchantId) {
        return String.format("Advertiser_config_%s", merchantId);
    }


    public static final String HASH_KEY_NAME = "CONFIGURATION";
    @Autowired
    private RedisTemplate redisTemplate;


    public AdvertiserConfig save(AdvertiserConfig advertiserConfig){
        redisTemplate.opsForHash().put(HASH_KEY_NAME, advertiserConfig.getAdvertiserId(), advertiserConfig);
        return advertiserConfig;
    }

    public List<AdvertiserConfig> findAll(){
        return redisTemplate.opsForHash().values(HASH_KEY_NAME);
    }

    public AdvertiserConfig findItemById(String id){
        return (AdvertiserConfig) redisTemplate.opsForHash().get(HASH_KEY_NAME, id);
    }


    public String deleteConfig(String id){
        redisTemplate.opsForHash().delete(HASH_KEY_NAME, id);
        return "Config deleted successfully !!";
    }

}
