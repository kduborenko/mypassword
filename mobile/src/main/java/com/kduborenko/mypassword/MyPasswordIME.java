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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MyPasswordIME extends InputMethodService {

	private static final Map<String, String> KNOWN_APPS
		= Collections.unmodifiableMap(new HashMap<String, String>()
	{{
		put("com.android.vending", "google.com");
		put("com.alibaba.aliexpresshd", "aliexpress.com");
		put("com.ubercab", "uber.com");
	}});

	private Spinner siteSpinner;
	private AccountService accountService;
	private boolean initialValueSet = false;

	private ServiceConnection mConn = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			AccountService.AccountBinder ab = (AccountService.AccountBinder) service;
			accountService = ab.getService();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			accountService = null;
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
		} else if (!initialValueSet) {
			String presumableSiteName = findAccountName(info.packageName);

			@SuppressWarnings("unchecked") ArrayAdapter<String> adapter
				= (ArrayAdapter<String>) siteSpinner.getAdapter();
			if (adapter.getPosition(presumableSiteName) == -1) {
				adapter.add(presumableSiteName);
			}
			siteSpinner.setSelection(adapter.getPosition(presumableSiteName));
			initialValueSet = true;
		}
	}

	private String findAccountName(String packageName) {
		if (KNOWN_APPS.containsKey(packageName)) {
			return KNOWN_APPS.get(packageName);
		}
		String[] packageNameParts = packageName.split("\\.");
		return packageNameParts[1] + "." + packageNameParts[0];
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unbindService(mConn);
	}

	@Override
	public View onCreateInputView() {
		final View inputView = getLayoutInflater().inflate(R.layout.input, null);

		inputView.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				InputConnection ic = getCurrentInputConnection();
				Spinner siteSpinner = (Spinner) inputView.findViewById(R.id.spinner);
				String siteName = (String) siteSpinner.getSelectedItem();
				ic.commitText(mPasswordGenerator.generatePassword(siteName, accountService.getPassword()), 1);
			}
		});

		inputView.findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				InputConnection ic = getCurrentInputConnection();
				ic.deleteSurroundingText(Integer.MAX_VALUE, Integer.MAX_VALUE);
			}
		});

		siteSpinner = (Spinner) inputView.findViewById(R.id.spinner);
		final ArrayAdapter<String> adapter = new ArrayAdapter<>(
			this, android.R.layout.simple_spinner_item, accountService.getSitesList());
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
