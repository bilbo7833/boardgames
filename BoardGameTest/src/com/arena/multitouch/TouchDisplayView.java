/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.arena.multitouch;

import java.text.DecimalFormat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.app.Activity;

import com.arena.common.Pawn;
import com.arena.common.Pawn.Status;

/**
 * View that shows touch events and their history. This view demonstrates the
 * use of {@link #onTouchEvent(MotionEvent)} and {@link MotionEvent}s to keep
 * track of touch pointers across events.
 */
public class TouchDisplayView extends View {
	
	private enum LastAction {
		START, SELECT, MOVE_SUCCESS, MOVE_ERROR
	}
	
    private static final int MAX_DISTANCE = 75;
    private static final int MAX_NUMBER_OF_PAWNS = 5;

    // Hold data for active touch pointer IDs
    private SparseArray<Pawn> mTouches;
    
    // Variable used to back up position before moving
    private Pawn backupPawn;

    // Is there an active touch?
    private boolean mHasTouch = false;
    
    // Is there a valid touch?
    private boolean mValidTouch = false;
    
    private LastAction lastAction = LastAction.START;
    
    public int moveDialogResult = -1;

    /**
     * Holds data related to a touch pointer, including its current position,
     * pressure and historical positions. Objects are allocated through an
     * object pool using {@link #obtain()} and {@link #recycle()} to reuse
     * existing objects.
     */


    public TouchDisplayView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // SparseArray for touch events, indexed by pawn id
        mTouches = new SparseArray<Pawn>(10);

