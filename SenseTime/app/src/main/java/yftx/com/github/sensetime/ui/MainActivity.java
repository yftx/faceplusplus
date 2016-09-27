package yftx.com.github.sensetime.ui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sensetime.stlivenesslibrary.LivenessDetector;
import com.sensetime.stlivenesslibrary.ui.LivenessActivity;

import yftx.com.github.sensetime.R;
import yftx.com.github.sensetime.customview.CustomButton;
import yftx.com.github.sensetime.util.Constants;
import yftx.com.github.sensetime.util.Utils;

public class MainActivity extends AppCompatActivity {
	private TextView detectListText, versionText, resultNote;
	private ImageView livenessIcon;
	private CustomButton moreImageView;
	private Button testBtn;
	private LinearLayout setDetectList;
	private static final int SET_DETECT_LIST_REQUEST_CODE = 0;
	private static final int START_DETECT_REQUEST_CODE = 1;
	private String sequence;
	private static final int PERMISSION_REQUEST_CAMERA = 0;
	private RelativeLayout resultLayout;
	public static String storageFolder;
	private Thread mThread = null;
	private final int MSGTYPE_CAMERA_CAN_USE = 1;
	private final int MSGTYPE_CAMERA_CAN_NOT_USE = 0;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSGTYPE_CAMERA_CAN_USE:
				testBtn.setClickable(true);
				startLiveness();
				break;
			case MSGTYPE_CAMERA_CAN_NOT_USE:
				testBtn.setClickable(true);
				Toast.makeText(MainActivity.this,
						getResources().getString(R.string.camera_refuse),
						Toast.LENGTH_SHORT).show();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    setTheme(R.style.NoActionBarShadowTheme);
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		resultNote = (TextView) findViewById(R.id.resultNote);
		versionText = (TextView) findViewById(R.id.versionText);
		versionText.setText("STLivenessLibrary " + LivenessDetector.getSDKVersion());
		detectListText = (TextView) findViewById(R.id.detectListText);
		sequence = detectListText.getText().toString();
		storageFolder = Utils.storageFolder;
		livenessIcon = (ImageView) findViewById(R.id.livenessIcon);
		resultLayout = (RelativeLayout) findViewById(R.id.resultLayout);
		moreImageView = (CustomButton) findViewById(R.id.moreImageView);
		moreImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, ShowImageActivity.class);
				startActivity(intent);
			}
		});
		setDetectList = (LinearLayout) findViewById(R.id.setDetectList);
		setDetectList.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, SetDetectListActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("DETECT_LIST", detectListText.getText().toString());
				intent.putExtras(bundle);
				startActivityForResult(intent, SET_DETECT_LIST_REQUEST_CODE);
			}
		});
		setDetectList.setVisibility(View.GONE);
		testBtn = (Button) findViewById(R.id.test);
		testBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isMarshmallow()) {
					if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
						// Request the permission. The result will be received
						// in onRequestPermissionResult()
						requestPermissions(
								new String[] { Manifest.permission.CAMERA },
								PERMISSION_REQUEST_CAMERA);
					} else {
						// Permission is already available, start camera preview
						startLiveness();
					}
				} else {
					if (mThread != null && mThread.isAlive()) {
						return;
					} else {
						mThread = new Thread(new Runnable() {
							@Override
							public void run() {
								testBtn.setClickable(false);
								if (cameraIsCanUse()) {
									handler.sendEmptyMessage(MSGTYPE_CAMERA_CAN_USE);
								} else {
									handler.sendEmptyMessage(MSGTYPE_CAMERA_CAN_NOT_USE);
								}
							}
						});
					}
					mThread.start();
				}
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mThread != null) {
			try {
				mThread.join(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		testBtn.setClickable(true);
	}

	protected void onSaveInstanceState(Bundle outState) {
		outState.putString(LivenessActivity.EXTRA_MOTION_SEQUENCE, sequence);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		String sequenceString = savedInstanceState.getString(LivenessActivity.EXTRA_MOTION_SEQUENCE);
		if (!TextUtils.isEmpty(sequenceString)) {
			sequence = sequenceString;
			detectListText.setText(sequence);
		}
		super.onRestoreInstanceState(savedInstanceState);
	}

	private void startLiveness() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String outputType = sp.getString(Constants.OUTPUTTYPE, Constants.MULTIIMAGE);
		String complexity = sp.getString(Constants.COMPLEXITY, Constants.NORMAL);
		boolean soundNotice = sp.getBoolean(Constants.NOTICE, true);
		SharedPreferences detectSp = getApplicationContext().getSharedPreferences(Constants.DETECTLIST, MODE_PRIVATE);
		String detectList = detectSp.getString(Constants.DETECTLIST, Constants.DEFAULTDETECTLIST);
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, LivenessActivity.class);
		/**
		 * EXTRA_MOTION_SEQUENCE 动作检测序列配置，支持四种检测动作， BLINK(眨眼), MOUTH（张嘴）, NOD（点头）, YAW（摇头）, 各个动作以空格隔开。 推荐第一个动作为BLINK.
		 * 默认配置为"BLINK MOUTH NOD YAW"
		 */
		intent.putExtra(LivenessActivity.EXTRA_MOTION_SEQUENCE, detectList);
		/**
		 * SOUND_NOTICE 配置, 传入的soundNotice为boolean值，true为打开, false为关闭, 默认为true.
		 */
		intent.putExtra(LivenessActivity.SOUND_NOTICE, soundNotice);
		/**
		 * OUTPUT_TYPE 配置, 传入的outputType类型为singleImg （单图）或 multiImg （多图）, 默认为multiImg.
		 */
		intent.putExtra(LivenessActivity.OUTPUT_TYPE, outputType);
		/**
		 * COMPLEXITY 配置, 传入的complexity类型, 支持四种难度:easy, normal, hard, hell.默认为normal.
		 */
		intent.putExtra(LivenessActivity.COMPLEXITY, complexity);
		File livenessFolder = new File(storageFolder);
		if (!livenessFolder.exists()) {
			livenessFolder.mkdirs();
		}
		// 开始检测之前请删除文件夹下保留的文件
		deleteFiles(storageFolder);
		/**
		 * EXTRA_RESULT_PATH 配置， 传入的storageFolder为sdcard下目录, 为了保存检测结果文件, 传入之前请确保该文件夹存在。
		 */
		intent.putExtra(LivenessActivity.EXTRA_RESULT_PATH, storageFolder);
		startActivityForResult(intent, START_DETECT_REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 0:
			switch (resultCode) {
			case RESULT_OK:
				Bundle bundle = data.getExtras();
				sequence = bundle.getString("DETECT_LIST");
				detectListText.setText(sequence);
				break;
			case RESULT_CANCELED:
				break;
			default:
				break;
			}
			break;
		case 1:
			switch (resultCode) {
			case RESULT_OK:
				resultNote.setBackgroundColor(getResources().getColor(R.color.background_color));
				Bundle b = data.getExtras();
				livenessIcon.setVisibility(View.GONE);
				resultLayout.setVisibility(View.VISIBLE);
				if (Utils.getImageListName(storageFolder) != null && Utils.getImageListName(storageFolder).size() > 0) {
					if (Utils.getLoacalBitmap(storageFolder + Utils.getImageListName(storageFolder).get(0)) != null) {
						Drawable drawable = new BitmapDrawable(Utils.getLoacalBitmap(storageFolder
										+ Utils.getImageListName(storageFolder).get(0)));
						resultLayout.setBackgroundDrawable(drawable);
						moreImageView.setBackgroundDrawable(new BitmapDrawable(Utils.getLoacalBitmap(storageFolder
										+ Utils.getImageListName(storageFolder).get(0))));
					}
				} else {
					moreImageView.setVisibility(View.INVISIBLE);
				}
				moreImageView.setText(Utils.getImageCountInPath(storageFolder)
								+ getResources().getString(R.string.zhang));
				resultNote.setText(getResources().getString(R.string.pass_liveness));
				break;
			case RESULT_CANCELED:
				resultNote.setBackgroundColor(getResources().getColor(R.color.err_color));
				resultLayout.setVisibility(View.GONE);
				livenessIcon.setVisibility(View.VISIBLE);
				resultNote.setText(getResources().getString(R.string.cancel_liveness));
				break;
			default:
				resultNote.setBackgroundColor(getResources().getColor(R.color.err_color));
				resultNote.setText(getResources().getString(R.string.failed_init_sdk));
				livenessIcon.setVisibility(View.VISIBLE);
				resultLayout.setVisibility(View.GONE);
				break;
			}
			break;
		}

	}

	public static void writeToFile(String fileName, String input) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(fileName), true));
			writer.write(input + "\n");
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static boolean isMarshmallow() {
		return Build.VERSION.SDK_INT >= 23;
	}

	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

		if (requestCode == PERMISSION_REQUEST_CAMERA) {
			// Request for camera permission.
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				// Permission has been granted. Start camera preview Activity.
				startLiveness();
			} else {
				// Permission request was denied.
				Utils.ToastUtil(this, getResources().getString(R.string.camera_refuse), null);
			}
		}
	}

	public static void deleteFiles(String folderPath) {
		File dir = new File(folderPath);
		if (dir == null || !dir.exists() || !dir.isDirectory() || dir.listFiles() == null)
			return;
		for (File file : dir.listFiles()) {
			if (file.isFile())
				file.delete();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.background_color)));
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setElevation(0);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.liveness_settings:
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, SettingsActivity.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public boolean cameraIsCanUse() {
		boolean isCanUse = true;
		Camera mCamera = null;
		try {
			mCamera = Camera.open();
			Camera.Parameters mParameters = mCamera.getParameters();
			mCamera.setParameters(mParameters);
		} catch (Exception e) {
			isCanUse = false;
		}
		if (mCamera != null) {
			try {
				mCamera.release();
			} catch (Exception e) {
				e.printStackTrace();
				return isCanUse;
			}
		}
		return isCanUse;
	}

}
