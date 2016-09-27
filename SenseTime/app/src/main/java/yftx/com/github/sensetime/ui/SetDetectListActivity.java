package yftx.com.github.sensetime.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import yftx.com.github.sensetime.R;
import yftx.com.github.sensetime.adapter.DetectListAdapter;
import yftx.com.github.sensetime.util.Constants;
import yftx.com.github.sensetime.util.Utils;


/**
 * Created by tracy on 3/10/16.
 */
public class SetDetectListActivity extends Activity implements View.OnClickListener {
	private Button blinkBtn, nodBtn, mouthBtn, yawBtn, doneBtn;
	private ListView listView;
	private List list;
	private DetectListAdapter myadapter;
	private StringBuffer mStringBuffer = new StringBuffer();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setdetectlist);
		init();
		list = new ArrayList();
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		SharedPreferences detectSp = getApplicationContext().getSharedPreferences(Constants.DETECTLIST, MODE_PRIVATE);
		String detectList = detectSp.getString(Constants.DETECTLIST, Constants.DEFAULTDETECTLIST);
		String[] splitStrings = detectList.split("\\s+");
		Collections.addAll(list, splitStrings);
		myadapter = new DetectListAdapter(this, list);
		listView.setAdapter(myadapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0) {
					Utils.ToastUtil(SetDetectListActivity.this, getResources().getString(R.string.first_action), null);
				} else {
					list.remove(position);
					myadapter.notifyDataSetChanged();
				}
			}
		});
	}

	public void init() {
		blinkBtn = (Button) findViewById(R.id.blinkBtn);
		nodBtn = (Button) findViewById(R.id.nodBtn);
		mouthBtn = (Button) findViewById(R.id.mouthBtn);
		yawBtn = (Button) findViewById(R.id.yawBtn);
		doneBtn = (Button) findViewById(R.id.doneBtn);
		listView = (ListView) findViewById(R.id.lv_show);
		blinkBtn.setOnClickListener(this);
		nodBtn.setOnClickListener(this);
		mouthBtn.setOnClickListener(this);
		yawBtn.setOnClickListener(this);
		doneBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (list.size() < 10) {
			switch (v.getId()) {
			case R.id.blinkBtn:
				list.add(getResources().getString(R.string.blink));
				myadapter.notifyDataSetChanged();
				break;
			case R.id.nodBtn:
				list.add(getResources().getString(R.string.nod));
				myadapter.notifyDataSetChanged();
				break;
			case R.id.mouthBtn:
				list.add(getResources().getString(R.string.mouth));
				myadapter.notifyDataSetChanged();
				break;
			case R.id.yawBtn:
				list.add(getResources().getString(R.string.yaw));
				myadapter.notifyDataSetChanged();
				break;
			case R.id.doneBtn:
				for (int i = 0; i < list.size(); i++) {
					mStringBuffer.append(list.get(i).toString() + " ");
				}
				SharedPreferences sp = getApplicationContext().getSharedPreferences(Constants.DETECTLIST, MODE_PRIVATE);
				Editor editor = sp.edit();
				editor.putString(Constants.DETECTLIST, mStringBuffer.toString());
				editor.commit();
				finish();
				break;
			}
		} else {
			if (v.getId() == R.id.doneBtn) {
				for (int i = 0; i < list.size(); i++) {
					mStringBuffer.append(list.get(i).toString() + " ");
				}
				SharedPreferences sp = getApplicationContext().getSharedPreferences(Constants.DETECTLIST, MODE_PRIVATE);
				Editor editor = sp.edit();
				editor.putString(Constants.DETECTLIST, mStringBuffer.toString());
				editor.commit();
				finish();
			} else {
				Utils.ToastUtil(this, getResources().getString(R.string.max_action), null);
			}
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}
}
