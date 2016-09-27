package com.sensetime.stlivenesslibrary.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.sensetime.stlivenesslibrary.R;

public class CircleTimeView extends View implements ITimeViewBase {

	private final float DPTOPX_SCALE = getResources().getDisplayMetrics().density;
	private int mTime = DEFAULT_MAX_TIME;

	private int mStartDegree = -90;
	private int mCurrentDegree = 360;

	private int mCircleColor;

	private int mTextColor;

	private int mRedusColor;

	private Paint mTextPaint;

	private Paint mRedusPaint;

	private RectF mOver = new RectF();

	private float mCircleWidth = DEFAULT_CIRCLE_WIDTH;

	private int mRedusAlpha = 175;

	private float mTextSize = DEFAULT_TEXT_SIZE;

	private String mTimeInfo = "";

	private static final int DEFAULT_CIRCLE_WIDTH = 7;

	private static final int DEFAULT_MAX_TIME = 9;

	private static final int DEFAULT_TEXT_SIZE = 20;

	private static final int DEFAULT_CIRCLE_COLOR = Color.argb(235, 74, 138, 255);
	private static final int DEFAULT_TEXT_COLOR = Color.argb(255, 255, 255, 255);
	private static final int DEFAULT_RADUS_COLOR = Color.argb(0, 255, 255, 255);

	public CircleTimeView(Context context) {
		super(context);
		init(null, 0);
	}

	public CircleTimeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs, 0);
	}

	public CircleTimeView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs, defStyle);
	}

	private void init(AttributeSet attrs, int defStyle) {
		final TypedArray attrArray = getContext()
						.obtainStyledAttributes(attrs, R.styleable.CircleTimeView, defStyle, 0);
		initAttr(attrArray);
		initPaint();
		if (attrArray != null) {
			attrArray.recycle();
		}

	}

	private void initAttr(TypedArray attrArray) {
		mCircleWidth = (int) (attrArray.getInt(R.styleable.CircleTimeView_circle_width, DEFAULT_CIRCLE_WIDTH) * DPTOPX_SCALE);
		mTime = (int) (attrArray.getInt(R.styleable.CircleTimeView_max_time, DEFAULT_MAX_TIME));
		mTextSize = (int) (attrArray.getInt(R.styleable.CircleTimeView_text_size, DEFAULT_TEXT_SIZE) * DPTOPX_SCALE);
		String tempColor = attrArray.getString(R.styleable.CircleTimeView_circle_color);
		if (tempColor != null) {
			try {
				mCircleColor = Color.parseColor(tempColor);
			} catch (IllegalArgumentException e) {
				mCircleColor = DEFAULT_CIRCLE_COLOR;
			}
		}

		tempColor = attrArray.getString(R.styleable.CircleTimeView_text_color);
		if (tempColor != null) {
			try {
				mTextColor = Color.parseColor(tempColor);
			} catch (IllegalArgumentException e) {
				mTextColor = DEFAULT_TEXT_COLOR;
			}
		}

		tempColor = attrArray.getString(R.styleable.CircleTimeView_redus_color);
		if (tempColor != null) {
			try {
				mRedusColor = Color.parseColor(tempColor);
			} catch (IllegalArgumentException e) {
				mRedusColor = DEFAULT_RADUS_COLOR;
			}
		}
		mRedusAlpha = Color.alpha(mRedusColor);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		initOver();

		canvas.drawCircle(this.getWidth() / 2f, this.getHeight() / 2, (float) (this.getWidth() / 2f), mRedusPaint);

		Rect rect = new Rect();

		mTextPaint.getTextBounds(mTimeInfo, 0, mTimeInfo.length(), rect);

		canvas.drawText(mTimeInfo, this.getWidth() / 2f - rect.width() / 1.5f,
						this.getHeight() / 2 + rect.height() / 2, mTextPaint);

	}

	private void initPaint() {

		mRedusPaint = new Paint();
		mRedusPaint.setAntiAlias(true);
		mRedusPaint.setColor(mCircleColor);
		mRedusPaint.setStrokeWidth(mCircleWidth);
		mRedusPaint.setStyle(Style.FILL);

		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setColor(mTextColor);
		mTextPaint.setStyle(Style.FILL);
		mTextPaint.setTextSize(mTextSize);
	}

	private void initOver() {
		mOver.top = mCircleWidth;
		mOver.left = mCircleWidth;
		mOver.bottom = this.getHeight() - mCircleWidth;
		mOver.right = this.getWidth() - mCircleWidth;
	}

	public void setTime(int time) {
		mTime = time;
	}

	public void setProgress(float currentTime) {
		mCurrentDegree = (int) (currentTime > mTime ? 0 : ((mTime - currentTime) / mTime * 360));
		mTimeInfo = (int) (mTime - currentTime) + "";
		invalidate();
	}

	@Override
	public void hide() {
		this.setVisibility(View.GONE);
	}

	@Override
	public void show() {
		this.setVisibility(View.VISIBLE);
	}

	@Override
	public int getMaxTime() {
		return mTime;
	}

}
