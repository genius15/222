package com.sogou.mobiletoolassist.contact;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class ContactRecordDB extends SQLiteOpenHelper {

	final private String users = "usersInfo";

	public ContactRecordDB(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		final String tablesql = "create table "
				+ users
				+ "(id INTEGER primary key autoincrement,name text,email text,hostip text,groupname text)";
		db.execSQL(tablesql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	public long insertContact(ContactInfo cInfo) {
		SQLiteDatabase mDatabase = getWritableDatabase();
		ContentValues cValues = new ContentValues();
		cValues.put("id", cInfo.id);
		cValues.put("name", cInfo.name);
		cValues.put("email", cInfo.email);
		cValues.put("hostip", cInfo.ip);
		cValues.put("groupname", cInfo.groupName);
		return mDatabase.insert(users, null, cValues);

	}

	public int updateContact(ContactInfo cInfo) {
		SQLiteDatabase mDatabase = getWritableDatabase();
		ContentValues cValues = new ContentValues();

		cValues.put("name", cInfo.name);
		cValues.put("email", cInfo.email);
		cValues.put("hostip", cInfo.ip);
		cValues.put("groupname", cInfo.groupName);
		return mDatabase.update(users, cValues, "where id=" + cInfo.id, null);

	}

	public int deleteContact(ContactInfo cInfo) {
		SQLiteDatabase mDatabase = getWritableDatabase();
		return mDatabase.delete(users, "where id=" + cInfo.id, null);

	}

	public void drop() {
		SQLiteDatabase mDatabase = getWritableDatabase();

		try {
			mDatabase.delete(users, null, null);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public ArrayList<ContactInfo> getUsersByGroup(String groupname) {
		SQLiteDatabase mDatabase = getReadableDatabase();
		Cursor coures = null;
		try {
			coures = mDatabase.query(users, new String[] { "id", "name",
					"email", "hostip", "groupname" }, "groupname=" + "\""
					+ groupname + "\"", null, null, null, null);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (coures == null || !coures.moveToFirst()) {
			coures.close();
			return null;
		}
		ArrayList<ContactInfo> contacts = new ArrayList<>();
		ContactInfo cInfo = null;

		for (; !coures.isAfterLast(); coures.moveToNext()) {
			cInfo = new ContactInfo();
			cInfo.id = coures.getInt(0);
			cInfo.name = coures.getString(1);
			cInfo.email = coures.getString(2);
			cInfo.ip = coures.getString(3);
			cInfo.groupName = coures.getString(4);
			contacts.add(cInfo);
		}
		coures.close();
		if (contacts.isEmpty()) {
			return null;
		}
		return contacts;
	}
}
