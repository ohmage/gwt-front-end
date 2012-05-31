Each module in this directory has two versions: regular and debug. For instance:
MobilizeWeb.gwt.xml
MobilizeWebDebug.gwt.xml

The first module uses release settings. The ant build script build.xml
uses this module to generate the war that is deployed to real servers.

The second module (*Debug) is for local development in Eclipse. Set your
Eclipse environment to use this file in your Run Configuration as follows:

INSTRUCTIONS FOR SETTING ECLIPSE TO USE DEBUG MODULE
- Click on the arrow next to the Run icon in the Eclipse toolbar
- Select Run Configurations
- Under the GWT Application heading, select your run config (or create a new one)
- Set the module to the debug module file (e.g., edu.ucla.cens.mobilize.MobilizeWebDebug)

DIFFERENCES BETWEEN THE MODULES:
- Logging is enabled in *Debug and not release
- *Debug keeps long css names to make troubleshooting in firebug/web inspector easier
- In the *Debug module, the deployStatus variable is set to DEBUG instead of RELEASE.
    The class edu.ucla.cens.mobilize.client.DeployStatus uses deployStatus
    when deciding which server to query for fetching data. This setup is useful,
    for instance, for running the gwt app in Eclipse and having it query a local
    version of the server running in tomcat. (Even though Eclipse and tomcat both
    use localhost, Eclipse serves the app from its own dev server, which uses a 
    different port than tomcat.)
     
    
NOTE: Because of the port difference mentioned above, when you run the app in Eclipse
and query a local version of the AndWellness server, you must make sure the server
has been compiled with the option to allow requests form any origin.
