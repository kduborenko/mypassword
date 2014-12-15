package com.kduborenko.mypassword;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.inputmethodservice.InputMethodService;
import android.os.IBinder;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.Arrays;

public class MyPasswordIME extends InputMethodService {

	private AuthenticationService authenticationService;

	private ServiceConnection mConn = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			AuthenticationService.AuthenticationBinder ab = (AuthenticationService.AuthenticationBinder) service;
			authenticationService = ab.getService();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			authenticationService = null;
		}
	};

	private PasswordGenerator mPasswordGenerator = new PasswordGenerator();

	public MyPasswordIME() {
	}

	@Override
	public View onCreateInputView() {
		final View inputView = getLayoutInflater().inflate( R.layout.input, null);

		bindService(new Intent(this, AuthenticationService.class), mConn, Context.BIND_AUTO_CREATE);

		Button okButton = (Button) inputView.findViewById(R.id.button);
		okButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				InputConnection ic = getCurrentInputConnection();
				Spinner siteSpinner = (Spinner) inputView.findViewById(R.id.spinner);
				String siteName = (String) siteSpinner.getSelectedItem();
				ic.commitText(mPasswordGenerator.generatePassword(siteName, authenticationService.getPassword()), 1);
			}
		});

		Spinner siteSpinner = (Spinner) inputView.findViewById(R.id.spinner);
		ArrayAdapter<String> adapter = new ArrayAdapter<>(
			this, android.R.layout.simple_spinner_item, Arrays.asList("mint.com", "facebook.com"));
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		siteSpinner.setAdapter(adapter);

		return inputView;
	}
}
