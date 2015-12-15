package com.hangjiang.puzzle.adapter;

import java.util.List;

import com.hangjiang.puzzle.R;
import com.hangjiang.puzzle.util.ScreenUtil;

import android.R.color;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class ImageGridAdapter extends BaseAdapter {
	
	private List<Bitmap> mPicList;
	private Context mContext;

	public ImageGridAdapter(Context context,List<Bitmap> picList){
		mPicList = picList;
		mContext = context;
	}

	@Override
	public int getCount() {
		return mPicList.size();
	}

	@Override
	public Object getItem(int position) {
		return mPicList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView iv_pic_item = null;
		View view = null;
		int density = (int) ScreenUtil.getDeviceDensity(mContext);
		view = LayoutInflater.from(mContext).inflate(R.layout.grid_item, null);
		iv_pic_item = (ImageView) view.findViewById(R.id.iv_item);
		iv_pic_item.setScaleType(ImageView.ScaleType.FIT_XY);
		iv_pic_item.setLayoutParams(new RelativeLayout.LayoutParams(80 * density,100 * density));
		
//		if(convertView == null){
//			iv_pic_item = new ImageView(mContext);
//			iv_pic_item.setLayoutParams(new GridView.LayoutParams(80 * density,100 * density));
//			iv_pic_item.setScaleType(ImageView.ScaleType.FIT_XY);
//		}else{
//			view = convertView;
//		}
		
		iv_pic_item.setImageBitmap(mPicList.get(position));
		
		return view;
	}

}
