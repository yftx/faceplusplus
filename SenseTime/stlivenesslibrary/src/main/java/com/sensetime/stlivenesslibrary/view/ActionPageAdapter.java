package com.sensetime.stlivenesslibrary.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sensetime.stlivenesslibrary.R;

public class ActionPageAdapter extends PagerAdapter{
	private Context context;
	private String[] mDetectList;

	public ActionPageAdapter(Context context,String[] mDetectList){
		this.context = context;
		this.mDetectList = mDetectList;
	}	

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		// TODO Auto-generated method stub		
		View view = View.inflate(context, R.layout.action_layout, null);
		if (mDetectList[position].equalsIgnoreCase(context.getString(R.string.blink))) {				
			((TextView)view.findViewById(R.id.action_title)).setText(context.getString(R.string.note_wink));
			((ImageView)view.findViewById(R.id.image)).setImageResource(R.drawable.linkface_blink);
			
		} else if (mDetectList[position].equalsIgnoreCase(context.getString(R.string.nod))) {
			((TextView)view.findViewById(R.id.action_title)).setText(context.getString(R.string.note_nod));
			((ImageView)view.findViewById(R.id.image)).setImageResource(R.drawable.linkface_nod);
		} else if (mDetectList[position].equalsIgnoreCase(context.getString(R.string.mouth))) {
			((TextView)view.findViewById(R.id.action_title)).setText(context.getString(R.string.note_mouth));
			((ImageView)view.findViewById(R.id.image)).setImageResource(R.drawable.linkface_mouth);
		} else if (mDetectList[position].equalsIgnoreCase(context.getString(R.string.yaw))) {
			((TextView)view.findViewById(R.id.action_title)).setText(context.getString(R.string.note_shakehead));
			((ImageView)view.findViewById(R.id.image)).setImageResource(R.drawable.linkface_yaw);
		}
		AnimationDrawable aDrawable = (AnimationDrawable)
						((((ImageView)view.findViewById(R.id.image))).getDrawable());
		aDrawable.start();
		
		container.addView(view);
		return view;
	}


	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mDetectList.length;
	}

	@Override
	public boolean isViewFromObject(View view, Object obj) {
		// TODO Auto-generated method stub
		return view == obj;
	}


}
