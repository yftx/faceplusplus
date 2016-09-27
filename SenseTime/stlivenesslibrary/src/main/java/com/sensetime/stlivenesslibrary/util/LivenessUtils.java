package com.sensetime.stlivenesslibrary.util;

import java.util.Arrays;
import java.util.List;

import android.util.Log;

public class LivenessUtils {
	private static final String TAG = "LivenessUtils";
	private static final boolean DEBUG = true;
	public static final int LIVE_BLINK = 0;//眨眼
	public static final int LIVE_SMILE = 1;//张嘴
	public static final int LIVE_PITCH = 2;//点头
	public static final int LIVE_YAW = 3;
	public static final int LIVE_NONE = 4;
	public static final int LIVE_DETECTION_TYPE_COUNT = 5;
	
	public static float getRobustRange(float[] scores) {
		if(scores == null){
			if(DEBUG){
				Log.d(TAG,"getRobustRange scores == null");
			}
			return 0;
		}
		int n = scores.length;
		Arrays.sort(scores);
		if (n < 20) {
			return 0;
		} else {
			int left = (int) (n * 0.1);
			int right = (int) (n * 0.9);
			return (scores[right] - scores[left]);
		}
	}

	public static boolean judgeResult(int type, float[] scores) {
		boolean result = false;
		switch (type) {
		case LIVE_YAW: {
			float robust_yaw_score = getRobustRange(scores);
			result = robust_yaw_score > 11.0f;
			break;
		}
		case LIVE_PITCH: {
			float robust_yaw_score = getRobustRange(scores);
			result = robust_yaw_score > 8.0f;
			break;
		}
		case LIVE_NONE:
			result = false;
			break;
		}
		return result;
	}
	
	public static boolean isFaceNoFront(int type,float[] pose) {
		if (pose == null || pose.length < 4) {
			if (DEBUG) {
				Log.d(TAG, "isFaceFront pose is null or length < 4");
			}
			return false;
		}
		if ((type != LIVE_YAW && Math.abs(pose[0]) > 20) //非摇头情况下，左右摆动20
				|| (type != LIVE_PITCH && Math.abs(pose[1]) > 20) //非点头情况下，上下摆动20
				|| (pose[3] < 50)) { //倾斜50度
			return true;
		}
		return false;
	}
	
	public static float[] getArrayFromList(List<Float> list){
		if(list == null){
			if(DEBUG){
				Log.d(TAG, "getArrayFromList list == null");
			}
			return null;
		}
		Float[] array = (Float[]) list
				.toArray(new Float[list.size()]);
		float[] scoresValue = new float[array.length];
		for (int i = 0; i < scoresValue.length; i++) {
			scoresValue[i] = array[i].floatValue();
		}
		return scoresValue;
	}

	public static String getSDKVersion() {
		return "1.2";
	}
}
