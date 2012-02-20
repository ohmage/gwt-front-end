package edu.ucla.cens.mobilize.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;

public interface FrontendResources extends ClientBundle {
  
  public static final FrontendResources INSTANCE = GWT.create(FrontendResources.class);
  
  @Source("sprites/sprite13.png")
  @ImageOptions(repeatStyle=ImageResource.RepeatStyle.Both) /* so gwt doesn't combine it with others */
  public ImageResource sprite13();

  @Source("sprites/sprite16.png")
  @ImageOptions(repeatStyle=ImageResource.RepeatStyle.Both) /* so gwt doesn't combine it with others */
  public ImageResource sprite16();
  
  interface Sprites extends CssResource {
    String small(); // composite image for 13x13 icons
    String med(); // composite image for 16x16 icons
    
    // vertical offsets (px) of icons in "small" sprite13.png 
    int asterisk_orange_small(); 
    int bullet_black(); 
    int bullet_green(); 
    int bullet_red(); 
    int chart_bar_small(); 
    int cog_go_small(); 
    int eye_bw_small(); 
    int group_small(); 
    int lock_small(); 
    int map_small(); 
    int page_white_download_small(); 
    int page_white_edit_small(); 
    int page_white_go_small(); 
    int page_white_magnify_small(); 
    int page_white_upload_small(); 
    int table_small();

    // vertical offsets (px) of icons in "med" sprite16.png
    int add_blue();
    int add_green(); 
    int add_orange(); 
    int chart_bar(); 
    int cog(); 
    int cross(); 
    int delete(); 
    int error(); 
    int exclamation(); 
    int eye(); 
    int folder(); 
    int folder_explore(); 
    int group(); 
    int group_add(); 
    int key(); 
    int lock(); 
    int magnifier(); 
    int page_white_download(); 
    int page_white_edit(); 
    int page_white_go(); 
    int page_white_upload(); 
    int phone(); 
    int star(); 
    int table(); 
    int table_multiple(); 
    int tick(); 
    int user_red();
  }
  
  @Source("sprites/sprites.css")
  public Sprites sprite();

}
