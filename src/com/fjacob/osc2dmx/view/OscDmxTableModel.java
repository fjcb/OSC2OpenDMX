package com.fjacob.osc2dmx.view;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

public class OscDmxTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -976155486752183659L;
	
	private String[] columns = {"Dmx", "Osc", "Arg"};;
	private int height = 1;
	
	private Map<Integer, String> dmx;
	private Map<Integer, String> osc;
	private Map<Integer, String> arg;
	
	public OscDmxTableModel() {		
		dmx = new HashMap<Integer, String>();
		osc = new HashMap<Integer, String>();
		arg = new HashMap<Integer, String>();
	}
	
	//called by model observers, does'nt fire a TableChanged()
	public void updateValueAt(String s, int row, int column) {
		//delegate data to the right column
		switch(column) {
			case 0: dmx.remove(row);
					dmx.put(row, s);
					break;
			case 1: osc.remove(row); 
					osc.put(row,s);
					break;
			case 2: arg.remove(row); 
					arg.put(row, s);
					break;
		}
	}
	
	public void restoreTableData(Map<Integer, Integer> dmx, Map<Integer, String> osc, Map<Integer, Integer> arg) {	
		this.dmx = castHashMap(dmx);
		this.osc = osc;
		this.arg = castHashMap(arg);
		
		int newHeight = 0;
		
		for(int key : this.dmx.keySet()) {
			if(key > newHeight) newHeight = key;
		}
		for(int key : this.osc.keySet()) {
			if(key > newHeight) newHeight = key;
		}
		for(int key : this.arg.keySet()) {
			if(key > newHeight) newHeight = key;
		}
		
		height = newHeight + 1;		//add a empty row
		
		fireTableStructureChanged();
	}
	
	private Map<Integer, String> castHashMap(Map<Integer, Integer> input) {
		Map<Integer, String> output = new HashMap<Integer, String>();
		Set<Integer> set = input.keySet();
		
		for(int key : set) {
			output.put(key, input.get(key).toString());
		}
		
		return output;
	}
	
	@Override
	public int getColumnCount() {		
		return columns.length;
	}

	@Override
	public int getRowCount() {
		return height;
	}
	
	@Override
	public void setValueAt(Object o, int row, int column) {
		//delegate data to the right column
		switch(column) {
			case 0: if(dmx.containsKey(row)) dmx.remove(row);
					dmx.put(row, (String) o); 
					break;
			case 1: if(osc.containsKey(row)) osc.remove(row);
					osc.put(row, (String) o); 
					break;
			case 2: if(arg.containsKey(row)) arg.remove(row);
					arg.put(row, (String) o); 
					break;
		}
		
		//set height of table
		if(row + 1 == height) {
			height = row + 2;
			
			fireTableDataChanged();
		}
		
		fireTableCellUpdated(row, column);
	}

	@Override
	public Object getValueAt(int row, int column) {
		switch(column) {
			case 0: return dmx.get(row);
			case 1: return osc.get(row);
			case 2: return arg.get(row);
			default: return null;
		}
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return true;
	}
	
	@Override
	public String getColumnName(int i) {
		return columns[i];
	}

}
