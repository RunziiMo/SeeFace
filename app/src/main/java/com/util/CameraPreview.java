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
 * 相机预览类，继承SurfaceView
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
	 * 初始化。获取SurFaceView的surfaveHolder,并且设置CallBack函数类为
	 * 实现了SurfaceHolder.Callback的该SurfaceView
	 * 
	 */
	@SuppressWarnings("deprecation")
	private void init() {
		Log.d("TAG", "CameraPreview initialize");

		mHolder = getHolder();
		mHolder.addCallback(this);
		// 过期的设置，但在3。0版本前要用
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
	 * SurfaceView创建，但是没有设置为摄像头的PreviewView，参阅了官方源码
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
	 * 如果你的SurfaceView会改变大小或旋转，就应该设置相应的事件，但是我们程序固定了所以简单的设置 .
	 * 在重新格式化之前要StopPreview
	 * 
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

		Log.d("TAG", "surface changed");

		if (null == mHolder.getSurface()) {
			// 如果surface不存在
			return;
		}

		// 在改变参数前stopPreview
		try {
			if (null != mCamera) {
				mCamera.stopPreview();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 重新格式化你的相机，设置相应的参数

		if (null != mCamera) {
			Camera.Parameters parameters = mCamera.getParameters();
			parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);

			requestLayout();

			mCamera.setParameters(parameters);
			mCamera.setDisplayOrientation(90);
			Log.d("TAG", "camera set parameters successfully!: " + parameters);

		}
		// 这里可以用来设置尺寸

		// 用新的尺寸开始预览
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

		// 重写onMeasure函数来寻找最合适的preview大小
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

		// 找到适合目标比例的PreviewSize
		for (Size size : sizes) {
			double ratio = (double) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
				continue;
			if (Math.abs(size.height - targetHeight) < minDiff) {
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}

		// 如果不能找到这种比例
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