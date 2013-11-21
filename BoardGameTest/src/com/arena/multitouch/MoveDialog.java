package com.arena.multitouch;

import com.android.example.input.multitouch.basicMultitouch.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;


public class MoveDialog extends DialogFragment {
	
	public interface MoveDialogListener {
		public void onMoveDialogYes(DialogFragment dialog);
		public void onMoveDialogNo(DialogFragment dialog);
	}
	
	MoveDialogListener mListener;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (MoveDialogListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement MoveDialogListener");
		}
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use dialog builder to create dialog
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
		
		dialogBuilder.setTitle(R.string.move_dialog_title)
			.setMessage(R.string.move_dialog_message)
			.setNegativeButton(R.string.move_dialog_no, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					// No
					mListener.onMoveDialogNo(MoveDialog.this);
				}
			})
			.setPositiveButton(R.string.move_dialog_yes, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					// Yes
					mListener.onMoveDialogYes(MoveDialog.this);
				}
			});
		
		AlertDialog moveDialog = dialogBuilder.create();
		
		return moveDialog;
	}
}
