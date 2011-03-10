<!doctype html>
<!-- The DOCTYPE declaration above will set the    -->
<!-- browser's rendering engine into               -->
<!-- "Standards Mode". Replacing this declaration  -->
<!-- with a "Quirks Mode" doctype may lead to some -->
<!-- differences in layout.                        -->

<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">

    <!--                                                               -->
    <!-- Consider inlining CSS to reduce the number of requested files -->
    <!--                                                               -->
    <link type="text/css" rel="stylesheet" href="../css/styles.css">
    	
    <!--                                           -->
    <!-- Any title is fine                         -->
    <!--                                           -->
    <title>AndWellness</title>
    
    <!-- Cool apple icon -->
    <link href="../favicon.ico" rel="shortcut icon" type="image/x-icon">
    
    <!--                                           -->
    <!-- This script loads your compiled module.   -->
    <!-- If you add any GWT meta tags, they must   -->
    <!-- be added before this line.                -->
    <!--                                           -->
    <script type="text/javascript" language="javascript" src="../mobilitymapandwellness/mobilitymapandwellness.nocache.js"></script>
  </head>

  <body class="calendarView">

    <!-- OPTIONAL: include this if you want history support -->
    <iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1' style="position:absolute;width:0;height:0;border:0"></iframe>
    
    <!-- RECOMMENDED if your web app will not function without JavaScript enabled -->
    <noscript>
      <div style="width: 22em; position: absolute; left: 50%; margin-left: -11em; color: red; background-color: white; border: 1px solid red; padding: 4px; font-family: sans-serif">
        Your web browser must have JavaScript enabled
        in order for this application to display correctly.
      </div>
    </noscript>

	<!-- Setup the basic website layout...header, contents, and footer -->
	<!-- The header and footer are included jsp style -->

	<div id="main_wrapper">
	
<jsp:include page="../jsp/header.jsp"/>
		
		<div id="contents">
				<!-- Month selector div -->
				<div id="monthSelectionView" class="border-color-standard"></div>
		
				<!-- Main calendar data view -->
				<div id="mapVisualizationView" class="float-left"></div>
				
				<div id ="calendarDayDetailView"></div>
				
				<div id="visualizationSelectionView" class="float-right border-color-standard"></div>
				
				<div id="dataPointBrowserView" class="float-right border-color-standard"></div>
				
				<div id="dateSelectionView" class="float-right border-color-standard"></div>
				
				<div id="dataCategorySelectionView" class="float-right border-color-standard"></div>
				
				<div class="clear-line"></div>
		</div>
		
<jsp:include page="../jsp/footer.jsp"/>

	</div>
  </body>
</html>
