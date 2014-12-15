package com.kduborenko.mypassword;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;


public class MyPasswordSettingsActivity extends Activity {

	private ServiceConnection mConn = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			AuthenticationService.AuthenticationBinder ab = (AuthenticationService.AuthenticationBinder) service;
			final AuthenticationService authenticationService = ab.getService();
			EditText masterPassword = (EditText) findViewById(R.id.masterPassword);
			masterPassword.setText(authenticationService.getPassword());
			masterPassword.addTextChangedListener(new TextWatcher() {
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {

				}

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					authenticationService.setPassword(s.toString());
				}

				@Override
				public void afterTextChanged(Editable s) {

				}
			});
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_password_settings);
		//noinspection ConstantConditions
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected void onStart() {
		super.onStart();
		bindService(new Intent(this, AuthenticationService.class), mConn, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {
		super.onStop();
		unbindService(mConn);
	}

	public boolean onOptionsItemSelected(MenuItem item){
		finish();
		return true;

	}

}
