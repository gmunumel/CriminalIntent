package com.bignerdranch.android.criminalintent;

import java.util.ArrayList;
import java.util.UUID;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class CrimeLab {
	private static final String TAG = "CrimeLab";
	private static final String FILENAME = "crimes.json";
	
	private ArrayList<Crime> mCrimes;
	private CriminalIntentJSONSerializer mSerializer;
	
	private static CrimeLab sCrimeLab;
	private Context mAppContext;
	
	private CrimeLab(Context appContext) {
		mAppContext = appContext;
		mSerializer = new CriminalIntentJSONSerializer(mAppContext, FILENAME);
		
		// Loading crimes from a file on disk using
		// serialized json format
		try {
			mCrimes = mSerializer.loadCrimes();
		} catch (Exception e) {
			mCrimes = new ArrayList<Crime>();
			Log.e(TAG, "Error loading crimes: ", e);
		}
	}
	
	public static CrimeLab get(Context c) {
		if(sCrimeLab == null) {
			sCrimeLab = new CrimeLab(c.getApplicationContext());
		}
		return sCrimeLab;
	}
	
	public void addCrime(Crime c) {
		mCrimes.add(c);
	}
	
	// Saving the parameters 
	public boolean saveCrimes(boolean external) {
		// Check if the external mode is available
		if (external) {
			if (!isExternalStorageWritable()){
				Log.e(TAG, "Error external mode not available");
				return false;
			}
		}
		try {
			mSerializer.saveCrimes(mCrimes, external);
			Log.d(TAG, "crimes saved to file");
			return true;
		} catch (Exception e) {
			Log.e(TAG, "Error saving crimes: ", e);
			return false;
		}
	}
	
	public ArrayList<Crime> getCrimes() {
		return mCrimes;
	}
	
	public Crime getCrime(UUID id) {
		for(Crime c : mCrimes) {
			if(c.getId().equals(id))
				return c;
		}
		return null;
	}

	// Checks if external storage is available for read and write 
	private boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}
}