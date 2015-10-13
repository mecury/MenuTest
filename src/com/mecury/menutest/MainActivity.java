package com.mecury.menutest;

import javax.security.auth.PrivateCredentialPermission;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity {

	private MenuUI menuUI;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		menuUI=new MenuUI(this); 
		setContentView(menuUI);
	}
}
