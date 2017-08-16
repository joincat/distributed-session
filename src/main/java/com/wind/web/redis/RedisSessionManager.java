package com.wind.web.redis;

import org.springframework.beans.factory.InitializingBean;
import redis.clients.jedis.*;
import redis.clients.util.Pool;
import redis.clients.util.Sharded;

import java.util.ArrayList;
import java.util.List;


public class RedisSessionManager implements InitializingBean {

    private static final Object REDIS_STORAGE_LOCK = new Object();

    private int readTimeout = 3000;//seconds
    private int connectionTimeout = 3000; //seconds
    private String redisServer; //redis服务地址、端口、用户名、密码，分片配置以逗号分隔，如localhost#6379#foobared,localhost#6379#foobared
    private int maxTotal = 8;
    private int maxIdle = 8;
    private long maxWaitMillis = -1;

    private List<JedisShardInfo> shards = new ArrayList<>();
    private JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
    private Pool<? extends BinaryJedisCommands> jedisPool;
    private RedisStorage redisStorage;

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public void setRedisServer(String redisServer) {
        for (String redisServerInfo : redisServer.split(",")) {
            JedisShardInfo jedisShardInfo = buildJedisShardInfo(redisServerInfo);
            shards.add(jedisShardInfo);
        }
    }

    private JedisShardInfo buildJedisShardInfo(String redisServerInfo) {
        String[] redisServerProperties = redisServerInfo.split("#");
        JedisShardInfo jedisShardInfo = new JedisShardInfo(redisServerProperties[0], Integer.valueOf(redisServerProperties[1]), connectionTimeout, readTimeout, Sharded.DEFAULT_WEIGHT);
        if (redisServerProperties.length == 3) {
            jedisShardInfo.setPassword(redisServerProperties[2]);
        }
        return jedisShardInfo;
    }

    public void setMaxTotal(int maxTotal) {
        this.jedisPoolConfig.setMaxTotal(maxTotal);
    }

    public void setMaxIdle(int maxIdle) {
        this.jedisPoolConfig.setMaxIdle(maxIdle);
    }

    public void setMaxWaitMillis(long maxWaitMillis) {
        this.jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (shards.size() == 1) {
            jedisPool = new JedisPool(jedisPoolConfig, shards.get(0).getHost(), shards.get(0).getPort(), connectionTimeout, shards.get(0).getPassword());
        } else {
            jedisPool = new ShardedJedisPool(jedisPoolConfig, shards);
        }
    }

    public RedisStorage getRedisStorage() {
        if (redisStorage == null) {
            synchronized (REDIS_STORAGE_LOCK) {
                if (redisStorage == null) {
                    redisStorage = new RedisStorage<>(jedisPool);
                }
            }
        }
        return redisStorage;
    }
}
