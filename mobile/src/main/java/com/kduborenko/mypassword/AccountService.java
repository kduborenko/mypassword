package com.kduborenko.mypassword;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AccountService extends Service {

	private AccountManager am;

	public AccountService() {
	}

	@Override
	public void onCreate() {
		super.onCreate();
		this.am = AccountManager.get(this);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return new AccountBinder();
	}

	public String getPassword() {
		Account account = getAccount();
		return am.getPassword(account);
	}

	private Account getAccount() {
		Account[] accounts = am.getAccountsByType("com.kduborenko.mypassword");
		if (accounts.length == 0) {
			am.addAccountExplicitly(new Account("myaccount", "com.kduborenko.mypassword"), "", null);
			accounts = am.getAccountsByType("com.kduborenko.mypassword");
		}
		return accounts[0];
	}

	public void setPassword(String password) {
		am.setPassword(getAccount(), password);
	}

	public List<String> getSitesList() {
		try {
			String sitesListJson = am.getUserData(getAccount(), "sitesList");
			if (sitesListJson == null) {
				return new ArrayList<>();
			}
			return toList(new JSONArray(sitesListJson));
		} catch (JSONException e) {
			return new ArrayList<>();
		}
	}

	private List<String> toList(JSONArray jsonArray) throws JSONException {
		List<String> list = new ArrayList<>(jsonArray.length());
		for (int i = 0; i < jsonArray.length(); i++) {
			list.add(jsonArray.getString(i));
		}
		return list;
	}

	public void addSite(String site) {
		List<String> sitesList = new ArrayList<>(getSitesList());
		sitesList.add(site);
		Collections.sort(sitesList);
		am.setUserData(getAccount(), "sitesList", new JSONArray(sitesList).toString());
	}

	public void removeSite(String site) {
		List<String> sitesList = new ArrayList<>(getSitesList());
		sitesList.remove(site);
		am.setUserData(getAccount(), "sitesList", new JSONArray(sitesList).toString());

	}

	public class AccountBinder extends Binder {
		public AccountService getService() {
			return AccountService.this;
		}
	}
}
