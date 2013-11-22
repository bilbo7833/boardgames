package com.arena.common;

import android.graphics.PointF;
import android.view.MotionEvent;

public class Touch {
    private float x;
    private float y;
    private float pressure = 0f;
    
    public Touch(float x, float y, float pressure) {
    	this.x = x;
        this.y = y;
        this.pressure = pressure;
    }
    
    public Touch(MotionEvent event) {
    	int index = event.getActionIndex();
    	this.x = event.getX(index);
        this.y = event.getY(index);
        this.pressure = event.getPressure(index);
    }
    
    public void setX(float x) {
    	this.x = x;
    	return;
    }
    

    public float getX() {
    	return this.x;
    }
    
    public void setY(float y) {
    	this.y = y;
    	return;
    }
    
    public float getY() {    
    	return this.y;
    }

    public void setPressure(float pressure) {
    	this.pressure = pressure;
    	return;
    }
    
    public float getPressure() {
    	return this.pressure;
    }    
    
    public void setTouch(float x, float y, float pressure) {
        this.x = x;
        this.y = y;
        this.pressure = pressure;
    }
    
    public PointF getCoordinates() {
    	return new PointF(this.x, this.y);
    }
}
