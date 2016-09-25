package com.wenyan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.util.CameraPreview;

public class CameraActivity extends Activity {

	private Camera mCamera;
	private CameraPreview mPreview;
	private ImageButton takeAPhoto;

	int mNumberOfCameras;
	int mCameraCurrentlyLocked;

	// 默认相机的ID
	int mDefaultCameraId;

	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(R.layout.camera_preview);

		if (!checkCameraHardware(getApplicationContext())) {
			Toast.makeText(this, R.string.no_camera, Toast.LENGTH_LONG).show();
			finish();
		}

		mPreview = new CameraPreview(this);

		FrameLayout preview = (FrameLayout) findViewById(R.id.FrameLayout1);
		preview.addView(mPreview);

		takeAPhoto = ((ImageButton) findViewById(R.id.takeAPhoto));

		takeAPhoto.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				// 拍照
				mCamera.takePicture(null, null, jpegCallback);

			}
		});

		mDefaultCameraId = getDefaultCameraId();
		mCameraCurrentlyLocked = mDefaultCameraId;

	}

	@Override
	protected void onResume() {
		super.onResume();

		// 打开默认id指定的摄像头
		mCamera = getCameraInstance(mCameraCurrentlyLocked);

		mPreview.setCamera(mCamera);
	}

	@Override
	protected void onPause() {
		super.onPause();

		// avtivity暂停的时候释放相机
		if (mCamera != null) {
			mPreview.setCamera(null);
			mCamera.release();
			mCamera = null;
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}

	private Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			Intent localIntent = new Intent();
			localIntent.putExtra("imageData", data);
			setResult(Activity.RESULT_OK, localIntent);
			finish();
		}
	};

	/**
	 * 得到默认相机的ID
	 * 
	 * @return
	 */
	private int getDefaultCameraId() {

		int defaultId = -1;

		// 找到摄像头的数量
		mNumberOfCameras = Camera.getNumberOfCameras();

		// 找到默认前置摄像头
		CameraInfo cameraInfo = new CameraInfo();
		for (int i = 0; i < mNumberOfCameras; i++) {
			Camera.getCameraInfo(i, cameraInfo);
			if (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT) {
				defaultId = i;
			}
		}
		if (-1 == defaultId) {
			if (mNumberOfCameras > 0) {
				// 如果没有前置摄像头
				defaultId = 0;
			} else {
				// 没有摄像头
				Toast.makeText(getApplicationContext(), R.string.no_camera,
						Toast.LENGTH_LONG).show();
			}
		}
		return defaultId;
	}

	/**
	 * 获取相机实例，从安卓源码得到
	 * 
	 * 
	 */
	public static Camera getCameraInstance(int cameraId) {

		Camera c = null;
		try {
			// 打开相应id摄像头
			c = Camera.open(cameraId);
		} catch (Exception e) {
			// 异常处理
			e.printStackTrace();
		}
		// 如果打开失败，返回空值
		return c;
	}

	/**
	 * 检查设备是否有相机
	 * 
	 */
	private boolean checkCameraHardware(Context context) {
		if (context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			return true;
		} else {
			return false;
		}
	}

}