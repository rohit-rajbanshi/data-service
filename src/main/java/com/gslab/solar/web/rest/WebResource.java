package com.gslab.solar.web.rest;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.DatapointsIngestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gslab.iot.solar.domain.dto.SolarPanel;
import com.gslab.solar.service.DataIngestionService;

/**
 * @author Rohit Rajbanshi
 *
 */
@Controller
public class WebResource {

	@Autowired
	private DataIngestionService dataIngestionService;


	@RequestMapping("/")
	@ResponseBody
	public String greetings() {
		return "hello";
	}

	/*
	 * @RequestMapping(method = RequestMethod.GET, value = "/tags") public
	 * TagsList getTags(@RequestHeader(name = "Authorization") String
	 * authorization) { LOG.info("Header:{}", authorization); try { List<Header>
	 * headers = generateHeaders(); TagsList tagsList =
	 * this.timeseriesClient.listTags(headers); return tagsList; } catch
	 * (Throwable e) { throw new
	 * RuntimeException("unable to get wind data, errorMsg=" + e.getMessage() +
	 * ". config=" + this.timeseriesConfig, e); } }
	 * 
	 * @RequestMapping("/getToken") public String getToken() throws Exception {
	 * try {
	 * 
	 * Properties prop = new Properties();
	 * prop.load(DataServiceApplication.input); ClientCredentialsResourceDetails
	 * resourceDetails = new ClientCredentialsResourceDetails(); String url =
	 * prop.getProperty("predix.oauth.issuerId.url");
	 * resourceDetails.setAccessTokenUri(url); String clientId =
	 * prop.getProperty("predix.oauth.clientId").split(":")[0].trim(); String
	 * clientSecret =
	 * prop.getProperty("predix.oauth.clientId").split(":")[1].trim();
	 * System.err.println(clientId + " " + clientSecret);
	 * resourceDetails.setClientId(clientId);
	 * resourceDetails.setClientSecret(clientSecret);
	 * 
	 * // resourceDetails.setClientId("app_client_id"); //
	 * resourceDetails.setClientSecret("secret");
	 * 
	 * OAuth2RestTemplate restTemplate = new
	 * OAuth2RestTemplate(resourceDetails); OAuth2AccessToken token =
	 * restTemplate.getAccessToken(); JSONObject jsonObj = new JSONObject();
	 * jsonObj.put("access_token", token.getValue()); jsonObj.put("token_type",
	 * token.getTokenType());
	 * 
	 * return jsonObj.toString(); } catch (HttpClientErrorException hce) { throw
	 * new Exception(hce); } }
	 */

	@RequestMapping(method = RequestMethod.POST, value = "/panel")
	public ResponseEntity<Boolean> postPanelData(@RequestBody SolarPanel[] solarPanels) {
		for (SolarPanel solarPanel : solarPanels) {
			dataIngestionService.ingest(solarPanel);
		}
		return new ResponseEntity<Boolean>(true, HttpStatus.CREATED);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/raw-panel")
	public ResponseEntity<Boolean> postRawPanelData(@RequestParam(value = "str") String str) {
			dataIngestionService.ingest(str);
		return new ResponseEntity<Boolean>(true, HttpStatus.CREATED);
	}

	protected Response handleResult(Object entity) {
		ResponseBuilder responseBuilder = Response.status(Status.OK);
		responseBuilder.type(MediaType.APPLICATION_JSON);
		responseBuilder.entity(entity);
		return responseBuilder.build();
	}

}
