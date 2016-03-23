package com.xiaomi.infra.pegasus.client;

import dsn.apps.ReplicationException;
import dsn.apps.read_response;
import dsn.apps.update_request;
import junit.framework.Assert;
import org.apache.thrift.TException;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by mi on 16-3-23.
 */
public class TestMultiThread {
    private static class VisitThread extends Thread {
        // so total operations = total_keys + removed_keys
        private static int total_keys = 7000;
        private static int removed_keys = 3000;

        private String name;
        private Table table;

        public VisitThread(String name, Table t) {
            this.name = name;
            this.table = t;
        }

        public void run() {
            ArrayList<Integer> values = new ArrayList<Integer>(total_keys);

            long value_sum = 0;
            for (int i = 0; i < total_keys; ++i) {
                values.add((int) (Math.random() * 1000));
            }

            int left_put = total_keys;
            int assigned_get = 0;

            int key_cursor = 0;
            int total_count = total_keys + removed_keys;

            long time = System.currentTimeMillis();
            for (int i = 1; i <= total_count; ++i) {
                if (i % 1000 == 0)
                    System.out.println(name + "round " + i);

                int t = (int) (Math.random() * (total_count - i));
                if (t < left_put) {
                    dsn.base.blob key = new dsn.base.blob(name + String.valueOf(key_cursor));
                    dsn.base.blob value = new dsn.base.blob(String.valueOf(values.get(key_cursor)));
                    try {
                        int r = table.put(new update_request(key, value));
                        Assert.assertEquals(0, r);
                    } catch (TException e) {
                        e.printStackTrace();
                        Assert.assertTrue(false);
                    } catch (ReplicationException e) {
                        e.printStackTrace();
                        Assert.assertTrue(false);
                    }
                    left_put--;
                    ++key_cursor;
                } else {
                    int index = (int) (Math.random() * Math.min(key_cursor + 100, total_keys));
                    dsn.base.blob key = new dsn.base.blob(name + String.valueOf(index));
                    try {
                        int r = table.remove(key);
                        Assert.assertEquals(0, r);
                        if (index < key_cursor) {
                            values.set(index, 0);
                        }
                    } catch (TException e) {
                        e.printStackTrace();
                        Assert.assertTrue(false);
                    } catch (ReplicationException e) {
                        e.printStackTrace();
                        Assert.assertTrue(false);
                    }
                }
            }

            for (int i = 1; i <= total_count; ++i) {
                if (i % 1000 == 0) {
                    System.out.println(name + "get round " + i);
                }
                dsn.base.blob key = new dsn.base.blob(name + String.valueOf(i));
                try {
                    read_response resp = table.get(key);
                    if (resp.error == 0) {
                        value_sum += Integer.valueOf(resp.getValue());
                    }
                } catch (TException e) {
                    e.printStackTrace();
                } catch (ReplicationException e) {
                    e.printStackTrace();
                }
            }

            System.out.printf("execute time for %s, %d\n", name, System.currentTimeMillis() - time);
            long expected_sum = 0;
            for (int v : values)
                expected_sum += v;
            System.out.printf("%s: expected sum: %d, sum from db: %d, test %s\n",
                    name, expected_sum, value_sum, expected_sum == value_sum ? "ok" : "failed");
        }
    }

    @Test
    public void testMultiThread() throws IllegalArgumentException, IOException, TException, ReplicationException {
        Cluster c = new Cluster();
        Table t = c.openTable("rrdb.instance0");

        System.out.println("start to run multi-thread test");

        ArrayList<VisitThread> threadList = new ArrayList<VisitThread>();
        for (int i = 0; i < 10; ++i) {
            threadList.add(new VisitThread("Thread_" + String.valueOf(i) + "_", t));
        }
        for (VisitThread vt : threadList) {
            vt.start();
        }
        for (VisitThread vt : threadList) {
            while (true) {
                try {
                    vt.join();
                    break;
                } catch (InterruptedException e) {
                }
            }
        }
    }
}
