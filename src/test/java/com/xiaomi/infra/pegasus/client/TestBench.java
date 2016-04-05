package com.xiaomi.infra.pegasus.client;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by mi on 16-3-23.
 */
public class TestBench {
    @Test
    public void testBench() throws PException {
        PegasusClientInterface client = PegasusClientFactory.getSingletonClient();
        String tableName = "rrdb.instance0";

        System.out.println("start to run single-thread test");

        int total_count = 100000;
        ArrayList<byte[]> keys = new ArrayList<byte[]>();
        ArrayList<byte[]> values = new ArrayList<byte[]>();
        for (int i = 0; i < total_count; i++) {
            String key = "testBench-" + String.format("%06d", i);
            String value = key + "-";
            StringBuilder sb = new StringBuilder();
            sb.append(key);
            sb.append("-");
            while (sb.length() < 100) {
                sb.append('0');
            }
            keys.add(key.getBytes());
            values.add(value.getBytes());
        }

        {
            long min_time = Long.MAX_VALUE;
            long max_time = 0;
            long start_time = System.nanoTime();
            long last_time = start_time;
            for (int i = 0; i < total_count; i++) {
                long begin_time = System.nanoTime();
                client.set(tableName, keys.get(i), null, values.get(i));
                long end_time = System.nanoTime();
                long dur_time = end_time - begin_time;
                if (dur_time < min_time) {
                    min_time = dur_time;
                }
                if (dur_time > max_time) {
                    max_time = dur_time;
                }
                if ((i + 1) % 1000 == 0) {
                    long cur_time = System.nanoTime();
                    long last_dur_time = cur_time - last_time;
                    String last_dur_time_str = String.format("%d.%06d", last_dur_time / 1000000000, last_dur_time % 1000000000 / 1000);
                    long total_dur_time = cur_time - start_time;
                    String total_dur_time_str = String.format("%d.%06d", total_dur_time / 1000000000, total_dur_time % 1000000000 / 1000);
                    double last_qps = (double) 1000 / last_dur_time * 1000000000;
                    double total_qps = (double) (i + 1) / total_dur_time * 1000000000;
                    String last_qps_str = String.format("%.1f", last_qps);
                    String total_qps_str = String.format("%.1f", total_qps);
                    System.out.println("testBench: (1000," + (i + 1) + ") ops and (" + last_qps_str + "," + total_qps_str + ") ops/second in (" + last_dur_time_str + "," + total_dur_time_str + ") seconds");
                    last_time = cur_time;
                }
            }
            double avg_time = (double)(last_time - start_time) / 100000 / 1000;
            String avg_time_str = String.format("%.4f", avg_time);
            double total_qps = (double) 100000 / (last_time - start_time) * 1000000000;
            String total_qps_str = String.format("%.1f", total_qps);
            System.out.println("fillseq :     " + avg_time_str + " micros/op " + total_qps_str + " ops/sec");
            System.out.println("Microseconds per op:");
            System.out.println("Count: 100000  Average: " + avg_time_str);
            System.out.println("Min: " + (min_time / 1000) + ".0000  Max:" + (max_time / 1000) + ".0000");
        }

        {
            long min_time = Long.MAX_VALUE;
            long max_time = 0;
            long start_time = System.nanoTime();
            long last_time = start_time;
            for (int i = 0; i < total_count; i++) {
                long begin_time = System.nanoTime();
                byte[] value = client.get(tableName, keys.get(i), null);
                long end_time = System.nanoTime();
                Assert.assertTrue(value != null);
                Assert.assertTrue(Arrays.equals(values.get(i), value));
                long dur_time = end_time - begin_time;
                if (dur_time < min_time) {
                    min_time = dur_time;
                }
                if (dur_time > max_time) {
                    max_time = dur_time;
                }
                if ((i + 1) % 1000 == 0) {
                    long cur_time = System.nanoTime();
                    long last_dur_time = cur_time - last_time;
                    String last_dur_time_str = String.format("%d.%06d", last_dur_time / 1000000000, last_dur_time % 1000000000 / 1000);
                    long total_dur_time = cur_time - start_time;
                    String total_dur_time_str = String.format("%d.%06d", total_dur_time / 1000000000, total_dur_time % 1000000000 / 1000);
                    double last_qps = (double) 1000 / last_dur_time * 1000000000;
                    double total_qps = (double) (i + 1) / total_dur_time * 1000000000;
                    String last_qps_str = String.format("%.1f", last_qps);
                    String total_qps_str = String.format("%.1f", total_qps);
                    System.out.println("testBench: (1000," + (i + 1) + ") ops and (" + last_qps_str + "," + total_qps_str + ") ops/second in (" + last_dur_time_str + "," + total_dur_time_str + ") seconds");
                    last_time = cur_time;
                }
            }
            double avg_time = (double)(last_time - start_time) / 100000 / 1000;
            String avg_time_str = String.format("%.4f", avg_time);
            double total_qps = (double) 100000 / (last_time - start_time) * 1000000000;
            String total_qps_str = String.format("%.1f", total_qps);
            System.out.println("readrandom :     " + avg_time_str + " micros/op " + total_qps_str + " ops/sec");
            System.out.println("Microseconds per op:");
            System.out.println("Count: 100000  Average: " + avg_time_str);
            System.out.println("Min: " + (min_time / 1000) + ".0000  Max:" + (max_time / 1000) + ".0000");
        }
    }
}
