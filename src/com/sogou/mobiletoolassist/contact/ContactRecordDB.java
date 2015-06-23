package com.sogou.mobiletoolassist.contact;

import java.util.ArrayList;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class ContactRecordDB extends SQLiteOpenHelper {

	public ContactRecordDB(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		final String tablesql = "";
		db.execSQL(tablesql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	public boolean insertContact(ContactInfo cInfo){
		return true;
	}
	
	public boolean updateContact(ContactInfo cInfo) {
		return true;
	}
	
	public boolean deleteContact(ContactInfo cInfo) {
		return true;
		
	}
	
	public boolean clearContact() {
		return true;
	}
	
	public ArrayList<ContactInfo> getAllContact() {
		return null;
	}
}
