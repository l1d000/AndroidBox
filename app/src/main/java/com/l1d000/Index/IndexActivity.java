package com.l1d000.Index;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


import com.l1d000.androidbox.R;
import com.l1d000.gattclient.GattClientActivity;
import com.l1d000.gattserver.GattServerActivity;
import com.l1d000.musicplayer.MediaBrowserMainActivity;

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

		Button mButtonBrowserPlayer = (Button)findViewById(R.id.index_music_browser_player);
		mButtonBrowserPlayer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(IndexActivity.this,
						MediaBrowserMainActivity.class);
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

	String[] permissions = new String[2];

	@SuppressLint("NewApi")
	public void requestReadExternalPermission() {

		if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
				!= PackageManager.PERMISSION_GRANTED) {

			if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {

			} else {
				// 0 是自己定义的请求coude
				//requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
				permissions[0] = Manifest.permission.ACCESS_COARSE_LOCATION;
			}
		}

		if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED) {

			if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {

			} else {
				// 1 是自己定义的请求coude
				permissions[1] = Manifest.permission.READ_EXTERNAL_STORAGE;

			}
		}

		if(permissions[0] != null || permissions[1] != null){
			requestPermissions(permissions, 0);
		}
		else {
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
