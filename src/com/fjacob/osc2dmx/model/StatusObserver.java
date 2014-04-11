package com.fjacob.osc2dmx.model;

public interface StatusObserver {
	public void recieveStatusMsg(String msg); //sends a status message to the view
	public void dmxWidgetConnected(); //callback function which indicates if the widget has been connected succesfully
	public void oscHostStarted(); //called if host has started
	public void oscHostStopped(); //called if host has stopped
}
