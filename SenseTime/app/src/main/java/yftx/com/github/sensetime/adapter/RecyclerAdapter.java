package yftx.com.github.sensetime.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import yftx.com.github.sensetime.R;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {

	private Bitmap[] mDatas;
	private Context mContext;
	private LayoutInflater mInflater;
	private View.OnClickListener mOnItemClickListener;

	public RecyclerAdapter(Context context, Bitmap[] bitmaps) {
		mContext = context;
		mDatas = bitmaps;
		mInflater = LayoutInflater.from(mContext);
	}

	public void setOnItemClickListener(View.OnClickListener onItemClickListener) {
		mOnItemClickListener = onItemClickListener;
	}

	@Override
	public int getItemCount() {
		return mDatas.length;
	}

	@Override
	public void onBindViewHolder(RecyclerViewHolder holder, int position) {
	    if (position >=0 && position < mDatas.length) {
	        holder.customImageView.setImageBitmap(mDatas[position]);
	    }
		if (mOnItemClickListener != null) {
			holder.customImageView.setTag(position);
			holder.customImageView.setOnClickListener(mOnItemClickListener);
		}
	}

	@Override
	public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = mInflater.inflate(R.layout.recyclerview_item, parent, false);
		RecyclerViewHolder holder = new RecyclerViewHolder(view);
		return holder;
	}

	class RecyclerViewHolder extends ViewHolder {
	    ImageView customImageView;

		public RecyclerViewHolder(View view) {
			super(view);
			customImageView = (ImageView)view.findViewById(R.id.customview_item);
		}
	}
}
