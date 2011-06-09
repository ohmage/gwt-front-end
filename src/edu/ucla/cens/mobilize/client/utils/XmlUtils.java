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
    // (element is one of: open tag, close tag, or text value)
    str = str.replaceAll("\n", ""); 
    str = str.replaceAll("</", "\n</"); 
    str = str.replaceAll(">", ">\n");
    String[] lines = str.split("\n");
    
    // Build string by looping through lines and adding indentation.
    
    StringBuilder sb = new StringBuilder();
    int depth = 0; // greater depth = more indentation
    boolean isClosingTag = false;
    boolean previousLineWasClosingTag = true;
    for (int i = 0; i < lines.length; i++) {
      
      // skip blank lines
      if (lines[i].isEmpty()) continue;
      
      // Adjust indentation level:
      // - decrease if this line is a closing tag
      // - no change if prev line was closing tag (this line is open tag at same level)
      // - increase for anything else (this line is open tag of nested node or a node value)
      isClosingTag = lines[i].startsWith("</");
      if (isClosingTag) depth--;
      else if (!previousLineWasClosingTag) depth++;
      
      // indent
      for (int tabCount = 0; tabCount < depth; tabCount++) {
        sb.append("\t");
      }
      
      // write the line
      sb.append(lines[i]);
      sb.append("\n");
      
      // bookkeeping
      previousLineWasClosingTag = isClosingTag;
    }
    
    return sb.toString();
  }
  
}
