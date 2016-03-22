package com.xiaomi.infra.pegasus.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.thrift.TException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dsn.apps.*;

public class Cluster {
    private static final Logger LOGGER = LoggerFactory.getLogger(Cluster.class);

    private static final String PEGASUS_PROPERTIES_FILE_KEY = "pegasus.properties.resourcefile";
    private static final String PEGASUS_PROPERTIES_FILE_DEFAULT = "pegasus.properties";

    private static final String PEGASUS_META_SERVERS_KEY = "meta_servers";

    static final int retry_when_meta_loss = 5;

    private cache.cluster_handler cluster_;

    public Cluster() throws IOException, IllegalArgumentException {
        cluster_ = new cache.cluster_handler(retry_when_meta_loss);
        Properties config = loadProperties();
        String meta_list = config.getProperty(PEGASUS_META_SERVERS_KEY);
        if (meta_list == null) {
            throw new IllegalArgumentException("no property " + PEGASUS_META_SERVERS_KEY);
        }
        meta_list = meta_list.trim();
        if (meta_list.isEmpty()) {
            throw new IllegalArgumentException("invalid property " + PEGASUS_META_SERVERS_KEY);
        }
        String[] meta_address = meta_list.split(",");
        for (String addr : meta_address) {
            String[] pair = addr.trim().split(":");
            cluster_.add_meta(pair[0].trim(), Integer.valueOf(pair[1].trim()));
        }
    }

    public Table openTable(String name) throws ReplicationException, TException {
        return new Table(cluster_, name);
    }

    public Table openTable(String name, cache.key_hash hash_function) throws ReplicationException, TException {
        return new Table(cluster_, name, hash_function);
    }

    private static Properties loadProperties() throws IOException {
        String pegasusFile = System.getProperty(PEGASUS_PROPERTIES_FILE_KEY, PEGASUS_PROPERTIES_FILE_DEFAULT);
        if (pegasusFile.isEmpty()) {
            pegasusFile = PEGASUS_PROPERTIES_FILE_DEFAULT;
        }
        String pathInResource = pegasusFile.startsWith("/") ? pegasusFile : "/" + pegasusFile;
        InputStream stream = Cluster.class.getResourceAsStream(pathInResource);
        Properties properties = new Properties();
        properties.load(stream);
        return properties;
    }
}

