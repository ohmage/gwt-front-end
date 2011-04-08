package edu.ucla.cens.mobilize.client.presenter;

import java.util.List;
import java.util.Map;

public interface Presenter {
  void go(Map<String, List<String>> params);
}
