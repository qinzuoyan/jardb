package dsn.apps;

import org.apache.thrift.TException;

import java.util.Properties;

public class cluster {
    public static final String PEGASUS_META_SERVERS_KEY = "meta_servers";
    public static final String PEGASUS_RETRY_WHEN_META_LOSS_KEY = "retry_when_meta_loss";
    public static final String PEGASUS_RETRY_WHEN_META_LOSS_DEF = "5";

    private cache.cluster_handler cluster_;

    public cluster(Properties config) throws IllegalArgumentException {
        int retry_when_meta_loss = Integer.valueOf(config.getProperty(
                PEGASUS_RETRY_WHEN_META_LOSS_KEY, PEGASUS_RETRY_WHEN_META_LOSS_DEF));
        cluster_ = new cache.cluster_handler(retry_when_meta_loss);
        String meta_list = config.getProperty(PEGASUS_META_SERVERS_KEY);
        if (meta_list == null) {
            throw new IllegalArgumentException("no property set: " + PEGASUS_META_SERVERS_KEY);
        }
        meta_list = meta_list.trim();
        if (meta_list.isEmpty()) {
            throw new IllegalArgumentException("invalid property: " + PEGASUS_META_SERVERS_KEY);
        }
        String[] meta_address = meta_list.split(",");
        for (String addr : meta_address) {
            String[] pair = addr.trim().split(":");
            cluster_.add_meta(pair[0].trim(), Integer.valueOf(pair[1].trim()));
        }
    }

    public table openTable(String name) throws ReplicationException, TException {
        return new table(cluster_, name);
    }

    public table openTable(String name, cache.key_hash hash_function) throws ReplicationException, TException {
        return new table(cluster_, name, hash_function);
    }
}

