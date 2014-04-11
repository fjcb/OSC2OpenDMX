package com.fjacob.osc2dmx.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.ListSelectionModel;
import javax.swing.JLabel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.GridLayout;
import javax.swing.JList;
import java.awt.Toolkit;
import java.io.File;
import java.util.Map;

import com.fjacob.osc2dmx.controller.ControllerInterface;
import com.fjacob.osc2dmx.model.DmxObserver;
import com.fjacob.osc2dmx.model.AbstractModel;
import com.fjacob.osc2dmx.model.OscObserver;
import com.fjacob.osc2dmx.model.StatusObserver;

public class View extends JFrame implements ActionListener, TableModelListener, StatusObserver, OscObserver, DmxObserver {
	
	private static final long serialVersionUID = -4577727165038039657L;
	
	private AbstractModel model;
	private ControllerInterface controller;
	
	//swing components
	private JTabbedPane tabbedPane;
	private JPanel panelOscTab;
	private JPanel panelDmxTab;
	private JPanel panelAboutTab;
	private JPanel panelMainTab;
	private JPanel panelMainCtrl;
	private JPanel panelStatus;
	private JPanel panelDmx;
	private JPanel panelOsc;
	private JButton btnConnect;
	private JButton btnTest;
	private JButton btnResetDmx;
	private JButton btnStart;
	private JButton btnStop;
	private JTextArea statusTextArea;
	private JScrollPane scrollStatus;
	private JPanel panelDmxInfo;
	private JScrollPane scrollDmxInfo;
	private JTable dmxTable;
	private JPanel panelNetwork;
	private JPanel panelAddresses;
	private JLabel lblLocalIp;
	private JTextField portInTextField;
	private JLabel lblPortIncomming;
	private JTextField portOutTextField;
	private JLabel lblPortOutgoing;
	private JTextField clientIpTextField;
	private JScrollPane oscTableScrollPane;
	private JPanel oscFilePanel;
	private JButton btnOpen;
	private JButton btnSaveAs;
	private JFileChooser fileChooser;
	private JTable oscDmxTable;
	private OscDmxTableModel oscDmxTableModel;
	private JButton btnReset;
	private JList clientList;
	private DefaultListModel clientListModel;
	private JCheckBox checkBroadcast;
	private JButton btnAdd;
	private JButton btnRemove;
	private JLabel lblAbout;
	
	public View(ControllerInterface c, AbstractModel m) {
		//setting the superclass constructor
		super();
		
		//create the gui
		createGui();
		
		this.controller = c;
		this.model = m;
		
		this.controller.setView(this);
		
		//register observers on model
		this.model.registerDmxObserver(this);
		this.model.registerOscObserver(this);
		this.model.registerStatusObserver(this);
	}

