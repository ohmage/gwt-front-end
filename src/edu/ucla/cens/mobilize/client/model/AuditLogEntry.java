package edu.ucla.cens.mobilize.client.model;

import java.util.Date;

import edu.ucla.cens.mobilize.client.AwConstants.AwUri;
import edu.ucla.cens.mobilize.client.common.RequestType;
import edu.ucla.cens.mobilize.client.common.ResponseStatus;

public class AuditLogEntry implements Comparable<AuditLogEntry> {
  private Date timestamp;
  private RequestType requestType;
  private ResponseStatus responseStatus;
  private String client;
  private double respondedMillis;
  private double receivedMillis;
  private double timeToFillRequest;
  private AwUri uri;
  private String requestParamsJson;
  private String extraDataJson;
  
  public Date getTimestamp() {
    return this.timestamp;
  }
  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp;
  }
  public RequestType getRequestType() {
    return requestType;
  }
  public void setRequestType(RequestType requestType) {
    this.requestType = requestType;
  }
  public ResponseStatus getResponseStatus() {
    return responseStatus;
  }
  public void setResponseStatus(ResponseStatus responseStatus) {
    this.responseStatus = responseStatus;
  }
  public String getClient() {
    return client;
  }
  public void setClient(String client) {
    this.client = client;
  }
  public double getRespondedMillis() {
    return respondedMillis;
  }
  public void setRespondedMillis(double respondedMillis) {
    this.respondedMillis = respondedMillis;
  }
  public double getReceivedMillis() {
    return receivedMillis;
  }
  public void setReceivedMillis(double d) {
    this.receivedMillis = d;
  }
  public double getTimeToFillRequest() {
    return timeToFillRequest;
  }
  public void setTimeToFillRequest(double timeToFillRequest) {
    this.timeToFillRequest = timeToFillRequest;
  }
  public AwUri getUri() {
    return uri;
  }
  public void setUri(String uriString) {
    this.uri = AwUri.fromString(uriString);
  }
  public void setUri(AwUri uri) {
    this.uri = uri;
  }
  public String getRequestParamsJson() {
    return requestParamsJson;
  }
  public void setRequestParamsJson(String requestParamsJson) {
    this.requestParamsJson = requestParamsJson;
  }
  public String getExtraDataJson() {
    return extraDataJson;
  }
  public void setExtraDataJson(String extraDataJson) {
    this.extraDataJson = extraDataJson;
  }
  @Override
  public int compareTo(AuditLogEntry other) {
    // sort by timestamp
    return this.getTimestamp().compareTo(other.getTimestamp());
  }
}
