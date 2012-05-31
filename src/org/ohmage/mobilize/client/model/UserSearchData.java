package org.ohmage.mobilize.client.model;

import java.util.ArrayList;
import java.util.List;

import org.ohmage.mobilize.client.dataaccess.requestparams.UserSearchParams;

/**
 * Use this class to store UserSearchInfo objects and metadata when fetching
 *   users from the server a page at a time.
 */
public class UserSearchData {
  private List<UserSearchInfo> userSearchInfos;
  private int totalUsers = 0;
  private int startIndex = 0;
  
  public void setUserSearchInfo(int startIndex, List<UserSearchInfo> userSearchInfos) {
    this.userSearchInfos = new ArrayList<UserSearchInfo>(userSearchInfos);
    this.startIndex = startIndex;
  }
  
  public void setTotalUserCount(int count) {
    this.totalUsers = count;
  }
  
  public int getTotalUserCount() {
    return this.totalUsers;
  }
  
  public int getStartIndex() {
    return this.startIndex;
  }

  public List<UserSearchInfo> getUserSearchInfos() {
    return this.userSearchInfos;
  }
  
  // TODO: delete or mark enabled/disabled
}
