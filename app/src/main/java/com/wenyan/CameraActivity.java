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

	// Ĭ�������ID
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
				// ����
				mCamera.takePicture(null, null, jpegCallback);

			}
		});

		mDefaultCameraId = getDefaultCameraId();
		mCameraCurrentlyLocked = mDefaultCameraId;

	}

	@Override
	protected void onResume() {
		super.onResume();

		// ��Ĭ��idָ��������ͷ
		mCamera = getCameraInstance(mCameraCurrentlyLocked);

		mPreview.setCamera(mCamera);
	}

	@Override
	protected void onPause() {
		super.onPause();

		// avtivity��ͣ��ʱ���ͷ����
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
	 * �õ�Ĭ�������ID
	 * 
	 * @return
	 */
	private int getDefaultCameraId() {

		int defaultId = -1;

		// �ҵ�����ͷ������
		mNumberOfCameras = Camera.getNumberOfCameras();

		// �ҵ�Ĭ��ǰ������ͷ
		CameraInfo cameraInfo = new CameraInfo();
		for (int i = 0; i < mNumberOfCameras; i++) {
			Camera.getCameraInfo(i, cameraInfo);
			if (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT) {
				defaultId = i;
			}
		}
		if (-1 == defaultId) {
			if (mNumberOfCameras > 0) {
				// ���û��ǰ������ͷ
				defaultId = 0;
			} else {
				// û������ͷ
				Toast.makeText(getApplicationContext(), R.string.no_camera,
						Toast.LENGTH_LONG).show();
			}
		}
		return defaultId;
	}

	/**
	 * ��ȡ���ʵ�����Ӱ�׿Դ��õ�
	 * 
	 * 
	 */
	public static Camera getCameraInstance(int cameraId) {

		Camera c = null;
		try {
			// ����Ӧid����ͷ
			c = Camera.open(cameraId);
		} catch (Exception e) {
			// �쳣����
			e.printStackTrace();
		}
		// �����ʧ�ܣ����ؿ�ֵ
		return c;
	}

	/**
	 * ����豸�Ƿ������
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