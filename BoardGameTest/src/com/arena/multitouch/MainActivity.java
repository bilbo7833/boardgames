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

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;

import com.android.example.input.multitouch.basicMultitouch.R;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.layout_mainactivity);
    }
    
	@Override
	public void onMoveDialogYes(DialogFragment dialog) {
		// Yes
		Log.d("move dialog", "Yes");
		TouchDisplayView view = (TouchDisplayView) findViewById(R.id.touch_display_view);
    	view.deactivateAllPawns();
		return;
	}
	
	@Override
	public void onMoveDialogNo(DialogFragment dialog) {
		// No
		Log.d("move dialog", "No");
		TouchDisplayView view = (TouchDisplayView) findViewById(R.id.touch_display_view);
    	view.reverseMobilePawn();
    	view.postInvalidate();
		return;
	}
    
    public void moveDialog() {
    	DialogFragment moveDialog = new MoveDialog();
		moveDialog.show(getSupportFragmentManager(), "move");
    }
}
