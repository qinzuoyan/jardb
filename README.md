# Pegasus Java Client

Performance test result (using com.xiaomi.infra.pegasus.client.TestBench):
```
fillseq :     430.3008 micros/op 2324.0 ops/sec
Microseconds per op:
Count: 100000  Average: 430.3008
Min: 320.0000  Max:873116.0000

readrandom :     153.2644 micros/op 6524.7 ops/sec
Microseconds per op:
Count: 100000  Average: 153.2644
Min: 55.0000  Max:8010.0000
```

As comparison, the test result using c++ client is:
```
fillseq_rrdb :     531.319 micros/op 1882 ops/sec;    0.2 MB/s
Microseconds per op:
Count: 100000  Average: 531.3196  StdDev: 1329.14
Min: 397.0000  Median: 488.5172  Max: 351585.0000

readrandom_rrdb :     345.503 micros/op 2894 ops/sec;    0.3 MB/s (100000 of 100000 found)
Microseconds per op:
Count: 100000  Average: 345.5033  StdDev: 108.39
Min: 140.0000  Median: 348.0257  Max: 8663.0000
```

