package com.gslab.solar.domain;

/**
 * Solar Panel. Data like Voltage, current and temperature will be posted by Sensors
 * @author Rohit Rajbanshi
 *
 */
public class SolarPanel {
	private String panelId;
	private double voltageIn;
	private double voltageOut;
	private double current;
	private double temperature;
	
	public double getVoltageIn() {
		return voltageIn;
	}
	public void setVoltageIn(double voltageIn) {
		this.voltageIn = voltageIn;
	}
	public double getVoltageOut() {
		return voltageOut;
	}
	public void setVoltageOut(double voltageOut) {
		this.voltageOut = voltageOut;
	}
	public double getCurrent() {
		return current;
	}
	public void setCurrent(double current) {
		this.current = current;
	}
	public double getTemperature() {
		return temperature;
	}
	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}
	public double getPower() {
		return voltageOut * current;
	} 
	public String getPanelId() {
		return panelId;
	}
	public void setPanelId(String panelId) {
		this.panelId = panelId;
	}
}
