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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

import com.arena.board.Pawn;
import com.arena.board.Pawn.Status;

/**
 * View that shows touch events and their history. This view demonstrates the
 * use of {@link #onTouchEvent(MotionEvent)} and {@link MotionEvent}s to keep
 * track of touch pointers across events.
 */
public class TouchDisplayView extends View {
	
	/**
     * Holds data related to a touch pointer, including its current position
     * and pressure.
     */
	public SparseArray<Pawn> pawns;
	
	public SparseArray<Pawn> movingRanges;

    // Is there an active touch?
    public boolean mHasTouch = false;

    public TouchDisplayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        pawns = new SparseArray<Pawn>(100);
        movingRanges = new SparseArray<Pawn>(100);
        initialisePaint();
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

        /* draw moving range first for active pawns to make sure that
         * moving range is always in the background */
        for (int i = 0; i < movingRanges.size(); i++) {
            // get the pointer id and associated data for this index
            int id = movingRanges.keyAt(i);
            Pawn movingRange = movingRanges.valueAt(i);
            drawMovingRange(canvas, id, movingRange);
        }
        
        // loop through all pawns and draw them
        for (int i = 0; i < pawns.size(); i++) {
            // get the pointer id and associated data for this index
            int id = pawns.keyAt(i);
            Pawn pawn = pawns.valueAt(i);
            
            // draw the data to the canvas
            drawCircle(canvas, id, pawn);
            drawLegend(canvas, id, pawn);
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
        float height = getResources().getDisplayMetrics().heightPixels;
        float width = getResources().getDisplayMetrics().widthPixels;
        float ydpi = getResources().getDisplayMetrics().ydpi;
        float xdpi = getResources().getDisplayMetrics().xdpi;
        
        Log.d("initialisePaint", "height: " + height + ", width: " + width + ", ydpi:" + ydpi + ", xdpi: " + xdpi + ", density:" + density);
        Log.d("initialisePaint", "derived metrics - height: " + height / (ydpi * density) * 2.54 + ", width: " + width / (xdpi * density) * 2.54);
        
        //mCircleRadius = CIRCLE_RADIUS_DP * density;
        mCircleRadius = xdpi * density / 2; 

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
    protected void drawCircle(Canvas canvas, int id, Pawn pawn) {
        // select the color based on the status of a pawn
    	Status status = pawn.getStatus();
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
        float pressure = Math.min(pawn.getPressure(), 1f);
        //float radius = pressure * mCircleRadius;
        float radius = mCircleRadius;

        canvas.drawCircle(pawn.getX(), pawn.getY() - (radius / 2f), radius,
                mCirclePaint);

        // draw its label next to the main circle
        canvas.drawText(pawn.getLabel()
//        		 + " - " + PRESSURE_FORMAT.format(data.pressure)
        		+ " - (" + XY_FORMAT.format(pawn.getX()) + "," + XY_FORMAT.format(pawn.getY())
                + ")" , pawn.getX() + radius, pawn.getY() - radius, mTextPaint);
    }
    
    protected void drawMovingRange(Canvas canvas, int id, Pawn pawn) {
        // select the color based on the status of a pawn
    	int color = COLORS[3];

        mCirclePaint.setColor(color);

        float pressure = Math.min(pawn.getPressure(), 1f);
        float radius = pressure * mCircleRadius;

        canvas.drawCircle(pawn.getX(), pawn.getY() - (radius / 2f), 5 * radius,
                mCirclePaint);
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
        		+ ", x: " + XY_FORMAT.format(data.getX())
        		+ ", y: " + XY_FORMAT.format(data.getY());
        	canvas.drawText(entry, 10, 30*id + 25, mTextPaint);
    }      
}
