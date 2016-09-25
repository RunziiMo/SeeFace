package com.wenyan;

import com.wenyan.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class MenuScreen extends Activity {


	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(R.layout.menu);
		new Handler().postDelayed(new Runnable() {
			public void run() {
				Intent mainIntent = new Intent(MenuScreen.this,
						MainActivity.class);
				MenuScreen.this.startActivity(mainIntent);
				MenuScreen.this.finish();
			}
		}, 5000);
	}


}