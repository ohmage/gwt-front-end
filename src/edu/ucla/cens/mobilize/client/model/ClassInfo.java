package edu.ucla.cens.mobilize.client.model;

public class ClassInfo {
  private String classId;
  private String classUrn;
  private String state; // e.g., CA
  private String district;
  private String school;
  private String classTitle; // e.g., CS101
  private String term;
  private int year;
  
  public ClassInfo() {
    classId = classUrn = state = district = school = classTitle = term = "";
    year = 0;
  }
  
  public String getClassId() { return this.classId; }
  public String getClassUrn() { return this.classUrn; }
  public String getState() { return this.state; }
  public String getDistrict() { return this.district; }
  public String getSchool() { return this.school; }
  public String getClassTitle() { return this.classTitle; }
  public String getTerm() { return this.term; }
  public int getYear() { return this.year; }
  
  public void setClassId(String classId) { this.classId = classId; }
  public void setClassUrn(String classUrn) { this.classUrn = classUrn; }
  public void setState(String state) { this.state = state; }
  public void setDistrict(String district) { this.district = district; }
  public void setSchool(String school) { this.school = school; }
  public void setClassTitle(String classTitle) { this.classTitle = classTitle; }
  public void setTerm(String term) { this.term = term; }
  public void setYear(int year) { this.year = year; }
}
