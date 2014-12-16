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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;


public class MyPasswordSettingsActivity extends Activity {

	private ServiceConnection mConn = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			AccountService.AccountBinder ab = (AccountService.AccountBinder) service;
			final AccountService accountService = ab.getService();
			EditText masterPassword = (EditText) findViewById(R.id.masterPassword);
			masterPassword.setText(accountService.getPassword());
			masterPassword.addTextChangedListener(new TextWatcher() {
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {

				}

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					accountService.setPassword(s.toString());
				}

				@Override
				public void afterTextChanged(Editable s) {

				}
			});
			findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					EditText newSite = (EditText) findViewById(R.id.newSite);
					accountService.addSite(newSite.getText().toString());
					renderSitesList((ListView) findViewById(R.id.sitesListView), accountService);
					newSite.setText("");
				}
			});
			ListView sitesListView = (ListView) findViewById(R.id.sitesListView);
			renderSitesList(sitesListView, accountService);
			sitesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					ListView sitesListView = (ListView) parent;
					String site = (String) sitesListView.getAdapter().getItem(position);
					accountService.removeSite(site);
					renderSitesList(sitesListView, accountService);
				}
			});
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {}
	};

	private void renderSitesList(ListView sitesListView, AccountService accountService) {
		sitesListView.setAdapter(new ArrayAdapter<>(getBaseContext(),
			android.R.layout.simple_spinner_dropdown_item,
			accountService.getSitesList()));
	}

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
		bindService(new Intent(this, AccountService.class), mConn, Context.BIND_AUTO_CREATE);
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
