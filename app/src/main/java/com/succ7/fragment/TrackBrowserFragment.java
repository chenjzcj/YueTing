package com.succ7.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore.Audio.Media;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.succ7.activity.MainContentActivity;
import com.succ7.activity.MutipleEditActivity;
import com.succ7.adapter.TrackAdapter;
import com.succ7.entity.AlbumInfo;
import com.succ7.entity.ArtistInfo;
import com.succ7.entity.FolderInfo;
import com.succ7.entity.PlaylistInfo;
import com.succ7.entity.TrackInfo;
import com.succ7.util.Constant;
import com.succ7.yueting.R;

import java.util.List;

/**
 * 读取并显示设备外部存储设备上的音乐文件
 * 
 * @author zhongcj(QQ:527633405) 2015-8-12
 * 
 */
public class TrackBrowserFragment extends Fragment implements
		OnItemClickListener, LoaderCallbacks<List<TrackInfo>> {
	private static final int MUSIC_RETRIEVE_LOADER = 0;
	// 调试用的标题
	private final String TAG = this.getClass().getSimpleName();
	private MainContentActivity mActivity;
	private SharedPreferences mSystemPreferences;
	private PopupWindow mT9KeyBoardWindow = null;
	/**
	 * 文件过滤设置改变的话重新加载显示数据
	 */
	private OnSharedPreferenceChangeListener mFilterPreferenceChangedListener = new OnSharedPreferenceChangeListener() {

		@Override
		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key) {
			if (key.equals(SettingFragment.KEY_FILTER_BY_SIZE)
					|| key.equals(SettingFragment.KEY_FILTER_BY_DURATION)) {
				// 歌曲过滤设置改变了
				if (!isDetached()
						&& Environment.getExternalStorageState().equals(
								Environment.MEDIA_MOUNTED)) {

				}
			}

		}
	};
	private InputMethodManager mInputMethodManager;
	/**
	 * 显示本地音乐的列表
	 */
	private ListView mView_ListView;
	private TextView mView_EmptyNoStorage, mView_EmptyNoSong, mView_Title;
	private ImageView mView_MenuNavigation, mView_MoreFunctions,
			mView_GoToPlayer, mView_KeyboardSwitcher;
	private LinearLayout mView_PlayAll, mView_Search, mView_MutipleChoose,
			mView_TrackOperations, mView_EmptyLoading;
	private RelativeLayout mView_SearchBar;
	private EditText mView_SearchInput;
	private Button mView_SearchCancel;
	private PopupMenu mOverflowPopupMenu;
	/** 手势检测 */
	private GestureDetector mDetector;
	/** 用来绑定数据至listview的适配器 */
	private TrackAdapter mAdapter = null;
	/** 排序规则 */
	private String mSortOrder = Media.TITLE_KEY;
	/** 弹出的搜索软键盘是否是自定义的T9键盘 */
	protected boolean mIsT9Keyboard = true;

	private ArtistInfo mArtistInfo = null;
	private FolderInfo mFolderInfo = null;
	private PlaylistInfo mPlaylistInfo = null;
	private AlbumInfo mAlbumInfo = null;

	@Override
	public void onAttach(Activity activity) {
		Log.i(TAG, "onAttach");
		super.onAttach(activity);
		if (activity instanceof MainContentActivity) {
			mActivity = (MainContentActivity) activity;
		}
		mSystemPreferences = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		mSystemPreferences
				.registerOnSharedPreferenceChangeListener(mFilterPreferenceChangedListener);

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		// 获取系统输入法管理器对象
		mInputMethodManager = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(TAG, "onCreateView");
		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.list_track,
				container, false);

		mView_ListView = (ListView) rootView
				.findViewById(R.id.listview_local_music);

		mView_EmptyNoStorage = (TextView) rootView
				.findViewById(R.id.empty_no_sdcard);
		mView_EmptyNoSong = (TextView) rootView
				.findViewById(R.id.empty_no_song);
		mView_EmptyLoading = (LinearLayout) rootView
				.findViewById(R.id.empty_loading);

		mView_MenuNavigation = (ImageView) rootView
				.findViewById(R.id.menu_navigation);

		mView_Title = (TextView) rootView.findViewById(R.id.title_of_top);

		mView_PlayAll = (LinearLayout) rootView.findViewById(R.id.btn_play_all);
		mView_Search = (LinearLayout) rootView.findViewById(R.id.btn_search);
		mView_MutipleChoose = (LinearLayout) rootView
				.findViewById(R.id.btn_mutiple_choose);

		mView_MoreFunctions = (ImageView) rootView
				.findViewById(R.id.more_functions);
		mView_GoToPlayer = (ImageView) rootView
				.findViewById(R.id.switch_to_player);

		mView_SearchBar = (RelativeLayout) rootView
				.findViewById(R.id.search_bar);
		mView_KeyboardSwitcher = (ImageView) rootView
				.findViewById(R.id.keyboard_switcher);
		mView_SearchInput = (EditText) rootView.findViewById(R.id.search_input);
		mView_SearchCancel = (Button) rootView.findViewById(R.id.cancel_search);
		mView_TrackOperations = (LinearLayout) rootView
				.findViewById(R.id.track_operations);

		mOverflowPopupMenu = new PopupMenu(getActivity(), mView_MoreFunctions);

		Bundle bundle = getArguments();
		if (bundle != null) {
			switch (bundle.getInt(Constant.PARENT)) {
			case Constant.START_FROM_LOCAL_MUSIC:
				mOverflowPopupMenu.getMenuInflater().inflate(
						R.menu.popup_local_music_list,
						mOverflowPopupMenu.getMenu());
				break;

			default:
				mOverflowPopupMenu.getMenuInflater().inflate(
						R.menu.popup_track_list, mOverflowPopupMenu.getMenu());
				break;
			}
		}

		return rootView;
	}

	/**
	 * 延迟listview的设置到activity创建时,为listview绑定数据适配器
	 */
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		Log.i(TAG, "onActivityCreated");
		super.onActivityCreated(savedInstanceState);

		initViewsSetting();
	}

	/**
	 * 初始化各个视图组件的设置
	 */
	private void initViewsSetting() {
		// 设置滑动手势
		mDetector = new GestureDetector(mActivity,
				new SimpleOnGestureListener() {
					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {
						// 从右向左滑动
						if (e1 != null && e2 != null) {
							if (e1.getX() - e2.getX() > 120) {
								mActivity.switchToPlayer();
								return true;
							}
						}
						return false;
					}
				});
		View.OnTouchListener gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (mDetector.onTouchEvent(event)) {
					return true;
				}
				return false;
			}
		};
		// listview设置从右向左滑动的手势
		mView_ListView.setOnTouchListener(gestureListener);

		// listview的设置-----------------------------------------------------
		// 创建一个空的适配器,用来显示加载的数据,适配器内容稍后由loader填充
		mAdapter = new TrackAdapter(getActivity());
		// 为listview绑定数据适配器
		mView_ListView.setAdapter(mAdapter);
		// 将listview注册到上下文菜单中
		registerForContextMenu(mView_ListView);
		// 为listview的条目绑定一个点击事件监听
		mView_ListView.setOnItemClickListener(this);
		// 没有数据时显示
		mView_ListView.setEmptyView(mView_EmptyLoading);
		// 标题的设置------------------------------------------------------------
		mView_Title.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 标题作为回退导航
				getFragmentManager().popBackStackImmediate();
			}
		});
		// 默认不可点击
		mView_Title.setClickable(false);
		// 跳转至播放界面----------------------------------------------------------
		mView_GoToPlayer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mActivity.switchToPlayer();

			}
		});
		// 顶部弹出菜单--------------------------------------------------
		mOverflowPopupMenu
				.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {
						switch (item.getItemId()) {
						case R.id.sort_by_music_name:
							mSortOrder = Media.TITLE_KEY;
							getLoaderManager().restartLoader(
									MUSIC_RETRIEVE_LOADER, null,
									TrackBrowserFragment.this);
							break;
						case R.id.sort_by_artist_name:
							mSortOrder = Media.ARTIST_KEY;
							getLoaderManager().restartLoader(
									MUSIC_RETRIEVE_LOADER, null,
									TrackBrowserFragment.this);
							break;
						case R.id.classify_by_artist:
							if (null != getParentFragment()
									&& getParentFragment() instanceof FrameLocalMusicFragment) {
								getFragmentManager()
										.beginTransaction()
										.replace(
												R.id.frame_for_nested_fragment,
												Fragment.instantiate(
														getActivity(),
														ArtistBrowserFragment.class
																.getName(),
														null))
										.addToBackStack(null).commit();
							}
							break;
						case R.id.classify_by_album:
							if (null != getParentFragment()
									&& getParentFragment() instanceof FrameLocalMusicFragment) {
								getFragmentManager()
										.beginTransaction()
										.replace(
												R.id.frame_for_nested_fragment,
												Fragment.instantiate(
														getActivity(),
														AlbumBrowserFragment.class
																.getName(),
														null))
										.addToBackStack(null).commit();
							}
							break;
						case R.id.classify_by_folder:
							if (null != getParentFragment()
									&& getParentFragment() instanceof FrameLocalMusicFragment) {
								getFragmentManager()
										.beginTransaction()
										.replace(
												R.id.frame_for_nested_fragment,
												Fragment.instantiate(
														getActivity(),
														FolderBrowserFragment.class
																.getName(),
														null))
										.addToBackStack(null).commit();
							}
							break;
						}
						return false;
					}
				});
		mView_MoreFunctions.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mOverflowPopupMenu.show();

			}
		});

		// 侧滑菜单弹出按钮--------------------------------------------------------------
		mView_MenuNavigation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 如果是T9键盘,则先将其隐藏
				if (mIsT9Keyboard) {
					mT9KeyBoardWindow.dismiss();
				}
				mActivity.getSlidingMenu().toggle();

			}
		});
		mView_MutipleChoose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),
						MutipleEditActivity.class);
				Bundle data = new Bundle();
				// 传递参数给多选界面
				switch (getArguments().getInt(Constant.PARENT)) {
				case Constant.START_FROM_LOCAL_MUSIC:
					data.putString(Constant.TITLE,
							getResources().getString(R.string.local_music));
					data.putInt(Constant.PARENT,
							Constant.START_FROM_LOCAL_MUSIC);
					break;
				case Constant.START_FROM_ARTIST:
					data.putString(Constant.TITLE, mArtistInfo.getArtistName());
					data.putInt(Constant.PARENT, Constant.START_FROM_ARTIST);
					break;
				case Constant.START_FROM_FOLER:
					data.putString(Constant.TITLE, mFolderInfo.getFolderName());
					data.putInt(Constant.PARENT, Constant.START_FROM_FOLER);
					break;
				case Constant.START_FROM_PLAYLIST:
					data.putString(Constant.TITLE,
							mPlaylistInfo.getPlaylistName());
					data.putInt(Constant.PARENT, Constant.START_FROM_PLAYLIST);
					data.putInt(Constant.PLAYLIST_ID, mPlaylistInfo.getId());
					break;
				case Constant.START_FROM_ALBUM:
					data.putString(Constant.TITLE, mAlbumInfo.getAlbumName());
					data.putInt(Constant.PARENT, Constant.START_FROM_ALBUM);
					break;
				default:
					break;
				}
				data.putInt(Constant.FIRST_VISIBLE_POSITION,
						mView_ListView.getFirstVisiblePosition());
				data.putParcelableArrayList(Constant.DATA_LIST,
						mAdapter.getData());
				intent.putExtras(data);
				startActivity(intent);

			}
		});

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

	}

	@Override
	public Loader<List<TrackInfo>> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onLoadFinished(Loader<List<TrackInfo>> arg0,
			List<TrackInfo> arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoaderReset(Loader<List<TrackInfo>> arg0) {
		// TODO Auto-generated method stub

	}
}
