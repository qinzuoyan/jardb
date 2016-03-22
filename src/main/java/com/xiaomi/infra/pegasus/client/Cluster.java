package com.xiaomi.infra.pegasus.client;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.thrift.TException;

import dsn.apps.*;

public class Cluster {
    static final int retry_when_meta_loss = 5;

    private cache.cluster_handler cluster_;

    public Cluster(String configFile) throws IOException, IllegalArgumentException {
        cluster_ = new cache.cluster_handler(configFile, retry_when_meta_loss);
        Properties config = new Properties();
        FileInputStream inputFile = new FileInputStream(configFile);
        config.load(inputFile);

        String meta_list = config.getProperty("replication.meta_servers");
        if (meta_list == null)
            throw new IllegalArgumentException("no property replication.meta_servers");
        String[] meta_address = meta_list.split(",");
        for (String addr : meta_address) {
            String[] pair = addr.split(":");
            cluster_.add_meta(pair[0], Integer.valueOf(pair[1]));
        }
    }

    public Table openTable(String name) throws ReplicationException, TException {
        return new Table(cluster_, name);
    }

    public Table openTable(String name, cache.key_hash hash_function) throws ReplicationException, TException {
        return new Table(cluster_, name, hash_function);
    }

    private static class VisitThread extends Thread {
        // so total operations = total_keys + removed_keys
        private static int total_keys = 1000000;
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
            for (int i = 0; i < total_count; ++i) {
                if ((i + 1) % 1000 == 0)
                    System.out.println(name + "round " + i);

                int t = (int) (Math.random() * (total_count - i));
                if (t < left_put) {
                    dsn.base.blob key = new dsn.base.blob(name + String.valueOf(key_cursor));
                    dsn.base.blob value = new dsn.base.blob(String.valueOf(values.get(key_cursor)));

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
                        dsn.base.blob key = new dsn.base.blob(name + String.valueOf(index));
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

            while (assigned_get < total_keys) {
                if ((assigned_get + 1) % 1000 == 0)
                    System.out.println(name + "get round " + assigned_get);
                try {
                    dsn.base.blob key = new dsn.base.blob(name + String.valueOf(assigned_get));
                    read_response resp = table.get(key);
                    if (resp.error == 0) {
                        value_sum += Integer.valueOf(resp.getValue());
                    }
                } catch (TException e) {
                    e.printStackTrace();
                } catch (ReplicationException e) {
                    e.printStackTrace();
                }
                ++assigned_get;
            }

            System.out.printf("execute time for %s, %d\n", name, System.currentTimeMillis() - time);
            long expected_sum = 0;
            for (int v : values)
                expected_sum += v;
            System.out.printf("%s: expected sum: %d, sum from db: %d, test %s\n",
                    name, expected_sum, value_sum, expected_sum == value_sum ? "ok" : "failed");
        }
    }

    private static void ping(Table t) throws TException, ReplicationException {
        System.out.println("ping our system with simple operations");
        int answer = t.put(new update_request(new dsn.base.blob("hello"), new dsn.base.blob("world")));
        System.out.println("put result: " + String.valueOf(answer));

        read_response resp = t.get(new dsn.base.blob("hello"));
        System.out.println("read result: " + resp.toString());

        answer = t.remove(new dsn.base.blob("hello"));
        System.out.println("remove result: " + String.valueOf(answer));

        resp = t.get(new dsn.base.blob("hello"));
        System.out.println("read result: " + resp.toString());
    }

    private static void multiThreadTest(Table t) throws TException, ReplicationException {
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

    public static void main(String[] args) throws IllegalArgumentException, IOException, ReplicationException, TException {
        Cluster c = new Cluster("jardb.properties");
        Table t = c.openTable("rrdb.instance0");

        ping(t);
        multiThreadTest(t);
    }
}
