package com.xiaomi.infra.pegasus.client;

import dsn.apps.ReplicationException;
import dsn.apps.read_response;
import dsn.apps.update_request;
import org.apache.thrift.TException;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by mi on 16-3-22.
 */
public class TestPing {

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
            for (int i = 0; i < total_keys; ++i)
                values.add((int) (Math.random() * 1000));

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
                    dsn.base.blob key = new dsn.base.blob( (name + String.valueOf(key_cursor)).getBytes() );
                    dsn.base.blob value = new dsn.base.blob(String.valueOf(values.get(key_cursor)).getBytes());

                    try {
                        if (table.put(new update_request(key, value)) != 0)
                            System.out.printf("%s: put failed, key=%s, value=%s\n", key.data, value.data);
                    } catch (TException e) {
                        e.printStackTrace();
                    } catch (ReplicationException e) {
                        e.printStackTrace();
                    }
                    left_put--;
                    ++key_cursor;
                } else {
                    int index = (int) (Math.random() * Math.min(key_cursor + 100, total_keys));

                    try {
                        dsn.base.blob key = new dsn.base.blob( (name + String.valueOf(index)).getBytes() );
                        if (table.remove(key) != 0)
                            System.out.printf("%s: remove failed, key=%s\n", name, key.data);
                        else if (index < key_cursor)
                            values.set(index, 0);
                    } catch (TException e) {
                        e.printStackTrace();
                    } catch (ReplicationException e) {
                        e.printStackTrace();
                    }
                }
            }

            for (int i = 1; i <= total_count; ++i) {
                if (i % 1000 == 0)
                    System.out.println(name + "get round " + i);
                try {
                    dsn.base.blob key = new dsn.base.blob( (name + String.valueOf(i)).getBytes() );
                    read_response resp = table.get(key);
                    if (resp.error == 0) {
                        value_sum += Integer.valueOf( new String(resp.getValue().data) );
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
    public void testPing() throws IllegalArgumentException, IOException, TException, ReplicationException {
        Cluster c = new Cluster();
        Table t = c.openTable("rrdb.instance0");

        System.out.println("ping our system with simple operations");
        int answer = t.put(new update_request(new dsn.base.blob("hello".getBytes()), new dsn.base.blob("world".getBytes())));
        System.out.println("put result: " + String.valueOf(answer));

        read_response resp = t.get(new dsn.base.blob("hello".getBytes()));
        System.out.println("read result: " + resp.toString());

        answer = t.remove(new dsn.base.blob("hello".getBytes()));
        System.out.println("remove result: " + String.valueOf(answer));

        resp = t.get(new dsn.base.blob("hello".getBytes()));
        System.out.println("read result: " + resp.toString());
    }

    @Test
    public void multiThreadTest() throws IllegalArgumentException, IOException, TException, ReplicationException {
        Cluster c = new Cluster();
        Table t = c.openTable("rrdb.instance0");

        System.out.println("start to run thread test");

        ArrayList<VisitThread> threadList = new ArrayList<VisitThread>();
        for (int i = 0; i < 10; ++i)
            threadList.add(new VisitThread("Thread_" + String.valueOf(i) + "_", t));
        for (VisitThread vt : threadList)
            vt.start();
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
