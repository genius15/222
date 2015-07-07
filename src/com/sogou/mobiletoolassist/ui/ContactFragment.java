package com.sogou.mobiletoolassist.ui;

import java.util.ArrayList;
import java.util.HashMap;

import com.sogou.mobiletoolassist.AssistActivity;
import com.sogou.mobiletoolassist.AssistApplication;
import com.sogou.mobiletoolassist.R;
import com.sogou.mobiletoolassist.adapter.ContactAdapter;
import com.sogou.mobiletoolassist.contact.ConstantValues;
import com.sogou.mobiletoolassist.contact.ContactInfo;

import com.sogou.mobiletoolassist.contact.ContactLoader;
import com.sogou.mobiletoolassist.contact.ContactRecordDB;
import com.sogou.mobiletoolassist.contact.GroupInfo;
import com.sogou.mobiletoolassist.contact.IdsArray;
import com.sogou.mobiletoolassist.contact.ContactInfoArray;
import com.sogou.mobiletoolassist.contact.GroupInfoArray;
import com.sogou.mobiletoolassist.contact.GroupInfoUpdate;
import com.sogou.mobiletoolassist.contact.StringArray;
import com.sogou.mobiletoolassist.util.NetworkUtil;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

import android.util.Log;
import android.view.LayoutInflater;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.view.ViewGroup;
import android.widget.ExpandableListView;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ContactFragment extends Fragment implements
		Response.Listener<ContactInfoArray>, Response.ErrorListener,
		GroupInfoUpdate ,LoaderCallbacks<Boolean>{
	private ContactAdapter recAdapter = null;
	private ExpandableListView listv = null;
	private HashMap<String, ArrayList<ContactInfo>> desktopqa = new HashMap<>();
	private HashMap<String, String> groupid_name = null;
	private RequestGroupId requestGroupId = null;
	private Gson gson = null;
	private ContactRecordDB dao = null;
	private String tag = ContactFragment.class.getSimpleName();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		Log.i(tag, "rec onCreateView");
		listv = (ExpandableListView) inflater.inflate(R.layout.receivers,
				container, false);
		recAdapter = new ContactAdapter(getActivity());
		listv.setAdapter(recAdapter);
		gson = new GsonBuilder().create();
		
		return listv;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.i(tag, "rec onCreate");
		requestGroupId = new RequestGroupId(this);
		getLoaderManager().initLoader(0, null, this);
		dao = new ContactRecordDB(getActivity(), "contacts", null, 6);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.i(tag, "onActivityCreated");
		
		
//		new LoadContact().execute();
	}

	private void requestUserInfo(IdsArray response) {

		String ids = gson.toJson(response);
		String allmen = ConstantValues.userinfo_url_pre + "(" + ids + ")";
		NetworkUtil.get(allmen, ContactInfoArray.class, this, this);
	}

	@Override
	public void onResponse(ContactInfoArray response) {
		if (response != null && !response.isEmpty()) {
			for (ContactInfo contactInfo : response) {
				String name = groupid_name.get(contactInfo.userGroupIds[0]);
				if (contactInfo.userGroupIds.length < 1 || name == null) {

					desktopqa.get("others").add(contactInfo);
					continue;
				}
				// group by the first group id
				String ip = AssistActivity.nameipMap.get(contactInfo.name);
				if (ip != null) {
					contactInfo.ip = ip;
				}
				contactInfo.groupName = name;
				desktopqa.get(name).add(contactInfo);
			}
			recAdapter.addData(desktopqa);
			new SaveContact().execute(response);
		}
	}
	private class SaveContact extends AsyncTask<ContactInfoArray, Void, Void>{

		@Override
		protected Void doInBackground(ContactInfoArray... params) {
			ArrayList<String> groups = new ArrayList<String>(groupid_name.values());
			String groupString = gson.toJson(groups);
			SharedPreferences sPreferences = AssistApplication
					.getAppDataPreferences();
			sPreferences.edit()
					.putString(getString(R.string.contact_group), groupString)
					.commit();
			dao.drop();
			for (ContactInfo contactInfo : params[0]) {
				dao.insertContact(contactInfo);
			}
			return null;
		}
		
	}
	
	
