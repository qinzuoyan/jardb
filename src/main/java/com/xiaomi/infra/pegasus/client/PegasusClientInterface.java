package com.xiaomi.infra.pegasus.client;

import java.util.Properties;

/**
 * @author qinzuoyan
 */
public interface PegasusClientInterface {
    /**
     * Get pegasus configuration for client.
     * @return config
     */
    public Properties getConfiguration();

    /**
     * Get value.
     * @param tableName table name
     * @param hashKey used to decide which partition to put this k-v,
     *                should not be null, and length must be greater than 0
     * @param sortKey all the k-v under hashKey will be sorted by sortKey,
     *                if null or length == 0, means no sort key.
     * @return value
     * @return null if not found
     * @throws PException
     */
    public byte[] get(String tableName, byte[] hashKey, byte[] sortKey) throws PException;

    /**
     * Set value.
     * @param tableName table name
     * @param hashKey used to decide which partition to put this k-v,
     *                should not be null, and length must be greater than 0
     * @param sortKey all the k-v under hashKey will be sorted by sortKey,
     *                if null or length == 0, means no sort key.
     * @param value should not be null
     * @throws PException
     */
    public void set(String tableName, byte[] hashKey, byte[] sortKey, byte[] value) throws PException;

    /**
     * Delete value.
     * @param tableName table name
     * @param hashKey used to decide which partition to put this k-v,
     *                should not be null, and length must be greater than 0
     * @param sortKey all the k-v under hashKey will be sorted by sortKey,
     *                if null or length == 0, means no sort key.
     * @throws PException
     */
    public void del(String tableName, byte[] hashKey, byte[] sortKey) throws PException;
}
