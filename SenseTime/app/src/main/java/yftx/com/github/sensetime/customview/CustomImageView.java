package yftx.com.github.sensetime.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import yftx.com.github.sensetime.R;


/**
 * Created by Administrator on 2016/7/11. 自定义ImageView控件，实现了圆角和边框，以及按下变色
 */

public class CustomImageView extends ImageView {
	private Paint mPressPaint;

	private int mWidth;
	private int mHeight;

	private int mPressAlpha;
	private int mPressColor;
	private int mRadius;
	private int mShapeType;
	private int mBorderWidth;
	private int mBorderColor;

	private static final int DEFAULT_PRESS_ALPHA = 64;
	private static final int DEFAULT_RADIUS = 1;
	private static final int DEFAULT_SHAPE_TYPE = 1;
	private static final int DEFAULT_BORDER_WIDTH = 2;

	public CustomImageView(Context context) {
		super(context);
		init(context, null);
	}

	public CustomImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public CustomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		// 初始化默认值
		mPressAlpha = DEFAULT_PRESS_ALPHA;
		mPressColor = getResources().getColor(R.color.ml_gray);
		mRadius = DEFAULT_RADIUS;
		mShapeType = DEFAULT_SHAPE_TYPE;
		mBorderWidth = DEFAULT_BORDER_WIDTH;
		mBorderColor = getResources().getColor(R.color.white);

		// 获取控件的属性值
		if (attrs != null) {
			TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CustomImageView);
			mPressColor = array.getColor(R.styleable.CustomImageView_press_color, mPressColor);
			mPressAlpha = array.getInteger(R.styleable.CustomImageView_press_alpha, mPressAlpha);
			mRadius = array.getDimensionPixelSize(R.styleable.CustomImageView_radius, mRadius);
			mShapeType = array.getInteger(R.styleable.CustomImageView_shape_type, mShapeType);
			mBorderWidth = array.getDimensionPixelOffset(R.styleable.CustomImageView_border_width, mBorderWidth);
			mBorderColor = array.getColor(R.styleable.CustomImageView_border_color, mBorderColor);
			array.recycle();
		}

		// 按下的画笔设置
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
		// 获取当前控件的 drawable
		Drawable drawable = getDrawable();
		if (drawable == null) {
			return;
		}
		// 这里 get 回来的宽度和高度是当前控件相对应的宽度和高度（在 xml 设置）
		if (getWidth() == 0 || getHeight() == 0) {
			return;
		}
		// 获取 bitmap，即传入 imageview 的 bitmap
		Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
		drawDrawable(canvas, bitmap);
		drawPress(canvas);
		drawBorder(canvas);
	}

	private void drawDrawable(Canvas canvas, Bitmap bitmap) {
		Paint paint = new Paint();
		paint.setColor(0xffffffff);
		paint.setAntiAlias(true);
		PorterDuffXfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
		int saveFlags = Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
						| Canvas.FULL_COLOR_LAYER_SAVE_FLAG | Canvas.CLIP_TO_LAYER_SAVE_FLAG;
		canvas.saveLayer(0, 0, mWidth, mHeight, null, saveFlags);

		if (mShapeType == 0) {
			// 画遮罩，画出来就是一个和空间大小相匹配的圆
			canvas.drawCircle(mWidth / 2, mHeight / 2, mWidth / 2, paint);
		} else {
			// 当ShapeType = 1 时 图片为圆角矩形
			RectF rectf = new RectF(0, 0, getWidth(), getHeight());
			canvas.drawRoundRect(rectf, mRadius, mRadius, paint);
		}

		paint.setXfermode(xfermode);

		if (bitmap.getWidth() > 0 && bitmap.getHeight() > 0) {
        		// 空间的大小 / bitmap 的大小 = bitmap 缩放的倍数
        		float scaleWidth = ((float) getWidth()) / bitmap.getWidth();
        		float scaleHeight = ((float) getHeight()) / bitmap.getHeight();
        
        		Matrix matrix = new Matrix();
        		matrix.postScale(scaleWidth, scaleHeight);
        
        		// bitmap 缩放
        		bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		}

		// draw
		canvas.drawBitmap(bitmap, 0, 0, paint);
		paint.setXfermode(null);
		canvas.restore();
	}

	private void drawPress(Canvas canvas) {

		if (mShapeType == 0) {
			canvas.drawCircle(mWidth / 2, mHeight / 2, mWidth / 2, mPressPaint);
		} else if (mShapeType == 1) {
			RectF rectF = new RectF(0, 0, mWidth, mHeight);
			canvas.drawRoundRect(rectF, mRadius, mRadius, mPressPaint);
		}
	}

	private void drawBorder(Canvas canvas) {
		if (mBorderWidth > 0) {
			Paint paint = new Paint();
			paint.setStrokeWidth(mBorderWidth);
			paint.setStyle(Paint.Style.STROKE);
			paint.setColor(mBorderColor);
			paint.setAntiAlias(true);
			if (mShapeType == 0) {
				canvas.drawCircle(mWidth / 2, mHeight / 2, mWidth / 2, paint);
			} else {
				// 当ShapeType = 1 时 图片为圆角矩形
				RectF rectf = new RectF(0, 0, getWidth(), getHeight());
				canvas.drawRoundRect(rectf, mRadius, mRadius, paint);
			}
		}
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
		case MotionEvent.ACTION_MOVE:
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
