package com.gslab.solar.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.Body;
import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.DatapointsIngestion;
import com.ge.predix.solsvc.bootstrap.ams.common.AssetConfig;
import com.ge.predix.solsvc.ext.util.JsonMapper;
import com.ge.predix.solsvc.restclient.impl.RestClient;
import com.ge.predix.solsvc.timeseries.bootstrap.client.TimeseriesClient;
import com.ge.predix.solsvc.timeseries.bootstrap.config.ITimeseriesConfig;
import com.gslab.iot.solar.domain.dto.SolarPanel;
import com.gslab.solar.domain.Panel;
import com.gslab.solar.domain.SensorType;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

/**
 * @author Rohit Rajbanshi
 */

@Service
public class DataIngestionService {

  private static final Logger LOG = LoggerFactory.getLogger(DataIngestionService.class);

  // @Autowired
  // private RestClient restClient;

  @Autowired
  private TimeseriesClient timeseriesClient;

  @Autowired
  @Qualifier("defaultTimeseriesConfig")
  private ITimeseriesConfig timeseriesConfig;

  @Autowired
  private SimpMessagingTemplate template;

  @Autowired
  private RestClient restClient;

  @Autowired
  private AssetConfig assetConfig;

  @Autowired
  private JsonMapper jsonMapper;

  ObjectMapper mapper = new ObjectMapper();

  private static Map<String, Panel> panels = new HashMap<>();
  private static List<Panel> pList = new ArrayList<>();

  @PostConstruct
  public void init() {
    try {
      getAllPanels();
      this.timeseriesClient.createConnectionToTimeseriesWebsocket();
      LOG.info("Websocket connection to Timeseries database established");
    } catch (Exception e) {
      throw new RuntimeException("unable to set up timeseries Websocket Pool timeseriesConfig=" + this.timeseriesConfig, e);
    }
  }

  private void getAllPanels() throws IOException {
    String url = "https://predix-asset.run.aws-usw02-pr.ice.predix.io/panels";
    List<Header> headers = new ArrayList<>(restClient.getSecureTokenForClientId());
    restClient.addZoneToHeaders(headers, assetConfig.getZoneId());
    headers.add(new BasicHeader("Content-Type", "application/json"));
    LOG.info("asset resthost url :{}", url);
    for (int i = 0; i < headers.size(); i++)
      LOG.info("headers :{}", headers.get(i));
    CloseableHttpResponse response = restClient.get(url, headers);
    String json = EntityUtils.toString(response.getEntity());
    pList = jsonMapper.fromJsonArray(json, Panel.class);
    for (Panel p : pList) {
      Set<SensorType> sensors = p.getAssetTag().keySet();
      System.out.println(sensors);
      panels.put(p.getId(), p);
    }
  }

  /**
   * Get headers for timeseries
   *
   * @return
   */
    /*
     * private List<Header> generateHeaders() { List<Header> headers =
	 * this.restClient.getSecureTokenForClientId();
	 * this.restClient.addZoneToHeaders(headers,
	 * this.timeseriesConfig.getZoneId()); return headers; }
	 */
  public void ingest(SolarPanel solarPanel) {
    ingestData(solarPanel);
  }

  public void ingest(String str) {
    ingestData(str);
  }

  // posting by parsing raw string

  private void ingestData(String str) {
    StringDataParser parser = new StringDataParser(str);
    parser.parseData(panels);
    List<String> panelIds = new ArrayList<String>(parser.getPanelData().keySet());
    System.out.println(panelIds);
    System.out.println(parser.getPanelData());
    Map<String, Map> m = parser.getPanelData();
    System.err.println(m);

    DatapointsIngestion data = new DatapointsIngestion();
    Date timestamp = new Date();

    for (int i = 0; i < panelIds.size(); i++) {

      data.setMessageId(panelIds.get(i) + "-" + timestamp.getTime());
      List<Body> bodies = new ArrayList<Body>();


      Set<String> sourceTagIds = m.get(panelIds.get(i)).keySet();

      Iterator<String> it = sourceTagIds.iterator();
      for(String sourceTagId : sourceTagIds) {
        Body body = new Body();
        body.setName(it.next());
        // System.err.println(panelIds.get(i) + "-" + sensors[j]);
        List<Object> datapoint = new ArrayList<>();
        datapoint.add(timestamp.getTime());
        datapoint.add((Double) m.get(panelIds.get(i)).get(sourceTagId));
//        LOG.info(m.get(panelIds.get(i)).get(it));
        datapoint.add(3);
        List<Object> datapoints = new ArrayList<>();
        datapoints.add(datapoint);
        body.setDatapoints(datapoints);
        LOG.info("Name: {} ",body.getName());
        bodies.add(body);
      }


      Body body = new Body();
      String assetId = panelIds.get(i).split(":")[0];
      body.setName(assetId + ":"+SensorType.POWER.value());
      // System.err.println(panelIds.get(i) + "-" + sensors[j]);
      List<Object> datapoint = new ArrayList<Object>();
      datapoint.add(timestamp.getTime());
      Double powerValue = (Double) m.get(panelIds.get(i)).get(panels.get(panelIds.get(i).split(":")[0].split("-")[2]).getAssetTag().get(SensorType.VOLTAGE_OUT).getSourceTagId()) *
                          (Double) m.get(panelIds.get(i)).get(panels.get(panelIds.get(i).split(":")[0].split("-")[2]).getAssetTag().get(SensorType.CURRENT).getSourceTagId());
      datapoint.add(powerValue);
      LOG.info(powerValue.toString());
      datapoint.add(3);
      List<Object> datapoints = new ArrayList<Object>();
      datapoints.add(datapoint);
      body.setDatapoints(datapoints);
      bodies.add(body);

      data.setBody(bodies);
      LOG.info(data.getBody().get(1).getName() + "  " + data.getBody().get(1).getDatapoints().get(0));
//			SolarPanel solarpanel = new SolarPanel();
//			solarpanel.setPanelId("10"+(i+4));
//			solarpanel.setCurrent((Double)m.get(panelIds.get(i)).get("current"));
//			solarpanel.setVoltageOut((Double)m.get(panelIds.get(i)).get("voltage-out"));
//			solarpanel.setVoltageIn((Double)m.get(panelIds.get(i)).get("voltage-in"));
//			solarpanel.setTemperature((Double)m.get(panelIds.get(i)).get("temperature"));
//			solarpanel.setTimestamp(new Date().getTime());
//			template.convertAndSend("/panel/gslab-solarpanel-10"+(i+4),solarpanel);
//			String string;
//			try {
//				string = mapper.writeValueAsString(solarpanel);
//				System.out.println(string);
//			} catch (JsonProcessingException e) {
//				e.printStackTrace();
//			}

      timeseriesClient.postDataToTimeseriesWebsocket(data);
    }
  }

