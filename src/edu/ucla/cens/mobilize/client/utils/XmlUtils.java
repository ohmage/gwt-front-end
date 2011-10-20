package edu.ucla.cens.mobilize.client.utils;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.XMLParser;

public class XmlUtils {
  /**
   * @param xmlString
   * @return Xml string formatted nicely (each tag on a new line, indented)
   */
  public static String prettyPrint(String xmlString) {
    // Remove whitespace
    Document xmlDoc = XMLParser.parse(xmlString);
    XMLParser.removeWhitespace(xmlDoc);
    String str = xmlDoc.toString();
    
    // Break xml up into lines with one element per line 
    // (element is one of: open tag, close tag, or value enclosed in both open and close tag)
    str = str.replaceAll("><", ">!~#~!<"); // insert unlikely token between adjacent tags 
    String[] lines = str.split("!~#~!"); // split to get tags on separate lines
    
    // Build string by looping through lines and adding indentation.
    
    StringBuilder sb = new StringBuilder();
    int depth = 0; // greater depth = more indentation
    boolean isClosingTag = false;
    boolean previousLineContainedClosingTag = true;
    for (int i = 0; i < lines.length; i++) {
      
      // skip blank lines
      if (lines[i].isEmpty()) continue;
      
      // Adjust indentation level:
      // - decrease if this line is a closing tag
      // - no change if prev line included a closing tag (means this line is an open tag at same level)
      // - increase for anything else (means this line starts with open tag of a nested node)
      isClosingTag = lines[i].startsWith("</");
      if (isClosingTag) depth--;
      else if (!previousLineContainedClosingTag) depth++;
      
      // indent
      for (int tabCount = 0; tabCount < depth; tabCount++) {
        sb.append("\t");
      }
      
      // write the line
      sb.append(lines[i]);
      sb.append("\n");
      
      // bookkeeping
      previousLineContainedClosingTag = lines[i].contains("</"); // doesn't have to be at the beginning
    }
    
    return sb.toString();
  }
  
}
