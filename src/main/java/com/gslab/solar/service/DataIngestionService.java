package com.gslab.solar.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.Body;
import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.DatapointsIngestion;
import com.ge.predix.solsvc.timeseries.bootstrap.client.TimeseriesClient;
import com.ge.predix.solsvc.timeseries.bootstrap.config.ITimeseriesConfig;
import com.gslab.solar.domain.SolarPanel;

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

	@PostConstruct
	public void init() {
		try {
			this.timeseriesClient.createConnectionToTimeseriesWebsocket();
			LOG.info("Websocket connection to Timeseries database established");
		} catch (Exception e) {
			throw new RuntimeException("unable to set up timeseries Websocket Pool timeseriesConfig=" + this.timeseriesConfig, e);
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
		parser.parseData();
		List<String> panelIds = new ArrayList<String>(parser.getPanelData().keySet());
		System.out.println(panelIds);
		System.out.println(parser.getPanelData());
		Map<String, Map> m = parser.getPanelData();
		String[] sensors = { "current", "voltageOut", "temperature", "voltageIn" };

		DatapointsIngestion data = new DatapointsIngestion();
		Date timestamp = new Date();

		for (int i = 0; i < panelIds.size(); i++) {

			data.setMessageId(panelIds.get(i) + "-" + timestamp.getTime());
			List<Body> bodies = new ArrayList<Body>();

			for (int j = 0; j < 4; j++) {
				Body body = new Body();
				body.setName(panelIds.get(i) + "-" + sensors[j]);
				// System.err.println(panelIds.get(i) + "-" + sensors[j]);
				List<Object> datapoint = new ArrayList<Object>();
				datapoint.add(timestamp.getTime());
				datapoint.add((Double) m.get(panelIds.get(i)).get(sensors[j]));
				System.err.println(m.get(panelIds.get(i)).get(sensors[j]));
				datapoint.add(3);
				List<Object> datapoints = new ArrayList<Object>();
				datapoints.add(datapoint);
				body.setDatapoints(datapoints);
				bodies.add(body);
			}

			Body body = new Body();
			body.setName(panelIds.get(i) + "-power");
			// System.err.println(panelIds.get(i) + "-" + sensors[j]);
			List<Object> datapoint = new ArrayList<Object>();
			datapoint.add(timestamp.getTime());
			Double powerValue = (Double) m.get(panelIds.get(i)).get(sensors[0]) * (Double) m.get(panelIds.get(i)).get(sensors[1]);
			datapoint.add(powerValue);
			System.err.println(powerValue);
			datapoint.add(3);
			List<Object> datapoints = new ArrayList<Object>();
			datapoints.add(datapoint);
			body.setDatapoints(datapoints);
			bodies.add(body);
			
			data.setBody(bodies);
			System.out.println(data);
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
		body1.setName("gslab-solarpanel-" + solarPanel.getPanelId() + "-voltageIn");
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
		body2.setName("gslab-solarpanel-" + solarPanel.getPanelId() + "-voltageOut");
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
		body3.setName("gslab-solarpanel-" + solarPanel.getPanelId() + "-current");
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
		body4.setName("gslab-solarpanel-" + solarPanel.getPanelId() + "-temperature");
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
		body5.setName("gslab-solarpanel-" + solarPanel.getPanelId() + "-power");
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
		
		
		timeseriesClient.postDataToTimeseriesWebsocket(data);

	}
}