	/*
	 * initializes the GUI
	 */
	public void createGui() {
		this.setIconImage(Toolkit.getDefaultToolkit().getImage("res/img/icon.png"));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("OSC2OpenDMX");
		this.setBounds(100, 100, 580, 480);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		this.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		//main tab
		panelMainTab = new JPanel();
		tabbedPane.addTab("Main", null, panelMainTab, null);
		panelMainTab.setLayout(new BorderLayout(0, 0));
		
		panelMainCtrl = new JPanel();
		panelMainTab.add(panelMainCtrl, BorderLayout.NORTH);
		panelMainCtrl.setLayout(new BoxLayout(panelMainCtrl, BoxLayout.X_AXIS));
		
		panelDmx = new JPanel();
		panelDmx.setBorder(new TitledBorder(null, "OpenDmx", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelMainCtrl.add(panelDmx);
		panelDmx.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		btnConnect = new JButton("connect");
		btnConnect.addActionListener(this);
		panelDmx.add(btnConnect);
		
		btnTest = new JButton("test");
		btnTest.addActionListener(this);
		panelDmx.add(btnTest);
		
		btnResetDmx = new JButton("reset");
		btnResetDmx.addActionListener(this);
		panelDmx.add(btnResetDmx);
		
		panelOsc = new JPanel();
		panelOsc.setBorder(new TitledBorder(null, "OSC Host", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelMainCtrl.add(panelOsc);
		panelOsc.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		btnStart = new JButton("start");
		btnStart.addActionListener(this);
		panelOsc.add(btnStart);
		
		btnStop = new JButton("stop");
		btnStop.addActionListener(this);
		btnStop.setEnabled(false);
		panelOsc.add(btnStop);
		
		panelStatus = new JPanel();
		panelStatus.setBorder(new TitledBorder(null, "Status", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelMainTab.add(panelStatus, BorderLayout.CENTER);
		panelStatus.setLayout(new BoxLayout(panelStatus, BoxLayout.X_AXIS));
		
		statusTextArea = new JTextArea();
		statusTextArea.setEditable(false);
		scrollStatus = new JScrollPane(statusTextArea);
		scrollStatus.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollStatus.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		panelStatus.add(scrollStatus);
		
		//osc tab
		panelOscTab = new JPanel();
		tabbedPane.addTab("Config", null, panelOscTab, null);
		panelOscTab.setLayout(new BorderLayout(0, 0));
		
		panelNetwork = new JPanel();
		panelNetwork.setBorder(new TitledBorder(null, "Network Settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelOscTab.add(panelNetwork, BorderLayout.NORTH);
		panelNetwork.setLayout(new GridLayout(0, 3, 0, 0));
		
		lblLocalIp = new JLabel("Local IP");
		lblLocalIp.setHorizontalAlignment(SwingConstants.LEFT);
		panelNetwork.add(lblLocalIp);
		
		lblPortIncomming = new JLabel("Port (incomming) ");
		lblPortIncomming.setHorizontalAlignment(SwingConstants.RIGHT);
		panelNetwork.add(lblPortIncomming);
		
		portInTextField = new JTextField();
		portInTextField.addActionListener(this);
		panelNetwork.add(portInTextField);
		portInTextField.setColumns(4);
		
		checkBroadcast = new JCheckBox("broadcast incomming data");
		checkBroadcast.addActionListener(this);
		panelNetwork.add(checkBroadcast);
		
		lblPortOutgoing = new JLabel("Port (outgoing) ");
		lblPortOutgoing.setHorizontalAlignment(SwingConstants.RIGHT);
		panelNetwork.add(lblPortOutgoing);
		
		portOutTextField = new JTextField();
		portOutTextField.addActionListener(this);
		panelNetwork.add(portOutTextField);
		portOutTextField.setColumns(4);
		
		panelAddresses = new JPanel();
		panelOscTab.add(panelAddresses, BorderLayout.CENTER);
		panelAddresses.setLayout(new GridLayout(1, 0, 0, 0));
		
		JPanel oscTablePanel = new JPanel();
		oscTablePanel.setBorder(new TitledBorder(null, "Softpatch", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelAddresses.add(oscTablePanel);
		GridBagLayout gbl_oscTablePanel = new GridBagLayout();
		gbl_oscTablePanel.columnWidths = new int[]{255, 0};
		gbl_oscTablePanel.rowHeights = new int[]{140, 140, 0, 0, 0};
		gbl_oscTablePanel.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_oscTablePanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		oscTablePanel.setLayout(gbl_oscTablePanel);
		
		oscTableScrollPane = new JScrollPane();
		GridBagConstraints gbc_oscTableScrollPane = new GridBagConstraints();
		gbc_oscTableScrollPane.gridheight = 3;
		gbc_oscTableScrollPane.weightx = 1.0;
		gbc_oscTableScrollPane.weighty = 1.0;
		gbc_oscTableScrollPane.anchor = GridBagConstraints.NORTH;
		gbc_oscTableScrollPane.fill = GridBagConstraints.BOTH;
		gbc_oscTableScrollPane.gridx = 0;
		gbc_oscTableScrollPane.gridy = 0;
		oscTablePanel.add(oscTableScrollPane, gbc_oscTableScrollPane);
		
		
		oscDmxTableModel = new OscDmxTableModel();
		oscDmxTableModel.addTableModelListener(this);
		oscDmxTable = new JTable(oscDmxTableModel);
		oscDmxTable.setCellSelectionEnabled(true);
		
		oscTableScrollPane.setViewportView(oscDmxTable);
		
		oscFilePanel = new JPanel();
		GridBagConstraints gbc_oscFilePanel = new GridBagConstraints();
		gbc_oscFilePanel.anchor = GridBagConstraints.SOUTH;
		gbc_oscFilePanel.fill = GridBagConstraints.BOTH;
		gbc_oscFilePanel.gridx = 0;
		gbc_oscFilePanel.gridy = 3;
		oscTablePanel.add(oscFilePanel, gbc_oscFilePanel);
		oscFilePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		btnOpen = new JButton("Open");
		btnOpen.addActionListener(this);
		oscFilePanel.add(btnOpen);
		
		btnSaveAs = new JButton("Save As");
		btnSaveAs.addActionListener(this);
		oscFilePanel.add(btnSaveAs);
		
		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setFileFilter(new FileFilter() {	
			//you can save and load your data to/from xml-files
			private String extension = "xml";
			
			@Override
			public boolean accept(File f) {
				if(f.isDirectory()) return true;
				if(f.getName().toLowerCase().endsWith(extension)) return true;
				return false;
			}

			@Override
			public String getDescription() {
				return extension;
			} });
		
		btnReset = new JButton("Reset");
		btnReset.addActionListener(this);
		oscFilePanel.add(btnReset);
		
		JPanel oscClientsPanel = new JPanel();
		oscClientsPanel.setBorder(new TitledBorder(null, "Clients", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelAddresses.add(oscClientsPanel);
		GridBagLayout gbl_oscClientsPanel = new GridBagLayout();
		gbl_oscClientsPanel.columnWidths = new int[]{255, 0};
		gbl_oscClientsPanel.rowHeights = new int[]{140, 140, 0, 0, 0};
		gbl_oscClientsPanel.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_oscClientsPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		oscClientsPanel.setLayout(gbl_oscClientsPanel);
		
		JScrollPane clientScrollPane = new JScrollPane();
		GridBagConstraints gbc_clientScrollPane = new GridBagConstraints();
		gbc_clientScrollPane.weighty = 1.0;
		gbc_clientScrollPane.weightx = 1.0;
		gbc_clientScrollPane.gridheight = 3;
		gbc_clientScrollPane.fill = GridBagConstraints.BOTH;
		gbc_clientScrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_clientScrollPane.gridx = 0;
		gbc_clientScrollPane.gridy = 0;
		oscClientsPanel.add(clientScrollPane, gbc_clientScrollPane);
		
		clientList = new JList();
		clientListModel = new DefaultListModel();
		clientList.setModel(clientListModel);
		clientScrollPane.setViewportView(clientList);
		
		JPanel clientCtrlPanel = new JPanel();
		GridBagConstraints gbc_clientCtrlPanel = new GridBagConstraints();
		gbc_clientCtrlPanel.fill = GridBagConstraints.BOTH;
		gbc_clientCtrlPanel.gridx = 0;
		gbc_clientCtrlPanel.gridy = 3;
		oscClientsPanel.add(clientCtrlPanel, gbc_clientCtrlPanel);
		clientCtrlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		clientIpTextField = new JTextField();
		clientCtrlPanel.add(clientIpTextField);
		clientIpTextField.setColumns(10);
		
		btnAdd = new JButton("Add");
		btnAdd.addActionListener(this);
		clientCtrlPanel.add(btnAdd);
		
		btnRemove = new JButton("Remove");
		btnRemove.addActionListener(this);
		clientCtrlPanel.add(btnRemove);
		
		
		//dmx tab
		panelDmxTab = new JPanel();
		tabbedPane.addTab("DMX", null, panelDmxTab, null);
		panelDmxTab.setLayout(new BorderLayout(0, 0));
		
		panelDmxInfo = new JPanel();
		panelDmxInfo.setBorder(new TitledBorder(null, "DMX Channel Data", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelDmxTab.add(panelDmxInfo);
		panelDmxInfo.setLayout(new BorderLayout(0, 0));
		
		dmxTable = new JTable();
		dmxTable.setModel(new DefaultTableModel(
			new String[] {
				"DMX", "Data", "DMX", "Data", "DMX", "Data", "DMX", "Data", "DMX", "Data", "DMX", "Data", "DMX", "Data", "DMX", "Data"
			},
			64 //rows
		));
		dmxTable.getColumnModel().getColumn(0).setResizable(false);
		dmxTable.setEnabled(false);
		dmxTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		dmxTable.setRowSelectionAllowed(false);
		scrollDmxInfo = new JScrollPane(dmxTable);
		scrollDmxInfo.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollDmxInfo.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		panelDmxInfo.add(scrollDmxInfo);
		
		panelAboutTab = new JPanel();
		lblAbout = new JLabel(	"<html><center><h1>OSC2OpenDMX</h1><br>"+
								"<i>Bachelor-Project</i><br><br>"+
								"Fritz Jacob<br><br>"+
								"HAW-Hamburg<br>"+
								"Department DMI<br>"+
								"Media Systems</center></html>");
		panelAboutTab.add(lblAbout);
		tabbedPane.addTab("About", null, panelAboutTab, null);
	}
	
	public void updateOscTable(int column, int row, String data) {
		oscDmxTableModel.updateValueAt(data, row, column);
	}
	
	//interface methods
	//-----------------------------------------------------------------------------
	//action listener
	@Override
	public void actionPerformed(ActionEvent ae) {
		//main tab
		if(ae.getSource() == btnConnect) {
			controller.connectDmx();
		}
		if(ae.getSource() == btnTest) {
			controller.testDmx();
		}
		if(ae.getSource() == btnResetDmx) {
			controller.resetDmx();
		}
		if(ae.getSource() == btnStart) {
			controller.startOscHost();
		}
		if(ae.getSource() == btnStop) {
			controller.stopOscHost();
		}
		
		//osctab
		if(ae.getSource() == portInTextField) {
			controller.setInPort(portInTextField.getText());
		}
		if(ae.getSource() == portOutTextField) {
			controller.setOutPort(portOutTextField.getText());
		}
		if(ae.getSource() == checkBroadcast) {
			if(checkBroadcast.isSelected()) {
				controller.enableBroadcast();
			} else {
				controller.disableBroadcast();
			}
		}
		if(ae.getSource() == btnOpen) {
			int returnValue = fileChooser.showOpenDialog(View.this);
			if(returnValue == JFileChooser.APPROVE_OPTION) {
				controller.openOscDmxTable(fileChooser.getSelectedFile().getAbsolutePath());
			}
		}
		if(ae.getSource() == btnSaveAs) {
			int returnValue = fileChooser.showSaveDialog(View.this);
			if(returnValue == JFileChooser.APPROVE_OPTION) {
				controller.saveOscDmxTable(fileChooser.getSelectedFile().getAbsolutePath());
			}
		}
		if(ae.getSource() == btnReset) {
			controller.resetOscDmxTable();
		}
		if(ae.getSource() == btnAdd) {
			controller.addClient(clientIpTextField.getText());
			clientIpTextField.setText("");
			clientIpTextField.grabFocus();
		}
		if(ae.getSource() == btnRemove) {
			controller.removeClient((String)clientList.getSelectedValue());
		}	
	}
	
	@Override
	public void tableChanged(TableModelEvent e) {
		if(e.getSource() == oscDmxTableModel) {	
			int column = e.getColumn();
			int row = e.getFirstRow();
			
			if(column == TableModelEvent.ALL_COLUMNS) return;
			
			String input = (String) oscDmxTableModel.getValueAt(row, column);			
			controller.setOscAdress(column, row, input);		
		}	
	}
	
	//model observers
	@Override
	public void dmxDataUpdated(int[] data) {
		int COLS = 8;
		for(int i = 0; i < data.length; i++) {
			int col = i%COLS * 2;
			int row = (int) Math.ceil(i/COLS);
			dmxTable.getModel().setValueAt((i+1), row, col);
			dmxTable.getModel().setValueAt(data[i], row, col+1);
		}
	}
	
	//called if a xml was opened; writes data to the osc-dmx-table
	@Override
	public void oscDmxAddressesUpdated(Map<Integer, Integer> dmx, Map<Integer, String> osc, Map<Integer, Integer> arg) {
		oscDmxTableModel.restoreTableData(dmx, osc, arg);
	}

	//write a message line to the status console
	@Override
	public void recieveStatusMsg(String msg) {
		String text = statusTextArea.getText();
		statusTextArea.setText(text + msg+ "\n");
		statusTextArea.setCaretPosition(text.length());
	}

	@Override
	public void ipAddressUpdated(String ip) {
		lblLocalIp.setText("Local IP: " + ip);
	}

	@Override
	public void clientListUpdated(String[] clients) {	
		clientListModel.removeAllElements();
		for(int i = 0; i < clients.length; i++) {
			clientListModel.addElement(clients[i]);
		}
	}

	@Override
	public void dmxWidgetConnected() {
		btnConnect.setEnabled(false);
		btnTest.setEnabled(true);	
	}

	@Override
	public void oscHostStarted() {
		btnStart.setEnabled(false);
		btnStop.setEnabled(true);
	}

	@Override
	public void oscHostStopped() {
		btnStart.setEnabled(true);
		btnStop.setEnabled(false);	
	}

	@Override
	public void inPortUpdated(int port) {
		portInTextField.setText(Integer.toString(port));	
	}

	@Override
	public void outPortUpdated(int port) {
		portOutTextField.setText(Integer.toString(port));
	}

}
