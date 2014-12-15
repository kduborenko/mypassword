package com.kduborenko.mypassword;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class AuthenticationService extends Service {

	public AuthenticationService() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		return new AuthenticationBinder();
	}

	public String getPassword() {
		AccountManager am = AccountManager.get(this);
		Account account = getAccount(am);
		return am.getPassword(account);
	}

	private Account getAccount(AccountManager am) {
		Account[] accounts = am.getAccountsByType("com.kduborenko.mypassword");
		if (accounts.length == 0) {
			am.addAccountExplicitly(new Account("myaccount", "com.kduborenko.mypassword"), "", null);
			accounts = am.getAccountsByType("com.kduborenko.mypassword");
		}
		return accounts[0];
	}

	public void setPassword(String password) {
		AccountManager am = AccountManager.get(this);
		am.setPassword(getAccount(am), password);
	}

	public class AuthenticationBinder extends Binder {
		public AuthenticationService getService() {
			return AuthenticationService.this;
		}
	}
}
