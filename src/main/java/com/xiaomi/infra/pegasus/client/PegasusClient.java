package com.xiaomi.infra.pegasus.client;

import dsn.apps.*;
import dsn.utils.tools;
import org.apache.commons.lang3.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.nio.ByteBuffer;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author qinzuoyan
 */
public class PegasusClient implements PegasusClientInterface {
    private static final Log LOG = LogFactory.getLog(PegasusClient.class);

    private final Properties config;
    private final dsn.apps.cluster cluster;
    private final ConcurrentHashMap<String, table> tableMap;
    private final Object tableMapLock;

    // pegasus client configuration keys
    public static final String[] PEGASUS_CLIENT_CONFIG_KEYS = new String[] {
            dsn.apps.cluster.PEGASUS_META_SERVERS_KEY,
            dsn.apps.cluster.PEGASUS_RETRY_WHEN_META_LOSS_KEY
    };

    // configPath could be:
    // - zk path: zk://host,host,host:port/path
    // - local file path: file///var/config
    // - resource path: resource:///var/config
    public PegasusClient(String configPath) throws PException {
        this(PConfigUtil.loadConfiguration(configPath));
    }

    public PegasusClient(Properties config) throws PException {
        this.config = config;
        this.cluster = new cluster(this.config);
        this.tableMap = new ConcurrentHashMap<String, table>();
        this.tableMapLock = new Object();
        LOG.info(getConfigurationString());
    }

    class pegasus_hasher implements cache.key_hash {
        @Override
        public long hash(byte[] key) {
            Validate.isTrue(key != null);
            ByteBuffer buf = ByteBuffer.wrap(key);
            int hashKeyLen = buf.getInt();
            Validate.isTrue(hashKeyLen > 0 && (4 + hashKeyLen <= key.length));
            return tools.dsn_crc64(key, 4, hashKeyLen);
        }
    }

    public byte[] generateKey(byte[] hashKey, byte[] sortKey) {
        int hashKeyLen = hashKey.length;
        int sortKeyLen = (sortKey == null ? 0 : sortKey.length);
        ByteBuffer buf = ByteBuffer.allocate(4 + hashKeyLen + sortKeyLen);
        buf.putInt(hashKeyLen);
        buf.put(hashKey);
        if (sortKeyLen > 0) {
            buf.put(sortKey);
        }
        return buf.array();
    }

    public table getTable(String tableName) throws PException {
        table table = tableMap.get(tableName);
        if (table == null) {
            synchronized (tableMapLock) {
                table = tableMap.get(tableName);
                if (table == null) {
                    try {
                        table = cluster.openTable(tableName, new pegasus_hasher());
                    } catch (Throwable e) {
                        throw new PException(e);
                    }
                    tableMap.put(tableName, table);
                }
            }
        }
        return table;
    }

    @Override
    public Properties getConfiguration() {
        return config;
    }

    @Override
    public byte[] get(String tableName, byte[] hashKey, byte[] sortKey) throws PException {
        if (hashKey == null || hashKey.length == 0) {
            throw new PException("Invalid parameter: hashKey should not be null or empty");
        }
        table table = getTable(tableName);
        dsn.base.blob request = new dsn.base.blob(generateKey(hashKey, sortKey));
        read_response response;
        try {
            response = table.get(request);
        } catch (Throwable e) {
            throw new PException(e);
        }
        if (response.error == 0) {
            Validate.isTrue(response.value != null);
            return response.value.data;
        }
        else if (response.error == 1) {
            return null;
        }
        else {
            throw new PException("Rocksdb error: " + response.error);
        }
    }

    @Override
    public void set(String tableName, byte[] hashKey, byte[] sortKey, byte[] value) throws PException {
        if (hashKey == null || hashKey.length == 0) {
            throw new PException("Invalid parameter: hashKey should not be null or empty");
        }
        if (value == null) {
            throw new PException("Invalid parameter: value should not be null");
        }
        table table = getTable(tableName);
        dsn.base.blob k = new dsn.base.blob(generateKey(hashKey, sortKey));
        dsn.base.blob v = new dsn.base.blob(value);
        update_request request = new update_request(k, v);
        int response;
        try {
            response = table.put(request);
        } catch (Throwable e) {
            throw new PException(e);
        }
        if (response != 0) {
            throw new PException("Rocksdb error: " + response);
        }
    }

    @Override
    public void del(String tableName, byte[] hashKey, byte[] sortKey) throws PException {
        if (hashKey == null || hashKey.length == 0) {
            throw new PException("Invalid parameter: hashKey should not be null or empty");
        }
        table table = getTable(tableName);
        dsn.base.blob request = new dsn.base.blob(generateKey(hashKey, sortKey));
        int response;
        try {
            response = table.remove(request);
        } catch (Throwable e) {
            throw new PException(e);
        }
        if (response != 0) {
            throw new PException("Rocksdb error: " + response);
        }
    }

    public String getConfigurationString() {
        String configString = "PegasusClient Configuration:\n";
        if (this.config == null) {
            return configString;
        }
        for (int i = 0; i < PEGASUS_CLIENT_CONFIG_KEYS.length; ++i) {
            configString += (PEGASUS_CLIENT_CONFIG_KEYS[i] + "="
                    + this.config.getProperty(PEGASUS_CLIENT_CONFIG_KEYS[i], "") + "\n");
        }
        return configString;
    }
}
