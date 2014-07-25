package com.bignerdranch.android.criminalintent;

import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Button;

public class ChooseDateTimeFragment extends DialogFragment {
	public static final String EXTRA_DATE =
			"com.bignerdranch.android.criminalintent.date";

	private static final String DIALOG_DATE = "date";
	private static final int REQUEST_DATE = 0;

	private Button mDateButton, mTimeButton;
	private Date mDate; 

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		mDate = (Date)getArguments().getSerializable(EXTRA_DATE);

		View v = getActivity().getLayoutInflater()
				.inflate(R.layout.dialog_date_time, null);
        mDateButton = (Button)v.findViewById(R.id.choose_date_button);
        mDateButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		FragmentManager fm = getActivity()
        				.getSupportFragmentManager();
        		DatePickerFragment dialog = DatePickerFragment
        				.newInstance(mDate);
        		dialog.setTargetFragment(ChooseDateTimeFragment.this, REQUEST_DATE);
        		dialog.show(fm, DIALOG_DATE);
        	}
        });
        
        mTimeButton = (Button)v.findViewById(R.id.choose_time_button);

		return new AlertDialog.Builder(getActivity())
			.setView(v)
			.setTitle(R.string.choose_date_time)
			.setPositiveButton(android.R.string.ok, null)
			.create();
	}

	public static ChooseDateTimeFragment newInstance(Date date) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_DATE, date);
		
		ChooseDateTimeFragment fragment = new ChooseDateTimeFragment();
		fragment.setArguments(args);
		
		return fragment;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (resultCode != Activity.RESULT_OK) return;
	    if (requestCode == REQUEST_DATE) {
	        mDate = (Date)data
	            .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
			sendResult(Activity.RESULT_OK);
	    } 
	}

	private void sendResult(int resultCode) {
		if (getTargetFragment() == null) 
	        return;
	    Intent i = new Intent();
	    i.putExtra(EXTRA_DATE, mDate);
	    getTargetFragment()
	        .onActivityResult(getTargetRequestCode(), resultCode, i);
	}
}