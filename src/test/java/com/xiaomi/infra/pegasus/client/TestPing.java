package com.xiaomi.infra.pegasus.client;

import dsn.apps.ReplicationException;
import dsn.apps.read_response;
import dsn.apps.update_request;
import junit.framework.Assert;
import org.apache.thrift.TException;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.SynchronousQueue;

/**
 * Created by mi on 16-3-22.
 */
public class TestPing {
    @Test
    public void testPing() throws IllegalArgumentException, IOException, TException, ReplicationException {
        Cluster c = new Cluster();
        Table t = c.openTable("rrdb.instance0");

        System.out.println("start to run ping test");

        System.out.println("ping our system with simple operations");
        int answer = t.put(new update_request(new dsn.base.blob("hello"), new dsn.base.blob("world")));
        System.out.println("put result: " + String.valueOf(answer));
        Assert.assertEquals(0, answer);

        read_response resp = t.get(new dsn.base.blob("hello"));
        System.out.println("read result: " + resp.toString());
        Assert.assertEquals(0, resp.getError());
        Assert.assertEquals("world", resp.getValue());

        answer = t.remove(new dsn.base.blob("hello"));
        System.out.println("remove result: " + String.valueOf(answer));
        Assert.assertEquals(0, answer);

        resp = t.get(new dsn.base.blob("hello"));
        System.out.println("read result: " + resp.toString());
        Assert.assertEquals(1, resp.getError());
    }
}
