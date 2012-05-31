package org.ohmage.mobilize.client.utils;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;

import org.ohmage.mobilize.client.common.HistoryTokens;
import org.ohmage.mobilize.client.exceptions.AuthenticationException;

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
