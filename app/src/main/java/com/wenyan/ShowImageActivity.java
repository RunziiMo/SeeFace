package com.wenyan;

import java.io.File;
import java.util.List;

import com.util.ChildAdapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ShowImageActivity extends Activity {
	private GridView mGridView;
	private List<String> list;
	private ChildAdapter adapter;
	private TextView mTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_image_activity);
		mTextView = (TextView) findViewById(R.id.albumChild);
		mGridView = (GridView) findViewById(R.id.child_grid);
		list = getIntent().getStringArrayListExtra("data");
		mTextView.setText(getIntent().getCharSequenceExtra("folderName"));
		adapter = new ChildAdapter(this, list, mGridView);
		mGridView.setAdapter(adapter);

	}

	public void buttonAlbumDoThing(View view) {
		int position = adapter.getCheckedPositon();
		switch (view.getId()) {
		
		//显示当前选中图片的Toast
		case R.id.button_preview:
			if (position >= 0) {
				LayoutInflater inflater = getLayoutInflater();
				View layout = inflater.inflate(R.layout.toast_layout, null);

				ImageView image = (ImageView) layout
						.findViewById(R.id.toastImageView);
				image.setImageURI(Uri.fromFile(new File(list.get(position))));

				Toast toast = new Toast(getApplicationContext());
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.setDuration(Toast.LENGTH_LONG);
				toast.setView(layout);
				toast.show();
			}
			break;
		case R.id.button_OK:
			if (position >= 0) {
				Intent intent = new Intent();
				intent.putExtra("imagePath", list.get(position));
				setResult(Activity.RESULT_OK, intent);
				finish();
			}
		default:
			break;
		}

	}

}
