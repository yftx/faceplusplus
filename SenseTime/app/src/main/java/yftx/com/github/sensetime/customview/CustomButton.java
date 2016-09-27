package yftx.com.github.sensetime.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.Button;


import yftx.com.github.sensetime.R;

/**
 * @author zhaoyanmei
 * @date 2016/7/12.
 */

public class CustomButton extends Button {
	private Paint mPressPaint;

	private int mWidth;
	private int mHeight;

	private int mPressAlpha;
	private int mPressColor;
	private int mBorderWidth;
	private int mBorderColor;
	private int mShadowColor;

	private int default_press_alpha = 64;
	private int default_border_width = 1;
	private int default_press_color = getResources().getColor(R.color.ml_gray);
	private int default_border_color = getResources().getColor(R.color.white);

	public CustomButton(Context context) {
		super(context);
		init(context, null);
	}

	public CustomButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public void init(Context context, AttributeSet attrs) {
		// 初始化默认值
		mPressAlpha = default_press_alpha;
		mPressColor = default_press_color;
		mBorderWidth = default_border_width;
		mBorderColor = default_border_color;
		// 获取控件属性值
		if (attrs != null) {
			TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CustomButton);
			mPressColor = array.getColor(R.styleable.CustomButton_press_color1, mPressColor);
			mPressAlpha = array.getInteger(R.styleable.CustomButton_press_alpha1, mPressAlpha);
			mBorderWidth = array.getDimensionPixelOffset(R.styleable.CustomButton_border_width1, mBorderWidth);
			mBorderColor = array.getColor(R.styleable.CustomButton_border_color1, mBorderColor);
			mShadowColor = array.getColor(R.styleable.CustomButton_shadow_color1, mShadowColor);
			array.recycle();
		}
		// 按下画笔设置
		mPressPaint = new Paint();
		mPressPaint.setAntiAlias(true);
		mPressPaint.setStyle(Paint.Style.FILL);
		mPressPaint.setColor(mPressColor);
		mPressPaint.setAlpha(0);
		mPressPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

		setClickable(true);
		setDrawingCacheEnabled(true);
		setWillNotDraw(false);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 获取当前控件的drawable
		Drawable drawable = getBackground();
		if (drawable == null)
			return;
		if (getWidth() == 0 || getHeight() == 0)
			return;

		drawShadow(canvas);
		Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
		drawDrawable(canvas, bitmap);
		drawPress(canvas);
		drawBorder(canvas);
		drawMasker(canvas);
	}

	private void drawShadow(Canvas canvas) {
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setAntiAlias(true);
		paint.setColor(mShadowColor);
		paint.setAlpha(150);
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);

		Rect rect = new Rect(8, -8, getWidth() - 8, 20);
		canvas.drawRect(rect, paint);

		paint.setAlpha(100);
		Rect rect1 = new Rect(12, -12, getWidth() - 12, 15);
		canvas.drawRect(rect1, paint);
	}

	private void drawDrawable(Canvas canvas, Bitmap bitmap) {
		Paint paint = new Paint();
		paint.setColor(0xffffffff);
		paint.setAntiAlias(true);
		PorterDuffXfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
		int saveFlags = Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
						| Canvas.FULL_COLOR_LAYER_SAVE_FLAG | Canvas.CLIP_TO_LAYER_SAVE_FLAG;
		canvas.saveLayer(0, 0, mWidth, mHeight, null, saveFlags);

		canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
		paint.setXfermode(xfermode);

		if (bitmap.getWidth() > 0 && bitmap.getHeight() > 0 ) {
        		float scaleWidth = ((float) getWidth()) / bitmap.getWidth();
        		float scaleHeight = ((float) getHeight()) / bitmap.getHeight();
        		Matrix matrix = new Matrix();
        		matrix.postScale(scaleWidth, scaleHeight);
        
        		// bitmap缩放
        		bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		}

		// draw上去
		canvas.drawBitmap(bitmap, 0, 0, paint);
	}

	private void drawPress(Canvas canvas) {
		Rect rect = new Rect(0, 0, mWidth, mHeight);
		canvas.drawRect(rect, mPressPaint);
	}

	private void drawBorder(Canvas canvas) {
		if (mBorderWidth > 0) {
			Paint paint = new Paint();
			paint.setStrokeWidth(mBorderWidth);
			paint.setStyle(Paint.Style.STROKE);
			paint.setColor(mBorderColor);
			paint.setAntiAlias(true);

			Rect rect = new Rect(0, 0, getWidth(), getHeight());
			canvas.drawRect(rect, paint);
		}
	}

	private void drawMasker(Canvas canvas) {
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setAntiAlias(true);
		paint.setColor(Color.BLACK);
		paint.setAlpha(150);
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);

		Rect rect = new Rect(mBorderWidth, mBorderWidth, getWidth() - mBorderWidth, getHeight() - mBorderWidth);
		canvas.drawRect(rect, paint);
		paint.setColor(Color.WHITE);
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
		        20, getResources().getDisplayMetrics()));
		Paint.FontMetricsInt fontMetricsInt = paint.getFontMetricsInt();
		int baseline = (rect.bottom + rect.top - fontMetricsInt.bottom - fontMetricsInt.top) / 2;
		canvas.drawText(getText().toString(), rect.centerX(), baseline, paint);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mWidth = w;
		mHeight = h;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mPressPaint.setAlpha(mPressAlpha);
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			mPressPaint.setAlpha(0);
			invalidate();
			break;
		default:
			invalidate();
			break;
		}
		return super.onTouchEvent(event);
	}

}
