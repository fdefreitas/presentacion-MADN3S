package org.madn3s.robot.common;

import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.OmniPilot;

import org.json.JSONObject;
import org.madn3s.io.BluetoothTunnel;

public class BluetoothControlled {
	
	private OmniPilot omniPilot;
	@SuppressWarnings("unused")
	private BluetoothTunnel bTunnel;
	@SuppressWarnings("unused")
	private UltrasonicSensor uSensor;
	
	public BluetoothControlled(OmniPilot omniPilot, UltrasonicSensor uSensor) {
		this.omniPilot = omniPilot;
		this.bTunnel = BluetoothTunnel.getInstance();
		this.uSensor = uSensor;
	}
	
	public boolean processMsg(JSONObject message){
		boolean result = false;
		try{
			String action =  message.getString("action");
			Utils.printToScreen(action,0,1,false);
			omniPilot.stop();
			if(message.has("speed")){
				omniPilot.setTravelSpeed(message.getInt("speed"));
			}
			if(action.equalsIgnoreCase("forward")){
				omniPilot.forward();
			} else if(action.equalsIgnoreCase("bacward")){
				omniPilot.backward();
			} else if(action.equalsIgnoreCase("left")){
				omniPilot.steer(15);
			} else if(action.equalsIgnoreCase("right")){
				omniPilot.steer(-15);
			}
		} catch (Exception e){
			Utils.printToScreen(e.getMessage());
		}
		return result;
	}
	

}
