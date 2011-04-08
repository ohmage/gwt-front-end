package edu.ucla.cens.mobilize.client.common;

import java.util.List;

public class UserRoles {
  public boolean admin;
  public boolean author;
  public boolean participant;
  public boolean analyst;
  public boolean supervisor;
  
  public UserRoles() {}
  
  public UserRoles(List<UserRole> listOfRoles) {
    admin = listOfRoles.contains(UserRole.ADMIN);
    author = listOfRoles.contains(UserRole.AUTHOR);
    participant = listOfRoles.contains(UserRole.PARTICIPANT);
    analyst = listOfRoles.contains(UserRole.ANALYST);
    supervisor = listOfRoles.contains(UserRole.SUPERVISOR);
  }
  
}
