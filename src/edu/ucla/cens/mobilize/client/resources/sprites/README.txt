HOW TO USE SPRITES IN A UIBINDER TEMPLATE

Suppose you have an HTMLPanel myElement and you want to display a bullet - one of the images
in the sprite composite - before the element's text.

1. Import the client bundle into the uibinder template using ui:with

<ui:UiBinder xmlns:ui="urn:ui:com.google.gwt.uibinder" xmlns:g="urn:import:com.google.gwt.user.client.ui">
  <ui:with field="res" type="edu.ucla.cens.mobilize.client.resources.FrontendResources" />

2. In the corresponding .java file, inject the resources before the template is instantiated

  public MyConstructor() {
    FrontendResources.INSTANCE.sprite().ensureInjected();
    initWidget(uiBinder.createAndBindUi(this));

3. Set the background image of the element to be the composite sprite image by assigning
the sprite's css class to the element. The sprites are in client/resources/sprites with
names like spriteXX.png. The corresponding class names are defined in sprites.css. To 
find the class name, first find the sprite file that contains your image, then look for
that file name in sprites.css. For instance, 13x13 icons are in sprite13.png, which is 
accessed with class name "small", and 16x16 icons are in sprite16.png with class "med".

  <g:HTMLPanel addStyleNames='{res.sprite.small}'>UiBinder widget example</HTMLPanel>
  
  <div class='{res.sprite.med}'>Pure html example.</div>

4. Pull your image's offset (y-position within the sprite) into the uibinder template and
assign it to a variable with an @eval statement. The image offsets are defined as css
constants in sprite.css. (Look for lines starting with @def.) The convention is to give
the constant the same name as the image's original filename, before it was turned into
a sprite. (Look in 13.zip, 16.zip, etc for the original images.)
NOTE: You must append "px" to the result.

<ui:UiBinder xmlns:ui="urn:ui:com.google.gwt.uibinder" xmlns:g="urn:import:com.google.gwt.user.client.ui">
  <ui:with field="res" type="edu.ucla.cens.mobilize.client.resources.FrontendResources" />
  <g:style>
    @eval BULLET_OFFSET edu.ucla.cens.mobilize.client.resources.FrontendResources.sprite.bullet_black()+"px";
    .myStyle {
      background-position: 2px BULLET_OFFSET;
      padding-left: 20px;
    }
  </g:style>

NOTE: You may find it easier to use the helper class client.ui.SpriteHelper, especially if 
  you want to do any arithmetic on the offset. (see, for instance, lock_small_PLUS_TWO())
  Some forums said you'd be able to do the arithmetic directly in an eval statement but I was
  not able to get it to work - the statement always interpreted the + as string concatentation
  because of the "px" in the statement.
  
@eval SAME_AS_BULLET_OFFSET edu.ucla.cens.mobilize.client.ui.SpriteHelper.bullet_black()+"px";

5. Now you can add another css class to the element and style it as usual, using the variable 
you created with @eval as the background y-position. 

<ui:UiBinder xmlns:ui="urn:ui:com.google.gwt.uibinder" xmlns:g="urn:import:com.google.gwt.user.client.ui">
  <ui:with field="res" type="edu.ucla.cens.mobilize.client.resources.FrontendResources" />
  <g:style>
    @eval BULLET_OFFSET edu.ucla.cens.mobilize.client.resources.FrontendResources.sprite.bullet_black()+"px";
    .myStyle {
      background-position: 2px BULLET_OFFSET;
      padding-left: 20px;
    }
  </g:style>

  <g:HTMLPanel addStyleNames='{res.sprite.small} {style.myStyle}'>UiBinder widget example</HTMLPanel>
  
  <div class='{res.sprite.med} {style.myStyle}'>Pure html example.</div>
  
  
Troubleshooting: 
- If the background doesn't show up, make sure you called ensureInjected
- If the image shows up but is set to the first image, check that you included "px" in the @eval statement


***************************************************************
HOW TO ADD NEW IMAGES TO THE SPRITE
http://spritegen.website-performance.org/

1. Add the new image to the zip file that contains other images you want to combine it with. 
For instance, 13.zip contains 13x13 images and 16.zip contains 16x16.

2. Generate a vertical sprite online: http://spritegen.website-performance.org/
This site also outputs css containing the offset of each item in the image, like:
.sprite-add_green{ background-position: 0 -21px;  } 
.sprite-add_orange{ background-position: 0 -42px;  } 

Note that the original file names were "add_green.png" and "add_orange.png".
TODO: write down the settings used to generate the sprite (e.g., space between images)

3. Convert the css statements to @def statements and save them in sprites.css. 
(TODO: script to automate this step)

BEFORE:
.sprite-add_green{ background-position: 0 -21px;  } 
AFTER:
@def add_green -21px;  

4. Copy the sprite image to resources/sprites/. If you were updating one of the existing
sprites, you should be done. GWT will detect that the resource has changed and give it a
new cacheable name. If you are creating a new sprite, add an ImageResource accessor for the
file in the FrontendResources ClientBundle (look to see what was done for sprite13.png) 
and add a css class name for the sprite in sprites.css (see .small for an example)

5. Check over places the images are used in the app in case the generator has done something different.

NOTE: New sprite image resources should be given repeatStyle=ImageResource.RepeatStyle.Both
to prevent GWT from combining it with other images, and if you do not want the images
to be automatically inlined you need to add a property to the module file:
<set-property name='ClientBundle.enableInlining' value='false' /> 

***************************************************************
NOTES ABOUT IMPLEMENTATION

There were several different ways to use sprites in GWT. 

ClientBundle automatically inlined images as data url resources in some browsers but
not others, and in the browsers that used real sprites, there was no way to get a
single vertical column of images, which made styling things like bulleted lists or
menu items with icons difficult. (You'd need a separate dom element set to the exact
image size to prevent the adjacent images from showing through.)

Sprites could be manually generated, saved in war/images/ and accessed with a global
css file. You'd need to make sure the image had a different name each time you 
re-generated it to make sure the old sprite wasn't cached, and there was no way
to access an image's offset within the sprite programmatically (as with the @eval statement)
so there was less flexibility in styling/positioning.

The current solution is a combination of the two above. The sprite is generated with an
external program so images can be stacked in a single vertical column. The image is stored
in a ClientBundle, which handles versioning, and the offsets are defined as GWT css constants
so they can be accessed in the template code. This strategy seemed more flexible and was
also nice because it lets you do the styling in the individual ui:binder templates.

Another solution might be to use data:urls everywhere, which would embed the images in the
css itself and eliminate the need to load/cache images at all. IIRC this method isn't supported
in all browsers, however.

Useful links:

Simple Sprite example
http://stackoverflow.com/questions/4535094/how-do-i-use-image-sprites-in-gwt

Using ImageResources
http://code.google.com/webtoolkit/doc/latest/DevGuideClientBundle.html#ImageResource

Info about @eval
http://code.google.com/webtoolkit/doc/latest/DevGuideClientBundle.html#Runtime_substitution


***************************************************************