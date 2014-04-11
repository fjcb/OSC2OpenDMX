package com.fjacob.osc2dmx.prototype;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class OscAddresses {
	private final static String PATH = "res/OscAddr.xml";
	
	public static String[] getAddresses(int size) {
		String[] addr = new String[size];
		for(int i = 0; i < addr.length; i++) {
			addr[i] = "";
		}
		
		//read xml data
		Document dom = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			dom = db.parse(PATH);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(dom == null) {
			return addr;
		} else {
			System.out.println(" XML Parser: reading Osc-Adress-Table ..");
			
			NodeList nodes = dom.getElementsByTagName("Address");
			for(int i = 0; i < nodes.getLength(); i++) {
				//get Adress item
				Node n = nodes.item(i);
				//read index
				int index = Integer.parseInt(n.getAttributes().getNamedItem("index").getNodeValue());
				//read address
				String name = n.getAttributes().getNamedItem("name").getNodeValue();
				//write address to output at index
				if(index < size) addr[index] = name;
			}
		}
		
		return addr;
	}

}
