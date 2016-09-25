package com.util;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * ���Ԥ���࣬�̳�SurfaceView
 * 
 * @author Wouldyou
 * 
 */
public class CameraPreview extends SurfaceView implements
		SurfaceHolder.Callback {

	private SurfaceHolder mHolder;
	private Camera mCamera;
	Size mPreviewSize;
	List<Size> mSupportedPreviewSizes;

	public CameraPreview(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public CameraPreview(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CameraPreview(Context context) {
		super(context);
		init();
	}

	/**
	 * ��ʼ������ȡSurFaceView��surfaveHolder,��������CallBack������Ϊ
	 * ʵ����SurfaceHolder.Callback�ĸ�SurfaceView
	 * 
	 */
	@SuppressWarnings("deprecation")
	private void init() {
		Log.d("TAG", "CameraPreview initialize");

		mHolder = getHolder();
		mHolder.addCallback(this);
		// ���ڵ����ã�����3��0�汾ǰҪ��
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

	}

	public void setCamera(Camera camera) {

		mCamera = camera;
		if (mCamera != null) {
			mSupportedPreviewSizes = mCamera.getParameters()
					.getSupportedPreviewSizes();
			requestLayout();
		}
	}

	/**
	 * SurfaceView����������û������Ϊ����ͷ��PreviewView�������˹ٷ�Դ��
	 * 
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {

		try {
			if (null != mCamera) {
				mCamera.setPreviewDisplay(holder);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
			Log.d("TAG",
					"Error setting camera preview display: " + e1.getMessage());
		}
		try {
			if (null != mCamera) {
				mCamera.startPreview();
			}

			Log.d("TAG", "surfaceCreated successfully! ");
		} catch (Exception e) {
			Log.d("TAG", "Error setting camera preview: " + e.getMessage());
		}
	}

	/**
	 * ������SurfaceView��ı��С����ת����Ӧ��������Ӧ���¼����������ǳ���̶������Լ򵥵����� .
	 * �����¸�ʽ��֮ǰҪStopPreview
	 * 
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

		Log.d("TAG", "surface changed");

		if (null == mHolder.getSurface()) {
			// ���surface������
			return;
		}

		// �ڸı����ǰstopPreview
		try {
			if (null != mCamera) {
				mCamera.stopPreview();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// ���¸�ʽ����������������Ӧ�Ĳ���

		if (null != mCamera) {
			Camera.Parameters parameters = mCamera.getParameters();
			parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);

			requestLayout();

			mCamera.setParameters(parameters);
			mCamera.setDisplayOrientation(90);
			Log.d("TAG", "camera set parameters successfully!: " + parameters);

		}
		// ��������������óߴ�

		// ���µĳߴ翪ʼԤ��
		try {
			if (null != mCamera) {

				mCamera.setPreviewDisplay(mHolder);
				mCamera.startPreview();
			}

		} catch (Exception e) {
			Log.d("TAG", "Error starting camera preview: " + e.getMessage());
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d("TAG", "surfaceDestroyed");

		if (null != mCamera) {
			mCamera.stopPreview();
		}

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		// ��дonMeasure������Ѱ������ʵ�preview��С
		final int width = resolveSize(getSuggestedMinimumWidth(),
				widthMeasureSpec);
		final int height = resolveSize(getSuggestedMinimumHeight(),
				heightMeasureSpec);
		setMeasuredDimension(width, height);

		if (mSupportedPreviewSizes != null) {
			mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width,
					height);
		}
	}

	private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
		final double ASPECT_TOLERANCE = 0.1;
		double targetRatio = (double) w / h;
		if (sizes == null)
			return null;

		Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;

		int targetHeight = h;

		// �ҵ��ʺ�Ŀ�������PreviewSize
		for (Size size : sizes) {
			double ratio = (double) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
				continue;
			if (Math.abs(size.height - targetHeight) < minDiff) {
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}

		// ��������ҵ����ֱ���
		if (optimalSize == null) {
			minDiff = Double.MAX_VALUE;
			for (Size size : sizes) {
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}
		return optimalSize;
	}

}