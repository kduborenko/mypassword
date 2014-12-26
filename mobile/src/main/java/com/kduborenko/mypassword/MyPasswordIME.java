package com.kduborenko.mypassword;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.inputmethodservice.InputMethodService;
import android.os.IBinder;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class MyPasswordIME extends InputMethodService {

	private AccountService mAccountService;

	private ServiceConnection mConn = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			AccountService.AccountBinder ab = (AccountService.AccountBinder) service;
			mAccountService = ab.getService();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mAccountService = null;
		}
	};

	private PasswordGenerator mPasswordGenerator = new PasswordGenerator();

	public MyPasswordIME() {
	}

	@Override
	public void onCreate() {
		super.onCreate();
		bindService(new Intent(this, AccountService.class), mConn, Context.BIND_AUTO_CREATE);
	}

	@Override
	public void onStartInputView(EditorInfo info, boolean restarting) {
		super.onStartInputView(info, restarting);
		boolean password = (info.inputType & InputType.TYPE_TEXT_VARIATION_PASSWORD) != 0;
		if (!password) {
			InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			imm.switchToNextInputMethod(MyPasswordIME.this.getWindow().getWindow().getAttributes().token, false);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unbindService(mConn);
	}

	@Override
	public View onCreateInputView() {
		final View inputView = getLayoutInflater().inflate( R.layout.input, null);

		inputView.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				InputConnection ic = getCurrentInputConnection();
				Spinner siteSpinner = (Spinner) inputView.findViewById(R.id.spinner);
				String siteName = (String) siteSpinner.getSelectedItem();
				ic.commitText(mPasswordGenerator.generatePassword(siteName, mAccountService.getPassword()), 1);
			}
		});

		inputView.findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				InputConnection ic = getCurrentInputConnection();
				ic.deleteSurroundingText(Integer.MAX_VALUE, Integer.MAX_VALUE);
			}
		});

		Spinner siteSpinner = (Spinner) inputView.findViewById(R.id.spinner);
		final ArrayAdapter<String> adapter = new ArrayAdapter<>(
			this, android.R.layout.simple_spinner_item, mAccountService.getSitesList());
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		siteSpinner.setAdapter(adapter);

		Button nextLayout = (Button) inputView.findViewById(R.id.nextLayout);
		nextLayout.setText("\uD83C\uDF10");
		nextLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				imm.switchToNextInputMethod(MyPasswordIME.this.getWindow().getWindow().getAttributes().token, false);
			}
		});

		return inputView;
	}


}
