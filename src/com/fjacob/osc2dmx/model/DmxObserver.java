package com.fjacob.osc2dmx.model;

public interface DmxObserver {
	public void dmxDataUpdated(int[] data); //sends a dmx data array[512] to the view
}
