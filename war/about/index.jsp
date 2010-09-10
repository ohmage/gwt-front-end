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
    <link type="text/css" rel="stylesheet" href="/css/styles.css">
	
    <!--                                           -->
    <!-- Any title is fine                         -->
    <!--                                           -->
    <title>AndWellness</title>
    
    <!-- Cool apple icon -->
    <link href="/favicon.ico" rel="shortcut icon" type="image/x-icon">
    
    <!--                                           -->
    <!-- This script loads your compiled module.   -->
    <!-- If you add any GWT meta tags, they must   -->
    <!-- be added before this line.                -->
    <!--                                           -->
    <script type="text/javascript" language="javascript" src="mywebapp/mywebapp.nocache.js"></script>
  </head>

  <body class="page-id-500">

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
	
<jsp:include page="/jsp/header.jsp"/>
		
		<div id="contents">
				<h2>About</h2>
				<p>AndWellness is a fully configurable end-to-end personal data collection system consisting of an Android mobile application
				to collect data, a Tomcat/MySQL backend to store/retreive data, and a collection of various visualizations to browse the data.  
				AndWellness is currenlty under heavy development, and will eventually be released under a fully open source license.</p>
				
				<p>The usage model of AndWellness is simple.  Researchers or administractors can author
				<i>campaigns</i>, which, for lack of a better word, define data collection campaigns.  Campaigns include any number of surveys
				which query the user for responses at scheduled intervals, and additionally collect continuous location and activity inference data.  Surveys are authored
				via a configurable XML schema, and with with data types such as datetime, number, single choice responses, multi choice responses, 
				text, image and audio.  Once a survey is authored, the administrator can upload the survey to the server and attach it to the desired
				campaign.  Finally, the administrator can add users to their campaign.  The users download the free Android application, which will
				auto configure itself upon login and start collecting data.</p>
		
				<p>Once the campaign is running, both the administrators and users can login to view their upload stats and see various
				standard visualizations including a calendar view, timeline view, and map view.  The data access HTTP based API is fully open and can
				be hooked into other custom visualizations or standard data analysis toolkits.</p>
		
				<h2>Methodologies</h2>
				<p>AndWellness strives to implement and maintain the latest Experience Sampling (ES) methodologies.  Previous
				research has clearly shown the need to collect data <i>in situ</i> to avoid recall bias, and researchers have tried everything from
				paper diaries to automated phone systems to get these data.  However, these older methods can only collect basic survey responses
				and fail to obtain clean, unbiased data as participants become less motivated over time.  Recently, newer platforms such as 
				Palm Pilots have been used, but these still suffer from limited types of data input and a lack of realtime data upload.  AndWellness
				uses Android mobile phones to employ methodologies such as: </p>
				
				<ul>
					<li>Minimize user interaction by writing surveys with branching logic to minimize the number of questions
					asked.  Sample various continuous sensors to get information without bothering the user.</li>
					<li>Random sampling of data throughout the day.  The participant must respond within a certain time period and hence
					cannot "backfill" a day or more of data.</li>
					<li>Instant feedback.  View user upload statistics and visualize user data instantly.  Users that are failing
					to upload data can be contacted for followup.</li>
					<li>New types of data collection.  AndWellness allows the collection of location traces, activity traces, and a number 
					of bluetooth devices along with standard survey responses.</li>
				</ul>
				
				<p>These techniques both verify user data and distrupt the user as little as possible.  A less inconvenienced
				user will be more likely to contiue to respond to questions instead of dropping out.</p>
		
				<h2>Studies</h2>
				<p>AndWellness is currently employed in various studies:</p>
				<ul>
					<li>A study by Professor Patti Ganz at UCLA to monitor and investigate young breast cancer survivors.  We are integrating
					mobile phone monitoring into the already planned study to obtain initial feedback from a group of real particiapnts.</li>
					<li>A study funded by the Center for HIV Identification Prevention and Treatment will provide mobile phones 
					to at risk youth to collect highly sensitive information to better explore the tradeoff between privacy needs 
					and study compliance.</li>
					<li>In this NIH funded study, we will provide mobile phones to young moms for 6 months, to help them self-monitor diet, stress, 
					and exercise, three key risk factors for cardiovascular disease. We will use the data from the pilot to evaluate the validity, 
					reliability, and usability of the AndWellness application.</li>
				</ul>
		
			    <h2>Publications/Talks</h2>
			    <p><b>AndWellness: An Open Mobile System for Activity and Experience Sampling</b>, 
			    John Hicks, Nithya Ramanathan, Donnie Kim, Mohamad Monibi, Joshua Selsky, 
			    Mark Hansen, Deborah Estrin, <i>WirelessHealth '10</i>, October 2010 [<a href="http://lecs.cs.ucla.edu/~jhicks/pubs/wireless-health.pdf">PDF</a>]</p>
			
				<h2>Inspirations and Various Related Work</h2>
				<p><b><a href="http://code.google.com/p/opendatakit/">Open Data Kit (ODK)</a>:</b> ODK is an open source data collection system designed to be used by 
				organizations to collect, aggregate, and visualize data.
				ODK leans more towards data collection campaigns involving more tehcnically savvy participants who are hired or volunteer to collect to collect data about
				specific places in an organized manner.</p>

				<p><b><a href="http://myexperience.sourceforge.net/">MyExperience</a>:</b> MyExperience is a similar mobile data collection tool originally developed at
				Intel Research and recently moved to Sourceforge.  MyExperience runs on the windows mobile operating system and uses a system of sensors, triggers,
				and actions to collect data from participants.</p>
			
			    <h2>Contact</h2>
  
		</div>
		
<jsp:include page="/jsp/footer.jsp"/>

	</div>
  </body>
</html>
