package edu.ucla.cens.mobilize.client.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds information about a specific campaign configuration, this is based on the configuration version.
 * 
 * @author jhicks
 *
 */
public class ConfigurationInfo {
    private List<SurveyInfo> surveyList;
    private String xmlConfiguration;
  
    
    public ConfigurationInfo() {
        surveyList = new ArrayList<SurveyInfo>();
    }
    
    public ConfigurationInfo(ConfigurationInfo configurationInfo) {
        setSurveyList(configurationInfo.getSurveyList());
        setXmlConfiguration(configurationInfo.getXmlConfiguration());
    }
    
    public void addSurvey(SurveyInfo survey) { surveyList.add(survey); }
    public void setSurveyList(List<SurveyInfo> surveyList) { this.surveyList = surveyList; }
    public List<SurveyInfo> getSurveyList() {
        List<SurveyInfo> copy = new ArrayList<SurveyInfo>();
        for (SurveyInfo survey:surveyList) {
            copy.add(new SurveyInfo(survey));
        }
        return copy;
    }
    
    public void setXmlConfiguration(String xmlConfiguration) { this.xmlConfiguration = xmlConfiguration; }
    public String getXmlConfiguration() { return xmlConfiguration; }
    
    public String toString() {
        StringBuffer toString = new StringBuffer();
        
        toString.append("ConfigurationInfo: ");
        toString.append(", surveyList: " + getSurveyList());
        
        return toString.toString();
    }
    
    public List<SurveyInfo> getSurveys() {
      List<SurveyInfo> retval = new ArrayList<SurveyInfo>();
      retval.addAll(this.surveyList);
      return retval;
    }
}
