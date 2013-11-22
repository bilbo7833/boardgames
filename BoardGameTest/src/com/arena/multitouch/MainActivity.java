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

import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;

import com.android.example.input.multitouch.basicMultitouch.R;
import com.arena.common.Pawn;
import com.arena.common.Pawn.Status;
import com.arena.multitouch.MoveDialog.MoveDialogListener;

/**
 * This is an example of keeping track of individual touches across multiple
 * {@link MotionEvent}s.
 * <p>
 * This is illustrated by a View ({@link TouchDisplayView}) that responds to
 * touch events and draws coloured circles for each pointer, stores the last
 * positions of this pointer and draws them. This example shows the relationship
 * between MotionEvent indices, pointer identifiers and actions.
 *
 * @see MotionEvent
 */
public class MainActivity extends FragmentActivity implements MoveDialogListener {

    private static final int MAX_DISTANCE = 75;
    private static final int MAX_NUMBER_OF_PAWNS = 5;
	
	private enum LastAction {
		START, SELECT, MOVE_SUCCESS, MOVE_ERROR
	}
	
    // Hold data for active touch pointer IDs
    private SparseArray<Pawn> mTouches;
    
    private SparseArray<Pawn> movingRanges;
    
    // Variable used to back up position before moving
    private Pawn backupPawn;
	
    private LastAction lastAction = LastAction.START;
    
    // Is there a valid touch?
    private boolean mValidTouch = false;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // SparseArray for touch events, indexed by pawn id
        mTouches = new SparseArray<Pawn>(MAX_NUMBER_OF_PAWNS);
        movingRanges = new SparseArray<Pawn>(MAX_NUMBER_OF_PAWNS);
        
        setContentView(R.layout.layout_mainactivity);
    }
    
	@Override
	public void onMoveDialogYes(DialogFragment dialog) {
		// Yes
		Log.d("move dialog", "Yes");
    	deactivateAllPawns();
    	movingRanges.clear();
    	updateTouchDisplayView();
		return;
	}
	
	@Override
	public void onMoveDialogNo(DialogFragment dialog) {
		// No
		Log.d("move dialog", "No");
    	reverseMobilePawn();
    	movingRanges.clear();
    	updateTouchDisplayView();
		return;
	}
    
    public void moveDialog() {
    	DialogFragment moveDialog = new MoveDialog();
		moveDialog.show(getSupportFragmentManager(), "move");
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

        // update touch_display_view
        updateTouchDisplayView();

        return true;
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
	    	  data.setTouch(closestPawn.getX(), closestPawn.getY(), closestPawn.getPressure());
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
	
		TouchDisplayView view = (TouchDisplayView) findViewById(R.id.touch_display_view);
	    view.mHasTouch = true;
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
    		removeInactiveMovingRanges();
    		createMovingRangesForActivePawns();
    		makeActivePawnsMobile();
    	}
    	if (lastAction == LastAction.MOVE_SUCCESS) {
    		// Open move dialog and ask for confirmation
    		moveDialog();
    	}
    	return;	
    }
    
    
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
    private void deactivateAllPawns() {
    	for (int i = 0; i < mTouches.size(); i++) {
    		int id = mTouches.keyAt(i);
    		Pawn data = mTouches.valueAt(i);
    		data.setStatus(Status.INACTIVE);
    		mTouches.put(id, data);
    	}
    }
    
    private void makeActivePawnsMobile() {
    	for (int i = 0; i < mTouches.size(); i++) {
    		int id = mTouches.keyAt(i);
    		Pawn data = mTouches.valueAt(i);
    		if (data.getStatus() == Status.ACTIVE) {
    			data.setStatus(Status.MOBILE);
    			mTouches.put(id, data);
    		}
    	}
    }
    
    private boolean backupActivePawn() {
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
    
    private boolean reverseMobilePawn() {
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
    
    private void removeInactiveMovingRanges() {
    	for (int i = 0; i < movingRanges.size(); i++) {
    		int id = movingRanges.keyAt(i);
    		if (mTouches.get(id).getStatus() == Status.INACTIVE)		
    			movingRanges.remove(id);
    	}
    	return;    	
    }
    
    private void createMovingRangesForActivePawns() {
    	for (int i = 0; i < mTouches.size(); i++) {
    		int id = mTouches.keyAt(i);
    		Pawn movingRange = mTouches.valueAt(i);
    		if (movingRange.getStatus() == Status.ACTIVE)		
    			movingRanges.put(id, movingRange);
    	}
    	return;
    }
    
    private void updateTouchDisplayView() {
    	TouchDisplayView view = (TouchDisplayView) findViewById(R.id.touch_display_view);
    	view.pawns = mTouches;
    	view.movingRanges = movingRanges;
		view.postInvalidate();
    }
}
