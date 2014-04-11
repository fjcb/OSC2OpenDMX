package com.fjacob.osc2dmx.controller;

import com.fjacob.osc2dmx.view.View;

public interface ControllerInterface {
	void setView(View view);
	
	void connectDmx();
	void testDmx();
	void resetDmx();
	void startOscHost();
	void stopOscHost();
	
	void setInPort(String port);
	void setOutPort(String port);
	void enableBroadcast();
	void disableBroadcast();
	void openOscDmxTable(String path);
	void saveOscDmxTable(String path);
	void resetOscDmxTable();
	void addClient(String clientIp);
	void removeClient(String clientIp);
	
	//will be called, if a cell in oscTable has changed
	void setOscAdress(int column, int row, String data);
}
