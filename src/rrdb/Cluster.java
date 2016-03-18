package rrdb;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.thrift.TException;

import dsn.apps.*;

public class Cluster {
  static final int retry_when_meta_loss = 5;
  
  private cache.cluster_handler cluster_;   
  private static boolean ignoreLine(String line) {
    if (line == null || line.isEmpty() || line.charAt(0)=='\n' || line.charAt(0)=='#')
      return true;
    return false;
  }

  private static String[] get_address(String line) throws IllegalArgumentException
  {
    String[] result = line.split(":");
    if (result == null || result.length != 2)
      throw new IllegalArgumentException("invalid ip address: "+line);    
    return result;
  }
  
  public Cluster(String configFile) throws IOException, IllegalArgumentException
  {
    cluster_ = new cache.cluster_handler(configFile, retry_when_meta_loss);

    FileReader dataFile = new FileReader(configFile);
    @SuppressWarnings("resource")
    BufferedReader bufferFile = new BufferedReader(dataFile);
    
    String line;
    boolean startReadMeta = false;
    while ( (line = bufferFile.readLine()) != null ) {
      line.trim();
      if ( ignoreLine(line) )
        continue;
      
      if ( "[replication.meta_servers]".equals(line) )
        startReadMeta = true;
      else if (startReadMeta == true) {
        if ( line.startsWith("[") )
          startReadMeta = false;
        else {
          String[] pair = get_address(line);
          cluster_.add_meta(pair[0], Integer.parseInt(pair[1]));
        }
      }
    }
  }
  
  public Table openTable(String name) throws ReplicationException, TException
  {
    return new Table(cluster_, name);
  }
  
  public Table openTable(String name, dsn.apps.cache.key_hasher hash_function) throws ReplicationException, TException
  {
    return new Table(cluster_, name, hash_function);
  }
  
  private static class VisitThread extends Thread {
    // so total operations = total_keys + removed_keys
    private static int total_keys = 1000;
    private static int removed_keys = 300;
    
    private String name;
    private Table table;
    public VisitThread(Table t)
    {
      this.name = Thread.currentThread().getName() + "_";
      this.table = t;
    }
    
    public void run() 
    {
      ArrayList<Integer> values = new ArrayList<Integer>(total_keys);
      
      long value_sum = 0;
      for (int i=0; i<total_keys; ++i)
        values.add((int) (Math.random()*1000));
      
      int left_put = total_keys;
      int assigned_get = 0;
      
      int key_cursor = 0;
      int total_count = total_keys + removed_keys;
      
      long time = System.currentTimeMillis();
      for (int i=0; i<total_count; ++i)
      {
        int t = (int) (Math.random()*(total_count-i));
        if (t < left_put) {       
          dsn.base.blob key = new dsn.base.blob(name+String.valueOf(key_cursor));
          dsn.base.blob value = new dsn.base.blob(String.valueOf( values.get(key_cursor) ));
          
          try {
            if ( table.put(new update_request(key, value)) != 0 )
              System.out.printf("%s: put failed, key=%s, value=%s\n", key.data, value.data);
          } catch (TException | ReplicationException e) {
            e.printStackTrace();
          }
          left_put--;
          ++key_cursor;       
        }
        else {
          int index = (int) (Math.random()*Math.min(key_cursor+100, total_keys));
          
          try {
            dsn.base.blob key = new dsn.base.blob(name + String.valueOf(index));
            if ( table.remove(key) != 0 )
              System.out.printf("%s: remove failed, key=%s\n", name, key.data);
            else if (index < key_cursor)
              values.set(index, 0);
          }
          catch (TException | ReplicationException e) {
            System.out.println(e.getLocalizedMessage());
          }       
        }     
      }
      
      while (assigned_get < total_keys)
      {
        try {
          dsn.base.blob key = new dsn.base.blob(name + String.valueOf(assigned_get));
          read_response resp = table.get(key);
          if (resp.error == 0) {
            value_sum += Integer.valueOf(resp.getValue());
          }
        }
        catch (TException | ReplicationException e) {
          System.out.println(e.getLocalizedMessage());
        }
        ++assigned_get;     
      }
      
      System.out.printf("execute time for %s, %d\n", name, System.currentTimeMillis() - time);
      long expected_sum = 0;
      for (int v: values)
        expected_sum += v;
      System.out.printf("%s: expected sum: %d, sum from db: %d, test %s\n", 
          name, expected_sum, value_sum, expected_sum==value_sum?"ok":"failed");
    }
  }
  
  private static void ping(Table t) throws TException, ReplicationException
  {
    System.out.println("ping our system with simple operations");
    int answer = t.put(new update_request(new dsn.base.blob("hello"), new dsn.base.blob("world")));
    System.out.println("put result: " + String.valueOf(answer));
    
    read_response resp = t.get( new dsn.base.blob("hello"));
    System.out.println("read result: " + resp.toString());
    
    answer = t.remove(new dsn.base.blob("hello"));
    System.out.println("remove result: " + String.valueOf(answer));
    
    resp = t.get(new dsn.base.blob("hello"));
    System.out.println("read result: " + resp.toString());    
  }

  private static void multiThreadTest(Table t) throws TException, ReplicationException
  {
    System.out.println("start to run thread test");
    
    ArrayList<VisitThread> threadList = new ArrayList<VisitThread>();
    for (int i=0; i<10; ++i)
      threadList.add(new VisitThread(t) );
    for (VisitThread vt: threadList)
      vt.start();
    for (VisitThread vt: threadList)
    {
      while (true) {
        try {
          vt.join();
          break;
        }
        catch (InterruptedException e)
        {
        }
      }
    }
  }
  
  private static void definiteLoop(Table t) 
  {
    int i=0;
    while (true) {
      dsn.base.blob key = new dsn.base.blob( "test_key" + String.valueOf(i));
      StringBuilder sb = new StringBuilder();
      for (int j=0; j<10; ++j)
        sb.append(String.valueOf(i));
      
      while ( true ) {
        try {
          int answer = t.put(new update_request(key, new dsn.base.blob(sb.toString())));
          System.out.println("put key " + key.data + " with result " + answer);          
          utils.tools.sleepFor(1000);
          
          read_response resp = t.get(key);
          System.out.println("read result: " + resp.toString());
          utils.tools.sleepFor(1000);
          
          answer = t.remove(key);
          System.out.println("remove result: " + String.valueOf(answer));
          utils.tools.sleepFor(1000);
          
          resp = t.get(key);
          System.out.println("read result: " + resp.toString());
          utils.tools.sleepFor(1000);
          
          break;
        }
        catch (TException | ReplicationException e) {
          e.printStackTrace();
        }
      }
      
      ++i;
    }
  }
  
  public static void main(String[] args) throws IllegalArgumentException, IOException, ReplicationException, TException
  {
    Cluster c = new Cluster("config.ini");
    Table t = c.openTable("rrdb.instance0");
   
    ping(t);
    multiThreadTest(t);
    definiteLoop(t);
  }
}
