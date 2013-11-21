package com.arena.common;

import android.graphics.PointF;
import android.view.MotionEvent;

public class Touch {
    public float x;
    public float y;
    public float pressure = 0f;
    
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
    
    public void setTouch(float x, float y, float pressure) {
        this.x = x;
        this.y = y;
        this.pressure = pressure;
    }
    
    public PointF getCoordinates() {
    	return new PointF(this.x, this.y);
    }
}
