package com.example.ratelimittest.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import java.time.Duration;

public class RateLimitService {

  private final static Bucket bucket;

  static {
    // define the limit 1 time per 10 minute
    Bandwidth limit = Bandwidth.simple(1, Duration.ofSeconds(10));
    // construct the bucket
    bucket = Bucket.builder().addLimit(limit).build();
  }
  public void test() {

    boolean b = bucket.tryConsume(1);

    if (b) {
      System.out.println("#######");
      System.out.println("success!!!");
      System.out.println("#######");
    }
    else {
      System.out.println("#######");
      System.out.println("failed!");
      System.out.println("#######");
    }
  }

}
