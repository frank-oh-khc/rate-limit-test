package com.example.ratelimittest.service;

import static org.junit.jupiter.api.Assertions.*;

import com.example.ratelimittest.config.RedisConfig;
import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.Test;
import org.springframework.util.StopWatch;

class RateLimitServiceTest {

  @Test
  void test() throws Exception {
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
    redisConfig.redisRateLimitConfig();

    Bucket bucket = RedisConfig.getBucket();
    System.out.println(bucket);
    bucket.tryConsume(1);
  }

}
