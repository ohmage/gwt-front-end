package edu.ucla.cens.mobilize.client.ui;

import edu.ucla.cens.mobilize.client.resources.FrontendResources;

/**
 * Convenience class for use in ui:binder templates. Use the static methods here
 *   to access vertical offsets of icons in sprites. If you need to make an adjustment
 *   to an offset, do it here instead of in the css file so the css file can be 
 *   automatically generated.
 *   
 * To set an image from one of the sprites as the background of an element in the page, 
 *   first set the sprite as the background image of the element, then set the element's
 *   css background-position to the vertical offset of the element you're trying to show.
 *   
 * (A) First set the element's background image to be the sprite that contains the image you want to show.
 * To do so, import FrontendResources into the uibinder file, inject FrontendResources into the 
 * corresponding .java file, and add the appropriate sprite's css class name to the element.
 *
 * In the uibinder (ui.xml) file: 
 *  <ui:with field="res" type="edu.ucla.cens.mobilize.client.resources.FrontendResources" /> 
 *   
 * In the .java file, before initWidget add the line:
 *   FrontendResources.INSTANCE.sprite().ensureInjected();
 *    initWidget(uiBinder.createAndBindUi(this));
 *  
 * Now you can use the sprite css class in either file:
 * 
 * <g:HTMLPanel addStyleNames='{res.sprite.small}' />
 * <span class='{res.sprite.med}' /> 
 * 
 * HTMLPanel html;
 * html.addStyleName(FrontendResources.INSTANCE.sprite().small());
 * 
 * (B) Next, set the element's background position to the offset of the image you want visible.
 * You can access an offset in a ui:binder template using the eval directive:
 * 
 * \@eval MY_OFFSET edu.ucla.cens.mobilize.client.ui.SpriteHelper.asterisk_orange_small()+"px";
 * .myClass{ background-position: 5px MY_OFFSET; }
 * 
 * Troubleshooting:
 * (1) If the background image doesn't appear at all in the page, check to make sure you've 
 *   called FrontendResources.INSTANCE.sprite().ensureInjected() in your .java file before
 *   the template is instantiated.
 * (2) If the background image is set but the background-position is not (so the first image
 *   in the sprite is visible instead of the one you were trying to show) check to make sure
 *   you've included "px" at the end of the eval statement. 
 * 
 * @author shlurbee
 */
public class SpriteHelper {
  public static int asterisk_orange_small() {
    return FrontendResources.INSTANCE.sprite().asterisk_orange_small();
  } 
  public static int bullet_black() {
    return FrontendResources.INSTANCE.sprite().bullet_black();
  } 
  public static int bullet_green() {
    return FrontendResources.INSTANCE.sprite().bullet_green();
  } 
  public static int bullet_red() {
    return FrontendResources.INSTANCE.sprite().bullet_red();
  } 
  public static int chart_bar_small() {
    return FrontendResources.INSTANCE.sprite().chart_bar_small();
  } 
  public static int cog_go_small() {
    return FrontendResources.INSTANCE.sprite().cog_go_small();
  } 
  public static int eye_bw_small() {
    return FrontendResources.INSTANCE.sprite().eye_bw_small();
  } 
  public static int group_small() {
    return FrontendResources.INSTANCE.sprite().group_small();
  } 
  public static int group_small_MINUS_TWO() {
    return group_small() - 2;
  }
  public static int lock_small() {
    return FrontendResources.INSTANCE.sprite().lock_small();
  } 
  public static int lock_small_MINUS_TWO() {
    return lock_small() - 2;
  }
  public static int map_small() {
    return FrontendResources.INSTANCE.sprite().map_small();
  } 
  public static int page_white_download_small() {
    return FrontendResources.INSTANCE.sprite().page_white_download_small();
  } 
  public static int page_white_edit_small() {
    return FrontendResources.INSTANCE.sprite().page_white_edit_small();
  } 
  public static int page_white_go_small() {
    return FrontendResources.INSTANCE.sprite().page_white_go_small();
  } 
  public static int page_white_magnify_small() {
    return FrontendResources.INSTANCE.sprite().page_white_magnify_small();
  } 
  public static int page_white_upload_small() {
    return FrontendResources.INSTANCE.sprite().page_white_upload_small();
  } 
  public static int table_small() {
    return FrontendResources.INSTANCE.sprite().table_small();
  }
  public static int add_blue() {
    return FrontendResources.INSTANCE.sprite().add_blue();
  } 
  public static int add_green() {
    return FrontendResources.INSTANCE.sprite().add_green();
  } 
  public static int add_orange() {
    return FrontendResources.INSTANCE.sprite().add_orange();
  } 
  public static int chart_bar() {
    return FrontendResources.INSTANCE.sprite().chart_bar();
  } 
  public static int cog() {
    return FrontendResources.INSTANCE.sprite().cog();
  } 
  public static int cross() {
    return FrontendResources.INSTANCE.sprite().cross();
  } 
  public static int delete() {
    return FrontendResources.INSTANCE.sprite().delete();
  } 
  public static int error() {
    return FrontendResources.INSTANCE.sprite().error();
  } 
  public static int exclamation() {
    return FrontendResources.INSTANCE.sprite().exclamation();
  } 
  public static int eye() {
    return FrontendResources.INSTANCE.sprite().eye();
  } 
  public static int folder() {
    return FrontendResources.INSTANCE.sprite().folder();
  } 
  public static int folder_explore() {
    return FrontendResources.INSTANCE.sprite().folder_explore();
  } 
  public static int group() {
    return FrontendResources.INSTANCE.sprite().group();
  } 
  public static int group_PLUS_TWO() {
    return group() + 2;
  }
  public static int group_add() {
    return FrontendResources.INSTANCE.sprite().group_add();
  } 
  public static int key() {
    return FrontendResources.INSTANCE.sprite().key();
  } 
  public static int lock() {
    return FrontendResources.INSTANCE.sprite().lock();
  } 
  public static int lock_PLUS_TWO() {
    return lock() + 2;
  }
  public static int magnifier() {
    return FrontendResources.INSTANCE.sprite().magnifier();
  } 
  public static int page_white_download() {
    return FrontendResources.INSTANCE.sprite().page_white_download();
  }
  public static int page_white_download_PLUS_TWO() {
    return page_white_download() + 2;
  }
  public static int page_white_edit() {
    return FrontendResources.INSTANCE.sprite().page_white_edit();
  } 
  public static int page_white_go() {
    return FrontendResources.INSTANCE.sprite().page_white_go();
  } 
  public static int page_white_upload() {
    return FrontendResources.INSTANCE.sprite().page_white_upload();
  } 
  public static int phone() {
    return FrontendResources.INSTANCE.sprite().phone();
  } 
  public static int star() {
    return FrontendResources.INSTANCE.sprite().star();
  } 
  public static int table() {
    return FrontendResources.INSTANCE.sprite().table();
  } 
  public static int table_multiple() {
    return FrontendResources.INSTANCE.sprite().table_multiple();
  } 
  public static int tick() {
    return FrontendResources.INSTANCE.sprite().tick();
  } 
  public static int user_red() {
    return FrontendResources.INSTANCE.sprite().user_red();
  }

}
