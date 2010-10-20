package edu.ucla.cens.AndWellnessVisualizations.client.testing;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.google.code.p.gwtchismes.client.GWTCSimpleDatePicker;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

import edu.ucla.cens.AndWellnessVisualizations.client.CalendarAppController;
import edu.ucla.cens.AndWellnessVisualizations.client.ClientInfo;
import edu.ucla.cens.AndWellnessVisualizations.client.common.AuthTokenLoginManager;
import edu.ucla.cens.AndWellnessVisualizations.client.event.DataPointLabelSelectionEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.event.NewDataPointAwDataEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.model.AuthorizationTokenQueryAwData;
import edu.ucla.cens.AndWellnessVisualizations.client.model.ConfigAwData;
import edu.ucla.cens.AndWellnessVisualizations.client.model.DataPointAwData;
import edu.ucla.cens.AndWellnessVisualizations.client.model.DataPointQueryAwData;
import edu.ucla.cens.AndWellnessVisualizations.client.model.UserInfo;
import edu.ucla.cens.AndWellnessVisualizations.client.presenter.CalendarVisualizationPresenter;
import edu.ucla.cens.AndWellnessVisualizations.client.presenter.MonthSelectionPresenter;
import edu.ucla.cens.AndWellnessVisualizations.client.rpcservice.LocalAndWellnessRpcService;
import edu.ucla.cens.AndWellnessVisualizations.client.view.CalendarVisualizationView;
import edu.ucla.cens.AndWellnessVisualizations.client.view.CalendarVisualizationViewImpl;
import edu.ucla.cens.AndWellnessVisualizations.client.view.MonthSelectionView;
import edu.ucla.cens.AndWellnessVisualizations.client.view.MonthSelectionViewImpl;

/**
 * Test of the CalendarVisualization.  For now only initializes the View and attaches
 * it to the test_calendar_visualization div.
 * 
 * @author jhicks
 *
 */
public class CalendarVisualizationTest implements EntryPoint {
    private Date currentMonth = new Date();
    private String currentDataLabel = "alcoholNumberOfDrinks";
    
    public LocalAndWellnessRpcService rpcService;
    public HandlerManager eventBus;
    public AuthTokenLoginManager loginManager;
    
    private static Logger _logger = Logger.getLogger(CalendarVisualizationTest.class.getName());
    
    /**
     * Creates the ValendarVisualizationView on the RootPanel
     */
    public void onModuleLoad() {
        // Initialize the rpc service and event bus for the app
        rpcService = new LocalAndWellnessRpcService();
        eventBus = new HandlerManager(null);
        loginManager = new AuthTokenLoginManager(eventBus);
        
        // First login to get the rpcService ready
        doLogin();
    }
    
    private void initAppController() {
        CalendarAppController calAppController = new CalendarAppController(rpcService, eventBus, loginManager);
        calAppController.go();
    }

    
    private void setDataPointLabel() {
        _logger.info("New data point label selection: " + currentDataLabel);
        
        eventBus.fireEvent(new DataPointLabelSelectionEvent(currentDataLabel));
    }
    
    private void doLogin() {
        _logger.info("Attempting to login...");
        
        // First login
        rpcService.fetchAuthorizationToken("abc", "123", new AsyncCallback<UserInfo>() {

            @Override
            public void onFailure(Throwable caught) {
                _logger.warning("Authorization failed with reason " + caught.getMessage());
            }

            @Override
            public void onSuccess(UserInfo result) {
                _logger.info("Successfully logged in");
                
                // Init the app controller
                initAppController();
                
                setDataPointLabel();
            }
            
        });
        

    }
    
    private void fetchData() {
        // Data for the rpc request
        Date startDate, endDate;
        String userName, campaignId, clientName, authToken;
        List<String> dataPointLabels = new ArrayList<String>();
        
        // Find the first and last day of the requested month
        startDate = GWTCSimpleDatePicker.getFirstDayOfMonth(currentMonth);
        endDate = GWTCSimpleDatePicker.getLastDayOfMonth(currentMonth);
        userName = "testUser";
        campaignId = "testCampaign";
        clientName = "testGWTClient";
        authToken = "1234567890";
        dataPointLabels.add(currentDataLabel);
        
        // Make sure we are logged in
        if (! loginManager.isCurrentlyLoggedIn()) {
            _logger.warning("Cannot fetch data without first logging in!");
            return;
        }
        
        _logger.info("Sending out a request for data label " + currentDataLabel);
        
        // Send our request to the rpcService and handle the result
        rpcService.fetchDataPoints(startDate, endDate, userName, dataPointLabels, campaignId, clientName, authToken,
                new AsyncCallback<List<DataPointAwData>>() {

            public void onSuccess(List<DataPointAwData> data) {
                _logger.info("Received " + data.size() + " data points from the server");
                
                eventBus.fireEvent(new NewDataPointAwDataEvent(data));
            }
            
            public void onFailure(Throwable error) {
                _logger.warning("fetchDataPoints called onFailure with reason " + error.getMessage());
            }
        });
    }
    
    private void testXMLParse() {   
        _logger.info("Sending out a request for the campaign configuration information.");
        
        rpcService.fetchConfigData(new AsyncCallback<ConfigAwData>() {
            public void onFailure(Throwable caught) {
                _logger.warning("testXMLParse called onFailure with reason " + caught.getMessage());
            }

            public void onSuccess(ConfigAwData result) {
                _logger.info("Received configuration information from the server.");
                
                parseXml(result.getConfigurationXML());                
            }
        });
    }
    
    private void parseXml(String xml) {
        Document xmlDocument = XMLParser.parse(xml);
        NodeList nodes = xmlDocument.getElementsByTagName("prompt");
        
        _logger.finer("Found " + nodes.getLength() + " prompt nodes");
        
        for (int i = 0; i < nodes.getLength(); ++i) {
            NodeList childNodes = nodes.item(i).getChildNodes();
            
            for (int j = 0; j < childNodes.getLength(); ++j) {
                Node childNode = childNodes.item(j);
                
                _logger.finer(childNode.getNodeName() + ": " + childNode.getChildNodes().item(0).getNodeValue());
               
            }
        }
        
        Node campaignNameNode = xmlDocument.getElementsByTagName("campaignName").item(0);
        
        _logger.finer("Found campaignName: " + campaignNameNode.getChildNodes().item(0).getNodeValue());
    }
}
