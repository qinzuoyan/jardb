package dsn.utils;

public class tools {
    private static class dsn_crc {
        public static final long crc64_poly = 0x9a6c9329ac4bc9b5l;
        public static final int crc32_poly = 0x82f63b78;
        public static final int crc32_table[] = new int[0x100];
        public static final long crc64_table[] = new long[0x100];

        static {
            for (int i = 0; i < 256; ++i) {
                int k1 = i;
                long k2 = (long) i;
                for (int j = 0; j < 8; ++j) {
                    if ((k1 & 1) == 1)
                        k1 = (k1 >>> 1) ^ crc32_poly;
                    else
                        k1 = (k1 >>> 1);

                    if ((k2 & 1) == 1)
                        k2 = (k2 >>> 1) ^ crc64_poly;
                    else
                        k2 = (k2 >>> 1);
                }
                crc32_table[i] = k1;
                crc64_table[i] = k2;
            }
        }
    }

    public static void sleepFor(long ms) {
        long startTime = System.currentTimeMillis();
        long elapse;
        while (ms > 0) {
            try {
                Thread.sleep(ms);
                break;
            } catch (InterruptedException e) {
                elapse = System.currentTimeMillis() - startTime;
                ms -= elapse;
                startTime += elapse;
            }
        }
    }

    public static void waitForever(Object obj) {
        try {
            obj.wait();
        } catch (InterruptedException e) {
        }
    }

    public static void notify(Object obj) {
        synchronized (obj) {
            obj.notify();
        }
    }

    public static int dsn_crc32(byte[] array) {
        return dsn_crc32(array, 0, array.length);
    }

    public static int dsn_crc32(byte[] array, int offset, int length) {
        int crc = -1;
        for (int i = offset; i < length; ++i)
            crc = dsn_crc.crc32_table[(array[i] ^ crc) & 0xFF] ^ (crc >>> 8);
        return ~crc;
    }

    public static long dsn_crc64(byte[] array) {
        return dsn_crc64(array, 0, array.length);
    }

    public static long dsn_crc64(byte[] array, int offset, int length) {
        long crc = -1;
        for (int i = offset; i < length; ++i)
            crc = dsn_crc.crc64_table[(array[i] ^ (int) crc) & 0xFF] ^ (crc >>> 8);
        return ~crc;
    }
}
