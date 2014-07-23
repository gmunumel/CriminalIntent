package com.bignerdranch.android.criminalintent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;

public class ChooseDateTimeFragment extends DialogFragment {

	private Button mDateButton, mTimeButton;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		View v = getActivity().getLayoutInflater()
				.inflate(R.layout.dialog_date_time, null);
        mDateButton = (Button)v.findViewById(R.id.choose_date_button);
        
        
        
        mTimeButton = (Button)v.findViewById(R.id.choose_time_button);

		return new AlertDialog.Builder(getActivity())
			.setView(v)
			.setTitle(R.string.choose_date_time)
			.setPositiveButton(android.R.string.ok, null)
			.create();
	}
}
