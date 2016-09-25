package com.wenyan;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.util.FaceDetectApi;
import com.wenyan.R;

public class MainActivity extends Activity {

	public final static String JSON = "wenyan.JSON";
	public final static int PICK_IMAGE = 23333;
	public final static int PICK_PHOTO = 2333;
	private final static String ARG_IMAGE_BYTES = "image_holder";
	private ProgressDialog mDialog = null;
	private ImageView imagePicked;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		int[] imageR = new int[] { R.drawable.kezhendong,
				R.drawable.kezhendong2, R.drawable.yangmi };

		Random random = new Random();

		imagePicked = (ImageView) findViewById(R.id.imagePicked);

		ImageDataHolder.INSTANCE.loadData(
				Uri.parse("android.resource://" + getPackageName() + "/"
						+ imageR[random.nextInt(3)]), this);
		imagePicked.setImageBitmap(ImageDataHolder.INSTANCE.getBitmap());

		if (savedInstanceState != null) {
			ImageDataHolder.INSTANCE.recoverFromByteArray(savedInstanceState
					.getByteArray(ARG_IMAGE_BYTES));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putByteArray(ARG_IMAGE_BYTES,
				ImageDataHolder.INSTANCE.getByteArray());
	}

	public void buttonMainDoThing(View view) {

		switch (view.getId()) {
		case R.id.button_take_photo:
			Intent photoPickerIntent = new Intent(MainActivity.this,
					CameraActivity.class);
			startActivityForResult(photoPickerIntent, PICK_PHOTO);
			break;
		case R.id.button_pick_image:
			Intent photoPickerIntent1 = new Intent(MainActivity.this,
					ImageSelect.class);
			startActivityForResult(photoPickerIntent1, PICK_IMAGE);
			break;
		case R.id.button_face_detection:
			FaceDetectApi.APICallback callback = new FaceDetectApi.APICallback() {

				public void gotResponse(final String sResponse) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							mDialog.dismiss();
							MainActivity.this.startDisplayMessage(sResponse);
						}
					});
				}
			};
			mDialog = ProgressDialog.show(this, "", "正在评价你的脸……");
			FaceDetectApi.face_detect(ImageDataHolder.INSTANCE.getByteArray(),
					callback);
			break;
		default:
			break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PICK_IMAGE) {
			if (resultCode == RESULT_OK) {
				String path = data.getStringExtra("imagePath");
				Uri imagePath = Uri.fromFile(new File(path));
				ImageDataHolder.INSTANCE.loadData(imagePath, this);
				imagePicked
						.setImageBitmap(ImageDataHolder.INSTANCE.getBitmap());
			}
		} else if (requestCode == PICK_PHOTO) {
			if (resultCode == RESULT_OK) {
				byte[] byteData = data.getByteArrayExtra("imageData");
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeByteArray(byteData, 0, byteData.length,
						options);
				options.inJustDecodeBounds = false;
				double imageBili = imagePicked.getWidth()
						/ imagePicked.getHeight();
				double bitmapBili = options.outHeight / options.outWidth;
				int be = imageBili > bitmapBili ? (int) (options.outWidth / (float) imagePicked
						.getHeight())
						: (int) (options.outHeight / (float) imagePicked
								.getWidth());
				if (be <= 0) {
					be = 1;
				}
				options.inSampleSize = be;
				Bitmap bm = BitmapFactory.decodeByteArray(byteData, 0,
						byteData.length, options);
				Matrix m = new Matrix();
				m.setRotate(-90);
				m.postScale(-1, 1);
				Bitmap bmGai = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
						bm.getHeight(), m, true);
				imagePicked.setImageBitmap(bmGai);
				ImageDataHolder.INSTANCE.loadData(bmGai, this);
				Toast.makeText(this, "载入图像成功", Toast.LENGTH_LONG).show();
			}
		}
	}

	private void startDisplayMessage(String message) {
		Bitmap bitmap = null;
		final ArrayList<Integer> mMessage = new ArrayList<Integer>();
		HashMap<String, Double> mHashMap = new HashMap<String, Double>();
		try {
			JSONObject jsonRoot = new JSONObject(message);
			if (jsonRoot.has("face_detection")) {
				bitmap = ImageDataHolder.INSTANCE.getBitmap().copy(
						Bitmap.Config.ARGB_8888, true);
				JSONArray jsonArray = jsonRoot.getJSONArray("face_detection");
				int count = 0;
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject json = jsonArray.getJSONObject(i);
					if (json.getDouble("confidence") >= 0.1) {
						count++;
						this.drawTag(json, count, bitmap);
						this.setText(json, count, mMessage);
					}
				}
			} else if (jsonRoot.has("scene_understanding")) {
				bitmap = ImageDataHolder.INSTANCE.getBitmap();
				JSONArray jsonArray = jsonRoot
						.getJSONArray("scene_understanding");
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject json = jsonArray.getJSONObject(i);
					mHashMap.put(json.getString("label"),
							json.getDouble("score"));
				}
			}
		} catch (JSONException e) {
			Log.v("Exception", e.getMessage());
		}
		if (imagePicked != null) {

			imagePicked.setImageBitmap(bitmap);
		}
		new Handler().postDelayed(new Runnable() {
			public void run() {
				Intent mainIntent = new Intent(MainActivity.this,
						DisplayMessageActivity.class);
				mainIntent.putIntegerArrayListExtra(JSON, mMessage);
				MainActivity.this.startActivity(mainIntent);
			}
		}, 2000);
	}

	/**
	 * 顺序 外貌 性别 年龄
	 * 
	 * @param json
	 * @param index
	 * @param message
	 * @throws JSONException
	 */

	private void setText(JSONObject json, int index, ArrayList<Integer> message)
			throws JSONException {
		message.add((int) (json.getDouble("beauty") * 100));
		message.add((int) (json.getDouble("sex") * 100));
		message.add((int) (json.getDouble("age") * 100));
	}

	/**
	 * 画出五官
	 * 
	 * @param json
	 * @param index
	 * @param bitmap
	 * @throws JSONException
	 */
	private void drawTag(JSONObject json, int index, Bitmap bitmap)
			throws JSONException {
		Canvas canvas = new Canvas(bitmap);
		Paint paint = new Paint();
		JSONObject box = json.getJSONObject("boundingbox");
		JSONObject size = box.getJSONObject("size");
		JSONObject tl = box.getJSONObject("tl");
		float height = (float) size.getDouble("height");
		float width = (float) size.getDouble("width");
		float top = (float) tl.getDouble("y");
		float left = (float) tl.getDouble("x");
		paint.setColor(json.getDouble("sex") >= 0.5 ? Color.BLUE : Color.RED);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(3);
		canvas.drawRect(left, top, left + width, top + height, paint);
		paint.setStrokeWidth(2);
		paint.setTextSize(20);
		paint.setTextAlign(Paint.Align.CENTER);
		canvas.drawText(String.format("%d", index), left + width / 2,
				(float) (top + height + 25), paint);

		// Draw five points.
		paint.setStyle(Paint.Style.FILL);
		JSONObject eyeLeft = json.getJSONObject("eye_left");
		JSONObject eyeRight = json.getJSONObject("eye_right");
		JSONObject mouthLeft = json.getJSONObject("mouth_l");
		JSONObject mouthRight = json.getJSONObject("mouth_r");
		JSONObject nose = json.getJSONObject("nose");
		int pointWidth = (int) (width / 30);
		canvas.drawCircle((float) eyeLeft.getDouble("x"),
				(float) eyeLeft.getDouble("y"), pointWidth, paint);
		canvas.drawCircle((float) eyeRight.getDouble("x"),
				(float) eyeRight.getDouble("y"), pointWidth, paint);
		canvas.drawCircle((float) mouthLeft.getDouble("x"),
				(float) mouthLeft.getDouble("y"), pointWidth, paint);
		canvas.drawCircle((float) mouthRight.getDouble("x"),
				(float) mouthRight.getDouble("y"), pointWidth, paint);
		canvas.drawCircle((float) nose.getDouble("x"),
				(float) nose.getDouble("y"), pointWidth, paint);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitBy2Click();
		}
		return false;
	}

	private static Boolean isExit = false;

	private void exitBy2Click() {
		Timer tExit = null;
		if (isExit == false) {
			isExit = true;
			Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				public void run() {
					isExit = false;
				}
			}, 2000);

		} else {
			finish();
			System.exit(0);
		}
	}

}
