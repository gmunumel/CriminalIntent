package com.bignerdranch.android.criminalintent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import android.content.Context;
import android.util.Log;

public class CriminalIntentJSONSerializer {
	private static final String TAG = "CriminalIntentJSONSerializer";

	private Context mContext;
	private String mFilename;
	
	public CriminalIntentJSONSerializer(Context c, String f) {
		mContext = c;
		mFilename = f;
	}
	
	// Load crimes from a  file in disk
	public ArrayList<Crime> loadCrimes(boolean externalFile) 
			throws IOException, JSONException {
		ArrayList<Crime> crimes = new ArrayList<Crime>();
		BufferedReader reader = null;
		try {
			// Open and read the file into a StringBuilder
			if(externalFile) {
				File extCrimeFile = new File(mContext.getExternalFilesDir(null), mFilename);
				FileInputStream extFile = new FileInputStream(extCrimeFile);
				reader = new BufferedReader(new InputStreamReader(extFile));
			} else {
				InputStream in = mContext.openFileInput(mFilename);
				reader = new BufferedReader(new InputStreamReader(in));
			}
			StringBuilder jsonString = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				// Line breaks are omitted and irrelevant
				jsonString.append(line);
			}
			// Parse the JSON using JSONTokener
			JSONArray array = (JSONArray) new JSONTokener(jsonString.toString())
				.nextValue();
			// Build the array of crimes from JSONObjects
			for (int i = 0; i < array.length(); i++) {
				crimes.add(new Crime(array.getJSONObject(i)));
			}
		} catch (FileNotFoundException e) {
			// Ignore this one; it happens when starting fresh
		} finally {
			if (reader != null)
				reader.close();
		}
		return crimes;
	}

	// Saving the list of crimes to a file in disk
	public void saveCrimes(ArrayList<Crime> crimes, boolean externalFile)
			throws JSONException, IOException {
		
		// Build an array in JSON
		JSONArray array = new JSONArray();
		for (Crime c : crimes)
			array.put(c.toJSON());
		
		// Write the file to disk
		Writer writer = null;
		try {
			if (externalFile) {
				// get the external files dir
		        File extDataDir = new File(mContext.getExternalFilesDir(null), mFilename);
		        // log the dir to be written to
		        Log.d(TAG, "The external files dir is: " + extDataDir.toString());
				File extCrimeFile = new File(extDataDir.toString());
				FileOutputStream extFile = new FileOutputStream(extCrimeFile);
				writer = new OutputStreamWriter(extFile);
			} else {
				OutputStream out = mContext
					.openFileOutput(mFilename, Context.MODE_PRIVATE);
				writer = new OutputStreamWriter(out);
			}
			writer.write(array.toString());
		} finally {
			if (writer != null)
				writer.close();
		}
	}
}