        initialisePaint();
    }


    private void eventActionDown(MotionEvent event) {

    	final int action = event.getAction();
    	
		// extract index from MotionEvent
		int id;
		int index = -1;
	
		if ((action & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
			/*
			 * Only one touch event is stored in the MotionEvent. Extract
			 * the pointer identifier of this touch from the first index
			 * within the MotionEvent object.
			 */
			index = 0;
		}
		else if ((action & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_DOWN) {
	        /*
	         * The MotionEvent object contains multiple pointers. Need to
	         * extract the index at which the data for this particular event
	         * is stored.
	         */
			index = event.getActionIndex();
		}
		
		// get corresponding pointer id
		id = event.getPointerId(index);
		Pawn data = new Pawn(event.getX(index), event.getY(index),
				event.getPressure(index), Status.ACTIVE);
		
		// check whether touch refers to a new or existing pawn
		int closestId = -1;
		float distance = 10000f;
		mValidTouch = true;
		
		if (mTouches.size()>0) {
			closestId = getClosestTouchpointID(data.getCoordinates());
			Pawn closestPawn = mTouches.get(closestId);
	        distance = distance(data.getCoordinates(), closestPawn.getCoordinates());
	      
	      if (distance < MAX_DISTANCE) {
	    	  id = closestId;
	    	  data.setTouch(closestPawn.x, closestPawn.y, closestPawn.pressure);
	    	  if (mTouches.get(closestId).getStatus() == Status.MOBILE)
	    		  data.setStatus(Status.MOBILE);
	      }
	      else if (mTouches.size() < MAX_NUMBER_OF_PAWNS) {
	    	  id = mTouches.size();
	    	  data.setStatus(Status.ACTIVE);
	      }
	      else
	    	  mValidTouch = false;
	    }
		
		if (mValidTouch) {
			// deactivate all pawns and then insert the active pawn 
			deactivateAllPawns();
			data.setLabel("id: " + id);
			mTouches.put(id, data);
		}
	
	    mHasTouch = true;
	    lastAction = LastAction.SELECT;
	    return;
    }
    
    
    private void eventActionMove(MotionEvent event) {

        /*
         * Loop through all active pointers contained within this event.
         * Data for each pointer is stored in a MotionEvent at an index
         * (starting from 0 up to the number of active pointers). This
         * loop goes through each of these active pointers, extracts its
         * data (position and pressure) and updates its stored data. A
         * pointer is identified by its pointer number which stays
         * constant across touch events as long as it remains active.
         * This identifier is used to keep track of a pointer across
         * events.
         */
        for (int index = 0; index < event.getPointerCount(); index++) {

        	// get pawn id closest to actual touch
            Pawn data = new Pawn(event.getX(index), event.getY(index),
        			event.getPressure(index));	
        	int id = getClosestTouchpointID(data.getCoordinates());
        	
        	if (mValidTouch) {
        		// get the data stored externally about this pointer.
                data = mTouches.get(id);
                lastAction = LastAction.MOVE_ERROR;

                // if the active pawn is moved set new values
                if (data.getStatus() == Status.MOBILE) {
                	data.setTouch(event.getX(index), event.getY(index),
                			event.getPressure(index));
                	lastAction = LastAction.MOVE_SUCCESS;
                }
        	}
        }
        return;
    }
    
    
    private void eventActionUp(MotionEvent event) {
        /*
         * Extract the pointer identifier for the only event stored in
         * the MotionEvent object and remove it from the list of active
         * touches.
         */
    	if (lastAction == LastAction.SELECT) {
    		backupActivePawn();
    		makeActivePawnsMobile();
    	}
    	if (lastAction == LastAction.MOVE_SUCCESS) {
    		// Open move dialog and ask for confirmation
    		((MainActivity) getContext()).moveDialog();
    	}
    	return;	
    }
    
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {

    	final int action = event.getAction();

        /*
         * Switch on the action. The action is extracted from the event by
         * applying the MotionEvent.ACTION_MASK. Alternatively a call to
         * event.getActionMasked() would yield in the action as well.
         */
        switch (action & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN: {
            	/* ACTION_DOWN: first pressed gesture has started
            	 *
    	         * ACTION_POINTER_DOWN: A non-primary pointer has gone down, after an
    	         * event for the primary pointer (ACTION_DOWN) has already been received.
    	         */
            	
            	eventActionDown(event);
                break;
            }
            
            case MotionEvent.ACTION_MOVE: {
                /*
                 * A change event happened during a pressed gesture. (Between ACTION_DOWN
                 * and ACTION_UP or ACTION_POINTER_DOWN and ACTION_POINTER_UP)
                 */
            	
            	eventActionMove(event);                
                break;
            }

            case MotionEvent.ACTION_UP: {
                /*
                 * Final pointer has gone up and has ended the last pressed gesture.
                 */

            	eventActionUp(event);
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
            	/*
                 * A non-primary pointer has gone up and other pointers are still active.
                 */

                break;
            }
        }

        // trigger redraw on UI thread
        this.postInvalidate();

        return true;
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Canvas background color depends on whether there is an active touch
        if (mHasTouch) {
            canvas.drawColor(BACKGROUND_ACTIVE);
        } else {
            // draw inactive border
            canvas.drawRect(mBorderWidth, mBorderWidth, getWidth() - mBorderWidth, getHeight()
                    - mBorderWidth, mBorderPaint);
        }

        // loop through all active touches and draw them
        for (int i = 0; i < mTouches.size(); i++) {

            // get the pointer id and associated data for this index
            int id = mTouches.keyAt(i);
            Pawn data = mTouches.valueAt(i);

            // draw the data to the canvas
            drawCircle(canvas, id, data);
            drawLegend(canvas, id, data);
        }
    }

    /*
     * Below are only helper methods and variables required for drawing.
     */

    // radius of active touch circle in dp
    private static final float CIRCLE_RADIUS_DP = 200f;

    // calculated radiuses in px
    private float mCircleRadius;

    private Paint mCirclePaint = new Paint();
    private Paint mTextPaint = new Paint();

    private static final int BACKGROUND_ACTIVE = Color.WHITE;

    // inactive border
    private static final float INACTIVE_BORDER_DP = 15f;
    private static final int INACTIVE_BORDER_COLOR = 0xFFffd060;
    private Paint mBorderPaint = new Paint();
    private float mBorderWidth;

    public final int[] COLORS = {
            0xFF33B5E5, 0xFFAA66CC, 0xFF99CC00, 0xFFFFBB33, 0xFFFF4444,
            0xFF0099CC, 0xFF9933CC, 0xFF669900, 0xFFFF8800, 0xFFCC0000
    };
    
    public DecimalFormat XY_FORMAT = new DecimalFormat("#0");
    public DecimalFormat PRESSURE_FORMAT = new DecimalFormat("#0.00");

    /**
     * Sets up the required {@link Paint} objects for the screen density of this
     * device.
     */
    private void initialisePaint() {

        // Calculate radiuses in px from dp based on screen density
        float density = getResources().getDisplayMetrics().density;
        mCircleRadius = CIRCLE_RADIUS_DP * density;

        // Setup text paint for circle label
        mTextPaint.setTextSize(27f);
        mTextPaint.setColor(Color.BLACK);

        // Setup paint for inactive border
        mBorderWidth = INACTIVE_BORDER_DP * density;
        mBorderPaint.setStrokeWidth(mBorderWidth);
        mBorderPaint.setColor(INACTIVE_BORDER_COLOR);
        mBorderPaint.setStyle(Paint.Style.STROKE);

    }

    /**
     * Draws the data encapsulated by a {@link TouchHistory} object to a canvas.
     * A large circle indicates the current position held by the
     * {@link TouchHistory} object, while a smaller circle is drawn for each
     * entry in its history. The size of the large circle is scaled depending on
     * its pressure, clamped to a maximum of <code>1.0</code>.
     *
     * @param canvas
     * @param id
     * @param data
     */
    protected void drawCircle(Canvas canvas, int id, Pawn data) {
        // select the color based on the status of a pawn
    	Status status = data.getStatus();
    	int color = 0;
    	if (status == Status.INACTIVE)
    		color = COLORS[0];
    	else if (status == Status.ACTIVE)
    		color = COLORS[1];
    	else if (status == Status.MOBILE)
    		color = COLORS[2];

        mCirclePaint.setColor(color);
        mTextPaint.setColor(color);

        /*
         * Draw the circle, size scaled to its pressure. Pressure is clamped to
         * 1.0 max to ensure proper drawing. (Reported pressure values can
         * exceed 1.0, depending on the calibration of the touch screen).
         */
        float pressure = Math.min(data.pressure, 1f);
        float radius = pressure * mCircleRadius;

        canvas.drawCircle(data.x, (data.y) - (radius / 2f), radius,
                mCirclePaint);

        // draw its label next to the main circle
        canvas.drawText(data.getLabel()
//        		 + " - " + PRESSURE_FORMAT.format(data.pressure)
        		+ " - (" + XY_FORMAT.format(data.x) + "," + XY_FORMAT.format(data.y)
                + ")" , data.x + radius, data.y - radius, mTextPaint);
    }
    
    protected void drawLegend(Canvas canvas, int id, Pawn data) {
        // select the color based on the status of a pawn
    	Status status = data.getStatus();
    	int color = 0;
    	if (status == Status.INACTIVE)
    		color = COLORS[0];
    	else if (status == Status.ACTIVE)
    		color = COLORS[1];
    	else if (status == Status.MOBILE)
    		color = COLORS[2];
   		mTextPaint.setColor(color);
   		
        // write legend
        String entry = "id: " + id
        		+ ", x: " + XY_FORMAT.format(data.x)
        		+ ", y: " + XY_FORMAT.format(data.y);
        	canvas.drawText(entry, 10, 30*id + 25, mTextPaint);
    }

    
    /**
     * 
     *
     * @param x
     * @param y
     * @return
     */    
    private int getClosestTouchpointID(PointF p) {
    	int closestID = -1;
    	float distance = 1000000;
    	
    	for (int i = 0; i < mTouches.size(); i++) {
            // get the pointer id and associated data for this index
            int id = mTouches.keyAt(i);
            Pawn data = mTouches.valueAt(i);
            
            float d = distance(p, data.getCoordinates());
            if (d < distance) {
            	closestID = id;
            	distance = d;
            }
    	}

    	return closestID;
    }
    
    private static float distance(PointF p, PointF q) {
    	return PointF.length(p.x - q.x, p.y - q.y);
    }
    
    /** deactivateAllPawns simply changes the 'active'
     * attribute for all pawns to 'false'.
     */
    public void deactivateAllPawns() {
    	for (int i = 0; i < mTouches.size(); i++) {
    		int id = mTouches.keyAt(i);
    		Pawn data = mTouches.valueAt(i);
    		data.setStatus(Status.INACTIVE);
    		mTouches.put(id, data);
    	}
    }
    
    public void makeActivePawnsMobile() {
    	for (int i = 0; i < mTouches.size(); i++) {
    		int id = mTouches.keyAt(i);
    		Pawn data = mTouches.valueAt(i);
    		if (data.getStatus() == Status.ACTIVE) {
    			data.setStatus(Status.MOBILE);
    			mTouches.put(id, data);
    		}
    	}
    }
    
    public boolean backupActivePawn() {
    	for (int i = 0; i < mTouches.size(); i++) {
    		backupPawn = mTouches.valueAt(i);
    		if (backupPawn.getStatus() == Status.ACTIVE) {
    			Log.d("backupActivePawn", "saved pawn: id " + backupPawn.getLabel() + ",x = "
    					+ backupPawn.getCoordinates().x + ",y = " + backupPawn.getCoordinates().y);
    			return true;
    		}
    	}
    	return false;
    }
    
    public boolean reverseMobilePawn() {
    	for (int i = 0; i < mTouches.size(); i++) {
    		int id = mTouches.keyAt(i);
    		Pawn pawn = mTouches.valueAt(i);
    		pawn.logDebug("backupActivePawn");
    		if (pawn.getStatus() == Status.MOBILE) {		
    			mTouches.put(id, backupPawn);
    			Log.d("backupActivePawn", "restored pawn");
    			return true;
    		}
    	}
    	return false;
    }
}
