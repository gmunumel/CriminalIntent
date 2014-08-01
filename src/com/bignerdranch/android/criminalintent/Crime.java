package com.bignerdranch.android.criminalintent;

import java.util.Date;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

public class Crime {

	private static final String JSON_ID = "id";
	private static final String JSON_TITLE = "title";
	private static final String JSON_SOLVED = "solved";
	private static final String JSON_DATE = "date";
	private static final String JSON_SUSPECT = "suspect";
	private static final String JSON_SUSPECT_PHONE_NUMBER = "suspect_phone_number";
	
	private UUID mId;
	private String mTitle;
	private Date mDate;
	private boolean mSolved;
	private String mSuspect;
	private String mSuspectPhoneNumber;
	
	public Crime() {
		// Generate unique identifier
		mId = UUID.randomUUID();
		mDate = new Date();
	}
	
	// Get json serialized parameters to local variables
	public Crime(JSONObject json) throws JSONException {
		mId = UUID.fromString(json.getString(JSON_ID));
		if (json.has(JSON_TITLE)) {
			mTitle = json.getString(JSON_TITLE);
		}
		mSolved = json.getBoolean(JSON_SOLVED);
		mDate = new Date(json.getLong(JSON_DATE));

		if (json.has(JSON_SUSPECT)) {
			mSuspect = json.getString(JSON_SUSPECT);
			mSuspectPhoneNumber = json.getString(JSON_SUSPECT_PHONE_NUMBER);
		}
	}

	// Convert Crime parameters to json format
	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put(JSON_ID, mId.toString());
		json.put(JSON_TITLE, mTitle);
		json.put(JSON_SOLVED, mSolved);
		json.put(JSON_DATE, mDate.getTime());
		json.put(JSON_SUSPECT, mSuspect);
		json.put(JSON_SUSPECT_PHONE_NUMBER, mSuspectPhoneNumber);
		return json;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public UUID getId() {
		return mId;
	}

	public Date getDate() {
		return mDate;
	}

	public void setDate(Date date) {
		mDate = date;
	}

	public boolean isSolved() {
		return mSolved;
	}

	public void setSolved(boolean solved) {
		mSolved = solved;
	}
	
	public String getSuspect() {
		return mSuspect;
	}
	
	public void setSuspect(String suspect) {
		mSuspect = suspect;
	}

	public String getSuspectPhoneNumber() {
		return mSuspectPhoneNumber;
	}
	
	public void setSuspectPhoneNumber(String suspectPhoneNumber) {
		mSuspectPhoneNumber = suspectPhoneNumber;
	}

	@Override
	public String toString() {
		return mTitle;
	}
}
