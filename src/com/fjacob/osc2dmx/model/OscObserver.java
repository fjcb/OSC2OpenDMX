package com.fjacob.osc2dmx.model;

import java.util.Map;

public interface OscObserver {
	public void oscDmxAddressesUpdated(Map<Integer, Integer> dmx, Map<Integer, String> osc, Map<Integer, Integer> arg); //sends a array of osc addresses to the view
	public void ipAddressUpdated(String ip); //sends the hosts ip address to the view
	public void clientListUpdated(String[] clients); //sends a array of osc clients to the view
	public void inPortUpdated(int port);
	public void outPortUpdated(int port);
}
