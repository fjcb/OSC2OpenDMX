package com.fjacob.osc2dmx.main;

import javax.swing.UIManager;

import com.fjacob.osc2dmx.controller.Controller;
import com.fjacob.osc2dmx.controller.ControllerInterface;
import com.fjacob.osc2dmx.model.AbstractModel;
import com.fjacob.osc2dmx.model.Model;
import com.fjacob.osc2dmx.util.ExceptionLog;
import com.fjacob.osc2dmx.view.View;

public class MainOsc2Dmx {

	public static void main(String[] args) {
		//status message in console
		System.out.println(" starting Osc2Dmx...");
		
		//set look and feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			ExceptionLog.log("MainOsc2Dmx", e.toString());
		} 
		
		//create model, controller and view
		AbstractModel model = new Model();
		ControllerInterface controller = new Controller(model);
		View view = new View(controller, model);
		
		//set everything visible
		view.setVisible(true);
	}

}
