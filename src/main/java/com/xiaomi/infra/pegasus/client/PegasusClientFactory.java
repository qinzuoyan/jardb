package com.xiaomi.infra.pegasus.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author qinzuoyan
 */
public class PegasusClientFactory {
    private static final Log LOG = LogFactory.getLog(PegasusClientFactory.class);

    private static volatile PegasusClient singletonClient = null;
    private static Object singletonClientLock = new Object();

    public static PegasusClientInterface getSingletonClient() throws PException {
        return getSingletonClient("resource:///pegasus.properties");
    }

    public static PegasusClientInterface getSingletonClient(String configPath) throws PException {
        return createSingletonClient(new Class<?>[]{String.class}, new Object[]{configPath});
    }

    protected static PegasusClientInterface createSingletonClient(Class<?>[] paraTypes, Object[] parameters) throws PException {
        if (singletonClient == null) {
            synchronized (singletonClientLock) {
                if (singletonClient == null) {
                    try {
                        singletonClient = PegasusClient.class.getConstructor(paraTypes).newInstance(parameters);
                        LOG.info("Create Singleton PegasusClient");
                    } catch (Throwable e) {
                        throw new PException("Create Singleton PegasusClient failed", e);
                    }
                }
            }
        }
        return singletonClient;
    }
}
