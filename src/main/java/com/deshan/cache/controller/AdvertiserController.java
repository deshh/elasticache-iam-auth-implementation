package com.deshan.cache.controller;

import com.deshan.cache.model.AdvertiserConfig;
import com.deshan.cache.repository.AdvertiserConfigRedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/advertiser")
public class AdvertiserController {

    @Autowired
    private AdvertiserConfigRedisRepository advertiserConfigRedisRepository;

    @PostMapping
    public AdvertiserConfig save(@RequestBody AdvertiserConfig advertiserConfig) {
        return advertiserConfigRedisRepository.save(advertiserConfig);
    }

    @GetMapping
    public List<AdvertiserConfig> getConfigs() {
        return advertiserConfigRedisRepository.findAll();
    }

    @GetMapping("/{id}")
    public AdvertiserConfig findConfigItemById(@PathVariable String id) {
        return advertiserConfigRedisRepository.findItemById(id);
    }


    @DeleteMapping("/{id}")
    public String deleteConfigById(@PathVariable String id)   {
        return advertiserConfigRedisRepository.deleteConfig(id);
    }

}