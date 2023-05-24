package com.example.ratelimittest.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisURI;
import io.lettuce.core.cluster.RedisClusterClient;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.Getter;

public class RedisConfig {

  private static final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
  @Getter
  private static Bucket bucket;

  public void redisRateLimitConfig() {
    RedisClusterClient redisClusterClient = redisClusterClient();
    LettuceBasedProxyManager proxyManager = LettuceBasedProxyManager.builderFor(redisClusterClient)
        .withExpirationStrategy(ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(
            Duration.ofSeconds(10)))
        .build();

    BucketConfiguration configuration = BucketConfiguration.builder()
        .addLimit(Bandwidth.simple(1_000, Duration.ofMinutes(1)))
        .build();
    bucket = proxyManager.builder().build("cheering", configuration);
  }

  private List<String> nodes = Arrays.asList("127.0.0.1:7001", "127.0.0.1:7002", "127.0.0.1:7003",
      "127.0.0.1:7004", "127.0.0.1:7005", "127.0.0.1:7006"); // replace with your nodes

  public RedisClusterClient redisClusterClient() {
    List<RedisURI> redisURIs = getRedisURIs();
    return RedisClusterClient.create(redisURIs);
  }

  private List<RedisURI> getRedisURIs() {
    return nodes.stream()
        .map(node -> node.split(":"))
        .map(this::toRedisURI)
        .collect(Collectors.toList());
  }

  private RedisURI toRedisURI(String[] node) {
    String host = node[0];
    int port = Integer.parseInt(node[1]);
    return RedisURI.builder()
        .withHost(host)
        .withPort(port)
        .withTimeout(Duration.ofSeconds(60)) // adjust as needed
        .build();
  }

}
