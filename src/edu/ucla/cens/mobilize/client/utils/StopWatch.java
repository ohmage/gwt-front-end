package edu.ucla.cens.mobilize.client.utils;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.Duration;

public class StopWatch {
  private static StopWatchTimer timer = new StopWatchNoOp();
  
  public static void enable() {
    StopWatch.timer = new StopWatchDebug();
  }
  
  public static void start(String key) {
    StopWatch.timer.start(key);
  }
  
  public static void stop(String key) {
    StopWatch.timer.stop(key);
  }
  
  public static Double getTotal(String key) {
    return StopWatch.timer.getTotal(key);
  }
  
  public static void reset(String key) {
    StopWatch.timer.reset(key);
  }
  
  public static void resetAll() {
    StopWatch.timer.resetAll();
  }
  
  public static Map<String, Double> getTotals() {
    return StopWatch.timer.getTotals();
  }
  
  public static String getTotalsString() {
    return StopWatch.timer.getTotalsString();
  }
  
  private interface StopWatchTimer {
    void start(String key);
    void stop(String key);
    Double getTotal(String key);
    void reset(String key);
    void resetAll();
    Map<String, Double> getTotals();
    String getTotalsString();
  }
  
  private static class StopWatchDebug implements StopWatchTimer {
    private static class TimeData { public double start=0.0; public double total=0.0; }
    
    private static Map<String, TimeData> times = new HashMap<String, TimeData>();
    
    @Override
    public void start(String key) {
      if (!times.containsKey(key)) {
        times.put(key, new TimeData());
      }
      times.get(key).start = Duration.currentTimeMillis();
    }

    @Override
    public void stop(String key) {
      if (times.containsKey(key)) {
        times.get(key).total += Duration.currentTimeMillis() - times.get(key).start;
      }
    }

    @Override
    public Double getTotal(String key) {
      return times.containsKey(key) ? times.get(key).total : -1; 
    }

    @Override
    public void reset(String key) {
      times.remove(key);
    }

    @Override
    public void resetAll() {
      times.clear();
    }

    @Override
    public Map<String, Double> getTotals() { 
      Map<String, Double> totals = new HashMap<String, Double>();
      for (String key : times.keySet()) {
        totals.put(key, times.get(key).total);
      }
      return totals;
    }

    @Override
    public String getTotalsString() {
      StringBuilder sb = new StringBuilder();
      for (String key : times.keySet()) {
        sb.append(key).append(":").append(times.get(key).total).append("  ");
      }
      return sb.toString();
    }
  };
  
  private static class StopWatchNoOp implements StopWatchTimer {

    @Override
    public void start(String key) {}

    @Override
    public void stop(String key) {}

    @Override
    public Double getTotal(String key) { return -1.0; }

    @Override
    public void reset(String key) {}

    @Override
    public void resetAll() {}

    @Override
    public Map<String, Double> getTotals() {
      Map<String, Double> map = new HashMap<String, Double>();
      map.put("no_times_in_production", -1.0);
      return map;
    }

    @Override
    public String getTotalsString() { return "stopwatch not enabled"; }
  }
}
