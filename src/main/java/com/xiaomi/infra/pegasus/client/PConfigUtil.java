package com.xiaomi.infra.pegasus.client;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.BytesPushThroughSerializer;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * @author qinzuoyan
 */
public class PConfigUtil {
    private static final Log LOG = LogFactory.getLog(PConfigUtil.class);

    public static final String ZK_PREFIX = "zk://";
    public static final String LOCAL_FILE_PREFIX = "file://";
    public static final String RESOURCE_PREFIX = "resource://";
    public static final String SLASH = "/";
    public static final String ZK_SERVER_SEPERATOR = ",";
    public static final int ZK_SESSION_TIMEOUT = 30000;
    public static final int ZK_CONNECTION_TIMEOUT = 30000;

    public static final String PEGASUS_BUSINESS_ROOT_NODE = "/databases/pegasus";

    public static boolean isZkPath(String path) {
        return path.startsWith(ZK_PREFIX);
    }

    public static boolean isLocalFile(String path) {
        return path.startsWith(LOCAL_FILE_PREFIX);
    }

    public static boolean isResource(String path) {
        return path.startsWith(RESOURCE_PREFIX);
    }

    // return as "zk://{zkServers}/databases/pegasus/{businessName}"
    public static String getBusinessConfigZkUri(String zkServers, String businessName) throws PException {
        return ZK_PREFIX + getZkHostAndPortString(zkServers) + getBusinessConfigZkPath(businessName);
    }

    // load client configuration from configPath, which could be local file path or zk path or resource path.
    public static Properties loadConfiguration(String configPath) throws PException {
        try {
            Properties config = new Properties();
            if (PConfigUtil.isZkPath(configPath)) {
                config.load(new ByteArrayInputStream(PConfigUtil.loadConfigFromZK(configPath)));
            } else if (PConfigUtil.isLocalFile(configPath)) {
                config.load(new BufferedInputStream(new FileInputStream(
                        configPath.substring(PConfigUtil.LOCAL_FILE_PREFIX.length()))));
            } else if (PConfigUtil.isResource(configPath)) {
                config.load(PegasusClient.class.getResourceAsStream(
                        configPath.substring(PConfigUtil.RESOURCE_PREFIX.length())));
            } else {
                throw new PException("configPath format error, " +
                        "should be local file format as 'file:///var/config', " +
                        "or zk path format as 'zk://host:port/path', " +
                        "or resource format as 'resource:///var/config', " +
                        "but actual configPath is " + configPath);
            }
            return config;
        } catch (Throwable e) {
            if (e instanceof PException) {
                throw (PException)e;
            } else {
                throw new PException(e);
            }
        }
    }

    public static byte[] loadConfigFromZK(String zkUri) throws PException {
        Pair<String, String> zkServerAndPath = getZkServerAndPath(zkUri);
        String server = zkServerAndPath.getKey();
        String path = zkServerAndPath.getValue();
        LOG.info("Pegasus load client information from zkServer=" + server + ", zkPath=" + path);
        ZkClient client = new ZkClient(server, ZK_SESSION_TIMEOUT, ZK_CONNECTION_TIMEOUT,
                new BytesPushThroughSerializer());
        return client.readData(path);
    }

    protected static String getBusinessConfigZkPath(String businessName) {
        return PEGASUS_BUSINESS_ROOT_NODE + SLASH + businessName;
    }

    // business tend to use zkServers formatted as: host_1:port,host_2:port...
    // the method will convert the format : 'host_1:port,host_2:port...' to 'host_1,host_2,...:port'
    // if zkServers has been formatted as : 'host_1,host_2...:port', return directly
    protected static String getZkHostAndPortString(String zkServers) throws PException {
        String[] servers = zkServers.split(ZK_SERVER_SEPERATOR);
        String hostString = null;
        String portString = null;
        for (int i = 0; i < servers.length; ++i) {
            String[] hostAndPort = servers[i].trim().split(":");
            if (hostAndPort.length > 2) {
                throw new PException("wrong format zk server format, zkServers=" + zkServers);
            }

            if (hostString == null) {
                hostString = hostAndPort[0].trim();
            } else {
                hostString += ("," + hostAndPort[0].trim());
            }
            if (hostAndPort.length > 1) {
                String port = hostAndPort[1].trim();
                if (portString != null && !portString.equals(port)) {
                    throw new PException("port is not same among all servers, zkServers=" + zkServers);
                }
                portString = port;
            }
        }
        if (portString == null) {
            throw new PException("no port found in zk server, zkServers=" + zkServers);
        }
        return hostString + ":" + portString;
    }

    // a simple function to get server and path from zkUri: zk://server/path
    // where server is formatted as 'host_1,host_2,host_3:port'
    protected static Pair<String, String> getZkServerAndPath(String zkUri) throws PException {
        try {
            // skip "zk://"
            String tempZkUri = zkUri.substring(5);
            int firstSlashIndex = tempZkUri.indexOf("/");
            String server = tempZkUri.substring(0, firstSlashIndex);
            String path = tempZkUri.substring(firstSlashIndex);
            return Pair.of(server, path);
        } catch (Exception e) {
            throw new PException(e);
        }
    }
}
