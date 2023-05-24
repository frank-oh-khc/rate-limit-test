package com.example.ratelimittest.service;

import com.example.ratelimittest.config.RedisConfig;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.BucketProxy;
import io.github.bucket4j.distributed.proxy.RemoteBucketBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.util.StopWatch;

class RateLimitServiceTest {

    @Test
    void localTest() throws Exception {
        RateLimitService rateLimitService = new RateLimitService();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        for (int i = 0; i < 30; i++) {

            rateLimitService.test();
            stopWatch.stop();
            System.out.println(stopWatch.getTotalTimeMillis());
            stopWatch.start();
            Thread.sleep(1000);
        }
    }

    @Test
    void redisTest() throws Exception {

        RedisConfig redisConfig = new RedisConfig();
        RemoteBucketBuilder remoteBucketBuilder = redisConfig.getRemoteBucketBuilder();
        BucketConfiguration bucketConfiguration = RedisConfig.getBucketConfiguration();

        BucketProxy build = remoteBucketBuilder.build(999, bucketConfiguration);

        for (int i = 0; i < 15; i++) {

            boolean b = build.tryConsume(1);

            if (b) {
                System.out.println(true);
            } else {
                System.out.println(false);
            }
        }
    }

}
