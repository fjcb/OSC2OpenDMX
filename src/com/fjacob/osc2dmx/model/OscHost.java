package com.fjacob.osc2dmx.model;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.LinkedList;
import java.util.Map;

import com.fjacob.osc2dmx.util.Constants;
import com.fjacob.osc2dmx.util.ExceptionLog;
import com.fjacob.osc2dmx.util.InputValidator;

import de.sciss.net.OSCListener;
import de.sciss.net.OSCMessage;
import de.sciss.net.OSCReceiver;
import de.sciss.net.OSCTransmitter;

public class OscHost {
	
	private float[] dmxData = new float[Constants.MAX_CHANNELS];
	private int inPort = Constants.DEF_IN_PORT;
	private int outPort = Constants.DEF_OUT_PORT;
	
	private OSCReceiver receiver = null;
	private OSCTransmitter transmitter = null;
	private DatagramChannel dataChannel = null;
	private final Object notify = new Object();
	
	private AddressIO io = null;
	private boolean running = false;
	private boolean broadcast = false;
	
	private LinkedList<String> clients = new LinkedList<String>();
	
	public OscHost() {
		//initialize data
		for(int i = 0; i < dmxData.length; i++) {
			dmxData[i] = 0;
		}
		
		//configure server
		setupServer();	
	}
	
	//configures the server's components.
	//used for start and restart.
	private void setupServer() {
		try {			
			dataChannel = DatagramChannel.open();
			dataChannel.socket().bind(new InetSocketAddress(inPort));
			receiver = OSCReceiver.newUsing(dataChannel);
			transmitter = OSCTransmitter.newUsing(dataChannel);
			
			receiver.addOSCListener(new OSCListener() {

				@Override
				public void messageReceived(OSCMessage m, SocketAddress addr, long time) {
					//System.out.println("OSc Msg recieved: "+m+" - "+toIpString(addr));
					if(clients.contains(toIpString(addr))) {
						for(int i = 0; i < m.getArgCount(); i++) {				//test for each argument if there is a dmx channel
							int[] dmxChannels = io.getDmxChannels(m.getName(), i);		//get dmx channels for current argument
							for(int k : dmxChannels) {
								if(k > 0) {
									//store dmx data
									dmxData[k-1] = InputValidator.validateOscInput(Float.parseFloat(m.getArg(i).toString()));
									
									if(broadcast) {
										//broadcast
										broadcastIncommingData(m, addr, k);
									}
								}
							}
						}
						synchronized(notify) {
							notify.notifyAll();
						}
					}
				}
					
			});
			
		} catch (IOException e) {
			ExceptionLog.log(this.toString(), e.toString());
		}
	}
	
	//resets all components of the server.
	//used for restart/ port changing.
	private void resetServer() {
		try {
			receiver.stopListening();
		
			receiver.dispose();
		
			transmitter.dispose();
		
			dataChannel.close();
		} catch (IOException e) {
			ExceptionLog.log(this.toString(), e.toString());
		}
	}
	
	private String toIpString(SocketAddress addr) {
		String ip =  addr.toString();
		if(ip.startsWith("/")) ip = ip.substring(1);	//delete "/" at the beginning
		if(ip.contains(":")) ip = ip.split(":")[0];		//delte ":0123" at the end
		return ip;
	}
	
	public void addClient(String client) {
		clients.add(client);
	}
	
	public void removeClient(String client) {
		clients.remove(client);
	}
	
	public boolean isClient(String client) {
		if(clients.contains(client)) return true;
			else return false;
	}
	
	public String[] getClients() {
		//casting from LinkList to StringArray
		Object objectArray[] = clients.toArray();
		String stringArray[] = new String[objectArray.length];
		for(int i = 0; i < stringArray.length; i++) {
			stringArray[i] = (String) objectArray[i];
		}
		
		return stringArray;
	}
	
	public void start() {
		//only start hte server if AdressIO has been set
		if(io != null) {
			try {
				receiver.startListening();			
				running = true;
			} catch (IOException e) {
				ExceptionLog.log(this.toString(), e.toString());
			}
			
		}
	}
	
	public void stop() {
		try {
			receiver.stopListening();
			running = false;
		} catch (IOException e) {
			ExceptionLog.log(this.toString(), e.toString());
		}
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public int[] readDmxData() {
		int[] output = new int[Constants.MAX_CHANNELS];
		
		//convert from 0..1 to 0..255
		for(int i = 0; i < output.length; i++){
			output[i] = (int)(dmxData[i]*255);
		}
		
		return output;
	}
	
	public void setInPort(int port) {
		inPort = port;
		
		//first stop the server
		resetServer();
		
		//then set it up with new settings
		setupServer();
		
		//if the server was running, restart it
		if(running == true) {
			try {
				receiver.startListening();
			} catch (IOException e) {
				ExceptionLog.log(this.toString(), e.toString());
			}
		}
	}
	
	public void setOutPort(int port) {
		outPort = port;
	}
	
	public void setAddressIO(AddressIO io) {
		this.io = io;
	}
	
	//this method broadcasts incomming data to all clients, which are using the same osc address or manipulating the same dmx channel
	public void broadcastIncommingData(OSCMessage m, SocketAddress addr, int dmxChannel) {
		String sender = toIpString(addr);
		
		//first: simple boradcast the same message
		//for all clients except the one who sent the message
		for(String ip : clients) {
			if(!ip.equals(sender) && ip != null) {							
				try {
					transmitter.send(m, new InetSocketAddress(ip, outPort));	//incomming message loopback
				} catch (IOException e) {
					ExceptionLog.log(this.toString(), e.toString());
				} 		
			}
		}
		
		//second: modify message, then broadcast
		//check other oscAddrs working on this dmx channel
		Map<String, Integer> parallelOscAddrs = io.getParallelOscAddresses(dmxChannel);
		for(String oscAddr : parallelOscAddrs.keySet())	{
			int argIdx = parallelOscAddrs.get(oscAddr);
			
			//create new osc message
			OSCMessage newMsg = new OSCMessage(oscAddr);
			
			if(argIdx == 0) {
				//just one argument, we can create the new msg instant
				newMsg = new OSCMessage(oscAddr, new Object[] { dmxData[dmxChannel-1] });
			} else {
				//for more arguments, first get information for other arguments
				Object[] data = new Object[argIdx+1];
				data[argIdx] = dmxData[dmxChannel-1];
				
				for(int i = 0; i < data.length; i++) {
					Map<Integer, Integer> args = io.getOscArgs(oscAddr);				
					if(args.containsKey(i)) {
						data[i] = dmxData[args.get(i)-1];
						newMsg = new OSCMessage(oscAddr, data);
					}
				}
			}
			
			//broadcast to clients
			for(String ip : clients) {
				if(ip != null) {							
					try {
						transmitter.send(newMsg, new InetSocketAddress(ip, outPort));	//incomming message loopback
					} catch (IOException e) {
						ExceptionLog.log(this.toString(), e.toString());
					} 		
				}
			}
		}
		
	}
	
	public String getIpAddress() {
		try {
			return receiver.getLocalAddress().getAddress().toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void setBroadcast(boolean broadcast) {
		this.broadcast = broadcast;
	}
	
	public void resetDmxData() {
		for(int i = 0; i < dmxData.length; i++) {
			dmxData[i] = 0f;
		}
	}
}
