package com.gslab.solar.service;

import java.util.HashMap;
import java.util.Map;

public class StringDataParser {

	private String str;
	private Integer totalRec;
	private String panelTag;
	private Map<String, Map> panelData;

	public StringDataParser(String str) {
		this.str = str.trim();
		panelData = new HashMap<String, Map>();
	}

	public void parseData() {

		String temp = str;
		totalRec = Integer.parseInt(temp.substring(0, 2));
		temp = temp.substring(2);
		int i = totalRec;
		while (--i >= 0) {

			Map<String, Double> values = new HashMap<String, Double>();

			panelTag = "gslab-solarpanel-" + temp.substring(0, 3);
			temp = temp.substring(3);

			values.put("voltageIn", Double.parseDouble(temp.substring(0, 5)));
			values.put("current", Double.parseDouble(temp.substring(5, 10)));
			values.put("voltageOut", Double.parseDouble(temp.substring(10, 15)));
			values.put("temperature", Double.parseDouble(temp.substring(15, 20)));

			temp = temp.substring(20);
			panelData.put(panelTag, values);
		}
	}

	public Integer getTotalRec() {
		return totalRec;
	}

	public void setTotalRec(Integer totalRec) {
		this.totalRec = totalRec;
	}

	public String getPanelTag() {
		return panelTag;
	}

	public void setPanelTag(String panelTag) {
		this.panelTag = panelTag;
	}

	public Map<String, Map> getPanelData() {
		return panelData;
	}

	public void setPanelData(Map<String, Map> panelData) {
		this.panelData = panelData;
	}

}
