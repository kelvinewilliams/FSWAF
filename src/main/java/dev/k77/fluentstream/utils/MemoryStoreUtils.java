package dev.k77.fluentstream.utils;
import java.io.IOException;
import java.net.InetSocketAddress;

import dev.k77.fluentstream.constants.MemoryStoreConstants;
import net.spy.memcached.MemcachedClient;

public class MemoryStoreUtils {
    private MemcachedClient client;
    public MemoryStoreUtils() throws IOException {
        client = new MemcachedClient(
                    new InetSocketAddress(MemoryStoreConstants.CONFIG_ENDPOINT,
                        MemoryStoreConstants.CLUSTER_PORT));

    }

    public void set(String key, Object object, int lifetime) {
        client.set(key, lifetime, object);

    }

    public Object get(String key) {
        return client.get(key);
    }
}

