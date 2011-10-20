package edu.ucla.cens.mobilize.client.dataaccess.awdataobjects;

import com.google.gwt.core.client.JavaScriptObject;

public class PromptResponseAwData extends JavaScriptObject {
  protected PromptResponseAwData() {};

  public final native String getDisplayType() /*-{ return this.prompt_display_type; }-*/;
  public final native String getPromptText() /*-{ return this.prompt_text; }-*/;
  
  // response can be text, number, or uuid of an image. use prompt type to determine which
  public final native String getPromptResponse() /*-{ 
    return this.prompt_response + ""; // make sure it's a string 
  }-*/;
  public final native String getPromptType() /*-{ return this.prompt_type; }-*/;
  
  // When user responds to a survey by picking an item from a list, only the 
  // item key is recorded. This method translates the key back into a 
  // user-friendly label for display.
  // Only makes sense for choice prompt_types (e.g., single_choice, multi_choice)
  public final native String getChoiceLabelFromGlossary(String key) /*-{
    if (key == "NOT_DISPLAYED" || key == "SKIPPED") return key;
    // glossary looks like:"prompt_choice_glossary":{"3":{"label":"Restaurant"},"2":{"label":"Work"},"1":{"label":"School"},"0":{"label":"Home"},"7":{"label":"Other"},"6":{"label":"Party"},"5":{"label":"Vehicle"},"4":{"label":"Friends' houses"}}
    var retval = "---";
    if (this.prompt_choice_glossary && this.prompt_choice_glossary[key]) {
      retval = this.prompt_choice_glossary[key].label;
    }
    return retval;
  }-*/;
  
  // index of prompt in xml. allows front end to sort and display prompts in the
  // same order in which they appeared in the xml
  public final native int getIndex() /*-{
    return this.prompt_index;
  }-*/;
}
