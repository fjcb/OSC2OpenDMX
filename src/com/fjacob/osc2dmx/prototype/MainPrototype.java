package com.fjacob.osc2dmx.prototype;

import java.io.IOException;

import com.juanjo.openDmx.OpenDmx;

public class MainPrototype implements Runnable {

	private boolean running = false;
	private Thread server;
	
	public final static int MAX_CHANNELS = 512;
	private int[] dmxData = new int[MAX_CHANNELS];
	
	private OscServer oscServer;
	
	public static void main(String[] args) throws IOException {
		MainPrototype prototype = new MainPrototype();
		prototype.start();
	}
	
	public MainPrototype() throws IOException {
		System.out.println(" initialize MainPrototype..");
		
		//create Thread
		server = new Thread(this);
		
		//create OSC Server
		oscServer = new OscServer();		
	}
	
	public void start() throws IOException {	
		//connect to dmx widget
		if(OpenDmx.connect(OpenDmx.OPENDMX_TX)) {
			running = true;
			System.out.println(" DMX widget connected");
		} else {
			running = false;
			System.err.println(" DMX widget not connected");
		}
		
		//start thread
		server.start();
		System.out.println(" thread started");
		
		//start server
		oscServer.start();
		System.out.println(" Osc Servcer started");
	}

	@Override
	public void run() {
		//starts only if dmx widget is connected
		try {
			while(running && System.in.available()==0) {
				//read osc input
				dmxData = oscServer.readData();			
				
				//print info
				System.out.print(" \n .. sending data: ");
				for(int i = 0; i < 10; i ++) {
					System.out.print(dmxData[i] + ", ");
				}
				
				//write dmx output
				for(int i=0;i<MAX_CHANNELS;i++) {	
					OpenDmx.setValue(i,dmxData[i]);					
				}
				
				Thread.sleep(22);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			stop();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void stop() throws IOException {
		running = false;
		OpenDmx.disconnect();	
		oscServer.stop();	
		
		System.out.println(" Osc2Dmx has stopped");
	}

}
