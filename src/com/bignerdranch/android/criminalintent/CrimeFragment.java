package com.bignerdranch.android.criminalintent;

import java.util.Date;
import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

public class CrimeFragment extends Fragment {

	public static final String EXTRA_CRIME_ID =
			"com.bignerdranch.android.criminalintent.crime_id";
	
	private static final String DIALOG_DATE = "date";
	private static final String DIALOG_TIME = "time";
	private static final String DIALOG_CHOOSE_DATE_TIME = "choose_date_time";
	private static final int REQUEST_DATE = 0;
	private static final int REQUEST_TIME = 1;
	
	private Crime mCrime;
	private EditText mTitleField;
	private Button mDateButton, mTimeButton, mDateTimeButton;
    private CheckBox mSolvedCheckBox;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		
		UUID crimeId = (UUID)getArguments().getSerializable(EXTRA_CRIME_ID);
		
		mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				if (NavUtils.getParentActivityName(getActivity()) != null) {
	                NavUtils.navigateUpFromSameTask(getActivity());
	            }
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@TargetApi(11)
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, 
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, parent, false);
        
        // Setting the up button
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        	if (NavUtils.getParentActivityName(getActivity()) != null) {
                getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        mTitleField = (EditText)v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(
                    CharSequence c, int start, int before, int count) {
                mCrime.setTitle(c.toString());
            }
            public void beforeTextChanged(
                    CharSequence c, int start, int count, int after) {
                // This space intentionally left blank
            }
            public void afterTextChanged(Editable c) {
                // This one too
            } 
        });

        mDateButton = (Button)v.findViewById(R.id.crime_date);
        // Challenge | Improvement in DateFormat
        //mDateButton.setText(android.text.format.DateFormat.format("E, MMM dd, yyyy.", mCrime.getDate()));
        // end challenge
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		FragmentManager fm = getActivity()
        				.getSupportFragmentManager();
        		DatePickerFragment dialog = DatePickerFragment
        				.newInstance(mCrime.getDate());
        		dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
        		dialog.show(fm, DIALOG_DATE);
        	}
        });
        
        // Challenge 1 final chapter 12 | Adding a TimePicker
        mTimeButton = (Button)v.findViewById(R.id.crime_time);
        updateTime();
        mTimeButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		FragmentManager fm = getActivity()
        				.getSupportFragmentManager();
        		TimePickerFragment dialog = TimePickerFragment
        				.newInstance(mCrime.getDate());
        		dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
        		dialog.show(fm, DIALOG_TIME);
        	}
        });
        // end challenge 1

        // Challenge 2 final chapter 12 | Adding a new Fragment to 
        // choose if you change Date or Time
        mDateTimeButton = (Button)v.findViewById(R.id.crime_date_time);
        mDateTimeButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		FragmentManager fm = getActivity()
        				.getSupportFragmentManager();
        		ChooseDateTimeFragment dialog = ChooseDateTimeFragment
        				.newInstance(mCrime.getDate());
        		dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
        		dialog.show(fm, DIALOG_CHOOSE_DATE_TIME);
        	}
        });
        // end challenge 2
        
        mSolvedCheckBox = (CheckBox)v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Set the crime's solved property
                mCrime.setSolved(isChecked);
            }
        });
        
        return v;
    }
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (resultCode != Activity.RESULT_OK) return;
	    if (requestCode == REQUEST_DATE) {
	        Date date = (Date)data
	            .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
	        mCrime.setDate(date);
	        updateDate();
	    } else if (requestCode == REQUEST_TIME) {
	        Date date = (Date)data
	            .getSerializableExtra(TimePickerFragment.EXTRA_TIME);
	        mCrime.setDate(date);
	        updateTime();
	    }
	}
	
	private void updateDate() {
		mDateButton.setText(mCrime.getDate().toString());
	}

	private void updateTime() {
        mTimeButton.setText(android.text.format.DateFormat.format("K:m a", mCrime.getDate()));
	}

	public static CrimeFragment newInstance(UUID crimeId) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_CRIME_ID, crimeId);
		
		CrimeFragment fragment = new CrimeFragment();
		fragment.setArguments(args);
		
		return fragment;
	}
}