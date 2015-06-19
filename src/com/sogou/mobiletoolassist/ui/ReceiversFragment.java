package com.sogou.mobiletoolassist.ui;

import java.util.ArrayList;
import java.util.HashMap;

import com.sogou.mobiletoolassist.AssistActivity;
import com.sogou.mobiletoolassist.R;
import com.sogou.mobiletoolassist.adapter.ContactAdapter;
import com.sogou.mobiletoolassist.contact.ConstantValues;
import com.sogou.mobiletoolassist.contact.ContactInfo;
import com.sogou.mobiletoolassist.contact.GroupInfo;
import com.sogou.mobiletoolassist.contact.IdsArray;
import com.sogou.mobiletoolassist.contact.ContactInfoArray;
import com.sogou.mobiletoolassist.contact.GroupInfoArray;
import com.sogou.mobiletoolassist.contact.GroupInfoUpdate;
import com.sogou.mobiletoolassist.util.NetworkUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

public class ReceiversFragment extends Fragment implements
		Response.Listener<ContactInfoArray>, Response.ErrorListener,
		GroupInfoUpdate {
	private ContactAdapter recAdapter = null;
	private ExpandableListView listv = null;
	private HashMap<String, ArrayList<ContactInfo>> desktopqa = new HashMap<>();
	private HashMap<String, String> groupid_name = null;
	private RequestGroupId requestGroupId = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i("learn", "rec onCreateView");
		listv = (ExpandableListView) inflater.inflate(R.layout.receivers,
				container, false);
		recAdapter = new ContactAdapter(getActivity());
		listv.setAdapter(recAdapter);
		return listv;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestGroupId = new RequestGroupId(this);
		Log.i("learn", "rec onCreate");
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (requestGroupId != null) {
			requestGroupId.Request();
		}
	}

	private void requestUserInfo(IdsArray response) {
		Gson gson = new Gson();

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
				
				desktopqa.get(name).add(contactInfo);
			}
			recAdapter.addData(desktopqa);
		}
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		error.printStackTrace();
	}

	private void RequestUserIds() {
		NetworkUtil
				.get(ConstantValues.allids_url,
						IdsArray.class, new Response.Listener<IdsArray>() {
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
			NetworkUtil.get(ConstantValues.allgroup_ids_url, IdsArray.class, this, this);
		}

		@Override
		public void onErrorResponse(VolleyError error) {
			// TODO Auto-generated method stub
			error.printStackTrace();
		}

		@Override
		public void onResponse(IdsArray response) {
			// id 请求回来后请求各组信息
			String url = ConstantValues.groupinfo_url_pre+"("+new Gson().toJson(response)+")";
			NetworkUtil.get(url,
					GroupInfoArray.class,
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

}
