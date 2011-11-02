package edu.ucla.cens.mobilize.client.model;

public class UserShortInfo implements Comparable<UserShortInfo> {
  
  String username;
  String firstName;
  String lastName;
  String email;
  String organization;
  String personalId;
  
  public String getUsername() {
    return username;
  }
  public void setUsername(String username) {
    this.username = username;
  }
  public String getFirstName() {
    return firstName;
  }
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }
  public String getLastName() {
    return lastName;
  }
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }
  public String getEmail() {
    return email;
  }
  public void setEmail(String email) {
    this.email = email;
  }
  public String getOrganization() {
    return organization;
  }
  public void setOrganization(String organization) {
    this.organization = organization;
  }
  public String getPersonalId() {
    return personalId;
  }
  public void setPersonalId(String personalId) {
    this.personalId = personalId;
  }
  
  @Override
  public int compareTo(UserShortInfo other) {
    return this.getUsername().compareTo(other.getUsername());
  }
}
