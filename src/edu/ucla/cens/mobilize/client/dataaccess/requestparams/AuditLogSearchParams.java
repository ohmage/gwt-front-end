package edu.ucla.cens.mobilize.client.dataaccess.requestparams;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import edu.ucla.cens.mobilize.client.AwConstants.ErrorCode;
import edu.ucla.cens.mobilize.client.common.RequestType;
import edu.ucla.cens.mobilize.client.common.ResponseStatus;
import edu.ucla.cens.mobilize.client.utils.DateUtils;
import edu.ucla.cens.mobilize.client.utils.MapUtils;

public class AuditLogSearchParams implements RequestParams {
  
  public String authToken;
  public String client;
  public RequestType requestType_opt;
  public ResponseStatus responseType_opt;
  public String uri_opt; // should be one of the endpoints in AwConstants.AwUri
  public String clientValue_opt;
  public String deviceIdValue_opt;
  public ErrorCode errorCode_opt;
  public Date startDate_opt;
  public Date endDate_opt;
  
  public String toString() {
    assert authToken != null && !authToken.isEmpty() : "authToken is required";
    assert client != null && !client.isEmpty() : "client is required";
    
    Map<String, String> params = new HashMap<String, String>();
    params.put("auth_token", authToken);
    params.put("client", client);
    if (requestType_opt != null) {
      params.put("request_type", requestType_opt.toServerString());
    }
    if (responseType_opt != null) {
      params.put("response_type", responseType_opt.toServerString());
    }
    if (uri_opt != null && !uri_opt.isEmpty()) {
      params.put("uri", uri_opt);
    }
    if (clientValue_opt != null && !clientValue_opt.isEmpty()) {
      params.put("client", clientValue_opt);
    }
    if (deviceIdValue_opt != null && !deviceIdValue_opt.isEmpty()) {
      params.put("device_id_value", deviceIdValue_opt);
    }
    // FIXME: will this work? do the error code strings need "E" in front?
    if (errorCode_opt != null) {
      params.put("error_code", errorCode_opt.getErrorCode());
    }
    if (startDate_opt != null && endDate_opt != null) {
      params.put("start_date", DateUtils.translateToApiRequestFormat(startDate_opt));
      params.put("end_date", DateUtils.translateToApiRequestFormat(endDate_opt));
    }
    return MapUtils.translateToParameters(params);
  }
}
