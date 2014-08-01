package com.bignerdranch.android.criminalintent;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
	private static final int REQUEST_CONTACT = 2;
	
	private Crime mCrime;
	private EditText mTitleField;
	private Button mDateButton, mTimeButton, 
		mDateTimeButton, mSuspectButton, mSuspectCallButton;
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
			// create menu options for deleting crimes | challenge chapter 18
			case R.id.menu_item_delete_crime:
				CrimeLab.get(getActivity()).deleteCrime(mCrime);
				// Launch CrimeListActivity 
				Intent i = new Intent(getActivity(), CrimeListActivity.class);
				startActivity(i);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	// Save the crimes on file when it's paused
	@Override
	public void onPause() {
		super.onPause();
		// True to external storage
		// False otherwise
		CrimeLab.get(getActivity()).saveCrimes(true);
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
        
        // Nullable Crime instance in case I removed all the
        // crimes and want to avoid a nullPointerException
        if (mCrime == null) {
        	mCrime = new Crime();
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
        // This code was commented to save space on cell phone
        /*
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
        */
        // end challenge 1

        // Challenge 2 final chapter 12 | Adding a new Fragment to 
        // choose if you change Date or Time
        // This code was commented to save space on cell phone
        /*
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
        */
        // end challenge 2
        
        mSolvedCheckBox = (CheckBox)v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Set the crime's solved property
                mCrime.setSolved(isChecked);
            }
        });
        
        // Adding the use of Implicit Intent | Chapter 21
        Button reportButton = (Button)v.findViewById(R.id.crime_reportButton);
        reportButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT, 
                    getString(R.string.crime_report_subject));
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);
            }
        });

        mSuspectButton = (Button)v.findViewById(R.id.crime_suspectButton);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               Intent i = new Intent(Intent.ACTION_PICK,
                    ContactsContract.Contacts.CONTENT_URI);
               startActivityForResult(i, REQUEST_CONTACT);                
            }
        });

        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

        // Challenge: Call a suspect | Chapter 21
        mSuspectCallButton = (Button)v.findViewById(R.id.crime_suspect_callButton);
        mSuspectCallButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if (mCrime.getSuspectPhoneNumber() != null) {
            		Uri number = Uri.parse("tel:" + mCrime.getSuspectPhoneNumber());
            		Intent i = new Intent(Intent.ACTION_DIAL);
            		i.setData(number);
            		startActivity(i);
            	} else {
            		
            	}
            }
        });
        // end of challenge
        
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
	    } else if (requestCode == REQUEST_CONTACT) {
	        Uri contactUri = data.getData();

	        // Find the lookup key of the contact
	        List<String> items = contactUri.getPathSegments();
            String lookupKey = items.get(2);

	        // Specify which fields you want your query to return
	        // values for.
	        String[] queryFields = new String[] { 
	        	ContactsContract.Contacts.LOOKUP_KEY,
	            ContactsContract.Contacts.DISPLAY_NAME,
	            ContactsContract.CommonDataKinds.Phone.NUMBER
	        };

	        // Perform your query - the contactUri is like a "where"
	        // clause here
	        contactUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
	        Cursor c = getActivity().getContentResolver()
	            .query(contactUri, queryFields, null, null, null);

	        // Double-check that you actually got results
	        if (c.getCount() == 0) {
	            c.close();
	            return;
	        }
	        // Pull out the first column of the first row of data - 
	        // that is your suspect's name.
	        c.moveToFirst();
	        String suspect = "", suspectPhoneNumber = "";
	        do {
	        	String suspectLookupKey = c.getString(c.getColumnIndexOrThrow(
	        			ContactsContract.Contacts.LOOKUP_KEY));
	        	if (lookupKey.equals(suspectLookupKey)) {
	        		suspect = c.getString(c
                            .getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
	        		suspectPhoneNumber = c.getString(c
                            .getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
	        		break;
	        	}
	        	
	        } while (c.moveToNext());
	        mCrime.setSuspect(suspect);
	        mCrime.setSuspectPhoneNumber(suspectPhoneNumber);
	        mSuspectButton.setText(suspect);
	        c.close();
	    }
	}
	
	// create menu options for deleting crimes | challenge chapter 18
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.crime_list_item_context, menu);
	}
	
	public static CrimeFragment newInstance(UUID crimeId) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_CRIME_ID, crimeId);
		
		CrimeFragment fragment = new CrimeFragment();
		fragment.setArguments(args);
		
		return fragment;
	}

	private void updateDate() {
		mDateButton.setText(mCrime.getDate().toString());
	}

	private void updateTime() {
        mTimeButton.setText(android.text.format.DateFormat.format("K:m a", mCrime.getDate()));
	}
	
	private String getCrimeReport() {
	    String solvedString = null;
	    if (mCrime.isSolved()) {
	        solvedString = getString(R.string.crime_report_solved);
	    } else {
	        solvedString = getString(R.string.crime_report_unsolved);
	    }
	    String dateFormat = "EEE, MMM dd";
	    String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();
	    
	    String suspect = mCrime.getSuspect();
	    if (suspect == null) {
	        suspect = getString(R.string.crime_report_no_suspect);
	    } else {
	        suspect = getString(R.string.crime_report_suspect, suspect);
	    }
	    String report = getString(R.string.crime_report, 
	        mCrime.getTitle(), dateString, solvedString, suspect);
	    return report;
	}
}