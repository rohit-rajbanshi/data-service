package com.gslab.solar.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ge.predix.entity.timeseries.datapoints.queryrequest.DatapointsQuery;
import com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag;
import com.ge.predix.entity.timeseries.datapoints.queryresponse.DatapointsResponse;
import com.ge.predix.solsvc.restclient.impl.RestClient;
import com.ge.predix.solsvc.timeseries.bootstrap.client.TimeseriesClient;
import com.ge.predix.solsvc.timeseries.bootstrap.config.ITimeseriesConfig;
import com.gslab.solar.domain.Interval;

@Service
public class DataQueryService {

	private static final Logger LOG = LoggerFactory.getLogger(DataIngestionService.class);

	@Autowired
	private TimeseriesClient timeseriesClient;

	@Autowired
	private RestClient restClient;

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

	public DatapointsResponse queryData(Interval interval, String panelId) {
		System.out.println(panelId + " " + interval.getStartTimel() + " " + interval.getEndTime());
		DatapointsQuery query = new DatapointsQuery();
		List<Tag> tags = new ArrayList<Tag>();
		Tag tag = new Tag();
		tag.setName(panelId);
		tags.add(tag);
		query.setTags(tags);
		query.setStart(interval.getStartTimel());
		query.setEnd(interval.getEndTime());
		List<Header> headers = generateHeaders();
		headers.add(new BasicHeader("Content-Type", "application/json"));
		DatapointsResponse response = timeseriesClient.queryForDatapoints(query, headers);
		return response;
	}

	@SuppressWarnings({})
	private List<Header> generateHeaders() {
		List<Header> headers = this.restClient.getSecureTokenForClientId();
		this.restClient.addZoneToHeaders(headers, this.timeseriesConfig.getZoneId());
		return headers;
	}

}
