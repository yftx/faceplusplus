package com.sensetime.stlivenesslibrary.ui;

import java.util.List;

import android.app.Fragment;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.sensetime.stlivenesslibrary.R;
import com.sensetime.stlivenesslibrary.util.Constants;

/**
 * 
 * @author MatrixCV
 * 
 * Camera相关设置
 *
 */
public class CameraOverlapFragment extends Fragment {

	public static final boolean DEBUG = true;
	private static final boolean DEBUG_PREVIEW_SIZE = false;
	private static final String TAG = "CameraOverlapFragment";
	protected Camera mCamera = null;
	protected CameraInfo mCameraInfo = null;
	protected boolean mIsCameraInit;
	protected SurfaceView mSurfaceview = null;
	protected SurfaceView mOverlap = null;
	protected SurfaceHolder mSurfaceHolder = null;
	Camera.PreviewCallback mPreviewCallback;
	private Matrix mMatrix = new Matrix();
	protected int CameraFacing = CameraInfo.CAMERA_FACING_FRONT;
	private boolean mHasFrontCamera;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.linkface_fragment_camera_overlap, container,
				false);
		initView(view);
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(mIsCameraInit && mCamera == null){
			openCamera(CameraFacing);
		}
	}
	
	@Override
	public void onPause(){
		if(DEBUG){
			Log.i(TAG, "onPause");
		}
		releaseCamera();
		super.onPause();
	}
	
	private void openCamera(int CameraFacing){
		releaseCamera();
		mHasFrontCamera = false;
		openCameraFront();
		if (!mHasFrontCamera) {
			openCamera(true);
		}
		try {
			mCamera.setPreviewDisplay(mSurfaceHolder);
			initCameraParameters();
		} catch (Exception ex) {
			releaseCamera();
			onOpenCameraError(mHasFrontCamera);
		}
	}
	
	private void openCameraFront(){
		openCamera(false);
	}
	
	private void openCamera(boolean any){
		CameraInfo info = new CameraInfo();
		for(int i = 0; i < Camera.getNumberOfCameras(); i++) {
			Camera.getCameraInfo(i, info);
			boolean front = info.facing == CameraFacing;
			if(any || front) {
				if(front){
					mHasFrontCamera = true;
				}
				try{
					mCamera = Camera.open(i);
					mCameraInfo = info;
				} catch(RuntimeException e) {
					e.printStackTrace();
					if (mCamera != null) {
						mCamera.release();
						mCamera = null;
					}
					continue;
				}
				break;
			}
		}
	}
	
	private void onOpenCameraError(boolean hasFrontCamera) {
		onErrorHappen(LivenessActivity.RESULT_CAMERA_ERROR_NOPRERMISSION_OR_USED);
	}
	
	protected void onErrorHappen(int resultCode) {
		if(getActivity() == null){
			if(DEBUG){
				Log.e(TAG, "onOpenCameraError getActivity() = null");
			}
			return;
		}
		((LivenessActivity)getActivity()).onErrorHappen(resultCode);
	}

	private void initView(View view){
		mSurfaceview = (SurfaceView) view.findViewById(R.id.surfaceViewCamera);
		mOverlap = (SurfaceView) view.findViewById(R.id.surfaceViewOverlap);
		mOverlap.setZOrderOnTop(true);
		mOverlap.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		mSurfaceHolder = mSurfaceview.getHolder();
		mSurfaceHolder.addCallback(mSurfaceViewCallBack);
	}

	public void setPreviewCallback(Camera.PreviewCallback previewCallback) {
		mPreviewCallback = previewCallback;
		if(mCamera != null){
			mCamera.setPreviewCallback(previewCallback);
		}
	}

	public Matrix getMatrix() {
		return mMatrix;
	}
	
	private SurfaceHolder.Callback mSurfaceViewCallBack = new SurfaceHolder.Callback() {
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format,
				int width, int height) {
			if(DEBUG){
				Log.i(TAG, "SurfaceHolder.Callback?Surface Changed " + width
						+ "x" + height);
			}
			mMatrix.reset();
			mMatrix.setScale(width/(float)Constants.PREVIEW_HEIGHT, height/(float)Constants.PREVIEW_WIDTH);
			initCameraParameters();
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			mCamera = null;
			openCamera(CameraFacing);
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			if(DEBUG){
				Log.d(TAG, "SurfaceHolder.Callback surfaceDestroyed");
			}
			releaseCamera();
			mIsCameraInit = false;
		}
	};
	
	protected void initCameraParameters() {
		mIsCameraInit = true;
		if (mCamera == null) {
			if(DEBUG){
				Log.e(TAG,"initCameraParameters mCamera == null");
			}
			return;
		}

		try {
			Camera.Parameters parameters = mCamera.getParameters();
			parameters.setPreviewFormat(ImageFormat.NV21);
			if(DEBUG_PREVIEW_SIZE){
				debugPreviewSize();
			}
			parameters.setPreviewSize(Constants.PREVIEW_WIDTH,
					Constants.PREVIEW_HEIGHT);
			if (parameters.getMinExposureCompensation() < 0 && parameters.getMaxExposureCompensation() > 0
					&& (Math.abs(parameters.getMinExposureCompensation()) == parameters.getMaxExposureCompensation())) {
				parameters.setExposureCompensation(0);
			}
			if (DEBUG){
				 Log.d(TAG, "min:" + parameters.getMinExposureCompensation() + "max:" +parameters.getMaxExposureCompensation());
			}
			if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
				parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
			}
			parameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
//			if(parameters.isAutoExposureLockSupported()){
//				parameters.setAutoExposureLock(true);
//			}
//			if(parameters.isAutoWhiteBalanceLockSupported()){
//				parameters.setWhiteBalance(Parameters.WHITE_BALANCE_AUTO);
//			}
			if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
				parameters.set("orientation", "portrait");
				parameters.set("rotation", 90);
				if (mCameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT && mCameraInfo.orientation == 90) {
					mCamera.setDisplayOrientation(270);
				} else {
					mCamera.setDisplayOrientation(90);
				}
				if(DEBUG){
					Log.d(TAG, "orientation: portrait");
				}
			} else {
				parameters.set("orientation", "landscape");
				mCamera.setDisplayOrientation(0);
				if(DEBUG){
					Log.d(TAG, "orientation: landscape");
				}
			}
			
			mCamera.setParameters(parameters);
			mCamera.setPreviewCallback(mPreviewCallback);
			mCamera.startPreview();
			
			if(DEBUG_PREVIEW_SIZE){
				debugPreviewSizeAfterSetParameter();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void releaseCamera(){
		if (null == mCamera) {
			return;
		}
		try {
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();
			mCamera.release();
		} catch (Exception e) {
			// TODO: handle exception
		}
		mCamera = null;
	}
	
	private void debugPreviewSizeAfterSetParameter() {
		if(mCamera == null || mCamera.getParameters() == null){
			if(DEBUG){
				Log.e(TAG, "debugPreviewSizeAfterSetParameter mCamera == null or getParameters = null");
			}
			return ;
		}
		Size csize = mCamera.getParameters().getPreviewSize();
		if(csize == null){
			if(DEBUG){
				Log.e(TAG, "debugPreviewSizeAfterSetParameter csize == null");
			}
			return;
		}
		if(DEBUG){
			Log.d(TAG , "initCamera after setting, previewSize:width: "+ csize.width + " height: " + csize.height);
		}
		
	}

	private void debugPreviewSize(){
		if(mCamera == null){
			if(DEBUG){
				Log.e(TAG, "debugPreviewSize mCamera == null");
			}
			return ;
		}
		List<Size> previewSizes = mCamera.getParameters()
				.getSupportedPreviewSizes();
		for (int i = 0; i < previewSizes.size(); i++) {
			Size psize = previewSizes.get(i);
			if(DEBUG){

			}
		}
	}
}
