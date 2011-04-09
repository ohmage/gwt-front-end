package edu.ucla.cens.mobilize.client.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import edu.ucla.cens.mobilize.client.model.PromptInfo;
import edu.ucla.cens.mobilize.client.model.SurveyInfo;

import name.pehl.totoe.xml.client.Document;
import name.pehl.totoe.xml.client.Node;
import name.pehl.totoe.xml.client.XPathException;
import name.pehl.totoe.xml.client.XmlParseException;
import name.pehl.totoe.xml.client.XmlParser;

/**
 * Helper class for parsing/querying campaign xml config
 * @author vhajdik
 *
 */

// TODO: test and then put this in awdatatranslators in place of xml translation

public class XmlConfigTranslator {
  private static Logger _logger = Logger.getLogger(XmlConfigTranslator.class.getName());
  private Document xmlDocument_doNotUse;
  private Document xmlDocument() {
    Document retval = null;
    if (xmlDocument_doNotUse != null) {
      retval = xmlDocument_doNotUse;
    } else {
      _logger.severe("XmlConfigTranslator used before xml doc was loaded.");
      retval = new XmlParser().parse("<error>XmlConfigTranslator used before xml doc was loaded<error>");
    }
    return retval;
  }
  
  // http://code.google.com/p/totoe/wiki/XmlUsage
  // https://github.com/cens/AndWellnessConfiguration/blob/master/spec/configuration.xsd
  
  // returns true if document was loaded successfully
  public boolean loadFromXml(String xmlString) {
    boolean success = false;
    try {
      // string is used to build dom and then discarded
      this.xmlDocument_doNotUse = new XmlParser().parse(xmlString);
      success = true;
    } catch (XmlParseException xmlException) {
      _logger.severe("Document could not be loaded. XmlParseException: " + 
      		xmlException.getMessage());
    }
    return success;
  }
  
  public String getCampaignName() {
    return xmlDocument().selectValue("//campaignName");
  }
  
  // get info object for one survey
  public SurveyInfo getSurveyInfo(String surveyId) {
    SurveyInfo surveyInfo = null;
    try {
      String deleteme = xmlDocument().toString();
      Node surveyNode = xmlDocument().selectNode("//survey[id=\"" + surveyId + "\"]");
      surveyInfo = this.nodeToSurveyInfo(surveyNode);
    } catch (XPathException exception) {
      // TODO: log warning
    } catch (Exception otherException) {
      // TODO: wtf?
    }
    return surveyInfo;
  }
  
  // gets a list of all survey infos in this campaign
  public List<SurveyInfo> getSurveyInfos() {
    List<SurveyInfo> retVal = new ArrayList<SurveyInfo>();
    List<Node> surveyNodes = xmlDocument().selectNodes("//survey");
    // https://github.com/cens/AndWellnessConfiguration/blob/master/spec/configuration.xsd
    for (Node n : surveyNodes) {
      retVal.add(nodeToSurveyInfo(n));
    }
    return retVal;
  }
  
  // gets a list of ids of all surveys in this campaign - useful so you
  // can get the ids for data point queries without having to build
  // all the surveyinfo objects
  public List<String> getSurveyIds() {
    List<String> ids = null;
    try {
      ids = Arrays.asList(xmlDocument().selectValues("//surveys/survey/id"));
    } catch (XPathException exception) {
      // FIXME: error handling
    }
    return ids;
  }
  
  // gets list of survey infos matching the ids given in surveyIds
  public List<SurveyInfo> getSurveyInfos(List<String> surveyIds) {
    List<SurveyInfo> retVal = new ArrayList<SurveyInfo>(); 
    // TODO
    return retVal;
  }
  
  private SurveyInfo nodeToSurveyInfo(Node n) {
    // TODO: check for null and log error if xml has unexpected format
    
    // get survey info from xml
    SurveyInfo info = new SurveyInfo();
    info.setSurveyName(n.selectValue("//id"));
    info.setSurveyTitle(n.selectValue("//title"));
    info.setSurveyDescription(n.selectValue("//description"));
    
    // add all prompt nodes, including those nested in repeatable set nodes
    List<Node> promptNodes = n.selectNodes("//prompt"); 
    for (Node pn : promptNodes) {
      info.addPrompt(nodeToPromptInfo(pn));
      // FIXME: check for error
    }
    
    return info;
  }
  
  private PromptInfo nodeToPromptInfo(Node n) {
    // TODO: check for null and log error if xml has unexpected format
    
    // FIXME: are some of these values optional?
    PromptInfo info = new PromptInfo();
    info.setPromptId(n.selectValue("id")); 
    info.setDisplayLabel(n.selectValue("displayLabel"));
    info.setDisplayType(n.selectValue("displayType"));
    info.setPromptType(n.selectValue("promptType"));
    String unit = n.selectValue("unit"); // optional
    info.setUnit((unit != null) ? unit : ""); // set to blank if missing
    return info;
  }
  
}
