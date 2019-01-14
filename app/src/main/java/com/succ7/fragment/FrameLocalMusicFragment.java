package com.succ7.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.succ7.util.Constant;
import com.succ7.yueting.R;

/**
 * 本地音乐列表
 * 
 * @author zhongcj(QQ:527633405) 2015-8-11
 * 
 */
public class FrameLocalMusicFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.frame_for_nested_fragment,
				container, false);

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Bundle bundle = new Bundle();
		bundle.putInt(Constant.PARENT, Constant.START_FROM_LOCAL_MUSIC);

		getChildFragmentManager()
				.beginTransaction()
				.replace(
						R.id.frame_for_nested_fragment,
						Fragment.instantiate(getActivity(),
								TrackBrowserFragment.class.getName(), bundle))
				.commit();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		
	}
}
