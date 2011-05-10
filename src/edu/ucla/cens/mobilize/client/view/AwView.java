package edu.ucla.cens.mobilize.client.view;

public interface AwView {
  void showMessage(String message);
  void showError(String error);
  void clearMessages();
}
