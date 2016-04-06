package com.xiaomi.infra.pegasus.client;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

/**
 * Created by mi on 16-3-22.
 */
public class TestPing {
    @Test
    public void testPing() throws PException {
        PegasusClientInterface client = PegasusClientFactory.getSingletonClient();
        String tableName = "rrdb.instance0";

        byte[] hashKey = "hello".getBytes();
        byte[] sortKey = "0".getBytes();
        byte[] value = "world".getBytes();

        System.out.println("set value ...");
        client.set(tableName, hashKey, sortKey, value);
        System.out.println("set value ok");

        System.out.println("get value ...");
        byte[] result = client.get(tableName, hashKey, sortKey);
        Assert.assertTrue(Arrays.equals(value, result));
        System.out.println("get value ok");

        System.out.println("del value ...");
        client.del(tableName, hashKey,sortKey);
        System.out.println("del value ok");

        System.out.println("get deleted value ...");
        result = client.get(tableName, hashKey, sortKey);
        Assert.assertEquals(result, null);
        System.out.println("get deleted value ok");

        PegasusClientFactory.closeSingletonClient();
    }

    @Test
    public void testPingZK() throws PException {
        String zkPath = "zk://127.0.0.1:12181/databases/pegasus/test-java-client";
        PegasusClientInterface client = PegasusClientFactory.getSingletonClient(zkPath);
        String tableName = "rrdb.instance0";

        byte[] hashKey = "hello".getBytes();
        byte[] sortKey = "0".getBytes();
        byte[] value = "world".getBytes();

        System.out.println("set value ...");
        client.set(tableName, hashKey, sortKey, value);
        System.out.println("set value ok");

        System.out.println("get value ...");
        byte[] result = client.get(tableName, hashKey, sortKey);
        Assert.assertTrue(Arrays.equals(value, result));
        System.out.println("get value ok");

        System.out.println("del value ...");
        client.del(tableName, hashKey,sortKey);
        System.out.println("del value ok");

        System.out.println("get deleted value ...");
        result = client.get(tableName, hashKey, sortKey);
        Assert.assertEquals(result, null);
        System.out.println("get deleted value ok");

        PegasusClientFactory.closeSingletonClient();
    }
}
