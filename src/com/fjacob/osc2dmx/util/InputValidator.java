package com.fjacob.osc2dmx.util;

public class InputValidator {	
	//validates a port. return -1 if the given port contains non-numbers or is greater than 65535
	public static int validatePort(String port) {
		int input = -1;
		try {
			input = Integer.parseInt(port);
		} catch (Exception e) {
		}
		if(input > 0 && input < 65536) {
			return input;
		} else {
			return -1;
		}
	}
	
	//returns true for a valid ip address
	public static boolean validateIpAddress(String ip) {
		String[] parts = ip.split("\\.");
		if(parts.length != 4) return false;
		
		for(String s : parts) {
			try {
				int i = Integer.parseInt(s);
				if(i < 0 || i > 255) return false;
			} catch (Exception e) {
				return false;
			}
		}
		
		return true;
	}
	
	public static int validateDmxChannel(String dmxChannel) {
		int input = -1;
		try {
			input = Integer.parseInt(dmxChannel);
		} catch (Exception e) {
		}
		if(input > 0 && input < 513) {
			return input;
		} else {
			return -1;
		}
	}
	
	public static String validateOscAddress(String addr) {
		//input = "" ?
		if(addr.equals("")) return "";
		
		String output = "";
		
		//add a slash if necessary
		if(addr.startsWith("/")) output = addr; 
			else output = "/" + addr;
	
		return output;
	}

	public static int validateArgIdx(String data) {
		int input = -1;
		try {
			input = Integer.parseInt(data);
		} catch (Exception e) {
		}
		if(input >= 0) {
			return input;
		} else {
			return -1;
		}
	}
	
	//returns a value between 0 and 1 (even if the input is smaller than 0 or above 1)
	public static float validateOscInput(float input) {
		if(input >= 0f) {
			if(input <= 1f) {
				//between 0 and 1
				return input;
			} else {
				//larger than 1
				return 1f;
			}
		} else {
			//smaller than 0
			return 0f;
		}
	}
	
	//adds file extension to the path
	public static String validateSavePath(String path) {
		if(path.endsWith("." + Constants.FILE_EXT)) {
			return path;
		} else {
			return path + "."+ Constants.FILE_EXT;
		}
	}
	
}