//	private class LoadContact extends AsyncTask<Void, Void, Boolean>{
//
//		@Override
//		protected Boolean doInBackground(Void... params) {
//			SharedPreferences sPreferences = AssistApplication
//					.getAppDataPreferences();
//			String string = sPreferences.getString(getString(R.string.contact_group), null);
//			StringArray groupList = gson.fromJson(string, StringArray.class);
//			if (groupList == null) {
//				return false;
//			}
//			boolean loaded = false;
//			for (String string2 : groupList) {
//				if (desktopqa.get(string2) == null) {
//					ArrayList<ContactInfo> lists = dao.getUsersByGroup(string2);
//					if (lists != null) {
//						desktopqa.put(string2, lists);
//						loaded = true;
//					}				
//				}else {
//					loaded = true;
//				}
//				
//			}		
//			
//			return loaded;
//		}
//		@Override
//		protected void onPostExecute(Boolean loaded){
//			if (!loaded && requestGroupId != null) {
//				requestGroupId.Request();
//			}else {
//				recAdapter.addData(desktopqa);
//			}
//		}
//	}

	@Override
	public void onErrorResponse(VolleyError error) {
		error.printStackTrace();
	}

	private void RequestUserIds() {
		NetworkUtil.get(ConstantValues.allids_url, IdsArray.class,
				new Response.Listener<IdsArray>() {
					@Override
					public void onResponse(IdsArray response) {
						if (response != null && !response.isEmpty()) {
							requestUserInfo(response);
						}

					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						error.printStackTrace();
					}

				});
	}

	private class RequestGroupId implements Response.Listener<IdsArray>,
			Response.ErrorListener {
		GroupInfoUpdate gInfoUpdate = null;

		public RequestGroupId(GroupInfoUpdate update) {
			gInfoUpdate = update;
		}

		public void Request() {
			NetworkUtil.get(ConstantValues.allgroup_ids_url, IdsArray.class,
					this, this);
		}

		@Override
		public void onErrorResponse(VolleyError error) {
			// TODO Auto-generated method stub
			error.printStackTrace();
		}

		@Override
		public void onResponse(IdsArray response) {
			// id 请求回来后请求各组信息
			String url = ConstantValues.groupinfo_url_pre + "("
					+ new Gson().toJson(response) + ")";
			NetworkUtil.get(url, GroupInfoArray.class,
					new Response.Listener<GroupInfoArray>() {

						@Override
						public void onResponse(GroupInfoArray response) {
							gInfoUpdate.updateGroupInfo(response);
						}
					}, new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							// TODO Auto-generated method stub
							error.printStackTrace();
						}
					});
		}

	}

	@Override
	public void updateGroupInfo(GroupInfoArray gInfoArray) {
		if (groupid_name == null) {
			groupid_name = new HashMap<>();
		}

		for (GroupInfo groupInfo : gInfoArray) {
			desktopqa.put(groupInfo.name, new ArrayList<ContactInfo>());
			groupid_name.put(String.valueOf(groupInfo.id), groupInfo.name);
		}

		desktopqa.put("others", new ArrayList<ContactInfo>());
		// group info is done,request users info
		RequestUserIds();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.contact_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.clear_contact:
			recAdapter.clearData();
			break;
		case R.id.refresh_contact:
			if (requestGroupId != null) {
				requestGroupId.Request();
			}
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public Loader<Boolean> onCreateLoader(int arg0, Bundle arg1) {
		ContactLoader loader =  new ContactLoader(getActivity());
		if (dao == null) {
			dao = new ContactRecordDB(getActivity(), "contacts", null, 6);
		}
		loader.setDao(dao);
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Boolean> arg0, Boolean loaded) {
		// TODO Auto-generated method stub
		ContactLoader clLoader = (ContactLoader) arg0;
		desktopqa = clLoader.getData();
		if (!loaded && requestGroupId != null) {
			requestGroupId.Request();
		}else {
			recAdapter.addData(desktopqa);
		}
		//dao.close();
	}


	@Override
	public void onLoaderReset(Loader<Boolean> arg0) {
		Log.i(tag, "fffff");
		
	}
}
