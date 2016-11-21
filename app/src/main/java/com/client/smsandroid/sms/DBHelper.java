package com.client.smsandroid.sms;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBHelper {

	private static SQLiteDatabase db = null;
	private String path = "data/data/com.android.providers.telephony/databases/mmssms.db";

	public DBHelper() {
		if (db == null) {
			db = SQLiteDatabase.openDatabase(path, null,SQLiteDatabase.OPEN_READWRITE);
		}
	}

	public List<SmsModel> selectByPhone(String phone) {
		List<SmsModel> SmsList = new ArrayList<SmsModel>();

		Cursor cursor = db.query("sms", new String[] { "_id", "address","body", "date" }, "address=?", new String[]{phone}, null, null, "date desc");
		
		while (cursor.moveToNext()) {
			SmsModel model = new SmsModel();
			model.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
			model.setAddress(cursor.getString(cursor.getColumnIndex("address")));
			model.setBody(cursor.getString(cursor.getColumnIndex("body")));
			model.setDate(cursor.getLong(cursor.getColumnIndex("date")));
			SmsList.add(model);
		}
		
		return SmsList;
	}
	
	public void editById(int id,String body){
	    ContentValues values = new ContentValues();
	    values.put("body", body);
	    db.update("sms", values, "_id=?", new String[]{id+""});
	}
}
