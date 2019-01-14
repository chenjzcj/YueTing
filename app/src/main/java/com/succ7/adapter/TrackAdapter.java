package com.succ7.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.succ7.entity.TrackInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 音乐列表适配器
 * 
 * @author zhongcj(QQ:527633405) 2015-8-13
 * 
 */
public class TrackAdapter extends BaseAdapter {
	private Context mContext = null;
	/**
	 * 数据源
	 */
	private ArrayList<TrackInfo> mData = null;
	/**
	 * 播放时为相应播放条目显示一个播放标记
	 */
	private int mActivateItemPos = -1;

	public TrackAdapter(Context context) {
		mContext = context;
		mData = new ArrayList<TrackInfo>();
	}

	public void setData(List<TrackInfo> data) {
		mData.clear();
		if (data != null) {
			mData.addAll(data);
		}
		mActivateItemPos = -1;
		notifyDataSetChanged();
	}

	public ArrayList<TrackInfo> getData() {
		return mData;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}

}
