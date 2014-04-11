package com.fjacob.osc2dmx.model;

import com.fjacob.osc2dmx.util.Constants;
import com.fjacob.osc2dmx.util.ExceptionLog;
import com.juanjo.openDmx.OpenDmx;


/**
 * @author Fritz Jacob
 *
 */
public class Model extends AbstractModel implements Runnable {
	
	private Thread modelThread;
	private OscHost oscHost;
	private AddressIO io;
	
	private boolean dmxConnected;
	private boolean oscHostRunning;
	private boolean dmxTest;
	private long testCounter;
	private int[] dmxData;
	
	public Model() {
		modelThread = new Thread(this);
		modelThread.start();
		
		oscHost = new OscHost();
		io = new AddressIO();
		oscHost.setAddressIO(io);
		
		dmxConnected = false;
		oscHostRunning = false;
		dmxTest = false;
		testCounter = 0;
		
		//create dmx array and set all values to 0
		dmxData = new int[512];
		for(int i = 0; i < dmxData.length; i++) {
			dmxData[i] = 0;
		}
	}
	
	//main loop
	@Override
	public void run() {
		while(true) {
			//oscHost
			//-------------------------------------------------------------
			if(oscHostRunning) {
				//recieve new osc data
				dmxData = oscHost.readDmxData();	
			}
			//-------------------------------------------------------------
			
			//dmxTest
			//-------------------------------------------------------------
			if(dmxTest) {
				if(testCounter == 0) {
					testCounter = System.currentTimeMillis();
				}
				
				//setting all channels for 1 sec to 100%
				for(int i = 0; i < dmxData.length; i++) {
					dmxData[i] = 255;
				}
				
				if(System.currentTimeMillis() - testCounter > 1000) {
					//stop test
					dmxTest = false;
					testCounter = 0;
					
					//reset dmx data
					for(int i = 0; i < dmxData.length; i++) {
						dmxData[i] = 0;
					}
					
					sendStatusMsg("DMX test has finished.");
				}
				
			}
			//-------------------------------------------------------------
			
			//write dmx data to widget
			//-------------------------------------------------------------
			if(dmxConnected) {
				for(int i = 0; i < Constants.MAX_CHANNELS; i++) {
					OpenDmx.setValue(i, dmxData[i]);
				}
				
				//update view
				updateDmxData(dmxData);
			}
			//-------------------------------------------------------------
			
			//pause thread
			//-------------------------------------------------------------
			try {
				Thread.sleep(22);	//44,1 hz is the standard frequenz of a dmx signal
			} catch (InterruptedException e) {
				ExceptionLog.log(this.toString(), e.toString());
			}
			//-------------------------------------------------------------
		}	
	}
	
	//method which manages the dmx widget connection
	@Override
	public void connect() {
		try {
			if(OpenDmx.connect(OpenDmx.OPENDMX_TX)) {
				dmxConnected = true;
				
				//notify observers
				connectDmxWidget();
						
				//status message
				sendStatusMsg("DMX widget connected succesfully.");
			} else {
				sendStatusMsg("DMX widget not connected!");
			}
		} catch(Exception e) {
			//e.printStackTrace();
		}
	}
	
	//runs a short dmx test
	@Override
	public void testDmx() {
		if(dmxConnected) {
			if(testCounter == 0) {
				if(oscHostRunning) {
					sendStatusMsg("OSC host has to be stopped first.");
				} else {
					//run test
					sendStatusMsg("Starting DMX test...");
					dmxTest = true;
				}	
			} else {
				sendStatusMsg("DMX test is already running.");
			}
		} else {
			//dmx widget isn't connected
			sendStatusMsg("Cannot run test. DMX widget is not connected.");
		}
	}
	
	//set all dmx data to 0
	@Override
	public void resetDmx() {
		//reset dmxData in oscHost
		oscHost.resetDmxData();
		
		//set dmxData array to 0;
		for(int i = 0; i < dmxData.length; i++) {
			dmxData[i] = 0;
		}
		
		//status message
		sendStatusMsg("All dmx data resetted to 0.");
	}
	
	//this method starts the osc host
	@Override
	public void startOscHost() {
		//start host
		oscHost.start();
		oscHostRunning = true;
		
		//notify observers
		oscHostStarted();
		
		//status message
		sendStatusMsg("OSC Host started.");
	}
	
	//this method stops the osc host
	@Override
	public void stopOscHost() {
		//stop host
		oscHost.stop();
		oscHostRunning = false;
		
		//notify observers
		oscHostStopped();
		
		//status message
		sendStatusMsg("OSC Host stopped.");
	}
	
	@Override
	public void setInPort(int port) {
		if(port != -1) {
			oscHost.setInPort(port);
			
			//status message
			sendStatusMsg("Port for incomming data set to "+ port);
		} else {
			//status message
			sendStatusMsg("New port for incomming data is invalid!");
		}	
	}
	
	@Override
	public void setOutPort(int port) {
		if(port != -1) {
			oscHost.setOutPort(port);
			
			//status message
			sendStatusMsg("Port for outcomming data set to "+ port);
		} else {
			//status message
			sendStatusMsg("New port for outgoing data is invalid!");
		}
		
	}
	
	public void addClient(String client) {
		if(!oscHost.isClient(client)) {
			oscHost.addClient(client);
			
			//notify view
			updateClientList();
			
			//status message
			sendStatusMsg(client + " added.");
		} else {
			sendStatusMsg(client + " already in list.");
		}
	}
	
	@Override
	public AddressIO getAddressIO() {
		return io;
	}
	
	public void removeClient(String client) {
		oscHost.removeClient(client);
		
		//notify view
		updateClientList();
		
		//status message
		sendStatusMsg(client + " removed.");
	}

	@Override
	public void openOscDmxTable(String path) {
		if(io.openFile(path)) {
			//success
			//status message
			sendStatusMsg("File ("+path+") opened successfully.");
		} else {
			//status message
			sendStatusMsg("Invalid file ("+path+").");
		}
		//update view
		updateOscDmxAdresses(io.getDmxValues(), io.getOscValues(), io.getArgValues());	
	}

	@Override
	public void saveOscDmxTable(String path) {	
		if(io.saveFile(path)) {
			//success
			//status message
			sendStatusMsg("File ("+path+") saved successfully.");
		} else {
			//status message
			sendStatusMsg("An error occured while saving file ("+path+").");
		}
	}

	@Override
	public void resetOscDmxTable() {
		io.reset();
		
		//update view
		updateOscDmxAdresses(io.getDmxValues(), io.getOscValues(), io.getArgValues());
	}

	@Override
	public void enableBroadcast() {
		this.oscHost.setBroadcast(true);
		
		//status message
		sendStatusMsg("Broadcast for incomming data enabled.");
	}

	@Override
	public void disableBroadcast() {
		this.oscHost.setBroadcast(false);
		
		//status message
		sendStatusMsg("Broadcast for incomming data disabled.");
	}

	@Override
	public OscHost getOscHost() {
		return oscHost;
	}

	@Override
	public void initOscObservers() {
		//display local ip
		updateIpAddress(oscHost.getIpAddress());	

		//display standard ports
		updateInPort(Constants.DEF_IN_PORT);
		updateOutPort(Constants.DEF_OUT_PORT);	
	}

	@Override
	public void initDmxObservers() {
		//display empty dmx table
		updateDmxData(dmxData);
	}

	@Override
	public void initStatusObservers() {
		//status message
		sendStatusMsg("OSC2DMX started.");
	}	

}
