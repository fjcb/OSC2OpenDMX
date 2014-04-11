package com.fjacob.osc2dmx.model;

import java.util.LinkedList;
import java.util.Map;

public abstract class AbstractModel {
	private LinkedList<OscObserver> oscObserver = new LinkedList<OscObserver>();
	private LinkedList<DmxObserver> dmxObserver = new LinkedList<DmxObserver>();
	private LinkedList<StatusObserver> statusObserver = new LinkedList<StatusObserver>();
	
	//register and remove observers
	public void registerOscObserver(OscObserver o) {
		oscObserver.add(o);
		initOscObservers();
	}
	
	public void registerDmxObserver(DmxObserver o) {
		dmxObserver.add(o);
		initDmxObservers();
	}
	
	public void registerStatusObserver(StatusObserver o) {
		statusObserver.add(o);
		initStatusObservers();
	}
	
	public void removeOscObserver(OscObserver o) {
		oscObserver.remove(o);
	}
	
	public void removeDmxObserver(DmxObserver o) {
		dmxObserver.remove(o);
	}
	
	public void removeStatusObserver(StatusObserver o) {
		statusObserver.remove(o);
	}
	
	//status observer methods
	public void sendStatusMsg(String msg) {
		for(StatusObserver o : statusObserver) {
			o.recieveStatusMsg(msg);
		}
	}
	
	public void oscHostStarted() {
		for(StatusObserver o : statusObserver) {
			o.oscHostStarted();
		}
	}
	
	public void oscHostStopped() {
		for(StatusObserver o : statusObserver) {
			o.oscHostStopped();
		}
	}
	
	public void connectDmxWidget() {
		for(StatusObserver o : statusObserver) {
			o.dmxWidgetConnected();
		}
	}
	
	//osc observer methods
	public void updateOscDmxAdresses(Map<Integer, Integer> dmx, Map<Integer, String> osc, Map<Integer, Integer> arg) {
		for(OscObserver o : oscObserver) {
			o.oscDmxAddressesUpdated(dmx, osc, arg);
		}
	}

	public void updateIpAddress(String adress) {
		for(OscObserver o : oscObserver) {
			o.ipAddressUpdated(adress);
		}
	}
	
	public void updateClientList() {
		for(OscObserver o : oscObserver) {
			o.clientListUpdated(getOscHost().getClients());
		}
	}
	
	//dmx observer methods
	public void updateDmxData(int[] data) {
		for(DmxObserver o : dmxObserver) {
			o.dmxDataUpdated(data);
		}
	}
	
	public void updateInPort(int port) {
		for(OscObserver o : oscObserver) {
			o.inPortUpdated(port);
		}
	}
	
	public void updateOutPort(int port) {
		for(OscObserver o : oscObserver) {
			o.outPortUpdated(port);
		}
	}
	
	//abstract methods
	public abstract void connect();
	public abstract void resetDmx();
	public abstract void startOscHost();
	public abstract void stopOscHost();
	public abstract void setInPort(int port);
	public abstract void setOutPort(int port);
	public abstract void enableBroadcast();
	public abstract void disableBroadcast();
	public abstract void addClient(String client);
	public abstract void removeClient(String client);
	public abstract void testDmx();
	public abstract AddressIO getAddressIO();
	public abstract void openOscDmxTable(String path);
	public abstract void saveOscDmxTable(String path);
	public abstract void resetOscDmxTable();
	public abstract OscHost getOscHost();
	public abstract void initOscObservers();
	public abstract void initDmxObservers();
	public abstract void initStatusObservers();

}
