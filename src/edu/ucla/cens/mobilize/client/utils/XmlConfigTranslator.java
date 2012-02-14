package edu.ucla.cens.mobilize.client.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import edu.ucla.cens.mobilize.client.common.PromptType;
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
 */

public class XmlConfigTranslator {
  private static Logger _logger = Logger.getLogger(XmlConfigTranslator.class.getName());
  private Document xmlDocument;
  
  // http://code.google.com/p/totoe/wiki/XmlUsage
  // https://github.com/cens/AndWellnessConfiguration/blob/master/spec/configuration.xsd
  
  // returns true if document was loaded successfully
  public boolean loadFromXml(String xmlString) {
    boolean success = false;
    try {
      // string is used to build dom and then discarded
      this.xmlDocument = new XmlParser().parse(xmlString);
      success = true;
    } catch (XmlParseException xmlException) {
      _logger.severe("Document could not be loaded. XmlParseException: " + 
      		xmlException.getMessage());
    } catch (Exception exception) {
      _logger.severe("Document could not be loaded. " + exception.getMessage());
    }
    return success;
  }
  
  public String getCampaignName() {
    assert xmlDocument != null : "xmlDocument not loaded";
    return xmlDocument.selectValue("//campaignName");
  }
  
  /**
   * @param surveyId
   * @return SurveyInfo object that matches surveyId or null if not found
   */
  public SurveyInfo getSurveyInfo(String surveyId) {
    assert xmlDocument != null : "xmlDocument not loaded";
    return nodeToSurveyInfo(xmlDocument.selectNode("//survey[id=\"" + surveyId + "\"]"));
  }
  
  /**
   * @return List of all survey infos in this campaign
   */
  public List<SurveyInfo> getSurveyInfos() {
    assert xmlDocument != null : "xmlDocument not loaded";
    List<SurveyInfo> retVal = new ArrayList<SurveyInfo>();
    List<Node> surveyNodes = xmlDocument.selectNodes("//survey");
    // https://github.com/cens/AndWellnessConfiguration/blob/master/spec/configuration.xsd
    for (Node n : surveyNodes) {
      retVal.add(nodeToSurveyInfo(n));
    }
    return retVal;
  }

  /**
   * Gets a list of ids of all surveys in the campaign. Useful for getting ids to
   *   use in an api query without having to build all the SurveyInfo objs 
   * @return List of id strings
   */
  public List<String> getSurveyIds() {
    assert xmlDocument != null : "xmlDocument not loaded";
    List<String> ids = null;
    try {
      ids = Arrays.asList(xmlDocument.selectValues("//surveys/survey/id"));
    } catch (XPathException exception) {
      _logger.severe("Failed to get survey ids from xml: " + exception.getMessage());
    }
    return ids;
  }

  
  public List<PromptInfo> getPromptInfos() {
    assert xmlDocument != null : "xmlDocument not loaded";
    List<PromptInfo> retval = new ArrayList<PromptInfo>();
    List<Node> promptNodes = xmlDocument.selectNodes("//prompt");
    for (Node n : promptNodes) {
      retval.add(nodeToPromptInfo(n));
    }
    return retval;
  }
  
  /**
   * Gets a list of all prompt ids used in this campaign. Assumes prompt ids
   *   are unique across all surveys in the campaign
   * @return List promptId strings or null if xpath is invalid
   */
  public List<String> getPromptIds() {
    assert xmlDocument != null : "xmlDocument not loaded";
    return Arrays.asList(xmlDocument.selectValues("//prompt/id"));
  }

  /**
   * @param promptType e.g., "photo", "number", etc
   * @see http://www.lecs.cs.ucla.edu/wikis/andwellness/index.php/AndWellness-Prompt-Authoring-Notes#Prompt_Datatypes
   * @return List of ids of surveys that have at least one prompt of type promptType
   */
  public List<String> getSurveyIdsByPromptType(String promptType) {
    assert xmlDocument != null : "xmlDocument not loaded";
    return Arrays.asList(xmlDocument.selectValues("//survey[descendant::promptType=\"" + promptType + "\"]/id"));
  }
  
  
  /**
   * @return SurveyInfo object built from the survey node n or null if unsuccessful
   */
  private SurveyInfo nodeToSurveyInfo(Node n) {
    if (n == null) return null;
    
    // get survey info from xml
    SurveyInfo info = new SurveyInfo();
    info.setSurveyName(n.selectValue("//id"));
    info.setSurveyTitle(n.selectValue("//title"));
    info.setSurveyDescription(n.selectValue("//description"));
    
    // add all prompt nodes, including those nested in repeatable set nodes
    List<Node> promptNodes = n.selectNodes("//prompt"); 
    for (Node pn : promptNodes) {
      PromptInfo promptInfo = nodeToPromptInfo(pn);
      if (promptInfo != null) {
        info.addPrompt(promptInfo);
      }
    }
    
    return info;
  }
  
  private PromptInfo nodeToPromptInfo(Node n) {
    if (n == null) return null;
    PromptInfo info = new PromptInfo();
    info.setPromptId(n.selectValue("id")); 
    info.setDisplayLabel(n.selectValue("displayLabel"));
    info.setDisplayType(n.selectValue("displayType"));
    info.setPromptType(PromptType.fromString(n.selectValue("promptType")));
    String unit = n.selectValue("unit"); // optional
    info.setUnit((unit != null) ? unit : ""); // set to blank if missing
    return info;
  }
  
}
