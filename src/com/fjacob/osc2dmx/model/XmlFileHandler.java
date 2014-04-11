package com.fjacob.osc2dmx.model;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.fjacob.osc2dmx.util.*;

public class XmlFileHandler extends AbstractFileHandler {
	
	public XmlFileHandler(AddressIO io) {
		setAddressIO(io);
	}

	@Override
	public boolean openFile(String path) {
		boolean success = false;
		
		Document dom = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			dom = db.parse(path);
		} catch (ParserConfigurationException e) {
			ExceptionLog.log(this.toString(), e.toString());
		} catch (SAXException e) {
			ExceptionLog.log(this.toString(), e.toString());
		} catch (IOException e) {
			ExceptionLog.log(this.toString(), e.toString());
		}
		
		if(dom != null) {
			NodeList nodes = dom.getElementsByTagName("Address");
			for(int i = 0; i < nodes.getLength(); i++) {
				//get address node
				Node n = nodes.item(i);
				
				//read and validate row
				int row = -1;
				Node rowAttribute = n.getAttributes().getNamedItem("row");
				if(rowAttribute != null) {
					String rowString = rowAttribute.getNodeValue();
					try {
						row = Integer.parseInt(rowString);
					} catch(NumberFormatException e) {
						row = -1;
					}
				}
				
				//read and validate dmx
				int dmx = -1;
				Node dmxAttribute = n.getAttributes().getNamedItem("dmx");
				if(dmxAttribute != null) {
					dmx = InputValidator.validateDmxChannel(dmxAttribute.getNodeValue());
				}
				
				//read and validate osc
				String osc = "";
				Node oscAttribute = n.getAttributes().getNamedItem("osc");
				if(oscAttribute != null) {
					osc = InputValidator.validateOscAddress(oscAttribute.getNodeValue());
				}
				
				//read and validate arg-idx
				int argIdx = -1;
				Node argIdxAttribute = n.getAttributes().getNamedItem("arg-idx");
				if(argIdxAttribute != null) {
					String argIdxString = argIdxAttribute.getNodeValue();
					try {
						argIdx = Integer.parseInt(argIdxString);
					} catch(NumberFormatException e) {
						argIdx = -1;
						ExceptionLog.log(this.toString(), e.toString());
					}
				}

				//write data to model
				if(row >= 0) {
					if(dmx > 0) {
						getAddressIO().addDmx(row, dmx);
					}
					if(!osc.equals("")) {
						getAddressIO().addOsc(row, osc);
					}
					if(argIdx >= 0) {
						getAddressIO().addArgIdx(row, argIdx);
					}
					
					success = true;
				}	
			}
		}
		
		return success;		
	}

	@Override
	public boolean saveFile(String path) {
		boolean success = false;
		
		Document dom = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			dom = db.newDocument();
		} catch (ParserConfigurationException e) {
			ExceptionLog.log(this.toString(), e.toString());
		} 
		
		if(dom != null) {
			Element root = dom.createElement("OscDmxTable");
			
			AddressIO io = getAddressIO();
			Map<Integer, Integer> dmx = io.getDmxValues();
			Map<Integer, String> osc = io.getOscValues();
			Map<Integer, Integer> arg = io.getArgValues();
			
			for(int i = 0; i <= io.getMaxKey(); i++) {
				Element addr = dom.createElement("Address");
				addr.setAttribute("row", ((Integer)i).toString());
				
				if(dmx.containsKey(i)) addr.setAttribute("dmx", dmx.get(i).toString());
					else addr.setAttribute("dmx", "");
				if(osc.containsKey(i)) addr.setAttribute("osc", osc.get(i));
					else addr.setAttribute("osc", "");
				if(arg.containsKey(i)) addr.setAttribute("arg-idx", arg.get(i).toString());
					else addr.setAttribute("arg-idx", "");
				
				root.appendChild(addr);
			}
			
			dom.appendChild(root);
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			try {
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(dom);
				StreamResult result = new StreamResult(new File(path));
				
				transformer.transform(source, result);
				
				success = true;		
			} catch (TransformerConfigurationException e) {
				ExceptionLog.log(this.toString(), e.toString());
			} catch (TransformerException e) {
				ExceptionLog.log(this.toString(), e.toString());
			}		
		}
		
		return success;		
	}
}
