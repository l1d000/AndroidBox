package com.l1d000.gatt;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.l1d000.blegatt.R;
import com.l1d000.gattclient.GattClientActivity;
import com.l1d000.gattserver.GattServerActivity;


public class IndexActivity extends AppCompatActivity {

	private void  do_init(){
		setContentView(R.layout.index_main);
		Button mButtonClient = (Button)findViewById(R.id.index_client);
		mButtonClient.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(IndexActivity.this,
						GattClientActivity.class);
				startActivity(intent);
			//	finish();
			}
		});

		Button mButtonServer = (Button)findViewById(R.id.index_server);
		mButtonServer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(IndexActivity.this,
						GattServerActivity.class);
				startActivity(intent);
			//	finish();
			}
		});
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestReadExternalPermission();
	}


	@SuppressLint("NewApi")
	public void requestReadExternalPermission() {
		if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
				!= PackageManager.PERMISSION_GRANTED) {

			if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {

			} else {
				// 0 是自己定义的请求coude
				requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
			}
		} else {
			do_init();

		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

		switch (requestCode) {
			case 0: {

				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {

					// permission was granted
					// request successfully, handle you transactions
					do_init();

				} else {

					// permission denied
					// request failed
				}

				return;
			}
			default:
				break;

		}
	}

}
