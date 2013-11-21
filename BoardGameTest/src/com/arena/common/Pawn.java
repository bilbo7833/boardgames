package com.arena.common;

import android.util.Log;

public class Pawn extends Touch {
	
	public enum Status {
    	INACTIVE, ACTIVE, MOBILE
    }
	
    private String label = null;
    private Status status;

    public Pawn(float x, float y, float pressure) {
    	super(x, y, pressure);
    	this.status = Status.INACTIVE;
    }
    
    public Pawn(float x, float y, float pressure, Status status) {
    	super(x, y, pressure);
    	this.status = status;
    }
    
    public void setStatus(Status status) {
    	this.status = status;
    }
    
    public Status getStatus() {
    	return this.status;
    }
    
    public void setLabel(String label) {
    	this.label = label;
    }
    
    public String getLabel() {
    	return this.label;
    }
    
    public String getStatusString() {
        String statusString = "";
        switch (this.status) {
        case ACTIVE: 
        	statusString = "active";
        	break;
        case INACTIVE:
        	statusString = "inactive";
        	break;
        case MOBILE:
        	statusString = "mobile";
        	break;
        }
        return statusString;
    }
    
    public void logDebug(String tag) {
    	Log.d(tag, "pawn - " + this.getLabel() + ",x: " + this.x 
    			+ ",y: " + this.y + ",status: " + this.status);
    	return;
    }
    
}
