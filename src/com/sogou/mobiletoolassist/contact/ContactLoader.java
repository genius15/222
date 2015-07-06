package com.sogou.mobiletoolassist.contact;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.AsyncTaskLoader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sogou.mobiletoolassist.AssistApplication;
import com.sogou.mobiletoolassist.R;

public class ContactLoader extends AsyncTaskLoader<Boolean>{
	private HashMap<String, ArrayList<ContactInfo>> desktopqa = new HashMap<>();
	private ContactRecordDB dao = null;
	private Context context;
	public ContactLoader(Context context) {
		super(context);
		this.context = context;
		
	}
	public void setDao(ContactRecordDB dao){
		this.dao = dao;
	}
	public HashMap<String, ArrayList<ContactInfo>> getData(){
		return desktopqa;
	}
	@Override
	public Boolean loadInBackground() {
		SharedPreferences sPreferences = AssistApplication
				.getAppDataPreferences();
		String string = sPreferences.getString(context.getString(R.string.contact_group), null);
		Gson gson = new GsonBuilder().create();
		StringArray groupList = gson.fromJson(string, StringArray.class);
		if (groupList == null) {
			return false ;
		}
		boolean loaded = false;
		for (String string2 : groupList) {
			if (desktopqa.get(string2) == null) {
				ArrayList<ContactInfo> lists = dao.getUsersByGroup(string2);
				if (lists != null) {
					desktopqa.put(string2, lists);
					loaded = true;
				}				
			}else {
				loaded = true;
			}
			
		}		
		
		
		return loaded;
	}
//	@Override
//	protected void onStartLoading() {
//		forceLoad();
//	}
}