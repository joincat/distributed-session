package com.wind.web.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.serializer.DefaultDeserializer;
import org.springframework.core.serializer.DefaultSerializer;
import org.springframework.core.serializer.support.DeserializingConverter;
import org.springframework.core.serializer.support.SerializingConverter;
import redis.clients.jedis.BinaryJedisCommands;

import redis.clients.util.Pool;

import java.io.Closeable;
import java.io.IOException;


public class RedisStorage<Resource extends BinaryJedisCommands> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisStorage.class);

    private Pool<Resource> pool;

    public RedisStorage(Pool<Resource> pool) {
        this.pool = pool;
    }

    public void set(String key, Object value) {
        Resource resource = pool.getResource();
        try {
            resource.set(key.getBytes(), serializeObject(value));
        } finally {
            close(resource);
        }
    }

    public Object get(String key) {
        Resource resource = pool.getResource();
        try {
            byte[] bytes = resource.get(key.getBytes());
            if (bytes == null || bytes.length == 0) {
                return null;
            }
            return deserializeObject(bytes);
        } finally {
            close(resource);
        }
    }

    public void put(String key, Object value, int seconds) {
        Resource resource = pool.getResource();
        try {
            resource.setex(key.getBytes(), seconds, serializeObject(value));
        } finally {
            close(resource);
        }
    }

    public void replace(String key, Object value, int seconds) {
        Resource resource = pool.getResource();
        try {
            resource.setex(key.getBytes(), seconds, serializeObject(value));
        } finally {
            close(resource);
        }
    }

    public Long del(String key) {
        Resource resource = pool.getResource();
        try {
            return resource.del(key.getBytes());
        } finally {
            close(resource);
        }
    }

    private void close(Resource resource) {
        if (resource != null) {
            try {
                ((Closeable) resource).close();
            } catch (IOException e) {
                LOGGER.error("redis client close error", e);
            }
        }
    }

    public Boolean exists(String key) {
        Resource resource = pool.getResource();
        try {
            return resource.exists(key.getBytes());
        } finally {
            close(resource);
        }
    }

    public Long setnx(String key, String value) {
        Resource resource = pool.getResource();
        try {
            return resource.setnx(key.getBytes(), serializeObject(value));
        } finally {
            close(resource);
        }
    }

    public Object deserializeObject(byte[] b) {
        try {
            DeserializingConverter dc = new DeserializingConverter(new DefaultDeserializer());
            return dc.convert(b);
        } catch (Exception e) {
            return null;
        }
    }

    public byte[] serializeObject(Object obj) {
        SerializingConverter sc = new SerializingConverter(new DefaultSerializer());
        return sc.convert(obj);
    }
}
