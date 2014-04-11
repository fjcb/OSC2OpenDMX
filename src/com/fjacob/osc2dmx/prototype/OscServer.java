package com.fjacob.osc2dmx.prototype;

import java.io.IOException;
import java.net.SocketAddress;

import de.sciss.net.OSCListener;
import de.sciss.net.OSCMessage;
import de.sciss.net.OSCServer;

public class OscServer {
	private int[] data = new int[MainPrototype.MAX_CHANNELS];
	private String[] addresses = new String[MainPrototype.MAX_CHANNELS];
	
	private OSCServer server;
	
	public OscServer() throws IOException {
		//initialize data
		for(int i = 0; i < data.length; i++) {
			data[i] = 0;
		}
		
		addresses = OscAddresses.getAddresses(512);		
		
		//server test, udp, port: 4444
		server = OSCServer.newUsing("udp", 4444);
		server.addOSCListener(new OSCListener() {

			@Override
			public void messageReceived(OSCMessage m, SocketAddress addr, long time) {
				System.out.println("rx " +m.getName() + ": " + m.getArg(0));
				int index = findAddress(m.getName());
				if(index >= 0) {
					data[index] = (int) (Float.parseFloat(m.getArg(0).toString()) * 255);					
				}
			}
				
		});
	}

	public void start() throws IOException {
		server.start();		
	}

	public int[] readData() {		
		return data;
	}
	
	private int findAddress(String q) {
		for(int i = 0; i < addresses.length; i++) {
			if(addresses[i].equals(q)) return i;
		}		
		return -1;
	}
	
	public void stop() throws IOException {
		server.stop();
	}

}