  // posting by using json body

  private void ingestData(SolarPanel solarPanel) {
    DatapointsIngestion data = new DatapointsIngestion();
    Date timestamp = new Date();
    data.setMessageId(solarPanel.getPanelId() + "-" + timestamp.getTime());
    List<Body> bodies = new ArrayList<>();

    Body body1 = new Body();
    body1.setName("solar-panel-" + solarPanel.getPanelId() + ":voltage-in");
    // com.ge.predix.entity.util.map.Map map = new
    // com.ge.predix.entity.util.map.Map();
    // map.put("assetId",
    // "gslab-solarpanel-"+solarPanel.getPanelId()+"-vlotageIn");
    // body1.setAttributes(map);
    List<Object> datapoint1 = new ArrayList<Object>();
    datapoint1.add(timestamp.getTime());
    datapoint1.add(solarPanel.getVoltageIn());
    datapoint1.add(3);
    List<Object> datapoints1 = new ArrayList<Object>();
    datapoints1.add(datapoint1);
    body1.setDatapoints(datapoints1);

    Body body2 = new Body();
    body2.setName("solar-panel-" + solarPanel.getPanelId() + ":voltage-out");
    // com.ge.predix.entity.util.map.Map map = new
    // com.ge.predix.entity.util.map.Map();
    // map.put("assetId",
    // "gslab-solarpanel-"+solarPanel.getPanelId()+"-vlotageIn");
    // body1.setAttributes(map);
    List<Object> datapoint2 = new ArrayList<Object>();
    datapoint2.add(timestamp.getTime());
    datapoint2.add(solarPanel.getVoltageOut());
    datapoint2.add(3);
    List<Object> datapoints2 = new ArrayList<Object>();
    datapoints2.add(datapoint2);
    body2.setDatapoints(datapoints2);

    Body body3 = new Body();
    body3.setName("solar-panel-" + solarPanel.getPanelId() + ":current");
    // com.ge.predix.entity.util.map.Map map = new
    // com.ge.predix.entity.util.map.Map();
    // map.put("assetId",
    // "gslab-solarpanel-"+solarPanel.getPanelId()+"-vlotageIn");
    // body1.setAttributes(map);
    List<Object> datapoint3 = new ArrayList<Object>();
    datapoint3.add(timestamp.getTime());
    datapoint3.add(solarPanel.getCurrent());
    datapoint3.add(3);
    List<Object> datapoints3 = new ArrayList<Object>();
    datapoints3.add(datapoint3);
    body3.setDatapoints(datapoints3);

    Body body4 = new Body();
    body4.setName("solar-panel-" + solarPanel.getPanelId() + ":temperature");
    // com.ge.predix.entity.util.map.Map map = new
    // com.ge.predix.entity.util.map.Map();
    // map.put("assetId",
    // "gslab-solarpanel-"+solarPanel.getPanelId()+"-vlotageIn");
    // body1.setAttributes(map);
    List<Object> datapoint4 = new ArrayList<Object>();
    datapoint4.add(timestamp.getTime());
    datapoint4.add(solarPanel.getTemperature());
    datapoint4.add(3);
    List<Object> datapoints4 = new ArrayList<Object>();
    datapoints4.add(datapoint4);
    body4.setDatapoints(datapoints4);

    Body body5 = new Body();
    body5.setName("solar-panel-" + solarPanel.getPanelId() + ":power");
    // com.ge.predix.entity.util.map.Map map = new
    // com.ge.predix.entity.util.map.Map();
    // map.put("assetId",
    // "gslab-solarpanel-"+solarPanel.getPanelId()+"-vlotageIn");
    // body1.setAttributes(map);
    List<Object> datapoint5 = new ArrayList<Object>();
    datapoint5.add(timestamp.getTime());
    datapoint5.add(solarPanel.getPower());
    datapoint5.add(3);
    List<Object> datapoints5 = new ArrayList<Object>();
    datapoints5.add(datapoint5);
    body5.setDatapoints(datapoints5);

    bodies.add(body1);
    bodies.add(body2);
    bodies.add(body3);
    bodies.add(body4);
    bodies.add(body5);
    data.setBody(bodies);

    String string;
    try {
      string = mapper.writeValueAsString(solarPanel);
      System.out.println(string);
    } catch (JsonProcessingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
//		template.convertAndSend("/panel/gslab-solarpanel-"+solarPanel.getPanelId(),solarPanel);

    timeseriesClient.postDataToTimeseriesWebsocket(data);

  }
}
