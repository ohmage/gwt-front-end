package edu.ucla.cens.mobilize.client.utils;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;

import edu.ucla.cens.mobilize.client.common.HistoryTokens;
import edu.ucla.cens.mobilize.client.exceptions.AuthenticationException;

public class AwErrorUtils {
  public static void logoutIfAuthException(Throwable caught) {
    if (caught.getClass().equals(AuthenticationException.class)) {
      Timer t = new Timer() {
        @Override
        public void run() {
          History.newItem(HistoryTokens.logout());          
        }
      };
      t.schedule(2500);
    }
  }
}
