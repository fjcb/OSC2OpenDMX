package com.fjacob.osc2dmx.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AddressIO {	
																
	private Map<Integer, Integer> dmx;	
	private Map<Integer, String> osc;	
	private Map<Integer, Integer> arg;	
	
	private AbstractFileHandler xmlFileHandler;
	private int maxKey;
	
	public AddressIO() {							// e.g.
		dmx = new HashMap<Integer, Integer>();		//	0, 123
		osc = new HashMap<Integer, String>();		//  0, /1/fader1
		arg = new HashMap<Integer, Integer>();		//	0, 1
		
		xmlFileHandler = new XmlFileHandler(this);
		maxKey = 0;
	}
	
	//model interface
	public void addDmx(int key, int dmx) {
		this.dmx.put(key, dmx);
		if(key > maxKey) maxKey = key;
	}
	
	//model interface
	public void addOsc(int key, String osc) {
		this.osc.put(key, osc);
		if(key > maxKey) maxKey = key;
	}
	
	//model interface
	public void addArgIdx(int key, int argIdx) {
		this.arg.put(key, argIdx);
		if(key > maxKey) maxKey = key;
	}
	
	public Map<Integer, Integer> getDmxValues() {
		return dmx;
	}
	
	public Map<Integer, String> getOscValues() {
		return osc;
	}
	
	public Map<Integer, Integer> getArgValues() {
		return arg;
	}
	
	public int getMaxKey() {
		return maxKey;
	}	
	
	//oscHost interface
	public int[] getDmxChannels(String oscAddr, int argIdx) {
		Set<Integer> output = new HashSet<Integer>();
		
		for(int key : osc.keySet()) {
			//test for osc addr
			if(osc.get(key).equals(oscAddr)) {
				//test for arg idx
				if(arg.containsKey(key) && arg.get(key) == argIdx) {
					//get dmx channel, if exists
					if(dmx.get(key) != null) {
						output.add(dmx.get(key));
					}
				}
			}
		}
		
		//returns at least i = {-1}
		if(output.isEmpty()) {
			return new int[] {-1};
		} else {
			int[] outArray  = new int[output.size()];
			int i = 0;
			for(int value : output) {
				outArray[i++] = value;
			}
			return outArray;
		}
	}

	public boolean openFile(String path) {
		//reset first
		reset();
		
		//open file and return result
		return xmlFileHandler.openFile(path);
	}

	public boolean saveFile(String path) {
		//save file and return result
		return xmlFileHandler.saveFile(path);
	}

	public void reset() {
		dmx.clear();
		osc.clear();
		arg.clear();	
		
		maxKey = 0;
	}	
	
	//used for broadcast
	public Map<Integer, Integer> getOscArgs(String oscAddr) {
		//returns number of arg-indexes and their dmx-indexes for one osc address
		//e.g.:	/test1[0]: 123
		//		/test1[1]: 234
		//			returns:	(0, 123), (1, 234) as Map
		//input: osc address
		//output: Map(Arg, DmxIdx)
		Map<Integer, Integer> output = new HashMap<Integer, Integer>();
		
		for(int key : osc.keySet()) {
			if(osc.get(key).equals(oscAddr)) {
				if(arg.containsKey(key) && dmx.containsKey(key)) {
					output.put(arg.get(key), dmx.get(key));
				}
			}
		}
		return output;
	}
	
	//used for broadcast
	public Map<String, Integer> getParallelOscAddresses(int dmxChannel) {
		//find out dmx channel
		//input: dmxChannel
		//output: Map(OscAddr, ArgIdx)
		
		Map<String, Integer> output = new HashMap<String, Integer>();
		
		//get keys for this dmx channel
		for(int key : dmx.keySet()) {
			if(dmx.get(key) == dmxChannel) {
				//add oscAddr and argIdx to output if exist
				if(osc.containsKey(key) && arg.containsKey(key)) {
					output.put(osc.get(key), arg.get(key));
				}	
			}
		}
		return output;
	}

}
