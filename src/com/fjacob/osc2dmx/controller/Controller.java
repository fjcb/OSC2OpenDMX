package com.fjacob.osc2dmx.controller;

import com.fjacob.osc2dmx.model.AbstractModel;
import com.fjacob.osc2dmx.util.InputValidator;
import com.fjacob.osc2dmx.view.View;

public class Controller implements ControllerInterface {
	private AbstractModel model;
	private View view;
	
	public Controller(AbstractModel model) {
		this.model = model;
	}
	
	@Override
	public void setView(View view) {
		this.view = view;
	}
	
	@Override
	public void connectDmx() {
		model.connect();
	}

	@Override
	public void testDmx() {
		model.testDmx();		
	}
	
	@Override
	public void resetDmx() {
		model.resetDmx();
	}

	@Override
	public void startOscHost() {
		model.startOscHost();
	}

	@Override
	public void stopOscHost() {
		model.stopOscHost();
		
	}

	@Override
	public void setInPort(String port) {
		int inPort = InputValidator.validatePort(port);
		model.setInPort(inPort);
	}

	@Override
	public void setOutPort(String port) {
		int outPort = InputValidator.validatePort(port);
		model.setOutPort(outPort);		
	}

	@Override
	public void openOscDmxTable(String path) {
		model.openOscDmxTable(path);	
	}

	@Override
	public void saveOscDmxTable(String path) {
		model.saveOscDmxTable(InputValidator.validateSavePath(path));		
	}

	@Override
	public void resetOscDmxTable() {
		model.resetOscDmxTable();
	}

	@Override
	public void addClient(String clientIp) {
		//validate input
		if(InputValidator.validateIpAddress(clientIp)) {		
			//notify model
			model.addClient(clientIp);
		}
		
	}

	@Override
	public void removeClient(String clientIp) {	
		//notify model
		model.removeClient(clientIp);	
	}

	@Override
	public void setOscAdress(int column, int row, String data) {
		//break if data = ""
		if(data.equals("")) return;
		
		switch(column) {
		case 0:
			//validate dmx address
			int dmxChannel = InputValidator.validateDmxChannel(data);
			
			//change view
			if(dmxChannel == -1) view.updateOscTable(column, row, ""); //delete cell at wrong input
			else view.updateOscTable(column, row, ""+dmxChannel);
			
			//send data to model
			model.getAddressIO().addDmx(row, dmxChannel);
			break;

		case 1:
			//validate osc address
			String oscAddress = InputValidator.validateOscAddress(data);
			
			//change view
			view.updateOscTable(column, row, oscAddress);
			
			//send data to model
			model.getAddressIO().addOsc(row, oscAddress);
			break;
			
		case 2:
			//validate arg index
			int argIdx = InputValidator.validateArgIdx(data);
			
			//change view
			if(argIdx == -1) view.updateOscTable(column, row, ""); //delete cell at wrong input
			else view.updateOscTable(column, row, ""+argIdx);
			
			//send data to model
			model.getAddressIO().addArgIdx(row, argIdx);
			break;
		}
		
	}

	@Override
	public void enableBroadcast() {
		model.enableBroadcast();
	}

	@Override
	public void disableBroadcast() {
		model.disableBroadcast();
	}
}
