package edu.ucla.cens.mobilize.client.ui;


import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.common.HistoryTokens;
import edu.ucla.cens.mobilize.client.model.ClassInfo;

public class ClassDetail extends Composite {

  private static ClassDetailUiBinder uiBinder = GWT
      .create(ClassDetailUiBinder.class);

  interface ClassDetailUiBinder extends UiBinder<Widget, ClassDetail> {
  }

  @UiField InlineLabel className;
  @UiField InlineLabel classUrn;
  @UiField InlineLabel description;
  @UiField VerticalPanel privilegedMembersVerticalPanel;
  @UiField VerticalPanel membersVerticalPanel;
  @UiField InlineHyperlink editLink;
  
  public ClassDetail() {
    initWidget(uiBinder.createAndBindUi(this));
  }
  
  public void setClassDetail(ClassInfo classDetail) {
    this.editLink.setTargetHistoryToken(HistoryTokens.classEdit(classDetail.getClassId()));
    this.className.setText(classDetail.getClassName());
    this.classUrn.setText(classDetail.getClassId());
    this.description.setText(classDetail.getDescription());
    this.privilegedMembersVerticalPanel.clear();
    for (String privilegedUserLogin : classDetail.getPrivilegedMemberLogins()) {
      this.privilegedMembersVerticalPanel.add(new InlineLabel(privilegedUserLogin));
    }
    this.membersVerticalPanel.clear();
    for (String memberLogin : classDetail.getMemberLogins()) {
      this.membersVerticalPanel.add(new InlineLabel(memberLogin));
    }
    
  }

}
