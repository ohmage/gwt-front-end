package edu.ucla.cens.mobilize.client.view;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import edu.ucla.cens.mobilize.client.common.RoleCampaign;
import edu.ucla.cens.mobilize.client.common.RunningState;
import edu.ucla.cens.mobilize.client.model.CampaignShortInfo;
import edu.ucla.cens.mobilize.client.model.CampaignDetailedInfo;
import edu.ucla.cens.mobilize.client.ui.CampaignDetail;
import edu.ucla.cens.mobilize.client.ui.CampaignEditFormView;
import edu.ucla.cens.mobilize.client.ui.CampaignList;

/**
 * Everything in the campaigns tab is part of the CampaignView.
 * (There are several subviews.)
 * 
 * @author vhajdik
 *
 */
public interface CampaignView extends IsWidget {
  // set flags to control display for different roles
  void setCanCreate(boolean canEdit);
  
  CampaignEditFormView getCampaignEditForm();  
  CampaignList getCampaignList(); 
  CampaignDetail getCampaignDetail(); 
  
  // hide/show subviews
  void showList();
  void showDetail();
  void showEditForm();
  
  // show messages to user
  void showError(String msg, Throwable caught);
  void showMsg(String msg);
  void hideMsg();

  // set data for display
  void setCampaignList(List<CampaignShortInfo> campaigns);
  void setCampaignListFilters(RunningState state, RoleCampaign role, Date fromDate, Date toDate);
  void setCampaignDetail(CampaignDetailedInfo campaign);
  void setCampaignEdit(CampaignDetailedInfo campaign);
  
}
