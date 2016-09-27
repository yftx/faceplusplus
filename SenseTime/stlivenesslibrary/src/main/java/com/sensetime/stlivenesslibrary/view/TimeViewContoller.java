package com.sensetime.stlivenesslibrary.view;

import android.os.CountDownTimer;

public class TimeViewContoller {
	
	private ITimeViewBase mTimeView;
	private CountDownTimer mCountDownTimer;
	private float mCurrentTime;
	private int mMaxTime;
	private boolean mStop;
	private CallBack mCallBack; 
	
	public interface CallBack{
		public void onTimeEnd();
	}
	
	public TimeViewContoller(ITimeViewBase view){
		mTimeView = view;
		mMaxTime = mTimeView.getMaxTime();
		mCountDownTimer = new CountDownTimer(mMaxTime * 1000, 50) {
	
	@Override
			public void onTick(long millisUntilFinished) {
				mCurrentTime = mMaxTime - millisUntilFinished / 1000.0f;
				mTimeView.setProgress(mCurrentTime);
		}
			
			@Override
			public void onFinish() {
			onTimeEnd();
		}
		};
	}
	
	public void stop(){
		mStop = true;
		mCountDownTimer.cancel();
	}
	
	public void start(){
		start(true);
	}
	
	public void start(boolean again){
		if(!again){
			if(!mStop){
				return;
			}
			mStop = false;
			if(mCurrentTime > mMaxTime){
				onTimeEnd();
				return;
			}
			mCountDownTimer.cancel();
			mCountDownTimer.start();
		}else{
			reset();
		}
	}
	
	private void onTimeEnd() {
		hide();	
		if(null != mCallBack){
			mCallBack.onTimeEnd();
		}
	}

	public void setCallBack(CallBack callback){
		mCallBack = callback;
	}
	
	private void reset(){
		show();
		mCurrentTime = 0;
		mCountDownTimer.cancel();
		mCountDownTimer.start();
	}
	
	public void hide(){
		mStop = true;
		mCountDownTimer.cancel();
		mTimeView.hide();
	}
	
	public void show(){
		mStop = false;
		mTimeView.show();
	}
	
}
