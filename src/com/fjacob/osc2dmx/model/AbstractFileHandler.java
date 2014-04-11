package com.fjacob.osc2dmx.model;

public abstract class AbstractFileHandler {
	
	private AddressIO io = null;
	
	public AddressIO getAddressIO() {
		return io;
	}
	
	public void setAddressIO(AddressIO io) {
		this.io = io;
	}
	
	public abstract boolean openFile(String path);
	public abstract boolean saveFile(String path);
}
