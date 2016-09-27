package yftx.com.github.sensetime.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import yftx.com.github.sensetime.R;


/**
 * Created by tracy on 3/15/16.
 */
public class DetectListAdapter extends BaseAdapter {
	private Context context;
	private List list;
	private ViewHolder holder;

	public DetectListAdapter(Context context, List list) {
		this.context = context;
		this.list = list;
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@SuppressLint("InflateParams")
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter, null);
			holder = new ViewHolder();
			holder.text = (TextView) convertView.findViewById(R.id.textView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.text.setText(list.get(position).toString());
		return convertView;
	}

	static class ViewHolder {
		TextView text;
	}

}
