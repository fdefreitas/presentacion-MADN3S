package org.madn3s.robot.common;

import org.json.JSONObject;
import org.madn3s.io.BluetoothTunnel;

import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.OmniPilot;

public class Scanner {
	
	private OmniPilot omniPilot;
	private int points;
	private int travelSpeed;
	private double angle;
	private float radius = 9f;
	private float wheelDiameter = 4.8f;
	private double circumferenceRadius;
	private double distance;
	private double bias;
	private boolean last;
	private boolean move;
	private boolean finish = false;
	private BluetoothTunnel bTunnel;
	private UltrasonicSensor uSensor;
	
	public Scanner(OmniPilot omniPilot, int points, int travelSpeed, float radius, float wheelDiameter, UltrasonicSensor uSensor) {
		this.omniPilot = omniPilot;
		this.uSensor = uSensor;
		this.uSensor.continuous();
		this.points = points;
		this.travelSpeed = travelSpeed;
		this.angle = 360 / points;
		this.radius = radius;
		this.wheelDiameter = wheelDiameter;
		this.circumferenceRadius = this.uSensor.getDistance();
		this.last = false;
		this.move = false;
		this.finish = false;
		this.bias = 1;
		this.distance = Math.floor(2 * this.circumferenceRadius * Math.sin(Math.PI / points)) - bias;
		this.bTunnel = BluetoothTunnel.getInstance();
	}
	
	public OmniPilot getOmniPilot() {
		return omniPilot;
	}
	public void setOmniPilot(OmniPilot omniPilot) {
		this.omniPilot = omniPilot;
	}
	public int getPoints() {
		return points;
	}
	public void setPoints(int points) {
		this.points = points;
	}
	public int getTravelSpeed() {
		return travelSpeed;
	}
	public void setTravelSpeed(int travelSpeed) {
		this.travelSpeed = travelSpeed;
	}
	public double getAngle() {
		return angle;
	}
	public void setAngle(double angle) {
		this.angle = angle;
	}
	public float getRadius() {
		return radius;
	}
	public void setRadius(float radius) {
		this.radius = radius;
	}
	public float getWheelDiameter() {
		return wheelDiameter;
	}
	public void setWheelDiameter(float wheelDiameter) {
		this.wheelDiameter = wheelDiameter;
	}
	public double getCircumferenceRadius() {
		return circumferenceRadius;
	}
	public void setCircumferenceRadius(double circumferenceRadius) {
		this.circumferenceRadius = circumferenceRadius;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	
	public boolean processMsg(JSONObject message){
		boolean result = false;
		try{
			String action =  message.getString("action");
			Utils.printToScreen("action : " + action, 0,2, false);
//			Utils.printToScreen(action, 0, 1, false);
			if(action.equalsIgnoreCase("move")){
				move = true;
			} else if(action.equalsIgnoreCase("wait")){
				move = false;
			} else if(action.equalsIgnoreCase("finish")){
				finish = true;
			} else if(action.equalsIgnoreCase("config")){
//				circumferenceRadius = uSensor.getDistance();
				circumferenceRadius = message.getDouble("radius");
				move = finish = false;
				boolean calculate = false;
				if(message.has("points")){
					points =  message.getInt("points");
					calculate = true;
				}
				
				if(message.has("bias")){
					bias =  message.getDouble("bias");
					calculate = true;
				}
				
				if(message.has("speed")){
					travelSpeed = message.getInt("speed");
					omniPilot.setTravelSpeed(travelSpeed);
				}
				
//				if(calculate){
					distance =  Math.floor(2 * circumferenceRadius * Math.sin(Math.PI / points)) - bias;
//				}
				Utils.printToScreen("radius : " + circumferenceRadius, 0,3, false);
				Utils.printToScreen("bias : " + bias, 0,4, false);
				Utils.printToScreen("points : " + points, 0,5, false);
				Utils.printToScreen("speed : " + travelSpeed, 0,6, false);
				Utils.printToScreen("distance : " + distance, 0,7, false);
			}
			
			if(move){
				last = moveToNextPoint(action);
				if(!last){
					JSONObject response = new JSONObject();
					response.put("error", false);
					response.put("message", "PICTURE");
					bTunnel.writeMessage(response.toString());
				} else {
					JSONObject response = new JSONObject();
					response.put("error", false);
					response.put("message", "finish");
					bTunnel.writeMessage(response.toString());
				}
				move = false;
			}
			if(finish){
				JSONObject response = new JSONObject();
				response.put("error", false);
				response.put("message", "finish");
				bTunnel.writeMessage(response.toString());
				finish = false;
			}
		} catch (Exception e){
			Utils.printToScreen(e.getMessage());
		}
		return result;
	}
	
	private boolean moveToNextPoint(String msg) {
		if(msg.equalsIgnoreCase("finish")){
			return true;
		}
		//TODO: medimos y con el radio en 9 funciona casi perfecto, debe haber un pequeño error en la medicion
		omniPilot.travelArc(circumferenceRadius, distance, 90);
		return false;
	}
	

}
