package com.demo.throttle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Transaction;

import java.util.concurrent.TimeUnit;

@Component
public class RateLimitControl implements AutoCloseable {

    private static final String KEY_SEPARATOR = ":";
    private static final String KEY_PART_SEPARATOR = ".";
    private static final int DEFAULT_THROTTLE = 1000; // attempts per minute
    private static final Logger LOG = LoggerFactory.getLogger(RateLimitControl.class);


    @Value("${redis.maxTotalConn:50}")
    private int maxTotalConn;

    @Value("${redis.maxIdleConn:50}")
    private int maxIdleConn;

    @Value("${redis.minIdleConn:10}")
    private int minIdleConn;

    @Value("${redis.host:localhost}")
    private String redisHost;

    @Value("${redis.port:6379}")
    private int redisPort;


    private static JedisPool jedisPool = null;


    private void init() {

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(maxTotalConn);
        jedisPoolConfig.setMaxIdle(maxIdleConn);
        jedisPoolConfig.setMinIdle(minIdleConn);

        jedisPool = new JedisPool(jedisPoolConfig, redisHost, redisPort);
    }

    public static void main(String[] args) {
        System.out.println(System.currentTimeMillis());
        System.out.println(System.currentTimeMillis() / 60000);
        System.out.println(TimeUnit.SECONDS.toSeconds(10));
        System.out.println(Math.toIntExact(TimeUnit.SECONDS.toSeconds(10)));
    }

    public boolean isAllowed(String apiName, int timeWindow, TimeUnit timeWindowUnit, String... keyParts) {
        if (jedisPool == null)
            init();

        long timeWindowId = System.currentTimeMillis() / timeWindowUnit.toMillis(timeWindow);
        String key = timeWindowId + KEY_SEPARATOR + String.join(KEY_PART_SEPARATOR, keyParts);

        String currentRedisValue = null;
        try (Jedis jedis = jedisPool.getResource()) {

            currentRedisValue = jedis.get(key);
            if (currentRedisValue != null && getRateLimit(jedis, apiName) <= Long.parseLong(currentRedisValue)) {
                return false;
            }
            incrementAndExpire(jedis, key, Math.toIntExact(timeWindowUnit.toSeconds(timeWindow)));

        } catch (NumberFormatException e) {
            //currentRedisValue won't be null at that line
            LOG.error("Value '{}' retrieved from Redis is not parsable to 'long'", currentRedisValue);

        } catch (ArithmeticException e) {
            LOG.error("Time window value too high: {} value in {} time unit", timeWindow, timeWindowUnit);

        } catch (RuntimeException e) {
            LOG.error("Unable to perform Redis operation on key: {}", key, e);
        }

        return true;
    }


    @Override
    public void close() {

        if (jedisPool != null) {
            jedisPool.close();
            jedisPool.destroy();
        }
    }

    private int getRateLimit(Jedis jedis, String apiName) {
        String rateLimit = jedis.get(apiName);
        if (StringUtils.isEmpty(rateLimit)) {
            String defaultLimit = jedis.get("widget.default");
            if (StringUtils.isEmpty(defaultLimit)) {
                return Integer.parseInt(defaultLimit);
            } else {
                return DEFAULT_THROTTLE;
            }
        } else {
            return Integer.parseInt(rateLimit);
        }
    }

    private void incrementAndExpire(Jedis jedis, String key, int ttlInSeconds) {
        Transaction t = jedis.multi();
        t.incr(key);
        t.expire(key, ttlInSeconds);
        t.exec();
    }
}