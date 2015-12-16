# [DEPRECATED] ohmage 2.x gwt front end
As of ohmage 2.17, this tool is now considered legacy.  It is suggested instead that you instead use the [navbar](https://github.com/mobilizingcs/navbar)-based setup and include micro-frontends (most are available at github.com/mobilizingcs) as you see fit.


This is the ohmage browser front end.  For the associated server application, check out the ohmage server repo [here](https://github.com/cens/ohmageServer).

The majority of this code is written in Java and compiled to JavaScript using the included GWT libraries.
To compile the code, make sure java and javac are installed and run `ant clean build buildwar`. 
This creates a Java EE war file which can then be placed into a Servlet container. The app has been tested in Tomcat and developed
using Eclipse and the associated Eclipse GWT tools.

The purpose of this app is to allow study participants and researchers to view and export data collected from the 
[ohmage Android](https://github.com/ohmage/ohmageAndroidLib) or [MWF](http://mwf.ucla.edu/) applications. The app also serves
as a place to manage research studies and participant groups.

ohmage has been mostly highly used in research studies related to health, but it is also used as a tool to teach students computer science
in a project called Mobilize. More details about how this front end application works can be found on the Mobilize [wiki](http://wiki.mobilizingcs.org/app/web).  
